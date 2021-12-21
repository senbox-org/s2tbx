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


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.s2.ortho.Sentinel2OrthoProductReader;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.util.SystemUtils;
import org.locationtech.jts.geom.Coordinate;

import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.image.DataBuffer.TYPE_FLOAT;

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

    private final S2Config config;

    private ProductCharacteristics productCharacteristics;

    protected final HashMap<String, VirtualPath> resourceResolver;

    protected S2Metadata(S2Config config) {
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

    public ArrayList<String> getOrderedTileList() {
        ArrayList<String> orderedTileList = new ArrayList<>(17);
        for(Tile tile : this.getTileList()) {
            orderedTileList.add(tile.getId());
        }
        java.util.Collections.sort(orderedTileList);
        return orderedTileList;
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

        public AnglesGrid getViewingIncidenceAnglesGrids(int bandId,int detectorId) {
            if(viewingIncidenceAnglesGrids == null) {
                return null;
            }

            int width = 23;
            int height = 23;
            for(int i=0;i<viewingIncidenceAnglesGrids.length;i++) {
                width = viewingIncidenceAnglesGrids[i].azimuth.length;
                height = viewingIncidenceAnglesGrids[i].azimuth[0].length;
                if(viewingIncidenceAnglesGrids[i].bandId == bandId && viewingIncidenceAnglesGrids[i].detectorId == detectorId) {
                    return viewingIncidenceAnglesGrids[i];
                }
            }

            AnglesGrid emptyGrid = new AnglesGrid();
            emptyGrid.setBandId(bandId);
            emptyGrid.setDetectorId(detectorId);
            float[][] azimuth = new float[width][height];
            float[][] zenith = new float[width][height];
            for(int i= 0;i<width;i++) {
                for(int j= 0;j<height;j++) {
                    azimuth[i][j] = Float.NaN;
                    zenith[i][j] = Float.NaN;
                }
            }
            emptyGrid.setAzimuth(azimuth);
            emptyGrid.setZenith(zenith);
            return emptyGrid;
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

        public int getWidth() {
            if(zenith == null || zenith[0] == null) {
                return 0;
            }
            int zenithWidth = zenith[0].length;

            if(azimuth == null || azimuth[0] == null) {
                return 0;
            }
            int azimuthWidth = azimuth[0].length;
            if(azimuthWidth != zenithWidth) {
                return 0;
            }
            return azimuthWidth;
        }

        public int getHeight() {
            if(zenith == null) {
                return 0;
            }
            int zenithHeight = zenith.length;
            if(azimuth == null) {
                return 0;
            }
            int azimuthHeight = azimuth.length;
            if(azimuthHeight != zenithHeight) {
                return 0;
            }
            return zenithHeight;
        }

        public float[] getZenithArray() {
            int gridWidth = getWidth();
            int gridHeight = getHeight();
            if(gridWidth == 0 || gridHeight == 0) {
                return null;
            }
            float[] viewingZeniths = new float[gridWidth * gridHeight];
            Arrays.fill(viewingZeniths, Float.NaN);
            for (int y = 0; y < gridHeight; y++) {
                for (int x = 0; x < gridWidth; x++) {
                    final int index = y * gridWidth + x;
                    if (isValidAngle(zenith[y][x])) {
                        viewingZeniths[index] = zenith[y][x];
                    }
                }
            }
            return viewingZeniths;
        }

        public float[] getAzimuthArray() {
            int gridWidth = getWidth();
            int gridHeight = getHeight();
            if(gridWidth == 0 || gridHeight == 0) {
                return null;
            }
            float[] viewingAzimuths = new float[gridWidth * gridHeight];
            Arrays.fill(viewingAzimuths, Float.NaN);
            for (int y = 0; y < gridHeight; y++) {
                for (int x = 0; x < gridWidth; x++) {
                    final int index = y * gridWidth + x;
                    if (isValidAngle(azimuth[y][x])) {
                        viewingAzimuths[index] = azimuth[y][x];
                    }
                }
            }
            return viewingAzimuths;
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
        private Integer processingBaseline;
        private String missionID;
        private S2BandInformation[] bandInformations;
        private String metaDataLevel;
        private double quantificationValue;
        private int psd;
        private String[] offsets;

        public String[] getOffsetList() {
            return offsets;
        }

        public void setOffsetList(String[] offsets) {
            this.offsets = offsets;
        }
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

        public void setProcessingBaseline(Integer processingBaseline) {
            this.processingBaseline = processingBaseline;
        }

        public Integer getProcessingBaseline() {
            return processingBaseline;
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
            //Compute index for adding in order by band and by detector
            int index = 0;
            if(anglesGrids.size() > 0) {
                int currentBand = anglesGrid.bandId;
                int currentDetector = anglesGrid.detectorId;
                while(index < anglesGrids.size() && anglesGrids.get(index).getBandId() < currentBand) {
                    index++;
                }
                while(index < anglesGrids.size() && anglesGrids.get(index).getBandId() == currentBand && anglesGrids.get(index).getDetectorId() < currentDetector) {
                    index++;
                }
            }

            anglesGrids.add(index,anglesGrid);
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
    public static int getPSD(VirtualPath path) {
        int bufferSizeInBytes = 5 * 1024;
        try (InputStream inputStream = path.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.defaultCharset());
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader, bufferSizeInBytes)) {

            String regex = "psd-\\d{2,}.sentinel2.eo.esa.int";
            Pattern p = Pattern.compile(regex);
            StringBuilder str = new StringBuilder();
            char[] buffer = new char[bufferSizeInBytes];
            int characterReadNow;
            while ((characterReadNow = bufferedReader.read(buffer)) >= 0) {
                str.append(buffer, 0, characterReadNow);

                Matcher m = p.matcher(str);
                if (m.find()) {
                    int position = m.start();
                    String psdNumber = str.substring(position+4, position+6);
                    return Integer.parseInt(psdNumber);
                }
            }

            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Read the content of 'path' searching the string "psd-XX.sentinel2.eo.esa.int" and return an integer.
     * Checks also some items in file to be able to distinguish PSD subversion 14.x and convert to an integer 14x
     * @param path
     * @return the psd version number or 0 if a problem occurs while reading the file or the version is not found.
     */
    public static int getFullPSDversion(VirtualPath path){
        int psd=0;
        int processingBaseline = getProcessingBaseline(path);
        try (InputStream stream = path.getInputStream()){

            String xmlStreamAsString = IOUtils.toString(stream);
            String aux = xmlStreamAsString;
            String regex = "psd-\\d{2,}.sentinel2.eo.esa.int";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(xmlStreamAsString);
            if (m.find()) {
                int position = m.start();
                String psdNumber = xmlStreamAsString.substring(position+4,position+6);

                //Check specific 14.3 psd, not possible to distinguish in 'regex'
                if(processingBaseline>1000) {
                    SystemUtils.LOG.warning("WARNING: the processing baseline is inconsistent. Please, check your product if it is not a demonstration product.");
                    
                }

                if(Integer.parseInt(psdNumber) == 14 && processingBaseline>399)
                {
                    psd = 148;
                } else if(Integer.parseInt(psdNumber) == 14 && !aux.contains("L2A_Product_Info") && !aux.contains("TILE_ID_2A")) {
                    psd = 143;
                }else{
                    psd = Integer.parseInt(psdNumber);
                }

                
            } else {
                psd = 0;
            }

        } catch (Exception e) {
            psd = 0;
        }
        return psd;
    }

    /**
     * Read the content of 'path' searching the processing baseline and return the XX parsed to an integer.
     * @param path
     * @return the processing baseline version number or 0 if an error
     */
    public static int getProcessingBaseline(VirtualPath path) {
        int bufferSizeInBytes = 5 * 1024;
        try (InputStream inputStream = path.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.defaultCharset());
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader, bufferSizeInBytes)) {

            String regex = "<PROCESSING_BASELINE>([.|0-9]{5})</PROCESSING_BASELINE>";
            Pattern p = Pattern.compile(regex);
            StringBuilder str = new StringBuilder();
            char[] buffer = new char[bufferSizeInBytes];
            int characterReadNow;
            while ((characterReadNow = bufferedReader.read(buffer)) >= 0) {
                str.append(buffer, 0, characterReadNow);
                Matcher m = p.matcher(str);
                if (m.find()) {
                    int position = m.start();
                    String psdNumber = str.substring(position+21, position+26);
                    psdNumber=psdNumber.replace(".", "");
                    return Integer.parseInt(psdNumber);
                }
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get the angles grid of one detector (it mosaics the different tiles)
     * @param bandId
     * @param detectorId
     * @return S2BandAnglesGridByDetector[2] -> [0]: Zenith     [1]: Azimuth
     */
    public S2BandAnglesGridByDetector[] getAnglesGridByDetector(int bandId, int detectorId) {
        HashMap<Tile, S2BandAnglesGrid> zenithAnglesGridsMap = new HashMap<>();
        HashMap<Tile, S2BandAnglesGrid> azimuthAnglesGridsMap = new HashMap<>();
        for (S2Metadata.Tile tile : tileList) {
            S2BandAnglesGridByDetector[] bandAnglesGridByDetector = getAnglesGridByDetectorByTile(tile.getId(),bandId,detectorId);
            if (bandAnglesGridByDetector != null) {
                zenithAnglesGridsMap.put(tile, bandAnglesGridByDetector[0]);
                azimuthAnglesGridsMap.put(tile, bandAnglesGridByDetector[1]);
            }
        }

        S2BandAnglesGrid zenithAngleGrid = getMosaicS2BandAnglesGrid(zenithAnglesGridsMap);
        S2BandAnglesGrid azimuthAngleGrid = getMosaicS2BandAnglesGrid(azimuthAnglesGridsMap);

        S2BandAnglesGridByDetector[] bandAnglesGridByDetector = new S2BandAnglesGridByDetector[2];
        bandAnglesGridByDetector[0] = new S2BandAnglesGridByDetector(Sentinel2OrthoProductReader.VIEW_ZENITH_PREFIX, S2BandConstants.getBand(bandId), detectorId, zenithAngleGrid.getWidth(), zenithAngleGrid.getHeight(), zenithAngleGrid.originX, zenithAngleGrid.originY, zenithAngleGrid.getResolutionX(), zenithAngleGrid.getResolutionY(), zenithAngleGrid.getData());
        bandAnglesGridByDetector[1] = new S2BandAnglesGridByDetector(Sentinel2OrthoProductReader.VIEW_AZIMUTH_PREFIX, S2BandConstants.getBand(bandId), detectorId, azimuthAngleGrid.getWidth(), azimuthAngleGrid.getHeight(), azimuthAngleGrid.originX, azimuthAngleGrid.originY, azimuthAngleGrid.getResolutionX(), azimuthAngleGrid.getResolutionY(), azimuthAngleGrid.getData());

        return bandAnglesGridByDetector;
    }

    /**
     *
     * @param tileId
     * @param bandId
     * @param detectorId
     * @return S2BandAnglesGridByDetector[2] -> [0]: Zenith     [1]: Azimuth
     */
    public S2BandAnglesGridByDetector[] getAnglesGridByDetectorByTile(String tileId, int bandId, int detectorId) {
        try {
            Tile tile = getTile(tileId);
            int resolution = tile.getAnglesResolution();
            int gridHeight = tile.getSunAnglesGrid().getHeight();
            int gridWidth = tile.getSunAnglesGrid().getWidth();

            S2Metadata.AnglesGrid viewingIncidenceAnglesGrid = tile.getViewingIncidenceAnglesGrids(bandId, detectorId);
            if (viewingIncidenceAnglesGrid == null) {
                return null;
            }

            float[] viewingZeniths = viewingIncidenceAnglesGrid.getZenithArray();
            float[] viewingAzimuths = viewingIncidenceAnglesGrid.getAzimuthArray();


            S2BandAnglesGridByDetector[] bandAnglesGridByDetector = new S2BandAnglesGridByDetector[2];
            bandAnglesGridByDetector[0] = new S2BandAnglesGridByDetector(Sentinel2OrthoProductReader.VIEW_ZENITH_PREFIX, S2BandConstants.getBand(bandId), detectorId, gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, viewingZeniths);
            bandAnglesGridByDetector[1] = new S2BandAnglesGridByDetector(Sentinel2OrthoProductReader.VIEW_AZIMUTH_PREFIX, S2BandConstants.getBand(bandId), detectorId, gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, viewingAzimuths);

            return bandAnglesGridByDetector;
        } catch (IOException e) {
            //do nothing
        }
        return null;
    }

    /**
     *
     * @param tileId
     * @return [0]: Zenith     [1]: Azimuth
     */
    public S2BandAnglesGrid[] getSunAnglesGridByTile(String tileId) {
        try {
            Tile tile = getTile(tileId);
            int resolution = tile.getAnglesResolution();


            S2Metadata.AnglesGrid sunAnglesGrid = tile.getSunAnglesGrid();
            if (sunAnglesGrid == null) {
                return null;
            }
            int gridHeight = tile.getSunAnglesGrid().getHeight();
            int gridWidth = tile.getSunAnglesGrid().getWidth();

            float[] sunZeniths = sunAnglesGrid.getZenithArray();
            float[] sunAzimuths = sunAnglesGrid.getAzimuthArray();


            S2BandAnglesGrid[] sunBandAnglesGrid = new S2BandAnglesGrid[2];
            sunBandAnglesGrid[0] = new S2BandAnglesGrid(Sentinel2OrthoProductReader.SUN_ZENITH_PREFIX, null, gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, sunZeniths);
            sunBandAnglesGrid[1] = new S2BandAnglesGrid(Sentinel2OrthoProductReader.SUN_AZIMUTH_PREFIX, null, gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(), (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution, sunAzimuths);

            return sunBandAnglesGrid;
        } catch (IOException e) {
            //do nothing
        }
        return null;
    }

    /**
     *
     * @return [0]: Zenith     [1]: Azimuth
     */
    public S2BandAnglesGrid[] getSunAnglesGrid() {

        HashMap<Tile, S2BandAnglesGrid> zenithAnglesGridsMap = new HashMap<>();
        HashMap<Tile, S2BandAnglesGrid> azimuthAnglesGridsMap = new HashMap<>();
        for (S2Metadata.Tile tile : tileList) {
            S2BandAnglesGrid[] bandAnglesGrid = getSunAnglesGridByTile(tile.getId());
            if (bandAnglesGrid != null) {
                zenithAnglesGridsMap.put(tile, bandAnglesGrid[0]);
                azimuthAnglesGridsMap.put(tile, bandAnglesGrid[1]);
            }
        }

        S2BandAnglesGrid zenithAngleGrid = getMosaicS2BandAnglesGrid(zenithAnglesGridsMap);
        S2BandAnglesGrid azimuthAngleGrid = getMosaicS2BandAnglesGrid(azimuthAnglesGridsMap);

        S2BandAnglesGrid[] bandAnglesGrid = new S2BandAnglesGrid[2];
        bandAnglesGrid[0] = new S2BandAnglesGrid(Sentinel2OrthoProductReader.SUN_ZENITH_PREFIX, null, zenithAngleGrid.getWidth(), zenithAngleGrid.getHeight(), zenithAngleGrid.originX, zenithAngleGrid.originY, zenithAngleGrid.getResolutionX(), zenithAngleGrid.getResolutionY(), zenithAngleGrid.getData());
        bandAnglesGrid[1] = new S2BandAnglesGrid(Sentinel2OrthoProductReader.SUN_AZIMUTH_PREFIX, null, azimuthAngleGrid.getWidth(), azimuthAngleGrid.getHeight(), azimuthAngleGrid.originX, azimuthAngleGrid.originY, azimuthAngleGrid.getResolutionX(), azimuthAngleGrid.getResolutionY(), azimuthAngleGrid.getData());

        return bandAnglesGrid;
    }

    /**
     * Mosaic the angles grids.
     * It uses INTERP_NEAREST instead of BILINEAR to avoid removing some rows/columns
     * If the rows/columns are removed, we will have to extrapolate more and the error will be probably higher
     * @param anglesGridsMap
     * @return
     */
    private S2BandAnglesGrid getMosaicS2BandAnglesGrid(HashMap<S2Metadata.Tile, S2BandAnglesGrid> anglesGridsMap) {
        float masterOriginX = Float.MAX_VALUE, masterOriginY = -Float.MAX_VALUE;

        int widthAnglesTile = 0;
        int heightAnglesTile = 0;

        //angle band resolution
        float resX = 0;
        float resY = 0;

        TileGeometry tileGeometry = null;

        for(Map.Entry<S2Metadata.Tile, S2BandAnglesGrid> entry : anglesGridsMap.entrySet()) {
            S2BandAnglesGrid s2BandAnglesGrid = entry.getValue();
            S2Metadata.Tile tile = entry.getKey();

            if (entry.getValue() != null) {
                widthAnglesTile = s2BandAnglesGrid.getWidth();
                heightAnglesTile = s2BandAnglesGrid.getHeight();
                resX = s2BandAnglesGrid.getResolutionX();
                resY = s2BandAnglesGrid.getResolutionY();
                tileGeometry = tile.getTileGeometry(S2SpatialResolution.R10M);
                if (masterOriginX > s2BandAnglesGrid.originX) masterOriginX = s2BandAnglesGrid.originX;
                if (masterOriginY < s2BandAnglesGrid.originY) masterOriginY = s2BandAnglesGrid.originY;
            }
        }


        if (masterOriginX == Float.MAX_VALUE || masterOriginY == -Float.MAX_VALUE || resX == 0 || resY == 0 || widthAnglesTile == 0 || heightAnglesTile == 0) {
            return null;
        }

        //Mosaic of planar image
        ArrayList<PlanarImage> tileImages = new ArrayList<>();

        for (String tileId : getOrderedTileList()) {

            int[] bandOffsets = {0};
            SampleModel sampleModel = new PixelInterleavedSampleModel(TYPE_FLOAT, widthAnglesTile, heightAnglesTile, 1, widthAnglesTile, bandOffsets);
            ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.TRANSLUCENT, TYPE_FLOAT);
            PlanarImage opImage;

            DataBuffer buffer = new DataBufferFloat(widthAnglesTile*heightAnglesTile*1);
            // Wrap it in a writable raster
            WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);
            S2BandAnglesGrid bandAnglesGrid = null;
            try {
                bandAnglesGrid = anglesGridsMap.get(getTile(tileId));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bandAnglesGrid == null) {
                continue;
            }


            raster.setPixels(0, 0, widthAnglesTile, heightAnglesTile, bandAnglesGrid.getData());


            // And finally create an image with this raster
            BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
            opImage = PlanarImage.wrapRenderedImage(image);

            // Translate tile
            float transX=(bandAnglesGrid.originX-masterOriginX)/bandAnglesGrid.getResolutionX();
            float transY=(bandAnglesGrid.originY-masterOriginY)/bandAnglesGrid.getResolutionY();

            RenderingHints hints=new RenderingHints(JAI.KEY_TILE_CACHE, JAI.getDefaultInstance().getTileCache());
            hints.put(JAI.KEY_BORDER_EXTENDER, new BorderExtenderConstant(new double[]{Double.NaN}));
            opImage = TranslateDescriptor.create(opImage,
                                                       transX,
                                                       -transY,
                                                       Interpolation.getInstance(Interpolation.INTERP_NEAREST), hints);
            //Use nearestNeighbour because bilinear delete the borders
            tileImages.add(opImage);
        }

        ImageLayout imageLayout = new ImageLayout();
        imageLayout.setMinX(0);
        imageLayout.setMinY(0);
        imageLayout.setTileWidth(S2Config.DEFAULT_JAI_TILE_SIZE);
        imageLayout.setTileHeight(S2Config.DEFAULT_JAI_TILE_SIZE);
        imageLayout.setTileGridXOffset(0);
        imageLayout.setTileGridYOffset(0);

        RenderingHints hints = new RenderingHints(JAI.KEY_TILE_CACHE,JAI.getDefaultInstance().getTileCache());
        hints.put(JAI.KEY_IMAGE_LAYOUT, imageLayout);

        RenderedOp mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                                                            MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                            null, null, new double[][] {{-1.0}}, new double[]{S2Config.FILL_CODE_MOSAIC_ANGLES},
                                                            hints);

        ImageLayout imageLayout2 = new ImageLayout();
        imageLayout2.setMinX(0);
        imageLayout2.setMinY(0);
        imageLayout2.setTileWidth(mosaicOp.getWidth());
        imageLayout2.setTileHeight(mosaicOp.getHeight());
        imageLayout2.setTileGridXOffset(0);
        imageLayout2.setTileGridYOffset(0);

        RenderingHints hints2 = new RenderingHints(JAI.KEY_TILE_CACHE,JAI.getDefaultInstance().getTileCache());
        hints.put(JAI.KEY_IMAGE_LAYOUT, imageLayout2);
        mosaicOp.setRenderingHints(hints2);

        DataBuffer dataBuffer = mosaicOp.getData().getDataBuffer();
        float[] array = new float[mosaicOp.getWidth()* mosaicOp.getHeight()];
        for (int i = 0; i< mosaicOp.getWidth()* mosaicOp.getHeight(); i++) {
            array[i] = dataBuffer.getElemFloat(i);
        }

        S2BandAnglesGrid bandAnglesGrid = new S2BandAnglesGrid("mosaic", null, mosaicOp.getWidth(), mosaicOp.getHeight(), masterOriginX, masterOriginY, resX, resY, array);
        return bandAnglesGrid;
    }

    private static boolean isValidAngle(float value) {
        return !Float.isNaN(value) && !Float.isInfinite(value);
    }

}
