package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.snap.core.image.AbstractSubsetTileOpImage;
import org.esa.snap.core.image.ImageReadBoundsSupport;
import org.esa.snap.dataio.gdal.drivers.*;
//import org.esa.snap.core.util.ImageUtils;

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
    private final int[] bandList = new int[]{0}; // the band index is zero
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
            final int level = getLevel();
            /*int levelDestinationX = ImageUtils.computeLevelSize(normalBoundsIntersection.x, level);
            int levelDestinationY = ImageUtils.computeLevelSize(normalBoundsIntersection.y, level);
            int levelDestinationWidth = ImageUtils.computeLevelSize(normalBoundsIntersection.width, level);
            int levelDestinationHeight = ImageUtils.computeLevelSize(normalBoundsIntersection.height, level);*/
            int levelDestinationX = normalBoundsIntersection.x >> level;
            int levelDestinationY = normalBoundsIntersection.y >> level;
            int levelDestinationWidth = normalBoundsIntersection.width >> level;
            int levelDestinationHeight = normalBoundsIntersection.height >> level;
            try {
                /*RenderedImage readTileImage = this.imageReader.read(levelDestinationX, levelDestinationY, levelDestinationWidth, levelDestinationHeight);
                Raster imageRaster = readTileImage.getData();*/
                Raster imageRaster = this.imageReader.read(levelDestinationX, levelDestinationY, levelDestinationWidth, levelDestinationHeight);
                /*Raster readBandRaster = imageRaster.createChild(0, 0, readTileImage.getWidth(), readTileImage.getHeight(), 0, 0, bandList);*/
                /*levelDestinationRaster.setDataElements(levelDestinationRaster.getMinX(), levelDestinationRaster.getMinY(), readBandRaster);*/
                levelDestinationRaster.setDataElements(levelDestinationRaster.getMinX(), levelDestinationRaster.getMinY(), imageRaster);
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to read the data for level " + level + " and rectangle " + levelDestinationRectangle + ".", ex);
            }
        }
    }

    private static abstract class ImageReader {

        private final GDALBandSource bandSource;
        final int[] index = new int[] { 0 };
        private final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
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

        Raster read(int areaX, int areaY, int areaWidth, int areaHeight) throws IOException {
            if (this.gdalDataset == null) {
                createDataset();
            }
            final int pixels = areaWidth * areaHeight;
            final int gdalBufferDataType = this.gdalBand.getDataType();
            final int bufferSize = pixels * GDAL.getDataTypeSize(gdalBufferDataType);
            final ByteBuffer data = ByteBuffer.allocateDirect(bufferSize);
            data.order(ByteOrder.nativeOrder());

            final int dataBufferType = getDataBufferType();
            final int xSizeToRead = Math.min(areaWidth, getBandWidth() -  areaX);
            final int ySizeToRead = Math.min(areaHeight, getBandHeight() -  areaY);
            final int returnVal = this.gdalBand.readRasterDirect(areaX, areaY, xSizeToRead, ySizeToRead, areaWidth, areaHeight, gdalBufferDataType, data);
            if (returnVal == GDALConstConstants.ceNone()) {
                DataBuffer imageDataBuffer;
                switch (dataBufferType) {
                    case DataBuffer.TYPE_BYTE:
                        byte[] bytes = new byte[pixels];
                        data.get(bytes);
                        imageDataBuffer = new DataBufferByte(bytes, pixels);
                        break;
                    case DataBuffer.TYPE_SHORT:
                        short[] shorts = new short[pixels];
                        data.asShortBuffer().get(shorts);
                        imageDataBuffer = new DataBufferShort(shorts, shorts.length);
                        break;
                    case DataBuffer.TYPE_USHORT:
                        shorts = new short[pixels];
                        data.asShortBuffer().get(shorts);
                        imageDataBuffer = new DataBufferUShort(shorts, shorts.length);
                        break;
                    case DataBuffer.TYPE_INT:
                        int[] ints = new int[pixels];
                        data.asIntBuffer().get(ints);
                        imageDataBuffer = new DataBufferInt(ints, ints.length);
                        break;
                    case DataBuffer.TYPE_FLOAT:
                        float[] floats = new float[pixels];
                        data.asFloatBuffer().get(floats);
                        imageDataBuffer = new DataBufferFloat(floats, floats.length);
                        break;
                    case DataBuffer.TYPE_DOUBLE:
                        double[] doubles = new double[pixels];
                        data.asDoubleBuffer().get(doubles);
                        imageDataBuffer = new DataBufferDouble(doubles, doubles.length);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown data buffer type " + dataBufferType + ".");
                }
                final SampleModel sampleModel = new ComponentSampleModel(imageDataBuffer.getDataType(), areaWidth, areaHeight, 1, areaWidth, index);
                // The code below is not necessary since, in the calling class, only the raster from the image is used
                /*final WritableRaster writableRaster = Raster.createWritableRaster(sampleModel, imageDataBuffer, null);
                BufferedImage image;
                if ((int) this.gdalBand.getRasterColorInterpretation() == GDALConstConstants.gciPaletteindex()) {
                    final ColorModel cm = this.gdalBand.getRasterColorTable().getIndexColorModel(GDAL.getDataTypeSize(gdalBufferDataType));
                    image = new BufferedImage(cm, writableRaster, false, null);
                } else if (imageDataBuffer instanceof DataBufferByte || imageDataBuffer instanceof DataBufferUShort) {
                    final int imageType = (imageDataBuffer instanceof DataBufferByte) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_USHORT_GRAY;
                    image = new BufferedImage(areaWidth, areaHeight, imageType);
                    image.setData(writableRaster);
                } else {
                    final ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, imageDataBuffer.getDataType());
                    image = new BufferedImage(cm, writableRaster, true, null);
                }
                return image;*/
                return Raster.createWritableRaster(sampleModel, imageDataBuffer, null);
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
