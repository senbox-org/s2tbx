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
 * A representation of the NITF Image subheader <p/> The ImageSubheader
 * component represents the NITF file image subheader from the NITF file. This
 * part of the record has information that directly is associated with an image
 * segment.
 */

public final class ImageSubheader extends NITFObject
{

    /**
     * @see NITFObject#NITFObject(long)
     */
    ImageSubheader(long address)
    {
        super(address);
    }

    /**
     * Retrieve the actual number of bits per pixel as represented in the image
     * subheader.
     * 
     * @return A Field associated with the field
     */
    public native Field getActualBitsPerPixel();

    /**
     * Retrieve an array of band info associated with the image.
     * 
     * @return A Field associated with the field
     */

    public native BandInfo[] getBandInfo();

    /**
     * Retrieve the specified compression rate.
     * 
     * @return A Field associated with the field
     */

    public native Field getCompressionRate();

    /**
     * Retrieve the corner coordinates.
     * 
     * @return A Field associated with the field
     */
    public native Field getCornerCoordinates();

    /**
     * Is the image encrypted?
     * 
     * @return A Field associated with the field
     */
    public native Field getEncrypted();

    /**
     * Retrieve the extended header length.
     * 
     * @return A Field associated with the field
     */
    public native Field getExtendedHeaderLength();

    /**
     * Retrieve the extended header overflow Field.
     * 
     * @return A Field associated with the field
     */
    public native Field getExtendedHeaderOverflow();

    /**
     * Get file part type.
     * 
     * @return A Field associated with the field
     */
    public native Field getFilePartType();

    /**
     * Get image attachment level.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageAttachmentLevel();

    /**
     * Get the image category.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageCategory();

    /**
     * Retrieve the image comments.
     * 
     * @return An array of Fields
     */
    public native Field[] getImageComments();

    /**
     * Retrieve the image compression.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageCompression();

    /**
     * Retrieve the image coordinate system.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageCoordinateSystem();

    /**
     * Get image date and time.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageDateAndTime();

    /**
     * Get image date and time.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageDisplayLevel();

    /**
     * Get the associated image id
     * 
     * @return A Field associated with the field
     */
    public native Field getImageId();

    /**
     * Get the image location.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageLocation();

    /**
     * Get the image magnification Field.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageMagnification();

    /**
     * Get the image (blocking) mode
     * 
     * @return A Field associated with the field
     */
    public native Field getImageMode();

    /**
     * Get the image representation
     * 
     * @return A Field associated with the field
     */
    public native Field getImageRepresentation();

    /**
     * Get the security classification.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageSecurityClass();

    /**
     * Get the image source.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageSource();

    /**
     * Get the image sync code.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageSyncCode();

    /**
     * Get the image title.
     * 
     * @return A Field associated with the field
     */
    public native Field getImageTitle();

    /**
     * Get the number of bits per pixel in the image.
     * 
     * @return A Field associated with the field
     */

    public native Field getNumBitsPerPixel();

    /**
     * Get the number of blocks per column in the image.
     * 
     * @return A Field associated with the field
     */
    public native Field getNumBlocksPerCol();

    /**
     * Get the number of blocks per row in the image.
     * 
     * @return A Field associated with the field
     */
    public native Field getNumBlocksPerRow();

    /**
     * Get the number of columns in the image
     * 
     * @return A Field associated with the field
     */
    public native Field getNumCols();

    /**
     * Get the number of image bands (for instance, RGB is typically 3).
     * 
     * @return A Field associated with the field
     */
    public native Field getNumImageBands();

    /**
     * Get the number of image comments.
     * 
     * @return A Field associated with the field
     */
    public native Field getNumImageComments();

    /**
     * Get the number of multispectral image bands. It should always have only
     * one or the other -- if image bands is zero, this should be at least one,
     * and vice versa.
     * 
     * @return A Field associated with the field
     */
    public native Field getNumMultispectralImageBands();

    /**
     * Get the number of pixels per horizontal block.
     * 
     * @return A Field associated with the field
     */
    public native Field getNumPixelsPerHorizBlock();

    /**
     * Get the number of pixels per vertical block.
     * 
     * @return A Field associated with the field
     */
    public native Field getNumPixelsPerVertBlock();

    /**
     * Get the number of rows in the image
     * 
     * @return A Field associated with the field
     */
    public native Field getNumRows();

    /**
     * Find out the pixel justification.
     * 
     * @return A Field associated with the field
     */
    public native Field getPixelJustification();

    /**
     * Pixel Field type could be int, float, etc.
     * 
     * @return A Field associated with the field
     */

    public native Field getPixelValueType();

    /**
     * Retrieve the security info.
     * 
     * @return A Field associated with the field
     */
    public native FileSecurity getSecurityGroup();

    /**
     * Get the target id.
     * 
     * @return A Field associated with the field
     */
    public native Field getTargetId();

    /**
     * Tell us how much user data to expect.
     * 
     * @return A Field associated with the field
     */
    public native Field getUserDefinedImageDataLength();

    /**
     * Tell us how much user defined overflow is there.
     * 
     * @return A Field associated with the field
     */
    public native Field getUserDefinedOverflow();

    /**
     * Retrieve an object representing the user data
     * 
     * @return A Field associated with the field
     */
    public native Extensions getUserDefinedSection();

    /**
     * Retrieve an object representing the extended section
     * 
     * @return A Field associated with the field
     */
    public native Extensions getExtendedSection();

    /**
     * This function adds the given comment string to the list of comments
     * associated with this image subheader. The numImageComments field value
     * gets incremented by 1. If the number of comments is already equal to 9,
     * then false is returned, and the comment is not added. <p/> The function
     * assumes that the numImageComments field value and the actual size of the
     * imageComments buffer are consistent with eachother. <p/> If the position
     * is out of the array bounds, or less than zero, the comment will be
     * appended to the end.
     * 
     * @param comment
     *            the comment to add
     * @param index
     *            the index to add it at
     * @return the index the comment was actually placed at
     * @throws NITFException
     */
    public native int insertImageComment(String comment, int index)
            throws NITFException;

    /**
     * This function removes the comment at the given position from the list of
     * comments associated with this image subheader. The numImageComments field
     * value gets decremented by 1. If the number of comments is already equal
     * to 0, nothing is done. <p/> The function assumes that the
     * numImageComments field value and the actual size of the imageComments
     * buffer are consistent with eachother. <p/> If the position is out of the
     * array bounds, or less than zero, then nothing is done.
     * 
     * @param index
     *            the index of the image comment to remove
     * @return
     * @throws NITFException
     */
    public native boolean removeImageComment(int index) throws NITFException;

    /**
     * getBandCount returns the number of bands in The image associated with the
     * subheader. The band count can come from one of two fields, NBANDS or
     * XBANDS. There can be errors decoding the value fields and inconsistent
     * values in NBANDS, XBANDS and IREP. <p/>
     * 
     * @return number of bands
     */
    public native int getBandCount();

    /**
     * createBands creates the specified number of BandInfo objects, and updates
     * the bandInfo array. This also updates the NBANDS/XBANDS fields in the
     * ImageSubheader. This function assumes that the current size of the
     * bandInfo array is EQUAL to the current number of toatl bands (NBANDS +
     * XBANDS). An error is returned if memory problems occur, or if the total
     * bands will exceed 99999.
     * 
     * @param numBands
     *            number of bands to create
     * @return true if the bands are created, false otherwise
     * @throws NITFException
     */
    public native boolean createBands(int numBands) throws NITFException;

    /**
     * Removes a band from the image. This removes the underlying BandInfo
     * object and decrements the band count.
     * 
     * @param index
     * @throws NITFException
     */
    public native void removeBand(int index) throws NITFException;

    /**
     * Prints the data associated with the ImageSubheader to a PrintStream
     * 
     * @param out
     */
    public void print(PrintStream out) throws NITFException
    {
        out.println("FilePartType = [" + getFilePartType() + "]");
        out.println("ImageId = [" + getImageId() + "]");
        out.println("ImageDateAndTime = [" + getImageDateAndTime() + "]");
        out.println("TargetId = [" + getTargetId() + "]");
        out.println("ImageTitle = [" + getImageTitle() + "]");
        out.println("ImageSecurityClass = [" + getImageSecurityClass() + "]");

        getSecurityGroup().print(out);

        out.println("Encrypted = [" + getEncrypted() + "]");
        out.println("ImageSource = [" + getImageSource() + "]");
        out.println("NumRows = [" + getNumRows() + "]");
        out.println("NumCols = [" + getNumCols() + "]");
        out.println("PixelFieldType = [" + getPixelValueType() + "]");
        out.println("ImageRepresentation = [" + getImageRepresentation() + "]");
        out.println("ImageCategory = [" + getImageCategory() + "]");
        out.println("ActualBitsPerPixel = [" + getActualBitsPerPixel() + "]");
        out.println("PixelJustification = [" + getPixelJustification() + "]");
        out.println("ImageCoordinateSystem = [" + getImageCoordinateSystem()
                + "]");
        out.println("CornerCoordinates = [" + getCornerCoordinates() + "]");
        out.println("NumImageComments = [" + getNumImageComments() + "]");

        final Field[] imageComments = getImageComments();
        for (int i = 0; i < imageComments.length; i++)
        {
            String imageComment = imageComments[i].getStringData();
            out.println("   comment[" + i + "] = [" + imageComment + "]");
        }

        out.println("ImageCompression = [" + getImageCompression() + "]");
        out.println("CompressionRate = [" + getCompressionRate() + "]");
        out.println("NumImageBands = [" + getNumImageBands() + "]");

        BandInfo[] bandInfo = getBandInfo();
        for (int i = 0; i < bandInfo.length; i++)
        {
            BandInfo info = bandInfo[i];

            out.println("   ----- BandInfo [" + i + "] -----");
            out.println("   Representation = [" + info.getRepresentation()
                    + "]");
            out.println("   Subcategory = [" + info.getSubcategory() + "]");
            out.println("   ImageFilterCondition = ["
                    + info.getImageFilterCondition() + "]");
            out.println("   ImageFilterCode = [" + info.getImageFilterCode()
                    + "]");
            out.println("   NumLUTS = [" + info.getNumLUTs() + "]");
        }

        out.println("ImageSyncCode = [" + getImageSyncCode() + "]");
        out.println("ImageMode = [" + getImageMode() + "]");
        out.println("NumBlocksPerRow = [" + getNumBlocksPerRow() + "]");
        out.println("NumBlocksPerCol = [" + getNumBlocksPerCol() + "]");
        out.println("NumPixelsPerHorizBlock = [" + getNumPixelsPerHorizBlock()
                + "]");
        out.println("NumPixelsPerVertBlock = [" + getNumPixelsPerVertBlock()
                + "]");
        out.println("NumBitsPerPixel = [" + getNumBitsPerPixel() + "]");
        out.println("ImageDisplayLevel = [" + getImageDisplayLevel() + "]");
        out.println("ImageAttachmentLevel = [" + getImageAttachmentLevel()
                + "]");
        out.println("ImageLocation = [" + getImageLocation() + "]");
        out.println("ImageMagnification = [" + getImageMagnification() + "]");
        out.println("UserDefinedImageDataLength = ["
                + getUserDefinedImageDataLength() + "]");
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
