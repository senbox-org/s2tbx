/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import com.vividsolutions.jts.geom.Polygon;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.math3.util.Pair;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.S2WavebandInfo;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.l1c.filepaterns.S2L1CGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleImageFilename;
import org.esa.s2tbx.dataio.s2.l1c.filepaterns.S2L1CGranuleMetadataFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.gml.EopPolygon;
import org.esa.s2tbx.dataio.s2.gml.GmlFilter;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.framework.datamodel.*;
import org.esa.snap.jai.ImageManager;
import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.io.FileUtils;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.CRS;
import org.jdom.JDOMException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.esa.s2tbx.dataio.openjpeg.StackTraceUtils;

import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// todo - register reasonable RGB profile(s)
// todo - set a band's validMaskExpr or no-data value (read from GML)
// todo - set band's ImageInfo from min,max,histogram found in header (--> L1cMetadata.quicklookDescriptor)
// todo - viewing incidence tie-point grids contain NaN values - find out how to correctly treat them

// todo - better collect problems during product opening and generate problem report (requires reader API change), see {@report "Problem detected..."} code marks

/**
 * <p>
 * This product reader can currently read single L1C tiles (also called L1C granules) and entire L1C scenes composed of
 * multiple L1C tiles.
 * </p>
 * <p>
 * To read single tiles, select any tile image file (IMG_*.jp2) within a product package. The reader will then
 * collect other band images for the selected tile and wiull also try to read the metadata file (MTD_*.xml).
 * </p>
 * <p>To read an entire scene, select the metadata file (MTD_*.xml) within a product package. The reader will then
 * collect other tile/band images and create a mosaic on the fly.
 * </p>
 *
 * @author Norman Fomferra
 */
public class Sentinel2L1CProductReader extends Sentinel2ProductReader {

    static final int SUN_ZENITH_GRID_INDEX = 0;
    static final int SUN_AZIMUTH_GRID_INDEX = 1;
    static final int VIEW_ZENITH_GRID_INDEX = 2;
    static final int VIEW_AZIMUTH_GRID_INDEX = 3;

    static final String USER_CACHE_DIR = "s2tbx/l1c-reader/cache";

    private final int productResolution;
    private final boolean isMultiResolution;
    private final String epsgCode;

    private File cacheDir;
    protected final Logger logger;

    static class BandInfo {
        final Map<String, File> tileIdToFileMap;
        final int bandIndex;
        final S2WavebandInfo wavebandInfo;
        final TileLayout imageLayout;

        BandInfo(Map<String, File> tileIdToFileMap, int bandIndex, S2WavebandInfo wavebandInfo, TileLayout imageLayout) {
            this.tileIdToFileMap = Collections.unmodifiableMap(tileIdToFileMap);
            this.bandIndex = bandIndex;
            this.wavebandInfo = wavebandInfo;
            this.imageLayout = imageLayout;
        }

        public S2WavebandInfo getWavebandInfo() {
            return wavebandInfo;
        }

        public TileLayout getImageLayout() {
            return imageLayout;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public Sentinel2L1CProductReader(ProductReaderPlugIn readerPlugIn, int productResolution, boolean isMultiResolution, String epsgCode) {
        super(readerPlugIn, S2L1CConfig.getInstance());
        logger = SystemUtils.LOG;
        this.isMultiResolution = isMultiResolution;
        this.productResolution = productResolution;
        this.epsgCode = epsgCode;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

    @Override
    protected Product getMosaicProduct(File metadataFile) throws IOException {
        Objects.requireNonNull(metadataFile);

        boolean isAGranule = S2L1CGranuleMetadataFilename.isGranuleFilename(metadataFile.getName());

        if(isAGranule) {
            logger.fine("Reading a granule");
        }

        // update the tile layout
        updateTileLayout(metadataFile.toPath(), isAGranule, productResolution);


        String filterTileId = null;
        File rootMetaDataFile = null;
        String granuleDirName = null;

        // we need to recover parent metadata file if we have a granule
        if (isAGranule) {
            granuleDirName = metadataFile.getParentFile().getName();
            try {
                Objects.requireNonNull(metadataFile.getParentFile());
                Objects.requireNonNull(metadataFile.getParentFile().getParentFile());
                Objects.requireNonNull(metadataFile.getParentFile().getParentFile().getParentFile());
            } catch (NullPointerException npe) {
                throw new IOException(String.format("Unable to retrieve the product associated to granule metadata file [%s]", metadataFile.getName()));
            }

            File up2levels = metadataFile.getParentFile().getParentFile().getParentFile();
            File tileIdFilter = metadataFile.getParentFile();

            filterTileId = tileIdFilter.getName();

            File[] files = up2levels.listFiles();
            for (File f : files) {
                if (S2ProductFilename.isProductFilename(f.getName()) && S2ProductFilename.isMetadataFilename(f.getName())) {
                    rootMetaDataFile = f;
                    break;
                }
            }
            if (rootMetaDataFile == null) {
                throw new IOException(String.format("Unable to retrieve the product associated to granule metadata file [%s]", metadataFile.getName()));
            }
        } else {
            rootMetaDataFile = metadataFile;
        }

        final String aFilter = filterTileId;

        L1cMetadata metadataHeader = null;


        try {
            metadataHeader = L1cMetadata.parseHeader(rootMetaDataFile, granuleDirName, getConfig().getTileLayouts(), epsgCode);
        } catch (JDOMException|JAXBException e) {
            throw new IOException("Failed to parse metadata in " + rootMetaDataFile.getName());
        }

        L1cSceneDescription sceneDescription = L1cSceneDescription.create(metadataHeader,
                                                                          L1cMetadata.Tile.idGeom.G10M,
                                                                          getConfig());
        logger.fine("Scene Description: " + sceneDescription);

        File productDir = getProductDir(rootMetaDataFile);
        initCacheDir(productDir);

        L1cMetadata.ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();


        // set the product global geo-coding
        Product product = new Product(FileUtils.getFilenameWithoutExtension(rootMetaDataFile),
                                      "S2_MSI_" + productCharacteristics.processingLevel,
                                      sceneDescription.getSceneRectangle().width,
                                      sceneDescription.getSceneRectangle().height);

        product.getMetadataRoot().addElement(metadataHeader.getMetadataElement());
        product.setFileLocation(metadataFile);

        Envelope2D sceneEnvelope = sceneDescription.getSceneEnvelope();

        try {
            product.setGeoCoding(new CrsGeoCoding(sceneEnvelope.getCoordinateReferenceSystem(),
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    sceneEnvelope.getMinX(),
                    sceneEnvelope.getMaxY(),
                    this.productResolution,
                    this.productResolution,
                    0.0, 0.0));
        } catch (TransformException e) {
            logger.severe("Error caught during product geo coding");
        } catch (FactoryException e) {
            logger.severe("Error caught during product geo coding");
        }

        product.setPreferredTileSize(S2Config.DEFAULT_JAI_TILE_SIZE, S2Config.DEFAULT_JAI_TILE_SIZE);
        product.setNumResolutionsMax(getConfig().getTileLayout(S2SpatialResolution.R10M.resolution).numResolutions);
        product.setAutoGrouping("sun:view");

        // create the band mosaics per UTM zones
        for (String utmZone : metadataHeader.getUTMZonesList()) {
            Map<Integer, BandInfo> bandInfoMap = new HashMap<>();

            // if we selected the granule there is only one UTM zone and we'll get here only once, just extract the granule
            // otherwise get the list of tiles for this UTM zone
            List<L1cMetadata.Tile> utmZoneTileList = metadataHeader.getTileList(utmZone);
            if (isAGranule) {
                utmZoneTileList = utmZoneTileList.stream().filter(p -> p.id.equalsIgnoreCase(aFilter)).collect(Collectors.toList());
            }
            // for all bands of the UTM zone, store tiles files names
            for (S2SpectralInformation bandInformation : productCharacteristics.bandInformations) {
                int bandIndex = bandInformation.getBandId();
                if (bandIndex >= 0 && bandIndex < productCharacteristics.bandInformations.length) {

                    HashMap<String, File> tileFileMap = new HashMap<String, File>();
                    for (L1cMetadata.Tile tile : utmZoneTileList) {
                        S2L1CGranuleDirFilename gf = S2L1CGranuleDirFilename.create(tile.id);
                        S2GranuleImageFilename imageFilename = gf.getImageFilename(bandInformation.getPhysicalBand());

                        String imgFilename = "GRANULE" + File.separator + tile.id + File.separator + "IMG_DATA" + File.separator + imageFilename.name;

                        logger.finer("Adding file " + imgFilename + " to band: " + bandInformation.getPhysicalBand());

                        File file = new File(productDir, imgFilename);
                        if (file.exists()) {
                            tileFileMap.put(tile.id, file);
                        } else {
                            logger.warning(String.format("Warning: missing file %s\n", file));
                        }
                    }

                    if (!tileFileMap.isEmpty()) {
                        BandInfo bandInfo = createBandInfoFromHeaderInfo(bandInformation, tileFileMap);
                        bandInfoMap.put(bandIndex, bandInfo);
                    } else {
                        logger.warning(String.format("Warning: no image files found for band %s\n", bandInformation.getPhysicalBand()));
                    }
                } else {
                    logger.warning(String.format("Warning: illegal band index detected for band %s\n", bandInformation.getPhysicalBand()));
                }
            }


            if(!bandInfoMap.isEmpty())
            {
                addBands(product, bandInfoMap, sceneDescription.getSceneEnvelope(), new L1cSceneMultiLevelImageFactory(sceneDescription, ImageManager.getImageToModelTransform(product.getGeoCoding())));
            }

            List<EopPolygon> polygons = filterMasksInUTMZones(utmZoneTileList);

            Map<String, List<EopPolygon>> polygonsByType = new HashMap<>();

            // todo put polygon creation in a function
            if(!polygons.isEmpty())
            {
                // first collect all types
                Set<String> polygonTypes = polygons.stream().map(p -> p.getType()).collect(Collectors.toSet());
                for(String polygonType: polygonTypes)
                {
                    polygonsByType.put(polygonType, polygons.stream().filter(p -> p.getType().equals(polygonType)).collect(Collectors.toList()) );
                }

                for (String polygonType : polygonTypes) {
                    final SimpleFeatureType type = Placemark.createGeometryFeatureType();
                    final DefaultFeatureCollection collection = new DefaultFeatureCollection("S2L1CMasks", type);

                    List<EopPolygon> typedPolygon = polygonsByType.get(polygonType);
                    for (int index = 0; index < typedPolygon.size(); index++) {
                        Polygon pol = typedPolygon.get(index).getPolygon();

                        Object[] data1 = {pol, String.format("Polygon-%s", index)};
                        SimpleFeatureImpl f1 = new SimpleFeatureImpl(data1, type, new FeatureIdImpl(String.format("F-%s", index)), true);
                        collection.add(f1);
                    }

                    VectorDataNode vdn = new VectorDataNode(polygonType, collection);
                    vdn.setOwner(product);
                    product.addMask(polygonType, vdn, "GML Mask", Color.GREEN, 0.8);
                }
            }
        }

        if(!"Brief".equalsIgnoreCase(productCharacteristics.getMetaDataLevel())) {
            addTiePointGridBand(product, metadataHeader, sceneDescription, "sun_zenith", SUN_ZENITH_GRID_INDEX);
            addTiePointGridBand(product, metadataHeader, sceneDescription, "sun_azimuth", SUN_AZIMUTH_GRID_INDEX);
            addTiePointGridBand(product, metadataHeader, sceneDescription, "view_zenith", VIEW_ZENITH_GRID_INDEX);
            addTiePointGridBand(product, metadataHeader, sceneDescription, "view_azimuth", VIEW_AZIMUTH_GRID_INDEX);
        }


        return product;
    }

    private  List<EopPolygon> filterMasksInUTMZones(List<L1cMetadata.Tile> utmZoneTileList) {
        List<EopPolygon> polygons = new ArrayList<EopPolygon>();

        GmlFilter gmlFilter = new GmlFilter();
        if(!utmZoneTileList.isEmpty())
        {
            for(L1cMetadata.Tile tile: utmZoneTileList)
            {
                L1cMetadata.MaskFilename[] filenames = tile.maskFilenames;

                if(filenames != null) {
                    for (L1cMetadata.MaskFilename aMaskFile : filenames) {
                        File aFile = aMaskFile.getName();
                        Pair<String, List<EopPolygon>> polys = gmlFilter.parse(aFile);

                        boolean warningForPolygonsOutOfUtmZone = false;

                        if (!polys.getFirst().isEmpty()) {
                            int indexOfColons = polys.getFirst().indexOf(':');
                            if (indexOfColons != -1) {
                                String realCsCode = tile.horizontalCsCode.substring(tile.horizontalCsCode.indexOf(':') + 1);
                                if (polys.getFirst().contains(realCsCode)) {
                                    polygons.addAll(polys.getSecond());
                                } else {
                                    warningForPolygonsOutOfUtmZone = true;
                                }
                            }
                        }

                        if (warningForPolygonsOutOfUtmZone) {
                            logger.warning(String.format("Polygons detected out of its UTM zone in file [%s] !", aFile.getAbsolutePath()));
                        }
                    }
                }
            }
        }

        return polygons;
    }


    private void addTiePointGridBand(Product product, L1cMetadata metadataHeader, L1cSceneDescription sceneDescription, String name, int tiePointGridIndex) {
        final Band band = product.addBand(name, ProductData.TYPE_FLOAT32);
        band.setSourceImage(new DefaultMultiLevelImage(new TiePointGridL1cSceneMultiLevelSource(sceneDescription, metadataHeader, ImageManager.getImageToModelTransform(product.getGeoCoding()), 6, tiePointGridIndex)));
    }

    private void addBands(Product product, Map<Integer, BandInfo> bandInfoMap, Envelope2D envelope, MultiLevelImageFactory mlif) throws IOException {
        ArrayList<Integer> bandIndexes = new ArrayList<Integer>(bandInfoMap.keySet());
        Collections.sort(bandIndexes);

        if (bandIndexes.isEmpty()) {
            throw new IOException("No valid bands found.");
        }

        for (Integer bandIndex : bandIndexes) {
            BandInfo bandInfo = bandInfoMap.get(bandIndex);

            if(isMultiResolution || bandInfo.getWavebandInfo().resolution.resolution == this.productResolution)
            {
                Band band = addBand(product, bandInfo);
                band.setSourceImage(mlif.createSourceImage(bandInfo));

                try {
                    band.setGeoCoding(new CrsGeoCoding(CRS.decode(epsgCode),
                            band.getRasterWidth(),
                            band.getRasterHeight(),
                            envelope.getMinX(),
                            envelope.getMaxY(),
                            bandInfo.getWavebandInfo().resolution.resolution,
                            bandInfo.getWavebandInfo().resolution.resolution,
                            0.0, 0.0));
                } catch (FactoryException e) {
                    logger.severe("Illegal CRS");
                } catch (TransformException e) {
                    logger.severe("Illegal projection");
                }

                /*
                try {
                    AffineTransform scaler = AffineTransform.getScaleInstance(this.productResolution, this.productResolution).createInverse();
                    AffineTransform move = AffineTransform.getTranslateInstance(-envelope.getMinX(), -envelope.getMinY());
                    AffineTransform mirror_y = new AffineTransform(1, 0, 0, -1, 0, envelope.getHeight() / this.productResolution);

                    AffineTransform world2pixel = new AffineTransform(mirror_y);
                    world2pixel.concatenate(scaler);
                    world2pixel.concatenate(move);

                    S2SceneRasterTransform transform = new S2SceneRasterTransform(new AffineTransform2D(world2pixel), new AffineTransform2D(world2pixel.createInverse()));

                    // todo uncomment when mutiresolution works using setSceneRasterTransform
                    // band.setSceneRasterTransform(transform);
                } catch (NoninvertibleTransformException e) {
                    logger.severe("Illegal transform");
                }
                */
            }
        }
    }

    private Band addBand(Product product, BandInfo bandInfo) {
        int index = S2SpatialResolution.valueOfId(bandInfo.getWavebandInfo().resolution.id).resolution / S2SpatialResolution.R10M.resolution;

        String bandName = bandInfo.wavebandInfo.bandName;
        final Band band = new Band(bandName, S2Config.SAMPLE_PRODUCT_DATA_TYPE, product.getSceneRasterWidth()  / index, product.getSceneRasterHeight()  / index);
        product.addBand(band);

        band.setSpectralBandIndex(bandInfo.bandIndex);
        band.setSpectralWavelength((float) bandInfo.wavebandInfo.wavelength);
        band.setSpectralBandwidth((float) bandInfo.wavebandInfo.bandwidth);

        setValidPixelMask(band, bandName);

        return band;
    }

    private void setValidPixelMask(Band band, String bandName) {
        band.setNoDataValue(0);
        band.setValidPixelExpression(String.format("%s.raw > %s",
                bandName, S2Config.RAW_NO_DATA_THRESHOLD));
    }

    /*private void addL1cTileTiePointGrids(L1cMetadata metadataHeader, Product product, int tileIndex) {
        final TiePointGrid[] tiePointGrids = createL1cTileTiePointGrids(metadataHeader, tileIndex);
        for (TiePointGrid tiePointGrid : tiePointGrids) {
            product.addTiePointGrid(tiePointGrid);
        }
    }*/

    private TiePointGrid[] createL1cTileTiePointGrids(L1cMetadata metadataHeader, int tileIndex) {
        TiePointGrid[] tiePointGrid = null;
        L1cMetadata.Tile tile = metadataHeader.getTileList().get(tileIndex);
        L1cMetadata.AnglesGrid anglesGrid = tile.sunAnglesGrid;
        if(anglesGrid != null) {
            int gridHeight = tile.sunAnglesGrid.zenith.length;
            int gridWidth = tile.sunAnglesGrid.zenith[0].length;
            float[] sunZeniths = new float[gridWidth * gridHeight];
            float[] sunAzimuths = new float[gridWidth * gridHeight];
            float[] viewingZeniths = new float[gridWidth * gridHeight];
            float[] viewingAzimuths = new float[gridWidth * gridHeight];
            Arrays.fill(viewingZeniths, Float.NaN);
            Arrays.fill(viewingAzimuths, Float.NaN);
            L1cMetadata.AnglesGrid sunAnglesGrid = tile.sunAnglesGrid;
            L1cMetadata.AnglesGrid[] viewingIncidenceAnglesGrids = tile.viewingIncidenceAnglesGrids;
            for (int y = 0; y < gridHeight; y++) {
                for (int x = 0; x < gridWidth; x++) {
                    final int index = y * gridWidth + x;
                    sunZeniths[index] = sunAnglesGrid.zenith[y][x];
                    sunAzimuths[index] = sunAnglesGrid.azimuth[y][x];
                    for (L1cMetadata.AnglesGrid grid : viewingIncidenceAnglesGrids) {
                        try {
                            if (y < grid.zenith.length) {
                                if (x < grid.zenith[y].length) {
                                    if (!Float.isNaN(grid.zenith[y][x])) {
                                        viewingZeniths[index] = grid.zenith[y][x];
                                    }
                                }
                            }

                            if (y < grid.azimuth.length) {
                                if (x < grid.azimuth[y].length) {
                                    if (!Float.isNaN(grid.azimuth[y][x])) {
                                        viewingAzimuths[index] = grid.azimuth[y][x];
                                    }
                                }
                            }

                        } catch (Exception e) {
                            // {@report "Solar info problem"}
                            logger.severe(StackTraceUtils.getStackTrace(e));
                        }
                    }
                }
            }
            tiePointGrid = new TiePointGrid[]{
                    createTiePointGrid("sun_zenith", gridWidth, gridHeight, sunZeniths),
                    createTiePointGrid("sun_azimuth", gridWidth, gridHeight, sunAzimuths),
                    createTiePointGrid("view_zenith", gridWidth, gridHeight, viewingZeniths),
                    createTiePointGrid("view_azimuth", gridWidth, gridHeight, viewingAzimuths)
            };
        }
        return tiePointGrid;
    }

    private TiePointGrid createTiePointGrid(String name, int gridWidth, int gridHeight, float[] values) {
        final TiePointGrid tiePointGrid = new TiePointGrid(name, gridWidth, gridHeight, 0.0F, 0.0F, 500.0F, 500.0F, values);
        tiePointGrid.setNoDataValue(Double.NaN);
        tiePointGrid.setNoDataValueUsed(true);
        return tiePointGrid;
    }

    private BandInfo createBandInfoFromHeaderInfo(S2SpectralInformation bandInformation, Map<String, File> tileFileMap) {
        S2SpatialResolution spatialResolution = S2SpatialResolution.valueOfResolution(bandInformation.getResolution());
        return new BandInfo(tileFileMap,
                            bandInformation.getBandId(),
                            new S2WavebandInfo(bandInformation.getBandId(),
                                               bandInformation.getPhysicalBand(),
                                               spatialResolution, bandInformation.getWavelengthCentral(),
                                               bandInformation.getWavelengthMax() - bandInformation.getWavelengthMin()),
                            getConfig().getTileLayout(spatialResolution.resolution));
    }

    static File getProductDir(File productFile) throws IOException {
        final File resolvedFile = productFile.getCanonicalFile();
        if (!resolvedFile.exists()) {
            throw new FileNotFoundException("File not found: " + productFile);
        }

        if (productFile.getParentFile() == null) {
            return new File(".").getCanonicalFile();
        }

        return productFile.getParentFile();
    }

    void initCacheDir(File productDir) throws IOException {
        cacheDir = new File(new File(SystemUtils.getApplicationDataDir(), USER_CACHE_DIR),
                            productDir.getName());
        //noinspection ResultOfMethodCallIgnored
        cacheDir.mkdirs();
        if (!cacheDir.exists() || !cacheDir.isDirectory() || !cacheDir.canWrite()) {
            throw new IOException("Can't access package cache directory");
        }
    }

    private abstract class MultiLevelImageFactory {
        protected final AffineTransform imageToModelTransform;

        protected MultiLevelImageFactory(AffineTransform imageToModelTransform) {
            this.imageToModelTransform = imageToModelTransform;
        }

        public abstract MultiLevelImage createSourceImage(BandInfo bandInfo);
    }

    private class L1cSceneMultiLevelImageFactory extends MultiLevelImageFactory {

        private final L1cSceneDescription sceneDescription;

        public L1cSceneMultiLevelImageFactory(L1cSceneDescription sceneDescription, AffineTransform imageToModelTransform) {
            super(imageToModelTransform);

            SystemUtils.LOG.fine("Model factory: " + ToStringBuilder.reflectionToString(imageToModelTransform));

            this.sceneDescription = sceneDescription;
        }

        @Override
        public MultiLevelImage createSourceImage(BandInfo bandInfo) {
            BandL1cSceneMultiLevelSource bandScene = new BandL1cSceneMultiLevelSource(sceneDescription, bandInfo, imageToModelTransform);
            SystemUtils.LOG.fine("BandScene: " + bandScene);
            return new DefaultMultiLevelImage(bandScene);
        }
    }


    /**
     * A MultiLevelSource for a scene made of multiple L1C tiles.
     */
    private abstract class AbstractL1cSceneMultiLevelSource extends AbstractMultiLevelSource {
        protected final L1cSceneDescription sceneDescription;

        AbstractL1cSceneMultiLevelSource(L1cSceneDescription sceneDescription, AffineTransform imageToModelTransform, int numResolutions) {
            super(new DefaultMultiLevelModel(numResolutions,
                                             imageToModelTransform,
                                             sceneDescription.getSceneRectangle().width,
                                             sceneDescription.getSceneRectangle().height));
            this.sceneDescription = sceneDescription;
        }


        protected abstract PlanarImage createL1cTileImage(String tileId, int level);
    }

    /**
     * A MultiLevelSource used by bands for a scene made of multiple L1C tiles.
     */
    private final class BandL1cSceneMultiLevelSource extends AbstractL1cSceneMultiLevelSource {
        private final BandInfo bandInfo;

        public BandL1cSceneMultiLevelSource(L1cSceneDescription sceneDescription, BandInfo bandInfo, AffineTransform imageToModelTransform) {
            super(sceneDescription, imageToModelTransform, bandInfo.imageLayout.numResolutions);
            this.bandInfo = bandInfo;
        }

        @Override
        protected PlanarImage createL1cTileImage(String tileId, int level) {
            File imageFile = bandInfo.tileIdToFileMap.get(tileId);
            PlanarImage planarImage = L1cTileOpImage.create(imageFile,
                                                            cacheDir,
                                                            null, // tileRectangle.getLocation(),
                                                            bandInfo.imageLayout,
                                                            getConfig().getTileLayouts(),
                                                            getModel(),
                                                            bandInfo.wavebandInfo.resolution,
                                                            level);

            logger.fine(String.format("Planar image model: %s", getModel().toString()));

            logger.fine(String.format("Planar image created: %s %s: minX=%d, minY=%d, width=%d, height=%d\n",
                                      bandInfo.wavebandInfo.bandName, tileId,
                                      planarImage.getMinX(), planarImage.getMinY(),
                                      planarImage.getWidth(), planarImage.getHeight()));

            return planarImage;
        }

        @Override
        protected RenderedImage createImage(int level) {
            ArrayList<RenderedImage> tileImages = new ArrayList<RenderedImage>();

            for (String tileId : sceneDescription.getTileIds()) {
                int tileIndex = sceneDescription.getTileIndex(tileId);
                Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);

                PlanarImage opImage = createL1cTileImage(tileId, level);

                {
                    double factorX = 1.0 / (Math.pow(2, level) * (this.bandInfo.wavebandInfo.resolution.resolution / S2SpatialResolution.R10M.resolution));
                    double factorY = 1.0 / (Math.pow(2, level) * (this.bandInfo.wavebandInfo.resolution.resolution / S2SpatialResolution.R10M.resolution));

                    opImage = TranslateDescriptor.create(opImage,
                                                         (float) Math.floor((tileRectangle.x * factorX)),
                                                         (float) Math.floor((tileRectangle.y * factorY)),
                                                         Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);

                    logger.fine(String.format("Translate descriptor: %s", ToStringBuilder.reflectionToString(opImage)));
                }

                logger.log(Level.parse(S2Config.LOG_SCENE), String.format("opImage added for level %d at (%d,%d) with size (%d,%d)%n", level, opImage.getMinX(), opImage.getMinY(), opImage.getWidth(), opImage.getHeight()));
                tileImages.add(opImage);
            }

            if (tileImages.isEmpty()) {
                logger.warning("No tile images for mosaic");
                return null;
            }

            ImageLayout imageLayout = new ImageLayout();
            imageLayout.setMinX(0);
            imageLayout.setMinY(0);
            imageLayout.setTileWidth(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileHeight(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileGridXOffset(0);
            imageLayout.setTileGridYOffset(0);

            RenderedOp mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                                                          MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                          null, null, new double[][]{{1.0}}, new double[]{S2Config.FILL_CODE_MOSAIC_BG},
                                                          new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));

            // todo add crop or extend here to ensure "right" size...
            Rectangle fitrect = new Rectangle(0, 0, (int) sceneDescription.getSceneEnvelope().getWidth() / bandInfo.wavebandInfo.resolution.resolution, (int) sceneDescription.getSceneEnvelope().getHeight() / bandInfo.wavebandInfo.resolution.resolution);
            final Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitrect, Math.pow(2.0, level));

            BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);

            if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
                int rightPad = destBounds.width - mosaicOp.getWidth();
                int bottomPad = destBounds.height - mosaicOp.getHeight();
                SystemUtils.LOG.fine(String.format("Border: (%d, %d), (%d, %d)", mosaicOp.getWidth(), destBounds.width, mosaicOp.getHeight(), destBounds.height));

                mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, null);
            }


            if (this.bandInfo.wavebandInfo.resolution != S2SpatialResolution.R10M) {
                PlanarImage scaled = L1cTileOpImage.createGenericScaledImage(mosaicOp, sceneDescription.getSceneEnvelope(), this.bandInfo.wavebandInfo.resolution, level);

                logger.fine(String.format("mosaicOp created for level %d at (%d,%d) with size (%d, %d)%n", level, scaled.getMinX(), scaled.getMinY(), scaled.getWidth(), scaled.getHeight()));

                return scaled;
            }

            // todo add crop ?

            logger.fine(String.format("mosaicOp created for level %d at (%d,%d) with size (%d, %d)%n", level, mosaicOp.getMinX(), mosaicOp.getMinY(), mosaicOp.getWidth(), mosaicOp.getHeight()));

            return mosaicOp;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    /**
     * A MultiLevelSource used by bands for a scene made of multiple L1C tiles.
     */
    private final class TiePointGridL1cSceneMultiLevelSource extends AbstractL1cSceneMultiLevelSource {

        private final L1cMetadata metadata;
        private final int tiePointGridIndex;
        private HashMap<String, TiePointGrid[]> tiePointGridsMap;

        public TiePointGridL1cSceneMultiLevelSource(L1cSceneDescription sceneDescription, L1cMetadata metadata, AffineTransform imageToModelTransform, int numResolutions, int tiePointGridIndex) {
            super(sceneDescription, imageToModelTransform, numResolutions);
            this.metadata = metadata;
            this.tiePointGridIndex = tiePointGridIndex;
            tiePointGridsMap = new HashMap<String, TiePointGrid[]>();
        }

        @Override
        protected PlanarImage createL1cTileImage(String tileId, int level) {
            PlanarImage tiePointGridL1CTileImage = null;
            TiePointGrid[] tiePointGrids = tiePointGridsMap.get(tileId);
            if (tiePointGrids == null) {
                final int tileIndex = sceneDescription.getTileIndex(tileId);

                tiePointGrids = createL1cTileTiePointGrids(metadata, tileIndex);
                if(tiePointGrids != null) {
                    tiePointGridsMap.put(tileId, tiePointGrids);
                }
            }

            if(tiePointGrids != null) {
                tiePointGridL1CTileImage = (PlanarImage) tiePointGrids[tiePointGridIndex].getSourceImage().getImage(level);
            }

            return tiePointGridL1CTileImage;
        }

        @Override
        protected RenderedImage createImage(int level) {
            ArrayList<RenderedImage> tileImages = new ArrayList<RenderedImage>();

            for (String tileId : sceneDescription.getTileIds()) {

                int tileIndex = sceneDescription.getTileIndex(tileId);
                Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);

                PlanarImage opImage = createL1cTileImage(tileId, level);

                // todo - This translation step is actually not required because we can create L1cTileOpImages
                // with minX, minY set as it is required by the MosaicDescriptor and indicated by its API doc.
                // But if we do it like that, we get lots of weird visual artifacts in the resulting mosaic.
                if (opImage != null) {
                    opImage = TranslateDescriptor.create(opImage,
                                                         (float) (tileRectangle.x >> level),
                                                         (float) (tileRectangle.y >> level),
                                                         Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);


                    logger.log(Level.parse(S2Config.LOG_SCENE), String.format("opImage added for level %d at (%d,%d)%n", level, opImage.getMinX(), opImage.getMinY()));
                    tileImages.add(opImage);
                }
            }

            if (tileImages.isEmpty()) {
                logger.warning("no tile images for mosaic");
                return null;
            }

            ImageLayout imageLayout = new ImageLayout();
            imageLayout.setMinX(0);
            imageLayout.setMinY(0);
            imageLayout.setTileWidth(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileHeight(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileGridXOffset(0);
            imageLayout.setTileGridYOffset(0);

            RenderedOp mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                                                          MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                          null, null, new double[][]{{1.0}}, new double[]{S2Config.FILL_CODE_MOSAIC_BG},
                                                          new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));

            logger.fine(String.format("mosaicOp created for level %d at (%d,%d)%n", level, mosaicOp.getMinX(), mosaicOp.getMinY()));
            logger.fine(String.format("mosaicOp size: (%d,%d)%n", mosaicOp.getWidth(), mosaicOp.getHeight()));

            return mosaicOp;
        }
    }



    @Override
    protected String[] getBandNames(int resolution) {
        String[] bandNames;

        switch (resolution) {
            case 10:
                bandNames = new String[] {"B02", "B03", "B04", "B08"};
                break;
            case 20:
                bandNames = new String[] {"B05", "B06", "B07", "B8A", "B09", "B11", "B12"};
                break;
            case 60:
                bandNames = new String[] {"B01", "B09", "B10"};
                break;
            default:
                SystemUtils.LOG.warning("Invalid resolution: " + resolution);
                bandNames = null;
                break;
        }

        return bandNames;
    }
}
