package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.drivers.*;
import org.esa.snap.core.image.AbstractSubsetTileOpImage;
import org.esa.snap.core.image.ImageReadBoundsSupport;
import org.esa.snap.core.util.ImageUtils;

import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A JAI operator for handling the tiles of the products imported with the GDAL library.
 *
 * @author Jean Coravu
 * @author Adrian Draghici
 */
class GDALTileOpImage extends AbstractSubsetTileOpImage {

    private ImageReader imageReader;

    GDALTileOpImage(GDALBandSource bandSource, int dataBufferType, int tileWidth, int tileHeight, int tileOffsetFromReadBoundsX, int tileOffsetFromReadBoundsY,
                    ImageReadBoundsSupport imageReadBoundsSupport, Dimension defaultJAIReadTileSize) {

        super(dataBufferType, tileWidth, tileHeight, tileOffsetFromReadBoundsX, tileOffsetFromReadBoundsY, imageReadBoundsSupport, defaultJAIReadTileSize);

        this.imageReader = new ImageReader(bandSource) {
            @Override
            protected int getLevel() {
                return GDALTileOpImage.this.getLevel();
            }

            @Override
            protected int getDataBufferType() {
                return GDALTileOpImage.this.getSampleModel().getDataType();
            }
        };
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
            int level = getLevel();
            int levelDestinationX = ImageUtils.computeLevelSize(normalBoundsIntersection.x, level);
            int levelDestinationY = ImageUtils.computeLevelSize(normalBoundsIntersection.y, level);
            int levelDestinationWidth = ImageUtils.computeLevelSize(normalBoundsIntersection.width, level);
            int levelDestinationHeight = ImageUtils.computeLevelSize(normalBoundsIntersection.height, level);
            try {
                RenderedImage readTileImage = this.imageReader.read(levelDestinationX, levelDestinationY, levelDestinationWidth, levelDestinationHeight);
                Raster imageRaster = readTileImage.getData();
                int bandList[] = new int[]{0}; // the band index is zero
                Raster readBandRaster = imageRaster.createChild(0, 0, readTileImage.getWidth(), readTileImage.getHeight(), 0, 0, bandList);
                levelDestinationRaster.setDataElements(levelDestinationRaster.getMinX(), levelDestinationRaster.getMinY(), readBandRaster);
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to read the data for level " + level + " and rectangle " + levelDestinationRectangle + ".", ex);
            }
        }
    }

    private static abstract class ImageReader {

        private final GDALBandSource bandSource;

        private Dataset gdalDataset;
        private Band gdalBand;

        private ImageReader(GDALBandSource bandSource) {
            this.bandSource = bandSource;
        }

        protected abstract int getLevel();

        protected abstract int getDataBufferType();

        int getBandWidth() {
            if (this.gdalDataset == null) {
                createDataset();
            }
            return this.gdalBand.getXSize();
        }

        int getBandHeight() {
            if (this.gdalDataset == null) {
                createDataset();
            }
            return this.gdalBand.getYSize();
        }

        void close() {
            if (this.gdalDataset != null) {
                this.gdalDataset.delete();
                this.gdalDataset = null;
                this.gdalBand.delete();
                this.gdalBand = null;
            }
        }

        RenderedImage read(int areaX, int areaY, int areaWidth, int areaHeight) throws IOException {
            if (this.gdalDataset == null) {
                createDataset();
            }
            int imageWidth = areaWidth;
            int imageHeight = areaHeight;
            int pixels = imageWidth * imageHeight;
            int gdalBufferDataType = this.gdalBand.getDataType();
            int bufferSize = pixels * GDAL.getDataTypeSize(gdalBufferDataType);
            ByteBuffer data = ByteBuffer.allocateDirect(bufferSize);
            data.order(ByteOrder.nativeOrder());

            int dataBufferType = getDataBufferType();
            int xSizeToRead = Math.min(areaWidth, getBandWidth() -  areaX);
            int ySizeToRead = Math.min(areaHeight, getBandHeight() -  areaY);
            int returnVal = this.gdalBand.readRasterDirect(areaX, areaY, xSizeToRead, ySizeToRead, areaWidth, areaHeight, gdalBufferDataType, data);
            if (returnVal == GDALConstConstants.ceNone()) {
                DataBuffer imageDataBuffer = null;
                if (dataBufferType == DataBuffer.TYPE_BYTE) {
                    byte[] bytes = new byte[pixels];
                    data.get(bytes);
                    imageDataBuffer = new DataBufferByte(bytes, pixels);
                } else if (dataBufferType == DataBuffer.TYPE_SHORT) {
                    short[] shorts = new short[pixels];
                    data.asShortBuffer().get(shorts);
                    imageDataBuffer = new DataBufferShort(shorts, shorts.length);
                } else if (dataBufferType == DataBuffer.TYPE_USHORT) {
                    short[] shorts = new short[pixels];
                    data.asShortBuffer().get(shorts);
                    imageDataBuffer = new DataBufferUShort(shorts, shorts.length);
                } else if (dataBufferType == DataBuffer.TYPE_INT) {
                    int[] ints = new int[pixels];
                    data.asIntBuffer().get(ints);
                    imageDataBuffer = new DataBufferInt(ints, ints.length);
                } else if (dataBufferType == DataBuffer.TYPE_FLOAT) {
                    float[] floats = new float[pixels];
                    data.asFloatBuffer().get(floats);
                    imageDataBuffer = new DataBufferFloat(floats, floats.length);
                } else if (dataBufferType == DataBuffer.TYPE_DOUBLE) {
                    double[] doubles = new double[pixels];
                    data.asDoubleBuffer().get(doubles);
                    imageDataBuffer = new DataBufferDouble(doubles, doubles.length);
                } else {
                    throw new IllegalArgumentException("Unknown data buffer type " + dataBufferType + ".");
                }
                int[] index = new int[] { 0 };
                SampleModel sampleModel = new ComponentSampleModel(imageDataBuffer.getDataType(), imageWidth, imageHeight, 1, imageWidth, index);
                WritableRaster writableRaster = Raster.createWritableRaster(sampleModel, imageDataBuffer, null);
                BufferedImage image;
                if (this.gdalBand.getRasterColorInterpretation() == GDALConstConstants.gciPaletteindex()) {
                    ColorModel cm = this.gdalBand.getRasterColorTable().getIndexColorModel(GDAL.getDataTypeSize(gdalBufferDataType));
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
                throw new IOException("Failed to read the product band data: rectangle=["+areaX+", "+areaY+", "+areaWidth+", "+areaHeight+"]"+" returnVal="+returnVal+".");
            }
        }

        private void createDataset() {
            String filePath = this.bandSource.getSourceLocalFile().toString();
            this.gdalDataset = GDAL.open(filePath, GDALConst.gaReadonly());
            if (this.gdalDataset == null) {
                throw new IllegalStateException("Failed to open the GDAL dataset for file '" + filePath + "'.");
            } else {
                // bands are not 0-base indexed, so we must add 1
                Band gdalRasterBand = this.gdalDataset.getRasterBand(this.bandSource.getBandIndex() + 1);
                int level = getLevel();
                if (level > 0 && gdalRasterBand.getOverviewCount() > 0) {
                    this.gdalBand = gdalRasterBand.getOverview(level - 1);
                } else {
                    this.gdalBand = gdalRasterBand;
                }
            }
        }
    }
}
