package org.esa.s2tbx.dataio.s2.l1c;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.esa.snap.core.datamodel.ProductData;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by obarrile on 30/09/2016.
 */
public class L1cMetadataPSD13Helper {

    private final static String PRODUCT_SCHEMA_FILE_PATH = "schemas/PSD13/S2_User_Product_Level-1C_Metadata.xsd";
    private final static String GRANULE_SCHEMA_FILE_PATH = "schemas/L2A_PSD12/S2_PDI_Level-1C_Tile_Metadata.xsd";
    private final static String DATASTRIP_SCHEMA_FILE_PATH = "schemas/L2A_PSD12/S2_PDI_Level-1C_Datastrip_Metadata.xsd";
    private final static String SCHEMA13_BASE_PATH = "schemas/PSD13/";
    private final static String SCHEMA12_BASE_PATH = "schemas/L2A_PSD12/";

    private static Map<String, XSType> elementTypes;

    /*static {
        final XSOMParser schemaParser = new XSOMParser();
        final ClassLoader classLoader = L1cMetadataPSD13Helper.class.getClassLoader();
        try {
            URL schemaURL = classLoader.getResource(SCHEMA_FILE_PATH + "S2_User_Product_Level-1C_Metadata.xsd");
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
            Logger.getLogger(L1cMetadataPSD13Helper.class.getName()).severe(e.getMessage());
        }
    }*/

    public static String[] getProductSchemaLocations() {

        String[] locations = new String[1];
        locations[0] = PRODUCT_SCHEMA_FILE_PATH;

        return locations;
    }

    public static String[] getGranuleSchemaLocations() {

        String[] locations = new String[1];
        locations[0] = GRANULE_SCHEMA_FILE_PATH;

        return locations;
    }

    public static String[] getDatastripSchemaLocations() {

        String[] locations = new String[1];
        locations[0] = DATASTRIP_SCHEMA_FILE_PATH;

        return locations;
    }

    public static String getSchemaBasePath(String psd) {

       if(psd.equals("PSD13")) {
           return SCHEMA13_BASE_PATH;
       }
        return SCHEMA12_BASE_PATH;
    }


    private List<String> getResourceFiles(String path ) throws IOException {
        List<String> filenames = new ArrayList<>();

        try(
                InputStream in = getResourceAsStream(path );
                BufferedReader br = new BufferedReader(new InputStreamReader(in ) ) ) {
            String resource;

            while( (resource = br.readLine()) != null ) {
                filenames.add( resource );
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream( String resource ) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream( resource );

        return in == null ? getClass().getResourceAsStream( resource ) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
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
