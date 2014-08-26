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
 * A representation of the image band information that is recorded in the NITF
 * image subheader.
 */
public final class BandInfo extends NITFObject
{
    BandInfo(long address)
    {
        super(address);
    }

    /**
     * Returns the Band Entries Per LUT Field
     * 
     * @return the Band Entries Per LUT Field
     */
    public native Field getBandEntriesPerLUT();

    /**
     * Returns the Image Filter Code Field
     * 
     * @return the Image Filter Code Field
     */
    public native Field getImageFilterCode();

    /**
     * Returns the Image Filter Condition Field
     * 
     * @return the Image Filter Condition Field
     */
    public native Field getImageFilterCondition();

    /**
     * Returns the Num LUTS Field
     * 
     * @return the Num LUTS Field
     */
    public native Field getNumLUTs();

    /**
     * Returns the Representation Field
     * 
     * @return the Representation Field
     */
    public native Field getRepresentation();

    /**
     * Returns the Subcategory Field
     * 
     * @return the Subcategory Field
     */
    public native Field getSubcategory();

    /**
     * Returns the LookUpTable associated with this BandInfo
     * 
     * @return the LookUpTable associated with this BandInfo, or null if none
     *         specified
     */
    public native LookupTable getLookupTable();

    /**
     * Sets the lookupTable for this BandInfo
     * 
     * @param lookupTable
     *            the LookupTable
     */
    public native void setLookupTable(LookupTable lookupTable);
}
