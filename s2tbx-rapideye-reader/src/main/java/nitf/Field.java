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
 * A Field corresponds to the data and basic information regarding a NITF field.
 * Each field in the NITF headers has a Field associated with it.
 */
public class Field extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    Field(long address)
    {
        super(address);
    }

    /**
     * Returns the Value type Either NITF_BCS_A, NITF_BCS_N, or NITF_BINARY
     * 
     * @return
     */
    public native FieldType getType();

    /**
     * Returns the field length
     * 
     * @return the length of the field
     */
    public native long getLength();

    /**
     * Sets the raw data to the data specified If the size of data is larger
     * than the field size, the data is truncated.
     * 
     * @param data
     *            raw data
     * @return true if the data gets set, false otherwise
     */
    public native boolean setRawData(byte[] data);

    /**
     * Sets the raw data to the the data from the input String
     * 
     * @param data
     *            input String data
     * @return true if the data gets set, false otherwise
     */
    public native boolean setData(String data);

    /**
     * Return the raw data for this Field
     * 
     * @return raw byte array, or null if an error occurs
     */
    public native byte[] getRawData();

    /**
     * Return the data formatted as a String This should only be used if you
     * know the data consists of valid characters.
     * 
     * @return data formatted as a String, or null if an error occurs
     */
    public native String getStringData();

    /**
     * Return the data formatted as an integer This should only be used if you
     * know the data consists of valid integer characters.
     * 
     * @return data formatted as an integer
     */
    public native int getIntData();

    /**
     * Return the data formatted as an double This should only be used if you
     * know the data consists of valid characters capable of converting to a
     * double.
     * 
     * @return data formatted as an double
     */
    /* public native double getRealData(); */

    /**
     * Overrides the toString() function Same as calling getStringData()
     * 
     * @return String representation of the data
     */
    public String toString()
    {
        try
        {
            return getStringData();
        }
        catch (Exception e)
        {
            // e.printStackTrace();
        }
        return new String(getRawData());
    }

}
