package jp2.segments;

import jp2.MarkerSegment;
import jp2.MarkerType;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class IgnoredSegment extends MarkerSegment {
    private final int code;

    public IgnoredSegment(int code) {
        super(MarkerType.___);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String toHexString() {
            return "0x" + Integer.toHexString(code).toUpperCase();
        }

    @Override
    public void readFrom(ImageInputStream stream) throws IOException {
    }
}
