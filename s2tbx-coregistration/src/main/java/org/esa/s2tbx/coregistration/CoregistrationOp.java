package org.esa.s2tbx.coregistration;

import com.bc.ceres.jai.GeneralFilterFunction;
import com.bc.ceres.jai.opimage.GeneralFilterOpImage;
import org.esa.s2tbx.coregistration.operators.ComputeCompareOp;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ArrayUtils;

import javax.imageio.ImageIO;
import javax.media.jai.*;
import javax.media.jai.operator.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.awt.image.DataBufferFloat;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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

    @Parameter(label = "Band Index", defaultValue = "0", description = "Band Index!!!")
    public int bandIndex = 0;

    @Parameter(label = "Number of levels", defaultValue = "6", description = "The number of levels to process the images.")
    public int levels = 6;

    @Parameter(label = "Rank number", defaultValue = "4", description = "Value used to compute the rank.")
    public int rank = 4;

    @Parameter(label = "Number of interations", defaultValue = "2", description = "The number of interations for each level and for each radius.")
    public int iterations = 2;

    private static final float[] burt1D = new float[]{0.05f, 0.25f, 0.4f, 0.25f, 0.05f};

    @Parameter(label = "Radius values", defaultValue = "32, 28, 24, 20, 16, 12, 8", description = "The radius integer values splitted by comma.")
    public static final String radius = "32, 28, 24, 20, 16, 12, 8";

    @Parameter(label = "Temp save location", defaultValue = "D:\\Sentinel2_PROJECT\\p_down\\output", description = "....")
    public static final String saveLocation = "D:\\Sentinel2_PROJECT\\p_down\\output";


    public CoregistrationOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int[] radArray;
        try {
            radArray = Arrays.stream(radius.split(","))
                    .map(String::trim).mapToInt(Integer::parseInt).toArray();
        }catch (Exception ex){
            throw new OperatorException("Radius parameter not valid. Provide a list of integer values separated by comma!");
        }
        if(radArray != null && radArray.length > 0) {
            targetProduct = new Product("coregistered_" + slaveProduct.getName(),
            slaveProduct.getProductType(),
            slaveProduct.getSceneRasterWidth(),
            slaveProduct.getSceneRasterHeight());
            targetProduct.setDescription(slaveProduct.getDescription());
            targetProduct.setSceneGeoCoding(slaveProduct.getSceneGeoCoding());
            doExecute(masterProduct, slaveProduct, radArray);
        }
    }

    public void doExecute(Product sourceMasterProduct, Product sourceSlaveProduct, int[] radArray) {
        //IMPROVEMENT: if necessary, JAI dithering operation compresses the three bands of an RGB image to a single-banded byte image.
        long startTime = System.currentTimeMillis();
        int levelMaster = sourceMasterProduct.getBandAt(bandIndex).getMultiLevelModel().getLevelCount();
        BufferedImage sourceMasterImage = convertBufferedImage(sourceMasterProduct.getBandAt(bandIndex).getSourceImage().getImage(0));
        BufferedImage processedMasterImage = sourceMasterImage;

        Band originalSlaveBand = sourceSlaveProduct.getBandAt(bandIndex);
        int levelSlave = originalSlaveBand.getMultiLevelModel().getLevelCount();
        BufferedImage sourceSlaveImage = convertBufferedImage(originalSlaveBand.getSourceImage().getImage(0));
        float xFactor = (float) sourceMasterImage.getWidth() / sourceSlaveImage.getWidth();
        float yFactor = (float) sourceMasterImage.getWidth() / sourceSlaveImage.getWidth();
        BufferedImage processedSlaveImage = sourceSlaveImage;
        if(xFactor != 1f || yFactor != 1f) {
            processedSlaveImage = resize(sourceSlaveImage, xFactor, yFactor,
                    Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
        }
        if(processedMasterImage.getWidth() != processedSlaveImage.getWidth() || processedMasterImage.getHeight() != processedSlaveImage.getHeight()){
            throw new OperatorException("Slave image dimensions different from master image dimensions. Cannot apply algorithm!");
        }

        if (contrast) {
            processedMasterImage = applyContrast(sourceMasterImage);
            processedSlaveImage = applyContrast(sourceSlaveImage);
        }

        BufferedImage[] pyramidMaster = pyramid(processedMasterImage, levels);
        BufferedImage[] pyramidSlave = pyramid(processedSlaveImage, levels);
//        BufferedImage[] pyramidMaster = pyramid2("D:\\Sentinel2_PROJECT\\p_down\\output\\in\\pyram_0_", levels);
//        BufferedImage[] pyramidSlave = pyramid2("D:\\Sentinel2_PROJECT\\p_down\\output\\in\\pyram_1_", levels);

        BufferedImage u = null, v = null, meshRow = null, meshCol = null;

        for (int k = pyramidMaster.length - 1; k >= 0; k--) {
            System.out.println(System.currentTimeMillis() - startTime);
            BufferedImage levelMasterImage = pyramidMaster[k];
            BufferedImage levelSlaveImage = pyramidSlave[k];

            writeImage(levelMasterImage, "D:\\Sentinel2_PROJECT\\p_down\\output\\level_master_" + (k + 1) + ".tif");
            writeImage(levelSlaveImage, "D:\\Sentinel2_PROJECT\\p_down\\output\\level_slave_" + (k + 1) + ".tif");

            BufferedImage levelMasterImageEq, levelSlaveImageEq;
            if (contrast) {
                //clahe should be applied directly on BufferedImage, to avoid transferng from/to image
                levelMasterImageEq = equalize(convertBufferedImage(levelMasterImage));
                levelSlaveImageEq = equalize(convertBufferedImage(levelSlaveImage));
            } else {
                levelMasterImageEq = levelMasterImage;
                levelSlaveImageEq = levelSlaveImage;
            }
            //init meshgrid and u/v
            meshRow = createMeshRowImage(levelMasterImage.getHeight(), levelMasterImage.getWidth());
            meshCol = createMeshColImage(levelMasterImage.getHeight(), levelMasterImage.getWidth());

            writeImage(meshRow, "D:\\Sentinel2_PROJECT\\p_down\\output\\meshRow_" + (k + 1) + ".tif");
            writeImage(meshCol, "D:\\Sentinel2_PROJECT\\p_down\\output\\meshCol_" + (k + 1) + ".tif");

            if (k == pyramidMaster.length - 1) {
                //createMesh(levelMasterImage.getWidth(), levelMasterImage.getHeight(), u, v);
                u = createConstImage(levelMasterImage.getHeight(), levelMasterImage.getWidth(), 0);
                v = createConstImage(levelMasterImage.getHeight(), levelMasterImage.getWidth(), 0);
            } else {
                u = resize(u, (float) levelMasterImage.getWidth() / u.getWidth(),
                        (float) levelMasterImage.getHeight() / u.getHeight(), Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
                u = rescale(2, 0, u);
                //u = addBorder(u, 0, levelMasterImage.getWidth() - u.getWidth(), 0, levelMasterImage.getHeight() - u.getHeight());

                v = resize(v, (float) levelMasterImage.getWidth() / v.getWidth(),
                        (float) levelMasterImage.getHeight() / v.getHeight(), Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
                v = rescale(2, 0, v);
                //v = addBorder(v, 0, levelMasterImage.getWidth() - v.getWidth(), 0, levelMasterImage.getHeight() - v.getHeight());

            }
            writeImage(u, "D:\\Sentinel2_PROJECT\\p_down\\output\\U_" + (k + 1) + ".tif");
            writeImage(v, "D:\\Sentinel2_PROJECT\\p_down\\output\\V_" + (k + 1) + ".tif");

            BufferedImage I0, I1sup, I1inf;

            if (rank != 0) {

                I0 = imageFromMatrix(MatrixUtils.rank_sup(matrixFromImage((BufferedImage) levelMasterImage), rank));
                I1sup = imageFromMatrix(MatrixUtils.rank_sup(matrixFromImage((BufferedImage) levelSlaveImage), rank));
                I1inf = imageFromMatrix(MatrixUtils.rank_inf(matrixFromImage((BufferedImage) levelSlaveImage), rank));
            } else {
                I0 = levelMasterImage;
                I1sup = levelSlaveImage;
                I1inf = rescale(-1, 1, levelSlaveImage);//TODO invers image

            }

            writeImage(I0, "D:\\Sentinel2_PROJECT\\p_down\\output\\I0_sup_" + (k + 1) + ".tif");
            writeImage(I1sup, "D:\\Sentinel2_PROJECT\\p_down\\output\\I1_sup_" + (k + 1) + ".tif");
            writeImage(I1inf, "D:\\Sentinel2_PROJECT\\p_down\\output\\I1_inf_" + (k + 1) + ".tif");

            BufferedImage Ix = gradientRow2(I0);
            BufferedImage Iy = gradientCol2(I0);
            BufferedImage Ixx = MultiplyDescriptor.create(Ix, Ix, null).getAsBufferedImage();
            BufferedImage Iyy = MultiplyDescriptor.create(Iy, Iy, null).getAsBufferedImage();
            BufferedImage Ixy = MultiplyDescriptor.create(Ix, Iy, null).getAsBufferedImage();

            writeImage(Ix, "D:\\Sentinel2_PROJECT\\p_down\\output\\IX_" + (k + 1) + ".tif");
            writeImage(Iy, "D:\\Sentinel2_PROJECT\\p_down\\output\\IY_" + (k + 1) + ".tif");
            writeImage(Ixx, "D:\\Sentinel2_PROJECT\\p_down\\output\\IXX_" + (k + 1) + ".tif");
            writeImage(Iyy, "D:\\Sentinel2_PROJECT\\p_down\\output\\IYY_" + (k + 1) + ".tif");
            writeImage(Ixy, "D:\\Sentinel2_PROJECT\\p_down\\output\\IXY_" + (k + 1) + ".tif");


            for (int rad = 0; rad < radArray.length; rad++) {
                int r = radArray[rad];
                float[] fen = new float[2 * r + 1];
                for (int j = 0; j < fen.length; j++) {
                    fen[j] = 1;
                }
                BufferedImage A = doubleConvolve(Ixx, fen);
                BufferedImage B = doubleConvolve(Iyy, fen);
                BufferedImage C = doubleConvolve(Ixy, fen);

                writeImage(A, "D:\\Sentinel2_PROJECT\\p_down\\output\\A_" + (k + 1) + "_" + r + ".tif");
                writeImage(B, "D:\\Sentinel2_PROJECT\\p_down\\output\\B_" + (k + 1) + "_" + r + ".tif");
                writeImage(C, "D:\\Sentinel2_PROJECT\\p_down\\output\\C_" + (k + 1) + "_" + r + ".tif");

                for (int j = 0; j < iterations; j++) {
                    ParameterBlock pb = new ParameterBlock();
                    pb.addSource(meshCol);
                    pb.addSource(u);
                    BufferedImage dx = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();

                    writeImage(dx, "D:\\Sentinel2_PROJECT\\p_down\\output\\dx_" + (k + 1) + "_" + r + ".tif");

                    pb = new ParameterBlock();
                    pb.addSource(meshRow);
                    pb.addSource(v);
                    BufferedImage dy = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();

                    writeImage(dy, "D:\\Sentinel2_PROJECT\\p_down\\output\\dy_" + (k + 1) + "_" + r + ".tif");

                    //TODO suppose width=height
                    dx = ClampDescriptor.create(dx, new double[]{0}, new double[]{dx.getWidth() - 1}, null).getAsBufferedImage();
                    writeImage(dx, "D:\\Sentinel2_PROJECT\\p_down\\output\\dx_clamp_" + (k + 1) + "_" + r + ".tif");
                    dy = ClampDescriptor.create(dy, new double[]{0}, new double[]{dy.getHeight() - 1}, null).getAsBufferedImage();
                    writeImage(dy, "D:\\Sentinel2_PROJECT\\p_down\\output\\dy_clamp_" + (k + 1) + "_" + r + ".tif");

                    BufferedImage I1w = interpolate(I1sup, dx, dy);
                    writeImage(I1w, "D:\\Sentinel2_PROJECT\\p_down\\output\\I1W_" + (k + 1) + "_" + r + ".tif");

                    if (contrast) {
                        BufferedImage H1w = interpolate(levelSlaveImageEq, dx, dy);

                        float[] wi = new float[2 * rank + 1];
                        for (int i = 0; i < wi.length; i++) {
                            wi[i] = 1;
                        }
                        double[] wid = new double[2 * rank + 1];
                        for (int i = 0; i < wi.length; i++) {
                            wi[i] = 1;
                        }

                        BufferedImage Hdif = SubtractDescriptor.create(levelMasterImageEq, H1w, null).getAsBufferedImage();//levelMasterImageEq-H1w
                        BufferedImage crit1 = doubleConvolve(Hdif, wi);
                        BufferedImage Hinvert = SubtractFromConstDescriptor.create(
                                AddDescriptor.create(levelMasterImageEq, H1w, null).getAsBufferedImage(),
                                wid, null).getAsBufferedImage();
                        BufferedImage crit2 = doubleConvolve(Hinvert, wi);
                        BufferedImage I1w_inf = interpolate(I1inf, dx, dy);
                        ComputeCompareOp compareOp = new ComputeCompareOp(I1w, I1w_inf, crit1, crit2, null, null);
                        I1w = compareOp.getAsBufferedImage();
                    }

                    BufferedImage I0I1 = SubtractDescriptor.create(I0, I1w, null).getAsBufferedImage();
                    BufferedImage P1 = MultiplyDescriptor.create(u, Ix, null).getAsBufferedImage();
                    BufferedImage P2 = MultiplyDescriptor.create(v, Iy, null).getAsBufferedImage();
                    BufferedImage It = AddDescriptor.create(AddDescriptor.create(I0I1, P1, null).getAsBufferedImage(), P2, null).getAsBufferedImage();

                    writeImage(I0I1, "D:\\Sentinel2_PROJECT\\p_down\\output\\I0I1_" + (k + 1) + "_" + r + ".tif");
                    writeImage(P1, "D:\\Sentinel2_PROJECT\\p_down\\output\\P1_" + (k + 1) + "_" + r + ".tif");
                    writeImage(P2, "D:\\Sentinel2_PROJECT\\p_down\\output\\P2_" + (k + 1) + "_" + r + ".tif");
                    writeImage(It, "D:\\Sentinel2_PROJECT\\p_down\\output\\It_" + (k + 1) + "_" + r + ".tif");

                    BufferedImage G = MultiplyDescriptor.create(It, Ix, null).getAsBufferedImage();
                    BufferedImage H = MultiplyDescriptor.create(It, Iy, null).getAsBufferedImage();

                    writeImage(G, "D:\\Sentinel2_PROJECT\\p_down\\output\\G_" + (k + 1) + "_" + r + ".tif");
                    writeImage(H, "D:\\Sentinel2_PROJECT\\p_down\\output\\H_" + (k + 1) + "_" + r + ".tif");

                    G = doubleConvolve(G, fen);
                    H = doubleConvolve(H, fen);

                    writeImage(G, "D:\\Sentinel2_PROJECT\\p_down\\output\\G_conv_" + (k + 1) + "_" + r + ".tif");
                    writeImage(H, "D:\\Sentinel2_PROJECT\\p_down\\output\\H_conv_" + (k + 1) + "_" + r + ".tif");


                    BufferedImage D = SubtractDescriptor.create(
                            MultiplyDescriptor.create(A, B, null).getAsBufferedImage(),
                            MultiplyDescriptor.create(C, C, null).getAsBufferedImage(), null).getAsBufferedImage();

                    writeImage(D, "D:\\Sentinel2_PROJECT\\p_down\\output\\D_" + (k + 1) + "_" + r + ".tif");

                    u = DivideDescriptor.create(
                            SubtractDescriptor.create(
                                    MultiplyDescriptor.create(G, B, null).getAsBufferedImage(),
                                    MultiplyDescriptor.create(C, H, null).getAsBufferedImage(),
                                    null).getAsBufferedImage(),
                            D, null).getAsBufferedImage();
                    v = DivideDescriptor.create(
                            SubtractDescriptor.create(
                                    MultiplyDescriptor.create(A, H, null).getAsBufferedImage(),
                                    MultiplyDescriptor.create(C, G, null).getAsBufferedImage(),
                                    null).getAsBufferedImage(),
                            D, null).getAsBufferedImage();

                    writeImage(u, "D:\\Sentinel2_PROJECT\\p_down\\output\\U_" + (k + 1) + "_" + r + ".tif");
                    writeImage(v, "D:\\Sentinel2_PROJECT\\p_down\\output\\V_" + (k + 1) + "_" + r + ".tif");

                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(meshCol);
        pb.addSource(u);
        BufferedImage dx = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();

        pb = new ParameterBlock();
        pb.addSource(meshRow);
        pb.addSource(v);
        BufferedImage dy = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();

        BufferedImage targetImage = interpolate(sourceSlaveImage, dx, dy);
        writeImage(targetImage, "D:\\Sentinel2_PROJECT\\p_down\\output\\targetImage.tif");

        xFactor = sourceSlaveImage.getWidth() / targetImage.getWidth();
        yFactor = sourceSlaveImage.getWidth() / targetImage.getWidth();
        if(xFactor != 1f || yFactor != 1f) {
            targetImage = resize(targetImage, xFactor, yFactor,
                    Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
        }

        Band targetBand = new Band(originalSlaveBand.getName(),
                ProductData.TYPE_FLOAT32,
                originalSlaveBand.getRasterWidth(),
                originalSlaveBand.getRasterHeight());
        targetBand.setSourceImage(targetImage);
        targetProduct.addBand(targetBand);

        /*try {
            GeoTiffProductWriter writer = new GeoTiffProductWriter(new GeoTiffProductWriterPlugIn());
            writer.writeProductNodes(targetProduct, new File("D:\\Sentinel2_PROJECT\\p_down\\output\\landsat1000proc.tif"));

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        System.out.println("FINISH");
    }

    private BufferedImage applyContrast(BufferedImage inputImage) {
        final BufferedImage extrema = ExtremaDescriptor.create(inputImage, null, 1, 1, false, 1, null).getAsBufferedImage();
        double[][] minMax = (double[][]) extrema.getProperty("Extrema");
        double min = minMax[0][0];
        double max = minMax[1][0];
        double dif = max - min;

        return rescale(1.0 / (max - min), min / (max - min), inputImage);
    }

    private BufferedImage[] pyramid(BufferedImage inputImage, int level) {
        BufferedImage[] imagePyramid = new BufferedImage[level + 1];
        imagePyramid[0] = inputImage;
        for (int k = 0; k < level; k++) {
            imagePyramid[k + 1] = pyramBurt(imagePyramid[k]);
        }
        return imagePyramid;
    }

    //TODO only for tests
    private BufferedImage[] pyramid2(String prefix, int level) {
        BufferedImage[] imagePyramid = new BufferedImage[level + 1];

        for (int k = 0; k <= level; k++) {
//            try {
//                GeoTiffProductReader reader = new GeoTiffProductReader(new GeoTiffProductReaderPlugIn());
//                Product prod;
//                prod = reader.readProductNodes(new File(prefix + (k + 1) + ".tif"), null);
//                imagePyramid[k] = convertBufferedImage(prod.getBandAt(0).getSourceImage().getImage(0));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return imagePyramid;
    }


    private BufferedImage pyramBurt(BufferedImage inputImage) {
        BufferedImage convolvedImg = doubleConvolve(inputImage, burt1D);

        //resize with 1/2 factor:
        /*RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        RenderedOp resizeOp = SubsampleAverageDescriptor.create(convolvedImg,
                (Double) 0.5, (Double) 0.5, hints);

        BufferedImage bufferedResizedImage = resizeOp.getAsBufferedImage();
        */
        float[][] resized = MatrixUtils.subsample(matrixFromImage(convolvedImg));
        return imageFromMatrix(resized);
    }

    private BufferedImage doubleConvolve(BufferedImage input, float[] factor) {
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

    private BufferedImage imageFromMatrix(float[][] input) {
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

    private BufferedImage imageFromArray(float[] input, int width, int height) {
        SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, width, height, 1, width, new int[]{0});
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_FLOAT);
        DataBuffer buffer = new DataBufferFloat(width * height * 1);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);
        raster.setPixels(0, 0, width, height, input);
        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }

    private float[][] matrixFromImage(BufferedImage img) {
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

    private BufferedImage addBorder(BufferedImage inputImage, int left, int right, int top, int bottom) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(inputImage);
        pb.add(left);
        pb.add(right);
        pb.add(top);
        pb.add(bottom);
        pb.add(BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
        return JAI.create("border", pb).getAsBufferedImage();
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

    private BufferedImage convertBufferedImage(RenderedImage img) {
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
    private BufferedImage interpolate4(BufferedImage img, BufferedImage dy, BufferedImage dx) {
        assert img.getWidth() == dx.getWidth();
        assert img.getHeight() == dx.getHeight();
        assert img.getWidth() == dy.getWidth();
        assert img.getHeight() == dy.getHeight();

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

    private BufferedImage interpolate(BufferedImage img, BufferedImage dx, BufferedImage dy) {
        float[][] mimg = matrixFromImage(img);
        float[][] mdx = matrixFromImage(dx);
        float[][] mdy = matrixFromImage(dy);

        float[][] result = MatrixUtils.interp2(mimg, mdx, mdy);

        BufferedImage rimg = imageFromMatrix(result);

        return rimg;
    }

    private BufferedImage interpolate() {

        float[][] A = new float[3][3];
        float[][] x = new float[3][3];
        float[][] y = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                A[i][j] = i * 3 + j + 1;
                x[i][j] = i;
                y[i][j] = j;
            }
        }

        int xStep = 1;
        int xNumCells = 3;
        int yStep = 1;
        int yNumCells = 3;
        float[] warpPositions = new float[2 * (xNumCells + 1) * (yNumCells + 1)];
        warpPositions[0] = 0;
        warpPositions[1] = 0;
        warpPositions[2] = 1;
        warpPositions[3] = 0;
        warpPositions[4] = 2;
        warpPositions[5] = 0;
        warpPositions[6] = 2;
        warpPositions[7] = 0;
        warpPositions[8] = 0;
        warpPositions[9] = 1;
        warpPositions[10] = 1;
        warpPositions[11] = 1;
        warpPositions[12] = 2;
        warpPositions[13] = 1;
        warpPositions[14] = 2;
        warpPositions[15] = 1;
        warpPositions[16] = 0;
        warpPositions[17] = 2;
        warpPositions[18] = 1;
        warpPositions[19] = 2;
        warpPositions[20] = 2;
        warpPositions[21] = 2;
        warpPositions[22] = 2;
        warpPositions[23] = 2;
        warpPositions[24] = 0;
        warpPositions[25] = 2;
        warpPositions[26] = 1;
        warpPositions[27] = 2;
        warpPositions[28] = 2;
        warpPositions[29] = 2;
        warpPositions[30] = 2;
        warpPositions[31] = 3;


        Warp warp = new WarpGrid(0, xStep, xNumCells, 0, yStep, yNumCells, warpPositions);
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(imageFromMatrix(A));
        pb.add(warp);
        pb.add(new InterpolationBilinear());
        BufferedImage result = JAI.create("warp", pb).getAsBufferedImage();


        if (result.getData().getWidth() != result.getData().getHeight()) {
            int t = 9;
        }
        return result;
    }

    private BufferedImage gradientCol(BufferedImage source) {
        GeneralFilterFunction f = new GeneralFilterFunction(1, 3, 0, 0, null) {
            @Override
            public float filter(float[] fdata) {
                return (fdata[2] - fdata[0]) / 2.0f;
            }
        };
        return new GeneralFilterOpImage(source, null, null, null, f).getAsBufferedImage();
    }

    private BufferedImage gradientRow(BufferedImage source) {
        GeneralFilterFunction f = new GeneralFilterFunction(3, 1, 0, 0, null) {
            @Override
            public float filter(float[] fdata) {
                return (fdata[2] - fdata[0]) / 2.0f;
            }
        };
        return new GeneralFilterOpImage(source, null, null, null, f).getAsBufferedImage();
    }

    private BufferedImage gradientRow2(BufferedImage source) {
        return imageFromMatrix(MatrixUtils.gradientrow(matrixFromImage(source)));
    }

    private BufferedImage gradientCol2(BufferedImage source) {
        return imageFromMatrix(MatrixUtils.gradientcol(matrixFromImage(source)));
    }

    private BufferedImage createConstImage(int width, int height, Integer n) {
        return ConstantDescriptor.create((float) width, (float) height, new Integer[]{n}, null).getAsBufferedImage();
    }

    private BufferedImage createMeshColImage(int width, int height) {
        float[] values = new float[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                values[i * height + j] = j;
            }
        }
        return imageFromArray(values, width, height);
    }

    private BufferedImage createMeshRowImage(int width, int height) {
        float[] values = new float[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                values[i * height + j] = i;
            }
        }
        return imageFromArray(values, width, height);
    }

    private BufferedImage rescale(double multiplyFactor, double addFactor, BufferedImage sourceImage) {
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

    private void writeImage(BufferedImage img, String fileLocation) {
        File fileImg = new File(fileLocation);
        try {
            ImageIO.write(img, "tif", fileImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage resize(BufferedImage sourceImage, float rescaleXfactor, float rescaleYfactor, Interpolation interp) {
        return ScaleDescriptor.create(sourceImage, rescaleXfactor, rescaleYfactor, 0.0f, 0.0f, interp, null).getAsBufferedImage();
    }

//    public static void main(String args[]) {
//        try {
//            //interpolate();
//            int length = 200;
//            float[][] A = new float[length][length];
//            float[][] x = new float[length][length];
//            float[][] y = new float[length][length];
//            for (int i = 0; i < length; i++) {
//                for (int j = 0; j < length; j++) {
//                    A[i][j] = i * length + j + 1;
//                    x[i][j] = j;
//                    y[i][j] = i;
//                }
//            }
//            //MatrixUtils.rank_inf(A, 1);
//            BufferedImage result = interpolate(imageFromMatrix(A), imageFromMatrix(x), imageFromMatrix(y));
//            int i = 9;
//            /*float[] values = new float[25];
//            float[] x = new float[25];
//            float[] y = new float[25];
//            for(int i=0;i<25;i++){
//                values[i]=i+1;
//                x[i]=i/5;
//                y[i]=i%5;
//            }
//            BufferedImage ri = interpolate(imageFromArray(values, 5, 5), imageFromArray(x, 5, 5), imageFromArray(y, 5, 5));
//            ri.getData();*/
//
//            /*File file = new File("D:\\temp\\SBC_unp-907-13.tif");
//            SeekableStream s = new FileSeekableStream(file);
//
//            TIFFDecodeParam param = null;
//
//            ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
//
//            NullOpImage op1 =
//                    new NullOpImage(dec.decodeAsBufferedImage(0),
//                            null,
//                            OpImage.OP_IO_BOUND,
//                            null);
//
//            BufferedImage pg1 =  op1.getAsBufferedImage();
//
//            ParameterBlock pb = new ParameterBlock();
//            pb.addSource(pg1);
//            pb.addSource(pg1);
//            BufferedImage dx = (BufferedImage) JAI.create("add", pb);*/
//
//
//            /*Sentinel2L2AProductReader reader = new Sentinel2L2AProductReader(null, "EPSG:32632");
//            Product prod1 = reader.readProductNodes(new File("D:\\Sentinel2_PROJECT\\p_down\\S2A_MSIL2A_20170805T102031_N0205_R065_T32TNQ_20170805T102535.SAFE\\MTD_MSIL2A.xml"), null);
//            prod1.getBandAt(0).getData();
//
//            LandsatGeotiffReader reader2 = new LandsatGeotiffReader(null);
//            Product prod2 = reader2.readProductNodes(new File("D:\\Sentinel2_PROJECT\\p_down\\LC08_L1TP_193029_20170805_20170812_01_T1.tar.gz"), null);
//            prod2.getBandAt(0).getData();*/
//
//            GeoTiffProductReader reader = new GeoTiffProductReader(null);
//            Product prod1 = reader.readProductNodes(new File("D:\\Sentinel2_PROJECT\\p_down\\1000prod\\subset_B2_of_S2A_resampled_resampled1000.tif"), null);
//            GeoTiffProductReader reader2 = new GeoTiffProductReader(null);
//            Product prod2 = reader2.readProductNodes(new File("D:\\Sentinel2_PROJECT\\p_down\\1000prod\\subset_blue_of_landsat_resampled1000.tif"), null);
//
//
////            GeoTiffProductReader reader = new GeoTiffProductReader(new GeoTiffProductReaderPlugIn());
////            Product prod1 = reader.readProductNodes(new File("D:\\Sentinel2_PROJECT\\p_down\\gefolki\\datasets\\radar_bandep.tif"), null);
////            GeoTiffProductReader reader2 = new GeoTiffProductReader(new GeoTiffProductReaderPlugIn());
////            Product prod2 = reader2.readProductNodes(new File("D:\\Sentinel2_PROJECT\\p_down\\gefolki\\datasets\\lidar_georef.tif"), null);
//
//            CoregistrationOp op = new CoregistrationOp();
//            op.setSourceProducts(prod1, prod2);
//            op.initialize();
//
//            op.targetProduct.getBandAt(0).getData();
//            //write target product
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

}
