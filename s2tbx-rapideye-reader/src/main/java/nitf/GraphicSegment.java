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
 * A representation of the NITF Graphic (Symobl) Segment
 */
public final class GraphicSegment extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    GraphicSegment(long address)
    {
        super(address);
    }

    /**
     * Returns the GraphicSubheader from this segment
     * 
     * @return the GraphicSubheader from this segment
     */
    public native GraphicSubheader getSubheader();

    /**
     * Returns the file offset where this graphic segment starts
     * 
     * @return the file offset where this graphic segment starts
     */
    public native long getOffset();

    /**
     * Returns the end offset
     * 
     * @return the end offset
     */
    public native long getEnd();

}
