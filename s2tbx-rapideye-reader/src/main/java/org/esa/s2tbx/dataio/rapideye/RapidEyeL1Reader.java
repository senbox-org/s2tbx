/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio.rapideye;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.apache.commons.lang.StringUtils;
import org.esa.s2tbx.dataio.readers.MultipleMetadataGeoTiffBasedReader;
import org.esa.snap.engine_utilities.file.AbstractFile;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.nitf.NITFMetadata;
import org.esa.s2tbx.dataio.nitf.NITFReaderWrapper;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.*;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.xml.sax.SAXException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ScaleDescriptor;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reader for RapidEye L1 (NITF) products.
 *
 * @author Cosmin Cara
 */
public class RapidEyeL1Reader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(RapidEyeL1Reader.class.getName());

    static {
        XmlMetadataParserFactory.registerParser(RapidEyeMetadata.class, new XmlMetadataParser<>(RapidEyeMetadata.class));
    }

    public static final String UNUSABLE_DATA_BAND_NAME = "unusable_data";

    private final Path colorPaletteFilePath;

    private VirtualDirEx productDirectory;
    private ImageInputStreamSpi imageInputStreamSpi;
    private List<NITFReaderWrapper> bandImageReaders;
    private GeoTiffImageReader geoTiffImageReader;

    RapidEyeL1Reader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn);

        this.colorPaletteFilePath = colorPaletteFilePath;
        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
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

        closeResources();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.productDirectory = VirtualDirEx.build(productPath, !AbstractFile.isLocalPath(productPath), true);

            RapidEyeMetadata metadata = readMetadata(this.productDirectory);

            String productName = metadata.getProductName();
            if (StringUtils.isBlank(productName)) {
                productName = RapidEyeConstants.PRODUCT_GENERIC_NAME;
            }
            int defaultProductWidth = metadata.getRasterWidth();
            int defaultProductHeight = metadata.getRasterHeight();
            ProductSubsetDef subsetDef = getSubsetDef();
            Rectangle productBounds;
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productBounds = new Rectangle(0, 0, defaultProductWidth, defaultProductHeight);
            } else {
                GeoCoding productDefaultGeoCoding = buildTiePointGridGeoCoding(metadata, defaultProductWidth, defaultProductHeight, null);
                productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, false);
            }
            if (productBounds.isEmpty()) {
                throw new IllegalStateException("Empty product bounds.");
            }

            Product product = new Product(productName, RapidEyeConstants.L1_FORMAT_NAMES[0], productBounds.width, productBounds.height, this);
            product.setProductType(metadata.getMetadataProfile());
            product.setStartTime(metadata.getProductStartTime());
            product.setEndTime(metadata.getProductEndTime());
            product.setFileLocation(productPath.toFile());

            Dimension defaultJAIReadTileSize = JAI.getDefaultTileSize();
            //Dimension preferredTileSize = defaultJAIReadTileSize; // new Dimension(defaultJAIReadTileSize.width * 2, defaultJAIReadTileSize.height *2); // multiple mosaic tile size
            Dimension preferredTileSize = new Dimension(defaultProductWidth, defaultProductHeight);

            product.setPreferredTileSize(preferredTileSize);

            if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                product.getMetadataRoot().addElement(metadata.getRootElement());
            }

            if (logger.isLoggable(Level.FINE)) {
                String logMessage = "Use the NITF API to read the RapidEye L1 product from input '" + productPath.toString() + "'.";
                logger.log(Level.FINE, logMessage);
            }

            this.bandImageReaders = new ArrayList<>();
            String[] nitfFiles = metadata.getRasterFileNames();
            if (nitfFiles != null && nitfFiles.length > 0) {
                GeoCoding bandGeoCoding = product.getSceneGeoCoding();
                boolean addMetadataFromNitfAPI = false;
                for (int i = 0; i < nitfFiles.length; i++) {
                    String bandName = getBandName(i);
                    if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                        File localFile = this.productDirectory.getFile(nitfFiles[i]);
                        Band targetBand;
                        NITFReaderWrapper nitfReader = new NITFReaderWrapper(localFile);
                        this.bandImageReaders.add(nitfReader);

                        if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                            NITFMetadata nitfMetadata = nitfReader.getMetadata();
                            if (nitfMetadata != null && !addMetadataFromNitfAPI) {
                                product.getMetadataRoot().addElement(nitfMetadata.getMetadataRoot());
                                addMetadataFromNitfAPI = true;
                            }
                        }
                        targetBand = new ColorPaletteBand(bandName, metadata.getPixelFormat(), productBounds.width, productBounds.height, this.colorPaletteFilePath);
                        if (bandGeoCoding != null) {
                            targetBand.setGeoCoding(bandGeoCoding);
                        }
                        int dataBufferType = ImageManager.getDataBufferType(targetBand.getDataType());
                        RapidEyeL1MultiLevelSource multiLevelSource = new RapidEyeL1MultiLevelSource(nitfReader, dataBufferType, productBounds, preferredTileSize,
                                                                                                     targetBand.getGeoCoding(), defaultJAIReadTileSize);
                        // compute the tile size of the image layout object based on the tile size from the tileOpImage used to read the data
                        ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
                        targetBand.setSourceImage(new DefaultMultiLevelImage(multiLevelSource, imageLayout));
                        targetBand.setSpectralWavelength(RapidEyeConstants.WAVELENGTHS[i]);
//                        targetBand.setUnit("cW/m\u00B2 sr μm");// issues on windows testing platform with special characters, therefore use the characters codes instead
                        targetBand.setUnit("cW/m\u00B2 sr \u03bcm");
                        targetBand.setSpectralBandwidth(RapidEyeConstants.BANDWIDTHS[i]);
                        targetBand.setSpectralBandIndex(i);
                        targetBand.setScalingFactor(metadata.getScaleFactor(i));

                        product.addBand(targetBand);
                    }
                }
            }

            // add masks
            String maskFileName = metadata.getMaskFileName();
            if (maskFileName != null) {
                FlagCoding flagCoding = createFlagCoding();

                if (subsetDef == null || subsetDef.isNodeAccepted(UNUSABLE_DATA_BAND_NAME)) {
                    File file = this.productDirectory.getFile(maskFileName);
                    this.geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(file.toPath());

                    GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(getReaderPlugIn());
                    Product udmGeoTiffProduct = geoTiffProductReader.readProduct(this.geoTiffImageReader, null);
                    Band geoTiffBand = udmGeoTiffProduct.getBandAt(0);
                    float scaleX = (float) metadata.getRasterWidth() / (float) udmGeoTiffProduct.getSceneRasterWidth();
                    float scaleY = (float) metadata.getRasterHeight() / (float) udmGeoTiffProduct.getSceneRasterHeight();
                    RenderedOp renderedOp = ScaleDescriptor.create(geoTiffBand.getSourceImage(), scaleX, scaleY, 0.0f, 0.0f, Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);
                    Band unusableDataBand = product.addBand(UNUSABLE_DATA_BAND_NAME, geoTiffBand.getDataType());
                    unusableDataBand.setSourceImage(renderedOp);
                    unusableDataBand.setSampleCoding(flagCoding);

                    product.getFlagCodingGroup().add(flagCoding);
                }

                String flagCodingName = flagCoding.getName();
                for (String flagName : flagCoding.getFlagNames()) {
                    if (subsetDef == null || subsetDef.isNodeAccepted(flagName)) {
                        MetadataAttribute flag = flagCoding.getFlag(flagName);
                        Mask mask = Mask.BandMathsType.create(flagName, flag.getDescription(), product.getSceneRasterWidth(), product.getSceneRasterHeight(), flagCodingName + "." + flagName, ColorIterator.next(), 0.5);
                        product.getMaskGroup().add(mask);
                    }
                }
            }
            if (product != null) {
                TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(metadata, defaultProductWidth, defaultProductHeight, subsetDef);
                product.addTiePointGrid(productGeoCoding.getLatGrid());
                product.addTiePointGrid(productGeoCoding.getLonGrid());
                product.setSceneGeoCoding(productGeoCoding);
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

    private void closeResources() {
        try {
            if (this.bandImageReaders != null) {
                for (NITFReaderWrapper nitfReader : this.bandImageReaders) {
                    try {
                        nitfReader.close();
                    } catch (Exception ignore) {
                        // ignore
                    }
                }
                this.bandImageReaders.clear();
                this.bandImageReaders = null;
            }
            if (this.geoTiffImageReader != null) {
                this.geoTiffImageReader.close();
                this.geoTiffImageReader = null;
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

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isCompressed()) {
            return super.getProductComponents();
        } else {
            RapidEyeMetadata metadata = null;
            try {
                metadata = readMetadata(this.productDirectory);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

            TreeNode<File> result = super.getProductComponents();
            String[] fileNames = getMetadataFileNames(RapidEyeConstants.METADATA_FILE_SUFFIX);
            for (String fileName : fileNames) {
                try {
                    addProductComponentIfNotPresent(fileName, productDirectory.getFile(fileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", fileName));
                }
            }
            String[] nitfFiles = getRasterFileNames(metadata);
            for(String fileName : nitfFiles){
                try{
                    addProductComponentIfNotPresent(fileName, productDirectory.getFile(fileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", fileName));
                }
            }
            String maskFileName = metadata.getMaskFileName();
            if (maskFileName != null) {
                try {
                    addProductComponentIfNotPresent(maskFileName, productDirectory.getFile(maskFileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", maskFileName));
                }
            }
            return result;
        }
    }

    private String[] getRasterFileNames(RapidEyeMetadata metadata) {
        String[] fileNames;
        if (metadata != null) {
            fileNames = metadata.getRasterFileNames();
        } else {
            try {
                List<String> files = new ArrayList<>();
                String[] productFiles = productDirectory.list(".");
                for (String file : productFiles) {
                    if (file.toLowerCase().endsWith(RapidEyeConstants.NTF_EXTENSION)) {
                        files.add(file);
                    }
                }
                fileNames = new String[files.size()];
                fileNames = files.toArray(fileNames);
            } catch (IOException e) {
                fileNames = new String[0];
                logger.warning(e.getMessage());
            }
        }
        return fileNames;
    }

    private String[] getMetadataFileNames(String exclusion) {
        String[] fileNames;
        try {
            List<String> files = new ArrayList<>();
            String[] productFiles = productDirectory.listAllFiles();
            for (String file : productFiles) {
                String lCase = file.toLowerCase();
                if ((exclusion == null || !lCase.endsWith(exclusion)) && lCase.endsWith(RapidEyeConstants.METADATA_EXTENSION))
                    files.add(file);
            }
            fileNames = files.toArray(new String[0]);
        } catch (IOException e) {
            fileNames = new String[0];
            logger.warning(e.getMessage());
        }
        return fileNames;
    }

    private void addProductComponentIfNotPresent(String componentId, File componentFile, TreeNode<File> currentComponents) {
        TreeNode<File> resultComponent = null;
        for (TreeNode node : currentComponents.getChildren()) {
            if (node.getId().equalsIgnoreCase(componentId.toLowerCase())) {
                //noinspection unchecked
                resultComponent = node;
                break;
            }
        }
        if (resultComponent == null) {
            resultComponent = new TreeNode<>(componentId, componentFile);
            currentComponents.addChild(resultComponent);
        }
    }

    public static String getBandName(int bandIndex) {
        return (bandIndex < RapidEyeConstants.BAND_NAMES.length) ? RapidEyeConstants.BAND_NAMES[bandIndex] : ("band_" + bandIndex);
    }

    public static FlagCoding createFlagCoding() {
        FlagCoding flagCoding = new FlagCoding("unusable_data");
        flagCoding.addFlag(RapidEyeConstants.FLAG_BLACK_FILL, 1, "area was not imaged by spacecraft");
        flagCoding.addFlag(RapidEyeConstants.FLAG_CLOUDS, 2, "cloud covered");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_BLUE_DATA, 4, "missing/suspect data in blue band");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_GREEN_DATA, 8, "missing/suspect data in green band");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_RED_DATA, 16, "missing/suspect data in red band");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_RED_EDGE_DATA, 32, "missing/suspect data in red edge band");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_NIR_DATA, 64, "missing/suspect data in nir band");
        return flagCoding;
    }

    public static RapidEyeMetadata readMetadata(VirtualDirEx productDirectory) throws InstantiationException, ParserConfigurationException, SAXException, IOException {
        String[] filePaths = productDirectory.listAllFiles();
        String productMetadataRelativeFilePath = null;
        List<String> metadataRelativeFilePaths = new ArrayList<>();
        for (String relativeFilePath : filePaths) {
            if (StringUtils.endsWithIgnoreCase(relativeFilePath, RapidEyeConstants.METADATA_FILE_SUFFIX)) {
                productMetadataRelativeFilePath = relativeFilePath;
            } else if (StringUtils.endsWithIgnoreCase(relativeFilePath, RapidEyeConstants.METADATA_EXTENSION)) {
                metadataRelativeFilePaths.add(relativeFilePath);
            }
        }
        if (productMetadataRelativeFilePath == null) {
            throw new NullPointerException("The metadata file is null.");
        }
        RapidEyeMetadata productMetadata = MultipleMetadataGeoTiffBasedReader.readProductMetadata(productDirectory, productMetadataRelativeFilePath, RapidEyeMetadata.class);
        String metadataProfile = productMetadata.getMetadataProfile();
        if (metadataProfile == null || !metadataProfile.startsWith(RapidEyeConstants.PROFILE_L1)) {
            throw new IllegalStateException("The selected product is not a RapidEye L1 product. Please use the appropriate filter");
        }

        for (String fileName : metadataRelativeFilePaths) {
            try {
                logger.info(String.format("Reading metadata file %s", fileName));
                RapidEyeMetadata slaveMetadata;
                try (FilePathInputStream metadataInputStream = productDirectory.getInputStream(fileName)) {
                    slaveMetadata = (RapidEyeMetadata) XmlMetadataParserFactory.getParser(RapidEyeMetadata.class).parse(metadataInputStream);
                    slaveMetadata.setPath(metadataInputStream.getPath());
                    slaveMetadata.setFileName(metadataInputStream.getPath().getFileName().toString());
                }
                MetadataElement newNode = null;
                if (fileName.endsWith("_rpc.xml")) {
                    newNode = new MetadataElement("Rational Polynomial Coefficients");
                    XmlMetadata.CopyChildElements(slaveMetadata.getRootElement(), newNode);
                } else if (fileName.endsWith("_sci.xml")) {
                    newNode = new MetadataElement("Spacecraft Information");
                    XmlMetadata.CopyChildElements(slaveMetadata.getRootElement(), newNode);
                }
                if (newNode != null) {
                    productMetadata.getRootElement().addElement(newNode);
                }
            } catch (IOException e) {
                logger.warning(String.format("Error encountered while opening file %s", fileName));
            }
        }
        return productMetadata;
    }

    public static TiePointGeoCoding buildTiePointGridGeoCoding(RapidEyeMetadata metadata, int width, int height, ProductSubsetDef subsetDef) {
        TiePointGrid latGrid = buildTiePointGrid("latitude", 2, 2, 0, 0, width, height, metadata.getCornersLatitudes(), TiePointGrid.DISCONT_NONE);
        TiePointGrid lonGrid = buildTiePointGrid("longitude", 2, 2, 0, 0, width, height, metadata.getCornersLongitudes(), TiePointGrid.DISCONT_AT_180);
        if (subsetDef != null && subsetDef.getRegion() != null) {
            lonGrid = TiePointGrid.createSubset(lonGrid, subsetDef);
            latGrid = TiePointGrid.createSubset(latGrid, subsetDef);
        }
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    protected static class ColorIterator {
        static final ArrayList<Color> colors;
        static Iterator<Color> colorIterator;

        static {
            colors = new ArrayList<>();
            colors.add(Color.red);
            colors.add(Color.red.darker());
            colors.add(Color.blue);
            colors.add(Color.blue.darker());
            colors.add(Color.green);
            colors.add(Color.green.darker());
            colors.add(Color.yellow);
            colors.add(Color.yellow.darker());
            colors.add(Color.magenta);
            colors.add(Color.magenta.darker());
            colors.add(Color.pink);
            colors.add(Color.pink.darker());
            colors.add(Color.cyan);
            colors.add(Color.cyan.darker());
            colors.add(Color.orange);
            colors.add(Color.orange.darker());
            colors.add(Color.blue.darker().darker());
            colors.add(Color.green.darker().darker());
            colors.add(Color.yellow.darker().darker());
            colors.add(Color.magenta.darker().darker());
            colors.add(Color.pink.darker().darker());
            colorIterator = colors.iterator();
        }

        private ColorIterator() {
        }

        static Color next() {
            if (!colorIterator.hasNext()) {
                colorIterator = colors.iterator();
            }
            return colorIterator.next();
        }
    }
}
