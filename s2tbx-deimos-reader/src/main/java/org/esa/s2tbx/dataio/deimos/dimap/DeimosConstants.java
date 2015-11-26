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

package org.esa.s2tbx.dataio.deimos.dimap;

import java.io.File;

/**
 * Holder class for string constants.
 *
 * @author Cosmin Cara
 */
public class DeimosConstants {
    public static final String METADATA_EXTENSION = ".dim";
    public static final String DIMAP = "DIMAP";
    public static final String DEIMOS = "DEIMOS";
    public static final String NODATA_VALUE = "nodata";
    public static final String SATURATED_VALUE = "SATURATED";
    public static final int UNIT_MULTIPLIER = 1000000000;
    public static final String[] DEFAULT_BAND_NAMES = { "NIR", "Red", "Green" };
    public static final String DEFAULT_UNIT = "W/(m^2*sr*µm)";
    public static final String VALUE_NOT_AVAILABLE = "N/A";
    public static final String STRING_ZERO = "0";
    public static final String PROCESSING_1R = "1R";
    public static final String PROCESSING_1T = "1T";
    public static final String PROCESSING_2T = "ORTHORECTIFIED";
    public static final String DEIMOS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final double MAX_LEVEL = 255.0;
    public static final String LATITUDE_BAND_NAME = "latitude";
    public static final String LONGITUDE_BAND_NAME = "longitude";

    /*
     * Metadata XPath-equivalent navigation paths
     */
    public static final String PATH_SOURCE_ID = "/Dimap_Document/Dataset_Sources/Source_Information/SOURCE_ID";
    public static final String PATH_SOURCE_DESCRIPTION = "/dimap_document/dataset_sources/source_information/source_description";
    public static final String PATH_METADATA_FORMAT = "/dimap_document/metadata_id/metadata_format";
    public static final String PATH_METADATA_PROFILE = "/dimap_document/metadata_id/metadata_profile";
    public static final String PATH_NCOLS = "/dimap_document/raster_dimensions/ncols";
    public static final String PATH_NROWS = "/dimap_document/raster_dimensions/nrows";
    public static final String PATH_DATA_FILE_PATH = "/dimap_document/data_access/data_file/data_file_path/href";
    public static final String PATH_BAND_DESCRIPTION = "/dimap_document/image_interpretation/spectral_band_info/band_description";
    public static final String PATH_SPECIAL_VALUE_TEXT = "/dimap_document/image_display/special_value/special_value_text";
    public static final String PATH_SPECIAL_VALUE_INDEX = "/dimap_document/image_display/special_value/special_value_index";
    public static final String PATH_SCENE_CENTER_DATE = "/dimap_document/dataset_sources/source_information/scene_source/imaging_date";
    public static final String PATH_SCENE_CENTER_TIME = "/dimap_document/dataset_sources/source_information/scene_source/imaging_time";
    public static final String PATH_NBANDS = "/dimap_document/raster_dimensions/nbands";
    public static final String PATH_SPECIAL_VALUE_COLOR_RED_LEVEL = "/dimap_document/image_display/special_value/special_value_color/red_level";
    public static final String PATH_SPECIAL_VALUE_COLOR_GREEN_LEVEL = "/dimap_document/image_display/special_value/special_value_color/green_level";
    public static final String PATH_SPECIAL_VALUE_COLOR_BLUE_LEVEL = "/dimap_document/image_display/special_value/special_value_color/blue_level";
    public static final String PATH_PHYSICAL_BIAS = "/dimap_document/image_interpretation/spectral_band_info/physical_bias";
    public static final String PATH_PHYSICAL_GAIN = "/dimap_document/image_interpretation/spectral_band_info/physical_gain";
    public static final String PATH_NBITS = "/dimap_document/raster_encoding/nbits";
    public static final String PATH_PHYSICAL_UNIT = "/dimap_document/image_interpretation/spectral_band_info/physical_unit";
    public static final String PATH_ULXMAP = "/dimap_document/geoposition/geoposition_insert/ulxmap";
    public static final String PATH_ULYMAP = "/dimap_document/geoposition/geoposition_insert/ulymap";
    public static final String PATH_XDIM = "/dimap_document/geoposition/geoposition_insert/xdim";
    public static final String PATH_YDIM = "/dimap_document/geoposition/geoposition_insert/ydim";
    public static final String PATH_GEOPOSITION_INSERT = "/dimap_document/geoposition/geoposition_insert";
    public static final String PATH_SPECTRAL_BAND_INFO = "/dimap_document/image_interpretation/spectral_band_info";
    public static final String PATH_GEOMETRIC_PROCESSING = "/dimap_document/data_processing/geometric_processing";
    public static final String PATH_TIE_POINT_DATA_X = "/dimap_document/geoposition/geoposition_points/tie_point/tie_point_data_x";
    public static final String PATH_TIE_POINT_DATA_Y = "/dimap_document/geoposition/geoposition_points/tie_point/tie_point_data_y";
    public static final String PATH_TIE_POINT_CRS_X = "/dimap_document/geoposition/geoposition_points/tie_point/tie_point_crs_x";
    public static final String PATH_TIE_POINT_CRS_Y = "/dimap_document/geoposition/geoposition_points/tie_point/tie_point_crs_y";

    // constants for plugins
    public static final Class[] DIMAP_READER_INPUT_TYPES = new Class[]{String.class, File.class};
    public static final String DIMAP_DESCRIPTION = "DEIMOS Data Products";
    public static final String[] DIMAP_DEFAULT_EXTENSIONS = new String[]{".dim", ".zip", ".tar"};
    public static final String[] DIMAP_FORMAT_NAMES = new String[]{"DEIMOSDimap"};
    public static final String[] MINIMAL_PRODUCT_PATTERNS = new String[] {
            "((.*[/\\\\]?de[0-1][0-9]_sl[1-9]_\\d{1,3}[pst]_([0-1][rgt]|2[rt])_\\d{8}t\\d{6}_\\d{8}t\\d{6}_([a-z]+)_\\d+_[0-9a-f]+(\\w+))|(.*[/\\\\]?(\\w+)_l1[rt]))\\.dim",
            "((.*[/\\\\]?de[0-1][0-9]_sl[1-9]_\\d{1,3}[pst]_([0-1][rgt]|2[rt])_\\d{8}t\\d{6}_\\d{8}t\\d{6}_([a-z]+)_\\d+_[0-9a-f]+(\\w+))|(.*[/\\\\]?(\\w+)_l1[rt]))\\.tif"
            };
}
