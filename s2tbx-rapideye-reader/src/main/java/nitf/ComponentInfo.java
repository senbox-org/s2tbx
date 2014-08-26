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
 * A representation of the component information found in the NITF File Header.
 * Each component (images, graphics, etc.) has metadata pertaining to the length
 * of the data, and length of the subheader. This class captures that.
 */
public final class ComponentInfo extends NITFObject
{

    ComponentInfo(long address)
    {
        super(address);
    }

    /**
     * Returns the length of the Subheader
     * 
     * @return
     */
    public native Field getLengthSubheader();

    /**
     * Returns the length of the Data
     * 
     * @return
     */
    public native Field getLengthData();

}
