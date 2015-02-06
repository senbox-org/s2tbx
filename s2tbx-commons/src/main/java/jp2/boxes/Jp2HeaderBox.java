package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import java.io.IOException;
import java.util.Map;

/**
 * @author Norman Fomferra
 */
public class Jp2HeaderBox extends Box {

    Map<Integer, Box> subBoxes;

    public Jp2HeaderBox(BoxType type, long position, long length, int dataOffset) {
        super(type, position, length, dataOffset);
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {
    }

}
