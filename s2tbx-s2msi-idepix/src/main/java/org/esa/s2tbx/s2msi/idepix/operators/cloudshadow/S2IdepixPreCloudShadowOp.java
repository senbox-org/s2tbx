package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import com.bc.ceres.core.ProgressMonitor;
import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.PixelPos;
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
import java.util.*;

/**
 * @author Tonio Fincke, Dagmar Müller
 */
@OperatorMetadata(alias = "Idepix.Sentinel2.CloudShadow.Preprocess",
        category = "Optical",
        authors = "Grit Kirches, Michael Paperin, Olaf Danne, Tonio Fincke, Dagmar Müller",
        copyright = "(c) Brockmann Consult GmbH",
        version = "1.0",
        description = "Pre-processing for algorithm detecting cloud shadow...")

public class S2IdepixPreCloudShadowOp extends Operator {

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
    private RasterDataNode sourceViewZenith;
    private RasterDataNode sourceViewAzimuth;

    private RasterDataNode sourceAltitude;

    private Band targetBandCloudShadow;

    private static int tileid;

    static int mincloudBase = 100;
    static int maxcloudTop = 10000;
    //for calculating a single cloud path
    private float sunZenithMean;
    private float sunAzimuthMean;
    private float viewAzimuthMean;
    private float viewZenithMean;
    private float minAltitude =0;

    //map for the different tiles: meanReflectance per offset.
    private Map<Integer, double[][]> meanReflPerTile = new HashMap<>();
    private Map<Integer, Integer> NCloudOverLand = new HashMap<>();
    private Map<Integer, Integer> NCloudOverWater = new HashMap<>();
    private Map<Integer, Integer> NValidPixelTile = new HashMap<>();

    public Point2D[] cloudPath;

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
    private static final String sourceViewAzimuthName = "view_azimuth_mean";
    private static final String sourceViewZenithName = "view_zenith_mean";
    private static final String sourceAltitudeName = "elevation";
    private static final String sourceFlagName1 = "pixel_classif_flags";
    private final static String BAND_NAME_CLOUD_SHADOW = "FlagBand";
    
    private Mode analysisMode;

    private static final String F_INVALID_DESCR_TEXT = "Invalid pixels";
    private static final String F_CLOUD_DESCR_TEXT = "Cloud pixels";
    private static final String F_MOUNTAIN_SHADOW_DESCR_TEXT = "Mountain shadow pixels";
    private static final String F_CLOUD_SHADOW_DESCR_TEXT = "Cloud shadow pixels";
    private static final String F_LAND_DESCR_TEXT = "Land pixels";
    private static final String F_WATER_DESCR_TEXT = "Water pixels";
    private static final String F_HAZE_DESCR_TEXT = "Potential haze/semitransparent cloud pixels";
    private static final String F_POTENTIAL_CLOUD_SHADOW_DESCR_TEXT = "Potential cloud shadow pixels";
    private static final String F_SHIFTED_CLOUD_SHADOW_DESCR_TEXT = "Shifted cloud mask as shadow pixels";
    private static final String F_CLOUD_SHADOW_COMB_DESCR_TEXT = "cloud mask (combination)";
    private static final String F_CLOUD_BUFFER_DESCR_TEXT = "Cloud buffer";
    private static final String F_SHIFTED_CLOUD_SHADOW_GAPS_DESCR_TEXT = "shifted cloud mask in cloud gap";

    public static final int F_WATER = 0;
    public static final int F_LAND = 1;
    public static final int F_CLOUD = 2;
    public static final int F_HAZE = 3;
    public static final int F_CLOUD_SHADOW = 4;
    public static final int F_MOUNTAIN_SHADOW = 5;
    public static final int F_INVALID = 6;
    public static final int F_CLOUD_BUFFER = 7;
    public static final int F_POTENTIAL_CLOUD_SHADOW = 8;
    public static final int F_SHIFTED_CLOUD_SHADOW = 9;
    public static final int F_CLOUD_SHADOW_COMB = 10;
    public static final int F_SHIFTED_CLOUD_SHADOW_GAPS = 11;

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
        sourceViewAzimuth = s2ClassifProduct.getBand(sourceViewAzimuthName);
        sourceViewZenith = s2ClassifProduct.getBand(sourceViewZenithName);
        sourceAltitude = s2ClassifProduct.getBand(sourceAltitudeName);

        final GeoPos centerGeoPos =
                getCenterGeoPos(s2ClassifProduct.getSceneGeoCoding(), s2ClassifProduct.getSceneRasterWidth(), s2ClassifProduct.getSceneRasterHeight());
        System.out.println(centerGeoPos.getLat());
        maxcloudTop = setCloudTopHeigh(centerGeoPos.getLat());
        System.out.println(maxcloudTop);

        //create a single potential cloud path for the granule.
        // sunZenithMean, sunAzimuthMean is the value at the central pixel.
        minAltitude = 0;
        sunZenithMean = getRasterNodeValueAtCenter(sourceSunZenith, s2ClassifProduct.getSceneRasterWidth(), s2ClassifProduct.getSceneRasterHeight());
        sunAzimuthMean = getRasterNodeValueAtCenter(sourceSunAzimuth, s2ClassifProduct.getSceneRasterWidth(), s2ClassifProduct.getSceneRasterHeight());
        viewAzimuthMean = getRasterNodeValueAtCenter(sourceViewAzimuth, s2ClassifProduct.getSceneRasterWidth(), s2ClassifProduct.getSceneRasterHeight());
        viewZenithMean = getRasterNodeValueAtCenter(sourceViewZenith,targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());

        sunAzimuthMean = convertToApparentSunAzimuth();

        sourceBandFlag1 = s2ClassifProduct.getBand(sourceFlagName1);
        if (s2CloudBufferProduct != null) {
            sourceBandFlag2 = s2CloudBufferProduct.getBand(sourceFlagName1);
        }

        targetBandCloudShadow = targetProduct.addBand(BAND_NAME_CLOUD_SHADOW, ProductData.TYPE_INT32);
        attachFlagCoding(targetBandCloudShadow);
        setupBitmasks(targetProduct);

        spatialResolution = determineSourceResolution();
        searchBorderRadius = (int) determineSearchBorderRadius(S2IdepixPreCloudShadowOp.spatialResolution,
                maximumSunZenith);
        final int tileWidth = determineSourceTileWidth(s2ClassifProduct.getSceneRasterWidth(), sourceTileWidth,
                searchBorderRadius, searchBorderRadius);
        final int tileHeight = determineSourceTileHeight(s2ClassifProduct.getSceneRasterHeight(), sourceTileHeight,
                searchBorderRadius, searchBorderRadius);
        tileid = 0;
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

    //aus S2tbxReprojectionOp kopiert:
    private GeoPos getCenterGeoPos(GeoCoding geoCoding, int width, int height) {
        final PixelPos centerPixelPos = new PixelPos(0.5 * width + 0.5,
                0.5 * height + 0.5);
        return geoCoding.getGeoPos(centerPixelPos, null);
    }

    private float getRasterNodeValueAtCenter(RasterDataNode var, int width, int height) {
        return var.getSampleFloat( (int) (0.5 * width) , (int) (0.5 * height) );
    }

    private int setCloudTopHeigh(double lat){
        return (int) Math.ceil(0.5* Math.pow(90.-Math.abs(lat), 2.) + (90.-Math.abs(lat))*25 + 5000);
    }
    /*
    package local for testing
     */
    @SuppressWarnings("WeakerAccess")
    Rectangle getSourceRectangle(Rectangle targetRectangle, Point2D[] relativePath) {
        final int productWidth = getSourceProduct().getSceneRasterWidth();
        final int productHeight = getSourceProduct().getSceneRasterHeight();
        final int relativeX = (int) relativePath[relativePath.length - 1].getX();
        final int relativeY = (int) relativePath[relativePath.length - 1].getY();

        // borders are now extended in both directions left-right, top-down.
        // so it needs a reduction in x0,y0 and addition in x1,y1
        int x0 = Math.max(0, targetRectangle.x + Math.min(0, -1*Math.abs(relativeX)));
        int y0 = Math.max(0, targetRectangle.y + Math.min(0, -1*Math.abs(relativeY)));
        int x1 = Math.min(productWidth, targetRectangle.x + targetRectangle.width + Math.max(0, Math.abs(relativeX)));
        int y1 = Math.min(productHeight, targetRectangle.y + targetRectangle.height + Math.max(0, Math.abs(relativeY)));
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

    float convertToApparentSunAzimuth(){
        //here: cloud path is calculated for center pixel sunZenith and sunAzimuth.
        // after correction of sun azimuth angle into apparent sun azimuth angle.
        // Due to projection of the cloud at view_zenith>0 the position of the cloud becomes distorted.
        // The true position still causes the shadow - and it cannot be determined without the cloud top height.
        // So instead, the apparent sun azimuth angle is calculated and used to find the cloudShadowRelativePath.

        double diff_phi = sunAzimuthMean - viewAzimuthMean;
        if(diff_phi < 0) diff_phi = 180 + diff_phi;
        if(diff_phi > 90) diff_phi = diff_phi - 90;
        diff_phi = diff_phi * Math.tan(viewZenithMean*MathUtils.DTOR);
        if(viewAzimuthMean>180) diff_phi= -1.*diff_phi;

        System.out.println("viewAzimuthMean " + viewAzimuthMean);
        System.out.println("viewZenithMean " + viewZenithMean);

        System.out.println("diff_phi " + diff_phi);
        return (float) (sunAzimuthMean + diff_phi);
    }

    int testCloudInTile(int[] flagArray){
        int out=0;
        for( int i=0; i<flagArray.length; i++){
            if (((flagArray[i] & PreparationMaskBand.CLOUD_FLAG) == PreparationMaskBand.CLOUD_FLAG)) {
                out = 1;
                break;
            }
        }
        return out;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws OperatorException {

        final float[] targetAltitude = getSamples(sourceAltitude, targetRectangle);
        final List<Float> altitudes = Arrays.asList(ArrayUtils.toObject(targetAltitude));
        /*float minAltitude = Math.max(0, Collections.min(altitudes));
        if (Float.isNaN(minAltitude)) {
            minAltitude = 0;
        }*/

        //here: cloud path is calculated for center pixel sunZenith and sunAzimuth.
        final Point2D[] cloudShadowRelativePath = CloudShadowUtils.getRelativePath(
                minAltitude, sunZenithMean * MathUtils.DTOR, sunAzimuthMean * MathUtils.DTOR, maxcloudTop,
                targetRectangle, targetRectangle, getSourceProduct().getSceneRasterHeight(),
                getSourceProduct().getSceneRasterWidth(), spatialResolution, true, false);

        cloudPath = cloudShadowRelativePath;

        final Rectangle sourceRectangle = getSourceRectangle(targetRectangle, cloudShadowRelativePath);

        int sourceLength = sourceRectangle.width * sourceRectangle.height;

        final int[] flagArray = new int[sourceLength];
        final int mytileid = tileid++;
        
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

        int numClouds = 1;
        //int numClouds = testCloudInTile(flagArray);

        if (numClouds > 0) {

            //all land cover types
            /*final ShiftingCloudBULKalongCloudPath cloudTest = new ShiftingCloudBULKalongCloudPath();
            cloudTest.ShiftingCloudBULKalongCloudPath(sourceRectangle, targetRectangle, sunZenithMean, sunAzimuthMean, clusterData, flagArray, cloudShadowRelativePath);

            meanReflPerTile.put(mytileid, cloudTest.getMeanReflectanceAlongPath());
            */

            //statistics by type: 0: all, 1: over land, 2: over water
            final ShiftingCloudBulkAlongCloudPathType cloudTest = new ShiftingCloudBulkAlongCloudPathType();
            cloudTest.ShiftingCloudBulkAlongCloudPathType(sourceRectangle, targetRectangle, sunZenithMean, sunAzimuthMean, clusterData, flagArray, cloudShadowRelativePath);

            meanReflPerTile.put(mytileid, cloudTest.getMeanReflectanceAlongPath());
            NCloudOverLand.put(mytileid, cloudTest.getNCloudOverLand());
            NCloudOverWater.put(mytileid, cloudTest.getNCloudOverWater());
            NValidPixelTile.put(mytileid, cloudTest.getNValidPixel());
        }
        
    }

    /*
    public Map<Integer, double[]> getMeanReflPerTile() {
        return meanReflPerTile;
    }*/

    public Map<Integer, double[][]> getMeanReflPerTile() {
        return meanReflPerTile;
    }

    public Map<Integer, Integer> getNCloudOverLandPerTile() {
        return NCloudOverLand;
    }
    public Map<Integer, Integer> getNCloudOverWaterPerTile() {
        return NCloudOverWater;
    }
    public Map<Integer, Integer> getNValidPixelTile() {
        return NValidPixelTile;
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
        cloudCoding.addFlag("potential_cloud_shadow", BitSetter.setFlag(0, F_POTENTIAL_CLOUD_SHADOW),
                F_POTENTIAL_CLOUD_SHADOW_DESCR_TEXT);
        cloudCoding.addFlag("shifted_cloud_shadow", BitSetter.setFlag(0, F_SHIFTED_CLOUD_SHADOW),
                F_SHIFTED_CLOUD_SHADOW_DESCR_TEXT);
        cloudCoding.addFlag("cloud_shadow_comb", BitSetter.setFlag(0, F_CLOUD_SHADOW_COMB),
                F_CLOUD_SHADOW_COMB_DESCR_TEXT);
        cloudCoding.addFlag("shifted_cloud_shadow_gaps", BitSetter.setFlag(0, F_SHIFTED_CLOUD_SHADOW_GAPS),
                F_SHIFTED_CLOUD_SHADOW_GAPS_DESCR_TEXT);
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
                Color.RED, 0.5f);
        targetProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("mountain_shadow",
                F_MOUNTAIN_SHADOW_DESCR_TEXT, w, h,
                "FlagBand.mountain_shadow",
                Color.PINK, 0.5f);
        targetProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("potential_cloud_shadow",
                F_POTENTIAL_CLOUD_SHADOW_DESCR_TEXT, w, h,
                "FlagBand.potential_cloud_shadow",
                Color.ORANGE, 0.5f);
        targetProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("shifted_cloud_shadow",
                F_SHIFTED_CLOUD_SHADOW_DESCR_TEXT, w, h,
                "FlagBand.shifted_cloud_shadow",
                Color.MAGENTA, 0.5f);
        targetProduct.getMaskGroup().add(index, mask);
        mask = Mask.BandMathsType.create("cloud_shadow_comb",
                F_CLOUD_SHADOW_COMB_DESCR_TEXT, w, h,
                "FlagBand.cloud_shadow_comb",
                Color.BLUE, 0.5f);
        targetProduct.getMaskGroup().add(index, mask);
        mask = Mask.BandMathsType.create("shifted_cloud_shadow_gaps",
                F_SHIFTED_CLOUD_SHADOW_GAPS_DESCR_TEXT, w, h,
                "FlagBand.shifted_cloud_shadow_gaps",
                Color.BLUE, 0.5f);
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

    private static class MountainShadowMaxFloatComparator implements Comparator<Float> {

        @Override
        public int compare(Float o1, Float o2) {
            if(Float.isNaN(o1) && Float.isNaN(o2)) {
                return 0;
            } else if (Float.isNaN(o1)) {
                return -1;
            } else if (Float.isNaN(o2)) {
                return 1;
            } else if (o1 < o2) {
                return -1;
            } else if (o2 > o1) {
                return 1;
            }
            return 0;
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixPreCloudShadowOp.class);
        }
    }

}
