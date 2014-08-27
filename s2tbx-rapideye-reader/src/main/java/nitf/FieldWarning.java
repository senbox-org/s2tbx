/*
 * =========================================================================
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, If not,
 * see <http://www.gnu.org/licenses/>.
 */

package nitf;

/**
 * This class represents a Warning that is created by the NITF system while
 * processing the data in some way. Usually a FieldWarning is created when a
 * Field does not contain valid data.
 * 
 */
public class FieldWarning extends DestructibleObject
{
    /**
     * @see NITFObject#NITFObject(long)
     */
    FieldWarning(long address)
    {
        super(address);
    }

    /**
     * Returns the name of the offending field
     * 
     * @return name of the field, or null
     */
    public native String getFieldName();

    /**
     * Returns the offending Field object, or null if the warning is not
     * associated with one particular Field
     * 
     * @return a Field object, or null
     */
    public native Field getField();

    /**
     * Returns the warning message, or expectation message
     * 
     * @return the warning message, or null
     */
    public native String getWarning();

    /**
     * Returns the file offset if it is possible for the library to figure it
     * out. Otherwise, (if the warning was not created while parsing the file)
     * it will most likely return 0.
     * 
     * @return
     */
    public native long getFileOffset();

    /**
     * Overrides the toString() function
     * 
     * @return String representation of the warning
     */
    public String toString()
    {
        Field field = getField();
        String warning = getWarning();
        String name = getFieldName();
        StringBuilder buf = new StringBuilder("WARNING: [");

        if (name != null)
        {
            buf.append(name);
        }
        else
        {
            buf.append("UNKNOWN");
        }
        buf.append("] --> ");
        if (warning != null)
        {
            buf.append("[").append(warning).append("]");
        }

        if (field != null)
        {
            String data = field.getStringData();
            if (data != null)
            {
                buf.append(" data = [").append(data).append("]");
            }
        }

        return buf.toString();
    }

    @Override
    protected MemoryDestructor getDestructor()
    {
        return new Destructor();
    }

    private static class Destructor implements MemoryDestructor
    {
        public native boolean destructMemory(long nativeAddress);
    }

}
