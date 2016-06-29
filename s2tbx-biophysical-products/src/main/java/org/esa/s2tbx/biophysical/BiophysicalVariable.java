package org.esa.s2tbx.biophysical;

/**
 * Created by jmalik on 20/06/16.
 */
public enum BiophysicalVariable {
    LAI("LAI"),
    LAI_Cab("LAI_Cab"),
    LAI_Cw("LAI_Cw"),
    FAPAR("FAPAR"),
    FCOVER("FCOVER");

    private String description;

    BiophysicalVariable(String description) {
        this.description = description;
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
}
