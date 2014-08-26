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

/**
 * 
 */
package nitf;

/**
 * <code>MaxDownSampler</code>
 * 
 * The maxDownSample selects the maximum pixel <p/> The row and column skip
 * factors divide the sub-window into non-overlaping sample windows. The pixel
 * with the maximum value in each sample window is the down-sampled value for
 * that window. For complex images, the maximum the maximum absolute value (the
 * corresponding complex value, not the real absolute value is the down-sample
 * value.
 * 
 * Created: Oct 6, 2005 9:23:06 PM
 */
public final class MaxDownSampler extends DownSampler
{

    /**
     * @param address
     */
    public MaxDownSampler(long address)
    {
        super(address);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     * @param skipRows
     * @param skipCols
     */
    public MaxDownSampler(int skipRows, int skipCols)
    {
        construct(skipRows, skipCols);
    }

    /**
     * 
     * @param skipRows
     * @param skipCols
     */
    private native void construct(int skipRows, int skipCols);

    /*
     * (non-Javadoc)
     * 
     * @see nitf.DownSampler#apply(byte[][], byte[][], int, int, int, int, int,
     *      int, int, int, int)
     */
    protected native boolean apply(byte[][] inputWindows,
            byte[][] outputWindows, int numBands, int numWindowRows,
            int numWindowCols, int numInputCols, int numSubWindowCols,
            int pixelType, int pixelSize, int rowsInLastWindow,
            int colsInLastWindow) throws NITFException;

    /*
     * (non-Javadoc)
     * 
     * @see nitf.DownSampler#getRowSkip()
     */
    public native int getRowSkip();

    /*
     * (non-Javadoc)
     * 
     * @see nitf.DownSampler#getColSkip()
     */
    public native int getColSkip();

    /*
     * (non-Javadoc)
     * 
     * @see nitf.DownSampler#getMinSupportedBands()
     */
    public native int getMinSupportedBands();

    /*
     * (non-Javadoc)
     * 
     * @see nitf.DownSampler#getMaxSupportedBands()
     */
    public native int getMaxSupportedBands();

    /*
     * (non-Javadoc)
     * 
     * @see nitf.DownSampler#isMultiBand()
     */
    public native boolean isMultiBand();

}
