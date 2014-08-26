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

public class NativeIOInterface extends IOInterface
{
    protected NativeIOInterface()
    {
    }

    protected NativeIOInterface(long address)
    {
        super(address);
    }

    public native void read(byte[] buf, int size) throws NITFException;

    public native void write(final byte[] buf, int size) throws NITFException;

    public native boolean canSeek();
    
    public native long seek(long offset, int whence) throws NITFException;

    public native long tell() throws NITFException;

    public native long getSize() throws NITFException;
    
    public native int getMode() throws NITFException;

    public native void close() throws NITFException;

    @Override
    protected void construct()
    {
        // do nothing, by default
    }

}
