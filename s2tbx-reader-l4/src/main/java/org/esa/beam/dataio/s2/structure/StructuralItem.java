package org.esa.beam.dataio.s2.structure;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class StructuralItem implements FilenameFilter
{
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if and only if the name should be
     * included in the file list; <code>false</code> otherwise.
     */
    @Override
    public boolean accept(File dir, String name) {
        if (!isRegExpr())
        {
            boolean result = name.equals(pattern);
            return result;
        }
        else
        {
            boolean result = regexpr.matcher(name).matches();
            return result;
        }
    }

    public enum Type { FILE, DIRECTORY};
    final private boolean optional;
    final private Type type;
    final private boolean isRegExpr;
    final private String pattern;
    final private Pattern regexpr;

    public StructuralItem(boolean optional, Type type, boolean isRegExpr, String pattern) {
        this.optional = optional;
        this.type = type;
        this.isRegExpr = isRegExpr;
        this.pattern = pattern;

        if(isRegExpr)
        {
            regexpr = Pattern.compile(pattern);
        }
        else
        {
            regexpr = null;
        }
    }

    public boolean isOptional() {
        return optional;
    }

    public Type getType() {
        return type;
    }

    public boolean isRegExpr() {
        return isRegExpr;
    }

    public String getPattern() {
        return pattern;
    }
}
