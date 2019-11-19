package org.esa.s2tbx.dataio.readers;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.PrecisionModel;
import org.esa.snap.core.datamodel.Placemark;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.FeatureId;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Simple GML parser to extract polygons from GML files.
 *
 * @author Cosmin Cara
 */
public class GMLReader {
    protected static Logger systemLogger = Logger.getLogger(GMLReader.class.getName());
    protected static SimpleFeatureType featureType = Placemark.createGeometryFeatureType();
    public static VectorDataNode parse(String maskName, Path inputFile) {
        VectorDataNode node = null;
        try (InputStream inputStream = Files.newInputStream(inputFile)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            Handler handler = new Handler();
            parser.parse(inputStream, handler);
            List<SimpleFeature> polygonFeatures = handler.getResult();
            DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("VectorMasks", featureType);
            featureCollection.addAll(polygonFeatures);
            /*for (int i = 0; i < polygonFeatures.size(); i++) {
                SimpleFeature feature = new SimpleFeatureImpl(new Object[]{ polygonFeatures.get(i), String.format("Polygon-%s", i)},
                        featureType, new FeatureIdImpl(String.format("F-%s", i)), true);
                featureCollection.add(feature);
            }*/
            node = new VectorDataNode(maskName, featureCollection);
        } catch (Exception e) {
            systemLogger.warning(e.getMessage());
        }
        return node;
    }

    protected static class Handler extends DefaultHandler {
        private List<SimpleFeature> result;
        private List<Object> currentFeaturePoligons;
        private StringBuilder buffer;
        private FeatureId currentFeatureId;
        private LinearRing currentLinearRing;
        private List<Coordinate> currentCoordinates;
        private int linRingCount;
        private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));
        private int dimensions;

        public List<SimpleFeature> getResult() {
            return result;
        }

        @Override
        public void startDocument() throws SAXException {
            try {
                result = new ArrayList<>();
                buffer = new StringBuilder();
                currentFeaturePoligons = new ArrayList<>();
            } catch (Exception e) {
                systemLogger.severe(e.getMessage());
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            buffer.append(new String(ch, start, length).replace("\n", ""));
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.indexOf(":") > 0) {
                qName = qName.substring(qName.indexOf(":") + 1);
            }
            buffer.setLength(0);
            switch (qName) {
                case "LinearRing":
                    linRingCount++;
                    break;
                case "MaskFeature":
                    String id = attributes.getValue("gml:id");
                    currentFeatureId = new FeatureIdImpl(id);
                    currentFeaturePoligons.clear();
                    break;
                case "posList":
                    String dimStr = attributes.getValue("srsDimension");
                    try {
                        dimensions = Integer.parseInt(dimStr);
                    } catch (Exception e) {
                        dimensions = 2;
                    }
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.indexOf(":") > 0) {
                qName = qName.substring(qName.indexOf(":") + 1);
            }
            switch (qName) {
                case "MaskFeature":
                    if (currentFeaturePoligons.size() > 0) {
                        result.add(new SimpleFeatureImpl(currentFeaturePoligons.toArray(), featureType, currentFeatureId, true));
                    }
                    break;
                case "Polygon":
                    if (currentLinearRing != null) {
                        currentFeaturePoligons.add(geometryFactory.createPolygon(currentLinearRing));
                        currentFeaturePoligons.add(String.format("Polygon-%s", linRingCount));
                        currentLinearRing = null;
                    }
                    break;
                case "exterior":
                    break;
                case "LinearRing":
                    if (currentCoordinates != null) {
                        if (currentCoordinates.get(0).equals2D(currentCoordinates.get(currentCoordinates.size() - 1))) {
                            currentLinearRing = geometryFactory.createLinearRing(currentCoordinates.toArray(new Coordinate[currentCoordinates.size()]));
                        } else {
                            systemLogger.warning(String.format("The linear ring #%s is not a closed polygon!", linRingCount));
                        }
                        currentCoordinates = null;
                    }
                    break;
                case "posList":
                    StringTokenizer tokenizer = new StringTokenizer(buffer.toString(), " ", true);
                    currentCoordinates = new ArrayList<>();
                    int idx = 0;
                    Coordinate current = new Coordinate();
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (" ".equals(token)) {
                            if (idx == dimensions) {
                                currentCoordinates.add(current);
                                current = new Coordinate();
                                idx = 0;
                            }
                        } else {
                            current.setOrdinate(idx++, Double.parseDouble(token));
                        }
                    }
                    if (idx == dimensions) {
                        currentCoordinates.add(current);
                    }

                    break;
            }
            buffer.setLength(0);
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            String error = e.getMessage();
            if (!error.contains("no grammar found")) {
                systemLogger.warning(error);
            }
        }
    }
}
