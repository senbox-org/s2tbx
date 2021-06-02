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

package org.esa.s2tbx.dataio.spot;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.s2tbx.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.s2tbx.dataio.spot.dimap.SpotSceneMetadata;
import org.esa.s2tbx.dataio.spot.dimap.VolumeComponent;
import org.esa.s2tbx.dataio.spot.dimap.VolumeMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixCell;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixMultiLevelSource;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.geotools.metadata.InvalidMetadataException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This product reader is intended for reading SPOT-1 to SPOT-5 scene files
 * from compressed archive files or from file system.
 *
 * @author Cosmin Cara
 * modified 20190515 for VFS compatibility by Oana H.
 */
public class SpotDimapProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(SpotDimapProductReader.class.getName());

    static {
        XmlMetadataParserFactory.registerParser(SpotDimapMetadata.class, new XmlMetadataParser<SpotDimapMetadata>(SpotDimapMetadata.class));
    }

    private ImageInputStreamSpi imageInputStreamSpi;
    private VirtualDirEx productDirectory;
    private GeoTiffImageReader geoTiffImageReader;

    public SpotDimapProductReader(SpotDimapProductReaderPlugin readerPlugIn) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
                                          throws IOException {

        throw new UnsupportedOperationException("Method not implemented");
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
            this.productDirectory = VirtualDirEx.build(productPath, false, true);

            SpotSceneMetadata metadata = SpotSceneMetadata.create(this.productDirectory, logger);
            String productType = SpotConstants.DIMAP_FORMAT_NAMES[0];
            Product product;
            if (isSingleVolumeMetadata(metadata.getVolumeMetadata())) {
                product = readSingleVolumeProduct(productPath, metadata, productType);
            } else {
                product = readMultipleVolumeProduct(productPath, metadata,productType);
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

    private Product readMultipleVolumeProduct(Path productPath, SpotSceneMetadata wrappingMetadata, String productType) throws Exception {
        ProductSubsetDef subsetDef = getSubsetDef();
        Dimension defaultProductSize = new Dimension(wrappingMetadata.getExpectedVolumeWidth(), wrappingMetadata.getExpectedVolumeHeight());

        // add bands
        MosaicMatrix[] spotBandMatrices = buildMultipleVolumeBandMatrices(wrappingMetadata);

        Rectangle productBounds;
        if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
            productBounds = new Rectangle(0, 0, defaultProductSize.width, defaultProductSize.height);
        } else {
            java.util.List<SpotDimapMetadata> componentMetadataList = wrappingMetadata.getComponentsMetadata();
            SpotDimapMetadata firstDimapMetadata = componentMetadataList.get(0);
            String rasterFileName = getTiffImageForMultipleVolume(firstDimapMetadata);
            File rasterFile = this.productDirectory.getFile(rasterFileName);
            GeoCoding productDefaultGeoCoding = GeoTiffProductReader.readGeoCoding(rasterFile.toPath(), null);
            boolean isMultiSizeProduct = isMultiSize(spotBandMatrices);
            productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, isMultiSizeProduct);
        }
        if (productBounds.isEmpty()) {
            throw new IllegalStateException("Empty product bounds.");
        }

        java.util.List<SpotDimapMetadata> componentMetadataList = wrappingMetadata.getComponentsMetadata();
        SpotDimapMetadata firstDimapMetadata = componentMetadataList.get(0);

        Product product = new Product(firstDimapMetadata.getProductName(), productType, productBounds.width, productBounds.height, this);
        if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
            product.getMetadataRoot().addElement(wrappingMetadata.getRootElement());
        }
        ProductData.UTC centerTime = firstDimapMetadata.getCenterTime();
        product.setStartTime(centerTime);
        product.setEndTime(centerTime);
        product.setDescription(firstDimapMetadata.getProductDescription());
        product.setFileLocation(productPath.toFile());

        Dimension defaultJAIReadTileSize = JAI.getDefaultTileSize();
        product.setPreferredTileSize(defaultJAIReadTileSize);

        for (int fileIndex=0; fileIndex<componentMetadataList.size(); fileIndex++) {
            SpotDimapMetadata componentMetadata = componentMetadataList.get(fileIndex);
            String rasterFileName = getTiffImageForMultipleVolume(componentMetadata);
            File rasterFile = this.productDirectory.getFile(rasterFileName);
            try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(rasterFile.toPath())) {
                GeoCoding geoCoding = GeoTiffImageReader.buildGeoCoding(geoTiffImageReader.getImageMetadata(), defaultProductSize.width, defaultProductSize.height, productBounds);
                if (geoCoding != null) {
                    product.setSceneGeoCoding(geoCoding);
                    break;
                }
            }
        }

        GeoCoding bandGeoCoding = product.getSceneGeoCoding();
        String[] bandNames = firstDimapMetadata.getBandNames();
        String[] bandUnits = firstDimapMetadata.getBandUnits();
        for (int bandIndex = 0; bandIndex < bandNames.length; bandIndex++) {
            if (!spotBandMatrices[bandIndex].isConsistent()) {
                throw new IllegalStateException("The matrix for band index " + bandIndex+" has empty cells.");
            }
            int matrixTotalWidth = spotBandMatrices[bandIndex].computeTotalWidth();
            if (matrixTotalWidth != defaultProductSize.width) {
                throw new IllegalStateException("Invalid values: matrix total width=" + matrixTotalWidth+", default product width="+ defaultProductSize.width);
            }
            int matrixTotalHeight = spotBandMatrices[bandIndex].computeTotalHeight();
            if (matrixTotalHeight != defaultProductSize.height) {
                throw new IllegalStateException("Invalid values: matrix total height=" + matrixTotalHeight+", default product height="+ defaultProductSize.height);
            }
            int dataBufferType = computeMatrixCellsDataBufferType(spotBandMatrices[bandIndex]);
            if (subsetDef == null || subsetDef.isNodeAccepted(bandNames[bandIndex])) {
                Band band = new Band(bandNames[bandIndex], ImageManager.getProductDataType(dataBufferType), productBounds.width, productBounds.height);
                band.setGeoCoding(bandGeoCoding);
                int noDataValueAsInt = firstDimapMetadata.getNoDataValue();
                Double noDataValue = null;
                if (noDataValueAsInt > -1) {
                    noDataValue = (double)noDataValueAsInt;
                    band.setNoDataValue(noDataValueAsInt);
                    band.setNoDataValueUsed(true);
                }
                float waveLength = firstDimapMetadata.getWavelength(bandIndex);
                if (waveLength > 0) {
                    band.setSpectralWavelength(waveLength);
                }
                float bandWidth = firstDimapMetadata.getBandwidth(bandIndex);
                if (bandWidth > 0) {
                    band.setSpectralBandwidth(bandWidth);
                }
                if (bandIndex < bandUnits.length) {
                    band.setUnit(bandUnits[bandIndex]);
                }
                band.setSpectralBandIndex(bandIndex + 1);
                band.setDescription(bandNames[bandIndex]);

                int maximumBandLevelCount = spotBandMatrices[bandIndex].computeMinimumLevelCount();
                int bandLevelCount = DefaultMultiLevelModel.getLevelCount(productBounds.width, productBounds.height);
                if (bandLevelCount > maximumBandLevelCount) {
                    bandLevelCount = maximumBandLevelCount;
                }
                GeoTiffMatrixMultiLevelSource multiLevelSource = new GeoTiffMatrixMultiLevelSource(bandLevelCount, spotBandMatrices[bandIndex], productBounds,
                                                                                                bandIndex, band.getGeoCoding(), noDataValue, defaultJAIReadTileSize);
                ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
                band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource, imageLayout));
                product.addBand(band);
            }
        }

        // add masks
        ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();
        for (int fileIndex=0; fileIndex<componentMetadataList.size(); fileIndex++) {
            SpotDimapMetadata componentMetadata = componentMetadataList.get(fileIndex);
            int noDataValue = componentMetadata.getNoDataValue();
            if ((subsetDef == null || subsetDef.isNodeAccepted(SpotConstants.NODATA_VALUE)) && noDataValue >= 0) {
                if (!maskGroup.contains(SpotConstants.NODATA_VALUE)) {
                    maskGroup.add(buildNoDataMask(product.getSceneRasterWidth(), product.getSceneRasterHeight(), noDataValue, componentMetadata.getNoDataColor()));
                }
            }
            int saturatedValue = componentMetadata.getSaturatedPixelValue();
            if ((subsetDef == null || subsetDef.isNodeAccepted(SpotConstants.SATURATED_VALUE)) && saturatedValue >= 0) {
                if (!maskGroup.contains(SpotConstants.SATURATED_VALUE)) {
                    maskGroup.add(buildSaturatedMask(product.getSceneRasterWidth(), product.getSceneRasterHeight(), saturatedValue, componentMetadata.getSaturatedColor()));
                }
            }
        }

        return product;
    }

    private MosaicMatrix[] buildMultipleVolumeBandMatrices(SpotSceneMetadata wrappingMetadata) throws Exception {
        java.util.List<SpotDimapMetadata> componentMetadataList = wrappingMetadata.getComponentsMetadata();
        SpotDimapMetadata firstDimapMetadata = componentMetadataList.get(0);
        int tileRowCount = wrappingMetadata.getExpectedTileComponentRows();
        int tileColumnCount = wrappingMetadata.getExpectedTileComponentCols();
        String[] firstMetadataBandNames = firstDimapMetadata.getBandNames();
        MosaicMatrix[] spotBandMatrices = new MosaicMatrix[firstMetadataBandNames.length];
        for (int bandIndex = 0; bandIndex < firstMetadataBandNames.length; bandIndex++) {
            spotBandMatrices[bandIndex] = new MosaicMatrix(tileRowCount, tileColumnCount);
        }

        Path localTempFolder = this.productDirectory.makeLocalTempFolder();
        int dataType = 0;
        for (int fileIndex=0; fileIndex<componentMetadataList.size(); fileIndex++) {
            SpotDimapMetadata componentMetadata = componentMetadataList.get(fileIndex);
            String[] metadataBandNames = componentMetadata.getBandNames();
            if (metadataBandNames.length != firstMetadataBandNames.length) {
                throw new IllegalStateException("Invalid band count: first band name count="+firstMetadataBandNames.length+", band name count="+metadataBandNames.length);
            }
            String rasterFileName = getTiffImageForMultipleVolume(componentMetadata);
            File rasterFile = this.productDirectory.getFile(rasterFileName);
            Path tiffImagePath = rasterFile.toPath();
            int cellWidth;
            int cellHeight;
            int bandCount;
            int dataBufferType;
            try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(tiffImagePath)) {
                SampleModel sampleModel = geoTiffImageReader.getSampleModel();
                if (sampleModel.getNumBands() != firstMetadataBandNames.length) {
                    throw new IllegalStateException("Invalid band count: band count from image="+sampleModel.getNumBands()+", band count from metadata="+firstMetadataBandNames.length);
                }
                cellWidth = geoTiffImageReader.getImageWidth();
                cellHeight = geoTiffImageReader.getImageHeight();
                bandCount = sampleModel.getNumBands();
                dataBufferType = sampleModel.getDataType();
            }
            if (fileIndex == 0) {
                dataType = dataBufferType;
            } else if (dataType != dataBufferType) {
                throw new IllegalStateException("Different data type count: fileIndex=" + fileIndex + ", dataType=" + dataType + ", dataBufferType=" + dataBufferType + ".");
            }

            GeoTiffMatrixCell matrixCell = new GeoTiffMatrixCell(cellWidth, cellHeight, dataBufferType, tiffImagePath, null, localTempFolder);
            for (int bandIndex = 0; bandIndex<bandCount; bandIndex++) {
                spotBandMatrices[bandIndex].addCell(matrixCell);
            }
        }
        return spotBandMatrices;
    }

    private Product readSingleVolumeProduct(Path productPath, SpotSceneMetadata metadata, String productType) throws Exception {
        if (metadata.getComponentsMetadata().size() != 1) {
            throw new IllegalStateException("Wrong reader for multiple volume components");
        }
        SpotDimapMetadata dimapMetadata = metadata.getComponentMetadata(0);
        if (!SpotConstants.DIMAP.equals(dimapMetadata.getFormatName())) {
            throw new IllegalArgumentException(String.format("Component product %s is not in DIMAP format!", dimapMetadata.getProductName()));
        }
        String[] fileNames = dimapMetadata.getRasterFileNames();
        if (fileNames == null || fileNames.length == 0) {
            throw new IllegalStateException("No raster file found in metadata.");
        }
        ProductSubsetDef subsetDef = getSubsetDef();
        Dimension defaultProductSize = new Dimension(dimapMetadata.getRasterWidth(), dimapMetadata.getRasterHeight());

        // add bands
        String rasterFileName = getTiffImageForSingleVolume(dimapMetadata);
        File rasterFile = this.productDirectory.getFile(rasterFileName);
        this.geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(rasterFile.toPath());

        Rectangle productBounds;
        if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
            productBounds = new Rectangle(0, 0, defaultProductSize.width, defaultProductSize.height);
        } else {
            GeoCoding productDefaultGeoCoding = GeoTiffProductReader.readGeoCoding(rasterFile.toPath(), null);
            productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, false);
        }
        if (productBounds.isEmpty()) {
            throw new IllegalStateException("Empty product bounds.");
        }

        int noDataValueAsInt = dimapMetadata.getNoDataValue();
        Double noDataValue = (noDataValueAsInt >= 0) ? (double)noDataValueAsInt : null;

        String productName = (StringUtils.isNullOrEmpty(dimapMetadata.getProductName())) ? SpotConstants.DEFAULT_PRODUCT_NAME : dimapMetadata.getProductName();
        Product product = new Product(productName, productType, productBounds.width, productBounds.height, this);
        Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
        product.setPreferredTileSize(preferredTileSize);
        ProductData.UTC centerTime = dimapMetadata.getCenterTime();
        if (centerTime == null) {
            product.setStartTime(dimapMetadata.getProductStartTime());
            product.setEndTime(dimapMetadata.getProductEndTime());
        } else {
            product.setStartTime(centerTime);
            product.setEndTime(centerTime);
        }
        product.setProductType(dimapMetadata.getMetadataProfile());
        product.setDescription(dimapMetadata.getProductDescription());
        product.setFileLocation(productPath.toFile());

        // validate the image size according to the product size
        geoTiffImageReader.validateSize(defaultProductSize.width, defaultProductSize.height);

        GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(getReaderPlugIn(), null);
        Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, null, productBounds, noDataValue);
        geoTiffProduct.transferGeoCodingTo(product, null);

        if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
            product.getMetadataRoot().addElement(dimapMetadata.getRootElement());
            if (geoTiffProduct.getMetadataRoot() != null) {
                XmlMetadata.CopyChildElements(geoTiffProduct.getMetadataRoot(), product.getMetadataRoot());
            }
        }

        String[] bandNames = dimapMetadata.getBandNames();
        String[] bandUnits = dimapMetadata.getBandUnits();
        for (int bandIndex = 0; bandIndex < geoTiffProduct.getNumBands(); bandIndex++) {
            String bandName = (bandIndex < bandNames.length) ? bandNames[bandIndex] : (SpotConstants.DEFAULT_BAND_NAME_PREFIX + bandIndex);
            if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                Band geoTiffBand = geoTiffProduct.getBandAt(bandIndex);
                geoTiffBand.setName(bandName);
                if (noDataValueAsInt >= 0) {
                    geoTiffBand.setNoDataValue(noDataValueAsInt);
                    geoTiffBand.setNoDataValueUsed(true);
                }
                if (dimapMetadata.getWavelength(bandIndex) > 0) {
                    geoTiffBand.setSpectralWavelength(dimapMetadata.getWavelength(bandIndex));
                }
                if (dimapMetadata.getBandwidth(bandIndex) > 0) {
                    geoTiffBand.setSpectralBandwidth(dimapMetadata.getBandwidth(bandIndex));
                }
                if (bandIndex < bandUnits.length) {
                    geoTiffBand.setUnit(bandUnits[bandIndex]);
                }
                geoTiffBand.setDescription(bandName);
                product.addBand(geoTiffBand);
            }
        }

        // add masks
        if ((subsetDef == null || subsetDef.isNodeAccepted(SpotConstants.NODATA_VALUE)) && noDataValueAsInt >= 0) {
            product.getMaskGroup().add(buildNoDataMask(product.getSceneRasterWidth(), product.getSceneRasterHeight(), noDataValueAsInt, dimapMetadata.getNoDataColor()));
        }
        int saturatedValue = dimapMetadata.getSaturatedPixelValue();
        if ((subsetDef == null || subsetDef.isNodeAccepted(SpotConstants.SATURATED_VALUE)) && saturatedValue >= 0) {
            product.getMaskGroup().add(buildSaturatedMask(product.getSceneRasterWidth(), product.getSceneRasterHeight(), saturatedValue, dimapMetadata.getSaturatedColor()));
        }

        return product;
    }

    private void closeResources() {
        try {
            if (this.geoTiffImageReader != null) {
                try {
                    this.geoTiffImageReader.close();
                } catch (Exception ignore) {
                    // ignore
                }
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
            TreeNode<File> result = super.getProductComponents();
            //if the volume metadata file is present, but it is not in the list, add it!
            SpotSceneMetadata metadata = SpotSceneMetadata.create(this.productDirectory, logger);
            try {
                File volumeMetadataPhysicalFile = productDirectory.getFile(SpotConstants.DIMAP_VOLUME_FILE);
                if (metadata.getVolumeMetadata() != null) {
                    addProductComponentIfNotPresent(SpotConstants.DIMAP_VOLUME_FILE, volumeMetadataPhysicalFile, result);
                    //add components of the volumes (like SCENE01 folders)
                    for (VolumeComponent component : metadata.getVolumeMetadata().getDimapComponents()) {
                        try {
                            //add thumb file of the component
                            addProductComponentIfNotPresent(component.getThumbnailPath(), productDirectory.getFile(component.getThumbnailPath()), result);
                        } catch (IOException ex) {
                            logger.warning(ex.getMessage());
                        }
                        try {
                            //add path file of the component
                            if (component.getType().equals(SpotConstants.DIMAP)) {
                                addProductComponentIfNotPresent(component.getPath(), productDirectory.getFile(component.getPath()), result);
                            }
                        } catch (IOException ex) {
                            logger.warning(ex.getMessage());
                        }
                    }
                }
            } catch (IOException ex) {
                logger.warning(ex.getMessage());
            }
            //add components of the metadatas
            for (SpotDimapMetadata componentMetadata : metadata.getComponentsMetadata()) {
                try {
                    String[] fileNames = componentMetadata.getRasterFileNames();
                    if (fileNames == null || fileNames.length == 0)
                        throw new InvalidMetadataException("No raster file found in metadata");
                    String fileId = componentMetadata.getPath().toString().toLowerCase().replace(componentMetadata.getFileName().toLowerCase(),
                            fileNames[0].toLowerCase());
                    addProductComponentIfNotPresent(fileId, productDirectory.getFile(fileId), result);
                } catch (IOException e) {
                    logger.warning(e.getMessage());
                }
            }
            return result;
        }
    }

    private void addProductComponentIfNotPresent(String componentId, File componentFile, TreeNode<File> currentComponents) {
        TreeNode<File> resultComponent = null;
        for (TreeNode node : currentComponents.getChildren()) {
            if (node.getId().toLowerCase().equals(componentId.toLowerCase())) {
                //noinspection unchecked
                resultComponent = node;
                break;
            }
        }
        if (resultComponent == null) {
            resultComponent = new TreeNode<File>(componentId, componentFile);
            currentComponents.addChild(resultComponent);
        }
    }

    private static Mask buildNoDataMask(int productWidth, int productHeight, int noDataValue, Color noDataColor) {
        return Mask.BandMathsType.create(SpotConstants.NODATA_VALUE, SpotConstants.NODATA_VALUE, productWidth, productHeight, String.valueOf(noDataValue), noDataColor, 0.5);
    }

    private static Mask buildSaturatedMask(int productWidth, int productHeight, int saturatedValue, Color saturatedDataColor) {
        return Mask.BandMathsType.create(SpotConstants.SATURATED_VALUE, SpotConstants.SATURATED_VALUE, productWidth, productHeight, String.valueOf(saturatedValue), saturatedDataColor, 0.5);
    }

    public static boolean isSingleVolumeMetadata(VolumeMetadata volumeMetadata) {
        if (volumeMetadata == null) {
            logger.warning("No volume metadata found. Will assume single volume product.");
            return true;
        }
        if (SpotConstants.PROFILE_MULTI_VOLUME.equals(volumeMetadata.getProfileName())) {
            logger.info("Multi-volume product detected.");
            return false;
        }
        if (SpotConstants.PROFILE_VOLUME.equals(volumeMetadata.getProfileName())) {
            logger.info("Single volume product detected.");
        } else {
            logger.warning("Metadata profile unknown, will use SPOTScene reader.");
        }
        return true;
    }

    public static String getTiffImageForSingleVolume(SpotDimapMetadata dimapMetadata) {
        String[] fileNames = dimapMetadata.getRasterFileNames();
        return dimapMetadata.getPath().toString().toLowerCase().replace(dimapMetadata.getFileName().toLowerCase(), fileNames[0].toLowerCase());
    }

    public static String getTiffImageForMultipleVolume(SpotDimapMetadata componentMetadata) {
        return componentMetadata.getPath().toString().toLowerCase().replace(componentMetadata.getFileName().toLowerCase(), componentMetadata.getRasterFileNames()[0].toLowerCase());
    }

    private static boolean isMultiSize(MosaicMatrix[] spotBandMatrices) throws IOException {
        int defaultFirstBandWidth = spotBandMatrices[0].computeTotalWidth();
        int defaultFirstBandHeight = spotBandMatrices[0].computeTotalHeight();
        for (int i = 1; i < spotBandMatrices.length; i++) {
            if (defaultFirstBandWidth != spotBandMatrices[i].computeTotalWidth()) {
                return true;
            }
            if (defaultFirstBandHeight != spotBandMatrices[i].computeTotalHeight()) {
                return true;
            }
        }
        return false;
    }
    private static int computeMatrixCellsDataBufferType(MosaicMatrix mosaicMatrix) {
        if (mosaicMatrix.getRowCount() > 0 && mosaicMatrix.getColumnCount() > 0) {
            GeoTiffMatrixCell firstMatrixCell = (GeoTiffMatrixCell)mosaicMatrix.getCellAt(0, 0);
            for (int rowIndex = 0; rowIndex < mosaicMatrix.getRowCount(); rowIndex++) {
                for (int columnIndex = 0; columnIndex < mosaicMatrix.getColumnCount(); columnIndex++) {
                    GeoTiffMatrixCell matrixCell = (GeoTiffMatrixCell)mosaicMatrix.getCellAt(rowIndex, columnIndex);
                    if (firstMatrixCell.getDataBufferType() != matrixCell.getDataBufferType()) {
                        throw new IllegalStateException("Different data buffer types: cell at "+rowIndex+", "+columnIndex+" has data type " + matrixCell.getDataBufferType()+" and cell at "+0+", "+0+" has data type " + firstMatrixCell.getDataBufferType()+".");
                    }
                }
            }
            return firstMatrixCell.getDataBufferType();
        } else {
            throw new IllegalArgumentException("The matrix is empty: rowCount="+mosaicMatrix.getRowCount()+", columnCount="+mosaicMatrix.getColumnCount()+".");
        }
    }
}
