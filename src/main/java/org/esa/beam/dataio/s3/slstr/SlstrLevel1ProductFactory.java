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
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.jai.ImageManager;

import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FilenameFilter;
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
                        || name.contains("geometry") || name.contains("indices") || name.contains("met"));
            }
        });


        return Arrays.asList(fileNames);
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
    protected RenderedImage modifySourceImage(short[] sourceResolutions, RenderingHints renderingHints, MultiLevelImage sourceImage) {
        final float scaleX = sourceResolutions[0] / referenceResolutions[0];
        final float scaleY;
        if (sourceResolutions.length == 2) {
            scaleY = sourceResolutions[1] / referenceResolutions[1];
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
    protected float[] getOffsets(double sourceStartOffset, double sourceTrackOffset) {
        final float offsetX = (float) (referenceTrackOffset - sourceTrackOffset);
        final float offsetY = (float) (sourceStartOffset - referenceStartOffset);
        return new float[]{offsetX, offsetY};
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
