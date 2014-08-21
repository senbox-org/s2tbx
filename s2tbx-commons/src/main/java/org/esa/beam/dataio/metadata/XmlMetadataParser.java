package org.esa.beam.dataio.metadata;

import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.logging.BeamLogManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * SAX parser for XML metadata. This is because DOM parsing would consume more time and resources
 * for large metadata files.
 * @author Cosmin Cara
 */
public class XmlMetadataParser<T extends XmlMetadata> {

    protected Class fileClass;
    protected String[] schemaLocations;

    /**
     * Tries to infer the type of the element, based on the available XSD schema definition.
     * If no schema definition exist, the type will always be <code>ProductData.ASCII</code>.
     *
     * @param elementName   The name of the XML element.
     * @param value         The value of the XML element.
     * @return      An instance of <code>ProductData</code> wrapping the element value.
     */
    protected ProductData inferType(String elementName, String value) {
        return ProductData.ASCII.createInstance(value);
    }

    /**
     * Constructs an instance of <code>XmlMetadataParser</code> for the given metadata class.
     *
     * @param metadataClass    The class of metadata (it should be derived from <code>XmlMetadata</code>).
     */
    public XmlMetadataParser(Class metadataClass) {
        this.fileClass = metadataClass;
    }

    /**
     * Tries to parse the given <code>InputStream</code> (which may be a string or a stream over a file).
     *
     * @param inputStream   The input stream
     * @return  If successful, it returns an instance of a class extending <code>XmlMetadata</code>.
     * @throws ParserConfigurationException     Exception is thrown by the underlying SAX mechanism.
     * @throws SAXException                     Exception is thrown if the XML is not well formed.
     * @throws IOException                      Exception is thrown if there is a problem reading the input stream.
     */
    public T parse(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        if (schemaLocations != null && shouldValidateSchema()) {
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            ClassLoader classLoader = this.getClass().getClassLoader();
            List<StreamSource> streamSourceList = new Vector<StreamSource>();
            for (String schemaLocation : schemaLocations) {
                InputStream is = classLoader.getResourceAsStream(schemaLocation);
                StreamSource streamSource = new StreamSource(is);
                streamSourceList.add(streamSource);
            }
            StreamSource sources[] = new StreamSource[streamSourceList.size()];
            Schema schema = schemaFactory.newSchema(streamSourceList.toArray(sources));
            factory.setSchema(schema);
            factory.setValidating(true);
        }
        SAXParser parser = factory.newSAXParser();
        MetadataHandler handler = new MetadataHandler();
        parser.parse(inputStream, handler);

        return handler.getResult();
    }

    /**
     * Indicates if the XSD validation should be performed.
     * Override this in derived classes to enable schema validation.
     * @return  The default implementation always returns <code>false</code>.
     *          In a derived class, <code>true</code> would mean that the XML
     *          schema validation should be performed.
     */
    protected boolean shouldValidateSchema() {
        return false;
    }

    /**
     * Sets the location(s) of the XSD schema(s) that should be used for XSD
     * schema validation.
     *
     * @param schemaLocations   An array of schema locations.
     */
    protected void setSchemaLocations(String[] schemaLocations) {
        this.schemaLocations = schemaLocations;
    }

    /**
     * Actual document handler implementation
     */
    protected class MetadataHandler extends DefaultHandler
    {
        private T result;
        private String buffer;
        private Stack<MetadataElement> elementStack;
        private Logger systemLogger;

        public T getResult() {
            return result;
        }

        @Override
        public void startDocument() throws SAXException {
            systemLogger = BeamLogManager.getSystemLogger();
            elementStack = new Stack<MetadataElement>();
            try {
                @SuppressWarnings("unchecked") Constructor<T> ctor = fileClass.getConstructor(String.class);
                result = ctor.newInstance("Metadata");
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
            buffer = new String(ch, start, length);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // strip any namespace prefix
            if (qName.indexOf(":") > 0) {
                qName = qName.substring(qName.indexOf(":") + 1);
            }
            MetadataElement element = new MetadataElement(qName);
            buffer = "";
            for (int i = 0; i < attributes.getLength(); i++)
            {
                element.addAttribute(new MetadataAttribute(attributes.getQName(i), ProductData.ASCII.createInstance(attributes.getValue(i)), false));
            }
            elementStack.push(element);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            MetadataElement closingElement =  elementStack.pop();
            if (!elementStack.empty())
            {
                if (buffer != null && !buffer.isEmpty() && !buffer.startsWith("\n")) {
                    elementStack.peek().addAttribute(new MetadataAttribute(closingElement.getName(), inferType(qName, buffer), false));
                    buffer = "";
                } else {
                    elementStack.peek().addElement(closingElement);
                }
            } else {
                XmlMetadata.CopyChildElements(closingElement, result.getRootElement());
                result.getRootElement().setName("Metadata");
            }
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            String error = e.getMessage();
            if (!(error.contains("Dimap_Document") || error.contains("no grammar found")))
                systemLogger.warning(e.getMessage());
        }
    }

}
