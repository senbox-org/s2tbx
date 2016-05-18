package org.esa.s2tbx.s2msi.idepix.algorithms.sentinel2;

import org.esa.s2tbx.s2msi.idepix.util.IdepixConstants;
import org.esa.s2tbx.s2msi.idepix.util.IdepixUtils;
import org.esa.snap.core.util.math.MathUtils;

/**
 * IDEPIX pixel identification algorithm for Sentinel-2 (MSI instrument)
 *
 * @author olafd
 */
public class Sentinel2Algorithm {

    static final float UNCERTAINTY_VALUE = 0.5f;
    static final float LAND_THRESH = 0.9f;
    static final float WATER_THRESH = 0.9f;

    static final float BRIGHTWHITE_THRESH = 1.5f;
    static final float NDSI_THRESH = 0.6f;
//    static final float CLOUD_THRESH = 1.65f;   // changed back, 20160503 todo: further check how this works
//    static final float CLOUD_THRESH = 2.0f;  // OD, 20160422
    static final float CLOUD_THRESH = 1.8f;  // JM, 20160517
    static final float BRIGHT_THRESH = 0.25f;  // changed back, 20160503 todo: further check how this works
//    static final float BRIGHT_THRESH = 0.8f;  // OD, 20160422
    static final float BRIGHT_FOR_WHITE_THRESH = 0.8f;
    static final float WHITE_THRESH = 0.9f;
    static final float NDVI_THRESH = 0.5f;

    static final float TC_THRESH = 2100.0f; // what's this?
    static final float NDWI_THRESH = 0.25f;  // what's this?

    private float[] refl;
    private double brr442Thresh;
    private double[] nnOutput;
    private boolean isLand;


    public boolean isBrightWhite() {
        return !isInvalid() && (whiteValue() + brightValue() > getBrightWhiteThreshold());
    }

    public boolean isCloud() {
        boolean threshTest = whiteValue() + brightValue() + pressureValue() + temperatureValue() > CLOUD_THRESH;

//        return !isInvalid() && (threshTest && !isClearSnow());

        // JM, 20160517:
        boolean yellowTest = (Math.abs(refl[3]-refl[2]) < 700.0) && (refl[1] < Math.min(refl[2], refl[3]));
        boolean darkurbanTest = (Math.abs(refl[3]-refl[2]) < 500.0) && (Math.abs(refl[2]-refl[1]) < 500.0) &&
                ( (refl[1]+refl[2]+refl[3])/3.0 < 2000.0);

//        return !isInvalid() && (threshTest && !isClearSnow() && !yellowTest && !darkurbanTest);
        return !isInvalid() && (threshTest && !isClearSnow() && !yellowTest);

    }

    public boolean isClearLand() {
        if (isInvalid()) {
            return false;
        }
        float landValue;

        if (!MathUtils.equalValues(radiometricLandValue(), UNCERTAINTY_VALUE)) {
            landValue = radiometricLandValue();
        } else if (aPrioriLandValue() > UNCERTAINTY_VALUE) {
            landValue = aPrioriLandValue();
        } else {
            return false; // this means: if we have no information about land, we return isClearLand = false
        }
        return (isLand() && !isCloud() && landValue > LAND_THRESH);
    }

    public boolean isClearWater() {
        if (isInvalid()) {
            return false;
        }
        float waterValue;
        if (!MathUtils.equalValues(radiometricWaterValue(), UNCERTAINTY_VALUE)) {
            waterValue = radiometricWaterValue();
        } else if (aPrioriWaterValue() > UNCERTAINTY_VALUE) {
            waterValue = aPrioriWaterValue();
        } else {
            return false; // this means: if we have no information about water, we return isClearWater = false
        }
        return (!isLand() && !isCloud() && waterValue > WATER_THRESH);
    }

    public boolean isClearSnow() {
//        return (!isInvalid() && isLand() && isBrightWhite() && ndsiValue() > getNdsiThreshold());
        return (!isInvalid() && isLand() && ndsiValue() > getNdsiThreshold() &&
        !((ndwiValue() > getNdwiThreshold()) && (tcValue() < getTcThreshold() )));  // JM, 20160517
    }

    public boolean isSeaIce() {
        return false;
    }

    public boolean isLand() {
//        return aPrioriLandValue() > LAND_THRESH;
        return isLand;
    }

    public boolean isWater() {
//        return !isInvalid() && aPrioriWaterValue() > WATER_THRESH;  // todo: check again when we use SRTM water mask
        return !isInvalid() && !isLand();
    }

    public boolean isBright() {
        return brightValue() > getBrightThreshold();
    }

    public boolean isWhite() {
        return whiteValue() > getWhiteThreshold();
    }

    public boolean isVegRisk() {
        return ndviValue() > getNdviThreshold();
    }

    public boolean isHigh() {
        return false;
    }

    public boolean isInvalid() {
        return false;
    }

    // feature values
    public float tcValue() {
        // what is TC?
        return 0.3029f*refl[1] + 0.2786f*refl[2] + 0.4733f*refl[3] + 0.5599f*refl[8] + 0.508f*refl[11] + 0.1872f*refl[12];
    }

    public float ndwiValue() {
        // what is NDWI?
        return (refl[8] - refl[11]) / (refl[8] + refl[11]);
    }

    public float spectralFlatnessValue() {
        final double slope0 = IdepixUtils.spectralSlope(refl[1], refl[0],
                                                        IdepixConstants.S2_MSI_WAVELENGTHS[1],
                                                        IdepixConstants.S2_MSI_WAVELENGTHS[0]);
        final double slope1 = IdepixUtils.spectralSlope(refl[2], refl[3],
                                                        IdepixConstants.S2_MSI_WAVELENGTHS[2],
                                                        IdepixConstants.S2_MSI_WAVELENGTHS[3]);
        final double slope2 = IdepixUtils.spectralSlope(refl[4], refl[6],
                                                        IdepixConstants.S2_MSI_WAVELENGTHS[4],
                                                        IdepixConstants.S2_MSI_WAVELENGTHS[6]);

        final double flatness = 1.0f - Math.abs(1000.0 * (slope0 + slope1 + slope2) / 3.0);
        // todo: check if it should be like this:
//        final double flatness = 1.0f - Math.abs((slope0 + slope1 + slope2) / (3.0*1000.0));
        return (float) Math.max(0.0f, flatness);
    }

    public float whiteValue() {
        if (brightValue() > BRIGHT_FOR_WHITE_THRESH) {
            return spectralFlatnessValue();
        } else {
            return 0.0f;
        }
    }

    public float brightValue() {
        if (refl[0] <= 0.0 || brr442Thresh <= 0.0) {
            return IdepixConstants.NO_DATA_VALUE;
        } else {
            return (float) (refl[0] / (6.0 * brr442Thresh));
        }
    }

    public float ndsiValue() {
        return (refl[2] - refl[11]) / (refl[2] + refl[11]);
    }

    public float ndviValue() {
        double value = (refl[8] - refl[3]) / (refl[8] + refl[3]);
        value = 0.5 * (value + 1);
        value = Math.min(value, 1.0);
        value = Math.max(value, 0.0);
        return (float) value;
    }

    public float pressureValue() {
        return UNCERTAINTY_VALUE;
    }

    public float aPrioriLandValue() {
        return radiometricLandValue();
    }

    public float aPrioriWaterValue() {
        return radiometricWaterValue();
    }


    // SETTERS
    public void setRhoToa442Thresh(double brr442Thresh) {
        this.brr442Thresh = brr442Thresh;
    }

    public void setRefl(float[] refl) {
        this.refl = refl;
    }

    public void setNnOutput(double[] nnOutput) {
        this.nnOutput = nnOutput;
    }

    public void setIsLand(boolean isLand) {
        this.isLand = isLand;
    }

    // GETTERS
    public float getNdsiThreshold() {
        return NDSI_THRESH;
    }

    public float getNdviThreshold() {
        return NDVI_THRESH;
    }

    public float getNdwiThreshold() {
        return NDWI_THRESH;
    }

    public float getTcThreshold() {
        return TC_THRESH;
    }

    public float getBrightThreshold() {
        return BRIGHT_THRESH;
    }

    public float getWhiteThreshold() {
        return WHITE_THRESH;
    }

    public double[] getNnOutput() {
        return nnOutput;
    }

    public float temperatureValue() {
        return UNCERTAINTY_VALUE;
    }

    public float radiometricLandValue() {
        if (refl[5] >= refl[3]) {        // todo: what is refl620, refl620thresh ?
            return 1.0f;
        } else {
            return UNCERTAINTY_VALUE;
        }
    }

    public float radiometricWaterValue() {
        if (refl[5] < refl[3]) {        // todo: what is refl620, refl620thresh ?
            return 1.0f;
        } else {
            return UNCERTAINTY_VALUE;
        }
    }

    public float getBrightWhiteThreshold() {
        return BRIGHTWHITE_THRESH;
    }

    public float glintRiskValue() {
        return UNCERTAINTY_VALUE;
    }

}
