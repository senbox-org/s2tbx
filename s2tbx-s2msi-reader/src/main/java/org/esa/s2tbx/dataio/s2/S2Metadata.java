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

package org.esa.s2tbx.dataio.s2;


import com.vividsolutions.jts.geom.Coordinate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.VirtualPath;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the Sentinel-2 MSI XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Nicolas Ducoin
 */
public abstract class S2Metadata {

    private List<MetadataElement> metadataElements;
    String format = null;

    private List<Tile> tileList;

    private S2Config config;

    private ProductCharacteristics productCharacteristics;

    protected HashMap<String, VirtualPath> resourceResolver;


    public S2Metadata(S2Config config) {
        this.config = config;
        this.metadataElements = new ArrayList<>();
        this.resourceResolver = new HashMap<>();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public S2Config getConfig() {
        return config;
    }

    public List<MetadataElement> getMetadataElements() {
        return metadataElements;
    }

    public Tile getTile(String tileID) throws IOException {
        for (Tile tile : tileList) {
            if (tile.getId() == tileID) {
                return tile;
            }
        }
        throw new IOException(String.format("No tile with id %s", tileID));
    }

    public List<Tile> getTileList() {
        return tileList;
    }

    public void resetTileList() {
        tileList = new ArrayList<>();
    }

    public void addTileToList(Tile tile) {
        tileList.add(tile);
    }

    public ProductCharacteristics getProductCharacteristics() {
        return productCharacteristics;
    }

    public void setProductCharacteristics(ProductCharacteristics productCharacteristics) {
        this.productCharacteristics = productCharacteristics;
    }

    public VirtualPath resolveResource(String identifier) {
        return resourceResolver.get(identifier);
    }

    /**
     * from the input stream, replace the psd number in the header to allow jaxb to find
     * xsd files. This allows to open product with psd different from the reference one
     * for small changes.
     */
    static InputStream changePSDIfRequired(InputStream xmlStream, String psdNumber) throws IOException {
        if (psdNumber==null) return xmlStream;

        InputStream updatedXmlStream;

        String xmlStreamAsString = IOUtils.toString(xmlStream);

        final String psd13String = "psd-" + psdNumber + ".sentinel2.eo.esa.int";
        if (!xmlStreamAsString.contains(psd13String)) {
            String regex = "psd-\\d{2,}.sentinel2.eo.esa.int";
            String updatedXmlStreamAsString =
                    xmlStreamAsString.replaceAll(
                            regex, psd13String);
            updatedXmlStream = IOUtils.toInputStream(updatedXmlStreamAsString, "UTF-8");
        } else {
            updatedXmlStream = IOUtils.toInputStream(xmlStreamAsString, "UTF-8");
        }

        return updatedXmlStream;
    }

   /* protected MetadataElement parseTree(Element element, MetadataElement mdParent, Set<String> excludes) {

        MetadataElement mdElement = new MetadataElement(element.getName());

        List attributes = element.getAttributes();
        for (Object a : attributes) {
            Attribute attribute = (Attribute) a;
            MetadataAttribute mdAttribute = new MetadataAttribute(attribute.getName().toUpperCase(), ProductData.createInstance(attribute.getValue()), true);
            mdElement.addAttribute(mdAttribute);
        }

        for (Object c : element.getChildren()) {
            Element child = (Element) c;
            String childName = child.getName();
            String childValue = child.getValue();
            if (!excludes.contains(childName)) {
                if (childValue != null && !childValue.isEmpty() && childName.equals(childName.toUpperCase())) {
                    MetadataAttribute mdAttribute = new MetadataAttribute(childName, ProductData.createInstance(childValue), true);
                    String unit = child.getAttributeValue("unit");
                    if (unit != null) {
                        mdAttribute.setUnit(unit);
                    }
                    mdElement.addAttribute(mdAttribute);
                } else {
                    parseTree(child, mdElement, excludes);
                }
            }
        }

        if (mdParent != null) {
            mdParent.addElement(mdElement);
        }

        return mdElement;
    }*/


    public static class Tile {
        private String id;
        private String detectorId;
        private String horizontalCsName;
        private String horizontalCsCode;
        private Map<S2SpatialResolution, TileGeometry> tileGeometries;
        private int anglesResolution;
        private AnglesGrid sunAnglesGrid;
        private AnglesGrid[] viewingIncidenceAnglesGrids;
        private MaskFilename[] maskFilenames;
        public List<Coordinate> corners;


        public Tile(String id) {
            this.id = id;
        }

        public Tile(String id, String detectorId) {
            this.id = id;
            this.detectorId = detectorId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHorizontalCsName() {
            return horizontalCsName;
        }

        public void setHorizontalCsName(String horizontalCsName) {
            this.horizontalCsName = horizontalCsName;
        }

        public String getHorizontalCsCode() {
            return horizontalCsCode;
        }

        public void setHorizontalCsCode(String horizontalCsCode) {
            this.horizontalCsCode = horizontalCsCode;
        }

        public AnglesGrid getSunAnglesGrid() {
            return sunAnglesGrid;
        }

        public void setSunAnglesGrid(AnglesGrid sunAnglesGrid) {
            this.sunAnglesGrid = sunAnglesGrid;
        }

        public AnglesGrid[] getViewingIncidenceAnglesGrids() {
            return viewingIncidenceAnglesGrids;
        }

        public void setViewingIncidenceAnglesGrids(AnglesGrid[] viewingIncidenceAnglesGrids) {
            this.viewingIncidenceAnglesGrids = viewingIncidenceAnglesGrids;
        }

        public int getAnglesResolution() {
            return anglesResolution;
        }

        public void setAnglesResolution(int anglesResolution) {
            this.anglesResolution = anglesResolution;
        }

        public String getDetectorId() {
            return detectorId;
        }

        public void setDetectorId(String detectorId) {
            this.detectorId = detectorId;
        }

        public MaskFilename[] getMaskFilenames() {
            return maskFilenames;
        }

        public void setMaskFilenames(MaskFilename[] maskFilenames) {
            this.maskFilenames = maskFilenames;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }

        public void setTileGeometries(Map<S2SpatialResolution, TileGeometry> tileGeometries) {
            this.tileGeometries = tileGeometries;
        }

        public TileGeometry getTileGeometry(S2SpatialResolution resolution) {
            return tileGeometries.get(resolution);
        }
    }

    public static class MaskFilename {
        String bandId;
        String type;
        VirtualPath path;

        public MaskFilename(String bandId, String type, VirtualPath path) {
            this.bandId = bandId;
            this.type = type;
            this.path = path;
        }

        public String getBandId() {
            return bandId;
        }

        public void setBandId(String bandId) {
            this.bandId = bandId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public VirtualPath getPath() {
            return path;
        }

        public void setPath(VirtualPath path) {
            this.path = path;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public static class AnglesGrid {
        private int bandId;
        private int detectorId;
        private float[][] zenith;
        private float[][] azimuth;

        public int getBandId() {
            return bandId;
        }

        public void setBandId(int bandId) {
            this.bandId = bandId;
        }

        public int getDetectorId() {
            return detectorId;
        }

        public void setDetectorId(int detectorId) {
            this.detectorId = detectorId;
        }

        public float[][] getZenith() {
            return zenith;
        }

        public void setZenith(float[][] zenith) {
            this.zenith = zenith;
        }

        public float[][] getAzimuth() {
            return azimuth;
        }

        public void setAzimuth(float[][] azimuth) {
            this.azimuth = azimuth;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public static class TileGeometry {
        private int numRows;
        private int numCols;
        private double upperLeftX;
        private double upperLeftY;
        private double xDim;
        private double yDim;
        private String detector;
        private Integer position;
        private int resolution;
        private int numRowsDetector;

        public int getNumRows() {
            return numRows;
        }

        public void setNumRows(int numRows) {
            this.numRows = numRows;
        }

        public int getNumCols() {
            return numCols;
        }

        public void setNumCols(int numCols) {
            this.numCols = numCols;
        }

        public double getUpperLeftX() {
            return upperLeftX;
        }

        public void setUpperLeftX(double upperLeftX) {
            this.upperLeftX = upperLeftX;
        }

        public double getUpperLeftY() {
            return upperLeftY;
        }

        public void setUpperLeftY(double upperLeftY) {
            this.upperLeftY = upperLeftY;
        }

        public double getxDim() {
            return xDim;
        }

        public void setxDim(double xDim) {
            this.xDim = xDim;
        }

        public double getyDim() {
            return yDim;
        }

        public void setyDim(double yDim) {
            this.yDim = yDim;
        }

        public String getDetector() {
            return detector;
        }

        public void setDetector(String detector) {
            this.detector = detector;
        }

        public Integer getPosition() {
            return position;
        }

        public void setPosition(Integer position) {
            this.position = position;
        }

        public int getResolution() {
            return resolution;
        }

        public void setResolution(int resolution) {
            this.resolution = resolution;
        }

        public int getNumRowsDetector() {
            return numRowsDetector;
        }

        public void setNumRowsDetector(int numRowsDetector) {
            this.numRowsDetector = numRowsDetector;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public static class ProductCharacteristics {
        private String spacecraft;
        private String datasetProductionDate;
        private String productStartTime;
        private String productStopTime;
        private String datatakeSensingStartTime;
        private String processingLevel;
        private S2BandInformation[] bandInformations;
        private String metaDataLevel;
        private double quantificationValue;
        private int psd;

        public int getPsd() {
            return psd;
        }

        public void setPsd(int psd) {
            this.psd = psd;
        }


        public String getDatatakeSensingStartTime () {
            return datatakeSensingStartTime;
        }

        public void setDatatakeSensingStartTime (String datatakeSensingStartTime) {
            this.datatakeSensingStartTime = datatakeSensingStartTime;
        }

        public String getSpacecraft() {
            return spacecraft;
        }

        public void setSpacecraft(String spacecraft) {
            this.spacecraft = spacecraft;
        }

        public String getDatasetProductionDate() {
            return datasetProductionDate;
        }

        public void setDatasetProductionDate(String datasetProductionDate) {
            this.datasetProductionDate = datasetProductionDate;
        }

        public String getProductStartTime() {
            return productStartTime;
        }

        public void setProductStartTime(String productStartTime) {
            this.productStartTime = productStartTime;
        }

        public String getProductStopTime() {
            return productStopTime;
        }

        public void setProductStopTime(String productStopTime) {
            this.productStopTime = productStopTime;
        }

        public String getProcessingLevel() {
            return processingLevel;
        }

        public void setProcessingLevel(String processingLevel) {
            this.processingLevel = processingLevel;
        }

        public S2BandInformation[] getBandInformations() {
            return bandInformations;
        }

        public void setBandInformations(S2BandInformation[] bandInformations) {
            this.bandInformations = bandInformations;
        }

        public void addBandInformation(S2BandInformation bandInformation) {
            ArrayList<S2BandInformation> newBandInformations = new ArrayList<>();
            for(int i = 0; i<this.getBandInformations().length ; i++) {
                newBandInformations.add(this.getBandInformations()[i]);
            }
            newBandInformations.add(bandInformation);
            this.setBandInformations((S2BandInformation[]) newBandInformations.toArray());
        }

        public String getMetaDataLevel() {
            return metaDataLevel;
        }

        public void setMetaDataLevel(String metaDataLevel) {
            this.metaDataLevel = metaDataLevel;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }


        public double getQuantificationValue() {
            return quantificationValue;
        }

        public void setQuantificationValue(double quantificationValue) {
            this.quantificationValue = quantificationValue;
        }
    }

    /**
     * Used for wrapping the angles read from the metadata
     * @param valuesZenith each string contains the zenith angles of a full row separated by commas
     * @param valuesAzimuth each string contains the azimuth angles of a full row separated by commas
     * @return
     */
    public static AnglesGrid wrapAngles (String[] valuesZenith, String[] valuesAzimuth) {
        S2Metadata.AnglesGrid anglesGrid = null;

        if(valuesAzimuth == null || valuesZenith == null) {
            return null;
        }
        int nRows = valuesZenith.length;
        int nCols = valuesZenith[0].split(" ").length;

        if(nRows != valuesAzimuth.length || nCols != valuesAzimuth[0].split(" ").length) {
            return null;
        }

        anglesGrid = new S2Metadata.AnglesGrid();
        anglesGrid.setAzimuth(new float[nRows][nCols]);
        anglesGrid.setZenith(new float[nRows][nCols]);

        for (int rowindex = 0; rowindex < nRows; rowindex++) {
            String[] zenithSplit = valuesZenith[rowindex].split(" ");
            String[] azimuthSplit = valuesAzimuth[rowindex].split(" ");
            if(zenithSplit == null || azimuthSplit == null || zenithSplit.length != nCols ||azimuthSplit.length != nCols) {
                SystemUtils.LOG.severe("zenith and azimuth array length differ in line " + rowindex + " - " + valuesZenith[rowindex] + " - " + valuesAzimuth[rowindex]);
                return null;
            }
            for (int colindex = 0; colindex < nCols; colindex++) {
                anglesGrid.getZenith()[rowindex][colindex] = parseFloat(zenithSplit[colindex]);
                anglesGrid.getAzimuth()[rowindex][colindex] = parseFloat(azimuthSplit[colindex]);
            }
        }
        return anglesGrid;
    }

    static float parseFloat(String s) {
        if ("INF".equals(s)) {
            return Float.POSITIVE_INFINITY;
        } else if ("-INF".equals(s)) {
            return Float.NEGATIVE_INFINITY;
        } else {
            return Float.parseFloat(s);
        }
    }

    public static AnglesGrid[] wrapStandardViewingAngles(MetadataElement tileAnglesMetadataElement) {
        ArrayList<AnglesGrid> anglesGrids = new ArrayList<>();
        for(MetadataElement viewingAnglesElement : tileAnglesMetadataElement.getElements()) {
            if (!viewingAnglesElement.getName().equals("Viewing_Incidence_Angles_Grids")) {
                continue;
            }
            MetadataElement azimuthElement = viewingAnglesElement.getElement("Azimuth");
            if(azimuthElement == null) continue;
            MetadataElement valuesAzimuthElement = azimuthElement.getElement("Values_List");
            if(valuesAzimuthElement == null) continue;
            MetadataAttribute[] azAnglesAttributes = valuesAzimuthElement.getAttributes();
            if(azAnglesAttributes == null) continue;

            MetadataElement zenithElement = viewingAnglesElement.getElement("Zenith");
            if(zenithElement == null) continue;
            MetadataElement valuesZenithElement = zenithElement.getElement("Values_List");
            if(valuesZenithElement == null) continue;
            MetadataAttribute[] zenAnglesAttributes = valuesZenithElement.getAttributes();
            if(zenAnglesAttributes == null) continue;

            int nRows = azAnglesAttributes.length;
            if(nRows != zenAnglesAttributes.length) {
                continue;
            }
            String[] azAnglesString = new String[nRows];
            String[] zenAnglesString = new String[nRows];
            for(int i = 0 ; i < nRows ; i++) {
                azAnglesString[i] = azAnglesAttributes[i].getData().toString();
                zenAnglesString[i] = zenAnglesAttributes[i].getData().toString();
            }
            AnglesGrid anglesGrid= S2Metadata.wrapAngles(zenAnglesString, azAnglesString);
            anglesGrid.setBandId(Integer.parseInt(viewingAnglesElement.getAttributeString("bandId")));
            anglesGrid.setDetectorId(Integer.parseInt(viewingAnglesElement.getAttributeString("detectorId")));
            anglesGrids.add(anglesGrid);
        }
        return anglesGrids.toArray(new AnglesGrid[anglesGrids.size()]);
    }

    public int getPsd() {
        return productCharacteristics.getPsd();
    }

    /**
     * Read the content of 'path' searching the string "psd-XX.sentinel2.eo.esa.int" and return the XX parsed to an integer.
     * @param path
     * @return the psd version number or 0 if a problem occurs while reading the file or the version is not found.
     */
    public static int getPSD(VirtualPath path){
        try (InputStream stream = /*new FileInputStream(path.toString())*/path.getInputStream()){
            //FileInputStream fileStream = new FileInputStream(path.toString());
            String xmlStreamAsString = IOUtils.toString(stream);
            String regex = "psd-\\d{2,}.sentinel2.eo.esa.int";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(xmlStreamAsString);
            if (m.find()) {
                int position = m.start();
                String psdNumber = xmlStreamAsString.substring(position+4,position+6);
                return Integer.parseInt(psdNumber);
            }
            else {
                return 0;
            }

        } catch (Exception e) {
            return 0;
        }
    }
}
