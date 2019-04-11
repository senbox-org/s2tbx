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
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.transform.AffineTransform2D;
import org.esa.snap.core.util.jai.JAIUtils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Basic reader for Ikonos products.
 *
 * @author Denisa Stefanescu
 */

public class IkonosProductReader extends AbstractProductReader {

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
    protected IkonosProductReader(final ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    /**
     * Force deletion of directory
     *
     * @param path path to file/directory
     * @return return true if successful
     */
    private static boolean deleteDirectory(final File path) {
        if (path.exists()) {
            final File[] files = path.listFiles();
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
        final IkonosProductReaderPlugin readerPlugin = (IkonosProductReaderPlugin) getReaderPlugIn();
        final File inputFile = getInputFile();
        this.productDirectory = readerPlugin.getInput(getInput());
        this.tiffProduct = new ArrayList<>();
        final String productFilePath = this.productDirectory.getBasePath();
        String fileName;
        //product file name differs from archive file name
        if (this.productDirectory.isCompressed()) {
            fileName = productFilePath.substring(productFilePath.lastIndexOf(File.separator) + 1, productFilePath.lastIndexOf(IkonosConstants.PRODUCT_FILE_SUFFIX));
        } else {
            fileName = productFilePath.substring(productFilePath.lastIndexOf(File.separator) + 1, productFilePath.lastIndexOf("."));
        }

        this.metadata = IkonosMetadata.create(this.productDirectory.getFile(fileName + IkonosConstants.METADATA_FILE_SUFFIX).toPath());

        if (metadata != null) {

            this.metadata.unZipImageFiles(this.productDirectory.getFile(fileName + IkonosConstants.ARCHIVE_FILE_EXTENSION).toPath().toString());

            final String dir = metadata.getImageDirectoryPath();
            final File[] imageDirectoryFileList = new File(dir).listFiles();
            Matcher matcher = Pattern.compile(IkonosConstants.PATH_ZIP_FILE_NAME_PATTERN).matcher(imageDirectoryFileList[0].getName());
            String imageDirectoryName = null;
            while (matcher.find()) {
                imageDirectoryName = matcher.group();
            }

            this.metadata.getMetadataComponent().setImageDirectoryName(imageDirectoryName + ".ZIP");

            this.metadata.createBandMetadata();

            final List<BandMetadata> bandMetadataList = this.metadata.getBandsMetadata();
            final BandMetadataUtil bUtil = new BandMetadataUtil(bandMetadataList.toArray(new BandMetadata[bandMetadataList.size()]));
            final int width = bUtil.getMaxNumColumns();
            final int height = bUtil.getMaxNumLines();
            this.product = new Product(this.metadata.getProductName(), IkonosConstants.PRODUCT_GENERIC_NAME, width, height);
            this.product.setStartTime(this.metadata.getProductStartTime());
            this.product.setEndTime(this.metadata.getProductEndTime());
            this.product.setDescription(this.metadata.getProductDescription());
            this.product.getMetadataRoot().addElement(this.metadata.getRootElement());
            this.product.setFileLocation(inputFile);
            this.product.setProductReader(this);

            final String dirPath = this.metadata.getImageDirectoryPath();
            final String dirNameExtension = this.metadata.getMetadataComponent().getImageDirectoryName();
            final String dirName = dirNameExtension.substring(0, dirNameExtension.lastIndexOf("."));
            int levels;
            for (BandMetadata aBandMetadataList : bandMetadataList) {
                final String imageFileName = aBandMetadataList.getImageFileName();
                this.tiffProduct.add(ProductIO.readProduct(Paths.get(dirPath).resolve(dirName).resolve(imageFileName + IkonosConstants.IMAGE_EXTENSION).toFile()));
                this.tiffImageIndex++;
                final Band band = this.tiffProduct.get(this.tiffImageIndex - 1).getBandAt(0);
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
                        switch (IkonosConstants.BAND_NAMES[bandNameIndex]) {
                            case "1":
                                bandName = "Blue";
                                break;
                            case "2":
                                bandName = "Green";
                                break;
                            case "3":
                                bandName = "Red";
                                break;
                            case "4":
                                bandName = "Near";
                                break;
                        }
                        bandGain = IkonosConstants.BAND_GAIN[bandNameIndex];
                    }
                }
                if (bandName == null) {
                    bandName = IkonosConstants.BAND_NAMES[4];
                    bandGain = Arrays.asList(IkonosConstants.BAND_GAIN).stream().mapToDouble(p -> p).sum() / (IkonosConstants.BAND_NAMES.length - 1);
                    final GeoCoding bandGeoCoding = this.tiffProduct.get(this.tiffImageIndex - 1).getSceneGeoCoding();
                    if (bandGeoCoding != null && this.product.getSceneGeoCoding() == null) {
                        this.product.setSceneGeoCoding(bandGeoCoding);
                    }
                }

                final Band targetBand = new Band(bandName, band.getDataType(), band.getRasterWidth(), band.getRasterHeight());
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
                        final AffineTransform2D transform2D =
                                new AffineTransform2D((float) width / band.getRasterWidth(), 0.0, 0.0,
                                                      (float) height / band.getRasterHeight(), 0.0, 0.0);
                        targetBand.setGeoCoding(addTiePointGridGeo(this.metadata, targetBand));
                        targetBand.setImageToModelTransform(transform2D);
                    }
                }
                final MosaicMultiLevelSource bandSource =
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
    private void initProductTiePointGeoCoding(final IkonosMetadata ikonosMetadata, final Product product) {
        float[][] cornerLonsLats = ikonosMetadata.getMetadataComponent().getTiePointGridPoints();
        final int sceneWidth = product.getSceneRasterWidth();
        final int sceneHeight = product.getSceneRasterHeight();
        final TiePointGrid latGrid = createTiePointGrid(IkonosConstants.LAT_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[0]);
        product.addTiePointGrid(latGrid);
        final TiePointGrid lonGrid = createTiePointGrid(IkonosConstants.LON_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[1]);
        product.addTiePointGrid(lonGrid);
        if (latGrid != null && lonGrid != null) {
            product.setSceneGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
        }
    }

    private GeoCoding addTiePointGridGeo(final IkonosMetadata metadata, final Band targetBand) {
        final float[][] cornerLonsLats = metadata.getMetadataComponent().getTiePointGridPoints();
        final int sceneWidth = targetBand.getRasterWidth();
        final int sceneHeight = targetBand.getRasterHeight();
        final TiePointGrid latGrid = createTiePointGrid(IkonosConstants.LAT_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[0]);
        final TiePointGrid lonGrid = createTiePointGrid(IkonosConstants.LON_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[1]);
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
