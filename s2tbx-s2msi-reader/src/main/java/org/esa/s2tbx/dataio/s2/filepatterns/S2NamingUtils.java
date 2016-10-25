package org.esa.s2tbx.dataio.s2.filepatterns;

/**
 * Created by obarrile on 25/10/2016.
 */
public class S2NamingUtils {

    public static final String DEFAULT_SEPARATOR = "_";

    public static String buildTemplate(S2NamingItems[] items, String separator){
        if(separator == null) {
            separator = new String(DEFAULT_SEPARATOR);
        }
        String templateString = "";
        for(S2NamingItems item : items) {
            if(item.PREFIX.startsWith(".") && templateString.length()>separator.length()) {
                templateString = templateString.substring(0, templateString.length() - separator.length());
            }
            templateString = templateString + item.PREFIX + item.template + separator;

        }
        if(templateString.length()>separator.length()) {
            templateString = templateString.substring(0, templateString.length() - separator.length());
        }
        return templateString;
    }

    public static String buildREGEX(S2NamingItems[] items, String separator){
        if(separator == null) {
            separator = new String(DEFAULT_SEPARATOR);
        }
        String templateREGEX = "";
        for(S2NamingItems item : items) {
            if(item.PREFIX.startsWith(".") && templateREGEX.length()>separator.length()) {
                templateREGEX = templateREGEX.substring(0, templateREGEX.length() - separator.length());
            }
            templateREGEX = templateREGEX + item.PREFIX + item.REGEX + separator;
        }
        if(templateREGEX.length()>separator.length()) {
            templateREGEX = templateREGEX.substring(0, templateREGEX.length() - separator.length());
        }
        return templateREGEX;
    }
}
