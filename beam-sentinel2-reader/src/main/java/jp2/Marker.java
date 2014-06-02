package jp2;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class Marker extends MarkerSegment {
    public Marker(MarkerType markerType) {
        super(markerType);
    }

    @Override
    public void readFrom(ImageInputStream stream) throws IOException {
    }
}
