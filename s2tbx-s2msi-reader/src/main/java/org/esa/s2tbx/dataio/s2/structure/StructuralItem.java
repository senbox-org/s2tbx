/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.s2.structure;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class StructuralItem implements FilenameFilter {
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
        if (!isRegExpr()) {
            return name.equals(pattern);
        } else {
            return regexpr.matcher(name).matches();
        }
    }

    public enum Type {FILE, DIRECTORY}

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

        if (isRegExpr) {
            regexpr = Pattern.compile(pattern);
        } else {
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
