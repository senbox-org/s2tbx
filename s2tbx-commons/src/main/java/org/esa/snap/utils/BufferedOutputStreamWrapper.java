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
