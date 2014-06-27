package org.esa.beam.dataio.s2;

import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.StringUtils;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p/>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Norman Fomferra
 */
public class L1cMetadata {

    static Element NULL_ELEM = new Element("null") {
    };
    private final MetadataElement metadataElement;


    static class Tile {
        String id;
        String horizontalCsName;
        String horizontalCsCode;
        TileGeometry tileGeometry10M;
        TileGeometry tileGeometry20M;
        TileGeometry tileGeometry60M;
        AnglesGrid sunAnglesGrid;
        AnglesGrid[] viewingIncidenceAnglesGrids;

        public Tile(String id) {
            this.id = id;
            tileGeometry10M = new TileGeometry();
            tileGeometry20M = new TileGeometry();
            tileGeometry60M = new TileGeometry();
        }
    }

    static class AnglesGrid {
        int bandId;
        int detectorId;
        float[][] zenith;
        float[][] azimuth;
    }

    static class TileGeometry {
        int numRows;
        int numCols;
        double upperLeftX;
        double upperLeftY;
        double xDim;
        double yDim;
    }

    static class ResampleData {
        ReflectanceConversion reflectanceConversion;
        int quantificationValue;
    }

    static class ReflectanceConversion {
        double u;
        /**
         * Unit: W/m²/µm
         */
        double[] solarIrradiances;
    }

    static class ProductCharacteristics {
        String spacecraft;
        String datasetProductionDate;
        String processingLevel;
        SpectralInformation[] bandInformations;
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
    }

    static class QuicklookDescriptor {
        int imageNCols;
        int imageNRows;
        Histogram[] histogramList;
    }

    static class Histogram {
        public int bandId;
        int[] values;
        int step;
        double min;
        double max;
        double mean;
        double stdDev;
    }

    private final List<Tile> tileList;
    private final ResampleData resampleData;
    private final ProductCharacteristics productCharacteristics;
    private final QuicklookDescriptor quicklookDescriptor;

    public static L1cMetadata parseHeader(File file) throws JDOMException, IOException {
        return new L1cMetadata(new SAXBuilder().build(file).getRootElement());
    }

    public static L1cMetadata parseHeader(Reader reader) throws JDOMException, IOException {
        return new L1cMetadata(new SAXBuilder().build(reader).getRootElement());
    }

    public List<Tile> getTileList() {
        return tileList;
    }

    public ResampleData getResampleData() {
        return resampleData;
    }

    public ProductCharacteristics getProductCharacteristics() {
        return productCharacteristics;
    }

    public QuicklookDescriptor getQuicklookDescriptor() {
        return quicklookDescriptor;
    }

    public MetadataElement getMetadataElement() {
        return metadataElement;
    }

    private L1cMetadata(Element rootElement) throws DataConversionException {
        tileList = parseTileList(rootElement);
        resampleData = parseResampleData(rootElement);
        productCharacteristics = parseProductCharacteristics(rootElement);
        quicklookDescriptor = parseQuicklookDescriptor(rootElement);
        metadataElement = parseAll(rootElement);
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

    private static ProductCharacteristics parseProductCharacteristics(Element rootElement) throws DataConversionException {
        Element productCharacteristicsElem = getChild(rootElement, "Product_Characteristics");
        ProductCharacteristics resampleData = new ProductCharacteristics();
        resampleData.spacecraft = getElementValueString(productCharacteristicsElem, "SPACECRAFT");
        resampleData.datasetProductionDate = getElementValueString(productCharacteristicsElem, "DATASET_PRODUCTION_DATE");
        resampleData.processingLevel = getElementValueString(productCharacteristicsElem, "PROCESSING_LEVEL");
        resampleData.bandInformations = parseBandInformation(productCharacteristicsElem);
        return resampleData;
    }

    private static SpectralInformation[] parseBandInformation(Element resampleDataElem) throws DataConversionException {
        List<Element> spectralInformationElemList = getChildren(resampleDataElem, "Spectral_Information", "Spectral_Information_List");
        SpectralInformation[] spectralInformations = new SpectralInformation[spectralInformationElemList.size()];
        for (int i = 0; i < spectralInformations.length; i++) {
            SpectralInformation spectralInformation = new SpectralInformation();
            Element element = spectralInformationElemList.get(i);
            spectralInformation.bandId = Integer.parseInt(element.getAttributeValue("band_id"));
            spectralInformation.physicalBand = element.getAttributeValue("physical_band");
            spectralInformation.resolution = getElementValueInt(element, "RESOLUTION");
            Element wavelenght = getChild(element, "Wavelenght");
            spectralInformation.wavelenghtMin = getElementValueDouble(wavelenght, "MIN");
            spectralInformation.wavelenghtMin = getElementValueDouble(wavelenght, "MAX");
            spectralInformation.wavelenghtCentral = getElementValueDouble(wavelenght, "CENTRAL");
            Element spectralResponse = getChild(element, "Spectral_Response");
            spectralInformation.spectralResponseStep = getElementValueDouble(spectralResponse, "STEP");
            spectralInformation.spectralResponseValues = StringUtils.toDoubleArray(getElementValueString(spectralResponse, "VALUES"), " ");
            spectralInformations[i] = spectralInformation;
        }
        return spectralInformations;
    }

    private static QuicklookDescriptor parseQuicklookDescriptor(Element rootElement) throws DataConversionException {
        QuicklookDescriptor quicklookDescriptor = new QuicklookDescriptor();
        Element imageSize = getChild(rootElement, "Data_Strip", "Quicklook_Descriptor", "Image_Size");
        quicklookDescriptor.imageNCols = getElementValueInt(imageSize, "NCOLS");
        quicklookDescriptor.imageNRows = getElementValueInt(imageSize, "NROWS");

        List<Element> histogramElements = getChildren(rootElement, "Histogram", "Data_Strip", "Quicklook_Descriptor", "Histogram_List");
        quicklookDescriptor.histogramList = new Histogram[histogramElements.size()];

        for (int i = 0; i < quicklookDescriptor.histogramList.length; i++) {
            Element histogramElement = histogramElements.get(i);
            Histogram histogram = new Histogram();
            String valuesText = getElementValueString(histogramElement, "VALUES");
            histogram.bandId = getAttributeValueInt(histogramElement, "band_id");
            histogram.values = StringUtils.toIntArray(valuesText, " ");
            histogram.step = getElementValueInt(histogramElement, "STEP");
            histogram.min = getElementValueDouble(histogramElement, "MIN");
            histogram.max = getElementValueDouble(histogramElement, "MAX");
            histogram.mean = getElementValueDouble(histogramElement, "MEAN");
            histogram.stdDev = getElementValueDouble(histogramElement, "STD_DEV");
            quicklookDescriptor.histogramList[i] = histogram;
        }

        return quicklookDescriptor;
    }

    private static ResampleData parseResampleData(Element rootElement) throws DataConversionException {
        Element resampleDataElem = getChild(rootElement, "Resample_Data");
        ResampleData resampleData = new ResampleData();
        resampleData.quantificationValue = getElementValueInt(resampleDataElem, "QUANTIFICATION_VALUE");
        resampleData.reflectanceConversion = parseReflectanceConversion(resampleDataElem);
        return resampleData;
    }

    private static ReflectanceConversion parseReflectanceConversion(Element resampleDataElem) throws DataConversionException {
        Element reflectanceConversionElem = getChild(resampleDataElem, "Reflectance_Conversion");
        ReflectanceConversion reflectanceConversion = new ReflectanceConversion();
        reflectanceConversion.u = getElementValueDouble(reflectanceConversionElem, "U");

        List<Element> solarIrradianceList = getChildren(reflectanceConversionElem, "SOLAR_IRRADIANCE", "Solar_Irradiance_List");
        double[] solarIrradiances = new double[solarIrradianceList.size()];
        for (int i = 0; i < solarIrradiances.length; i++) {
            solarIrradiances[i] = getElementValueDouble(solarIrradianceList.get(i).getValue(), "SOLAR_IRRADIANCE");
        }
        reflectanceConversion.solarIrradiances = solarIrradiances;
        return reflectanceConversion;
    }

    private static List<Tile> parseTileList(Element rootElement) throws DataConversionException {
        List<Element> tileElements = getChildren(rootElement, "Tile", "Data_Strip", "Tiles_List");

        List<Tile> tileList = new ArrayList<Tile>(tileElements.size());
        for (Element tileElem : tileElements) {
            Tile tile = new Tile(tileElem.getAttributeValue("id"));
            tileList.add(tile);

            Element descriptionElem = getChild(tileElem, "Tile_Description");
            tile.horizontalCsName = getElementValueString(descriptionElem, "HORIZONTAL_CS_NAME");
            tile.horizontalCsCode = getElementValueString(descriptionElem, "HORIZONTAL_CS_CODE");

            List<Element> sizeElements = getChildren(descriptionElem, "Size");
            for (Element sizeElement : sizeElements) {
                TileGeometry tileGeometry;
                if ("10".equals(sizeElement.getAttributeValue("resolution"))) {
                    tileGeometry = tile.tileGeometry10M;
                } else if ("20".equals(sizeElement.getAttributeValue("resolution"))) {
                    tileGeometry = tile.tileGeometry20M;
                } else if ("60".equals(sizeElement.getAttributeValue("resolution"))) {
                    tileGeometry = tile.tileGeometry60M;
                } else {
                    tileGeometry = null;
                }
                if (tileGeometry != null) {
                    tileGeometry.numRows = getElementValueInt(sizeElement, "NROWS");
                    tileGeometry.numCols = getElementValueInt(sizeElement, "NCOLS");
                }
            }

            List<Element> geoposElements = getChildren(descriptionElem, "Geoposition");
            for (Element geoposElement : geoposElements) {
                TileGeometry tileGeometry;
                if ("10".equals(geoposElement.getAttributeValue("resolution"))) {
                    tileGeometry = tile.tileGeometry10M;
                } else if ("20".equals(geoposElement.getAttributeValue("resolution"))) {
                    tileGeometry = tile.tileGeometry20M;
                } else if ("60".equals(geoposElement.getAttributeValue("resolution"))) {
                    tileGeometry = tile.tileGeometry60M;
                } else {
                    tileGeometry = null;
                }
                if (tileGeometry != null) {
                    tileGeometry.upperLeftX = getElementValueDouble(geoposElement, "ULX");
                    tileGeometry.upperLeftY = getElementValueDouble(geoposElement, "ULY");
                    tileGeometry.xDim = getElementValueDouble(geoposElement, "XDIM");
                    tileGeometry.yDim = getElementValueDouble(geoposElement, "YDIM");
                }
            }

            Element saGridElem = getChild(tileElem, "Sun_Angles_Grid");
            AnglesGrid saGrid = new AnglesGrid();
            saGrid.bandId = -1;
            saGrid.detectorId = -1;
            saGrid.zenith = getAnglesMatrix(saGridElem, "Zenith");
            saGrid.azimuth = getAnglesMatrix(saGridElem, "Azimuth");
            tile.sunAnglesGrid = saGrid;

            List<Element> vaGridElemList = getChildren(tileElem, "Viewing_Incidence_Angles_Grids");
            tile.viewingIncidenceAnglesGrids = new AnglesGrid[vaGridElemList.size()];
            for (int j = 0; j < vaGridElemList.size(); j++) {
                Element vaGridElem = vaGridElemList.get(j);
                AnglesGrid vaGrid = new AnglesGrid();
                vaGrid.bandId = Integer.parseInt(vaGridElem.getAttributeValue("band_id"));
                vaGrid.detectorId = Integer.parseInt(vaGridElem.getAttributeValue("detector_id"));
                vaGrid.zenith = getAnglesMatrix(vaGridElem, "Zenith");
                vaGrid.azimuth = getAnglesMatrix(vaGridElem, "Azimuth");
                tile.viewingIncidenceAnglesGrids[j] = vaGrid;
            }
        }
        return tileList;
    }

    void dumpMatrix(float[][] matrix) {
        for (float[] floats : matrix) {
            for (float aFloat : floats) {
                System.out.print(Float.isNaN(aFloat) ? 'O' : '*');
            }
            System.out.println();
        }
    }


    private static float[][] getAnglesMatrix(Element viagElem, String parentName) {
        List<Element> values = getChildren(viagElem, "VALUES", parentName, "Values_List");
        float[][] matrix = new float[values.size()][];
        for (int i1 = 0; i1 < values.size(); i1++) {
            Element value = values.get(i1);
            float[] row = StringUtils.toFloatArray(value.getValue(), " ");
            matrix[i1] = row;
        }
        return matrix;
    }


    private static Element getChild(Element parent, String name) {
        if (parent == null) {
            return NULL_ELEM;
        }
        Element child = parent.getChild(name);
        if (child == null) {
            return NULL_ELEM;
        }
        return child;
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

    private static List<Element> getChildren(Element parent, String name) {
        return (List<Element>) parent.getChildren(name);
    }

    public static List<Element> getChildren(Element parent, String name, String... path) {
        Element child = getChild(parent, path);
        return (List<Element>) child.getChildren(name);
    }

    private static String getElementValueString(Element parent, String name) {
        return getChild(parent, name).getValue().trim();
    }

    private static double getElementValueDouble(String elementValue, String name) throws DataConversionException {
        try {
            return Double.parseDouble(elementValue);
        } catch (NumberFormatException e) {
            throw new DataConversionException(name, "double");
        }
    }

    private static double getElementValueDouble(Element parent, String name) throws DataConversionException {
        Element child = getChild(parent, name);
        return getElementValueDouble(child.getValue().trim(), name);
    }


    private static int getElementValueInt(String elementValue, String name) throws DataConversionException {
        try {
            return Integer.parseInt(elementValue);
        } catch (NumberFormatException e) {
            throw new DataConversionException(name, "int");
        }
    }

    private static int getElementValueInt(Element parent, String name) throws DataConversionException {
        Element child = getChild(parent, name);
        return getElementValueInt(child.getValue().trim(), name);
    }

    private static int getAttributeValueInt(Element element, String name) throws DataConversionException {
        try {
            return Integer.parseInt(element.getAttributeValue(name));
        } catch (NumberFormatException e) {
            throw new DataConversionException(name, "int");
        }
    }
}
