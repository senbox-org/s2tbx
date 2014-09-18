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

import org.esa.beam.dataio.NativeLibraryLoader;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;

/**
 * The Base NITF Object
 * <p/>
 * 
 * Every core NITF Object extends this class
 */
public abstract class NITFObject
{

    public static final long INVALID_ADDRESS = 0;

    /**
     * The name of the library to load
     */
    public static final String NITF_LIBRARY_NAME = "nitf.jni-c";

    /* Load the library */
    static {
        try {
            NativeLibraryLoader.loadLibraryFromJar("/resources/lib/" + NativeLibraryLoader.getOSFamily() + "/" + NITF_LIBRARY_NAME);
			//System.loadLibrary(NITF_LIBRARY_NAME);
        } catch (Throwable e) {
			/* Try to load the library in the lib directory */
			String path;
			try {
				path = NITFObject.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				path = URLDecoder.decode(path, "UTF-8");
			} catch (UnsupportedEncodingException x) {
				throw new UnsatisfiedLinkError();
			}	
			
			System.load(path + "../lib/" +  NativeLibraryLoader.getOSFamily() + "/" + System.mapLibraryName(NITF_LIBRARY_NAME));
		}
    }

    /* This is the memory address of the underlying native object */
    protected long address = INVALID_ADDRESS;

    /**
     * Constructs a new NITF Object, using the native address supplied for the
     * underlying operations
     * 
     * @param address
     *            the native memory address
     */
    protected NITFObject(long address)
    {
        this.address = address;
    }

    /**
     * Default constructor. The object will not be valid unless an address is
     * supplied later.
     */
    protected NITFObject()
    {
        address = 0;
    }

    /**
     * Returns the native memory address
     * 
     * @return
     */
    synchronized long getAddress()
    {
        return address;
    }

    /**
     * Sets the native memory address
     * 
     * @param address
     */
    synchronized void setAddress(long address)
    {
        this.address = address;
    }

    /**
     * Returns true if the object is valid, false otherwise
     * 
     * @return
     */
    public synchronized boolean isValid()
    {
        return address != INVALID_ADDRESS;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof NITFObject
                && ((NITFObject) obj).getAddress() == getAddress();
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        address = INVALID_ADDRESS;
    }
}
