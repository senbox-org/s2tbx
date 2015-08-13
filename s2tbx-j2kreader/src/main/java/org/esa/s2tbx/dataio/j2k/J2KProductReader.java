package org.esa.s2tbx.dataio.j2k;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.BandMatrix;
import org.esa.s2tbx.dataio.ByteArrayOutputStream;
import org.esa.s2tbx.dataio.Parallel;
import org.esa.s2tbx.dataio.j2k.internal.OpjExecutor;
import org.esa.s2tbx.dataio.j2k.metadata.*;
import org.esa.s2tbx.dataio.j2k.metadata.ImageInfo;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.esa.snap.framework.dataio.AbstractProductReader;
import org.esa.snap.framework.dataio.DecodeQualification;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.framework.datamodel.*;
import org.geotools.referencing.CRS;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

/**
 * Generic reader for JP2 files.
 *
 * @author Cosmin Cara
 */
public class J2KProductReader extends AbstractProductReader {

    private static final Map<Integer, Integer> precisionTypeMap = new HashMap<Integer, Integer>() {{
        put(8, ProductData.TYPE_UINT8);
        put(12, ProductData.TYPE_UINT16);
        put(16, ProductData.TYPE_UINT16);
        put(32, ProductData.TYPE_FLOAT32);
    }};

    protected Product product;
    protected final Logger logger;
    protected Jp2XmlMetadata metadata;
    protected ImageInfo imageInfo;
    protected CodeStreamInfo csInfo;
    private final Map<Band, BandMatrix> bandMap;
    private File tmpFolder;
    protected final Map<int[], ProductData> readLines;
    volatile boolean tilesUncompressed;
    private FutureTask<Void> unpackTask;
    private ExecutorService executor;

    protected J2KProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        bandMap = new HashMap<>();
        readLines = new HashMap<>();
        logger = Logger.getLogger(J2KProductReader.class.getName());
        registerMetadataParser();
        unpackTask = new FutureTask<>(new UnpackProcess());
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void close() throws IOException {
        for (BandMatrix matrix : bandMap.values()) {
            BandMatrix.BandMatrixCell[] cells = matrix.getCells();
            for (BandMatrix.BandMatrixCell cell : cells) {
                if (cell != null && cell.band != null) {
                    ProductReader reader = cell.band.getProductReader();
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        }
        File[] files = tmpFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        tmpFolder.delete();
        super.close();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        if (getReaderPlugIn().getDecodeQualification(super.getInput()) == DecodeQualification.UNABLE) {
            throw new IOException("The selected product cannot be read with the current reader.");
        }
        File inputFile = getFileInput(getInput());
        tmpFolder = new File(System.getProperty("java.io.tmpdir"), inputFile.getName().replace(".jp2", "") + "_cached");
        tmpFolder.mkdir();
        logger.info("Reading product metadata");
        try {
            OpjExecutor dumper = new OpjExecutor(S2Config.OPJ_INFO_EXE);
            OpjDumpFile dumpFile = new OpjDumpFile(new File(tmpFolder, inputFile.getName() + "_dump.txt"));
            Map<String, String> params = new HashMap<String, String>() {{
                put("-i", inputFile.getAbsolutePath());
                put("-o", dumpFile.getPath());
            }};
            if (dumper.execute(params) == 0) {
                logger.info(dumper.getLastOutput());
                dumpFile.parse();
                imageInfo = dumpFile.getImageInfo();
                csInfo = dumpFile.getCodeStreamInfo();
                int imageWidth = imageInfo.getWidth();
                int imageHeight = imageInfo.getHeight();
                product = new Product(inputFile.getName(), "J2K", imageWidth, imageHeight);
                MetadataElement metadataRoot = product.getMetadataRoot();
                metadataRoot.addElement(imageInfo.toMetadataElement());
                metadataRoot.addElement(csInfo.toMetadataElement());
                metadata = new Jp2XmlMetadataReader(inputFile).read();
                if (metadata != null) {
                    metadata.setFileName(inputFile.getName());
                    metadataRoot.addElement(metadata.getRootElement());
                    String crsGeocoding = metadata.getCrsGeocoding();
                    Point2D origin = metadata.getOrigin();
                    if (crsGeocoding != null && origin != null) {
                        GeoCoding geoCoding = null;
                        try {
                            geoCoding = new CrsGeoCoding(CRS.decode(crsGeocoding.replace("::", ":")),
                                    imageWidth, imageHeight,
                                    origin.getX(), origin.getY(),
                                    metadata.getStepX(), -metadata.getStepY());
                        } catch (Exception gEx) {
                            try {
                                float oX = (float) origin.getX();
                                float oY = (float) origin.getY();
                                float h = (float) imageHeight * (float) metadata.getStepY();
                                float w = (float) imageWidth * (float) metadata.getStepX();
                                float[] latPoints = new float[]{oY + h, oY + h, oY, oY};
                                float[] lonPoints = new float[]{oX, oX + w, oX, oX + w};
                                TiePointGrid latGrid = createTiePointGrid("latitude", 2, 2, 0, 0, imageWidth, imageHeight, latPoints);
                                TiePointGrid lonGrid = createTiePointGrid("longitude", 2, 2, 0, 0, imageWidth, imageHeight, lonPoints);
                                geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
                                product.addTiePointGrid(latGrid);
                                product.addTiePointGrid(lonGrid);
                            } catch (Exception ignored) {
                            }
                        }
                        if (geoCoding != null) {
                            product.setGeoCoding(geoCoding);
                        }
                    }
                }
                List<CodeStreamInfo.TileComponentInfo> componentTilesInfo = csInfo.getComponentTilesInfo();
                int numBands = componentTilesInfo.size();
                for (int i = 0; i < numBands; i++) {
                    Band virtualBand = new Band("band_" + String.valueOf(i + 1),
                                                precisionTypeMap.get(imageInfo.getComponents().get(i).getPrecision()),
                                                imageWidth,
                                                imageHeight);
                    product.addBand(virtualBand);
                    bandMap.put(virtualBand, new BandMatrix(csInfo.getNumTilesY(), csInfo.getNumTilesX()));
                }
                product.setPreferredTileSize(new Dimension(512, 512));
                tilesUncompressed = false;
                executor.execute(unpackTask);
            } else {
                logger.warning(dumper.getLastError());
            }
        } catch (Exception mex) {
            String msg = String.format("Error while reading file %s", inputFile);
            throw new IOException(msg);
        }
        if (product != null) {
            product.setModified(false);
        }
        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        while (!tilesUncompressed) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        int[] key = new int[] { sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY };
        if (readLines.containsKey(key)) {
            destBuffer = readLines.get(key);
        } else {
            BandMatrix bandMatrix = bandMap.get(destBand);
            int readWidth = 0;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Map<BandMatrix.BandMatrixCell, Rectangle> rectangleMap = bandMatrix.computeIntersection(destOffsetX, destOffsetY, destWidth, destHeight);
            for (BandMatrix.BandMatrixCell cell : rectangleMap.keySet()) {
                Rectangle readArea = rectangleMap.get(cell);
                GeoTiffReader reader = (GeoTiffReader) cell.band.getProductReader();
                if (reader == null) {
                    logger.severe("No reader found for band data");
                } else {
                    int bandDestOffsetX = readArea.x - cell.cellStartPixelX;
                    int bandDestOffsetY = readArea.y - cell.cellStartPixelY;
                    int bandDestWidth = readArea.width;
                    int bandDestHeight = readArea.height;
                    ProductData bandBuffer = createProductData(destBuffer.getType(), bandDestWidth * bandDestHeight);
                    reader.readBandRasterDataImpl(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY,
                            cell.band, bandDestOffsetX, bandDestOffsetY, bandDestWidth, bandDestHeight, bandBuffer, pm);
                    byteArrayOutputStream.reset();
                    MemoryCacheImageOutputStream writeStream = null;
                    ImageInputStream readStream = null;
                    try {
                        writeStream = new MemoryCacheImageOutputStream(byteArrayOutputStream);
                        bandBuffer.writeTo(writeStream);
                        writeStream.flush();
                        readStream = new MemoryCacheImageInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                        for (int y = 0; y < destHeight; y++) {
                            destBuffer.readFrom(y * destWidth + readWidth, bandDestWidth, readStream);
                        }
                        readWidth += bandDestWidth;
                    } finally {
                        if (readStream != null) {
                            readStream.close();
                        }
                        if (writeStream != null) {
                            writeStream.close();
                        }
                    }
                }
            }
            readLines.put(key, destBuffer);
        }
    }

    protected void registerMetadataParser() {
        XmlMetadataParserFactory.registerParser(Jp2XmlMetadata.class, new XmlMetadataParser<>(Jp2XmlMetadata.class));
    }

    /**
     * Returns a File object from the input of the reader.
     * @param input the input object
     * @return  Either a new instance of File, if the input represents the file name, or the casted input File.
     */
    protected File getFileInput(Object input) {
        if (input instanceof String) {
            return new File((String) input);
        } else if (input instanceof File) {
            return (File) input;
        }
        return null;
    }

    void addBands(Product product, File tileFile, int row, int col) {
        try {
            GeoTiffReader tiffReader = new GeoTiffReader(getReaderPlugIn());
            Product tiffProduct = tiffReader.readProductNodes(tileFile, null);
            if (tiffProduct != null) {
                int numTiffBands = tiffProduct.getNumBands();
                for (int idx = 0; idx < numTiffBands; idx++) {
                    Band srcBand = tiffProduct.getBandAt(idx);
                    Band targetBand = product.getBandAt(idx);
                    bandMap.get(targetBand).addCellAt(row, col, srcBand,
                            new Point2D.Float(col * csInfo.getTileWidth(), row * csInfo.getTileHeight()),
                            1, 1);
                    targetBand.setScalingFactor(srcBand.getScalingFactor());
                    targetBand.setScalingOffset(srcBand.getScalingOffset());
                    targetBand.setSampleCoding(srcBand.getSampleCoding());
                    targetBand.setImageInfo(srcBand.getImageInfo());
                }
            }
        } catch (Exception e) {
            logger.severe("Error reading file " + tileFile.getName() + ": " + e.getMessage());
        }
    }

    private ProductData createProductData(int dataType, int size) {
        ProductData buffer;
        switch (dataType) {
            case ProductData.TYPE_UINT8:
                buffer = ProductData.createUnsignedInstance(new byte[size]);
                break;
            case ProductData.TYPE_INT8:
                buffer = ProductData.createInstance(new byte[size]);
                break;
            case ProductData.TYPE_UINT16:
                buffer = ProductData.createUnsignedInstance(new short[size]);
                break;
            case ProductData.TYPE_INT16:
                buffer = ProductData.createInstance(new short[size]);
                break;
            case ProductData.TYPE_INT32:
                buffer = ProductData.createInstance(new int[size]);
                break;
            case ProductData.TYPE_UINT32:
                buffer = ProductData.createUnsignedInstance(new int[size]);
                break;
            case ProductData.TYPE_FLOAT32:
                buffer = ProductData.createInstance(new float[size]);
                break;
            default:
                buffer = ProductData.createUnsignedInstance(new byte[size]);
                break;
        }
        return buffer;
    }

    private void uncompressTiles() {
        final int cols = csInfo.getNumTilesX();
        final int rows = csInfo.getNumTilesY();
        final File[][] tiles = new File[rows][cols];
        List<int[]> unprocessed = new ArrayList<>();
        boolean rowsFirst = rows >= cols;
        if (rowsFirst) {
            Parallel.For(0, rows, x -> {
                for (int y = 0; y < cols; y++) {
                    int tileIndex = y + x * cols;
                    tiles[x][y] = decompressTile(tileIndex);
                    if (!tiles[x][y].exists()) {
                        synchronized (unprocessed) {
                            unprocessed.add(new int[]{x, y, tileIndex});
                        }
                    }
                }
            });
        } else {
            Parallel.For(0, cols, y -> {
                for (int x = 0; x < rows; x++) {
                    int tileIndex = y + x * cols;
                    tiles[x][y] = decompressTile(tileIndex);
                    if (!tiles[x][y].exists()) {
                        synchronized (unprocessed) {
                            unprocessed.add(new int[]{x, y, tileIndex});
                        }
                    }
                }
            });
        }
        Parallel.ForEach(unprocessed, ints -> {
            tiles[ints[0]][ints[1]] = decompressTile(ints[2]);
        });

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                addBands(product, tiles[x][y], x, y);
            }
        }
    }

    private File decompressTile(int index) {
        File tileFile = new File(tmpFolder, "tile_" + String.valueOf(index) + ".tif");
        if (!tileFile.exists()) {
            final OpjExecutor decompress = new OpjExecutor(S2Config.OPJ_DECOMPRESSOR_EXE);
            final Map<String, String> params = new HashMap<String, String>() {{
                put("-i", getFileInput(getInput()).getAbsolutePath());
                put("-r", "0");
                put("-l", String.valueOf(csInfo.getNumLayers()));
            }};
            params.put("-o", tileFile.getAbsolutePath());
            params.put("-t", String.valueOf(index));
            if (decompress.execute(params) != 0) {
                logger.severe(decompress.getLastError());
            } else {
                logger.info("Decompressed tile #" + String.valueOf(index));
            }
        }
        return tileFile;
    }

    private class UnpackProcess implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            try {
                uncompressTiles();
            } finally {
                tilesUncompressed = true;
            }
            return null;
        }
    }

    class GeoTiffReader extends GeoTiffProductReader {

        public GeoTiffReader(ProductReaderPlugIn readerPlugIn) {
            super(readerPlugIn);
        }

        @Override
        protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
            super.readBandRasterDataImpl(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, destBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
        }
    }
}
