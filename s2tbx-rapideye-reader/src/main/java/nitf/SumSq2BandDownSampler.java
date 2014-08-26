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
 * <code>SumSq2BandDownSampler</code>
 * 
 * Sum of square, two band, down-sample method
 * 
 * The maximum is calculated as the sum of the sum of squares of two bands. The
 * caller must supply exactly two bands. The complex pixel type as the
 * individual band pixel type is not supported
 * 
 * For a more comprehensive discussion of the merits and drawbacks of this type
 * of down-sampling, please refer to the NITF manual 1.1.
 * 
 * Created: Oct 6, 2005 9:23:06 PM
 */
public final class SumSq2BandDownSampler extends DownSampler
{

    /**
     * @param address
     */
    public SumSq2BandDownSampler(long address)
    {
        super(address);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     * @param skipRows
     * @param skipCols
     */
    public SumSq2BandDownSampler(int skipRows, int skipCols)
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
