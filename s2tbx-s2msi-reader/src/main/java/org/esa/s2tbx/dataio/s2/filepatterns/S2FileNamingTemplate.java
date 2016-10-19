package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.s2tbx.dataio.s2.S2Metadata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 19/10/2016.
 */
public class S2FileNamingTemplate {
    private final String template;
    //private final String REGEX;
    private final Pattern PATTERN;

    public S2FileNamingTemplate(String template, String REGEX) {
        this.template = template;
        //this.REGEX = REGEX;
        PATTERN = Pattern.compile(REGEX);
    }

    public boolean matches(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        return matcher.matches();
    }

    public String getFileName(S2FileNamingItems namingItems) {
        return replaceTemplate(template, namingItems);
    }

    public static String replaceTemplate(String template, S2FileNamingItems namingItems) {
        String filename = template.replace(S2FileNamingConstants.MISSION_ID, namingItems.getMissionID())
                .replace(S2FileNamingConstants.SITE_CENTRE, namingItems.getSiteCentre())
                .replace(S2FileNamingConstants.CREATION_DATE, namingItems.getCreationDate())
                .replace(S2FileNamingConstants.ABSOLUTE_ORBIT, namingItems.getAbsoluteOrbit())
                .replace(S2FileNamingConstants.TILE_NUMBER, namingItems.getTileNumber())
                .replace(S2FileNamingConstants.RESOLUTION, namingItems.getResolution())
                .replace(S2FileNamingConstants.RELATIVE_ORBIT, namingItems.getRelativeOrbit())
                .replace(S2FileNamingConstants.DATATAKE_SENSING_START, namingItems.getDatatakeSensingStart())
                .replace(S2FileNamingConstants.PRODUCTION_BASELINE, namingItems.getProductBaseline())
                .replace(S2FileNamingConstants.PRODUCT_DISCRIM, namingItems.getProductDiscrim())
                .replace(S2FileNamingConstants.START_TIME, namingItems.getStartTime())
                .replace(S2FileNamingConstants.STOP_TIME, namingItems.getStopTime());
        return filename;
    }
}
