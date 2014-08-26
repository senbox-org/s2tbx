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
 * Class representing a NITF object that has underlying memory that can be
 * independently destructed.
 */
public abstract class DestructibleObject extends NITFObject
{

    /**
     * Default constructor
     */
    protected DestructibleObject()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param address
     *            the memory address of the underlying object
     */
    protected DestructibleObject(long address)
    {
        super(address);
        NITFResourceManager.getInstance().incrementRefCount(this);
    }

    /**
     * Sets the memory address, but also increments the reference count
     * 
     * @param address
     */
    synchronized void setAddress(long address)
    {
        super.setAddress(address);
        NITFResourceManager.getInstance().incrementRefCount(this);
    }

    /**
     * Actually destructs the underlying memory.
     */
    protected abstract MemoryDestructor getDestructor();

    /**
     * Attempts to destruct the underlying object, if it is not referenced
     * elsewhere.
     * 
     * Note: This method is public in order to give you more control of the
     * underlying memory. The garbage collector doesn't always finalize all
     * objects that go out of scope, so publicizing this could be useful.
     * 
     * If you try to access the object after it's underlying object has been
     * destroyed, you could be sunk. Call isValid() to see if it is still a
     * valid object.
     */
    @Deprecated
    public void destruct()
    {
        // does nothing - exists for backwards-compatibility
        // NITFResourceManager.getInstance().decrementRefCount(this);
    }

    @Override
    protected void finalize() throws Throwable
    {
        NITFResourceManager.getInstance()
                .decrementRefCount(getAddress(), false);
        super.finalize();
    }

    @Override
    public String toString()
    {
        return NITFResourceManager.getInstance().getObjectInfo(getAddress());
    }
}
