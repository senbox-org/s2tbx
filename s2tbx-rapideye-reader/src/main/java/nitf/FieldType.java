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
 * FieldType
 */
package nitf;

/**
 * FieldType <p/> This enumeration class provides the possible types for a Field
 */
public class FieldType
{
    /**
     * NITF Field Type: Represents a BCS-A character set
     */
    public static final FieldType NITF_BCS_A = new FieldType("NITF_BCS_A");

    /**
     * NITF Field Type: Represents a BCS-N character set
     */
    public static final FieldType NITF_BCS_N = new FieldType("NITF_BCS_N");

    /**
     * NITF Field Type: Represents a binary type
     */
    public static final FieldType NITF_BINARY = new FieldType("NITF_BINARY");

    protected final String myName; // for debug only

    protected FieldType(String name)
    {
        myName = name;
    }

    public String toString()
    {
        return myName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
//        if (obj == null)
//            return false;
//        if (!(obj instanceof FieldType))
//            return false;
        return obj != null && obj instanceof FieldType && obj.toString().equals(toString());
    }
}
