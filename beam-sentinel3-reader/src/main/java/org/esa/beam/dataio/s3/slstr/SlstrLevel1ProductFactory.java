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

import com.bc.ceres.glevel.MultiLevelImage;
import org.esa.beam.dataio.s3.Manifest;
import org.esa.beam.dataio.s3.Sentinel3ProductReader;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.RasterDataNode;

import javax.media.jai.Interpolation;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
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
    protected double getStartOffset(MetadataElement globalAttributes, String sourceBandName) {
        double startOffset = globalAttributes.getAttributeDouble("start_offset");
        if (startOffset != 0) {
            return startOffset;
        }
        if (sourceBandName.endsWith("_an") ||
                sourceBandName.endsWith("_bn") ||
                sourceBandName.endsWith("_cn")) {
            return 1.0;
        } else if (sourceBandName.endsWith("_ao") || sourceBandName.endsWith("_bo") ||
                sourceBandName.endsWith("_co")) {
            return 779.0;
        } else if (sourceBandName.endsWith("_in")) {
            return 0.5;
        } else if (sourceBandName.endsWith("_io")) {
            return 389.5;
        } else if (sourceBandName.endsWith("_to")) {
            return 1.;
        }
        return startOffset;
    }

    @Override
    protected double getTrackOffset(MetadataElement globalAttributes, String sourceBandName) {
        double trackOffset = globalAttributes.getAttributeDouble("track_offset");
        if (trackOffset != 0) {
            return trackOffset;
        } else if (sourceBandName.endsWith("_an") || sourceBandName.endsWith("_cn") ||
                sourceBandName.endsWith("_bn")) {
            return -960.0;
        } else if (sourceBandName.endsWith("_in")) {
            return -480.0;
        } else if (sourceBandName.endsWith("_ao") || sourceBandName.endsWith("_bo") ||
                sourceBandName.endsWith("_co")) {
            return 398.0;
        } else if (sourceBandName.endsWith("_io")) {
            return 199.0;
        } else if (sourceBandName.endsWith("_tx") || sourceBandName.endsWith("_tn")) {
            return -30.0;
        } else if(sourceBandName.endsWith("_to")) {
            return -31.2;
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
        if (resolutions.length == 1) {
            resolutions = new short[]{resolutions[0], resolutions[0]};
        }
        final String productName = globalAttributes.getOwner().getProduct().getName();
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
    protected RenderedImage modifySourceImage(short[] sourceResolutions, RenderingHints renderingHints,
                                              MultiLevelImage sourceImage) {
        final float scaleX = (float) sourceResolutions[0] / (float) referenceResolutions[0];
        final float scaleY;
        if (sourceResolutions.length == 2) {
            scaleY = (float) sourceResolutions[1] / (float) referenceResolutions[1];
        } else {
            scaleY = scaleX;
        }
        RenderedImage image = sourceImage;
        if (scaleX != 1.0 || scaleY != 1.0) {
            image = ScaleDescriptor.create(image, scaleX, scaleY, 0.0f, 0.0f,
                                           Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                                           renderingHints);
        }
        return image;
    }

    @Override
    protected float[] getTiePointGridOffsets(double sourceStartOffset, double sourceTrackOffset,
                                             int subSamplingX, int subSamplingY, short[] sourceResolutions) {
        // TODO - why is the calculation different from that in the super method?
        float[] tiePointGridOffsets = new float[2];
        tiePointGridOffsets[0] = (float) ((referenceTrackOffset -
                // TODO - this cannot be correct: track offset does not have units, source resolution is given in meter;
                // subtraction of numbers with different units makes no sense
                sourceTrackOffset * sourceResolutions[0]) / referenceResolutions[0]) * subSamplingX;
        tiePointGridOffsets[1] = (float) ((sourceStartOffset * sourceResolutions[1] -
                // TODO - see above
                referenceStartOffset) / referenceResolutions[1]) * subSamplingY;
        return tiePointGridOffsets;
    }

    @Override
    protected float[] getOffsets(double sourceStartOffset, double sourceTrackOffset, short[] sourceResolutions) {
        final float offsetX = (float) (sourceTrackOffset * sourceResolutions[0] -
                referenceTrackOffset) / referenceResolutions[0];
        final float offsetY = (float) (sourceStartOffset * sourceResolutions[1] -
                referenceStartOffset) / referenceResolutions[1];
        return new float[]{offsetX, offsetY};
    }

    @Override
    protected void initialize(Product[] sourceProducts, Product targetProduct) {
        super.initialize(sourceProducts, targetProduct);
        referenceStartOffset *= referenceResolutions[0];
        referenceTrackOffset *= referenceResolutions[1];
    }

    @Override
    protected Product findMasterProduct() {
        final List<Product> productList = getOpenProductList();
        Product masterProduct = productList.get(0);
        for (int i = 1; i < productList.size(); i++) {
            Product product = productList.get(i);
            if (product.getSceneRasterWidth() > masterProduct.getSceneRasterWidth() &&
                    product.getSceneRasterHeight() > masterProduct.getSceneRasterHeight()) {
                masterProduct = product;
            }
        }
        return masterProduct;
    }

}
