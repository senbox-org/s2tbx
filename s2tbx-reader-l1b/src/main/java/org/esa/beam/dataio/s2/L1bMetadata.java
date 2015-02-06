package org.esa.beam.dataio.s2;

import com.vividsolutions.jts.geom.Coordinate;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata.Level1B_Granule;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1b.Level1B_User_Product;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.beam.dataio.s2.filepatterns.S2L1bDatastripDirFilename;
import org.esa.beam.dataio.s2.filepatterns.S2L1bDatastripFilename;
import org.esa.beam.dataio.s2.filepatterns.S2L1bGranuleDirFilename;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.Guardian;
import org.esa.beam.util.logging.BeamLogManager;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
        String horizontalCsCode;
        TileGeometry tileGeometry10M;
        TileGeometry tileGeometry20M;
        TileGeometry tileGeometry60M;

        public static enum idGeom{G10M, G20M, G60M};

        public Tile(String id, String detectorId) {
            this.id = id;
            this.detectorId = detectorId;
            tileGeometry10M = new TileGeometry();
            tileGeometry20M = new TileGeometry();
            tileGeometry60M = new TileGeometry();
        }

        public TileGeometry getGeometry(idGeom index)
        {
            switch (index)
            {
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
        public ReferencedEnvelope envelope;
        public int position;
        int xDim;
        int yDim;
        public Coordinate corner;
        public int resolution;
        public int numRowsDetector;
        public Coordinate llcorner;
        public String detector;

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

    private L1bMetadata(InputStream stream, File file, String parent) throws DataConversionException
    {
        try {
            context = L1bMetadataProc.getJaxbContext();
            unmarshaller = context.createUnmarshaller();

            Object ob =  unmarshaller.unmarshal(stream);
            Object casted = ((JAXBElement)ob).getValue();

            Level1B_User_Product product = (Level1B_User_Product) casted;
            productCharacteristics = L1bMetadataProc.getProductOrganization(product);

            crs = L1bMetadataProc.getCrs(product);

            Collection<String> tileNames = L1bMetadataProc.getTiles(product);
            List<File> fullTileNamesList = new ArrayList<File>();

            tileList = new ArrayList<Tile>();

            for (String granuleName: tileNames)
            {
                File nestedMetadata = new File(parent, "GRANULE" + File.separator + granuleName);
                logger.log(Level.WARNING, "Looking for: " + nestedMetadata.getAbsolutePath());

                if(nestedMetadata.exists())
                {
                    S2L1bGranuleDirFilename aGranuleDir = S2L1bGranuleDirFilename.create(granuleName);
                    Guardian.assertNotNull("aGranuleDir", aGranuleDir);
                    String theName = aGranuleDir.getMetadataFilename().name;

                    File nestedGranuleMetadata = new File(parent, "GRANULE" + File.separator + granuleName + File.separator + theName);
                    if(nestedGranuleMetadata.exists()) {
                        fullTileNamesList.add(nestedGranuleMetadata);
                    } else {
                        String errorMessage = "Corrupted product: the file for the granule " + granuleName + " is missing";
                        logger.log(Level.WARNING, errorMessage);
                    }
                }
                else
                {
                    logger.log(Level.SEVERE, "File not found: " + nestedMetadata.getAbsolutePath());
                }
            }

            int index = 0;
            for(File aGranuleMetadataFile: fullTileNamesList)
            {
                long startTime = System.currentTimeMillis();

                Object aob =  unmarshaller.unmarshal(new FileInputStream(aGranuleMetadataFile));
                Object acasted = ((JAXBElement)aob).getValue();

                Level1B_Granule aGranule = (Level1B_Granule) acasted;
                long endTime = System.currentTimeMillis();

                // todo remove this logs
                logger.log(Level.SEVERE, "That took " + (endTime - startTime) + " milliseconds");

                startTime = System.currentTimeMillis();
                Map<Integer, TileGeometry> geoms = L1bMetadataProc.getGranuleGeometries(aGranule);
                endTime = System.currentTimeMillis();
                logger.log(Level.SEVERE, "The granule took " + (endTime - startTime) + " milliseconds");

                Tile t = new Tile(aGranule.getGeneral_Info().getGRANULE_ID().getValue(), aGranule.getGeneral_Info().getDETECTOR_ID().getValue());

                // todo look at geometric info here

                t.tileGeometry10M = geoms.get(10);
                t.tileGeometry20M = geoms.get(20);
                t.tileGeometry60M = geoms.get(60);

                tileList.add(t);
                index = index + 1;
                logger.log(Level.WARNING, "Added tile num: " + index);
            }

            S2L1bDatastripFilename stripName = L1bMetadataProc.getDatastrip(product);
            S2L1bDatastripDirFilename dirStripName = L1bMetadataProc.getDatastripDir(product);

            File dataStripMetadata = new File(parent, "DATASTRIP" + File.separator + dirStripName.name + File.separator + stripName.name);

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
