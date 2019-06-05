package org.esa.s2tbx.lib.openjpeg;

import java.io.*;

/**
 * Created by jcoravu on 30/4/2019.
 */
public class InvalidContiguousCodestreamException extends IOException {

    public InvalidContiguousCodestreamException() {
        super();
    }

    public InvalidContiguousCodestreamException(String s) {
        super(s);
    }
}
