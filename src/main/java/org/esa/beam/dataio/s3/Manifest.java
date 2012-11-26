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

package org.esa.beam.dataio.s3;

import org.esa.beam.dataio.util.XPathHelper;
import org.esa.beam.framework.datamodel.ProductData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathFactory;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class encapsulating the manifest file of Sentinel-3 Synergy products.
 *
 * @author Olaf Danne
 * @author Ralf Quast
 * @since 1.0
 */
public class Manifest {

    private final Document doc;
    private final XPathHelper xPathHelper;

    public static Manifest createManifest(Document manifestDocument) {
        return new Manifest(manifestDocument);
    }

    private Manifest(Document manifestDocument) {
        doc = manifestDocument;
        xPathHelper = new XPathHelper(XPathFactory.newInstance().newXPath());
    }

    public String getDescription() {
        return xPathHelper.getString("/XFDU/informationPackageMap/contentUnit/@textInfo", doc);
    }

    public ProductData.UTC getStartTime() {
        return getTime("startTime");
    }

    public ProductData.UTC getStopTime() {
        return getTime("stopTime");
    }

    public List<String> getFileNames(final String schema) {
        final List<String> fileNameList = new ArrayList<String>();

        getFileNames("dataObjectSection/dataObject", schema, fileNameList);
        getFileNames("metadataSection/metadataObject", schema, fileNameList);

        return fileNameList;
    }

    public List<String> getFileNames(String objectPath, final String schema, List<String> fileNameList) {
        final NodeList nodeList = xPathHelper.getNodeList(
                "/XFDU/" + objectPath + "[@repID='" + schema + "']", doc);
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node item = nodeList.item(i);
            final String fileName = xPathHelper.getString("./byteStream/fileLocation/@href", item);
            if (!fileNameList.contains(fileName)) {
                fileNameList.add(fileName);
            }
        }

        return fileNameList;
    }

    public String getFileName(final String objectPath, final String schema) {
        final Node node = xPathHelper.getNode("/XFDU/" + objectPath + "[@repID='" + schema + "']", doc);
        return xPathHelper.getString("./byteStream/fileLocation/@href", node);
    }

    private ProductData.UTC getTime(final String name) {
        final Node period = xPathHelper.getNode("/XFDU/metadataSection/metadataObject[@ID='acquisitionPeriod']", doc);
        final String time = xPathHelper.getString("//metadataWrap/xmlData/acquisitionPeriod/" + name, period);
        try {
            if (Character.isDigit(time.charAt(time.length() - 1))) {
                return ProductData.UTC.parse(time, "yyyy-MM-dd'T'HH:mm:ss");
            }
            return ProductData.UTC.parse(time, "yyyy-MM-dd'T'HH:mm:ssZ");
        } catch (ParseException ignored) {
            return null;
        }
    }
}
