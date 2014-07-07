package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class ContiguousCodestreamBox extends Box {
    public ContiguousCodestreamBox(BoxType type, long position, long length, int dataOffset) {
        super(type, position, length, dataOffset);
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {
        final ImageInputStream stream = reader.getStream();
        // ignore contents for time being
        stream.seek(position + length);
    }
}
