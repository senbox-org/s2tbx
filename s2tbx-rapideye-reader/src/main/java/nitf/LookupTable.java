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
 * Represents a LookupTable used in the BandInfo object
 */
public class LookupTable extends DestructibleObject
{

    /**
     * Creates a new LookupTable
     * 
     * @param numTables
     *            the number of tables
     * @param numEntries
     *            the number of entries per table
     * @param lutData
     *            the actual LUT data, whose size should be (numTables *
     *            numEntries)
     * @throws NITFException
     */
    public LookupTable(int numTables, int numEntries, byte[] lutData)
            throws NITFException
    {
        construct(numTables, numEntries, lutData);
    }

    /**
     * Constructs the underlying memory
     * 
     * @param numTables
     *            the number of tables
     * @param numEntries
     *            the number of entries per table
     * @param lutData
     *            the actual LUT data, whose size should be (numTables *
     *            numEntries)
     * 
     * @throws NITFException
     */
    protected native void construct(int numTables, int numEntries,
                                    byte[] lutData) throws NITFException;

    /**
     * @see NITFObject#NITFObject(long)
     */
    LookupTable(long address)
    {
        super(address);
    }

    /**
     * @return the number of tables
     */
    public synchronized native int getNumTables();

    /**
     * @return the number of entries per table
     */
    public synchronized native int getNumEntries();

    /**
     * @return the data for the LUT table(s)
     */
    public synchronized native byte[] getData();

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
