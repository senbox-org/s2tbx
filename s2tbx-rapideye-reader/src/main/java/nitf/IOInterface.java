/*
 * =========================================================================
 * This file is part of NITRO
 * =========================================================================
 * 
 * (C) Copyright 2004 - 2010, General Dynamics - Advanced Information Systems
 * 
 * NITRO is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, If not,
 * see <http://www.gnu.org/licenses/>.
 */
package nitf;

public abstract class IOInterface extends DestructibleObject
{

    /**
     * Seek offset is relative to current position
     */
    public static final int SEEK_CUR = 10;

    /**
     * Seek offset is relative to start of file
     */
    public static final int SEEK_SET = 20;

    /**
     * Seek offset is relative to end of file
     */
    public static final int SEEK_END = 30;
    
    /**
     * Access mode - read-only privileges
     */
    public static final int NITF_ACCESS_READONLY = 0x01;

    /**
     * Access mode - write-only privileges
     */
    public static final int NITF_ACCESS_WRITEONLY = 0x02;

    /**
     * Access mode - reading and/or writing
     */
    public static final int NITF_ACCESS_READWRITE = 0x03;

    IOInterface(long address)
    {
        super(address);
    }

    /**
     * Default constructor
     */
    public IOInterface()
    {
        construct();
    }

    /**
     * Reads size bytes into the specified byte buffer
     * 
     * @param buf
     *            the byte buffer to store the data
     * @param size
     *            the number of bytes to read
     * @throws NITFException
     */
    public abstract void read(byte[] buf, int size) throws NITFException;

    public void read(byte[] buf) throws NITFException
    {
        read(buf, buf.length);
    }

    public byte[] read(int size) throws NITFException
    {
        byte[] buf = new byte[size];
        read(buf);
        return buf;
    }

    /**
     * Writes bytes to the IO handle at the current position
     * 
     * @param buf
     *            the byte buffer containing the data
     * @param size
     *            the number of bytes to write
     * @throws NITFException
     */
    public abstract void write(final byte[] buf, int size) throws NITFException;

    /**
     * Writes bytes to the IO handle at the current position
     * 
     * @param buf
     * @throws NITFException
     */
    public void write(final byte[] buf) throws NITFException
    {
        if (buf != null && buf.length > 0)
            write(buf, buf.length);
    }
    
    public abstract boolean canSeek();

    /**
     * Seeks to the specified offset relative to the position specified by
     * whence
     * 
     * @param offset
     *            the offset
     * @param whence
     *            the type of seek, either SEEK_CUR, SEEK_SET, or SEEK_END
     * @return the position in the file after the seek
     * @throws NITFException
     */
    public abstract long seek(long offset, int whence) throws NITFException;
    

    public long seek(long offset) throws NITFException
    {
        return seek(offset, SEEK_SET);
    }

    /**
     * Returns the current IO position
     * 
     * @return the current IO position
     * @throws NITFException
     */
    public abstract long tell() throws NITFException;

    /**
     * Returns the size of the IO descriptor
     * 
     * @return the size of the IO descriptor
     * @throws NITFException
     */
    public abstract long getSize() throws NITFException;
    
    public abstract int getMode() throws NITFException;

    /**
     * Closes the IO handle.
     */
    public abstract void close() throws NITFException;

    protected native void construct();

    @Override
    protected MemoryDestructor getDestructor()
    {
        return new Destructor();
    }

    private static class Destructor implements MemoryDestructor
    {
        public native boolean destructMemory(long nativeAddress);
    }

}
