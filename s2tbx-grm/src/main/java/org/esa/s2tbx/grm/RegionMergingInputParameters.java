package org.esa.s2tbx.grm;

/**
 * @author Jean Coravu
 */
public class RegionMergingInputParameters {
    private String mergingCostCriterion;
    private String regionMergingCriterion;
    private int totalIterationsForSecondSegmentation;
    private float threshold;
    private float spectralWeight;
    private float shapeWeight;

    public RegionMergingInputParameters(String mergingCostCriterion, String regionMergingCriterion, int totalIterationsForSecondSegmentation,
                                        float threshold, float spectralWeight, float shapeWeight) {

        this.mergingCostCriterion = mergingCostCriterion;
        this.regionMergingCriterion = regionMergingCriterion;
        this.totalIterationsForSecondSegmentation = totalIterationsForSecondSegmentation;
        this.threshold = threshold;
        this.spectralWeight = spectralWeight;
        this.shapeWeight = shapeWeight;
    }

    public String getMergingCostCriterion() {
        return mergingCostCriterion;
    }

    public String getRegionMergingCriterion() {
        return regionMergingCriterion;
    }

    public int getTotalIterationsForSecondSegmentation() {
        return totalIterationsForSecondSegmentation;
    }

    public float getThreshold() {
        return threshold;
    }

    public float getSpectralWeight() {
        return spectralWeight;
    }

    public float getShapeWeight() {
        return shapeWeight;
    }
}
