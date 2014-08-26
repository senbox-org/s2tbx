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
 * A representation of the NITF Data Extension Segment.
 */
public final class DESegment extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    DESegment(long address)
    {
        super(address);
    }

    /**
     * Returns the DESubheader from this segment
     * 
     * @return the DESubheader from this segment
     */
    public native DESubheader getSubheader();

    /**
     * Returns the file offset where this DES segment starts
     * 
     * @return the file offset where this DES segment starts
     */
    public native long getOffset();

    /**
     * Return the end marker
     * 
     * @return the end marker
     */
    public native long getEnd();

}
