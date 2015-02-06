package jp2;

import jp2.segments.IgnoredSegment;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class CodestreamReader {
    ImageInputStream stream;
    private final long position;
    long length;

    boolean init;

    public CodestreamReader(ImageInputStream stream, long position, long length) {
        this.stream = stream;
        this.position = position;
        this.length = length;

    }

    public MarkerSegment readSegment() throws IOException {
        if (!init) {
            init = true;
            stream.seek(position);
        }

        final int code = stream.readShort() & 0x0000ffff;

        //stream.seek(position + length - 2);

        final MarkerType markerType = MarkerType.get(code);
        if (markerType != null) {
            final MarkerSegment segment = markerType.createSegment();
            segment.readFrom(stream);
            return segment;
        } else {
            final MarkerSegment segment = new IgnoredSegment(code);
            segment.readFrom(stream);
            return segment;
        }
    }
}
