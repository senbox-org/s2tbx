package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class BitsPerComponentBox extends Box {

    public BitsPerComponentBox(BoxType type, long position, long length, int dataOffset) {
        super(type, position, length, dataOffset);
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {


    }
}
