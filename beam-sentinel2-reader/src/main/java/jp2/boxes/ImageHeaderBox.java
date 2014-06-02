package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class ImageHeaderBox extends Box {

    private long height;
    private long width;
    private int nc;
    private int bpc;
    private int c;
    private int unkC;
    private int ipr;

    public ImageHeaderBox(BoxType type, long position, long length, int dataOffset) {
        super(type, position, length,dataOffset);
    }

    public long getHeight() {
        return height;
    }

    public long getWidth() {
        return width;
    }

    public int getNc() {
        return nc;
    }

    public int getBpc() {
        return bpc;
    }

    public int getC() {
        return c;
    }

    public int getUnkC() {
        return unkC;
    }

    public int getIpr() {
        return ipr;
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {
        final ImageInputStream stream = reader.getStream();
        height = (stream.readInt() & 0x00000000ffffffffL);
        width = (stream.readInt() & 0x00000000ffffffffL);
        nc = (stream.readShort() & 0x0000ffff);
        bpc = (stream.readByte() & 0x000000ff);
        c = (stream.readByte() & 0x000000ff);
        unkC = (stream.readByte() & 0x000000ff);
        ipr = (stream.readByte() & 0x000000ff);
    }

}
