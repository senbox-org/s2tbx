package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class ColorSpecificationBox extends Box {

    private int meth;
    private int prec;
    private int approx;
    private long enumCS;
    private byte[] profile;

    public ColorSpecificationBox(BoxType type, long position, long length, int dataOffset) {
        super(type, position, length, dataOffset);
    }

    public int getMeth() {
        return meth;
    }

    public int getPrec() {
        return prec;
    }

    public int getApprox() {
        return approx;
    }

    public long getEnumCS() {
        return enumCS;
    }

    public byte[] getProfile() {
        return profile;
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {
        final ImageInputStream stream = reader.getStream();
        meth = (stream.readByte() & 0x000000ff);
        prec = stream.readByte();
        approx = (stream.readByte() & 0x000000ff);
        enumCS = (stream.readInt() & 0x00000000ffffffffL);
        final long profileLength = length - (stream.getStreamPosition() - position);
        profile = new byte[(int) profileLength];
        stream.read(profile);
    }
}
