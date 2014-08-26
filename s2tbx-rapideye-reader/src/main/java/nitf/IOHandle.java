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
 * A representation of a handle to a file object.
 * <p/>
 * This class provides access to a native file object, so it does not use the
 * Java io library.
 */
public final class IOHandle extends NativeIOInterface
{
    /**
     * Creates a new file
     */
    public static final int NITF_CREATE = 10;

    /**
     * Truncates the file
     */
    public static final int NITF_TRUNCATE = 11;

    /**
     * Opens an existing file
     */
    public static final int NITF_OPEN_EXISTING = 12;

    private String fileName;

    // private long ioHandle;

    IOHandle(long address)
    {
        super(address);
    }

    /**
     * Creates a new IOHandle object for READING, using the file referenced from
     * a parent pathname string; This sets the access flag to
     * NITF_ACCESS_READONLY and the creation flag to NITF_OPEN_EXISTING
     * 
     * @param fileName
     *            the path of the file associated with this handle
     * @throws NITFException
     */
    public IOHandle(String fileName) throws NITFException
    {
        this(fileName, NITF_ACCESS_READONLY, NITF_OPEN_EXISTING);
    }

    /**
     * Creates a new IOHandle object, using the file referenced from a parent
     * pathname string and an access modifier.
     * 
     * @param fileName
     *            the path of the file associated with this handle
     * @param accessFlag
     *            options are <code>NITF_ACCESS_READONLY<code>,
     *                     <code>NITF_ACCESS_WRITEONLY</code>, or
     *            <code>NITF_ACCESS_READWRITE</code>
     * @param creationFlag
     *            options are <code>NITF_CREATE<code>,
     *                     <code>NITF_TRUNCATE</code>, or
     *            <code>NITF_OPEN_EXISTING</code>
     * @throws NITFException
     */
    public IOHandle(String fileName, int accessFlag, int creationFlag)
            throws NITFException
    {
        super();
        this.fileName = fileName;

        if (accessFlag != NITF_ACCESS_READONLY
                && accessFlag != NITF_ACCESS_READWRITE
                && accessFlag != NITF_ACCESS_WRITEONLY)
        {
            throw new NITFException(
                    "Access flag must be a valid NITF_ACCESS flag");
        }
        if (creationFlag != NITF_CREATE && creationFlag != NITF_TRUNCATE
                && creationFlag != NITF_OPEN_EXISTING)
        {
            throw new NITFException(
                    "Creation flag must be a valid NITF Creation flag");
        }

        createHandle(fileName, accessFlag, creationFlag);
    }

    /**
     * Return the name of the file associated with this handle
     * 
     * @return the name of the file associated with this handle
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Native function used internally to create a file handle
     * 
     * @param fileName
     * @param accessFlag
     * @param creationFlag
     * @return
     * @throws NITFException
     */
    private native void createHandle(String fileName, int accessFlag,
                                     int creationFlag) throws NITFException;
    
}
