/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio;

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
        File inputFile = (File) input;
        return super.createInputStreamInstance(new RandomAccessFile(inputFile.getAbsolutePath(), "rw").getChannel(), useCache, cacheDir);
    }

    @Override
    public Class<?> getInputClass() {
        return File.class;
    }
}
