package org.esa.beam.dataio.s2;


import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;

import java.io.IOException;
import java.io.InputStream;

public class NamespaceFilter {

    public NamespaceFilter() {

    }

    public void parse(String fileName) throws JDOMException, IOException {
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
                        System.out.println(web_app_content.toString());
                    }
                }
            }
        }
    }
}