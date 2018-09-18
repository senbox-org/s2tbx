package org.esa.s2tbx.dataio.ikonos;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
import org.esa.s2tbx.dataio.ikonos.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.ikonos.metadata.BandMetadata;
import org.esa.s2tbx.dataio.ikonos.metadata.BandMetadataUtil;
import org.esa.s2tbx.dataio.ikonos.metadata.IkonosMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.transform.AffineTransform2D;
import org.esa.snap.core.util.jai.JAIUtils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Basic reader for Ikonos products.
 *
 * @author Denisa Stefanescu
 */

public class IkonosProductReader extends AbstractProductReader {
    private static final Logger logger = Logger.getLogger(IkonosProductReader.class.getName());

    private VirtualDirEx productDirectory;
    private Product product;
    private IkonosMetadata metadata;
    private List<Product> tiffProduct;
    private int tiffImageIndex;

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be {@code null} for internal reader
     *                     implementations
     */
    protected IkonosProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    /**
     * Force deletion of directory
     *
     * @param path path to file/directory
     * @return return true if successful
     */
    private static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return (path.delete());
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        IkonosProductReaderPlugin readerPlugin = (IkonosProductReaderPlugin) getReaderPlugIn();
        final File inputFile = getInputFile();
        this.productDirectory = readerPlugin.getInput(getInput());
        this.tiffProduct = new ArrayList<>();
        String productFilePath = this.productDirectory.getBasePath();
        String fileName;
        //product file name differs from archive file name
        if (this.productDirectory.isCompressed()) {
            fileName = productFilePath.substring(productFilePath.lastIndexOf("\\") + 1, productFilePath.lastIndexOf(IkonosConstants.PRODUCT_FILE_SUFFIX));
        } else {
            fileName = productFilePath.substring(productFilePath.lastIndexOf("\\") + 1, productFilePath.lastIndexOf("."));
        }

        this.metadata = IkonosMetadata.create(this.productDirectory.getFile(fileName + IkonosConstants.METADATA_FILE_SUFFIX).toPath());

        if (metadata != null) {

            this.metadata.unZipImageFiles(this.productDirectory.getFile(fileName + IkonosConstants.ARCHIVE_FILE_EXTENSION).toPath().toString());

            String dir = metadata.getImageDirectoryPath();
            File[] imageDirectoryFileList = new File(dir).listFiles();
            Matcher matcher = Pattern.compile(IkonosConstants.PATH_ZIP_FILE_NAME_PATTERN).matcher(imageDirectoryFileList[0].getName());
            String imageDirectoryName = null;
            while (matcher.find()) {
                imageDirectoryName = matcher.group().toString();
            }

            this.metadata.getMetadataComponent().setImageDirectoryName(imageDirectoryName + ".ZIP");

            this.metadata.createBandMetadata();

            List<BandMetadata> bandMetadataList = this.metadata.getBandsMetadata();
            BandMetadataUtil bUtil = new BandMetadataUtil(bandMetadataList.toArray(new BandMetadata[bandMetadataList.size()]));
            int width = bUtil.getMaxNumColumns();
            int height = bUtil.getMaxNumLines();
            this.product = new Product(this.metadata.getProductName(), IkonosConstants.PRODUCT_GENERIC_NAME, width, height);
            this.product.setStartTime(this.metadata.getProductStartTime());
            this.product.setEndTime(this.metadata.getProductEndTime());
            this.product.setDescription(this.metadata.getProductDescription());
            this.product.getMetadataRoot().addElement(this.metadata.getRootElement());
            this.product.setFileLocation(inputFile);
            this.product.setProductReader(this);

            String dirPath = this.metadata.getImageDirectoryPath();
            String dirNameExtension = this.metadata.getMetadataComponent().getImageDirectoryName();
            String dirName = dirNameExtension.substring(0, dirNameExtension.lastIndexOf("."));
            int levels;
            Double bandGainPan = 0.0;
            for (BandMetadata aBandMetadataList : bandMetadataList) {
                String imageFileName = aBandMetadataList.getImageFileName();
                this.tiffProduct.add(ProductIO.readProduct(Paths.get(dirPath).resolve(dirName).resolve(imageFileName + IkonosConstants.IMAGE_EXTENSION).toFile()));
                this.tiffImageIndex++;
                Band band = this.tiffProduct.get(this.tiffImageIndex - 1).getBandAt(0);
                if (this.tiffProduct.get(this.tiffImageIndex - 1).getSceneGeoCoding() == null &&
                        this.product.getSceneGeoCoding() == null) {
                    initProductTiePointGeoCoding(this.metadata, this.product);
                }
                levels = band.getSourceImage().getModel().getLevelCount();
                final Dimension tileSize = JAIUtils.computePreferredTileSize(band.getRasterWidth(), band.getRasterHeight(), 1);
                String bandName = null;
                Double bandGain = null;

                for (int bandNameIndex = 0; bandNameIndex < IkonosConstants.BAND_NAMES.length - 1; bandNameIndex++) {
                    if (imageFileName.contains(IkonosConstants.FILE_NAMES[bandNameIndex])) {
                        bandName = IkonosConstants.BAND_NAMES[bandNameIndex];
                        bandGain = IkonosConstants.BAND_GAIN[bandNameIndex];
                        bandGainPan += IkonosConstants.BAND_GAIN[bandNameIndex];
                    }
                }
                if (bandName == null) {
                    bandName = IkonosConstants.BAND_NAMES[4];
                    bandGain = bandGainPan / (IkonosConstants.BAND_NAMES.length - 1);
                    GeoCoding bandGeoCoding = this.tiffProduct.get(this.tiffImageIndex - 1).getSceneGeoCoding();
                    if (bandGeoCoding != null && this.product.getSceneGeoCoding() == null) {
                        this.product.setSceneGeoCoding(bandGeoCoding);
                    }
                }

                Band targetBand = new Band(bandName, band.getDataType(), band.getRasterWidth(), band.getRasterHeight());
                targetBand.setSpectralBandIndex(band.getSpectralBandIndex());
                targetBand.setSpectralWavelength(band.getSpectralWavelength());
                targetBand.setSpectralBandwidth(band.getSpectralBandwidth());
                targetBand.setSolarFlux(band.getSolarFlux());
                targetBand.setUnit(IkonosConstants.BAND_MEASURE_UNIT);
                targetBand.setNoDataValue(band.getNoDataValue());
                targetBand.setNoDataValueUsed(true);
                targetBand.setDescription(band.getDescription());
                targetBand.setScalingFactor(bandGain);

                if (band.getGeoCoding() != null) {
                    targetBand.setGeoCoding(band.getGeoCoding());
                } else {
                    if (width != band.getRasterWidth()) {
                        AffineTransform2D transform2D =
                                new AffineTransform2D((float) width / band.getRasterWidth(), 0.0, 0.0,
                                        (float) height / band.getRasterHeight(), 0.0, 0.0);
                        targetBand.setGeoCoding(addTiePointGridGeo(this.metadata, targetBand));
                        targetBand.setImageToModelTransform(transform2D);
                    }
                }
                MosaicMultiLevelSource bandSource =
                        new MosaicMultiLevelSource(band,
                                band.getRasterWidth(), band.getRasterHeight(),
                                tileSize.width, tileSize.height,
                                levels, band.getGeoCoding() != null ? targetBand.getGeoCoding() != null ?
                                Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                Product.findImageToModelTransform(product.getSceneGeoCoding()) :
                                targetBand.getImageToModelTransform());
                targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
                this.product.addBand(targetBand);
            }
        }
        return this.product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY,
                                          int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY,
                                          Band destBand,
                                          int destOffsetX, int destOffsetY,
                                          int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm) throws IOException {

    }

    /**
     * Creates geo-coding based on latitude/longitude {@code TiePointGrid}s</li>
     *
     * @param ikonosMetadata IkonosMetadata parameter
     * @param product        Product to add TiePointGrid and TiePointGridGeoCoding
     */
    private void initProductTiePointGeoCoding(IkonosMetadata ikonosMetadata, Product product) {
        float[][] cornerLonsLats = ikonosMetadata.getMetadataComponent().getTiePointGridPoints();
        int sceneWidth = product.getSceneRasterWidth();
        int sceneHeight = product.getSceneRasterHeight();
        TiePointGrid latGrid = createTiePointGrid(IkonosConstants.LAT_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[0]);
        product.addTiePointGrid(latGrid);
        TiePointGrid lonGrid = createTiePointGrid(IkonosConstants.LON_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[1]);
        product.addTiePointGrid(lonGrid);
        if (latGrid != null && lonGrid != null) {
            product.setSceneGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
        }
    }

    private GeoCoding addTiePointGridGeo(IkonosMetadata metadata, Band targetBand) {
        float[][] cornerLonsLats = metadata.getMetadataComponent().getTiePointGridPoints();
        int sceneWidth = targetBand.getRasterWidth();
        int sceneHeight = targetBand.getRasterHeight();
        TiePointGrid latGrid = createTiePointGrid(IkonosConstants.LAT_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[0]);
        TiePointGrid lonGrid = createTiePointGrid(IkonosConstants.LON_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[1]);
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    @Override
    public void close() throws IOException {
        System.gc();
        if (product != null) {
            for (Band band : product.getBands()) {
                MultiLevelImage sourceImage = band.getSourceImage();
                if (sourceImage != null) {
                    sourceImage.reset();
                    sourceImage.dispose();
                }
            }
        }
        if (this.productDirectory != null) {
            this.productDirectory.close();
            this.productDirectory = null;
        }
        if (this.tiffProduct != null) {
            for (Iterator<Product> iterator = this.tiffProduct.listIterator(); iterator.hasNext(); ) {
                Product product = iterator.next();
                if (product != null) {
                    product.closeIO();
                    product.dispose();
                    iterator.remove();
                }
            }
        }
        if (this.metadata != null) {
            File imageDir = new File(this.metadata.getImageDirectoryPath());
            if (imageDir.exists()) {
                deleteDirectory(imageDir);
            }
        }
        super.close();
    }

    /**
     * Return a file from the input and if the input file does not exist an error is throws
     *
     * @return inputFile
     * @throws FileNotFoundException
     */
    private File getInputFile() throws FileNotFoundException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }

        return inputFile;
    }
}
