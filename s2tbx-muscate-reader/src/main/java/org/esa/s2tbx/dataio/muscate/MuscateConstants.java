package org.esa.s2tbx.dataio.muscate;

import java.awt.*;
import java.io.File;

/**
 * Created by obarrile on 26/01/2017.
 */
public class MuscateConstants {

    public static final String COMPLET = "COMPLET";
    public static final String HYBRID = "HYBRID";
    public static final String DISTRIBUTED = "DISTRIBUTED";
    public static final String METADATA_MUSCATE = "METADATA_MUSCATE";
    public static final String METADATA_EXTENSION = ".xml";
    public static final String MUSCATE = "MUSCATE";
    public static final String NODATA_VALUE = "nodata";
    public static final String NODATA_WATER_VAPOR = "water_vapor_content_nodata";
    public static final String NODATA_AEROSOL = "aerosol_optical_thickness_nodata";
    public static final String VALUE_NOT_AVAILABLE = "N/A";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String VALUE_NOT_DATE = "9999-12-31'T'23:59:59.999'Z'";
    public static final String STRING_ZERO = "0";

    /*
    * Metadata XPath-equivalent navigation paths
    */
    public static final String PATH_SOURCE_ID = "/Muscate_Metadata_Document/Product_Characteristics/PRODUCT_ID";
    public static final String PATH_SOURCE_DESCRIPTION = "/Muscate_Metadata_Document/Dataset_Identification/DESCRIPTION";
    public static final String PATH_METADATA_FORMAT = "/Muscate_Metadata_Document/Metadata_Identification/METADATA_PROFILE";
    public static final String PATH_METADATA_PROFILE = "/Muscate_Metadata_Document/Metadata_Identification/METADATA_PROFILE";
    public static final String PATH_ACQUISITION_DATE = "/Muscate_Metadata_Document/Product_Characteristics/ACQUISITION_DATE";
    public static final String PATH_DESCRIPTION = "/Muscate_Metadata_Document/Dataset_Identification/DESCRIPTION";
    public static final String PATH_PRODUCT_VERSION = "/Muscate_Metadata_Document/Product_Characteristics/PRODUCT_VERSION";
    public static final String PATH_NROWS = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Group_Geopositioning_List/Group_Geopositioning/NROWS";
    public static final String PATH_NCOLS = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Group_Geopositioning_List/Group_Geopositioning/NCOLS";
    public static final String PATH_IMAGE_FILE_LIST = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/Image_List/Image/Image_File_List";
    public static final String PATH_GEOPOSITIONING_ID = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Group_Geopositioning_List/Group_Geopositioning/group_id";
    public static final String PATH_GEOPOSITIONING_ULX = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Group_Geopositioning_List/Group_Geopositioning/ULX";
    public static final String PATH_GEOPOSITIONING_ULY = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Group_Geopositioning_List/Group_Geopositioning/ULY";
    public static final String PATH_GEOPOSITIONING_XDIM = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Group_Geopositioning_List/Group_Geopositioning/XDIM";
    public static final String PATH_GEOPOSITIONING_YDIM = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Group_Geopositioning_List/Group_Geopositioning/YDIM";
    public static final String PATH_GEOPOSITIONING_NROWS = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Group_Geopositioning_List/Group_Geopositioning/NROWS";
    public static final String PATH_GEOPOSITIONING_NCOLS = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Group_Geopositioning_List/Group_Geopositioning/NCOLS";
    public static final String PATH_CS_CODE = "/Muscate_Metadata_Document/Geoposition_Informations/Coordinate_Reference_System/Horizontal_Coordinate_System/HORIZONTAL_CS_CODE";
    public static final String PATH_GLOBAL_GEOPOSITIONING_POINT_NAME = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Global_Geopositioning/Point/name";
    public static final String PATH_GLOBAL_GEOPOSITIONING_POINT_X = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Global_Geopositioning/Point/X";
    public static final String PATH_GLOBAL_GEOPOSITIONING_POINT_Y = "/Muscate_Metadata_Document/Geoposition_Informations/Geopositioning/Global_Geopositioning/Point/Y";

    public static final String PATH_SPECTRAL_BAND_INFORMATION_BAND = "/Muscate_Metadata_Document/Radiometric_Informations/Spectral_Band_Informations_List/Spectral_Band_Informations/band_id";
    public static final String PATH_SPECTRAL_BAND_INFORMATION_IRRADIANCE = "/Muscate_Metadata_Document/Radiometric_Informations/Spectral_Band_Informations_List/Spectral_Band_Informations/SOLAR_IRRADIANCE";
    public static final String PATH_SPECTRAL_BAND_INFORMATION_CENTRAL_WAVELENGTH = "/Muscate_Metadata_Document/Radiometric_Informations/Spectral_Band_Informations_List/Spectral_Band_Informations/Wavelength/CENTRAL";


    public static final String PATH_QUICKLOOK = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/QUICKLOOK";
    public static final String PATH_IMAGE_NATURE = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/Image_List/Image/Image_Properties/NATURE";
    public static final String PATH_IMAGE_FORMAT = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/Image_List/Image/Image_Properties/FORMAT";
    public static final String PATH_IMAGE_ENCODING = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/Image_List/Image/Image_Properties/ENCODING";
    public static final String PATH_IMAGE_ENDIANNESS = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/Image_List/Image/Image_Properties/ENDIANNESS";
    public static final String PATH_IMAGE_COMPRESSION = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/Image_List/Image/Image_Properties/COMPRESSION";
    public static final String PATH_IMAGE_FILE_GROUP_ID = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/Image_List/Image/Image_File_List/IMAGE_FILE/group_id";
    public static final String PATH_IMAGE_FILE_BAND_ID = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/Image_List/Image/Image_File_List/IMAGE_FILE/band_id";
    public static final String PATH_IMAGE_FILE = "/Muscate_Metadata_Document/Product_Organisation/Muscate_Product/Image_List/Image/Image_File_List/IMAGE_FILE";

    //no data values
    public static final String PATH_VALUES_NODATA = "/Muscate_Metadata_Document/Radiometric_Informations/Special_Values_List";
    public static final String PATH_VALUES_NODATA_NAME = "/Muscate_Metadata_Document/Radiometric_Informations/Special_Values_List/SPECIAL_VALUE/name";
    public static final String NODATA_NAME_REFLECTANCE = "nodata";
    public static final String DEFAULT_REFLECTANCE_NODATA = "-10000.0";
    public static final String NODATA_NAME_AOT = "aerosol_optical_thickness_nodata";
    public static final String DEFAULT_AOT_NODATA = "0.0";
    public static final String NODATA_NAME_WVC = "water_vapor_content_nodata";
    public static final String DEFAULT_WVC_NODATA = "0.0";

    //quantification values
    public static final String PATH_REFLECTANCE_QUANTIFICATION = "/Muscate_Metadata_Document/Radiometric_Informations/REFLECTANCE_QUANTIFICATION_VALUE";
    public static final String DEFAULT_REFLECTANCE_QUANTIFICATION = "10000.0";
    public static final String PATH_WVC_QUANTIFICATION = "/Muscate_Metadata_Document/Radiometric_Informations/WATER_VAPOR_CONTENT_QUANTIFICATION_VALUE";
    public static final String DEFAULT_WVC_QUANTIFICATION = "20.0";
    public static final String PATH_AOT_QUANTIFICATION = "/Muscate_Metadata_Document/Radiometric_Informations/AEROSOL_OPTICAL_THICKNESS_QUANTIFICATION_VALUE";
    public static final String DEFAULT_AOT_QUANTIFICATION = "200.0";

    // constants for plugins
    public static final Class[] MUSCATE_READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String MUSCATE_DESCRIPTION = "MUSCATE Data Products";
    public static final String[] MUSCATE_DEFAULT_EXTENSIONS = new String[]{".xml", ".zip", ".tar"};
    public static final String[] MUSCATE_FORMAT_NAMES = new String[]{"MUSCATE"};
    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[] {
            ".*[/\\\\]?[A-Z|0-9|-]+_[0-9]{8}-[0-9]{6}-[0-9]{3}_[A-Z|0-9|-]+_[A-Z|0-9|-]*_[C|H|D]_V[0-9|-]*[A-Z|_]*\\.(xml|XML|zip|ZIP)"
    };

    public static final String XML_PATTERN = ".*[/\\\\]?[A-Z|0-9|-]+_[0-9]{8}-[0-9]{6}-[0-9]{3}_[A-Z|0-9|-]+_[A-Z|0-9|-]*_[C|H|D].*MTD_ALL\\.xml";
    public static final String QUICKLOOK_PATTERN = ".*[/\\\\]?[A-Z|0-9|-]+_[0-9]{8}-[0-9]{6}-[0-9]{3}_[A-Z|0-9|-]+_[A-Z|0-9|-]*_[C|H|D].*\\.jpg";
    public static final String REFLECTANCE_PATTERN = "([A-Z|0-9|-]+)_([0-9]{8}-[0-9]{6}-[0-9]{3})_([A-Z|0-9|-]+)_([A-Z|0-9|-]+)_([C|H|D])_([A-Z|0-9|-]+)_([A-Z|0-9|-]+)_(B[A|0-9]+)\\.tif";

    public enum GEOPHYSICAL_BIT {
        Water(0, "MG2_Water_Mask_", "Water mask", Color.red),
        Cloud(1,"MG2_Cloud_Mask_All_Cloud_","Result of a 'logical OR' for all the cloud masks",Color.red.darker()),
        Snow(2,"MG2_Snow_Mask_","Snow mask",Color.blue),
        Cloud_Shadow(3,"MG2_Shadow_Mask_Of_Cloud_","Shadow masks of cloud",Color.blue.darker()),
        Topography_Shadow(4,"MG2_Topographical_Shadows_Mask_","Topographical shadows mask",Color.green),
        Hidden_Surface(5,"MG2_Hidden_Areas_Mask_","Hidden areas mask",Color.green.darker()),
        Sun_Too_Low(6,"MG2_Sun_Too_Low_Mask_","Sun too low mask",Color.yellow),
        Tangent_Sun(7,"MG2_Tangent_Sun_Mask_","Tangent sun mask",Color.yellow.darker());

        private int bit;
        private String prefixName = null;
        private String description = null;
        private Color color = null;

        GEOPHYSICAL_BIT(int bit, String prefix, String description, Color color) {
            this.bit = bit;
            prefixName = new String(prefix);
            this.description = new String(description);
            this.color = color;
        }

        public int getBit() {
            return bit;
        }

        public String getPrefixName() {
            return prefixName;
        }

        public String getDescription() {
            return description;
        }

        public Color getColor() {
            return color;
        }
    }

}
