package org.esa.s2tbx.coregistration;

import org.esa.s2tbx.coregistration.operators.ComputeCompareOp;
import org.esa.s2tbx.coregistration.operators.ImageOperations;
import org.esa.s2tbx.coregistration.operators.ImagePyramidCache;
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

import javax.media.jai.*;
import javax.media.jai.operator.*;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;

/**
 * Coregistration.... [TODO]
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

    @Parameter(label = "Band Index of Master product", defaultValue = "0", description = "Band Index!!!")
    public int masterBandIndex = 0;

    @Parameter(label = "Band Index of Slave product", defaultValue = "0", description = "Band Index!!!")
    public int slaveBandIndex = 0;

    @Parameter(label = "Number of levels", defaultValue = "6", description = "The number of levels to process the images.")
    public int levels = 6;

    @Parameter(label = "Rank number", defaultValue = "4", description = "Value used to compute the rank.")
    public int rank = 4;

    @Parameter(label = "Number of interations", defaultValue = "2", description = "The number of interations for each level and for each radius.")
    public int iterations = 2;

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
        try {
            getLogger().info("Started coregistration of products " + sourceMasterProduct.getName()
                    + "(" + sourceMasterProduct.getSceneRasterWidth() + "X" + sourceMasterProduct.getSceneRasterHeight() + ")"
                    + " and " + sourceSlaveProduct.getName()
                    + "(" + sourceSlaveProduct.getSceneRasterWidth() + "X" + sourceSlaveProduct.getSceneRasterHeight() + ")");
            long startTime = System.currentTimeMillis();
            int levelMaster = sourceMasterProduct.getBandAt(masterBandIndex).getMultiLevelModel().getLevelCount();
            BufferedImage sourceMasterImage = ImageOperations.convertBufferedImage(sourceMasterProduct.getBandAt(masterBandIndex).getSourceImage().getImage(0));
            BufferedImage processedMasterImage = sourceMasterImage;

            Band originalSlaveBand = sourceSlaveProduct.getBandAt(slaveBandIndex);
            int levelSlave = originalSlaveBand.getMultiLevelModel().getLevelCount();
            BufferedImage sourceSlaveImage = ImageOperations.convertBufferedImage(originalSlaveBand.getSourceImage().getImage(0));
            float xFactor = (float) sourceMasterImage.getWidth() / sourceSlaveImage.getWidth();
            float yFactor = (float) sourceMasterImage.getHeight() / sourceSlaveImage.getHeight();
            BufferedImage processedSlaveImage = sourceSlaveImage;
            if (xFactor != 1f || yFactor != 1f) {
                processedSlaveImage = ImageOperations.resize(sourceSlaveImage, xFactor, yFactor,
                        Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
            }
            if (processedMasterImage.getWidth() != processedSlaveImage.getWidth() || processedMasterImage.getHeight() != processedSlaveImage.getHeight()) {
                throw new OperatorException("Slave image dimensions different from master image dimensions. Cannot apply algorithm!");
            }

            if (contrast) {
                processedMasterImage = ImageOperations.applyContrast(sourceMasterImage);
                processedSlaveImage = ImageOperations.applyContrast(sourceSlaveImage);
            }

            ImagePyramidCache pyramidMaster = new ImagePyramidCache(processedMasterImage, levels);
            pyramidMaster.compute();
            ImagePyramidCache pyramidSlave = new ImagePyramidCache(processedSlaveImage, levels);
            pyramidSlave.compute();
            //BufferedImage[] pyramidMaster = pyramid(processedMasterImage, levels);
            //BufferedImage[] pyramidSlave = pyramid(processedSlaveImage, levels);
//        BufferedImage[] pyramidMaster = pyramid2("D:\\Sentinel2_PROJECT\\p_down\\output\\in\\pyram_0_", levels);
//        BufferedImage[] pyramidSlave = pyramid2("D:\\Sentinel2_PROJECT\\p_down\\output\\in\\pyram_1_", levels);

            BufferedImage u = null, v = null, meshRow = null, meshCol = null;

            for (int k = levels; k >= 0; k--) {
                getLogger().info("Start level " + k + " after " + (System.currentTimeMillis() - startTime)/1000f + "sec from start.");
                BufferedImage levelMasterImage = pyramidMaster.getImage(k);
                BufferedImage levelSlaveImage = pyramidSlave.getImage(k);

                ImageOperations.writeImage(levelMasterImage, "D:\\Sentinel2_PROJECT\\p_down\\output\\level_master_" + (k + 1) + ".tif");
                ImageOperations.writeImage(levelSlaveImage, "D:\\Sentinel2_PROJECT\\p_down\\output\\level_slave_" + (k + 1) + ".tif");

                BufferedImage levelMasterImageEq, levelSlaveImageEq;
                if (contrast) {
                    //clahe should be applied directly on BufferedImage, to avoid transferng from/to image
                    levelMasterImageEq = ImageOperations.equalize(levelMasterImage);
                    levelSlaveImageEq = ImageOperations.equalize(levelSlaveImage);
                } else {
                    levelMasterImageEq = levelMasterImage;
                    levelSlaveImageEq = levelSlaveImage;
                }
                //init meshgrid and u/v
                //meshRow = createMeshRowImage(levelMasterImage.getHeight(), levelMasterImage.getWidth());
                //meshCol = createMeshColImage(levelMasterImage.getHeight(), levelMasterImage.getWidth());
                meshRow = ImageOperations.createMeshRowImage(levelMasterImage.getWidth(), levelMasterImage.getHeight());
                meshCol = ImageOperations.createMeshColImage(levelMasterImage.getWidth(), levelMasterImage.getHeight());

                ImageOperations.writeImage(meshRow, "D:\\Sentinel2_PROJECT\\p_down\\output\\meshRow_" + (k + 1) + ".tif");
                ImageOperations.writeImage(meshCol, "D:\\Sentinel2_PROJECT\\p_down\\output\\meshCol_" + (k + 1) + ".tif");

                if (k == levels) {
                    //createMesh(levelMasterImage.getWidth(), levelMasterImage.getHeight(), u, v);
                    //u = createConstImage(levelMasterImage.getHeight(), levelMasterImage.getWidth(), 0);
                    //v = createConstImage(levelMasterImage.getHeight(), levelMasterImage.getWidth(), 0);
                    u = ImageOperations.createConstImage(levelMasterImage.getWidth(), levelMasterImage.getHeight(), 0);
                    v = ImageOperations.createConstImage(levelMasterImage.getWidth(), levelMasterImage.getHeight(), 0);
                } else {
                    u = ImageOperations.resize(u, (float) levelMasterImage.getWidth() / u.getWidth(),
                            (float) levelMasterImage.getHeight() / u.getHeight(), Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
                    u = ImageOperations.rescale(2, 0, u);
                    //u = addBorder(u, 0, levelMasterImage.getWidth() - u.getWidth(), 0, levelMasterImage.getHeight() - u.getHeight());

                    v = ImageOperations.resize(v, (float) levelMasterImage.getWidth() / v.getWidth(),
                            (float) levelMasterImage.getHeight() / v.getHeight(), Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
                    v = ImageOperations.rescale(2, 0, v);
                    //v = addBorder(v, 0, levelMasterImage.getWidth() - v.getWidth(), 0, levelMasterImage.getHeight() - v.getHeight());

                }
                ImageOperations.writeImage(u, "D:\\Sentinel2_PROJECT\\p_down\\output\\U_" + (k + 1) + ".tif");
                ImageOperations.writeImage(v, "D:\\Sentinel2_PROJECT\\p_down\\output\\V_" + (k + 1) + ".tif");

                BufferedImage I0, I1sup, I1inf;

                if (rank != 0) {

                    I0 = ImageOperations.imageFromMatrix(MatrixUtils.rank_sup(ImageOperations.matrixFromImage(levelMasterImage), rank));
                    I1sup = ImageOperations.imageFromMatrix(MatrixUtils.rank_sup(ImageOperations.matrixFromImage(levelSlaveImage), rank));
                    I1inf = ImageOperations.imageFromMatrix(MatrixUtils.rank_inf(ImageOperations.matrixFromImage(levelSlaveImage), rank));
                } else {
                    I0 = levelMasterImage;
                    I1sup = levelSlaveImage;
                    I1inf = ImageOperations.rescale(-1, 1, levelSlaveImage);//TODO invers image

                }

                ImageOperations.writeImage(I0, "D:\\Sentinel2_PROJECT\\p_down\\output\\I0_sup_" + (k + 1) + ".tif");
                ImageOperations.writeImage(I1sup, "D:\\Sentinel2_PROJECT\\p_down\\output\\I1_sup_" + (k + 1) + ".tif");
                ImageOperations.writeImage(I1inf, "D:\\Sentinel2_PROJECT\\p_down\\output\\I1_inf_" + (k + 1) + ".tif");

                BufferedImage Ix = ImageOperations.gradientRow2(I0);
                BufferedImage Iy = ImageOperations.gradientCol2(I0);
                BufferedImage Ixx = MultiplyDescriptor.create(Ix, Ix, null).getAsBufferedImage();
                BufferedImage Iyy = MultiplyDescriptor.create(Iy, Iy, null).getAsBufferedImage();
                BufferedImage Ixy = MultiplyDescriptor.create(Ix, Iy, null).getAsBufferedImage();

                ImageOperations.writeImage(Ix, "D:\\Sentinel2_PROJECT\\p_down\\output\\IX_" + (k + 1) + ".tif");
                ImageOperations.writeImage(Iy, "D:\\Sentinel2_PROJECT\\p_down\\output\\IY_" + (k + 1) + ".tif");
                ImageOperations.writeImage(Ixx, "D:\\Sentinel2_PROJECT\\p_down\\output\\IXX_" + (k + 1) + ".tif");
                ImageOperations.writeImage(Iyy, "D:\\Sentinel2_PROJECT\\p_down\\output\\IYY_" + (k + 1) + ".tif");
                ImageOperations.writeImage(Ixy, "D:\\Sentinel2_PROJECT\\p_down\\output\\IXY_" + (k + 1) + ".tif");


                for (int rad = 0; rad < radArray.length; rad++) {
                    int r = radArray[rad];
                    float[] fen = new float[2 * r + 1];
                    for (int j = 0; j < fen.length; j++) {
                        fen[j] = 1;
                    }
                    BufferedImage A = ImageOperations.doubleConvolve(Ixx, fen);
                    BufferedImage B = ImageOperations.doubleConvolve(Iyy, fen);
                    BufferedImage C = ImageOperations.doubleConvolve(Ixy, fen);

                    ImageOperations.writeImage(A, "D:\\Sentinel2_PROJECT\\p_down\\output\\A_" + (k + 1) + "_" + r + ".tif");
                    ImageOperations.writeImage(B, "D:\\Sentinel2_PROJECT\\p_down\\output\\B_" + (k + 1) + "_" + r + ".tif");
                    ImageOperations.writeImage(C, "D:\\Sentinel2_PROJECT\\p_down\\output\\C_" + (k + 1) + "_" + r + ".tif");

                    for (int j = 0; j < iterations; j++) {
                        ParameterBlock pb = new ParameterBlock();
                        pb.addSource(meshCol);
                        pb.addSource(u);
                        BufferedImage dx = JAI.create("add", pb).getAsBufferedImage();

                        ImageOperations.writeImage(dx, "D:\\Sentinel2_PROJECT\\p_down\\output\\dx_" + (k + 1) + "_" + r + ".tif");

                        pb = new ParameterBlock();
                        pb.addSource(meshRow);
                        pb.addSource(v);
                        BufferedImage dy = JAI.create("add", pb).getAsBufferedImage();

                        ImageOperations.writeImage(dy, "D:\\Sentinel2_PROJECT\\p_down\\output\\dy_" + (k + 1) + "_" + r + ".tif");

                        //TODO suppose width=height
                        dx = ClampDescriptor.create(dx, new double[]{0}, new double[]{dx.getWidth() - 1}, null).getAsBufferedImage();
                        ImageOperations.writeImage(dx, "D:\\Sentinel2_PROJECT\\p_down\\output\\dx_clamp_" + (k + 1) + "_" + r + ".tif");
                        dy = ClampDescriptor.create(dy, new double[]{0}, new double[]{dy.getHeight() - 1}, null).getAsBufferedImage();
                        ImageOperations.writeImage(dy, "D:\\Sentinel2_PROJECT\\p_down\\output\\dy_clamp_" + (k + 1) + "_" + r + ".tif");

                        BufferedImage I1w = ImageOperations.interpolate(I1sup, dx, dy);
                        ImageOperations.writeImage(I1w, "D:\\Sentinel2_PROJECT\\p_down\\output\\I1W_" + (k + 1) + "_" + r + ".tif");

                        if (contrast) {
                            BufferedImage H1w = ImageOperations.interpolate(levelSlaveImageEq, dx, dy);

                            float[] wi = new float[2 * rank + 1];
                            for (int i = 0; i < wi.length; i++) {
                                wi[i] = 1;
                            }
                            double[] wid = new double[2 * rank + 1];
                            for (int i = 0; i < wi.length; i++) {
                                wi[i] = 1;
                            }

                            BufferedImage Hdif = SubtractDescriptor.create(levelMasterImageEq, H1w, null).getAsBufferedImage();//levelMasterImageEq-H1w
                            BufferedImage crit1 = ImageOperations.doubleConvolve(Hdif, wi);
                            BufferedImage Hinvert = SubtractFromConstDescriptor.create(
                                    AddDescriptor.create(levelMasterImageEq, H1w, null).getAsBufferedImage(),
                                    wid, null).getAsBufferedImage();
                            BufferedImage crit2 = ImageOperations.doubleConvolve(Hinvert, wi);
                            BufferedImage I1w_inf = ImageOperations.interpolate(I1inf, dx, dy);
                            ComputeCompareOp compareOp = new ComputeCompareOp(I1w, I1w_inf, crit1, crit2, null, null);
                            I1w = compareOp.getAsBufferedImage();
                        }

                        BufferedImage I0I1 = SubtractDescriptor.create(I0, I1w, null).getAsBufferedImage();
                        BufferedImage P1 = MultiplyDescriptor.create(u, Ix, null).getAsBufferedImage();
                        BufferedImage P2 = MultiplyDescriptor.create(v, Iy, null).getAsBufferedImage();
                        BufferedImage It = AddDescriptor.create(AddDescriptor.create(I0I1, P1, null).getAsBufferedImage(), P2, null).getAsBufferedImage();

                        ImageOperations.writeImage(I0I1, "D:\\Sentinel2_PROJECT\\p_down\\output\\I0I1_" + (k + 1) + "_" + r + ".tif");
                        ImageOperations.writeImage(P1, "D:\\Sentinel2_PROJECT\\p_down\\output\\P1_" + (k + 1) + "_" + r + ".tif");
                        ImageOperations.writeImage(P2, "D:\\Sentinel2_PROJECT\\p_down\\output\\P2_" + (k + 1) + "_" + r + ".tif");
                        ImageOperations.writeImage(It, "D:\\Sentinel2_PROJECT\\p_down\\output\\It_" + (k + 1) + "_" + r + ".tif");

                        BufferedImage G = MultiplyDescriptor.create(It, Ix, null).getAsBufferedImage();
                        BufferedImage H = MultiplyDescriptor.create(It, Iy, null).getAsBufferedImage();

                        ImageOperations.writeImage(G, "D:\\Sentinel2_PROJECT\\p_down\\output\\G_" + (k + 1) + "_" + r + ".tif");
                        ImageOperations.writeImage(H, "D:\\Sentinel2_PROJECT\\p_down\\output\\H_" + (k + 1) + "_" + r + ".tif");

                        G = ImageOperations.doubleConvolve(G, fen);
                        H = ImageOperations.doubleConvolve(H, fen);

                        ImageOperations.writeImage(G, "D:\\Sentinel2_PROJECT\\p_down\\output\\G_conv_" + (k + 1) + "_" + r + ".tif");
                        ImageOperations.writeImage(H, "D:\\Sentinel2_PROJECT\\p_down\\output\\H_conv_" + (k + 1) + "_" + r + ".tif");


                        BufferedImage D = SubtractDescriptor.create(
                                MultiplyDescriptor.create(A, B, null).getAsBufferedImage(),
                                MultiplyDescriptor.create(C, C, null).getAsBufferedImage(), null).getAsBufferedImage();

                        ImageOperations.writeImage(D, "D:\\Sentinel2_PROJECT\\p_down\\output\\D_" + (k + 1) + "_" + r + ".tif");

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

                        //NoDataReplacerOpImage.register(JAI.getDefaultInstance());

                        u = ImageOperations.replace(u, Float.NaN, 0.0f);
                        v = ImageOperations.replace(v, Float.NaN, 0.0f);

                        ImageOperations.writeImage(u, "D:\\Sentinel2_PROJECT\\p_down\\output\\U_" + (k + 1) + "_" + r + ".tif");
                        ImageOperations.writeImage(v, "D:\\Sentinel2_PROJECT\\p_down\\output\\V_" + (k + 1) + "_" + r + ".tif");

                    }
                }
            }

            ParameterBlock pb = new ParameterBlock();
            pb.addSource(meshCol);
            pb.addSource(u);
            BufferedImage dx = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();

            pb = new ParameterBlock();
            pb.addSource(meshRow);
            pb.addSource(v);
            BufferedImage dy = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();

            BufferedImage targetImage = ImageOperations.interpolate(processedSlaveImage, dx, dy);
            ImageOperations.writeImage(targetImage, "D:\\Sentinel2_PROJECT\\p_down\\output\\targetImage.tif");

            xFactor = (float) sourceSlaveImage.getWidth() / targetImage.getWidth();
            yFactor = (float) sourceSlaveImage.getHeight() / targetImage.getHeight();
            if (xFactor != 1f || yFactor != 1f) {
                targetImage = ImageOperations.resize(targetImage, xFactor, yFactor,
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

            getLogger().info("Finished coregistration in ");
        }catch (Exception ex){
            throw new OperatorException("Error on coregistration processing (possibly too large images) : " + ex.getMessage());
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(CoregistrationOp.class);
        }
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
