package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.S2FileNamingTemplate;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingItems;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 24/10/2016.
 */
public class L1cNamingConventionSAFECompactSingle implements INamingConvention {

    public static final S2NamingItems[] productDirNameConvention = {S2NamingItems.MISSION_ID,
            S2NamingItems.FILE_CLASS,
            S2NamingItems.FILE_TYPE_PRODUCT_COMPACT,
            S2NamingItems.SITE_CENTRE_PRODUCT,
            S2NamingItems.DATATAKE_SENSING_START_TIME,
            S2NamingItems.PROCESSING_BASELINE_WITHOUT_POINT,
            S2NamingItems.RELATIVE_ORBIT,
            S2NamingItems.PRODUCT_DISCRIMINATOR,
            S2NamingItems.FORMAT_SAFE,
    };

    public static final String productXml = "MTD_MSIL1C.xml";

    public static final S2NamingItems[] granuleDirNameConvention = {
            S2NamingItems.LEVEL,
            S2NamingItems.TILE_NUMBER,
            S2NamingItems.ABSOLUTE_ORBIT,
            S2NamingItems.GRANULE_DISCRIMINATOR
    };

    public static final String granuleXml = "MTD_TL.xml";

    public static final S2NamingItems[] datastripDirNameConvention = {
            S2NamingItems.DATASTRIP_FLAG,
            S2NamingItems.SITE_CENTRE,
            S2NamingItems.CREATION_DATE,
            S2NamingItems.APPLICABILITY_START
    };

    public static final String datastripXml = "MTD_DS.xml";

    public static final S2NamingItems[] imageNameConvention = {
            S2NamingItems.TILE_NUMBER,
            S2NamingItems.DATATAKE_SENSING_START_TIME,
            S2NamingItems.BAND_INDEX,
            S2NamingItems.FORMAT_JP2
    };

    final S2FileNamingTemplate productDirTemplate;
    final S2FileNamingTemplate productXmlTemplate;
    final S2FileNamingTemplate datastripDirTemplate;
    final S2FileNamingTemplate datastripXmlTemplate;
    final S2FileNamingTemplate granuleDirTemplate;
    final S2FileNamingTemplate granuleXmlTemplate;
    final S2FileNamingTemplate imageTemplate;

    public L1cNamingConventionSAFECompactSingle() {
        productDirTemplate = new S2FileNamingTemplate(productDirNameConvention,"_");
        productXmlTemplate = new S2FileNamingTemplate(productXml,productXml);
        datastripDirTemplate = new S2FileNamingTemplate(datastripDirNameConvention,"_");
        datastripXmlTemplate = new S2FileNamingTemplate(datastripXml,datastripXml);
        granuleDirTemplate = new S2FileNamingTemplate(granuleDirNameConvention,"_");
        granuleXmlTemplate = new S2FileNamingTemplate(granuleXml,granuleXml);
        imageTemplate = new S2FileNamingTemplate(imageNameConvention,null);
    }

    public static S2FileNamingTemplate getStaticProductXmlTemplate() {
        return new S2FileNamingTemplate(productXml,productXml);
    }

    @Override
    public String getConventionID() {
        return "L1C-SAFECOMPACT";
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
        values.put(S2NamingItems.BAND_INDEX,bandId);
        String templateString = imageTemplate.getFileName(values);

        //create the S2FileNamingTemplate
        spectralBandImage= new S2FileNamingTemplate(templateString,imageTemplate.getRegex());

        return spectralBandImage;
    }

    public static boolean productMatches(String filename) {
        Pattern PATTERN = Pattern.compile(productXml);
        final Matcher matcher = PATTERN.matcher(filename);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean granuleMatches(String filename) {
        Pattern PATTERN = Pattern.compile(granuleXml);
        final Matcher matcher = PATTERN.matcher(filename);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }


    public static ArrayList<Pattern> getXmlInputPatterns() {
        ArrayList<Pattern> patterns = new ArrayList<>();
        patterns.add(Pattern.compile(productXml));
        patterns.add(Pattern.compile(granuleXml));
        return patterns;
    }

    public static ArrayList<Pattern> getDirInputPatterns() {
        ArrayList<Pattern> patterns = new ArrayList<>();
        patterns.add(Pattern.compile(S2NamingUtils.buildREGEX(granuleDirNameConvention,"_")));
        patterns.add(Pattern.compile(S2NamingUtils.buildREGEX(productDirNameConvention,"_")));
        return patterns;
    }

    public static ArrayList<Pattern> getInputPatterns() {
        ArrayList<Pattern> patterns = getXmlInputPatterns();
        ArrayList<Pattern> dirPatterns = getDirInputPatterns();
        for(Pattern pattern : dirPatterns) {
            patterns.add(pattern);
        }
        return patterns;
    }

}
