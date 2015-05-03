package org.esa.s2tbx.dataio.s2;


import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NamespaceFilter {

    private final GMLReader innerReader;
    private final XMLOutputter xmlOutput;

    public NamespaceFilter() throws JAXBException {
        this.innerReader = new GMLReader();
        this.xmlOutput = new XMLOutputter();
    }

    public List<JAXBElement> parse(String fileName) throws JDOMException, IOException, JAXBException {
        List<JAXBElement> gmlElementsRecovered = new ArrayList<>();

        InputStream stream = getClass().getResourceAsStream(fileName);

        // Use a SAX builder
        SAXBuilder builder = new SAXBuilder();
        // build a JDOM2 Document using the SAXBuilder.
        Document jdomDoc = builder.build(stream);

        //get the root element
        Element web_app = jdomDoc.getRootElement();

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

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        xmlOutput.output(capturedElement, baos);
                        String content = baos.toString();

                        Unmarshaller un = innerReader.getUnmarshaller();
                        ByteArrayInputStream bain = new ByteArrayInputStream(content.getBytes());
                        JAXBElement ob = (JAXBElement) un.unmarshal(bain);
                        gmlElementsRecovered.add(ob);

                        // fixme remove sysout
                        System.out.println(ob.getDeclaredType().getName());
                    }
                }
            }
        }

        return gmlElementsRecovered;
    }
}