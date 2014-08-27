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
 * <code>DownSampler</code>
 * 
 * The abstract DownSampler class.
 * 
 */
public abstract class DownSampler extends DestructibleObject
{

    /**
     * This maps addresses to DownSampler objects. Since this is a special case,
     * where we do underlying callback code, we need to know if this is a
     * user-extended DownSampler, or one of the provided ones.
     */
    protected static final Map downSamplerMap = Collections
            .synchronizedMap(new HashMap());

    /**
     * @see DestructibleObject#DestructibleObject(long)
     */
    DownSampler(long address)
    {
        super(address);
    }

    /**
     * Make this private, so you can't instantiate just a plain DownSampler
     */
    protected DownSampler()
    {
        super();

        // ////////////////////////////////////////////////////
        // THIS IS A VERY IMPORTANT STEP
        // WE MUST FIRST CHECK IF THIS IS ONE OF THE BUILT-IN
        // DOWNSAMPLER TYPES (PIXELSKIP)
        // IF SO, WE DO NOT CONSTRUCT THE UNDERLYING MEMORY
        // BECAUSE WE ALREADY DO SO FOR THOSE TYPES
        // THIS IS MORE MEANT FOR USER-EXTENDED DOWNSAMPLERS

        // parse the stack trace to get the calling class
        Throwable t = new Throwable();
        StackTraceElement stea[] = t.getStackTrace();
        StackTraceElement caller = stea[1];

        try
        {
            // get the calling class
            Class callerClass = Class.forName(caller.getClassName());
            if (!callerClass.equals(PixelSkipDownSampler.class)
                    && !callerClass.equals(MaxDownSampler.class)
                    && !callerClass.equals(SumSq2BandDownSampler.class))
            {
                // construct();

                // TODO implement the JNI code that allows a callback...
                // for now, we do not allow any other subclasses
                throw new InstantiationError(
                        "Cannot create a subclass of DownSampler. This is currently unsupported.");
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Constructs the underlying memory
     */
    private native void construct();

    /**
     * This callback provides the interface for applying a sampling method while
     * reading. The object argument gives access to the userData field which
     * allows for customization of the available fields for this callback. A
     * transformation between the input window and the output window occurs in
     * this method.
     * 
     * @param inputWindows
     *            Array of input image fragments (one/band)
     * @param outputWindows
     *            Array of sub-sampled image fragments (one/band)
     * @param numBands
     *            Number of bands to down-sample
     * @param numWindowRows
     *            How many rows for the input window
     * @param numWindowCols
     *            How many cols for the input window
     * @param numInputCols
     *            Number of columns in input buffer, full res
     * @param numSubWindowCols
     *            The number of columns in the sub-window
     * @param pixelType
     *            The pixel type (valid values found in System.h)
     * @param pixelSize
     *            The size of one pixel
     * @param rowsInLastWindow
     *            The number of rows in the final window
     * @param colsInLastWindow
     *            The number of cols in the final window
     * @return NITF_SUCCESS on success, NITF_FAILURE on failure
     * 
     *         Note:
     * 
     *         The numWindowRows, numWindowCols, and numSubWindowCols values are
     *         in
     *         output image units (units of sample windows). For example, with a
     *         pixel
     *         skip of 3 in columns, if the sub-window request spans columns
     *         0-299, then
     *         numSubWindowCols is 100. If the block is such that a particular
     *         request
     *         spans columns 90-149 (60 full resolution columns), then
     *         numWindowCols is
     *         20. The numInputCols value is in full resolution units. This
     *         value gives
     *         the length, in pixels of one row in the input buffer. This buffer
     *         is used
     *         for all down-sample calls. Since the number of windows can vary
     *         from call
     *         to call, this buffer has a worst case length. Therefore, it is
     *         not
     *         possible to move from one row to the next with just the number of
     *         sample
     *         windows per row (numWindowCols) for the current request
     */
    protected abstract boolean apply(byte[][] inputWindows,
                                     byte[][] outputWindows, int numBands,
                                     int numWindowRows, int numWindowCols,
                                     int numInputCols, int numSubWindowCols,
                                     int pixelType, int pixelSize,
                                     int rowsInLastWindow, int colsInLastWindow)
            throws NITFException;

    /**
     * @return Returns the Row Skip size
     */
    public abstract int getRowSkip();

    /**
     * @return Returns the Column Skip size
     */
    public abstract int getColSkip();

    /**
     * @return Returns the minimum supported bands for this DownSampler
     */
    public abstract int getMinSupportedBands();

    /**
     * @return Returns the maximum supported bands for this DownSampler
     */
    public abstract int getMaxSupportedBands();

    /**
     * @return Returns true if this DownSampler only DownSamples in multi-band
     *         mode (usually this occurs if the algorithm applies to an image
     *         with 2 bands that are related, such as complex imagery).
     *         Otherwise, this returns false.
     */
    public abstract boolean isMultiBand();

    /**
     * This returns the DownSampler object represented by the given underlying
     * memory address. This helps the JNI code, since this is a special class
     * where we do different things depending on the extended class type.
     * 
     * @param address
     * @return
     */
    protected static final DownSampler getByAddress(long address)
    {
        DownSampler downSampler = null;
        synchronized (downSamplerMap)
        {
            final Object o = downSamplerMap.get(address);
            if (o != null)
            {
                downSampler = (DownSampler) o;
            }
        }
        return downSampler;
    }

    /**
     * This sets the class type for the given DownSampler instance
     * 
     * @param downSampler
     */
    protected static final void register(DownSampler downSampler)
    {
        synchronized (downSamplerMap)
        {
            final Long key = downSampler.getAddress();
            if (!downSamplerMap.containsKey(key))
                downSamplerMap.put(key, downSampler);
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
