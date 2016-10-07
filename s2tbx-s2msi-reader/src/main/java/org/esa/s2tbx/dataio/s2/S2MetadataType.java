/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2;

/**
 * @author opicas-p
 */
public interface S2MetadataType {
    String L1C = "https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1c:https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_tile_metadata:https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_datastrip_metadata:https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap";
    String L1B = "https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1b:https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata:https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_datastrip_metadata:https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap";
    String L2A = "https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_2a_tile_metadata:https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_2a:https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_2a:https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap";
    String L3 = "https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_3_tile_metadata:https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_3:https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_3:https.psd_12_sentinel2_eo_esa_int.dico._12.pdgs.dimap:https.psd_13_sentinel2_eo_esa_int.dico._13.pdgs.dimap";

    String L1C_PRODUCT_SCHEMA_FILE_PATH = "schemas/PSD13/S2_User_Product_Level-1C_Metadata.xsd";
    String L1C_GRANULE_SCHEMA_FILE_PATH = "schemas/L2A_PSD12/S2_PDI_Level-1C_Tile_Metadata.xsd";
    String L1C_DATASTRIP_SCHEMA_FILE_PATH = "schemas/L2A_PSD12/S2_PDI_Level-1C_Datastrip_Metadata.xsd";

    //TODO test
    String L2A_PRODUCT_SCHEMA_FILE_PATH = "schemas/L2A_PSD12/S2_User_Product_Level-2A_Metadata.xsd";
    String L2A_GRANULE_SCHEMA_FILE_PATH = "schemas/L2A_PSD12/S2_PDI_Level-2A_Tile_Metadata.xsd";
    String L2A_DATASTRIP_SCHEMA_FILE_PATH = "schemas/L2A_PSD12/S2_PDI_Level-2A_Datastrip_Metadata.xsd";

    //TODO test
    String L3_PRODUCT_SCHEMA_FILE_PATH = "schemas/L3_PSD131/S2_User_Product_Level-3_Metadata.xsd";
    String L3_GRANULE_SCHEMA_FILE_PATH = "schemas/L3_PSD131/S2_PDI_Level-3_Tile_Metadata.xsd";
    String L3_DATASTRIP_SCHEMA_FILE_PATH = "schemas/L3_PSD131/S2_PDI_Level-3_Datastrip_Metadata.xsd";

    String SCHEMA13_BASE_PATH = "schemas/PSD13/";
    String SCHEMA12_BASE_PATH = "schemas/L2A_PSD12/";
}
