/*
 * Copyright (c) 2012. Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 */

package org.esa.beam.dataio.s3.olci;

import org.esa.beam.dataio.s3.DataSetPointer;
import org.esa.beam.dataio.util.XPathHelper;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class encapsulating the manifest file of an Olci Level 1b product.
 *
 * @author Marco Peters
 * @since 1.0
 */
class OlciL1bManifest {

    public static final String FIXED_HEADER_BASE_PATH = "/Earth_Explorer_Header/Fixed_Header/";
    public static final String MPH_BASE_PATH = "/Earth_Explorer_Header/Variable_Header/Main_Product_Header/";
    public static final String SPH_BASE_PATH = "/Earth_Explorer_Header/Variable_Header/Specific_Product_Header/";

    private final Document doc;
    private final XPathHelper xPathHelper;

    /**
     * Creates an instance of this class by using the given W3C document.
     *
     * @param manifestDocument the W3C manifest document.
     */
    OlciL1bManifest(Document manifestDocument) {
        doc = manifestDocument;
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPathHelper = new XPathHelper(xPath);
    }

    public String getProductName() {
        return xPathHelper.getString(FIXED_HEADER_BASE_PATH + "File_Name", doc);
    }

    public String getDescription() {
        return xPathHelper.getString(FIXED_HEADER_BASE_PATH + "File_Description", doc);
    }

    public String getProductType() {
        return xPathHelper.getString(FIXED_HEADER_BASE_PATH + "File_Type", doc);
    }

    public int getLineCount() {
        return Integer.parseInt(xPathHelper.getString(SPH_BASE_PATH + "Image_Size/Lines_Number", doc));
    }

    public int getColumnCount() {
        return Integer.parseInt(xPathHelper.getString(SPH_BASE_PATH + "Columns_Number", doc));
    }

    public ProductData.UTC getStartTime() {
        String utcString = xPathHelper.getString(MPH_BASE_PATH + "Start_Time", doc);
        try {
            return ProductData.UTC.parse(utcString, "'UTC='yyyy-MM-dd'T'HH:mm:ss");
        } catch (ParseException ignored) {
            return null;
        }
    }

    public ProductData.UTC getStopTime() {
        String utcString = xPathHelper.getString(MPH_BASE_PATH + "Stop_Time", doc);
        try {
            return ProductData.UTC.parse(utcString, "'UTC='yyyy-MM-dd'T'HH:mm:ss");
        } catch (ParseException ignored) {
            return null;
        }
    }

    public List<DataSetPointer> getDataSetPointers(DataSetPointer.Type type) {
        String xPath = String.format("%sList_of_Data_Objects/Data_Object_Descriptor[Type='%s']", SPH_BASE_PATH, type);
        NodeList nodeList = xPathHelper.getNodeList(xPath, doc);
        List<DataSetPointer> dataSetPointers = new ArrayList<DataSetPointer>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node dataObjectDescriptorNode = nodeList.item(i);
            String fileName = xPathHelper.getString("Filename", dataObjectDescriptorNode);
            String fileFormat = xPathHelper.getString("File_Format", dataObjectDescriptorNode);
            dataSetPointers.add(new DataSetPointer(fileName, fileFormat, type));
        }

        return dataSetPointers;
    }

    public MetadataElement getFixedHeader() {
        Node node = xPathHelper.getNode("/Earth_Explorer_Header/Fixed_Header", doc);
        return convertNodeToMetadataElement(node, new MetadataElement(node.getNodeName()));
    }

    public MetadataElement getMainProductHeader() {
        Node node = xPathHelper.getNode("/Earth_Explorer_Header/Variable_Header/Main_Product_Header", doc);
        return convertNodeToMetadataElement(node, new MetadataElement(node.getNodeName()));
    }

    public MetadataElement getSpecificProductHeader() {
        Node node = xPathHelper.getNode("/Earth_Explorer_Header/Variable_Header/Specific_Product_Header", doc);
        return convertNodeToMetadataElement(node, new MetadataElement(node.getNodeName()));
    }

    private MetadataElement convertNodeToMetadataElement(Node rootNode, MetadataElement rootMetadata) {
        NodeList childNodes = rootNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (hasElementChildNodes(node)) {
                    MetadataElement element = new MetadataElement(node.getNodeName());
                    convertNodeToMetadataElement(node, element);
                    rootMetadata.addElement(element);
                } else {
                    String nodevalue = node.getTextContent();
                    ProductData textContent = ProductData.createInstance(nodevalue);
                    rootMetadata.addAttribute(new MetadataAttribute(node.getNodeName(), textContent, true));
                }
            }
        }

        return rootMetadata;
    }

    private boolean hasElementChildNodes(Node rootNode) {
        NodeList childNodes = rootNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }
}
