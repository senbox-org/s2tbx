package org.esa.s2tbx.dataio.kompsat2.metadata;

/**
 * This maps to the corresponding Kompsat2 Component element.
 *
 * @author Razvan Dumitrascu
 */

public class Kompsat2Component {

    private String imageDirectoryName;
    private float[][] tiePointGridPoints;
    private String crsCode;
    private String originPos;

    public Kompsat2Component() {
    }

    public String getImageDirectoryName() {
        return imageDirectoryName;
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
        this.crsCode = crsCode;
    }

    public void setImageDirectoryName(String imageDirectoryName) {
        this.imageDirectoryName = imageDirectoryName;
    }

    public String getOriginPos() {
        return originPos;
    }

    public void setOriginPos(String originPos) {
        this.originPos = originPos;
    }
}
