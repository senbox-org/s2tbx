package org.esa.beam.dataio.spot.internal;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.logging.BeamLogManager;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * This utility class provides several methods to help with XSD schema validation
 * and type conversion (from XS types to BEAM types).
 *
 * @author  Cosmin Cara
 */
public class DimapSchemaHelper {

    private final static String SCHEMA_FILE_PATH = "org/esa/beam/dataio/spot/";

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
            if ((schemaSet = schemaParser.getResult()) != null && (schema = schemaSet.getSchema(1)) != null) {
                elementDecls = schema.getElementDecls();
                elementTypes = new HashMap<String, XSType>(elementDecls.size());
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
            BeamLogManager.getSystemLogger().severe(e.getMessage());
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
        ProductData retVal;
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
        return retVal;
    }
}
