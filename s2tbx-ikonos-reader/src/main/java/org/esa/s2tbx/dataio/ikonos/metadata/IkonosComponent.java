package org.esa.s2tbx.dataio.ikonos.metadata;

import java.nio.file.Path;

/**
 * This maps to the corresponding Ikonos Component element.
 *
 * @author Denisa Stefanescu
 */

public class IkonosComponent {

    private String imageDirectoryName;
    private Path parentPath;
    private float[][] tiePointGridPoints;
    private String crsCode;
    private String originPos;

    public IkonosComponent(Path parentPath) {
        this.setParentPath(parentPath);
    }

    public String getImageDirectoryName() {
        return imageDirectoryName;
    }

    public void setImageDirectoryName(String imageDirectoryName) {
            this.imageDirectoryName = imageDirectoryName;
    }

    public float[][] getTiePointGridPoints() {
        return tiePointGridPoints;
    }

    public void setTiePointGridPoints(float[][] tiePointGridPoints) {
        this.tiePointGridPoints = tiePointGridPoints;
    }

    public String getCrsCode() {
        return crsCode;
    }

    public void setCrsCode(String crsCode) {
        this.crsCode = decodeUTMtoEPSG(crsCode);
    }

    /**
     * Transform UTM format (ex: UTM_34N) to EPSG
     */
    public static String decodeUTMtoEPSG(final String crsCode){
        String tempCrsCode = null;
        if(crsCode.contains("N")){
            tempCrsCode = "EPSG:326";
        }else if(crsCode.contains("S")){
            tempCrsCode = "EPSG:327";
        }
        if(crsCode.contains("_")){
            tempCrsCode = tempCrsCode + crsCode.split("_")[1].substring(0,2);
        }
        return tempCrsCode;
    }

    public Path getParentPath() {
        return parentPath;
    }

    public void setParentPath(Path parentPath) {
        this.parentPath = parentPath;
    }

    public String getOriginPos() {
        return originPos;
    }

    public void setOriginPos(String originPos) {
        this.originPos = originPos;
    }

}
