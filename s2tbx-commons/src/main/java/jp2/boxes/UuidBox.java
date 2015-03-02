package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Norman Fomferra
 */
public class UuidBox extends Box {

    private UUID uiid;
    private byte[] data;

    public UuidBox(BoxType type, long position, long length, int dataOffset) {
        super(type, position, length, dataOffset);
    }

    public UUID getUiid() {
        return uiid;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {
        final ImageInputStream stream = reader.getStream();
        final long mostSigBits = stream.readLong();
        final long leastSigBits = stream.readLong();
        uiid = new UUID(mostSigBits, leastSigBits);
        final long dataLength = length - (stream.getStreamPosition() - position);
        data = new byte[(int) dataLength];
        stream.read(data);
    }
}
