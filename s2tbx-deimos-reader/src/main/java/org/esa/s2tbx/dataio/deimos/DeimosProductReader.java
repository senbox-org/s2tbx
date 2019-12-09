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

package org.esa.s2tbx.dataio.deimos;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.deimos.dimap.DeimosConstants;
import org.esa.s2tbx.dataio.deimos.dimap.DeimosMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.readers.GeoTiffBasedReader;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.SAXException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This product reader is intended for reading DEIMOS-1 files
 * from compressed archive files, from tar files or from (uncompressed) file system.
 *
 * @author Cosmin Cara
 */
public class DeimosProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(DeimosProductReader.class.getName());

    static {
        XmlMetadataParserFactory.registerParser(DeimosMetadata.class, new XmlMetadataParser<>(DeimosMetadata.class));
    }

    private VirtualDirEx productDirectory;
    private ImageInputStreamSpi imageInputStreamSpi;
    private List<GeoTiffImageReader> bandImageReaders;

    public DeimosProductReader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
            throws IOException {

        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new DeimosMetadataInspector();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.productDirectory = VirtualDirEx.build(productPath, false, true);

            List<DeimosMetadata> deimosMetadata = readMetadata(this.productDirectory);

            Dimension defaultProductSize = computeMaximumProductSize(deimosMetadata);
            ProductSubsetDef subsetDef = getSubsetDef();
            Rectangle productBounds = ImageUtils.computeProductBounds(defaultProductSize.width, defaultProductSize.height, subsetDef);

            String productName = (deimosMetadata.size() > 0) ?  deimosMetadata.get(0).getProductName() : "Deimos";
            Product product = new Product(productName, DeimosConstants.DIMAP_FORMAT_NAMES[0], defaultProductSize.width, defaultProductSize.height, this);
            product.setFileLocation(productPath.toFile());
            Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
            product.setPreferredTileSize(preferredTileSize);
            String groupPattern = computeGroupPattern(deimosMetadata.size());
            if (!StringUtils.isNullOrEmpty(groupPattern)) {
                product.setAutoGrouping(groupPattern);
            }
            DeimosMetadata firstMetadata = deimosMetadata.get(0);
            ProductData.UTC centerTime = firstMetadata.getCenterTime();
            if (centerTime == null) {
                product.setStartTime(firstMetadata.getProductStartTime());
                product.setEndTime(firstMetadata.getProductEndTime());
            } else {
                product.setStartTime(centerTime);
                product.setEndTime(centerTime);
            }
            List<String> rasterFileNames = getRasterFileNames(deimosMetadata, this.productDirectory);
            if (rasterFileNames.size() < deimosMetadata.size()) {
                throw new ArrayIndexOutOfBoundsException("Invalid size: rasterMetadataList=" + deimosMetadata.size() + ", rasterFileNames=" + rasterFileNames.size());
            }
            this.bandImageReaders = new ArrayList<>(deimosMetadata.size());
            for (int i = 0; i < deimosMetadata.size(); i++) {
                DeimosMetadata currentMetadata = deimosMetadata.get(i);
                File rasterFile = this.productDirectory.getFile(rasterFileNames.get(i));
                GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(rasterFile.toPath());
                this.bandImageReaders.add(geoTiffImageReader);

                Dimension defaultBandSize = geoTiffImageReader.validateSize(currentMetadata.getRasterWidth(), currentMetadata.getRasterHeight());
                Rectangle bandBounds = ImageUtils.computeBandBoundsBasedOnPercent(productBounds, defaultProductSize.width, defaultProductSize.height, defaultBandSize.width, defaultBandSize.height);
                GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(getReaderPlugIn(), null);
                Product getTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, rasterFile.toPath(), bandBounds);

                if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                    product.getMetadataRoot().addElement(currentMetadata.getRootElement());
                    if (getTiffProduct.getMetadataRoot() != null) {
                        XmlMetadata.CopyChildElements(getTiffProduct.getMetadataRoot(), product.getMetadataRoot());
                    }
                }
                if (i == 0) {
                    getTiffProduct.transferGeoCodingTo(product, null);
                    if (getTiffProduct.getPreferredTileSize() != null) {
                        product.setPreferredTileSize(getTiffProduct.getPreferredTileSize());
                    }
                }
                if (DeimosConstants.PROCESSING_1R.equals(currentMetadata.getProcessingLevel())) {
                    //TODO Jean should use firstMetadata to compute the geo coding
                    TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(firstMetadata);
                    if (productGeoCoding != null) {
                        product.addTiePointGrid(productGeoCoding.getLatGrid());
                        product.addTiePointGrid(productGeoCoding.getLonGrid());
                        product.setSceneGeoCoding(productGeoCoding);
                    }
                }

                // add bands
                String[] bandNames = currentMetadata.getBandNames();
                if (bandNames.length != getTiffProduct.getNumBands()) {
                    throw new IllegalStateException("Invalid size: metadata band count="+bandNames.length+", geo tiff product band count=" + getTiffProduct.getNumBands()+".");
                }
                String bandPrefix = computeBandPrefix(deimosMetadata.size(), i);
                for (int k = 0; k < getTiffProduct.getNumBands(); k++) {
                    String bandName = bandPrefix + bandNames[k];
                    if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                        Band geoTiffBand = getTiffProduct.getBandAt(k);
                        geoTiffBand.setName(bandName);
                        product.addBand(geoTiffBand);
                    }
                }

                // remove the bands from the geo tif product
                getTiffProduct.getBandGroup().removeAll();
            }

            // add masks
            ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();
            if (subsetDef == null || subsetDef.isNodeAccepted(DeimosConstants.NODATA_VALUE)) {
                int noDataValue = firstMetadata.getNoDataValue();
                if (noDataValue >= 0 && !maskGroup.contains(DeimosConstants.NODATA_VALUE)) {
                    maskGroup.add(buildNoDataMask(product.getSceneRasterWidth(), product.getSceneRasterHeight(), noDataValue, firstMetadata.getNoDataColor()));
                }
            }
            if (subsetDef == null || subsetDef.isNodeAccepted(DeimosConstants.SATURATED_VALUE)) {
                int saturatedValue = firstMetadata.getSaturatedPixelValue();
                if (saturatedValue >= 0 && !maskGroup.contains(DeimosConstants.SATURATED_VALUE)) {
                    maskGroup.add(buildSaturatedMask(product.getSceneRasterWidth(), product.getSceneRasterHeight(), saturatedValue, firstMetadata.getSaturatedColor()));
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

    public static String computeBandPrefix(int metadataCount, int bandIndex) {
        return (metadataCount > 1) ? ("scene_" + String.valueOf(bandIndex) + "_") : "";
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

    private static <MetadataType extends XmlMetadata> List<String> getRasterFileNames(List<MetadataType> metadata, VirtualDirEx productDirectory) {
        List<String> rasterFileNames = new ArrayList<>();
        if (metadata != null) {
            for (MetadataType metadataComponent : metadata) {
                String[] partialList = metadataComponent.getRasterFileNames();
                if (partialList != null) {
                    rasterFileNames.addAll(Arrays.asList(partialList));
                }
            }
        }
        if (rasterFileNames.size() == 0) {
            try {
                String[] allTiffFiles = productDirectory.findAll(".tif");
                if (allTiffFiles != null) {
                    rasterFileNames.addAll(Arrays.asList(allTiffFiles));
                }
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
        }
        return rasterFileNames;
    }

    public static TiePointGeoCoding buildTiePointGridGeoCoding(DeimosMetadata deimosMetadata) {
        DeimosMetadata.InsertionPoint[] geoPositionPoints = deimosMetadata.getGeopositionPoints();
        if (geoPositionPoints != null) {
            int numPoints = geoPositionPoints.length;
            if (numPoints > 1 && (int)(numPoints / Math.sqrt((double)numPoints)) == numPoints) {
                float stepX = geoPositionPoints[1].stepX - geoPositionPoints[0].stepX;
                float stepY = geoPositionPoints[1].stepY - geoPositionPoints[0].stepY;
                float[] latitudes = new float[numPoints];
                float[] longitudes = new float[numPoints];
                for (int i = 0; i < numPoints; i++) {
                    latitudes[i] = geoPositionPoints[i].y;
                    longitudes[i] = geoPositionPoints[i].x;
                }
                int latitudeGridSize = (int) Math.sqrt(latitudes.length);
                TiePointGrid latGrid = buildTiePointGrid("latitude", latitudeGridSize, latitudeGridSize, 0, 0, stepX, stepY, latitudes, TiePointGrid.DISCONT_NONE);
                int longitudeGridSize = (int) Math.sqrt(longitudes.length);
                TiePointGrid lonGrid = buildTiePointGrid("longitude", longitudeGridSize, longitudeGridSize, 0, 0, stepX, stepY, longitudes, TiePointGrid.DISCONT_AT_180);
                return new TiePointGeoCoding(latGrid, lonGrid);
            }
        }
        return null;
    }

    private static String computeGroupPattern(int metadataCount) {
        String groupPattern = "";
        if (metadataCount > 1) {
            for (int i = 0; i < metadataCount; i++) {
                if (i > 0) {
                    groupPattern += ":";
                }
                groupPattern += "scene_" + String.valueOf(i);
            }
        }
        return groupPattern;
    }

    public static List<DeimosMetadata> readMetadata(VirtualDirEx productDirectory)
            throws IOException, InstantiationException, ParserConfigurationException, SAXException {

        String[] metadataFiles = productDirectory.findAll(DeimosConstants.METADATA_EXTENSION);
        //TODO Jean test on Linux if 'metadatafiles' contains items
        //If the input is archive, the list should contain the full item path(needed for some Deimos products opened on linux)
        //if (productDirectory.isCompressed() && metadataFiles[0].contains("/")) {
        //productDirectory.listAllFilesWithPath();
        //}

        List<DeimosMetadata> metadata = new ArrayList<>(metadataFiles.length);
        for (String file : metadataFiles) {
            try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(file)) {
                DeimosMetadata metaDataItem = (DeimosMetadata) XmlMetadataParserFactory.getParser(DeimosMetadata.class).parse(filePathInputStream);
                Path filePath = filePathInputStream.getPath();
                metaDataItem.setPath(filePath);
                metaDataItem.setFileName(filePath.getFileName().toString());
                metadata.add(metaDataItem);
            }
        }
        if (metadata.size() == 0) {
            throw new IllegalStateException("No metadata files.");
        }
        return metadata;
    }

    public static <MetadataType extends XmlMetadata> Dimension computeMaximumProductSize(List<MetadataType> metadata) {
        MetadataType item = metadata.get(0);
        int width = item.getRasterWidth();
        int height = item.getRasterHeight();
        for (int i=1; i<metadata.size(); i++) {
            item = metadata.get(i);
            if (width < item.getRasterWidth()) {
                width = item.getRasterWidth();
            }
            if (height < item.getRasterHeight()) {
                height = item.getRasterHeight();
            }
        }
        if (width <= 0) {
            throw new IllegalStateException("The product width " + width + " is invalid.");
        }
        if (height <= 0) {
            throw new IllegalStateException("The product height " + height + " is invalid.");
        }
        return new Dimension(width, height);
    }

    private static Mask buildNoDataMask(int productWith, int productHeight, int noDataValue, Color noDataColor) {
        return Mask.BandMathsType.create(DeimosConstants.NODATA_VALUE, DeimosConstants.NODATA_VALUE, productWith, productHeight, String.valueOf(noDataValue), noDataColor, 0.5);
    }

    private static Mask buildSaturatedMask(int productWith, int productHeight, int saturatedValue, Color saturatedColor) {
        return Mask.BandMathsType.create(DeimosConstants.SATURATED_VALUE, DeimosConstants.SATURATED_VALUE, productWith, productHeight, String.valueOf(saturatedValue), saturatedColor, 0.5);
    }
}
