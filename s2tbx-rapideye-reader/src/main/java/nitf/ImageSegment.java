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
 * A representation of the NITF Image Segment <p/> The image segment corresponds
 * to each image plus its subheader which exists in the NITF file. The segment
 * also contains some book-keeping information regarding its offsets.
 */
public final class ImageSegment extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    ImageSegment(long address)
    {
        super(address);
    }

    /**
     * Retrieve the subheader corresponding to this image segment
     * 
     * @return The associated image subheader
     */
    public native ImageSubheader getSubheader();

    /**
     * Retrieve the offset into the file for an image
     * 
     * @return An integer offset
     */
    public native long getImageOffset();

    /**
     * Retrieve the ending offset for the image
     * 
     * @return An integer offset
     */
    public native long getImageEnd();
}
