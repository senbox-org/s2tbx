package org.esa.s2tbx.s2msi.idepix.operators.mountainshadow;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
import org.opengis.referencing.operation.MathTransform;

import javax.media.jai.BorderExtender;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Map;

/**
 * @author Tonio Fincke
 */
public class SlopeAspectOrientationOp extends Operator {

    @SourceProduct
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(description = "Whether to compute the slope", defaultValue = "true")
    private boolean computeSlope;

    @Parameter(description = "Whether to compute the aspect", defaultValue = "true")
    private boolean computeAspect;

    @Parameter(description = "Whether to compute the orientation", defaultValue = "true")
    private boolean computeOrientation;

    //    @Parameter(description = "The spatial resolution of the source image. If not given, it is attempted to extract" +
//            "this from the geo-coding. Required for computing slope and aspect.")
    private double spatialResolution;

    private GeoCoding sourceGeoCoding;
    private Band elevationBand;
    private Band slopeBand;
    private Band aspectBand;
    private Band orientationBand;

    @Override
    public void initialize() throws OperatorException {
        sourceProduct = getSourceProduct();
        ensureSingleRasterSize(sourceProduct);
        sourceGeoCoding = sourceProduct.getSceneGeoCoding();
        if (sourceGeoCoding == null) {
            throw new OperatorException("Source product has no geo-coding");
        }
//        if (Double.isNaN(spatialResolution) && (computeSlope || computeAspect)) {
        if (computeSlope || computeAspect) {
            if (sourceGeoCoding instanceof CrsGeoCoding) {
                final MathTransform i2m = sourceGeoCoding.getImageToMapTransform();
                if (i2m instanceof AffineTransform) {
                    spatialResolution = ((AffineTransform) i2m).getScaleX();
                } else {
                    throw new OperatorException("Could not retrieve spatial resolution from Geo-coding");
                }
            } else {
                throw new OperatorException("Could not retrieve spatial resolution from Geo-coding");
            }
        }
        elevationBand = sourceProduct.getBand(S2IdepixConstants.ELEVATION_BAND_NAME);
        if (elevationBand == null && (computeSlope || computeAspect)) {
            throw new OperatorException("Elevation band required to compute slope or aspect");
        }
        targetProduct = createTargetProduct("Slope-Aspect-Orientation", "slope-aspect-orientation");
        if (computeSlope) {
            slopeBand = targetProduct.addBand("Slope", ProductData.TYPE_FLOAT32);
            slopeBand.setDescription("Slope of each pixel as angle");
            slopeBand.setUnit("rad [0..pi/2]");
            slopeBand.setNoDataValue(-9999.);
            slopeBand.setNoDataValueUsed(true);
        }
        if (computeAspect) {
            aspectBand = targetProduct.addBand("Aspect", ProductData.TYPE_FLOAT32);
            aspectBand.setDescription("Aspect of each pixel as angle between raster -Y direction and steepest slope, " +
                                              "clockwise");
            aspectBand.setUnit("rad [-pi..pi]");
            aspectBand.setNoDataValue(-9999.);
            aspectBand.setNoDataValueUsed(true);
        }
        if (computeOrientation) {
            orientationBand = targetProduct.addBand("Orientation", ProductData.TYPE_FLOAT32);
            orientationBand.setDescription("Orientation of each pixel as angle between east and raster X direction, " +
                                                   "clockwise");
            orientationBand.setUnit("rad [-pi..pi]");
            orientationBand.setNoDataValue(-9999.);
            orientationBand.setNoDataValueUsed(true);
        }
        setTargetProduct(targetProduct);
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws OperatorException {
        if (!computeSlope && !computeAspect && !computeOrientation) {
            return;
        }
        final Rectangle sourceRectangle = getSourceRectangle_2(targetRectangle);
        float[] elevationData = new float[(int) (sourceRectangle.getWidth() * sourceRectangle.getHeight())];
        if (computeSlope || computeAspect) {
            final BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);
            final Tile elevationTile = getSourceTile(elevationBand, sourceRectangle, borderExtender);
            elevationData = elevationTile.getDataBufferFloat();
        }
        float[] sourceLatitudes = new float[(int) (sourceRectangle.getWidth() * sourceRectangle.getHeight())];
        float[] sourceLongitudes = new float[(int) (sourceRectangle.getWidth() * sourceRectangle.getHeight())];
        if (computeOrientation) {
            ((CrsGeoCoding) getSourceProduct().getSceneGeoCoding()).getPixels((int) sourceRectangle.getMinX(),
                                                                              (int) sourceRectangle.getMinY(),
                                                                              (int) sourceRectangle.getWidth(),
                                                                              (int) sourceRectangle.getHeight(),
                                                                              sourceLatitudes,
                                                                              sourceLongitudes);
        }
        int sourceIndex = 0;
        int targetIndex = 0;
        final float[] slopeData = targetTiles.get(slopeBand).getDataBufferFloat();
        final float[] aspectData = targetTiles.get(aspectBand).getDataBufferFloat();
        final float[] orientationData = targetTiles.get(orientationBand).getDataBufferFloat();
        for (int y = targetRectangle.y; y < targetRectangle.y + targetRectangle.height; y++) {
            sourceIndex += sourceRectangle.width;
            targetIndex += targetRectangle.width;
            for (int x = targetRectangle.x; x < targetRectangle.x + targetRectangle.width; x++) {
                sourceIndex++;
                targetIndex++;
                if (computeSlope || computeAspect) {
                    final float[] slopeAndAspect = computeSlopeAndAspect(elevationData, sourceIndex,
                                                                         spatialResolution, sourceRectangle.width);
                    slopeData[targetIndex] = slopeAndAspect[0];
                    aspectData[targetIndex] = slopeAndAspect[1];
                }
                if (computeOrientation) {
                    orientationData[targetIndex] = computeOrientation(sourceLatitudes, sourceLongitudes,
                                                                      sourceIndex, sourceRectangle.width);
                }
            }
        }
    }

    /* package local for testing */
    static float[] computeSlopeAndAspect(float[] elevationData, int sourceIndex, double spatialResolution,
                                         int sourceWidth) {
        float elevA1 = elevationData[sourceIndex - sourceWidth - 1];
        float elevA2 = elevationData[sourceIndex - sourceWidth];
        float elevA3 = elevationData[sourceIndex - sourceWidth + 1];
        float elevA4 = elevationData[sourceIndex - 1];
        float elevA6 = elevationData[sourceIndex + 1];
        float elevA7 = elevationData[sourceIndex + sourceWidth - 1];
        float elevA8 = elevationData[sourceIndex + sourceWidth];
        float elevA9 = elevationData[sourceIndex + sourceWidth + 1];
        float b = (elevA3 + 2 * elevA6 + elevA9 - elevA1 - 2 * elevA4 - elevA7) / 8f;
        float c = (elevA1 + 2 * elevA2 + elevA3 - elevA7 - 2 * elevA8 - elevA9) / 8f;
        float slope = (float) Math.atan(Math.sqrt(Math.pow(b / spatialResolution, 2) +
                                                          Math.pow(c / spatialResolution, 2)));
        float aspect = (float) Math.atan2(-b, -c);
        return new float[]{slope, aspect};
    }

    /* package local for testing */
    static float computeOrientation(float[] latData, float[] lonData, int sourceIndex, int sourceWidth) {
        float lat1 = latData[sourceIndex - 1];
        float lat2 = latData[sourceIndex + 1];
        float lon1 = lonData[sourceIndex - 1];
        float lon2 = lonData[sourceIndex + 1];
        return (float) Math.atan2(- (lat2 - lat1), (lon2 - lon1) * Math.cos(Math.toRadians(lat1)));
    }

    /* package local for testing */
//    static float computeOrientation(float[] latData, float[] lonData, int sourceIndex) {
//
//    }

    /* package local for testing */
    static Rectangle getSourceRectangle_2(Rectangle targetRectangle) {
        return new Rectangle(targetRectangle.x -1, targetRectangle.y - 1,
                             targetRectangle.width + 2, targetRectangle.height + 2);
    }

    /* package local for testing */
    static Rectangle getSourceRectangle(Rectangle targetRectangle, int productWidth, int productHeight) {
        int leftExtender = 1;
        int rightExtender = 1;
        int upperExtender = 1;
        int lowerExtender = 1;
        if (targetRectangle.x == 0) {
            leftExtender = 0;
        }
        if (targetRectangle.y == 0) {
            upperExtender = 0;
        }
        if (targetRectangle.x + targetRectangle.width == productWidth) {
            rightExtender = 0;
        }
        if (targetRectangle.y + targetRectangle.height == productHeight) {
            lowerExtender = 0;
        }
        return new Rectangle(targetRectangle.x - leftExtender,
                             targetRectangle.y - upperExtender,
                             targetRectangle.width + leftExtender + rightExtender,
                             targetRectangle.height + upperExtender + lowerExtender);
    }

    private Product createTargetProduct(String name, String type) {
        final int sceneWidth = sourceProduct.getSceneRasterWidth();
        final int sceneHeight = sourceProduct.getSceneRasterHeight();

        Product targetProduct = new Product(name, type, sceneWidth, sceneHeight);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        targetProduct.setStartTime(sourceProduct.getStartTime());
        targetProduct.setEndTime(sourceProduct.getEndTime());

        return targetProduct;
    }


    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixMountainShadowOp.class);
        }
    }
}
