package org.esa.s2tbx.dataio;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jcoravu on 12/4/2019.
 */
public class AutoCloseInputStream extends FilterInputStream {

    private final Closeable closeable;

    public AutoCloseInputStream(InputStream inputStream, Closeable closeable) {
        super(inputStream);

        this.closeable = closeable;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            this.closeable.close();
        }
    }
}
