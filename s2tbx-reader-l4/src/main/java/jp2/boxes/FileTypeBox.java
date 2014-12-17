package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class FileTypeBox extends Box {

    private int br;
    private int minV;
    private int cl0;

    public FileTypeBox(BoxType type, long position, long length, int dataOffset) {
        super(type, position, length, dataOffset);
    }

    public int getBr() {
        return br;
    }

    public int getMinV() {
        return minV;
    }

    public int getCl0() {
        return cl0;
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {
        final ImageInputStream stream = reader.getStream();
        br = stream.readInt();
        minV = stream.readInt();
        cl0 = stream.readInt();
    }
}
