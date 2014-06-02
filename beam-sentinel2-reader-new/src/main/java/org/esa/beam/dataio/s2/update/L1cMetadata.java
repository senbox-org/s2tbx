package org.esa.beam.dataio.s2.update;

import org.esa.beam.framework.datamodel.GeoPos;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p/>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Tonio Fincke
 * @author Norman Fomferra
 */
public class L1cMetadata {

    static Element NULL_ELEM = new Element("null") {
    };
    private final MetadataElement metadataElement;
    private TileGeometry tileGeometry;
    private float solzeCenter;
    private float solazCenter;
    private float vzaCenter;
    private float vaaCenter;
    private float[] solze = new float[4];
    private float[] solaz = new float[4];
    private float[] vza = new float[4];
    private float[] vaa = new float[4];

    static class TileGeometry {
        GeoPos center;
        GeoPos nw;
        GeoPos ne;
        GeoPos se;
        GeoPos sw;
    }

    public static L1cMetadata parseHeader(File file) throws JDOMException, IOException {
        return new L1cMetadata(new SAXBuilder().build(file).getRootElement());
    }

    public static L1cMetadata parseHeader(Reader reader) throws JDOMException, IOException {
        return new L1cMetadata(new SAXBuilder().build(reader).getRootElement());
    }

    public float[] getCornerLatitudes() {
        float[] res = new float[4];
        res[0] = tileGeometry.nw.getLat();
        res[1] = tileGeometry.ne.getLat();
        res[2] = tileGeometry.sw.getLat();
        res[3] = tileGeometry.se.getLat();
        return res;
    }

    public float[] getCornerLongitudes() {
        float[] res = new float[4];
        res[0] = tileGeometry.nw.getLon();
        res[1] = tileGeometry.ne.getLon();
        res[2] = tileGeometry.sw.getLon();
        res[3] = tileGeometry.se.getLon();
        return res;
    }

    public float[] getSolarZenith() {
        return solze;
    }

    public float[] getSolarAzimuth() {
        return solaz;
    }

    public float[] getViewZenith() {
        return vza;
    }

    public float[] getViewAzimuth() {
        return vaa;
    }

    private L1cMetadata(Element rootElement) throws DataConversionException {
        metadataElement = parseAll(rootElement);
        tileGeometry = parseTileGeometry(rootElement);
        parseAngles(rootElement);
    }

    private void parseAngles(Element parent) {
        final Element angularChild = parent.getChild("angular");
        solzeCenter = Float.parseFloat(angularChild.getChild("solze").getValue());
        solazCenter = Float.parseFloat(angularChild.getChild("solaz").getValue());
        vzaCenter = Float.parseFloat(angularChild.getChild("vza").getValue());
        vaaCenter = Float.parseFloat(angularChild.getChild("vaa").getValue());
        parseAngle(angularChild, "solze_arr", solze);
        parseAngle(angularChild, "solaz_arr", solaz);
        parseAngle(angularChild, "vza_arr", vza);
        parseAngle(angularChild, "vaa_arr", vaa);
    }

    private void parseAngle(Element angleParent, String angletoParse, float[] angleArray) {
        final Element angleChild = angleParent.getChild(angletoParse);
        final List<Element> valueChildren = (List<Element>) angleChild.getChildren();
        for (int i = 0; i < valueChildren.size(); i++) {
            Element valueChild = valueChildren.get(i);
            angleArray[i] = Float.parseFloat(valueChild.getValue());
        }
    }

    private TileGeometry parseTileGeometry(Element parent) {
        TileGeometry tg = new TileGeometry();
        final Element tileChild = parent.getChild("tile");
        final Element centerCoordinates = tileChild.getChild("center_coordinates");
        final List<Element> centerCoordinatesChildren = (List<Element>) centerCoordinates.getChildren();
        tg.center = new GeoPos(Float.parseFloat(centerCoordinatesChildren.get(0).getValue()),
                               Float.parseFloat(centerCoordinatesChildren.get(1).getValue()));
        final Element cornerCoordinates = tileChild.getChild("corner_coordinates");
        final List<Element> cornerCoordinatesChildren = (List<Element>) cornerCoordinates.getChildren();
        tg.nw = new GeoPos(Float.parseFloat(cornerCoordinatesChildren.get(0).getValue()),
                           Float.parseFloat(cornerCoordinatesChildren.get(1).getValue()));
        tg.ne = new GeoPos(Float.parseFloat(cornerCoordinatesChildren.get(2).getValue()),
                           Float.parseFloat(cornerCoordinatesChildren.get(3).getValue()));
        tg.se = new GeoPos(Float.parseFloat(cornerCoordinatesChildren.get(4).getValue()),
                           Float.parseFloat(cornerCoordinatesChildren.get(5).getValue()));
        tg.sw = new GeoPos(Float.parseFloat(cornerCoordinatesChildren.get(6).getValue()),
                           Float.parseFloat(cornerCoordinatesChildren.get(7).getValue()));
        return tg;
    }

    private MetadataElement parseAll(Element parent) {
        return parseTree(parent, null, new HashSet<String>(Arrays.asList("entity_id", "acquisition_date",
                                                                         "orbit_path", "orbit_row", "target_path",
                                                                         "target_row", "station_sgs",
                                                                         "scene_start_time", "scene_stop_time")));
    }

    private MetadataElement parseTree(Element element, MetadataElement mdParent, Set<String> includes) {

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
            if (includes.contains(childName)) {
                if (childValue != null && !childValue.isEmpty()) {
                    MetadataAttribute mdAttribute = new MetadataAttribute(childName, ProductData.createInstance(childValue), true);
                    String unit = child.getAttributeValue("unit");
                    if (unit != null) {
                        mdAttribute.setUnit(unit);
                    }
                    mdElement.addAttribute(mdAttribute);
                }
            }
        }

        if (mdParent != null) {
            mdParent.addElement(mdElement);
        }
        return mdElement;
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
