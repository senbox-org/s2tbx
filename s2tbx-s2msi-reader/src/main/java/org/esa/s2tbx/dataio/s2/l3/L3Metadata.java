package org.esa.s2tbx.dataio.s2.l3;

import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_3_tile_metadata.Level3_Tile;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_2a.Level2A_User_Product;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_3.Level3_User_Product;
import https.psd_12_sentinel2_eo_esa_int.dico._12.pdgs.dimap.A_L3_PIXEL_LEVEL_QI;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l3.L3MetadataProc;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.esa.s2tbx.dataio.s2.l3.L3MetadataProc.makeMSCInformation;

/**
 * Created by obarrile on 15/06/2016.
 */
public class L3Metadata extends S2Metadata {

    private static final String PSD_STRING = null;

    protected Logger logger = SystemUtils.LOG;

    public static L3Metadata parseHeader(File file, String granuleName, S2Config config, String epsg, S2SpatialResolution productResolution) throws JDOMException, IOException, JAXBException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return new L3Metadata(stream, file, file.getParent(), granuleName, config, epsg, productResolution);
        }
    }

    private L3Metadata(InputStream stream, File file, String parent, String granuleName, S2Config config, String epsg, S2SpatialResolution productResolution) throws JDOMException, JAXBException, FileNotFoundException {
        super(config, L3MetadataProc.getJaxbContext(), PSD_STRING);

        try {
            Object userProductOrTile = updateAndUnmarshal(stream);

            if (userProductOrTile instanceof Level3_User_Product) {
                initProduct(file, parent, granuleName, userProductOrTile, epsg, productResolution);
            } else {
                initTile(file, userProductOrTile);
            }
        } catch (JAXBException | JDOMException | IOException e) {
            logger.severe(Utils.getStackTrace(e));
        }
    }

    private void initProduct(File file, String parent, String granuleName, Object casted, String epsg, S2SpatialResolution productResolution) throws IOException, JAXBException, JDOMException {
        Level3_User_Product product = (Level3_User_Product) casted;
        setProductCharacteristics(L3MetadataProc.getProductOrganization(product, productResolution));

        Collection<String> tileNames;

        if (granuleName == null) {
            tileNames = L3MetadataProc.getTiles(product);
        } else {
            tileNames = Collections.singletonList(granuleName);
        }

        List<File> fullTileNamesList = new ArrayList<>();

        resetTileList();

        for (String tileName : tileNames) {
            S2GranuleDirFilename aGranuleDir = S2OrthoGranuleDirFilename.create(tileName);
            if (aGranuleDir != null) {
                String theName = aGranuleDir.getMetadataFilename().name;

                File nestedGranuleMetadata = new File(parent, "GRANULE" + File.separator + tileName + File.separator + theName);
                if (nestedGranuleMetadata.exists()) {
                    fullTileNamesList.add(nestedGranuleMetadata);
                } else {
                    String errorMessage = "Corrupted product: the file for the granule " + tileName + " is missing";
                    logger.log(Level.WARNING, errorMessage);
                }
            }
        }


        int countInputs=0;
        for (File aGranuleMetadataFile : fullTileNamesList) {
            try (FileInputStream granuleStream = new FileInputStream(aGranuleMetadataFile)) {
                Level3_Tile aTile = (Level3_Tile) updateAndUnmarshal(granuleStream);

                Map<S2SpatialResolution, S2Metadata.TileGeometry> geoms = L3MetadataProc.getTileGeometries(aTile);

                S2Metadata.Tile tile = new S2Metadata.Tile(aGranuleMetadataFile.getParentFile().getName());
                tile.setHorizontalCsCode(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_CODE());
                tile.setHorizontalCsName(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_NAME());

                for (A_L3_PIXEL_LEVEL_QI pixel_level_qi : aTile.getQuality_Indicators_Info().getL3_Pixel_Level_QI()) {
                    String pviName = pixel_level_qi.getPVI_FILENAME();
                    int aux = Integer.parseInt(pviName.substring(pviName.lastIndexOf("_") + 1));
                    if (aux > countInputs) countInputs = aux;
                }
                if (!tile.getHorizontalCsCode().equals(epsg)) {
                    // skip tiles that are not in the desired UTM zone
                    logger.info(String.format("Skipping tile %s because it has crs %s instead of requested %s", aGranuleMetadataFile.getName(), tile.getHorizontalCsCode(), epsg));
                    continue;
                }

                tile.setTileGeometries(geoms);
                tile.setSunAnglesGrid(L3MetadataProc.getSunGrid(aTile));
                tile.setViewingIncidenceAnglesGrids(L3MetadataProc.getAnglesGrid(aTile));
                tile.setMaskFilenames(L3MetadataProc.getMasks(aTile, aGranuleMetadataFile));
                addTileToList(tile);
            }
        }

        // Updates mosaic band
        for(int i = 0; i<getProductCharacteristics().getBandInformations().length; i++) {
            if(getProductCharacteristics().getBandInformations()[i].getPhysicalBand().equals("mosaic_info")) {
                getProductCharacteristics().getBandInformations()[i]=makeMSCInformation(productResolution, countInputs);
            }
        }


        S2DatastripFilename stripName = L3MetadataProc.getDatastrip(product);
        S2DatastripDirFilename dirStripName = L3MetadataProc.getDatastripDir(product);

        File dataStripMetadata = new File(parent, "DATASTRIP" + File.separator + dirStripName.name + File.separator + stripName.name);

        MetadataElement userProduct = parseAll(new SAXBuilder().build(file).getRootElement());
        MetadataElement dataStrip = parseAll(new SAXBuilder().build(dataStripMetadata).getRootElement());
        getMetadataElements().add(userProduct);
        getMetadataElements().add(dataStrip);
        MetadataElement granulesMetaData = new MetadataElement("Granules");

        for (File aGranuleMetadataFile : fullTileNamesList) {
            MetadataElement aGranule = parseAll(new SAXBuilder().build(aGranuleMetadataFile).getRootElement());

            MetadataElement generalInfo = aGranule.getElement("General_Info");
            if (generalInfo != null) {
                MetadataAttribute tileIdAttr = generalInfo.getAttribute("TILE_ID_3");
                if (tileIdAttr != null) {
                    String newName = tileIdAttr.getData().toString();
                    if (newName.length() > 56)
                        aGranule.setName("Level-1C_Tile_" + newName.substring(50, 55));
                }
            }

            granulesMetaData.addElement(aGranule);
        }

        getMetadataElements().add(granulesMetaData);
    }

    private void initTile(File file, Object casted) throws IOException, JAXBException, JDOMException {
        Level3_Tile aTile = (Level3_Tile) casted;
        resetTileList();

        Map<S2SpatialResolution, S2Metadata.TileGeometry> geoms = L3MetadataProc.getTileGeometries(aTile);

        S2Metadata.Tile tile = new S2Metadata.Tile(file.getParentFile().getName());
        tile.setHorizontalCsCode(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_CODE());
        tile.setHorizontalCsName(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_NAME());

        tile.setTileGeometries(geoms);
        tile.setSunAnglesGrid(L3MetadataProc.getSunGrid(aTile));
        tile.setViewingIncidenceAnglesGrids(L3MetadataProc.getAnglesGrid(aTile));

        L3MetadataProc.getMasks(aTile, file);
        tile.setMaskFilenames(L3MetadataProc.getMasks(aTile, file));

        addTileToList(tile);
    }
}
