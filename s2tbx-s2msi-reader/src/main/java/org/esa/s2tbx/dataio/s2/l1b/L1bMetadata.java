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

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleMetadataFilename;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.SystemUtils;
import org.jdom.DataConversionException;
import org.jdom.JDOMException;

import javax.xml.bind.JAXBException;
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
public class L1bMetadata extends S2Metadata {

    //private static final String PSD_STRING = "13";

    protected Logger logger = SystemUtils.LOG;

    private ProductCharacteristics productCharacteristics;


    public static L1bMetadata parseHeader(File file, String granuleName, S2Config config) throws JDOMException, IOException, JAXBException {
        return new L1bMetadata(file.toPath(), granuleName, config);
    }

    public ProductCharacteristics getProductCharacteristics() {
        return productCharacteristics;
    }

    private L1bMetadata(Path path, String granuleName, S2Config config) throws DataConversionException, JAXBException, IOException {
        super(config);

        resetTileList();
        boolean isGranuleMetadata = S2L1BGranuleMetadataFilename.isGranuleFilename(path.getFileName().toString());

        if(!isGranuleMetadata) {
            initProduct(path, granuleName);
        } else {
            initTile(path);
        }
        /*try {
            Object userProductOrTile = updateAndUnmarshal(stream);

            if (userProductOrTile instanceof Level1B_User_Product) {
                initProduct(file, parent, granuleName, userProductOrTile);
            } else {
                initTile(userProductOrTile);
            }

        } catch (JAXBException | JDOMException | IOException e) {
            logger.severe(Utils.getStackTrace(e));
        }*/
    }


    private void initProduct(Path path, String granuleName) throws IOException {

        IL1bProductMetadata metadataProduct = L1bMetadataFactory.createL1bProductMetadata(path);

        productCharacteristics = metadataProduct.getProductOrganization();

        Collection<String> tileNames;
        if (granuleName == null) {
            tileNames = metadataProduct.getTiles();
        } else {
            tileNames = Collections.singletonList(granuleName);
        }
        List<Path> fullTileNamesList = new ArrayList<>();


        for (String tileName : tileNames) {
            Path nestedMetadata = path.resolveSibling("GRANULE").resolve(tileName);

            if (Files.exists(nestedMetadata)) {
                logger.log(Level.FINE, "File found: " + nestedMetadata);
                S2GranuleDirFilename aGranuleDir = S2L1BGranuleDirFilename.create(tileName);
                Guardian.assertNotNull("aGranuleDir", aGranuleDir);
                String theName = aGranuleDir.getMetadataFilename().name;

                Path nestedGranuleMetadata = path.resolveSibling("GRANULE").resolve(tileName).resolve(theName);
                if (Files.exists(nestedGranuleMetadata)) {
                    fullTileNamesList.add(nestedGranuleMetadata);
                } else {
                    String errorMessage = "Corrupted product: the file for the granule " + tileName + " is missing";
                    logger.log(Level.WARNING, errorMessage);
                }
            } else {
                logger.log(Level.SEVERE, "File not found: " + nestedMetadata);
            }
        }

        /*for (Path aGranuleMetadataPath : fullTileNamesList) {
            try (FileInputStream granuleStream = new FileInputStream(aGranuleMetadataFile)) {
                Level1B_Granule aGranule = (Level1B_Granule) updateAndUnmarshal(granuleStream);

                Map<S2SpatialResolution, TileGeometry> geoms = L1bMetadataProc.getGranuleGeometries(aGranule, getConfig());

                Tile tile = new Tile(aGranule.getGeneral_Info().getGRANULE_ID().getValue(), aGranule.getGeneral_Info().getDETECTOR_ID().getValue());
                tile.setTileGeometries(geoms);
                tile.corners = L1bMetadataProc.getGranuleCorners(aGranule); // counterclockwise
                addTileToList(tile);
            }
        }*/

        S2DatastripFilename stripName = metadataProduct.getDatastrip();
        S2DatastripDirFilename dirStripName = metadataProduct.getDatastripDir();
        Path datastripPath = path.resolveSibling("DATASTRIP").resolve(dirStripName.name).resolve(stripName.name);
        IL1bDatastripMetadata metadataDatastrip = L1bMetadataFactory.createL1bDatastripMetadata(datastripPath);

        getMetadataElements().add(metadataProduct.getMetadataElement());
        getMetadataElements().add(metadataDatastrip.getMetadataElement());

        //Init Tiles
        for (Path granuleMetadataPath : fullTileNamesList) {
            initTile(granuleMetadataPath);
        }


       /* //MetadataElement userProduct = parseAll(new SAXBuilder().build(file).getRootElement());
        Set<String> exclusions = new HashSet<String>() {{
            add("Viewing_Incidence_Angles_Grids");
            add("Sun_Angles_Grid");
        }};
        MetadataElement userProduct = PlainXmlMetadata.parse(file.toPath(), exclusions);
        //MetadataElement dataStrip = parseAll(new SAXBuilder().build(dataStripMetadata).getRootElement());
        MetadataElement dataStrip = PlainXmlMetadata.parse(dataStripMetadata.toPath(), null);
        getMetadataElements().add(userProduct);
        getMetadataElements().add(dataStrip);
        MetadataElement granulesMetaData = new MetadataElement("Granules");


        for (File aGranuleMetadataFile : fullTileNamesList) {
            //MetadataElement aGranule = parseAll(new SAXBuilder().build(aGranuleMetadataFile).getRootElement());
            MetadataElement aGranule = PlainXmlMetadata.parse(aGranuleMetadataFile.toPath(), exclusions);

            MetadataElement generalInfo = aGranule.getElement("General_Info");
            if (generalInfo != null) {
                MetadataAttribute tileIdAttr = generalInfo.getAttribute("GRANULE_ID");
                if (tileIdAttr != null) {
                    String newName = tileIdAttr.getData().toString();
                    if (newName.length() > 62)
                        aGranule.setName("Level-1B_Granule_" + newName.substring(51, 61));
                }
            }

            granulesMetaData.addElement(aGranule);
        }

        getMetadataElements().add(granulesMetaData);*/
    }




    /*private void initProduct(File file, String parent, String granuleName, Object casted
    ) throws IOException, JAXBException, JDOMException {
        Level1B_User_Product product = (Level1B_User_Product) casted;
        productCharacteristics = L1bMetadataProc.getProductOrganization(product);

        Collection<String> tileNames;
        if (granuleName == null) {
            tileNames = L1bMetadataProc.getTiles(product);
        } else {
            tileNames = Collections.singletonList(granuleName);
        }
        List<File> fullTileNamesList = new ArrayList<>();


        resetTileList();

        for (String tileName : tileNames) {
            File nestedMetadata = new File(parent, "GRANULE" + File.separator + tileName);

            if (nestedMetadata.exists()) {
                logger.log(Level.FINE, "File found: " + nestedMetadata.getAbsolutePath());
                S2GranuleDirFilename aGranuleDir = S2L1BGranuleDirFilename.create(tileName);
                Guardian.assertNotNull("aGranuleDir", aGranuleDir);
                String theName = aGranuleDir.getMetadataFilename().name;

                File nestedGranuleMetadata = new File(parent, "GRANULE" + File.separator + tileName + File.separator + theName);
                if (nestedGranuleMetadata.exists()) {
                    fullTileNamesList.add(nestedGranuleMetadata);
                } else {
                    String errorMessage = "Corrupted product: the file for the granule " + tileName + " is missing";
                    logger.log(Level.WARNING, errorMessage);
                }
            } else {
                logger.log(Level.SEVERE, "File not found: " + nestedMetadata.getAbsolutePath());
            }
        }

        for (File aGranuleMetadataFile : fullTileNamesList) {
            try (FileInputStream granuleStream = new FileInputStream(aGranuleMetadataFile)) {
                Level1B_Granule aGranule = (Level1B_Granule) updateAndUnmarshal(granuleStream);

                Map<S2SpatialResolution, TileGeometry> geoms = L1bMetadataProc.getGranuleGeometries(aGranule, getConfig());

                Tile tile = new Tile(aGranule.getGeneral_Info().getGRANULE_ID().getValue(), aGranule.getGeneral_Info().getDETECTOR_ID().getValue());
                tile.setTileGeometries(geoms);
                tile.corners = L1bMetadataProc.getGranuleCorners(aGranule); // counterclockwise
                addTileToList(tile);
            }
        }

        S2DatastripFilename stripName = L1bMetadataProc.getDatastrip(product);
        S2DatastripDirFilename dirStripName = L1bMetadataProc.getDatastripDir(product);

        File dataStripMetadata = new File(parent, "DATASTRIP" + File.separator + dirStripName.name + File.separator + stripName.name);

        //MetadataElement userProduct = parseAll(new SAXBuilder().build(file).getRootElement());
        Set<String> exclusions = new HashSet<String>() {{
                add("Viewing_Incidence_Angles_Grids");
                add("Sun_Angles_Grid");
            }};
        MetadataElement userProduct = PlainXmlMetadata.parse(file.toPath(), exclusions);
        //MetadataElement dataStrip = parseAll(new SAXBuilder().build(dataStripMetadata).getRootElement());
        MetadataElement dataStrip = PlainXmlMetadata.parse(dataStripMetadata.toPath(), null);
        getMetadataElements().add(userProduct);
        getMetadataElements().add(dataStrip);
        MetadataElement granulesMetaData = new MetadataElement("Granules");


        for (File aGranuleMetadataFile : fullTileNamesList) {
            //MetadataElement aGranule = parseAll(new SAXBuilder().build(aGranuleMetadataFile).getRootElement());
            MetadataElement aGranule = PlainXmlMetadata.parse(aGranuleMetadataFile.toPath(), exclusions);

            MetadataElement generalInfo = aGranule.getElement("General_Info");
            if (generalInfo != null) {
                MetadataAttribute tileIdAttr = generalInfo.getAttribute("GRANULE_ID");
                if (tileIdAttr != null) {
                    String newName = tileIdAttr.getData().toString();
                    if (newName.length() > 62)
                        aGranule.setName("Level-1B_Granule_" + newName.substring(51, 61));
                }
            }

            granulesMetaData.addElement(aGranule);
        }

        getMetadataElements().add(granulesMetaData);
    }*/

    private void initTile(Path path) throws IOException{
        IL1bGranuleMetadata granuleMetadata = L1bMetadataFactory.createL1bGranuleMetadata(path);

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

        //granuleMetadata.updateName(); //for including the tile id
        granulesMetaData.addElement(granuleMetadata.getSimplifiedMetadataElement());
    }
    /*private void initTile(Object casted) throws IOException, JAXBException, JDOMException {
        Level1B_Granule aGranule = (Level1B_Granule) casted;
        productCharacteristics = new L1bMetadata.ProductCharacteristics();
        resetTileList();
        Map<S2SpatialResolution, TileGeometry> geoms = L1bMetadataProc.getGranuleGeometries(aGranule, getConfig());
        Tile tile = new Tile(aGranule.getGeneral_Info().getGRANULE_ID().getValue(), aGranule.getGeneral_Info().getDETECTOR_ID().getValue());
        tile.setTileGeometries(geoms);
        tile.corners = L1bMetadataProc.getGranuleCorners(aGranule); // counterclockwise
        addTileToList(tile);
    }*/
}
