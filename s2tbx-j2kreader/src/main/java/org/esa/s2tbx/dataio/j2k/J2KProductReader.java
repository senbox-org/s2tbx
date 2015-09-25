package org.esa.s2tbx.dataio.j2k;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.j2k.internal.J2KProductReaderConstants;
import org.esa.s2tbx.dataio.j2k.internal.JP2MultiLevelSource;
import org.esa.s2tbx.dataio.j2k.internal.OpjExecutor;
import org.esa.s2tbx.dataio.j2k.metadata.*;
import org.esa.s2tbx.dataio.j2k.metadata.ImageInfo;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegExecRetriever;
import org.esa.snap.framework.dataio.AbstractProductReader;
import org.esa.snap.framework.dataio.DecodeQualification;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.framework.datamodel.*;
import org.esa.snap.util.SystemUtils;
import org.geotools.referencing.CRS;

import javax.media.jai.JAI;
import java.awt.geom.Point2D;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final Map<Integer, Integer> dataTypeMap = new HashMap<Integer, Integer>() {{
        put(8, DataBuffer.TYPE_BYTE);
        put(12, DataBuffer.TYPE_USHORT);
        put(16, DataBuffer.TYPE_USHORT);
        put(32, DataBuffer.TYPE_FLOAT);
    }};

    protected Product product;
    protected final Logger logger;
    protected Jp2XmlMetadata metadata;
    protected ImageInfo imageInfo;
    protected CodeStreamInfo csInfo;
    private File tmpFolder;

    protected J2KProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        logger = Logger.getLogger(J2KProductReader.class.getName());
        registerMetadataParser();
    }

    @Override
    public void close() throws IOException {
        JAI.getDefaultInstance().getTileCache().flush();
        if (product != null) {
            for (Band band : product.getBands()) {
                MultiLevelImage sourceImage = band.getSourceImage();
                if (sourceImage != null) {
                    sourceImage.reset();
                    sourceImage = null;
                }
            }
        }
        File[] files = tmpFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete())
                    file.deleteOnExit();
            }
        }
        if (!tmpFolder.delete())
            tmpFolder.deleteOnExit();
        super.close();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        if (getReaderPlugIn().getDecodeQualification(super.getInput()) == DecodeQualification.UNABLE) {
            throw new IOException("The selected product cannot be read with the current reader.");
        }
        File inputFile = getFileInput(getInput());
        tmpFolder = new File(SystemUtils.getCacheDir(), inputFile.getName().toLowerCase().replace(".jp2", "") + "_cached");
        tmpFolder.mkdir();
        logger.info("Reading product metadata");
        try {
            OpjExecutor dumper = new OpjExecutor(OpenJpegExecRetriever.getSafeInfoExtractorAndUpdatePermissions());
            OpjDumpFile dumpFile = new OpjDumpFile(new File(tmpFolder, String.format(J2KProductReaderConstants.JP2_INFO_FILE, inputFile.getName())));
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
                for (int bandIdx = 0; bandIdx < numBands; bandIdx++) {
                    int precision = imageInfo.getComponents().get(bandIdx).getPrecision();
                    Band virtualBand = new Band("band_" + String.valueOf(bandIdx + 1),
                                                precisionTypeMap.get(precision),
                                                imageWidth,
                                                imageHeight);
                    JP2MultiLevelSource source = new JP2MultiLevelSource(
                            getFileInput(getInput()),
                            tmpFolder,
                            bandIdx,
                            imageWidth, imageHeight,
                            csInfo.getTileWidth(), csInfo.getTileHeight(),
                            csInfo.getNumTilesX(), csInfo.getNumTilesY(),
                            csInfo.getNumResolutions(), dataTypeMap.get(precision),
                            product.getGeoCoding());
                    virtualBand.setSourceImage(new DefaultMultiLevelImage(source));
                    product.addBand(virtualBand);
                }
                product.setPreferredTileSize(JAI.getDefaultTileSize());
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
}
