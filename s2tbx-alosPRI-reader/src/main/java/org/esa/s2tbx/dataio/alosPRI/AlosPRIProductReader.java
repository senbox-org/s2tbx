package org.esa.s2tbx.dataio.alosPRI;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alosPRI.internal.AlosPRIConstants;
import org.esa.s2tbx.dataio.alosPRI.internal.AlosPRIMetadata;
import org.esa.s2tbx.dataio.alosPRI.internal.ImageMetadata;
import org.esa.s2tbx.dataio.alosPRI.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.util.jai.JAIUtils;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * This product reader is intended for reading ALOS PRISM files
 *
 * @author Denisa Stefanescu
 */

public class AlosPRIProductReader extends AbstractProductReader {
    private static final Logger logger = Logger.getLogger(AlosPRIProductReader.class.getName());

    private VirtualDirEx productDirectory;
    private AlosPRIMetadata metadata;
    private Product product;
    private List<Product> tiffProduct;
    private int tiffImageIndex;

    static {
        XmlMetadataParserFactory.registerParser(AlosPRIMetadata.class, new AlosPRIMetadata.AlosPRIMetadataParser(AlosPRIMetadata.class));
    }

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be {@code null} for internal reader
     *                     implementations
     */
    public AlosPRIProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        File inputFile = getFileInput(getInput());
        AlosPRIProductReaderPlugin readerPlugin = (AlosPRIProductReaderPlugin) getReaderPlugIn();
        productDirectory = readerPlugin.getInput(getInput());
        this.tiffProduct = new ArrayList<>();
        String productFilePath = this.productDirectory.getBasePath();
        String fileName;

        if (this.productDirectory.isCompressed()) {
            fileName = productFilePath.substring(productFilePath.lastIndexOf("\\") + 1, productFilePath.lastIndexOf(AlosPRIConstants.PRODUCT_FILE_SUFFIX));
        } else {
            fileName = productFilePath.substring(productFilePath.lastIndexOf("\\") + 1, productFilePath.lastIndexOf("."));
        }

        this.metadata = XmlMetadata.create(AlosPRIMetadata.class, this.productDirectory.getFile(fileName + AlosPRIConstants.METADATA_FILE_SUFFIX).toPath());
        if (metadata != null) {
            if (productDirectory.isCompressed()) {
                this.metadata.unZipImageFiles(this.productDirectory.getFile(fileName + AlosPRIConstants.ARCHIVE_FILE_EXTENSION).toPath().toString());
                productDirectory = VirtualDirEx.create(new File(metadata.getImageDirectoryPath()));
                productDirectory.setFolderDepth(4);
            } else {
                productDirectory.setFolderDepth(4);
                if (productDirectory.exists(fileName)) {
                    productDirectory = VirtualDirEx.create(new File(inputFile.getAbsolutePath().substring(0, inputFile.getAbsolutePath().indexOf(AlosPRIConstants.METADATA_FILE_SUFFIX))));
                    productDirectory.setFolderDepth(4);

                } else {
                    this.metadata.unZipImageFiles(this.productDirectory.getFile(fileName + AlosPRIConstants.ARCHIVE_FILE_EXTENSION).toPath().toString());
                }
            }
            for (String file : productDirectory.listAllFiles()) {
                if (file.endsWith(AlosPRIConstants.IMAGE_METADATA_EXTENSION)) {
                    metadata.addComponentMetadata(productDirectory.getFile(file));
                }
            }
            List<ImageMetadata> imageMetadataList = metadata.getImageMetadataList();
            if (imageMetadataList.size() == 0) {
                throw new IOException("No raster found");
            }

            ImageMetadata.InsertionPoint origin = metadata.getProductOrigin();
            float offsetX = (metadata.getMaxInsertPointX() - metadata.getMinInsertPointX()) / metadata.getStepSizeX();
            float offsetY = (metadata.getMaxInsertPointY() - metadata.getMinInsertPointY()) / metadata.getStepSizeY();

            int width = metadata.getRasterWidth();
            int height = metadata.getRasterHeight();

            this.product = new Product(this.metadata.getProductName(), AlosPRIConstants.PRODUCT_GENERIC_NAME, width, height);
            this.product.setStartTime(this.metadata.getProductStartTime());
            this.product.setEndTime(this.metadata.getProductEndTime());
            this.product.setDescription(this.metadata.getProductDescription());
            this.product.getMetadataRoot().addElement(this.metadata.getRootElement());
            this.product.setFileLocation(inputFile);
            this.product.setProductReader(this);
            if (metadata.hasInsertPoint()) {
                String crsCode = metadata.getCrsCode();
                try {
                    GeoCoding geoCoding = new CrsGeoCoding(CRS.decode(crsCode),
                                                           width, height,
                                                           origin.x, origin.y,
                                                           origin.stepX, origin.stepY);
                    product.setSceneGeoCoding(geoCoding);
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
            } else {
                initProductTiePointGeoCoding(this.product, offsetX, offsetY);
            }
            int levels;

            for (ImageMetadata imageMetadata : imageMetadataList) {

                product.getMetadataRoot().addElement(imageMetadata.getRootElement());
                File rasterFile = productDirectory.getFile(imageMetadata.getFileName().substring(0, imageMetadata.getFileName().indexOf(".")) + AlosPRIConstants.IMAGE_EXTENSION);

                this.tiffProduct.add(ProductIO.readProduct(rasterFile));
                this.tiffImageIndex++;
                Band band = this.tiffProduct.get(this.tiffImageIndex - 1).getBandAt(0);

                levels = band.getSourceImage().getModel().getLevelCount();
                final Dimension tileSize = JAIUtils.computePreferredTileSize(band.getRasterWidth(), band.getRasterHeight(), 1);

                Band targetBand = new Band(imageMetadata.getBandName(), band.getDataType(), band.getRasterWidth(), band.getRasterHeight());
                targetBand.setSpectralBandIndex(band.getSpectralBandIndex());
                targetBand.setSpectralWavelength(band.getSpectralWavelength());
                targetBand.setSpectralBandwidth(band.getSpectralBandwidth());
                targetBand.setSolarFlux(band.getSolarFlux());
                targetBand.setUnit(imageMetadata.getBandUnit());
                targetBand.setNoDataValue(imageMetadata.getNoDataValue());
                targetBand.setNoDataValueUsed(true);
                targetBand.setDescription(imageMetadata.getBandDescription());
                targetBand.setScalingFactor(imageMetadata.getGain());
                targetBand.setScalingOffset(band.getScalingOffset());
                initBandGeoCoding(imageMetadata, targetBand, width, height);

                MosaicMultiLevelSource bandSource =
                        new MosaicMultiLevelSource(band,
                                                   band.getRasterWidth(), band.getRasterHeight(),
                                                   tileSize.width, tileSize.height,
                                                   levels, targetBand.getGeoCoding() != null ?
                                Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                Product.findImageToModelTransform(product.getSceneGeoCoding()));
                targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
                this.product.addBand(targetBand);
                addMasks(product, imageMetadata);
            }
        }
        return this.product;
    }

    private void addMasks(Product target, ImageMetadata metadata) {
        ProductNodeGroup<Mask> maskGroup = target.getMaskGroup();
        if (!maskGroup.contains(AlosPRIConstants.NODATA)) {
            int noDataValue = metadata.getNoDataValue();
            maskGroup.add(Mask.BandMathsType.create(AlosPRIConstants.NODATA, AlosPRIConstants.NODATA,
                                                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                                                    String.valueOf(noDataValue), Color.BLACK, 0.5));
        }
        if (!maskGroup.contains(AlosPRIConstants.SATURATED)) {
            int saturatedValue = metadata.getSaturatedValue();
            maskGroup.add(Mask.BandMathsType.create(AlosPRIConstants.SATURATED, AlosPRIConstants.SATURATED,
                                                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                                                    String.valueOf(saturatedValue), Color.ORANGE, 0.5));
        }
    }

    private void initProductTiePointGeoCoding(Product product, float offsetX, float offsetY) {
        float[][] cornerLonsLats = metadata.getMaxCorners();
        int sceneWidth = product.getSceneRasterWidth();
        int sceneHeight = product.getSceneRasterHeight();
        TiePointGrid latGrid = createTiePointGrid(AlosPRIConstants.LAT_DS_NAME, 2, 2, offsetX, offsetY, sceneWidth, sceneHeight, cornerLonsLats[1]);
        product.addTiePointGrid(latGrid);
        TiePointGrid lonGrid = createTiePointGrid(AlosPRIConstants.LON_DS_NAME, 2, 2, offsetX, offsetY, sceneWidth, sceneHeight, cornerLonsLats[0]);
        product.addTiePointGrid(lonGrid);
        product.setSceneGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
    }

    private GeoCoding addTiePointGridGeo(int width, int height, float offsetX, float offsetY) {
        float[][] cornerLonsLats = this.metadata.getMaxCorners();
        TiePointGrid latGrid = createTiePointGrid(AlosPRIConstants.LAT_DS_NAME, 2, 2, offsetX, offsetY, width, height, cornerLonsLats[1]);
        TiePointGrid lonGrid = createTiePointGrid(AlosPRIConstants.LON_DS_NAME, 2, 2, offsetX, offsetY, width, height, cornerLonsLats[0]);
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    private void initBandGeoCoding(ImageMetadata imageMetadata, Band band, int sceneWidth, int sceneHeight) {
        int bandWidth = imageMetadata.getRasterWidth();
        int bandHeight = imageMetadata.getRasterHeight();
        GeoCoding geoCoding = null;
        ImageMetadata.InsertionPoint insertPoint = imageMetadata.getInsertPoint();
        String crsCode = imageMetadata.getCrsCode();
        try {
            CoordinateReferenceSystem crs = CRS.decode(crsCode);
            if (imageMetadata.hasInsertPoint()) {
                geoCoding = new CrsGeoCoding(crs,
                                             bandWidth, bandHeight,
                                             insertPoint.x, insertPoint.y,
                                             insertPoint.stepX, insertPoint.stepY, 0.0, 0.0);
            } else {
                if (sceneWidth != bandWidth) {
                    AffineTransform2D transform2D = new AffineTransform2D((float) sceneWidth / bandWidth, 0.0, 0.0, (float) sceneHeight / bandHeight, 0.0, 0.0);
                    geoCoding = addTiePointGridGeo(bandWidth, bandHeight, metadata.bandOffset().get(imageMetadata.getBandName())[0], metadata.bandOffset().get(imageMetadata.getBandName())[1]);
                    band.setImageToModelTransform(transform2D);
                }
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        if (band.getGeoCoding() == null) {
            band.setGeoCoding(geoCoding);
        }
    }

    /**
     * Returns a File object from the input of the reader.
     *
     * @param input the input object
     * @return Either a new instance of File, if the input represents the file name, or the casted input File.
     */
    protected File getFileInput(Object input) {
        if (input instanceof String) {
            return new File((String) input);
        } else if (input instanceof File) {
            return (File) input;
        }
        return null;
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

    @Override
    public void readTiePointGridRasterData(TiePointGrid tpg, int destOffsetX, int destOffsetY,
                                           int destWidth, int destHeight, ProductData destBuffer,
                                           ProgressMonitor pm) throws IOException {
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
        if (this.metadata != null && this.metadata.getImageDirectoryPath() != null) {
            File imageDir = new File(this.metadata.getImageDirectoryPath());
            if (imageDir.exists()) {
                deleteDirectory(imageDir);
            }
        }
        super.close();
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

}
