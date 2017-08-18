package org.esa.s2tbx.coregistration;

import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.opencv.core.Mat;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.SubsampleAverageDescriptor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferFloat;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Hashtable;

/**
 * Coregistration
 *
 * @author Ramona Manda
 * @since 6.0.0
 */
@OperatorMetadata(
        alias = "CoregistrationOp",
        version = "1.0",
        category = "Optical",
        description = "The 'Coregistration Processor' operator ...",
        authors = "RM",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class CoregistrationOp extends Operator {

    @SourceProduct(alias = "master", description = "The source product which serves as master.")
    private Product masterProduct;

    @SourceProduct(alias = "slave", description = "The source product which serves as slave.")
    private Product slaveProduct;

    @TargetProduct(description = "The target product which will use the master's location.")
    private Product targetProduct;

    public boolean contrast = false;
    public int levels = 6;
    public int rank = 4;
    public int iterations = 2;
    private static final float[] burt1D = new float[]{0.05f, 0.25f, 0.4f, 0.25f, 0.05f};
    private static final int[] radius = new int[]{32, 28, 24, 20, 16, 12, 8};

    public CoregistrationOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        targetProduct = new Product("coregistered_" + slaveProduct.getName(),
                slaveProduct.getProductType(),
                slaveProduct.getSceneRasterWidth(),
                slaveProduct.getSceneRasterHeight());
        targetProduct.setDescription(slaveProduct.getDescription());
        //TODO compute target product as interpolation of slave product with u and v as flow matrices
    }

    public void doExecute(Product sourceMasterProduct, Product sourceSlaveProduct, int bandIndex) {
        //IMPROVEMENT: if necessary, JAI dithering operation compresses the three bands of an RGB image to a single-banded byte image.

        int levelMaster = sourceMasterProduct.getBandAt(bandIndex).getMultiLevelModel().getLevelCount();
        RenderedImage sourceMasterImage = sourceMasterProduct.getBandAt(bandIndex).getSourceImage().getImage(levelMaster);
        //RenderedImage sourceMasterImage = img;
        RenderedImage processedMasterImage = sourceMasterImage;

        int levelSlave = sourceSlaveProduct.getBandAt(bandIndex).getMultiLevelModel().getLevelCount();
        RenderedImage sourceSlaveImage = sourceMasterProduct.getBandAt(bandIndex).getSourceImage().getImage(levelSlave);
        //RenderedImage sourceMasterImage = img;
        RenderedImage processedSlaveImage = sourceSlaveImage;

        if (contrast) {
            processedMasterImage = applyContrast(sourceMasterImage);
            processedSlaveImage = applyContrast(sourceSlaveImage);
        }

        RenderedImage[] pyramidMaster = pyramid(processedMasterImage, levels);
        RenderedImage[] pyramidSlave = pyramid(processedSlaveImage, levels);

        float[][] u, v, meshRow, meshCol;

        u = new float[0][0];
        v = new float[0][0];

        for (int k = pyramidMaster.length - 1; k >= 0; k++) {
            RenderedImage levelMasterImage = pyramidMaster[k];
            RenderedImage levelSlaveImage = pyramidSlave[k];
            RenderedImage levelMasterImageEq, levelSlaveImageEq;
            if (contrast) {
                //clahe should be applied directly on RenderedImage, to avoid transferng from/to image
                levelMasterImageEq = equalize(convertRenderedImage(levelMasterImage));
                levelSlaveImageEq = equalize(convertRenderedImage(levelSlaveImage));
            } else {
                levelMasterImageEq = levelMasterImage;
                levelSlaveImageEq = levelSlaveImage;
            }
            //init meshgrid and u/v
            meshCol = new float[levelMasterImage.getWidth()][levelMasterImage.getHeight()];
            meshRow = new float[levelMasterImage.getWidth()][levelMasterImage.getHeight()];
            MatrixUtils.meshgrid(meshCol, meshRow, levelMasterImage.getHeight(), levelMasterImage.getWidth());
            if (k == pyramidMaster.length - 1) {
                u = new float[levelMasterImage.getWidth()][levelMasterImage.getHeight()];
                v = new float[levelMasterImage.getWidth()][levelMasterImage.getHeight()];
            } else {
                float[][] u1 = resize(u, levelMasterImage.getWidth(), levelMasterImage.getHeight());
                u = u1;
                float[][] v1 = resize(v, levelMasterImage.getWidth(), levelMasterImage.getHeight());
                v = v1;

            }
            float[][] I0, I1sup, I1inf;

            float[][] imgI0Data = matrixFromImage((BufferedImage) levelMasterImage);
            I0 = imgI0Data;

            float[][] imgI1Data = matrixFromImage((BufferedImage) levelSlaveImage);
            I1sup = imgI1Data;

            float[][] imgI1pData = matrixFromImage((BufferedImage) processedSlaveImage);
            for (int i = 0; i < imgI1pData.length; i++) {
                for (int j = 0; j < imgI1pData[0].length; j++) {
                    imgI1pData[i][j] = 1 - imgI1pData[i][j];
                }
                I1inf = imgI1pData;
                if (rank != 0) {
                    I0 = MatrixUtils.rank_sup(imgI0Data, rank);
                    I1sup = MatrixUtils.rank_sup(imgI1Data, rank);
                    I1inf = MatrixUtils.rank_inf(imgI1Data, rank);
                }
                float[][] Ix = MatrixUtils.gradientRow(I0);
                float[][] Iy = MatrixUtils.gradientColumn(I0);
                float[][] Ixx = MatrixUtils.simpleMultiply(Ix, Ix);
                float[][] Iyy = MatrixUtils.simpleMultiply(Iy, Iy);
                float[][] Ixy = MatrixUtils.simpleMultiply(Ix, Iy);

                for (int rad = 0; rad < radius.length; rad++) {
                    int r = radius[rad];
                    float[] fen = new float[2 * r + 1];
                    for (int j = 0; j < fen.length; j++) {
                        fen[j] = 1;
                    }
                    float[][] A = doubleConvolve(Ixx, fen);
                    float[][] B = doubleConvolve(Iyy, fen);
                    float[][] C = doubleConvolve(Ixy, fen);

                    for (int j = 0; j < iterations; j++) {
                        float[][] dx = MatrixUtils.sum(meshCol, u);
                        float[][] dy = MatrixUtils.sum(meshRow, v);
                        for (int ii = 0; ii < dx.length; ii++) {
                            for (int jj = 0; jj < dx[0].length; jj++) {
                                if (dx[ii][jj] < 1) {
                                    dx[ii][jj] = 1;
                                } else if (dx[ii][jj] > levelMasterImage.getWidth()) {
                                    dx[ii][jj] = levelMasterImage.getWidth();
                                }
                                if (dy[ii][jj] < 1) {
                                    dy[ii][jj] = 1;
                                } else if (dy[ii][jj] > levelMasterImage.getHeight()) {
                                    dy[ii][jj] = levelMasterImage.getWidth();
                                }
                            }
                        }
                        float[][] I1w = I1sup;
                        //..........................
                        //interpolate I1_sup with dx and dy - JAI rescale/ applyContrast
                        if (contrast) {

                            //interpolate H1 with dx and dy
                            //convolve H0-H1
                            //convolve 1-H0-H1
                            //interpolate I1_inf with dx and dy
                            //check convolution values and fill in with last interpolation
                        }

                        float[][] It = MatrixUtils.sum(
                                MatrixUtils.sum(
                                        MatrixUtils.difference(matrixFromImage((BufferedImage) processedMasterImage), I1w),
                                        MatrixUtils.simpleMultiply(u, Ix)),
                                MatrixUtils.simpleMultiply(v, Iy));

                        float[][] G = MatrixUtils.simpleMultiply(It, Ix);
                        float[][] H = MatrixUtils.simpleMultiply(It, Iy);
                        G = doubleConvolve(G, fen);
                        H = doubleConvolve(H, fen);
                        float[][] D = MatrixUtils.difference(
                                MatrixUtils.simpleMultiply(A, B),
                                MatrixUtils.simpleMultiply(C, C));
                        u = MatrixUtils.simpleDivide(
                                MatrixUtils.difference(
                                        MatrixUtils.simpleMultiply(G, B),
                                        MatrixUtils.simpleMultiply(C, H)),
                                D);
                        v = MatrixUtils.simpleDivide(
                                MatrixUtils.difference(
                                        MatrixUtils.simpleMultiply(A, H),
                                        MatrixUtils.simpleMultiply(C, G)),
                                D);
                        //u and v filled with 0 where divided could not be done

                    }
                }
            }

        }
    }

    private RenderedImage applyContrast(RenderedImage inputImage) {
        final RenderedImage extrema = ExtremaDescriptor.create(inputImage, null, 1, 1, false, 1, null);
        double[][] minMax = (double[][]) extrema.getProperty("Extrema");
        double min = minMax[0][0];
        double max = minMax[1][0];
        double dif = max - min;

        double[] multiplyByThis = new double[1];
        multiplyByThis[0] = 1.0 / (max - min);
        double[] addThis = new double[1];
        addThis[0] = min / (max - min);
        ParameterBlock pbRescale = new ParameterBlock();
        pbRescale.add(multiplyByThis);
        pbRescale.add(addThis);
        pbRescale.addSource(inputImage);
        PlanarImage outImage = (PlanarImage) JAI.create("rescale", pbRescale);
        return outImage;
    }

    private RenderedImage[] pyramid(RenderedImage inputImage, int level) {
        RenderedImage[] imagePyramid = new RenderedImage[level + 1];
        imagePyramid[0] = inputImage;
        for (int k = 0; k <= level; k++) {
            imagePyramid[k + 1] = pyramBurt(imagePyramid[k]);
        }
        return imagePyramid;
    }


    private RenderedImage pyramBurt(RenderedImage inputImage) {
        int radius = 2;
        RenderedImage borderedImage = addBorder(inputImage, radius, radius, radius, radius);
        PlanarImage convolvedImg = doubleConvolve(borderedImage, burt1D);

        //resize with 1/2 factor:
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        RenderedOp resizeOp = SubsampleAverageDescriptor.create(convolvedImg,
                0.5, 0.5, hints);

        BufferedImage bufferedResizedImage = resizeOp.getAsBufferedImage();

        return bufferedResizedImage;
    }

    private PlanarImage doubleConvolve(RenderedImage input, float[] factor) {
        int kernelSize = factor.length;
        KernelJAI kernel = new KernelJAI(kernelSize, kernelSize, burt1D);
        PlanarImage convolvedImg = JAI.create("convolve", input, kernel);
        PlanarImage secondConvImg = JAI.create("convolve", convolvedImg, kernel);
        return secondConvImg;
    }

    private BufferedImage imageFromMatrix(float[][] input) {
        //TODO to test the methods above
        BufferedImage nImg = new BufferedImage(input[0].length, input[0].length,
                BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster er = nImg.getRaster();
        er.setDataElements(0, 0, input);
        nImg.setData(er);
        return nImg;
    }

    private float[][] matrixFromImage(BufferedImage img) {
        int imgHeight = img.getHeight();
        int imgWidth = img.getWidth();
        float[] values = ((DataBufferFloat) img.getData().getDataBuffer()).getData();
        float[][] imgData = new float[imgHeight][imgWidth];
        for (int i = 0; i < values.length; i++) {
            imgData[i / imgHeight][i % imgHeight] = values[i];
        }
        return imgData;
    }

    private float[][] doubleConvolve(float[][] input, float[] factor) {
        BufferedImage nImg = imageFromMatrix(input);
        return null;
        //return doubleConvolve(nImg, factor).getData().getDataBuffer().getElemFloat(i);
    }

    private float[][] resize(float[][] source, int width, int height) {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        RenderedOp resizeOp = SubsampleAverageDescriptor.create(imageFromMatrix(source),
                (double) width / source[0].length, (double) height / source.length, hints);

        BufferedImage bufferedResizedImage = resizeOp.getAsBufferedImage();

        return matrixFromImage(bufferedResizedImage);
    }

    private RenderedImage addBorder(RenderedImage inputImage, int left, int right, int top, int bottom) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(inputImage);
        pb.add(left);
        pb.add(right);
        pb.add(top);
        pb.add(bottom);
        pb.add(BorderExtender.BORDER_ZERO);
        return JAI.create("border", pb);
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(CoregistrationOp.class);
        }
    }

    BufferedImage equalize(BufferedImage src) {
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
        /* [TODO] solution to access pixel values to avoid RenderedImage transformation ?????
        1 int width = pi.getWidth();
2 int height = pi.getHeight();
3 SampleModel sm = pi.getSampleModel();
4 int nbands = sm.getNumBands();
5 int[] pixel = new int[nbands];
6 RandomIter iterator = RandomIterFactory.create(pi, null);
7 for(int h=0;h<height;h++)
8 for(int w=0;w<width;w++)
9 {
10 iterator.getPixel(w,h,pixel);
11 System.out.print("at ("+w+","+h+"): ");
12 for(int band=0;band<nbands;band++)
13 System.out.print(pixel[band]+" ");
14 System.out.println();
15 }
         */
    }

    public static BufferedImage convertRenderedImage(RenderedImage img) {
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

}
