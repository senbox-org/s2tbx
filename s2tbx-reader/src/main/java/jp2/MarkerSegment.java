package jp2;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public abstract class MarkerSegment {
    private final MarkerType markerType;

    protected MarkerSegment(MarkerType markerType) {
        this.markerType = markerType;
    }

    public MarkerType getMarkerType() {
        return markerType;
    }

    public int getCode() {
        return markerType.getCode();
    }

    public String toHexString() {
        return markerType.toHexString();
    }

    public abstract void readFrom(ImageInputStream stream) throws IOException;
}
