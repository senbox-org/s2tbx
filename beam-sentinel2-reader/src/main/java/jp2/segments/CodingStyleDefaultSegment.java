package jp2.segments;

import jp2.MarkerSegment;
import jp2.MarkerType;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class CodingStyleDefaultSegment extends MarkerSegment {

    private short lcod;

    public CodingStyleDefaultSegment(MarkerType markerType) {
        super(markerType);
    }

    @Override
    public void readFrom(ImageInputStream stream) throws IOException {
        lcod = stream.readShort();

    }
}
