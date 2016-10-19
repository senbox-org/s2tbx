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

package org.esa.s2tbx.dataio.s2.l1c;


import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleMetadataFilename;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Norman Fomferra
 */
public class L1cMetadata extends S2Metadata {

    private static final int DEFAULT_ANGLES_RESOLUTION = 5000;

    protected Logger logger = SystemUtils.LOG;

    public static L1cMetadata parseHeader(File file, String granuleName, S2Config config, String epsg) throws IOException, ParserConfigurationException, SAXException {
        return new L1cMetadata(file.toPath(), granuleName, config, epsg);
    }


    private L1cMetadata(Path path, String granuleName, S2Config s2config, String epsg) throws IOException, ParserConfigurationException, SAXException {
        super(s2config);
        resetTileList();
        boolean isGranuleMetadata = S2OrthoGranuleMetadataFilename.isGranuleFilename(path.getFileName().toString());

        if(!isGranuleMetadata) {
            initProduct(path, granuleName, epsg);
        } else {
            initTile(path, epsg);
        }
        //TODO
    }

    private void initProduct(Path path, String granuleName, String epsg) throws IOException, ParserConfigurationException, SAXException {
        IL1cProductMetadata metadataProduct = L1cMetadataFactory.createL1cProductMetadata(path);
        setProductCharacteristics(metadataProduct.getProductOrganization());
        //TODO metadataProduct.updateNamingItems(getNamingItems());

        Collection<String> tileNames;

        if (granuleName == null) {
            tileNames = metadataProduct.getTiles();
        } else {
            tileNames = Collections.singletonList(granuleName);
        }

        S2DatastripFilename stripName = metadataProduct.getDatastrip();
        S2DatastripDirFilename dirStripName = metadataProduct.getDatastripDir();
        Path datastripPath = path.resolveSibling("DATASTRIP").resolve(dirStripName.name).resolve(stripName.name);
        IL1cDatastripMetadata metadataDatastrip = L1cMetadataFactory.createL1cDatastripMetadata(datastripPath);
        //TODO metadataDatastrip.updateNamingItems(getNamingItems());

        getMetadataElements().add(metadataProduct.getMetadataElement());
        getMetadataElements().add(metadataDatastrip.getMetadataElement());

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
        for (Path granuleMetadataPath : granuleMetadataPathList) {
            initTile(granuleMetadataPath, epsg);
        }
    }


    private void initTile(Path path, String epsg) throws IOException, ParserConfigurationException, SAXException {

        IL1cGranuleMetadata granuleMetadata = L1cMetadataFactory.createL1cGranuleMetadata(path);
        //TODO granuleMetadata.updateNamingItems(getNamingItems());

        if(getProductCharacteristics() == null) {
            setProductCharacteristics(granuleMetadata.getTileProductOrganization());
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

        //granuleMetadata.updateName(); //for including the tile id
        granulesMetaData.addElement(granuleMetadata.getSimplifiedMetadataElement());
    }

}
