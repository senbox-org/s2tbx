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

package org.esa.s2tbx.dataio.s2.l1b.metadata;

import org.apache.commons.math3.util.Pair;
import org.esa.s2tbx.dataio.s2.*;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.l1b.L1bSceneDescription;
import org.esa.s2tbx.dataio.s2.l1b.Sentinel2L1BProductReader;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.snap.core.datamodel.MetadataElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.esa.s2tbx.dataio.s2.l1b.metadata.L1bMetadataProc.makeTileInformation;

/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Norman Fomferra
 */
public class L1bMetadata extends S2Metadata {

    private static final Logger logger = Logger.getLogger(L1bMetadata.class.getName());

    private final boolean foundProductMetadata;
    private final VirtualPath productMetadataPath;
    private final String granuleFolderName;

    public L1bMetadata(VirtualPath path, String granuleFolderName, S2Config config, boolean foundProductMetadata, INamingConvention namingConvention)
                       throws IOException, ParserConfigurationException, SAXException {

        super(config);

        this.foundProductMetadata = foundProductMetadata;
        this.productMetadataPath = path;
        this.granuleFolderName = granuleFolderName;

        resetTileList();

        if (foundProductMetadata) {
            initProduct(path, granuleFolderName, namingConvention);
        } else {
            initTile(path);
        }
    }

    public boolean isGranule() {
        return (this.granuleFolderName != null);
    }

    public String getGranuleFolderName() {
        return granuleFolderName;
    }

    public boolean isFoundProductMetadata() {
        return foundProductMetadata;
    }

    public VirtualPath getProductMetadataPath() {
        return productMetadataPath;
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
            initTile(granuleMetadataPath);
        }
    }

    private void initTile(VirtualPath path) throws IOException, ParserConfigurationException, SAXException {
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

    public List<L1bMetadata.Tile> computeTiles() {
        List<L1bMetadata.Tile> availableTiles = getTileList();
        List<L1bMetadata.Tile> tileList;
        if (isGranule()) {
            tileList = new ArrayList<>();
            for (L1bMetadata.Tile tile : availableTiles) {
                if (tile.getId().equalsIgnoreCase(getGranuleFolderName())) {
                    tileList.add(tile);
                }
            }
        } else {
            tileList = availableTiles;
        }
        return tileList;
    }

    public Map<String, Sentinel2L1BProductReader.L1BBandInfo> computeBandInfoByKey(List<L1bMetadata.Tile> tileList) {
        Map<String, Sentinel2L1BProductReader.L1BBandInfo> bandInfoByKey = new HashMap<>();
        VirtualPath productDir = getProductMetadataPath().getParent();
        ProductCharacteristics productCharacteristics = getProductCharacteristics();
        if (productCharacteristics.getBandInformations() == null) {
            logger.warning("There are no spectral information here !"); // fixme Look for optional info in schema
        } else {
            // order bands by physicalBand
            Map<String, S2BandInformation> sin = new HashMap<>();
            for (S2BandInformation bandInformation : productCharacteristics.getBandInformations()) {
                sin.put(bandInformation.getPhysicalBand(), bandInformation);
            }

            Map<Pair<String, String>, Map<String, VirtualPath>> detectorBandInfoMap = new HashMap<>();
            for (Tile tile : tileList) {
                S2L1BGranuleDirFilename gf = (S2L1BGranuleDirFilename) S2L1BGranuleDirFilename.create(tile.getId());
                if (gf == null) {
                    throw new NullPointerException("Product files don't match regular expressions.");
                }
                for (S2BandInformation bandInformation : productCharacteristics.getBandInformations()) {
                    String imageFileName = computeFileName(isFoundProductMetadata(), this, tile, bandInformation, gf);

                    logger.finer("Adding file " + imageFileName + " to band: " + bandInformation.getPhysicalBand() + ", and detector: " + gf.getDetectorId());

                    VirtualPath path = productDir.resolve(imageFileName);
                    if (path.exists()) {
                        Pair<String, String> key = new Pair<>(bandInformation.getPhysicalBand(), gf.getDetectorId());
                        Map<String, VirtualPath> pathMapper = detectorBandInfoMap.getOrDefault(key, new HashMap<>());
                        pathMapper.put(tile.getId(), path);
                        if (!detectorBandInfoMap.containsKey(key)) {
                            detectorBandInfoMap.put(key, pathMapper);
                        }
                    } else {
                        logger.warning(String.format("Warning: missing file %s\n", path));
                    }
                }
            }
            if (!detectorBandInfoMap.isEmpty()) {
                S2Config config = getConfig();
                for (Pair<String, String> key : detectorBandInfoMap.keySet()) {
                    Sentinel2L1BProductReader.L1BBandInfo tileBandInfo = createBandInfoFromHeaderInfo(key.getSecond(), sin.get(key.getFirst()), detectorBandInfoMap.get(key), config);
                    String keyMix = key.getSecond() + key.getFirst(); // composite band name : detector + band
                    bandInfoByKey.put(keyMix, tileBandInfo);
                }
            }
        }
        return bandInfoByKey;
    }

    public static Sentinel2L1BProductReader.L1BBandInfo createBandInfoFromHeaderInfo(String detector, S2BandInformation bandInformation, Map<String, VirtualPath> tilePathMap, S2Config config) {
        S2SpatialResolution spatialResolution = bandInformation.getResolution();
        return new Sentinel2L1BProductReader.L1BBandInfo(tilePathMap, detector, bandInformation, config.getTileLayout(spatialResolution.resolution));
    }

    private static String computeFileName(boolean foundProductMetadata, L1bMetadata metadataHeader, Tile tile, S2BandInformation bandInformation, S2L1BGranuleDirFilename gf) {
        String imageFileName;
        if (foundProductMetadata) {
            imageFileName = String.format("GRANULE%s%s%s%s", File.separator, metadataHeader.resolveResource(tile.getId()).getFileName().toString(),
                    File.separator,
                    bandInformation.getImageFileTemplate()
                            .replace("{{TILENUMBER}}", gf.getTileID())
                            .replace("{{MISSION_ID}}", gf.missionID)
                            .replace("{{SITECENTRE}}", gf.siteCentre)
                            .replace("{{CREATIONDATE}}", gf.creationDate)
                            .replace("{{STARTDATE}}", gf.startDate)
                            .replace("{{DETECTOR}}", gf.detectorId)
                            .replace("{{RESOLUTION}}", String.format("%d", bandInformation.getResolution().resolution)));

        } else {
            imageFileName = bandInformation.getImageFileTemplate()
                    .replace("{{TILENUMBER}}", gf.getTileID())
                    .replace("{{MISSION_ID}}", gf.missionID)
                    .replace("{{SITECENTRE}}", gf.siteCentre)
                    .replace("{{CREATIONDATE}}", gf.creationDate)
                    .replace("{{STARTDATE}}", gf.startDate)
                    .replace("{{DETECTOR}}", gf.detectorId)
                    .replace("{{RESOLUTION}}", String.format("%d", bandInformation.getResolution().resolution));

        }
        return imageFileName;
    }

}
