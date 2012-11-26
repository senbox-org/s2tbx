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
import org.esa.beam.dataio.s3.ManifestI;
import org.esa.beam.dataio.s3.Sentinel3ProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
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

    private double masterStartOffset;
    private double masterTrackOffset;
    private short[] masterResolutions;

    public SlstrLevel1ProductFactory(Sentinel3ProductReader productReader) {
        super(productReader);
    }

    @Override
    protected List<String> getFileNames(ManifestI manifest) {
        final File directory = getInputFileParentDirectory();

        final String[] fileNames = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                //todo add other filenames
                return name.endsWith(".nc") && (name.contains("radiance") || name.contains("flags")
                        || name.contains("geodetic_tx") || name.contains("BT"));
            }
        });


        return Arrays.asList(fileNames);
    }

    @Override
    protected void setTimes(Product targetProduct) {
    }

//    @Override
//    protected Band addBand(Band sourceBand, Product targetProduct) {
//        final Product sourceProduct = sourceBand.getProduct();
//        final MetadataElement globalAttributes = sourceProduct.getMetadataRoot().getElement("Global_Attributes");
//        final double sourceStartOffset = globalAttributes.getAttributeDouble("start_offset");
//        final double sourceTrackOffset = globalAttributes.getAttributeDouble("track_offset");
//        short[] sourceResolutions = (short[]) globalAttributes.getAttribute("resolution").getDataElems();
//        final char penUltimateChar = sourceProduct.getName().charAt(sourceProduct.getName().length() - 2);
//        if (((Character) penUltimateChar).compareTo('i') == 0) {
//            sourceResolutions = new short[]{1000, 1000};
//        } else {
//            sourceResolutions = new short[]{500, 500};
//        }
//
//        final Band targetBand = copyBand(sourceBand, targetProduct, false);
//        final ImageLayout imageLayout = ImageManager.createSingleBandedImageLayout(targetBand);
//        final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);
//
//        final MultiLevelImage sourceImage = sourceBand.getSourceImage();
//        final int targetW = targetBand.getRasterWidth();
//        final int targetH = targetBand.getRasterHeight();
////        final float offsetX = (float) (sourceTrackOffset - masterTrackOffset);
////        final float offsetY = (float) (sourceStartOffset - masterStartOffset);
//
//        final float offsetX = (float) (sourceTrackOffset * sourceResolutions[0] / masterResolutions[0] - masterTrackOffset);
//        final float offsetY = (float) (sourceStartOffset * sourceResolutions[1] / masterResolutions[1] - masterStartOffset);
//
//        final int padX = Math.round(Math.abs(offsetX));
//        final int padY = Math.round(Math.abs(offsetY));
//        final BorderExtender borderExtender = new BorderExtenderConstant(new double[]{targetBand.getNoDataValue()});
//        final RenderedImage extendedImage = BorderDescriptor.create(sourceImage,
//                                                                    padX,
//                                                                    targetW - padX - sourceImage.getWidth(),
//                                                                    padY,
//                                                                    padY,
//                                                                    borderExtender, renderingHints);
//
//        final RenderedImage translatedImage = TranslateDescriptor.create(extendedImage,
//                                                                         offsetX,
//                                                                         offsetY,
//                                                                         null,
//                                                                         renderingHints);
//        final RenderedImage croppedImage = CropDescriptor.create(translatedImage, 0.0f, 0.0f,
//                                                                 (float) targetW,
//                                                                 (float) targetH,
//                                                                 renderingHints);
//        targetBand.setSourceImage(croppedImage);
//        return targetBand;
//    }

    @Override
    protected RasterDataNode addSpecialNode(Band sourceBand, Product targetProduct) {
        final Product sourceProduct = sourceBand.getProduct();
        final MetadataElement globalAttributes = sourceProduct.getMetadataRoot().getElement("Global_Attributes");
        final double sourceStartOffset = globalAttributes.getAttributeDouble("start_offset");
        final double sourceTrackOffset = globalAttributes.getAttributeDouble("track_offset");
        short[] sourceResolutions = (short[]) globalAttributes.getAttribute("resolution").getDataElems();
        if (sourceResolutions.length == 1) {
            sourceResolutions = new short[]{sourceResolutions[0], sourceResolutions[0]};
        }
        final char penUltimateChar = sourceProduct.getName().charAt(sourceProduct.getName().length() - 2);
        if (sourceResolutions[0] == 0 && sourceResolutions[1] == 0) {
            if (((Character) penUltimateChar).compareTo('i') == 0) {
                sourceResolutions = new short[]{1000, 1000};
            } else if (((Character) penUltimateChar).compareTo('t') == 0) {
                sourceResolutions = new short[]{16000, 16000};
            } else {
                sourceResolutions = new short[]{500, 500};
            }
        }
        if (((Character) penUltimateChar).compareTo('t') != 0) {
            final Band targetBand = copyBand(sourceBand, targetProduct, false);
            final ImageLayout imageLayout = ImageManager.createSingleBandedImageLayout(targetBand);
            final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);

            final MultiLevelImage sourceImage = sourceBand.getSourceImage();
            final int targetW = targetBand.getRasterWidth();
            final int targetH = targetBand.getRasterHeight();

//            final float offsetX = (float) (sourceTrackOffset * sourceResolutions[0] / masterResolutions[0] - masterTrackOffset);
//            final float offsetY = (float) (sourceStartOffset * sourceResolutions[1] / masterResolutions[1] - masterStartOffset);

            final float scaleX = sourceResolutions[0] / masterResolutions[0];
            final float scaleY;
            if (sourceResolutions.length == 2) {
                scaleY = sourceResolutions[1] / masterResolutions[1];
            } else {
                scaleY = scaleX;
            }
            final float offsetX = (float) (sourceTrackOffset - masterTrackOffset);
            final float offsetY = (float) (sourceStartOffset - masterStartOffset);

            final int padX = Math.round(Math.abs(offsetX));
            final int padY = Math.round(Math.abs(offsetY));

            RenderedImage image = sourceImage;
            if (scaleX != 1.0 || scaleY != 1.0) {
                image = ScaleDescriptor.create(image, scaleX, scaleY, 0.0f, 0.0f,
                                               Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                                               renderingHints);
            }
            final BorderExtender borderExtender = new BorderExtenderConstant(new double[]{targetBand.getNoDataValue()});
            image = BorderDescriptor.create(image,
                                            padX,
                                            targetW - padX - image.getWidth(),
                                            padY,
                                            padY,
                                            borderExtender, renderingHints);
            if (offsetX != 0.0f || offsetY != 0.0f) {
                image = TranslateDescriptor.create(image,
                                                   offsetX,
                                                   offsetY,
                                                   null,
                                                   renderingHints);
            }
            image = CropDescriptor.create(image, 0.0f, 0.0f,
                                          (float) targetW,
                                          (float) targetH,
                                          renderingHints);
            targetBand.setSourceImage(image);
            return targetBand;
        } else { // tie-point data
            final int subSamplingX = sourceResolutions[0] / masterResolutions[0];
//            final int subSamplingY = sourceResolutions[1] / masterResolutions[1];
            final int subSamplingY;
            if (sourceResolutions.length == 2) {
                subSamplingY = sourceResolutions[1] / masterResolutions[1];
            } else {
                //noinspection SuspiciousNameCombination
                subSamplingY = subSamplingX;
            }
            final float offsetX = (float) (masterTrackOffset - sourceTrackOffset * subSamplingX);
            final float offsetY = (float) (sourceStartOffset * subSamplingY - masterStartOffset);
//            final float offsetX = (float) (sourceTrackOffset * sourceResolutions[0] / masterResolutions[0] - masterTrackOffset);
//            final float offsetX = (float)(sourceTrackOffset * sourceResolutions[0]);
//            final float offsetY = (float) (sourceStartOffset * sourceResolutions[1] / masterResolutions[1] - masterStartOffset);
//            final float offsetY = (float)(sourceStartOffset * sourceResolutions[1]);

            return copyBand(sourceBand, targetProduct, subSamplingX, subSamplingY, offsetX, offsetY);
        }
    }

    @Override
    protected void initialize(Product[] sourceProducts, Product targetProduct) {
        final MetadataElement globalAttributes = findMasterProduct().getMetadataRoot().getElement("Global_Attributes");
        masterStartOffset = globalAttributes.getAttributeDouble("start_offset");
        masterTrackOffset = globalAttributes.getAttributeDouble("track_offset");
        masterResolutions = (short[]) globalAttributes.getAttribute("resolution").getDataElems();
        if (masterResolutions.length == 1) {
            masterResolutions = new short[]{masterResolutions[0], masterResolutions[0]};
        }
        if (masterResolutions[0] == 0 && masterResolutions[1] == 0) {
            masterResolutions = new short[]{500, 500};
        }
    }

//    @Override
//    protected void setGeoCoding(Product targetProduct) throws IOException {
//        TODO - delete when tie point data in LST are valid
//    }

    @Override
    protected Product findMasterProduct() {
        final List<Product> productList = getOpenProductList();
        Product masterProduct = productList.get(0);
        for (int i = 1; i < productList.size(); i++) {
            Product product = productList.get(i);
            if (product.getSceneRasterWidth() > masterProduct.getSceneRasterWidth() && product.getSceneRasterHeight() > masterProduct.getSceneRasterHeight()) {
                masterProduct = product;
            }
        }
        return masterProduct;
    }

}
