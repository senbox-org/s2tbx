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
 * Enumeration class for the NITF version
 */
public enum Version
{

    /**
     * Represents a NITF 2.0 type
     */
    NITF_20("NITF 2.0"),

    /**
     * Represents a NITF 2.1 type
     */
    NITF_21("NITF 2.1"),

    /**
     * Unknown type
     */
    UNKNOWN;

    // a name that can be queried that describes the version
    private String name;

    public String toString()
    {
        return name != null ? name : super.toString();
    }

    // keep private
    private Version(String name)
    {
        this.name = name;
    }

    private Version()
    {
        this(null);
    }

}
