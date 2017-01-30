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

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.s2tbx.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.geotools.metadata.InvalidMetadataException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This rootProduct reader is intended for reading SPOT-1 to SPOT-5 scene files
 * from compressed archive files or from file system.
 *
 * @author Cosmin Cara
 */
public class SpotDimapSimpleProductReader extends SpotProductReader {

    protected SpotDimapSimpleProductReader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn, colorPaletteFilePath);
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
            return SpotConstants.PROFILE_VOLUME;
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
        Assert.notNull(wrappingMetadata, "This reader should be instantiated only by SpotDimapProductReader");
        Assert.argument(wrappingMetadata.getComponentsMetadata().size() == 1, "Wrong reader for multiple volume components");

        SpotDimapMetadata dimapMetadata = wrappingMetadata.getComponentMetadata(0);
        if (dimapMetadata.getRasterWidth() > 0 && dimapMetadata.getRasterHeight() > 0) {
            product = createProduct(dimapMetadata.getRasterWidth(), dimapMetadata.getRasterHeight(), dimapMetadata);
        }
        for (int fileIndex = 0; fileIndex < wrappingMetadata.getNumComponents(); fileIndex++) {
            addBands(product, wrappingMetadata.getComponentMetadata(fileIndex), fileIndex);
            addMetadataMasks(product, wrappingMetadata.getComponentMetadata(fileIndex));
        }
        product.setModified(false);

        return product;
    }

    @Override
    protected void addBands(Product product, SpotDimapMetadata componentMetadata, int componentIndex) {
        String[] bandNames = componentMetadata.getBandNames();
        String[] bandUnits = componentMetadata.getBandUnits();
        int width, height, currentW, currentH;
        width = product.getSceneRasterWidth();
        height = product.getSceneRasterHeight();
        currentW = componentMetadata.getRasterWidth();
        currentH = componentMetadata.getRasterHeight();
        if (width == currentW && height == currentH) {
            try {
                if (SpotConstants.DIMAP.equals(componentMetadata.getFormatName())) {
                    String[] fileNames = componentMetadata.getRasterFileNames();
                    if (fileNames == null || fileNames.length == 0) {
                        throw new InvalidMetadataException("No raster file found in metadata");
                    }
                    String rasterFileName = componentMetadata.getPath().toLowerCase().replace(componentMetadata.getFileName().toLowerCase(), fileNames[0].toLowerCase());
                    File rasterFile = productDirectory.getFile(rasterFileName);
                    GeoTiffProductReader tiffReader = new GeoTiffReaderEx(getReaderPlugIn());
                    logger.info("Read product nodes");
                    Product tiffProduct = tiffReader.readProductNodes(rasterFile, null);
                    if (tiffProduct != null) {
                        if (product == null) {
                            product = createProduct(tiffProduct.getSceneRasterWidth(), tiffProduct.getSceneRasterHeight(), wrappingMetadata.getComponentMetadata(0));
                        }
                        MetadataElement tiffMetadata = tiffProduct.getMetadataRoot();
                        if (tiffMetadata != null) {
                            XmlMetadata.CopyChildElements(tiffMetadata, product.getMetadataRoot());
                        }
                        tiffProduct.transferGeoCodingTo(product, null);
                        product.setPreferredTileSize(tiffProduct.getPreferredTileSize());

                        int numBands = tiffProduct.getNumBands();
                        String bandPrefix = "";
                        logger.info("Read bands");
                        if (wrappingMetadata.hasMultipleComponents()) {
                            bandPrefix = "scene_" + String.valueOf(componentIndex) + "_";
                            String groupPattern = "";
                            for (int idx = 0; idx < wrappingMetadata.getNumComponents(); idx++) {
                                groupPattern += "scene_" + String.valueOf(idx) + ":";
                            }
                            groupPattern = groupPattern.substring(0, groupPattern.length() - 1);
                            product.setAutoGrouping(groupPattern);
                        }
                        for (int idx = 0; idx < numBands; idx++) {
                            Band srcBand = tiffProduct.getBandAt(idx);
                            String bandName = bandPrefix + (idx < bandNames.length ? bandNames[idx] : SpotConstants.DEFAULT_BAND_NAME_PREFIX + idx);
                            Band targetBand = new ColorPaletteBand(bandName, srcBand.getDataType(), product.getSceneRasterWidth(), product.getSceneRasterHeight(), this.colorPaletteFilePath);
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
                            targetBand.setSpectralBandIndex(srcBand.getSpectralBandIndex());
                            targetBand.setDescription(bandName);

                            product.addBand(targetBand);

                            readBandStatistics(targetBand, idx, componentMetadata);
                            bandMap.put(targetBand, srcBand);
                        }
                    }
                } else {
                    logger.warning(String.format("Component product %s is not in DIMAP format!", componentMetadata.getProductName()));
                }
            } catch (IOException ioEx) {
                logger.severe("Error while reading component: " + ioEx.getMessage());
            }
        } else {
            logger.warning(String.format("Cannot add component product %s due to raster size [Found: %d x %d pixels, Expected: %d x %d pixels]",
                    componentMetadata.getProductName(), currentW, currentH, width, height));
        }
    }

    @Override
    protected void addMetadataMasks(Product product, SpotDimapMetadata componentMetadata) {
        logger.info("Create masks");
        int noDataValue,saturatedValue;
        if ((noDataValue = componentMetadata.getNoDataValue()) >= 0) {
            product.getMaskGroup().add(Mask.BandMathsType.create(SpotConstants.NODATA_VALUE,
                    SpotConstants.NODATA_VALUE,
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    String.valueOf(noDataValue),
                    componentMetadata.getNoDataColor(),
                    0.5));
        }
        if ((saturatedValue = componentMetadata.getSaturatedPixelValue()) >= 0) {
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
