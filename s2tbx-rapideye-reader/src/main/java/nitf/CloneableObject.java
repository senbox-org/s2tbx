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
 * This class is a NITFObject that can be cloned. <p/>
 * -------------------------- Note to Developer -------------------------- Since
 * this extends DestructibleObject, when it gets created, it is referenced
 * within the NITFUtil class. When we clone things, we want a separate reference
 * to that new memory. Since DestructibleObject takes care of that for us, we
 * don't need anything separate in here that tracks the clone memory. <p/> For
 * some objects that we may want to make cloneable from within Java, we may not
 * want to destruct the memory of the original, since it might be part of a
 * larger structure (for example, FileHeader is part of a Record, and should not
 * be destructed or else it will disrupct the parent Record). However, we will
 * want to be able to destruct the clone object. For this reason, these
 * particular objects will have to check to see if itself is a clone first
 * before calling the super.destruct(). Thus, for some objects, it is a good
 * idea to override the DestructibleObject.destruct() method. Keep that in mind.
 */
public abstract class CloneableObject extends DestructibleObject
{

    /* keeps track of the clone status */
    private boolean clone = false;

    /**
     * Returns true if it is a clone, false otherwise
     * 
     * @return
     */
    final boolean isClone()
    {
        return clone;
    }

    /**
     * Sets the clone status
     * 
     * @param clone
     */
    final void setClone(boolean clone)
    {
        this.clone = clone;
    }

    /**
     * Returns a clone of this NITFObject
     * 
     * @return
     * @throws NITFException
     */
    public abstract CloneableObject makeClone() throws NITFException;

    /**
     * @see DestructibleObject
     */
    protected CloneableObject()
    {
        super();
    }

    /**
     * @see DestructibleObject#DestructibleObject(long)
     */
    protected CloneableObject(long address)
    {
        super(address);
    }

}
