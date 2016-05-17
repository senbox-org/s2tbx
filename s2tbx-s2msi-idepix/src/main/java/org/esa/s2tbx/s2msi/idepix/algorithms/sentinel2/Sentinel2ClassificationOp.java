package org.esa.s2tbx.s2msi.idepix.algorithms.sentinel2;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.idepix.util.IdepixConstants;
import org.esa.s2tbx.s2msi.idepix.util.IdepixUtils;
import org.esa.s2tbx.s2msi.idepix.util.SchillerNeuralNetWrapper;
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
import org.esa.snap.core.util.math.MathUtils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Sentinel-2 (MSI) pixel classification operator.
 *
 * @author olafd
 */
@OperatorMetadata(alias = "Idepix.Sentinel2.Classification",
        version = "2.2",
        internal = true,
        authors = "Olaf Danne",
        copyright = "(c) 2008, 2012 by Brockmann Consult",
        description = "Operator for pixel classification from Sentinel-2 MSI data.")
public class Sentinel2ClassificationOp extends Operator {

    public static final double DELTA_RHO_TOA_442_THRESHOLD = 0.03;
    public static final double RHO_TOA_442_THRESHOLD = 0.03;

    @Parameter(defaultValue = "true",
            label = " Write TOA Reflectances to the target product",
            description = " Write TOA Reflectances to the target product")
    private boolean copyToaReflectances;

    @Parameter(defaultValue = "true",
            label = " Write Feature Values to the target product",
            description = " Write all Feature Values to the target product")
    private boolean copyFeatureValues;

    // NN stuff is deactivated unless we have a better net

    //    @Parameter(defaultValue = "1.95",
//            label = " NN cloud ambiguous lower boundary",
//            description = " NN cloud ambiguous lower boundary")
//    private double nnCloudAmbiguousLowerBoundaryValue;
    private double nnCloudAmbiguousLowerBoundaryValue = 1.95;

    //    @Parameter(defaultValue = "3.45",
//            label = " NN cloud ambiguous/sure separation value",
//            description = " NN cloud ambiguous cloud ambiguous/sure separation value")
//    private double nnCloudAmbiguousSureSeparationValue;
    private double nnCloudAmbiguousSureSeparationValue = 3.45;

    //    @Parameter(defaultValue = "4.3",
//            label = " NN cloud sure/snow separation value",
//            description = " NN cloud ambiguous cloud sure/snow separation value")
//    private double nnCloudSureSnowSeparationValue;
    private double nnCloudSureSnowSeparationValue = 4.3;

    //    @Parameter(defaultValue = "false",
//            label = " Apply NN for pixel classification purely (not combined with feature value approach)",
//            description = " Apply NN for pixelclassification purely (not combined with feature value  approach)")
//    private boolean applyNNPure;
    private boolean applyNNPure = false;

    //    @Parameter(defaultValue = "false",
//            label = " Ignore NN and only use feature value approach for pixel classification (if set, overrides previous option)",
//            description = " Ignore NN and only use feature value approach for pixel classification (if set, overrides previous option)")
//    private boolean ignoreNN;
    boolean ignoreNN = true;       // currently bad results. Wait for better S2 NN.

    //    @Parameter(defaultValue = "true",
//            label = " Write NN output value to the target product",
//            description = " Write NN output value to the target product")
//    private boolean copyNNValue = true;
    private boolean copyNNValue = false;


    @SourceProduct(alias = "l1c", description = "The MSI L1C source product.")
    Product sourceProduct;

    @TargetProduct(description = "The target product.")
    Product targetProduct;

    private Band[] s2MsiReflBands;
    Band classifFlagBand;

    Band szaBand;
    Band vzaBand;
    Band saaBand;
    Band vaaBand;

    // features:
    Band temperatureBand;
    Band brightBand;
    Band whiteBand;
    Band brightWhiteBand;
    Band spectralFlatnessBand;
    Band ndviBand;
    Band ndsiBand;
    Band glintRiskBand;
    Band radioLandBand;
    Band radioWaterBand;


    public static final String NN_NAME = "20x4x2_1012.9.net";    // Landsat 'all' NN
    ThreadLocal<SchillerNeuralNetWrapper> neuralNet;


    @Override
    public void initialize() throws OperatorException {
        setBands();
        readSchillerNeuralNets();
        createTargetProduct();
        extendTargetProduct();

    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {

        Tile[] s2ReflectanceTiles = new Tile[IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES.length];
        float[] s2MsiReflectance = new float[IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES.length];
        for (int i = 0; i < IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES.length; i++) {
            s2ReflectanceTiles[i] = getSourceTile(s2MsiReflBands[i], rectangle);
        }

        GeoPos geoPos = null;
        final Band cloudFlagTargetBand = targetProduct.getBand(IdepixUtils.IDEPIX_CLASSIF_FLAGS);
        final Tile cloudFlagTargetTile = targetTiles.get(cloudFlagTargetBand);

        final Tile szaTile = getSourceTile(szaBand, rectangle);
        final Tile vzaTile = getSourceTile(vzaBand, rectangle);
        final Tile saaTile = getSourceTile(saaBand, rectangle);
        final Tile vaaTile = getSourceTile(vaaBand, rectangle);

        final Band nnTargetBand = targetProduct.getBand("nn_value");
        final Tile nnTargetTile = targetTiles.get(nnTargetBand);
        try {
            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                checkForCancellation();
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    // todo: later:
//                    byte waterMaskSample = WatermaskClassifier.INVALID_VALUE;
//                    byte waterMaskFraction = WatermaskClassifier.INVALID_VALUE;
//                    if (!gaUseL1bLandWaterFlag) {
//                        final GeoCoding geoCoding = sourceProduct.getGeoCoding();
//                        if (geoCoding.canGetGeoPos()) {
//                            geoPos = geoCoding.getGeoPos(new PixelPos(x, y), geoPos);
//                            waterMaskSample = strategy.getWatermaskSample(geoPos.lat, geoPos.lon);
//                            waterMaskFraction = strategy.getWatermaskFraction(geoCoding, x, y);
//                        }
//                    }

                    // set up pixel properties for given instruments...
                    Sentinel2Algorithm s2MsiAlgorithm = createS2MsiAlgorithm(s2ReflectanceTiles,
                                                                             szaTile, vzaTile, saaTile, vaaTile,
                                                                             s2MsiReflectance,
                                                                             y,
                                                                             x);

                    setCloudFlag(cloudFlagTargetTile, y, x, s2MsiAlgorithm);

                    // apply improvement from NN approach...
                    final double[] nnOutput = s2MsiAlgorithm.getNnOutput();

                    if (!ignoreNN) {
                        if (applyNNPure) {
                            // 'pure Schiller'
                            if (!cloudFlagTargetTile.getSampleBit(x, y, IdepixConstants.F_INVALID)) {
                                cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD_AMBIGUOUS, false);
                                cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD_SURE, false);
                                cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD, false);
                                cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLEAR_SNOW, false);
                                if (nnOutput[0] > nnCloudAmbiguousLowerBoundaryValue &&
                                        nnOutput[0] <= nnCloudAmbiguousSureSeparationValue) {
                                    // this would be as 'CLOUD_AMBIGUOUS'...
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD_AMBIGUOUS, true);
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD, true);
                                }
                                if (nnOutput[0] > nnCloudAmbiguousSureSeparationValue &&
                                        nnOutput[0] <= nnCloudSureSnowSeparationValue) {
                                    // this would be as 'CLOUD_SURE'...
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD_SURE, true);
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD, true);
                                }
                                if (nnOutput[0] > nnCloudSureSnowSeparationValue) {
                                    // this would be as 'SNOW/ICE'...
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLEAR_SNOW, true);
                                }
                            }
                        } else {
                            // just 'refinement with Schiller', as with old net. // todo: what do we want??
                            if (!cloudFlagTargetTile.getSampleBit(x, y, IdepixConstants.F_CLOUD) &&
                                    !cloudFlagTargetTile.getSampleBit(x, y, IdepixConstants.F_CLOUD_SURE)) {
                                if (nnOutput[0] > nnCloudAmbiguousLowerBoundaryValue &&
                                        nnOutput[0] <= nnCloudAmbiguousSureSeparationValue) {
                                    // this would be as 'CLOUD_AMBIGUOUS' in CC and makes many coastlines as cloud...
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD_AMBIGUOUS, true);
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD, true);
                                }
                                if (nnOutput[0] > nnCloudAmbiguousSureSeparationValue &&
                                        nnOutput[0] <= nnCloudSureSnowSeparationValue) {
                                    //   'CLOUD_SURE' as in CC (20140424, OD)
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD_SURE, true);
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD_AMBIGUOUS, false);
                                    cloudFlagTargetTile.setSample(x, y, IdepixConstants.F_CLOUD, true);
                                }
                            }
                        }
                    }
                    nnTargetTile.setSample(x, y, nnOutput[0]);

                    // for given instrument, compute more pixel properties and write to distinct band
                    for (Band band : targetProduct.getBands()) {
                        final Tile targetTile = targetTiles.get(band);
                        setPixelSamples(band, targetTile, y, x, s2MsiAlgorithm);
                    }
                }
            }

        } catch (Exception e) {
            throw new OperatorException("Failed to provide GA cloud screening:\n" + e.getMessage(), e);
        }
    }

    public void setBands() {
        s2MsiReflBands = new Band[IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES.length];
        for (int i = 0; i < IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES.length; i++) {
            s2MsiReflBands[i] = sourceProduct.getBand(IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES[i]);
        }

        szaBand = sourceProduct.getBand(IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[0]);
        vzaBand = sourceProduct.getBand(IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[1]);
        saaBand = sourceProduct.getBand(IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[2]);
        vaaBand = sourceProduct.getBand(IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[3]);
    }

    public void extendTargetProduct() throws OperatorException {
        if (copyToaReflectances) {
            copyReflectances();
        }

        if (copyNNValue) {
            targetProduct.addBand("nn_value", ProductData.TYPE_FLOAT32);
        }
    }

    private void copyReflectances() {
        for (int i = 0; i < IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES.length; i++) {
            final Band b = ProductUtils.copyBand(IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES[i], sourceProduct,
                                                 targetProduct, true);
            b.setUnit("dl");
        }
    }

    private Sentinel2Algorithm createS2MsiAlgorithm(Tile[] s2MsiReflectanceTiles,
                                                    Tile szaTile, Tile vzaTile, Tile saaTile, Tile vaaTile,
                                                    float[] s2MsiReflectances,
                                                    int y,
                                                    int x) {
        Sentinel2Algorithm s2MsiAlgorithm = new Sentinel2Algorithm();

        for (int i = 0; i < IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES.length; i++) {
            s2MsiReflectances[i] = s2MsiReflectanceTiles[i].getSampleFloat(x, y)/10000.0f;
        }
        s2MsiAlgorithm.setRefl(s2MsiReflectances);

        if (x == 275 && y == 1145)  {
            System.out.println("x,y = " + x + "," + y);
        }

        final double sza = szaTile.getSampleDouble(x, y);
        final double vza = vzaTile.getSampleDouble(x, y);
        final double saa = saaTile.getSampleDouble(x, y);
        final double vaa = vaaTile.getSampleDouble(x, y);
        final double rhoToa442Thresh = calcRhoToa442ThresholdTerm(sza, vza, saa, vaa);
        s2MsiAlgorithm.setRhoToa442Thresh(rhoToa442Thresh);

        SchillerNeuralNetWrapper nnWrapper = neuralNet.get();
        double[] inputVector = nnWrapper.getInputVector();

        float[] s2ToLandsatReflectances = mapToLandsatReflectances(s2MsiReflectances, inputVector);
        for (int i = 0; i < inputVector.length; i++) {
            inputVector[i] = Math.sqrt(s2ToLandsatReflectances[i]);
        }
        s2MsiAlgorithm.setNnOutput(nnWrapper.getNeuralNet().calc(inputVector));

//        final boolean isLand = watermaskFraction < WATERMASK_FRACTION_THRESH;
//        s2MsiAlgorithm.setL1FlagLand(isLand);
//        setIsWaterByFraction(watermaskFraction, s2MsiAlgorithm);

        return s2MsiAlgorithm;
    }

    private float[] mapToLandsatReflectances(float[] s2MsiReflectances, double[] inputVector) {
        //        the net has 8 inputs:
        //        input  1 is SQRT_coastal_aerosol in [0.255898,1.388849]
        //        input  2 is SQRT_blue in [0.221542,1.479245]
        //        input  3 is SQRT_green in [0.170573,1.543012]
        //        input  4 is SQRT_red in [0.125654,1.678217]
        //        input  5 is SQRT_near_infrared in [0.082347,1.775742]
        //        input  6 is SQRT_swir_1 in [0.032031,1.356978]
        //        input  7 is SQRT_swir_2 in [0.008660,1.840141]
        //        input  8 is SQRT_cirrus in [0.000000,0.878521]

        // L1 --> B1         440/443
        // L2 --> B2         480/490
        // L3 --> B3         560/560
        // L4 --> B4         655/665
        // L5 --> B8A        865/865
        // L6 --> B11        1610/1610
        // L7 --> B12        2200/2190
        // L9 --> B10        1370/1375

        float[] mappedToLandsatReflectances = new float[inputVector.length];
        if (inputVector.length < 8) {
            throw new OperatorException("Incompatible NN: " + NN_NAME + " - cannot continue.");
        }
        System.arraycopy(s2MsiReflectances, 0, mappedToLandsatReflectances, 0, 4);
        mappedToLandsatReflectances[4] = s2MsiReflectances[8];
        mappedToLandsatReflectances[5] = s2MsiReflectances[11];
        mappedToLandsatReflectances[6] = s2MsiReflectances[12];
        mappedToLandsatReflectances[7] = s2MsiReflectances[10];

        return mappedToLandsatReflectances;
    }

    private double calcRhoToa442ThresholdTerm(double sza, double vza, double saa, double vaa) {
        final double thetaScatt = IdepixUtils.calcScatteringAngle(sza, vza, saa, vaa) * MathUtils.DTOR;
        double cosThetaScatt = Math.cos(thetaScatt);
        return RHO_TOA_442_THRESHOLD + DELTA_RHO_TOA_442_THRESHOLD * cosThetaScatt * cosThetaScatt;
    }



    private void readSchillerNeuralNets() {
        try (InputStream merisLandIS = getClass().getResourceAsStream(NN_NAME)) {
            neuralNet = SchillerNeuralNetWrapper.create(merisLandIS);
        } catch (IOException e) {
            throw new OperatorException("Cannot read Neural Nets: " + e.getMessage());
        }
    }

    void createTargetProduct() throws OperatorException {
        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();

        targetProduct = new Product(sourceProduct.getName(), sourceProduct.getProductType(), sceneWidth, sceneHeight);

        classifFlagBand = targetProduct.addBand(IdepixUtils.IDEPIX_CLASSIF_FLAGS, ProductData.TYPE_INT32);
        FlagCoding flagCoding = IdepixUtils.createIdepixFlagCoding(IdepixUtils.IDEPIX_CLASSIF_FLAGS);
        classifFlagBand.setSampleCoding(flagCoding);
        targetProduct.getFlagCodingGroup().add(flagCoding);

        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);

        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        targetProduct.setStartTime(sourceProduct.getStartTime());
        targetProduct.setEndTime(sourceProduct.getEndTime());
        ProductUtils.copyMetadata(sourceProduct, targetProduct);

        if (copyFeatureValues) {
            brightBand = targetProduct.addBand("bright_value", ProductData.TYPE_FLOAT32);
            IdepixUtils.setNewBandProperties(brightBand, "Brightness", "dl", IdepixConstants.NO_DATA_VALUE, true);
            whiteBand = targetProduct.addBand("white_value", ProductData.TYPE_FLOAT32);
            IdepixUtils.setNewBandProperties(whiteBand, "Whiteness", "dl", IdepixConstants.NO_DATA_VALUE, true);
            brightWhiteBand = targetProduct.addBand("bright_white_value", ProductData.TYPE_FLOAT32);
            IdepixUtils.setNewBandProperties(brightWhiteBand, "Brightwhiteness", "dl", IdepixConstants.NO_DATA_VALUE,
                                             true);
//            temperatureBand = targetProduct.addBand("temperature_value", ProductData.TYPE_FLOAT32);
//            IdepixUtils.setNewBandProperties(temperatureBand, "Temperature", "K", IdepixConstants.NO_DATA_VALUE, true);
            spectralFlatnessBand = targetProduct.addBand("spectral_flatness_value", ProductData.TYPE_FLOAT32);
            IdepixUtils.setNewBandProperties(spectralFlatnessBand, "Spectral Flatness", "dl",
                                             IdepixConstants.NO_DATA_VALUE, true);
            ndviBand = targetProduct.addBand("ndvi_value", ProductData.TYPE_FLOAT32);
            IdepixUtils.setNewBandProperties(ndviBand, "NDVI", "dl", IdepixConstants.NO_DATA_VALUE, true);
            ndsiBand = targetProduct.addBand("ndsi_value", ProductData.TYPE_FLOAT32);
            IdepixUtils.setNewBandProperties(ndsiBand, "NDSI", "dl", IdepixConstants.NO_DATA_VALUE, true);
//            glintRiskBand = targetProduct.addBand("glint_risk_value", ProductData.TYPE_FLOAT32);
//            IdepixUtils.setNewBandProperties(glintRiskBand, "GLINT_RISK", "dl", IdepixConstants.NO_DATA_VALUE, true);
            radioLandBand = targetProduct.addBand("radiometric_land_value", ProductData.TYPE_FLOAT32);
            IdepixUtils.setNewBandProperties(radioLandBand, "Radiometric Land Value", "", IdepixConstants.NO_DATA_VALUE,
                                             true);
            radioWaterBand = targetProduct.addBand("radiometric_water_value", ProductData.TYPE_FLOAT32);
            IdepixUtils.setNewBandProperties(radioWaterBand, "Radiometric Water Value", "",
                                             IdepixConstants.NO_DATA_VALUE, true);
        }

    }

    void setPixelSamples(Band band, Tile targetTile, int y, int x, Sentinel2Algorithm s2Algorithm) {
        // for given instrument, compute more pixel properties and write to distinct band
        if (band == brightBand) {
            targetTile.setSample(x, y, s2Algorithm.brightValue());
        } else if (band == whiteBand) {
            targetTile.setSample(x, y, s2Algorithm.whiteValue());
        } else if (band == brightWhiteBand) {
            targetTile.setSample(x, y, s2Algorithm.brightValue() + s2Algorithm.whiteValue());
        } else if (band == temperatureBand) {
            targetTile.setSample(x, y, s2Algorithm.temperatureValue());
        } else if (band == spectralFlatnessBand) {
            targetTile.setSample(x, y, s2Algorithm.spectralFlatnessValue());
        } else if (band == ndviBand) {
            targetTile.setSample(x, y, s2Algorithm.ndviValue());
        } else if (band == ndsiBand) {
            targetTile.setSample(x, y, s2Algorithm.ndsiValue());
        } else if (band == glintRiskBand) {
            targetTile.setSample(x, y, s2Algorithm.glintRiskValue());
        } else if (band == radioLandBand) {
            targetTile.setSample(x, y, s2Algorithm.radiometricLandValue());
        } else if (band == radioWaterBand) {
            targetTile.setSample(x, y, s2Algorithm.radiometricWaterValue());
        }
    }

    void setCloudFlag(Tile targetTile, int y, int x, Sentinel2Algorithm s2Algorithm) {
        // for given instrument, compute boolean pixel properties and write to cloud flag band
        targetTile.setSample(x, y, IdepixConstants.F_INVALID, s2Algorithm.isInvalid());
        targetTile.setSample(x, y, IdepixConstants.F_CLOUD, s2Algorithm.isCloud());
        targetTile.setSample(x, y, IdepixConstants.F_CLOUD_SURE, s2Algorithm.isCloud());
        targetTile.setSample(x, y, IdepixConstants.F_CLOUD_SHADOW, false); // not computed here
        targetTile.setSample(x, y, IdepixConstants.F_CLEAR_LAND, s2Algorithm.isClearLand());
        targetTile.setSample(x, y, IdepixConstants.F_CLEAR_WATER, s2Algorithm.isClearWater());
        targetTile.setSample(x, y, IdepixConstants.F_CLEAR_SNOW, s2Algorithm.isClearSnow());
        targetTile.setSample(x, y, IdepixConstants.F_LAND, s2Algorithm.isLand());
        targetTile.setSample(x, y, IdepixConstants.F_WATER, s2Algorithm.isWater());
        targetTile.setSample(x, y, IdepixConstants.F_BRIGHT, s2Algorithm.isBright());
        targetTile.setSample(x, y, IdepixConstants.F_WHITE, s2Algorithm.isWhite());
        targetTile.setSample(x, y, IdepixConstants.F_BRIGHTWHITE, s2Algorithm.isBrightWhite());
        targetTile.setSample(x, y, IdepixConstants.F_HIGH, s2Algorithm.isHigh());
        targetTile.setSample(x, y, IdepixConstants.F_VEG_RISK, s2Algorithm.isVegRisk());
        targetTile.setSample(x, y, IdepixConstants.F_SEAICE, s2Algorithm.isSeaIce());
    }

    /**
     * The Service Provider Interface (SPI) for the operator.
     * It provides operator meta-data and is a factory for new operator instances.
     */
    public static class Spi extends OperatorSpi {

        public Spi() {
            super(Sentinel2ClassificationOp.class);
        }
    }
}
