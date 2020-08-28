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

package org.esa.s2tbx.dataio.s2.gml;


import org.locationtech.jts.geom.Polygon;
import org.apache.commons.math3.util.Pair;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.StreamingParser;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GmlFilter {

    private final XMLOutputter xmlOutput;

    public GmlFilter() {
        this.xmlOutput = new XMLOutputter();
    }

    /**
     * Parses GML3 using the streaming parser.
     */
    public static List<Polygon> streamParseGML3(InputStream in) {
        GMLConfiguration gml = new GMLConfiguration();
        List<Polygon> polygons = new ArrayList<Polygon>();

        try {
            StreamingParser parser = new StreamingParser( gml, in, Polygon.class );

            Polygon f = null;
            while( ( f = (Polygon) parser.parse() ) != null ) {
                polygons.add(f);
            }
        } catch (ParserConfigurationException e) {
            // {@report "classpath problem !"}
        } catch (SAXException e) {
            // {@report "classpath problem !"}
        }

        return polygons;
    }

    public Pair<String, List<EopPolygon>> parse(InputStream stream) {
        SAXBuilder builder = new SAXBuilder();
        Document jdomDoc = null;

        try {
            jdomDoc = builder.build(stream);

            //get the root element
            Element web_app = jdomDoc.getRootElement();
            String maskEpsg = "";

            Namespace gml = Namespace.getNamespace("http://www.opengis.net/gml/3.2");
            Namespace eop = Namespace.getNamespace("http://www.opengis.net/eop/2.0");

            List<Element> targeted = web_app.getChildren("boundedBy", gml);
            if(!targeted.isEmpty()) {
                Element aEnvelope = targeted.get(0).getChild("Envelope", gml);
                if(aEnvelope != null) {
                    maskEpsg = aEnvelope.getAttribute("srsName").getValue();
                }
            }

            List<EopPolygon> recoveredGeometries = new ArrayList<>();

            IteratorIterable<Content> contents = web_app.getDescendants();
            while (contents.hasNext()) {
                Content web_app_content = contents.next();
                if (!web_app_content.getCType().equals(CType.Text) && !web_app_content.getCType().equals(CType.Comment))
                {
                    boolean withGml = (web_app_content.getNamespacesInScope().get(0).getPrefix().contains("gml"));
                    if(withGml)
                    {
                        boolean parentNotGml = !(web_app_content.getParentElement().getNamespace().getPrefix().contains("gml"));
                        if(parentNotGml)
                        {
                            Element capturedElement = (Element) web_app_content;
                            Attribute attr = null;
                            String polygonId = "";
                            String typeId = "";
                            if(capturedElement.getName().contains("Polygon"))
                            {
                                attr = capturedElement.getAttribute("id", gml);
                                if(attr != null)
                                {
                                    polygonId = attr.getValue();
                                    if(polygonId.indexOf('.') != -1)
                                    {
                                        typeId = polygonId.substring(0, polygonId.indexOf('.'));
                                    }
                                }
                            }

                            Document newDoc = new Document(capturedElement.clone().detach());

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            xmlOutput.output(newDoc, baos);
                            String replacedContent = baos.toString().replace("/www.opengis.net/gml/3.2", "/www.opengis.net/gml");

                            InputStream ois = new ByteArrayInputStream(replacedContent.getBytes());

                            List<Polygon> pols = streamParseGML3(ois);
                            for(Polygon pol: pols)
                            {
                                recoveredGeometries.add(new EopPolygon(polygonId, typeId, pol));
                            }
                        }
                    }

                }
            }

            return new Pair<String, List<EopPolygon>>(maskEpsg,recoveredGeometries);
        } catch (JDOMException e) {
            // {@report "parse xml problem !"}
        } catch (IOException e) {
            // {@report "IO problem !"}
        }

        return new Pair<String, List<EopPolygon>>("", new ArrayList<>());
    }

    public Pair<String, List<EopPolygon>> parse(String resource) {
        InputStream stream = getClass().getResourceAsStream(resource);

        return parse(stream);
    }

    public Pair<String, List<EopPolygon>> parse(File fileName) {

        InputStream stream = null;
        try {
            stream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return parse(stream);
    }
}