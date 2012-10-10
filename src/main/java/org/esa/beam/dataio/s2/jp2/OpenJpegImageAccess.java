package org.esa.beam.dataio.s2.jp2;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Binding for the OpenJPEG JPEG2000 library (http://www.openjpeg.org/).
 *
 * @author Norman Fomferra
 */
public interface OpenJpegImageAccess extends ImageAccess, Library {
    JasPerImageAccess INSTANCE = (JasPerImageAccess) Native.loadLibrary("jna_openjpeg",
                                                                        JasPerImageAccess.class);
}
