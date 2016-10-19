package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.S2FileNamingTemplate;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingItems;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 19/10/2016.
 */
public class L1cNamingConventionSAFE implements INamingConvention {
    public static final String productDirNameConvention = S2NamingItems.MISSION_ID.template + "_" +
            S2NamingItems.FILE_CLASS.template + "_" +
            S2NamingItems.FILE_TYPE_PRODUCT.template + "_" +
            S2NamingItems.SITE_CENTRE_PRODUCT.template + "_" +
            S2NamingItems.PRODUCT_DISCRIMINATOR.template + "_R" +
            S2NamingItems.RELATIVE_ORBIT.template + "_V" +
            S2NamingItems.START_TIME.template + "_" +
            S2NamingItems.STOP_TIME.template + "." + S2NamingItems.FORMAT.template;
    public static final String productDirREGEX = S2NamingItems.MISSION_ID.REGEX + "_" +
            S2NamingItems.FILE_CLASS.REGEX + "_" +
            S2NamingItems.FILE_TYPE_PRODUCT.REGEX + "_" +
            S2NamingItems.SITE_CENTRE_PRODUCT.REGEX + "_" +
            S2NamingItems.PRODUCT_DISCRIMINATOR.REGEX + "_R" +
            S2NamingItems.RELATIVE_ORBIT.REGEX + "_V" +
            S2NamingItems.START_TIME.REGEX + "_" +
            S2NamingItems.STOP_TIME.REGEX + "." + S2NamingItems.FORMAT.REGEX;
    public static final String productXmlNameConvention = S2NamingItems.MISSION_ID.template + "_" +
            S2NamingItems.FILE_CLASS.template + "_" +
            S2NamingItems.FILE_TYPE_PRODUCT_XML.template + "_" +
            S2NamingItems.SITE_CENTRE_PRODUCT.template + "_" +
            S2NamingItems.PRODUCT_DISCRIMINATOR.template + "_R" +
            S2NamingItems.RELATIVE_ORBIT.template + "_V" +
            S2NamingItems.START_TIME.template + "_" +
            S2NamingItems.STOP_TIME.template + ".xml";
    public static final String productXmlREGEX = S2NamingItems.MISSION_ID.REGEX + "_" +
            S2NamingItems.FILE_CLASS.REGEX + "_" +
            S2NamingItems.FILE_TYPE_PRODUCT_XML.REGEX + "_" +
            S2NamingItems.SITE_CENTRE_PRODUCT.REGEX + "_" +
            S2NamingItems.PRODUCT_DISCRIMINATOR.REGEX + "_R" +
            S2NamingItems.RELATIVE_ORBIT.REGEX + "_V" +
            S2NamingItems.START_TIME.REGEX + "_" +
            S2NamingItems.STOP_TIME.REGEX + ".xml";
    public static final String datastripDirNameConvention = S2NamingItems.MISSION_ID.template + "_" +
            S2NamingItems.FILE_CLASS.template + "_" +
            S2NamingItems.FILE_TYPE_DATASTRIP.template + "_" +
            S2NamingItems.SITE_CENTRE.template + "_" +
            S2NamingItems.CREATION_DATE.template +
            "_S" + S2NamingItems.CREATION_DATE.template +
            "_N" + S2NamingItems.PRODUCTION_BASELINE.template;
    public static final String datastripDirREGEX = S2NamingItems.MISSION_ID.REGEX + "_" +
            S2NamingItems.FILE_CLASS.REGEX + "_" +
            S2NamingItems.FILE_TYPE_DATASTRIP.REGEX + "_" +
            S2NamingItems.SITE_CENTRE.REGEX + "_" +
            S2NamingItems.CREATION_DATE.REGEX +
            "_S" + S2NamingItems.CREATION_DATE.REGEX +
            "_N" + S2NamingItems.PRODUCTION_BASELINE.REGEX;
    public static final String datastripXmlNameConvention = S2NamingItems.MISSION_ID.template + "_" +
            S2NamingItems.FILE_CLASS.template + "_" +
            S2NamingItems.FILE_TYPE_DATASTRIP_XML.template + "_" +
            S2NamingItems.SITE_CENTRE.template + "_" +
            S2NamingItems.CREATION_DATE.template +
            "_S" + S2NamingItems.CREATION_DATE.template + ".xml";
    public static final String datastripXmlREGEX = S2NamingItems.MISSION_ID.REGEX + "_" +
            S2NamingItems.FILE_CLASS.REGEX + "_" +
            S2NamingItems.FILE_TYPE_DATASTRIP_XML.REGEX + "_" +
            S2NamingItems.SITE_CENTRE.REGEX + "_" +
            S2NamingItems.CREATION_DATE.REGEX +
            "_S" + S2NamingItems.CREATION_DATE.REGEX + ".xml";
    public static final String granuleDirNameConvention = S2NamingItems.MISSION_ID.template + "_" +
            S2NamingItems.FILE_CLASS.template + "_" +
            S2NamingItems.FILE_TYPE_GRANULE.template + "_" +
            S2NamingItems.SITE_CENTRE.template + "_" +
            S2NamingItems.CREATION_DATE.template + "_A" +
            S2NamingItems.ABSOLUTE_ORBIT.template + "_T" +
            S2NamingItems.TILE_NUMBER.template +
            "_N" + S2NamingItems.PRODUCTION_BASELINE.template;
    public static final String granuleDirREGEX = S2NamingItems.MISSION_ID.REGEX + "_" +
            S2NamingItems.FILE_CLASS.REGEX + "_" +
            S2NamingItems.FILE_TYPE_GRANULE.REGEX + "_" +
            S2NamingItems.SITE_CENTRE.REGEX + "_" +
            S2NamingItems.CREATION_DATE.REGEX + "_A" +
            S2NamingItems.ABSOLUTE_ORBIT.REGEX + "_T" +
            S2NamingItems.TILE_NUMBER.REGEX +
            "_N" + S2NamingItems.PRODUCTION_BASELINE.REGEX;
    public static final String granuleXmlNameConvention = S2NamingItems.MISSION_ID.template + "_" +
            S2NamingItems.FILE_CLASS.template + "_" +
            S2NamingItems.FILE_TYPE_GRANULE_XML.template + "_" +
            S2NamingItems.SITE_CENTRE.template + "_" +
            S2NamingItems.CREATION_DATE.template + "_A" +
            S2NamingItems.ABSOLUTE_ORBIT.template + "_T" +
            S2NamingItems.TILE_NUMBER.template + ".xml";
    public static final String granuleXmlREGEX = S2NamingItems.MISSION_ID.REGEX + "_" +
            S2NamingItems.FILE_CLASS.REGEX + "_" +
            S2NamingItems.FILE_TYPE_GRANULE_XML.REGEX + "_" +
            S2NamingItems.SITE_CENTRE.REGEX + "_" +
            S2NamingItems.CREATION_DATE.REGEX + "_A" +
            S2NamingItems.ABSOLUTE_ORBIT.REGEX + "_T" +
            S2NamingItems.TILE_NUMBER.REGEX + ".xml";



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
        Pattern PATTERN = Pattern.compile(productXmlREGEX);
        final Matcher matcher = PATTERN.matcher(filename);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean granuleMatches(String filename) {
        Pattern PATTERN = Pattern.compile(granuleXmlREGEX);
        final Matcher matcher = PATTERN.matcher(filename);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

}
