package org.esa.s2tbx.coregistration.operators;

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

/**
 * Created by ramonag on 6/13/2017.
 */
public class ContrastOperator extends PointOpImage {
    private int param1;
    private int param2;

    /** Constructs an SampleOpImage. The image dimensions are copied
     * from the source image. The tile grid layout, SampleModel, and
     * ColorModel may optionally be specified by an ImageLayout
     * object.
     *
     * @param source a RenderedImage.
     * @param layout an ImageLayout optionally containing the tile
     * grid layout, SampleModel, and ColorModel, or
     * null.
     */
    public ContrastOperator(RenderedImage source,
                            ImageLayout layout,
                            Integer param1,
                            Integer param2) {
        super(source, null, layout, null, true);
        this.param1 = param1.intValue();
        this.param2 = param2.intValue();
    }

    /**
     * Performs a modified threshold operation on the pixels in a
     * given rectangle. Sample values below a lower limit are clamped
     * to 0, while those above an upper limit are clamped to 255. The
     * results are returned in the input WritableRaster dest. The
     * sources are cobbled.
     *
     * @param sources an array of sources, guarantee to provide all
     * necessary source data for computing the rectangle.
     * @param dest a tile that contains the rectangle to be computed.
     * @param destRect the rectangle within this OpImage to be
     * processed.
     */
    protected void computeRect(Raster[] sources,
                               WritableRaster dest,
                               Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = mapDestRect(destRect, 0);
// RasterAccessor is a convienient way to represent any given
// Raster in a usable format. It has very little overhead if
// the underlying Raster is in a common format (PixelSequential
// for this release) and allows generic code to process
// a Raster with an exotic format. Essentially, it allows the
// common case to processed quickly and the rare case to be
// processed easily.
// This "best case" formatTag is used to create a pair of
// RasterAccessors for processing the source and dest rasters
        RasterFormatTag[] formatTags = getFormatTags();
        RasterAccessor srcAccessor =
                new RasterAccessor(sources[0], srcRect, formatTags[0],
                        getSource(0).getColorModel());
        RasterAccessor dstAccessor =
                new RasterAccessor(dest, destRect, formatTags[1],
                        getColorModel());

// Depending on the base dataType of the RasterAccessors,
// either the byteLoop or intLoop method is called. The two
// functions are virtually the same, except for the data type
// of the underlying arrays.
        switch (dstAccessor.getDataType()) {
            case DataBuffer.TYPE_BYTE:
                byteLoop(srcAccessor,dstAccessor);
                break;
            case DataBuffer.TYPE_INT:
                intLoop(srcAccessor,dstAccessor);
                break;
            default:
                String className = this.getClass().getName();
                throw new RuntimeException(className +
                        " does not implement computeRect" +
                        " for short/float/double data");
        }
// If the RasterAccessor object set up a temporary buffer for the
// op to write to, tell the RasterAccessor to write that data
// to the raster now that we’re done with it.
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }
    /**
     * Computes an area of a given byte-based destination Raster using
     * a souce RasterAccessor and a destination RasterAccesor.
     * Processing is done as if the bytes are unsigned, even though
     * the Java language has support only for signed bytes as a
     * primitive datatype.
     */
    private void byteLoop(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        byte dstDataArrays[][] = dst.getByteDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();

        byte srcDataArrays[][] = src.getByteDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        byte bp1 = (byte)(param1 & 0xff);
        byte bp2 = (byte)(param2 & 0xff);
// A standard imaging loop
        for (int k = 0; k < dnumBands; k++) {
            byte dstData[] = dstDataArrays[k];
            byte srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; i++) {
                    int pixel = srcData[srcPixelOffset] & 0xff;
                    dstData[dstPixelOffset] = (byte)((pixel-bp1)/(bp2-bp1));

                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
    /**
     * Computes an area of a given int-based destination Raster using
     * a source RasterAccessor and a destination RasterAccesor.
     */
    private void intLoop(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        int dstDataArrays[][] = dst.getIntDataArrays();
        int dstBandOffsets[] = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int srcDataArrays[][] = src.getIntDataArrays();
        int srcBandOffsets[] = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();

        for (int k = 0; k < dnumBands; k++) {
            int dstData[] = dstDataArrays[k];
            int srcData[] = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; j++) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; i++) {
                    int pixel = srcData[srcPixelOffset];
                    dstData[dstPixelOffset] = (pixel-param1)/(param2-param1);
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}
