package org.esa.beam.dataio.s3.slstr;

import com.bc.ceres.glevel.MultiLevelImage;
import org.esa.beam.dataio.s3.manifest.ManifestProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
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

abstract class SlstrLevel2ProductReader extends ManifestProductReader {

    private double nadStartOffset;
    private double nadTrackOffset;
    private short[] nadResolutions;

    protected SlstrLevel2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected RasterDataNode addSpecialNode(Band sourceBand, Product targetProduct) {
        final Product sourceProduct = sourceBand.getProduct();
        final MetadataElement globalAttributes = sourceProduct.getMetadataRoot().getElement("Global_Attributes");
        final double sourceStartOffset = globalAttributes.getAttributeDouble("start_offset");
        final double sourceTrackOffset = globalAttributes.getAttributeDouble("track_offset");
        final short[] sourceResolutions = (short[]) globalAttributes.getAttribute("resolution").getDataElems();

        if (sourceResolutions[0] == nadResolutions[0]) { // oblique-view band
            final Band targetBand = copyBand(sourceBand, targetProduct, false);
            final ImageLayout imageLayout = ImageManager.createSingleBandedImageLayout(targetBand);
            final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);

            final MultiLevelImage sourceImage = sourceBand.getSourceImage();
            final int targetW = targetBand.getRasterWidth();
            final int targetH = targetBand.getRasterHeight();
            final float offsetX = (float) (sourceTrackOffset - nadTrackOffset);
            final float offsetY = (float) (sourceStartOffset - nadStartOffset);
            final int padX = Math.round(Math.abs(offsetX));
            final int padY = Math.round(Math.abs(offsetY));
            final BorderExtender borderExtender = new BorderExtenderConstant(new double[]{targetBand.getNoDataValue()});
            final RenderedImage extendedImage = BorderDescriptor.create(sourceImage,
                                                                        padX,
                                                                        targetW - padX - sourceImage.getWidth(),
                                                                        padY,
                                                                        padY,
                                                                        borderExtender, renderingHints);

            final RenderedImage translatedImage = TranslateDescriptor.create(extendedImage,
                                                                             offsetX,
                                                                             offsetY,
                                                                             null,
                                                                             renderingHints);
            final RenderedImage croppedImage = CropDescriptor.create(translatedImage, 0.0f, 0.0f,
                                                                     (float) targetW,
                                                                     (float) targetH,
                                                                     renderingHints);
            targetBand.setSourceImage(croppedImage);
            return targetBand;
        } else { // tie-point data
            final int subSamplingX = sourceResolutions[0] / nadResolutions[0];
            final int subSamplingY;
            if (sourceResolutions.length == 2) {
                subSamplingY = sourceResolutions[1] / nadResolutions[1];
            } else {
                //noinspection SuspiciousNameCombination
                subSamplingY = subSamplingX;
            }
            final float offsetX = (float) (nadTrackOffset - sourceTrackOffset * subSamplingX);
            final float offsetY = (float) (sourceStartOffset * subSamplingY - nadStartOffset);

            return copyBand(sourceBand, targetProduct, subSamplingX, subSamplingY, offsetX, offsetY);
        }
    }

    @Override
    protected final void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        targetNode.setName(sourceBand.getProduct().getName() + "_" + sourceBand.getName());
    }

    @Override
    protected final void initialize(Product[] sourceProducts, Product targetProduct) {
        final MetadataElement globalAttributes = sourceProducts[0].getMetadataRoot().getElement("Global_Attributes");
        nadStartOffset = globalAttributes.getAttributeDouble("start_offset");
        nadTrackOffset = globalAttributes.getAttributeDouble("track_offset");
        nadResolutions = (short[]) globalAttributes.getAttribute("resolution").getDataElems();
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
            if (sourceProduct.getAutoGrouping() != null) {
                for (final String[] groups : sourceProduct.getAutoGrouping()) {
                    if (patternBuilder.length() > 0) {
                        patternBuilder.append(":");
                    }
                    patternBuilder.append(sourceProduct.getName());
                    for (final String group : groups) {
                        patternBuilder.append("/");
                        patternBuilder.append(group);
                    }
                }
            }
            if (patternBuilder.length() > 0) {
                patternBuilder.append(":");
            }
            patternBuilder.append(sourceProduct.getName());
        }
        targetProduct.setAutoGrouping(patternBuilder.toString());
    }
}
