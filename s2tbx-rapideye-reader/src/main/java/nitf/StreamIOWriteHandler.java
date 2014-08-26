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
 * A WriteHandler that just streams from an input IOInterface to an output one.
 * 
 * Handy for bypassing segments when needing to round-trip.
 */
public class StreamIOWriteHandler extends WriteHandler
{
    private static final int CHUNK_SIZE = 8192;

    private IOInterface input;

    private long offset;

    private long bytes;

    public StreamIOWriteHandler(IOInterface input, long offset, long bytes)
    {
        this.input = input;
        this.offset = offset;
        this.bytes = bytes;
    }

    public StreamIOWriteHandler(IOInterface input, long offset)
            throws NITFException
    {
        this(input, offset, input.getSize());
    }

    public StreamIOWriteHandler(IOInterface input) throws NITFException
    {
        this(input, 0);
    }

    @Override
    public StreamIOWriteHandler clone()
    {
        return new StreamIOWriteHandler(input, offset, bytes);
    }

    @Override
    public void write(IOInterface io) throws NITFException
    {
        // stream the input to the output in chunks

        // first, seek to the right spot of the input handle
        input.seek(offset, IOInterface.SEEK_SET);

        byte[] buf = new byte[CHUNK_SIZE];

        long toWrite = bytes;
        int bytesThisPass;

        while (toWrite > 0)
        {
            bytesThisPass = toWrite >= CHUNK_SIZE ? CHUNK_SIZE : (int) toWrite;

            // read
            input.read(buf, bytesThisPass);

            // write
            io.write(buf, bytesThisPass);

            // update count
            toWrite -= bytesThisPass;
        }
    }
}
