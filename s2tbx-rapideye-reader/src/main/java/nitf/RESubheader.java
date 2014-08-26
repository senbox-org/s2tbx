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

import java.io.PrintStream;

/**
 * A representation of the NITF Reserved Extensions subheader
 */
public final class RESubheader extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    RESubheader(long address) throws NITFException
    {
        super(address);
    }

    /**
     * Returns the File Part Type Field
     * 
     * @return
     */
    public native Field getFilePartType();

    /**
     * Returns the TypeID Field
     * 
     * @return
     */
    public native Field getTypeID();

    /**
     * Returns the RES Security Classification Field
     * 
     * @return
     */
    public native Field getSecurityClass();

    /**
     * Returns the FileSecurity info for this RES Subheader
     * 
     * @return
     */
    public native FileSecurity getSecurityGroup();

    /**
     * Returns the RES Version Field
     * 
     * @return
     */
    public native Field getVersion();

    /**
     * Returns the Subheader Fields Length Field
     * 
     * @return
     */
    public native Field getSubheaderFieldsLength();

    /**
     * Returns the Subheader Field data
     * 
     * @return
     */
    public native char[] getSubheaderFields();

    /**
     * Returns the data length
     * 
     * @return
     */
    public native long getDataLength();

    /**
     * Sets the data length
     * 
     * @param dataLength
     */
    public native void setDataLength(long dataLength);

    /**
     * Prints the data associated with the RESubheader to a PrintStream
     * 
     * @param out
     */
    public void print(PrintStream out)
    {
        out.println("FilePartType = [" + getFilePartType() + "]");
        out.println("TypeID = [" + getTypeID() + "]");
        out.println("Version = [" + getVersion() + "]");
        out.println("SecurityClass = [" + getSecurityClass() + "]");

        getSecurityGroup().print(out);

        out.println("SubheaderFieldsLength = [" + getSubheaderFieldsLength()
                + "]");
        out.println("DataLength = [" + getDataLength() + "]");
    }
}
