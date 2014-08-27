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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class provides the functionality to manage the underlying memory
 */
public final class NITFResourceManager
{

    // this makes sure that the jni library gets loaded by NITFObject
    static
    {
        NITFObject.class.getName();
    }

    private static final Log log = LogFactory.getLog(NITFResourceManager.class);

    // the Singleton instance
    private static final NITFResourceManager singleton = new NITFResourceManager();

    /**
     * @return the singleton instance of the NITFResourceManager
     */
    public static NITFResourceManager getInstance()
    {
        return singleton;
    }

    private static class TrackedObject
    {
        private Long address = NITFObject.INVALID_ADDRESS;

        private Integer javaRefCount = 0;

        private Integer nativeRefCount = 0;

        private MemoryDestructor destructor = null;

        private String className = null;

        public TrackedObject(DestructibleObject object)
        {
            address = object.getAddress();
            className = object.getClass().getCanonicalName();
            destructor = object.getDestructor();
            javaRefCount = 1;
        }

        public void reference(boolean nativeRef)
        {
            if (nativeRef)
                nativeRefCount += 1;
            else
                javaRefCount += 1;
        }

        public void unReference(boolean nativeRef)
        {
            if (nativeRef)
                nativeRefCount -= 1;
            else
                javaRefCount -= 1;
        }

        boolean canDestroy()
        {
            return javaRefCount <= 0 && nativeRefCount <= 0;
        }

        boolean destroy()
        {
            return canDestroy() && destructor != null && destructor.destructMemory(address);
        }

        @Override
        public String toString()
        {
            return "[" + className + ", address=" + address + ", javaRefs="
                    + javaRefCount + ", nativeRefs=" + nativeRefCount + "]";
        }
    }

    private Map<Long, TrackedObject> trackedObjects;

    /**
     * Increments the java reference count of an object
     * 
     * @param object
     */
    protected void incrementRefCount(DestructibleObject object)
    {
        if (object != null && object.isValid())
        {
            long address = object.getAddress();
            if (trackedObjects.containsKey(address))
            {
                TrackedObject trackedObject = trackedObjects.get(address);
                trackedObject.reference(false);
                log.debug("Incremented ref count: " + trackedObject.toString());
            }
            else
            {
                TrackedObject trackedObject = new TrackedObject(object);
                trackedObjects.put(address, trackedObject);
                log.debug("Tracking new object: " + trackedObject.toString());
            }
        }
        else
        {
            log.error("Cannot reference invalid object");
        }
    }

    /**
     * Increments either the java or native reference count
     * 
     * @param address
     * @param nativeRef
     */
    protected void incrementRefCount(long address, boolean nativeRef)
    {
        if (address != NITFObject.INVALID_ADDRESS)
        {
            if (trackedObjects.containsKey(address))
            {
                TrackedObject trackedObject = trackedObjects.get(address);
                trackedObject.reference(nativeRef);
                log.debug("Incremented ref count: " + trackedObject.toString());
            }
            else
            {
                log.error("Unable to track unkown object: " + address);
            }
        }
        else
        {
            log.error("Cannot reference invalid address");
        }
    }

    /**
     * Decrements the reference count of an object, and destructs the object if
     * the count is now < 1
     * 
     * @param object
     */
    protected void decrementRefCount(long address, boolean nativeRef)
    {
        if (address != NITFObject.INVALID_ADDRESS)
        {
            // if its in here, update its count, if not, forget about it
            if (trackedObjects.containsKey(address))
            {
                TrackedObject trackedObject = trackedObjects.get(address);
                trackedObject.unReference(nativeRef);
                log.debug("Decremented ref count: " + trackedObject.toString());
                if (trackedObject.canDestroy())
                {
                    if (trackedObject.destroy())
                    {
                        trackedObjects.remove(address);
                        log.debug("Destroyed object: "
                                + trackedObject.toString());
                    }
                    else
                    {
                        log.error("Unable to destroy object: "
                                + trackedObject.toString());
                    }
                }
            }
            else
            {
                log
                        .warn("Unable to decrement reference count for untracked address: "
                                + address);
            }
        }
        else
        {
            log.error("Cannot reference invalid address");
        }
    }

    protected String getObjectInfo(long address)
    {
        TrackedObject trackedObject = trackedObjects.get(address);
        return trackedObject != null ? trackedObject.toString() : null;
    }

    /**
     * This attempts to load plugins from the directory given
     * 
     * @param dirName
     *            plugin directory to load
     * @throws NITFException
     */
    public static void loadPluginDir(String dirName) throws NITFException
    {
        PluginRegistry.loadPluginDir(dirName);
    }

    // private constructor
    private NITFResourceManager()
    {
        trackedObjects = Collections
                .synchronizedMap(new LinkedHashMap<Long, TrackedObject>());
    }

}
