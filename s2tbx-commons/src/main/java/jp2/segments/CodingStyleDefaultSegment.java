package jp2.segments;

import jp2.MarkerSegment;
import jp2.MarkerType;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class CodingStyleDefaultSegment extends MarkerSegment {

    private short lcod;
    private int layers;
    private int order;
    private int SGcodA;
    private int SGcodB;
    private int SGcodC;
    private short levels;

    public short getLevels() {
        return levels;
    }

    public CodingStyleDefaultSegment(MarkerType markerType) {
        super(markerType);
    }

    @Override
    public void readFrom(ImageInputStream stream) throws IOException {
        lcod = stream.readShort();
        order = stream.readByte();
        int raw = stream.readInt();
        layers = raw & 0x00ffff00;
        layers = layers >> 8;
        levels = stream.readByte();
        levels = (short) (levels + 1);
    }

    public short getLcod() {
        return lcod;
    }

    public int getLayers() {
        return layers;
    }

    public int getOrder() {
        return order;
    }
}
