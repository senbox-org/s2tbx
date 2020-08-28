package org.esa.s2tbx.coregistration.operators;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;

/**
 * JAI operator for extracting pixels from 2 rasters as follows: the input consists in 4 raster and the results'pixel value is as follows:
 * <br>- pixel value from raster 1 if the same pixel value of raster 4 is bigger then the pixel value from raster 3.
 * <br>- pixel value from raster 2 if the same pixel value of raster 3 is bigger or equal with the pixel value from raster 4.
 */
public class ComputeCompareOp extends PointOpImage {

    /* Source 1 band increment */
    private int s1bd = 1;

    /* Source 2 band increment */
    private int s2bd = 1;

    /* Source 3 band increment */
    private int s3bd = 1;

    /* Source 4 band increment */
    private int s4bd = 1;

    /* Bilevel data flag. */
    private boolean areBinarySampleModels = false;

    /**
     * Constructs an <code>AddOpImage</code>.
     * <p>
     * <p>The <code>layout</code> parameter may optionally contains the
     * tile grid layout, sample model, and/or color model. The image
     * dimension is determined by the intersection of the bounding boxes
     * of the two source images.
     * <p>
     * <p>The image layout of the first source image, <code>source1</code>,
     * is used as the fall-back for the image layout of the destination
     * image. Any layout parameters not specified in the <code>layout</code>
     * argument are set to the same value as that of <code>source1</code>.
     *
     * @param source1 The first source image.
     * @param source2 The second source image.     *
     * @param comp1   The first compare image.
     * @param comp2   The second compare image.
     * @param layout  The destination image layout.
     */
    public ComputeCompareOp(RenderedImage source1,
                            RenderedImage source2,
                            RenderedImage comp1,
                            RenderedImage comp2,
                            Map config,
                            ImageLayout layout) {
        super(source1, source2, layout, config, true);

        if (ImageUtil.isBinary(getSampleModel()) &&
                ImageUtil.isBinary(source1.getSampleModel()) &&
                ImageUtil.isBinary(source2.getSampleModel())) {
            // Binary processing case: RasterAccessor
            areBinarySampleModels = true;
        } else {
            // Get the source band counts.
            int numBands1 = source1.getSampleModel().getNumBands();
            int numBands2 = source2.getSampleModel().getNumBands();
            int numBands3 = comp1.getSampleModel().getNumBands();
            int numBands4 = comp2.getSampleModel().getNumBands();

            // Handle the special case of adding a single band image to
            // each band of a multi-band image.
            int numBandsDst;
            if (layout != null && layout.isValid(ImageLayout.SAMPLE_MODEL_MASK)) {
                SampleModel sm = layout.getSampleModel(null);
                numBandsDst = sm.getNumBands();

                // One of the sources must be single-banded and the other must
                // have at most the number of bands in the SampleModel hint.
                //TODO what happens when multiple bands....
                if (numBandsDst > 1 &&
                        ((numBands1 == 1 && numBands2 > 1) ||
                                (numBands2 == 1 && numBands1 > 1))) {
                    // Clamp the destination band count to the number of
                    // bands in the multi-band source.
                    numBandsDst = Math.min(Math.max(numBands1, numBands2),
                            numBandsDst);

                    // Create a new SampleModel if necessary.
                    if (numBandsDst != sampleModel.getNumBands()) {
                        sampleModel =
                                RasterFactory.createComponentSampleModel(
                                        sm,
                                        sampleModel.getTransferType(),
                                        sampleModel.getWidth(),
                                        sampleModel.getHeight(),
                                        numBandsDst);

                        if (colorModel != null &&
                                !JDKWorkarounds.areCompatibleDataModels(sampleModel,
                                        colorModel)) {
                            colorModel =
                                    ImageUtil.getCompatibleColorModel(sampleModel,
                                            config);
                        }
                    }

                    // Set the source band increments.
                    s1bd = numBands1 == 1 ? 0 : 1;
                    s2bd = numBands2 == 1 ? 0 : 1;
                    s3bd = numBands3 == 1 ? 0 : 1;
                    s4bd = numBands4 == 1 ? 0 : 1;
                }
            }
        }

        // Set flag to permit in-place operation.
        permitInPlaceOperation();
    }

    /**
     * Adds the pixel values of two source images within a specified
     * rectangle.
     *
     * @param sources  Cobbled sources, guaranteed to provide all the
     *                 source data necessary for computing the rectangle.
     * @param dest     The tile containing the rectangle to be computed.
     * @param destRect The rectangle within the tile to be computed.
     */
    protected void computeRect(Raster[] sources,
                               WritableRaster dest,
                               Rectangle destRect) {
        if (areBinarySampleModels) {
            // Retrieve format tags.
            RasterFormatTag[] formatTags = getFormatTags();

            // For PointOpImage, srcRect = destRect.
            RasterAccessor s1 =
                    new RasterAccessor(sources[0], destRect,
                            formatTags[0],
                            getSourceImage(0).getColorModel());
            RasterAccessor s2 =
                    new RasterAccessor(sources[1], destRect,
                            formatTags[1],
                            getSourceImage(1).getColorModel());
            RasterAccessor s3 =
                    new RasterAccessor(sources[2], destRect,
                            formatTags[2],
                            getSourceImage(2).getColorModel());
            RasterAccessor s4 =
                    new RasterAccessor(sources[3], destRect,
                            formatTags[3],
                            getSourceImage(3).getColorModel());
            RasterAccessor d =
                    new RasterAccessor(dest, destRect,
                            formatTags[4], getColorModel());

            if (d.isBinary()) {
                byte[] src1Bits = s1.getBinaryDataArray();
                byte[] src2Bits = s2.getBinaryDataArray();
                byte[] src3Bits = s3.getBinaryDataArray();
                byte[] src4Bits = s4.getBinaryDataArray();
                byte[] dstBits = d.getBinaryDataArray();

                int length = dstBits.length;
                for (int i = 0; i < length; i++) {
                    if (src3Bits[i] > src4Bits[i]) {
                        dstBits[i] = src2Bits[i];
                    } else {
                        dstBits[i] = src1Bits[i];
                    }
                }

                d.copyBinaryDataToRaster();

                return;
            }
        }

        // Retrieve format tags.
        RasterFormatTag[] formatTags = getFormatTags();

        RasterAccessor s1 = new RasterAccessor(sources[0], destRect,
                formatTags[0],
                getSourceImage(0).getColorModel());
        RasterAccessor s2 = new RasterAccessor(sources[1], destRect,
                formatTags[1],
                getSourceImage(1).getColorModel());
        RasterAccessor s3 = new RasterAccessor(sources[2], destRect,
                formatTags[2],
                getSourceImage(2).getColorModel());
        RasterAccessor s4 = new RasterAccessor(sources[3], destRect,
                formatTags[3],
                getSourceImage(3).getColorModel());
        RasterAccessor d = new RasterAccessor(dest, destRect,
                formatTags[4], getColorModel());

        switch (d.getDataType()) {
            case DataBuffer.TYPE_BYTE:
                computeRectByte(s1, s2, s3, s4, d);
                break;
            case DataBuffer.TYPE_USHORT:
                computeRectUShort(s1, s2, s3, s4, d);
                break;
            case DataBuffer.TYPE_SHORT:
                computeRectShort(s1, s2, s3, s4, d);
                break;
            case DataBuffer.TYPE_INT:
                computeRectInt(s1, s2, s3, s4, d);
                break;
            case DataBuffer.TYPE_FLOAT:
                computeRectFloat(s1, s2, s3, s4, d);
                break;
            case DataBuffer.TYPE_DOUBLE:
                computeRectDouble(s1, s2, s3, s4, d);
                break;
        }

        if (d.needsClamping()) {
            d.clampDataArrays();
        }
        d.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor src1,
                                 RasterAccessor src2,
                                 RasterAccessor src3,
                                 RasterAccessor src4,
                                 RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        byte[][] s1Data = src1.getByteDataArrays();

        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        byte[][] s2Data = src2.getByteDataArrays();

        int s3LineStride = src3.getScanlineStride();
        int s3PixelStride = src3.getPixelStride();
        int[] s3BandOffsets = src3.getBandOffsets();
        byte[][] s3Data = src3.getByteDataArrays();

        int s4LineStride = src4.getScanlineStride();
        int s4PixelStride = src4.getPixelStride();
        int[] s4BandOffsets = src4.getBandOffsets();
        byte[][] s4Data = src4.getByteDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        byte[][] dData = dst.getByteDataArrays();

        for (int b = 0, s1b = 0, s2b = 0, s3b = 0, s4b = 0; b < bands;
             b++, s1b += s1bd, s2b += s2bd, s3b += s3bd, s4b += s4bd) {
            byte[] s1 = s1Data[s1b];
            byte[] s2 = s2Data[s2b];
            byte[] s3 = s2Data[s3b];
            byte[] s4 = s2Data[s4b];
            byte[] d = dData[b];

            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int s3LineOffset = s3BandOffsets[s3b];
            int s4LineOffset = s4BandOffsets[s4b];
            int dLineOffset = dBandOffsets[b];

            for (int h = 0; h < dheight; h++) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int s3PixelOffset = s3LineOffset;
                int s4PixelOffset = s4LineOffset;
                int dPixelOffset = dLineOffset;

                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                s3LineOffset += s3LineStride;
                s4LineOffset += s4LineStride;
                dLineOffset += dLineStride;

                for (int w = 0; w < dwidth; w++) {
                    if (s3[s3PixelOffset] > s4[s4PixelOffset]) {
                        d[dPixelOffset] = s2[s2PixelOffset];
                    } else {
                        d[dPixelOffset] = s1[s1PixelOffset];
                    }

                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    s3PixelOffset += s3PixelStride;
                    s4PixelOffset += s4PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor src1,
                                   RasterAccessor src2,
                                   RasterAccessor src3,
                                   RasterAccessor src4,
                                   RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        short[][] s1Data = src1.getShortDataArrays();

        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        short[][] s2Data = src2.getShortDataArrays();

        int s3LineStride = src3.getScanlineStride();
        int s3PixelStride = src3.getPixelStride();
        int[] s3BandOffsets = src3.getBandOffsets();
        short[][] s3Data = src3.getShortDataArrays();

        int s4LineStride = src4.getScanlineStride();
        int s4PixelStride = src4.getPixelStride();
        int[] s4BandOffsets = src4.getBandOffsets();
        short[][] s4Data = src4.getShortDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();

        for (int b = 0, s1b = 0, s2b = 0, s3b = 0, s4b = 0; b < bands;
             b++, s1b += s1bd, s2b += s2bd, s3b += s3bd, s4b += s4bd) {
            short[] s1 = s1Data[s1b];
            short[] s2 = s2Data[s2b];
            short[] s3 = s3Data[s3b];
            short[] s4 = s4Data[s4b];
            short[] d = dData[b];

            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int s3LineOffset = s3BandOffsets[s3b];
            int s4LineOffset = s4BandOffsets[s4b];
            int dLineOffset = dBandOffsets[b];

            for (int h = 0; h < dheight; h++) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int s3PixelOffset = s3LineOffset;
                int s4PixelOffset = s4LineOffset;
                int dPixelOffset = dLineOffset;

                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                s3LineOffset += s3LineStride;
                s4LineOffset += s4LineStride;
                dLineOffset += dLineStride;

                for (int w = 0; w < dwidth; w++) {
                    if (s3[s3PixelOffset] > s4[s4PixelOffset]) {
                        d[dPixelOffset] = s2[s2PixelOffset];
                    } else {
                        d[dPixelOffset] = s1[s1PixelOffset];
                    }

                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    s3PixelOffset += s3PixelStride;
                    s4PixelOffset += s4PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor src1,
                                  RasterAccessor src2,
                                  RasterAccessor src3,
                                  RasterAccessor src4,
                                  RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        short[][] s1Data = src1.getShortDataArrays();

        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        short[][] s2Data = src2.getShortDataArrays();

        int s3LineStride = src3.getScanlineStride();
        int s3PixelStride = src3.getPixelStride();
        int[] s3BandOffsets = src3.getBandOffsets();
        short[][] s3Data = src3.getShortDataArrays();

        int s4LineStride = src4.getScanlineStride();
        int s4PixelStride = src4.getPixelStride();
        int[] s4BandOffsets = src4.getBandOffsets();
        short[][] s4Data = src4.getShortDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();

        for (int b = 0, s1b = 0, s2b = 0, s3b = 0, s4b = 0; b < bands;
             b++, s1b += s1bd, s2b += s2bd, s3b += s3bd, s4b += s4bd) {
            short[] s1 = s1Data[s1b];
            short[] s2 = s2Data[s2b];
            short[] s3 = s3Data[s3b];
            short[] s4 = s4Data[s4b];
            short[] d = dData[b];

            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int s3LineOffset = s3BandOffsets[s3b];
            int s4LineOffset = s4BandOffsets[s4b];
            int dLineOffset = dBandOffsets[b];

            for (int h = 0; h < dheight; h++) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int s3PixelOffset = s3LineOffset;
                int s4PixelOffset = s4LineOffset;
                int dPixelOffset = dLineOffset;

                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                s3LineOffset += s3LineStride;
                s4LineOffset += s4LineStride;
                dLineOffset += dLineStride;

                for (int w = 0; w < dwidth; w++) {
                    if (s3[s3PixelOffset] > s4[s4PixelOffset]) {
                        d[dPixelOffset] = s2[s2PixelOffset];
                    } else {
                        d[dPixelOffset] = s1[s1PixelOffset];
                    }

                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    s3PixelOffset += s3PixelStride;
                    s4PixelOffset += s4PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    //TODO extend
    private void computeRectInt(RasterAccessor src1,
                                RasterAccessor src2,
                                RasterAccessor src3,
                                RasterAccessor src4,
                                RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        int[][] s1Data = src1.getIntDataArrays();

        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        int[][] s2Data = src2.getIntDataArrays();

        int s3LineStride = src3.getScanlineStride();
        int s3PixelStride = src3.getPixelStride();
        int[] s3BandOffsets = src3.getBandOffsets();
        int[][] s3Data = src3.getIntDataArrays();

        int s4LineStride = src4.getScanlineStride();
        int s4PixelStride = src4.getPixelStride();
        int[] s4BandOffsets = src4.getBandOffsets();
        int[][] s4Data = src4.getIntDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        int[][] dData = dst.getIntDataArrays();

        /*
         * The destination data type may be any of the integral data types.
         * The "clamp" function must clamp to the appropriate range for
         * that data type.
         */
        switch (sampleModel.getTransferType()) {
            case DataBuffer.TYPE_BYTE:
                for (int b = 0, s1b = 0, s2b = 0, s3b = 0, s4b = 0; b < bands;
                     b++, s1b += s1bd, s2b += s2bd, s3b += s3bd, s4b += s4bd) {
                    int[] s1 = s1Data[s1b];
                    int[] s2 = s2Data[s2b];
                    int[] s3 = s3Data[s3b];
                    int[] s4 = s4Data[s4b];
                    int[] d = dData[b];

                    int s1LineOffset = s1BandOffsets[s1b];
                    int s2LineOffset = s2BandOffsets[s2b];
                    int s3LineOffset = s3BandOffsets[s3b];
                    int s4LineOffset = s4BandOffsets[s4b];
                    int dLineOffset = dBandOffsets[b];

                    for (int h = 0; h < dheight; h++) {
                        int s1PixelOffset = s1LineOffset;
                        int s2PixelOffset = s2LineOffset;
                        int s3PixelOffset = s3LineOffset;
                        int s4PixelOffset = s4LineOffset;
                        int dPixelOffset = dLineOffset;

                        s1LineOffset += s1LineStride;
                        s2LineOffset += s2LineStride;
                        s3LineOffset += s3LineStride;
                        s4LineOffset += s4LineStride;
                        dLineOffset += dLineStride;

                        int sum = 0;
                        for (int w = 0; w < dwidth; w++) {
                            //
                            if (s3[s3PixelOffset] > s4[s4PixelOffset]) {
                                d[dPixelOffset] = s2[s2PixelOffset];
                            } else {
                                d[dPixelOffset] = s1[s1PixelOffset];
                            }

                            s1PixelOffset += s1PixelStride;
                            s2PixelOffset += s2PixelStride;
                            s3PixelOffset += s3PixelStride;
                            s4PixelOffset += s4PixelStride;
                            dPixelOffset += dPixelStride;
                        }
                    }
                }
                break;

            case DataBuffer.TYPE_USHORT:
                for (int b = 0, s1b = 0, s2b = 0, s3b = 0, s4b = 0; b < bands;
                     b++, s1b += s1bd, s2b += s2bd, s3b += s3bd, s4b += s4bd) {
                    int[] s1 = s1Data[s1b];
                    int[] s2 = s2Data[s2b];
                    int[] s3 = s3Data[s3b];
                    int[] s4 = s4Data[s4b];
                    int[] d = dData[b];

                    int s1LineOffset = s1BandOffsets[s1b];
                    int s2LineOffset = s2BandOffsets[s2b];
                    int s3LineOffset = s3BandOffsets[s3b];
                    int s4LineOffset = s4BandOffsets[s4b];
                    int dLineOffset = dBandOffsets[b];

                    for (int h = 0; h < dheight; h++) {
                        int s1PixelOffset = s1LineOffset;
                        int s2PixelOffset = s2LineOffset;
                        int s3PixelOffset = s3LineOffset;
                        int s4PixelOffset = s4LineOffset;
                        int dPixelOffset = dLineOffset;

                        s1LineOffset += s1LineStride;
                        s2LineOffset += s2LineStride;
                        s3LineOffset += s3LineStride;
                        s4LineOffset += s4LineStride;
                        dLineOffset += dLineStride;

                        for (int w = 0; w < dwidth; w++) {
                            if (s3[s3PixelOffset] > s4[s4PixelOffset]) {
                                d[dPixelOffset] = s2[s2PixelOffset];
                            } else {
                                d[dPixelOffset] = s1[s1PixelOffset];
                            }

                            s1PixelOffset += s1PixelStride;
                            s2PixelOffset += s2PixelStride;
                            s3PixelOffset += s3PixelStride;
                            s4PixelOffset += s4PixelStride;
                            dPixelOffset += dPixelStride;
                        }
                    }
                }
                break;

            case DataBuffer.TYPE_SHORT:
                for (int b = 0, s1b = 0, s2b = 0, s3b = 0, s4b = 0; b < bands;
                     b++, s1b += s1bd, s2b += s2bd, s3b += s3bd, s4b += s4bd) {
                    int[] s1 = s1Data[s1b];
                    int[] s2 = s2Data[s2b];
                    int[] s3 = s3Data[s3b];
                    int[] s4 = s4Data[s4b];
                    int[] d = dData[b];

                    int s1LineOffset = s1BandOffsets[s1b];
                    int s2LineOffset = s2BandOffsets[s2b];
                    int s3LineOffset = s3BandOffsets[s3b];
                    int s4LineOffset = s4BandOffsets[s4b];
                    int dLineOffset = dBandOffsets[b];

                    for (int h = 0; h < dheight; h++) {
                        int s1PixelOffset = s1LineOffset;
                        int s2PixelOffset = s2LineOffset;
                        int s3PixelOffset = s3LineOffset;
                        int s4PixelOffset = s4LineOffset;
                        int dPixelOffset = dLineOffset;

                        s1LineOffset += s1LineStride;
                        s2LineOffset += s2LineStride;
                        s3LineOffset += s3LineStride;
                        s4LineOffset += s4LineStride;
                        dLineOffset += dLineStride;

                        for (int w = 0; w < dwidth; w++) {
                            if (s3[s3PixelOffset] > s4[s4PixelOffset]) {
                                d[dPixelOffset] = s2[s2PixelOffset];
                            } else {
                                d[dPixelOffset] = s1[s1PixelOffset];
                            }

                            s1PixelOffset += s1PixelStride;
                            s2PixelOffset += s2PixelStride;
                            s3PixelOffset += s3PixelStride;
                            s4PixelOffset += s4PixelStride;
                            dPixelOffset += dPixelStride;
                        }
                    }
                }
                break;

            case DataBuffer.TYPE_INT:
                for (int b = 0, s1b = 0, s2b = 0, s3b = 0, s4b = 0; b < bands;
                     b++, s1b += s1bd, s2b += s2bd, s3b += s3bd, s4b += s4bd) {
                    int[] s1 = s1Data[s1b];
                    int[] s2 = s2Data[s2b];
                    int[] s3 = s3Data[s3b];
                    int[] s4 = s4Data[s4b];
                    int[] d = dData[b];

                    int s1LineOffset = s1BandOffsets[s1b];
                    int s2LineOffset = s2BandOffsets[s2b];
                    int s3LineOffset = s3BandOffsets[s3b];
                    int s4LineOffset = s4BandOffsets[s4b];
                    int dLineOffset = dBandOffsets[b];

                    for (int h = 0; h < dheight; h++) {
                        int s1PixelOffset = s1LineOffset;
                        int s2PixelOffset = s2LineOffset;
                        int s3PixelOffset = s3LineOffset;
                        int s4PixelOffset = s4LineOffset;
                        int dPixelOffset = dLineOffset;

                        s1LineOffset += s1LineStride;
                        s2LineOffset += s2LineStride;
                        s3LineOffset += s3LineStride;
                        s4LineOffset += s4LineStride;
                        dLineOffset += dLineStride;

                        for (int w = 0; w < dwidth; w++) {
                            if (s3[s3PixelOffset] > s4[s4PixelOffset]) {
                                d[dPixelOffset] = s2[s2PixelOffset];
                            } else {
                                d[dPixelOffset] = s1[s1PixelOffset];
                            }

                            s1PixelOffset += s1PixelStride;
                            s2PixelOffset += s2PixelStride;
                            s3PixelOffset += s3PixelStride;
                            s4PixelOffset += s4PixelStride;
                            dPixelOffset += dPixelStride;
                        }
                    }
                }
                break;
        }
    }

    //TODO extend
    private void computeRectFloat(RasterAccessor src1,
                                  RasterAccessor src2,
                                  RasterAccessor src3,
                                  RasterAccessor src4,
                                  RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        float[][] s1Data = src1.getFloatDataArrays();

        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        float[][] s2Data = src2.getFloatDataArrays();

        int s3LineStride = src3.getScanlineStride();
        int s3PixelStride = src3.getPixelStride();
        int[] s3BandOffsets = src3.getBandOffsets();
        float[][] s3Data = src3.getFloatDataArrays();

        int s4LineStride = src4.getScanlineStride();
        int s4PixelStride = src4.getPixelStride();
        int[] s4BandOffsets = src4.getBandOffsets();
        float[][] s4Data = src4.getFloatDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        float[][] dData = dst.getFloatDataArrays();


        for (int b = 0, s1b = 0, s2b = 0, s3b = 0, s4b = 0; b < bands;
             b++, s1b += s1bd, s2b += s2bd, s3b += s3bd, s4b += s4bd) {
            float[] s1 = s1Data[s1b];
            float[] s2 = s2Data[s2b];
            float[] s3 = s3Data[s3b];
            float[] s4 = s4Data[s4b];
            float[] d = dData[b];

            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int s3LineOffset = s3BandOffsets[s3b];
            int s4LineOffset = s4BandOffsets[s4b];
            int dLineOffset = dBandOffsets[b];

            for (int h = 0; h < dheight; h++) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int s3PixelOffset = s3LineOffset;
                int s4PixelOffset = s4LineOffset;
                int dPixelOffset = dLineOffset;

                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                s3LineOffset += s3LineStride;
                s4LineOffset += s4LineStride;
                dLineOffset += dLineStride;

                for (int w = 0; w < dwidth; w++) {
                    if (s3[s3PixelOffset] > s4[s4PixelOffset]) {
                        d[dPixelOffset] = s2[s2PixelOffset];
                    } else {
                        d[dPixelOffset] = s1[s1PixelOffset];
                    }

                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    s3PixelOffset += s3PixelStride;
                    s4PixelOffset += s4PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

    //TODO extend
    private void computeRectDouble(RasterAccessor src1,
                                   RasterAccessor src2,
                                   RasterAccessor src3,
                                   RasterAccessor src4,
                                   RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        double[][] s1Data = src1.getDoubleDataArrays();

        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        double[][] s2Data = src2.getDoubleDataArrays();

        int s3LineStride = src3.getScanlineStride();
        int s3PixelStride = src3.getPixelStride();
        int[] s3BandOffsets = src3.getBandOffsets();
        double[][] s3Data = src3.getDoubleDataArrays();

        int s4LineStride = src4.getScanlineStride();
        int s4PixelStride = src4.getPixelStride();
        int[] s4BandOffsets = src4.getBandOffsets();
        double[][] s4Data = src4.getDoubleDataArrays();

        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        double[][] dData = dst.getDoubleDataArrays();

        for (int b = 0, s1b = 0, s2b = 0, s3b = 0, s4b = 0; b < bands;
             b++, s1b += s1bd, s2b += s2bd, s3b += s3bd, s4b += s4bd) {
            double[] s1 = s1Data[s1b];
            double[] s2 = s2Data[s2b];
            double[] s3 = s3Data[s3b];
            double[] s4 = s4Data[s4b];
            double[] d = dData[b];

            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int s3LineOffset = s3BandOffsets[s3b];
            int s4LineOffset = s4BandOffsets[s4b];
            int dLineOffset = dBandOffsets[b];

            for (int h = 0; h < dheight; h++) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int s3PixelOffset = s3LineOffset;
                int s4PixelOffset = s4LineOffset;
                int dPixelOffset = dLineOffset;

                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                s3LineOffset += s3LineStride;
                s4LineOffset += s4LineStride;
                dLineOffset += dLineStride;

                for (int w = 0; w < dwidth; w++) {
                    if (s3[s3PixelOffset] > s4[s4PixelOffset]) {
                        d[dPixelOffset] = s2[s2PixelOffset];
                    } else {
                        d[dPixelOffset] = s1[s1PixelOffset];
                    }

                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    s3PixelOffset += s3PixelStride;
                    s4PixelOffset += s4PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
        }
    }

}
