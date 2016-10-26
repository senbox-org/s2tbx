package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.S2FileNamingTemplate;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingItems;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingUtils;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.ortho.S2CRSHelper;
import org.esa.s2tbx.dataio.s2.ortho.S2ProductCRSCache;
import org.esa.s2tbx.dataio.s2.ortho.S2ProductCRSCacheEntry;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleMetadataFilename;
import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.esa.s2tbx.dataio.s2.ortho.S2CRSHelper.tileIdentifierToEPSG;

/**
 * Created by obarrile on 19/10/2016.
 */
public class L1cNamingConventionSAFE implements INamingConvention {

    public static final S2NamingItems[] productDirNameConvention = {S2NamingItems.MISSION_ID,
                                                                    S2NamingItems.FILE_CLASS,
                                                                    S2NamingItems.FILE_TYPE_PRODUCT_DIR,
                                                                    S2NamingItems.SITE_CENTRE_PRODUCT,
                                                                    S2NamingItems.PRODUCT_DISCRIMINATOR,
                                                                    S2NamingItems.RELATIVE_ORBIT,
                                                                    S2NamingItems.APPLICABILITY_TIME_PERIOD,
                                                                    S2NamingItems.FORMAT_SAFE};

    public static final S2NamingItems[] productXmlNameConvention = {
            S2NamingItems.MISSION_ID,
            S2NamingItems.FILE_CLASS,
            S2NamingItems.FILE_TYPE_PRODUCT_XML,
            S2NamingItems.SITE_CENTRE_PRODUCT,
            S2NamingItems.PRODUCT_DISCRIMINATOR,
            S2NamingItems.RELATIVE_ORBIT,
            S2NamingItems.APPLICABILITY_TIME_PERIOD,
            S2NamingItems.FORMAT_XML
            };

    public static final S2NamingItems[] granuleDirNameConvention = {
            S2NamingItems.MISSION_ID,
            S2NamingItems.FILE_CLASS,
            S2NamingItems.FILE_TYPE_GRANULE_DIR,
            S2NamingItems.SITE_CENTRE,
            S2NamingItems.CREATION_DATE,
            S2NamingItems.ABSOLUTE_ORBIT,
            S2NamingItems.TILE_NUMBER,
            S2NamingItems.PROCESSING_BASELINE
    };

    public static final S2NamingItems[] granuleXmlNameConvention = {
            S2NamingItems.MISSION_ID,
            S2NamingItems.FILE_CLASS,
            S2NamingItems.FILE_TYPE_GRANULE_XML,
            S2NamingItems.SITE_CENTRE,
            S2NamingItems.CREATION_DATE,
            S2NamingItems.ABSOLUTE_ORBIT,
            S2NamingItems.TILE_NUMBER,
            S2NamingItems.FORMAT_XML
    };

    public static final S2NamingItems[] datastripDirNameConvention = {
            S2NamingItems.MISSION_ID,
            S2NamingItems.FILE_CLASS,
            S2NamingItems.FILE_TYPE_DATASTRIP_DIR,
            S2NamingItems.SITE_CENTRE,
            S2NamingItems.CREATION_DATE,
            S2NamingItems.APPLICABILITY_START,
            S2NamingItems.PROCESSING_BASELINE
    };

    public static final S2NamingItems[] datastripXmlNameConvention = {
            S2NamingItems.MISSION_ID,
            S2NamingItems.FILE_CLASS,
            S2NamingItems.FILE_TYPE_DATASTRIP_XML,
            S2NamingItems.SITE_CENTRE,
            S2NamingItems.CREATION_DATE,
            S2NamingItems.APPLICABILITY_START,
            S2NamingItems.FORMAT_XML
    };

    public static final S2NamingItems[] imageNameConvention = {
            S2NamingItems.MISSION_ID,
            S2NamingItems.FILE_CLASS,
            S2NamingItems.FILE_TYPE_GRANULE_DIR,
            S2NamingItems.SITE_CENTRE,
            S2NamingItems.CREATION_DATE,
            S2NamingItems.ABSOLUTE_ORBIT,
            S2NamingItems.TILE_NUMBER,
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

    public L1cNamingConventionSAFE() {
        productDirTemplate = new S2FileNamingTemplate(productDirNameConvention,null);
        productXmlTemplate = new S2FileNamingTemplate(productXmlNameConvention,null);
        datastripDirTemplate = new S2FileNamingTemplate(datastripDirNameConvention,null);
        datastripXmlTemplate = new S2FileNamingTemplate(datastripXmlNameConvention,null);
        granuleDirTemplate = new S2FileNamingTemplate(granuleDirNameConvention,null);
        granuleXmlTemplate = new S2FileNamingTemplate(granuleXmlNameConvention,null);
        imageTemplate = new S2FileNamingTemplate(imageNameConvention,null);
    }

    @Override
    public String getConventionID() {
        return "L1C-SAFE";
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
    /*public static final String productDirNameConvention = S2NamingItems.MISSION_ID.template + "_" +
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

    public static final String spectralBandImageNameConvention = S2NamingItems.MISSION_ID.template + "_" +
            S2NamingItems.FILE_CLASS.template + "_" +
            S2NamingItems.FILE_TYPE_GRANULE.template + "_" +
            S2NamingItems.SITE_CENTRE.template + "_" +
            S2NamingItems.CREATION_DATE.template + "_A" +
            S2NamingItems.ABSOLUTE_ORBIT.template + "_T" +
            S2NamingItems.TILE_NUMBER.template +
            "_" + S2NamingItems.BAND_FILE_ID.template + ".jp2";
    public static final String spectralBandImageREGEX = S2NamingItems.MISSION_ID.REGEX + "_" +
            S2NamingItems.FILE_CLASS.REGEX + "_" +
            S2NamingItems.FILE_TYPE_GRANULE.REGEX + "_" +
            S2NamingItems.SITE_CENTRE.REGEX + "_" +
            S2NamingItems.CREATION_DATE.REGEX + "_A" +
            S2NamingItems.ABSOLUTE_ORBIT.REGEX + "_T" +
            S2NamingItems.TILE_NUMBER.REGEX +
            "_" + S2NamingItems.BAND_FILE_ID.REGEX + ".jp2";



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
    }*/

    public static boolean productMatches(String filename) {
        Pattern PATTERN = Pattern.compile(S2NamingUtils.buildREGEX(productXmlNameConvention,"_"));
        final Matcher matcher = PATTERN.matcher(filename);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean productDirMatches(String filename) {
        Pattern PATTERN = Pattern.compile(S2NamingUtils.buildREGEX(productDirNameConvention,"_"));
        final Matcher matcher = PATTERN.matcher(filename);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean granuleMatches(String filename) {
        Pattern PATTERN = Pattern.compile(S2NamingUtils.buildREGEX(granuleXmlNameConvention,"_"));
        final Matcher matcher = PATTERN.matcher(filename);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean granuleDirMatches(String filename) {
        Pattern PATTERN = Pattern.compile(S2NamingUtils.buildREGEX(granuleDirNameConvention,"_"));
        final Matcher matcher = PATTERN.matcher(filename);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static ArrayList<Pattern> getXmlInputPatterns() {
        ArrayList<Pattern> patterns = new ArrayList<>();
        patterns.add(Pattern.compile(S2NamingUtils.buildREGEX(granuleXmlNameConvention,"_")));
        patterns.add(Pattern.compile(S2NamingUtils.buildREGEX(productXmlNameConvention,"_")));
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

    //TODO
    @Override
    public S2ProductCRSCacheEntry createCacheEntry(Path path) {
        Set<String> epsgCodeList = new HashSet<>();
        S2Config.Sentinel2ProductLevel level;
        S2Config.Sentinel2InputType inputType;

        String filename = path.getFileName().toString();
        if(productMatches(filename)) {
            inputType = S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA;
            level = S2Config.levelString2ProductLevel("L1C");//TODO
            S2ProductFilename productFilename = S2ProductFilename.create(filename);
            if (productFilename != null) {
                if (level == S2Config.Sentinel2ProductLevel.L1C || level == S2Config.Sentinel2ProductLevel.L2A
                        || level == S2Config.Sentinel2ProductLevel.L3) {
                    File granuleFolder = path.resolveSibling("GRANULE").toFile();
                    if(!granuleFolder.exists() || !granuleFolder.isDirectory()) {
                        SystemUtils.LOG.warning("Invalid Sentinel-2 product: 'GRANULE' folder containing at least one granule is required");
                        return null;
                    }
                    if(granuleFolder.listFiles() == null || granuleFolder.listFiles().length == 0) {
                        SystemUtils.LOG.warning("Invalid Sentinel-2 product: 'GRANULE' folder must contain at least one granule");
                        return null;
                    }
                    for (File granule : granuleFolder.listFiles()) {
                        if (granule.isDirectory()) {
                            //TODO bien
                            S2OrthoGranuleDirFilename granuleDirFilename = S2OrthoGranuleDirFilename.create(granule.getName());
                            String epsgCode = S2CRSHelper.tileIdentifierToEPSG(granuleDirFilename.tileNumber);
                            epsgCodeList.add(epsgCode);
                        }
                    }
                }
            }
        } else if(granuleMatches(filename)) {
            inputType = S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA;
            level = S2Config.levelString2ProductLevel("L1C");//TODO
            //TODO
            S2OrthoGranuleMetadataFilename granuleMetadataFilename = S2OrthoGranuleMetadataFilename.create(filename);
            if (granuleMetadataFilename != null &&
                    (level == S2Config.Sentinel2ProductLevel.L1C || level == S2Config.Sentinel2ProductLevel.L2A
                            || level == S2Config.Sentinel2ProductLevel.L3)) {
                String tileId = granuleMetadataFilename.tileNumber;
                String epsg = tileIdentifierToEPSG(tileId);
                epsgCodeList.add(epsg);
            }
        } else {
            return null;
        }

        return new S2ProductCRSCacheEntry(epsgCodeList,level,inputType,this);
    }
}
