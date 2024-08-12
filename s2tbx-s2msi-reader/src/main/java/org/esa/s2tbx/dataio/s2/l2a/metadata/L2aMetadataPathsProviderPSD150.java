package org.esa.s2tbx.dataio.s2.l2a.metadata;


import org.esa.s2tbx.dataio.s2.l2a.L2aPSD150Constants;

/**
 * Created by diana on 26/07/2024
 */
public class L2aMetadataPathsProviderPSD150 implements IL2aMetadataPathsProvider{

    public L2aMetadataPathsProviderPSD150() {
    }
    @Override
    public String[] getProductSchemaLocations() {
        return L2aPSD150Constants.getProductSchemaLocations();
    }

    @Override
    public String[] getGranuleSchemaLocations() {
        return L2aPSD150Constants.getGranuleSchemaLocations();
    }

    @Override
    public String[] getDatastripSchemaLocations() {
        return L2aPSD150Constants.getDatastripSchemaLocations();
    }

    @Override
    public String getProductSchemaBasePath() {
        return L2aPSD150Constants.getProductSchemaBasePath();
    }

    @Override
    public String getDatastripSchemaBasePath() {
        return L2aPSD150Constants.getDatastripSchemaBasePath();
    }

    @Override
    public String getGranuleSchemaBasePath() {
        return L2aPSD150Constants.getGranuleSchemaBasePath();
    }

    @Override
    public String getPATH_PRODUCT_METADATA_DATATAKE() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_DATATAKE;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_SPACECRAFT() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_SPACECRAFT;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_DATATAKE_TYPE() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_DATATAKE_TYPE;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_SENSING_START() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_SENSING_START;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_SENSING_ORBIT_NUMBER() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_SENSING_ORBIT_NUMBER;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_ORBIT_DIRECTION() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_ORBIT_DIRECTION;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_PRODUCT_START_TIME() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_PRODUCT_START_TIME;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_PRODUCT_STOP_TIME() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_PRODUCT_STOP_TIME;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_PRODUCT_URI() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_PRODUCT_URI;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_PROCESSING_LEVEL() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_PROCESSING_LEVEL;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_PRODUCT_TYPE() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_PRODUCT_TYPE;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_PROCESSING_BASELINE() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_PROCESSING_BASELINE;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_GENERATION_TIME() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_GENERATION_TIME;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_PREVIEW_IMAGE_URL() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_PREVIEW_IMAGE_URL;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_PREVIEW_GEO_INFO() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_PREVIEW_GEO_INFO;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_METADATA_LEVEL() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_METADATA_LEVEL;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_GRANULE_LIST() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_GRANULE_LIST;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_GRANULE_LIST_ALT() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_GRANULE_LIST_ALT;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_DATASTRIP_LIST() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_DATASTRIP_LIST_ALT() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST_ALT;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_IMAGE_ID() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_IMAGE_ID;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_L1C_TOA_QUANTIFICATION_VALUE() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_L1C_TOA_QUANTIFICATION_VALUE;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_L2A_BOA_QUANTIFICATION_VALUE() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_L2A_BOA_QUANTIFICATION_VALUE;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_L2A_AOT_QUANTIFICATION_VALUE() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_L2A_AOT_QUANTIFICATION_VALUE;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_L2A_WVP_QUANTIFICATION_VALUE() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_L2A_WVP_QUANTIFICATION_VALUE;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_BOA_ADD_OFFSET_VALUES_LIST() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_BOA_ADD_OFFSET_VALUES_LIST;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_BAND_LIST() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_BAND_LIST;
    }

    @Override
    public String getPATH_PRODUCT_METADATA_PRODUCT_FORMAT() {
        return L2aPSD150Constants.PATH_PRODUCT_METADATA_PRODUCT_FORMAT;
    }

    @Override
    public String getPATH_GRANULE_METADATA_TILE_ID() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_TILE_ID;
    }

    @Override
    public String getPATH_GRANULE_METADATA_HORIZONTAL_CS_NAME() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_NAME;
    }

    @Override
    public String getPATH_GRANULE_METADATA_HORIZONTAL_CS_CODE() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_CODE;
    }

    @Override
    public String getPATH_GRANULE_METADATA_SIZE_RESOLUTION() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_SIZE_RESOLUTION;
    }

    @Override
    public String getPATH_GRANULE_METADATA_SIZE_NROWS() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_SIZE_NROWS;
    }

    @Override
    public String getPATH_GRANULE_METADATA_SIZE_NCOLS() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_SIZE_NCOLS;
    }

    @Override
    public String getPATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION;
    }

    @Override
    public String getPATH_GRANULE_METADATA_GEOPOSITION_ULX() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULX;
    }

    @Override
    public String getPATH_GRANULE_METADATA_GEOPOSITION_ULY() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULY;
    }

    @Override
    public String getPATH_GRANULE_METADATA_GEOPOSITION_XDIM() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_GEOPOSITION_XDIM;
    }

    @Override
    public String getPATH_GRANULE_METADATA_GEOPOSITION_YDIM() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_GEOPOSITION_YDIM;
    }

    @Override
    public String getPATH_GRANULE_METADATA_ANGLE_RESOLUTION() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_ANGLE_RESOLUTION;
    }

    @Override
    public String getPATH_GRANULE_METADATA_SUN_ZENITH_ANGLES() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_SUN_ZENITH_ANGLES;
    }

    @Override
    public String getPATH_GRANULE_METADATA_SUN_AZIMUTH_ANGLES() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_SUN_AZIMUTH_ANGLES;
    }

    @Override
    public String getPATH_GRANULE_METADATA_VIEWING_ZENITH_ANGLES() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_VIEWING_ZENITH_ANGLES;
    }

    @Override
    public String getPATH_GRANULE_METADATA_VIEWING_AZIMUTH_ANGLES() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_VIEWING_AZIMUTH_ANGLES;
    }

    @Override
    public String getPATH_GRANULE_METADATA_VIEWING_BAND_ID() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_VIEWING_BAND_ID;
    }

    @Override
    public String getPATH_GRANULE_METADATA_MASK_FILENAME() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_MASK_FILENAME;
    }

    @Override
    public String getPATH_GRANULE_METADATA_MASK_TYPE() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_MASK_TYPE;
    }

    @Override
    public String getPATH_GRANULE_METADATA_MASK_BAND() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_MASK_BAND;
    }

    @Override
    public String getPATH_GRANULE_METADATA_PVI_FILENAME() {
        return L2aPSD150Constants.PATH_GRANULE_METADATA_PVI_FILENAME;
    }

}

