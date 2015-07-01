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


import jp2.TileLayout;
import org.apache.commons.io.IOUtils;
import org.esa.snap.framework.datamodel.MetadataAttribute;
import org.esa.snap.framework.datamodel.MetadataElement;
import org.esa.snap.framework.datamodel.ProductData;
import org.jdom.Attribute;
import org.jdom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the Sentinel-2 MSI XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Nicolas Ducoin
 */
public abstract class S2Metadata {

    private TileLayout[] tileLayouts;

    private Unmarshaller unmarshaller;

    private String psdString;

    public S2Metadata(TileLayout[] tileLayouts, JAXBContext context, String psdString) throws JAXBException {
        this.tileLayouts = tileLayouts;
        this.unmarshaller = context.createUnmarshaller();
        this.psdString = psdString;
    }

    public TileLayout[] getTileLayouts() {
        return tileLayouts;
    }


    protected Object updateAndUnmarshal(InputStream xmlStream) throws IOException, JAXBException {
        InputStream updatedStream = changePSDIfRequired(xmlStream, psdString);
        Object ob = unmarshaller.unmarshal(updatedStream);
        return ((JAXBElement) ob).getValue();
    }

    /**
     * from the input stream, replace the psd number in the header to allow jaxb to find
     * xsd files. This allows to open product with psd different from the reference one
     * for small changes.
     */
    static InputStream changePSDIfRequired(InputStream xmlStream, String psdNumber) throws IOException {
        InputStream updatedXmlStream;

        String xmlStreamAsString = IOUtils.toString(xmlStream);

        final String psd13String = "psd-" + psdNumber + ".sentinel2.eo.esa.int";
        if (!xmlStreamAsString.contains(psd13String)) {
            String regex="psd-\\d{2,}.sentinel2.eo.esa.int";
            String updatedXmlStreamAsString =
                    xmlStreamAsString.replaceAll(
                            regex, psd13String);
            updatedXmlStream = IOUtils.toInputStream(updatedXmlStreamAsString, "UTF-8");
        } else {
            updatedXmlStream = IOUtils.toInputStream(xmlStreamAsString, "UTF-8");
        }

        return updatedXmlStream;
    }

    protected MetadataElement parseAll(Element parent) {
        return parseTree(parent, null, new HashSet<>(Arrays.asList("Viewing_Incidence_Angles_Grids", "Sun_Angles_Grid")));
    }

    protected MetadataElement parseTree(Element element, MetadataElement mdParent, Set<String> excludes) {

        MetadataElement mdElement = new MetadataElement(element.getName());

        List attributes = element.getAttributes();
        for (Object a : attributes) {
            Attribute attribute = (Attribute) a;
            MetadataAttribute mdAttribute = new MetadataAttribute(attribute.getName().toUpperCase(), ProductData.createInstance(attribute.getValue()), true);
            mdElement.addAttribute(mdAttribute);
        }

        for (Object c : element.getChildren()) {
            Element child = (Element) c;
            String childName = child.getName();
            String childValue = child.getValue();
            if (!excludes.contains(childName)) {
                if (childValue != null && !childValue.isEmpty() && childName.equals(childName.toUpperCase())) {
                    MetadataAttribute mdAttribute = new MetadataAttribute(childName, ProductData.createInstance(childValue), true);
                    String unit = child.getAttributeValue("unit");
                    if (unit != null) {
                        mdAttribute.setUnit(unit);
                    }
                    mdElement.addAttribute(mdAttribute);
                } else {
                    parseTree(child, mdElement, excludes);
                }
            }
        }

        if (mdParent != null) {
            mdParent.addElement(mdElement);
        }

        return mdElement;
    }
}
