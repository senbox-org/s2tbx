package jp2;

import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public abstract class Box {
    protected final BoxType type;
    protected final long position;
    protected final long length;
    protected final int dataOffset;

    protected Box(BoxType type, long position, long length, int dataOffset) {
        this.type = type;
        this.position = position;
        this.length = length;
        this.dataOffset = dataOffset;
    }

    public BoxType getType() {
        return type;
    }

    public String getSymbol() {
            return type.getSymbol();
        }

    public int getCode() {
        return type.getCode();
    }

    public long getPosition() {
        return position;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public long getLength() {
        return length;
    }

    public abstract void readFrom(BoxReader reader) throws IOException;
}
