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
 * Class that has the functionality of reading an image
 */
public final class ImageReader extends DestructibleObject
{

    /**
     * @see DestructibleObject#DestructibleObject(long)
     */
    ImageReader(long address)
    {
        super(address);
    }

    /**
     * Returns the IOInterface associated with this ImageReader
     * 
     * @return the IOInterface associated with this ImageReader
     */
    public native IOInterface getInput();

    /**
     * Returns the BlockingInfo used by this ImageReader
     * 
     * @return the BlockingInfo used by this ImageReader
     * @throws NITFException
     */
    public native BlockingInfo getBlockingInfo() throws NITFException;

    /**
     * Reads the data specified by the SubWindow into the byte[][] buffer
     * 
     * @param subWindow
     *            the window that defines data about the impending read
     * @param userBuf
     *            buffer to store the data
     * @return true if the the data was padded
     * @throws NITFException
     */
    public native boolean read(SubWindow subWindow, byte[][] userBuf)
            throws NITFException;

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
