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
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.s2tbx.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.s2tbx.dataio.spot.dimap.SpotViewMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.TreeNode;
import org.geotools.coverage.grid.io.imageio.geotiff.TiePoint;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
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

    private ImageInputStream imageInputStream;
    private SpotViewMetadata metadata;
    private SpotDimapMetadata imageMetadata;
    private VirtualDirEx zipDir;
    private final Object sharedLock;
    private final Path colorPaletteFilePath;

    static {
        XmlMetadataParserFactory.registerParser(SpotDimapMetadata.class, new XmlMetadataParser<SpotDimapMetadata>(SpotDimapMetadata.class));
        XmlMetadataParserFactory.registerParser(SpotViewMetadata.class, new XmlMetadataParser<SpotViewMetadata>(SpotViewMetadata.class));
    }

    protected SpotViewProductReader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn);

        this.colorPaletteFilePath = colorPaletteFilePath;
        this.sharedLock = new Object();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        logger.info("Reading product metadata");
        //zipDir = ((BaseProductReaderPlugIn)getReaderPlugIn()).getInput(getInput());
        Path inputPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
        this.zipDir = VirtualDirEx.build(inputPath);

        File metadataFile = zipDir.getFile(SpotConstants.SPOTVIEW_METADATA_FILE);
        File imageMetadataFile = zipDir.getFile(SpotConstants.SPOTSCENE_METADATA_FILE);
        if (metadataFile != null) {
            metadata = XmlMetadata.create(SpotViewMetadata.class, metadataFile);
        }
        if (imageMetadataFile != null) {
            imageMetadata = XmlMetadata.create(SpotDimapMetadata.class, imageMetadataFile);
        }
        Product product = null;
        if (metadata != null) {
            String productName = metadata.getProductName().replace(" ", "_").replace("/", "_");
            if (imageMetadata != null && imageMetadata.getProductName() != null) {
                productName = imageMetadata.getProductName();
            }
            product = new Product(productName,
                                  SpotConstants.SPOTVIEW_FORMAT_NAMES[0],
                                  metadata.getRasterWidth(),
                                  metadata.getRasterHeight());
            product.setProductReader(this);
            //product.setFileLocation(metadataFile);
            //product.setFileLocation(new File(zipDir.getBasePath()));
            product.setFileLocation(inputPath.toFile());
            product.getMetadataRoot().addElement(metadata.getRootElement());

            logger.info("Trying to attach tiepoint geocoding");
            initTiePointGeoCoding(product);
            if (product.getSceneGeoCoding() == null) {
                logger.info("Trying to attach geocoding");
                initGeoCoding(product);
            }
            //initGeoCoding(product, zipDir.getFile("geolayer.bil"), metadata.getGeolayerJavaByteOrder());
            logger.info("Reading product bands");
            initBands(product);

            product.setPreferredTileSize(ImageManager.getPreferredTileSize(product));

            initialiseInputStream(zipDir.getFile(SpotConstants.SPOTVIEW_RASTER_FILENAME), metadata.getRasterJavaByteOrder());

            product.setModified(false);
        }
        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        final int sourceMaxY = sourceOffsetY + sourceHeight - 1;
        Product product = destBand.getProduct();
        final int elemSize = destBuffer.getElemSize();

        final int bandIndex = product.getBandIndex(destBand.getName());

        final long lineSizeInBytes = (long)metadata.getRasterWidth() * (long)metadata.getRasterPixelSize();
        int numBands = product.getNumBands();

        pm.beginTask("Reading band '" + destBand.getName() + "'...", sourceMaxY - sourceOffsetY);
        try {
            int destPos = 0;
            for (int sourceY = sourceOffsetY; sourceY <= sourceMaxY; sourceY += sourceStepY) {
                if (pm.isCanceled()) {
                    break;
                }
                synchronized (sharedLock) {
                    long lineStartPos = sourceY * numBands * lineSizeInBytes + bandIndex * lineSizeInBytes;
                    imageInputStream.seek(lineStartPos + elemSize * sourceOffsetX);
                    destBuffer.readFrom(destPos, destWidth, imageInputStream);
                    destPos += destWidth;
                }
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    private void initialiseInputStream(File inputFile, ByteOrder rasterByteOrder) {
        try {
            imageInputStream = ImageIO.createImageInputStream(inputFile);
            imageInputStream.setByteOrder(rasterByteOrder);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    private void initTiePointGeoCoding(Product product) {
        TiePoint[] tiePoints = imageMetadata.getTiePoints();
        if (tiePoints != null && tiePoints.length == 4) {
            float[] latPoints = new float[tiePoints.length];
            float[] lonPoints = new float[tiePoints.length];
            for (int i = 0; i < tiePoints.length; i++) {
                latPoints[(i != 2 ? (i != 3 ? i : 2) : 3)] = (float) tiePoints[i].getValueAt(4);
                lonPoints[(i != 2 ? (i != 3 ? i : 2) : 3)] = (float) tiePoints[i].getValueAt(3);
            }
            TiePointGrid latGrid = createTiePointGrid("latitude", 2, 2, 0, 0, metadata.getRasterWidth(), metadata.getRasterHeight(), latPoints);
            product.addTiePointGrid(latGrid);
            TiePointGrid lonGrid = createTiePointGrid("longitude", 2, 2, 0, 0, metadata.getRasterWidth(), metadata.getRasterHeight(), lonPoints);
            product.addTiePointGrid(lonGrid);
            GeoCoding geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
            product.setSceneGeoCoding(geoCoding);
        }
    }

    private void initGeoCoding(Product product) {
        try {
            String projectionCode = metadata.getProjectionCode();
            if (projectionCode != null && projectionCode.startsWith("epsg")) {
                CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", null);
                CoordinateReferenceSystem crs = factory.createProjectedCRS(projectionCode);
                if (crs != null) {
                    AffineTransform transformation = new AffineTransform();
                    transformation.translate(metadata.getRasterGeoRefY(), metadata.getRasterGeoRefX());
                    transformation.scale(metadata.getRasterGeoRefSizeX(), -metadata.getRasterGeoRefSizeY());
                    transformation.rotate(-Math.toRadians(imageMetadata.getOrientation()));
                    // Why do we need to do this??? Because it seems that the coordinates given were somehow
                    // offset during processing
                    transformation.translate(-product.getSceneRasterHeight(), -product.getSceneRasterWidth());
                    Rectangle rectangle = new Rectangle(metadata.getRasterWidth(), metadata.getRasterHeight());
                    CrsGeoCoding geoCoding = new CrsGeoCoding(crs, rectangle, transformation);
                    product.setSceneGeoCoding(geoCoding);
                }
            }
        } catch (FactoryException e) {
            e.printStackTrace();
        } catch (TransformException e) {
            e.printStackTrace();
        }
    }

    private void initBands(Product product) {
        final int dataType = metadata.getRasterDataType();

        final String[] bandNames = metadata.getBandNames();
        for (int i = 0; i < bandNames.length; i++) {
            ColorPaletteBand band = new ColorPaletteBand(bandNames[i], dataType, product.getSceneRasterWidth(), product.getSceneRasterHeight(), this.colorPaletteFilePath);

            band.setSpectralWavelength(imageMetadata.getWavelength(i));
            band.setSpectralBandwidth(imageMetadata.getBandwidth(i));
            //band.setScalingOffset(imageMetadata.getScalingOffset(i));
            //band.setScalingFactor(imageMetadata.getScalingFactor(i));

            band.setNoDataValueUsed(true);
            band.setNoDataValue(imageMetadata.getNoDataValue());

            product.addBand(band);
        }
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (zipDir.isCompressed()) {
            return super.getProductComponents();
        } else {
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

    private void addProductComponentIfNotPresent(String componentId, TreeNode<File> currentComponents) {
        try {
            File componentFile = zipDir.getFile(componentId);
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

    @Override
    public void close() throws IOException {
        if (imageInputStream != null) {
            imageInputStream.close();
        }
        super.close();
    }
}
