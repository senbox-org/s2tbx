/*
 *
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

package org.esa.s2tbx.dataio.s2;


import com.vividsolutions.jts.geom.Polygon;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.StreamingParser;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
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

    public List<Polygon> parse(InputStream stream) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document jdomDoc = builder.build(stream);

        //get the root element
        Element web_app = jdomDoc.getRootElement();

        List<Polygon> recoveredGeometries = new ArrayList<>();

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
                        Document newDoc = new Document(capturedElement.clone().detach());

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        xmlOutput.output(newDoc, baos);
                        String replacedContent = baos.toString().replace("/www.opengis.net/gml/3.2", "/www.opengis.net/gml");

                        InputStream ois = new ByteArrayInputStream(replacedContent.getBytes());

                        recoveredGeometries.addAll(streamParseGML3(ois));
                    }
                }
            }
        }

        return recoveredGeometries;
    }

    public List<Polygon> parse(String resource) throws JDOMException, IOException {
        InputStream stream = getClass().getResourceAsStream(resource);

        return parse(stream);
    }

    public List<Polygon> parse(File fileName) throws JDOMException, IOException {
        InputStream stream = new FileInputStream(fileName);

        return parse(stream);
    }
}