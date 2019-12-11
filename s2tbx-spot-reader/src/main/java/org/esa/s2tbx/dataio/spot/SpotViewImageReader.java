package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.datamodel.ProductData;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * Created by jcoravu on 11/12/2019.
 */
public class SpotViewImageReader implements Closeable {

    private final ImageInputStream imageInputStream;
    private final int imageWidth;
    private final int imagePixelSize;

    public SpotViewImageReader(File inputFile, ByteOrder rasterByteOrder, int imageWidth, int imagePixelSize) throws IOException {
        this.imageInputStream = ImageIO.createImageInputStream(inputFile);
        this.imageInputStream.setByteOrder(rasterByteOrder);
        this.imageWidth = imageWidth;
        this.imagePixelSize = imagePixelSize;
    }

    @Override
    public void close() {
        try {
            this.imageInputStream.close();
        } catch (IOException e) {
            // ignore
        }
    }

    public void readBandRasterData(int bandIndex, int numBands, int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                   int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer)
                                   throws IOException {

        long lineSizeInBytes = (long) this.imageWidth * (long) this.imagePixelSize;
        int sourceMaxY = sourceOffsetY + sourceHeight - 1;
        int destPos = 0;
        for (int sourceY = sourceOffsetY; sourceY <= sourceMaxY; sourceY += sourceStepY) {
            long lineStartPos = (sourceY * numBands * lineSizeInBytes) + (bandIndex * lineSizeInBytes);
            this.imageInputStream.seek(lineStartPos + (destBuffer.getElemSize() * sourceOffsetX));
            destBuffer.readFrom(destPos, destWidth, imageInputStream);
            destPos += destWidth;
        }
    }
}
