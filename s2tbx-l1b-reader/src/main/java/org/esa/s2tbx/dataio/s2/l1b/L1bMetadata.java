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

package org.esa.s2tbx.dataio.s2.l1b;

import com.vividsolutions.jts.geom.Coordinate;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_GEOMETRIC_HEADER_LIST_EXPERTISE;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_datastrip_metadata.Level1B_DataStrip;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata.Level1B_Granule;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1b.Level1B_User_Product;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.snap.framework.datamodel.MetadataAttribute;
import org.esa.snap.framework.datamodel.MetadataElement;
import org.esa.snap.framework.datamodel.ProductData;
import org.esa.snap.util.Guardian;
import org.esa.snap.util.logging.BeamLogManager;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Norman Fomferra
 */
public class L1bMetadata {

    public String getCrs() {
        return crs;
    }

    private String crs;

    static Element NULL_ELEM = new Element("null") {
    };


    private MetadataElement metadataElement;
    protected Logger logger = BeamLogManager.getSystemLogger();


    static class Tile {
        String id;
        String detectorId;
        TileGeometry tileGeometry10M;
        TileGeometry tileGeometry20M;
        TileGeometry tileGeometry60M;

        AnglesGrid sunAnglesGrid;
        AnglesGrid viewingIncidenceAnglesGrids;

        public List<Coordinate> corners;

        public static enum idGeom {G10M, G20M, G60M}

        ;

        public Tile(String id, String detectorId) {
            this.id = id;
            this.detectorId = detectorId;
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

    static class TileGeometry {
        int numRows;
        int numCols;
        public Integer position;
        int xDim;
        int yDim;
        public int resolution;
        public int numRowsDetector;
        public String detector;

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    static class AnglesGrid {
        int bandId;
        int detectorId;
        double zenith;
        double azimuth;

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    static class ProductCharacteristics {
        String spacecraft;
        String datasetProductionDate;
        String processingLevel;
        SpectralInformation[] bandInformations;

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    static class SpectralInformation {
        int bandId;
        String physicalBand;
        int resolution;
        double wavelenghtMin;
        double wavelenghtMax;
        double wavelenghtCentral;
        double spectralResponseStep;
        double[] spectralResponseValues;

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    private List<Tile> tileList;
    private List<String> imageList; //todo populate imagelist
    private ProductCharacteristics productCharacteristics;
    private JAXBContext context;
    private Unmarshaller unmarshaller;

    public static L1bMetadata parseHeader(File file) throws JDOMException, IOException {
        return new L1bMetadata(new FileInputStream(file), file, file.getParent());
    }

    public List<Tile> getTileList() {
        return tileList;
    }

    public ProductCharacteristics getProductCharacteristics() {
        return productCharacteristics;
    }


    public MetadataElement getMetadataElement() {
        return metadataElement;
    }

    private L1bMetadata(InputStream stream, File file, String parent) throws DataConversionException {
        try {
            context = L1bMetadataProc.getJaxbContext();
            unmarshaller = context.createUnmarshaller();

            Object ob = unmarshaller.unmarshal(stream);
            Object casted = ((JAXBElement) ob).getValue();

            if(casted instanceof Level1B_User_Product)
            {
                initProduct(stream, file, parent, casted);
            }
            else
            {
                initTile(stream, file, parent, casted);
            }

        } catch (JAXBException e) {
            logger.severe(Utils.getStackTrace(e));
        } catch (FileNotFoundException e) {
            logger.severe(Utils.getStackTrace(e));
        } catch (JDOMException e) {
            logger.severe(Utils.getStackTrace(e));
        } catch (IOException e) {
            logger.severe(Utils.getStackTrace(e));
        }
    }


    private void initProduct(InputStream stream, File file, String parent, Object casted) throws IOException, JAXBException, JDOMException {
        Level1B_User_Product product = (Level1B_User_Product) casted;
        productCharacteristics = L1bMetadataProc.getProductOrganization(product);

        crs = L1bMetadataProc.getCrs(product);

        Collection<String> tileNames = L1bMetadataProc.getTiles(product);
        List<File> fullTileNamesList = new ArrayList<File>();

        tileList = new ArrayList<Tile>();

        for (String granuleName : tileNames) {
            File nestedMetadata = new File(parent, "GRANULE" + File.separator + granuleName);

            if (nestedMetadata.exists()) {
                logger.log(Level.FINE, "File found: " + nestedMetadata.getAbsolutePath());
                S2GranuleDirFilename aGranuleDir = S2L1BGranuleDirFilename.create(granuleName);
                Guardian.assertNotNull("aGranuleDir", aGranuleDir);
                String theName = aGranuleDir.getMetadataFilename().name;

                File nestedGranuleMetadata = new File(parent, "GRANULE" + File.separator + granuleName + File.separator + theName);
                if (nestedGranuleMetadata.exists()) {
                    fullTileNamesList.add(nestedGranuleMetadata);
                } else {
                    String errorMessage = "Corrupted product: the file for the granule " + granuleName + " is missing";
                    logger.log(Level.WARNING, errorMessage);
                }
            } else {
                logger.log(Level.SEVERE, "File not found: " + nestedMetadata.getAbsolutePath());
            }
        }

        for (File aGranuleMetadataFile : fullTileNamesList) {
            Object aob = unmarshaller.unmarshal(new FileInputStream(aGranuleMetadataFile));
            Object acasted = ((JAXBElement) aob).getValue();

            Level1B_Granule aGranule = (Level1B_Granule) acasted;
            Map<Integer, TileGeometry> geoms = L1bMetadataProc.getGranuleGeometries(aGranule);

            Tile t = new Tile(aGranule.getGeneral_Info().getGRANULE_ID().getValue(), aGranule.getGeneral_Info().getDETECTOR_ID().getValue());

            t.tileGeometry10M = geoms.get(10);
            t.tileGeometry20M = geoms.get(20);
            t.tileGeometry60M = geoms.get(60);

            t.sunAnglesGrid = L1bMetadataProc.getSunGrid(aGranule);
            t.viewingIncidenceAnglesGrids = L1bMetadataProc.getAnglesGrid(aGranule);

            t.corners = L1bMetadataProc.getGranuleCorners(aGranule); // counterclockwise

            tileList.add(t);
        }

        S2DatastripFilename stripName = L1bMetadataProc.getDatastrip(product);
        S2DatastripDirFilename dirStripName = L1bMetadataProc.getDatastripDir(product);

        File dataStripMetadata = new File(parent, "DATASTRIP" + File.separator + dirStripName.name + File.separator + stripName.name);

        metadataElement = new MetadataElement("root");
        MetadataElement userProduct = parseAll(new SAXBuilder().build(file).getRootElement());
        MetadataElement dataStrip = parseAll(new SAXBuilder().build(dataStripMetadata).getRootElement());
        metadataElement.addElement(userProduct);
        metadataElement.addElement(dataStrip);
        MetadataElement granulesMetaData = new MetadataElement("Granules");

        // get datastrip...
        Object dStrip = unmarshaller.unmarshal(dataStripMetadata);
        Object castedStrip = ((JAXBElement) dStrip).getValue();

        Level1B_DataStrip theDataStrip = (Level1B_DataStrip) castedStrip;
        int numheaders = theDataStrip.getImage_Data_Info().getGeometric_Header_List().getGeometric_Header().size();


        List<AnglesGrid> sunGrid = new ArrayList<AnglesGrid>();
        List<AnglesGrid> incidenceGrid = new ArrayList<AnglesGrid>();

        List<A_GEOMETRIC_HEADER_LIST_EXPERTISE.Geometric_Header> headers = theDataStrip.getImage_Data_Info().getGeometric_Header_List().getGeometric_Header();
        for (A_GEOMETRIC_HEADER_LIST_EXPERTISE.Geometric_Header header : headers) {
            Iterator it = header.getLocated_Geometric_Header().iterator();
            while (it.hasNext()) {
                A_GEOMETRIC_HEADER_LIST_EXPERTISE.Geometric_Header.Located_Geometric_Header o = (A_GEOMETRIC_HEADER_LIST_EXPERTISE.Geometric_Header.Located_Geometric_Header) it.next();
                AnglesGrid tmpGrid = new AnglesGrid();
                tmpGrid.azimuth = o.getSolar_Angles().getAZIMUTH_ANGLE().getValue();
                tmpGrid.zenith = o.getSolar_Angles().getZENITH_ANGLE().getValue();
                sunGrid.add(tmpGrid);

                AnglesGrid tmpIncidenceGrid = new AnglesGrid();
                tmpIncidenceGrid.azimuth = o.getIncidence_Angles().getAZIMUTH_ANGLE().getValue();
                tmpIncidenceGrid.zenith = o.getIncidence_Angles().getZENITH_ANGLE().getValue();
                incidenceGrid.add(tmpIncidenceGrid);
            }
        }

        for (File aGranuleMetadataFile : fullTileNamesList) {
            MetadataElement aGranule = parseAll(new SAXBuilder().build(aGranuleMetadataFile).getRootElement());
            granulesMetaData.addElement(aGranule);
        }

        metadataElement.addElement(granulesMetaData);
    }

    private void initTile(InputStream stream, File file, String parent, Object casted) throws IOException, JAXBException, JDOMException {
        Level1B_Granule product = (Level1B_Granule) casted;
        productCharacteristics = new L1bMetadata.ProductCharacteristics();

        List<File> fullTileNamesList = new ArrayList<File>();
        tileList = new ArrayList<Tile>();

        {
            Level1B_Granule aGranule = product;
            Map<Integer, TileGeometry> geoms = L1bMetadataProc.getGranuleGeometries(aGranule);

            Tile t = new Tile(aGranule.getGeneral_Info().getGRANULE_ID().getValue(), aGranule.getGeneral_Info().getDETECTOR_ID().getValue());

            t.tileGeometry10M = geoms.get(10);
            t.tileGeometry20M = geoms.get(20);
            t.tileGeometry60M = geoms.get(60);

            t.sunAnglesGrid = L1bMetadataProc.getSunGrid(aGranule);
            t.viewingIncidenceAnglesGrids = L1bMetadataProc.getAnglesGrid(aGranule);

            t.corners = L1bMetadataProc.getGranuleCorners(aGranule); // counterclockwise

            tileList.add(t);
        }
    }

    private MetadataElement parseAll(Element parent) {
        return parseTree(parent, null, new HashSet<String>(Arrays.asList("Viewing_Incidence_Angles_Grids", "Sun_Angles_Grid")));
    }

    private MetadataElement parseTree(Element element, MetadataElement mdParent, Set<String> excludes) {

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
    }

    private static Element getChild(Element parent, String... path) {
        Element child = parent;
        if (child == null) {
            return NULL_ELEM;
        }
        for (String name : path) {
            child = child.getChild(name);
            if (child == null) {
                return NULL_ELEM;
            }
        }
        return child;
    }

    private static double getElementValueDouble(String elementValue, String name) throws DataConversionException {
        try {
            return Double.parseDouble(elementValue);
        } catch (NumberFormatException e) {
            throw new DataConversionException(name, "double");
        }
    }
}
