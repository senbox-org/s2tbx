package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import com.bc.ceres.core.ProgressMonitor;
import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.BitSetter;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.math.MathUtils;
import org.opengis.referencing.operation.MathTransform;

import javax.media.jai.BorderExtenderConstant;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * @author Tonio Fincke, Dagmar Müller
 */
@OperatorMetadata(alias = "Idepix.Sentinel2.CloudShadow.Postprocess",
        category = "Optical",
        authors = "Grit Kirches, Michael Paperin, Olaf Danne, Tonio Fincke, Dagmar Müller",
        copyright = "(c) Brockmann Consult GmbH",
        version = "1.0",
        description = "Post-processing for algorithm detecting cloud shadow...")

public class S2IdepixPostCloudShadowOp extends Operator {

    @SourceProduct(description = "The classification product.")
    private Product s2ClassifProduct;

    @SourceProduct(alias = "s2CloudBuffer", optional = true)
    private Product s2CloudBufferProduct;      // has only classifFlagBand with buffer added

    /*
    @SourceProduct(alias = "sourceProduct", optional = true)
    private Product sourceProduct;      // has only classifFlagBand with buffer added
    */

    @TargetProduct
    private Product targetProduct;

    @Parameter(description = "The mode by which clouds are detected. There are three options: Land/Water, Multiple Bands" +
            "or Single Band", valueSet = {"LandWater", "MultiBand", "SingleBand"}, defaultValue = "LandWater")
    private String mode;

    @Parameter(description = "Whether to also compute mountain shadow", defaultValue = "true")
    private boolean computeMountainShadow;

    @Parameter(description = "Offset along cloud path to minimum reflectance (over all tiles)", defaultValue = "0")
    private int bestOffset;
    //private int[] bestOffset;

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
    private Band targetBandCloudID;
    private Band targetBandTileID;
    private Band targetBandShadowID;
    private Band targetBandCloudTest;

    private static int tileid;
    private Map<Integer, Rectangle> rectangleTile = new HashMap<>();

    static int mincloudBase = 100;
    private static int maxcloudTop = 10000;
    //for calculating a single cloud path
    private float sunZenithMean;
    private float sunAzimuthMean;
    private float viewAzimuthMean;
    private float viewZenithMean;
    private float minAltitude =0;

    //map for the different tiles: meanReflectance per offset.
    //public Map<Integer, double[]> meanReflPerTile = new HashMap<>();

    private static double spatialResolution;  //[m]
    static int clusterCountDefine = 4;
    static double OUTLIER_THRESHOLD = 0.94;
    static double Threshold_Whiteness_Darkness = -1000;
    static int GROWING_CLOUD = 1;
    private static int searchBorderRadius;
    private static final String sourceBandNameClusterA = "B8A";
    private static final String sourceBandNameClusterB = "B3";
    private static final String sourceSunZenithName = "sun_zenith";
    private static final String sourceSunAzimuthName = "sun_azimuth";
    private static final String sourceViewAzimuthName = "view_azimuth_mean";
    private static final String sourceViewZenithName = "view_zenith_mean";
    private static final String sourceAltitudeName = "elevation";
    private static final String sourceFlagName1 = "pixel_classif_flags";
    private final static String BAND_NAME_CLOUD_SHADOW = "FlagBand";
    private final static String BAND_NAME_CLOUD_ID = "cloud_ids";
    private final static String BAND_NAME_TILE_ID = "tile_ids";
    private final static String BAND_NAME_SHADOW_ID = "shadow_ids";
    private final static String BAND_NAME_CLOUD_TEST = "cloud_test";
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

    private static final int F_WATER = 0;
    private static final int F_LAND = 1;
    private static final int F_CLOUD = 2;
    private static final int F_HAZE = 3;
    private static final int F_CLOUD_SHADOW = 4;
    private static final int F_MOUNTAIN_SHADOW = 5;
    private static final int F_INVALID = 6;
    private static final int F_CLOUD_BUFFER = 7;
    private static final int F_POTENTIAL_CLOUD_SHADOW = 8;
    private static final int F_SHIFTED_CLOUD_SHADOW = 9;
    private static final int F_CLOUD_SHADOW_COMB = 10;
    private static final int F_SHIFTED_CLOUD_SHADOW_GAPS = 11;

    @Override
    public void initialize() throws OperatorException {

        targetProduct = new Product(s2ClassifProduct.getName(), s2ClassifProduct.getProductType(), s2ClassifProduct.getSceneRasterWidth(), s2ClassifProduct.getSceneRasterHeight());
        ProductUtils.copyGeoCoding(s2ClassifProduct, targetProduct);
        targetBandCloudShadow = targetProduct.addBand(BAND_NAME_CLOUD_SHADOW, ProductData.TYPE_INT32);
        targetBandCloudID = targetProduct.addBand(BAND_NAME_CLOUD_ID, ProductData.TYPE_INT32);
        targetBandTileID = targetProduct.addBand(BAND_NAME_TILE_ID, ProductData.TYPE_INT32);
        targetBandShadowID = targetProduct.addBand(BAND_NAME_SHADOW_ID, ProductData.TYPE_INT32);
        //targetBandCloudTest = targetProduct.addBand(BAND_NAME_CLOUD_TEST, ProductData.TYPE_INT32);
        targetBandCloudTest = targetProduct.addBand(BAND_NAME_CLOUD_TEST, ProductData.TYPE_FLOAT64);
        attachFlagCoding(targetBandCloudShadow);
        setupBitmasks(targetProduct);

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
        sourceViewAzimuth = s2ClassifProduct.getBand(sourceViewAzimuthName);
        sourceViewZenith = s2ClassifProduct.getBand(sourceViewZenithName);

        final GeoPos centerGeoPos =
                getCenterGeoPos(targetProduct.getSceneGeoCoding(), targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());
        System.out.println(centerGeoPos.getLat());
        maxcloudTop = setCloudTopHeigh(centerGeoPos.getLat());
        System.out.println(maxcloudTop);

        //create a single potential cloud path for the granule.
        // sunZenithMean, sunAzimuthMean is the value at the central pixel.
        minAltitude = 0;
        sunZenithMean = getRasterNodeValueAtCenter(sourceSunZenith, targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());
        sunAzimuthMean = getRasterNodeValueAtCenter(sourceSunAzimuth, targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());
        viewAzimuthMean = getRasterNodeValueAtCenter(sourceViewAzimuth,targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());
        viewZenithMean = getRasterNodeValueAtCenter(sourceViewZenith,targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());



        sunAzimuthMean = convertToApparentSunAzimuth();

        sourceBandFlag1 = s2ClassifProduct.getBand(sourceFlagName1);
        if (s2CloudBufferProduct != null) {
            sourceBandFlag2 = s2CloudBufferProduct.getBand(sourceFlagName1);
        }

        spatialResolution = determineSourceResolution();
        searchBorderRadius = (int) determineSearchBorderRadius(S2IdepixPostCloudShadowOp.spatialResolution,
                maximumSunZenith);
        final int tileWidth = determineSourceTileWidth(targetProduct.getSceneRasterWidth(), sourceTileWidth,
                searchBorderRadius, searchBorderRadius);
        final int tileHeight = determineSourceTileHeight(targetProduct.getSceneRasterHeight(), sourceTileHeight,
                searchBorderRadius, searchBorderRadius);
        tileid = 0;
        // todo: discuss
        if (targetProduct.getSceneRasterWidth() > tileWidth || targetProduct.getSceneRasterHeight() > tileHeight) {
            final int preferredTileWidth = Math.min(targetProduct.getSceneRasterWidth(), tileWidth);
            final int preferredTileHeight = Math.min(targetProduct.getSceneRasterHeight(), tileHeight);
            targetProduct.setPreferredTileSize(preferredTileWidth, preferredTileHeight); //1500
        } else {
            targetProduct.setPreferredTileSize(targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());
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

    //aus S2tbxReprojectionOp kopiert:
    private GeoPos getCenterGeoPos(GeoCoding geoCoding, int width, int height) {
        final PixelPos centerPixelPos = new PixelPos(0.5 * width + 0.5,
                0.5 * height + 0.5);
        return geoCoding.getGeoPos(centerPixelPos, null);
    }

    public float getRasterNodeValueAtCenter(RasterDataNode var, int width, int height) {
        return var.getSampleFloat( (int) (0.5 * width) , (int) (0.5 * height) );
    }

    private int setCloudTopHeigh(double lat){
        return (int) Math.ceil(0.5* Math.pow(90.-Math.abs(lat), 2.) + (90.-Math.abs(lat))*25 + 5000);
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
        cloudCoding.addFlag("cloud_buffer", BitSetter.setFlag(0, F_CLOUD_BUFFER),
                F_CLOUD_BUFFER_DESCR_TEXT);
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
        mask = Mask.BandMathsType.create("cloud_buffer",
                F_CLOUD_BUFFER_DESCR_TEXT, w, h,
                "FlagBand.cloud_buffer",
                Color.ORANGE, 0.5f);
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

    private static void fillTile(double[] inputData, Rectangle targetRectangle, Rectangle sourceRectangle, Tile targetTile) {
        for (int targetY = targetRectangle.y; targetY < targetRectangle.y + targetRectangle.height; targetY++) {
            int sourceY = targetY - sourceRectangle.y;
            for (int targetX = targetRectangle.x; targetX < targetRectangle.x + targetRectangle.width; targetX++) {
                int sourceX = targetX - sourceRectangle.x;
                int sourceIndex = sourceY * sourceRectangle.width + sourceX;
                targetTile.setSample(targetX, targetY, inputData[sourceIndex]);
            }
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

    private int determineSourceTileSize(int rasterSize, int tileSize, int borderExtension1, int borderExtension2) {
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

    private double determineSearchBorderRadius(double spatialResolution, double maxSunZenith) {
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

    Rectangle getSourceRectangleSquare(Rectangle targetRectangle, Point2D[] relativePath, int offset) {
        final int productWidth = getSourceProduct().getSceneRasterWidth();
        final int productHeight = getSourceProduct().getSceneRasterHeight();
        final int relativeX = (int) relativePath[offset].getX();
        final int relativeY = (int) relativePath[offset].getY();

        int border = (int) Math.sqrt(Math.pow(relativeX,2) + Math.pow(relativeY,2));
        // borders are now extended in all directions left-right, top-down.
        // still: the 3x3 sourceRectangles are only squares, if they are at the corners or at the center...

        int x0 = Math.max(0, targetRectangle.x - border);
        int y0 = Math.max(0, targetRectangle.y - border);
        int x1 = Math.min(productWidth, targetRectangle.x + targetRectangle.width + border);
        int y1 = Math.min(productHeight, targetRectangle.y + targetRectangle.height + border);
        return new Rectangle(x0, y0, x1 - x0, y1 - y0);
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

        /*System.out.println("viewAzimuthMean post " + viewAzimuthMean);
        System.out.println("viewZenithMean post " + viewZenithMean);
        System.out.println("diff_phi post " + diff_phi);*/

        return (float) (sunAzimuthMean + diff_phi);
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws OperatorException {

        final int mytileid = tileid;
        if(rectangleTile.isEmpty()){
            rectangleTile.put(mytileid, targetRectangle);
        }
        else {
            if(rectangleTile.containsValue(targetRectangle)){
                return;
            }
            else {
                rectangleTile.put(mytileid, targetRectangle);
            }
        }

        final float[] targetAltitude = getSamples(sourceAltitude, targetRectangle);
        final List<Float> altitudes = Arrays.asList(ArrayUtils.toObject(targetAltitude));



        final Point2D[] cloudShadowRelativePath = CloudShadowUtils.getRelativePath(
                minAltitude, sunZenithMean * MathUtils.DTOR, sunAzimuthMean  * MathUtils.DTOR, maxcloudTop,
                targetRectangle, targetRectangle, getSourceProduct().getSceneRasterHeight(),
                getSourceProduct().getSceneRasterWidth(), spatialResolution, true, false);

    //    System.out.println("average cloud height from offset: " + (Math.sqrt(Math.pow(cloudShadowRelativePath[bestOffset].getX(),2)
    //            +Math.pow(cloudShadowRelativePath[bestOffset].getY(),2))*60.)*Math.tan(sunZenithMean*MathUtils.DTOR));

        final Rectangle sourceRectangle = getSourceRectangle(targetRectangle, cloudShadowRelativePath);

        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int sourceLength = sourceRectangle.width * sourceRectangle.height;

        Tile targetTileCloudShadow = targetTiles.get(targetBandCloudShadow);
        Tile targetTileCloudID = targetTiles.get(targetBandCloudID);
        Tile targetTileTileID = targetTiles.get(targetBandTileID);
        Tile targetTileShadowID = targetTiles.get(targetBandShadowID);
        Tile targetTileCloudTest = targetTiles.get(targetBandCloudTest);

        final int[] flagArray = new int[sourceLength];
        //will be filled in SegmentationCloudClass Arrays.fill(cloudIdArray, ....);
        final int[] cloudIDArray = new int[sourceLength];
        final int[] tileIDArray = new int[sourceLength];
        final int[] shadowIDArray = new int[sourceLength];
        //final int[] cloudTestArray = new  int[sourceLength];
        final double[] cloudTestArray = new  double[sourceLength];
        Arrays.fill(tileIDArray, tileid++);

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

        PreparationMaskBand.prepareMaskBand(targetProduct.getSceneRasterWidth(),
                targetProduct.getSceneRasterHeight(), sourceRectangle, flagArray,
                flagDetector);

        if (computeMountainShadow) {
            float maxAltitude = Collections.max(altitudes, new S2IdepixPostCloudShadowOp.MountainShadowMaxFloatComparator());
            if (Float.isNaN(maxAltitude)) {
                maxAltitude = 0;
            }
            MountainShadowFlagger.flagMountainShadowArea(
                    s2ClassifProduct.getSceneRasterWidth(), s2ClassifProduct.getSceneRasterHeight(), sourceRectangle,
                    targetRectangle, sunZenithMean, sunAzimuthMean, altitude, flagArray, minAltitude, maxAltitude, cloudShadowRelativePath);
        }

        final FindContinuousAreas cloudIdentifier = new FindContinuousAreas(flagArray);
        Map<Integer, List<Integer>> cloudList = cloudIdentifier.computeAreaID(sourceWidth, sourceHeight, cloudIDArray, true);

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

        System.out.println("tile" + tileid + " " + cloudList.size());
        //if (numClouds > 0) {
        if (cloudList.size()>0){
            /*
            /   Clustering can be separated: potential cloud shadow over water, and over land.
            /   potentialShadowPositions: Collection of List of integers, which hold the index of potential cloud shadow pixels for each cloudID.
            /   offsetAtPotentialShadowPositions: Collection of List of integers, holding the step along the cloud shadow path in the potential cloud shadow. Useful to determine distances of clusters.
            */
            final Map[] results =
                    PotentialCloudShadowAreaIdentifier.identifyPotentialCloudShadowsPLUS(
                            sourceRectangle, targetRectangle, sunZenithMean, sunAzimuthMean, sourceLatitudes, sourceLongitudes,
                            altitude, flagArray, cloudIDArray, cloudShadowRelativePath);
            final Map<Integer, List<Integer>> potentialShadowPositions = results[0];
            final Map<Integer, List<Integer>> offsetAtPotentialShadow = results[1];


            System.out.println("[potential shadow is ready!]");

            // shifting by offset, but looking into water, land and all pixel. best offset is determined before (either from water, land, or all pixels).
            // only, if bestOffset > 0
            if(bestOffset >0) {
                final ShiftingCloudBulkAlongCloudPathType cloudTest = new ShiftingCloudBulkAlongCloudPathType();
                cloudTest.setTileShiftedCloudBULK(sourceRectangle, targetRectangle, sunAzimuthMean, flagArray, cloudShadowRelativePath, bestOffset); //offset: 0: all, 1: over land, 2: over water.

            }

            //combining information. clustered shadow is analysed for continuous areas.
            // shifting the shadow is done before and a correction is included.
            final CloudShadowFlaggerCombination cloudShadowFlagger = new CloudShadowFlaggerCombination();
            cloudShadowFlagger.flagCloudShadowAreas(clusterData, flagArray, potentialShadowPositions, offsetAtPotentialShadow, cloudList, bestOffset, analysisMode, sourceWidth, sourceHeight, shadowIDArray, cloudShadowRelativePath);

            //shifted cloud mask in cloud gaps.
            if(bestOffset>0){
                final CloudShadowFlaggerShiftInCloudGaps test = new CloudShadowFlaggerShiftInCloudGaps();
                test.setShiftedCloudInCloudGaps(sourceRectangle, flagArray, cloudShadowRelativePath, bestOffset, cloudList, cloudTestArray, spatialResolution);

            }

            /* Statistics are too bad to find the minimum correctly, if the clouds are shifted individually.
            final ShiftingCloudIndividualAlongCloudPath cloudIndividualTest = new ShiftingCloudIndividualAlongCloudPath();
            cloudIndividualTest.ShiftingCloudIndividualAlongCloudPath(sourceRectangle, targetRectangle, clusterData, flagArray, cloudShadowRelativePath, cloudList);
            */
        }


        fillTile(flagArray, targetRectangle, sourceRectangle, targetTileCloudShadow);
        fillTile(cloudIDArray, targetRectangle, sourceRectangle, targetTileCloudID);
        fillTile(tileIDArray, targetRectangle, sourceRectangle, targetTileTileID);
        fillTile(shadowIDArray, targetRectangle, sourceRectangle, targetTileShadowID);

        fillTile(cloudTestArray, targetRectangle, sourceRectangle, targetTileCloudTest);

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
            super(S2IdepixPostCloudShadowOp.class);
        }
    }

}
