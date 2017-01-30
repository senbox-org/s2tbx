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
import org.esa.s2tbx.dataio.BandMatrix;
import org.esa.s2tbx.dataio.ByteArrayOutputStream;
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.s2tbx.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.geotools.metadata.InvalidMetadataException;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * This reader is intended for reading SPOT-4 and SPOT-5 View files composed
 * of several products (for instance, large products that are split into several smaller ones.
 * @author Cosmin Cara
 */
public class SpotDimapVolumeProductReader extends SpotProductReader {
    private final Map<Band, BandMatrix> bandMap;
    protected final Map<int[], ProductData> readLines;

    protected SpotDimapVolumeProductReader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn, colorPaletteFilePath);

        bandMap = new HashMap<Band, BandMatrix>();
        readLines = new HashMap<int[], ProductData>();
    }

    @Override
    protected String getMetadataExtension() {
        return SpotConstants.DIMAP_DEFAULT_EXTENSIONS[0];
    }

    @Override
    protected String getMetadataProfile() {
        if (metadata != null && metadata.size() > 0) {
            return metadata.get(0).getMetadataProfile();
        } else {
            return SpotConstants.PROFILE_MULTI_VOLUME;
        }
    }

    @Override
    protected String getProductGenericName() {
        if (metadata != null && metadata.size() > 0) {
            return metadata.get(0).getProductName();
        } else {
            return SpotConstants.DEFAULT_PRODUCT_NAME;
        }
    }

    @Override
    protected String getMetadataFileSuffix() {
        return SpotConstants.SPOTSCENE_METADATA_FILE;
    }

    @Override
    protected String[] getBandNames() {
        return SpotConstants.DEFAULT_BAND_NAMES;
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        SpotDimapMetadata dimapMetadata = wrappingMetadata.getComponentMetadata(0);

        int width = wrappingMetadata.getExpectedVolumeWidth();
        int height = wrappingMetadata.getExpectedVolumeHeight();

        Product rootProduct = new Product(dimapMetadata.getProductName(), SpotConstants.DIMAP_FORMAT_NAMES[0], width, height);
        rootProduct.getMetadataRoot().addElement(wrappingMetadata.getRootElement());
        ProductData.UTC centerTime = dimapMetadata.getCenterTime();
        rootProduct.setStartTime(centerTime);
        rootProduct.setEndTime(centerTime);
        rootProduct.setDescription(dimapMetadata.getProductDescription());

        int numBands = dimapMetadata.getNumBands();
        String[] bandNames = wrappingMetadata.getComponentMetadata(0).getBandNames();
        for (int i = 0; i < numBands; i++) {
            ColorPaletteBand band = new ColorPaletteBand(bandNames[i], dimapMetadata.getPixelDataType(), width, height, this.colorPaletteFilePath);
            rootProduct.addBand(band);
            bandMap.put(band, new BandMatrix(wrappingMetadata.getExpectedTileComponentRows(), wrappingMetadata.getExpectedTileComponentCols()));
        }

        for (int fileIndex = 0; fileIndex < wrappingMetadata.getNumComponents(); fileIndex++) {
            addBands(rootProduct, wrappingMetadata.getComponentMetadata(fileIndex));
            addMasks(rootProduct, wrappingMetadata.getComponentMetadata(fileIndex));
        }
        rootProduct.setModified(false);

        return rootProduct;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY,
                                          int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY,
                                          Band destBand,
                                          int destOffsetX, int destOffsetY,
                                          int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm) throws IOException {
        int[] key = new int[] { sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY };
        if (!readLines.containsKey(key)) {
            BandMatrix bandMatrix = bandMap.get(destBand);
            BandMatrix.BandMatrixCell[] cells = bandMatrix.getCells();
            int readWidth = 0;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (BandMatrix.BandMatrixCell cell : cells) {
                Rectangle readArea = cell.intersection(destOffsetX, destOffsetY, destWidth, destHeight);
                if (readArea != null) {
                    ProductReader reader = cell.band.getProductReader();
                    if (reader == null) {
                        logger.severe("No reader found for band data");
                    } else {
                        int bandDestOffsetX = readArea.x - cell.cellStartPixelX;
                        int bandDestOffsetY = readArea.y - cell.cellStartPixelY;
                        int bandDestWidth = readArea.width;
                        int bandDestHeight = readArea.height;
                        ProductData bandBuffer = createProductData(destBuffer.getType(), bandDestWidth * bandDestHeight);
                        reader.readBandRasterData(cell.band, bandDestOffsetX, bandDestOffsetY, bandDestWidth, bandDestHeight, bandBuffer, pm);
                        MemoryCacheImageOutputStream writeStream = null;
                        ImageInputStream readStream = null;
                        try {
                            byteArrayOutputStream.reset();
                            writeStream = new MemoryCacheImageOutputStream(byteArrayOutputStream);
                            bandBuffer.writeTo(writeStream);
                            writeStream.flush();
                            readStream = new MemoryCacheImageInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

                            for (int y = 0; y < destHeight; y++) {
                                destBuffer.readFrom(y * destWidth + readWidth, bandDestWidth, readStream);
                            }
                            readWidth += bandDestWidth;
                        } finally {
                            if (writeStream != null) writeStream.close();
                            if (readStream != null) readStream.close();
                        }
                    }
                }
            }
            readLines.put(key, destBuffer);
        } else {
            logger.info("Line already read");
            destBuffer = readLines.get(key);
        }
    }

    private void addBands(Product product, SpotDimapMetadata componentMetadata) {
        String[] bandUnits = componentMetadata.getBandUnits();
        try {
            if (SpotConstants.DIMAP.equals(componentMetadata.getFormatName())) {
                String[] rasterFileNames = componentMetadata.getRasterFileNames();
                if (rasterFileNames == null || rasterFileNames.length == 0) {
                    throw new InvalidMetadataException("No raster file name found in metadata");
                }
                String rasterFileName = componentMetadata.getPath().toLowerCase().replace(componentMetadata.getFileName().toLowerCase(), componentMetadata.getRasterFileNames()[0].toLowerCase());
                File rasterFile = productDirectory.getFile(rasterFileName);
                GeoTiffProductReader tiffReader = new GeoTiffProductReader(getReaderPlugIn());
                logger.info("Read product nodes for component " + componentMetadata.getProductName());
                Product tiffProduct = tiffReader.readProductNodes(rasterFile, null);
                if (tiffProduct != null) {
                    MetadataElement tiffMetadata = tiffProduct.getMetadataRoot();
                    if (tiffMetadata != null) {
                        XmlMetadata.CopyChildElements(tiffMetadata, product.getMetadataRoot());
                    }
                    if (product.getSceneGeoCoding() == null) {
                        tiffProduct.transferGeoCodingTo(product, null);
                    }
                    if (product.getPreferredTileSize() == null) {
                        product.setPreferredTileSize(tiffProduct.getPreferredTileSize());
                    }
                    int numTiffBands = tiffProduct.getNumBands();
                    logger.info("Read bands for component " + componentMetadata.getProductName());
                    for (int idx = 0; idx < numTiffBands; idx++) {
                        Band srcBand = tiffProduct.getBandAt(idx);
                        Band targetBand = product.getBandAt(idx);
                        SpotDimapMetadata.InsertionPoint insertPoint = componentMetadata.getInsertPoint();
                        bandMap.get(targetBand).addCell(srcBand,
                                                        new Point2D.Float(insertPoint.x, insertPoint.y),
                                                        insertPoint.stepX, insertPoint.stepY);
                        targetBand.setNoDataValue(componentMetadata.getNoDataValue() > -1 ? componentMetadata.getNoDataValue() : srcBand.getNoDataValue());
                        targetBand.setNoDataValueUsed((componentMetadata.getNoDataValue() > -1));
                        targetBand.setSpectralWavelength(componentMetadata.getWavelength(idx) > 0 ? componentMetadata.getWavelength(idx) : srcBand.getSpectralWavelength());
                        targetBand.setSpectralBandwidth(componentMetadata.getBandwidth(idx) > 0 ? componentMetadata.getBandwidth(idx) : srcBand.getSpectralBandwidth());
                        targetBand.setScalingFactor(srcBand.getScalingFactor());
                        targetBand.setScalingOffset(srcBand.getScalingOffset());
                        targetBand.setSolarFlux(srcBand.getSolarFlux());
                        targetBand.setUnit(srcBand.getUnit() != null ? srcBand.getUnit() : bandUnits[idx]);
                        targetBand.setSampleCoding(srcBand.getSampleCoding());
                        targetBand.setImageInfo(srcBand.getImageInfo());
                        //targetBand.setSpectralBandIndex(srcBand.getSpectralBandIndex());
                        targetBand.setSpectralBandIndex(idx + 1);
                        readBandStatistics(targetBand, idx, componentMetadata);
                        if (targetBand.getDescription() == null)
                            if (srcBand.getDescription() == null)
                                targetBand.setDescription("Combined " + targetBand.getName() + " from underlying components");
                            else
                                targetBand.setDescription(srcBand.getDescription());
                    }
                }
            } else {
                logger.warning(String.format("Component product %s is not in DIMAP format!", componentMetadata.getProductName()));
            }
        } catch (IOException ioEx) {
            logger.severe("Error while reading component: " + ioEx.getMessage());
        }
    }

    private void addMasks(Product product, SpotDimapMetadata componentMetadata) {
        logger.info("Create masks");
        int noDataValue,saturatedValue;
        if ((noDataValue = componentMetadata.getNoDataValue()) >= 0 && !product.getMaskGroup().contains(SpotConstants.NODATA_VALUE)) {
            product.getMaskGroup().add(Mask.BandMathsType.create(SpotConstants.NODATA_VALUE,
                    SpotConstants.NODATA_VALUE,
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    String.valueOf(noDataValue),
                    componentMetadata.getNoDataColor(),
                    0.5));
        }
        if ((saturatedValue = componentMetadata.getSaturatedPixelValue()) >= 0 && !product.getMaskGroup().contains(SpotConstants.SATURATED_VALUE)) {
            product.getMaskGroup().add(Mask.BandMathsType.create(SpotConstants.SATURATED_VALUE,
                    SpotConstants.SATURATED_VALUE,
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    String.valueOf(saturatedValue),
                    componentMetadata.getSaturatedColor(),
                    0.5));
        }
    }
}
