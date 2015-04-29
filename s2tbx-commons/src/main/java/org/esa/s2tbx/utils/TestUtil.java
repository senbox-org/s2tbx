/*
 *
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.utils;

import org.apache.commons.lang.SystemUtils;

import java.io.File;

import static org.junit.Assert.*;

public class TestUtil {

    // public static String ABSOLUTE_PATH = "D:\\Sentinel2_PROJECT\\Satellite_Imagery\\TestingJUnitFiles";
    public static final String PROPERTYNAME_DATA_DIR = "beam.reader.tests.data.dir";

    public static File getTestFile(String file) {
        final File testTgz = getTestFileOrDirectory(file);
        assertTrue(String.format("Looking for file: [%s]", testTgz.getAbsolutePath()), testTgz.isFile());
        return testTgz;
    }

    public static File getTestDirectory(String file) {
        final File testTgz = getTestFileOrDirectory(file);
        assertTrue(String.format("Is directory: [%s]", testTgz.getAbsolutePath()), testTgz.isDirectory());
        return testTgz;
    }

    private static File getTestFileOrDirectory(String file) {
        String partialPath = file;
        if(SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
        {
            partialPath = file.replaceAll("\\\\", "/");
        }

        String path = System.getProperty(PROPERTYNAME_DATA_DIR);
        return new File(path, partialPath);
    }
}
