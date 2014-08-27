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

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

/**
 * A representation of the NITF Tagged Record Extensions (TRE)
 */
public final class TRE extends DestructibleObject
{

    /**
     * Creates a new TRE of the given type. The tag is the TRE tag (such as
     * "JITCID"). This uses the default id for the given TRE. In some cases, the
     * plug-in may not be able to find the TRE, or it may not be able to decide
     * which TREDescription to use by default. In those cases, a NITFException
     * will be thrown.
     * 
     * @param tag
     *            the type of TRE to create
     * @param id
     *            the id of the TRE Description to use, or null to use the
     *            default
     * @throws NITFException
     *             if tag is an unkown type, or if the default description can
     *             not be identified
     */
    public TRE(String tag) throws NITFException
    {
        this(tag, null);
    }

    /**
     * Creates a new TRE of the given type. The tag is the TRE tag (such as
     * "JITCID"). The id is the identifier of the TREDescription. Each
     * TREDescription has its own identifier pertaining to a specific revision,
     * etc. Many TREs will not have a revision, so you can leave the id null.
     * 
     * @param tag
     *            the type of TRE to create
     * @param id
     *            the id of the TRE Description to use, or null to use the
     *            default
     * @throws NITFException
     *             if tag is an unkown type
     */
    public TRE(String tag, String id) throws NITFException
    {
        if (!PluginRegistry.canHandleTRE(tag))
            throw new NITFException(
                    "TRE Handler cannot be found for this TRE: " + tag);

        construct(tag, id);
    }

    /**
     * @see NITFObject#NITFObject(long)
     */
    TRE(long address)
    {
        super(address);
    }

    private native void construct(String tag, String id) throws NITFException;

    /**
     * Returns the (possibly computed) current size of this TRE
     * 
     * @return
     */
    public native int getCurrentSize();

    /**
     * Returns the TRE identifier tag
     * 
     * @return the TRE identifier tag
     */
    public native String getTag();

    /**
     * Returns true if the given field tag exists within the TRE The tag is the
     * identifier for the field. Check out the TRE Descriptions for a listing of
     * the field tags for each TRE. Note - Fields that loop will have a tag
     * TAG[i], such that i is the number in the loop, starting with 0
     * 
     * @param tag
     *            the identifier for the field
     * @return true if the field exists, false otherwise
     */
    public native boolean exists(String tag);

    /**
     * Returns a List of Fields that match the given pattern.
     * 
     * TODO : more documentation here
     * 
     * @param pattern
     *            the pattern to match
     * @return Fields that match the pattern
     * @throws NITFException
     *             if an error occurs or no field with with the given tag exists
     */
    public native List<FieldPair> find(String pattern) throws NITFException;

    /**
     * Returns the Field associated with the given tag
     * 
     * @param tag
     *            the identifier for the field
     * @return the Field, if found, or null if not found
     * @throws NITFException
     *             if an error occurs
     */
    public native Field getField(String tag) throws NITFException;

    /**
     * Attempts to set the value of the field referenced by tag to the data
     * given. Throws a NITFException if an error occurs.
     * 
     * @param tag
     *            the identifier for the field
     * @param data
     *            the data to use
     * @throws NITFException
     *             if an error occurs
     */
    public native boolean setField(String tag, byte[] data)
            throws NITFException;

    /**
     * Attempts to set the value of the field referenced by tag to the data
     * given. Throws a NITFException if an error occurs.
     * 
     * @param tag
     *            the identifier for the field
     * @param data
     *            the String to set the field to
     * @throws NITFException
     *             if an error occurs
     */
    public boolean setField(String tag, String data) throws NITFException
    {
        return setField(tag, data.getBytes());
    }

    /**
     * Prints the contents of the TRE to the given PrintStream
     * 
     * @param stream
     *            PrintStream to print to
     * @throws NITFException
     */
    public void print(PrintStream stream) throws NITFException
    {
        stream.println("\n---------------" + getTag() + "---------------");

        for (TREIterator it = iterator(); it.hasNext();)
        {
            FieldPair pair = it.next();

            String desc = it.getFieldDescription();

            Field field = pair.getField();
            stream
                    .println(pair.getName()
                            + (desc != null ? (" (" + desc + ")") : "")
                            + " = ["
                            + (field.getType() == FieldType.NITF_BINARY ? ("<binary stuff, length = "
                                    + field.getLength() + ">")
                                    : field.toString()) + "]");
        }

        stream.println("------------------------------------");
    }

    /**
     * Retrieve an Iterator for FieldPairs. This will iterate through all of the
     * fields in the TRE, returning a FieldPair for each.
     * 
     * @return Iterator of FieldPairs
     */
    public TREIterator iterator()
    {
        return new TREIterator(this);
    }

    /**
     * A simple class that models a name/field pair
     */
    public static final class FieldPair
    {
        protected String name;

        protected Field field;

        protected FieldPair()
        {
        }

        public String getName()
        {
            return name;
        }

        public Field getField()
        {
            return field;
        }
    }

    /**
     * An Iterator for TRE FieldPairs
     */
    public static final class TREIterator extends NITFObject implements
            Iterator<FieldPair>
    {
        protected TREIterator(TRE tre)
        {
            construct(tre);
        }

        protected TREIterator(long address)
        {
            super(address);
        }

        public native boolean hasNext();

        public native FieldPair next();

        public native String getFieldDescription();

        public void remove()
        {
            // do nothing - not supported
        }

        private native void construct(TRE tre);
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
