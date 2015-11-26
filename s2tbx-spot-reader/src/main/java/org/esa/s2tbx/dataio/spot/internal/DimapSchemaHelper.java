/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio.spot.internal;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;
import org.esa.snap.core.datamodel.ProductData;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This utility class provides several methods to help with XSD schema validation
 * and type conversion (from XS types to BEAM types).
 *
 * @author Cosmin Cara
 */
public class DimapSchemaHelper {

    private final static String SCHEMA_FILE_PATH = "org/esa/s2tbx/dataio/spot/";

    @SuppressWarnings("CanBeFinal")
    private static Map<String, XSType> elementTypes;

    static {
        final XSOMParser schemaParser = new XSOMParser();
        final ClassLoader classLoader = DimapSchemaHelper.class.getClassLoader();
        try {
            URL schemaURL = classLoader.getResource(SCHEMA_FILE_PATH + "Spot_Scene.xsd");
            schemaParser.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    String resourcePath = SCHEMA_FILE_PATH + systemId.substring(systemId.lastIndexOf("/") + 1);
                    URL resourceURL = classLoader.getResource(resourcePath);
                    InputSource output = null;
                    if (resourceURL != null) {
                        output = new InputSource(resourceURL.getFile());
                    }
                    return output;
                }
            });
            schemaParser.parse(schemaURL);
            XSSchemaSet schemaSet;
            XSSchema schema;
            Map<String, XSElementDecl> elementDecls;
            elementTypes = new HashMap<String, XSType>();
            if ((schemaSet = schemaParser.getResult()) != null && (schema = schemaSet.getSchema(1)) != null) {
                elementDecls = schema.getElementDecls();
                for (String elementName : elementDecls.keySet()) {
                    XSElementDecl xsElementDecl = elementDecls.get(elementName);
                    XSType xsType = xsElementDecl.getType();
                    while (xsType != null && !xsType.isSimpleType() && !"anyType".equals(xsType.getName())) {
                        xsType = xsType.getBaseType();
                    }
                    if (xsType != null) {
                        elementTypes.put(elementName, xsType.getBaseType());
                    }
                }
            }
        } catch (SAXException e) {
            Logger.getLogger(DimapSchemaHelper.class.getName()).severe(e.getMessage());
        }
    }

    public static String[] getSchemaLocations() {
        String[] locations = new String[0];
        File location = new File(SCHEMA_FILE_PATH);
        if (location.exists()) {
            locations = location.list();
        }
        return locations;
    }

    public static ProductData createProductData(String elementName, String elementValue) {
        int beamType = XsTypeToMetadataType(elementTypes.get(elementName));
        return createInstance(beamType, elementValue);
    }

    private static int XsTypeToMetadataType(XSType xsType) {
        String name = "";
        if (xsType != null) {
            name = xsType.getName();
        }
        if ("byte".equalsIgnoreCase(name)) {
            return ProductData.TYPE_UINT8;
        } else if ("integer".equalsIgnoreCase(name)) {
            return ProductData.TYPE_INT32;
        } else if ("double".equalsIgnoreCase(name) ||
                "real".equalsIgnoreCase(name)) {
            return ProductData.TYPE_FLOAT32;
        } else if ("date".equals(name) ||
                "dateTime".equals(name)) {
            return ProductData.TYPE_UTC;
        } else {
            return ProductData.TYPE_ASCII;
        }
    }

    private static ProductData createInstance(int type, String value) {
        ProductData retVal = null;
        try {
            switch (type) {
                case ProductData.TYPE_UINT8:
                    retVal = ProductData.createInstance(type);
                    retVal.setElemUInt(Byte.parseByte(value));
                    break;
                case ProductData.TYPE_INT32:
                    retVal = ProductData.createInstance(type);
                    retVal.setElemInt(Integer.parseInt(value));
                    break;
                case ProductData.TYPE_FLOAT32:
                    retVal = ProductData.createInstance(type);
                    retVal.setElemFloat(Float.parseFloat(value));
                    break;
                case ProductData.TYPE_UTC:
                    try {
                        retVal = ProductData.UTC.parse(value);
                    } catch (ParseException e) {
                        retVal = new ProductData.ASCII(value);
                    }
                    break;
                default:
                    retVal = new ProductData.ASCII(value);
                    break;
            }
        } catch (Exception e) {
            retVal = new ProductData.ASCII(value);
        }
        return retVal;
    }
}
