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

package org.esa.s2tbx.dataio.s2.l2a.metadata;

import org.apache.commons.io.IOUtils;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadata;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Norman Fomferra
 */
public class L2aMetadata extends S2OrthoMetadata {

    private static final int DEFAULT_ANGLES_RESOLUTION = 5000;

    // /**
    //  * Read the content of 'path' searching the string "psd-XX.sentinel2.eo.esa.int" and return an integer.
    //  * Checks also some items in file to be able to distinguish PSD subversion 14.x and convert to an integer 14x
    //  * @param path
    //  * @return the psd version number or 0 if a problem occurs while reading the file or the version is not found.
    //  */
    // public static int getFullPSDversion(VirtualPath path){
    //     int psd=0;
    //     try (InputStream stream = path.getInputStream()){

    //         String xmlStreamAsString = IOUtils.toString(stream);
    //         String aux = xmlStreamAsString;
    //         String regex = "psd-\\d{2,}.sentinel2.eo.esa.int";

    //         Pattern p = Pattern.compile(regex);
    //         Matcher m = p.matcher(xmlStreamAsString);
    //         if (m.find()) {
    //             int position = m.start();
    //             String psdNumber = xmlStreamAsString.substring(position+4,position+6);

    //             //Check specific 14.3 psd, not possible to distinguish in 'regex'
    //             int processingBaseline = S2Metadata.getProcessingBaseline(path);
    //             if(Integer.parseInt(psdNumber) == 14 && processingBaseline>399)
    //             {
    //                 psd = 148;
    //             } else if(Integer.parseInt(psdNumber) == 14 && !aux.contains("L2A_Product_Info") && !aux.contains("TILE_ID_2A")) {
    //                 psd = 143;
    //             }
                
    //         } else {
    //             psd = 0;
    //         }

    //     } catch (Exception e) {
    //         psd = 0;
    //     }
    //     return psd;
    // }

    protected Logger logger = SystemUtils.LOG;

    public static L2aMetadata parseHeader(VirtualPath path, String granuleName, S2Config config, String epsg, S2SpatialResolution productResolution, boolean isAGranule, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {
        return new L2aMetadata(path, granuleName, config, epsg, productResolution, isAGranule, namingConvention);
    }


    private L2aMetadata(VirtualPath path, String granuleName, S2Config s2config, String epsg, S2SpatialResolution productResolution, boolean isAGranule, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {
        super(path, granuleName, !isAGranule, s2config);
        resetTileList();

        if(!isAGranule) {
            initProduct(path, granuleName, epsg, productResolution, namingConvention);
        } else {
            initTile(path, epsg, productResolution, namingConvention);
        }
    }


    private void initProduct(VirtualPath path, String granuleName, String epsg, S2SpatialResolution productResolution, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {
        IL2aProductMetadata metadataProduct = L2aMetadataFactory.createL2aProductMetadata(path);
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
            IL2aDatastripMetadata metadataDatastrip = L2aMetadataFactory.createL2aDatastripMetadata(datastripPath);
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
                continue;
            }
            resourceResolver.put(tileName,folder);
            granuleMetadataPathList.add(xml);
        }

        //Init Tiles
        for (VirtualPath granuleMetadataPath : granuleMetadataPathList) {
            initTile(granuleMetadataPath, epsg, productResolution, namingConvention);
        }
    }

    private void initTile(VirtualPath path, String epsg, S2SpatialResolution resolution, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {

        IL2aGranuleMetadata granuleMetadata = L2aMetadataFactory.createL2aGranuleMetadata(path);
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
            return;
        }

        tile.setTileGeometries(geoms);

        try {
            tile.setAnglesResolution(granuleMetadata.getAnglesResolution());
        } catch (Exception e) {
            logger.warning("Angles resolution cannot be obtained");
            tile.setAnglesResolution(DEFAULT_ANGLES_RESOLUTION);
        }

        tile.setSunAnglesGrid(granuleMetadata.getSunGrid());
        if(!getProductCharacteristics().getMetaDataLevel().equals("Brief")) {
            tile.setViewingIncidenceAnglesGrids(granuleMetadata.getViewingAnglesGrid());
        }

        //granuleMetadata.getMasks(path);
        tile.setMaskFilenames(granuleMetadata.getMasks(path));

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
    }
}
