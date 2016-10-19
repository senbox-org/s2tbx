package org.esa.s2tbx.dataio.s2.filepatterns;

/**
 * Created by obarrile on 19/10/2016.
 */
public class S2FileNamingItems {
    private String missionID = null;
    private String siteCentre = null;
    private String creationDate = null;
    private String absoluteOrbit = null;
    private String relativeOrbit = null;
    private String tileNumber = null;
    private String resolution = null;
    private String datatakeSensingStart = null;
    private String productBaseline = null;
    private String productDiscrim = null;
    private String startTime = null;
    private String stopTime = null;

    public String getMissionID() {
        return missionID;
    }

    public void setMissionID(String missionID) {
        this.missionID = missionID;
    }

    public String getSiteCentre() {
        return siteCentre;
    }

    public void setSiteCentre(String siteCentre) {
        this.siteCentre = siteCentre;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAbsoluteOrbit() {
        return absoluteOrbit;
    }

    public void setAbsoluteOrbit(String absoluteOrbit) {
        this.absoluteOrbit = absoluteOrbit;
    }

    public String getRelativeOrbit() {
        return relativeOrbit;
    }

    public void setRelativeOrbit(String relativeOrbit) {
        this.relativeOrbit = relativeOrbit;
    }

    public String getTileNumber() {
        return tileNumber;
    }

    public void setTileNumber(String tileNumber) {
        this.tileNumber = tileNumber;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDatatakeSensingStart() {
        return datatakeSensingStart;
    }

    public void setDatatakeSensingStart(String datatakeSensingStart) {
        this.datatakeSensingStart = datatakeSensingStart;
    }

    public String getProductBaseline() {
        return productBaseline;
    }

    public void setProductBaseline(String productBaseline) {
        this.productBaseline = productBaseline;
    }

    public String getProductDiscrim() {
        return productDiscrim;
    }

    public void setProductDiscrim(String productDiscrim) {
        this.productDiscrim = productDiscrim;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }
}
