/* =========================================================================
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program; if not, If not, 
 * see <http://www.gnu.org/licenses/>.
 *
 */

package nitf;

/**
 * <code>MemorySource</code>
 * 
 * The MemorySource class extends the BandSource class, allowing you to provide
 * data from a memory buffer.
 * 
 * This class is final because the BandSource class uses special logic when it
 * gets constructed from a subclass. This can however be used as a delegate
 * member inside another specialized BandSource.
 */
public final class MemorySource extends BandSource
{

    protected MemorySource()
    {
        super();
    }

    /**
     * @param address
     */
    protected MemorySource(long address)
    {
        super(address);
    }

    /**
     * Constructs and returns a BandSource from memory
     * 
     * This is one of the two BandSources the library provides for free. If you
     * want something more detailed to your needs, feel free to extend the
     * BandInfo abstract class.
     * 
     * @param data
     *            the source byte buffer
     * @param size
     *            the size (in bytes) of data for this band
     * @param start
     *            the start offset
     * @param numBytesPerPixel
     *            the number of bytes per pixel this is ignored if pixelSkip ==
     *            0
     * @param pixelSkip
     *            the number of pixels to skip, that are between pixels of this
     *            band. i.e. the number of bands in the data buffer - 1 If this
     *            is 0, it signifies a contiguous read.
     * @throws NITFException
     */
    public MemorySource(byte[] data, int size, int start, int numBytesPerPixel,
            int pixelSkip) throws NITFException
    {
        construct(data, size, start, numBytesPerPixel, pixelSkip);
    }

    /**
     * Constructs the underlying memory
     * 
     * @param data
     * @param size
     * @param start
     * @param pixelSkip
     */
    private native void construct(byte[] data, int size, int start,
            int numBytesPerPixel, int pixelSkip);

    /*
     * (non-Javadoc)
     * 
     * @see nitf.BandSource#read(byte[], int)
     */
    public native void read(byte[] buf, int size) throws NITFException;
    
    @Override
    public native long getSize() throws NITFException;
    
    @Override
    public native void setSize(long size) throws NITFException;

}
