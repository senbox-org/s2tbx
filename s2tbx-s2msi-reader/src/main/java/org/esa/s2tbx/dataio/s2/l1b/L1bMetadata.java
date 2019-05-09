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

package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
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
public class L1bMetadata extends S2Metadata {
    protected Logger logger = SystemUtils.LOG;

    public static L1bMetadata parseHeader(VirtualPath path, String granuleName, S2Config config, boolean isAGranule, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {
        return new L1bMetadata(path, granuleName, config, isAGranule, namingConvention);
    }

    private L1bMetadata(VirtualPath path, String granuleName, S2Config config, boolean isAGranule, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {
        super(config);

        resetTileList();

        if(!isAGranule) {
            initProduct(path, granuleName, namingConvention);
        } else {
            initTile(path, namingConvention);
        }
    }


    private void initProduct(VirtualPath path, String granuleName, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {

        IL1bProductMetadata metadataProduct = L1bMetadataFactory.createL1bProductMetadata(path);
        if(metadataProduct == null) {
            throw new IOException(String.format("Unable to read metadata from %s",path.getFileName().toString()));
        }
        setProductCharacteristics(metadataProduct.getProductOrganization());

        Collection<String> tileNames;
        if (granuleName == null) {
            tileNames = metadataProduct.getTiles();
        } else {
            tileNames = Collections.singletonList(granuleName);
        }


        //add product metadata
        getMetadataElements().add(metadataProduct.getMetadataElement());

        //add datastrip metadata
        for(VirtualPath datastripPath : namingConvention.getDatastripXmlPaths()) {
            IL1bDatastripMetadata metadataDatastrip = L1bMetadataFactory.createL1bDatastripMetadata(datastripPath);
            getMetadataElements().add(metadataDatastrip.getMetadataElement());
        }

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
        for (VirtualPath granuleMetadataPath : granuleMetadataPathList) {
            initTile(granuleMetadataPath, namingConvention);
        }
    }

    private void initTile(VirtualPath path, INamingConvention namingConvention) throws IOException, ParserConfigurationException, SAXException {
        IL1bGranuleMetadata granuleMetadata = L1bMetadataFactory.createL1bGranuleMetadata(path);
        if(granuleMetadata == null) {
            throw new IOException(String.format("Unable to read metadata from %s",path.getFileName().toString()));
        }
        if(getProductCharacteristics() == null) {
            setProductCharacteristics(granuleMetadata.getTileProductOrganization());
        }

        Map<S2SpatialResolution, TileGeometry> geoms = granuleMetadata.getGranuleGeometries(getConfig());
        Tile tile = new Tile(granuleMetadata.getGranuleID(), granuleMetadata.getDetectorID());
        tile.setTileGeometries(geoms);
        tile.corners = granuleMetadata.getGranuleCorners(); // counterclockwise
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
