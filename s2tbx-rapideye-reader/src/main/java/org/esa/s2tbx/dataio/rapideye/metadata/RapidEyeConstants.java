/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.rapideye.metadata;

import org.esa.snap.core.datamodel.ProductData;

import java.io.File;

/**
 * Utility class for holding various string constants for Rapid Eye reader.
 *
 * @author Cosmin Cara
 */
public class RapidEyeConstants {
    // constants for plugins
    public static final Class[] READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String L1_DESCRIPTION = "RapidEye L1 Data Products";
    public static final String L3_DESCRIPTION = "RapidEye L3 Data Products";
    public static final String[] DEFAULT_EXTENSIONS = new String[]{".xml", ".zip"};
    public static final String[] L1_FORMAT_NAMES = new String[]{"RapidEyeNITF"};
    public static final String[] L3_FORMAT_NAMES = new String[]{"RapidEyeGeoTIFF"};

    public static final String METADATA_FILE_SUFFIX = "metadata.xml";
    public static final String BROWSE_FILE_SUFFIX = "browse.tif";
    public static final String PRODUCT_GENERIC_NAME = "RapidEye Product";
    public static final String NTF_EXTENSION = ".ntf";
    //public static final String TIF_EXTENSION = ".tif";
    public static final String METADATA_EXTENSION = ".xml";
    public static final String UDM_FILE_SUFFIX = "udm.tif";
    public static final String PROFILE_L1 = "L1";
    public static final String PROFILE_L3 = "L3";
    public static final String[] BAND_NAMES = new String[]{"blue", "green", "red", "red_edge", "near_infrared"};
    public static final float[] WAVELENGTHS = new float[]{440, 520, 630, 690, 760};
    public static final float[] BANDWIDTHS = new float[]{70, 70, 55, 40, 90};
    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
    public static final String NOT_AVAILABLE = "N/A";

    public static final String[] L1_MINIMAL_PRODUCT_PATTERNS = new String[]{
            ".*_band[1-6]\\.ntf",
            ".*_metadata\\.xml"};
    public static final String[] L3_MINIMAL_PRODUCT_PATTERNS = new String[]{
            ".*_re[1-5]_3[a-b].*\\.tif",
            ".*_re[1-5]_3[a-b].*_metadata\\.xml"};
    public static final String[] NOT_L3_FILENAME_PATTERNS = {".*\\.ntf"};

    //public static final double RADIOMETRIC_SCALE_FACTOR = 0.01;
    public static final String TAG_EARTH_OBSERVATION_META_DATA = "EarthObservationMetaData";
    public static final String TAG_IDENTIFIER = "identifier";
    public static final String TAG_RESULT_OF = "resultOf";
    public static final String TAG_PRODUCT = "product";
    public static final String TAG_PRODUCT_INFORMATION = "ProductInformation";
    public static final String TAG_PRODUCT_FORMAT = "productFormat";
    public static final String TAG_NUM_ROWS = "numRows";
    public static final String TAG_NUM_COLUMNS = "numColumns";
    public static final String TAG_FILE_NAME = "fileName";
    public static final String TOKEN_BAND_N = "bandN";
    public static final String TOKEN_BAND_X = "band%d";
    public static final String TAG_SPATIAL_REFERENCE_SYSTEM = "spatialReferenceSystem";
    public static final String TAG_EPSG_CODE = "epsgCode";
    public static final String TAG_GEODETIC_DATUM = "geodeticDatum";
    public static final String TAG_PROJECTION = "projection";
    public static final String TAG_PROJECTION_ZONE = "projectionZone";
    public static final String TAG_START_DATE_TIME = "startDateTime";
    public static final String TAG_END_DATE_TIME = "endDateTime";
    public static final String TAG_BAND_SPECIFIC_METADATA = "bandSpecificMetadata";
    public static final String TAG_RADIOMETRIC_SCALE_FACTOR = "radiometricScaleFactor";
    public static final String TAG_PIXEL_FORMAT = "pixelFormat";
    public static final String TAG_EARTH_OBSERVATION_RESULT = "EarthObservationResult";
    public static final String TAG_BAND_NUMBER = "bandNumber";
    public static final String TAG_NUM_BANDS = "numBands";
    public static final String TAG_TOP_LEFT = "topLeft";
    public static final String TAG_TOP_RIGHT = "topRight";
    public static final String TAG_BOTTOM_LEFT = "bottomLeft";
    public static final String TAG_BOTTOM_RIGHT = "bottomRight";
    public static final String TAG_TARGET = "target";
    public static final String TAG_FOOTPRINT = "Footprint";
    public static final String TAG_GEOGRAPHIC_LOCATION = "geographicLocation";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_META_DATA_PROPERTY = "metaDataProperty";
    public static final String TAG_BROWSE = "browse";
    public static final String TAG_BROWSE_INFORMATION = "BrowseInformation";
    public static final String TAG_PRODUCT_TYPE = "productType";
    public static final String TAG_RPC = "Rational Polynomial Coefficients";
    public static final String TAG_LINEOFF = "lineOff";
    public static final String TAG_SAMPLEOFF = "sampleOff";
    public static final String TAG_LATOFF = "latOff";
    public static final String TAG_LONGOFF = "longOff";
    public static final String TAG_HEIGHTOFF = "heightOff";
    public static final String TAG_LINESCALE = "lineScale";
    public static final String TAG_SAMPLESCALE = "sampleScale";
    public static final String TAG_LATSCALE = "latScale";
    public static final String TAG_LONGSCALE = "longScale";
    public static final String TAG_HEIGHTSCALE = "heightScale";
    public static final String TAG_LINENUMCOEFF = "lineNumCoeff";
    public static final String TAG_LINEDENCOEFF = "lineDenCoeff";
    public static final String TAG_SAMPLENUMCOEFF = "sampleNumCoeff";
    public static final String TAG_SAMPLEDENCOEFF = "sampleDenCoeff";
    public static final String TAG_MOSAIC_DECOMPOSITION = "mosaicDecomposition";
    public static final String TAG_MOSAIC_TILE = "mosaicTile";
    public static final String TAG_USING = "using";
    public static final String TAG_EARTH_OBSERVATION_EQUIPMENT = "EarthObservationEquipment";
    public static final String TAG_PLATFORM_OUTER = "platform";
    public static final String TAG_PLATFORM_INNER = "Platform";
    public static final String TAG_SERIAL_IDENTIFIER = "serialIdentifier";
    public static final String TAG_ORBIT_TYPE = "orbitType";
    public static final String TAG_INSTRUMENT_OUTER = "instrument";
    public static final String TAG_INSTRUMENT_INNER = "Instrument";
    public static final String TAG_SHORT_NAME = "shortName";
    public static final String TAG_SENSOR_OUTER = "sensor";
    public static final String TAG_SENSOR_INNER = "Sensor";
    public static final String TAG_SENSOR_TYPE = "sensorType";
    public static final String TAG_VALID_TIME = "validTime";
    public static final String TAG_BEGIN_POSITION = "beginPosition";
    public static final String TAG_END_POSITION = "endPosition";
    public static final String TAG_TIME_PERIOD = "TimePeriod";
    public static final String FLAG_BLACK_FILL = "black_fill";
    public static final String FLAG_CLOUDS = "clouds";
    public static final String FLAG_MISSING_BLUE_DATA = "missing_blue_data";
    public static final String FLAG_MISSING_GREEN_DATA = "missing_green_data";
    public static final String FLAG_MISSING_RED_DATA = "missing_red_data";
    public static final String FLAG_MISSING_RED_EDGE_DATA = "missing_red_edge_data";
    public static final String FLAG_MISSING_NIR_DATA = "missing_nir_data";

    public enum PixelFormat {

        UNSIGNED_INTEGER("16U", ProductData.TYPE_UINT16),
        SIGNED_INTEGER("SI", ProductData.TYPE_INT16);

        private final String value;
        private final int dataType;

        private PixelFormat(String value, int dataType) {
            this.value = value;
            this.dataType = dataType;
        }

        @Override
        public String toString() {
            return value;
        }

        public int getDataType() {
            return dataType;
        }
    }
}
