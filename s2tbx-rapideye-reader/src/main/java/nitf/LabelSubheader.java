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
 * A representation of the NITF Label subheader
 */
public final class LabelSubheader extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    LabelSubheader(long address)
    {
        super(address);
    }

    /**
     * Return the File Part Type (LA) Field
     * 
     * @return
     */
    public native Field getFilePartType();

    /**
     * Return the Label ID (LID) Field
     * 
     * @return
     */
    public native Field getLabelID();

    /**
     * Return the Label Security Classification (LSCLAS) Field
     * 
     * @return
     */
    public native Field getSecurityClass();

    /**
     * Return the FileSecurity info for this Label Header
     * 
     * @return
     */
    public native FileSecurity getSecurityGroup();

    /**
     * Return the Encryption (ENCRYP) Field
     * 
     * @return
     */
    public native Field getEncrypted();

    /**
     * Return the Label Font Style (LFS) Field
     * 
     * @return
     */
    public native Field getFontStyle();

    /**
     * Return the Label Cell Width (LCW) Field
     * 
     * @return
     */
    public native Field getCellWidth();

    /**
     * Return the Label Cell Height (LCH) Field
     * 
     * @return
     */
    public native Field getCellHeight();

    /**
     * Return the Display Level (LDLVL) Field
     * 
     * @return
     */
    public native Field getDisplayLevel();

    /**
     * Return the Attachment Level (LALVL) Field
     * 
     * @return
     */
    public native Field getAttachmentLevel();

    /**
     * Return the Label Location - Row (LLOC) Field
     * 
     * @return
     */
    public native Field getLocationRow();

    /**
     * Return the Label Location - Column (LLOC) Field
     * 
     * @return
     */
    public native Field getLocationColumn();

    /**
     * Return the Label Text Color (LTC) Field
     * 
     * @return
     */
    public native Field getTextColor();

    /**
     * Return the Label Background Bolor (LBC) Field
     * 
     * @return
     */
    public native Field getBackgroundColor();

    /**
     * Return the Extended Subheader Data Length (LXSHDL) Field
     * 
     * @return
     */
    public native Field getExtendedHeaderLength();

    /**
     * Return the Extended Subheader Overflow (LXSOFL) Field
     * 
     * @return
     */
    public native Field getExtendedHeaderOverflow();

    /**
     * Return the Extended Subheader Data extensions section
     * 
     * @return
     */
    public native Extensions getExtendedSection();

    /**
     * Prints the data associated with the LabelSubheader to a PrintStream
     * 
     * @param out
     */
    public void print(PrintStream out)
    {
        // TODO
    }
}
