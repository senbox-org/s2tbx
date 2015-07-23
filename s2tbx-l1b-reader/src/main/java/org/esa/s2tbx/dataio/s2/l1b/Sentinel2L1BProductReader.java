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

package org.esa.s2tbx.dataio.s2.l1b;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import com.vividsolutions.jts.geom.Coordinate;
import jp2.TileLayout;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.math3.util.Pair;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.S2WavebandInfo;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleImageFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleMetadataFilename;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.framework.datamodel.Band;
import org.esa.snap.framework.datamodel.CrsGeoCoding;
import org.esa.snap.framework.datamodel.GeoCoding;
import org.esa.snap.framework.datamodel.Product;
import org.esa.snap.framework.datamodel.ProductData;
import org.esa.snap.framework.datamodel.TiePointGeoCoding;
import org.esa.snap.framework.datamodel.TiePointGrid;
import org.esa.snap.jai.ImageManager;
import org.esa.snap.util.Guardian;
import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.io.FileUtils;
import org.geotools.geometry.Envelope2D;
import org.jdom.JDOMException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.esa.s2tbx.dataio.s2.l1b.CoordinateUtils.*;
import static org.esa.s2tbx.dataio.s2.l1b.L1bMetadata.*;

// import com.jcabi.aspects.Loggable;

// import org.github.jamm.MemoryMeter;

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
public class Sentinel2L1BProductReader extends Sentinel2ProductReader {

    static final String USER_CACHE_DIR = "s2tbx/l1b-reader/cache";

    private final boolean forceResize;
    private final boolean isMultiResolution;

    private File cacheDir;
    protected final Logger logger;
    private int productResolution;


    // private MemoryMeter meter;

    public static class TileBandInfo {
        final Map<String, File> tileIdToFileMap;
        final int bandIndex;
        final S2WavebandInfo wavebandInfo;
        final TileLayout imageLayout;
        final String detectorId;

        TileBandInfo(Map<String, File> tileIdToFileMap, int bandIndex, String detector, S2WavebandInfo wavebandInfo, TileLayout imageLayout) {
            this.tileIdToFileMap = Collections.unmodifiableMap(tileIdToFileMap);
            this.bandIndex = bandIndex;
            this.detectorId = detector == null ? "" : detector;
            this.wavebandInfo = wavebandInfo;
            this.imageLayout = imageLayout;
        }

        public S2WavebandInfo getWavebandInfo() {
            return wavebandInfo;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public Sentinel2L1BProductReader(ProductReaderPlugIn readerPlugIn, boolean forceResize, int productResolution) {
        super(readerPlugIn, S2L1bConfig.getInstance());
        logger = SystemUtils.LOG;
        this.forceResize = forceResize;
        this.productResolution = productResolution;
        isMultiResolution = false;
    }

    Sentinel2L1BProductReader(ProductReaderPlugIn readerPlugIn, boolean forceResize) {
        super(readerPlugIn, S2L1bConfig.getInstance());
        logger = SystemUtils.LOG;
        this.forceResize = forceResize;
        this.productResolution = -1;
        isMultiResolution = true;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        logger.fine("readProductNodeImpl, " + getInput().toString());

        Product p;

        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }

        if (S2ProductFilename.isProductFilename(inputFile.getName())) {
            boolean isAGranule = S2L1BGranuleMetadataFilename.isGranuleFilename(inputFile.getName());
            if(isAGranule)
            {
                logger.fine("Reading a granule");
            }

            p = getL1bMosaicProduct(inputFile, isAGranule);

            if (p != null) {
                readMasks(p);
                p.setModified(false);
            }
        } else {
            throw new IOException("Unhandled file type.");
        }

        return p;
    }

    private TiePointGrid addTiePointGrid(int width, int height, String gridName, float[] tiePoints) {
        final TiePointGrid tiePointGrid = createTiePointGrid(gridName, 2, 2, 0, 0, width, height, tiePoints);
        return tiePointGrid;
    }

    private void readMasks(Product p) {
        Assert.notNull(p);
        // critical Implement this method
    }

    private Product getL1bMosaicProduct(File granuleMetadataFile, boolean isAGranule) throws IOException {
        Objects.requireNonNull(granuleMetadataFile);
        // first we need to recover parent metadata file...

        String filterTileId = null;
        File metadataFile = null;
        if(isAGranule)
        {
            try
            {
                Objects.requireNonNull(granuleMetadataFile.getParentFile());
                Objects.requireNonNull(granuleMetadataFile.getParentFile().getParentFile());
                Objects.requireNonNull(granuleMetadataFile.getParentFile().getParentFile().getParentFile());
            } catch (NullPointerException npe)
            {
                throw new IOException(String.format("Unable to retrieve the product associated to granule metadata file [%s]", granuleMetadataFile.getName()));
            }

            File up2levels = granuleMetadataFile.getParentFile().getParentFile().getParentFile();
            File tileIdFilter = granuleMetadataFile.getParentFile();

            filterTileId = tileIdFilter.getName();

            File[] files = up2levels.listFiles();
            for(File f: files)
            {
                if(S2ProductFilename.isProductFilename(f.getName()) && S2ProductFilename.isMetadataFilename(f.getName()))
                {
                    metadataFile = f;
                    break;
                }
            }
            if(metadataFile == null)
            {
                throw new IOException(String.format("Unable to retrieve the product associated to granule metadata file [%s]", granuleMetadataFile.getName()));
            }
        }
        else
        {
            metadataFile = granuleMetadataFile;
        }

        final String aFilter = filterTileId;

        L1bMetadata metadataHeader;

        try {
            metadataHeader = parseHeader(metadataFile, getConfig().getTileLayouts());
        } catch (JDOMException|JAXBException e) {
            SystemUtils.LOG.severe(Utils.getStackTrace(e));
            throw new IOException("Failed to parse metadata in " + metadataFile.getName());
        }

        L1bSceneDescription sceneDescription = L1bSceneDescription.create(metadataHeader, Tile.idGeom.G10M, getConfig());
        logger.fine("Scene Description: " + sceneDescription);

        File productDir = getProductDir(metadataFile);
        initCacheDir(productDir);

        ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();

        // fixme what if bandInformations are optional ?
        Map<Integer, TileBandInfo> bandInfoMap = new HashMap<Integer, TileBandInfo>();
        List<L1bMetadata.Tile> tileList = metadataHeader.getTileList();

        if(isAGranule)
        {
            tileList = metadataHeader.getTileList().stream().filter(p -> p.id.equalsIgnoreCase(aFilter)).collect(Collectors.toList());
        }

        Map<String, Tile> tilesById = new HashMap<>(tileList.size());
        for (Tile aTile : tileList) {
            tilesById.put(aTile.id, aTile);
        }

        if (productCharacteristics.bandInformations != null) {
            for (S2SpectralInformation bandInformation : productCharacteristics.bandInformations) {
                int bandIndex = bandInformation.getBandId();
                if (bandIndex >= 0 && bandIndex < productCharacteristics.bandInformations.length) {

                    HashMap<String, File> tileFileMap = new HashMap<String, File>();
                    for (Tile tile : tileList) {
                        S2GranuleDirFilename gf = S2L1BGranuleDirFilename.create(tile.id);
                        Guardian.assertNotNull("Product files don't match regular expressions", gf);

                        S2GranuleImageFilename granuleFileName = gf.getImageFilename(bandInformation.getPhysicalBand());
                        String imgFilename = "GRANULE" + File.separator + tile.id + File.separator + "IMG_DATA" + File.separator + granuleFileName.name;
                        logger.finer("Adding file " + imgFilename + " to band: " + bandInformation.getPhysicalBand());

                        File file = new File(productDir, imgFilename);
                        if (file.exists()) {
                            tileFileMap.put(tile.id, file);
                        } else {
                            logger.warning(String.format("Warning: missing file %s\n", file));
                        }
                    }

                    if (!tileFileMap.isEmpty()) {
                        TileBandInfo tileBandInfo = createBandInfoFromHeaderInfo(bandInformation, tileFileMap);
                        bandInfoMap.put(bandIndex, tileBandInfo);
                    } else {
                        logger.warning(
                                String.format("Warning: no image files found for band %s\n",
                                              bandInformation.getPhysicalBand()));
                    }
                } else {
                    logger.warning(
                            String.format("Warning: illegal band index detected for band %s\n",
                                          bandInformation.getPhysicalBand()));
                }
            }
        } else {
            logger.warning("There are no spectral information here !");
        }

        String[] detectors = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        // Order bands by physicalBand
        Map<String, S2SpectralInformation> sin = new HashMap<String, S2SpectralInformation>();
        for (S2SpectralInformation bandInformation : productCharacteristics.bandInformations) {
            sin.put(bandInformation.getPhysicalBand(), bandInformation);
        }

        Map<Pair<String, String>, Map<String, File>> detectorBandInfoMap = new HashMap<Pair<String, String>, Map<String, File>>();
        Map<String, TileBandInfo> bandInfoByKey = new HashMap<String, TileBandInfo>();
        if (productCharacteristics.bandInformations != null) {
            for (Tile tile : tileList) {
                S2L1BGranuleDirFilename gf = (S2L1BGranuleDirFilename) S2L1BGranuleDirFilename.create(tile.id);
                Guardian.assertNotNull("Product files don't match regular expressions", gf);

                for (S2SpectralInformation bandInformation : productCharacteristics.bandInformations) {
                    S2GranuleImageFilename granuleFileName = gf.getImageFilename(bandInformation.getPhysicalBand());
                    String imgFilename = "GRANULE" + File.separator + tile.id + File.separator + "IMG_DATA" + File.separator + granuleFileName.name;

                    logger.finer("Adding file " + imgFilename + " to band: " + bandInformation.getPhysicalBand() + ", and detector: " + gf.getDetectorId());

                    File file = new File(productDir, imgFilename);
                    if (file.exists()) {
                        Pair<String, String> key = new Pair<String, String>(bandInformation.getPhysicalBand(), gf.getDetectorId());
                        Map<String, File> fileMapper = detectorBandInfoMap.getOrDefault(key, new HashMap<String, File>());
                        fileMapper.put(tile.id, file);
                        if (!detectorBandInfoMap.containsKey(key)) {
                            detectorBandInfoMap.put(key, fileMapper);
                        }
                    } else {
                        logger.warning(String.format("Warning: missing file %s\n", file));
                    }
                }
            }

            if (!detectorBandInfoMap.isEmpty()) {
                for (Pair<String, String> key : detectorBandInfoMap.keySet()) {
                    TileBandInfo tileBandInfo = createBandInfoFromHeaderInfo(
                            key.getSecond(), sin.get(key.getFirst()), detectorBandInfoMap.get(key));

                    // composite band name : detector + band
                    String keyMix = key.getSecond() + key.getFirst();
                    bandInfoByKey.put(keyMix, tileBandInfo);
                }
            }
        } else {
            // fixme Look for optional info in schema
            logger.warning("There are no spectral information here !");
        }

        Product product = new Product(FileUtils.getFilenameWithoutExtension(metadataFile),
                                      "S2_MSI_" + productCharacteristics.processingLevel,
                                      sceneDescription.getSceneRectangle().width,
                                      sceneDescription.getSceneRectangle().height);

        product.setFileLocation(metadataFile.getParentFile());

        Map<String, GeoCoding> geoCodingsByDetector = new HashMap<>();

        // fixme Iterate over detectorBandInfoMap and add TiePointGrids to product
        if (!bandInfoByKey.isEmpty()) {
            for (TileBandInfo tbi : bandInfoByKey.values()) {
                if (!geoCodingsByDetector.containsKey(tbi.detectorId)) {
                    GeoCoding gc = getGeoCodingFromTileBandInfo(tbi, tilesById, product);
                    geoCodingsByDetector.put(tbi.detectorId, gc);
                }
            }
        }

        product.getMetadataRoot().addElement(metadataHeader.getMetadataElement());
        // setStartStopTime(product, mtdFilename.start, mtdFilename.stop);

        addDetectorBands(product, bandInfoByKey, sceneDescription.getSceneEnvelope(), new L1bSceneMultiLevelImageFactory(sceneDescription, ImageManager.getImageToModelTransform(product.getGeoCoding())));

        return product;
    }

    // todo use getSimpleGeoCodingFromTileBandInfo instead of getGeoCodingFromTileBandInfo
    private GeoCoding getSimpleGeoCodingFromTileBandInfo(TileBandInfo tileBandInfo, Map<String, Tile> tileList, Product product) {
        Objects.requireNonNull(tileBandInfo);
        Objects.requireNonNull(tileList);
        Objects.requireNonNull(product);

        Set<String> ourTileIds = tileBandInfo.tileIdToFileMap.keySet();
        List<Tile> aList = new ArrayList<Tile>(ourTileIds.size());
        List<Coordinate> coords = new ArrayList<>();
        for (String tileId : ourTileIds) {
            Tile currentTile = tileList.get(tileId);
            aList.add(currentTile);
        }

        // sort tiles by position
        Collections.sort(aList, (Tile u1, Tile u2) -> u1.tileGeometry10M.position.compareTo(u2.tileGeometry10M.position));

        for (Tile currentTile : aList) {
            coords.add(currentTile.corners.get(0));
            coords.add(currentTile.corners.get(3));
        }
        coords.add(aList.get(aList.size() - 1).corners.get(1));
        coords.add(aList.get(aList.size() - 1).corners.get(2));

        float[] lats = convertDoublesToFloats(getLatitudes(coords));
        float[] lons = convertDoublesToFloats(getLongitudes(coords));

        // todo create Tiepointgrid add add to product
        TiePointGrid latGrid = addTiePointGrid(aList.get(0).tileGeometry10M.numCols, aList.get(0).tileGeometry10M.numRowsDetector, tileBandInfo.wavebandInfo.bandName + ",latitude", lats);
        product.addTiePointGrid(latGrid);
        TiePointGrid lonGrid = addTiePointGrid(aList.get(0).tileGeometry10M.numCols, aList.get(0).tileGeometry10M.numRowsDetector, tileBandInfo.wavebandInfo.bandName + ",longitude", lons);
        product.addTiePointGrid(lonGrid);

        GeoCoding geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
        return geoCoding;
    }

    /**
     * Uses the 4 lat-lon corners of a detector to create the geocoding
     *
     * @param tileBandInfo
     * @param tileList
     * @param product
     * @return
     */
    private GeoCoding getGeoCodingFromTileBandInfo(TileBandInfo tileBandInfo, Map<String, Tile> tileList, Product product) {
        Objects.requireNonNull(tileBandInfo);
        Objects.requireNonNull(tileList);
        Objects.requireNonNull(product);

        Set<String> ourTileIds = tileBandInfo.tileIdToFileMap.keySet();
        List<Tile> aList = new ArrayList<Tile>(ourTileIds.size());
        List<Coordinate> coords = new ArrayList<>();
        for (String tileId : ourTileIds) {
            Tile currentTile = tileList.get(tileId);
            aList.add(currentTile);
        }

        // sort tiles by position
        Collections.sort(aList, (Tile u1, Tile u2) -> u1.tileGeometry10M.position.compareTo(u2.tileGeometry10M.position));

        coords.add(aList.get(0).corners.get(0));
        coords.add(aList.get(0).corners.get(3));
        coords.add(aList.get(aList.size() - 1).corners.get(1));
        coords.add(aList.get(aList.size() - 1).corners.get(2));

        // todo look at TiePointGrid construction, clockwise, counterclockwise ?
        float[] lats = convertDoublesToFloats(getLatitudes(coords));
        float[] lons = convertDoublesToFloats(getLongitudes(coords));

        TiePointGrid latGrid = addTiePointGrid(aList.get(0).tileGeometry10M.numCols, aList.get(0).tileGeometry10M.numRowsDetector, tileBandInfo.wavebandInfo.bandName + ",latitude", lats);
        product.addTiePointGrid(latGrid);
        TiePointGrid lonGrid = addTiePointGrid(aList.get(0).tileGeometry10M.numCols, aList.get(0).tileGeometry10M.numRowsDetector, tileBandInfo.wavebandInfo.bandName + ",longitude", lons);
        product.addTiePointGrid(lonGrid);

        GeoCoding geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
        return geoCoding;
    }

    private void addDetectorBands(Product product, Map<String, TileBandInfo> stringBandInfoMap, Envelope2D envelope, MultiLevelImageFactory mlif) throws IOException {
        product.setPreferredTileSize(S2Config.DEFAULT_JAI_TILE_SIZE, S2Config.DEFAULT_JAI_TILE_SIZE);
        product.setNumResolutionsMax(getConfig().getTileLayouts()[0].numResolutions);

        product.setAutoGrouping("D01:D02:D03:D04:D05:D06:D07:D08:D09:D10:D11:D12");

        ArrayList<String> bandIndexes = new ArrayList<String>(stringBandInfoMap.keySet());
        Collections.sort(bandIndexes);

        if (bandIndexes.isEmpty()) {
            throw new IOException("No valid bands found.");
        }

        for (String bandIndex : bandIndexes) {
            TileBandInfo tileBandInfo = stringBandInfoMap.get(bandIndex);
            if (isMultiResolution || tileBandInfo.getWavebandInfo().resolution.resolution == this.productResolution){
                Band band = addBand(product, tileBandInfo);
                band.setSourceImage(mlif.createSourceImage(tileBandInfo));

                if (!forceResize) {
                    try {
                        band.setGeoCoding(new CrsGeoCoding(envelope.getCoordinateReferenceSystem(),
                                band.getRasterWidth(),
                                band.getRasterHeight(),
                                envelope.getMinX(),
                                envelope.getMaxY(),
                                tileBandInfo.getWavebandInfo().resolution.resolution,
                                tileBandInfo.getWavebandInfo().resolution.resolution,
                                0.0, 0.0));
                    } catch (FactoryException e) {
                        logger.severe("Illegal CRS");
                    } catch (TransformException e) {
                        logger.severe("Illegal projection");
                    }
                }
            }
        }
        

    }

    private Band addBand(Product product, TileBandInfo tileBandInfo) {
        int index = S2SpatialResolution.valueOfId(tileBandInfo.getWavebandInfo().resolution.id).resolution / S2SpatialResolution.R10M.resolution;
        int defRes = S2SpatialResolution.R10M.resolution;

        final Band band = new Band(tileBandInfo.wavebandInfo.bandName, S2Config.SAMPLE_PRODUCT_DATA_TYPE, product.getSceneRasterWidth()  / index, product.getSceneRasterHeight()  / index);
        product.addBand(band);

        band.setSpectralBandIndex(tileBandInfo.bandIndex);
        band.setSpectralWavelength((float) tileBandInfo.wavebandInfo.wavelength);
        band.setSpectralBandwidth((float) tileBandInfo.wavebandInfo.bandwidth);

        setValidPixelMask(band, tileBandInfo.wavebandInfo.bandName);

        return band;
    }

    private void setValidPixelMask(Band band, String bandName) {
        band.setNoDataValue(0);
        band.setValidPixelExpression(String.format("%s.raw > %s",
                                                   bandName, S2Config.RAW_NO_DATA_THRESHOLD));
    }

    private TiePointGrid createTiePointGrid(String name, int gridWidth, int gridHeight, float[] values) {
        final TiePointGrid tiePointGrid = new TiePointGrid(name, gridWidth, gridHeight, 0.0F, 0.0F, 500.0F, 500.0F, values);
        tiePointGrid.setNoDataValue(Double.NaN);
        tiePointGrid.setNoDataValueUsed(true);
        return tiePointGrid;
    }

    private static Map<String, File> createFileMap(String tileId, File imageFile) {
        Map<String, File> tileIdToFileMap = new HashMap<String, File>();
        tileIdToFileMap.put(tileId, imageFile);
        return tileIdToFileMap;
    }

    private void setStartStopTime(Product product, String start, String stop) {
        try {
            product.setStartTime(ProductData.UTC.parse(start, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // {@report "illegal start date"}
        }

        try {
            product.setEndTime(ProductData.UTC.parse(stop, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // {@report "illegal stop date"}
        }
    }

    private TileBandInfo createBandInfoFromDefaults(int bandIndex, S2WavebandInfo wavebandInfo, String tileId, File imageFile) {
        // L1cTileLayout aLayout = CodeStreamUtils.getL1bTileLayout(imageFile.toURI().toString(), null);
        return new TileBandInfo(createFileMap(tileId, imageFile),
                                bandIndex, null,
                                wavebandInfo,
                                // aLayout);
                                //todo test this
                                getConfig().getTileLayouts()[wavebandInfo.resolution.id]);
    }

    private TileBandInfo createBandInfoFromHeaderInfo(S2SpectralInformation bandInformation, Map<String, File> tileFileMap) {
        S2SpatialResolution spatialResolution = S2SpatialResolution.valueOfResolution(bandInformation.getResolution());
        return new TileBandInfo(tileFileMap,
                                bandInformation.getBandId(), null,
                                new S2WavebandInfo(bandInformation.getBandId(),
                                                   bandInformation.getPhysicalBand(),
                                                      spatialResolution, bandInformation.getWavelengthCentral(),
                                                      Math.abs(bandInformation.getWavelengthMax() + bandInformation.getWavelengthMin())),
                                getConfig().getTileLayouts()[spatialResolution.id]);
    }

    private TileBandInfo createBandInfoFromHeaderInfo(String detector, S2SpectralInformation bandInformation, Map<String, File> tileFileMap) {
        S2SpatialResolution spatialResolution = S2SpatialResolution.valueOfResolution(bandInformation.getResolution());
        return new TileBandInfo(tileFileMap,
                                bandInformation.getBandId(), detector,
                                new S2WavebandInfo(bandInformation.getBandId(),
                                                      detector + bandInformation.getPhysicalBand(), // notice that text shown to user in menu (detector, band) is evaluated as an expression !!
                                                      spatialResolution, bandInformation.getWavelengthCentral(),
                                                      Math.abs(bandInformation.getWavelengthMax() + bandInformation.getWavelengthMin())),
                                getConfig().getTileLayouts()[spatialResolution.id]);
    }

    private void setGeoCoding(Product product, Envelope2D envelope) {
        try {
            product.setGeoCoding(new CrsGeoCoding(envelope.getCoordinateReferenceSystem(),
                                                  product.getSceneRasterWidth(),
                                                  product.getSceneRasterHeight(),
                                                  envelope.getMinX(),
                                                  envelope.getMaxY(),
                                                  S2SpatialResolution.R10M.resolution,
                                                  S2SpatialResolution.R10M.resolution,
                                                  0.0, 0.0));
        } catch (FactoryException e) {
            logger.severe("Illegal CRS");
        } catch (TransformException e) {
            logger.severe("Illegal projection");
        }
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

        public abstract MultiLevelImage createSourceImage(TileBandInfo tileBandInfo);
    }

    private class L1bTileMultiLevelImageFactory extends MultiLevelImageFactory {
        private L1bTileMultiLevelImageFactory(AffineTransform imageToModelTransform) {
            super(imageToModelTransform);
        }

        public MultiLevelImage createSourceImage(TileBandInfo tileBandInfo) {
            return new DefaultMultiLevelImage(new L1bTileMultiLevelSource(tileBandInfo, imageToModelTransform));
        }
    }

    private class L1bSceneMultiLevelImageFactory extends MultiLevelImageFactory {

        private final L1bSceneDescription sceneDescription;

        public L1bSceneMultiLevelImageFactory(L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform) {
            super(imageToModelTransform);

            SystemUtils.LOG.fine("Model factory: " + ToStringBuilder.reflectionToString(imageToModelTransform));

            this.sceneDescription = sceneDescription;
        }

        @Override
        // @Loggable
        public MultiLevelImage createSourceImage(TileBandInfo tileBandInfo) {
            BandL1bSceneMultiLevelSource bandScene = new BandL1bSceneMultiLevelSource(sceneDescription, tileBandInfo, imageToModelTransform);
            SystemUtils.LOG.log(Level.parse(S2Config.LOG_SCENE), "BandScene: " + bandScene);
            return new DefaultMultiLevelImage(bandScene);
        }
    }

    /**
     * A MultiLevelSource for single L1C tiles.
     */
    private class L1bTileMultiLevelSource extends AbstractMultiLevelSource {
        final TileBandInfo tileBandInfo;

        public L1bTileMultiLevelSource(TileBandInfo tileBandInfo, AffineTransform imageToModelTransform) {
            super(new DefaultMultiLevelModel(tileBandInfo.imageLayout.numResolutions,
                                             imageToModelTransform,
                                             getConfig().getTileLayouts()[0].width, // todo we must use data from jp2 files to update this
                                             getConfig().getTileLayouts()[0].height)); // todo we must use data from jp2 files to update this
            this.tileBandInfo = tileBandInfo;
        }

        @Override
        protected RenderedImage createImage(int level) {
            File imageFile = tileBandInfo.tileIdToFileMap.values().iterator().next();
            return L1bTileOpImage.create(imageFile,
                                         cacheDir,
                                         null,
                                         tileBandInfo.imageLayout,
                                         getConfig().getTileLayouts(),
                                         getModel(),
                                         tileBandInfo.wavebandInfo.resolution,
                                         level);
        }

    }

    /**
     * A MultiLevelSource for a scene made of multiple L1C tiles.
     */
    private abstract class AbstractL1bSceneMultiLevelSource extends AbstractMultiLevelSource {
        protected final L1bSceneDescription sceneDescription;

        AbstractL1bSceneMultiLevelSource(L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform, int numResolutions) {
            super(new DefaultMultiLevelModel(numResolutions,
                                             imageToModelTransform,
                                             sceneDescription.getSceneRectangle().width,
                                             sceneDescription.getSceneRectangle().height));
            this.sceneDescription = sceneDescription;
        }


        protected abstract PlanarImage createL1bTileImage(String tileId, int level);
    }

    /**
     * A MultiLevelSource used by bands for a scene made of multiple L1C tiles.
     */
    private final class BandL1bSceneMultiLevelSource extends AbstractL1bSceneMultiLevelSource {
        private final TileBandInfo tileBandInfo;

        public BandL1bSceneMultiLevelSource(L1bSceneDescription sceneDescription, TileBandInfo tileBandInfo, AffineTransform imageToModelTransform) {
            super(sceneDescription, imageToModelTransform, tileBandInfo.imageLayout.numResolutions);
            this.tileBandInfo = tileBandInfo;
        }

        @Override
        protected PlanarImage createL1bTileImage(String tileId, int level) {
            File imageFile = tileBandInfo.tileIdToFileMap.get(tileId);

            PlanarImage planarImage = L1bTileOpImage.create(imageFile,
                                                            cacheDir,
                                                            null, // tileRectangle.getLocation(),
                                                            tileBandInfo.imageLayout,
                                                            getConfig().getTileLayouts(),
                                                            getModel(),
                                                            tileBandInfo.wavebandInfo.resolution,
                                                            level);

            logger.fine(String.format("Planar image model: %s", getModel().toString()));

            logger.fine(String.format("Planar image created: %s %s: minX=%d, minY=%d, width=%d, height=%d\n",
                                      tileBandInfo.wavebandInfo.bandName, tileId,
                                      planarImage.getMinX(), planarImage.getMinY(),
                                      planarImage.getWidth(), planarImage.getHeight()));

            return planarImage;
        }

        @Override
        protected RenderedImage createImage(int level) {
            ArrayList<RenderedImage> tileImages = new ArrayList<RenderedImage>();

            long preImage = 0;
            long postImage = 0;

            List<String> tiles = sceneDescription.getTileIds().stream().filter(x -> x.contains(tileBandInfo.detectorId)).collect(Collectors.toList());

            for (String tileId : tiles) {
                int tileIndex = sceneDescription.getTileIndex(tileId);
                Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);

                PlanarImage opImage = createL1bTileImage(tileId, level);
                {
                    double factorX = 1.0 / (Math.pow(2, level) * (this.tileBandInfo.wavebandInfo.resolution.resolution / S2SpatialResolution.R10M.resolution));
                    double factorY = 1.0 / (Math.pow(2, level) * (this.tileBandInfo.wavebandInfo.resolution.resolution / S2SpatialResolution.R10M.resolution));

                    opImage = TranslateDescriptor.create(opImage,
                                                         (float) Math.floor((tileRectangle.x * factorX)),
                                                         (float) Math.floor((tileRectangle.y * factorY)),
                                                         Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);

                    logger.log(Level.parse(S2Config.LOG_SCENE), String.format("Translate descriptor: %s", ToStringBuilder.reflectionToString(opImage)));
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
            Rectangle fitrect = new Rectangle(0, 0, (int) sceneDescription.getSceneEnvelope().getWidth() / tileBandInfo.wavebandInfo.resolution.resolution, (int) sceneDescription.getSceneEnvelope().getHeight() / tileBandInfo.wavebandInfo.resolution.resolution);
            final Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitrect, Math.pow(2.0, level));

            BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);

            if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
                int rightPad = destBounds.width - mosaicOp.getWidth();
                int bottomPad = destBounds.height - mosaicOp.getHeight();
                SystemUtils.LOG.log(Level.parse(S2Config.LOG_SCENE), String.format("Border: (%d, %d), (%d, %d)", mosaicOp.getWidth(), destBounds.width, mosaicOp.getHeight(), destBounds.height));

                mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, null);
            }

            if (this.tileBandInfo.wavebandInfo.resolution != S2SpatialResolution.R10M) {
                PlanarImage scaled = L1bTileOpImage.createGenericScaledImage(mosaicOp, sceneDescription.getSceneEnvelope(), this.tileBandInfo.wavebandInfo.resolution, level, forceResize);

                logger.log(Level.parse(S2Config.LOG_SCENE), String.format("mosaicOp created for level %d at (%d,%d) with size (%d, %d)%n", level, scaled.getMinX(), scaled.getMinY(), scaled.getWidth(), scaled.getHeight()));

                return scaled;
            }
            // todo add crop ?

            logger.log(Level.parse(S2Config.LOG_SCENE), String.format("mosaicOp created for level %d at (%d,%d) with size (%d, %d)%n", level, mosaicOp.getMinX(), mosaicOp.getMinY(), mosaicOp.getWidth(), mosaicOp.getHeight()));

            return mosaicOp;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
