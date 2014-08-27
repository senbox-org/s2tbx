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
import java.util.Collection;
import java.util.Iterator;

/**
 * A representation of an extensions segment in the NITF specification.
 * Extensions segments contain TREs, which can be found at the end of component
 * segments.
 */
public final class Extensions extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    Extensions(long address)
    {
        super(address);
    }

    /**
     * Append a TRE to this extension segment
     * 
     * @param tre
     *            the TRE to append
     * @throws NITFException
     */
    public native void appendTRE(TRE tre) throws NITFException;

    /**
     * Returns an array of TREs with the given name
     * 
     * @param name
     *            the name of the TRE(s) to return
     * @return an array of TREs with the given name, or null if none exist
     */
    public native Collection<TRE> getTREsByName(String name);

    /**
     * Remove all instances of a given TRE from this extensions object.
     * 
     * @param name
     *            the tag name of the TREs to remove
     * @throws NITFException
     */
    public native void removeTREsByName(String name) throws NITFException;

    /**
     * Returns true if a TRE with the given name exists, false otherwise
     * 
     * @param name
     *            the name of the TRE
     * @return true if a TRE with the given name exists, false otherwise
     */
    public native boolean exists(String name);

    /**
     * @return a Collection of the TREs in this extensions segment
     */
    public native Collection<TRE> getAll();

    /**
     * Retrieve an Iterator for FieldPairs. This will iterate through all of the
     * fields in the TRE, returning a FieldPair for each.
     * 
     * @return Iterator of FieldPairs
     */
    public Iterator<TRE> iterator()
    {
        return new ExtensionsIterator(this);
    }
    /**
     * An Iterator for TRE FieldPairs
     */
    static class ExtensionsIterator extends NITFObject implements Iterator<TRE>
    {
        protected ExtensionsIterator(Extensions ext)
        {
            construct(ext);
        }

        protected ExtensionsIterator(long address)
        {
            super(address);
        }

        public native boolean hasNext();

        public native TRE next();

        public native void remove();

        private native void construct(Extensions ext);
    }
    /**
     * @param version
     *            The NITF version
     * @return Computes and returns the length of the Extensions segment, in its
     *         current state
     */
    public native long computeLength(Version version);

    /**
     * This function is here only to simplify printing out data. Unless you
     * really like this formatting style, I recommend that you not use it.
     * 
     * @param out
     *            The stream to print to
     */
    public void print(PrintStream out) throws NITFException
    {
        final Iterable tres = getAll();
        for (Object tre1 : tres) {
            TRE tre = (TRE) tre1;
            tre.print(out);
        }
    }

}
