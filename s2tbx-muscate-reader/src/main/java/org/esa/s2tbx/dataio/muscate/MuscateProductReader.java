package org.esa.s2tbx.dataio.muscate;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
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
import org.esa.snap.core.datamodel.quicklooks.Quicklook;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReaderPlugIn;
import org.geotools.referencing.CRS;

import javax.media.jai.PlanarImage;
import java.awt.*;
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
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.image.DataBuffer.TYPE_FLOAT;
import static org.esa.snap.utils.DateHelper.parseDate;


/**
 * Created by obarrile on 26/01/2017.
 */
public class MuscateProductReader extends AbstractProductReader implements S2AnglesGeometry {

    private ArrayList<MuscateMetadata.Geoposition> geopositions = new ArrayList<>();
    private VirtualDirEx virtualDir;
    protected ArrayList<Product> associatedProducts = new ArrayList<>();
    protected ArrayList<String> addedFiles = new ArrayList<>();
    protected final Logger logger;
    protected MuscateMetadata metadata = null;

    protected MuscateProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        logger = SystemUtils.LOG;
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        try {
            virtualDir = getInput(getInput());
            if (virtualDir == null) {
                throw new FileNotFoundException(getInput().toString());
            }
        } catch (IOException e) {
            throw new FileNotFoundException(getInput().toString());
        }

        //create metadata
        XmlMetadataParserFactory.registerParser(MuscateMetadata.class, new XmlMetadataParser<>(MuscateMetadata.class));


        InputStream metadataInputStream = getInputStreamXml();
        if (metadataInputStream == null) {
            throw new IOException(String.format("Unable to read metadata file from product: %s", getInput().toString()));
        }
        try {
            metadata = (MuscateMetadata) XmlMetadataParserFactory.getParser(MuscateMetadata.class).parse(metadataInputStream);
        } catch (Exception e) {
            throw new IOException(String.format("Unable to parse metadata file: %s", getInput().toString()));
        }

        //close stream
        if (metadataInputStream != null) try {
            metadataInputStream.close();
        } catch (IOException e) {
            // swallowed exception
        }

        //read resolutions
        for (String resolution : metadata.getResolutionStrings()) {
            geopositions.add(metadata.getGeoposition(resolution));
        }


        //create product
        Product product = new Product(metadata.getProductName(),
                                      "MUSCATE",
                                      metadata.getRasterWidth(),
                                      metadata.getRasterHeight());
        product.setDescription(metadata.getDescription());

        product.getMetadataRoot().addElement(metadata.getRootElement());

        //set File Location
        File fileLocation = null;
        try {
            // in case of zip products, getTempDir returns the temporary location of the uncompressed product
            fileLocation = virtualDir.getTempDir();
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        if (fileLocation == null) {
            fileLocation = new File(virtualDir.getBasePath());
        }
        product.setFileLocation(fileLocation);



        product.setSceneGeoCoding(metadata.getCrsGeoCoding());
        product.setNumResolutionsMax(geopositions.size());
        product.setAutoGrouping("Aux_Mask:AOT_Interpolation:AOT:Surface_Reflectance:Flat_Reflectance:WVC:cloud:MG2:mg2:sun:view:edge:" +
                                        "detector_footprint-B01:detector_footprint-B02:detector_footprint-B03:detector_footprint-B04:detector_footprint-B05:detector_footprint-B06:detector_footprint-B07:detector_footprint-B08:" +
                                        "detector_footprint-B8A:detector_footprint-B09:detector_footprint-B10:detector_footprint-B11:detector_footprint-B12:defective:saturation");
        product.setStartTime(parseDate(metadata.getAcquisitionDate(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        product.setEndTime(parseDate(metadata.getAcquisitionDate(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        //add bands
        for (MuscateImage image : metadata.getImages()) {
            addImage(product, image);
        }

        //add masks
        for (MuscateMask mask : metadata.getMasks()) {
            addMask(product, mask);
        }

        //angles
        //sun angles
        //addAngles(product, metadata.getSunAnglesGrid());

        MuscateMetadata.AnglesGrid SunAnglesGrid = metadata.getSunAnglesGrid();
        //add Zenith
        addAngles(product, "sun_zenith",
                  "Sun zenith angles", SunAnglesGrid.getWidth(), SunAnglesGrid.getHeight(),
                  SunAnglesGrid.getZenith(), SunAnglesGrid.getResX(), SunAnglesGrid.getResY());
        //add Azimuth
        addAngles(product, "sun_azimuth",
                  "Sun azimuth angles", SunAnglesGrid.getWidth(), SunAnglesGrid.getHeight(),
                  SunAnglesGrid.getAzimuth(), SunAnglesGrid.getResX(), SunAnglesGrid.getResY());


        //viewing angles
        for(String bandId : metadata.getBandNames()) {
            MuscateMetadata.AnglesGrid anglesGrid = metadata.getViewingAnglesGrid(bandId);
            //addAngles(product, anglesGrid);
            //add Zenith
            addAngles(product, "view_zenith_" + anglesGrid.getBandId(),
                      "Viewing zenith angles", anglesGrid.getWidth(), anglesGrid.getHeight(),
                      anglesGrid.getZenith(), anglesGrid.getResX(), anglesGrid.getResY());
            //add Azimuth
            addAngles(product, "view_azimuth_" + anglesGrid.getBandId(),
                      "Viewing azimuth angles", anglesGrid.getWidth(), anglesGrid.getHeight(),
                      anglesGrid.getAzimuth(), anglesGrid.getResX(), anglesGrid.getResY());
        }

        //Add mean angles
        MuscateMetadata.AnglesGrid meanViewingAnglesGrid = metadata.getMeanViewingAnglesGrid();
        if(meanViewingAnglesGrid != null) {
            addAngles(product, "view_zenith_mean",
                      "Mean viewing zenith angles", meanViewingAnglesGrid.getWidth(), meanViewingAnglesGrid.getHeight(),
                      meanViewingAnglesGrid.getZenith(), meanViewingAnglesGrid.getResX(), meanViewingAnglesGrid.getResY());
            //add Azimuth
            addAngles(product, "view_azimuth_mean",
                      "Mean viewing azimuth angles", meanViewingAnglesGrid.getWidth(), meanViewingAnglesGrid.getHeight(),
                      meanViewingAnglesGrid.getAzimuth(), meanViewingAnglesGrid.getResX(), meanViewingAnglesGrid.getResY());
        }


        //TODO check add quicklook
        //addQuicklooks(product);

        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        //TODO use this instead of source image?
    }


    private File getFileInput(Object input) {
        File outFile = null;
        if (input instanceof String) {
            outFile = new File((String) input);
        } else if (input instanceof File) {
            outFile = (File) input;
        }
        return outFile;
    }

    private VirtualDirEx getInput(Object input) throws IOException {
        File inputFile = getFileInput(input);
        if (inputFile.isFile() && !VirtualDirEx.isPackedFile(inputFile)) {
            final File absoluteFile = inputFile.getAbsoluteFile();
            inputFile = absoluteFile.getParentFile();
            if (inputFile == null) {
                throw new IOException("Unable to retrieve parent file: " + absoluteFile.getAbsolutePath());
            }
        }
        return VirtualDirEx.create(inputFile);
    }

    private InputStream getInputStreamXml() {
        String xmlFile = "";
        try {
            String[] files = null;
            files = virtualDir.listAll();
            for (String file : files) {
                if (file.endsWith(".xml") && file.matches(MuscateConstants.XML_PATTERN)) {
                    xmlFile = file;
                    return virtualDir.getInputStream(file);
                }
            }
        } catch (IOException e) {
            logger.warning(String.format("Unable to get input stream: %s", xmlFile));
        }
        return null;
    }


    private void addQuicklooks(Product product) {
        try {
            String[] files = null;
            files = virtualDir.listAll();
            for (String file : files) {
                if (file.matches(MuscateConstants.QUICKLOOK_PATTERN)) {
                    product.getQuicklookGroup().add(new Quicklook(product, Quicklook.DEFAULT_QUICKLOOK_NAME, virtualDir.getFile(file)));
                }
            }
        } catch (IOException e) {
            logger.warning(String.format("Unable to add quicklook to product %s", product.getName()));
        }
    }

    private void addAngles(Product product, MuscateMetadata.AnglesGrid anglesGrid) {

        //add Zenith
        addAngles(product, "Angles_Zenith_" + anglesGrid.getBandId(),
                  "Viewing zenith angles", anglesGrid.getWidth(), anglesGrid.getHeight(),
                  anglesGrid.getZenith(), anglesGrid.getResX(), anglesGrid.getResY());
        //add Azimuth
        addAngles(product, "Angles_Azimuth_" + anglesGrid.getBandId(),
                  "Viewing azimuth angles", anglesGrid.getWidth(), anglesGrid.getHeight(),
                  anglesGrid.getAzimuth(), anglesGrid.getResX(), anglesGrid.getResY());

    }



    private void addAngles(Product product, String angleBandName, String description, int width, int height, float[] data, float resX, float resY ) {

        int[] bandOffsets = {0};
        SampleModel sampleModel = new PixelInterleavedSampleModel(TYPE_FLOAT, width, height, 1, width, bandOffsets);
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.TRANSLUCENT, TYPE_FLOAT);
        PlanarImage opImage;
        DataBuffer buffer = new DataBufferFloat(width*height);
        // Wrap it in a writable raster
        WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);
        //Search index of angleID

        raster.setPixels(0, 0, width, height, data);

        // And finally create an image with this raster
        BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
        opImage = PlanarImage.wrapRenderedImage(image);

        Band band = new Band(angleBandName, ProductData.TYPE_FLOAT32, width, height);
        band.setDescription(description);
        band.setUnit("°");
        band.setNoDataValue(Double.NaN);
        band.setNoDataValueUsed(true);


        try {
            band.setGeoCoding(new CrsGeoCoding(CRS.decode("EPSG:" + metadata.getEPSG()),
                                               band.getRasterWidth(),
                                               band.getRasterHeight(),
                                               geopositions.get(0).ulx,
                                               geopositions.get(0).uly,
                                               resX,
                                               resY,
                                               0.0, 0.0));
        } catch (Exception e) {
            logger.warning(String.format("Unable to set geocoding to the band %s",angleBandName));
        }

        band.setImageToModelTransform(product.findImageToModelTransform(band.getGeoCoding()));

        //set source image mut be done after setGeocoding and setImageToModelTransform
        band.setSourceImage(opImage);
        product.addBand(band);
    }


    private void addImage(Product product, MuscateImage image) {
        //some checks
        if (product == null) {
            logger.warning("Unable to add image to a null product");
            return;
        }
        if (image == null || image.nature == null) {
            logger.warning(String.format("Unable to add an image with a null nature to the product: %s", product.getName()));
            return;
        }

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

    private void addMask(Product product, MuscateMask mask) {
        //some checks
        if (product == null) {
            logger.warning("Unable to add mask to a null product");
            return;
        }
        if (mask == null || mask.nature == null) {
            logger.warning(String.format("Unable to add a mask with a null nature to the product: %s", product.getName()));
            return;
        }

        if (mask.nature.equals("AOT_Interpolation")) {
            for (String file : mask.getMaskFiles()) {
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addAOTMask(product, file);
                }
            }
        } else if (mask.nature.equals("Cloud")) {
            for (String file : mask.getMaskFiles()) {
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addCloudMask(product, file);
                }

            }
//        } else if (mask.nature.equals("Cloud_Shadow")) {
//            for (String file : mask.getMaskFiles()) {
//                if (!addedFiles.contains(file)) {
//                    addedFiles.add(file);
//                    addCloudShadowMask(product, file);
//                }
//
//            }
        } else if (mask.nature.equals("Edge")) {
            for (String file : mask.getMaskFiles()) {
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addEdgeMask(product, file);
                }
            }
        } else if (mask.nature.equals("Saturation")) {
            for (String file : mask.getMaskFiles()) {
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addSaturationMask(product, file);
                }
            }
        } else if (mask.nature.equals("Geophysics") || mask.nature.equals("Cloud_Shadow")) {
            //In some old products the Nature is Cloud_Shadow. Perhaps an error?
            for (String file : mask.getMaskFiles()) {
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addGeophysicsMask(product, file);
                }
            }
        } else if (mask.nature.equals("Detector_Footprint")) {
            for (String file : mask.getMaskFiles()) {
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addDetectorFootprintMask(product, file);
                }
            }
        } else if (mask.nature.equals("Defective_Pixel")) {
            for (String file : mask.getMaskFiles()) {
                if (!addedFiles.contains(file)) {
                    addedFiles.add(file);
                    addDefectivePixelMask(product, file);
                }
            }
        } else {
            logger.warning(String.format("Unable to add mask. Unknown nature: %s", mask.nature));
        }
    }

    //get the bands and include the product in associated product, to be properly closed when closing Muscate product
    private Band getTifBand(String pathString, int bandIndex) {
        GeoTiffProductReaderPlugIn geoTiffReaderPlugIn = new GeoTiffProductReaderPlugIn();
        final GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(geoTiffReaderPlugIn);
        Product tiffProduct = null;
        Band band = null;
        try {
            //InputStream inputStream = null;
            File inputFile = null;
            try {
                //inputStream = virtualDir.getInputStream(pathString);
                inputFile = virtualDir.getFile(pathString);
            } catch (IOException e) {
                if (/*inputStream == null ||*/ inputFile == null) {
                    String fileName = pathString.substring(pathString.lastIndexOf("/") + 1);
                    //inputStream = virtualDir.getInputStream(fileName);
                    inputFile = virtualDir.getFile(fileName);
                }
            }
            //if (inputStream == null) {
            //    throw new IOException("InputStream null");
            //}

            tiffProduct = geoTiffProductReader.readProductNodes(/*inputStream*/inputFile, null);
            associatedProducts.add(tiffProduct);
            band = tiffProduct.getBandAt(bandIndex);
        } catch (IOException e) {
            logger.warning(String.format("Unable to get band %d of the product: %s", bandIndex, pathString));
            return null;
        }
        return band;
    }

    private MuscateMetadata.Geoposition getGeoposition(int width, int height) {
        for (MuscateMetadata.Geoposition geoposition : geopositions) {
            if (geoposition.nRows == height && geoposition.nCols == width) {
                return geoposition;
            }
        }
        return null;
    }

    private void addAOTImage(Product product, String pathString) {
        Band srcBand = getTifBand(pathString, 1);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }
        MuscateMetadata.Geoposition geoposition = getGeoposition(srcBand.getRasterWidth(), srcBand.getRasterHeight());
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.",
                                         pathString, product.getName()));
            return;
        }
        String bandName = "AOT_" + geoposition.id;

        Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
        product.addBand(targetBand);
        ProductUtils.copyGeoCoding(srcBand, targetBand);
        targetBand.setNoDataValue(metadata.getAOTNoDataValue());
        targetBand.setNoDataValueUsed(true);
        targetBand.setScalingFactor(1.0d / metadata.getAOTQuantificationValue());
        targetBand.setScalingOffset(0.0d);
        targetBand.setSampleCoding(srcBand.getSampleCoding());
        targetBand.setImageInfo(srcBand.getImageInfo());
        targetBand.setDescription(String.format("Aerosol Optical Thickness at %.0fm resolution", geoposition.xDim));
        targetBand.setSourceImage(srcBand.getSourceImage());
    }

    private void addWVCImage(Product product, String pathString) {
        Band srcBand = getTifBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }
        MuscateMetadata.Geoposition geoposition = getGeoposition(srcBand.getRasterWidth(), srcBand.getRasterHeight());
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.",
                                         pathString, product.getName()));
            return;
        }
        String bandName = "WVC_" + geoposition.id;

        Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
        product.addBand(targetBand);
        ProductUtils.copyGeoCoding(srcBand, targetBand);
        targetBand.setNoDataValue(metadata.getWVCNoDataValue());
        targetBand.setNoDataValueUsed(true);
        targetBand.setScalingFactor(1.0d / metadata.getWVCQuantificationValue());
        targetBand.setScalingOffset(0.0d);
        targetBand.setUnit("cm"); //TODO verify
        targetBand.setSampleCoding(srcBand.getSampleCoding());
        targetBand.setImageInfo(srcBand.getImageInfo());
        targetBand.setDescription(String.format("Water vapor content at %.0fm resolution in %s", geoposition.xDim, targetBand.getUnit()));
        targetBand.setSourceImage(srcBand.getSourceImage());

    }


    private void addReflectanceImage(Product product, String pathString, String prefix) {
        Band srcBand = getTifBand(pathString, 0);
        if (srcBand == null) {
            logger.warning(String.format("Image %s not added", pathString));
            return;
        }
        MuscateMetadata.Geoposition geoposition = getGeoposition(srcBand.getRasterWidth(), srcBand.getRasterHeight());
        if (geoposition == null) {
            logger.warning(String.format("Unrecognized geometry of image %s, it will not be added to the product %s.",
                                         pathString, product.getName()));
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

    private void addAOTMask(Product product, String pathString) {
        Band srcBand = getTifBand(pathString, 0);
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

        String bandName = "Aux_Mask_aot_interpolation_" + geoposition.id;
        String maskName = "AOT_Interpolation_Mask_" + geoposition.id;


        Band targetBand = new Band(bandName, srcBand.getDataType(), srcBand.getRasterWidth(), srcBand.getRasterHeight());
        product.addBand(targetBand);
        ProductUtils.copyGeoCoding(srcBand, targetBand);
        targetBand.setNoDataValueUsed(false);
        targetBand.setScalingFactor(1);
        targetBand.setScalingOffset(0);
        targetBand.setSampleCoding(srcBand.getSampleCoding());
        targetBand.setImageInfo(srcBand.getImageInfo());
        targetBand.setDescription("Interpolated AOT pixels mask");
        targetBand.setSourceImage(srcBand.getSourceImage());

        Mask mask = Mask.BandMathsType.create(maskName,
                                              "Interpolated AOT pixels mask",
                                              width, height,
                                              String.format("bit_set(%s,0)", bandName),
                                              Color.BLUE,
                                              0.5);
        ProductUtils.copyGeoCoding(srcBand, mask);
        product.addMask(mask);
    }

    private void addEdgeMask(Product product, String pathString) {
        Band srcBand = getTifBand(pathString, 0);
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
        Band srcBand = getTifBand(pathString, 0);
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
        Band srcBand = getTifBand(pathString, 0);
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

        String bandName = "Aux_Mask_Cloud_" + geoposition.id;

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
        Band srcBand = getTifBand(pathString, 0);
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

        String bandName = "Aux_Mask_MG2_" + geoposition.id;

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

        ColorIterator.reset();

        //addMasks
        Mask mask0 = Mask.BandMathsType.create("MG2_Water_Mask_" + geoposition.id,
                                               "Water mask",
                                               width, height,
                                               String.format("bit_set(%s,0)", bandName),
                                               ColorIterator.next(),
                                               0.5);
        ProductUtils.copyGeoCoding(srcBand, mask0);
        product.addMask(mask0);

        Mask mask1 = Mask.BandMathsType.create("MG2_Cloud_Mask_All_Cloud_" + geoposition.id,
                                               "Result of a 'logical OR' for all the cloud masks",
                                               width, height,
                                               String.format("bit_set(%s,1)", bandName),
                                               ColorIterator.next(),
                                               0.5);
        ProductUtils.copyGeoCoding(srcBand, mask1);
        product.addMask(mask1);

        Mask mask2 = Mask.BandMathsType.create("MG2_Snow_Mask_" + geoposition.id,
                                               "Snow mask",
                                               width, height,
                                               String.format("bit_set(%s,2)", bandName),
                                               ColorIterator.next(),
                                               0.5);
        ProductUtils.copyGeoCoding(srcBand, mask2);
        product.addMask(mask2);

        Mask mask3 = Mask.BandMathsType.create("MG2_Shadow_Mask_Of_Cloud_" + geoposition.id,
                                               "Shadow masks of cloud",
                                               width, height,
                                               String.format("bit_set(%s,3)", bandName),
                                               ColorIterator.next(),
                                               0.5);
        ProductUtils.copyGeoCoding(srcBand, mask3);
        product.addMask(mask3);

        Mask mask4 = Mask.BandMathsType.create("MG2_Topographical_Shadows_Mask_" + geoposition.id,
                                               "Topographical shadows mask",
                                               width, height,
                                               String.format("bit_set(%s,4)", bandName),
                                               ColorIterator.next(),
                                               0.5);
        ProductUtils.copyGeoCoding(srcBand, mask4);
        product.addMask(mask4);

        Mask mask5 = Mask.BandMathsType.create("MG2_Hidden_Areas_Mask_" + geoposition.id,
                                               "Hidden areas mask",
                                               width, height,
                                               String.format("bit_set(%s,5)", bandName),
                                               ColorIterator.next(),
                                               0.5);
        ProductUtils.copyGeoCoding(srcBand, mask5);
        product.addMask(mask5);

        Mask mask6 = Mask.BandMathsType.create("MG2_Sun_Too_Low_Mask_" + geoposition.id,
                                               "Sun too low mask",
                                               width, height,
                                               String.format("bit_set(%s,6)", bandName),
                                               ColorIterator.next(),
                                               0.5);
        ProductUtils.copyGeoCoding(srcBand, mask6);
        product.addMask(mask6);

        Mask mask7 = Mask.BandMathsType.create("MG2_Tangent_Sun_Mask_" + geoposition.id,
                                               "Tangent sun mask",
                                               width, height,
                                               String.format("bit_set(%s,7)", bandName),
                                               ColorIterator.next(),
                                               0.5);
        ProductUtils.copyGeoCoding(srcBand, mask7);
        product.addMask(mask7);
    }

    private int getDetectorFromFilename(String pathString) {
        Pattern p = Pattern.compile(".*D[0-9]{2}\\.tif");
        Matcher m = p.matcher(pathString);
        if(!m.matches()) {
            return 0;
        }
        return Integer.parseInt(pathString.substring(pathString.length()-6,pathString.length()-4));
    }

    private String formatBandNameTo3characters(String band) {
        if(band.startsWith("B") && band.length() == 2) {
            return String.format("B0%c",band.charAt(1));
        } else {
            return band;
        }
    }

    private void addDetectorFootprintMask(Product product, String pathString) {
        Band srcBand = getTifBand(pathString, 0);
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

        Color color = ColorIterator.next();

        //addMasks
        for(int i = 0 ; i < orderedBandNames.length ; i++) {
            Mask mask = Mask.BandMathsType.create(String.format("detector_footprint-%s-%02d",formatBandNameTo3characters(orderedBandNames[i]),detector),
                                      "Detector footprint",
                                      width, height,
                                      String.format("bit_set(%s,%d)", bandName,i),
                                      color,
                                      0.5);
            ProductUtils.copyGeoCoding(srcBand, mask);
            product.addMask(mask);
        }
        return;
    }

    private void addDefectivePixelMask(Product product, String pathString) {
        Band srcBand = getTifBand(pathString, 0);
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

        Color color= ColorIterator.next();

        //addMasks
        for(int i = 0 ; i < orderedBandNames.length ; i++) {
            Mask mask = Mask.BandMathsType.create(String.format("defective_%s",orderedBandNames[i]),
                                                  "Detector footprint",
                                                  width, height,
                                                  String.format("bit_set(%s,%d)", bandName,i),
                                                  color,
                                                  0.5);
            ProductUtils.copyGeoCoding(srcBand, mask);
            product.addMask(mask);
        }
        return;
    }


    @Override
    public void close() throws IOException {
        super.close();
        for (Product product : associatedProducts) {
            product.dispose();
            product = null;
        }
        associatedProducts.clear();
        virtualDir.close();
        geopositions = null;
        associatedProducts = null;
    }

    private static String getBandFromFileName(String filename) {
        Pattern pattern = Pattern.compile(MuscateConstants.REFLECTANCE_PATTERN);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return matcher.group(8);
        }
        return ("UNKNOWN");
    }

    public S2BandAnglesGridByDetector[] getViewingIncidenceAnglesGrids(int bandId, int detectorId){
        if(metadata == null) return null;
        MuscateMetadata.AnglesGrid[] viewingAnglesList = metadata.getViewingAnglesGrid();
        S2BandConstants bandConstants = S2BandConstants.getBand(bandId);

        for(MuscateMetadata.AnglesGrid viewingAngles : viewingAnglesList) {
            if(viewingAngles.getBandId().equals(bandConstants.getPhysicalName()) && Integer.parseInt(viewingAngles.getDetectorId()) == detectorId) {
                S2BandAnglesGridByDetector[] bandAnglesGridByDetector = new S2BandAnglesGridByDetector[2];
                bandAnglesGridByDetector[0] = new S2BandAnglesGridByDetector("view_zenith", bandConstants, detectorId, viewingAngles.getWidth(), viewingAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, viewingAngles.getResX(), viewingAngles.getResY(), viewingAngles.getZenith());
                bandAnglesGridByDetector[1] = new S2BandAnglesGridByDetector("view_azimuth", bandConstants, detectorId, viewingAngles.getWidth(), viewingAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, viewingAngles.getResX(), viewingAngles.getResY(), viewingAngles.getAzimuth());
                return bandAnglesGridByDetector;
            }
        }

        return null;

    }
    public S2BandAnglesGrid[] getSunAnglesGrid(){
        if(metadata == null) return null;
        MuscateMetadata.AnglesGrid sunAngles = metadata.getSunAnglesGrid();

        S2BandAnglesGrid[] bandAnglesGrid = new S2BandAnglesGrid[2];
        bandAnglesGrid[0] = new S2BandAnglesGrid("sun_zenith", null, sunAngles.getWidth(), sunAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, sunAngles.getResX(), sunAngles.getResY(), sunAngles.getZenith());
        bandAnglesGrid[1] = new S2BandAnglesGrid("sun_azimuth", null, sunAngles.getWidth(), sunAngles.getHeight(), (float) metadata.getUpperLeft().x, (float) metadata.getUpperLeft().y, sunAngles.getResX(), sunAngles.getResY(), sunAngles.getAzimuth());
        return bandAnglesGrid;
    }
}
