package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.dataio.AbstractProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.Guardian;
import org.gdal.gdal.Dataset;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class GDALProductWriter extends AbstractProductWriter {
    private static final Logger logger = Logger.getLogger(GDALProductWriter.class.getName());;

    private Dataset dataset;
    private Map<Band, org.gdal.gdal.Band> bandsMap;

    public GDALProductWriter(ProductWriterPlugIn writerPlugIn) {
        super(writerPlugIn);
    }

    @Override
    protected void writeProductNodesImpl() throws IOException {
//        Object output = getOutput();
//
//        logger.info("Saving the product using the GDAL writer into the file '" + output.toString() + "'.");
//
//        Path outputFile = GDALProductReader.getFileInput(output);
//        if (outputFile == null) {
//            throw new IOException("The file '"+ output + "' to save the product is invalid.");
//        }
//
//        Product sourceProduct = getSourceProduct();
//        int imageWidth = sourceProduct.getSceneRasterWidth();
//        int imageHeight = sourceProduct.getSceneRasterHeight();
//        int bandCount = sourceProduct.getNumBands();
//
//        Driver driver = gdal.GetDriverByName("NITF");
//
//        this.dataset = driver.Create(outputFile.toString(), imageWidth, imageHeight, bandCount, gdalconst.GDT_Byte);
//        this.bandsMap = new HashMap<Band, org.gdal.gdal.Band>(bandCount);
    }

    @Override
    public void writeBandRasterData(Band sourceBand, int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, ProductData sourceBuffer, ProgressMonitor pm) throws IOException {
//        Guardian.assertNotNull("sourceBand", sourceBand);
//        Guardian.assertNotNull("sourceBuffer", sourceBuffer);
//        checkBufferSize(sourceWidth, sourceHeight, sourceBuffer);
//
//        long sourceBandWidth = sourceBand.getRasterWidth();
//        long sourceBandHeight = sourceBand.getRasterHeight();
//        checkSourceRegionInsideBandRegion(sourceWidth, sourceBandWidth, sourceHeight, sourceBandHeight, sourceOffsetX, sourceOffsetY);
//
//        Product sourceProduct = getSourceProduct();
//        org.gdal.gdal.Band gdalBand = this.bandsMap.get(sourceBand);
//        if (gdalBand == null) {
//            int bandIndex = sourceProduct.getBandIndex(sourceBand.getName());
//            gdalBand = this.dataset.GetRasterBand(bandIndex + 1);
//            this.bandsMap.put(sourceBand, gdalBand);
//        }
//
//        System.out.println("writeBandRasterData RasterCount="+this.dataset.getRasterCount()+" sourceBand.hashcode="+sourceBand.hashCode()+" gdalBand.hashCode="+gdalBand.hashCode()+" sourceOffsetX="+sourceOffsetX+" sourceOffsetY="+sourceOffsetY+" sourceWidth="+sourceWidth+" sourceHeight="+sourceHeight);
//
//        short[] data = (short[])sourceBuffer.getElems();
//        ImageOutputStream outputStream = new GDALBandImageOutputStream(gdalBand);
//        long outputPos = (long) sourceOffsetY * sourceBandWidth + (long) sourceOffsetX;
//        pm.beginTask("Writing band '" + sourceBand.getName() + "'...", sourceHeight);
//        try {
//            for (int sourceY = sourceOffsetY; sourceY < sourceHeight; sourceY++) {
//                int xOffset = 0;
//                int yOffset = sourceY;
//                int xSize = sourceWidth;
//                int ySize = 1;
//                gdalBand.WriteRaster(xOffset, yOffset, xSize, ySize, data);
//            }
//        } finally {
//            pm.done();
//        }
    }
//    public int WriteRaster(int xoff,
//                           int yoff,
//                           int xsize,
//                           int ysize,
//                           int buf_xsize,
//                           int buf_ysize,
//                           int buf_type,
//                           byte[] array,
//                           int nPixelSpace,
//                           int nLineSpace)
//    for (int i = 0; i < ysize; i++)
//    {
//        for (int j = 0; j < xsize; j++)
//        {
//            floatArray[j] = (float) (i + j);
//        }
//        band.WriteRaster(0, i, xsize, 1, floatArray);
//    }
    @Override
    public void flush() throws IOException {
        System.out.println("flush");

    }

    @Override
    public void close() throws IOException {
        System.out.println("close");
        this.dataset.delete();
    }

    @Override
    public void deleteOutput() throws IOException {
        System.out.println("deleteOutput");

    }

    private static void checkBufferSize(int sourceWidth, int sourceHeight, ProductData sourceBuffer) {
        final int expectedBufferSize = sourceWidth * sourceHeight;
        final int actualBufferSize = sourceBuffer.getNumElems();
        Guardian.assertEquals("sourceWidth * sourceHeight", actualBufferSize, expectedBufferSize);  /*I18N*/
    }

    private static void checkSourceRegionInsideBandRegion(int sourceWidth, final long sourceBandWidth, int sourceHeight,
                                                          final long sourceBandHeight, int sourceOffsetX,
                                                          int sourceOffsetY) {
        Guardian.assertWithinRange("sourceWidth", sourceWidth, 1, sourceBandWidth);
        Guardian.assertWithinRange("sourceHeight", sourceHeight, 1, sourceBandHeight);
        Guardian.assertWithinRange("sourceOffsetX", sourceOffsetX, 0, sourceBandWidth - sourceWidth);
        Guardian.assertWithinRange("sourceOffsetY", sourceOffsetY, 0, sourceBandHeight - sourceHeight);
    }

}
