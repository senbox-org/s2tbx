package org.esa.s2tbx.dataio.jp2.internal;

import java.io.IOException;

/**
 * Created by Razvan Dumitrascu on 1/18/2017.
 */
public class JP2DataException extends IOException{

    public JP2DataException(String message) {
        super("JP2 Exception: " + message);
    }

    public JP2DataException(Throwable cause) {
        super("JP2 Exception", cause);
    }

}
