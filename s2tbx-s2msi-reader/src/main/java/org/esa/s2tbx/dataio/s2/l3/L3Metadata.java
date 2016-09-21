package org.esa.s2tbx.dataio.s2.l3;

import https.psd_12_sentinel2_eo_esa_int.dico._12.pdgs.dimap.A_L3_PIXEL_LEVEL_QI;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_3_tile_metadata.Level3_Tile;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_3.Level3_User_Product;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.metadata.PlainXmlMetadata;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;
import org.jdom.JDOMException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by obarrile on 15/06/2016.
 */
public class L3Metadata extends S2Metadata {

    private static final String PSD_STRING = null;
    public static final String MOSAIC_BAND_NAME = "quality_mosaic_info";
    private static final int DEFAULT_ANGLES_RESOLUTION = 5000;

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
            resetTileList();
            int maxIndex = 0;

            if (userProductOrTile instanceof Level3_User_Product) {
                maxIndex = initProduct(file, parent, granuleName, userProductOrTile, epsg, productResolution);
            } else {
                maxIndex = initTile(file, userProductOrTile, epsg, productResolution);
            }
            //add band information (at the end because we need to read the metadata to know the maximum index of mosaic)
            List<S2BandInformation> bandInfoList = L3MetadataProc.getBandInformationList(productResolution, getProductCharacteristics().getQuantificationValue(), maxIndex);
            int size = bandInfoList.size();
            getProductCharacteristics().setBandInformations(bandInfoList.toArray(new S2BandInformation[size]));

        } catch (JAXBException | JDOMException | IOException e) {
            logger.severe(Utils.getStackTrace(e));
        }
    }

    private int initProduct(File file, String parent, String granuleName, Object casted, String epsg, S2SpatialResolution productResolution) throws IOException, JAXBException, JDOMException {
        Level3_User_Product product = (Level3_User_Product) casted;
        setProductCharacteristics(L3MetadataProc.getProductOrganization(product, productResolution));

        Collection<String> tileNames;

        if (granuleName == null) {
            tileNames = L3MetadataProc.getTiles(product);
        } else {
            tileNames = Collections.singletonList(granuleName);
        }

        S2DatastripFilename stripName = L3MetadataProc.getDatastrip(product);
        S2DatastripDirFilename dirStripName = L3MetadataProc.getDatastripDir(product);

        File dataStripMetadata = new File(parent, "DATASTRIP" + File.separator + dirStripName.name + File.separator + stripName.name);
        Set<String> exclusions = new HashSet<String>() {{
            add("Viewing_Incidence_Angles_Grids");
            add("Sun_Angles_Grid");
        }};
        //MetadataElement userProduct = parseAll(new SAXBuilder().build(file).getRootElement());
        MetadataElement userProduct = PlainXmlMetadata.parse(file.toPath(), exclusions);
        //MetadataElement dataStrip = parseAll(new SAXBuilder().build(dataStripMetadata).getRootElement());
        MetadataElement dataStrip = PlainXmlMetadata.parse(dataStripMetadata.toPath(), null);
        getMetadataElements().add(userProduct);
        getMetadataElements().add(dataStrip);

        List<File> fullTileNamesList = new ArrayList<>();
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

        int maxIndex=1;
        //Init Tiles
        for (File aGranuleMetadataFile : fullTileNamesList) {
            FileInputStream granuleStream = new FileInputStream(aGranuleMetadataFile);
            Object granule = updateAndUnmarshal(granuleStream);
            granuleStream.close();
            int maxIndexTile = initTile(aGranuleMetadataFile, granule, epsg, productResolution);
            if (maxIndexTile > maxIndex) {
                maxIndex = maxIndexTile;
            }

        }
        return maxIndex;
    }

    /**
     *
     * @param file
     * @param casted
     * @param epsg
     * @param resolution
     * @return the maximum index of mosaic
     * @throws IOException
     * @throws JAXBException
     * @throws JDOMException
     */
    private int initTile(File file, Object casted, String epsg, S2SpatialResolution resolution) throws IOException, JAXBException, JDOMException {

        Level3_Tile aTile = (Level3_Tile) casted;
        if(getProductCharacteristics() == null) {
            logger.warning("Warning: the default quantification values will be used because they cannot be found in metadata\n");
            setProductCharacteristics(L3MetadataProc.getTileProductOrganization(aTile, resolution));
        }

        Map<S2SpatialResolution, TileGeometry> geoms = L3MetadataProc.getTileGeometries(aTile);

        S2Metadata.Tile tile = new S2Metadata.Tile(file.getParentFile().getName());
        tile.setHorizontalCsCode(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_CODE());
        tile.setHorizontalCsName(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_NAME());

        if (epsg != null && !tile.getHorizontalCsCode().equals(epsg)) {
            // skip tiles that are not in the desired UTM zone
            logger.info(String.format("Skipping tile %s because it has crs %s instead of requested %s", file.getName(), tile.getHorizontalCsCode(), epsg));
            return 0;
        }

        tile.setTileGeometries(geoms);

        try {
            tile.setAnglesResolution((int) aTile.getGeometric_Info().getTile_Angles().getSun_Angles_Grid().getAzimuth().getCOL_STEP().getValue());
        } catch (Exception e) {
            logger.warning("Angles resolution cannot be obtained");
            tile.setAnglesResolution(DEFAULT_ANGLES_RESOLUTION);
        }

        tile.setSunAnglesGrid(L3MetadataProc.getSunGrid(aTile));
        tile.setViewingIncidenceAnglesGrids(L3MetadataProc.getAnglesGrid(aTile));


        addTileToList(tile);

        //Search "Granules" metadata element. If it does not exist, it is created
        MetadataElement granulesMetaData = null;
        for(MetadataElement metadataElement : getMetadataElements()) {
            if(metadataElement.getName().equals("Granules")) {
                granulesMetaData = metadataElement;
            }
        }
        if (granulesMetaData == null) {
            granulesMetaData = new MetadataElement("Granules");
            getMetadataElements().add(granulesMetaData);
        }

        //MetadataElement aGranule = parseAll(new SAXBuilder().build(file).getRootElement());
        MetadataElement aGranule = PlainXmlMetadata.parse(file.toPath(), new HashSet<String>() {{
            add("Viewing_Incidence_Angles_Grids");
            add("Sun_Angles_Grid");
        }});

        //write the ID to improve UI
        MetadataElement generalInfo = aGranule.getElement("General_Info");
        if (generalInfo != null) {
            MetadataAttribute tileIdAttr = generalInfo.getAttribute("TILE_ID_3");
            if (tileIdAttr != null) {
                String newName = tileIdAttr.getData().toString();
                if (newName.length() > 56)
                    aGranule.setName("Level-03_Tile_" + newName.substring(50, 55));
            }
        }
        granulesMetaData.addElement(aGranule);

        //get maximum index of mosaic
        int maxIndex = 0;
        for (A_L3_PIXEL_LEVEL_QI pixel_level_qi : aTile.getQuality_Indicators_Info().getL3_Pixel_Level_QI()) {
            String pviName = pixel_level_qi.getPVI_FILENAME();
            int aux = Integer.parseInt(pviName.substring(pviName.lastIndexOf("_") + 1));
            if (aux > maxIndex) maxIndex = aux;
        }

        return maxIndex;
    }
}
