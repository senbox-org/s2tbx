package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class IgnoredBox extends Box {
    private final int code;

    public IgnoredBox(int code, long position, long length, int dataOffset) {
        super(BoxType.____, position, length, dataOffset);
        this.code = code;
    }

    public String getSymbol() {
        return BoxType.encode4b(code);
    }

    public int getCode() {
        return code;
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {
        final ImageInputStream stream = reader.getStream();
        // ignore contents
        stream.seek(position + length);
    }
}
