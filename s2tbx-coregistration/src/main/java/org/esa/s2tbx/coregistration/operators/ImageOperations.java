package org.esa.s2tbx.coregistration.operators;

import com.bc.ceres.jai.GeneralFilterFunction;
import com.bc.ceres.jai.opimage.GeneralFilterOpImage;
import org.esa.s2tbx.coregistration.MatrixUtils;
import org.esa.snap.core.gpf.OperatorSpi;

import javax.media.jai.*;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.awt.image.DataBufferFloat;
import java.awt.image.renderable.ParameterBlock;
import java.util.Hashtable;

/**
 * Utility class for image-based operations
 */
public class ImageOperations {

    public static BufferedImage replace(BufferedImage img, float val, float replace) {
        return (new ReplaceValueOp(img, null, null, null, Float.NaN, null, new double[]{val}, new double[]{replace})).getAsBufferedImage();
        //JAI.create("org.geotools.gce.gtopo30.NoDataReplacer", pb).getAsBufferedImage();
    }

    public static RenderedImage replace(RenderedImage img, float val, float replace) {
        return (new ReplaceValueOp(img, null, null, null, Float.NaN, null, new double[]{val}, new double[]{replace}));
        //JAI.create("org.geotools.gce.gtopo30.NoDataReplacer", pb).getAsBufferedImage();
    }

    public static BufferedImage applyContrast(BufferedImage inputImage) {
        final BufferedImage extrema = ExtremaDescriptor.create(inputImage, null, 1, 1, false, 1, null).getAsBufferedImage();
        double[][] minMax = (double[][]) extrema.getProperty("Extrema");
        double min = minMax[0][0];
        double max = minMax[1][0];
        double dif = max - min;

        return rescale(1.0 / (max - min), min / (max - min), inputImage);
    }

    public static BufferedImage doubleConvolve(BufferedImage input, float[] factor) {
        int kernelSize = factor.length;
        int rad = (kernelSize - 1) / 2;
        BufferedImage borderedImage = addBorder(input, rad, rad, rad, rad);
        KernelJAI kernel = new KernelJAI(kernelSize, 1, factor);
        BufferedImage convolvedImg = JAI.create("convolve", borderedImage, kernel).getAsBufferedImage();
        kernel = new KernelJAI(1, kernelSize, factor);
        BufferedImage secondConvolvedImg = JAI.create("convolve", convolvedImg, kernel).getAsBufferedImage();
        int mrad = (-1) * rad;
        BufferedImage finalImage = addBorder(secondConvolvedImg, mrad, mrad, mrad, mrad);
        return finalImage;
    }

    public static RenderedImage doubleConvolve(RenderedImage input, float[] factor) {
        int kernelSize = factor.length;
        int rad = (kernelSize - 1) / 2;
        RenderedImage borderedImage = addBorder(input, rad, rad, rad, rad);
        KernelJAI kernel = new KernelJAI(kernelSize, 1, factor);
        RenderedImage convolvedImg = JAI.create("convolve", borderedImage, kernel);//.getAsBufferedImage();
        kernel = new KernelJAI(1, kernelSize, factor);
        RenderedImage secondConvolvedImg = JAI.create("convolve", convolvedImg, kernel);//.getAsBufferedImage();
        int mrad = (-1) * rad;
        RenderedImage finalImage = addBorder(secondConvolvedImg, mrad, mrad, mrad, mrad);
        return finalImage;
    }

    public static BufferedImage imageFromMatrix(float[][] input) {
        int width = input[0].length;
        int height = input.length;

        float[] inputArray = new float[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                inputArray[i * width + j] = input[i][j];
            }
        }
        return imageFromArray(inputArray, width, height);
    }

    public static BufferedImage imageFromArray(float[] input, int width, int height) {
        SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, width, height, 1, width, new int[]{0});
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_FLOAT);
        DataBuffer buffer = new DataBufferFloat(width * height * 1);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);
        raster.setPixels(0, 0, width, height, input);
        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }

    public static float[][] matrixFromImage(BufferedImage img) {
        int imgHeight = img.getHeight();
        int imgWidth = img.getWidth();
        float[][] imgData = new float[imgHeight][imgWidth];

        DataBuffer dataBuffer = img.getData().getDataBuffer();
        switch (dataBuffer.getDataType()) {
            case DataBuffer.TYPE_BYTE: {
                DataBufferByte dataBufferByte = (DataBufferByte) dataBuffer;
                byte[] values = dataBufferByte.getData();

                int p = 0;
                for (int i = 0; i < imgHeight; i++) {
                    for (int j = 0; j < imgWidth; j++) {
                        imgData[i][j] = Byte.toUnsignedInt(values[p++]);
                    }
                }
                break;
            }
            case DataBuffer.TYPE_USHORT: {
                DataBufferUShort dataBufferUShort = (DataBufferUShort) dataBuffer;
                short[] values = dataBufferUShort.getData();

                int p = 0;
                for (int i = 0; i < imgHeight; i++) {
                    for (int j = 0; j < imgWidth; j++) {
                        imgData[i][j] = Short.toUnsignedInt(values[p++]);
                    }
                }
                break;
            }
            case DataBuffer.TYPE_SHORT: {
                DataBufferShort dataBufferShort = (DataBufferShort) dataBuffer;
                short[] values = dataBufferShort.getData();

                int p = 0;
                for (int i = 0; i < imgHeight; i++) {
                    for (int j = 0; j < imgWidth; j++) {
                        imgData[i][j] =  values[p++];
                    }
                }
                break;
            }
            case DataBuffer.TYPE_INT: {
                DataBufferInt dataBufferInt = (DataBufferInt) dataBuffer;
                int[] values = dataBufferInt.getData();

                int p = 0;
                for (int i = 0; i < imgHeight; i++) {
                    for (int j = 0; j < imgWidth; j++) {
                        imgData[i][j] = values[p++];
                    }
                }
                break;
            }
            case DataBuffer.TYPE_FLOAT: {
                DataBufferFloat dataBufferFloat = (DataBufferFloat) dataBuffer;
                float[] values = dataBufferFloat.getData();

                int p = 0;
                for (int i = 0; i < imgHeight; i++) {
                    for (int j = 0; j < imgWidth; j++) {
                        imgData[i][j] = values[p++];
                    }
                }
                break;
            }
            default:
                throw new RuntimeException("Unsupported image format");
        }

        return imgData;
    }

    public static BufferedImage addBorder(BufferedImage inputImage, int left, int right, int top, int bottom) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(inputImage);
        pb.add(left);
        pb.add(right);
        pb.add(top);
        pb.add(bottom);
        pb.add(BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
        return JAI.create("border", pb).getAsBufferedImage();
    }

    public static RenderedImage addBorder(RenderedImage inputImage, int left, int right, int top, int bottom) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(inputImage);
        pb.add(left);
        pb.add(right);
        pb.add(top);
        pb.add(bottom);
        pb.add(BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
        return JAI.create("border", pb);
    }

    public static BufferedImage equalize(BufferedImage src) {
        //TODO Histogram histo = Histogram.computeHistogramFloat(values, IndexValidator.TRUE, 5, null, null, ProgressMonitor.NULL);
        BufferedImage nImg = new BufferedImage(src.getWidth(), src.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        int nHistSize = 65536;

        WritableRaster wr = src.getRaster();
        WritableRaster er = nImg.getRaster();
        int totpix = wr.getWidth() * wr.getHeight();
        int[] histogram = new int[nHistSize];

        for (int x = 0; x < wr.getWidth(); x++) {
            for (int y = 0; y < wr.getHeight(); y++) {
                histogram[wr.getSample(x, y, 0)]++;
            }
        }

        int[] chistogram = new int[nHistSize];
        chistogram[0] = histogram[0];
        for (int i = 1; i < nHistSize; i++) {
            chistogram[i] = chistogram[i - 1] + histogram[i];
        }

        float[] arr = new float[nHistSize];
        for (int i = 0; i < nHistSize; i++) {
            arr[i] = (float) ((chistogram[i] * 255.0) / (float) totpix);
        }

        for (int x = 0; x < wr.getWidth(); x++) {
            for (int y = 0; y < wr.getHeight(); y++) {
                int nVal = (int) arr[wr.getSample(x, y, 0)];
                er.setSample(x, y, 0, nVal);
            }
        }
        nImg.setData(er);
        return nImg;
    }

    public static RenderedImage equalize(RenderedImage src) {
        return src;
    }

    public static BufferedImage convertBufferedImage(RenderedImage img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm
                .createCompatibleWritableRaster(width, height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable properties = new Hashtable<>();
        String[] keys = img.getPropertyNames();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        BufferedImage result = new BufferedImage(cm, raster,
                isAlphaPremultiplied, properties);
        img.copyData(raster);
        return result;
    }

    //TODO not used
    public static BufferedImage interpolate4(BufferedImage img, BufferedImage dy, BufferedImage dx) {


        DataBuffer dyBuffer = dy.getData().getDataBuffer();
        DataBuffer dxBuffer = dx.getData().getDataBuffer();
        int xStep = 1;
        int xNumCells = (int) img.getWidth();
        int yStep = 1;
        int yNumCells = (int) img.getHeight();
        float[] warpPositions = new float[2 * (xNumCells + 1) * (yNumCells + 1)];
        for (int y = 0; y < yNumCells; y++) {
            for (int x = 0; x < xNumCells; x++) {
                int pos = 2 * (y * (xNumCells + 1) + x);
                int idx = y * xNumCells + x;
                warpPositions[pos] = dyBuffer.getElemFloat(idx);// dx.getData().getSampleFloat(x, y, 0);
                warpPositions[pos + 1] = dxBuffer.getElemFloat(idx);//dy.getData().getSampleFloat(x, y, 0);
            }
        }
        for (int y = 0; y <= yNumCells; y++) {
            int pos = 2 * (y * (xNumCells + 1) + xNumCells);
            warpPositions[pos] = xNumCells - 1f;// dx.getData().getSampleFloat(x, y, 0);
            warpPositions[pos + 1] = y;//dxBuffer.getElemFloat(xNumCells - 1) + 1;//dy.getData().getSampleFloat(x, y, 0);
        }
        for (int x = 0; x < xNumCells; x++) {
            int pos = 2 * (yNumCells * (xNumCells + 1) + x);
            warpPositions[pos] = x;//dyBuffer.getElemFloat(yNumCells - 1) + 1;// dx.getData().getSampleFloat(x, y, 0);
            warpPositions[pos + 1] = yNumCells - 1f;//dy.getData().getSampleFloat(x, y, 0);
        }

        Warp warp = new WarpGrid(0, xStep, xNumCells, 0, yStep, yNumCells, warpPositions);
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(warp);
        pb.add(new InterpolationBilinear());
        BufferedImage result = JAI.create("warp", pb).getAsBufferedImage();

        assert img.getWidth() == result.getWidth();
        assert img.getHeight() == result.getHeight();

        if (result.getData().getWidth() != result.getData().getHeight()) {
            int t = 9;
        }
        return result;
    }

    public static BufferedImage interpolate(BufferedImage img, BufferedImage dx, BufferedImage dy) {
        float[][] mimg = matrixFromImage(img);
        float[][] mdx = matrixFromImage(dx);
        float[][] mdy = matrixFromImage(dy);

        float[][] result = MatrixUtils.interp2(mimg, mdx, mdy);

        BufferedImage rimg = imageFromMatrix(result);

        return rimg;
    }

    public static BufferedImage gradientCol(BufferedImage source) {
        GeneralFilterFunction f = new GeneralFilterFunction(1, 3, 0, 0, null) {
            @Override
            public float filter(float[] fdata) {
                return (fdata[2] - fdata[0]) / 2.0f;
            }
        };
        return new GeneralFilterOpImage(source, null, null, null, f).getAsBufferedImage();
    }

    public static BufferedImage gradientRow(BufferedImage source) {
        GeneralFilterFunction f = new GeneralFilterFunction(3, 1, 0, 0, null) {
            @Override
            public float filter(float[] fdata) {
                return (fdata[2] - fdata[0]) / 2.0f;
            }
        };
        return new GeneralFilterOpImage(source, null, null, null, f).getAsBufferedImage();
    }

    public static BufferedImage gradientRow2(BufferedImage source) {
        return imageFromMatrix(MatrixUtils.gradientrow(matrixFromImage(source)));
    }

    public static BufferedImage gradientCol2(BufferedImage source) {
        return imageFromMatrix(MatrixUtils.gradientcol(matrixFromImage(source)));
    }

    public static BufferedImage createConstImage2(int width, int height, Integer n) {
        return ConstantDescriptor.create((float) width, (float) height, new Integer[]{n}, null).getAsBufferedImage();
    }

    public static RenderedImage createConstImage(int width, int height, Integer n) {
        return ConstantDescriptor.create((float) width, (float) height, new Integer[]{n}, null).getAsBufferedImage();
    }

    public static BufferedImage createMeshColImage(int width, int height) {
        float[] values = new float[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                values[i * width + j] = j;
            }
        }
        return imageFromArray(values, width, height);
    }

    public static BufferedImage createMeshRowImage(int width, int height) {
        float[] values = new float[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                values[i * width + j] = i;
            }
        }
        return imageFromArray(values, width, height);
    }

    public static BufferedImage rescale(double multiplyFactor, double addFactor, BufferedImage sourceImage) {
        int bands = sourceImage.getData().getNumBands();
        double[] multiplyParam = new double[bands], addParam = new double[bands];
        for (int i = 0; i < bands; i++) {
            multiplyParam[i] = multiplyFactor;
            addParam[i] = addFactor;
        }
        ParameterBlock pbRescale = new ParameterBlock();
        pbRescale.add(multiplyParam);
        pbRescale.add(addParam);
        pbRescale.addSource(sourceImage);
        return JAI.create("rescale", pbRescale).getAsBufferedImage();

    }

    public static RenderedImage rescale(double multiplyFactor, double addFactor, RenderedImage sourceImage) {
        int bands = sourceImage.getData().getNumBands();
        double[] multiplyParam = new double[bands], addParam = new double[bands];
        for (int i = 0; i < bands; i++) {
            multiplyParam[i] = multiplyFactor;
            addParam[i] = addFactor;
        }
        ParameterBlock pbRescale = new ParameterBlock();
        pbRescale.add(multiplyParam);
        pbRescale.add(addParam);
        pbRescale.addSource(sourceImage);
        return JAI.create("rescale", pbRescale);

    }

    public static void writeImage(BufferedImage img, String fileLocation) {
        /*File fileImg = new File(fileLocation);
        try {
            ImageIO.write(img, "tif", fileImg);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static void writeImage(RenderedImage img, String fileLocation) {
        /*File fileImg = new File(fileLocation);
        try {
            ImageIO.write(img, "tif", fileImg);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static BufferedImage resize(BufferedImage sourceImage, float rescaleXfactor, float rescaleYfactor, Interpolation interp) {
        return ScaleDescriptor.create(sourceImage, rescaleXfactor, rescaleYfactor, 0.0f, 0.0f, interp, null).getAsBufferedImage();
    }

    public static RenderedImage resize(RenderedImage sourceImage, float rescaleXfactor, float rescaleYfactor, Interpolation interp) {
        return ScaleDescriptor.create(sourceImage, rescaleXfactor, rescaleYfactor, 0.0f, 0.0f, interp, null);
    }
}
