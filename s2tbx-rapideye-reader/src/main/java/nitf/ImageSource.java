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
 * Class used to compile an image together, based on its constituent bands.
 */
public final class ImageSource extends DestructibleObject
{

    /**
     * Default constructor
     * 
     * @throws NITFException
     */
    public ImageSource() throws NITFException
    {
        construct();
    }

    /**
     * @see DestructibleObject#DestructibleObject(long)
     */
    ImageSource(long address)
    {
        super(address);
    }

    private native void construct() throws NITFException;

    /**
     * Returns an array of the BandSources associated with this ImageSource
     * 
     * @return an array of the BandSources associated with this ImageSource
     */
    public native BandSource[] getBandSources();

    /**
     * Returns the number of band sources
     * 
     * @return the number of band sources
     */
    public native int getSize();

    /**
     * Adds another BandSource to this ImageSource
     * 
     * @param bandSource
     *            the BandSource to append
     * @return true if the BandSource added successfully, false otherwise
     * @throws NITFException
     */
    public native boolean addBand(BandSource bandSource) throws NITFException;

    /**
     * Returns the BandSource associated by the position denoted by number
     * 
     * @param number
     *            the BandSource, starting at array position 0
     * @return the BandSource associated by the position denoted by number
     * @throws NITFException
     */
    public native BandSource getBand(int number) throws NITFException;

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
