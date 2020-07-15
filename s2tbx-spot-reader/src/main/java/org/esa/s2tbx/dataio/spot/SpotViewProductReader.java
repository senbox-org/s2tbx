/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.spot;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.s2tbx.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.s2tbx.dataio.spot.dimap.SpotViewMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.core.util.jai.JAIUtils;
import org.geotools.coverage.grid.io.imageio.geotiff.TiePoint;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.xml.sax.SAXException;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * This product reader is intended for reading SPOTView files
 * from compressed archive files or from file system.
 * This may be not the official format from Spot Image, but is used by DLR and MERIA.
 * There seems to be a precision problem with attaching a CrsGeoCoding using EPSG:3035.
 *
 * @author Cosmin Cara
 * modified 20190516 for VFS compatibility by Oana H.
 */
public class SpotViewProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(SpotViewProductReader.class.getName());

    static {
        XmlMetadataParserFactory.registerParser(SpotDimapMetadata.class, new XmlMetadataParser<SpotDimapMetadata>(SpotDimapMetadata.class));
        XmlMetadataParserFactory.registerParser(SpotViewMetadata.class, new XmlMetadataParser<SpotViewMetadata>(SpotViewMetadata.class));
    }

    private final Path colorPaletteFilePath;

    private VirtualDirEx productDirectory;
    private SpotViewImageReader spotViewImageReader;

    public SpotViewProductReader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn);

        this.colorPaletteFilePath = colorPaletteFilePath;
    }

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.productDirectory = VirtualDirEx.build(productPath);

            SpotViewMetadata productMetadata = readProductMetadata(this.productDirectory);
            SpotDimapMetadata imageMetadata = readImageMetadata(this.productDirectory);

            String productName = productMetadata.getProductName().replace(" ", "_").replace("/", "_");
            if (imageMetadata != null && imageMetadata.getProductName() != null) {
                productName = imageMetadata.getProductName();
            }

            int defaultProductWidth = productMetadata.getRasterWidth();
            int defaultProductHeight = productMetadata.getRasterHeight();

            ProductSubsetDef subsetDef = getSubsetDef();
            Rectangle productBounds;
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productBounds = new Rectangle(0, 0, defaultProductWidth, defaultProductHeight);
            } else {
                GeoCoding productDefaultGeoCoding = buildTiePointGridGeoCoding(productMetadata, imageMetadata, null);
                if (productDefaultGeoCoding == null) {
                    productDefaultGeoCoding = buildCrsGeoCoding(defaultProductWidth, defaultProductHeight, productMetadata, imageMetadata);
                }
                productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, false);
            }
            if (productBounds.isEmpty()) {
                throw new IllegalStateException("Empty product bounds.");
            }

            Product product = new Product(productName, SpotConstants.SPOTVIEW_FORMAT_NAMES[0], productBounds.width, productBounds.height, this);
            product.setFileLocation(productPath.toFile());
            if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                product.getMetadataRoot().addElement(productMetadata.getRootElement());
            }

            Dimension defaultJAIReadTileSize = JAI.getDefaultTileSize();
            Dimension preferredTileSize = defaultJAIReadTileSize; // new Dimension(defaultJAIReadTileSize.width * 2, defaultJAIReadTileSize.height *2); // multiple mosaic tile size

            product.setPreferredTileSize(preferredTileSize);

            TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(productMetadata, imageMetadata, subsetDef);
            if (productGeoCoding == null) {
                CrsGeoCoding crsGeoCoding = buildCrsGeoCoding(product.getSceneRasterWidth(), product.getSceneRasterHeight(), productMetadata, imageMetadata);
                if (crsGeoCoding != null) {
                    product.setSceneGeoCoding(crsGeoCoding);
                }
            } else {
                product.addTiePointGrid(productGeoCoding.getLatGrid());
                product.addTiePointGrid(productGeoCoding.getLonGrid());
                product.setSceneGeoCoding(productGeoCoding);
            }

            File inputFile = this.productDirectory.getFile(SpotConstants.SPOTVIEW_RASTER_FILENAME);
            this.spotViewImageReader = new SpotViewImageReader(inputFile, productMetadata.getRasterJavaByteOrder(), defaultProductWidth, productMetadata.getRasterPixelSize());

            // add bands
            double noDataValue = imageMetadata.getNoDataValue();
            int bandDataType = productMetadata.getRasterDataType();
            int dataBufferType = ImageManager.getDataBufferType(bandDataType);
            String[] bandNames = productMetadata.getBandNames();
            GeoCoding bandGeoCoding = product.getSceneGeoCoding();
            for (int bandIndex = 0; bandIndex < bandNames.length; bandIndex++) {
                if (subsetDef == null || subsetDef.isNodeAccepted(bandNames[bandIndex])) {
                    ColorPaletteBand band = new ColorPaletteBand(bandNames[bandIndex], bandDataType, product.getSceneRasterWidth(), product.getSceneRasterHeight(), this.colorPaletteFilePath);
                    if (bandGeoCoding != null) {
                        band.setGeoCoding(bandGeoCoding);
                    }
                    band.setSpectralWavelength(imageMetadata.getWavelength(bandIndex));
                    band.setSpectralBandwidth(imageMetadata.getBandwidth(bandIndex));
                    band.setNoDataValueUsed(true);
                    band.setNoDataValue(noDataValue);

                    SpotViewMultiLevelSource multiLevelSource = new SpotViewMultiLevelSource(this.spotViewImageReader, dataBufferType, productBounds, preferredTileSize,
                                                                                             bandIndex, bandNames.length, bandGeoCoding, noDataValue, defaultJAIReadTileSize);
                    // compute the tile size of the image layout object based on the tile size from the tileOpImage used to read the data
                    ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
                    band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource, imageLayout));

                    product.addBand(band);
                }
            }

            success = true;

            return product;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        } finally {
            if (!success) {
                closeResources();
            }
        }
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm)
                                          throws IOException {

        Product product = destBand.getProduct();
        int bandIndex = product.getBandIndex(destBand.getName());
        int numBands = product.getNumBands();
        synchronized (this.spotViewImageReader) {
            this.spotViewImageReader.readBandRasterData(bandIndex, numBands, sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer);
        }
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isCompressed()) {
            return super.getProductComponents();
        } else {
            SpotViewMetadata metadata;
            SpotDimapMetadata imageMetadata;
            try {
                metadata = readMetadata(this.productDirectory, SpotConstants.SPOTVIEW_METADATA_FILE, SpotViewMetadata.class);
                imageMetadata = readMetadata(this.productDirectory, SpotConstants.SPOTSCENE_METADATA_FILE, SpotDimapMetadata.class);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            TreeNode<File> result = super.getProductComponents();
            addProductComponentIfNotPresent(metadata.getFileName(), result);
            addProductComponentIfNotPresent(metadata.getGeolayerFileName(), result);
            //addProductComponentIfNotPresent(metadata.getRasterFileName(), result);
            addProductComponentIfNotPresent(SpotConstants.SPOTSCENE_METADATA_FILE, result);//WARN: it should be imageMetadata.getFileName()
            for (String name : imageMetadata.getRasterFileNames())
                addProductComponentIfNotPresent(name, result);
            return result;
        }
    }

    private void closeResources() {
        try {
            if (this.spotViewImageReader != null) {
                this.spotViewImageReader.close();
                this.spotViewImageReader = null;
            }
        } finally {
            if (this.productDirectory != null) {
                this.productDirectory.close();
                this.productDirectory = null;
            }
        }
    }

    private void addProductComponentIfNotPresent(String componentId, TreeNode<File> currentComponents) {
        try {
            File componentFile = productDirectory.getFile(componentId);
            TreeNode<File> resultComponent = null;
            for (TreeNode node : currentComponents.getChildren()) {
                if (node.getId().toLowerCase().equals(componentId.toLowerCase())) {
                    //noinspection unchecked
                    resultComponent = node;
                    break;
                }
            }
            if (resultComponent == null) {
                resultComponent = new TreeNode<File>(componentId, componentFile);
                currentComponents.addChild(resultComponent);
            }
        } catch (IOException ex) {
            logger.warning(ex.getMessage());
        }
    }

    public static TiePointGeoCoding buildTiePointGridGeoCoding(SpotViewMetadata productMetadata, SpotDimapMetadata imageMetadata, ProductSubsetDef subsetDef) {
        TiePoint[] tiePoints = imageMetadata.getTiePoints();
        if (tiePoints != null && tiePoints.length == 4) {
            float[] latPoints = new float[tiePoints.length];
            float[] lonPoints = new float[tiePoints.length];
            for (int i = 0; i < tiePoints.length; i++) {
                latPoints[(i != 2 ? (i != 3 ? i : 2) : 3)] = (float) tiePoints[i].getValueAt(4);
                lonPoints[(i != 2 ? (i != 3 ? i : 2) : 3)] = (float) tiePoints[i].getValueAt(3);
            }
            TiePointGrid latGrid = buildTiePointGrid("latitude", 2, 2, 0, 0, productMetadata.getRasterWidth(), productMetadata.getRasterHeight(), latPoints, TiePointGrid.DISCONT_NONE);
            TiePointGrid lonGrid = buildTiePointGrid("longitude", 2, 2, 0, 0, productMetadata.getRasterWidth(), productMetadata.getRasterHeight(), lonPoints, TiePointGrid.DISCONT_AT_180);
            if (subsetDef != null && subsetDef.getRegion() != null) {
                lonGrid = TiePointGrid.createSubset(lonGrid, subsetDef);
                latGrid = TiePointGrid.createSubset(latGrid, subsetDef);
            }
            return new TiePointGeoCoding(latGrid, lonGrid);
        }
        return null;
    }

    public static CrsGeoCoding buildCrsGeoCoding(int productWidth, int productHeight, SpotViewMetadata productMetadata, SpotDimapMetadata imageMetadata)
            throws FactoryException, TransformException {

        String projectionCode = productMetadata.getProjectionCode();
        if (projectionCode != null && projectionCode.startsWith("epsg")) {
            CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", null);
            CoordinateReferenceSystem crs = factory.createProjectedCRS(projectionCode);
            if (crs != null) {
                AffineTransform transformation = new AffineTransform();
                transformation.translate(productMetadata.getRasterGeoRefY(), productMetadata.getRasterGeoRefX());
                transformation.scale(productMetadata.getRasterGeoRefSizeX(), -productMetadata.getRasterGeoRefSizeY());
                transformation.rotate(-Math.toRadians(imageMetadata.getOrientation()));
                // Why do we need to do this??? Because it seems that the coordinates given were somehow
                // offset during processing
                transformation.translate(-productHeight, -productWidth);
                Rectangle rectangle = new Rectangle(productMetadata.getRasterWidth(), productMetadata.getRasterHeight());
                return new CrsGeoCoding(crs, rectangle, transformation);
            }
        }
        return null;
    }

    public static SpotViewMetadata readProductMetadata(VirtualDirEx productDirectory) throws IOException, InstantiationException, ParserConfigurationException, SAXException {
        return readMetadata(productDirectory, SpotConstants.SPOTVIEW_METADATA_FILE, SpotViewMetadata.class);
    }

    public static SpotDimapMetadata readImageMetadata(VirtualDirEx productDirectory) throws IOException, InstantiationException, ParserConfigurationException, SAXException {
        return readMetadata(productDirectory, SpotConstants.SPOTSCENE_METADATA_FILE, SpotDimapMetadata.class);
    }

    private static <MetadataType extends GenericXmlMetadata> MetadataType readMetadata(VirtualDirEx productDirectory, String metadataRelativeFilePath, Class<MetadataType> metadataClass)
            throws IOException, InstantiationException, ParserConfigurationException, SAXException {

        try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(metadataRelativeFilePath)) {
            MetadataType metaDataItem = (MetadataType) XmlMetadataParserFactory.getParser(metadataClass).parse(filePathInputStream);
            Path filePath = filePathInputStream.getPath();
            metaDataItem.setPath(filePath);
            metaDataItem.setFileName(filePath.getFileName().toString());
            String metadataProfile = metaDataItem.getMetadataProfile();
            if (metadataProfile != null) {
                metaDataItem.setName(metadataProfile);
            }
            return metaDataItem;
        }
    }
}
