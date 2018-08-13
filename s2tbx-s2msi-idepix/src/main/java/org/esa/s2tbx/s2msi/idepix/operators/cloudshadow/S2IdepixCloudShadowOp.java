package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.gpf.internal.OperatorImage;
import org.esa.snap.core.gpf.internal.OperatorImageTileStack;
import org.esa.snap.core.util.SystemUtils;
import org.opengis.referencing.operation.MathTransform;

import javax.media.jai.CachedTile;
import javax.media.jai.JAI;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@OperatorMetadata(alias = "CCICloudShadow",
        category = "Optical",
        authors = "Grit Kirches, Michael Paperin, Olaf Danne, Tonio Fincke, Dagmar Mueller",
        copyright = "(c) Brockmann Consult GmbH",
        version = "1.0",
        description = "Algorithm detecting cloud shadow...")
public class S2IdepixCloudShadowOp extends Operator {

    @SourceProduct(description = "The original input product")
    private Product l1cProduct;

    @SourceProduct(description = "The classification product.")
    private Product s2ClassifProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(description = "The mode by which clouds are detected. There are three options: Land/Water, Multiple Bands" +
            "or Single Band", valueSet = {"LandWater", "MultiBand", "SingleBand"}, defaultValue = "LandWater")
    private String mode;

    @Parameter(description = "Whether to also compute mountain shadow", defaultValue = "true")
    private boolean computeMountainShadow;

    @Parameter(defaultValue = "0.01",
            label = " Threshold CW_THRESH",
            description = " Threshold CW_THRESH")
    private double cwThresh;

    @Parameter(defaultValue = "-0.11",
            label = " Threshold GCL_THRESH",
            description = " Threshold GCL_THRESH")
    private double gclThresh;

    @Parameter(defaultValue = "0.01",
            label = " Threshold CL_THRESH",
            description = " Threshold CL_THRESH")
    private double clThresh;

    @Parameter(description = "The digital elevation model.", defaultValue = "SRTM 3Sec", label = "Digital Elevation Model")
    private String demName = "SRTM 3Sec";

    public final static String BAND_NAME_CLOUD_SHADOW = "FlagBand";

    @Override
    public void initialize() throws OperatorException {
        JAI.getDefaultInstance().getTileCache().setTileComparator(new S2IdepixCloudShadowTileComparator());

        int sourceResolution = determineSourceResolution(l1cProduct);

        Product classificationProduct = getClassificationProduct(sourceResolution);

        HashMap<String, Product> postInput = new HashMap<>();
        postInput.put("s2ClassifProduct", classificationProduct);
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("mode", mode);
        Product postProduct = GPF.createProduct("Idepix.Sentinel2.CloudShadow.Postprocess", postParams, postInput);

        setTargetProduct(prepareTargetProduct(sourceResolution, postProduct));
    }

    private int determineSourceResolution(Product product) throws OperatorException {
        final GeoCoding sceneGeoCoding = product.getSceneGeoCoding();
        if (sceneGeoCoding instanceof CrsGeoCoding) {
            final MathTransform imageToMapTransform = sceneGeoCoding.getImageToMapTransform();
            if (imageToMapTransform instanceof AffineTransform) {
                return (int)((AffineTransform) imageToMapTransform).getScaleX();
            }
        }
        throw new OperatorException("Invalid product");
    }

    private Product getClassificationProduct(int resolution) {
        if (resolution == 60) {
            return s2ClassifProduct;
        }

        HashMap<String, Product> resamplingInput = new HashMap<>();
        resamplingInput.put("sourceProduct", l1cProduct);
        Map<String, Object> resamplingParams = new HashMap<>();
        resamplingParams.put("upsampling", "Nearest");
        resamplingParams.put("downsampling", "First");
        resamplingParams.put("targetResolution", 60);
        Product resampledProduct = GPF.createProduct("Resample", resamplingParams, resamplingInput);

        HashMap<String, Product> classificationInput = new HashMap<>();
        classificationInput.put("sourceProduct", resampledProduct);
        Map<String, Object> classificationParams = new HashMap<>();
        classificationParams.put("computeMountainShadow", false);
        classificationParams.put("computeCloudShadow", false);
        classificationParams.put("computeCloudBuffer", false);
        classificationParams.put("cwThresh", cwThresh);
        classificationParams.put("gclThresh", gclThresh);
        classificationParams.put("clThresh", clThresh);
        classificationParams.put("demName", demName);

        return GPF.createProduct("Idepix.Sentinel2", classificationParams, classificationInput);
    }

    private Product prepareTargetProduct(int resolution, Product postProcessedProduct) {
        if (resolution == 60) {
            return postProcessedProduct;
        }

        HashMap<String, Product> resamplingInput = new HashMap<>();
        resamplingInput.put("sourceProduct", postProcessedProduct);
        Map<String, Object> resamplingParams = new HashMap<>();
        resamplingParams.put("upsampling", "Nearest");
        resamplingParams.put("downsampling", "First");
        resamplingParams.put("targetResolution", resolution);
        return GPF.createProduct("Resample", resamplingParams, resamplingInput);
    }

    private static class S2IdepixCloudShadowTileComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            return getWeight(o1) - getWeight(o2) - 1; // do not return equality as that keeps us in while loop
        }

        private int getWeight(Object o) {
            if (!(o instanceof CachedTile)) {
                return -1;
            }
            RenderedImage oOwner = ((CachedTile) o).getOwner();
            if (oOwner instanceof OperatorImageTileStack) {
                String bandName = ((OperatorImageTileStack) oOwner).getTargetBand().getName();
                if (bandName.equals("pixel_classif_flags")) {
                    return 4;
                }
                if (bandName.equals(S2IdepixCloudShadowOp.BAND_NAME_CLOUD_SHADOW)) {
                    return 8;
                }
            }
            if (oOwner instanceof OperatorImage &&
                    ((OperatorImage) oOwner).getTargetBand().getName().equals("elevation")) {
                return 2;
            }

            return 0;
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixCloudShadowOp.class);
        }
    }

}
