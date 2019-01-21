package org.esa.s2tbx.processor.mci.ui;

enum Presets {
    NONE("None", "", "", "", "", "", ""),
    S2MSI_L1C_MCI("S2MSI L1C MCI", "B4", "B6", "B5", "MCI", "MCI_slope",
                  "B11<0.01 && B8<B4"),
    S2MSI_L2_MCI("S2MSI L2 MCI", "B4", "B6", "B5", "MCI", "MCI_slope",
                 "scl_water");

    private final String label;
    private final String lowerBaselineBandName;
    private final String upperBaselineBandName;
    private final String signalBandName;
    private final String lineHeightBandName;
    private final String slopeBandName;
    private final String maskExpression;

    private Presets(String label, String upperBaselineBandName, String lowerBaselineBandName,
                    String signalBandName, String lineHeightBandName, String slopeBandName, String maskExpression) {
        this.label = label;
        this.upperBaselineBandName = upperBaselineBandName;
        this.lowerBaselineBandName = lowerBaselineBandName;
        this.signalBandName = signalBandName;
        this.lineHeightBandName = lineHeightBandName;
        this.slopeBandName = slopeBandName;
        this.maskExpression = maskExpression;
    }

    @Override
    public String toString() {
        return label;
    }

    String getLowerBaselineBandName() {
        return lowerBaselineBandName;
    }

    String getUpperBaselineBandName() {
        return upperBaselineBandName;
    }

    String getSignalBandName() {
        return signalBandName;
    }

    String getLineHeightBandName() {
        return lineHeightBandName;
    }

    String getSlopeBandName() {
        return slopeBandName;
    }

    String getMaskExpression() {
        return maskExpression;
    }
}
