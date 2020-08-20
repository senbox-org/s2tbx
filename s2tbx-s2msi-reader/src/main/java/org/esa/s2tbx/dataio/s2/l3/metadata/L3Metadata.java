package org.esa.s2tbx.dataio.s2.l3.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadata;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by obarrile on 15/06/2016.
 */
public class L3Metadata extends S2OrthoMetadata {

    public static final String MOSAIC_BAND_NAME = "quality_mosaic_info";
    private static final int DEFAULT_ANGLES_RESOLUTION = 5000;

    protected Logger logger = SystemUtils.LOG;

    public static L3Metadata parseHeader(VirtualPath path, String granuleName, S2Config config, String epsg, S2SpatialResolution productResolution, boolean isAGranule, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {

        return new L3Metadata(path, granuleName, config, epsg, productResolution, isAGranule, namingConvention);

    }

    private L3Metadata(VirtualPath path, String granuleName, S2Config config, String epsg, S2SpatialResolution productResolution, boolean isAGranule, INamingConvention namingConvention) throws  IOException, ParserConfigurationException, SAXException {
        super(path, granuleName, !isAGranule, config);

        resetTileList();
        int maxIndex = 0;

        if(!isAGranule) {
            maxIndex = initProduct(path, granuleName, epsg, productResolution, namingConvention);
        } else {
            maxIndex = initTile(path, epsg, productResolution, namingConvention);
        }

        //add band information (at the end because we need to read the metadata to know the maximum index of mosaic)
        List<S2BandInformation> bandInfoList = L3MetadataProc.getBandInformationList(getFormat(), productResolution, getProductCharacteristics().getQuantificationValue(), maxIndex);
        int size = bandInfoList.size();
        getProductCharacteristics().setBandInformations(bandInfoList.toArray(new S2BandInformation[size]));

    }

    private int initProduct(VirtualPath path, String granuleName, String epsg, S2SpatialResolution productResolution, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {
        IL3ProductMetadata metadataProduct = L3MetadataFactory.createL3ProductMetadata(path);
        if(metadataProduct == null) {
            throw new IOException(String.format("Unable to read metadata from %s",path.getFileName().toString()));
        }
        setFormat(metadataProduct.getFormat());
        setProductCharacteristics(metadataProduct.getProductOrganization(path, productResolution));

        Collection<String> tileNames = null;

        if (granuleName == null) {
            tileNames = metadataProduct.getTiles();
        } else {
            String granuleId = namingConvention.findGranuleId(metadataProduct.getTiles(),granuleName);
            if(granuleId == null) {
                throw new IOException(String.format("Unable to find %s into the available product granules", granuleName));
            }
            tileNames = Collections.singletonList(granuleId);
        }

        //add product metadata
        getMetadataElements().add(metadataProduct.getMetadataElement());

        //add datastrip metadatas
        for(VirtualPath datastripPath : namingConvention.getDatastripXmlPaths()) {
            IL3DatastripMetadata metadataDatastrip = L3MetadataFactory.createL3DatastripMetadata(datastripPath);
            getMetadataElements().add(metadataDatastrip.getMetadataElement());
        }

        //Check if the tiles found in metadata exist and add them to granuleMetadataPathList
        ArrayList<VirtualPath> granuleMetadataPathList = new ArrayList<>();
        for (String tileName : tileNames) {
            VirtualPath folder = namingConvention.findGranuleFolderFromTileId(tileName);
            VirtualPath xml = namingConvention.findXmlFromTileId(tileName);
            if(folder == null || xml == null) {
                String errorMessage = "Corrupted product: the file for the granule " + tileName + " is missing";
                logger.log(Level.WARNING, errorMessage);
            }
            resourceResolver.put(tileName,folder);
            granuleMetadataPathList.add(xml);
        }

        //Init Tiles
        int maxIndex=1;
        for (VirtualPath granuleMetadataPath : granuleMetadataPathList) {
            int maxIndexTile =initTile(granuleMetadataPath, epsg, productResolution, namingConvention);
            if (maxIndexTile > maxIndex) {
                maxIndex = maxIndexTile;
            }
        }
        return maxIndex;
    }

    private int initTile(VirtualPath path, String epsg, S2SpatialResolution resolution, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {

        IL3GranuleMetadata granuleMetadata = L3MetadataFactory.createL3GranuleMetadata(path);
        if(granuleMetadata == null) {
            throw new IOException(String.format("Unable to read metadata from %s",path.getFileName().toString()));
        }
        if(getFormat() == null) {
            setFormat(granuleMetadata.getFormat());
        }

        if(getProductCharacteristics() == null) {
            setProductCharacteristics(granuleMetadata.getTileProductOrganization(path, resolution));
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
