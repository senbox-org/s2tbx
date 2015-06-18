package org.esa.s2tbx.dataio.nitf;

import java.io.File;

/**
 * Created by kraftek on 6/18/2015.
 */
public class NITFConstants {
    public static final Class[] READER_INPUT_TYPES = new Class[] { String.class, File.class };
    public static final String[] FORMAT_NAMES = new String[] { "GenericNITF" };
    public static final String NTF_EXTENSION = ".ntf";
    public static final String NITF_DESCRIPTION = "NITF Image";
}
