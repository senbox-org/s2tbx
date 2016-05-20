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

import https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata.Level1B_Granule;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1b.Level1B_User_Product;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.SystemUtils;
import org.jdom.DataConversionException;
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

/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Norman Fomferra
 */
public class L1bMetadata extends S2Metadata {

    private static final String PSD_STRING = "13";

    protected Logger logger = SystemUtils.LOG;

    private ProductCharacteristics productCharacteristics;


    public static L1bMetadata parseHeader(File file, String granuleName, S2Config config) throws JDOMException, IOException, JAXBException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return new L1bMetadata(stream, file, file.getParent(), granuleName, config);
        }
    }

    public ProductCharacteristics getProductCharacteristics() {
        return productCharacteristics;
    }

    private L1bMetadata(InputStream stream, File file, String parent, String granuleName, S2Config config) throws DataConversionException, JAXBException, FileNotFoundException {
        super(config, L1bMetadataProc.getJaxbContext(), PSD_STRING);

        try {
            Object userProductOrTile = updateAndUnmarshal(stream);

            if (userProductOrTile instanceof Level1B_User_Product) {
                initProduct(file, parent, granuleName, userProductOrTile);
            } else {
                initTile(userProductOrTile);
            }

        } catch (JAXBException | JDOMException | IOException e) {
            logger.severe(Utils.getStackTrace(e));
        }
    }

    private void initProduct(File file, String parent, String granuleName, Object casted
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

        MetadataElement userProduct = parseAll(new SAXBuilder().build(file).getRootElement());
        MetadataElement dataStrip = parseAll(new SAXBuilder().build(dataStripMetadata).getRootElement());
        getMetadataElements().add(userProduct);
        getMetadataElements().add(dataStrip);
        MetadataElement granulesMetaData = new MetadataElement("Granules");


        for (File aGranuleMetadataFile : fullTileNamesList) {
            MetadataElement aGranule = parseAll(new SAXBuilder().build(aGranuleMetadataFile).getRootElement());
            granulesMetaData.addElement(aGranule);
        }

        getMetadataElements().add(granulesMetaData);
    }

    private void initTile(Object casted) throws IOException, JAXBException, JDOMException {
        Level1B_Granule aGranule = (Level1B_Granule) casted;
        productCharacteristics = new L1bMetadata.ProductCharacteristics();
        resetTileList();
        Map<S2SpatialResolution, TileGeometry> geoms = L1bMetadataProc.getGranuleGeometries(aGranule, getConfig());
        Tile tile = new Tile(aGranule.getGeneral_Info().getGRANULE_ID().getValue(), aGranule.getGeneral_Info().getDETECTOR_ID().getValue());
        tile.setTileGeometries(geoms);
        tile.corners = L1bMetadataProc.getGranuleCorners(aGranule); // counterclockwise
        addTileToList(tile);
    }
}
