package org.esa.s2tbx.coregistration;

import com.bc.ceres.core.ProgressMonitor;
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
import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;

/**
 * Coregistration operation based on rasters.
 *
 * @author Ramona Manda
 * @since 6.0.0
 */
@OperatorMetadata(
        alias = "CoregistrationOp",
        version = "1.0",
        category = "Raster/Geometric",
        description = "Coregisters two rasters, not considering their location",
        authors = "Ramona M",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class CoregistrationOp extends Operator {

    /*
    Change alias to expected value of GraphBuilder
     */
//    @SourceProduct(alias = "Master", description = "The source product which serves as master.")
    @SourceProduct(alias = "sourceProduct", description = "The source product which serves as master.", label = "Master")
    private Product masterProduct;

    /*
    Change alias to expected value of GraphBuilder
     */
//    @SourceProduct(alias = "Slave", description = "The source product which serves as slave.")
    @SourceProduct(alias = "sourceProduct.1", description = "The source product which serves as slave.", label = "Slave")
    private Product slaveProduct;

    @TargetProduct(description = "The target product which will use the master's location.")
    private Product targetProduct;

    @Parameter(label = "Master band", description = "The master product band", rasterDataNodeType = Band.class)
    private String masterSourceBand;

    //@Parameter(label = "Slave band", description = "The band...", valueSet = {""})
    @Parameter(label = "Slave band", description = "The slave product band", rasterDataNodeType = Band.class)
    private String slaveSourceBand;

    private boolean contrast = false;

    @Parameter(label = "Number of levels", defaultValue = "6", description = "The number of levels to process the images.")
    private int levels = 6;

    @Parameter(label = "Rank number", defaultValue = "4", description = "Value used to compute the rank.")
    private int rank = 4;

    @Parameter(label = "Number of interations", defaultValue = "2", description = "The number of interations for each level and for each radius.")
    private int iterations = 2;

    @Parameter(label = "Radius values", defaultValue = "32, 28, 24, 20, 16, 12, 8", description = "The radius integer values splitted by comma.")
    private String radius = "32, 28, 24, 20, 16, 12, 8";

    public String hiddenSlaveBand;
    private int[] radArray;

    public CoregistrationOp() {
    }

    @Override
    public Dimension ensureSingleRasterSize(Product... products) throws OperatorException {
        //no need to consider only single-size products.
        Product p;
        if (products.length > 0 && (p = products[0]) != null) {
            return new Dimension(p.getSceneRasterWidth(), p.getSceneRasterHeight());
        } else {
            return new Dimension(0, 0);
        }
    }

    @Override
    public void setParameter(String name, Object value) {
        if (name.equals("slaveSourceBand")) {
            hiddenSlaveBand = value.toString();
        } else {
            super.setParameter(name, value);
        }
    }


    @Override
    public void initialize() throws OperatorException {
        // 20190315 OH: when using gpt, the setter for slaveSourceBand (where hiddenSlaveBand is initialized) is not called, so hiddenSlaveBand remains null
        if (hiddenSlaveBand == null)
        {
            hiddenSlaveBand = slaveSourceBand;
        }

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
        }
    }

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        if (masterSourceBand == null || masterSourceBand.isEmpty() ||
                masterProduct.getBand(masterSourceBand) == null) {
            throw new OperatorException("Band name " + masterSourceBand + " wrong for master product " +
                masterProduct.getName() + " having bands : " + Arrays.toString(masterProduct.getBandGroup().toArray()));
        }
        if (hiddenSlaveBand == null || hiddenSlaveBand.isEmpty() ||
                slaveProduct.getBand(hiddenSlaveBand) == null) {
            throw new OperatorException("Band name " + hiddenSlaveBand + " wrong for slave product " +
                    slaveProduct.getName() + " having bands : " + Arrays.toString(slaveProduct.getBandGroup().toArray()));
        }
        try {
            getLogger().info("Started coregistration of products " + masterProduct.getName()
                    + "(" + masterProduct.getSceneRasterWidth() + "X" + masterProduct.getSceneRasterHeight() + ")"
                    + " and " + slaveProduct.getName()
                    + "(" + slaveProduct.getSceneRasterWidth() + "X" + slaveProduct.getSceneRasterHeight() + ")");
            long startTime = System.currentTimeMillis();

            Band originalMasterBand = masterProduct.getBand(masterSourceBand);
            int levelMaster = originalMasterBand.getMultiLevelModel().getLevelCount();
            BufferedImage sourceMasterImage = ImageOperations.convertBufferedImage(originalMasterBand.getSourceImage().getImage(0));
            BufferedImage processedMasterImage = sourceMasterImage;

            Band originalSlaveBand = slaveProduct.getBand(hiddenSlaveBand);
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
            ImagePyramidCache pyramidSlave = new ImagePyramidCache(processedSlaveImage, levels);

            for (int k = levels; k >= 0; k--) {
                getLogger().info("Start level " + k + " after " + (System.currentTimeMillis() - startTime) / 1000f + "sec from start.");
                BufferedImage levelMasterImage = pyramidMaster.getImage(k);
                BufferedImage levelSlaveImage = pyramidSlave.getImage(k);
                int width = levelSlaveImage.getWidth();
                int height = levelSlaveImage.getHeight();

                BufferedImage levelMasterImageEq, levelSlaveImageEq;
                if (contrast) {
                    //clahe should be applied directly on BufferedImage, to avoid transfering from/to image
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

                for (int rad = 0; rad < radArray.length; rad++) {
                    int r = radArray[rad];
                    getLogger().info("rad=" + r);
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

                    for (int iter = 0; iter < iterations; iter++) {

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

                        RenderedImage G = MultiplyDescriptor.create(It.get(), Ixload.get(), null);
                        RenderedImage H = MultiplyDescriptor.create(It.get(), Iyload.get(), null);

                        G = ImageOperations.doubleConvolve(G, fen);
                        H = ImageOperations.doubleConvolve(H, fen);

                        RenderedImage u1 = DivideDescriptor.create(
                                SubtractDescriptor.create(
                                        MultiplyDescriptor.create(G, B, null),
                                        MultiplyDescriptor.create(C, H, null),
                                        null),
                                D, null);
                        u1 = ImageOperations.replace(u1, Float.NaN, 0.0f);
                        ImagePyramidCache.writeImage(((PlanarImage) u1).getAsBufferedImage(), "u");

                        RenderedImage v1 = DivideDescriptor.create(
                                SubtractDescriptor.create(
                                        MultiplyDescriptor.create(A, H, null).getAsBufferedImage(),
                                        MultiplyDescriptor.create(C, G, null).getAsBufferedImage(),
                                        null).getAsBufferedImage(),
                                D, null).getAsBufferedImage();
                        v1 = ImageOperations.replace(v1, Float.NaN, 0.0f);
                        ImagePyramidCache.writeImage(((PlanarImage) v1).getAsBufferedImage(), "v");

                        Runtime.getRuntime().gc();
                    }
                }
            }


            xFactor = (float) sourceSlaveImage.getWidth() / processedSlaveImage.getWidth();
            yFactor = (float) sourceSlaveImage.getHeight() / processedSlaveImage.getHeight();

            ParameterBlock pb = new ParameterBlock();
            pb.addSource(ImagePyramidCache.readImage("meshCol"));
            pb.addSource(ImagePyramidCache.readImage("u"));
            BufferedImage dx = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();
            if (xFactor != 1f || yFactor != 1f) {
                dx = ImageOperations.resize(dx, xFactor, yFactor,
                        Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
            }

            pb = new ParameterBlock();
            pb.addSource(ImagePyramidCache.readImage("meshRow"));
            pb.addSource(ImagePyramidCache.readImage("v"));
            BufferedImage dy = (BufferedImage) JAI.create("add", pb).getAsBufferedImage();
            if (xFactor != 1f || yFactor != 1f) {
                dy = ImageOperations.resize(dy, xFactor, yFactor,
                        Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
            }

            BufferedImage targetImage = ImageOperations.interpolate(sourceSlaveImage, dx, dy);

            pyramidMaster.cleanPyramid();
            pyramidSlave.cleanPyramid();

            Band targetBand = new Band(originalSlaveBand.getName(),
                    ProductData.TYPE_FLOAT32,
                    originalSlaveBand.getRasterWidth(),
                    originalSlaveBand.getRasterHeight());
            targetBand.setSourceImage(targetImage);
            targetProduct.addBand(targetBand);

            for (Band b : Arrays.asList(slaveProduct.getBands())) {
                if (!b.getName().equals(hiddenSlaveBand)) {

                    BufferedImage bImage = ImageOperations.interpolate(
                            ImageOperations.convertBufferedImage(b.getSourceImage().getImage(0))
                            , dx, dy);

                    Band btBand = new Band(b.getName(),
                            ProductData.TYPE_FLOAT32,
                            b.getRasterWidth(),
                            b.getRasterHeight());
                    btBand.setSourceImage(targetImage);
                    targetProduct.addBand(btBand);

                }
            }

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

        @Override
        public Operator createOperator(Map<String, Object> parameters,
                                       Map<String, Product> sourceProducts,
                                       RenderingHints renderingHints) throws OperatorException {
            String slaveBand = parameters.get("slaveSourceBand") != null ? parameters.get("slaveSourceBand").toString() : null;
            parameters.remove("slaveSourceBand");
            Operator op = super.createOperator(parameters, sourceProducts, renderingHints);
            if (op instanceof CoregistrationOp) {
                ((CoregistrationOp) op).hiddenSlaveBand = slaveBand;
            }
            return op;
        }
    }

}
