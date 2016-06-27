/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.s2.ortho;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.math3.util.Precision;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.jp2.internal.JP2TileOpImage;
import org.esa.s2tbx.dataio.openjpeg.StackTraceUtils;
import org.esa.s2tbx.dataio.s2.ColorIterator;
import org.esa.s2tbx.dataio.s2.S2BandAnglesGrid;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.TimeProbe;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.gml.EopPolygon;
import org.esa.s2tbx.dataio.s2.gml.GmlFilter;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleMetadataFilename;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Placemark;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.image.SourceImageScaler;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.core.util.jai.JAIDebug;
import org.esa.snap.core.util.jai.JAIUtils;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.awt.image.DataBuffer.*;
import static org.esa.s2tbx.dataio.openjpeg.OpenJpegUtils.validateOpenJpegExecutables;
import static org.esa.snap.utils.DateHelper.parseDate;


/**
 * <p>
 * Base class for Sentinel-2 readers of orthorectified products
 * </p>
 * <p>
 * To read single tiles, select any tile image file (IMG_*.jp2) within a product package. The reader will then
 * collect other band images for the selected tile and will also try to read the metadata file (MTD_*.xml).
 * </p>
 * <p>To read an entire scene, select the metadata file (MTD_*.xml) within a product package. The reader will then
 * collect other tile/band images and create a mosaic on the fly.
 * </p>
 *
 * @author Norman Fomferra
 * @author Nicolas Ducoin
 */
public abstract class Sentinel2OrthoProductReader extends Sentinel2ProductReader {

    static final String VIEW_ZENITH_PREFIX = "view_zenith";
    static final String VIEW_AZIMUTH_PREFIX = "view_azimuth";
    static final String SUN_ZENITH_PREFIX = "sun_zenith";
    static final String SUN_AZIMUTH_PREFIX = "sun_azimuth";


    private final String epsgCode;
    protected final Logger logger;

    public Sentinel2OrthoProductReader(ProductReaderPlugIn readerPlugIn, String epsgCode) {
        super(readerPlugIn);
        logger = SystemUtils.LOG;
        this.epsgCode = epsgCode;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

    protected abstract String getReaderCacheDir();

    protected abstract S2Metadata parseHeader(File file, String granuleName, S2Config config, String epsg) throws IOException;

    protected abstract String getImagePathString(String imageFileName, S2SpatialResolution resolution);

    @Override
    protected Product getMosaicProduct(File metadataFile) throws IOException {

        if (!validateOpenJpegExecutables(S2Config.OPJ_INFO_EXE, S2Config.OPJ_DECOMPRESSOR_EXE)) {
            throw new IOException("Invalid OpenJpeg executables");
        }

        Objects.requireNonNull(metadataFile);

        boolean isAGranule = S2OrthoGranuleMetadataFilename.isGranuleFilename(metadataFile.getName());

        if (isAGranule) {
            logger.fine("Reading a granule");
        }

        TimeProbe timeProbe = TimeProbe.start();
        // update the tile layout
        if (!updateTileLayout(metadataFile.toPath(), isAGranule)) {
            throw new IOException(String.format("Unable to retrieve the JPEG tile layout associated to product [%s]", metadataFile.getName()));
        }
        SystemUtils.LOG.fine(String.format("[timeprobe] updateTileLayout : %s ms", timeProbe.elapsed(TimeUnit.MILLISECONDS)));

        String filterTileId = null;
        File rootMetaDataFile = null;
        String granuleDirName = null;

        timeProbe.reset();
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
            if (files != null) {
                for (File f : files) {
                    if (S2ProductFilename.isProductFilename(f.getName()) && S2ProductFilename.isMetadataFilename(f.getName())) {
                        rootMetaDataFile = f;
                        break;
                    }
                }
            }
            if (rootMetaDataFile == null) {
                throw new IOException(String.format("Unable to retrieve the product associated to granule metadata file [%s]", metadataFile.getName()));
            }
        } else {
            rootMetaDataFile = metadataFile;
        }

        final String aFilter = filterTileId;

        S2Metadata metadataHeader = parseHeader(rootMetaDataFile, granuleDirName, getConfig(), epsgCode);
        SystemUtils.LOG.fine(String.format("[timeprobe] metadata parsing : %s ms", timeProbe.elapsed(TimeUnit.MILLISECONDS)));
        timeProbe.reset();

        S2OrthoSceneLayout sceneDescription = S2OrthoSceneLayout.create(metadataHeader);
        logger.fine("Scene Description: " + sceneDescription);

        File productDir = getProductDir(rootMetaDataFile);
        initCacheDir(productDir);

        S2Metadata.ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();

        Product product = new Product(FileUtils.getFilenameWithoutExtension(rootMetaDataFile),
                                      "S2_MSI_" + productCharacteristics.getProcessingLevel(),
                                      sceneDescription.getSceneDimension(getProductResolution()).width,
                                      sceneDescription.getSceneDimension(getProductResolution()).height);

        for (MetadataElement metadataElement : metadataHeader.getMetadataElements()) {
            product.getMetadataRoot().addElement(metadataElement);
        }
        product.setFileLocation(metadataFile);

        try {
            product.setSceneGeoCoding(new CrsGeoCoding(CRS.decode(this.epsgCode),
                                                       product.getSceneRasterWidth(),
                                                       product.getSceneRasterHeight(),
                                                       sceneDescription.getSceneOrigin()[0],
                                                       sceneDescription.getSceneOrigin()[1],
                                                       this.getProductResolution().resolution,
                                                       this.getProductResolution().resolution,
                                                       0.0, 0.0));
        } catch (FactoryException e) {
            throw new IOException(e);
        } catch (TransformException e) {
            throw new IOException(e);
        }

        product.setPreferredTileSize(S2Config.DEFAULT_JAI_TILE_SIZE, S2Config.DEFAULT_JAI_TILE_SIZE);
        product.setNumResolutionsMax(getConfig().getTileLayout(S2SpatialResolution.R10M.resolution).numResolutions);
        product.setAutoGrouping("sun:view:quality");

        product.setStartTime(parseDate(productCharacteristics.getProductStartTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        product.setEndTime(parseDate(productCharacteristics.getProductStopTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        List<BandInfo> bandInfoList = new ArrayList<>();

        List<S2Metadata.Tile> tileList = metadataHeader.getTileList();
        if (isAGranule) {
            tileList = tileList.stream().filter(p -> p.getId().equalsIgnoreCase(aFilter)).collect(Collectors.toList());
        }

        // Verify access to granule image files, and store absolute location
        for (S2BandInformation bandInformation : productCharacteristics.getBandInformations()) {
            HashMap<String, File> tileFileMap = new HashMap<>();
            for (S2Metadata.Tile tile : tileList) {
                S2OrthoGranuleDirFilename gf = S2OrthoGranuleDirFilename.create(tile.getId());
                if (gf != null) {

                    String imgFilename = String.format("GRANULE%s%s%s%s", File.separator, tile.getId(),
                                                       File.separator,
                                                       bandInformation.getImageFileTemplate()
                                                               .replace("{{MISSION_ID}}", gf.missionID)
                                                               .replace("{{SITECENTRE}}", gf.siteCentre)
                                                               .replace("{{CREATIONDATE}}", gf.creationDate)
                                                               .replace("{{ABSOLUTEORBIT}}", gf.absoluteOrbit)
                                                               .replace("{{TILENUMBER}}", gf.tileNumber)
                                                               .replace("{{RESOLUTION}}", String.format("%d", bandInformation.getResolution().resolution)));

                    logger.finer("Adding file " + imgFilename + " to band: " + bandInformation.getPhysicalBand());

                    File file = new File(productDir, imgFilename);
                    if (file.exists()) {
                        tileFileMap.put(tile.getId(), file);
                    } else {
                        logger.warning(String.format("Warning: missing file %s\n", file));
                    }
                }
            }

            if (!tileFileMap.isEmpty()) {
                BandInfo bandInfo = createBandInfoFromHeaderInfo(bandInformation, tileFileMap);
                bandInfoList.add(bandInfo);
            } else {
                logger.warning(String.format("Warning: no image files found for band %s\n", bandInformation.getPhysicalBand()));
            }
        }
        SystemUtils.LOG.fine(String.format("[timeprobe] product initialisation : %s ms", timeProbe.elapsed(TimeUnit.MILLISECONDS)));
        timeProbe.reset();

        if (!bandInfoList.isEmpty()) {
            addBands(product,
                     bandInfoList,
                     sceneDescription);
            SystemUtils.LOG.fine(String.format("[timeprobe] addBands : %s ms", timeProbe.elapsed(TimeUnit.MILLISECONDS)));
            timeProbe.reset();


            scaleBands(product, bandInfoList);
            SystemUtils.LOG.fine(String.format("[timeprobe] scaleBands : %s ms", timeProbe.elapsed(TimeUnit.MILLISECONDS)));
            timeProbe.reset();

            addVectorMasks(product, tileList, bandInfoList);
            SystemUtils.LOG.fine(String.format("[timeprobe] addVectorMasks : %s ms", timeProbe.elapsed(TimeUnit.MILLISECONDS)));
            timeProbe.reset();

            addIndexMasks(product, bandInfoList, sceneDescription);
            SystemUtils.LOG.fine(String.format("[timeprobe] addIndexMasks : %s ms", timeProbe.elapsed(TimeUnit.MILLISECONDS)));
            timeProbe.reset();
        }

        if (!"Brief".equalsIgnoreCase(productCharacteristics.getMetaDataLevel())) {

            HashMap<String, S2BandAnglesGrid[]> anglesGridsMap = new HashMap<>();
            for (S2Metadata.Tile tile : tileList) {
                S2BandAnglesGrid[] bandAnglesGrids = createS2OrthoAnglesGrids(metadataHeader, tile.getId());
                if (bandAnglesGrids != null) {
                    anglesGridsMap.put(tile.getId(), bandAnglesGrids);
                }
            }

            addAnglesBands(product, metadataHeader, sceneDescription, anglesGridsMap);

            SystemUtils.LOG.fine(String.format("[timeprobe] addTiePointGridBand : %s ms", timeProbe.elapsed(TimeUnit.MILLISECONDS)));
            timeProbe.reset();
        }

        return product;
    }

    abstract protected int getMaskLevel();

    private void addAnglesBands(Product product, S2Metadata metadataHeader, S2OrthoSceneLayout sceneDescription, HashMap<String, S2BandAnglesGrid[]> bandAnglesGridsMap ) {

        float masterOriginX = Float.MAX_VALUE, masterOriginY = -Float.MAX_VALUE;
        int minX = 0, minY = 0;
        int widthAnglesTile = 0;
        int heightAnglesTile = 0;
        float resX = 0;
        float resY = 0;
        S2BandAnglesGrid[] bandAnglesGrid = null;
        //Search upper-left tile
        for (String tileId : sceneDescription.getTileIds()) {
            //test all S2SpatialResolutions because sometimes not all are available
            for (S2SpatialResolution s2res : S2SpatialResolution.values()) {
                if (sceneDescription.getTilePositionInScene(tileId, s2res).getX() == 0 && sceneDescription.getTilePositionInScene(tileId, s2res).getY() == 0) {
                    bandAnglesGrid = bandAnglesGridsMap.get(tileId);
                    masterOriginX = bandAnglesGrid[0].originX;
                    masterOriginY = bandAnglesGrid[0].originY;
                    widthAnglesTile = bandAnglesGrid[0].getWidth();
                    heightAnglesTile = bandAnglesGrid[0].getHeight();
                    resX = bandAnglesGrid[0].getResX();
                    resY = bandAnglesGrid[0].getResY();
                }
            }
        }

        if(masterOriginX == Float.MAX_VALUE || masterOriginY == -Float.MAX_VALUE || resX == 0 || resY == 0 || widthAnglesTile == 0 || heightAnglesTile == 0) {
            logger.warning("Invalid tile data for computing the angles mosaic");
            return;
        }

        for (int i = 0; i<bandAnglesGrid.length; i++) {

            int[] bandOffsets = {0};
            SampleModel sampleModel = new PixelInterleavedSampleModel(TYPE_FLOAT, widthAnglesTile, heightAnglesTile, 1, widthAnglesTile, bandOffsets);
            //data buffer (where the pixels are stored)
            DataBuffer buffer = new DataBufferFloat(widthAnglesTile*heightAnglesTile*1);

            // Wrap it in a writable raster
            WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);
            raster.setPixels(0, 0, widthAnglesTile, heightAnglesTile, bandAnglesGrid[i].getData());
            ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.TRANSLUCENT, TYPE_FLOAT);

            // Create an image with the raster
            BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
            final PlanarImage planarImage = PlanarImage.wrapRenderedImage(image);

            //Mosaic of planar image
            ArrayList<PlanarImage> tileImages = new ArrayList<>();

            for (String tileId : sceneDescription.getTileIds()) {
                PlanarImage opImage = null;

                DataBuffer buffer2 = new DataBufferFloat(widthAnglesTile*heightAnglesTile*1);

                // Wrap it in a writable raster
                WritableRaster raster2 = Raster.createWritableRaster(sampleModel, buffer2, null);
                S2BandAnglesGrid[] bandAnglesGrids2 = bandAnglesGridsMap.get(tileId);
                raster2.setPixels(0, 0, widthAnglesTile, heightAnglesTile, bandAnglesGrids2[i].getData());
                ColorSpace colorSpace2 = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                ColorModel colorModel2 = new ComponentColorModel(colorSpace2, false, false, Transparency.TRANSLUCENT, TYPE_FLOAT);

                // And finally create an image with this raster
                BufferedImage image2 = new BufferedImage(colorModel2, raster2, colorModel2.isAlphaPremultiplied(), null);
                opImage = PlanarImage.wrapRenderedImage(image2);

                /*
                 * Translate the images
                 */

                // Apply tile translation
                float transX=(bandAnglesGrids2[0].originX-masterOriginX)/bandAnglesGrids2[0].getResX();
                float transY=(bandAnglesGrids2[0].originY-masterOriginY)/bandAnglesGrids2[0].getResY();

                opImage = TranslateDescriptor.create(opImage,
                                                     transX,
                                                     -transY,
                                                     Interpolation.getInstance(Interpolation.INTERP_BILINEAR), null);


                if(opImage.getMinX()<minX) minX = opImage.getMinX();
                if(opImage.getMinY()<minY) minY = opImage.getMinY();

                // Feed the image list for mosaic
                tileImages.add(opImage);
            }

            if (tileImages.isEmpty()) {
                logger.warning("No tile images for angles mosaic");
                return;
            }

            ImageLayout imageLayout = new ImageLayout();
            imageLayout.setMinX(minX);
            imageLayout.setMinY(minY);
            imageLayout.setTileWidth(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileHeight(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileGridXOffset(0);
            imageLayout.setTileGridYOffset(0);

            RenderedOp mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                                                          MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                          null, null,new double[][] {{0.0}}, new double[]{S2Config.FILL_CODE_MOSAIC_ANGLES},
                                                          new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));

            //Crop output image because with bilinear interpolation some pixels (last column and row) are NaN.
            mosaicOp = CropDescriptor.create(mosaicOp,
                                             0.0f, 0.0f, (float) mosaicOp.getWidth()-1, (float) mosaicOp.getHeight()-1,
                                             new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));


            Band band;
            if(bandAnglesGrid[i].getBand() != null) {
                band = new Band(bandAnglesGrid[i].getPrefix() + "_" + bandAnglesGrid[i].getBand().getFilenameBandId(), ProductData.TYPE_FLOAT32, mosaicOp.getWidth(), mosaicOp.getHeight());
            } else {
                band = new Band(bandAnglesGrid[i].getPrefix(), ProductData.TYPE_FLOAT32, mosaicOp.getWidth(), mosaicOp.getHeight());
            }
            String description = "";
            if(bandAnglesGrid[i].getPrefix().startsWith(VIEW_ZENITH_PREFIX)) {
                description = "Viewing incidence zenith angle";
            }
            if(bandAnglesGrid[i].getPrefix().startsWith(VIEW_AZIMUTH_PREFIX)) {
                description = "Viewing incidence azimuth angle";
            }
            if(bandAnglesGrid[i].getPrefix().startsWith(SUN_ZENITH_PREFIX)) {
                description = "Solar zenith angle";
            }
            if(bandAnglesGrid[i].getPrefix().startsWith(SUN_AZIMUTH_PREFIX)) {
                description = "Solar azimuth angle";
            }

            band.setDescription(description);
            band.setUnit("°");

            try {
                band.setGeoCoding(new CrsGeoCoding(CRS.decode(epsgCode),
                                                   band.getRasterWidth(),
                                                   band.getRasterHeight(),
                                                   sceneDescription.getSceneOrigin()[0],
                                                   sceneDescription.getSceneOrigin()[1],
                                                   resX,
                                                   resY,
                                                   0.0, 0.0));
            } catch (FactoryException e) {
                //throw new IOException(e);
            } catch (TransformException e) {
                //throw new IOException(e);
            }

            band.setImageToModelTransform(product.findImageToModelTransform(band.getGeoCoding()));

            //set source image mut be done after setGeocoding and setImageToModelTransform
            band.setSourceImage(mosaicOp);
            product.addBand(band);

        }

    }



    private void addBands(Product product, List<BandInfo> bandInfoList, S2OrthoSceneLayout sceneDescription) throws IOException {
        for (BandInfo bandInfo : bandInfoList) {
            Dimension dimension = sceneDescription.getSceneDimension(bandInfo.getBandInformation().getResolution());
            Band band = addBand(product, bandInfo, dimension);
            band.setDescription(bandInfo.getBandInformation().getDescription());
            band.setUnit(bandInfo.getBandInformation().getUnit());

            double pixelSize = 0;
            if (isMultiResolution()) {
                pixelSize = (double) bandInfo.getBandInformation().getResolution().resolution;
            } else {
                pixelSize = (double) getProductResolution().resolution;
            }

            try {
                band.setGeoCoding(new CrsGeoCoding(CRS.decode(epsgCode),
                                                   band.getRasterWidth(),
                                                   band.getRasterHeight(),
                                                   sceneDescription.getSceneOrigin()[0],
                                                   sceneDescription.getSceneOrigin()[1],
                                                   pixelSize,
                                                   pixelSize,
                                                   0.0, 0.0));
            } catch (FactoryException e) {
                throw new IOException(e);
            } catch (TransformException e) {
                throw new IOException(e);
            }

            MultiLevelImageFactory mlif = new L1cSceneMultiLevelImageFactory(
                    sceneDescription,
                    Product.findImageToModelTransform(band.getGeoCoding()));

            band.setSourceImage(mlif.createSourceImage(bandInfo));

        }
    }

    private void scaleBands(Product product, List<BandInfo> bandInfoList) throws IOException {

        // In MultiResolution mode, all bands are kept at their native resolution
        if (isMultiResolution()) {
            return;
        }

        // Find a reference band for rescaling the bands at other resolution
        MultiLevelImage targetImage = null;
        for (BandInfo bandInfo : bandInfoList) {
            if (bandInfo.getBandInformation().getResolution() == getProductResolution()) {
                Band referenceBand = product.getBand(bandInfo.getBandInformation().getPhysicalBand());
                targetImage = referenceBand.getSourceImage();
                break;
            }
        }

        // If the product only has a subset of bands, we may not find what we are looking for
        if (targetImage == null) {
            String error = String.format("Products with no bands at %s m resolution currently cannot be read by the %s m reader", getProductResolution().resolution, getProductResolution().resolution);
            throw new IOException(error);
        }

        for (Band band : product.getBands()) {
            final MultiLevelImage sourceImage = band.getSourceImage();

            if (sourceImage.getWidth() == product.getSceneRasterWidth()
                    && sourceImage.getHeight() == product.getSceneRasterHeight()) {
                // Do not rescaled band which are already at the correct resolution
                continue;
            }

            ImageLayout imageLayout = new ImageLayout();
            ImageManager.getPreferredTileSize(product);
            final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);
            float[] scalings = new float[2];
            scalings[0] = product.getSceneRasterWidth() / (float) sourceImage.getWidth();
            scalings[1] = product.getSceneRasterHeight() / (float) sourceImage.getHeight();
            PlanarImage scaledImage = SourceImageScaler.scaleMultiLevelImage(targetImage, sourceImage, scalings, null, renderingHints,
                                                                             band.getNoDataValue(),
                                                                             Interpolation.getInstance(Interpolation.INTERP_NEAREST));
            band.setSourceImage(scaledImage);
        }
    }

    private void addIndexMasks(Product product, List<BandInfo> bandInfoList, S2OrthoSceneLayout sceneDescription) throws IOException {
        for (BandInfo bandInfo : bandInfoList) {
            if (bandInfo.getBandInformation() instanceof S2IndexBandInformation) {
                S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) bandInfo.getBandInformation();
                IndexCoding indexCoding = indexBandInformation.getIndexCoding();
                product.getIndexCodingGroup().add(indexCoding);

                Dimension dimension = sceneDescription.getSceneDimension(bandInfo.getBandInformation().getResolution());

                List<Color> colors = indexBandInformation.getColors();
                Iterator<Color> colorIterator = colors.iterator();

                for (String indexName : indexCoding.getIndexNames()) {
                    int indexValue = indexCoding.getIndexValue(indexName);
                    String description = indexCoding.getIndex(indexName).getDescription();
                    if (!colorIterator.hasNext()) {
                        // we should never be here : programming error.
                        throw new IOException(String.format("Unexpected error when creating index masks : colors list does not have the same size as index coding"));
                    }
                    Color color = colorIterator.next();
                    Mask mask = Mask.BandMathsType.create("scl_" + indexName.toLowerCase(), description, dimension.width, dimension.height,
                                                          String.format("%s.raw == %d", indexBandInformation.getPhysicalBand(), indexValue), color, 0.5);
                    product.addMask(mask);
                }
            }
        }
    }

    private void addVectorMasks(Product product, List<S2Metadata.Tile> tileList, List<BandInfo> bandInfoList) throws IOException {
        for (MaskInfo maskInfo : MaskInfo.values()) {
            if (!maskInfo.isPresentAtLevel(getMaskLevel()))
                continue;

            if (!maskInfo.isPerBand()) {
                // cloud masks are provided once and valid for all bands
                addVectorMask(product, tileList, maskInfo, null, bandInfoList);
            } else {
                // for other masks, we have one mask instance for each spectral band
                for (BandInfo bandInfo : bandInfoList) {
                    if (bandInfo.getBandInformation() instanceof S2SpectralInformation) {
                        addVectorMask(product, tileList, maskInfo, (S2SpectralInformation) bandInfo.getBandInformation(), bandInfoList);
                    }
                }
            }
        }
    }

    private void addVectorMask(Product product, List<S2Metadata.Tile> tileList, MaskInfo maskInfo, S2SpectralInformation spectralInfo, List<BandInfo> bandInfoList) {
        List<EopPolygon> productPolygons = new ArrayList<>();

        boolean maskFilesFound = false;
        for (S2Metadata.Tile tile : tileList) {

            if (tile.getMaskFilenames() == null) {
                continue;
            }

            for (S2Metadata.MaskFilename maskFilename : tile.getMaskFilenames()) {

                // We are only interested in a single mask main type
                if (!maskFilename.getType().equals(maskInfo.getMainType())) {
                    continue;
                }

                if (spectralInfo != null) {
                    // We are only interested in masks for a certain band
                    if (!maskFilename.getBandId().equals(String.format("%s", spectralInfo.getBandId()))) {
                        continue;
                    }
                }

                maskFilesFound = true;

                // Read all polygons from the mask file
                GmlFilter gmlFilter = new GmlFilter();
                List<EopPolygon> polygonsForTile = gmlFilter.parse(maskFilename.getName()).getSecond();

                // We are interested only in a single subtype
                polygonsForTile = polygonsForTile.stream().filter(p -> p.getType().equals(maskInfo.getSubType())).collect(Collectors.toList());

                // Merge polygons from this tile to product polygon list
                productPolygons.addAll(polygonsForTile);
            }
        }

        if (!maskFilesFound) {
            return;
        }

        // TODO : why do we use this here ?
        final SimpleFeatureType type = Placemark.createGeometryFeatureType();
        // TODO : why "S2L1CMasks" ?
        final DefaultFeatureCollection collection = new DefaultFeatureCollection("S2L1CMasks", type);

        for (int index = 0; index < productPolygons.size(); index++) {
            Polygon polygon = productPolygons.get(index).getPolygon();

            Object[] data1 = {polygon, String.format("Polygon-%s", index)};
            SimpleFeatureImpl f1 = new SimpleFeatureImpl(data1, type, new FeatureIdImpl(String.format("F-%s", index)), true);
            collection.add(f1);
        }

        if (spectralInfo == null) {
            // This mask is not specific to a band
            // So we need one version of it for each resolution present in the band list
            for (S2SpatialResolution resolution : S2SpatialResolution.values()) {
                // Find a band with this resolution
                Band referenceBand = null;
                for (BandInfo bandInfo : bandInfoList) {
                    if (bandInfo.getBandInformation().getResolution() == resolution) {
                        referenceBand = product.getBand(bandInfo.getBandInformation().getPhysicalBand());
                        break;
                    }
                }

                // We may not find a band with this resolution
                if (referenceBand == null) {
                    continue;
                }

                // We need a different name for each resolution version
                String description = maskInfo.getDescription();
                String snapName = String.format("%s_%dm", maskInfo.getSnapName(), resolution.resolution);
                VectorDataNode vdn = new VectorDataNode(snapName, collection);
                vdn.setOwner(product);
                product.addMask(snapName,
                                vdn,
                                description,
                                maskInfo.getColor(),
                                maskInfo.getTransparency(),
                                referenceBand);
            }
        } else {
            // This mask is specific to a band
            Band referenceBand = product.getBand(spectralInfo.getPhysicalBand());
            String bandName = spectralInfo.getPhysicalBand();
            String snapName = maskInfo.getSnapNameForBand(bandName);
            String description = maskInfo.getDescriptionForBand(bandName);
            VectorDataNode vdn = new VectorDataNode(snapName, collection);
            vdn.setOwner(product);
            product.addMask(snapName,
                            vdn,
                            description,
                            maskInfo.getColor(),
                            maskInfo.getTransparency(),
                            referenceBand);
        }

    }

    private boolean isValidAngle(float value) {
        return !Float.isNaN(value) && !Float.isInfinite(value);
    }


    private S2BandAnglesGrid[] createS2OrthoAnglesGrids(S2Metadata metadataHeader, String tileId) throws IOException {

        S2BandAnglesGrid[] bandAnglesGrid = null;
        ArrayList<S2BandAnglesGrid> listBandAnglesGrid = new ArrayList<>();
        S2Metadata.Tile tile = metadataHeader.getTile(tileId);
        S2Metadata.AnglesGrid anglesGrid = tile.getSunAnglesGrid();
        int resolution = tile.getAnglesResolution();

        if (anglesGrid == null) {
            return bandAnglesGrid;
        }

        int gridHeight = tile.getSunAnglesGrid().getZenith().length;
        int gridWidth = tile.getSunAnglesGrid().getZenith()[0].length;
        float[] sunZeniths = new float[gridWidth * gridHeight];
        float[] sunAzimuths = new float[gridWidth * gridHeight];
        float[] viewingZeniths = new float[gridWidth * gridHeight];
        float[] viewingAzimuths = new float[gridWidth * gridHeight];
        int[] viewingZenithsCount = new int[gridWidth * gridHeight];
        int[] viewingAzimuthsCount = new int[gridWidth * gridHeight];

        Arrays.fill(viewingZeniths, Float.NaN);
        Arrays.fill(viewingAzimuths, Float.NaN);

        S2Metadata.AnglesGrid sunAnglesGrid = tile.getSunAnglesGrid();
        S2Metadata.AnglesGrid[] viewingIncidenceAnglesGrids = tile.getViewingIncidenceAnglesGrids();

        int iLastBandId = -1;
        int bandId;
        for (S2Metadata.AnglesGrid grid : viewingIncidenceAnglesGrids) {
            bandId = grid.getBandId();

            //if lastBand and the current band are different, the lecture of the last band has finished and we add it to listBandAnglesGrid
            //after that, the arrays are filled again with NaN
            if (iLastBandId != bandId) {
                if (iLastBandId >= 0) {
                    float[] zeniths = new float[gridWidth * gridHeight];
                    float[] azimuths = new float[gridWidth * gridHeight];
                    System.arraycopy(viewingZeniths, 0, zeniths, 0, gridWidth * gridHeight);
                    System.arraycopy(viewingAzimuths, 0, azimuths, 0, gridWidth * gridHeight);
                    listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_ZENITH_PREFIX, S2BandConstants.getBand(iLastBandId), gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, zeniths));
                    listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_AZIMUTH_PREFIX, S2BandConstants.getBand(iLastBandId), gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, azimuths));
                }
                Arrays.fill(viewingZeniths, Float.NaN);
                Arrays.fill(viewingAzimuths, Float.NaN);
                iLastBandId = bandId;
            }

            for (int y = 0; y < gridHeight; y++) {
                for (int x = 0; x < gridWidth; x++) {
                    final int index = y * gridWidth + x;
                    try {
                        if (y < grid.getZenith().length) {
                            if (x < grid.getZenith()[y].length) {
                                if (isValidAngle(grid.getZenith()[y][x])) {
                                    viewingZeniths[index] = grid.getZenith()[y][x];
                                }
                            }
                        }

                        if (y < grid.getAzimuth().length) {
                            if (x < grid.getAzimuth()[y].length) {
                                if (isValidAngle(grid.getAzimuth()[y][x])) {
                                    viewingAzimuths[index] = grid.getAzimuth()[y][x];
                                }
                            }
                        }

                    } catch (Exception e) {
                        logger.severe(StackTraceUtils.getStackTrace(e));
                    }
                }
            }
        }

        //add the last band which is in memory
        if (iLastBandId > 0) {
            float[] zeniths = new float[gridWidth * gridHeight];
            float[] azimuths = new float[gridWidth * gridHeight];
            System.arraycopy(viewingZeniths, 0, zeniths, 0, gridWidth * gridHeight);
            System.arraycopy(viewingAzimuths, 0, azimuths, 0, gridWidth * gridHeight);
            listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_ZENITH_PREFIX, S2BandConstants.getBand(iLastBandId), gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, zeniths));
            listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_AZIMUTH_PREFIX, S2BandConstants.getBand(iLastBandId), gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, azimuths));
        }

        //Compute the mean viewing angles
        Arrays.fill(viewingZeniths, 0.0f);
        Arrays.fill(viewingAzimuths, 0.0f);
        Arrays.fill(viewingZenithsCount, 0);
        Arrays.fill(viewingAzimuthsCount, 0);
        for (S2BandAnglesGrid grid : listBandAnglesGrid) {
            for(int i = 0 ; i < grid.getData().length ; i++) {
                if(grid.getPrefix().equals(VIEW_ZENITH_PREFIX) && isValidAngle(grid.getData()[i])) {
                    viewingZeniths[i] = viewingZeniths[i] + grid.getData()[i];
                    viewingZenithsCount[i]++;
                }
                if(grid.getPrefix().equals(VIEW_AZIMUTH_PREFIX) && isValidAngle(grid.getData()[i])) {
                    viewingAzimuths[i] = viewingAzimuths[i] + grid.getData()[i];
                    viewingAzimuthsCount[i]++;
                }
            }
        }
        for(int i = 0 ; i < viewingZeniths.length ; i++) {
            if(viewingZenithsCount[i] !=0) viewingZeniths[i] = viewingZeniths[i] / viewingZenithsCount[i];
            if(viewingAzimuthsCount[i] !=0)  viewingAzimuths[i] = viewingAzimuths[i] / viewingAzimuthsCount[i];
        }

        listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_ZENITH_PREFIX, null, gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, viewingZeniths));
        listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_AZIMUTH_PREFIX, null, gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, viewingAzimuths));


        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                final int index = y * gridWidth + x;
                sunZeniths[index] = sunAnglesGrid.getZenith()[y][x];
                sunAzimuths[index] = sunAnglesGrid.getAzimuth()[y][x];
            }
        }
        listBandAnglesGrid.add(new S2BandAnglesGrid(SUN_ZENITH_PREFIX, null, gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, sunZeniths));
        listBandAnglesGrid.add(new S2BandAnglesGrid(SUN_AZIMUTH_PREFIX, null, gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, sunAzimuths));

        bandAnglesGrid = listBandAnglesGrid.toArray(new S2BandAnglesGrid[listBandAnglesGrid.size()]);


        return bandAnglesGrid;
    }

    private BandInfo createBandInfoFromHeaderInfo(S2BandInformation bandInformation, Map<String, File> tileFileMap) {
        S2SpatialResolution spatialResolution = bandInformation.getResolution();
        return new BandInfo(tileFileMap,
                            bandInformation,
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

    private abstract class MultiLevelImageFactory {
        protected final AffineTransform imageToModelTransform;

        protected MultiLevelImageFactory(AffineTransform imageToModelTransform) {
            this.imageToModelTransform = imageToModelTransform;
        }

        public abstract MultiLevelImage createSourceImage(BandInfo bandInfo);
    }

    private class L1cSceneMultiLevelImageFactory extends MultiLevelImageFactory {

        private final S2OrthoSceneLayout sceneDescription;

        public L1cSceneMultiLevelImageFactory(S2OrthoSceneLayout sceneDescription, AffineTransform imageToModelTransform) {
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
        protected final S2OrthoSceneLayout sceneDescription;

        AbstractL1cSceneMultiLevelSource(S2OrthoSceneLayout sceneDescription, S2SpatialResolution bandResolution, AffineTransform imageToModelTransform, int numResolutions) {
            super(new DefaultMultiLevelModel(numResolutions,
                                             imageToModelTransform,
                                             sceneDescription.getSceneDimension(bandResolution).width,
                                             sceneDescription.getSceneDimension(bandResolution).height));
            this.sceneDescription = sceneDescription;
        }
    }

    /**
     * A MultiLevelSource used by bands for a scene made of multiple L1C tiles.
     */
    private final class BandL1cSceneMultiLevelSource extends AbstractL1cSceneMultiLevelSource {
        private final BandInfo bandInfo;

        public BandL1cSceneMultiLevelSource(S2OrthoSceneLayout sceneDescription, BandInfo bandInfo, AffineTransform imageToModelTransform) {
            super(sceneDescription, bandInfo.getBandInformation().getResolution(), imageToModelTransform, bandInfo.getImageLayout().numResolutions);
            this.bandInfo = bandInfo;
        }

        protected RenderedImage createImage(int level) {
            ArrayList<RenderedImage> tileImages = new ArrayList<>();

            for (String tileId : sceneDescription.getTileIds()) {
                /*
                 * Get the a PlanarImage of the tile at native resolution, with a [0,0] origin
                 */
                File imageFile = bandInfo.getTileIdToFileMap().get(tileId);

                // Get the band native resolution
                S2SpatialResolution bandNativeResolution = bandInfo.getBandInformation().getResolution();
                // Get the position of the L1C tile in full scene at level 0
                Rectangle l1cTileRectangleL0 = sceneDescription.getTilePositionInScene(tileId, bandNativeResolution);
                // Get the position of the L1C tile in full scene at current requested level
                //Rectangle l1cTileRectangle = DefaultMultiLevelSource.getLevelImageBounds(l1cTileRectangleL0, getModel().getScale(level));

                /*
                 * Iterate over internal JP2 tiles
                 */
                TileLayout l1cTileLayout = bandInfo.getImageLayout();
                for (int x = 0; x < l1cTileLayout.numXTiles; x++) {
                    for (int y = 0; y < l1cTileLayout.numYTiles; y++) {

                        // Get the position of the internal JP2 tile of L1C tile in full scene at current requested level
                        /*
                        Rectangle relativePositionL0 = new Rectangle(x*l1cTileLayout.width, y * l1cTileLayout.height, l1cTileLayout.width, l1cTileLayout.height);
                        Rectangle relativePosition = DefaultMultiLevelSource.getLevelImageBounds(relativePositionL0, getModel().getScale(level));
                        Rectangle absolutePosition = (Rectangle)relativePosition.clone();
                        absolutePosition.translate(l1cTileRectangle.x, l1cTileRectangle.y);
                        */

                        Rectangle internalJp2TileRectangleL0 = new Rectangle(
                                l1cTileRectangleL0.x + x * l1cTileLayout.tileWidth,
                                l1cTileRectangleL0.y + y * l1cTileLayout.tileHeight,
                                l1cTileLayout.tileWidth,
                                l1cTileLayout.tileHeight);
                        Rectangle internalJp2TileRectangle = DefaultMultiLevelSource.getLevelImageBounds(internalJp2TileRectangleL0, getModel().getScale(level));

                        PlanarImage opImage;
                        try {
                            TileLayout currentLayout = l1cTileLayout;
                            // The edge tiles dimensions may be less than the dimensions from JP2 header
                            // because the size of the image is not necessarily a multiple of the tile size
                            if (y == l1cTileLayout.numYTiles - 1 || x == l1cTileLayout.numXTiles - 1) {
                                currentLayout = new TileLayout(l1cTileLayout.width, l1cTileLayout.height,
                                                               l1cTileLayout.width - x * l1cTileLayout.tileWidth, l1cTileLayout.height - y * l1cTileLayout.tileHeight,
                                                               l1cTileLayout.numXTiles, l1cTileLayout.numYTiles, l1cTileLayout.numResolutions);
                            }
                            opImage = JP2TileOpImage.create(imageFile.toPath(), getCacheDir().toPath(),
                                                            0, y, x, currentLayout, getModel(), TYPE_USHORT, level);

                            if (opImage != null) {
                                opImage = TranslateDescriptor.create(opImage,
                                                                     (float) (internalJp2TileRectangle.x),
                                                                     (float) (internalJp2TileRectangle.y),
                                                                     Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);
                            }
                        } catch (IOException ex) {
                            opImage = ConstantDescriptor.create((float) internalJp2TileRectangle.width, (float) internalJp2TileRectangle.height, new Number[]{0}, null);
                        }
                        tileImages.add(opImage);
                    }
                }
                if (tileImages.isEmpty()) {
                    logger.warning("No tile images for mosaic");
                    return null;
                }
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

            /*
             * Adjust size of output image
             */
            // Get dimension at level 0
            S2SpatialResolution bandNativeResolution = bandInfo.getBandInformation().getResolution();
            Dimension bandDimensionLevel0 = sceneDescription.getSceneDimension(bandNativeResolution);
            // Compute dimension at level 'level' according to "J2K rule"
            Rectangle bandRectangle = DefaultMultiLevelSource.getLevelImageBounds(
                    new Rectangle(bandDimensionLevel0.width, bandDimensionLevel0.height),
                    getModel().getScale(level));
            // Crop accordingly
            RenderedOp croppedMosaic = CropDescriptor.create(mosaicOp,
                                                             0.0f, 0.0f, (float) bandRectangle.width, (float) bandRectangle.height,
                                                             new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));

            return croppedMosaic;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }



    @Override
    protected String[] getBandNames(S2SpatialResolution resolution) {
        String[] bandNames;

        switch (resolution) {
            case R10M:
                bandNames = new String[]{"B02", "B03", "B04", "B08"};
                break;
            case R20M:
                bandNames = new String[]{"B05", "B06", "B07", "B8A", "B11", "B12"};
                break;
            case R60M:
                bandNames = new String[]{"B01", "B09", "B10"};
                break;
            default:
                SystemUtils.LOG.warning("Invalid resolution: " + resolution);
                bandNames = null;
                break;
        }

        return bandNames;
    }
}
