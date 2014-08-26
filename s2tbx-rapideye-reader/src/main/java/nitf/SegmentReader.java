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

/**
 * SegmentReader
 * <p/>
 * This class provides a "jailed" IOHandle to the data in a segment.
 * <p/>
 * 
 * @see Reader#getNewGraphicReader(int)
 * @see Reader#getNewTextReader(int)
 */
public final class SegmentReader extends DestructibleObject
{

    SegmentReader(long address)
    {
        super(address);
    }

    /**
     * The read function reads data from the associated segment. The reading of
     * the data is serial as if a flat file were being read by an input stream.
     * Except for the first argument, this function has the same calling
     * sequence and behavior as IOHandle.read.
     * 
     * @param buffer
     *            buffer to hold data
     * @param count
     *            amount of data to return
     * @return true on success
     * @throws NITFException
     * @see IOHandle#read(byte[], int)
     */
    public native boolean read(byte[] buffer, int count) throws NITFException;

    /**
     * The read function reads data from the associated segment. The reading of
     * the data is serial as if a flat file were being read by an input stream.
     * Except for the first argument, this function has the same calling
     * sequence and behavior as IOHandle.read.
     * 
     * @param buffer
     *            buffer to hold data
     * @return true on success
     * @throws NITFException
     * @see IOHandle#read(byte[], int)
     */
    public boolean read(byte[] buffer) throws NITFException
    {
        return read(buffer, buffer.length);
    }

    /**
     * The seek function allows the user to seek within the extension data. This
     * function has the same calling sequence and behaivior as IOHandle.seek.
     * The offset is relative to the top of the segment data.
     * 
     * @param offset
     *            the offset in the file
     * @param whence
     *            the type of seek, either SEEK_CUR, SEEK_SET, or SEEK_END
     * @return the position in the file after the seek
     * @throws NITFException
     * @see IOHandle#seek(long, int)
     */
    public native long seek(long offset, int whence) throws NITFException;

    /**
     * The tell function allows the user to determine the current offset within
     * the extension data. This function has the same calling sequence and
     * behaivior as IOHandle.tell. The offset is relative to the top of the
     * segment data.
     * 
     * @return the current file pointer position
     * @throws NITFException
     * @see IOHandle#tell()
     */
    public native long tell() throws NITFException;

    /**
     * The seek function allows the user to determine the size of the data. This
     * function has the same calling sequence and behavior as IOHandle.getSize.
     * 
     * @return the size of the file descriptor
     * @throws NITFException
     * @see IOHandle#getSize()
     */
    public native long getSize() throws NITFException;

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
