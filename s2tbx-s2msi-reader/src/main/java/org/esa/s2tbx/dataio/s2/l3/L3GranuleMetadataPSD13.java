package org.esa.s2tbx.dataio.s2.l3;

import com.bc.ceres.core.Assert;
import https.psd_12_sentinel2_eo_esa_int.dico._12.pdgs.dimap.A_L3_PIXEL_LEVEL_QI;
import org.esa.s2tbx.dataio.metadata.GenericXmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2MetadataType;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.core.datamodel.MetadataElement;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by obarrile on 07/10/2016.
 */
public class L3GranuleMetadataPSD13 extends GenericXmlMetadata implements IL3GranuleMetadata  {

    private static class L3GranuleMetadataPSD13Parser extends XmlMetadataParser<L3GranuleMetadataPSD13> {

        public L3GranuleMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            String[] locations = {S2MetadataType.L3_GRANULE_SCHEMA_FILE_PATH};
            setSchemaLocations(locations);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L3GranuleMetadataPSD13 create(Path path) throws IOException {
        Assert.notNull(path);
        L3GranuleMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (Files.exists(path)) {
                stream = Files.newInputStream(path, StandardOpenOption.READ);
                //noinspection unchecked
                L3GranuleMetadataPSD13Parser parser = new L3GranuleMetadataPSD13Parser(L3GranuleMetadataPSD13.class);
                result = parser.parse(stream);
                result.updateName();
            }
        } catch (Exception e) {
            //Logger.getLogger(GenericXmlMetadata.class.getName()).severe(e.getMessage());
            //TODO
        } finally {
            if (stream != null) try {
                stream.close();
            } catch (IOException e) {
                // swallowed exception
            }
        }
        return result;
    }
    public L3GranuleMetadataPSD13(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }

    @Override
    public S2Metadata.ProductCharacteristics getTileProductOrganization(S2SpatialResolution resolution) {

        S2Metadata.ProductCharacteristics characteristics = new S2Metadata.ProductCharacteristics();
        characteristics.setSpacecraft("Sentinel-2");
        characteristics.setProcessingLevel("Level-3");

        double boaQuantification = 1000; //Default value
        characteristics.setQuantificationValue(boaQuantification);

        return characteristics;
    }

    @Override
    public Map<S2SpatialResolution, S2Metadata.TileGeometry> getTileGeometries() {
        Map<S2SpatialResolution, S2Metadata.TileGeometry> resolutions = new HashMap<>();
        for (String res : getAttributeValues(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION)) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(Integer.parseInt(res));
            S2Metadata.TileGeometry tgeox = new S2Metadata.TileGeometry();

            tgeox.setUpperLeftX(Double.parseDouble(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                            L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULX, "0")));
            tgeox.setUpperLeftY(Double.parseDouble(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                            L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULY, "0")));
            tgeox.setxDim(Double.parseDouble(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                      L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_XDIM, "0")));
            tgeox.setyDim(Double.parseDouble(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                      L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_YDIM, "0")));
            tgeox.setNumCols(Integer.parseInt(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_SIZE_RESOLUTION, res,
                                                                       L3PSD13Constants.PATH_GRANULE_METADATA_SIZE_NCOLS, "0")));
            tgeox.setNumRows(Integer.parseInt(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_SIZE_RESOLUTION, res,
                                                                       L3PSD13Constants.PATH_GRANULE_METADATA_SIZE_NROWS, "0")));
            resolutions.put(resolution, tgeox);
        }

        return resolutions;
    }

    @Override
    public String getTileID() {
        return getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_TILE_ID, null);
    }

    @Override
    public String getHORIZONTAL_CS_CODE() {
        return getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_CODE, null);
    }

    @Override
    public String getHORIZONTAL_CS_NAME() {
        return getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_NAME, null);
    }

    @Override
    public int getAnglesResolution() {
        return Integer.parseInt(getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_ANGLE_RESOLUTION, "0"));
    }

    @Override
    public S2Metadata.AnglesGrid getSunGrid() {
        return S2Metadata.wrapAngles(getAttributeValues(L3PSD13Constants.PATH_GRANULE_METADATA_SUN_ZENITH_ANGLES),
                                     getAttributeValues(L3PSD13Constants.PATH_GRANULE_METADATA_SUN_AZIMUTH_ANGLES));
    }

    @Override
    public S2Metadata.AnglesGrid[] getViewingAnglesGrid() {
        MetadataElement geometricElement = rootElement.getElement("Geometric_Info");
        if(geometricElement == null) {
            return null;
        }
        MetadataElement tileAnglesElement = geometricElement.getElement("Tile_Angles");
        if(tileAnglesElement == null) {
            return null;
        }
        return S2Metadata.wrapStandardViewingAngles(tileAnglesElement);
    }

    @Override
    public MetadataElement getMetadataElement() {
        return rootElement;
    }

    @Override
    public MetadataElement getSimplifiedMetadataElement() {
        //TODO
        return rootElement;
    }

    @Override
    public int getMaximumMosaicIndex() {
        int maxIndex = 0;
        for (String pviName : getAttributeValues(L3PSD13Constants.PATH_GRANULE_METADATA_PVI_FILENAME)) {
            int aux = Integer.parseInt(pviName.substring(pviName.lastIndexOf("_") + 1));
            if (aux > maxIndex) maxIndex = aux;
        }
        return maxIndex;
    }

    private void updateName() {
        String tileId = getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_TILE_ID, null);
        if(tileId == null || tileId.length()<56) {
            setName("Level-03_Tile_ID");
        }
        setName("Level-03_Tile_" + tileId.substring(50, 55));
    }
}
