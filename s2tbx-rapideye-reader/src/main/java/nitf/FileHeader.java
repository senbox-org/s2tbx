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

/**
 * Represents the NITF file header <p/> In NITF, the file header is the first
 * section of a file (before the image subheaders). It gives the reader
 * information about the length of the file, and how many components to expect.
 * It also contains security info and TRE extensions
 */
public final class FileHeader extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    FileHeader(long address)
    {
        super(address);
    }

    /**
     * Retrieve the background color
     * 
     * @return A Field corresponding to the field
     */
    public native Field getBackgroundColor();

    /**
     * Retrieve the file classification
     * 
     * @return A Field corresponding to the field
     */
    public native Field getClassification();

    /**
     * Retrieve the NITF compliance level of the writer that wrote this file.
     * 
     * @return A Field corresponding to the field
     */
    public native Field getComplianceLevel();

    /**
     * Is file data encrypted?
     * 
     * @return A Field corresponding to the field
     */
    public native Field getEncrypted();

    /**
     * Get extended header length (if any).
     * 
     * @return A Field corresponding to the field
     */
    public native Field getExtendedHeaderLength();

    /**
     * Get the extended header overflow.
     * 
     * @return A Field corresponding to the field
     */
    public native Field getExtendedHeaderOverflow();

    /**
     * Get time and date.
     * 
     * @return A Field corresponding to the field
     */
    public native Field getFileDateTime();

    /**
     * Get the file header representation (NITF or NSIF).
     * 
     * @return A Field corresponding to the field
     */
    public native Field getFileHeader();

    /**
     * Get the full length in bytes of the file
     * 
     * @return A Field corresponding to the field
     */
    public native Field getFileLength();

    /**
     * Get the title of the file
     * 
     * @return A Field corresponding to the field
     */
    public native Field getFileTitle();

    /**
     * The file version info shoud be 2.10 or 2.00.
     * 
     * @return A Field corresponding to the field
     */
    public native Field getFileVersion();

    /**
     * Get the file header length
     * 
     * @return A Field corresponding to the field
     */
    public native Field getHeaderLength();

    /**
     * Get this copy number
     * 
     * @return A Field corresponding to the field
     */
    public native Field getMessageCopyNum();

    /**
     * Get number of copies
     * 
     * @return A Field corresponding to the field
     */
    public native Field getMessageNumCopies();

    /**
     * Get the number of data extension segments in the file.
     * 
     * @return A Field corresponding to the field
     */
    public native Field getNumDataExtensions();

    /**
     * Get the number of graphics segments in the file. Only matters in a 2.1
     * file
     * 
     * @return A Field corresponding to the field
     */
    public native Field getNumGraphics();

    /**
     * Get the number of image segments in the file.
     * 
     * @return A Field corresponding to the field
     */
    public native Field getNumImages();

    /**
     * Get the number of label segments in the file. Only matters in a 2.0 file
     * 
     * @return A Field corresponding to the field
     */
    public native Field getNumLabels();

    /**
     * Get the number of reserved extension segments in the file.
     * 
     * @return A Field corresponding to the field
     */
    public native Field getNumReservedExtensions();

    /**
     * Get the number of text segments in the file.
     * 
     * @return A Field corresponding to the field
     */
    public native Field getNumTexts();

    /**
     * Get the originator's name
     * 
     * @return A Field corresponding to the field
     */
    public native Field getOriginatorName();

    /**
     * Get the originator's phone number
     * 
     * @return A Field corresponding to the field
     */
    public native Field getOriginatorPhone();

    /**
     * Get the origin station ID.
     * 
     * @return A Field corresponding to the field
     */
    public native Field getOriginStationID();

    /**
     * Get back a representation of the file security block of the header
     * 
     * @return A file security object representation
     */
    public native FileSecurity getSecurityGroup();

    /**
     * Get the system type
     * 
     * @return A Field corresponding to the field
     */
    public native Field getSystemType();

    /**
     * Get the user defined header length
     * 
     * @return A Field corresponding to the field
     */
    public native Field getUserDefinedHeaderLength();

    /**
     * Get user defined overflow
     * 
     * @return A Field corresponding to the field
     */
    public native Field getUserDefinedOverflow();

    /**
     * Info related to the image info (length, etc).
     * 
     * @return The corresponding info object array (1 for each)
     */
    public native ComponentInfo[] getImageInfo() throws NITFException;

    /**
     * Info related to the graphic info (length, etc). This only matters for
     * NITF 2.1
     * 
     * @return The corresponding info object array (1 for each)
     */
    public native ComponentInfo[] getGraphicInfo() throws NITFException;

    /**
     * Info related to the label info (length, etc). This only matters for NITF
     * 2.0
     * 
     * @return The corresponding info object array (1 for each)
     */
    public native ComponentInfo[] getLabelInfo() throws NITFException;

    /**
     * Info related to the text info (length, etc).
     * 
     * @return The corresponding info object array (1 for each)
     */
    public native ComponentInfo[] getTextInfo() throws NITFException;

    /**
     * Info related to the data extensions (length, etc).
     * 
     * @return The corresponding info object array (1 for each)
     */
    public native ComponentInfo[] getDataExtensionInfo() throws NITFException;

    /**
     * Info related to the reserved extensions (length, etc).
     * 
     * @return The corresponding info object array (1 for each)
     */
    public native ComponentInfo[] getReservedExtensionInfo()
            throws NITFException;

    /**
     * Get back the user defined section representation
     * 
     * @return The Extensions object representing this extensions
     */
    public native Extensions getUserDefinedSection();

    /**
     * Get back the extended section representation
     * 
     * @return The Extensions object representing this extensions
     */
    public native Extensions getExtendedSection();

    /**
     * Prints the data contained in this FileHeader to a PrintStream
     * 
     * @param out
     */
    public void print(PrintStream out) throws NITFException
    {
        out.println("FileHeader = [" + getFileHeader() + "]");
        out.println("FileVersion = [" + getFileVersion() + "]");
        out.println("ComplianceLevel = [" + getComplianceLevel() + "]");
        out.println("SystemType = [" + getSystemType() + "]");
        out.println("OriginStationID = [" + getOriginStationID() + "]");
        out.println("FileDateTime = [" + getFileDateTime() + "]");
        out.println("FileTitle = [" + getFileTitle() + "]");
        out.println("Classification = [" + getClassification() + "]");

        getSecurityGroup().print(out);

        out.println("MessageCopyNum = [" + getMessageCopyNum() + "]");
        out.println("MessageNumCopies = [" + getMessageNumCopies() + "]");
        out.println("Encrypted = [" + getEncrypted() + "]");
        out.println("BackgroundColor = [" + getBackgroundColor() + "]");
        out.println("OriginatorName = [" + getOriginatorName() + "]");
        out.println("OriginatorPhone = [" + getOriginatorPhone() + "]");

        out.println("FileLength = [" + getFileLength() + "]");
        out.println("HeaderLength = [" + getHeaderLength() + "]");

        out.println("NumImages = [" + getNumImages() + "]");
        ComponentInfo[] info = new nitf.ComponentInfo[0];
        try
        {
            info = getImageInfo();
            for (int i = 0; info != null && i < info.length; ++i)
            {
                out.println("    LengthSubheader[" + i + "] = ["
                        + info[i].getLengthSubheader() + "]");
                out.println("    LengthData[" + i + "] = ["
                        + info[i].getLengthData() + "]");
            }
        }
        catch (NITFException e)
        {
            e.printStackTrace(out);
        }

        out.println("NumGraphics = [" + getNumGraphics() + "]");
        try
        {
            info = getGraphicInfo();
            for (int i = 0; info != null && i < info.length; ++i)
            {
                out.println("    LengthSubheader[" + i + "] = ["
                        + info[i].getLengthSubheader() + "]");
                out.println("    LengthData[" + i + "] = ["
                        + info[i].getLengthData() + "]");
            }
        }
        catch (NITFException e)
        {
            e.printStackTrace(out);
        }
        out.println("NumLabels = [" + getNumLabels() + "]");
        try
        {
            info = getLabelInfo();
            for (int i = 0; info != null && i < info.length; ++i)
            {
                out.println("    LengthSubheader[" + i + "] = ["
                        + info[i].getLengthSubheader() + "]");
                out.println("    LengthData[" + i + "] = ["
                        + info[i].getLengthData() + "]");
            }
        }
        catch (NITFException e)
        {
            e.printStackTrace(out);
        }
        out.println("NumTexts = [" + getNumTexts() + "]");
        try
        {
            info = getTextInfo();
            for (int i = 0; info != null && i < info.length; ++i)
            {
                out.println("    LengthSubheader[" + i + "] = ["
                        + info[i].getLengthSubheader() + "]");
                out.println("    LengthData[" + i + "] = ["
                        + info[i].getLengthData() + "]");
            }
        }
        catch (NITFException e)
        {
            e.printStackTrace(out);
        }
        out.println("NumDataExtensions = [" + getNumDataExtensions() + "]");
        try
        {
            info = getDataExtensionInfo();
            for (int i = 0; info != null && i < info.length; ++i)
            {
                out.println("    LengthSubheader[" + i + "] = ["
                        + info[i].getLengthSubheader() + "]");
                out.println("    LengthData[" + i + "] = ["
                        + info[i].getLengthData() + "]");
            }
        }
        catch (NITFException e)
        {
            e.printStackTrace(out);
        }
        out.println("NumReservedExtensions = [" + getNumReservedExtensions()
                + "]");
        try
        {
            info = getReservedExtensionInfo();
            for (int i = 0; info != null && i < info.length; ++i)
            {
                out.println("    LengthSubheader[" + i + "] = ["
                        + info[i].getLengthSubheader() + "]");
                out.println("    LengthData[" + i + "] = ["
                        + info[i].getLengthData() + "]");
            }
        }
        catch (NITFException e)
        {
            e.printStackTrace(out);
        }
        out.println("UserDefinedHeaderLength = ["
                + getUserDefinedHeaderLength() + "]");
        out.println("UserDefinedOverflow = [" + getUserDefinedOverflow() + "]");
        out.println("ExtendedHeaderLength = [" + getExtendedHeaderLength()
                + "]");
        out.println("ExtendedHeaderOverflow = [" + getExtendedHeaderOverflow()
                + "]");

        // print the TREs, if any
        final Extensions extendedSection = getExtendedSection();
        if (extendedSection != null)
        {
            extendedSection.print(out);
        }
    }
}
