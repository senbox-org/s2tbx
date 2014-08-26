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
 * <code>PluginRegistry</code> This class provides an entrypoint to loading
 * plugins
 */
public final class PluginRegistry extends NITFObject
{
    /*
     * Note to Developer: I extended NITFObject solely to make sure the nitf-jni
     * library got loaded statically. This happens in NITFObject, so it only
     * made sense... Since this only has static functions, we hide the
     * constructors
     */

    // private
    private PluginRegistry()
    {
    }

    /**
     * This attempts to load plugins from the given directory
     * 
     * @param dirName
     *            plugin directory to load
     * @throws NITFException
     */
    public static native void loadPluginDir(String dirName)
            throws NITFException;

    /**
     * Returns true if the TRE is found in the registry, false otherwise
     * 
     * @param name
     *            tre name
     * @return
     */
    public static native boolean canHandleTRE(String name);
}
