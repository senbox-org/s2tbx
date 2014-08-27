package org.esa.beam.dataio.rapideye.nitf;

import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;

import java.text.ParseException;

/**
 * Contains parsed metadata from a NITF file.
 *
 * @author  Cosmin Cara
 */
public class NITFMetadata {

    final int FIRST_IMAGE = 0;
    MetadataElement root;

    NITFMetadata() {
    }

    public MetadataElement getMetadataRoot() { return root; }

    void setRootElement(MetadataElement newRoot) { root = newRoot; }

    public ProductData.UTC getFileDate() {
        ProductData.UTC fileDate = null;
        MetadataElement currentElement = root.getElement(NITFFields.TAG_FILE_HEADER);
        if (currentElement != null) {
            try {
                fileDate = ProductData.UTC.parse(currentElement.getAttributeString(NITFFields.FDT, ""), "ddHHmmss'Z'MMMyy");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return fileDate;
    }

    public String getFileTitle() {
        String ret = "";
        MetadataElement currentElement = root.getElement(NITFFields.TAG_FILE_HEADER);
        if (currentElement != null) {
            ret = currentElement.getAttributeString(NITFFields.FTITLE, "");
        }
        return ret;
    }

    public boolean isEncrypted() {
        boolean ret = false;
        MetadataElement currentElement = root.getElement(NITFFields.TAG_FILE_HEADER);
        if (currentElement != null) {
            int val = Integer.parseInt(currentElement.getAttributeString(NITFFields.ENCRYP, "0"));
            ret = (val == 1);
        }
        return ret;
    }

    /**
     * Returns the number of images contained in the NITF file.
     * @return  the number of images, or 0 if something goes wrong in reading the value.
     */
    public int getNumImages() {
        int ret = 0;
        MetadataElement currentElement = root.getElement(NITFFields.TAG_FILE_HEADER);
        if (currentElement != null) {
            ret = Integer.parseInt(currentElement.getAttributeString(NITFFields.NUMI, "0"));
        }
        return ret;
    }

    /**
     * Returns the number of bands of the first image contained in the NITF file.
     * This is a comodity method for files that contain only one image.
     * @return  the number of bands, or 0 if the read failed.
     */
    public int getNumBands() {
        return getNumBands(FIRST_IMAGE);
    }

    /**
     * Returns the number of bands of the imageIndex-th image contained in the NITF file.
     * If the imageIndex is not in the range 0..number of images - 1, an exception is thrown.
     * @param imageIndex    The number (0-based) of the image
     * @return  the number of bands, or 0 if the read failed
     */
    public int getNumBands(int imageIndex) {
        int ret = 0;
        if (imageIndex < 0)
            throw new IllegalArgumentException("Invalid image index");
        MetadataElement currentElement = root.getElement(NITFFields.TAG_IMAGE_SUBHEADERS);
        if (currentElement != null) {
            MetadataElement[] imageSubheaders = currentElement.getElements();
            if (imageIndex >= imageSubheaders.length)
                throw new IllegalArgumentException("Invalid image index");
            ret = Integer.parseInt(imageSubheaders[imageIndex].getAttributeString(NITFFields.NBANDS, "0"));
        }
        return ret;
    }

    /**
     * Returns the width, in pixels, of the first image.
     * @return  the number of pixels, or 0 if read fails.
     */
    public int getWidth() {
        return getWidth(FIRST_IMAGE);
    }

    /**
     * Returns the width, in pixels, of the imageIndex-th image.
     * @param imageIndex    the image index (0-based)
     * @return  the number of pixels, or 0 if read fails
     */
    public int getWidth(int imageIndex) {
        int ret = 0;
        if (imageIndex < 0)
            throw new IllegalArgumentException("Invalid image index");
        MetadataElement currentElement = root.getElement(NITFFields.TAG_IMAGE_SUBHEADERS);
        if (currentElement != null) {
            MetadataElement[] imageSubheaders = currentElement.getElements();
            if (imageIndex >= imageSubheaders.length)
                throw new IllegalArgumentException("Invalid image index");
            ret = Integer.parseInt(imageSubheaders[imageIndex].getAttributeString(NITFFields.NCOLS, "0"));
        }
        return ret;
    }

    /**
     * Returns the height, in pixels, of the first image.
     * @return  the number of pixels, or 0 if read fails.
     */
    public int getHeight() {
        return getHeight(FIRST_IMAGE);
    }

    /**
     * Returns the height, in pixels, of the imageIndex-th image.
     * @param imageIndex    The image index (0-based)
     * @return  the number of pixels, or 0 if read fails.
     */
    public int getHeight(int imageIndex) {
        int ret = 0;
        if (imageIndex < 0)
            throw new IllegalArgumentException("Invalid image index");
        MetadataElement currentElement = root.getElement(NITFFields.TAG_IMAGE_SUBHEADERS);
        if (currentElement != null) {
            MetadataElement[] imageSubheaders = currentElement.getElements();
            if (imageIndex >= imageSubheaders.length)
                throw new IllegalArgumentException("Invalid image index");
            ret = Integer.parseInt(imageSubheaders[imageIndex].getAttributeString(NITFFields.NROWS, "0"));
        }
        return ret;
    }

    /**
     * Returns the data type value of a pixel of the first image.
     * @see org.esa.beam.framework.datamodel.ProductData
     * @return  One of the ProductData.TYPE_* values.
     */
    public int getDataType() {
        return getDataType(FIRST_IMAGE);
    }

    /**
     * Returns the data type value of a pixel of the imageIndex-th image.
     * @param imageIndex    the image index (0-based)
     * @return  the data type value, or <code>ProductData.TYPE_UNDEFINED</code> if read fails
     */
    public int getDataType(int imageIndex) {
        int ret = ProductData.TYPE_UNDEFINED;
        if (imageIndex < 0)
            throw new IllegalArgumentException("Invalid image index");
        MetadataElement currentElement = root.getElement(NITFFields.TAG_IMAGE_SUBHEADERS);
        if (currentElement != null) {
            MetadataElement[] imageSubheaders = currentElement.getElements();
            if (imageIndex >= imageSubheaders.length)
                throw new IllegalArgumentException("Invalid image index");
            String valString = imageSubheaders[imageIndex].getAttributeString(NITFFields.PVTYPE);
            int abpp = Integer.parseInt(imageSubheaders[imageIndex].getAttributeString(NITFFields.ABPP));
            int nbpp = Integer.parseInt(imageSubheaders[imageIndex].getAttributeString(NITFFields.NBPP));
            if (abpp <= nbpp) {
                switch (nbpp) {
                    case 1:
                        if ("B".equals(valString)) {
                            ret = ProductData.TYPE_UNDEFINED; // support for bit type ??
                        }
                        break;
                    case 8:
                        if ("INT".equals(valString)) {
                            ret = ProductData.TYPE_UINT8;
                        } else if ("SI".equals(valString)) {
                            ret = ProductData.TYPE_INT8;
                        }
                        break;
                    case 12:
                        if ("INT".equals(valString)) {
                            ret = ProductData.TYPE_UINT16;
                        } else if ("SI".equals(valString)) {
                            ret = ProductData.TYPE_INT16;
                        }
                        break;
                    case 16:
                        if ("INT".equals(valString)) {
                            ret = ProductData.TYPE_UINT16;
                        } else if ("SI".equals(valString)) {
                            ret = ProductData.TYPE_INT16;
                        }
                        break;
                    case 32:
                        if ("INT".equals(valString)) {
                            ret = ProductData.TYPE_UINT32;
                        } else if ("SI".equals(valString)) {
                            ret = ProductData.TYPE_INT32;
                        } else if ("R".equals(valString)) {
                            ret = ProductData.TYPE_FLOAT32;
                        }
                        break;
                    case 64:
                        if ("INT".equals(valString)) {
                            ret = ProductData.TYPE_UNDEFINED;   // support for 64-bit integers ??
                        } else if ("SI".equals(valString)) {
                            ret = ProductData.TYPE_UNDEFINED;
                        } else if ("R".equals(valString)) {
                            ret = ProductData.TYPE_FLOAT64;
                        }
                        break;
                    default:
                        ret = ProductData.TYPE_UNDEFINED;
                        break;
                }
            }
        }
        return ret;
    }

    public String getUnit() {
        return getUnit(FIRST_IMAGE);
    }

    public String getUnit(int imageIndex) {
        String ret = null;
        if (imageIndex < 0)
            throw new IllegalArgumentException("Invalid image index");
        MetadataElement currentElement = root.getElement(NITFFields.TAG_IMAGE_SUBHEADERS);
        if (currentElement != null) {
            currentElement = currentElement.getElement(NITFFields.TAG_IMAGE_SUBHEADER + imageIndex);
            if (currentElement != null) {
                String value = currentElement.getAttributeString(NITFFields.ICAT, null);
                if (value != null) {
                    if ("MS".equals(value) || "HS".equals(value) || "IR".equals(value)) {
                        ret = "nm";
                    }
                }
            }
        }
        return ret;
    }

    public float getWavelength() {
        return getWavelength(FIRST_IMAGE);
    }

    public float getWavelength(int imageIndex) {
        float ret = -1f;
        if (imageIndex < 0)
            throw new IllegalArgumentException("Invalid image index");
        MetadataElement currentElement = root.getElement(NITFFields.TAG_IMAGE_SUBHEADERS);
        if (((currentElement = currentElement.getElement("Bands")) != null) &&
                ((currentElement = currentElement.getElement("BAND" + (imageIndex + 1))) != null)){
            String value = currentElement.getAttributeString(NITFFields.ISUBCAT, null);
            String unit = getUnit(imageIndex);
            if (value != null && unit != null) {
                ret = Float.parseFloat(value);
            }
        }
        return ret;
    }


}
