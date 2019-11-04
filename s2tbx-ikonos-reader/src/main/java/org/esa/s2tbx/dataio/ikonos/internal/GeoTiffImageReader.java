package org.esa.s2tbx.dataio.ikonos.internal;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;
import it.geosolutions.imageioimpl.plugins.tiff.TIFFRenderedImage;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

/**
 * Created by jcoravu on 28/10/2019.
 */
public class GeoTiffImageReader {

    private static final int FIRST_IMAGE = 0;

    private final VirtualDirEx productDirectory;
    private final String tiffImageRelativeFilePath;

    private TIFFImageReader imageReader;

    public GeoTiffImageReader(VirtualDirEx productDirectory, String tiffImageRelativeFilePath) {
        this.productDirectory = productDirectory;
        this.tiffImageRelativeFilePath = tiffImageRelativeFilePath;
    }

    public synchronized Raster readRect(int sourceOffsetX, int sourceOffsetY, int sourceStepX, int sourceStepY,
                                         int destOffsetX, int destOffsetY, int destWidth, int destHeight)
                                         throws IOException {

        if (this.imageReader == null) {
            createImageReader();
        }

        ImageReadParam readParam = this.imageReader.getDefaultReadParam();
        int subsamplingXOffset = sourceOffsetX % sourceStepX;
        int subsamplingYOffset = sourceOffsetY % sourceStepY;
        readParam.setSourceSubsampling(sourceStepX, sourceStepY, subsamplingXOffset, subsamplingYOffset);
        RenderedImage subsampledImage = this.imageReader.readAsRenderedImage(FIRST_IMAGE, readParam);

        return subsampledImage.getData(new Rectangle(destOffsetX, destOffsetY, destWidth, destHeight));
    }

    public synchronized int getImageWidth() throws IOException {
        if (this.imageReader == null) {
            createImageReader();
        }
        return this.imageReader.getWidth(FIRST_IMAGE);
    }

    public synchronized int getImageHeight() throws IOException {
        if (this.imageReader == null) {
            createImageReader();
        }
        return this.imageReader.getHeight(FIRST_IMAGE);
    }

    public synchronized void close() {
        if (this.imageReader != null) {
            ImageInputStream imageInputStream = (ImageInputStream)this.imageReader.getInput();
            try {
                imageInputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private void createImageReader() throws IOException {
        FilePathInputStream inputStream = this.productDirectory.getInputStream(this.tiffImageRelativeFilePath);
        try {
            if (this.tiffImageRelativeFilePath.endsWith(IkonosConstants.IMAGE_ARCHIVE_EXTENSION)) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                try {
                    GZIPInputStream gzipInputStream = new GZIPInputStream(bufferedInputStream);
                    try {
                        ImageInputStream imageInputStream = ImageIO.createImageInputStream(gzipInputStream);
                        try {
                            this.imageReader = getTIFFImageReader(imageInputStream);
                        } finally {
                            if (this.imageReader == null) {
                                imageInputStream.close(); // failed to get the image reader
                            }
                        }
                    } finally {
                        if (this.imageReader == null) {
                            gzipInputStream.close(); // failed to get the image reader
                        }
                    }
                } finally {
                    if (this.imageReader == null) {
                        bufferedInputStream.close(); // failed to get the image reader
                    }
                }
            } else {
                ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
                try {
                    this.imageReader = getTIFFImageReader(imageInputStream);
                } finally {
                    if (this.imageReader == null) {
                        imageInputStream.close(); // failed to get the image reader
                    }
                }
            }
        } finally {
            if (this.imageReader == null) {
                inputStream.close(); // failed to get the image reader
            }
        }
    }

    public static TIFFImageReader getTIFFImageReader(ImageInputStream imageInputStream) throws IOException {
        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
        while (imageReaders.hasNext()) {
            ImageReader reader = imageReaders.next();
            if (reader instanceof TIFFImageReader) {
                TIFFImageReader imageReader = (TIFFImageReader) reader;
                imageReader.setInput(imageInputStream);
                return imageReader;
            }
        }
        throw new IOException("GeoTiff imageReader not found.");
    }

    public static int getTIFFImageDataType(TIFFImageReader imageReader) throws IOException {
        ImageReadParam readParam = imageReader.getDefaultReadParam();
        TIFFRenderedImage baseImage = (TIFFRenderedImage) imageReader.readAsRenderedImage(FIRST_IMAGE, readParam);
        SampleModel sampleModel = baseImage.getSampleModel();
        return sampleModel.getDataType();
    }
}
