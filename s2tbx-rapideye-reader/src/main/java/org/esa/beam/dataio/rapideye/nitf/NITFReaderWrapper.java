package org.esa.beam.dataio.rapideye.nitf;

import com.bc.ceres.core.ProgressMonitor;
import nitf.ImageSegment;
import nitf.NITFException;
import nitf.imageio.ImageIOUtils;
import nitf.imageio.NITFReaderSpi;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.logging.BeamLogManager;

import javax.imageio.ImageReadParam;
import javax.imageio.spi.IIORegistry;
import java.awt.*;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Wrapper class over the nitro-nitf reader.
 *
 * @author Cosmin Cara
 *
 */
public class NITFReaderWrapper {

    /*
     * We need this lock because I/O operations performed by
     * the underlying nitf.imageio.NITFReader are not thread safe.
     */
    protected static final Object lock = new Object();

    protected nitf.imageio.NITFReader reader;
    protected final Logger logger;
    protected int numImages;
    protected int numBands;
    protected boolean hasBandPerImage;
    protected int imageWidth;
    protected int imageHeight;

    static {
        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new NITFReaderSpi());
    }

    public NITFReaderWrapper(File file) throws IOException {
        logger = BeamLogManager.getSystemLogger();
        reader = (nitf.imageio.NITFReader) ImageIOUtils.getImageReader("nitf", file);
        try {
            imageWidth = reader.getWidth(0);
            imageHeight = reader.getHeight(0);
            numBands = getNumBands();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumBands() {
        if (numBands == 0) {
            try {
                ImageSegment[] imageSegments = reader.getRecord().getImages();
                numImages = imageSegments.length;
                if (numImages == 1) {
                    numBands = imageSegments[0].getSubheader().getBandCount();
                    hasBandPerImage = false;
                } else {
                    numBands = numImages;
                    hasBandPerImage = true;
                }
            } catch (NITFException e) {
                logger.severe(e.getMessage());
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }
        return numBands;
    }

    public int getWidth() {
        if (imageWidth == 0) {
            try {
                imageWidth = reader.getWidth(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageWidth;
    }

    public int getHeight() {
        if (imageHeight == 0) {
            try {
                imageHeight = reader.getHeight(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageHeight;
    }

    public void readBandData(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight,
                             int sourceStepX, int sourceStepY, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        pm.beginTask("Reading band ...", sourceHeight);
        try {
            ImageReadParam readParam = reader.getDefaultReadParam();
            readParam.setSourceBands(new int[]{0});
            readParam.setSourceSubsampling(sourceStepX, sourceStepY, 0, 0);
            readParam.setSourceRegion(new Rectangle(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight));
            Raster raster;
            synchronized (lock) {
                raster = reader.readRaster(0, readParam);
            }
            DataBufferUShort dataBuffer = (DataBufferUShort) raster.getDataBuffer();
            if (destBuffer.getType() == ProductData.TYPE_UINT16) {
                for (int i = 0; i < dataBuffer.getSize(); i++) {
                    if (pm.isCanceled()) {
                        break;
                    }
                    destBuffer.setElemUIntAt(i, swapBytes((short)dataBuffer.getElem(i)));
                }
            }
            pm.worked(1);
        } catch (IOException e) {
            logger.severe(e.getMessage());
            throw e;
        } finally {
            pm.done();
        }
    }

    public NITFMetadata getMetadata() {
        NITFMetadata metadata = new NITFMetadata();
        try {
            metadata.setRootElement(NITFMetadataAdapter.read(reader));
        } catch (IOException e) {
            logger.warning("Error reading NITF metadata: " + e.getMessage());
        } catch (NITFException e) {
            logger.warning("Error processing NITF metadata: " + e.getMessage());
        }
        return metadata;
    }

    public void close() {
        if (reader != null)
            reader.dispose();
    }

    private short swapBytes(short input) {
        return (short) ((input & 0xff) << 8 | ((input >> 8) & 0xff) << 0);
    }
}
