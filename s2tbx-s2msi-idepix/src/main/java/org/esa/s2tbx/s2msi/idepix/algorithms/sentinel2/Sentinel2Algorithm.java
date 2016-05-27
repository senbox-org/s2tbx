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

    static final float NDWI_THRESH = 0.25f;  // what's this?

    static final float B3B11_THRESH = 1.0f;
    static final float TC1_THRESH = 0.36f;

    static final float GCW_THRESH = -0.1f;
    static final float TCW_TC_THRESH = -0.08f;
    static final float TCW_NDWI_THRESH = 0.4f;
    static final float CW_THRESH = 0.01f;
    static final float ELEVATION_THRESH = 2000.0f;
    static final float GCL_THRESH = -0.11f;
    static final float CL_THRESH = 0.01f;

    private float[] refl;
    private double brr442Thresh;
    private double elevation;
    private double[] nnOutput;
    private boolean isLand;

    public boolean isBrightWhite() {
        return !isInvalid() && (whiteValue() + brightValue() > getBrightWhiteThreshold());
    }

    public boolean isCloud() {
        // JM, 20160524:
        final boolean gcw = tc4CirrusValue()  < GCW_THRESH;
        final boolean tcw = tc4Value()  < TCW_TC_THRESH && ndwiValue() < TCW_NDWI_THRESH;
        final boolean cw = refl[10] > CW_THRESH && elevation > ELEVATION_THRESH;
        final boolean acw = isB3B11Water() && (gcw || tcw || cw);
        final boolean gcl = tc4CirrusValue()  < GCL_THRESH;
        final boolean cl = refl[10] > CL_THRESH && elevation > ELEVATION_THRESH;
        final boolean acl = ! isB3B11Water() && (gcl || cl);

        return !isInvalid() && !isClearSnow() && (acw || acl);

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
        return  !isInvalid() && isLand() &&
                ndsiValue() > getNdsiThreshold() &&
                !(isB3B11Water() && (tc1Value() < getTc1Threshold()));  // JM, 20160526
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

    public boolean isB3B11Water() {
        return b3b11Value() > B3B11_THRESH;
    }

    public boolean isInvalid() {
        return false;
    }

    // feature values
    public float b3b11Value() {
        return (refl[2] / refl[11]);
    }

    public float tc1Value() {
        return (0.3029f*refl[1] + 0.2786f*refl[2] + 0.4733f*refl[3] + 0.5599f*refl[8] + 0.508f*refl[11] + 0.1872f*refl[12]);
    }

    public float tc4Value() {
        return (-0.8239f*refl[1] + 0.0849f*refl[2] + 0.4396f*refl[3] - 0.058f*refl[8] + 0.2013f*refl[11] - 0.2773f* refl[12]);
    }

    public float tc4CirrusValue() {
        return (-0.8239f*refl[1] + 0.0849f*refl[2] + 0.4396f*refl[3] - 0.058f*refl[8] + 0.2013f*refl[11] - 0.2773f* refl[12] - refl[10]);
    }

    public float ndwiValue() {
        return ((refl[8]-refl[11])/(refl[8]+refl[11]));
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

    public void setElevation(double elevation) {
        this.elevation = elevation;
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

    public float getTc1Threshold() {
        return TC1_THRESH;
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
