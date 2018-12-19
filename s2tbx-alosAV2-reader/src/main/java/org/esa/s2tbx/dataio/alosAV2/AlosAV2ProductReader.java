package org.esa.s2tbx.dataio.alosAV2;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alosAV2.internal.AlosAV2Constants;
import org.esa.s2tbx.dataio.alosAV2.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.alosAV2.metadata.AlosAV2Metadata;
import org.esa.s2tbx.dataio.alosAV2.metadata.BandMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.transform.AffineTransform2D;
import org.esa.snap.core.util.jai.JAIUtils;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AlosAV2ProductReader extends AbstractProductReader {
    private static final Logger logger = Logger.getLogger(AlosAV2ProductReader.class.getName());

    private Product product;
    private VirtualDirEx productDirectory;
    private List<Product> tiffProduct;
    private AlosAV2Metadata metadata;
    private int tiffImageIndex;

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be {@code null} for internal reader
     *                     implementations
     */
    protected AlosAV2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }


    @Override
    protected Product readProductNodesImpl() throws IOException {
        AlosAV2ProductReaderPlugin readerPlugin = (AlosAV2ProductReaderPlugin) getReaderPlugIn();
        this.productDirectory = readerPlugin.getInput(getInput());
        this.tiffProduct = new ArrayList<>();
        String productFilePath = this.productDirectory.getBasePath();
        String fileName;
        //product file name differs from archive file name
        if (this.productDirectory.isCompressed()) {
            fileName = productFilePath.substring(productFilePath.lastIndexOf(File.separator) + 1, productFilePath.lastIndexOf(AlosAV2Constants.PRODUCT_FILE_SUFFIX));
        } else {
            fileName = productFilePath.substring(productFilePath.lastIndexOf(File.separator) + 1, productFilePath.lastIndexOf("."));
        }
        this.metadata = AlosAV2Metadata.create(this.productDirectory.getFile(fileName + AlosAV2Constants.METADATA_FILE_SUFFIX).toPath());

        if (metadata != null) {
            String dir = null;
            if (!productDirectory.exists(fileName)) {
                this.metadata.unZipImageFiles(this.productDirectory.getFile(fileName + AlosAV2Constants.ARCHIVE_FILE_EXTENSION).toPath().toString());
                Path folderPath = Paths.get(metadata.getImageDirectoryPath()).resolve(fileName);
                dir = folderPath.toString();
            } else {
                dir = productDirectory.getFile(fileName).toString();
            }


            File[] imageDirectoryFileList = new File(dir).listFiles();
            List<BandMetadata> imageMetadatas = new ArrayList<>();
            for (File file : imageDirectoryFileList) {
                if (file.getName().endsWith(AlosAV2Constants.IMAGE_METADATA_EXTENSION)) {
                    BandMetadata imgMetadata = BandMetadata.create(file.toPath());
                    imgMetadata.setImageFileName(file.getName().substring(0,file.getName().indexOf(".")));
                    imageMetadatas.add(imgMetadata);
                }
            }

            if (imageMetadatas.size() == 0) {
                throw new IOException("No raster found");
            }
            int width = metadata.getRasterWidth();
            int height = metadata.getRasterHeight();
            product = new Product(this.metadata.getProductName(), AlosAV2Constants.PRODUCT_TYPE, width, height);
            product.setFileLocation(new File(metadata.getPath()));
            product.setStartTime(metadata.getProductStartTime());
            product.setEndTime(metadata.getProductEndTime());
            product.setDescription(metadata.getProductDescription());
            for (BandMetadata imageMetadata : imageMetadatas) {
                product.getMetadataRoot().addElement(imageMetadata.getRootElement());
                String imageFileName = imageMetadata.getImageFileName();
                this.tiffProduct.add(ProductIO.readProduct(Paths.get(dir).resolve(imageFileName + AlosAV2Constants.IMAGE_EXTENSION).toFile()));
                this.tiffImageIndex++;

                int numBands = imageMetadata.getNumBands();
                BandMetadata.BandInfo[] bandInfos = imageMetadata.getBandsInformation();
                int noDataValue = imageMetadata.getNoDataValue();
                for (int index = 0; index < numBands; index++) {
                    Band band = this.tiffProduct.get(this.tiffImageIndex - 1).getBandAt(index);
                    if (this.tiffProduct.get(this.tiffImageIndex - 1).getSceneGeoCoding() == null &&
                            this.product.getSceneGeoCoding() == null) {
                        initProductTiePointGeoCoding(this.metadata, this.product);
                    }
                    int levels = band.getSourceImage().getModel().getLevelCount();
                    final Dimension tileSize = JAIUtils.computePreferredTileSize(band.getRasterWidth(), band.getRasterHeight(), 1);
                    GeoCoding bandGeoCoding = this.tiffProduct.get(this.tiffImageIndex - 1).getSceneGeoCoding();
                    if (bandGeoCoding != null && this.product.getSceneGeoCoding() == null) {
                        this.product.setSceneGeoCoding(bandGeoCoding);
                    }
                    Band targetBand = new Band(band.getName(), band.getDataType(),
                                               band.getRasterWidth(),
                                               band.getRasterHeight());
                    targetBand.setSpectralBandIndex(numBands > 1 ? index : -1);
                    targetBand.setSpectralWavelength(bandInfos[index].getCentralWavelength());
                    targetBand.setSpectralBandwidth(bandInfos[index].getBandwidth());
                    targetBand.setUnit(bandInfos[index].getUnit());
                    targetBand.setNoDataValue(noDataValue);
                    targetBand.setNoDataValueUsed(true);
                    targetBand.setScalingFactor(bandInfos[index].getGain());
                    targetBand.setScalingOffset(bandInfos[index].getBias());
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
                                                       levels,band.getGeoCoding() != null ? targetBand.getGeoCoding() != null ?
                                    Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                    Product.findImageToModelTransform(product.getSceneGeoCoding()) :
                                                               targetBand.getImageToModelTransform());
                    targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));

                    product.addBand(targetBand);
                }
            }
        }

        return this.product;
    }

    /**
     * Creates geo-coding based on latitude/longitude {@code TiePointGrid}s</li>
     *
     */
    private void initProductTiePointGeoCoding(AlosAV2Metadata alosMetadata, Product product) {
        float[][] cornerLonsLats = alosMetadata.getMetadataComponent().getTiePointGridPoints();
        int sceneWidth = product.getSceneRasterWidth();
        int sceneHeight = product.getSceneRasterHeight();
        TiePointGrid latGrid = createTiePointGrid(AlosAV2Constants.LAT_DS_NAME, 2, 2, 0, 0, sceneWidth , sceneHeight, cornerLonsLats[0]);
        product.addTiePointGrid(latGrid);
        TiePointGrid lonGrid = createTiePointGrid(AlosAV2Constants.LON_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[1]);
        product.addTiePointGrid(lonGrid);
        if (latGrid != null && lonGrid != null) {
            product.setSceneGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
        }
    }

    private GeoCoding addTiePointGridGeo(AlosAV2Metadata metadata, Band targetBand) {
        float[][] cornerLonsLats = metadata.getMetadataComponent().getTiePointGridPoints();
        int sceneWidth = targetBand.getRasterWidth();
        int sceneHeight =  targetBand.getRasterHeight();
        TiePointGrid latGrid = createTiePointGrid(AlosAV2Constants.LAT_DS_NAME, 2, 2, 0, 0, sceneWidth , sceneHeight, cornerLonsLats[0]);
        TiePointGrid lonGrid = createTiePointGrid(AlosAV2Constants.LON_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[1]);
        return new TiePointGeoCoding(latGrid, lonGrid);
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

    private File getInputFile() throws FileNotFoundException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }

        return inputFile;
    }

    public void close() throws IOException {
        System.gc();
        super.close();
    }
}
