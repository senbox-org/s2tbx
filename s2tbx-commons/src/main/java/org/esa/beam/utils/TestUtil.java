package org.esa.beam.utils;

import org.apache.commons.lang.SystemUtils;

import java.io.File;

import static org.junit.Assert.assertTrue;

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
