package org.esa.snap.utils;

import java.io.*;

/**
 * @author Jean Coravu
 */
public class BufferedOutputStreamWrapper {
    private final BufferedOutputStream inputStream;

    public BufferedOutputStreamWrapper(File file) throws FileNotFoundException {
        FileOutputStream fileInputStream = new FileOutputStream(file);
        this.inputStream = new BufferedOutputStream(fileInputStream);
    }

    public void close() throws IOException {
        this.inputStream.close();
    }

    public final void writeBoolean(boolean v) throws IOException {
        this.inputStream.write(v ? 1 : 0);
    }

    public final void writeLong(long v) throws IOException {
        this.inputStream.write((int)(v >>> 56) & 0xFF);
        this.inputStream.write((int)(v >>> 48) & 0xFF);
        this.inputStream.write((int)(v >>> 40) & 0xFF);
        this.inputStream.write((int)(v >>> 32) & 0xFF);
        this.inputStream.write((int)(v >>> 24) & 0xFF);
        this.inputStream.write((int)(v >>> 16) & 0xFF);
        this.inputStream.write((int)(v >>>  8) & 0xFF);
        this.inputStream.write((int)(v >>>  0) & 0xFF);
    }

    public final void writeInt(int v) throws IOException {
        this.inputStream.write((v >>> 24) & 0xFF);
        this.inputStream.write((v >>> 16) & 0xFF);
        this.inputStream.write((v >>>  8) & 0xFF);
        this.inputStream.write((v >>>  0) & 0xFF);
    }

    public void write(byte[] b) throws IOException {
        this.inputStream.write(b);
    }

    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }
}
