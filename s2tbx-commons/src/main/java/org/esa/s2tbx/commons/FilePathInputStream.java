package org.esa.s2tbx.commons;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Created by jcoravu on 30/4/2019.
 */
public class FilePathInputStream extends FilterInputStream {

    private final Path filePath;
    private final Closeable closeable;

    public FilePathInputStream(Path filePath, InputStream inputStream, Closeable closeable) {
        super(inputStream);

        this.filePath = filePath;
        this.closeable = closeable;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if (this.closeable != null) {
                this.closeable.close();
            }
        }
    }

    public Path getFilePath() {
        return filePath;
    }
}
