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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a source of an image band
 */
public abstract class BandSource extends DestructibleObject
{

    /**
     * This maps addresses to BandSource objects. Since this is a special case,
     * where we do underlying callback code, we need to know if this is a
     * user-extended BandSource, or one of the provided ones.
     */
    protected static final Map bandSourceMap = Collections.synchronizedMap(new HashMap());

    /**
     * Constructor
     * 
     * @param address
     *            the memory address of the underlying object
     */
    BandSource(long address)
    {
        super(address);
    }

    /**
     * Default Constructor
     */
    protected BandSource()
    {
        super();

        // ////////////////////////////////////////////////////
        // THIS IS A VERY IMPORTANT STEP
        // WE MUST FIRST CHECK IF THIS IS ONE OF THE BUILT-IN
        // BANDSOURCE TYPES (MEMORYSOURCE OR FILESOURCE)
        // IF SO, WE DO NOT CONSTRUCT THE UNDERLYING MEMORY
        // BECAUSE WE ALREADY DO SO FOR THOSE TYPES
        // THIS IS MORE MEANT FOR USER-EXTENDED BANDSOURCES

        // parse the stack trace to get the calling class
        Throwable t = new Throwable();
        StackTraceElement stea[] = t.getStackTrace();
        StackTraceElement caller = stea[1];

        try
        {
            // get the calling class
            Class callerClass = BandSource.class.getClassLoader().loadClass(
                    caller.getClassName());
            // Class.forName(caller.getClassName());
            if (!callerClass.equals(MemorySource.class)
                    && !callerClass.equals(FileSource.class))
                construct();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            // we have to construct because it is a class not in the
            // classpath that has extended BandSource
            construct();
        }
    }

    /**
     * Constructs the underlying memory
     */
    private native void construct();

    /**
     * Reads size bytes from the BandSource, and stores it in the given byte buf
     * 
     * @param buf
     *            The data buffer
     * @param size
     *            The number of bytes to read
     * @throws NITFException
     */
    public abstract void read(byte[] buf, int size) throws NITFException;
    
    public abstract long getSize() throws NITFException;
    
    public abstract void setSize(long size) throws NITFException;

    /**
     * This returns the BandSource object represented by the given underlying
     * memory address. This helps the JNI code, since this is a special class
     * where we do different things depending on the extended class type.
     * 
     * @param address
     * @return
     */
    protected static final BandSource getByAddress(long address)
    {
        /*BandSource source = null;
        synchronized (bandSourceMap)
        {
            final Object o = bandSourceMap.get(address);
            if (o != null)
            {
                source = (BandSource) o;
            }
        }
        return source;*/
        return (BandSource) bandSourceMap.get(address);
    }

    /**
     * This sets the class type for the given BandSource instance
     * 
     * @param bandSource
     */
    protected static final void register(BandSource bandSource)
    {
        synchronized (bandSourceMap)
        {
            final Long key = bandSource.getAddress();
            if (!bandSourceMap.containsKey(key))
                bandSourceMap.put(key, bandSource);
        }
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
