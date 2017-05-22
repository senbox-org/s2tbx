package org.esa.s2tbx.grm.utils;

import java.io.*;

/**
 * @author Jean Coravu
 */
public class BufferedInputStreamWrapper {
    private final BufferedInputStream inputStream;

    public BufferedInputStreamWrapper(File file) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        this.inputStream = new BufferedInputStream(fileInputStream);
    }

    public void close() throws IOException {
        this.inputStream.close();
    }

    public final boolean readBoolean() throws IOException {
        int ch = this.inputStream.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (ch != 0);
    }

    public final long readLong() throws IOException {
//        return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
        return ((long)(readInt()) << 32) | (readInt() & 0xFFFFFFFFL);
    }

    public final int readInt() throws IOException {
        int ch1 = this.inputStream.read();
        int ch2 = this.inputStream.read();
        int ch3 = this.inputStream.read();
        int ch4 = this.inputStream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        //return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
        return ((ch1 << 24) | (ch2 << 16) | (ch3 << 8) | (ch4 << 0));
    }

    public final void readFully(byte[] b) throws IOException {
        this.inputStream.read(b);
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }
}
