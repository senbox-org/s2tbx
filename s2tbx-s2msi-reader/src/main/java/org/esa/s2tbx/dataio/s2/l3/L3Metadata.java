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
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleMetadataFilename;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;
import org.jdom.JDOMException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static L3Metadata parseHeader(File file, String granuleName, S2Config config, String epsg, S2SpatialResolution productResolution) throws IOException {

        return new L3Metadata(file.toPath(), granuleName, config, epsg, productResolution);

    }

    private L3Metadata(Path path, String granuleName, S2Config config, String epsg, S2SpatialResolution productResolution) throws  IOException {
        super(config);

        resetTileList();
        int maxIndex = 0;
        boolean isGranuleMetadata = S2OrthoGranuleMetadataFilename.isGranuleFilename(path.getFileName().toString());

        if(!isGranuleMetadata) {
            maxIndex = initProduct(path, granuleName, epsg, productResolution);
        } else {
            maxIndex = initTile(path, epsg, productResolution);
        }

        //add band information (at the end because we need to read the metadata to know the maximum index of mosaic)
        List<S2BandInformation> bandInfoList = L3MetadataProc.getBandInformationList(productResolution, getProductCharacteristics().getQuantificationValue(), maxIndex);
        int size = bandInfoList.size();
        getProductCharacteristics().setBandInformations(bandInfoList.toArray(new S2BandInformation[size]));

    }

    private int initProduct(Path path, String granuleName, String epsg, S2SpatialResolution productResolution) throws IOException {
        IL3ProductMetadata metadataProduct = L3MetadataFactory.createL3ProductMetadata(path);
        setProductCharacteristics(metadataProduct.getProductOrganization(productResolution));

        Collection<String> tileNames;

        if (granuleName == null) {
            tileNames = metadataProduct.getTiles();
        } else {
            tileNames = Collections.singletonList(granuleName);
        }

        S2DatastripFilename stripName = metadataProduct.getDatastrip();
        S2DatastripDirFilename dirStripName = metadataProduct.getDatastripDir();
        Path datastripPath = path.resolveSibling("DATASTRIP").resolve(dirStripName.name).resolve(stripName.name);
        IL3DatastripMetadata metadataDatastrip = L3MetadataFactory.createL3DatastripMetadata(datastripPath);

        getMetadataElements().add(metadataProduct.getMetadataElement());
        getMetadataElements().add(metadataDatastrip.getMetadataElement());

        //Check if the tiles found in metadata exist and add them to fullTileNamesList
        ArrayList<Path> granuleMetadataPathList = new ArrayList<>();
        for (String tileName : tileNames) {
            S2OrthoGranuleDirFilename aGranuleDir = S2OrthoGranuleDirFilename.create(tileName);

            if (aGranuleDir != null) {
                String theName = aGranuleDir.getMetadataFilename().name;

                Path nestedGranuleMetadata = path.resolveSibling("GRANULE").resolve(tileName).resolve(theName);
                if (Files.exists(nestedGranuleMetadata)) {
                    granuleMetadataPathList.add(nestedGranuleMetadata);
                } else {
                    String errorMessage = "Corrupted product: the file for the granule " + tileName + " is missing";
                    logger.log(Level.WARNING, errorMessage);
                }
            }
        }

        //Init Tiles
        int maxIndex=1;
        for (Path granuleMetadataPath : granuleMetadataPathList) {
            int maxIndexTile =initTile(granuleMetadataPath, epsg, productResolution);
            if (maxIndexTile > maxIndex) {
                maxIndex = maxIndexTile;
            }
        }
        return maxIndex;
    }

    private int initTile(Path path, String epsg, S2SpatialResolution resolution) throws IOException {

        IL3GranuleMetadata granuleMetadata = L3MetadataFactory.createL3GranuleMetadata(path);

        if(getProductCharacteristics() == null) {
            setProductCharacteristics(granuleMetadata.getTileProductOrganization(resolution));
        }

        Map<S2SpatialResolution, TileGeometry> geoms = granuleMetadata.getTileGeometries();

        Tile tile = new Tile(granuleMetadata.getTileID());
        tile.setHorizontalCsCode(granuleMetadata.getHORIZONTAL_CS_CODE());
        tile.setHorizontalCsName(granuleMetadata.getHORIZONTAL_CS_NAME());

        if (epsg != null && !tile.getHorizontalCsCode().equals(epsg)) {
            // skip tiles that are not in the desired UTM zone
            logger.info(String.format("Skipping tile %s because it has crs %s instead of requested %s", path.getFileName().toString(), tile.getHorizontalCsCode(), epsg));
            return 0;
        }

        tile.setTileGeometries(geoms);

        try {
            tile.setAnglesResolution(granuleMetadata.getAnglesResolution());
        } catch (Exception e) {
            logger.warning("Angles resolution cannot be obtained");
            tile.setAnglesResolution(DEFAULT_ANGLES_RESOLUTION);
        }

        tile.setSunAnglesGrid(granuleMetadata.getSunGrid());
        if(getProductCharacteristics().getMetaDataLevel()== null || !getProductCharacteristics().getMetaDataLevel().equals("Brief")) {
            tile.setViewingIncidenceAnglesGrids(granuleMetadata.getViewingAnglesGrid());
        }

        addTileToList(tile);

        //Search "Granules" metadata element. If it does not exist, it is created
        MetadataElement granulesMetaData = null;
        for(MetadataElement metadataElement : getMetadataElements()) {
            if(metadataElement.getName().equals("Granules")) {
                granulesMetaData = metadataElement;
                break;
            }
        }
        if (granulesMetaData == null) {
            granulesMetaData = new MetadataElement("Granules");
            getMetadataElements().add(granulesMetaData);
        }

        granulesMetaData.addElement(granuleMetadata.getSimplifiedMetadataElement());

        return granuleMetadata.getMaximumMosaicIndex();
    }

}
