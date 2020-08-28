package org.esa.s2tbx.dataio.muscate;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.s2.S2BandAnglesGrid;
import org.esa.s2tbx.dataio.s2.S2BandAnglesGridByDetector;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.ortho.S2AnglesGeometry;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.SAXException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.media.jai.PlanarImage;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.image.DataBuffer.TYPE_FLOAT;
import static org.esa.snap.utils.DateHelper.parseDate;

/**
 * Created by obarrile on 26/01/2017.
 */
public class MuscateProductReader extends AbstractProductReader implements S2AnglesGeometry {

    private static final Logger logger = Logger.getLogger(MuscateProductReader.class.getName());

    private VirtualDirEx productDirectory;
    private MuscateMetadata metadata;
    private List<GeoTiffImageReader> bandImageReaders;
    private ImageInputStreamSpi imageInputStreamSpi;

    public MuscateProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.productDirectory = VirtualDirEx.build(productPath, false, false);

            ProductFilePathsHelper filePathsHelper = new ProductFilePathsHelper(this.productDirectory);

            this.metadata = filePathsHelper.getMetadata();

            ProductSubsetDef subsetDef = getSubsetDef();
            int defaultProductWidth = metadata.getRasterWidth();
            int defaultProductHeight = metadata.getRasterHeight();
            GeoCoding productDefaultGeoCoding = null;
            Rectangle productBounds;

            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productBounds = new Rectangle(0, 0, defaultProductWidth, defaultProductHeight);
            } else {
                productDefaultGeoCoding = metadata.buildCrsGeoCoding(null);
                boolean isMultiSize = filePathsHelper.isMultiSize();
                productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, isMultiSize);
            }
            if (productBounds.isEmpty()) {
                throw new IllegalStateException("Empty product bounds.");
            }

            // create product
            Product product = new Product(this.metadata.getProductName(), MuscateConstants.MUSCATE_FORMAT_NAMES[0], productBounds.width, productBounds.height);
            product.setDescription(metadata.getDescription());
            if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                product.getMetadataRoot().addElement(metadata.getRootElement());
            }
            product.setFileLocation(productPath.toFile());
            product.setSceneGeoCoding(metadata.buildCrsGeoCoding(productBounds));
            product.setNumResolutionsMax(metadata.getGeoPositions().size());
            product.setAutoGrouping(buildGroupPattern());
            product.setStartTime(parseDate(metadata.getAcquisitionDate(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            product.setEndTime(parseDate(metadata.getAcquisitionDate(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

            this.bandImageReaders = new ArrayList<>();

            // add bands
            for (MuscateImage muscateImage : metadata.getImages()) {
                if (muscateImage == null || muscateImage.nature == null) {
                    logger.warning(String.format("Unable to add an image with a null nature to the product: %s", product.getName()));
                } else {
                    List<Band> imageBands = readImageBands(muscateImage, defaultProductWidth, defaultProductHeight, filePathsHelper, productDefaultGeoCoding);
                    for (Band band : imageBands) {
                        product.addBand(band);
                    }
                }
            }

            List<Band> angleBands = readAngleBands(productBounds, defaultProductWidth, defaultProductHeight, metadata, subsetDef);
            for (Band band : angleBands) {
                product.addBand(band);
            }

            // add masks
            for (MuscateMask muscateMask : metadata.getMasks()) {
                if (muscateMask == null || muscateMask.nature == null) {
                    logger.warning(String.format("Unable to add a mask with a null nature to the product: %s", product.getName()));
                } else {
                    boolean isMultiSize = filePathsHelper.isMultiSize();
                    readMaskBands(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, filePathsHelper, muscateMask);
                }
            }

            success = true;

            return product;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        } finally {
            if (!success) {
                closeResources();
            }
        }
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) {
        // do nothing
    }

    @Override
    public S2BandAnglesGridByDetector[] getViewingIncidenceAnglesGrids(int bandId, int detectorId) {
        if (this.metadata == null) {
            return null;
        }
        MuscateMetadata.AnglesGrid[] viewingAnglesList = metadata.getViewingAnglesGrid();
        S2BandConstants bandConstants = S2BandConstants.getBand(bandId);

        for (MuscateMetadata.AnglesGrid viewingAngles : viewingAnglesList) {
            if (viewingAngles.getBandId().equals(bandConstants.getPhysicalName()) && Integer.parseInt(viewingAngles.getDetectorId()) == detectorId) {
                S2BandAnglesGridByDetector[] bandAnglesGridByDetector = new S2BandAnglesGridByDetector[2];
                bandAnglesGridByDetector[0] = new S2BandAnglesGridByDetector("view_zenith", bandConstants, detectorId, viewingAngles.getWidth(), viewingAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, viewingAngles.getResolutionX(), viewingAngles.getResolutionY(), viewingAngles.getZenith());
                bandAnglesGridByDetector[1] = new S2BandAnglesGridByDetector("view_azimuth", bandConstants, detectorId, viewingAngles.getWidth(), viewingAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, viewingAngles.getResolutionX(), viewingAngles.getResolutionY(), viewingAngles.getAzimuth());
                return bandAnglesGridByDetector;
            }
        }

        return null;
    }

    @Override
    public S2BandAnglesGrid[] getSunAnglesGrid() {
        if (this.metadata == null) {
            return null;
        }
        MuscateMetadata.AnglesGrid sunAngles = metadata.getSunAnglesGrid();

        S2BandAnglesGrid[] bandAnglesGrid = new S2BandAnglesGrid[2];
        bandAnglesGrid[0] = new S2BandAnglesGrid("sun_zenith", null, sunAngles.getWidth(), sunAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, sunAngles.getResolutionX(), sunAngles.getResolutionY(), sunAngles.getZenith());
        bandAnglesGrid[1] = new S2BandAnglesGrid("sun_azimuth", null, sunAngles.getWidth(), sunAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, sunAngles.getResolutionX(), sunAngles.getResolutionY(), sunAngles.getAzimuth());
        return bandAnglesGrid;
    }

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    private void closeResources() {
        try {
            if (this.bandImageReaders != null) {
                for (GeoTiffImageReader geoTiffImageReader : this.bandImageReaders) {
                    try {
                        geoTiffImageReader.close();
                    } catch (Exception ignore) {
                        // ignore
                    }
                }
                this.bandImageReaders.clear();
                this.bandImageReaders = null;
            }
        } finally {
            try {
                if (this.imageInputStreamSpi != null) {
                    ImageRegistryUtils.deregisterImageInputStreamSpi(this.imageInputStreamSpi);
                    this.imageInputStreamSpi = null;
                }
            } finally {
                if (this.productDirectory != null) {
                    this.productDirectory.close();
                    this.productDirectory = null;
                }
            }
        }
        System.gc();
    }

    private List<Band> readImageBands(MuscateImage muscateImage, int defaultProductWidth, int defaultProductHeight, ProductFilePathsHelper filePathsHelper, GeoCoding productDefaultGeoCoding)
            throws Exception {

        List<Band> productBands = new ArrayList<>();
        //TODO Read together AOT and WVC? they should be in the same tif file
        if (muscateImage.nature.equals(MuscateImage.AEROSOL_OPTICAL_THICKNESS_IMAGE)) {
            for (String tiffImageRelativeFilePath : muscateImage.getImageFiles()) {
                Band geoTiffBand = readAOTImageBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, filePathsHelper);
                if (geoTiffBand != null) {
                    productBands.add(geoTiffBand);
                }
            }
        } else if (muscateImage.nature.equals(MuscateImage.FLAT_REFLECTANCE_IMAGE)) {
            BandNameCallback bandNameCallback = buildFlatReflectanceImageBandNameCallback();
            for (String tiffImageRelativeFilePath : muscateImage.getImageFiles()) {
                Band geoTiffBand = readReflectanceImageBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, filePathsHelper, bandNameCallback);
                if (geoTiffBand != null) {
                    String bandId = getBandFromFileName(tiffImageRelativeFilePath);
                    geoTiffBand.setDescription(String.format("Ground reflectance with the correction of slope effects, band %s", bandId));
                    productBands.add(geoTiffBand);
                }
            }
        } else if (muscateImage.nature.equals(MuscateImage.SURFACE_REFLECTANCE_IMAGE)) {
            BandNameCallback bandNameCallback = buildSurfaceReflectanceImageBandNameCallback();
            for (String tiffImageRelativeFilePath : muscateImage.getImageFiles()) {
                Band geoTiffBand = readReflectanceImageBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, filePathsHelper, bandNameCallback);
                if (geoTiffBand != null) {
                    String bandId = getBandFromFileName(tiffImageRelativeFilePath);
                    geoTiffBand.setDescription(String.format("Ground reflectance without the correction of slope effects, band %s", bandId));
                    productBands.add(geoTiffBand);
                }
            }
        } else if (muscateImage.nature.equals(MuscateImage.WATER_VAPOR_CONTENT_IMAGE)) {
            for (String tiffImageRelativeFilePath : muscateImage.getImageFiles()) {
                Band geoTiffBand = readWVCImageBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, filePathsHelper);
                if (geoTiffBand != null) {
                    productBands.add(geoTiffBand);
                }
            }
        } else {
            logger.warning(String.format("Unable to add image. Unknown nature: %s", muscateImage.nature));
        }
        return productBands;
    }

    private void readMaskBands(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight,
                               ProductFilePathsHelper filePathsHelper, MuscateMask muscateMask)
                               throws Exception {

        // the mask depends on the version
        float versionFloat = metadata.getVersion();// (version == null) ? 0.0f : Float.valueOf(version);
        Set<String> addedFiles = new HashSet<>();

        if (muscateMask.nature.equals(MuscateMask.AOT_INTERPOLATION_MASK)) {
            int bitNumber = 0;
            if (versionFloat > MuscateMask.AOT_INTERPOLATION_MASK_VERSION) {
                bitNumber = 1; // after this version the bitNumber has changed
            }
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                readAOTMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper, bitNumber);
            }
        } else if (muscateMask.nature.equals(MuscateMask.DETAILED_CLOUD_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    readCloudMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.CLOUD_MASK)) {
            if (versionFloat < MuscateMask.CLOUD_MASK_VERSION) {
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    readCloudMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper);
                }
            } else {
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper, MuscateConstants.GEOPHYSICAL_BIT.Cloud);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.CLOUD_SHADOW_MASK)) {
            if (versionFloat < MuscateMask.CLOUD_SHADOW_MASK_VERSION) {
                // in some old products the Nature is Cloud_Shadow instead of Geophysics. Perhaps an error?
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper);
                }
            } else {
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper, MuscateConstants.GEOPHYSICAL_BIT.Cloud_Shadow);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.EDGE_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    readEdgeMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.SATURATION_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    readSaturationMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.GEOPHYSICS_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.DETECTOR_FOOTPRINT_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add( muscateMaskFile.path)) {
                    readDetectorFootprintMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight,  muscateMaskFile.path, filePathsHelper);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.DEFECTIVE_PIXEL_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    readDefectivePixelMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.HIDDEN_SURFACE_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper, MuscateConstants.GEOPHYSICAL_BIT.Hidden_Surface);
            }
        } else if (muscateMask.nature.equals(MuscateMask.SNOW_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper, MuscateConstants.GEOPHYSICAL_BIT.Snow);
            }
        } else if (muscateMask.nature.equals(MuscateMask.SUN_TOO_LOW_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper, MuscateConstants.GEOPHYSICAL_BIT.Sun_Too_Low);
            }
        } else if (muscateMask.nature.equals(MuscateMask.TANGENT_SUN_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper, MuscateConstants.GEOPHYSICAL_BIT.Tangent_Sun);
            }
        } else if (muscateMask.nature.equals(MuscateMask.TOPOGRAPHY_SHADOW_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper, MuscateConstants.GEOPHYSICAL_BIT.Topography_Shadow);
            }
        } else if (muscateMask.nature.equals(MuscateMask.WATER_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                readGeophysicsMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper, MuscateConstants.GEOPHYSICAL_BIT.Water);
            }
        } else if (muscateMask.nature.equals(MuscateMask.WVC_INTERPOLATION_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                readWVCMask(product, productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, muscateMaskFile.path, filePathsHelper);
            }
        } else {
            logger.warning(String.format("Unable to add mask. Unknown nature: %s", muscateMask.nature));
        }
    }

    private boolean isMaskAccepted(String maskName) {
        ProductSubsetDef subsetDef = getSubsetDef();
        return (subsetDef == null || subsetDef.isNodeAccepted(maskName));
    }

    private GeoTiffBandResult readGeoTiffProductBand(GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                                                     int bandIndex, ProductFilePathsHelper filePathsHelper,
                                                     BandNameCallback bandNameCallback, Double noDataValue)
                                                     throws Exception {

        String tiffImageFilePath = filePathsHelper.computeImageRelativeFilePath(tiffImageRelativeFilePath);
        GeoTiffBandResult geoTiffBandResult = null;
        boolean success = false;
        GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(this.productDirectory.getBaseFile().toPath(), tiffImageFilePath);
        try {
            // the tiff image exists and read the data
            int defaultBandWidth = geoTiffImageReader.getImageWidth();
            int defaultBandHeight = geoTiffImageReader.getImageHeight();
            MuscateMetadata.Geoposition geoPosition = filePathsHelper.getMetadata().getGeoposition(defaultBandWidth, defaultBandHeight);
            if (geoPosition == null) {
                logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product.", tiffImageRelativeFilePath));
            } else {
                ProductSubsetDef subsetDef = getSubsetDef();
                String bandName = bandNameCallback.buildBandName(geoPosition, tiffImageRelativeFilePath);// bandNamePrefix + geoPosition.id;
                if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                    Rectangle bandBounds;
                    if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                        bandBounds = new Rectangle(defaultBandWidth, defaultBandHeight);
                    } else {
                        GeoCoding bandDefaultGeoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
                        boolean isMultiSize = filePathsHelper.isMultiSize();
                        bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, bandDefaultGeoCoding, defaultProductWidth, defaultProductHeight,
                                                                                        defaultBandWidth, defaultBandHeight, isMultiSize);
                    }
                    if (!bandBounds.isEmpty()) {
                        // there is an intersection
                        GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(getReaderPlugIn(), null);
                        Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, null, bandBounds, noDataValue);
                        Band geoTiffBand = geoTiffProduct.getBandAt(bandIndex);
                        geoTiffBand.setName(bandName);
                        geoTiffBandResult = new GeoTiffBandResult(geoTiffBand, geoPosition);
                        success = true;
                    }
                }
            }
        } catch (IOException e) {
            logger.warning(String.format("Unable to get band %d of the product: %s", bandIndex, tiffImageRelativeFilePath));
        } catch (Exception e) {
            throw e;
        } finally {
            if (success) {
                this.bandImageReaders.add(geoTiffImageReader);
            } else {
                geoTiffImageReader.close();
            }
        }
        return geoTiffBandResult;
    }

    private Band readAOTImageBand(GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                                  ProductFilePathsHelper filePathsHelper)
                                  throws Exception {

        double noDataValue = filePathsHelper.getMetadata().getAOTNoDataValue();
        BandNameCallback bandNameCallback = buildAOTImageBandNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 1,
                                                                     filePathsHelper, bandNameCallback, noDataValue);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();
            geoTiffBand.setNoDataValue(noDataValue);
            geoTiffBand.setNoDataValueUsed(true);
            geoTiffBand.setScalingFactor(1.0d / filePathsHelper.getMetadata().getAOTQuantificationValue());
            geoTiffBand.setScalingOffset(0.0d);
            geoTiffBand.setDescription(String.format("Aerosol Optical Thickness at %.0fm resolution", geoTiffBandResult.getGeoPosition().xDim));
            return geoTiffBand;
        }
        return null;
    }

    private Band readWVCImageBand(GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper)
                                  throws Exception {

        double noDataValue = filePathsHelper.getMetadata().getWVCNoDataValue();
        BandNameCallback bandNameCallback = buildWVCImageBandNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, bandNameCallback, noDataValue);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();
            geoTiffBand.setNoDataValue(noDataValue);
            geoTiffBand.setNoDataValueUsed(true);
            geoTiffBand.setScalingFactor(1.0d / filePathsHelper.getMetadata().getWVCQuantificationValue());
            geoTiffBand.setScalingOffset(0.0d);
            geoTiffBand.setUnit("cm"); //TODO verify
            geoTiffBand.setDescription(String.format("Water vapor content at %.0fm resolution in %s", geoTiffBandResult.getGeoPosition().xDim, geoTiffBand.getUnit()));
            return geoTiffBand;
        }
        return null;
    }

    private Band readReflectanceImageBand(GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                                          ProductFilePathsHelper filePathsHelper, BandNameCallback bandNameCallback)
            throws Exception {

        double noDataValue = filePathsHelper.getMetadata().getReflectanceNoDataValue();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, bandNameCallback, noDataValue);
        if (geoTiffBandResult != null) {
            String bandId = getBandFromFileName(tiffImageRelativeFilePath);
            Band geoTiffBand = geoTiffBandResult.getBand();
            geoTiffBand.setNoDataValue(noDataValue);
            geoTiffBand.setNoDataValueUsed(true);
            geoTiffBand.setSpectralWavelength(filePathsHelper.getMetadata().getCentralWavelength(bandId)); //not available in metadata
            geoTiffBand.setScalingFactor(1.0d / filePathsHelper.getMetadata().getReflectanceQuantificationValue());
            geoTiffBand.setScalingOffset(0.0d);
            return geoTiffBand;
        }
        return null;
    }

    private void readAOTMask(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                             ProductFilePathsHelper filePathsHelper, int bitNumber)
                             throws Exception {

        BandNameCallback maskBandNameCallback = buildAOTMaskBandNamesCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, maskBandNameCallback, null);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();
            if (!product.containsBand(geoTiffBand.getName())) {
                geoTiffBand.setNoDataValueUsed(false);
                geoTiffBand.setScalingFactor(1);
                geoTiffBand.setScalingOffset(0);
                geoTiffBand.setDescription("Interpolated pixels mask");

                product.addBand(geoTiffBand);
            }
            String maskName = computeAOTMaskName(geoTiffBandResult.getGeoPosition()); // "AOT_Interpolation_Mask_" + geoTiffBandResult.getGeoPosition().id;
            if (isMaskAccepted(maskName) && !product.getMaskGroup().contains(maskName)) {
                Mask mask = buildMaskFromBand(geoTiffBand, maskName, "Interpolated AOT pixels mask", String.format("bit_set(%s,%d)", geoTiffBand.getName(), bitNumber), Color.BLUE);
                product.addMask(mask);
            }
        }
    }

    private void readWVCMask(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                             ProductFilePathsHelper filePathsHelper)
                             throws Exception {

        BandNameCallback maskBandNameCallback = buildWVCMaskNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, maskBandNameCallback, null);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();
            if (!product.containsBand(geoTiffBand.getName())) {
                geoTiffBand.setNoDataValueUsed(false);
                geoTiffBand.setScalingFactor(1);
                geoTiffBand.setScalingOffset(0);
                geoTiffBand.setDescription("Interpolated pixels mask");

                product.addBand(geoTiffBand);
            }
            String maskName = computeWVCMaskName(geoTiffBandResult.getGeoPosition()); // "WVC_Interpolation_Mask_" + geoTiffBandResult.getGeoPosition().id;
            if (isMaskAccepted(maskName) && !product.getMaskGroup().contains(maskName)) {
                Mask mask = buildMaskFromBand(geoTiffBand, maskName, "Interpolated WVC pixels mask", String.format("bit_set(%s,0)", geoTiffBand.getName()), Color.BLUE);
                product.addMask(mask);
            }
        }
    }

    private void readEdgeMask(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                              ProductFilePathsHelper filePathsHelper)
                              throws Exception {

        BandNameCallback maskBandNameCallback = buildEdgeMaskBandNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, maskBandNameCallback, null);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();
            geoTiffBand.setNoDataValueUsed(false);
            geoTiffBand.setScalingFactor(1);
            geoTiffBand.setScalingOffset(0);
            geoTiffBand.setDescription("Edge mask");

            product.addBand(geoTiffBand);

            String maskName = computeEdgeMaskName(geoTiffBandResult.getGeoPosition()); // "edge_mask_" + geoTiffBandResult.getGeoPosition().id;
            if (isMaskAccepted(maskName) && !product.getMaskGroup().contains(maskName)) {
                Mask mask = buildMaskFromBand(geoTiffBand, maskName, "Edge mask", String.format("bit_set(%s,0)", geoTiffBand.getName()), Color.GREEN);
                product.addMask(mask);
            }
        }
    }

    private void readSaturationMask(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                                    ProductFilePathsHelper filePathsHelper)
                                    throws Exception {

        BandNameCallback maskBandNameCallback = buildSaturationMaskBandNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, maskBandNameCallback, null);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();
            geoTiffBand.setNoDataValueUsed(false);
            geoTiffBand.setScalingFactor(1);
            geoTiffBand.setScalingOffset(0);
            geoTiffBand.setDescription("Saturation mask coded over 8 bits, 1 bit per spectral band (number of useful bits = number of spectral bands)");
            product.addBand(geoTiffBand);

            List<String> bands = filePathsHelper.getMetadata().getBandNames(geoTiffBandResult.getGeoPosition().id);
            for (int bitCount=0; bitCount<bands.size(); bitCount++) {
                String bandId = bands.get(bitCount);
                String maskName = computeSaturationMaskName(bandId);
                if (isMaskAccepted(maskName) && !product.getMaskGroup().contains(maskName)) {
                    Mask mask = buildMaskFromBand(geoTiffBand, maskName, String.format("Saturation mask of band %s", bandId), String.format("bit_set(%s,%d)", geoTiffBand.getName(), bitCount), Color.RED);
                    product.addMask(mask);
                }
            }
        }
    }

    private void readDetectorFootprintMask(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                                           ProductFilePathsHelper filePathsHelper)
                                           throws Exception {

        BandNameCallback maskBandNameCallback = buildDetectorFootprintMaskBandNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, maskBandNameCallback, null);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();
            geoTiffBand.setNoDataValueUsed(false);
            geoTiffBand.setDescription("Detector footprint");
            product.addBand(geoTiffBand);

            // add masks
            ColorIterator.reset();
            String[] orderedBandNames = filePathsHelper.getMetadata().getOrderedBandNames(geoTiffBandResult.getGeoPosition().id);
            for (int i = 0; i < orderedBandNames.length; i++) {
                String maskName = computeDetectorFootprintMaskName(tiffImageRelativeFilePath, orderedBandNames[i]);
                if (isMaskAccepted(maskName) && !product.getMaskGroup().contains(maskName)) {
                    Mask mask = buildMaskFromBand(geoTiffBand, maskName, "Detector footprint", String.format("bit_set(%s,%d)", geoTiffBand.getName(), i), ColorIterator.next());
                    product.addMask(mask);
                }
            }
        }
    }

    private void readCloudMask(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                               ProductFilePathsHelper filePathsHelper)
                               throws Exception {

        BandNameCallback maskBandNameCallback = buildCloudMaskBandNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, maskBandNameCallback, null);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();
            // add band to product if it hasn't been added yet
            if (!product.containsBand(geoTiffBand.getName())) {
                geoTiffBand.setNoDataValueUsed(false);
                geoTiffBand.setScalingFactor(1);
                geoTiffBand.setScalingOffset(0);
                geoTiffBand.setDescription("Cloud mask computed by MACCS software, made of 1 band coded over 8 useful bits");
                product.addBand(geoTiffBand);
            }

            // add masks
            ColorIterator.reset();
            String bandName = geoTiffBand.getName();
            MuscateMetadata.Geoposition geoposition = geoTiffBandResult.getGeoPosition();

            String maskName0 = computeCloudMaskAllName(geoposition);
            if (isMaskAccepted(maskName0) && !product.getMaskGroup().contains(maskName0)) {
                Mask mask0 = buildMaskFromBand(geoTiffBand, computeCloudMaskAllName(geoposition), "Result of a 'logical OR' for all the cloud and shadow maks", String.format("bit_set(%s,0)", bandName), ColorIterator.next());
                product.addMask(mask0);
            }

            String maskName1 = computeCloudMaskAllCloudName(geoposition);
            if (isMaskAccepted(maskName1) && !product.getMaskGroup().contains(maskName1)) {
                Mask mask1 = buildMaskFromBand(geoTiffBand, maskName1, "Result of a 'logical OR' for all the cloud masks", String.format("bit_set(%s,1)", bandName), ColorIterator.next());
                product.addMask(mask1);
            }

            String maskName2 = computeCloudMaskReflectanceName(geoposition);
            if (isMaskAccepted(maskName2) && !product.getMaskGroup().contains(maskName2)) {
                Mask mask2 = buildMaskFromBand(geoTiffBand, maskName2, "Cloud mask identified by a reflectance threshold", String.format("bit_set(%s,2)", bandName), ColorIterator.next());
                product.addMask(mask2);
            }

            String maskName3 = computeCloudMaskReflectanceVarianceName(geoposition);
            if (isMaskAccepted(maskName3) && !product.getMaskGroup().contains(maskName3)) {
                Mask mask3 = buildMaskFromBand(geoTiffBand, maskName3, "Cloud mask identified by a threshold on reflectance variance", String.format("bit_set(%s,3)", bandName), ColorIterator.next());
                product.addMask(mask3);
            }

            String maskName4 = computeCloudMaskExtensionName(geoposition);
            if (isMaskAccepted(maskName4) && !product.getMaskGroup().contains(maskName4)) {
                Mask mask4 = buildMaskFromBand(geoTiffBand, maskName4, "Cloud mask identified by the extension of cloud masks", String.format("bit_set(%s,4)", bandName), ColorIterator.next());
                product.addMask(mask4);
            }

            String maskName5 = computeCloudMaskInsideShadowName(geoposition);
            if (isMaskAccepted(maskName5) && !product.getMaskGroup().contains(maskName5)) {
                Mask mask5 = buildMaskFromBand(geoTiffBand, maskName5, "Shadow mask of clouds inside the image", String.format("bit_set(%s,5)", bandName), ColorIterator.next());
                product.addMask(mask5);
            }

            String maskName6 = computeCloudMaskOutsideShadowName(geoposition);
            if (isMaskAccepted(maskName6) && !product.getMaskGroup().contains(maskName6)) {
                Mask mask6 = buildMaskFromBand(geoTiffBand, computeCloudMaskOutsideShadowName(geoposition), "Shadow mask of clouds outside the image", String.format("bit_set(%s,6)", bandName), ColorIterator.next());
                product.addMask(mask6);
            }

            String maskName7 = computeCloudMaskCirrusName(geoposition);
            if (isMaskAccepted(maskName7) && !product.getMaskGroup().contains(maskName7)) {
                Mask mask7 = buildMaskFromBand(geoTiffBand, computeCloudMaskCirrusName(geoposition), "Cloud mask identified with the cirrus spectral band", String.format("bit_set(%s,7)", bandName), ColorIterator.next());
                product.addMask(mask7);
            }
        }
    }

    private void readGeophysicsMask(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                                    ProductFilePathsHelper filePathsHelper)
                                    throws Exception {

        BandNameCallback maskBandNamesCallback = buildGeophysicsMaskBandNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, maskBandNamesCallback, null);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();// readGeoTiffProductBand(tiffImageRelativeFilePath, 0);
            if (!product.containsBand(geoTiffBand.getName())) {
                geoTiffBand.setNoDataValueUsed(false);
                geoTiffBand.setScalingFactor(1);
                geoTiffBand.setScalingOffset(0);
                geoTiffBand.setDescription("Geophysical mask of level 2, made of 1 band coded over 8 useful bits");
                product.addBand(geoTiffBand);
            }

            for (MuscateConstants.GEOPHYSICAL_BIT geophysicalBit : MuscateConstants.GEOPHYSICAL_BIT.values()) {
                String maskName = computeGeographicMaskName(geophysicalBit, geoTiffBandResult.getGeoPosition());
                if (isMaskAccepted(maskName) && !product.getMaskGroup().contains(maskName)) {
                    Mask mask = buildGeophysicsMask(geophysicalBit, geoTiffBand, maskName);
                    product.addMask(mask);
                }
            }
        }
    }

    private void readGeophysicsMask(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight, String tiffImageRelativeFilePath,
                                    ProductFilePathsHelper filePathsHelper, MuscateConstants.GEOPHYSICAL_BIT geophysicalBit)
                                    throws Exception {

        BandNameCallback maskBandNamesCallback = buildGeophysicsMaskBandNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, maskBandNamesCallback, null);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();
            if (!product.containsBand(geoTiffBand.getName())) {
                geoTiffBand.setNoDataValueUsed(false);
                geoTiffBand.setScalingFactor(1);
                geoTiffBand.setScalingOffset(0);
                geoTiffBand.setDescription("Geophysical mask of level 2, made of 1 band coded over 8 useful bits");
                product.addBand(geoTiffBand);
            }
            String maskName = computeGeographicMaskName(geophysicalBit, geoTiffBandResult.getGeoPosition());
            if (isMaskAccepted(maskName) && !product.getMaskGroup().contains(maskName)) {
                Mask mask = buildGeophysicsMask(geophysicalBit, geoTiffBandResult.getBand(), maskName);
                product.addMask(mask);
            }
        }
    }

    private void readDefectivePixelMask(Product product, GeoCoding productDefaultGeoCoding, int defaultProductWidth, int defaultProductHeight,
                                        String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper)
                                        throws Exception {

        BandNameCallback maskBandNamesCallback = buildDefectivePixelMaskBandNameCallback();
        GeoTiffBandResult geoTiffBandResult = readGeoTiffProductBand(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, tiffImageRelativeFilePath, 0,
                                                                     filePathsHelper, maskBandNamesCallback, null);
        if (geoTiffBandResult != null) {
            Band geoTiffBand = geoTiffBandResult.getBand();// readGeoTiffProductBand(tiffImageRelativeFilePath, 0);
            geoTiffBand.setNoDataValueUsed(false);
            geoTiffBand.setDescription("Defective Pixel");
            product.addBand(geoTiffBand);

            // add masks
            ColorIterator.reset();
            MuscateMetadata.Geoposition geoposition = geoTiffBandResult.getGeoPosition();// metadata.getGeoposition(width, height);
            String[] orderedBandNames = metadata.getOrderedBandNames(geoposition.id);
            for (int i = 0; i < orderedBandNames.length; i++) {
                String maskName = computeDefectivePixelMaskName(orderedBandNames[i]);
                if (isMaskAccepted(maskName) && !product.getMaskGroup().contains(maskName)) {
                    Mask mask = buildMaskFromBand(geoTiffBand, maskName, "Defective pixel", String.format("bit_set(%s,%d)", geoTiffBand.getName(), i), ColorIterator.next());
                    product.addMask(mask);
                }
            }
        }
    }

    public static MuscateProductReader.BandNameCallback buildAOTMaskBandNamesCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> "Aux_IA_" + geoPosition.id;
    }

    public static MuscateProductReader.BandNameCallback buildWVCMaskNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> "Aux_IA_" + geoPosition.id;
    }

    public static MuscateProductReader.BandNameCallback buildEdgeMaskBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> "Aux_Mask_Edge_" + geoPosition.id;
    }

    public static String computeAOTMaskName(MuscateMetadata.Geoposition geoPosition) {
        return "AOT_Interpolation_Mask_" + geoPosition.id;
    }

    public static String computeWVCMaskName(MuscateMetadata.Geoposition geoPosition) {
        return "WVC_Interpolation_Mask_" + geoPosition.id;
    }

    public static String computeEdgeMaskName(MuscateMetadata.Geoposition geoPosition) {
        return "edge_mask_" + geoPosition.id;
    }

    public static MuscateProductReader.BandNameCallback buildSaturationMaskBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> "Aux_Mask_Saturation_" + geoPosition.id;
    }

    public static MuscateProductReader.BandNameCallback buildDetectorFootprintMaskBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> {
            int detector = getDetectorFromFilename(tiffImageRelativeFilePath);
            return String.format("Aux_Mask_Detector_Footprint_%s_%02d", geoPosition.id, detector);
        };
    }

    public static String computeSaturationMaskName(String bandId) {
        return "saturation_" + bandId;
    }

    public static String computeDetectorFootprintMaskName(String tiffImageRelativeFilePath, String orderedBandName) {
        int detector = getDetectorFromFilename(tiffImageRelativeFilePath);
        return String.format("detector_footprint-%s-%02d", formatBandNameTo3Characters(orderedBandName), detector);
    }

    public static MuscateProductReader.BandNameCallback buildCloudMaskBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> "Aux_Mask_Cloud_" + geoPosition.id;
    }

    public static String computeCloudMaskAllName(MuscateMetadata.Geoposition geoPosition) {
        return "cloud_mask_all_" + geoPosition.id;
    }

    public static String computeCloudMaskAllCloudName(MuscateMetadata.Geoposition geoPosition) {
        return "cloud_mask_all_cloud_" + geoPosition.id;
    }

    public static String computeCloudMaskReflectanceName(MuscateMetadata.Geoposition geoPosition) {
        return "cloud_mask_refl_" + geoPosition.id;
    }

    public static String computeCloudMaskReflectanceVarianceName(MuscateMetadata.Geoposition geoPosition) {
        return "cloud_mask_refl_var_" + geoPosition.id;
    }

    public static String computeCloudMaskExtensionName(MuscateMetadata.Geoposition geoPosition) {
        return "cloud_mask_extension_" + geoPosition.id;
    }

    public static String computeCloudMaskInsideShadowName(MuscateMetadata.Geoposition geoPosition) {
        return "cloud_mask_shadow_" + geoPosition.id;
    }

    public static String computeCloudMaskOutsideShadowName(MuscateMetadata.Geoposition geoPosition) {
        return "cloud_mask_sahdvar_" + geoPosition.id;
    }

    public static String computeCloudMaskCirrusName(MuscateMetadata.Geoposition geoPosition) {
        return "cloud_mask_cirrus_" + geoPosition.id;
    }

    public static MuscateProductReader.BandNameCallback buildGeophysicsMaskBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> "Aux_Mask_MG2_" + geoPosition.id;
    }

    public static MuscateProductReader.BandNameCallback buildDefectivePixelMaskBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> "Aux_Mask_Defective_Pixel_" + geoPosition.id;
    }

    public static String computeDefectivePixelMaskName(String orderedBandName) {
        return String.format("defective_%s", orderedBandName);
    }

    private static List<Band> readAngleBands(Rectangle productBounds, int defaultProductWidth, int defaultProductHeight, MuscateMetadata metadata, ProductSubsetDef subsetDef) {
        List<Band> angleBands = new ArrayList<>();

        MuscateMetadata.AnglesGrid sunAnglesGrid = metadata.getSunAnglesGrid();
        Band band;
        // add Zenith
        if(subsetDef == null || subsetDef.isNodeAccepted("sun_zenith")) {
            band = readAngleBand(productBounds, defaultProductWidth, defaultProductHeight, "sun_zenith", "Sun zenith angles", sunAnglesGrid.getSize(),
                                      sunAnglesGrid.getZenith(), sunAnglesGrid.getResolution(), metadata, subsetDef);
            angleBands.add(band);
        }

        // add Azimuth
        if(subsetDef == null || subsetDef.isNodeAccepted("sun_azimuth")) {
            band = readAngleBand(productBounds, defaultProductWidth, defaultProductHeight, "sun_azimuth", "Sun azimuth angles", sunAnglesGrid.getSize(),
                                 sunAnglesGrid.getAzimuth(), sunAnglesGrid.getResolution(), metadata, subsetDef);
            angleBands.add(band);
        }

        // viewing angles
        for (String bandId : metadata.getBandNames()) {
            MuscateMetadata.AnglesGrid anglesGrid = metadata.getViewingAnglesGrid(bandId);
            // add Zenith
            String bandNameZenith = "view_zenith_" + anglesGrid.getBandId();
            if(subsetDef == null || subsetDef.isNodeAccepted(bandNameZenith)) {
                band = readAngleBand(productBounds, defaultProductWidth, defaultProductHeight, bandNameZenith, "Viewing zenith angles", anglesGrid.getSize(),
                                     anglesGrid.getZenith(), anglesGrid.getResolution(), metadata, subsetDef);
                angleBands.add(band);
            }

            // add Azimuth
            String bandNameAzimuth = "view_azimuth_" + anglesGrid.getBandId();
            if(subsetDef == null || subsetDef.isNodeAccepted(bandNameAzimuth)) {
                band = readAngleBand(productBounds, defaultProductWidth, defaultProductHeight, bandNameAzimuth, "Viewing azimuth angles", anglesGrid.getSize(),
                                     anglesGrid.getAzimuth(), anglesGrid.getResolution(), metadata, subsetDef);
                angleBands.add(band);
            }
        }

        // add mean angles
        MuscateMetadata.AnglesGrid meanViewingAnglesGrid = metadata.getMeanViewingAnglesGrid();
        if (meanViewingAnglesGrid != null) {
            // add Zenith
            if(subsetDef == null || subsetDef.isNodeAccepted("view_zenith_mean")) {
                band = readAngleBand(productBounds, defaultProductWidth, defaultProductHeight, "view_zenith_mean", "Mean viewing zenith angles", meanViewingAnglesGrid.getSize(),
                                     meanViewingAnglesGrid.getZenith(), meanViewingAnglesGrid.getResolution(), metadata, subsetDef);
                angleBands.add(band);
            }

            // add Azimuth
            if(subsetDef == null || subsetDef.isNodeAccepted("view_azimuth_mean")) {
                band = readAngleBand(productBounds, defaultProductWidth, defaultProductHeight, "view_azimuth_mean", "Mean viewing azimuth angles", meanViewingAnglesGrid.getSize(),
                                     meanViewingAnglesGrid.getAzimuth(), meanViewingAnglesGrid.getResolution(), metadata, subsetDef);
                angleBands.add(band);
            }
        }

        return angleBands;
    }

    private static Rectangle computeBandBoundsBasedOnPercent(Rectangle productBounds, int defaultProductWidth, int defaultProductHeight, int defaultBandWidth, int defaultBandHeight) {
        float productOffsetXPercent = productBounds.x / (float)defaultProductWidth;
        float productOffsetYPercent = productBounds.y / (float)defaultProductHeight;
        float productWidthPercent = productBounds.width / (float)defaultProductWidth;
        float productHeightPercent = productBounds.height / (float)defaultProductHeight;
        int bandOffsetX = (int)(productOffsetXPercent * defaultBandWidth);
        int bandOffsetY = (int)(productOffsetYPercent * defaultBandHeight);
        int bandWidth = (int)Math.ceil(productWidthPercent * defaultBandWidth);
        int bandHeight = (int)Math.ceil(productHeightPercent * defaultBandHeight);
        return new Rectangle(bandOffsetX, bandOffsetY, bandWidth, bandHeight);
    }

    private static Band readAngleBand(Rectangle productBounds, int defaultProductWidth, int defaultProductHeight, String angleBandName, String description,
                                      Dimension defaultSize, float[] data, Point.Float resolution, MuscateMetadata metadata, ProductSubsetDef subsetDef) {

        Rectangle bandBounds;
        if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
            bandBounds = new Rectangle(defaultSize.width, defaultSize.height);
        } else {
            bandBounds = computeBandBoundsBasedOnPercent(productBounds, defaultProductWidth, defaultProductHeight, defaultSize.width, defaultSize.height);
        }
        if (bandBounds.isEmpty()) {
            throw new IllegalStateException("The region of the angle band '"+angleBandName+"' is empty: x="+bandBounds.x+", y="+bandBounds.y+", width="+bandBounds.width+", height="+bandBounds.height+".");
        }

        int[] bandOffsets = {0};
        SampleModel sampleModel = new PixelInterleavedSampleModel(TYPE_FLOAT, bandBounds.width, bandBounds.height, 1, bandBounds.width, bandOffsets);
        DataBuffer buffer = new DataBufferFloat(sampleModel.getWidth() * sampleModel.getHeight());
        WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);

        for (int rowIndex = bandBounds.y; rowIndex<(bandBounds.y + bandBounds.height); rowIndex++) {
            int rowOffset = rowIndex * defaultSize.width;
            for (int columnIndex = bandBounds.x; columnIndex<(bandBounds.x + bandBounds.width); columnIndex++) {
                int index = rowOffset + columnIndex;
                raster.setSample(columnIndex - bandBounds.x, rowIndex - bandBounds.y, 0, data[index]);
            }
        }

        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.TRANSLUCENT, TYPE_FLOAT);

        // and finally create an image with this raster
        BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
        PlanarImage sourceBandImage = PlanarImage.wrapRenderedImage(image);

        Band band = new Band(angleBandName, ProductData.TYPE_FLOAT32, sourceBandImage.getWidth(), sourceBandImage.getHeight());
        band.setDescription(description);
        //20200716 - issue with degree sign in DIMAP, decision to use "deg" fo unit instead of "°"
        //band.setUnit("°");
        band.setUnit("deg");
        band.setNoDataValue(Double.NaN);
        band.setNoDataValueUsed(true);

        try {
            CoordinateReferenceSystem mapCRS = CRS.decode("EPSG:" + metadata.getEPSG());
            MuscateMetadata.Geoposition firstGeoPosition = metadata.getGeoPositions().get(0);
            CrsGeoCoding crsGeoCoding = new CrsGeoCoding(mapCRS, band.getRasterWidth(), band.getRasterHeight(), firstGeoPosition.ulx, firstGeoPosition.uly, resolution.x, resolution.y, 0.0, 0.0);
            band.setGeoCoding(crsGeoCoding);
        } catch (Exception e) {
            logger.warning(String.format("Unable to set geocoding to the band %s", angleBandName));
        }
        band.setImageToModelTransform(Product.findImageToModelTransform(band.getGeoCoding()));
        // set source image must be done after setGeocoding and setImageToModelTransform
        band.setSourceImage(sourceBandImage);
        return band;
    }

    public static int getDetectorFromFilename(String pathString) {
        Pattern p = Pattern.compile(".*D[0-9]{2}\\.tif");
        Matcher m = p.matcher(pathString);
        if (!m.matches()) {
            return 0;
        }
        return Integer.parseInt(pathString.substring(pathString.length() - 6, pathString.length() - 4));
    }

    private static String formatBandNameTo3Characters(String band) {
        if (band.startsWith("B") && band.length() == 2) {
            return String.format("B0%c", band.charAt(1));
        }
        return band;
    }

    public static String getBandFromFileName(String filename) {
        Pattern pattern = Pattern.compile(MuscateConstants.REFLECTANCE_PATTERN);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return matcher.group(8);
        }
        return "UNKNOWN";
    }

    private static String buildGroupPattern() {
        return "Aux_Mask:AOT_Interpolation:AOT:Surface_Reflectance:Flat_Reflectance:WVC:cloud:MG2:mg2:sun:view:edge:" +
                "detector_footprint-B01:detector_footprint-B02:detector_footprint-B03:detector_footprint-B04:detector_footprint-B05:detector_footprint-B06:detector_footprint-B07:detector_footprint-B08:" +
                "detector_footprint-B8A:detector_footprint-B09:detector_footprint-B10:detector_footprint-B11:detector_footprint-B12:defective:saturation";
    }

    public static String computeGeographicMaskName(MuscateConstants.GEOPHYSICAL_BIT geophysicalBit, MuscateMetadata.Geoposition geoposition) {
        return geophysicalBit.getPrefixName() + geoposition.id;
    }

    private static Mask buildGeophysicsMask(MuscateConstants.GEOPHYSICAL_BIT geophysicalBit, Band sourceBand, String maskName) {
        return buildMaskFromBand(sourceBand, maskName, geophysicalBit.getDescription(), String.format("bit_set(%s,%d)", sourceBand.getName(), geophysicalBit.getBit()), geophysicalBit.getColor());
    }

    private static Mask buildMaskFromBand(Band sourceBand, String maskName, String maskDescription, String maskExpression, Color maskColor) {
        Mask mask = Mask.BandMathsType.create(maskName, maskDescription, sourceBand.getRasterWidth(), sourceBand.getRasterHeight(), maskExpression, maskColor, 0.5);
        ProductUtils.copyGeoCoding(sourceBand, mask);
        return mask;
    }

    public static MuscateProductReader.BandNameCallback buildWVCImageBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> "WVC_" + geoPosition.id;
    }

    public static MuscateProductReader.BandNameCallback buildFlatReflectanceImageBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> {
            String bandId = MuscateProductReader.getBandFromFileName(tiffImageRelativeFilePath);
            return "Flat_Reflectance_" + bandId;
        };
    }

    public static MuscateProductReader.BandNameCallback buildSurfaceReflectanceImageBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> {
            String bandId = MuscateProductReader.getBandFromFileName(tiffImageRelativeFilePath);
            return "Surface_Reflectance_" + bandId;
        };
    }

    public static MuscateProductReader.BandNameCallback buildAOTImageBandNameCallback() {
        return (geoPosition, tiffImageRelativeFilePath) -> "AOT_" + geoPosition.id;
    }

    public static interface BandNameCallback {
        String buildBandName(MuscateMetadata.Geoposition geoPosition, String tiffImageRelativeFilePath);
    }

    private static class GeoTiffBandResult {
        private final Band band;
        private final MuscateMetadata.Geoposition geoPosition;

        private GeoTiffBandResult(Band band, MuscateMetadata.Geoposition geoPosition) {
            this.band = band;
            this.geoPosition = geoPosition;
        }

        public Band getBand() {
            return band;
        }

        public MuscateMetadata.Geoposition getGeoPosition() {
            return geoPosition;
        }
    }
}
