package org.esa.s2tbx.coregistration;

import com.bc.ceres.jai.GeneralFilterFunction;
import com.bc.ceres.jai.opimage.GeneralFilterOpImage;
import it.geosolutions.jaiext.border.*;
import org.esa.s2tbx.coregistration.operators.ComputeCompareOp;
import org.esa.s2tbx.dataio.s2.l1c.Sentinel2L1CProductReader;
import org.esa.s2tbx.dataio.s2.l2a.Sentinel2L2AProductReader;
import org.esa.s3tbx.dataio.landsat.geotiff.LandsatGeotiffReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import javax.media.jai.*;
import javax.media.jai.operator.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferUShort;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.util.Hashtable;

/**
 * Coregistration
 *
 * @author Ramona Manda
 * @since 6.0.0
 */
@OperatorMetadata(
        alias = "S2tbx-CoregistrationOp",
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
        if (getSourceProducts().length == 2) {
            masterProduct = getSourceProducts()[0];
            slaveProduct = getSourceProducts()[1];
            targetProduct = new Product("coregistered_" + slaveProduct.getName(),
                    slaveProduct.getProductType(),
                    slaveProduct.getSceneRasterWidth(),
                    slaveProduct.getSceneRasterHeight());
            targetProduct.setDescription(slaveProduct.getDescription());
            doExecute(masterProduct, slaveProduct, 0);
        }
    }

    public void doExecute(Product sourceMasterProduct, Product sourceSlaveProduct, int bandIndex) {
        //IMPROVEMENT: if necessary, JAI dithering operation compresses the three bands of an RGB image to a single-banded byte image.

        int levelMaster = sourceMasterProduct.getBandAt(bandIndex).getMultiLevelModel().getLevelCount();
        RenderedImage sourceMasterImage = sourceMasterProduct.getBandAt(bandIndex).getSourceImage().getImage(levelMaster - 1);
        RenderedImage processedMasterImage = sourceMasterImage;

        Band originalSlaveBand = sourceSlaveProduct.getBandAt(bandIndex);
        int levelSlave = originalSlaveBand.getMultiLevelModel().getLevelCount();
        RenderedImage sourceSlaveImage = originalSlaveBand.getSourceImage().getImage(levelSlave - 1);
        RenderedImage processedSlaveImage = sourceSlaveImage;

        if (contrast) {
            processedMasterImage = applyContrast(sourceMasterImage);
            processedSlaveImage = applyContrast(sourceSlaveImage);
        }

        RenderedImage[] pyramidMaster = pyramid(processedMasterImage, levels);
        RenderedImage[] pyramidSlave = pyramid(processedSlaveImage, levels);

        RenderedImage u = null, v = null, meshRow, meshCol;

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
            meshRow = createMeshRowImage(levelMasterImage.getHeight(), levelMasterImage.getWidth());
            meshCol = createMeshColImage(levelMasterImage.getHeight(), levelMasterImage.getWidth());

            if (k == pyramidMaster.length - 1) {
                //createMesh(levelMasterImage.getWidth(), levelMasterImage.getHeight(), u, v);
                u = createConstImage(levelMasterImage.getHeight(), levelMasterImage.getWidth(), 0);
                v = createConstImage(levelMasterImage.getHeight(), levelMasterImage.getWidth(), 0);
            } else {
                ParameterBlock pbRescale = new ParameterBlock();
                pbRescale.add(2);
                pbRescale.add(0);
                pbRescale.addSource(u);
                u = (PlanarImage) JAI.create("rescale", pbRescale);
                u = addBorder(u, 0, levelMasterImage.getWidth() - u.getWidth(), 0, levelMasterImage.getHeight() - u.getHeight());

                pbRescale = new ParameterBlock();
                pbRescale.add(2);
                pbRescale.add(0);
                pbRescale.addSource(v);
                v = (PlanarImage) JAI.create("rescale", pbRescale);
                v = addBorder(v, 0, levelMasterImage.getWidth() - v.getWidth(), 0, levelMasterImage.getHeight() - v.getHeight());

            }
            RenderedImage I0, I1sup, I1inf;

            if (rank != 0) {

                I0 = imageFromMatrix(MatrixUtils.rank_sup(matrixFromImage((BufferedImage) levelMasterImage), rank));
                I1sup = imageFromMatrix(MatrixUtils.rank_sup(matrixFromImage((BufferedImage) levelSlaveImage), rank));
                I1inf = imageFromMatrix(MatrixUtils.rank_inf(matrixFromImage((BufferedImage) levelSlaveImage), rank));
            } else {
                I0 = levelMasterImage;
                I1sup = levelSlaveImage;

                ParameterBlock pbRescale = new ParameterBlock();
                pbRescale.add(-1);
                pbRescale.add(1);
                pbRescale.addSource(levelSlaveImage);
                I1inf = (PlanarImage) JAI.create("rescale", pbRescale);

            }

            RenderedImage Ix = gradientRow(I0);
            RenderedImage Iy = gradientCol(I0);
            RenderedImage Ixx = MultiplyDescriptor.create(Ix, Ix, null);
            RenderedImage Iyy = MultiplyDescriptor.create(Iy, Iy, null);
            RenderedImage Ixy = MultiplyDescriptor.create(Ix, Iy, null);

            for (int rad = 0; rad < radius.length; rad++) {
                int r = radius[rad];
                float[] fen = new float[2 * r + 1];
                for (int j = 0; j < fen.length; j++) {
                    fen[j] = 1;
                }
                RenderedImage A = doubleConvolve(Ixx, fen);
                RenderedImage B = doubleConvolve(Iyy, fen);
                RenderedImage C = doubleConvolve(Ixy, fen);

                for (int j = 0; j < iterations; j++) {
                    ParameterBlock pb = new ParameterBlock();
                    pb.addSource(meshCol);
                    pb.addSource(u);
                    RenderedImage dx = (RenderedImage) JAI.create("add", pb);

                    pb = new ParameterBlock();
                    pb.addSource(meshRow);
                    pb.addSource(v);
                    RenderedImage dy = (RenderedImage) JAI.create("add", pb);

                    restrictBounds(dx, dy);

                    RenderedImage I1w = interpolate(I1sup, dx, dy);

                    if (contrast) {
                        RenderedImage H1w = interpolate(levelSlaveImageEq, dx, dy);

                        float[] wi = new float[2 * rank + 1];
                        for (int i = 0; i < wi.length; i++) {
                            wi[i] = 1;
                        }
                        double[] wid = new double[2 * rank + 1];
                        for (int i = 0; i < wi.length; i++) {
                            wi[i] = 1;
                        }

                        RenderedImage Hdif = SubtractDescriptor.create(levelMasterImageEq, H1w, null);//levelMasterImageEq-H1w
                        RenderedImage crit1 = doubleConvolve(Hdif, wi);
                        RenderedImage Hinvert = SubtractFromConstDescriptor.create(
                                AddDescriptor.create(levelMasterImageEq, H1w, null),
                                wid, null);
                        RenderedImage crit2 = doubleConvolve(Hinvert, wi);
                        RenderedImage I1w_inf = interpolate(I1inf, dx, dy);
                        //I1w =
                        ComputeCompareOp op2 = new ComputeCompareOp(I1w, I1w_inf, crit1, crit2, null, null);
                        I1w = op2.getAsBufferedImage();
                        //interpolate H1 with dx and dy
                        //convolve H0-H1
                        //convolve 1-H0-H1
                        //interpolate I1_inf with dx and dy
                        //check convolution values and fill in with last interpolation
                    }

                    RenderedImage I0I1 = SubtractDescriptor.create(processedMasterImage, I1w, null);
                    RenderedImage P1 = MultiplyDescriptor.create(u, Ix, null);
                    RenderedImage P2 = MultiplyDescriptor.create(v, Iy, null);
                    RenderedImage It = AddDescriptor.create(AddDescriptor.create(I0I1, P1, null), P2, null);

                    RenderedImage G = MultiplyDescriptor.create(It, Ix, null);
                    RenderedImage H = MultiplyDescriptor.create(It, Iy, null);

                    G = doubleConvolve(G, fen);
                    H = doubleConvolve(H, fen);

                    RenderedImage D = SubtractDescriptor.create(
                            MultiplyDescriptor.create(A, B, null),
                            MultiplyDescriptor.create(C, C, null), null);


                    u = DivideDescriptor.create(
                            SubtractDescriptor.create(
                                    MultiplyDescriptor.create(G, B, null),
                                    MultiplyDescriptor.create(C, H, null),
                                    null),
                            D, null);
                    v = DivideDescriptor.create(
                            SubtractDescriptor.create(
                                    MultiplyDescriptor.create(A, H, null),
                                    MultiplyDescriptor.create(C, G, null),
                                    null),
                            D, null);
                }
            }
        }

        RenderedImage targetImage = interpolate(sourceSlaveImage, u, v);

        Band targetBand = new Band(originalSlaveBand.getName(),
                originalSlaveBand.getDataType(),
                originalSlaveBand.getRasterWidth(),
                originalSlaveBand.getRasterHeight());
        targetBand.setSourceImage(targetImage);
        targetProduct.addBand(targetBand);
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
        for (int k = 0; k < level; k++) {
            imagePyramid[k + 1] = pyramBurt(imagePyramid[k]);
        }
        return imagePyramid;
    }


    private RenderedImage pyramBurt(RenderedImage inputImage) {
        int radius = 2;
        RenderedImage borderedImage = addBorder(inputImage, radius, radius, radius, radius);
        RenderedImage convolvedImg = doubleConvolve(borderedImage, burt1D);

        //resize with 1/2 factor:
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        RenderedOp resizeOp = SubsampleAverageDescriptor.create(convolvedImg,
                (Double) 0.5, (Double) 0.5, hints);

        BufferedImage bufferedResizedImage = resizeOp.getAsBufferedImage();

        return bufferedResizedImage;
    }

    private RenderedImage doubleConvolve(RenderedImage input, float[] factor) {
        int kernelSize = factor.length;
        KernelJAI kernel = new KernelJAI(kernelSize, 1, factor);
        RenderedImage convolvedImg = JAI.create("convolve", input, kernel);
        RenderedImage secondConvImg = JAI.create("convolve", convolvedImg, kernel);
        return secondConvImg;
    }

    private BufferedImage imageFromMatrix(int[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        int[] inputArray = new int[rows * cols];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                inputArray[i * cols + j] = input[i][j];
            }
        }
        BufferedImage nImg = new BufferedImage(rows, cols,
                BufferedImage.TYPE_INT_RGB);
        WritableRaster er = nImg.getRaster();
        er.setDataElements(0, 0, inputArray);
        nImg.setData(er);
        return nImg;
    }

    private float[][] matrixFromImage(BufferedImage img) {
        int imgHeight = img.getHeight();
        int imgWidth = img.getWidth();
        short[] values = ((DataBufferUShort) img.getData().getDataBuffer()).getData();
        float[][] imgData = new float[imgHeight][imgWidth];
        for (int i = 0; i < values.length; i++) {
            imgData[i / imgHeight][i % imgHeight] = values[i];
        }
        return imgData;
    }

    private RenderedImage addBorder(RenderedImage inputImage, int left, int right, int top, int bottom) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(inputImage);
        pb.add(left);
        pb.add(right);
        pb.add(top);
        pb.add(bottom);
        pb.add(BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
        return JAI.create("border", pb);
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(CoregistrationOp.class);
        }
    }

    private BufferedImage equalize(BufferedImage src) {
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

    private void restrictBounds(RenderedImage colsImage, RenderedImage rowsImage) {
            /*for (int ii = 0; ii < dx.length; ii++) {
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
                        }*/
    }

    private RenderedImage interpolate(RenderedImage img, RenderedImage dx, RenderedImage dy) {
        int xStep = 1;
        int xNumCells = (int) img.getWidth();
        int yStep = 1;
        int yNumCells = (int) img.getHeight();
        float[] warpPositions = new float[2 * (xNumCells + 1) * (yNumCells + 1)];
        for (int y = 0; y <= yNumCells; y++) {
            for (int x = 0; x <= xNumCells; x++) {
                int pos = 2 * (y * (xNumCells + 1) + x);
                warpPositions[pos] = dx.getData().getSampleFloat(x, y, 0);
                warpPositions[pos + 1] = dy.getData().getSampleFloat(x, y, 0);
            }
        }
        Warp warp = new WarpGrid(0, xStep, xNumCells, 0, yStep, yNumCells, warpPositions);
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(warp);
        pb.add(new InterpolationBilinear());
        return (JAI.create("warp", pb));
    }

    private RenderedImage gradientRow(RenderedImage source) {
        GeneralFilterFunction f = new GeneralFilterFunction(1, 3, 0, 0, null) {
            @Override
            public float filter(float[] fdata) {
                return (fdata[2] - fdata[0]) / 2.0f;
            }
        };
        return new GeneralFilterOpImage(source, null, null, null, f);
    }

    private RenderedImage gradientCol(RenderedImage source) {
        GeneralFilterFunction f = new GeneralFilterFunction(3, 1, 0, 0, null) {
            @Override
            public float filter(float[] fdata) {
                return (fdata[2] - fdata[0]) / 2.0f;
            }
        };
        return new GeneralFilterOpImage(source, null, null, null, f);
    }

    private RenderedImage createConstImage(int width, int height, Integer n) {
        return ConstantDescriptor.create((float) width, (float) height, new Integer[]{n}, null);
    }

    private RenderedImage createMeshRowImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                img.setRGB(i, j, (new Color(i + 1, 0, 0)).getRGB());
            }
        }
        return img;
    }

    private RenderedImage createMeshColImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                img.setRGB(i, j, (new Color(j + 1, 0, 0)).getRGB());
            }
        }
        return img;
    }

    public static void main(String args[]) {
        try {
            Sentinel2L2AProductReader reader = new Sentinel2L2AProductReader(null, "EPSG:32632");
            Product prod1 = reader.readProductNodes(new File("D:\\Sentinel2_PROJECT\\p_down\\S2A_MSIL2A_20170805T102031_N0205_R065_T32TNQ_20170805T102535.SAFE\\MTD_MSIL2A.xml"), null);
            prod1.getBandAt(0).getData();

            LandsatGeotiffReader reader2 = new LandsatGeotiffReader(null);
            Product prod2 = reader2.readProductNodes(new File("D:\\Sentinel2_PROJECT\\p_down\\LC08_L1TP_193029_20170805_20170812_01_T1.tar.gz"), null);
            prod2.getBandAt(0).getData();

            CoregistrationOp op = new CoregistrationOp();
            op.setSourceProducts(prod1, prod2);
            op.initialize();

            op.targetProduct.getBandAt(0).getData();
            //write target product

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
