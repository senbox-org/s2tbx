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
 * A representation of the NITF security information. This information differs
 * between the NITF specification versions, but this class represents the
 * information as a whole. Each component that has security information makes
 * reference to a <code>FileSecurity</code> object.
 */
public final class FileSecurity extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    FileSecurity(long address)
    {
        super(address);
    }

    /**
     * Returns the classification authority Field, or null if one doesn't exist
     * 
     * @return the classification authority Field, or null if one doesn't exist
     */
    public native Field getClassificationAuthority();

    /**
     * Returns the classification authority type Field, or null if one doesn't
     * exist
     * 
     * @return the classification authority type Field, or null if one doesn't
     *         exist
     */
    public native Field getClassificationAuthorityType();

    /**
     * Returns the classification reason Field, or null if one doesn't exist
     * 
     * @return the classification reason Field, or null if one doesn't exist
     */
    public native Field getClassificationReason();

    /**
     * Returns the classification system Field, or null if one doesn't exist
     * 
     * @return the classification system Field, or null if one doesn't exist
     */
    public native Field getClassificationSystem();

    /**
     * Returns the classification text Field, or null if one doesn't exist
     * 
     * @return the classification text Field, or null if one doesn't exist
     */
    public native Field getClassificationText();

    /**
     * Returns the codewords Field, or null if one doesn't exist
     * 
     * @return the codewords Field, or null if one doesn't exist
     */
    public native Field getCodewords();

    /**
     * Returns the control and handling Field, or null if one doesn't exist
     * 
     * @return the control and handling Field, or null if one doesn't exist
     */
    public native Field getControlAndHandling();

    /**
     * Returns the declassification date Field, or null if one doesn't exist
     * 
     * @return the declassification date Field, or null if one doesn't exist
     */
    public native Field getDeclassificationDate();

    /**
     * Returns the declassification exemption Field, or null if one doesn't
     * exist
     * 
     * @return the declassification exemption Field, or null if one doesn't
     *         exist
     */
    public native Field getDeclassificationExemption();

    /**
     * Returns the declassification type Field, or null if one doesn't exist
     * 
     * @return the declassification type Field, or null if one doesn't exist
     */
    public native Field getDeclassificationType();

    /**
     * Returns the declassification downgrade Field, or null if one doesn't
     * exist
     * 
     * @return the declassification downgrade Field, or null if one doesn't
     *         exist
     */
    public native Field getDowngrade();

    /**
     * Returns the downgrade date/time Field, or null if one doesn't exist
     * 
     * @return the downgrade date/time Field, or null if one doesn't exist
     */
    public native Field getDowngradeDateTime();

    /**
     * Returns the releasing instructions Field, or null if one doesn't exist
     * 
     * @return the releasing instructions Field, or null if one doesn't exist
     */
    public native Field getReleasingInstructions();

    /**
     * Returns the security control number Field, or null if one doesn't exist
     * 
     * @return the security control number Field, or null if one doesn't exist
     */
    public native Field getSecurityControlNumber();

    /**
     * Returns the security source date Field, or null if one doesn't exist
     * 
     * @return the security source date Field, or null if one doesn't exist
     */
    public native Field getSecuritySourceDate();

    /**
     * Resize the file security for the given version. Warning: Only use this if
     * you know what you are doing. This will modify the underlying FileSecurity
     * fields.
     * 
     * @param version
     */
    public native void resizeForVersion(Version version) throws NITFException;

    /**
     * Prints the contents of the FileSecurity info
     * 
     * @param out
     *            the stream to write to
     */
    public void print(PrintStream out)
    {
        out.println("ClassificationAuthority = ["
                + getClassificationAuthority() + "]");
        out.println("ClassificationAuthorityType = ["
                + getClassificationAuthorityType() + "]");
        out.println("ClassificationReason = [" + getClassificationReason()
                + "]");
        out.println("ClassificationSystem = [" + getClassificationSystem()
                + "]");
        out.println("ClassificationText = [" + getClassificationText() + "]");
        out.println("Codewords = [" + getCodewords() + "]");
        out.println("ControlAndHandling = [" + getControlAndHandling() + "]");
        out.println("DeclassificationDate = [" + getDeclassificationDate()
                + "]");
        out.println("DeclassificationExemption = ["
                + getDeclassificationExemption() + "]");
        out.println("DeclassificationType = [" + getDeclassificationType()
                + "]");
        out.println("Downgrade = [" + getDowngrade() + "]");
        out.println("DowngradeDateTime = [" + getDowngradeDateTime() + "]");
        out.println("ReleasingInstructions = [" + getReleasingInstructions()
                + "]");
        out.println("SecurityControlNumber = [" + getSecurityControlNumber()
                + "]");
        out.println("SecuritySourceDate = [" + getSecuritySourceDate() + "]");
    }
}
