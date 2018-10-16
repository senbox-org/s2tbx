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
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.transform.AffineTransform2D;
import org.esa.snap.core.util.jai.JAIUtils;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Basic reader for Kompsat 2 products.
 *
 * @author Razvan Dumitrascu
 */

public class Kompsat2ProductReader  extends AbstractProductReader {

    private VirtualDirEx productDirectory;
    private Kompsat2Metadata metadata;
    private List<Product> tiffProduct;
    private Product product;
    private int tiffImageIndex;

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be {@code null} for internal reader
     *                     implementations
     */
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

        this.metadata = Kompsat2Metadata.create(this.productDirectory.getFile(fileName + Kompsat2Constants.METADATA_FILE_SUFFIX).toPath());
        if(metadata != null) {
            this.metadata.unZipImageFiles(this.productDirectory.getFile(fileName + Kompsat2Constants.ARCHIVE_FILE_EXTENSION).toPath().toString());
            this.metadata.createBandMetadata();
            List<BandMetadata> bandMetadataList = this.metadata.getBandsMetadata();
            BandMetadataUtil bUtil = new BandMetadataUtil(bandMetadataList.toArray(new BandMetadata[bandMetadataList.size()]));
            int width = bUtil.getMaxNumColumns();
            int height = bUtil.getMaxNumLines();
            this.product = new Product(this.metadata.getProductName(), Kompsat2Constants.KOMPSAT2_PRODUCT, width, height);
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
                this.tiffProduct.add(ProductIO.readProduct(Paths.get(dirPath).resolve(dirName).resolve(imageFileName + Kompsat2Constants.IMAGE_EXTENSION).toFile()));
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
                for (int bandNameIndex = 0; bandNameIndex < Kompsat2Constants.BAND_NAMES.length - 1; bandNameIndex++) {
                    if (imageFileName.contains(Kompsat2Constants.FILE_NAMES[bandNameIndex])) {
                        bandName = Kompsat2Constants.BAND_NAMES[bandNameIndex];
                        bandGain = Kompsat2Constants.KOMPSAT2_GAIN_VALUES[bandNameIndex];
                        bandGainPan += Kompsat2Constants.KOMPSAT2_GAIN_VALUES[bandNameIndex];
                    }
                }
                if (bandName == null) {
                    bandName = Kompsat2Constants.BAND_NAMES[4];
                    bandGain = bandGainPan / (Kompsat2Constants.BAND_NAMES.length - 1);
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
                targetBand.setUnit(Kompsat2Constants.KOMPSAT2_UNIT);
                targetBand.setNoDataValue(band.getNoDataValue());
                targetBand.setNoDataValueUsed(true);
                targetBand.setScalingFactor(bandGain);
                targetBand.setScalingOffset(band.getScalingOffset());
                targetBand.setDescription(band.getDescription());
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
     * @param k2Metadata Kompsat2Metadata parameter
     * @param product Product to add TiePointGrid and TiePointGridGeoCoding
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

    private GeoCoding addTiePointGridGeo(Kompsat2Metadata metadata, Band targetBand) {
        float[][] cornerLonsLats = metadata.getMetadataComponent().getTiePointGridPoints();
        int sceneWidth = targetBand.getRasterWidth();
        int sceneHeight =  targetBand.getRasterHeight();
        TiePointGrid latGrid = createTiePointGrid(Kompsat2Constants.LAT_DS_NAME, 2, 2, 0, 0, sceneWidth , sceneHeight, cornerLonsLats[0]);
        TiePointGrid lonGrid = createTiePointGrid(Kompsat2Constants.LON_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[1]);
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
        if(this.tiffProduct != null) {
            for (Iterator<Product> iterator = this.tiffProduct.listIterator(); iterator.hasNext(); ) {
                Product product = iterator.next();
                if (product != null) {
                    product.closeIO();
                    product.dispose();
                    iterator.remove();
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

    private File getInputFile() throws FileNotFoundException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }

        return inputFile;
    }
}
