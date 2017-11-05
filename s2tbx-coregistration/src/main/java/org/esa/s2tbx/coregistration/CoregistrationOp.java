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
import java.awt.image.renderable.RenderedImageFactory;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
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
        } catch (Exception ex) {
            throw new OperatorException("Radius parameter not valid. Provide a list of integer values separated by comma!");
        }
        if (radArray != null && radArray.length > 0) {
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
            float xFactor = (float) processedMasterImage.getWidth() / sourceSlaveImage.getWidth();
            float yFactor = (float) processedMasterImage.getHeight() / sourceSlaveImage.getHeight();
            BufferedImage processedSlaveImage = sourceSlaveImage;
            if (xFactor != 1f || yFactor != 1f) {
                processedSlaveImage = ImageOperations.resize(sourceSlaveImage, xFactor, yFactor,
                        Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
            }
            if (processedMasterImage.getWidth() != processedSlaveImage.getWidth() || processedMasterImage.getHeight() != processedSlaveImage.getHeight()) {
                throw new OperatorException("Slave image dimensions different from master image dimensions. Cannot apply algorithm!");
            }

            if (contrast) {
                processedMasterImage = ImageOperations.applyContrast(processedMasterImage);
                processedSlaveImage = ImageOperations.applyContrast(sourceSlaveImage);
            }

            ImagePyramidCache pyramidMaster = new ImagePyramidCache(processedMasterImage, levels);
            pyramidMaster.compute();
            ImagePyramidCache pyramidSlave = new ImagePyramidCache(processedSlaveImage, levels);

            //BufferedImage[] pyramidMaster = pyramid(processedMasterImage, levels);
            //BufferedImage[] pyramidSlave = pyramid(processedSlaveImage, levels);
            //BufferedImage[] pyramidMaster = pyramid2("D:\\Sentinel2_PROJECT\\p_down\\output\\in\\pyram_0_", levels);
            //BufferedImage[] pyramidSlave = pyramid2("D:\\Sentinel2_PROJECT\\p_down\\output\\in\\pyram_1_", levels);

            for (int k = levels; k >= 0; k--) {
                getLogger().info("Start level " + k + " after " + (System.currentTimeMillis() - startTime) / 1000f + "sec from start.");
                BufferedImage levelMasterImage = pyramidMaster.getImage(k);
                BufferedImage levelSlaveImage = pyramidSlave.getImage(k);
                int width = levelSlaveImage.getWidth();
                int height = levelSlaveImage.getHeight();

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

                WeakReference<BufferedImage> meshRow = new WeakReference<>(ImageOperations.createMeshRowImage(levelMasterImage.getWidth(), levelMasterImage.getHeight()));
                ImagePyramidCache.writeImage(meshRow.get(), "meshRow");
                WeakReference<BufferedImage> meshCol = new WeakReference<>(ImageOperations.createMeshColImage(levelMasterImage.getWidth(), levelMasterImage.getHeight()));
                ImagePyramidCache.writeImage(meshCol.get(), "meshCol");

                //ImageOperations.writeImage(meshRow, "D:\\Sentinel2_PROJECT\\p_down\\output\\meshRow_" + (k + 1) + ".tif");
                //ImageOperations.writeImage(meshCol, "D:\\Sentinel2_PROJECT\\p_down\\output\\meshCol_" + (k + 1) + ".tif");

                SoftReference<RenderedImage> u, v;
                if (k == levels) {
                    //createMesh(levelMasterImage.getWidth(), levelMasterImage.getHeight(), u, v);
                    //u = createConstImage(levelMasterImage.getHeight(), levelMasterImage.getWidth(), 0);
                    //v = createConstImage(levelMasterImage.getHeight(), levelMasterImage.getWidth(), 0);
                    u = new SoftReference(ImageOperations.createConstImage(levelMasterImage.getWidth(), levelMasterImage.getHeight(), 0));
                    v = new SoftReference(ImageOperations.createConstImage(levelMasterImage.getWidth(), levelMasterImage.getHeight(), 0));
                } else {
                    RenderedImage u1 = ImagePyramidCache.readImage("u");
                    u1 = ImageOperations.resize(u1, (float) levelMasterImage.getWidth() / u1.getWidth(),
                            (float) levelMasterImage.getHeight() / u1.getHeight(), Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
                    u1 = ImageOperations.rescale(2, 0, u1);
                    u = new SoftReference(u1);
                    //u = addBorder(u, 0, levelMasterImage.getWidth() - u.getWidth(), 0, levelMasterImage.getHeight() - u.getHeight());

                    RenderedImage v1 = ImagePyramidCache.readImage("v");
                    v1 = ImageOperations.resize(v1, (float) levelMasterImage.getWidth() / v1.getWidth(),
                            (float) levelMasterImage.getHeight() / v1.getHeight(), Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
                    v1 = ImageOperations.rescale(2, 0, v1);
                    v = new SoftReference(v1);
                    //v = addBorder(v, 0, levelMasterImage.getWidth() - v.getWidth(), 0, levelMasterImage.getHeight() - v.getHeight());

                }
                ImagePyramidCache.writeImage(u.get(), "u");
                ImagePyramidCache.writeImage(v.get(), "v");
                //ImageOperations.writeImage(u, "D:\\Sentinel2_PROJECT\\p_down\\output\\U_" + (k + 1) + ".tif");
                //ImageOperations.writeImage(v, "D:\\Sentinel2_PROJECT\\p_down\\output\\V_" + (k + 1) + ".tif");

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

                SoftReference<BufferedImage> Ix = new SoftReference<>(ImageOperations.gradientRow2(I0));
                ImagePyramidCache.writeImage(Ix.get(), "Ix");
                SoftReference<BufferedImage> Iy = new SoftReference<>(ImageOperations.gradientCol2(I0));
                ImagePyramidCache.writeImage(Iy.get(), "Iy");
                WeakReference<RenderedImage> Ixx = new WeakReference<>(MultiplyDescriptor.create(Ix.get(), Ix.get(), null));
                ImagePyramidCache.writeImage(Ixx.get(), "Ixx");
                WeakReference<RenderedImage> Iyy = new WeakReference<>(MultiplyDescriptor.create(Iy.get(), Iy.get(), null));
                ImagePyramidCache.writeImage(Iyy.get(), "Iyy");
                WeakReference<RenderedImage> Ixy = new WeakReference<>(MultiplyDescriptor.create(Ix.get(), Iy.get(), null));
                ImagePyramidCache.writeImage(Ixy.get(), "Ixy");


                /*ImageOperations.writeImage(Ix, "D:\\Sentinel2_PROJECT\\p_down\\output\\IX_" + (k + 1) + ".tif");
                ImageOperations.writeImage(Iy, "D:\\Sentinel2_PROJECT\\p_down\\output\\IY_" + (k + 1) + ".tif");
                ImageOperations.writeImage(Ixx, "D:\\Sentinel2_PROJECT\\p_down\\output\\IXX_" + (k + 1) + ".tif");
                ImageOperations.writeImage(Iyy, "D:\\Sentinel2_PROJECT\\p_down\\output\\IYY_" + (k + 1) + ".tif");
                ImageOperations.writeImage(Ixy, "D:\\Sentinel2_PROJECT\\p_down\\output\\IXY_" + (k + 1) + ".tif");*/


                for (int rad = 0; rad < radArray.length; rad++) {
                    int r = radArray[rad];
                    if (k == 0) {
                        getLogger().info("rad=" + r);
                    }
                    float[] fen = new float[2 * r + 1];
                    for (int j = 0; j < fen.length; j++) {
                        fen[j] = 1;
                    }
                    WeakReference<RenderedImage> Ixxload = new WeakReference<>(ImagePyramidCache.readImage("Ixx"));
                    RenderedImage A = ImageOperations.doubleConvolve(Ixxload.get(), fen);
                    WeakReference<RenderedImage> Iyyload = new WeakReference<>(ImagePyramidCache.readImage("Iyy"));
                    RenderedImage B = ImageOperations.doubleConvolve(Iyyload.get(), fen);
                    WeakReference<RenderedImage> Ixyload = new WeakReference<>(ImagePyramidCache.readImage("Ixy"));
                    RenderedImage C = ImageOperations.doubleConvolve(Ixyload.get(), fen);
                    RenderedImage D = SubtractDescriptor.create(
                            MultiplyDescriptor.create(A, B, null),
                            MultiplyDescriptor.create(C, C, null), null);

                    ImageOperations.writeImage(A, "D:\\Sentinel2_PROJECT\\p_down\\output\\A_" + (k + 1) + "_" + r + ".tif");
                    ImageOperations.writeImage(B, "D:\\Sentinel2_PROJECT\\p_down\\output\\B_" + (k + 1) + "_" + r + ".tif");
                    ImageOperations.writeImage(C, "D:\\Sentinel2_PROJECT\\p_down\\output\\C_" + (k + 1) + "_" + r + ".tif");

                    for (int iter = 0; iter < iterations; iter++) {

                        if (k == 0) {
                            getLogger().info("iter=" + iter);
                        }

                        ParameterBlock pb = new ParameterBlock();
                        pb.addSource(ImagePyramidCache.readImage("meshCol"));
                        pb.addSource(ImagePyramidCache.readImage("u"));
                        RenderedImage dx = ClampDescriptor.create(JAI.create("add", pb), new double[]{0}, new double[]{width - 1}, null);

                        pb = new ParameterBlock();
                        pb.addSource(ImagePyramidCache.readImage("meshRow"));
                        pb.addSource(ImagePyramidCache.readImage("v"));
                        RenderedImage dy = ClampDescriptor.create(JAI.create("add", pb), new double[]{0}, new double[]{height - 1}, null);

                        BufferedImage I1w = ImageOperations.interpolate(I1sup, ((RenderedOp) dx).getAsBufferedImage(), ((RenderedOp) dy).getAsBufferedImage());
                        //ImageOperations.writeImage(I1w, "D:\\Sentinel2_PROJECT\\p_down\\output\\I1W_" + (k + 1) + "_" + r + ".tif");

                        if (contrast) {
                            BufferedImage H1w = ImageOperations.interpolate(levelSlaveImageEq, ((RenderedOp) dx).getAsBufferedImage(),
                                    ((RenderedOp) dy).getAsBufferedImage());

                            float[] wi = new float[2 * rank + 1];
                            for (int i = 0; i < wi.length; i++) {
                                wi[i] = 1;
                            }
                            double[] wid = new double[2 * rank + 1];
                            for (int i = 0; i < wid.length; i++) {
                                wid[i] = 1;
                            }

                            BufferedImage Hdif = SubtractDescriptor.create(levelMasterImageEq, H1w, null).getAsBufferedImage();//levelMasterImageEq-H1w
                            BufferedImage crit1 = ImageOperations.doubleConvolve(Hdif, wi);
                            BufferedImage Hinvert = SubtractFromConstDescriptor.create(
                                    AddDescriptor.create(levelMasterImageEq, H1w, null).getAsBufferedImage(),
                                    wid, null).getAsBufferedImage();
                            BufferedImage crit2 = ImageOperations.doubleConvolve(Hinvert, wi);
                            BufferedImage I1w_inf = ImageOperations.interpolate(I1inf, ((RenderedOp) dx).getAsBufferedImage(),
                                    ((RenderedOp) dy).getAsBufferedImage());
                            ComputeCompareOp compareOp = new ComputeCompareOp(I1w, I1w_inf, crit1, crit2, null, null);
                            I1w = compareOp.getAsBufferedImage();
                        }

                        SoftReference<RenderedImage> I0I1 = new SoftReference(SubtractDescriptor.create(I0, I1w, null));
                        SoftReference<RenderedImage> Ixload = new SoftReference(ImagePyramidCache.readImage("Ix"));
                        WeakReference<RenderedImage> uload = new WeakReference(ImagePyramidCache.readImage("u"));
                        WeakReference<RenderedImage> P1 = new WeakReference(MultiplyDescriptor.create(uload.get(), Ixload.get(), null));
                        SoftReference<RenderedImage> Iyload = new SoftReference(ImagePyramidCache.readImage("Iy"));
                        WeakReference<RenderedImage> vload = new WeakReference(ImagePyramidCache.readImage("v"));
                        WeakReference<RenderedImage> P2 = new WeakReference(MultiplyDescriptor.create(vload.get(), Iyload.get(), null));
                        WeakReference<RenderedImage> It = new WeakReference(AddDescriptor.create(AddDescriptor.create(I0I1.get(), P1.get(), null), P2.get(), null));

                        //ImageOperations.writeImage(I0I1, "D:\\Sentinel2_PROJECT\\p_down\\output\\I0I1_" + (k + 1) + "_" + r + ".tif");
                        //ImageOperations.writeImage(P1, "D:\\Sentinel2_PROJECT\\p_down\\output\\P1_" + (k + 1) + "_" + r + ".tif");
                        //ImageOperations.writeImage(P2, "D:\\Sentinel2_PROJECT\\p_down\\output\\P2_" + (k + 1) + "_" + r + ".tif");
                        //ImageOperations.writeImage(It, "D:\\Sentinel2_PROJECT\\p_down\\output\\It_" + (k + 1) + "_" + r + ".tif");

                        RenderedImage G = MultiplyDescriptor.create(It.get(), Ixload.get(), null);
                        RenderedImage H = MultiplyDescriptor.create(It.get(), Iyload.get(), null);

                        //ImageOperations.writeImage(G, "D:\\Sentinel2_PROJECT\\p_down\\output\\G_" + (k + 1) + "_" + r + ".tif");
                        //ImageOperations.writeImage(H, "D:\\Sentinel2_PROJECT\\p_down\\output\\H_" + (k + 1) + "_" + r + ".tif");

                        G = ImageOperations.doubleConvolve(G, fen);
                        H = ImageOperations.doubleConvolve(H, fen);

                        //ImageOperations.writeImage(G, "D:\\Sentinel2_PROJECT\\p_down\\output\\G_conv_" + (k + 1) + "_" + r + ".tif");
                        //ImageOperations.writeImage(H, "D:\\Sentinel2_PROJECT\\p_down\\output\\H_conv_" + (k + 1) + "_" + r + ".tif");

                        //ImageOperations.writeImage(D, "D:\\Sentinel2_PROJECT\\p_down\\output\\D_" + (k + 1) + "_" + r + ".tif");

                        RenderedImage u1 = DivideDescriptor.create(
                                SubtractDescriptor.create(
                                        MultiplyDescriptor.create(G, B, null),
                                        MultiplyDescriptor.create(C, H, null),
                                        null),
                                D, null);
                        u1 = ImageOperations.replace(u1, Float.NaN, 0.0f);
                        ImagePyramidCache.writeImage(((PlanarImage)u1).getAsBufferedImage(), "u");

                        RenderedImage v1 = DivideDescriptor.create(
                                SubtractDescriptor.create(
                                        MultiplyDescriptor.create(A, H, null).getAsBufferedImage(),
                                        MultiplyDescriptor.create(C, G, null).getAsBufferedImage(),
                                        null).getAsBufferedImage(),
                                D, null).getAsBufferedImage();
                        v1 = ImageOperations.replace(v1, Float.NaN, 0.0f);
                        ImagePyramidCache.writeImage(((PlanarImage)v1).getAsBufferedImage(), "v");

                        Runtime.getRuntime().gc();

                        //ImageOperations.writeImage(u, "D:\\Sentinel2_PROJECT\\p_down\\output\\U_" + (k + 1) + "_" + r + ".tif");
                        //ImageOperations.writeImage(v, "D:\\Sentinel2_PROJECT\\p_down\\output\\V_" + (k + 1) + "_" + r + ".tif");

                    }
                }
            }

            ParameterBlock pb = new ParameterBlock();
            pb.addSource(ImagePyramidCache.readImage("meshCol"));
            pb.addSource(ImagePyramidCache.readImage("u"));
            BufferedImage dx = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();

            pb = new ParameterBlock();
            pb.addSource(ImagePyramidCache.readImage("meshRow"));
            pb.addSource(ImagePyramidCache.readImage("v"));
            BufferedImage dy = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();

            BufferedImage targetImage = ImageOperations.interpolate(processedSlaveImage, dx, dy);
            ImageOperations.writeImage(targetImage, "D:\\Sentinel2_PROJECT\\p_down\\output\\targetImage.tif");

            pyramidMaster.cleanPyramid();
            pyramidSlave.cleanPyramid();

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

            getLogger().info("Finished coregistration in " + (System.currentTimeMillis() - startTime) / 1000f + "sec");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OperatorException("Error on coregistration processing (possibly too large images) : " + ex.getMessage());
        } finally {
            try {
                ImagePyramidCache.cleanLocation();
            } catch (Exception ex) {
                getLogger().warning("During coregistration operation, temporary cache folder was not deleted!");
            }
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
