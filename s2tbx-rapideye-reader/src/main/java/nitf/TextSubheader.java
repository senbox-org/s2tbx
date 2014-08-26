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
 * A representation of the NITF Text subheader
 */
public class TextSubheader extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    TextSubheader(long address)
    {
        super(address);
    }

    /**
     * Returns the File Part Type (TE) Field
     * 
     * @return
     */
    public native Field getFilePartType();

    /**
     * Returns the Text Identifier (TEXTID) Field
     * 
     * @return
     */
    public native Field getTextID();

    /**
     * Returns the Text Attachment Level (TXTALVL) Field
     * 
     * @return
     */
    public native Field getAttachmentLevel();

    /**
     * Returns the Text Date and Time (TXTDT) Field
     * 
     * @return
     */
    public native Field getDateTime();

    /**
     * Returns the Text Title (TXTITL) Field
     * 
     * @return
     */
    public native Field getTitle();

    /**
     * Returns the Text Security Classification (TSCLAS) Field
     * 
     * @return
     */
    public native Field getSecurityClass();

    /**
     * Returnst the FileSecurity info for this Text Header
     * 
     * @return
     */
    public native FileSecurity getSecurityGroup();

    /**
     * Returns the Encryption (ENCRYP) Field
     * 
     * @return
     */
    public native Field getEncrypted();

    /**
     * Returns the Text Format (TXTFMT) Field
     * 
     * @return
     */
    public native Field getFormat();

    /**
     * Returns the Text Extended Subheader Data Length (TXSHDL) Field
     * 
     * @return
     */
    public native Field getExtendedHeaderLength();

    /**
     * Returns the Text Extended Subheader Overflow (TXSOFL) Field
     * 
     * @return
     */
    public native Field getExtendedHeaderOverflow();

    /**
     * Returns the Text Extended Subheader Data Extensions segment
     * 
     * @return
     */
    public native Extensions getExtendedSection();

    /**
     * Prints the data associated with the TextSubheader to a PrintStream
     * 
     * @param out
     */
    public void print(PrintStream out)
    {
        out.println("FilePartType = [" + getFilePartType() + "]");
        out.println("TextID = [" + getTextID() + "]");
        out.println("AttachmentLevel = [" + getAttachmentLevel() + "]");
        out.println("DateTime = [" + getDateTime() + "]");
        out.println("Title = [" + getTitle() + "]");
        out.println("SecurityClass = [" + getSecurityClass() + "]");

        getSecurityGroup().print(out);

        out.println("Encryption = [" + getEncrypted() + "]");
        out.println("Format = [" + getFormat() + "]");
        out.println("ExtendedHeaderLength = [" + getExtendedHeaderLength()
                + "]");
    }

}
