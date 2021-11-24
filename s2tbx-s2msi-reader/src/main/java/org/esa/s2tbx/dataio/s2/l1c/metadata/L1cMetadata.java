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

package org.esa.s2tbx.dataio.s2.l1c.metadata;


import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadata;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Norman Fomferra
 */
public class L1cMetadata extends S2OrthoMetadata {

    private static final int DEFAULT_ANGLES_RESOLUTION = 5000;

    protected Logger logger = SystemUtils.LOG;


    public static L1cMetadata parseHeader(VirtualPath path, String granuleName, S2Config config, String epsg, boolean isAGranule, INamingConvention namingConvention)
                                          throws IOException, ParserConfigurationException, SAXException {

        return new L1cMetadata(path, granuleName, config, epsg, isAGranule, namingConvention);
    }

    private L1cMetadata(VirtualPath path, String granuleName, S2Config s2config, String epsg, boolean isGranule, INamingConvention namingConvention)
                        throws IOException, ParserConfigurationException, SAXException {

        super(path, granuleName, !isGranule, s2config);

        resetTileList();
        if (!isGranule) {
            initProduct(path, granuleName, epsg, namingConvention);
        } else {
            initTile(path, null,epsg, namingConvention);
        }
    }

    private void initProduct(VirtualPath path, String granuleName, String epsg, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {
        IL1cProductMetadata metadataProduct = L1cMetadataFactory.createL1cProductMetadata(path);
        if(metadataProduct == null) {
            throw new IOException(String.format("Unable to read metadata from %s",path.getFileName().toString()));
        }
        setFormat(metadataProduct.getFormat());
        setProductCharacteristics(metadataProduct.getProductOrganization(path));

        Collection<String> tileNames = null;

        if (granuleName == null) {
            tileNames = metadataProduct.getTiles();
        } else {
            //the granule name is the name of the folder. Depending on the naming convention
            //it is the same than the tileNames or not. To find the granuleId between the tiles
            //it is needed to use the namingConvention
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
                IL1cDatastripMetadata metadataDatastrip = L1cMetadataFactory.createL1cDatastripMetadata(datastripPath);
                getMetadataElements().add(metadataDatastrip.getMetadataElement());
        }

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
            initTile(granuleMetadataPath, path, epsg, namingConvention);
        }
    }


    private void initTile(VirtualPath path, VirtualPath metadataProductpath,String epsg, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {

        IL1cGranuleMetadata granuleMetadata = L1cMetadataFactory.createL1cGranuleMetadata(path, metadataProductpath);
        if(granuleMetadata == null) {
            throw new IOException(String.format("Unable to read metadata from %s",path.getFileName().toString()));
        }
        if(getFormat() == null) {
            setFormat(granuleMetadata.getFormat());
        }
        if(getProductCharacteristics() == null) {
            setProductCharacteristics(granuleMetadata.getTileProductOrganization(path));
        }

        Map<S2SpatialResolution, TileGeometry> geoms = granuleMetadata.getTileGeometries();

        Tile tile = new Tile(granuleMetadata.getTileID());
        tile.setHorizontalCsCode(granuleMetadata.getHORIZONTAL_CS_CODE());
        tile.setHorizontalCsName(granuleMetadata.getHORIZONTAL_CS_NAME());

        if (epsg != null && tile.getHorizontalCsCode() != null && !tile.getHorizontalCsCode().equals(epsg)) {
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

        //granuleMetadata.updateName(); //for including the tile id
        granulesMetaData.addElement(granuleMetadata.getSimplifiedMetadataElement());
    }

}
