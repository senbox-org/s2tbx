package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.S2FileNamingTemplate;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingItems;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 24/10/2016.
 */
public class L1cNamingConventionSAFECompactSingle implements INamingConvention {

    public static final String productDirNameConvention = S2NamingItems.MISSION_ID.template + "_" +
            S2NamingItems.SITE_CENTRE_PRODUCT.template + "_" +
            S2NamingItems.PRODUCT_DISCRIMINATOR.template +
            "_N" + S2NamingItems.PRODUCTION_BASELINE.template + "_R" +
            S2NamingItems.RELATIVE_ORBIT.template + "_T" +
            S2NamingItems.TILE_NUMBER.template +"_" +
            S2NamingItems.STOP_TIME.template + "." + S2NamingItems.FORMAT.template;
    public static final String productDirREGEX = S2NamingItems.MISSION_ID.REGEX + "_" +
            S2NamingItems.SITE_CENTRE_PRODUCT.REGEX + "_" +
            S2NamingItems.PRODUCT_DISCRIMINATOR.REGEX +
            "_N" + S2NamingItems.PRODUCTION_BASELINE.REGEX + "_R" +
            S2NamingItems.RELATIVE_ORBIT.REGEX + "_T" +
            S2NamingItems.TILE_NUMBER.REGEX +"_" +
            S2NamingItems.STOP_TIME.REGEX + "." + S2NamingItems.FORMAT.REGEX;

    public static final String productXmlNameConvention = "MTD_MSIL1C.xml";
    public static final String productXmlREGEX = "MTD_MSIL1C.xml";

    public static final String datastripDirNameConvention = "DS_" +
            S2NamingItems.SITE_CENTRE.template + "_" +
            S2NamingItems.CREATION_DATE.template +
            "_S" + S2NamingItems.STOP_TIME.template;
    public static final String datastripDirREGEX = "DS_" +
            S2NamingItems.SITE_CENTRE.REGEX + "_" +
            S2NamingItems.CREATION_DATE.REGEX +
            "_S" + S2NamingItems.STOP_TIME.REGEX;

    public static final String datastripXmlNameConvention = "MTD_DS.xml";
    public static final String datastripXmlREGEX = "MTD_DS.xml";



    public static final String granuleDirNameConvention = "L1C_" +
            "_T" + S2NamingItems.TILE_NUMBER.template +
            "_A" + S2NamingItems.ABSOLUTE_ORBIT.template +
            "_" +S2NamingItems.STOP_TIME.template;
    public static final String granuleDirREGEX = "L1C_" +
            "_T" + S2NamingItems.TILE_NUMBER.REGEX +
            "_A" + S2NamingItems.ABSOLUTE_ORBIT.REGEX +
            "_" +S2NamingItems.STOP_TIME.REGEX;

    public static final String granuleXmlNameConvention = "MTD_TL.xml";
    public static final String granuleXmlREGEX = "MTD_TL.xml";

    public static final String spectralBandImageNameConvention =
            "T" + S2NamingItems.TILE_NUMBER.template +
                    "_" + S2NamingItems.DATATAKE_SENSING_START.template +
                    "_" + S2NamingItems.BAND_FILE_ID.template + ".jp2";
    public static final String spectralBandImageREGEX =
            "T" + S2NamingItems.TILE_NUMBER.REGEX +
            "_" + S2NamingItems.DATATAKE_SENSING_START.REGEX +
            "_" + S2NamingItems.BAND_FILE_ID.REGEX + ".jp2";



    final S2FileNamingTemplate productDirTemplate;
    final S2FileNamingTemplate productXmlTemplate;
    final S2FileNamingTemplate datastripDirTemplate;
    final S2FileNamingTemplate datastripXmlTemplate;
    final S2FileNamingTemplate granuleDirTemplate;
    final S2FileNamingTemplate granuleXmlTemplate;

    public L1cNamingConventionSAFECompactSingle() {
        productDirTemplate = new S2FileNamingTemplate(productDirNameConvention,productDirREGEX);
        productXmlTemplate = new S2FileNamingTemplate(productXmlNameConvention,productXmlREGEX);
        datastripDirTemplate = new S2FileNamingTemplate(datastripDirNameConvention,datastripDirREGEX);
        datastripXmlTemplate = new S2FileNamingTemplate(datastripXmlNameConvention,datastripXmlREGEX);
        granuleDirTemplate = new S2FileNamingTemplate(granuleDirNameConvention,granuleDirREGEX);
        granuleXmlTemplate = new S2FileNamingTemplate(granuleXmlNameConvention,granuleXmlREGEX);
    }

    public static S2FileNamingTemplate getStaticProductXmlTemplate() {
        return new S2FileNamingTemplate(productXmlNameConvention,productXmlREGEX);
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

    @Override
    public S2FileNamingTemplate getSpectralBandImageFileTemplate(String bandId) {
        S2FileNamingTemplate spectralBandImage;
        //replace the band id
        HashMap<S2NamingItems,String> values = new HashMap<>();
        values.put(S2NamingItems.BAND_FILE_ID,bandId);
        String templateString = S2FileNamingTemplate.replaceTemplate(spectralBandImageNameConvention,values);

        //create the S2FileNamingTemplate
        spectralBandImage= new S2FileNamingTemplate(templateString,spectralBandImageREGEX);

        return spectralBandImage;
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
