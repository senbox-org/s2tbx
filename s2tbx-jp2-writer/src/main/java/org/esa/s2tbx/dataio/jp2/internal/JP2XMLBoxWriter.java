package org.esa.s2tbx.dataio.jp2.internal;

import org.esa.s2tbx.dataio.jp2.metadata.JP2MetadataResources;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Class that prints the JP2 XML header
 * Created by Razvan Dumitrascu on 11/15/2016.
 */
public class JP2XMLBoxWriter {
    private FileOutputStream fileOutputStream;
    private JP2MetadataResources jp2MetadataResources;
    private int identation;
    private final static Logger logger = Logger.getLogger(JP2XMLBoxWriter.class.getName());

    /**
     * JP2XMLBoxWriter constructor
     */
    public JP2XMLBoxWriter(){
        this.fileOutputStream = null;
        this.jp2MetadataResources = null;
    }

    /**
     * Sets the resources needed for creating the XML JP2 header
     * @param fileOutputStream the output stream for the created image
     * @param jp2Metadata the metadata to be written
     * @throws XMLStreamException
     * @throws IOException
     */
    public void setResources(FileOutputStream fileOutputStream, JP2MetadataResources jp2Metadata) throws XMLStreamException, IOException {
        if(fileOutputStream == null){
            logger.warning("no fileOutputStream has been set");
            throw new IllegalArgumentException();
        }
        if(jp2Metadata == null){
            logger.warning("no jp2MetadataRespurces has been received");
            throw new IllegalArgumentException();
        }

        this.fileOutputStream = fileOutputStream;
        this.jp2MetadataResources = jp2Metadata;

        xmlJP2WriteStream(fileOutputStream);

    }

    /**
     * Creates the outputFactory and the XML Stream writer from the the JP2 output stream writer
     */
    private void xmlJP2WriteStream(FileOutputStream fileOutputStream ) throws XMLStreamException, IOException {

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        XMLStreamWriter tmpWriter = outputFactory.createXMLStreamWriter(this.fileOutputStream);
        writeStartDocument(tmpWriter);
        tmpWriter.flush();
        tmpWriter.close();
    }

    /**
     * Creates the XML header. The last character written is a null character so that the JP2 Reader identifies when the XML header has finished
     *  in order to start parsing it.
     */
    private void writeStartDocument(XMLStreamWriter tmpWriter) throws XMLStreamException, IOException {
        tmpWriter.writeStartDocument("UTF-8", "1.0");
        tmpWriter.writeCharacters("\n");
        tmpWriter.writeStartElement("gml:Polygon");
        tmpWriter.writeNamespace("gml","http://www.opengis.net/gml");
        tmpWriter.writeNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
        tmpWriter.writeAttribute("http://www.w3.org/2001/XMLSchema-instance","schemaLocation","http://www.opengeospatial.net/gml http://schemas.opengis.net/gml/3.1.1/profiles/gmlJP2Profile/1.0.0/gmlJP2Profile.xsd");
        tmpWriter.writeAttribute("gml:id","P0001");
        if(this.jp2MetadataResources.getEpsgNumber()!=0){
            tmpWriter.writeAttribute("srsName","urn:ogc:def:crs:EPSG::" + this.jp2MetadataResources.getEpsgNumber());
        }
        this.identation++;
        writeIdentation(this.identation,tmpWriter);
        writePolygon(tmpWriter);
        this.identation--;
        writeIdentation(this.identation,tmpWriter);
        tmpWriter.writeEndDocument();
        this.fileOutputStream.write(0x00);
    }

    private void writePolygon(XMLStreamWriter tmpWriter) throws XMLStreamException{
        this.identation++;
        writeIdentation(this.identation,tmpWriter);
        tmpWriter.writeStartElement("gml:exterior");
        this.identation++;
        writeIdentation(this.identation,tmpWriter);
        tmpWriter.writeStartElement("gml:LinearRing");
        this.identation++;
        for(int index=0;index<4;index++) {
            writeIdentation(this.identation,tmpWriter);
            tmpWriter.writeStartElement("gml:pos");
            tmpWriter.writeCharacters(this.jp2MetadataResources.getPoint(index).getX() + " " + this.jp2MetadataResources.getPoint(index).getY());
            tmpWriter.writeEndElement();
        }
        //write once more the starting corner (the upper left corner of the product)
        writeIdentation(this.identation,tmpWriter);
        tmpWriter.writeStartElement("gml:pos");
        tmpWriter.writeCharacters(this.jp2MetadataResources.getPoint(0).getX() + " " + this.jp2MetadataResources.getPoint(0).getY());
        tmpWriter.writeEndElement();
        writeEndElement(2,tmpWriter);
    }

    private  void writeEndElement(int numberOfElementsToClose,XMLStreamWriter tmpWriter) throws XMLStreamException {
        for(int index=0; index<numberOfElementsToClose;index++){
            this.identation--;
            writeIdentation(this.identation,tmpWriter);
            tmpWriter.writeEndElement();
        }
    }
    private void writeIdentation(int depth,XMLStreamWriter tmpWriter ) throws XMLStreamException {
        tmpWriter.writeCharacters("\n");
        for(int x=0; x<depth; x++) {
            tmpWriter.writeCharacters("  ");
        }
    }
}
