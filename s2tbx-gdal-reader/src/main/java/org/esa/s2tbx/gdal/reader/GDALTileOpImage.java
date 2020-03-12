package org.esa.s2tbx.gdal.reader;

import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.s2tbx.dataio.gdal.drivers.Band;
import org.esa.s2tbx.dataio.gdal.drivers.Dataset;
import org.esa.s2tbx.dataio.gdal.drivers.GDAL;
import org.esa.s2tbx.dataio.gdal.drivers.GDALConst;
import org.esa.s2tbx.dataio.gdal.drivers.GDALConstConstants;
import org.esa.snap.core.image.AbstractSubsetTileOpImage;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.SystemUtils;

import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A JAI operator for handling the tiles of the products imported with the GDAL library.
 *
 * @author Jean Coravu
 */
class GDALTileOpImage extends AbstractSubsetTileOpImage {

    private ImageReader imageReader;

    GDALTileOpImage(Path sourceLocalFile, int bandIndex, MultiLevelModel imageMultiLevelModel, int dataBufferType, Rectangle imageReadBounds, Dimension tileSize, Point tileOffsetFromReadBounds, int level) {

        super(imageMultiLevelModel, dataBufferType, imageReadBounds, tileSize, tileOffsetFromReadBounds, level);

        this.imageReader = new ImageReader(sourceLocalFile, bandIndex, dataBufferType, level);
    }

    @Override
    public synchronized void dispose() {
        super.dispose();

        if (this.imageReader != null) {
            this.imageReader.close();
            this.imageReader = null;
        }
    }

    @Override
    protected synchronized void computeRect(PlanarImage[] sources, WritableRaster levelDestinationRaster, Rectangle levelDestinationRectangle) {
        Rectangle normalBoundsIntersection = computeIntersectionOnNormalBounds(levelDestinationRectangle);
        if (!normalBoundsIntersection.isEmpty()) {
            int levelDestinationX = ImageUtils.computeLevelSize(normalBoundsIntersection.x, getLevel());
            int levelDestinationY = ImageUtils.computeLevelSize(normalBoundsIntersection.y, getLevel());
            int levelDestinationWidth = ImageUtils.computeLevelSize(normalBoundsIntersection.width, getLevel());
            int levelDestinationHeight = ImageUtils.computeLevelSize(normalBoundsIntersection.height, getLevel());

            Rectangle intersection = new Rectangle(levelDestinationX, levelDestinationY, levelDestinationWidth, levelDestinationHeight);
            if (!intersection.isEmpty()) {
                try {
                    int imageWidth = intersection.width;
                    int imageHeight = intersection.height;
                    int xOffset = intersection.x;
                    int yOffset = intersection.y;
                    int nXOffset = xOffset + imageWidth;
                    int nYOffset = yOffset + imageHeight;
                    int xBlock = xOffset / this.imageReader.getBandBlockWidth();
                    int yBlock = yOffset / this.imageReader.getBandBlockHeight();
                    int nXBlock = nXOffset / this.imageReader.getBandBlockWidth() + (nXOffset % this.imageReader.getBandBlockWidth() > 0 ? 1 : 0);
                    int nYBlock = nYOffset / this.imageReader.getBandBlockHeight() + (nYOffset % this.imageReader.getBandBlockHeight() > 0 ? 1 : 0);

                    long startTime = System.currentTimeMillis();

                    int x = levelDestinationRaster.getMinX();
                    Raster readBandRaster = null;

                    for (int iXBlock = xBlock; iXBlock < nXBlock; iXBlock++) {
                        int y = levelDestinationRaster.getMinY();
                        for (int iYBlock = yBlock; iYBlock < nYBlock; iYBlock++) {
                            RenderedImage readTileImage = this.imageReader.read(iXBlock, iYBlock);
                            if (readTileImage != null) {
                                int[] bandList = new int[]{0}; // the band index is zero
                                Raster imageRaster = readTileImage.getData();
                                int rasterWidth = Math.min(imageWidth, readTileImage.getTileWidth());
                                int rasterHeight = Math.min(imageHeight, readTileImage.getTileHeight());
                                int rasterParentX = 0;
                                int rasterParentY = 0;

                                if (levelDestinationRaster.getMinX() + rasterWidth <= imageRaster.getWidth()) {
                                    rasterParentX = levelDestinationRaster.getMinX();
                                }
                                if (levelDestinationRaster.getMinY() + rasterHeight <= imageRaster.getHeight()) {
                                    rasterParentY = levelDestinationRaster.getMinY();
                                }
                                readBandRaster = imageRaster.createChild(rasterParentX, rasterParentY, rasterWidth, rasterHeight, 0, 0, bandList);
                                levelDestinationRaster.setDataElements(x, y, readBandRaster);
                            }
                            if (readBandRaster != null) {
                                y += readBandRaster.getHeight();
                            }
                        }
                        if (readBandRaster != null) {
                            x += readBandRaster.getWidth();
                        }
                    }

                    long endTime = System.currentTimeMillis();
                    String msg = String.format("readBlockDirect (took %s)", (new SimpleDateFormat("mm:ss:SSS")).format(new Date(endTime - startTime)));
                    SystemUtils.LOG.info(msg);

                } catch (IOException ex) {
                    throw new IllegalStateException("Failed to read the data for level " + getLevel() + " and rectangle " + levelDestinationRectangle + ".", ex);
                }
            }
        }
    }

    private static class ImageReader {

        private final int dataBufferType;
        private final int level;
        private final Path sourceLocalFile;
        private final int bandIndex;

        private Dataset gdalDataset;
        private Band band;

        private ImageReader(Path sourceLocalFile, int bandIndex, int dataBufferType, int level) {
            this.sourceLocalFile = sourceLocalFile;
            this.dataBufferType = dataBufferType;
            this.bandIndex = bandIndex;
            this.level = level;
        }

        int getBandBlockWidth() {
            if (this.gdalDataset == null) {
                createDataset();
            }
            return this.band.getBlockXSize();
        }

        int getBandBlockHeight() {
            if (this.gdalDataset == null) {
                createDataset();
            }
            return this.band.getBlockYSize();
        }

        void close() {
            if (this.gdalDataset != null) {
                this.gdalDataset.delete();
                this.gdalDataset = null;
                this.band.delete();
                this.band = null;
            }
        }

        RenderedImage read(int iXBlock, int iYBlock) throws IOException {
            if (this.gdalDataset == null) {
                createDataset();
            }
            int imageWidth = getBandBlockWidth();
            int imageHeight = getBandBlockHeight();
            int pixels = imageWidth * imageHeight;
            int gdalBufferDataType = this.band.getDataType();
            int bufferSize = pixels * GDAL.getDataTypeSize(gdalBufferDataType);
            ByteBuffer data = ByteBuffer.allocateDirect(bufferSize);
            data.order(ByteOrder.nativeOrder());

            int returnVal = this.band.readBlockDirect(iXBlock, iYBlock, data);

            if (returnVal == GDALConstConstants.ceNone()) {
                DataBuffer imageDataBuffer;
                if (this.dataBufferType == DataBuffer.TYPE_BYTE) {
                    byte[] bytes = new byte[pixels];
                    data.get(bytes);
                    imageDataBuffer = new DataBufferByte(bytes, pixels);
                } else if (this.dataBufferType == DataBuffer.TYPE_SHORT) {
                    short[] shorts = new short[pixels];
                    data.asShortBuffer().get(shorts);
                    imageDataBuffer = new DataBufferShort(shorts, shorts.length);
                } else if (this.dataBufferType == DataBuffer.TYPE_USHORT) {
                    short[] shorts = new short[pixels];
                    data.asShortBuffer().get(shorts);
                    imageDataBuffer = new DataBufferUShort(shorts, shorts.length);
                } else if (this.dataBufferType == DataBuffer.TYPE_INT) {
                    int[] ints = new int[pixels];
                    data.asIntBuffer().get(ints);
                    imageDataBuffer = new DataBufferInt(ints, ints.length);
                } else if (this.dataBufferType == DataBuffer.TYPE_FLOAT) {
                    float[] floats = new float[pixels];
                    data.asFloatBuffer().get(floats);
                    imageDataBuffer = new DataBufferFloat(floats, floats.length);
                } else if (this.dataBufferType == DataBuffer.TYPE_DOUBLE) {
                    double[] doubles = new double[pixels];
                    data.asDoubleBuffer().get(doubles);
                    imageDataBuffer = new DataBufferDouble(doubles, doubles.length);
                } else {
                    throw new IllegalArgumentException("Unknown data buffer type " + this.dataBufferType + ".");
                }
                int[] index = new int[]{0};
                SampleModel sampleModel = new ComponentSampleModel(imageDataBuffer.getDataType(), imageWidth, imageHeight, 1, imageWidth, index);
                WritableRaster writableRaster = Raster.createWritableRaster(sampleModel, imageDataBuffer, null);
                BufferedImage image;
                if (this.band.getRasterColorInterpretation().equals(GDALConstConstants.gciPaletteindex())) {
                    ColorModel cm = this.band.getRasterColorTable().getIndexColorModel(GDAL.getDataTypeSize(gdalBufferDataType));
                    image = new BufferedImage(cm, writableRaster, false, null);
                } else if (imageDataBuffer instanceof DataBufferByte || imageDataBuffer instanceof DataBufferUShort) {
                    int imageType = (imageDataBuffer instanceof DataBufferByte) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_USHORT_GRAY;
                    image = new BufferedImage(imageWidth, imageHeight, imageType);
                    image.setData(writableRaster);
                } else {
                    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                    ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, imageDataBuffer.getDataType());
                    image = new BufferedImage(cm, writableRaster, true, null);
                }
                return image;
            } else {
                throw new IOException("Failed to read the product band data.");
            }
        }

        private void createDataset() {
            this.gdalDataset = GDAL.open(this.sourceLocalFile.toString(), GDALConst.gaReadonly());
            // bands are not 0-base indexed, so we must add 1
            if (this.gdalDataset != null) {
                Band gdalRasterBand = this.gdalDataset.getRasterBand(this.bandIndex + 1);
                if (this.level > 0 && gdalRasterBand.getOverviewCount() > 0) {
                    this.band = gdalRasterBand.getOverview(this.level - 1);
                } else {
                    this.band = gdalRasterBand;
                }
            }
        }
    }
}
