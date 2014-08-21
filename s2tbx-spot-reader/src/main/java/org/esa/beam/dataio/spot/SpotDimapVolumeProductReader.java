package org.esa.beam.dataio.spot;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.BandMatrix;
import org.esa.beam.dataio.ByteArrayOutputStream;
import org.esa.beam.dataio.geotiff.GeoTiffProductReader;
import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.spot.dimap.SpotConstants;
import org.esa.beam.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.*;
import org.geotools.metadata.InvalidMetadataException;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This reader is intended for reading SPOT-4 and SPOT-5 View files composed
 * of several products (for instance, large products that are split into several smaller ones.
 * @author Cosmin Cara
 */
public class SpotDimapVolumeProductReader extends SpotProductReader {

    //private Map<Band, List<GeoTiffProductReader>> readerMap;
    private final Map<Band, BandMatrix> bandMap;

    @SuppressWarnings("WeakerAccess")
    protected SpotDimapVolumeProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        //readerMap = new HashMap<Band, List<GeoTiffProductReader>>();
        bandMap = new HashMap<Band, BandMatrix>();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        SpotDimapMetadata dimapMetadata = metadata.getComponentMetadata(0);

        int width = metadata.getExpectedVolumeWidth();
        int height = metadata.getExpectedVolumeHeight();

        Product rootProduct = new Product(dimapMetadata.getProductName(),
                SpotConstants.DIMAP_FORMAT_NAMES[0],
                width,
                height);
        rootProduct.getMetadataRoot().addElement(metadata.getRootElement());
        ProductData.UTC centerTime = dimapMetadata.getCenterTime();
        rootProduct.setStartTime(centerTime);
        rootProduct.setEndTime(centerTime);
        rootProduct.setDescription(dimapMetadata.getProductDescription());

        int numBands = dimapMetadata.getNumBands();
        String[] bandNames = metadata.getComponentMetadata(0).getBandNames();
        for (int i = 0; i < numBands; i++) {
            Band virtualBand = new Band(bandNames[i], dimapMetadata.getPixelDataType(), width, height);
            rootProduct.addBand(virtualBand);
            bandMap.put(virtualBand, new BandMatrix(metadata.getExpectedTileComponentRows(), metadata.getExpectedTileComponentCols()));
            //readerMap.put(virtualBand, new ArrayList<GeoTiffProductReader>());
        }

        for (int fileIndex = 0; fileIndex < metadata.getNumComponents(); fileIndex++) {
            addBands(rootProduct, metadata.getComponentMetadata(fileIndex));
            addMasks(rootProduct, metadata.getComponentMetadata(fileIndex));
        }
        rootProduct.setModified(false);

        return rootProduct;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY,
                                          int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY,
                                          Band destBand,
                                          int destOffsetX, int destOffsetY,
                                          int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm) throws IOException {
            BandMatrix bandMatrix = bandMap.get(destBand);
            BandMatrix.BandMatrixCell[] cells = bandMatrix.getCells();
            int readWidth = 0;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (BandMatrix.BandMatrixCell cell : cells) {
                Rectangle readArea = cell.intersection(destOffsetX, destOffsetY, destWidth, destHeight);
                if (readArea != null) {
                    ProductReader reader = cell.band.getProductReader();
                    if (reader == null) {
                        logger.severe("No reader found for band data");
                    } else {
                        int bandDestOffsetX = readArea.x - cell.cellStartPixelX;
                        int bandDestOffsetY = readArea.y - cell.cellStartPixelY;
                        int bandDestWidth = readArea.width;
                        int bandDestHeight = readArea.height;
                        ProductData bandBuffer = createProductData(destBuffer.getType(), bandDestWidth * bandDestHeight);
                        reader.readBandRasterData(cell.band, bandDestOffsetX, bandDestOffsetY, bandDestWidth, bandDestHeight, bandBuffer, pm);
                        MemoryCacheImageOutputStream writeStream = null;
                        ImageInputStream readStream = null;
                        try {
                            byteArrayOutputStream.reset();
                            writeStream = new MemoryCacheImageOutputStream(byteArrayOutputStream);
                            bandBuffer.writeTo(writeStream);
                            writeStream.flush();
                            readStream = new MemoryCacheImageInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

                            for (int y = 0; y < destHeight; y++) {
                                destBuffer.readFrom(y * destWidth + readWidth, bandDestWidth, readStream);
                            }
                            readWidth += bandDestWidth;
                        } finally {
                            if (writeStream != null) writeStream.close();
                            if (readStream != null) readStream.close();
                        }
                    }
                }
            }
    }

    void addBands(Product product, SpotDimapMetadata componentMetadata)
    {
        String[] bandUnits = componentMetadata.getBandUnits();
        try {
            if (SpotConstants.DIMAP.equals(componentMetadata.getFormatName())) {
                String[] rasterFileNames = componentMetadata.getRasterFileNames();
                if (rasterFileNames == null || rasterFileNames.length == 0)
                    throw new InvalidMetadataException("No raster file name found in metadata");
                File rasterFile = productDirectory.getFile(componentMetadata.getPath().toLowerCase().replace(componentMetadata.getFileName().toLowerCase(),
                                                                                                             componentMetadata.getRasterFileNames()[0].toLowerCase()));
                GeoTiffProductReader tiffReader = new GeoTiffProductReader(getReaderPlugIn());
                logger.info("Read product nodes for component " + componentMetadata.getProductName());
                Product tiffProduct = tiffReader.readProductNodes(rasterFile, null);
                if (tiffProduct != null) {
                    MetadataElement tiffMetadata = tiffProduct.getMetadataRoot();
                    if (tiffMetadata != null) {
                        XmlMetadata.CopyChildElements(tiffMetadata, product.getMetadataRoot());
                    }
                    if (product.getGeoCoding() == null)
                        tiffProduct.transferGeoCodingTo(product, null);
                    if (product.getPreferredTileSize() == null)
                        product.setPreferredTileSize(tiffProduct.getPreferredTileSize());

                    int numTiffBands = tiffProduct.getNumBands();
                    logger.info("Read bands for component " + componentMetadata.getProductName());
                    for (int idx = 0; idx < numTiffBands; idx++) {
                        Band srcBand = tiffProduct.getBandAt(idx);
                        Band targetBand = product.getBandAt(idx);
                        SpotDimapMetadata.InsertionPoint insertPoint = componentMetadata.getInsertPoint();
                        bandMap.get(targetBand).addCell(srcBand,
                                                        new Point2D.Float(insertPoint.x, insertPoint.y),
                                                        insertPoint.stepX, insertPoint.stepY);
                        targetBand.setNoDataValue(componentMetadata.getNoDataValue() > -1 ? componentMetadata.getNoDataValue() : srcBand.getNoDataValue());
                        targetBand.setNoDataValueUsed((componentMetadata.getNoDataValue() > -1));
                        targetBand.setSpectralWavelength(componentMetadata.getWavelength(idx) > 0 ? componentMetadata.getWavelength(idx) : srcBand.getSpectralWavelength());
                        targetBand.setSpectralBandwidth(componentMetadata.getBandwidth(idx) > 0 ? componentMetadata.getBandwidth(idx) : srcBand.getSpectralBandwidth());
                        targetBand.setScalingFactor(srcBand.getScalingFactor());
                        targetBand.setScalingOffset(srcBand.getScalingOffset());
                        targetBand.setSolarFlux(srcBand.getSolarFlux());
                        targetBand.setUnit(srcBand.getUnit() != null ? srcBand.getUnit() : bandUnits[idx]);
                        targetBand.setSampleCoding(srcBand.getSampleCoding());
                        targetBand.setImageInfo(srcBand.getImageInfo());
                        //targetBand.setSpectralBandIndex(srcBand.getSpectralBandIndex());
                        targetBand.setSpectralBandIndex(idx + 1);
                        readBandStatistics(targetBand, idx, componentMetadata);
                        if (targetBand.getDescription() == null)
                            if (srcBand.getDescription() == null)
                                targetBand.setDescription("Combined " + targetBand.getName() + " from underlying components");
                            else
                                targetBand.setDescription(srcBand.getDescription());
                    }
                }
            } else {
                logger.warning(String.format("Component product %s is not in DIMAP format!", componentMetadata.getProductName()));
            }
        } catch (IOException ioEx) {
            logger.severe("Error while reading component: " + ioEx.getMessage());
        }
    }

    void addMasks(Product product, SpotDimapMetadata componentMetadata) {
        logger.info("Create masks");
        int noDataValue,saturatedValue;
        if ((noDataValue = componentMetadata.getNoDataValue()) >= 0 && !product.getMaskGroup().contains(SpotConstants.NODATA_VALUE)) {
            product.getMaskGroup().add(Mask.BandMathsType.create(SpotConstants.NODATA_VALUE,
                    SpotConstants.NODATA_VALUE,
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    String.valueOf(noDataValue),
                    componentMetadata.getNoDataColor(),
                    0.5));
        }
        if ((saturatedValue = componentMetadata.getSaturatedPixelValue()) >= 0 && !product.getMaskGroup().contains(SpotConstants.SATURATED_VALUE)) {
            product.getMaskGroup().add(Mask.BandMathsType.create(SpotConstants.SATURATED_VALUE,
                    SpotConstants.SATURATED_VALUE,
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    String.valueOf(saturatedValue),
                    componentMetadata.getSaturatedColor(),
                    0.5));
        }
    }
}
