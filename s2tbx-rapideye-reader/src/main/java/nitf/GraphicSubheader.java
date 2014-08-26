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
 * A representation of the NITF Graphic (Symobl) subheader
 */
public final class GraphicSubheader extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    GraphicSubheader(long address)
    {
        super(address);
    }

    /**
     * Returns the File Part Type (SY) Field
     * 
     * @return the File Part Type (SY) Field
     */
    public native Field getFilePartType();

    /**
     * Returns the Graphic Identifer (SID) Field
     * 
     * @return the Graphic Identifer (SID) Field
     */
    public native Field getGraphicID();

    /**
     * Returns the Graphic Name (SNAME) Field
     * 
     * @return the Graphic Name (SNAME) Field
     */
    public native Field getName();

    /**
     * Returns the Graphic Security Classification (SSCLAS) Field
     * 
     * @return the Graphic Security Classification (SSCLAS) Field
     */
    public native Field getSecurityClass();

    /**
     * Returns the FileSecurity associated with this Graphic Header
     * 
     * @return the FileSecurity associated with this Graphic Header
     */
    public native FileSecurity getSecurityGroup();

    /**
     * Returns the Encryption (ENCRYP) Field
     * 
     * @return the Encryption (ENCRYP) Field
     */
    public native Field getEncrypted();

    /**
     * Returns the Graphic Type (SFMT) Field
     * 
     * @return the Graphic Type (SFMT) Field
     */
    public native Field getStype();

    /**
     * Returns the Reserved for future use (SSTRUCT) Field
     * 
     * @return the Reserved for future use (SSTRUCT) Field
     */
    public native Field getRes1();

    /**
     * Returns the Graphic Display Level (SDLVL) Field
     * 
     * @return the Graphic Display Level (SDLVL) Field
     */
    public native Field getDisplayLevel();

    /**
     * Returns the Graphic Attachment Level (SALVL) Field
     * 
     * @return the Graphic Attachment Level (SALVL) Field
     */
    public native Field getAttachmentLevel();

    /**
     * Returns the Graphic Location - (SLOC) Field
     * 
     * @return the Graphic Location - (SLOC) Field
     */
    public native Field getLocation();

    /**
     * Returns the First Graphic Bound Location - (SBND1) Field
     * 
     * @return the First Graphic Bound Location - (SBND1) Field
     */
    public native Field getBound1Loc();

    /**
     * Returns the Graphic Color (SCOLOR) Field
     * 
     * @return the Graphic Color (SCOLOR) Field
     */
    public native Field getColor();

    /**
     * Returns the Second Graphic Bound Location - (SBND2) Field
     * 
     * @return the Second Graphic Bound Location - (SBND2) Field
     */
    public native Field getBound2Loc();

    /**
     * Returns the Reserved For Future Use (SRES2) Field
     * 
     * @return the Reserved For Future Use (SRES2) Field
     */
    public native Field getRes2();

    /**
     * Returns the Graphic Extended Subheader Data Length (SXSHDL) Field
     * 
     * @return the Graphic Extended Subheader Data Length (SXSHDL) Field
     */
    public native Field getExtendedHeaderLength();

    /**
     * Returns the Graphic Extended Subheader Overflow (SXSOFL) Field
     * 
     * @return the Graphic Extended Subheader Overflow (SXSOFL) Field
     */
    public native Field getExtendedHeaderOverflow();

    /**
     * Returns the Graphic Extended Subheader Data Extensions segment
     * 
     * @return the Graphic Extended Subheader Data Extensions segment
     */
    public native Extensions getExtendedSection();

    /**
     * Prints the data associated with the GraphicSubheader to a PrintStream
     * 
     * @param out
     */
    public void print(PrintStream out)
    {
        out.println("FilePartType = [" + getFilePartType() + "]");
        out.println("GraphicID = [" + getGraphicID() + "]");
        out.println("GraphicName = [" + getName() + "]");
        out.println("GraphicSecurityClass = [" + getSecurityClass() + "]");

        getSecurityGroup().print(out);

        out.println("Encryption = [" + getEncrypted() + "]");
        out.println("GraphicType = [" + getStype() + "]");
        out.println("Reserved1 = [" + getRes1() + "]");
        out.println("DisplayLevel = [" + getDisplayLevel() + "]");
        out.println("AttachmentLevel = [" + getAttachmentLevel() + "]");
        out.println("GraphicLocation = [" + getLocation() + "]");
        out.println("BoundLocation1 = [" + getBound1Loc() + "]");
        out.println("Color = [" + getColor() + "]");
        out.println("BoundLocation2 = [" + getBound2Loc() + "]");
        out.println("Reserved2 = [" + getRes2() + "]");
        out.println("Reserved2 = [" + getExtendedHeaderLength() + "]");
    }
}
