package org.esa.snap.utils;

import java.io.*;

/**
 * @author Jean Coravu
 */
public class BufferedOutputStreamWrapper {

    private final FileOutputStream fileOutputStream;
    private final BufferedOutputStream bufferedOutputStream;

    public BufferedOutputStreamWrapper(File file) throws FileNotFoundException {
        this.fileOutputStream = new FileOutputStream(file);
        this.bufferedOutputStream = new BufferedOutputStream(this.fileOutputStream);
    }

    public void close() throws IOException {
        // close first the buffered output stream and then the file output stream
        try {
            this.bufferedOutputStream.close();
        } finally {
            this.fileOutputStream.close();
        }
    }

    public final void writeBoolean(boolean v) throws IOException {
        this.bufferedOutputStream.write(v ? 1 : 0);
    }

    public final void writeLong(long v) throws IOException {
        this.bufferedOutputStream.write((int)(v >>> 56) & 0xFF);
        this.bufferedOutputStream.write((int)(v >>> 48) & 0xFF);
        this.bufferedOutputStream.write((int)(v >>> 40) & 0xFF);
        this.bufferedOutputStream.write((int)(v >>> 32) & 0xFF);
        this.bufferedOutputStream.write((int)(v >>> 24) & 0xFF);
        this.bufferedOutputStream.write((int)(v >>> 16) & 0xFF);
        this.bufferedOutputStream.write((int)(v >>>  8) & 0xFF);
        this.bufferedOutputStream.write((int)(v >>>  0) & 0xFF);
    }

    public final void writeInt(int v) throws IOException {
        this.bufferedOutputStream.write((v >>> 24) & 0xFF);
        this.bufferedOutputStream.write((v >>> 16) & 0xFF);
        this.bufferedOutputStream.write((v >>>  8) & 0xFF);
        this.bufferedOutputStream.write((v >>>  0) & 0xFF);
    }

    public void write(byte[] b) throws IOException {
        this.bufferedOutputStream.write(b);
    }

    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }
}
