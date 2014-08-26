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

import java.nio.ByteBuffer;

/**
 * Memory implementation of the IOInterface, from the Java side.
 */
public class MemoryIO extends IOInterface
{

    private ByteBuffer buffer;

    public MemoryIO(int capacity)
    {
        buffer = ByteBuffer.allocate(capacity);
    }

    public MemoryIO(byte[] data)
    {
        buffer = ByteBuffer.wrap(data);
    }

    @Override
    public void close() throws NITFException
    {
        // does nothing
    }

    @Override
    public long getSize() throws NITFException
    {
        return buffer.capacity();
    }
    
    @Override
    public int getMode() throws NITFException
    {
        return NITF_ACCESS_READWRITE;
    }

    @Override
    public void read(byte[] buf, int size) throws NITFException
    {
        int pos = buffer.position();
        if ((pos + size) > buffer.capacity() || size > buf.length)
            throw new NITFException("Attempting to read past buffer boundary.");
        System.arraycopy(buffer.array(), pos, buf, 0, size);
        buffer.position(pos + size);
    }
    
    @Override
    public boolean canSeek()
    {
        return true;
    }

    @Override
    public long seek(long offset, int whence) throws NITFException
    {
        int pos = buffer.position();
        switch (whence)
        {
        case IOInterface.SEEK_CUR:
            if (offset + pos > buffer.capacity())
                throw new NITFException(
                        "Attempting to seek past buffer boundary.");
            buffer.position((int) (pos + offset));
            break;
        case IOInterface.SEEK_END:
            throw new NITFException("SEEK_END is unsupported with MemoryIO.");
        case IOInterface.SEEK_SET:
            if (offset > buffer.capacity())
                throw new NITFException(
                        "Attempting to seek past buffer boundary.");
            buffer.position((int) (offset));
            break;
        }
        return buffer.position();
    }

    @Override
    public long tell() throws NITFException
    {
        return buffer.position();
    }

    @Override
    public void write(byte[] buf, int size) throws NITFException
    {
        int pos = buffer.position();
        if ((pos + size) > buffer.capacity() || size > buf.length)
            throw new NITFException("Attempting to write past buffer boundary.");
        System.arraycopy(buf, 0, buffer.array(), pos, size);
        buffer.position(pos + size);
    }

}
