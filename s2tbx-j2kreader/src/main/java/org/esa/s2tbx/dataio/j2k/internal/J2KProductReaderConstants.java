package org.esa.s2tbx.dataio.j2k.internal;

import java.io.File;

/**
 * Created by kraftek on 7/9/2015.
 */
public class J2KProductReaderConstants {
    public static final Class[] INPUT_TYPES = new Class[] { String.class, File.class };
    public static final String[] FORMAT_NAMES = new String[] { "JPEG2000" };
    public static final String[] DEFAULT_EXTENSIONS = new String[] { ".jp2" };
    public static final String DESCRIPTION = "JPEG-2000 Files";
    public static final String JP2_INFO_FILE = "%s_dump.txt";
}
