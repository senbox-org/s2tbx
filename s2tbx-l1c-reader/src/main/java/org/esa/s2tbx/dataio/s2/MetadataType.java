/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2;

/**
 * Created by opicas-p on 18/11/2014.
 */
public interface MetadataType {
    public String L1C = "https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1c:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_tile_metadata:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_datastrip_metadata:https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap";
    public String L1B = "https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1b:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_datastrip_metadata:https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap";
    public String L1A = "https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1a:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1a_datastrip_metadata:https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap";
    public String SEPARATOR = ":";
}
