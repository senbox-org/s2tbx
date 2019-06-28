package org.esa.s2tbx.dataio.muscate;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.s2.S2BandAnglesGrid;
import org.esa.s2tbx.dataio.s2.S2BandAnglesGridByDetector;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.ortho.S2AnglesGeometry;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReaderPlugIn;
import org.geotools.referencing.CRS;
import org.xml.sax.SAXException;

import javax.media.jai.PlanarImage;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Color;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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

    private List<MuscateMetadata.Geoposition> geoPositions;
    private VirtualDirEx virtualDir;
    private List<Product> associatedProducts;
    private List<String> addedFiles;
    private MuscateMetadata metadata;

    protected MuscateProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.geoPositions = new ArrayList<>();
        this.associatedProducts = new ArrayList<>();
        this.addedFiles = new ArrayList<>();

        XmlMetadataParserFactory.registerParser(MuscateMetadata.class, new XmlMetadataParser<>(MuscateMetadata.class));
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Path inputPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Reading Muscate product from the file '" + inputPath.toString() + "'.");
        }

        this.virtualDir = VirtualDirEx.build(inputPath, false, true);
        if (this.virtualDir == null) {
            throw new NullPointerException("The virtual dir is null for input path '" + inputPath.toString() + "'.");
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Reading Muscate product metadata.");
        }

        String metadataFile = findFirstMetadataFile();
        try (FilePathInputStream metadataInputStream = this.virtualDir.getInputStream(metadataFile)) {
            try {
                this.metadata = (MuscateMetadata) XmlMetadataParserFactory.getParser(MuscateMetadata.class).parse(metadataInputStream);
            } catch (ParserConfigurationException | SAXException | InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }

        // read resolutions
        for (String resolution : this.metadata.getResolutionStrings()) {
            this.geoPositions.add(this.metadata.getGeoposition(resolution));
        }

        // create product
        Product product = new Product(this.metadata.getProductName(), "MUSCATE", this.metadata.getRasterWidth(), this.metadata.getRasterHeight());
        product.setDescription(this.metadata.getDescription());
        product.getMetadataRoot().addElement(this.metadata.getRootElement());

        // set File Location
        product.setFileLocation(inputPath.toFile());

        product.setSceneGeoCoding(this.metadata.getCrsGeoCoding());
        product.setNumResolutionsMax(this.geoPositions.size());
        product.setAutoGrouping("Aux_Mask:AOT_Interpolation:AOT:Surface_Reflectance:Flat_Reflectance:WVC:cloud:MG2:mg2:sun:view:edge:" +
                "detector_footprint-B01:detector_footprint-B02:detector_footprint-B03:detector_footprint-B04:detector_footprint-B05:detector_footprint-B06:detector_footprint-B07:detector_footprint-B08:" +
                "detector_footprint-B8A:detector_footprint-B09:detector_footprint-B10:detector_footprint-B11:detector_footprint-B12:defective:saturation");
        product.setStartTime(parseDate(this.metadata.getAcquisitionDate(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        product.setEndTime(parseDate(this.metadata.getAcquisitionDate(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Adding Muscate product bands.");
        }

        // add bands
        for (MuscateImage image : this.metadata.getImages()) {
            addImage(product, image);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Adding Muscate product masks.");
        }

        // add masks
        for (MuscateMask mask : this.metadata.getMasks()) {
            addMask(product, mask);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Adding Muscate product angles.");
        }

        MuscateMetadata.AnglesGrid sunAnglesGrid = this.metadata.getSunAnglesGrid();

        // add Zenith
        addAngles(product, "sun_zenith", "Sun zenith angles", sunAnglesGrid.getWidth(),
                  sunAnglesGrid.getHeight(), sunAnglesGrid.getZenith(), sunAnglesGrid.getResX(), sunAnglesGrid.getResY());

        // add Azimuth
        addAngles(product, "sun_azimuth", "Sun azimuth angles", sunAnglesGrid.getWidth(),
                  sunAnglesGrid.getHeight(), sunAnglesGrid.getAzimuth(), sunAnglesGrid.getResX(), sunAnglesGrid.getResY());

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Adding Muscate product viewing angles.");
        }

        // viewing angles
        for (String bandId : this.metadata.getBandNames()) {
            MuscateMetadata.AnglesGrid anglesGrid = this.metadata.getViewingAnglesGrid(bandId);
            // add Zenith
            addAngles(product, "view_zenith_" + anglesGrid.getBandId(), "Viewing zenith angles", anglesGrid.getWidth(),
                      anglesGrid.getHeight(), anglesGrid.getZenith(), anglesGrid.getResX(), anglesGrid.getResY());

            // add Azimuth
            addAngles(product, "view_azimuth_" + anglesGrid.getBandId(), "Viewing azimuth angles", anglesGrid.getWidth(),
                      anglesGrid.getHeight(), anglesGrid.getAzimuth(), anglesGrid.getResX(), anglesGrid.getResY());
        }

        // add mean angles
        MuscateMetadata.AnglesGrid meanViewingAnglesGrid = this.metadata.getMeanViewingAnglesGrid();
        if (meanViewingAnglesGrid != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Adding Muscate product mean angles.");
            }

            // add Zenith
            addAngles(product, "view_zenith_mean", "Mean viewing zenith angles", meanViewingAnglesGrid.getWidth(),
                    meanViewingAnglesGrid.getHeight(), meanViewingAnglesGrid.getZenith(), meanViewingAnglesGrid.getResX(), meanViewingAnglesGrid.getResY());

            // add Azimuth
            addAngles(product, "view_azimuth_mean", "Mean viewing azimuth angles", meanViewingAnglesGrid.getWidth(),
                    meanViewingAnglesGrid.getHeight(), meanViewingAnglesGrid.getAzimuth(), meanViewingAnglesGrid.getResX(), meanViewingAnglesGrid.getResY());
        }

        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
                                          throws IOException {
        // do nothing
    }

    @Override
    public void close() throws IOException {
        super.close();

        for (Product product : this.associatedProducts) {
            product.dispose();
        }
        this.associatedProducts.clear();
        this.virtualDir.close();
        this.geoPositions = null;
        this.associatedProducts = null;
    }

    private String findFirstMetadataFile() throws IOException {
        String[] files = this.virtualDir.listAll();
        for (String file : files) {
            if (file.endsWith(".xml") && file.matches(MuscateConstants.XML_PATTERN)) {
                return file;
            }
        }
        return null;
    }

    private void addAngles(Product product, String angleBandName, String description, int width, int height, float[] data, float resX, float resY) {
        int[] bandOffsets = {0};
        SampleModel sampleModel = new PixelInterleavedSampleModel(TYPE_FLOAT, width, height, 1, width, bandOffsets);
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.TRANSLUCENT, TYPE_FLOAT);
        DataBuffer buffer = new DataBufferFloat(width * height);
        // wrap it in a writable raster
        WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);

        // search index of angleID
        raster.setPixels(0, 0, width, height, data);

        // and finally create an image with this raster
        BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
        PlanarImage opImage = PlanarImage.wrapRenderedImage(image);

        Band band = new Band(angleBandName, ProductData.TYPE_FLOAT32, width, height);
        band.setDescription(description);
        band.setUnit("°");
        band.setNoDataValue(Double.NaN);
        band.setNoDataValueUsed(true);

        try {
            band.setGeoCoding(new CrsGeoCoding(CRS.decode("EPSG:" + this.metadata.getEPSG()),
                    band.getRasterWidth(),
                    band.getRasterHeight(),
                    this.geoPositions.get(0).ulx,
                    this.geoPositions.get(0).uly,
                    resX,
                    resY,
                    0.0, 0.0));
        } catch (Exception e) {
            logger.warning(String.format("Unable to set geocoding to the band %s", angleBandName));
        }

        band.setImageToModelTransform(product.findImageToModelTransform(band.getGeoCoding()));

        // set source image mut be done after setGeocoding and setImageToModelTransform
        band.setSourceImage(opImage);
        product.addBand(band);
    }

    private void addImage(Product product, MuscateImage image) {
        if (image == null || image.nature == null) {
            logger.warning(String.format("Unable to add an image with a null nature to the product: %s", product.getName()));
        } else {
            //TODO Read together AOT and WVC? they should be in the same tif file
            if (image.nature.equals("Aerosol_Optical_Thickness")) {
                for (String file : image.getImageFiles()) {
                    addAOTImage(product, file);
                }
            } else if (image.nature.equals("Flat_Reflectance")) {
                for (String file : image.getImageFiles()) {
                    addReflectanceImage(product, file, "Flat");
                }
            } else if (image.nature.equals("Surface_Reflectance")) {
                for (String file : image.getImageFiles()) {
                    addReflectanceImage(product, file, "Surface");
                }
            } else if (image.nature.equals("Water_Vapor_Content")) {
                for (String file : image.getImageFiles()) {
                    addWVCImage(product, file);
                }
            } else {
                logger.warning(String.format("Unable to add image. Unknown nature: %s", image.nature));
            }
        }
    }

    private void addMask(Product product, MuscateMask mask) {
        // some checks
        if (mask == null || mask.nature == null) {
            logger.warning(String.format("Unable to add a mask with a null nature to the product: %s", product.getName()));
            return;
        }

        //The mask depends on the version
        String version = metadata.getProductVersion();
        float versionFloat = 0.0f;

        if(version != null) {
            versionFloat = Float.valueOf(version);
        }

        if (mask.nature.equals("AOT_Interpolation")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                }
                int bitNumber = 0;
                if(versionFloat > 1.55) bitNumber = 1; //after this version the bitNumber has changed
                addAOTMask(product, file, bitNumber);
            }
        } else if (mask.nature.equals("Detailed_Cloud")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addCloudMask(product, file);
                }
            }
        } else if (mask.nature.equals("Cloud")) {
            if(versionFloat < 1.95) {
                for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                    String file = muscateMaskFile.path;
                    if (!addedFiles.contains(file)) {
                        addedFiles.add(file);
                    }
                    addCloudMask(product, file);
                }
            } else {
                for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                    String file = muscateMaskFile.path;
                    if (!addedFiles.contains(file)) {
                        addedFiles.add(file);
                    }
                    addGeophysicsMask(product, file, MuscateConstants.GEOPHYSICAL_BIT.Cloud);
                }
            }
        } else if (mask.nature.equals("Cloud_Shadow")) {
            if(versionFloat < 2.05) {
                //In some old products the Nature is Cloud_Shadow instead of Geophysics. Perhaps an error?
                for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                    String file = muscateMaskFile.path;
                    if (!addedFiles.contains(file)) {
                        addedFiles.add(file);
                    }
                    addGeophysicsMask(product, file);
                }
            } else {
                for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                    String file = muscateMaskFile.path;
                    if (!addedFiles.contains(file)) {
                        addedFiles.add(file);
                    }
                    addGeophysicsMask(product, file, MuscateConstants.GEOPHYSICAL_BIT.Cloud_Shadow);
                }
            }
        } else if (mask.nature.equals("Edge")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addEdgeMask(product, file);
                }
            }
        } else if (mask.nature.equals("Saturation")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addSaturationMask(product, file);
                }
            }
        } else if (mask.nature.equals("Geophysics")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addGeophysicsMask(product, file);
                }
            }
        } else if (mask.nature.equals("Detector_Footprint")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addDetectorFootprintMask(product, file);
                }
            }
        } else if (mask.nature.equals("Defective_Pixel")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addDefectivePixelMask(product, file);
                }
            }
        } else if (mask.nature.equals("Hidden_Surface")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                }
                addGeophysicsMask(product, file, MuscateConstants.GEOPHYSICAL_BIT.Hidden_Surface);
            }
        } else if (mask.nature.equals("Snow")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                }
                addGeophysicsMask(product, file, MuscateConstants.GEOPHYSICAL_BIT.Snow);
            }
        } else if (mask.nature.equals("Sun_Too_Low")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                }
                addGeophysicsMask(product, file, MuscateConstants.GEOPHYSICAL_BIT.Sun_Too_Low);
            }
        } else if (mask.nature.equals("Tangent_Sun")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                }
                addGeophysicsMask(product, file, MuscateConstants.GEOPHYSICAL_BIT.Tangent_Sun);
            }
        } else if (mask.nature.equals("Topography_Shadow")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                }
                addGeophysicsMask(product, file, MuscateConstants.GEOPHYSICAL_BIT.Topography_Shadow);
            }
        } else if (mask.nature.equals("Water")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                }
                addGeophysicsMask(product, file, MuscateConstants.GEOPHYSICAL_BIT.Water);
            }
        } else if (mask.nature.equals("WVC_Interpolation")) {
            for (MuscateMaskFile muscateMaskFile : mask.getMaskFiles()) {
                String file = muscateMaskFile.path;
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                }
                addWVCMask(product, file);
            }
        } else {
            logger.warning(String.format("Unable to add mask. Unknown nature: %s", mask.nature));
        }
    }

    // get the bands and include the product in associated product, to be properly closed when closing Muscate product
    private Band readGeoTiffProductBand(String pathString, int bandIndex) {
        Band band = null;
        try {
            File inputFile;
            try {
                inputFile = this.virtualDir.getFile(pathString);
            } catch (FileNotFoundException e) {
                String fileName = pathString.substring(pathString.lastIndexOf("/") + 1);
                inputFile = this.virtualDir.getFile(fileName);
            }

            GeoTiffProductReaderPlugIn geoTiffReaderPlugIn = new GeoTiffProductReaderPlugIn();
            GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(geoTiffReaderPlugIn);
            Product tiffProduct = geoTiffProductReader.readProductNodes(inputFile, null);
            this.associatedProducts.add(tiffProduct);
            band = tiffProduct.getBandAt(bandIndex);
        } catch (IOException e) {
            logger.warning(String.format("Unable to get band %d of the product: %s", bandIndex, pathString));
        }
        return band;
    }

    private MuscateMetadata.Geoposition getGeoposition(int width, int height) {
        for (MuscateMetadata.Geoposition geoposition : this.geoPositions) {
            if (geoposition.nRows == height && geoposition.nCols == width) {
                return geoposition;
            }
        }
        return null;
    }

    private void addAOTImage(Product product, String pathString) {
        Band srcBand = readGeoTiffProductBand(pathString, 1);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
        } else {
            MuscateMetadata.Geoposition geoPosition = getGeoposition(srcBand.getRasterWidth(), srcBand.getRasterHeight());
            if (geoPosition == null) {
                logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.", pathString, product.getName()));
            } else {
                String bandName = "AOT_" + geoPosition.id;
                Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
                product.addBand(targetBand);
                ProductUtils.copyGeoCoding(srcBand, targetBand);
                targetBand.setNoDataValue(this.metadata.getAOTNoDataValue());
                targetBand.setNoDataValueUsed(true);
                targetBand.setScalingFactor(1.0d / this.metadata.getAOTQuantificationValue());
                targetBand.setScalingOffset(0.0d);
                targetBand.setSampleCoding(srcBand.getSampleCoding());
                targetBand.setImageInfo(srcBand.getImageInfo());
                targetBand.setDescription(String.format("Aerosol Optical Thickness at %.0fm resolution", geoPosition.xDim));
                targetBand.setSourceImage(srcBand.getSourceImage());
            }
        }
    }

    private void addWVCImage(Product product, String pathString) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
        } else {
            MuscateMetadata.Geoposition geoposition = getGeoposition(srcBand.getRasterWidth(), srcBand.getRasterHeight());
            if (geoposition == null) {
                logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.", pathString, product.getName()));
            } else {
                String bandName = "WVC_" + geoposition.id;

                Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
                product.addBand(targetBand);
                ProductUtils.copyGeoCoding(srcBand, targetBand);
                targetBand.setNoDataValue(this.metadata.getWVCNoDataValue());
                targetBand.setNoDataValueUsed(true);
                targetBand.setScalingFactor(1.0d / this.metadata.getWVCQuantificationValue());
                targetBand.setScalingOffset(0.0d);
                targetBand.setUnit("cm"); //TODO verify
                targetBand.setSampleCoding(srcBand.getSampleCoding());
                targetBand.setImageInfo(srcBand.getImageInfo());
                targetBand.setDescription(String.format("Water vapor content at %.0fm resolution in %s", geoposition.xDim, targetBand.getUnit()));
                targetBand.setSourceImage(srcBand.getSourceImage());
            }
        }
    }

    private void addReflectanceImage(Product product, String pathString, String prefix) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }
        MuscateMetadata.Geoposition geoposition = getGeoposition(srcBand.getRasterWidth(), srcBand.getRasterHeight());
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.", pathString, product.getName()));
            return;
        }
        String bandId = getBandFromFileName(pathString);
        String bandName = prefix + "_Reflectance_" + bandId;

        Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
        product.addBand(targetBand);
        ProductUtils.copyGeoCoding(srcBand, targetBand);
        targetBand.setNoDataValue(metadata.getReflectanceNoDataValue());
        targetBand.setNoDataValueUsed(true);
        targetBand.setSpectralWavelength(metadata.getCentralWavelength(bandId)); //not available in metadata
        //targetBand.setSpectralBandwidth(srcBand.getSpectralBandwidth()); //not available in metadata
        targetBand.setScalingFactor(1.0d / metadata.getReflectanceQuantificationValue());
        targetBand.setScalingOffset(0.0d);
        targetBand.setSampleCoding(srcBand.getSampleCoding());
        targetBand.setImageInfo(srcBand.getImageInfo());
        if (prefix.equals("Flat")) {
            targetBand.setDescription(String.format("Ground reflectance with the correction of slope effects, band %s", bandId));
        } else if (prefix.equals("Surface")) {
            targetBand.setDescription(String.format("Ground reflectance without the correction of slope effects, band %s", bandId));
        }
        targetBand.setSourceImage(srcBand.getSourceImage());
    }

    private void addAOTMask(Product product, String pathString, int bitNumber) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }

        int height = srcBand.getRasterHeight();
        int width = srcBand.getRasterWidth();

        MuscateMetadata.Geoposition geoposition = getGeoposition(width, height);
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.",
                    pathString, product.getName()));
            return;
        }

        String bandName = "Aux_IA_" + geoposition.id;
        String maskName = "AOT_Interpolation_Mask_" + geoposition.id;

        //Add aux band if it has not been added yet
        if(!product.containsBand(bandName)) {
            Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
            product.addBand(targetBand);
            ProductUtils.copyGeoCoding(srcBand, targetBand);
            targetBand.setNoDataValueUsed(false);
            targetBand.setScalingFactor(1);
            targetBand.setScalingOffset(0);
            targetBand.setSampleCoding(srcBand.getSampleCoding());
            targetBand.setImageInfo(srcBand.getImageInfo());
            targetBand.setDescription("Interpolated pixels mask");
            targetBand.setSourceImage(srcBand.getSourceImage());
        }

        Mask mask = Mask.BandMathsType.create(maskName,
                "Interpolated AOT pixels mask",
                width, height,
                String.format("bit_set(%s,%d)", bandName, bitNumber),
                Color.BLUE,
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask);
        product.addMask(mask);
    }

    private void addWVCMask(Product product, String pathString) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }

        int height = srcBand.getRasterHeight();
        int width = srcBand.getRasterWidth();

        MuscateMetadata.Geoposition geoposition = getGeoposition(width, height);
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.",
                                         pathString, product.getName()));
            return;
        }

        String bandName = "Aux_IA_" + geoposition.id;
        String maskName = "WVC_Interpolation_Mask_" + geoposition.id;

        //Add aux band if it has not been added yet
        if(!product.containsBand(bandName)) {
            Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
            product.addBand(targetBand);
            ProductUtils.copyGeoCoding(srcBand, targetBand);
            targetBand.setNoDataValueUsed(false);
            targetBand.setScalingFactor(1);
            targetBand.setScalingOffset(0);
            targetBand.setSampleCoding(srcBand.getSampleCoding());
            targetBand.setImageInfo(srcBand.getImageInfo());
            targetBand.setDescription("Interpolated pixels mask");
            targetBand.setSourceImage(srcBand.getSourceImage());
        }

        Mask mask = Mask.BandMathsType.create(maskName,
                                              "Interpolated WVC pixels mask",
                                              width, height,
                                              String.format("bit_set(%s,0)", bandName),
                                              Color.BLUE,
                                              0.5);
        ProductUtils.copyGeoCoding(srcBand, mask);
        product.addMask(mask);
    }

    private void addEdgeMask(Product product, String pathString) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }

        int height = srcBand.getRasterHeight();
        int width = srcBand.getRasterWidth();

        MuscateMetadata.Geoposition geoposition = getGeoposition(width, height);
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.",
                    pathString, product.getName()));
            return;
        }
        String bandName = "Aux_Mask_Edge_" + geoposition.id;
        String maskName = "edge_mask_" + geoposition.id;


        Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
        product.addBand(targetBand);
        ProductUtils.copyGeoCoding(srcBand, targetBand);
        targetBand.setNoDataValueUsed(false);
        targetBand.setScalingFactor(1);
        targetBand.setScalingOffset(0);
        targetBand.setSampleCoding(srcBand.getSampleCoding());
        targetBand.setImageInfo(srcBand.getImageInfo());
        targetBand.setDescription("Edge mask");
        targetBand.setSourceImage(srcBand.getSourceImage());

        Mask mask = Mask.BandMathsType.create(maskName,
                "Edge mask",
                width, height,
                String.format("bit_set(%s,0)", bandName),
                Color.GREEN,
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask);
        product.addMask(mask);
    }

    private void addSaturationMask(Product product, String pathString) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }

        int height = srcBand.getRasterHeight();
        int width = srcBand.getRasterWidth();

        MuscateMetadata.Geoposition geoposition = getGeoposition(width, height);
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.",
                    pathString, product.getName()));
            return;
        }

        String bandName = "Aux_Mask_Saturation_" + geoposition.id;

        ArrayList<String> bands = metadata.getBandNames(geoposition.id);

        Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
        product.addBand(targetBand);
        ProductUtils.copyGeoCoding(srcBand, targetBand);
        targetBand.setNoDataValueUsed(false);
        targetBand.setScalingFactor(1);
        targetBand.setScalingOffset(0);
        targetBand.setSampleCoding(srcBand.getSampleCoding());
        targetBand.setImageInfo(srcBand.getImageInfo());
        targetBand.setDescription("saturation mask coded over 8 bits, 1 bit per spectral band (number of useful bits = number of " +
                "spectral bands)");
        targetBand.setSourceImage(srcBand.getSourceImage());

        int bitCount = 0;
        for (String bandId : bands) {
            Mask mask = Mask.BandMathsType.create("saturation_" + bandId,
                    String.format("Saturation mask of band %s", bandId),
                    width, height,
                    String.format("bit_set(%s,%d)", bandName, bitCount),
                    Color.RED,
                    0.5);
            ProductUtils.copyGeoCoding(srcBand, mask);
            product.addMask(mask);
            bitCount++;
        }
    }

    private void addCloudMask(Product product, String pathString) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }

        int height = srcBand.getRasterHeight();
        int width = srcBand.getRasterWidth();

        MuscateMetadata.Geoposition geoposition = getGeoposition(width, height);
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.", pathString, product.getName()));
            return;
        }

        String bandName = "Aux_Mask_Cloud_" + geoposition.id;

        //add band to product if it hasn't been added yet
        if(!product.containsBand(bandName)) {
            Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
            product.addBand(targetBand);
            ProductUtils.copyGeoCoding(srcBand, targetBand);
            targetBand.setNoDataValueUsed(false);
            targetBand.setScalingFactor(1);
            targetBand.setScalingOffset(0);
            targetBand.setSampleCoding(srcBand.getSampleCoding());
            targetBand.setImageInfo(srcBand.getImageInfo());
            targetBand.setDescription("Cloud mask computed by MACCS software, made of 1 band coded over 8 useful bits");
            targetBand.setSourceImage(srcBand.getSourceImage());
        }

        ColorIterator.reset();

        //addMasks
        Mask mask0 = Mask.BandMathsType.create("cloud_mask_all_" + geoposition.id,
                "Result of a 'logical OR' for all the cloud and shadow maks",
                width, height,
                String.format("bit_set(%s,0)", bandName),
                ColorIterator.next(),
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask0);
        product.addMask(mask0);

        Mask mask1 = Mask.BandMathsType.create("cloud_mask_all_cloud_" + geoposition.id,
                "Result of a 'logical OR' for all the cloud masks",
                width, height,
                String.format("bit_set(%s,1)", bandName),
                ColorIterator.next(),
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask1);
        product.addMask(mask1);

        Mask mask2 = Mask.BandMathsType.create("cloud_mask_refl_" + geoposition.id,
                "Cloud mask identified by a reflectance threshold",
                width, height,
                String.format("bit_set(%s,2)", bandName),
                ColorIterator.next(),
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask2);
        product.addMask(mask2);

        Mask mask3 = Mask.BandMathsType.create("cloud_mask_refl_var_" + geoposition.id,
                "Cloud mask identified by a threshold on reflectance variance",
                width, height,
                String.format("bit_set(%s,3)", bandName),
                ColorIterator.next(),
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask3);
        product.addMask(mask3);

        Mask mask4 = Mask.BandMathsType.create("cloud_mask_extension_" + geoposition.id,
                "Cloud mask identified by the extension of cloud masks",
                width, height,
                String.format("bit_set(%s,4)", bandName),
                ColorIterator.next(),
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask4);
        product.addMask(mask4);

        Mask mask5 = Mask.BandMathsType.create("cloud_mask_shadow_" + geoposition.id,
                "Shadow mask of clouds inside the image",
                width, height,
                String.format("bit_set(%s,5)", bandName),
                ColorIterator.next(),
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask5);
        product.addMask(mask5);

        Mask mask6 = Mask.BandMathsType.create("cloud_mask_sahdvar_" + geoposition.id,
                "Shadow mask of clouds outside the image",
                width, height,
                String.format("bit_set(%s,6)", bandName),
                ColorIterator.next(),
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask6);
        product.addMask(mask6);

        Mask mask7 = Mask.BandMathsType.create("cloud_mask_cirrus_" + geoposition.id,
                "Cloud mask identified with the cirrus spectral band",
                width, height,
                String.format("bit_set(%s,7)", bandName),
                ColorIterator.next(),
                0.5);
        ProductUtils.copyGeoCoding(srcBand, mask7);
        product.addMask(mask7);
    }

    private void addGeophysicsMask(Product product, String pathString) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        for (MuscateConstants.GEOPHYSICAL_BIT geophysical_bit : MuscateConstants.GEOPHYSICAL_BIT.values()) {
            addGeophysicsMask(product, pathString, geophysical_bit, srcBand);
        }
    }

    private void addGeophysicsMask(Product product, String pathString, MuscateConstants.GEOPHYSICAL_BIT geophysical_bit, Band srcBand) {
        int height = srcBand.getRasterHeight();
        int width = srcBand.getRasterWidth();

        MuscateMetadata.Geoposition geoposition = getGeoposition(width, height);
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.",
                                         pathString, product.getName()));
            return;
        }

        String bandName = "Aux_Mask_MG2_" + geoposition.id;

        //add band to product if it hasn't been added yet
        if (!product.containsBand(bandName)) {
            Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
            product.addBand(targetBand);
            ProductUtils.copyGeoCoding(srcBand, targetBand);
            targetBand.setNoDataValueUsed(false);
            targetBand.setScalingFactor(1);
            targetBand.setScalingOffset(0);
            targetBand.setSampleCoding(srcBand.getSampleCoding());
            targetBand.setImageInfo(srcBand.getImageInfo());
            targetBand.setDescription("Geophysical mask of level 2, made of 1 band coded over 8 useful bits");
            targetBand.setSourceImage(srcBand.getSourceImage());
        }

        //addMasks
        Mask mask = Mask.BandMathsType.create(geophysical_bit.getPrefixName() + geoposition.id,
                                              geophysical_bit.getDescription(),
                                              width, height,
                                              String.format("bit_set(%s,%d)", bandName, geophysical_bit.getBit()),
                                              geophysical_bit.getColor(),
                                              0.5);
        ProductUtils.copyGeoCoding(srcBand, mask);
        product.addMask(mask);
    }

    private void addGeophysicsMask(Product product, String pathString, MuscateConstants.GEOPHYSICAL_BIT geophysical_bit) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }
        addGeophysicsMask(product, pathString, geophysical_bit, srcBand);
    }

    private int getDetectorFromFilename(String pathString) {
        Pattern p = Pattern.compile(".*D[0-9]{2}\\.tif");
        Matcher m = p.matcher(pathString);
        if (!m.matches()) {
            return 0;
        }
        return Integer.parseInt(pathString.substring(pathString.length() - 6, pathString.length() - 4));
    }

    private String formatBandNameTo3characters(String band) {
        if (band.startsWith("B") && band.length() == 2) {
            return String.format("B0%c", band.charAt(1));
        } else {
            return band;
        }
    }

    private void addDetectorFootprintMask(Product product, String pathString) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }

        int height = srcBand.getRasterHeight();
        int width = srcBand.getRasterWidth();

        MuscateMetadata.Geoposition geoposition = getGeoposition(width, height);
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.", pathString, product.getName()));
            return;
        }


        String[] orderedBandNames = metadata.getOrderedBandNames(geoposition.id);

        int detector = getDetectorFromFilename(pathString);

        String bandName = String.format("Aux_Mask_Detector_Footprint_%s_%02d", geoposition.id, detector);

        Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
        product.addBand(targetBand);
        ProductUtils.copyGeoCoding(srcBand, targetBand);
        targetBand.setNoDataValueUsed(false);
        targetBand.setSampleCoding(srcBand.getSampleCoding());
        targetBand.setImageInfo(srcBand.getImageInfo());
        targetBand.setDescription("Detector footprint");
        targetBand.setSourceImage(srcBand.getSourceImage());

        ColorIterator.reset();

        // add masks
        for (int i = 0; i < orderedBandNames.length; i++) {
            Mask mask = Mask.BandMathsType.create(String.format("detector_footprint-%s-%02d", formatBandNameTo3characters(orderedBandNames[i]), detector),
                    "Detector footprint",
                    width, height,
                    String.format("bit_set(%s,%d)", bandName, i),
                    ColorIterator.next(),
                    0.5);
            ProductUtils.copyGeoCoding(srcBand, mask);
            product.addMask(mask);
        }
        return;
    }

    private void addDefectivePixelMask(Product product, String pathString) {
        Band srcBand = readGeoTiffProductBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }

        int height = srcBand.getRasterHeight();
        int width = srcBand.getRasterWidth();

        MuscateMetadata.Geoposition geoposition = getGeoposition(width, height);
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.", pathString, product.getName()));
            return;
        }

        String[] orderedBandNames = metadata.getOrderedBandNames(geoposition.id);

        String bandName = String.format("Aux_Mask_Defective_Pixel_%s", geoposition.id);

        Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
        product.addBand(targetBand);
        ProductUtils.copyGeoCoding(srcBand, targetBand);
        targetBand.setNoDataValueUsed(false);
        targetBand.setSampleCoding(srcBand.getSampleCoding());
        targetBand.setImageInfo(srcBand.getImageInfo());
        targetBand.setDescription("Defective Pixel");
        targetBand.setSourceImage(srcBand.getSourceImage());

        ColorIterator.reset();

        //addMasks
        for (int i = 0; i < orderedBandNames.length; i++) {
            Mask mask = Mask.BandMathsType.create(String.format("defective_%s", orderedBandNames[i]),
                    "Defective pixel",
                    width, height,
                    String.format("bit_set(%s,%d)", bandName, i),
                    ColorIterator.next(),
                    0.5);
            ProductUtils.copyGeoCoding(srcBand, mask);
            product.addMask(mask);
        }
        return;
    }

    private static String getBandFromFileName(String filename) {
        Pattern pattern = Pattern.compile(MuscateConstants.REFLECTANCE_PATTERN);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return matcher.group(8);
        }
        return ("UNKNOWN");
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
                bandAnglesGridByDetector[0] = new S2BandAnglesGridByDetector("view_zenith", bandConstants, detectorId, viewingAngles.getWidth(), viewingAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, viewingAngles.getResX(), viewingAngles.getResY(), viewingAngles.getZenith());
                bandAnglesGridByDetector[1] = new S2BandAnglesGridByDetector("view_azimuth", bandConstants, detectorId, viewingAngles.getWidth(), viewingAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, viewingAngles.getResX(), viewingAngles.getResY(), viewingAngles.getAzimuth());
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
        bandAnglesGrid[0] = new S2BandAnglesGrid("sun_zenith", null, sunAngles.getWidth(), sunAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, sunAngles.getResX(), sunAngles.getResY(), sunAngles.getZenith());
        bandAnglesGrid[1] = new S2BandAnglesGrid("sun_azimuth", null, sunAngles.getWidth(), sunAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, sunAngles.getResX(), sunAngles.getResY(), sunAngles.getAzimuth());
        return bandAnglesGrid;
    }
}
