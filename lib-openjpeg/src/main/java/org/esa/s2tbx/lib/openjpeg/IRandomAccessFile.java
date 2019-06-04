package org.esa.s2tbx.lib.openjpeg;

import java.io.IOException;

public interface IRandomAccessFile {

    public short readShort() throws IOException;

    public int readUnsignedShort() throws IOException;

    public int readInt() throws IOException;

    public long readUnsignedInt() throws IOException;

    public long readLong() throws IOException;

    public float readFloat() throws IOException;

    public double readDouble() throws IOException;

    public long getPosition() throws IOException;

    public long getLength() throws IOException;

    public void seek(long off) throws IOException;

    public int read() throws IOException;

    public void readFully(byte b[], int off, int len) throws IOException;
}
