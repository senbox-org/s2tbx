package org.esa.s2tbx.dataio.kompsat2;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.s2tbx.dataio.kompsat2.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.kompsat2.metadata.BandMetadata;
import org.esa.s2tbx.dataio.kompsat2.metadata.BandMetadataUtil;
import org.esa.s2tbx.dataio.kompsat2.metadata.Kompsat2Metadata;
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
import java.lang.ref.WeakReference;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Basic reader for Kompsat 2 products.
 *
 * @author Razvan Dumitrascu
 */

public class Kompsat2ProductReader  extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(Kompsat2ProductReader.class.getName());

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be {@code null} for internal reader
     *                     implementations
     */
    private VirtualDirEx productDirectory;
    private Kompsat2Metadata metadata;
    private List<BandMetadata> bandMetadataList;
    private List<Product> tiffProduct;
    private Product product;
    private int tiffImageIndex;
    protected Kompsat2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Kompsat2ProductReaderPlugin readerPlugin = (Kompsat2ProductReaderPlugin)getReaderPlugIn();
        final File inputFile = getInputFile();
        this.productDirectory = readerPlugin.getInput(getInput());
        this.tiffProduct = new ArrayList<>();
        String productFilePath = this.productDirectory.getBasePath();
        String fileName;
        //product file name differs from archive file name
        if (this.productDirectory.isCompressed()) {
            fileName = productFilePath.substring(productFilePath.lastIndexOf("\\")+1,productFilePath.lastIndexOf(Kompsat2Constants.Product_FILE_SUFFIX));
        } else {
            fileName = productFilePath.substring(productFilePath.lastIndexOf("\\")+1,productFilePath.lastIndexOf("."));
        }
        this.metadata = Kompsat2Metadata.create(this.productDirectory.getFile(fileName+Kompsat2Constants.METADATA_FILE_SUFFIX).toPath());
        this.metadata.unZipImageFiles(this.productDirectory.getFile(fileName + Kompsat2Constants.ARCHIVE_FILE_EXTENSION).toPath().toString());
        this.metadata.createBandMetadata();
        this.bandMetadataList = this.metadata.getBandsMetadata();
        BandMetadataUtil bUtil = new BandMetadataUtil(this.bandMetadataList.toArray(new BandMetadata[this.bandMetadataList.size()]));
        int width = bUtil.getMaxNumColumns();
        int height = bUtil.getMaxNumLines();
        this.product = new Product(this.metadata.getProductName(),Kompsat2Constants.KOMPSAT2_PRODUCT, width, height);
        this.product.setFileLocation(new File (this.metadata.getPath()));
        this.product.setStartTime(this.metadata.getProductStartTime());
        this.product.setEndTime(this.metadata.getProductEndTime());
        this.product.setDescription(this.metadata.getProductDescription());
        this.product.getMetadataRoot().addElement(this.metadata.getRootElement());
        this.product.setFileLocation(inputFile);
        this.product.setProductReader(this);
        String dirPath = this.metadata.getImageDirectoryPath();
        String dirNameExtension = this.metadata.getMetadataComponent().getImageDirectoryName();
        String dirName = dirNameExtension.substring(0,dirNameExtension.lastIndexOf("."));
        int levels;
        for (int index = 0; index< this.bandMetadataList.size(); index++) {
            String imageFileName = this.bandMetadataList.get(index).getImageFileName();
            this.tiffProduct.add(ProductIO.readProduct(Paths.get(dirPath).resolve(dirName).resolve(imageFileName+Kompsat2Constants.IMAGE_EXTENSION).toFile()));
            this.tiffImageIndex++;
            Band band  = this.tiffProduct.get(this.tiffImageIndex-1).getBandAt(0);
            levels = band.getSourceImage().getModel().getLevelCount();
            final Dimension tileSize = JAIUtils.computePreferredTileSize(band.getRasterWidth(), band.getRasterHeight(), 1);
            String bandName = null;
            for (int bandNameIndex = 0; bandNameIndex<Kompsat2Constants.BAND_NAMES.length-1; bandNameIndex++) {
                if (imageFileName.contains(Kompsat2Constants.FILE_NAMES[bandNameIndex])) {
                    bandName = Kompsat2Constants.BAND_NAMES[bandNameIndex];
                }
            }
            if (bandName == null) {
                bandName = Kompsat2Constants.BAND_NAMES[4];
                GeoCoding bandGeoCoding = this.tiffProduct.get(this.tiffImageIndex-1).getSceneGeoCoding();
                if (bandGeoCoding != null && this.product.getSceneGeoCoding() == null) {
                    this.product.setSceneGeoCoding(bandGeoCoding);
                }
            }
            Band targetBand = new Band(bandName, band.getDataType(), band.getRasterWidth(), band.getRasterHeight());
            targetBand.setSpectralBandIndex(band.getSpectralBandIndex());
            targetBand.setSpectralWavelength(band.getSpectralWavelength());
            targetBand.setSpectralBandwidth(band.getSpectralBandwidth());
            targetBand.setSolarFlux(band.getSolarFlux());
            targetBand.setUnit(band.getUnit());
            targetBand.setNoDataValue(band.getNoDataValue());
            targetBand.setNoDataValueUsed(true);
            targetBand.setScalingFactor(band.getScalingFactor());
            targetBand.setScalingOffset(band.getScalingOffset());
            targetBand.setUnit(band.getUnit());
            targetBand.setDescription(band.getDescription());
            if (band.getGeoCoding() != null) {
                targetBand.setGeoCoding(band.getGeoCoding());
            } else {
                if (width != band.getRasterWidth()) {
                    AffineTransform2D transform2D =
                            new AffineTransform2D((float) width /  band.getRasterWidth(), 0.0, 0.0,
                                    (float) height / band.getRasterHeight(), 0.0, 0.0);
                    targetBand.setImageToModelTransform(transform2D);
                }
            }

            MosaicMultiLevelSource bandSource =
                    new MosaicMultiLevelSource(band,
                            band.getRasterWidth(), band.getRasterHeight(),
                            tileSize.width, tileSize.height,
                            levels,targetBand.getGeoCoding()!= null ?
                            Product.findImageToModelTransform(targetBand.getGeoCoding()):
                            targetBand.getImageToModelTransform());
            targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
            this.product.addBand(targetBand);

        }
        if (this.product.getSceneGeoCoding() == null) {
            initProductTiePointGeoCoding(this.metadata, this.product);
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
     * @param k2Metadata
     * @param product
     */
    private void initProductTiePointGeoCoding(Kompsat2Metadata k2Metadata, Product product) {
        float[][] cornerLonsLats = k2Metadata.getMetadataComponent().getTiePointGridPoints();
        int sceneWidth = product.getSceneRasterWidth();
        int sceneHeight = product.getSceneRasterHeight();
        TiePointGrid latGrid = createTiePointGrid(Kompsat2Constants.LAT_DS_NAME, 2, 2, 0, 0, sceneWidth , sceneHeight, cornerLonsLats[0]);
        product.addTiePointGrid(latGrid);
        TiePointGrid lonGrid = createTiePointGrid(Kompsat2Constants.LON_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[1]);
        product.addTiePointGrid(lonGrid);
        if (latGrid != null && lonGrid != null) {
            product.setSceneGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
        }
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
                    sourceImage = null;
                }
            }
        }
        if (this.productDirectory != null) {
            this.productDirectory.close();
            this.productDirectory = null;
        }
        if(this.tiffProduct != null) {
            for (Iterator<Product> iter = this.tiffProduct.listIterator(); iter.hasNext(); ) {
                Product product = iter.next();
                if (product != null) {
                    product.closeIO();
                    product.dispose();
                    product = null;
                    iter.remove();
                }
            }
        }
        if(this.metadata != null) {
            File imageDir = new File(this.metadata.getImageDirectoryPath());
            if (imageDir.exists()) {
                deleteDirectory(imageDir);
            }
        }
        super.close();
    }

    /**
     * Force deletion of directory
     * @param path
     * @return
     */
    private static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files =path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    private File getInputFile() throws FileNotFoundException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }

        return inputFile;
    }

}
