/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.l2a;

import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_2a_tile_metadata.Level2A_Tile;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_2a.Level2A_User_Product;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l2a.filepatterns.S2L2aGranuleDirFilename;
import org.esa.snap.framework.datamodel.MetadataElement;
import org.esa.snap.util.SystemUtils;
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
public class L2aMetadata extends S2Metadata {

    private static final String PSD_STRING = "12";

    private MetadataElement metadataElement;
    protected Logger logger = SystemUtils.LOG;




    private List<Tile> tileList;
    private Collection<ImageInfo> imageList;
    private ProductCharacteristics productCharacteristics;

    public static L2aMetadata parseHeader(File file, S2Config config) throws JDOMException, IOException, JAXBException {
        return new L2aMetadata(new FileInputStream(file), file, file.getParent(), config);
    }

    public List<Tile> getTileList() {
        return tileList;
    }

    public Collection<ImageInfo> getImageList() {
        return imageList;
    }

    public ProductCharacteristics getProductCharacteristics() {
        return productCharacteristics;
    }


    public MetadataElement getMetadataElement() {
        return metadataElement;
    }

    private L2aMetadata(InputStream stream, File file, String parent, S2Config config) throws DataConversionException, JAXBException, FileNotFoundException {
        super(config, L2aMetadataProc.getJaxbContext(), PSD_STRING);

        try {
            Object userProductOrTile = updateAndUnmarshal(stream);

            if(userProductOrTile instanceof Level2A_User_Product) {
                initProduct(stream, file, parent, userProductOrTile);
            } else {
                initTile(stream, file, parent, userProductOrTile);
            }
        } catch (JAXBException | JDOMException | IOException e) {
            logger.severe(Utils.getStackTrace(e));
        }
    }

    private void initProduct(InputStream stream, File file, String parent, Object casted) throws IOException, JAXBException, JDOMException {
        Level2A_User_Product product = (Level2A_User_Product) casted;
        productCharacteristics = L2aMetadataProc.getProductOrganization(product);

        Collection<String> tileNames = L2aMetadataProc.getTiles(product);
        imageList = L2aMetadataProc.getImages(product);
        List<File> fullTileNamesList = new ArrayList<>();

        tileList = new ArrayList<>();

        for (String granuleName : tileNames) {
            S2GranuleDirFilename aGranuleDir = S2L2aGranuleDirFilename.create(granuleName);
            String theName = aGranuleDir.getMetadataFilename().name;

            File nestedGranuleMetadata = new File(parent, "GRANULE" + File.separator + granuleName + File.separator + theName);
            if (nestedGranuleMetadata.exists()) {
                fullTileNamesList.add(nestedGranuleMetadata);
            } else {
                String errorMessage = "Corrupted product: the file for the granule " + granuleName + " is missing";
                logger.log(Level.WARNING, errorMessage);
            }
        }

        for (File aGranuleMetadataFile : fullTileNamesList) {
            FileInputStream granuleStream = new FileInputStream(aGranuleMetadataFile);
            Level2A_Tile aTile = (Level2A_Tile) updateAndUnmarshal(granuleStream);

            Map<Integer, TileGeometry> geoms = L2aMetadataProc.getTileGeometries(aTile);

            Tile tile = new Tile(aTile.getGeneral_Info().getTILE_ID_2A().getValue());
            tile.setHorizontalCsCode(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_CODE());
            tile.setHorizontalCsName(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_NAME());

            tile.setTileGeometry10M(geoms.get(S2SpatialResolution.R10M.resolution));
            tile.setTileGeometry20M(geoms.get(S2SpatialResolution.R20M.resolution));
            tile.setTileGeometry60M(geoms.get(S2SpatialResolution.R60M.resolution));

            tile.setSunAnglesGrid(L2aMetadataProc.getSunGrid(aTile));
            tile.setViewingIncidenceAnglesGrids(L2aMetadataProc.getAnglesGrid(aTile));

            tileList.add(tile);
        }

        S2DatastripFilename stripName = L2aMetadataProc.getDatastrip(product);
        S2DatastripDirFilename dirStripName = L2aMetadataProc.getDatastripDir(product);

        File dataStripMetadata = new File(parent, "DATASTRIP" + File.separator + dirStripName.name + File.separator + stripName.name);

        metadataElement = new MetadataElement("root");
        MetadataElement userProduct = parseAll(new SAXBuilder().build(file).getRootElement());
        MetadataElement dataStrip = parseAll(new SAXBuilder().build(dataStripMetadata).getRootElement());
        metadataElement.addElement(userProduct);
        metadataElement.addElement(dataStrip);
        MetadataElement granulesMetaData = new MetadataElement("Granules");

        for (File aGranuleMetadataFile : fullTileNamesList) {
            MetadataElement aGranule = parseAll(new SAXBuilder().build(aGranuleMetadataFile).getRootElement());
            granulesMetaData.addElement(aGranule);
        }

        metadataElement.addElement(granulesMetaData);
    }

    private void initTile(InputStream stream, File file, String parent, Object casted) throws IOException, JAXBException, JDOMException {
        Level2A_Tile aTile = (Level2A_Tile) casted;

        {
            Map<Integer, TileGeometry> geoms = L2aMetadataProc.getTileGeometries(aTile);

            Tile tile = new Tile(aTile.getGeneral_Info().getTILE_ID_2A().getValue());
            tile.setHorizontalCsCode(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_CODE());
            tile.setHorizontalCsName(aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_NAME());

            tile.setTileGeometry10M(geoms.get(10));
            tile.setTileGeometry20M(geoms.get(20));
            tile.setTileGeometry60M(geoms.get(60));

            tile.setSunAnglesGrid(L2aMetadataProc.getSunGrid(aTile));
            tile.setViewingIncidenceAnglesGrids(L2aMetadataProc.getAnglesGrid(aTile));

            tileList.add(tile);
        }
    }
}
