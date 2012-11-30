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
import org.esa.beam.dataio.s3.AbstractProductFactory;
import org.esa.beam.dataio.s3.Sentinel3ProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.datamodel.TiePointGeoCoding;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.jai.ImageManager;

import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.io.IOException;

public abstract class SlstrProductFactory extends AbstractProductFactory {

    protected double referenceStartOffset;
    protected double referenceTrackOffset;
    protected short[] referenceResolutions;

    protected SlstrProductFactory(Sentinel3ProductReader productReader) {
        super(productReader);
    }

    @Override
    protected RasterDataNode addSpecialNode(Band sourceBand, Product targetProduct) {
        final Product sourceProduct = sourceBand.getProduct();
        final MetadataElement globalAttributes = sourceProduct.getMetadataRoot().getElement("Global_Attributes");
        final double sourceStartOffset = getStartOffset(globalAttributes, sourceProduct.getName());
        final double sourceTrackOffset = getTrackOffset(globalAttributes, sourceProduct.getName());
        final short[] sourceResolutions = getResolutions(globalAttributes);
        if (isTiePointGrid(sourceResolutions)) {
            return copyTiePointGrid(sourceBand, targetProduct, sourceStartOffset, sourceTrackOffset, sourceResolutions);
        } else {
            final Band targetBand = copyBand(sourceBand, targetProduct, false);
            final float[] offsets = getOffsets(sourceStartOffset, sourceTrackOffset, sourceResolutions);
            final RenderedImage sourceImage = createSourceImage(sourceBand, offsets,
                                                                targetBand, sourceResolutions);
            targetBand.setSourceImage(sourceImage);
            return targetBand;
        }
    }

    protected double getTrackOffset(MetadataElement globalAttributes, String sourceProductName) {
        return globalAttributes.getAttributeDouble("track_offset");
    }

    protected double getStartOffset(MetadataElement globalAttributes, String sourceProductName) {
        return globalAttributes.getAttributeDouble("start_offset");
    }

    protected boolean isTiePointGrid(short[] sourceResolutions) {
        return sourceResolutions[0] != referenceResolutions[0];
    }

    protected short[] getResolutions(MetadataElement globalAttributes) {
        return (short[]) globalAttributes.getAttribute("resolution").getDataElems();
    }

    private RenderedImage createSourceImage(Band sourceBand, float[] offsets,
                                            Band targetBand, short[] sourceResolutions) {
        final ImageLayout imageLayout = ImageManager.createSingleBandedImageLayout(targetBand);
        final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);

        final MultiLevelImage sourceImage = sourceBand.getSourceImage();
        final int targetW = targetBand.getRasterWidth();
        final int targetH = targetBand.getRasterHeight();
        final int padX = Math.round(Math.abs(offsets[0]));
        final int padY = Math.round(Math.abs(offsets[1]));

        RenderedImage image = modifySourceImage(sourceResolutions, renderingHints, sourceImage);

        final BorderExtender borderExtender = new BorderExtenderConstant(new double[]{targetBand.getNoDataValue()});
        image = BorderDescriptor.create(image, padX, targetW - padX - image.getWidth(),
                                        padY, padY, borderExtender, renderingHints);
        if (offsets[0] != 0.0f || offsets[1] != 0.0f) {
            image = TranslateDescriptor.create(image, offsets[0], offsets[1], null, renderingHints);
        }
        return CropDescriptor.create(image, 0.0f, 0.0f, (float) targetW, (float) targetH, renderingHints);
    }

    protected float[] getOffsets(double sourceStartOffset, double sourceTrackOffset, short[] sourceResolutions) {
        float offsetX = (float) (sourceTrackOffset - referenceTrackOffset);
        float offsetY = (float) (sourceStartOffset - referenceStartOffset);
        return new float[]{offsetX, offsetY};
    }

    private RasterDataNode copyTiePointGrid(Band sourceBand, Product targetProduct, double sourceStartOffset,
                                            double sourceTrackOffset, short[] sourceResolutions) {
        final int subSamplingX = sourceResolutions[0] / referenceResolutions[0];
        final int subSamplingY;
        if (sourceResolutions.length == 2) {
            subSamplingY = sourceResolutions[1] / referenceResolutions[1];
        } else {
            //noinspection SuspiciousNameCombination
            subSamplingY = subSamplingX;
        }
        float[] tiePointGridOffsets = getTiePointGridOffsets(sourceStartOffset, sourceTrackOffset,
                                                             subSamplingX, subSamplingY, sourceResolutions);
        return copyBandAsTiePointGrid(sourceBand, targetProduct, subSamplingX, subSamplingY,
                                      tiePointGridOffsets[0], tiePointGridOffsets[1]);
    }

    protected float[] getTiePointGridOffsets(double sourceStartOffset, double sourceTrackOffset,
                                             int subSamplingX, int subSamplingY, short[] sourceResolutions) {
        float[] tiePointGridOffsets = new float[2];
        tiePointGridOffsets[0] = (float) (referenceTrackOffset - sourceTrackOffset * subSamplingX);
        tiePointGridOffsets[1] = (float) (sourceStartOffset * subSamplingY - referenceStartOffset);
        return tiePointGridOffsets;
    }

    @Override
    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        final String sourceBandName = sourceBand.getName();
        final String sourceProductName = sourceBand.getProduct().getName();
        if (sourceProductName.contains(sourceBandName)) {
            targetNode.setName(sourceProductName);
        } else {
            targetNode.setName(sourceProductName + "_" + sourceBandName);
        }
    }

    @Override
    protected void initialize(Product[] sourceProducts, Product targetProduct) {
        final Product masterProduct = findMasterProduct();
        final MetadataElement globalAttributes = masterProduct.getMetadataRoot().getElement("Global_Attributes");
        referenceStartOffset = getStartOffset(globalAttributes, masterProduct.getName());
        referenceTrackOffset = getTrackOffset(globalAttributes, masterProduct.getName());
        referenceResolutions = getResolutions(globalAttributes);
    }

    @Override
    protected void setGeoCoding(Product targetProduct) throws IOException {
        TiePointGrid latGrid = null;
        TiePointGrid lonGrid = null;
        for (final TiePointGrid grid : targetProduct.getTiePointGrids()) {
            if (latGrid == null && grid.getName().endsWith("latitude")) {
                latGrid = grid;
            }
            if (lonGrid == null && grid.getName().endsWith("longitude")) {
                lonGrid = grid;
            }
        }
        if (latGrid != null && lonGrid != null) {
            targetProduct.setGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
        }
    }

    @Override
    protected final void setAutoGrouping(Product[] sourceProducts, Product targetProduct) {
        final StringBuilder patternBuilder = new StringBuilder();
        for (final Product sourceProduct : sourceProducts) {
            final String sourceProductName = sourceProduct.getName();
            if (sourceProduct.getAutoGrouping() != null) {
                for (final String[] groups : sourceProduct.getAutoGrouping()) {
                    if (patternBuilder.length() > 0) {
                        patternBuilder.append(":");
                    }
                    patternBuilder.append(sourceProductName);
                    for (final String group : groups) {
                        patternBuilder.append("/");
                        patternBuilder.append(group);
                    }
                }
            }
            if (patternBuilder.length() > 0) {
                patternBuilder.append(":");
            }
            String patternName = sourceProductName;
            String[] unwantedPatternContents = new String[]{
                    "_an", "_ao", "_bn", "_bo", "_cn", "_co", "_in", "_io",
                    "_tn", "_to", "_tx"
            };
            for (String unwantedPatternContent : unwantedPatternContents) {
                if (sourceProductName.contains(unwantedPatternContent)) {
                    patternName = sourceProductName.substring(0, sourceProductName.lastIndexOf(unwantedPatternContent));
                    break;
                }
            }
            patternBuilder.append(patternName);
        }
        targetProduct.setAutoGrouping(patternBuilder.toString());
    }

    protected RenderedImage modifySourceImage(short[] sourceResolutions, RenderingHints renderingHints,
                                              MultiLevelImage sourceImage) {
        return sourceImage;
    }
}
