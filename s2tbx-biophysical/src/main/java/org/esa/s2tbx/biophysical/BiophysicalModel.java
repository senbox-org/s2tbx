package org.esa.s2tbx.biophysical;

/**
 * Created by obarrile on 24/01/2020.
 */
public enum BiophysicalModel {

    S2A("S2A", true, true, true, true, true),
    S2B("S2B", true, true, true, true, true),
    S2A_10m("S2A_10m", true, false, false, true, true),
    S2B_10m("S2B_10m", true, false, false, true, true),
    LANDSAT8("LANDSAT8", true, false, false, true, true);

    private String description;
    private boolean lai;
    private boolean cab;
    private boolean cw;
    private boolean fapar;
    private boolean fcover;

    BiophysicalModel(String description, boolean lai, boolean cab, boolean cw, boolean fapar, boolean fcover) {
        this.description = description;
        this.lai = lai;
        this.cab = cab;
        this.cw = cw;
        this.fapar = fapar;
        this.fcover = fcover;
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public String getDescription() {
        return description;
    }

    public boolean computesLAI() {
        return lai;
    }

    public boolean computesCAB() {
        return cab;
    }

    public boolean computesCW() {
        return cw;
    }

    public boolean computesFAPAR() {
        return fapar;
    }

    public boolean computesFCOVER() {
        return fcover;
    }

    public boolean computesVariable(BiophysicalVariable variable) {
        if(variable == BiophysicalVariable.LAI) return computesLAI();
        if(variable == BiophysicalVariable.LAI_Cab) return computesCAB();
        if(variable == BiophysicalVariable.LAI_Cw) return computesCW();
        if(variable == BiophysicalVariable.FAPAR) return computesFAPAR();
        if(variable == BiophysicalVariable.FCOVER) return computesFCOVER();
        return false;
    }

    public static BiophysicalModel getBiophysicalModel(String name) {
        for (BiophysicalModel biophysicalModel : BiophysicalModel.values()) {
            if (biophysicalModel.getName().equals(name.toLowerCase())) {
                return biophysicalModel;
            }
        }
        return null;
    }
}
