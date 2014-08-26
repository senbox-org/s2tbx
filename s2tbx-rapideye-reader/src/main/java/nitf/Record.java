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

/**
 * Represents a NITF record
 * <p/>
 * The Record object is a database-like record of the fields in a NITF file. It
 * a repository for information on reads, and as a source of information on
 * writes.
 * <p/>
 * A Record contains a file header, and various segments associated with
 * different sections of the NITF file. Segments and headers contain extensions
 * known as TREs and they are stored in hierarchical format in the components.
 */
public final class Record extends CloneableObject
{

    /**
     * Creates a new NITF 2.1 Record
     * 
     */
    public Record() throws NITFException
    {
        construct(Version.NITF_21);
    }

    /**
     * Creates a new record, of the given Version
     * 
     * @param version
     *            The Version of the NITF to create
     */
    public Record(Version version) throws NITFException
    {
        construct(version);
    }

    /**
     * @see CloneableObject#CloneableObject(long)
     */
    Record(long address)
    {
        super(address);
    }

    /**
     * Constructs the underlying memory
     * 
     * @param version
     *            The Version of the NITF to create
     * @throws NITFException
     */
    private native void construct(Version version) throws NITFException;

    /**
     * Returns a clone of this Record object, including it's underlying
     * structures
     * <p/>
     * Use this method when you want two entirely separate objects that point to
     * different memory locations. This will allow you to change each object
     * independently of one another. This could be useful if you read in a
     * Record, and want to modify the original in many ways.
     * 
     * @return a clone of this Record
     * @see CloneableObject
     */
    public native Record makeClone() throws NITFException;

    /**
     * Accessor for file header.
     * 
     * @return The file header component to the client
     */
    public native FileHeader getHeader() throws NITFException;

    /**
     * Get the array of image segments found in the NITF file
     * 
     * @return The segment array back to the client
     */
    public native ImageSegment[] getImages() throws NITFException;

    /**
     * Get the array of graphics segments found in the NITF file. This object is
     * only populated for NITF 2.1 files.
     * 
     * @return The segment array back to the client
     */

    public native GraphicSegment[] getGraphics() throws NITFException;

    /**
     * Get the array of labels segments found in the NITF file. This object is
     * only populated for NITF 2.0 files.
     * 
     * @return The segment array back to the client
     */
    public native LabelSegment[] getLabels() throws NITFException;

    /**
     * Get the array of text segments found in the NITF file. This object is
     * only populated for NITF 2.0 files.
     * 
     * @return The segment array back to the client
     */
    public native TextSegment[] getTexts() throws NITFException;

    /**
     * Get the array of data extension segments found in the NITF file. These
     * mostly contain overflow data.
     * 
     * @return The segment array back to the client
     */
    public native DESegment[] getDataExtensions() throws NITFException;

    /**
     * Get the array of reserved extension segments found in the NITF file. This
     * segment is not really used currently in NITF.
     * 
     * @return The segment array back to the client
     */
    public native RESegment[] getReservedExtensions() throws NITFException;

    /**
     * Adds an ImageSegment to this Record, and returns a handle to it
     * <p/>
     * NOTE: This also adds a componentInfo object to the FileHeader
     * 
     * @throws NITFException
     */
    public native ImageSegment newImageSegment() throws NITFException;

    /**
     * Adds a GraphicSegment to this Record, and returns a handle to it
     * <p/>
     * NOTE: This also adds a componentInfo object to the FileHeader
     * 
     * @throws NITFException
     */
    public native GraphicSegment newGraphicSegment() throws NITFException;

    /**
     * Adds a TextSegment to this Record, and returns a handle to it
     * <p/>
     * NOTE: This also adds a componentInfo object to the FileHeader
     * 
     * @throws NITFException
     */
    public native TextSegment newTextSegment() throws NITFException;

    /**
     * Adds a DESegment to this Record, and returns a handle to it
     * <p/>
     * NOTE: This also adds a componentInfo object to the FileHeader
     * 
     * @throws NITFException
     */
    public native DESegment newDESegment() throws NITFException;

    /**
     * This removes the segment at the given offset. The segment is
     * de-allocated, and removed from the system.
     * 
     * CAUTION!!! -- Removing a segment removes the underlying memory, so any
     * usage of the Java segment will result in unknown behavior.
     * 
     * @param segmentNumber
     * @throws NITFException
     */
    public native void removeImageSegment(int segmentNumber)
            throws NITFException;

    /**
     * This removes the segment at the given offset. The segment is
     * de-allocated, and removed from the system.
     * 
     * CAUTION!!! -- Removing a segment removes the underlying memory, so any
     * usage of the Java segment will result in unknown behavior.
     * 
     * @param segmentNumber
     * @throws NITFException
     */
    public native void removeGraphicSegment(int segmentNumber)
            throws NITFException;

    /**
     * This removes the segment at the given offset. The segment is
     * de-allocated, and removed from the system.
     * 
     * CAUTION!!! -- Removing a segment removes the underlying memory, so any
     * usage of the Java segment will result in unknown behavior.
     * 
     * @param segmentNumber
     * @throws NITFException
     */
    public native void removeLabelSegment(int segmentNumber)
            throws NITFException;

    /**
     * This removes the segment at the given offset. The segment is
     * de-allocated, and removed from the system.
     * 
     * CAUTION!!! -- Removing a segment removes the underlying memory, so any
     * usage of the Java segment will result in unknown behavior.
     * 
     * @param segmentNumber
     * @throws NITFException
     */
    public native void removeTextSegment(int segmentNumber)
            throws NITFException;

    /**
     * This removes the segment at the given offset. The segment is
     * de-allocated, and removed from the system.
     * 
     * CAUTION!!! -- Removing a segment removes the underlying memory, so any
     * usage of the Java segment will result in unknown behavior.
     * 
     * @param segmentNumber
     * @throws NITFException
     */
    public native void removeDataExtensionSegment(int segmentNumber)
            throws NITFException;

    /**
     * This removes the segment at the given offset. The segment is
     * de-allocated, and removed from the system.
     * 
     * CAUTION!!! -- Removing a segment removes the underlying memory, so any
     * usage of the Java segment will result in unknown behavior.
     * 
     * @param segmentNumber
     * @throws NITFException
     */
    public native void removeReservedExtensionSegment(int segmentNumber)
            throws NITFException;

    /**
     * Move an ImageSegment from the oldIndex to the nexIndex
     * 
     * @param oldIndex
     *            the current of the segment to move
     * @param newIndex
     *            the new index of the segment
     * @throws NITFException
     */
    public native void moveImageSegment(int oldIndex, int newIndex)
            throws NITFException;

    /**
     * Move a GraphicSegment from the oldIndex to the nexIndex
     * 
     * @param oldIndex
     *            the current of the segment to move
     * @param newIndex
     *            the new index of the segment
     * @throws NITFException
     */
    public native void moveGraphicSegment(int oldIndex, int newIndex)
            throws NITFException;

    /**
     * Move a TextSegment from the oldIndex to the nexIndex
     * 
     * @param oldIndex
     *            the current of the segment to move
     * @param newIndex
     *            the new index of the segment
     * @throws NITFException
     */
    public native void moveTextSegment(int oldIndex, int newIndex)
            throws NITFException;

    /**
     * Move a LabelSegment from the oldIndex to the nexIndex
     * 
     * @param oldIndex
     *            the current of the segment to move
     * @param newIndex
     *            the new index of the segment
     * @throws NITFException
     */
    public native void moveLabelSegment(int oldIndex, int newIndex)
            throws NITFException;

    /**
     * Move a DataExtensionSegment from the oldIndex to the nexIndex
     * 
     * @param oldIndex
     *            the current of the segment to move
     * @param newIndex
     *            the new index of the segment
     * @throws NITFException
     */
    public native void moveDataExtensionSegment(int oldIndex, int newIndex)
            throws NITFException;

    /**
     * Move a ReservedExtensionSegment from the oldIndex to the nexIndex
     * 
     * @param oldIndex
     *            the current of the segment to move
     * @param newIndex
     *            the new index of the segment
     * @throws NITFException
     */
    public native void moveReservedExtensionSegment(int oldIndex, int newIndex)
            throws NITFException;

    /**
     * This function is here only to simplify printing out data. Unless you
     * really like this formatting style, I recommend that you not use it.
     * 
     * @param out
     *            The stream to print to
     */
    public void print(PrintStream out) throws NITFException
    {
        out.println("----- Header -----");
        getHeader().print(out);

        final ImageSegment[] images = getImages();
        for (int i = 0; i < images.length; i++)
        {
            out.println("----- Image[" + i + "] -----");
            ImageSegment image = images[i];
            image.getSubheader().print(out);
        }

        final GraphicSegment[] graphics = getGraphics();
        for (int i = 0; i < graphics.length; i++)
        {
            out.println("----- Graphic[" + i + "] -----");
            GraphicSegment graphic = graphics[i];
            graphic.getSubheader().print(out);
        }

        final LabelSegment[] labels = getLabels();
        for (int i = 0; i < labels.length; i++)
        {
            out.println("----- Label[" + i + "] -----");
            LabelSegment label = labels[i];
            label.getSubheader().print(out);
        }

        final TextSegment[] texts = getTexts();
        for (int i = 0; i < texts.length; i++)
        {
            out.println("----- Text[" + i + "] -----");
            TextSegment text = texts[i];
            text.getSubheader().print(out);
        }

        final DESegment[] dataExtensions = getDataExtensions();
        for (int i = 0; i < dataExtensions.length; i++)
        {
            out.println("----- DES[" + i + "] -----");
            DESegment dataExtension = dataExtensions[i];
            dataExtension.getSubheader().print(out);
        }

        final RESegment[] reservedExtensions = getReservedExtensions();
        for (int i = 0; i < reservedExtensions.length; i++)
        {
            out.println("----- RES[" + i + "] -----");
            RESegment reservedExtension = reservedExtensions[i];
            reservedExtension.getSubheader().print(out);
        }
    }

    /**
     * This moves all TREs in TRE_OVERFLOW to the appropriate header
     * or subheader user data segment.
     * 
     * @throws NITFException
     */
    public native void mergeTREs() throws NITFException;
    
    /**
     * This moves TREs that are too big to fit in header or subheader
     * sections to TRE_OVERFLOW. NITRO will automatically call this
     * when writing a NITF file.
     * 
     * @throws NITFException
     */
    public native void unmergeTREs() throws NITFException;
    
    /**
     * Returns the version of NITF this Record represents
     * 
     * @return the version of NITF this Record represents
     * @see Version
     */
    public native Version getVersion();

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
