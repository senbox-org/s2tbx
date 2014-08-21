package jp2;

import jp2.boxes.IgnoredBox;

import javax.imageio.stream.ImageInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class BoxReader {
    private final ImageInputStream stream;
    private final long fileLength;
    private final Listener listener;

    public BoxReader(ImageInputStream stream, long fileLength, Listener listener) {
        this.stream = stream;
        this.fileLength = fileLength;

        if(listener == null)
        {
            this.listener = new EmptyListener();
        }
        else
        {
            this.listener = listener;
        }
    }

    public ImageInputStream getStream() {
        return stream;
    }

    public long getFileLength() {
        return fileLength;
    }

    public Box readBox() throws IOException {

        final long position = stream.getStreamPosition();

        final int nextInt32;
        try {
            nextInt32 = stream.readInt();
        } catch (EOFException e) {
            return null;
        }

        long length = nextInt32 & 0x00000000ffffffffL;
        final int type = stream.readInt();
        int dataOffset = 8;

        if (length == 0L) {
            length = fileLength - position;
        } else if (length == 1L) {
            length = stream.readLong();
            dataOffset += 8;
        }

        final Box box;
        if (length == 0L || length == 1L || length >= 8L) {
            final BoxType boxType = BoxType.get(type);
            if (boxType != null) {
                box = boxType.createBox(position, length, dataOffset);
                box.readFrom(this);
                listener.knownBoxSeen(box);
            } else {
                box = new IgnoredBox(type, position, length, dataOffset);
                stream.seek(position + length);
                listener.unknownBoxSeen(box);
            }
        } else {
            box = new IgnoredBox(type, position, length, dataOffset);
            listener.unknownBoxSeen(box);
        }
        return box;
    }

    public interface Listener {
        void knownBoxSeen(Box box);

        void unknownBoxSeen(Box box);
    }
}

class EmptyListener implements BoxReader.Listener
{

    @Override
    public void knownBoxSeen(Box box) {
        // do nothing
    }

    @Override
    public void unknownBoxSeen(Box box) {
        // do nothing
    }
}
