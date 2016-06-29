package org.esa.s2tbx.biophysical;

/**
 * Created by jmalik on 20/06/16.
 */
public enum BiophysicalVariable {
    LAI("LAI", ""),
    LAI_Cab("LAI_Cab", "g/cm2"),
    LAI_Cw("LAI_Cw", "g/m2"),
    FAPAR("FAPAR", ""),
    FCOVER("FCOVER", "");

    private String description;
    private String unit;

    BiophysicalVariable(String description, String unit) {
        this.description = description;
        this.unit = unit;
    }

    public String getSampleName() {
        return this.name().toLowerCase();
    }

    public String getBandName() {
        return this.name().toLowerCase();
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }
}
