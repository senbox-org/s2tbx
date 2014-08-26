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
 * Writes NITF files
 */
public final class Writer extends DestructibleObject
{

    /**
     * Create a new NITF Writer.
     * 
     * @throws NITFException
     */
    public Writer() throws NITFException
    {
        construct();
    }

    /**
     * @see DestructibleObject#DestructibleObject(long)
     */
    Writer(long address)
    {
        super(address);
    }

    private native void construct() throws NITFException;

    /**
     * @return the array of ImageWriters
     */
    public native ImageWriter[] getImageWriters();

    /**
     * @return the number of image writers
     */
    public native int getNumImageWriters();

    /**
     * @return the array of TextWriters
     */
    public native SegmentWriter[] getTextWriters();

    /**
     * @return Returns the number of image writers
     */
    public native int getNumTextWriters();

    /**
     * Returns the output IOInterface
     * 
     * @return the output IOInterface
     */
    native IOInterface getOutput();

    /**
     * Returns the associated Record Currently not public
     * 
     * @return the associated Record, or null if none is associated
     */
    native Record getRecord();

    /**
     * Prepares for writing. This is called before the write() function.
     * 
     * @param record
     *            the record to write
     * @param ioHandle
     *            the output IOHandle
     * @return true if the prepare completed successfully, false otherwise
     * @throws NITFException
     */
    public boolean prepare(Record record, IOInterface ioHandle)
            throws NITFException
    {
        return prepareIO(record, ioHandle);
    }

    /**
     * Prepares for writing. This is called before the write() function.
     * 
     * @param record
     *            the record to write
     * @param output
     *            the output IOInterface
     * @return true if the prepare completed successfully, false otherwise
     * @throws NITFException
     */
    public native boolean prepareIO(Record record, IOInterface output)
            throws NITFException;

    /**
     * Sets the WriteHandler for the Image at the given index.
     */
    public native void setImageWriteHandler(int index, WriteHandler writeHandler)
            throws NITFException;

    /**
     * Sets the WriteHandler for the Graphic at the given index.
     */
    public native void setGraphicWriteHandler(int index,
                                              WriteHandler writeHandler)
            throws NITFException;

    /**
     * Sets the WriteHandler for the Text at the given index.
     */
    public native void setTextWriteHandler(int index, WriteHandler writeHandler)
            throws NITFException;

    /**
     * Sets the WriteHandler for the DE at the given index.
     */
    public native void setDEWriteHandler(int index, WriteHandler writeHandler)
            throws NITFException;

    /**
     * Returns an ImageWriter pertaining to the image at the given index
     * 
     * @param imageNumber
     *            the index of the image
     * @return an ImageWriter pertaining to the image at the given index
     * @throws NITFException
     *             if a native error occurs
     */
    public native ImageWriter getNewImageWriter(int imageNumber)
            throws NITFException;

    /**
     * Returns a SegmentWriter pertaining to the text at the given index
     * 
     * @param textNumber
     *            the index of the text
     * @return a SegmentWriter pertaining to the text at the given index
     * @throws NITFException
     *             if a native error occurs
     */
    public native SegmentWriter getNewTextWriter(int textNumber)
            throws NITFException;

    /**
     * Returns a SegmentWriter pertaining to the graphic at the given index
     * 
     * @param graphicNumber
     *            the index of the graphic
     * @return a SegmentWriter pertaining to the graphic at the given index
     * @throws NITFException
     *             if a native error occurs
     */
    public native SegmentWriter getNewGraphicWriter(int graphicNumber)
            throws NITFException;

    /**
     * Returns a SegmentWriter pertaining to the DataExtension segment at the
     * given index
     * 
     * @param deNumber
     *            the index of the segment
     * @return a SegmentWriter pertaining to the DE at the given index
     * @throws NITFException
     *             if a native error occurs
     */
    public native SegmentWriter getNewDEWriter(int deNumber)
            throws NITFException;

    /**
     * Writes the record
     * 
     * @return true if the write was successful, false otherwise
     * @throws NITFException
     *             if a native error occurs
     */
    public native boolean write() throws NITFException;

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
