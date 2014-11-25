package org.esa.beam.dataio.s2;

import _int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata.Level1CTile;
import _int.esa.s2.pdgs.psd.user_product_level_1c.Level1CUserProduct;
import org.esa.beam.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.beam.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.beam.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.logging.BeamLogManager;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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


    private MetadataElement metadataElement;
    protected Logger logger = BeamLogManager.getSystemLogger();


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

    private List<Tile> tileList;
    private List<String> imageList; //todo populate imagelist
    private ProductCharacteristics productCharacteristics;

    public static L1cMetadata parseHeader(File file) throws JDOMException, IOException {
        return new L1cMetadata(new FileInputStream(file), file, file.getParent());
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

    private L1cMetadata(InputStream stream, File file, String parent) throws DataConversionException
    {
        try {
            Level1CUserProduct product = (Level1CUserProduct) L1cMetadataProc.readJaxbFromFilename(stream);
            productCharacteristics = L1cMetadataProc.getProductOrganization(product);

            Collection<String> tileNames = L1cMetadataProc.getTiles(product);
            List<File> fullTileNamesList = new ArrayList<File>();

            tileList = new ArrayList<Tile>();

            for (String granuleName: tileNames)
            {
                FileInputStream fi = (FileInputStream) stream;
                File nestedMetadata = new File(parent, "GRANULE" + File.separator + granuleName);

                S2GranuleDirFilename aGranuleDir = S2GranuleDirFilename.create(granuleName);
                String theName = aGranuleDir.getMetadataFilename().name;

                File nestedGranuleMetadata = new File(parent, "GRANULE" + File.separator + granuleName + File.separator + theName);
                if(nestedGranuleMetadata.exists()) {
                    fullTileNamesList.add(nestedGranuleMetadata);
                } else {
                    String errorMessage = "Corrupted product: the file for the granule " + granuleName + " is missing";
                    logger.log(Level.WARNING, errorMessage);
                }
            }

            for(File aGranuleMetadataFile: fullTileNamesList)
            {
                Level1CTile aTile = (Level1CTile) L1cMetadataProc.readJaxbFromFilename(new FileInputStream(aGranuleMetadataFile));
                Map<Integer, TileGeometry> geoms = L1cMetadataProc.getTileGeometries(aTile);

                Tile t = new Tile(aTile.getGeneralInfo().getTILEID().getValue());
                t.horizontalCsCode = aTile.getGeometricInfo().getTileGeocoding().getHORIZONTALCSCODE();
                t.horizontalCsName = aTile.getGeometricInfo().getTileGeocoding().getHORIZONTALCSNAME();

                t.tileGeometry10M = geoms.get(10);
                t.tileGeometry20M = geoms.get(20);
                t.tileGeometry60M = geoms.get(60);

                t.sunAnglesGrid = L1cMetadataProc.getSunGrid(aTile);
                t.viewingIncidenceAnglesGrids = L1cMetadataProc.getAnglesGrid(aTile);

                tileList.add(t);
            }

            S2DatastripFilename stripName = L1cMetadataProc.getDatastrip(product);
            S2DatastripDirFilename dirStripName = L1cMetadataProc.getDatastripDir(product);

            File dataStripMetadata = new File(parent, "DATASTRIP" + File.separator + dirStripName.name + File.separator + stripName.name);

            //todo improve exception handling
            metadataElement = new MetadataElement("root");
            MetadataElement userProduct = parseAll(new SAXBuilder().build(file).getRootElement());
            MetadataElement dataStrip = parseAll(new SAXBuilder().build(dataStripMetadata).getRootElement());
            metadataElement.addElement(userProduct);
            metadataElement.addElement(dataStrip);
            MetadataElement granulesMetaData = new MetadataElement("Granules");

            for(File aGranuleMetadataFile: fullTileNamesList)
            {
                MetadataElement aGranule = parseAll(new SAXBuilder().build(aGranuleMetadataFile).getRootElement());
                granulesMetaData.addElement(aGranule);
            }

            metadataElement.addElement(granulesMetaData);


            //todo improve exception handling
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            if (child == null)
            {
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
