package org.esa.beam.dataio.rapideye;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class TestUtil {

    public static String ABSOLUTE_PATH = "D:\\Sentinel2_PROJECT\\Satellite_Imagery\\TestingJUnitFiles";

    public static File getTestFile(String file) {
        final File testTgz = getTestFileOrDirectory(file);
        assertTrue(testTgz.isFile());
        return testTgz;
    }

    public static File getTestDirectory(String file) {
        final File testTgz = getTestFileOrDirectory(file);
        assertTrue(testTgz.isDirectory());
        return testTgz;
    }

    private static File getTestFileOrDirectory(String file) {
        /*File testTgz = new File("./s2tbx-spot-reader/src/test/resources/org/esa/beam/dataio/spot/" + file);
        if (!testTgz.exists()) {
            testTgz = new File("./src/test/resources/org/esa/beam/dataio/spot/" + file);
        }
        return testTgz;*/
        return new File(ABSOLUTE_PATH, file);
    }
}
