package org.esa.s2tbx.dataio.s2;

/**
 * Created by obarrile on 31/07/2017.
 */
public enum S2SpecificBandConstants {

    SCL("quality_scene_classification", "SCL"),
    MSC("quality_mosaic_info", "MSC"),
    AOT("quality_aot", "AOT"),
    WVP("quality_wvp", "WVP"),
    CLD("quality_cloud_confidence", "CLD"),
    SNW("quality_snow_confidence", "SNW"),
    DDV("quality_dense_dark_vegetation", "DDV");
    //TODO s2 EVOLUTION:  ADD NEW QUALITY INDICATOR

    private String physicalName;
    private String filenameBandId;

    S2SpecificBandConstants(String physicalName,
                    String filenameBandId ) {
        this.physicalName = physicalName;
        this.filenameBandId = filenameBandId;
    }


    public String getPhysicalName() {
        return physicalName;
    }

    public String getFilenameBandId() {
        return filenameBandId;
    }


    public static S2SpecificBandConstants getBandFromPhysicalName(String physicalName) {
        for (S2SpecificBandConstants band : S2SpecificBandConstants.values()) {
            if (physicalName.startsWith(band.getPhysicalName())) return band;
        }
        return null;
    }
}

