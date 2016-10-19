package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.S2FileNamingConstants;
import org.esa.s2tbx.dataio.s2.filepatterns.S2FileNamingTemplate;


import java.nio.file.Path;

/**
 * Created by obarrile on 19/10/2016.
 */
public class L1cNamingConventionSAFE implements INamingConvention {
    public static final String productDirNameConvention = S2FileNamingConstants.MISSION_ID + "_" +
                                                          S2FileNamingConstants.FILE_CLASS + "_" +
                                                          "PRD_MSIL1C_" +
                                                          /*S2FileNamingConstants.SITE_CENTRE*/"PDMC" + "_" +
                                                          S2FileNamingConstants.CREATION_DATE + "_" +
                                                          S2FileNamingConstants.RELATIVE_ORBIT + "_V" +
                                                          S2FileNamingConstants.START_TIME + "_" +
                                                          S2FileNamingConstants.STOP_TIME + ".SAFE";
    //TODO
    public static final String productDirREGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})([A-Z|0-9|_]+)(\\.[A-Z|a-z|0-9]{3,4})?";
    public static final String productXmlNameConvention = S2FileNamingConstants.MISSION_ID + "_" +
            S2FileNamingConstants.FILE_CLASS + "_" +
            "MSI_L1C_DS_EPA__" +
            S2FileNamingConstants.SITE_CENTRE + "_" +
            S2FileNamingConstants.CREATION_DATE + "_" +
            S2FileNamingConstants.RELATIVE_ORBIT + "_V" +
            S2FileNamingConstants.START_TIME + "_" +
            S2FileNamingConstants.STOP_TIME + ".xml";
    public static final String productXmlREGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})([A-Z|0-9|_]+)(\\.[A-Z|a-z|0-9]{3,4})?";

    public static final String datastripDirNameConvention = S2FileNamingConstants.MISSION_ID + "_" +
            S2FileNamingConstants.FILE_CLASS + "_" +
            "PRD_MSI_L1C_" +
            S2FileNamingConstants.SITE_CENTRE + "_" +
            S2FileNamingConstants.CREATION_DATE + "_" +
            S2FileNamingConstants.RELATIVE_ORBIT + "_V" +
            S2FileNamingConstants.START_TIME + "_" +
            S2FileNamingConstants.STOP_TIME + ".SAFE";
    public static final String datastripDirREGEX ="";
    public static final String datastripXmlNameConvention ="";
    public static final String datastripXmlREGEX ="";
    public static final String granuleDirNameConvention ="";
    public static final String granuleDirREGEX ="";
    public static final String granuleXmlNameConvention ="";
    public static final String granuleXmlREGEX ="";



    final S2FileNamingTemplate productDirTemplate;
    final S2FileNamingTemplate productXmlTemplate;
    final S2FileNamingTemplate datastripDirTemplate;
    final S2FileNamingTemplate datastripXmlTemplate;
    final S2FileNamingTemplate granuleDirTemplate;
    final S2FileNamingTemplate granuleXmlTemplate;

    public L1cNamingConventionSAFE() {
        productDirTemplate = new S2FileNamingTemplate(productDirNameConvention,productDirREGEX);
        productXmlTemplate = new S2FileNamingTemplate(productXmlNameConvention,productXmlREGEX);
        datastripDirTemplate = new S2FileNamingTemplate(datastripDirNameConvention,datastripDirREGEX);
        datastripXmlTemplate = new S2FileNamingTemplate(datastripXmlNameConvention,datastripXmlREGEX);
        granuleDirTemplate = new S2FileNamingTemplate(granuleDirNameConvention,granuleDirREGEX);
        granuleXmlTemplate = new S2FileNamingTemplate(granuleXmlNameConvention,granuleXmlREGEX);
    }

    @Override
    public String getConventionID() {
        return "SAFE";
    }

    @Override
    public S2FileNamingTemplate getProductDirTemplate() {
        return productDirTemplate;
    }

    @Override
    public S2FileNamingTemplate getProductXmlTemplate() {
        return productXmlTemplate;
    }

    @Override
    public S2FileNamingTemplate getDatastripDirTemplate() {
        return datastripDirTemplate;
    }

    @Override
    public S2FileNamingTemplate getDatastripXmlTemplate() {
        return datastripXmlTemplate;
    }

    @Override
    public S2FileNamingTemplate getGranuleDirTemplate() {
        return granuleDirTemplate;
    }

    @Override
    public S2FileNamingTemplate getGranuleXmlTemplate() {
        return granuleXmlTemplate;
    }

    public static boolean productMatches(String filename) {
        //TODO pattern match con granule xml and dir xml
        return true;
    }

    public static boolean granuleMatches(String filename) {
        //TODO pattern match con granule xml and dir xml
        return true;
    }

}
