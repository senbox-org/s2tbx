package org.esa.s2tbx.dataio.jp2.internal;

import java.io.IOException;

/**
 * An  I/O exception thrown by the <code>JP2ProductWriter</code> class in order to signal internal I/O errors with
 * the product to be exported
 *
 *  @author  Razvan Dumitrascu
 *  @since 5.0.2
 */
public final class JP2DataException extends IOException{

    public JP2DataException(String message) {
        super("JP2 Exception: " + message);
    }

}
