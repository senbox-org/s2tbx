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

package org.esa.s2tbx.dataio.s2.l1c;

import https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_tile_metadata.Level1C_Tile;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1c.Level1C_User_Product;
import jp2.TileLayout;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.l1c.filepaterns.S2L1CGranuleDirFilename;
import org.esa.snap.framework.datamodel.MetadataElement;
import org.esa.snap.util.SystemUtils;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Norman Fomferra
 */
public class L1cMetadata extends S2Metadata {

    private MetadataElement metadataElement;
    protected Logger logger = SystemUtils.LOG;


    static class Tile {
        String id;
        String horizontalCsName;
        String horizontalCsCode;
        TileGeometry tileGeometry10M;
        TileGeometry tileGeometry20M;
        TileGeometry tileGeometry60M;
        AnglesGrid sunAnglesGrid;
        AnglesGrid[] viewingIncidenceAnglesGrids;
        MaskFilename[] maskFilenames;

        public enum idGeom {G10M, G20M, G60M}

        public Tile(String id) {
            this.id = id;
            tileGeometry10M = new TileGeometry();
            tileGeometry20M = new TileGeometry();
            tileGeometry60M = new TileGeometry();
        }

        public TileGeometry getGeometry(idGeom index) {
            switch (index) {
                case G10M:
                    return tileGeometry10M;
                case G20M:
                    return tileGeometry20M;
                case G60M:
                    return tileGeometry60M;
                default:
                    throw new IllegalStateException();
            }
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    static class MaskFilename
    {
        String bandId;
        String type;
        File name;

        public MaskFilename(String bandId, String type, File name) {
            this.bandId = bandId;
            this.type = type;
            this.name = name;
        }

        public File getName() {
            return name;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    static class AnglesGrid {
        int bandId;
        int detectorId;
        float[][] zenith;
        float[][] azimuth;

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    static class TileGeometry {
        int numRows;
        int numCols;
        double upperLeftX;
        double upperLeftY;
        double xDim;
        double yDim;

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public static class ProductCharacteristics
    {
        String spacecraft;
        String datasetProductionDate;
        String processingLevel;
        SpectralInformation[] bandInformations;


        public String getSpacecraft() {
            return spacecraft;
        }

        public String getDatasetProductionDate() {
            return datasetProductionDate;
        }

        public String getProcessingLevel() {
            return processingLevel;
        }

        public SpectralInformation[] getBandInformations() {
            return bandInformations;
        }


        public String getMetaDataLevel() {
            return metaDataLevel;
        }

        public void setMetaDataLevel(String metaDataLevel) {
            this.metaDataLevel = metaDataLevel;
        }

        private String metaDataLevel;

        public ProductCharacteristics() {
            bandInformations = new SpectralInformation[]{};
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public static class SpectralInformation {
        int bandId;
        String physicalBand;
        int resolution;
        double wavelenghtMin;
        double wavelenghtMax;
        double wavelenghtCentral;
        double spectralResponseStep;

        double[] spectralResponseValues;

        public SpectralInformation() {
            spectralResponseValues = new double[]{};
        }

        public SpectralInformation(String physicalBand, int bandId, int resolution)
        {
            this.physicalBand = physicalBand;
            this.bandId = bandId;
            this.resolution = resolution;
            spectralResponseValues = new double[]{};
        }



        public double[] getSpectralResponseValues() {
            return spectralResponseValues;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    static class QuicklookDescriptor {
        int imageNCols;
        int imageNRows;
        Histogram[] histogramList;

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    static class Histogram {
        public int bandId;
        int[] values;
        int step;
        double min;
        double max;
        double mean;
        double stdDev;

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    private List<Tile> tileList;
    private Map<String, List<Tile>> allTileLists; // Key is UTM zone, values are the tiles associated to a UTM zone

    private ProductCharacteristics productCharacteristics;

    public static L1cMetadata parseHeader(File file, TileLayout[] tileLayouts) throws JDOMException, IOException, JAXBException {
        return new L1cMetadata(new FileInputStream(file), file, file.getParent(), tileLayouts);
    }

    public List<Tile> getTileList() {
        return tileList;
    }

    public Set<String> getUTMZonesList() {
        return allTileLists.keySet();
    }

    public List<Tile> getTileList(String utmZone) {
        return allTileLists.get(utmZone);
    }

    public ProductCharacteristics getProductCharacteristics() {
        return productCharacteristics;
    }


    public MetadataElement getMetadataElement() {
        return metadataElement;
    }

    private L1cMetadata(InputStream stream, File file, String parent, TileLayout[] tileLayouts) throws JDOMException, JAXBException, FileNotFoundException {
        super(tileLayouts, L1cMetadataProc.getJaxbContext());

        try {
            Object userProduct = updateAndUnmarshal(stream);

            if(userProduct instanceof Level1C_User_Product)
            {
                initProduct(stream, file, parent, userProduct);
            }
            else
            {
                initTile(stream, file, parent, userProduct);
            }
        } catch (UnmarshalException|JDOMException e) {
            logger.severe(String.format("Product is not conform to PSD: ", e.getMessage()));
            throw e;
        } catch (JAXBException e) {
            logger.severe(Utils.getStackTrace(e));
        } catch (FileNotFoundException e) {
            logger.severe(Utils.getStackTrace(e));
        }  catch (IOException e) {
            logger.severe(Utils.getStackTrace(e));
        }
    }

    private void initProduct(InputStream stream, File file, String parent, Object casted) throws IOException, JAXBException, JDOMException {
        Level1C_User_Product product = (Level1C_User_Product) casted;
        productCharacteristics = L1cMetadataProc.getProductOrganization(product);

        Collection<String> tileNames = L1cMetadataProc.getTiles(product);
        List<File> fullTileNamesList = new ArrayList<File>();


        // todo populate allTileLists
        allTileLists = new HashMap<String, List<Tile>>();

        tileList = new ArrayList<Tile>();

        for (String granuleName : tileNames) {
            FileInputStream fi = (FileInputStream) stream;
            File nestedMetadata = new File(parent, "GRANULE" + File.separator + granuleName);

            S2L1CGranuleDirFilename aGranuleDir = S2L1CGranuleDirFilename.create(granuleName);
            String theName = aGranuleDir.getMetadataFilename().name;

            File nestedGranuleMetadata = new File(parent, "GRANULE" + File.separator + granuleName + File.separator + theName);
            if (nestedGranuleMetadata.exists()) {
                fullTileNamesList.add(nestedGranuleMetadata);
            } else {
                String errorMessage = "Corrupted product: the file for the granule " + granuleName + " is missing";
                logger.log(Level.WARNING, errorMessage);
            }
        }

        Map<String, Counter> counters = new HashMap<String, Counter>();

        for (File aGranuleMetadataFile : fullTileNamesList) {
            FileInputStream granuleStream = new FileInputStream(aGranuleMetadataFile);
            Level1C_Tile aTile = (Level1C_Tile) updateAndUnmarshal(granuleStream);
            Map<Integer, TileGeometry> geoms = L1cMetadataProc.getTileGeometries(aTile);

            Tile t = new Tile(aTile.getGeneral_Info().getTILE_ID().getValue());
            t.horizontalCsCode = aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_CODE();
            t.horizontalCsName = aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_NAME();

            // counting tiles by UTM zones
            String key = t.horizontalCsCode;
            if (counters.containsKey(key)) {
                counters.get(key).increment();
            } else {
                counters.put(key, new Counter(key));
                counters.get(key).increment();
            }

            t.tileGeometry10M = geoms.get(10);
            t.tileGeometry20M = geoms.get(20);
            t.tileGeometry60M = geoms.get(60);

            t.sunAnglesGrid = L1cMetadataProc.getSunGrid(aTile);
            t.viewingIncidenceAnglesGrids = L1cMetadataProc.getAnglesGrid(aTile);

            t.maskFilenames = L1cMetadataProc.getMasks(aTile, aGranuleMetadataFile);

            tileList.add(t);
        }


        for(String key: counters.keySet())
        {
            List<L1cMetadata.Tile> aUTMZone = tileList.stream().filter(i -> i.horizontalCsCode.equals(key)).collect(Collectors.toList());
            allTileLists.put(key, aUTMZone);
        }

        // if it's a multi-UTM product, we create the product using only the main UTM zone (the one with more tiles)
        // todo make sure that tileList and allTileLists.get(maximus) are the same before removing this code

        //if (counters.values().size() > 1) {
        //    Counter maximus = Collections.max(counters.values());
        //    logger.info(String.format("There are %d UTM zones in this product, the main zone is [%s]", counters.size(), maximus.getName()));
        //    tileList = tileList.stream().filter(i -> i.horizontalCsCode.equals(maximus.getName())).collect(Collectors.toList());
        //}

        S2DatastripFilename stripName = L1cMetadataProc.getDatastrip(product);
        S2DatastripDirFilename dirStripName = L1cMetadataProc.getDatastripDir(product);

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
        Level1C_Tile product = (Level1C_Tile) casted;
        productCharacteristics = new L1cMetadata.ProductCharacteristics();

        tileList = new ArrayList<>();
        allTileLists = new HashMap<>();

        // todo move this code to a common function
        {
            Level1C_Tile aTile = product;
            Map<Integer, TileGeometry> geoms = L1cMetadataProc.getTileGeometries(aTile);

            Tile t = new Tile(aTile.getGeneral_Info().getTILE_ID().getValue());
            t.horizontalCsCode = aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_CODE();
            t.horizontalCsName = aTile.getGeometric_Info().getTile_Geocoding().getHORIZONTAL_CS_NAME();

            t.tileGeometry10M = geoms.get(10);
            t.tileGeometry20M = geoms.get(20);
            t.tileGeometry60M = geoms.get(60);

            t.sunAnglesGrid = L1cMetadataProc.getSunGrid(aTile);
            t.viewingIncidenceAnglesGrids = L1cMetadataProc.getAnglesGrid(aTile);

            L1cMetadataProc.getMasks(aTile, file);
            t.maskFilenames = L1cMetadataProc.getMasks(aTile, file);

            tileList.add(t);

            allTileLists.put(t.horizontalCsCode, tileList);
        }
    }
}
