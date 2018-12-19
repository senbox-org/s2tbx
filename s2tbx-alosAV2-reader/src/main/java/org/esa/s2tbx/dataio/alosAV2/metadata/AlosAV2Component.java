package org.esa.s2tbx.dataio.alosAV2.metadata;

import java.nio.file.Path;

public class AlosAV2Component {

    private Path parentPath;
    private float[][] tiePointGridPoints;
    private String crsCode;
    private String originPos;

    public AlosAV2Component(Path parentPath) {
        this.setParentPath(parentPath);
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
