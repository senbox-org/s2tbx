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
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
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
@OperatorMetadata(alias = "Idepix.Sentinel2.SlopeAspectOrientation",
        version = "1.0",
        internal = true,
        authors = "Tonio Fincke",
        copyright = "(c) 2018 by Brockmann Consult",
        description = "Computes Slope, Aspect and Orientation for a Sentinel-2 product " +
                "with elevation data and a CRS geocoding.")
public class SlopeAspectOrientationOp extends Operator {

    @SourceProduct
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    private double spatialResolution;

    private Band elevationBand;
    private Band slopeBand;
    private Band aspectBand;
    private Band orientationBand;
    private final static String TARGET_PRODUCT_NAME = "Slope-Aspect-Orientation";
    private final static String TARGET_PRODUCT_TYPE = "slope-aspect-orientation";
    final static String SLOPE_BAND_NAME = "slope";
    final static String ASPECT_BAND_NAME = "aspect";
    final static String ORIENTATION_BAND_NAME = "orientation";
    private final static String SLOPE_BAND_DESCRIPTION = "Slope of each pixel as angle";
    private final static String ASPECT_BAND_DESCRIPTION =
            "Aspect of each pixel as angle between raster -Y direction and steepest slope, clockwise";
    private final static String ORIENTATION_BAND_DESCRIPTION =
            "Orientation of each pixel as angle between east and raster X direction, clockwise";
    private final static String SLOPE_BAND_UNIT = "rad [0..pi/2]";
    private final static String ASPECT_BAND_UNIT = "rad [-pi..pi]";
    private final static String ORIENTATION_BAND_UNIT = "rad [-pi..pi]";

    @Override
    public void initialize() throws OperatorException {
        sourceProduct = getSourceProduct();
        ensureSingleRasterSize(sourceProduct);
        GeoCoding sourceGeoCoding = sourceProduct.getSceneGeoCoding();
        if (sourceGeoCoding == null) {
            throw new OperatorException("Source product has no geo-coding");
        }
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
        elevationBand = sourceProduct.getBand(S2IdepixConstants.ELEVATION_BAND_NAME);
        if (elevationBand == null) {
            throw new OperatorException("Elevation band required to compute slope or aspect");
        }
        targetProduct = createTargetProduct();
        slopeBand = createBand(SLOPE_BAND_NAME, SLOPE_BAND_DESCRIPTION, SLOPE_BAND_UNIT);
        aspectBand = createBand(ASPECT_BAND_NAME, ASPECT_BAND_DESCRIPTION, ASPECT_BAND_UNIT);
        orientationBand = createBand(ORIENTATION_BAND_NAME, ORIENTATION_BAND_DESCRIPTION, ORIENTATION_BAND_UNIT);
        setTargetProduct(targetProduct);
    }

    private Band createBand(String bandName, String description, String unit) {
        Band band = targetProduct.addBand(bandName, ProductData.TYPE_FLOAT32);
        band.setDescription(description);
        band.setUnit(unit);
        band.setNoDataValue(-9999.);
        band.setNoDataValueUsed(true);
        return band;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm)
            throws OperatorException {
        final Rectangle sourceRectangle = getSourceRectangle(targetRectangle);
        final BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);
        final Tile elevationTile = getSourceTile(elevationBand, sourceRectangle, borderExtender);
        float[] elevationData = elevationTile.getDataBufferFloat();
        float[] sourceLatitudes = new float[(int) (sourceRectangle.getWidth() * sourceRectangle.getHeight())];
        float[] sourceLongitudes = new float[(int) (sourceRectangle.getWidth() * sourceRectangle.getHeight())];
        ((CrsGeoCoding) getSourceProduct().getSceneGeoCoding()).getPixels((int) sourceRectangle.getMinX(),
                                                                          (int) sourceRectangle.getMinY(),
                                                                          (int) sourceRectangle.getWidth(),
                                                                          (int) sourceRectangle.getHeight(),
                                                                          sourceLatitudes,
                                                                          sourceLongitudes);
        int sourceIndex = sourceRectangle.width;
        int targetIndex = 0;
        final ProductData slopeDataBuffer = targetTiles.get(slopeBand).getDataBuffer();
        final ProductData aspectDataBuffer = targetTiles.get(aspectBand).getDataBuffer();
        final ProductData orientationDataBuffer = targetTiles.get(orientationBand).getDataBuffer();
        for (int y = targetRectangle.y; y < targetRectangle.y + targetRectangle.height; y++) {
            sourceIndex++;
            for (int x = targetRectangle.x; x < targetRectangle.x + targetRectangle.width; x++) {
                final float[] slopeAndAspect = computeSlopeAndAspect(elevationData, sourceIndex,
                                                                     spatialResolution, sourceRectangle.width);
                slopeDataBuffer.setElemFloatAt(targetIndex, slopeAndAspect[0]);
                aspectDataBuffer.setElemFloatAt(targetIndex, slopeAndAspect[1]);
                orientationDataBuffer.setElemFloatAt(targetIndex,
                                                     computeOrientation(sourceLatitudes, sourceLongitudes, sourceIndex));
                sourceIndex++;
                targetIndex++;
            }
            sourceIndex++;
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
    static float computeOrientation(float[] latData, float[] lonData, int sourceIndex) {
        float lat1 = latData[sourceIndex - 1];
        float lat2 = latData[sourceIndex + 1];
        float lon1 = lonData[sourceIndex - 1];
        float lon2 = lonData[sourceIndex + 1];
        return (float) Math.atan2(-(lat2 - lat1), (lon2 - lon1) * Math.cos(Math.toRadians(lat1)));
    }

    private static Rectangle getSourceRectangle(Rectangle targetRectangle) {
        return new Rectangle(targetRectangle.x - 1, targetRectangle.y - 1,
                             targetRectangle.width + 2, targetRectangle.height + 2);
    }

    private Product createTargetProduct() {
        final int sceneWidth = sourceProduct.getSceneRasterWidth();
        final int sceneHeight = sourceProduct.getSceneRasterHeight();
        Product targetProduct = new Product(TARGET_PRODUCT_NAME, TARGET_PRODUCT_TYPE, sceneWidth, sceneHeight);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        targetProduct.setStartTime(sourceProduct.getStartTime());
        targetProduct.setEndTime(sourceProduct.getEndTime());

        return targetProduct;
    }


    public static class Spi extends OperatorSpi {
        public Spi() {
            super(SlopeAspectOrientationOp.class);
        }
    }
}
