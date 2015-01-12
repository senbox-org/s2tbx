package org.esa.s2tbx.tooladapter.model;

import com.bc.ceres.binding.ConversionException;
import com.bc.ceres.binding.Converter;
import org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException;
import org.esa.s2tbx.tooladapter.model.parameters.Parameter;
import org.esa.s2tbx.tooladapter.model.parameters.ParameterFactory;
import org.esa.s2tbx.tooladapter.model.parameters.ParameterType;
import org.esa.s2tbx.tooladapter.model.templates.CommandLineTemplate;
import org.esa.s2tbx.tooladapter.model.templates.Template;
import org.esa.s2tbx.tooladapter.model.templates.TextFileTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** perform conversion between a {@link org.esa.s2tbx.tooladapter.model.Tool Tool}  object and its textual (xml) representation.
 *
 * @author Lucian Barbulescu.
 */
public class ToolConverter implements Converter<Tool> {

    /** Get the type of the java object
     * @return {@link org.esa.s2tbx.tooladapter.model.Tool Tool} class type
     */
    @Override
    public Class<? extends Tool> getValueType() {
        return Tool.class;
    }

    /**
     * Build a {@link org.esa.s2tbx.tooladapter.model.Tool Tool} object
     *
     * @param text the path to the xml descriptor of the {@link org.esa.s2tbx.tooladapter.model.Tool Tool} object
     * @return an instance of {@link org.esa.s2tbx.tooladapter.model.Tool Tool}
     * @throws ConversionException if the conversion cannot be performed
     */
    @Override
    public Tool parse(String text) throws ConversionException {
        // throw exception if the path is not correct
        if (text == null || text.length() == 0) {
            throw new ConversionException("You must specify the path to the tool's descriptor file");
        }

        //create the file object
        File descriptorFile = new File(text);
        if (!descriptorFile.isFile()) {
            throw new ConversionException("The path to the tool's descriptor file is invalid: " + text);
        }

        //create the tool object
        Tool tool = new Tool();
        tool.setDescriptor(descriptorFile);

        //create the SAX handler
        ToolXmlHandler handler = new ToolXmlHandler(tool);

        //create the SAX parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
            parser.parse(descriptorFile, handler);
        } catch (ParserConfigurationException|SAXException|IOException e) {
            throw new ConversionException("Error parsing tool's descriptor file: " + text, e);
        }

        return tool;
    }

    /** Create the descriptor file for a {@link org.esa.s2tbx.tooladapter.model.Tool Tool} object
     * @param value the {@link org.esa.s2tbx.tooladapter.model.Tool Tool}  instance
     * @return the full path to the descriptor file of the {@link org.esa.s2tbx.tooladapter.model.Tool Tool} instance
     */
    @Override
    public String format(Tool value) {
        //null value converts to empty string.
        if (value == null) {
            return "";
        }

        // get the tool ad XML
        String descriptorContent = value.toXmlString();
        String ret = "";

        //write the xml to the descriptor file
        try {
            FileWriter fw = new FileWriter(value.getDescriptor());
            //write the xml header
            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            fw.write(descriptorContent);
            fw.close();
            ret = value.getDescriptor().getAbsolutePath();
        } catch(IOException e) {
            ret = "";
        }

        return ret;
    }

    /**
     * Tool xml handler
     * @author Lucian Barbulescu
     */
    private class ToolXmlHandler extends DefaultHandler {

        /** The tool object that will be filled with data. */
        private Tool tool;

        /** The value of the current tag. */
        private String currentValue;

        /** The current template. */
        private Template currentTemplate;

        /** The current parameter. */
        private Parameter currentParameter;

        /** Constructor.
         * @param tool the toll object that will be filled with data.
         */
        ToolXmlHandler(Tool tool) {
            this.tool = tool;
            this.currentValue = null;
            this.currentTemplate = null;
            this.currentParameter = null;
        }

        /**
         * Receive notification of the start of an element.
         * <p/>
         * <p>By default, do nothing.  Application writers may override this
         * method in a subclass to take specific actions at the start of
         * each element (such as allocating a new tree node or writing
         * output to a file).</p>
         *
         * @param uri        The Namespace URI, or the empty string if the
         *                   element has no Namespace URI or if Namespace
         *                   processing is not being performed.
         * @param localName  The local name (without prefix), or the
         *                   empty string if Namespace processing is not being
         *                   performed.
         * @param qName      The qualified name (with prefix), or the
         *                   empty string if qualified names are not available.
         * @param attributes The attributes attached to the element.  If
         *                   there are no attributes, it shall be an empty
         *                   Attributes object.
         * @throws org.xml.sax.SAXException Any SAX exception, possibly
         *                                  wrapping another exception.
         * @see org.xml.sax.ContentHandler#startElement
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            this.currentValue = "";
            String type;
            String name;
            switch(qName) {
                case "toolFile":
                case "toolWorkingDirectory":
                case "content":
                    this.currentValue = "";
                    break;
                case "tool":
                    type = attributes.getValue("sourceType");
                    name = attributes.getValue("name");
                    this.tool.setName(name);
                    this.tool.setSourceType(type);
                    break;
                case "template":
                    type = attributes.getValue("type");
                    name = attributes.getValue("name");
                    switch(type) {
                        case "commandline":
                            this.currentTemplate = new CommandLineTemplate();
                            tool.setCommandLineTemplate((CommandLineTemplate)this.currentTemplate);
                            break;
                        case "text":
                            this.currentTemplate = new TextFileTemplate();
                            tool.setFileTemplate((TextFileTemplate) this.currentTemplate);
                            break;
                        case "xml":
                            //TODO: xml file
                            this.currentTemplate = new TextFileTemplate();
                            tool.setFileTemplate((TextFileTemplate) this.currentTemplate);
                            break;
                        default:
                            throw new SAXException("Unknown template type: " + type);
                    }
                    this.currentTemplate.setName(name);
                    break;
                case "parameter":
                    this.currentValue = "";
                    type = attributes.getValue("type");
                    name = attributes.getValue("name");
                    try {
                        this.currentParameter = ParameterFactory.instance().buildParameter(name, ParameterType.fromString(type));
                        //add parameter to template
                        this.currentTemplate.addParameter(this.currentParameter);
                    } catch (InvalidParameterException e) {
                        throw new SAXException("Invalid XML", e);
                    }
                    break;
            }
        }

        /**
         * Receive notification of the end of an element.
         * <p/>
         * <p>By default, do nothing.  Application writers may override this
         * method in a subclass to take specific actions at the end of
         * each element (such as finalising a tree node or writing
         * output to a file).</p>
         *
         * @param uri       The Namespace URI, or the empty string if the
         *                  element has no Namespace URI or if Namespace
         *                  processing is not being performed.
         * @param localName The local name (without prefix), or the
         *                  empty string if Namespace processing is not being
         *                  performed.
         * @param qName     The qualified name (with prefix), or the
         *                  empty string if qualified names are not available.
         * @throws org.xml.sax.SAXException Any SAX exception, possibly
         *                                  wrapping another exception.
         * @see org.xml.sax.ContentHandler#endElement
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch(qName) {
                case "toolFile":
                    tool.setFile(new File(this.currentValue.trim()));
                    this.currentValue = "";
                    break;
                case "toolWorkingDirectory":
                    tool.setWorkingDirectory(new File(this.currentValue.trim()));
                    this.currentValue = "";
                    break;
                case "content":
                    this.currentTemplate.setTemplate(this.currentValue.trim());
                    this.currentValue = "";
                    break;
                case "parameter":
                    try {
                        this.currentParameter.parseValue(this.currentValue.trim());
                    } catch (InvalidParameterException e) {
                        throw new SAXException("Invalid value for parameter " + this.currentParameter.getName()+": "+currentValue);
                    }
                    break;
            }
        }

        /**
         * Receive notification of character data inside an element.
         * <p/>
         * <p>By default, do nothing.  Application writers may override this
         * method to take specific actions for each chunk of character data
         * (such as adding the data to a node or buffer, or printing it to
         * a file).</p>
         *
         * @param ch     The characters.
         * @param start  The start position in the character array.
         * @param length The number of characters to use from the
         *               character array.
         * @throws org.xml.sax.SAXException Any SAX exception, possibly
         *                                  wrapping another exception.
         * @see org.xml.sax.ContentHandler#characters
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.currentValue += new String(ch, start, length);
        }
    }


}
