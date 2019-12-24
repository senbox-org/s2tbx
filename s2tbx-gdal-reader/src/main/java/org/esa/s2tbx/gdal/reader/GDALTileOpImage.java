package org.esa.s2tbx.gdal.reader;

import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.snap.core.image.AbstractSubsetTileOpImage;
import org.esa.snap.core.util.ImageUtils;
import org.gdal.gdal.Band;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;

/**
 * A JAI operator for handling the tiles of the products imported with the GDAL library.
 *
 * @author Jean Coravu
 */
class GDALTileOpImage extends AbstractSubsetTileOpImage {

    private ImageReader imageReader;

    public GDALTileOpImage(Path sourceLocalFile, int bandIndex, MultiLevelModel imageMultiLevelModel,
                           int dataBufferType, Rectangle imageBounds, Dimension tileSize, Point tileOffset, int level) {

        super(imageMultiLevelModel, dataBufferType, imageBounds, tileSize, tileOffset, level);

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
    protected synchronized void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destinationRectangle) {
        Rectangle tileBoundsIntersection = computeIntersectionOnNormalBounds(destinationRectangle);
        if (!tileBoundsIntersection.isEmpty()) {
            int levelDestinationX = ImageUtils.computeLevelSize(tileBoundsIntersection.x, getLevel());
            int levelDestinationY = ImageUtils.computeLevelSize(tileBoundsIntersection.y, getLevel());
            int levelDestinationWidth = ImageUtils.computeLevelSize(tileBoundsIntersection.width, getLevel());
            int levelDestinationHeight = ImageUtils.computeLevelSize(tileBoundsIntersection.height, getLevel());
//            int gdalLevelBandWidth = this.imageReader.getBandWidth();
//            if (levelDestinationWidth != gdalLevelBandWidth) {
//                throw new IllegalStateException("The level width is different: levelDestinationWidth=" + levelDestinationWidth + ", fileTileWidth=" + gdalLevelBandWidth);
//            }
//            int gdalLevelBandHeight = this.imageReader.getBandHeight();
//            if (levelDestinationHeight != gdalLevelBandHeight) {
//                throw new IllegalStateException("The level height is different: levelDestinationHeight=" + levelDestinationHeight + ", fileTileHeight=" + gdalLevelBandHeight);
//            }
            Rectangle intersection = new Rectangle(levelDestinationX, levelDestinationY, levelDestinationWidth, levelDestinationHeight);
            try {
                RenderedImage readTileImage = this.imageReader.read(intersection);
                Raster imageRaster = readTileImage.getData();
                int bandList[] = new int[]{0}; // the band index is zero
                Raster readBandRaster = imageRaster.createChild(0, 0, readTileImage.getWidth(), readTileImage.getHeight(), 0, 0, bandList);
                dest.setDataElements(dest.getMinX(), dest.getMinY(), readBandRaster);
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to read the data for level " + getLevel() + " and rectangle " + destinationRectangle + ".", ex);
            }
        }
    }

    private static class ImageReader {

        private final int dataBufferType;
        private final int level;
        private final Path sourceLocalFile;
        private final int bandIndex;

        private org.gdal.gdal.Dataset gdalDataset;
        private org.gdal.gdal.Band band;

        private ImageReader(Path sourceLocalFile, int bandIndex, int dataBufferType, int level) {
            this.sourceLocalFile = sourceLocalFile;
            this.dataBufferType = dataBufferType;
            this.bandIndex = bandIndex;
            this.level = level;
        }

        int getBandWidth() {
            if (this.gdalDataset == null) {
                createDataset();
            }
            return this.band.getXSize();
        }

        int getBandHeight() {
            if (this.gdalDataset == null) {
                createDataset();
            }
            return this.band.getYSize();
        }

        void close() {
            if (this.gdalDataset != null) {
                this.gdalDataset.delete();
                this.gdalDataset = null;
                this.band.delete();
                this.band = null;
            }
        }

        RenderedImage read(Rectangle rectangle) throws IOException {
            if (this.gdalDataset == null) {
                createDataset();
            }
            int imageWidth = rectangle.width;
            int imageHeight = rectangle.height;
            int pixels = imageWidth * imageHeight;
            int gdalBufferDataType = this.band.getDataType();
            int bufferSize = pixels * gdal.GetDataTypeSize(gdalBufferDataType);
            ByteBuffer data = ByteBuffer.allocateDirect(bufferSize);
            data.order(ByteOrder.nativeOrder());

            int xSizeToRead = Math.min(rectangle.width, getBandWidth() -  rectangle.x);
            int ySizeToRead = Math.min(rectangle.height, getBandHeight() -  rectangle.y);
            int returnVal = this.band.ReadRaster_Direct(rectangle.x, rectangle.y, xSizeToRead, ySizeToRead, rectangle.width, rectangle.height, gdalBufferDataType, data);
            if (returnVal == gdalconstConstants.CE_None) {
                DataBuffer imageDataBuffer = null;
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
                int[] index = new int[] { 0 };
                SampleModel sampleModel = new ComponentSampleModel(imageDataBuffer.getDataType(), imageWidth, imageHeight, 1, imageWidth, index);
                WritableRaster writableRaster = Raster.createWritableRaster(sampleModel, imageDataBuffer, null);
                BufferedImage image;
                if (this.band.GetRasterColorInterpretation() == gdalconstConstants.GCI_PaletteIndex) {
                    ColorModel cm = this.band.GetRasterColorTable().getIndexColorModel(gdal.GetDataTypeSize(gdalBufferDataType));
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
                throw new IOException("Failed to read the product band data: rectangle="+rectangle+" returnVal="+returnVal);
            }
        }

        private void createDataset() {
            this.gdalDataset = gdal.Open(sourceLocalFile.toString(), gdalconst.GA_ReadOnly);
            // bands are not 0-base indexed, so we must add 1
            Band gdalRasterBand = this.gdalDataset.GetRasterBand(this.bandIndex + 1);
            if (level > 0 && gdalRasterBand.GetOverviewCount() > 0) {
                this.band = gdalRasterBand.GetOverview(this.level - 1);
            } else {
                this.band = gdalRasterBand;
            }
        }
    }
}
