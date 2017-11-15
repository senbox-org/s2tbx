package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import com.bc.ceres.core.ProgressMonitor;
import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.RasterDataNode;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.BitSetter;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.math.MathUtils;
import org.opengis.referencing.operation.MathTransform;

import javax.media.jai.BorderExtenderConstant;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * todo: add comment
 */
@OperatorMetadata(alias = "CCICloudShadow",
        category = "Optical",
        authors = "Grit Kirches, Michael Paperin, Olaf Danne",
        copyright = "(c) Brockmann Consult GmbH",
        version = "1.0",
        description = "Algorithm detecting cloud shadow...")
public class S2IdepixCloudShadowOp extends Operator {

    @SourceProduct(description = "The classification product.")
    private Product s2ClassifProduct;

    @SourceProduct(alias = "s2CloudBuffer", optional = true)
    private Product s2CloudBufferProduct;      // has only classifFlagBand with buffer added

    @TargetProduct
    private Product targetProduct;

    @Parameter(description = "The mode by which clouds are detected. There are three options: Land/Water, Multiple Bands" +
            "or Single Band", valueSet = {"LandWater", "MultiBand", "SingleBand"}, defaultValue = "LandWater")
    private String mode;

    @Parameter(description = "Whether to also compute mountain shadow", defaultValue = "true")
    private boolean computeMountainShadow;

    private final static double MAX_CLOUD_HEIGHT = 8000.;
    private final static int MAX_TILE_DIMENSION = 1400;

    private Band sourceBandClusterA;
    private Band sourceBandClusterB;

    private Band sourceBandFlag1;
    private Band sourceBandFlag2;

    private RasterDataNode sourceSunZenith;
    private RasterDataNode sourceSunAzimuth;
    private RasterDataNode sourceAltitude;

    private Band targetBandCloudShadow;

    static int mincloudBase = 100;
    static int maxcloudTop = 10000;
    static double spatialResolution;  //[m]
    static int clusterCountDefine = 4;
    static double OUTLIER_THRESHOLD = 0.94;
    static double Threshold_Whiteness_Darkness = -1000;
    static int GROWING_CLOUD = 1;
    static int searchBorderRadius;
    private static final String sourceBandNameClusterA = "B8A";
    private static final String sourceBandNameClusterB = "B3";
    private static final String sourceSunZenithName = "sun_zenith";
    private static final String sourceSunAzimuthName = "sun_azimuth";
    private static final String sourceAltitudeName = "elevation";
    private static final String sourceFlagName1 = "pixel_classif_flags";
    public final static String BAND_NAME_CLOUD_SHADOW = "FlagBand";
    private Mode analysisMode;

    public static final String F_INVALID_DESCR_TEXT = "Invalid pixels";
    public static final String F_CLOUD_DESCR_TEXT = "Cloud pixels";
    public static final String F_MOUNTAIN_SHADOW_DESCR_TEXT = "Mountain shadow pixels";
    public static final String F_CLOUD_SHADOW_DESCR_TEXT = "Cloud shadow pixels";
    public static final String F_LAND_DESCR_TEXT = "Land pixels";
    public static final String F_WATER_DESCR_TEXT = "Water pixels";
    public static final String F_HAZE_DESCR_TEXT = "Potential haze/semitarnsparent cloud pixels";

    public static final int F_WATER = 0;
    public static final int F_LAND = 1;
    public static final int F_CLOUD = 2;
    public static final int F_HAZE = 3;
    public static final int F_CLOUD_SHADOW = 4;
    public static final int F_MOUNTAIN_SHADOW = 5;
    public static final int F_INVALID = 6;
    public static final int F_CLOUD_BUFFER = 7;

    @Override
    public void initialize() throws OperatorException {

        targetProduct = new Product(s2ClassifProduct.getName(), s2ClassifProduct.getProductType(), s2ClassifProduct.getSceneRasterWidth(), s2ClassifProduct.getSceneRasterHeight());

        ProductUtils.copyGeoCoding(s2ClassifProduct, targetProduct);

        sourceBandClusterA = s2ClassifProduct.getBand(sourceBandNameClusterA);
        sourceBandClusterB = s2ClassifProduct.getBand(sourceBandNameClusterB);

        sourceSunZenith = s2ClassifProduct.getBand(sourceSunZenithName);
        // take these. They're as good as the tile dimensions from any other band and DEFINITELY more reliable than
        // the preferred tile size of the s2ClassifProduct
        final int sourceTileWidth = sourceSunZenith.getSourceImage().getTileWidth();
        final int sourceTileHeight = sourceSunZenith.getSourceImage().getTileHeight();
        final double maximumSunZenith = sourceSunZenith.getStx().getMaximum();
        sourceSunAzimuth = s2ClassifProduct.getBand(sourceSunAzimuthName);
        sourceAltitude = s2ClassifProduct.getBand(sourceAltitudeName);

        sourceBandFlag1 = s2ClassifProduct.getBand(sourceFlagName1);
        if (s2CloudBufferProduct != null) {
            sourceBandFlag2 = s2CloudBufferProduct.getBand(sourceFlagName1);
        }

        targetBandCloudShadow = targetProduct.addBand(BAND_NAME_CLOUD_SHADOW, ProductData.TYPE_INT32);
        attachFlagCoding(targetBandCloudShadow);
        setupBitmasks(targetProduct);

        spatialResolution = determineSourceResolution();
        searchBorderRadius = (int) determineSearchBorderRadius(S2IdepixCloudShadowOp.spatialResolution, maximumSunZenith);
        final int tileWidth = determineSourceTileWidth(s2ClassifProduct.getSceneRasterWidth(), sourceTileWidth,
                                                       searchBorderRadius, searchBorderRadius);
        final int tileHeight = determineSourceTileHeight(s2ClassifProduct.getSceneRasterHeight(), sourceTileHeight,
                                                         searchBorderRadius, searchBorderRadius);

        // todo: discuss
        if (s2ClassifProduct.getSceneRasterWidth() > tileWidth || s2ClassifProduct.getSceneRasterHeight() > tileHeight) {
            final int preferredTileWidth = Math.min(s2ClassifProduct.getSceneRasterWidth(), tileWidth);
            final int preferredTileHeight = Math.min(s2ClassifProduct.getSceneRasterHeight(), tileHeight);
            targetProduct.setPreferredTileSize(preferredTileWidth, preferredTileHeight); //1500
        } else {
            targetProduct.setPreferredTileSize(s2ClassifProduct.getSceneRasterWidth(), s2ClassifProduct.getSceneRasterHeight());
            searchBorderRadius = 0;
        }
        switch (mode) {
            case "LandWater":
                analysisMode = Mode.LAND_WATER;
                break;
            case "MultiBand":
                analysisMode = Mode.MULTI_BAND;
                break;
            case "SingleBand":
                analysisMode = Mode.SINGLE_BAND;
                break;
            default:
                throw new OperatorException("Invalid analysis mode. Must be LandWater, MultiBand or SingleBand.");
        }
    }

    private double determineSourceResolution() throws OperatorException {
        final GeoCoding sceneGeoCoding = getSourceProduct().getSceneGeoCoding();
        if (sceneGeoCoding instanceof CrsGeoCoding) {
            final MathTransform imageToMapTransform = sceneGeoCoding.getImageToMapTransform();
            if (imageToMapTransform instanceof AffineTransform) {
                return ((AffineTransform) imageToMapTransform).getScaleX();
            }
        }
        throw new OperatorException("Invalid product: ");
    }

    private int determineSourceTileWidth(int rasterWidth, int tileWidth,
                                         int rightBorderExtension, int leftBorderExtension) {
        return determineSourceTileSize(rasterWidth, tileWidth, rightBorderExtension, leftBorderExtension);
    }

    private int determineSourceTileHeight(int rasterHeight, int tileHeight,
                                          int topBorderExtension, int bottomBorderExtension) {
        return determineSourceTileSize(rasterHeight, tileHeight, topBorderExtension, bottomBorderExtension);
    }

    int determineSourceTileSize(int rasterSize, int tileSize, int borderExtension1, int borderExtension2) {
        int maxTileSize = Math.min(MAX_TILE_DIMENSION - borderExtension1 - borderExtension2, 2 * tileSize);
        final int minNumTiles = (int) Math.floor(rasterSize / maxTileSize * 1.);
        int bestTileSize = Integer.MIN_VALUE;
        int smallestDiff = Integer.MAX_VALUE;
        for (int i = minNumTiles; i >= 1; i++) {
            if (rasterSize % i == 0) {
                final int candidateDiff = Math.abs(tileSize - rasterSize / i);
                if (candidateDiff > smallestDiff) {
                    break;
                }
                bestTileSize = rasterSize / i;
                smallestDiff = Math.abs(tileSize - bestTileSize);
            }
        }
        if (smallestDiff < Integer.MAX_VALUE) {
            return bestTileSize;
        }
        return maxTileSize;
    }

    double determineSearchBorderRadius(double spatialResolution, double maxSunZenith) {
        final double maxCloudDistance = MAX_CLOUD_HEIGHT / Math.tan(Math.toRadians(90. - maxSunZenith));
        return maxCloudDistance / spatialResolution;
    }

    int getRightBorderExtension(double searchBorderRadius, double minSunAzimuth, double maxSunAzimuth) {
        return (int) Math.ceil(searchBorderRadius * Math.max(0, Math.max(Math.sin(Math.toRadians(minSunAzimuth)),
                                                                         Math.sin(Math.toRadians(maxSunAzimuth)))));
    }

    int getLeftBorderExtension(double searchBorderRadius, double minSunAzimuth, double maxSunAzimuth) {
        return (int) Math.ceil(searchBorderRadius * Math.max(0, Math.max(Math.sin(Math.toRadians(minSunAzimuth)) * -1,
                                                                         Math.sin(Math.toRadians(maxSunAzimuth)) * -1)));
    }

    int getTopBorderExtension(double searchBorderRadius, double minSunAzimuth, double maxSunAzimuth) {
        return (int) Math.ceil(searchBorderRadius *
                                       Math.max(0, Math.max(Math.cos(Math.toRadians(minSunAzimuth)), Math.cos(Math.toRadians(maxSunAzimuth)))));
    }

    int getBottomBorderExtension(double searchBorderRadius, double minSunAzimuth, double maxSunAzimuth) {
        return (int) Math.ceil(searchBorderRadius * Math.max(0, Math.max(Math.cos(Math.toRadians(minSunAzimuth)) * -1,
                                                                         Math.cos(Math.toRadians(maxSunAzimuth)) * -1)));
    }

    private float[] getSamples(RasterDataNode rasterDataNode, Rectangle rectangle) {
        Tile tile = getSourceTile(rasterDataNode, rectangle, new BorderExtenderConstant(new double[]{Double.NaN}));
        return tile.getSamplesFloat();
    }

    private Rectangle getSourceRectangle(Rectangle targetRectangle, Point2D[] relativePath) {
        final int productWidth = getSourceProduct().getSceneRasterWidth();
        final int productHeight = getSourceProduct().getSceneRasterHeight();
        int x0 = Math.max(0, targetRectangle.x + (int) relativePath[relativePath.length - 1].getX());
        int y0 = Math.max(0, targetRectangle.y + (int) relativePath[relativePath.length - 1].getY());
        int x1 = Math.min(productWidth, targetRectangle.x + targetRectangle.width -
                (int) relativePath[relativePath.length - 1].getX());
        int y1 = Math.min(productHeight, targetRectangle.y + targetRectangle.height -
                (int) relativePath[relativePath.length - 1].getY());
        return new Rectangle(x0, y0, x1 - x0, y1 - y0);
    }

    private static float mean(float[] values) {
        float mean = 0f;
        int valueCount = 0;
        for (float value : values) {
            if (!Double.isNaN(value)) {
                mean += value;
                valueCount++;
            }
        }
        return mean / valueCount;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws OperatorException {

        final float sunZenithMean = mean(getSamples(sourceSunZenith, targetRectangle));
        final float sunAzimuthMean = mean(getSamples(sourceSunAzimuth, targetRectangle));
        final float[] targetAltitude = getSamples(sourceAltitude, targetRectangle);

        final List<Float> altitudes = Arrays.asList(ArrayUtils.toObject(targetAltitude));
        float minAltitude = Math.max(0, Collections.min(altitudes));
        final Point2D[] cloudShadowRelativePath = CloudShadowUtils.getRelativePath(
                minAltitude, sunZenithMean * MathUtils.DTOR, sunAzimuthMean * MathUtils.DTOR, maxcloudTop,
                targetRectangle, targetRectangle, getSourceProduct().getSceneRasterHeight(),
                getSourceProduct().getSceneRasterWidth(), spatialResolution, true, false);
        final Rectangle sourceRectangle = getSourceRectangle(targetRectangle, cloudShadowRelativePath);

        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int sourceLength = sourceRectangle.width * sourceRectangle.height;

        Tile targetTileCloudShadow = targetTiles.get(targetBandCloudShadow);

        final int[] flagArray = new int[sourceLength];
        //will be filled in SegmentationCloudClass Arrays.fill(cloudIdArray, ....);
        final int[] cloudIDArray = new int[sourceLength];

        final float[] altitude = getSamples(sourceAltitude, sourceRectangle);
        final float[][] clusterData = {getSamples(sourceBandClusterA, sourceRectangle),
                getSamples(sourceBandClusterB, sourceRectangle)};

        float[] sourceLatitudes = new float[sourceLength];
        float[] sourceLongitudes = new float[sourceLength];
        ((CrsGeoCoding) getSourceProduct().getSceneGeoCoding()).getPixels((int) sourceRectangle.getMinX(),
                                                                          (int) sourceRectangle.getMinY(),
                                                                          (int) sourceRectangle.getWidth(),
                                                                          (int) sourceRectangle.getHeight(),
                                                                          sourceLatitudes,
                                                                          sourceLongitudes);

        Tile sourceTileFlag1 = getSourceTile(sourceBandFlag1, sourceRectangle, new BorderExtenderConstant(new double[]{Double.NaN}));
        Tile sourceTileFlag2 = null;
        if (sourceBandFlag2 != null) {
            sourceTileFlag2 = getSourceTile(sourceBandFlag2, sourceRectangle, new BorderExtenderConstant(new double[]{Double.NaN}));
        }
        FlagDetector flagDetector = new FlagDetectorSentinel2(sourceTileFlag1, sourceTileFlag2, sourceRectangle);

        PreparationMaskBand.prepareMaskBand(s2ClassifProduct.getSceneRasterWidth(),
                                            s2ClassifProduct.getSceneRasterHeight(), sourceRectangle, flagArray,
                                            flagDetector);

        if (computeMountainShadow) {
            MountainShadowFlagger.flagMountainShadowArea(
                    s2ClassifProduct.getSceneRasterWidth(), s2ClassifProduct.getSceneRasterHeight(),
                    sourceRectangle, targetRectangle, sunZenithMean, sunAzimuthMean, sourceLatitudes,
                    sourceLongitudes, altitude, flagArray);
        }

        final CloudIdentifier cloudIdentifier = new CloudIdentifier(flagArray);
        int numClouds = cloudIdentifier.computeCloudID(sourceWidth, sourceHeight, cloudIDArray);

        //todo assessment of the order of processing steps
            /*double solarFluxNir = sourceBandHazeNir.getSolarFlux();
            HazeDetection detectHaze = new HazeDetection();
            detectHaze.calculatePotentialHazeAreas(sourceRectangle, sourceTileHazeBlue,
                    sourceTileHazeRed,
                    sourceTileHazeNir,
                    sourceWidth,
                    sourceHeight,
                    flagArray,
                    solarFluxNir);   */

        if (numClouds > 0) {
            final Collection<List<Integer>> potentialShadowPositions =
                    PotentialCloudShadowAreaIdentifier.identifyPotentialCloudShadows(
                            sourceRectangle, targetRectangle, sunZenithMean, sunAzimuthMean, sourceLatitudes, sourceLongitudes,
                            altitude, flagArray, cloudIDArray, cloudShadowRelativePath);
            final CloudShadowFlagger cloudShadowFlagger = new CloudShadowFlagger();
            cloudShadowFlagger.flagCloudShadowAreas(clusterData, flagArray, potentialShadowPositions, analysisMode);
        }
        fillTile(flagArray, targetRectangle, sourceRectangle, targetTileCloudShadow);
    }

    private void attachFlagCoding(Band targetBandCloudShadow) {
        FlagCoding cloudCoding = new FlagCoding("cloudCoding");
        cloudCoding.addFlag("water", BitSetter.setFlag(0, F_WATER), F_WATER_DESCR_TEXT);
        ;
        cloudCoding.addFlag("land", BitSetter.setFlag(0, F_LAND), F_LAND_DESCR_TEXT);
        cloudCoding.addFlag("cloud", BitSetter.setFlag(0, F_CLOUD), F_CLOUD_DESCR_TEXT);
        cloudCoding.addFlag("pot_haze", BitSetter.setFlag(0, F_HAZE), F_HAZE_DESCR_TEXT);
        cloudCoding.addFlag("cloudShadow", BitSetter.setFlag(0, F_CLOUD_SHADOW), F_CLOUD_SHADOW_DESCR_TEXT);
        cloudCoding.addFlag("mountain_shadow", BitSetter.setFlag(0, F_MOUNTAIN_SHADOW), F_MOUNTAIN_SHADOW_DESCR_TEXT);
        cloudCoding.addFlag("invalid", BitSetter.setFlag(0, F_INVALID), F_INVALID_DESCR_TEXT);
        targetBandCloudShadow.setSampleCoding(cloudCoding);
        targetBandCloudShadow.getProduct().getFlagCodingGroup().add(cloudCoding);
    }

    private static void setupBitmasks(Product targetProduct) {

        int index = 0;
        int w = targetProduct.getSceneRasterWidth();
        int h = targetProduct.getSceneRasterHeight();
        Mask mask;
        mask = Mask.BandMathsType.create("invalid",
                                         F_INVALID_DESCR_TEXT, w, h,
                                         "FlagBand.invalid",
                                         Color.DARK_GRAY, 0.5f);
        targetProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("land",
                                         F_LAND_DESCR_TEXT, w, h,
                                         "FlagBand.land",
                                         Color.GREEN, 0.5f);
        targetProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("water",
                                         F_WATER_DESCR_TEXT, w, h,
                                         "FlagBand.water",
                                         Color.BLUE, 0.5f);
        targetProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("cloud",
                                         F_CLOUD_DESCR_TEXT, w, h,
                                         "FlagBand.cloud",
                                         Color.YELLOW, 0.5f);
        targetProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("haze/semitransparent cloud",
                                         F_HAZE_DESCR_TEXT, w, h,
                                         " FlagBand.pot_haze",
                                         Color.CYAN, 0.5f);
        targetProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("cloud_shadow",
                                         F_CLOUD_SHADOW_DESCR_TEXT, w, h,
                                         "FlagBand.cloudShadow",
                                         Color.ORANGE, 0.5f);
        targetProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("mountain_shadow",
                                         F_MOUNTAIN_SHADOW_DESCR_TEXT, w, h,
                                         "FlagBand.mountain_shadow",
                                         Color.PINK, 0.5f);
        targetProduct.getMaskGroup().add(index, mask);
    }

    private static void fillTile(int[] inputData, Rectangle targetRectangle, Rectangle sourceRectangle, Tile targetTile) {
        for (int targetY = targetRectangle.y; targetY < targetRectangle.y + targetRectangle.height; targetY++) {
            int sourceY = targetY - sourceRectangle.y;
            for (int targetX = targetRectangle.x; targetX < targetRectangle.x + targetRectangle.width; targetX++) {
                int sourceX = targetX - sourceRectangle.x;
                int sourceIndex = sourceY * sourceRectangle.width + sourceX;
                targetTile.setSample(targetX, targetY, inputData[sourceIndex]);
            }
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixCloudShadowOp.class);
        }
    }

}
