package org.esa.s2tbx.dataio.s2.filepatterns;

import com.sun.org.apache.regexp.internal.RE;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 19/10/2016.
 */
public class S2FileNamingTemplate {
    private final String template;
    private final String REGEX;
    private final Pattern PATTERN;

    public S2FileNamingTemplate(String template, String REGEX) {
        this.template = template;
        this.REGEX = REGEX;
        PATTERN = Pattern.compile(REGEX);
    }

    public S2FileNamingTemplate (S2NamingItems[] items, String separator) {
        //Compute template
        this.template = S2NamingUtils.buildTemplate(items, separator);
        //Compute regex and PATTERN
        this.REGEX = S2NamingUtils.buildREGEX(items, separator);
        PATTERN = Pattern.compile(this.REGEX);
    }

    public boolean matches(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        return matcher.matches();
    }

    public String getFileName(HashMap<S2NamingItems, String> namingItems) {
        return replaceTemplate(template, namingItems);
    }

    public String getRegex() {
        return REGEX;
    }

    public static String replaceTemplate(String template, HashMap<S2NamingItems, String> namingItems) {
        String filename = template;
        if(namingItems == null) {
            return filename;
        }
        for(Map.Entry<S2NamingItems, String> entry : namingItems.entrySet()) {
            S2NamingItems key = entry.getKey();
            String value = entry.getValue();
            filename = filename.replace(key.template,value);
        }
        return filename;
    }
}
