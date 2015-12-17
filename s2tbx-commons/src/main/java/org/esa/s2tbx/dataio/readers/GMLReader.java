package org.esa.s2tbx.dataio.readers;

import com.vividsolutions.jts.geom.*;
import org.esa.snap.core.datamodel.Placemark;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
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
import java.util.logging.Logger;

/**
 * Simple GML parser to extract polygons from GML files.
 *
 * @author Cosmin Cara
 */
public class GMLReader {
    protected static Logger systemLogger = Logger.getLogger(GMLReader.class.getName());

    public static VectorDataNode parse(String maskName, Path inputFile) {
        VectorDataNode node = null;
        try (InputStream inputStream = Files.newInputStream(inputFile)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            Handler handler = new Handler();
            parser.parse(inputStream, handler);
            List<Polygon> polygons = handler.getResult();
            SimpleFeatureType featureType = Placemark.createGeometryFeatureType();
            DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("VectorMasks", featureType);
            for (int i = 0; i < polygons.size(); i++) {
                SimpleFeature feature = new SimpleFeatureImpl(new Object[]{ polygons.get(i), String.format("Polygon-%s", i)},
                        featureType, new FeatureIdImpl(String.format("F-%s", i)), true);
                featureCollection.add(feature);
            }
            node = new VectorDataNode(maskName, featureCollection);
        } catch (Exception e) {
            systemLogger.warning(e.getMessage());
        }
        return node;
    }

    protected static class Handler extends DefaultHandler
    {
        private List<Polygon> result;
        private StringBuilder buffer;
        private LinearRing currentLinearRing;
        private List<Coordinate> currentCoordinates;
        private int linRingCount;
        private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));

        public List<Polygon> getResult() {
            return result;
        }

        @Override
        public void startDocument() throws SAXException {
            try {
                result = new ArrayList<>();
                buffer = new StringBuilder();
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
            buffer.append(new String(ch, start, length));
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.indexOf(":") > 0) {
                qName = qName.substring(qName.indexOf(":") + 1);
            }
            buffer.setLength(0);
            if ("LinearRing".equals(qName)) {
                linRingCount++;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.indexOf(":") > 0) {
                qName = qName.substring(qName.indexOf(":") + 1);
            }
            switch (qName) {
                case "Polygon":
                    if (currentLinearRing != null) {
                        result.add(geometryFactory.createPolygon(currentLinearRing));
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
                    String[] points = buffer.toString().replace("\n", "").trim().split(" ");
                    currentCoordinates = new ArrayList<>();
                    for (int i = 0; i < points.length; i += 2) {
                        currentCoordinates.add(new Coordinate(Float.parseFloat(points[i]), Float.parseFloat(points[i+1])));
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
