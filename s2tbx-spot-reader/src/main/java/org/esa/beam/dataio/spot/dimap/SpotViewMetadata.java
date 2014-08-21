package org.esa.beam.dataio.spot.dimap;

import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;

import java.nio.ByteOrder;

/**
 * This class holds parsed metadata from XML file (which is not DIMAP).
 * It exposes convenience methods for fetching various useful metadata values.
 *
 * @author  Cosmin Cara
 */
public class SpotViewMetadata extends XmlMetadata {

    public SpotViewMetadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return SpotConstants.SPOTVIEW_METADATA_FILE;
    }

    @Override
    public String getProductName() {
        if (rootElement == null) {
            return null;
        }
        String name = null;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_PRODUCTION)) != null) {
            name = currentElement.getAttributeString(SpotConstants.TAG_DATASET_NAME);
            rootElement.setDescription(name);
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_DATASET_NAME));
        }
        return name;
    }

    @Override
    public int getNumBands() {
        int numBands = 0;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE)) != null) {
            try {
                numBands = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_CHANNELS));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_CHANNELS));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_CHANNELS));
        }
        return numBands;
    }

    @Override
    public String getFormatName() {
        String format = "NOT DIMAP";
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_METADATA_ID)) != null) {
            format = currentElement.getAttributeString(SpotConstants.TAG_METADATA_FORMAT);
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_METADATA_FORMAT));
        }
        return format;
    }

    @Override
    public String getMetadataProfile() {
        String profile = "SPOTScene";
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_METADATA_ID)) != null) {
            profile = currentElement.getAttributeString(SpotConstants.TAG_METADATA_PROFILE);
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_METADATA_PROFILE));
        }
        return profile;
    }

    @Override
    public int getRasterWidth() {
        int width = 0;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE)) != null) {
            try {
                width = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_COLUMNS));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_COLUMNS));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_COLUMNS));
        }
        return width;
    }

    @Override
    public int getRasterHeight() {
        int height = 0;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE)) != null) {
            try {
                height = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_ROWS));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_ROWS));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_ROWS));
        }
        return height;
    }

    @Override
    public String[] getRasterFileNames() {
        return new String[] { SpotConstants.SPOTVIEW_RASTER_FILENAME };
    }

    /**
     * Returns the names of the bands found in the metadata file.
     * If the expected metadata nodes are not present, then the default band names
     * are returned (i.e. band_n).
     * @return  an array of band names
     */
    public String[] getBandNames() {
        if (rootElement == null) {
            return null;
        }
        String[] names = new String[getNumBands()];
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE_INTERPRETATION)) != null) {
            MetadataElement[] bandInfo = currentElement.getElements();
            for (int i = 0; i < bandInfo.length; i++) {
                names[i] = bandInfo[i].getAttributeString(SpotConstants.TAG_BAND_DESCRIPTION, SpotConstants.DEFAULT_BAND_NAME_PREFIX + i);
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                names[i] = SpotConstants.DEFAULT_BAND_NAME_PREFIX + i;
            }
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_BAND_DESCRIPTION));
        }
        return names;
    }

    public ByteOrder getRasterJavaByteOrder() {
        int value = 0;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE)) != null) {
            try {
                value = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_BYTEORDER));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_BYTEORDER));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_BYTEORDER));
        }
        return value == 1 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    public int getRasterDataType() {
        int value = 0, retVal;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE)) != null) {
            try {
                value = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_BITS_PER_PIXEL));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_BITS_PER_PIXEL));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_BITS_PER_PIXEL));
        }
        switch (value) {
            case 1:
                retVal = ProductData.TYPE_UINT8;
                break;
            case 2:
                retVal = ProductData.TYPE_INT16;
                break;
            case 3:
                retVal = ProductData.TYPE_INT32;
                break;
            case 4:
                retVal = ProductData.TYPE_FLOAT32;
                break;
            default:
                retVal = ProductData.TYPE_UINT8;
                break;

        }
        return retVal;
    }

    public int getRasterPixelSize() {
        int value = 8;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE)) != null) {
            try {
                value = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_BITS_PER_PIXEL));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_BITS_PER_PIXEL));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_BITS_PER_PIXEL));
        }
        return value / 8;
    }

    public float getRasterGeoRefX() {
        float value = 0.0f;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEO_INFORMATION)) != null) {
            try {
                value = Float.parseFloat(currentElement.getAttributeString(SpotConstants.TAG_XGEOREF));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_XGEOREF));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_XGEOREF));
        }
        return value;
    }

    public float getRasterGeoRefY() {
        float value = 0.0f;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEO_INFORMATION)) != null) {
            try {
                value = Float.parseFloat(currentElement.getAttributeString(SpotConstants.TAG_YGEOREF));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_YGEOREF));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_YGEOREF));
        }
        return value;
    }

    public float getRasterGeoRefSizeX() {
        float value = 0.0f;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEO_INFORMATION)) != null) {
            try {
                value = Float.parseFloat(currentElement.getAttributeString(SpotConstants.TAG_XCELLRES));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_XCELLRES));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_XCELLRES));
        }
        return value;
    }

    public float getRasterGeoRefSizeY() {
        float value = 0.0f;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEO_INFORMATION)) != null) {
            try {
                value = Float.parseFloat(currentElement.getAttributeString(SpotConstants.TAG_YCELLRES));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_YCELLRES));
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_YCELLRES));
        }
        return value;
    }

    @SuppressWarnings("SameReturnValue")
    public String getGeolayerFileName() {
        return SpotConstants.SPOTVIEW_GEOLAYER_FILENAME;
    }

    public int getGeolayerNumBands() {
        int numBands = 0;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEOLAYER)) != null) {
            numBands = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_CHANNELS));
        }
        return numBands;
    }

    public int getGeolayerWidth() {
        int width = 0;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEOLAYER)) != null) {
            width = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_COLUMNS));
        }
        return width;
    }

    public int getGeolayerHeight() {
        int height = 0;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEOLAYER)) != null) {
            height = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_ROWS));
        }
        return height;
    }

    public ByteOrder getGeolayerJavaByteOrder() {
        int value = 0;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEOLAYER)) != null) {
            value = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_BYTEORDER));
        }
        return value == 1 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    public int getGeolayerPixelSize() {
        int value = 8;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEOLAYER)) != null) {
            value = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_BITS_PER_PIXEL));
        }
        return value / 8;
    }

    public int getGeolayerDataType() {
        int value = 0, retVal;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEOLAYER)) != null) {
            value = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_BITS_PER_PIXEL));
        }
        switch (value) {
            case 1:
                retVal = ProductData.TYPE_UINT8;
                break;
            case 2:
                retVal = ProductData.TYPE_INT16;
                break;
            case 3:
                retVal = ProductData.TYPE_INT32;
                break;
            case 4:
                retVal = ProductData.TYPE_FLOAT32;
                break;
            default:
                retVal = ProductData.TYPE_UINT8;
                break;

        }
        return retVal;
    }

    /**
     * Returns the code of the used projection, if any.
     * Please note that, for SPOTView products, a code of the projection is not present in the file.
     * For European projection, we assume the code "epsg:3035".
     * @return  the code of the projection used, or <code>null</code> if no projection
     */
    public String getProjectionCode() {
        if (rootElement == null) {
            return null;
        }
        String name = null;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_GEO_INFORMATION)) != null) {
            name = currentElement.getAttributeString(SpotConstants.TAG_PROJECTION);
            if (name != null && name.equalsIgnoreCase("european")) {
                name = SpotConstants.EPSG_3035;
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_PROJECTION));
        }
        return name;
    }
}
