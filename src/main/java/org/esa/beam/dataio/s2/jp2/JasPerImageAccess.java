package org.esa.beam.dataio.s2.jp2;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Binding for the JasPer JPEG2000 library (http://www.ece.uvic.ca/~frodo/jasper/).
 *
 * @author Norman Fomferra
 */
public interface JasPerImageAccess extends ImageAccess, Library {
    JasPerImageAccess INSTANCE = (JasPerImageAccess) Native.loadLibrary("jna_jasper",
                                                                        JasPerImageAccess.class);
}
