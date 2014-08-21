package org.esa.beam.dataio;

import com.sun.media.imageioimpl.stream.ChannelImageInputStreamSpi;

import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Wrapper over <code>ChannelImageInputStreamSpi</code>.
 * This is used to create a mapped file I/O (using NIO) instead of the "classic" FileImageInputStream.
 *
 * @author Cosmin Cara
 */
public class FileImageInputStreamSpi extends ChannelImageInputStreamSpi {

    @Override
    public ImageInputStream createInputStreamInstance(Object input, boolean useCache, File cacheDir) throws IOException {
        if (!File.class.isInstance(input))
            throw new IllegalArgumentException("This SPI accepts only java.io.File");
        File inputFile = (File)input;
        return super.createInputStreamInstance(new RandomAccessFile(inputFile.getAbsolutePath(), "rw").getChannel(), useCache, cacheDir);
    }

    @Override
    public Class<?> getInputClass() {
        return File.class;
    }
}
