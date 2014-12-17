package org.esa.beam.dataio.s2;

/**
 * Created by opicas-p on 18/11/2014.
 */
public interface MetadataType {
    public String L1C = "https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1c:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_tile_metadata:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_datastrip_metadata:https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap";
    public String L1B = "https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1b:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_datastrip_metadata:https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap";
    public String L1A = "https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1a:https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1a_datastrip_metadata:https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap";
    public String SEPARATOR = ":";
}
