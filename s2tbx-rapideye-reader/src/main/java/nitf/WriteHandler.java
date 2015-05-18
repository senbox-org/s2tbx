/*
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 *  with this program; if not, see http://www.gnu.org/licenses/
 */

package nitf;

/**
 * The WriteHandler class is essentially an interface for writing to an
 * IOInterface, but is an abstract class because under the covers it sets up
 * some native code that will facilitate the callback to your write method.
 */
public abstract class WriteHandler extends DestructibleObject {

    /**
     * Default constructor
     */
    public WriteHandler() {
        construct();
    }

    private native void construct();

    /**
     * Write to the given IOHandle. This is user-defined.
     *
     * @param io
     * @throws NITFException
     */
    public abstract void write(IOInterface io) throws NITFException;

    @Override
    protected MemoryDestructor getDestructor() {
        return new Destructor();
    }

    private static class Destructor implements MemoryDestructor {
        public native boolean destructMemory(long nativeAddress);
    }

}
