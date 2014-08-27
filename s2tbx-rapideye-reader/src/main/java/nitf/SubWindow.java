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
 * An object that contains information for reading image data.
 */
public final class SubWindow extends DestructibleObject
{

    /**
     * Creates a new SubWindow object
     * 
     * @throws NITFException
     */
    public SubWindow() throws NITFException
    {
        construct();
    }

    /**
     * @see DestructibleObject#DestructibleObject(long)
     */
    SubWindow(long address)
    {
        super(address);
    }

    /**
     * Returns the BandList array
     * 
     * @return
     */
    public native int[] getBandList();

    /**
     * Constructs the underlying memory
     * 
     * @throws NITFException
     */
    private native void construct() throws NITFException;

    /**
     * Sets the number of columns
     * 
     * @param numCols
     */
    public native void setNumCols(int numCols);

    /**
     * Sets the BandList array
     * 
     * @param bandList
     */
    public native void setBandList(int[] bandList);

    /**
     * Returns the start row
     * 
     * @return
     */
    public native int getStartRow();

    /**
     * Sets the start row
     * 
     * @param startRow
     */
    public native void setStartRow(int startRow);

    /**
     * Returns the number of rows
     * 
     * @return
     */
    public native int getNumRows();

    /**
     * Sets the number of rows
     * 
     * @param numRows
     */
    public native void setNumRows(int numRows);

    /**
     * Returns the start column
     * 
     * @return
     */
    public native int getStartCol();

    /**
     * Sets the start column
     * 
     * @param startCol
     */
    public native void setStartCol(int startCol);

    /**
     * Returns the number of columns
     * 
     * @return
     */
    public native int getNumCols();

    /**
     * Returns the number of bands
     * 
     * @return
     */
    public native int getNumBands();

    /**
     * Sets the number of bands
     * 
     * @param numBands
     */
    public native void setNumBands(int numBands);

    /**
     * Sets the DownSampler for this SubWindow If none is specified, or if
     * downSampler is null, the data will not be downsampled
     * 
     * @param downSampler
     */
    public native void setDownSampler(DownSampler downSampler);

    /**
     * Returns the DownSampler for this window, or null if none is specified
     * 
     * @return
     */
    public native DownSampler getDownSampler();

    public String toString()
    {
        return SubWindow.class.getName() + ":[" + "startCol=" + getStartCol() + ",startRow=" + getStartRow() + ",numCols=" + getNumCols() + ",numRows=" + getNumRows() + ",numBands=" + getNumBands() + "]";
    }

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
