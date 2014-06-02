package org.esa.beam.dataio.s3.slstr;/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import org.esa.beam.dataio.s3.Manifest;
import org.esa.beam.dataio.s3.Sentinel3ProductReader;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.RasterDataNode;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SlstrLevel1ProductFactory extends SlstrProductFactory {

    private Character penUltimateChar;

    public SlstrLevel1ProductFactory(Sentinel3ProductReader productReader) {
        super(productReader);
    }

    @Override
    protected List<String> getFileNames(Manifest manifest) {
        final File directory = getInputFileParentDirectory();

        final String[] fileNames = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".nc") && (name.contains("radiance") || name.contains("flags")
                        || name.contains("geodetic_tx") || name.contains("BT") || name.contains("cartesian_tx")
                        || name.contains("indices") || name.contains("met")
                );
            }
        });


        return Arrays.asList(fileNames);
    }

    @Override
    protected double getStartOffset(MetadataElement globalAttributes) {
        final String sourceProductName = globalAttributes.getProduct().getName();
        final double startOffset = globalAttributes.getAttributeDouble("start_offset");
        if (startOffset != 0.0) {
            return startOffset;
        }
        if (sourceProductName.contains("flags") && sourceProductName.endsWith("o")) {
            return 0.0;
        } else if (sourceProductName.endsWith("_in")) {
            return 0.5;
        } else if (sourceProductName.endsWith("_io")) {
            return 389.5;
        } else if (sourceProductName.endsWith("_an") ||
                sourceProductName.endsWith("_bn") ||
                sourceProductName.endsWith("_cn")) {
            return 1.0;
        } else if (sourceProductName.endsWith("_ao") || sourceProductName.endsWith("_bo") ||
                sourceProductName.endsWith("_co")) {
            return 779.0;
        } else if (sourceProductName.endsWith("_to")) {
            return 20.;
        }
        return startOffset;
    }

    @Override
    protected double getTrackOffset(MetadataElement globalAttributes) {
        final String sourceProductName = globalAttributes.getProduct().getName();
        final double trackOffset = globalAttributes.getAttributeDouble("track_offset");
        if (trackOffset != 0) {
            return trackOffset;
        } else if (sourceProductName.contains("flags") && sourceProductName.endsWith("o")) {
            return 50.0;
        } else if (sourceProductName.endsWith("_in")) {
            return -471.5;
        } else if (sourceProductName.endsWith("_io")) {
            return 207.0;
        } else if (sourceProductName.endsWith("_an") || sourceProductName.endsWith("_cn") ||
                sourceProductName.endsWith("_bn")) {
            return -943.0;
        } else if (sourceProductName.endsWith("_ao") || sourceProductName.endsWith("_bo") ||
                sourceProductName.endsWith("_co")) {
            return 412.0;
        } else if (sourceProductName.endsWith("_tx") || sourceProductName.endsWith("_tn")) {
            return -30.0;
        } else if (sourceProductName.endsWith("_to")) {
            return -70.;
        }
        return trackOffset;
    }

    @Override
    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        super.configureTargetNode(sourceBand, targetNode);
        final String productName = sourceBand.getProduct().getName();
        final String targetNodeName = targetNode.getName();
        if (targetNodeName.contains("BT") || targetNodeName.contains("radiance") & !(targetNodeName.contains("exception"))) {
            final String path = sourceBand.getProduct().getFileLocation().getAbsolutePath();
            String qualityProductName = productName.replace("BT", "quality").replace("radiance", "quality");
            final String qualityProductPath = path.replace(productName, qualityProductName);
            try {
                final Product product = ProductIO.readProduct(qualityProductPath);
                if (product != null) {
                    final float wavelength = product.getMetadataRoot().getElement("Variable_Attributes").getElement(
                            "band_centre").
                            getElement("values").getAttribute("data").getData().getElemFloat() * 1000;
                    ((Band) targetNode).setSpectralWavelength(wavelength);
                    final float bandwidth = product.getMetadataRoot().getElement("Variable_Attributes").getElement(
                            "bandwidth").
                            getElement("values").getAttribute("data").getData().getElemFloat() * 1000;
                    ((Band) targetNode).setSpectralBandwidth(bandwidth);
                }
            } catch (IOException e) {
                //no spectral properties can be assigned
            }
        }
    }

    @Override
    protected boolean isTiePointGrid(short[] sourceResolutions) {
        return penUltimateChar.compareTo('t') == 0;
    }

    @Override
    protected short[] getResolutions(MetadataElement globalAttributes) {
        short[] resolutions = super.getResolutions(globalAttributes);
        final String productName = globalAttributes.getProduct().getName();
        penUltimateChar = productName.charAt(productName.length() - 2);
        if (resolutions[0] == 0 && resolutions[1] == 0) {
            if (penUltimateChar.compareTo('i') == 0) {
                resolutions = new short[]{1000, 1000};
            } else if (penUltimateChar.compareTo('t') == 0) {
                resolutions = new short[]{16000, 16000};
            } else {
                resolutions = new short[]{500, 500};
            }
        }
        return resolutions;
    }

    @Override
    protected Product findMasterProduct() {
        final List<Product> productList = getOpenProductList();
        Product masterProduct = productList.get(0);
        for (int i = 1; i < productList.size(); i++) {
            Product product = productList.get(i);
            if (product.getSceneRasterWidth() > masterProduct.getSceneRasterWidth() &&
                    product.getSceneRasterHeight() > masterProduct.getSceneRasterHeight() &&
                    !product.getName().contains("flags")) {
                masterProduct = product;
            }
        }
        return masterProduct;
    }

    @Override
    protected void setAutoGrouping(Product[] sourceProducts, Product targetProduct) {
        String bandGrouping = getAutoGroupingString(sourceProducts);
        String[] unwantedGroups = new String[]{"F1_BT", "F2_BT", "S1_radiance", "S2_radiance", "S3_radiance",
                "S4_radiance", "S5_radiance", "S6_radiance", "S7_BT", "S8_BT", "S9_BT"};
        for (String unwantedGroup : unwantedGroups) {
            if (bandGrouping.startsWith(unwantedGroup)) {
                bandGrouping = bandGrouping.replace(unwantedGroup + ":", "");
            } else if (bandGrouping.contains(unwantedGroup)) {
                bandGrouping = bandGrouping.replace(":" + unwantedGroup, "");
            }
        }
        StringBuilder patternGrouping = new StringBuilder("F*BT_in*:F*BT_io*:radiance_an:" +
                                                                  "radiance_ao:radiance_bn:" +
                                                                  "radiance_bo:radiance_cn:" +
                                                                  "radiance_co:S*BT_in*:" +
                                                                  "S*BT_io*:");
        patternGrouping.append(bandGrouping);
        targetProduct.setAutoGrouping(patternGrouping.toString());
    }
}
