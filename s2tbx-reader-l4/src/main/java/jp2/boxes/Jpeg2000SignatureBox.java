package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class Jpeg2000SignatureBox extends Box {

    private int signature;

    public Jpeg2000SignatureBox(BoxType type, long position, long length, int dataOffset) {
        super(type, position, length, dataOffset);
    }

    public int getSignature() {
        return signature;
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {
          signature = reader.getStream().readInt();
    }
}
