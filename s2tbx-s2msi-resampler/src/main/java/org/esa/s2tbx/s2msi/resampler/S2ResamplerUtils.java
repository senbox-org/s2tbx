package org.esa.s2tbx.s2msi.resampler;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.MultiLevelModel;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.esa.s2tbx.dataio.s2.S2BandAnglesGrid;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.gpf.common.resample.Resample;

import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.image.DataBuffer.TYPE_FLOAT;


/**
 * Created by obarrile on 17/05/2017.
 */
public class S2ResamplerUtils {

    //Copied from ResamplingOp
    public static RenderedImage adjustImageToModelTransform(final MultiLevelImage image, MultiLevelModel model) {
        MultiLevelModel actualModel = model;
        if (model.getLevelCount() > image.getModel().getLevelCount()) {
            actualModel = new DefaultMultiLevelModel(image.getModel().getLevelCount(), model.getImageToModelTransform(0),
                                                     image.getWidth(), image.getHeight());
        }
        final AbstractMultiLevelSource source = new AbstractMultiLevelSource(actualModel) {
            @Override
            protected RenderedImage createImage(int level) {
                return image.getImage(level);
            }
        };
        return new DefaultMultiLevelImage(source);
    }

    public static MultiLevelImage createInterpolatedImage(MultiLevelImage sourceImage, double noDataValue,
                                                    AffineTransform sourceImageToModelTransform,
                                                           int referenceWidth,
                                                           int referenceHeight,
                                                           Dimension tileSize,
                                                           MultiLevelModel referenceMultiLevelModel,
                                                           Interpolation interpolation) {

        return Resample.createInterpolatedMultiLevelImage(sourceImage, noDataValue, sourceImageToModelTransform,
                                                          referenceWidth, referenceHeight, tileSize,
                                                          referenceMultiLevelModel, interpolation);
    }


    private static int getInterpolationType(String interpolationString) {
        final int interpolationType;
        if ("Nearest".equalsIgnoreCase(interpolationString)) {
            interpolationType = Interpolation.INTERP_NEAREST;
        } else if ("Bilinear".equalsIgnoreCase(interpolationString)) {
            interpolationType = Interpolation.INTERP_BILINEAR;
        } else if ("Bicubic".equalsIgnoreCase(interpolationString)) {
            interpolationType = Interpolation.INTERP_BICUBIC;
        } else {
            interpolationType = -1;
        }
        return interpolationType;
    }

    public static Interpolation getInterpolation(String interpolationString) {
        int interpolation = getInterpolationType(interpolationString);
        return Interpolation.getInstance(interpolation);
    }


    public PlanarImage createExtendedS2BandAnglesGrid(S2BandAnglesGrid bandAnglesGrid) {
        float[] extendedData = extendDataV2(bandAnglesGrid.getData(),bandAnglesGrid.getWidth(),bandAnglesGrid.getHeight());
        extendedData = replaceValues(extendedData,Float.NaN, 0.0f);
        return createFloatPlanarImage(extendedData,bandAnglesGrid.getWidth()+2,bandAnglesGrid.getHeight()+2);
    }

    public static PlanarImage createExtendedFloatPlanarImage(float[] src, int width, int height){
        return createFloatPlanarImage(extendDataV2(src,width,height),width+2,height+2);
    }


    public static PlanarImage createFloatPlanarImage(float[] src, int width, int height){
        int[] bandOffsets = {0};

        SampleModel sampleModel = new PixelInterleavedSampleModel(TYPE_FLOAT, width, height, 1, width, bandOffsets);
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.TRANSLUCENT, TYPE_FLOAT);
        PlanarImage opImage;
        DataBuffer buffer = new DataBufferFloat(width * height);

        // Wrap it in a writable raster
        WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);
        raster.setPixels(0, 0, width, height, src);

        // Create an image with this raster
        BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
        opImage = PlanarImage.wrapRenderedImage(image);
        return opImage;
    }

    public static float[] replaceValues(float[] src, float srcValue, float dstValue) {
        if(Float.isNaN(srcValue)) {
            for(int i = 0 ; i<src.length ; i++) {
                if(Float.isNaN(src[i])) src[i] = dstValue;
            }
        } else {
            for (int i = 0; i < src.length; i++) {
                if (src[i] == srcValue) src[i] = dstValue;
            }
        }
        return src;
    }


    public static float[] extendData (float[] src, int width, int height) {

        if(src.length != width * height) {
            return null;
        }

        int extendedWidth = width + 2;
        int extendedHeight = height + 2;
        float[] extendedData = new float[extendedWidth*extendedHeight];

        //fill with NaN
        Arrays.fill(extendedData, Float.NaN);

        //copy src data
        for(int i = 0; i<height;i++) {
            for(int j = 0; j<width;j++) {
                extendedData[(j + 1) + (i + 1) * extendedWidth] = src[j + i * width];
            }
        }

        boolean newValue = true;
        while (newValue) {
            newValue = false;
            //add row by row 1
            for (int i = 0; i < extendedHeight ; i++) {
                for (int j = 0; j < extendedWidth; j++) {
                    int iPosition = j + i * extendedWidth;
                    if (j < (extendedWidth - 2) && Float.isNaN(extendedData[iPosition]) && (!Float.isNaN(extendedData[iPosition + 1])) && (!Float.isNaN(extendedData[iPosition + 2]))) {
                        extendedData[iPosition] = extendedData[iPosition + 1] - (extendedData[iPosition + 2] - extendedData[iPosition + 1]);
                        newValue = true;
                    }
                    if (j >= 2 && Float.isNaN(extendedData[iPosition]) && (!Float.isNaN(extendedData[iPosition - 1])) && (!Float.isNaN(extendedData[iPosition - 2]))) {
                        extendedData[iPosition] = extendedData[iPosition - 1] + (extendedData[iPosition - 1] - extendedData[iPosition - 2]);
                        j = extendedWidth;
                        newValue = true;
                    }
                }
            }
            //add 1 col by col
            for (int j = 0; j < extendedWidth; j++) {
                for (int i = 0; i < extendedHeight; i++) {
                    int iPosition = j + i * extendedWidth;
                    if (i < (extendedHeight - 2) && Float.isNaN(extendedData[iPosition]) && (!Float.isNaN(extendedData[iPosition + extendedWidth])) && (!Float.isNaN(extendedData[iPosition + 2 * extendedWidth]))) {
                        extendedData[iPosition] = extendedData[iPosition + extendedWidth] - (extendedData[iPosition + 2 * extendedWidth] - extendedData[iPosition + extendedWidth]);
                        newValue = true;
                    }
                    if (i >= 2 && Float.isNaN(extendedData[iPosition]) && (!Float.isNaN(extendedData[iPosition - extendedWidth])) && (!Float.isNaN(extendedData[iPosition - 2 * extendedWidth]))) {
                        extendedData[iPosition] = extendedData[iPosition - extendedWidth] + (extendedData[iPosition - extendedWidth] - extendedData[iPosition - 2 * extendedWidth]);
                        i = extendedHeight;
                        newValue = true;
                    }

                }
            }
        }

        for (int i = 0; i < extendedHeight * extendedWidth; i++) {
            if (Float.isNaN(extendedData[i])) {
                extendedData[i] = 0.0f;
            }
        }

        return extendedData;
    }

    private static void surroundValuesWithINF (float[] src, int width, int height) {
        for (int i = 1; i < height-1 ; i++) {
            for (int j = 1; j < width-1 ; j++) {
                int iPosition = j + i * width;
                if (!Float.isFinite(src[iPosition])) {
                    continue;
                }
                if (!Float.isFinite(src[iPosition + 1]))
                    src[iPosition + 1] = Float.POSITIVE_INFINITY;
                if (!Float.isFinite(src[iPosition - 1]))
                    src[iPosition - 1] = Float.POSITIVE_INFINITY;
                if (!Float.isFinite(src[iPosition + width]))
                    src[iPosition + width] = Float.POSITIVE_INFINITY;
                if (!Float.isFinite(src[iPosition - width]))
                    src[iPosition - width] = Float.POSITIVE_INFINITY;
                if (!Float.isFinite(src[iPosition - width -1]))
                    src[iPosition - width -1] = Float.POSITIVE_INFINITY;
                if (!Float.isFinite(src[iPosition - width + 1]))
                    src[iPosition - width + 1] = Float.POSITIVE_INFINITY;
                if (!Float.isFinite(src[iPosition + width - 1]))
                    src[iPosition + width - 1] = Float.POSITIVE_INFINITY;
                if (!Float.isFinite(src[iPosition + width + 1]))
                    src[iPosition + width + 1] = Float.POSITIVE_INFINITY;
            }
        }
    }

    private static void extrapolateINFValues (float[] src, int width, int height) {
        boolean newValue = true;
        while (newValue) {
            newValue = false;
            //add row by row 1
            for (int i = 0; i < height ; i++) {
                for (int j = 0; j < width; j++) {
                    int iPosition = j + i * width;
                    if (j < (width - 2) && Float.isInfinite(src[iPosition]) && (Float.isFinite(src[iPosition + 1])) && (Float.isFinite(src[iPosition + 2]))) {
                        src[iPosition] = src[iPosition + 1] - (src[iPosition + 2] - src[iPosition + 1]);
                        newValue = true;
                    }
                    if (j >= 2 && Float.isInfinite(src[iPosition]) && (Float.isFinite(src[iPosition - 1])) && (Float.isFinite(src[iPosition - 2]))) {
                        src[iPosition] = src[iPosition - 1] + (src[iPosition - 1] - src[iPosition - 2]);
                        j = width;
                        newValue = true;
                    }
                }
            }
            //add 1 col by col
            for (int j = 0; j < width; j++) {
                for (int i = 0; i < height; i++) {
                    int iPosition = j + i * width;
                    if (i < (height - 2) && Float.isInfinite(src[iPosition]) && (Float.isFinite(src[iPosition + width])) && (Float.isFinite(src[iPosition + 2 * width]))) {
                        src[iPosition] = src[iPosition + width] - (src[iPosition + 2 * width] - src[iPosition + width]);
                        newValue = true;
                    }
                    if (i >= 2 && Float.isInfinite(src[iPosition]) && (Float.isFinite(src[iPosition - width])) && (Float.isFinite(src[iPosition - 2 * width]))) {
                        src[iPosition] = src[iPosition - width] + (src[iPosition - width] - src[iPosition - 2 * width]);
                        i = height;
                        newValue = true;
                    }

                }
            }
        }
    }

    private static void copyNeighbourINFValues (float[] src, int width, int height) {
        boolean newValue = true;
        while (newValue) {
            newValue = false;
            for (int j = 0; j < width; j++) {
                for (int i = 0; i < height; i++) {
                    int iPosition = j + i * width;
                    if (Float.isInfinite(src[iPosition])) {
                        if (isValidArrayPosition(src, iPosition + 1) && Float.isFinite(src[iPosition + 1])) {
                            src[iPosition] = src[iPosition + 1];
                            newValue = true;
                            continue;
                        } else if (isValidArrayPosition(src, iPosition - 1) && Float.isFinite(src[iPosition - 1])) {
                            src[iPosition] = src[iPosition - 1];
                            newValue = true;
                            continue;
                        } else if (isValidArrayPosition(src, iPosition + width) && Float.isFinite(src[iPosition + width])) {
                            src[iPosition] = src[iPosition + width];
                            newValue = true;
                            continue;
                        } else if (isValidArrayPosition(src, iPosition - width) && Float.isFinite(src[iPosition - width])) {
                            src[iPosition] = src[iPosition - width];
                            newValue = true;
                            continue;
                        } else if (isValidArrayPosition(src, iPosition - width - 1) && Float.isFinite(src[iPosition - width - 1])) {
                            src[iPosition] = src[iPosition - width - 1];
                            newValue = true;
                            continue;
                        } else if (isValidArrayPosition(src, iPosition - width + 1) && Float.isFinite(src[iPosition - width + 1])) {
                            src[iPosition] = src[iPosition - width + 1];
                            newValue = true;
                            continue;
                        } else if (isValidArrayPosition(src, iPosition + width - 1) && Float.isFinite(src[iPosition + width - 1])) {
                            src[iPosition] = src[iPosition + width - 1];
                            newValue = true;
                            continue;
                        } else if (isValidArrayPosition(src, iPosition + width + 1) && Float.isFinite(src[iPosition + width + 1])) {
                            src[iPosition] = src[iPosition + width + 1];
                            newValue = true;
                            continue;
                        }
                    }
                }
            }
        }
    }

    public static float[] extendDataV2 (float[] src, int width, int height) {

        if(src.length != width * height) {
            return null;
        }

        //create the new float[]
        int extendedWidth = width + 2;
        int extendedHeight = height + 2;
        float[] extendedData = new float[extendedWidth*extendedHeight];

        //fill with NaN
        Arrays.fill(extendedData, Float.NaN);

        //copy src data
        for(int i = 0; i<height;i++) {
            for(int j = 0; j<width;j++) {
                if(!Float.isNaN(src[j + i * width])) {
                    extendedData[(j + 1) + (i + 1) * extendedWidth] = src[j + i * width];
                }
            }
        }

        //compute the values at a distance <= 2 pixels with respect to the valid values.
        for(int i = 0; i < 2 ; i++) {
            surroundValuesWithINF(extendedData, extendedWidth, extendedHeight);
            extrapolateINFValues(extendedData, extendedWidth, extendedHeight);
            copyNeighbourINFValues(extendedData, extendedWidth, extendedHeight);
        }

        //finally replace NAN by 0.0
        for(int iPosition = 0; iPosition < extendedWidth * extendedHeight ; iPosition++) {
            if (Float.isNaN(extendedData[iPosition])) {
                extendedData[iPosition] = 0.0f;
            }
        }

        return extendedData;
    }

    private static boolean isValidArrayPosition(float[] array, int iPosition) {
        if (array == null) {
            return false;
        }
        if(iPosition < 0 || iPosition >= array.length) {
            return false;
        }
        return true;
    }

    public static MultiLevelImage createMultiLevelImage(float[] data, int width, int height, AffineTransform affineTransform) {
        //create a planarImage with the data
        PlanarImage planarImage = createFloatPlanarImage(data, width, height);
        //create multi-level model
        DefaultMultiLevelModel multiLevelModel = new DefaultMultiLevelModel(affineTransform, width, height);
        //create multi-level source
        DefaultMultiLevelSource multiLevelSource= new DefaultMultiLevelSource(planarImage, multiLevelModel, Interpolation.getInstance(Interpolation.INTERP_NEAREST));
        //create multi-level image
        MultiLevelImage multiLevelImage = new DefaultMultiLevelImage(multiLevelSource);
        return multiLevelImage;
    }

    public static void replaceBandSourceImage(Band originalBand, PlanarImage planarImage) {
        MultiLevelImage newMultiLevelImage = new DefaultMultiLevelImage(new DefaultMultiLevelSource(planarImage, originalBand.getMultiLevelModel(), Interpolation.getInstance(Interpolation.INTERP_NEAREST)));
        originalBand.setSourceImage(S2ResamplerUtils.adjustImageToModelTransform(newMultiLevelImage, originalBand.getMultiLevelModel()));
    }

    public static int countMatches(String[] inputs, String REGEX) {
        Pattern pattern = Pattern.compile(REGEX);
        int count = 0;
        for(String input: inputs) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) count++;
        }
        return count;
    }
}
