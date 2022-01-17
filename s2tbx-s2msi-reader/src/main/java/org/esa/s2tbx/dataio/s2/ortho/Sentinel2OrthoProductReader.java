/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2.ortho;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;

import org.esa.s2tbx.dataio.s2.CAMSReader;
import org.esa.s2tbx.dataio.s2.ColorIterator;
import org.esa.s2tbx.dataio.s2.ECMWFTReader;
import org.esa.s2tbx.dataio.s2.S2BandAnglesGrid;
import org.esa.s2tbx.dataio.s2.S2BandAnglesGridByDetector;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Constant;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SceneDescription;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.gml.EopPolygon;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo148;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadata;
import org.esa.s2tbx.dataio.s2.tiles.MosaicMatrixCellCallback;
import org.esa.s2tbx.dataio.s2.tiles.TileIndexBandMatrixCell;
import org.esa.s2tbx.dataio.s2.tiles.TileIndexMultiLevelSource;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Placemark;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.esa.snap.core.datamodel.VirtualBand;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.core.image.SourceImageScaler;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixMultiLevelSource;
import org.esa.snap.jp2.reader.internal.JP2MatrixBandMultiLevelSource;
import org.esa.snap.lib.openjpeg.utils.StackTraceUtils;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.awt.image.DataBuffer.TYPE_FLOAT;
import static org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadataProc.makeTileInformation;
import static org.esa.snap.utils.DateHelper.parseDate;

/**
 * <p>
 * Base class for Sentinel-2 readers of orthorectified products
 * </p>
 * <p>
 * To read single tiles, select any tile image file (IMG_*.jp2) within a product
 * package. The reader will then collect other band images for the selected tile
 * and will also try to read the metadata file (MTD_*.xml).
 * </p>
 * <p>
 * To read an entire scene, select the metadata file (MTD_*.xml) within a
 * product package. The reader will then collect other tile/band images and
 * create a mosaic on the fly.
 * </p>
 *
 * @author Norman Fomferra
 * @author Nicolas Ducoin modified 20200113 to support the advanced dialog for
 *         readers by Denisa Stefanescu
 */
public abstract class Sentinel2OrthoProductReader extends Sentinel2ProductReader implements S2AnglesGeometry {

    public static final String VIEW_ZENITH_PREFIX = "view_zenith";
    public static final String VIEW_AZIMUTH_PREFIX = "view_azimuth";
    public static final String SUN_ZENITH_PREFIX = "sun_zenith";
    public static final String SUN_AZIMUTH_PREFIX = "sun_azimuth";

    protected final String epsgCode;

    private S2OrthoMetadata orthoMetadataHeader;

    protected Sentinel2OrthoProductReader(ProductReaderPlugIn readerPlugIn, String epsgCode) {
        super(readerPlugIn);

        this.epsgCode = epsgCode;
    }

    protected abstract int getMaskLevel();

    protected abstract String getReaderCacheDir();

    protected S2SpatialResolution getProductResolution(INamingConvention namingConvention) {
        return namingConvention.getResolution();
    }

    @Override
    protected final void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight,
            int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth,
            int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

    @Override
    public final void readTiePointGridRasterData(TiePointGrid tpg, int destOffsetX, int destOffsetY, int destWidth,
            int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

    @Override
    protected final Product readProduct(String defaultProductName, boolean isGranule, S2Metadata metadataHeader,
            INamingConvention namingConvention, ProductSubsetDef subsetDef) throws Exception {
        this.orthoMetadataHeader = (S2OrthoMetadata) metadataHeader;
        VirtualPath rootMetadataPath = this.orthoMetadataHeader.getPath();
        S2SpatialResolution productResolution = getProductResolution(namingConvention);

        long startTime = System.currentTimeMillis();

        S2OrthoSceneLayout sceneDescription = S2OrthoSceneLayout.create(this.orthoMetadataHeader);

        if (logger.isLoggable(Level.FINE)) {
            double elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.d;
            logger.log(Level.FINE,
                    "Finish reading the scene description, elapsed time: " + elapsedTimeInSeconds + " seconds.");
        }

        // Check sceneDescription because a NullPointerException can be launched:
        // An error can be reproduced with a L2A product with 2 tiles in zone UTM30 and
        // 2 other tiles in zone UTM31.
        // The process is stopped and the tiles in zone UTM 31 are empty
        // The execution does not finish when updating tileLayout at the beginning of
        // this method
        // because the tile layout is obtained with the tile in zone UTM 30.
        // But the sceneLayout is computed with the tiles that are in the zone UTM 31 if
        // we select this PlugIn
        if (sceneDescription.getTileIds().size() == 0) {
            throw new IOException(String.format("No valid tiles associated to product [%s]",
                    rootMetadataPath.getFileName().toString()));
        }
        Dimension defaultProductSize = sceneDescription.getSceneDimension(productResolution);
        if (defaultProductSize == null) {
            throw new IOException(
                    String.format("Unable to retrieve the product associated to granule metadata file [%s]",
                            rootMetadataPath.getFileName().toString()));
        }

        VirtualPath productPath = getProductDir(rootMetadataPath);
        initCacheDir(productPath);

        S2Metadata.ProductCharacteristics productCharacteristics = this.orthoMetadataHeader.getProductCharacteristics();
        String productLevel = productCharacteristics.getProcessingLevel();
        String productType = "S2_MSI_" + productCharacteristics.getProcessingLevel();
        CoordinateReferenceSystem mapCRS = CRS.decode(this.epsgCode);
        GeoCoding productDefaultGeoCoding = null;
        Rectangle productBounds;
        if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
            productBounds = new Rectangle(0, 0, defaultProductSize.width, defaultProductSize.height);
        } else {
            productDefaultGeoCoding = buildGeoCoding(sceneDescription, mapCRS, productResolution.resolution,
                    productResolution.resolution, defaultProductSize, null);
            boolean isMultiSize = isMultiResolution();
            productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding,
                    defaultProductSize.width, defaultProductSize.height, isMultiSize);
        }
        if (productBounds.isEmpty()) {
            throw new IllegalStateException("Empty product bounds.");
        }

        Product product = new Product(defaultProductName, productType, productBounds.width, productBounds.height, this);

        if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
            for (MetadataElement metadataElement : this.orthoMetadataHeader.getMetadataElements()) {
                product.getMetadataRoot().addElement(metadataElement);
            }
        }
        GeoCoding productGeoCoding = buildGeoCoding(sceneDescription, mapCRS, productResolution.resolution,
                productResolution.resolution, defaultProductSize, productBounds);
        product.setSceneGeoCoding(productGeoCoding);

        Dimension defaultJAIReadTileSize = JAI.getDefaultTileSize();
        product.setPreferredTileSize(defaultJAIReadTileSize);

        product.setAutoGrouping(buildAutoGroupingPattern());
        product.setStartTime(parseDate(productCharacteristics.getProductStartTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        product.setEndTime(parseDate(productCharacteristics.getProductStopTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        List<S2Metadata.Tile> tileList = orthoMetadataHeader.getTileList();

        List<BandInfo> bandInfoList = orthoMetadataHeader.computeBandInfoByKey(tileList);
        if (!bandInfoList.isEmpty()) {
            int productMaximumResolutionCount = addBands(product, bandInfoList, sceneDescription, productResolution,
                    productDefaultGeoCoding, mapCRS, subsetDef, defaultJAIReadTileSize);
            product.setNumResolutionsMax(productMaximumResolutionCount);

            // In MultiResolution mode, all bands are kept at their native resolution
            if (!isMultiResolution()) {
                scaleBands(product, bandInfoList, productResolution);
            }
            S2Metadata.Tile tile = tileList.get(0);
            if (tile.getMaskFilenames() != null && (!tile.getMaskFilenames()[0].getPath().getFullPathString().endsWith(".gml"))) {
                    addRasterMasks(tileList, product, mapCRS, bandInfoList, sceneDescription, productResolution,
                            productDefaultGeoCoding, subsetDef, defaultJAIReadTileSize);
                    addIndexMasks(product, mapCRS, bandInfoList, sceneDescription, productResolution,
                            productDefaultGeoCoding, subsetDef);
            }else
            {
                addVectorMasks(product, tileList, bandInfoList, subsetDef);

                addIndexMasks(product, mapCRS, bandInfoList, sceneDescription, productResolution,
                        productDefaultGeoCoding, subsetDef);
            }
        }

        // add TileIndex if there are more than 1 tile
        if (sceneDescription.getOrderedTileIds().size() > 1 && !bandInfoList.isEmpty()) {
            List<S2SpatialResolution> resolutions = new ArrayList<>();
            // look for the resolutions used in bandInfoList for generating the tile index
            // only for them
            for (BandInfo bandInfo : bandInfoList) {
                if (!resolutions.contains(bandInfo.getBandInformation().getResolution())) {
                    resolutions.add(bandInfo.getBandInformation().getResolution());
                }
            }
            if (resolutions.size() > 0 && tileList.size() > 0) {
                addTileIndexes(product, mapCRS, resolutions, tileList, sceneDescription, productResolution,
                        productDefaultGeoCoding, subsetDef);
            }
        }
        if (!"Brief".equalsIgnoreCase(productCharacteristics.getMetaDataLevel())) {
            HashMap<String, S2BandAnglesGrid[]> anglesGridsMap = new HashMap<>();
            for (S2Metadata.Tile tile : tileList) {
                S2BandAnglesGrid[] bandAnglesGrids = createS2OrthoAnglesGrids(orthoMetadataHeader, tile.getId());
                if (bandAnglesGrids != null) {
                    anglesGridsMap.put(tile.getId(), bandAnglesGrids);
                }
            }
            if ((!productLevel.matches(S2Constant.LevelL2H) && !productLevel.matches(S2Constant.LevelL2F))
                    || anglesGridsMap.size() > 0) {
                addAnglesBands(mapCRS, defaultProductSize, product, sceneDescription, anglesGridsMap,
                        productDefaultGeoCoding, subsetDef);
            } else {
                // todo: add reader L2HF for the angle tif data
            }
        }

        for (S2Metadata.Tile tile : tileList)
            addGRIBBand(product, tile, sceneDescription, mapCRS, namingConvention);

        return product;
    }

    private void addGRIBBand(Product product, S2Metadata.Tile tile, S2OrthoSceneLayout sceneDescription,
            CoordinateReferenceSystem mapCRS, INamingConvention namingConvention)
            throws IOException, NoSuchAuthorityCodeException, FactoryException {
        VirtualPath tileFolder = namingConvention.findGranuleFolderFromTileId(tile.getId());
        S2Metadata.ProductCharacteristics characteristicsAUXDATA = new S2Metadata.ProductCharacteristics();
        VirtualPath folderAUXDATA = tileFolder.resolve("AUX_DATA");
        characteristicsAUXDATA.setDatatakeSensingStartTime("Unknown");
        if (folderAUXDATA.existsAndHasChildren()) {
            characteristicsAUXDATA.setSpacecraft("Sentinel-2");
            characteristicsAUXDATA.setProcessingLevel("Level-1C");
            characteristicsAUXDATA.setMetaDataLevel("Standard");
            VirtualPath[] gribFiles = folderAUXDATA.listPaths();
            String tileId = "";//if there is one tile, the tileId are not used
            if(orthoMetadataHeader.getTileList().size()>1)
                tileId = tile.getId();
            for (VirtualPath gribFile : gribFiles) {
                if (S2OrthoUtils.enableECMWFTData() && gribFile.getFileName().toString().contains("AUX_ECMWFT")) {
                    ECMWFTReader readerPlugin = new ECMWFTReader(gribFile.getFilePath().getPath(), getCacheDir(),tileId);
                    List<TiePointGrid> ecmwfGrids = readerPlugin.getECMWFGrids();
                    for (TiePointGrid tiePointGrid : ecmwfGrids) {
                        product.addTiePointGrid(tiePointGrid);
                    }
                } else if (S2OrthoUtils.enableCAMSData() && gribFile.getFileName().toString().contains("AUX_CAMSFO")) {
                    CAMSReader readerPlugin = new CAMSReader(gribFile.getFilePath().getPath(), getCacheDir(),tileId);
                    List<TiePointGrid> camsGrids = readerPlugin.getCAMSGrids();
                    for (TiePointGrid tiePointGrid : camsGrids) {
                        product.addTiePointGrid(tiePointGrid);
                    }
                }

            }
        }

    }

    private void addAnglesBands(CoordinateReferenceSystem mapCRS, Dimension defaultProductSize, Product product,
            S2OrthoSceneLayout sceneDescription, HashMap<String, S2BandAnglesGrid[]> bandAnglesGridsMap,
            GeoCoding productDefaultGeoCoding, ProductSubsetDef subsetDef) throws IOException {

        // the upper-left corner
        Point.Float masterOrigin = new Point.Float(Float.MAX_VALUE, -Float.MAX_VALUE);
        Dimension anglesTileSize = new Dimension(0, 0);
        // angle band resolution
        Point.Float resolution = new Point.Float(0.0f, 0.0f);
        // array of all angles in a tile
        Set<AngleID> angleIds = new HashSet<AngleID>();
        // search upper-left coordinates
        for (String tileId : sceneDescription.getOrderedTileIds()) {
            S2BandAnglesGrid[] bandAnglesGrid = bandAnglesGridsMap.get(tileId);
            anglesTileSize.width = bandAnglesGrid[0].getWidth();
            anglesTileSize.height = bandAnglesGrid[0].getHeight();
            resolution.x = bandAnglesGrid[0].getResolutionX();
            resolution.y = bandAnglesGrid[0].getResolutionY();
            if (masterOrigin.x > bandAnglesGrid[0].originX) {
                masterOrigin.x = bandAnglesGrid[0].originX;
            }
            if (masterOrigin.y < bandAnglesGrid[0].originY) {
                masterOrigin.y = bandAnglesGrid[0].originY;
            }
            for (S2BandAnglesGrid grid : bandAnglesGrid) {
                if (grid.getResolutionX() == resolution.x && grid.getResolutionY() == resolution.y
                        && grid.getWidth() == anglesTileSize.width && grid.getHeight() == anglesTileSize.height) {
                    angleIds.add(new AngleID(grid.getPrefix(), grid.getBand())); // if it is repeated, the angleID is
                                                                                 // not added because it is a HashSet
                }
            }
        }

        if (masterOrigin.x == Float.MAX_VALUE || masterOrigin.y == -Float.MAX_VALUE || resolution.x == 0
                || resolution.y == 0 || anglesTileSize.width == 0 || anglesTileSize.height == 0) {
            logger.warning("Invalid tile data for computing the angles mosaic");
            return;
        }

        // sort the angles
        List<AngleID> sortedList = new ArrayList(angleIds);
        Collections.sort(sortedList);

        for (AngleID angleID : sortedList) {
            String bandName = getAngleBandName(angleID);
            if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                Dimension defaultProductBandSize = null;
                Rectangle bandBounds = null;
                PlanarImage bandSourceImage = null;
                List<PlanarImage> tileImages = buildMosaicTileImages(angleID, sceneDescription, anglesTileSize,
                        bandAnglesGridsMap, masterOrigin);
                if (!tileImages.isEmpty()) {
                    ImageLayout imageLayout = new ImageLayout();
                    imageLayout.setMinX(0);
                    imageLayout.setMinY(0);
                    imageLayout.setTileWidth(S2Config.DEFAULT_JAI_TILE_SIZE);
                    imageLayout.setTileHeight(S2Config.DEFAULT_JAI_TILE_SIZE);
                    imageLayout.setTileGridXOffset(0);
                    imageLayout.setTileGridYOffset(0);

                    RenderingHints hints = new RenderingHints(JAI.KEY_TILE_CACHE,
                            JAI.getDefaultInstance().getTileCache());
                    hints.put(JAI.KEY_IMAGE_LAYOUT, imageLayout);

                    RenderedImage[] sources = tileImages.toArray(new RenderedImage[tileImages.size()]);
                    double[][] sourceThreshold = new double[][] { { -1.0 } };
                    double[] backgroundValues = new double[] { S2Config.FILL_CODE_MOSAIC_ANGLES };
                    RenderedOp mosaicOp = MosaicDescriptor.create(sources, MosaicDescriptor.MOSAIC_TYPE_OVERLAY, null,
                            null, sourceThreshold, backgroundValues, hints);

                    // Crop Mosaic if there are lines outside the scene
                    bandSourceImage = cropBordersOutsideScene(mosaicOp, resolution.x, resolution.y, sceneDescription);

                    int defaultBandWidth = bandSourceImage.getWidth();
                    int defaultBandHeight = bandSourceImage.getHeight();
                    Dimension defaultBandSize = new Dimension(defaultBandWidth, defaultBandHeight);
                    if (subsetDef != null && subsetDef.getSubsetRegion() != null) {
                        defaultProductBandSize = defaultProductSize;
                        GeoCoding bandDefaultGeoCoding = buildGeoCoding(sceneDescription, mapCRS, resolution.x,
                                resolution.y, defaultBandSize, null);
                        bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding,
                                bandDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height,
                                defaultBandWidth, defaultBandHeight, isMultiResolution());
                    } else {
                        defaultProductBandSize = new Dimension(defaultBandWidth, defaultBandHeight);
                        bandBounds = new Rectangle(defaultBandWidth, defaultBandHeight);
                    }
                    if (!bandBounds.isEmpty()) {
                        // there is an intersection
                        if (bandBounds.x > 0 || bandBounds.y > 0 || bandBounds.width != defaultBandWidth
                                || bandBounds.height != defaultBandHeight) {
                            Raster subsetSourceData = bandSourceImage.getData();
                            WritableRaster subsetRaster = subsetSourceData
                                    .createCompatibleWritableRaster(bandBounds.width, bandBounds.height);
                            for (int x = 0; x < bandBounds.width; x++) {
                                for (int y = 0; y < bandBounds.height; y++) {
                                    float value = subsetSourceData.getSampleFloat(x + bandBounds.x, y + bandBounds.y,
                                            0);
                                    subsetRaster.setSample(x, y, 0, value);
                                }
                            }
                            ColorModel colorModel = bandSourceImage.getColorModel();
                            BufferedImage image = new BufferedImage(colorModel, subsetRaster,
                                    colorModel.isAlphaPremultiplied(), null);
                            bandSourceImage = PlanarImage.wrapRenderedImage(image);
                        }
                    }
                }
                if (bandSourceImage == null) {
                    logger.warning("No tile images for angles mosaic");
                    return;
                }
                Band band = new Band(bandName, ProductData.TYPE_FLOAT32, bandSourceImage.getWidth(),
                        bandSourceImage.getHeight());
                String description = "";
                if (angleID.prefix.startsWith(VIEW_ZENITH_PREFIX)) {
                    description = "Viewing incidence zenith angle";
                } else if (angleID.prefix.startsWith(VIEW_AZIMUTH_PREFIX)) {
                    description = "Viewing incidence azimuth angle";
                } else if (angleID.prefix.startsWith(SUN_ZENITH_PREFIX)) {
                    description = "Solar zenith angle";
                } else if (angleID.prefix.startsWith(SUN_AZIMUTH_PREFIX)) {
                    description = "Solar azimuth angle";
                }
                band.setDescription(description);
                // 20200716 - issue with degree sign in DIMAP, decision to use "deg" fo unit
                // instead of "°"
                // band.setUnit("°");
                band.setUnit("deg");
                band.setNoDataValue(Double.NaN);
                band.setNoDataValueUsed(true);

                GeoCoding geoCoding = buildGeoCoding(sceneDescription, mapCRS, resolution.x, resolution.y,
                        defaultProductBandSize, bandBounds);
                band.setGeoCoding(geoCoding);

                band.setImageToModelTransform(product.findImageToModelTransform(band.getGeoCoding()));

                // set source image mut be done after setGeocoding and setImageToModelTransform
                band.setSourceImage(bandSourceImage);

                product.addBand(band);
            }
        }
    }

    private int addBands(Product product, List<BandInfo> bandInfoList, S2OrthoSceneLayout sceneDescription,
            S2SpatialResolution productResolution, GeoCoding productDefaultGeoCoding, CoordinateReferenceSystem mapCRS,
            ProductSubsetDef subsetDef, Dimension defaultJAIReadTileSize) throws IOException {

        Dimension defaultProductSize = sceneDescription.getSceneDimension(productResolution);
        Collection<String> bandMatrixTileIds = sceneDescription.getTileIds(); // sceneDescription.getOrderedTileIds();
        S2Metadata.ProductCharacteristics productCharacteristics = this.orthoMetadataHeader
        .getProductCharacteristics();
        String productLevel = productCharacteristics.getProcessingLevel();
        double quantificationValue = productCharacteristics.getQuantificationValue();
        int productMaximumResolutionCount = 0;
        double mosaicOpBackgroundValue = S2Config.FILL_CODE_MOSAIC_BG;
        int bandIndexNumber = 0;
        double mosaicOpSourceThreshold = 1.0d;
        String[] offsets = null;
        if((productCharacteristics.getPsd()>147) && S2OrthoUtils.addNegativeOffset()) {
            offsets =productCharacteristics.getOffsetList();
            if(offsets==null)
                logger.warning("The metadata offset values are not accessible.");
        }
        for (int i = 0; i < bandInfoList.size(); i++) {
            BandInfo bandInfo = bandInfoList.get(i);
            Dimension defaultBandSize = sceneDescription
                    .getSceneDimension(bandInfo.getBandInformation().getResolution());
            if (subsetDef == null || subsetDef.isNodeAccepted(bandInfo.getBandName())) {
                // Get the band native resolution
                S2SpatialResolution bandNativeResolution = bandInfo.getBandInformation().getResolution();
                double pixelSize;
                if (isMultiResolution()) {
                    pixelSize = (double) bandNativeResolution.resolution;
                } else {
                    pixelSize = (double) productResolution.resolution;
                }
                MosaicMatrix mosaicMatrix = buildBandMatrix(bandMatrixTileIds, sceneDescription, bandInfo);
                int defaultBandWidth = mosaicMatrix.computeTotalWidth();
                int defaultBandHeight = mosaicMatrix.computeTotalHeight();
                if (defaultBandSize.width != defaultBandWidth) {
                    throw new IllegalStateException("Invalid band width: nativeBandWidth=" + defaultBandSize.width
                            + ", defaultBandWidth=" + defaultBandWidth);
                }
                if (defaultBandSize.height != defaultBandHeight) {
                    throw new IllegalStateException("Invalid band height: nativeBandHeight=" + defaultBandSize.height
                            + ", defaultBandHeight=" + defaultBandHeight);
                }


                boolean geotiffOption = false;
                if (productLevel.matches(S2Constant.LevelL2H) || productLevel.matches(S2Constant.LevelL2F))
                    geotiffOption = true;
                int dataBufferType = computeMatrixCellsDataBufferType(mosaicMatrix);
                int resolutionCount = computeMatrixCellsResolutionCount(mosaicMatrix, geotiffOption);
                productMaximumResolutionCount = Math.max(productMaximumResolutionCount, resolutionCount);

                Rectangle bandBounds;
                if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                    bandBounds = new Rectangle(defaultBandWidth, defaultBandHeight);
                } else {
                    GeoCoding bandDefaultGeoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize,
                            defaultBandSize, null);
                    bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding,
                            bandDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, defaultBandWidth,
                            defaultBandHeight, isMultiResolution());
                }
                if (!bandBounds.isEmpty()) {
                    Band band = buildBand(bandInfo, bandBounds.width, bandBounds.height, dataBufferType);
                    band.setDescription(bandInfo.getBandInformation().getDescription());
                    band.setUnit(bandInfo.getBandInformation().getUnit());

                    GeoCoding geoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize,
                            defaultBandSize, bandBounds);
                    band.setGeoCoding(geoCoding);

                    if (geotiffOption) {
                        GeoTiffMatrixMultiLevelSource multiLevelSource = new GeoTiffMatrixMultiLevelSource(
                                resolutionCount, mosaicMatrix, bandBounds, bandIndexNumber, geoCoding, Double.NaN,
                                defaultJAIReadTileSize);
                        ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();

                        band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource, imageLayout));
                        product.addBand(band);
                    } else {
                        AffineTransform imageToModelTransform = Product.findImageToModelTransform(band.getGeoCoding());
                        JP2MatrixBandMultiLevelSource multiLevelSource = new JP2MatrixBandMultiLevelSource(
                                resolutionCount, mosaicMatrix, bandBounds, imageToModelTransform, bandIndexNumber,
                                mosaicOpBackgroundValue, mosaicOpSourceThreshold, defaultJAIReadTileSize);
                        ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
                        band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource, imageLayout));
                        if(offsets!=null && band.getUnit().matches("dl"))
                        {
                            for(String offsetStr: offsets) {
                                double offset = Double.parseDouble(offsetStr);
                                band.setScalingOffset(offset/quantificationValue);
                            }
                        }
                        product.addBand(band);
                    }
                }
            }
        }
        return productMaximumResolutionCount;
    }

    private void scaleBands(Product product, List<BandInfo> bandInfoList, S2SpatialResolution productResolution)
            throws IOException {
        // Find a reference band for rescaling the bands at other resolution
        MultiLevelImage targetImage = null;
        for (int i = 0; i < bandInfoList.size(); i++) {
            BandInfo bandInfo = bandInfoList.get(i);
            if (bandInfo.getBandInformation().getResolution() == productResolution) {
                Band referenceBand = product.getBand(bandInfo.getBandInformation().getPhysicalBand());
                targetImage = referenceBand.getSourceImage();
                break;
            }
        }

        // If the product only has a subset of bands, we may not find what we are
        // looking for
        if (targetImage == null) {
            String error = String.format(
                    "Products with no bands at %s m resolution currently cannot be read by the %s m reader",
                    productResolution.resolution, productResolution.resolution);
            throw new IOException(error);
        }

        for (int i = 0; i < product.getNumBands(); i++) {
            Band band = product.getBandAt(i);
            final MultiLevelImage sourceImage = band.getSourceImage();

            if (sourceImage.getWidth() == product.getSceneRasterWidth()
                    && sourceImage.getHeight() == product.getSceneRasterHeight()) {
                // Do not rescaled band which are already at the correct resolution
                continue;
            }

            ImageLayout imageLayout = new ImageLayout();
            ImageManager.getPreferredTileSize(product);
            final RenderingHints renderingHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout);
            float[] scalings = new float[2];
            scalings[0] = product.getSceneRasterWidth() / (float) sourceImage.getWidth();
            scalings[1] = product.getSceneRasterHeight() / (float) sourceImage.getHeight();
            PlanarImage scaledImage = SourceImageScaler.scaleMultiLevelImage(targetImage, sourceImage, scalings, null,
                    renderingHints, band.getNoDataValue(), Interpolation.getInstance(Interpolation.INTERP_NEAREST));
            band.setSourceImage(scaledImage);
        }
    }

    private void addIndexMasks(Product product, CoordinateReferenceSystem mapCRS, List<BandInfo> bandInfoList,
            S2OrthoSceneLayout sceneDescription, S2SpatialResolution productResolution,
            GeoCoding productDefaultGeoCoding, ProductSubsetDef subsetDef) throws IOException, FactoryException {
        for (BandInfo bandInfo : bandInfoList) {
            if (bandInfo.getBandInformation() instanceof S2IndexBandInformation) {
                Dimension defaultBandSize = sceneDescription
                        .getSceneDimension(bandInfo.getBandInformation().getResolution());
                S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) bandInfo.getBandInformation();
                IndexCoding indexCoding = indexBandInformation.getIndexCoding();
                product.getIndexCodingGroup().add(indexCoding);

                double pixelSize;
                if (isMultiResolution()) {
                    pixelSize = (double) bandInfo.getBandInformation().getResolution().resolution;
                } else {
                    pixelSize = (double) productResolution.resolution;
                }

                Rectangle bandBounds;
                if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                    bandBounds = new Rectangle(defaultBandSize.width, defaultBandSize.height);
                } else {
                    GeoCoding bandDefaultGeoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize,
                            defaultBandSize, null);
                    bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding,
                            bandDefaultGeoCoding, sceneDescription.getSceneDimension(productResolution).width,
                            sceneDescription.getSceneDimension(productResolution).height, defaultBandSize.width,
                            defaultBandSize.height, isMultiResolution());
                }
                if (!bandBounds.isEmpty()) {
                    // there is an intersection
                    Dimension dimension = new Dimension(bandBounds.width, bandBounds.height);

                    List<Color> colors = indexBandInformation.getColors();
                    Iterator<Color> colorIterator = colors.iterator();

                    for (String indexName : indexCoding.getIndexNames()) {
                        int indexValue = indexCoding.getIndexValue(indexName);
                        String description = indexCoding.getIndex(indexName).getDescription();
                        if (!colorIterator.hasNext()) {
                            // we should never be here : programming error.
                            throw new IOException(String.format(
                                    "Unexpected error when creating index masks : colors list does not have the same size as index coding"));
                        }
                        Color color = colorIterator.next();
                        String maskName = indexBandInformation.getPrefix() + indexName.toLowerCase();

                        if (subsetDef == null || (subsetDef.isNodeAccepted(maskName)
                                && subsetDef.isNodeAccepted(indexBandInformation.getPhysicalBand()))) {
                            Mask mask = Mask.BandMathsType.create(maskName, description, dimension.width,
                                    dimension.height,
                                    String.format("%s.raw == %d", indexBandInformation.getPhysicalBand(), indexValue),
                                    color, 0.5);

                            // set geoCoding
                            GeoCoding geoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize,
                                    defaultBandSize, bandBounds);
                            mask.setGeoCoding(geoCoding);

                            product.addMask(mask);
                        }
                    }
                }
            }
        }
    }

    private void addRasterMasks(List<S2Metadata.Tile> tileList, Product product, CoordinateReferenceSystem mapCRS,
            List<BandInfo> bandInfoList, S2OrthoSceneLayout sceneDescription, S2SpatialResolution productResolution,
            GeoCoding productDefaultGeoCoding, ProductSubsetDef subsetDef, Dimension defaultJAIReadTileSize)
            throws IOException {
        for (MaskInfo148 maskInfo : MaskInfo148.values()) {
            if (!maskInfo.isPresentAtLevel(getMaskLevel())) {
                continue;
            }
            if (!maskInfo.isEnabled()) {
                continue;
            }
            if (!maskInfo.isPerBand()) {
                // cloud masks are provided once and valid for all bands
                addRasterMask(product, tileList, maskInfo, mapCRS, null, null, bandInfoList, sceneDescription,
                        productResolution, productDefaultGeoCoding, subsetDef, defaultJAIReadTileSize);
            } else {
                // for other masks, we have one mask instance for each spectral band
                for (BandInfo bandInfo : bandInfoList) {
                    if (bandInfo.getBandInformation() instanceof S2SpectralInformation) {
                        addRasterMask(product, tileList, maskInfo, mapCRS,
                                (S2SpectralInformation) bandInfo.getBandInformation(), bandInfo, bandInfoList,
                                sceneDescription, productResolution, productDefaultGeoCoding, subsetDef,
                                defaultJAIReadTileSize);
                    }
                }
            }
        }
    }

    private void addRasterMask(Product product, List<S2Metadata.Tile> tileList, MaskInfo148 maskInfo,
            CoordinateReferenceSystem mapCRS, S2SpectralInformation spectralInfo, BandInfo bandInfo,
            List<BandInfo> bandInfoList, S2OrthoSceneLayout sceneDescription, S2SpatialResolution productResolution,
            GeoCoding productDefaultGeoCoding, ProductSubsetDef subsetDef, Dimension defaultJAIReadTileSize)
            throws IOException {
        S2Metadata.ProductCharacteristics productCharacteristics = this.orthoMetadataHeader
        .getProductCharacteristics();
        double quantificationValue = productCharacteristics.getQuantificationValue();
        VirtualPath maskPath = null;
        boolean maskFilesFound = false;
        for (S2Metadata.Tile tile : tileList) {
            if (tile.getMaskFilenames() == null) {
                continue;
            }

            for (S2Metadata.MaskFilename maskFilename : tile.getMaskFilenames()) {
                // We are only interested in a single mask main type
                if (!maskFilename.getType().equals(maskInfo.getMainType())) {
                    continue;
                }

                if (spectralInfo != null) {
                    // We are only interested in masks for a certain band
                    if (maskFilename.getBandId().equals(String.format("%s", spectralInfo.getBandId()))) {
                        maskPath = maskFilename.getPath();
                        maskFilesFound = true;
                        break;
                    }
                } else {
                    maskPath = maskFilename.getPath();
                    maskFilesFound = true;
                    break;
                }

            }
        }


        if (maskPath == null || !maskFilesFound) {
            return;
        }
        int productMaximumResolutionCount = 0;
        double mosaicOpBackgroundValue = S2Config.FILL_CODE_MOSAIC_BG;

        double mosaicOpSourceThreshold = 1.0d;

        if (spectralInfo == null) {
            for (int i = 0; i < maskInfo.getSubType().length; i++) {
                // This mask is not specific to a band
                // So we need one version of it for each resolution present in the band list
                S2SpatialResolution resolution = S2SpatialResolution.R60M;
                // Find a band with this resolution
                Band referenceBand = null;
                BandInfo referenceBandInfo = null;
                for (BandInfo bandInfoT : bandInfoList) {
                    if (bandInfoT.getBandInformation().getResolution() == resolution) {
                        referenceBand = product.getBand(bandInfoT.getBandInformation().getPhysicalBand());
                        referenceBandInfo = bandInfoT;
                        break;
                    }
                }

                // We may not find a band with this resolution
                if (referenceBand == null) {
                    continue;
                }
                Dimension defaultBandSize = sceneDescription
                        .getSceneDimension(referenceBandInfo.getBandInformation().getResolution());
                Dimension defaultProductSize = sceneDescription.getSceneDimension(productResolution);
                double pixelSize;
                if (isMultiResolution()) {
                    pixelSize = (double) referenceBandInfo.getBandInformation().getResolution().resolution;
                } else {
                    pixelSize = (double) productResolution.resolution;
                }
                Rectangle bandBounds;
                if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                    bandBounds = new Rectangle(defaultBandSize.width, defaultBandSize.height);
                } else {

                    GeoCoding bandDefaultGeoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize,
                            defaultBandSize, null);
                    bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding,
                            bandDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height,
                            defaultBandSize.width, defaultBandSize.height, isMultiResolution());
                }
                if (!bandBounds.isEmpty()) {
                    Collection<String> bandMatrixTileIds = sceneDescription.getTileIds();
                    Map<String, VirtualPath> tileIdToPathMapT = new HashMap<String, VirtualPath>();
                    tileIdToPathMapT.put(bandMatrixTileIds.iterator().next(), maskPath);
                    S2SpectralInformation spectralI = new S2SpectralInformation(String.format("B_%s", maskInfo.getSnapName(i)),
                            referenceBandInfo.getBandInformation().getResolution(), maskPath.getParent().toString(),
                            maskInfo.getDescription(i), null, quantificationValue, product.getNumBands(),
                            0.0, 0.0,
                            0.0);
                    BandInfo maskBandInfo = new BandInfo(tileIdToPathMapT, spectralI, null);
                    MosaicMatrix mosaicMatrix = buildBandMatrix(bandMatrixTileIds, sceneDescription, maskBandInfo);
                    int resolutionCount = computeMatrixCellsResolutionCount(mosaicMatrix, false);
                    productMaximumResolutionCount = Math.max(productMaximumResolutionCount, resolutionCount);
                    int dataBufferType = computeMatrixCellsDataBufferType(mosaicMatrix);
                    Band band = buildBand(maskBandInfo, bandBounds.width, bandBounds.height, dataBufferType);
                    band.setDescription(maskBandInfo.getBandInformation().getDescription());
                    band.setUnit("none");
                    band.setValidPixelExpression(null);
                    GeoCoding geoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize,
                            defaultBandSize, bandBounds);
                    band.setGeoCoding(geoCoding);

                    AffineTransform imageToModelTransform = Product.findImageToModelTransform(band.getGeoCoding());
                    JP2MatrixBandMultiLevelSource multiLevelSource = new JP2MatrixBandMultiLevelSource(resolutionCount,
                            mosaicMatrix, bandBounds, imageToModelTransform, i, mosaicOpBackgroundValue,
                            mosaicOpSourceThreshold, defaultJAIReadTileSize);
                    ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
                    band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource, imageLayout));
                    band.setNoDataValueUsed(false);
                    band.setScalingFactor(1);
                    band.setScalingOffset(0);
                    product.addBand(band);
                    Mask mask = Mask.BandMathsType.create(maskInfo.getSnapName(i), maskInfo.getDescription(i),
                            band.getRasterWidth(), band.getRasterHeight(),
                            String.format("%s.raw==%d", String.format("B_%s", maskInfo.getSnapName(i)), maskInfo.getValue(i)),
                            maskInfo.getColor(i), maskInfo.getTransparency(i));
                    ProductUtils.copyGeoCoding(band, mask);
                    product.addMask(mask);
                }
            }
        } else {
            Band band = null;
            GeoCoding geoCoding = null;
            StringBuilder bandExpression = new StringBuilder();
            String[] bandList = new String[ maskInfo.getSubType().length];
            for (int i = 0; i < maskInfo.getSubType().length; i++) {
                // // This mask is specific to a band
                String bandName = spectralInfo.getPhysicalBand();
                String maskBandName =  String.format("B_%s", maskInfo.getSnapNameForBand(bandName, i));
                if (!maskInfo.isMultiBand())
                    maskBandName = String.format("B_%s", maskInfo.getSnapNameForOneBand(bandName));

                S2SpatialResolution res = bandInfo.getBandInformation().getResolution();
                if(bandName.matches("B1")){
                    res=S2SpatialResolution.R60M;
                }
                S2SpectralInformation spectralI = new S2SpectralInformation(
                        maskBandName, res,
                        maskPath.getParent().toString(), maskInfo.getDescriptionForBand(bandName, i),
                        null, quantificationValue, product.getNumBands(),
                        0.0, 0.0,
                        0.0);

                Dimension defaultBandSize = sceneDescription.getSceneDimension(spectralI.getResolution());
                Dimension defaultProductSize = sceneDescription.getSceneDimension(productResolution);
                double pixelSize;
                if (isMultiResolution()) {
                    pixelSize = (double) spectralI.getResolution().resolution;
                } else {
                    pixelSize = (double) productResolution.resolution;
                }
                Rectangle bandBounds;
                if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                    bandBounds = new Rectangle(defaultBandSize.width, defaultBandSize.height);
                } else {

                    GeoCoding bandDefaultGeoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize,
                            defaultBandSize, null);
                    bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding,
                            bandDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height,
                            defaultBandSize.width, defaultBandSize.height, isMultiResolution());
                }
                if (!bandBounds.isEmpty()) {
                    if (maskInfo.isMultiBand() || (!maskInfo.isMultiBand() && i == 0)) {

                        Collection<String> bandMatrixTileIds = sceneDescription.getTileIds();
                        Map<String, VirtualPath> tileIdToPathMapT = new HashMap<String, VirtualPath>();
                        tileIdToPathMapT.put(bandMatrixTileIds.iterator().next(), maskPath);
                        BandInfo maskBandInfo = new BandInfo(tileIdToPathMapT, spectralI, null);
                        MosaicMatrix mosaicMatrix = buildBandMatrix(bandMatrixTileIds, sceneDescription, maskBandInfo);
                        int dataBufferType = computeMatrixCellsDataBufferType(mosaicMatrix);
                        int resolutionCount = computeMatrixCellsResolutionCount(mosaicMatrix, false);
                        productMaximumResolutionCount = Math.max(productMaximumResolutionCount, resolutionCount);
                        band = buildBand(maskBandInfo, bandBounds.width, bandBounds.height, dataBufferType);
                        band.setDescription(maskInfo.getDescriptionForBand(bandName, i));
                        band.setUnit("none");
                        band.setValidPixelExpression(null);

                        geoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize,
                                defaultBandSize, bandBounds);
                        band.setGeoCoding(geoCoding);
                        AffineTransform imageToModelTransform = Product.findImageToModelTransform(band.getGeoCoding());

                        JP2MatrixBandMultiLevelSource multiLevelSource = new JP2MatrixBandMultiLevelSource(
                                resolutionCount, mosaicMatrix, bandBounds, imageToModelTransform, i,
                                mosaicOpBackgroundValue, mosaicOpSourceThreshold, defaultJAIReadTileSize);
                        ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
                        band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource, imageLayout));
                        band.setNoDataValueUsed(false);
                        if(maskInfo.isMultiBand()) {
                            band.setScalingFactor(1);
                            band.setScalingOffset(0);
                            bandExpression.append(String.format("bit_set(%s,%d)", maskBandName, i));
                            bandList[i] = maskBandName;
                            if(i!=maskInfo.getSubType().length-1)
                                bandExpression.append(" AND ");
                        }
                        product.addBand(band);
                    }
                    if(!maskInfo.isMultiBand()) {
                        String maskName = maskInfo.getSnapNameForBand(bandName, i);
                        Mask mask = null;
                        if(maskInfo.getMainType().contains("MSK_DETFOO")) {
                            maskName = maskInfo.getSnapNameForDEFTOO(bandName, i);
                            mask = Mask.BandMathsType.create(maskName, maskInfo.getDescriptionForBandAndDetector(bandName, String.format("%d",i+1), i),
                                                band.getRasterWidth(), band.getRasterHeight(),
                            String.format("%s.raw==%d", maskBandName, maskInfo.getValue(i)), maskInfo.getColor(i),
                            maskInfo.getTransparency(i));
                        }else {
                            mask = Mask.BandMathsType.create(maskName,
                                maskInfo.getDescriptionForBand(bandName, i), band.getRasterWidth(), band.getRasterHeight(),
                                String.format("%s.raw==%d", maskBandName, maskInfo.getValue(i)),
                                maskInfo.getColor(i), maskInfo.getTransparency(i));
                        }
                        ProductUtils.copyGeoCoding(band, mask);
                        product.addMask(mask);
                    }
                }
            }
            if(maskInfo.isMultiBand()) {
                String bandName = spectralInfo.getPhysicalBand();
                String qualit_band = String.format("qualit_mask_%s", bandName);
                Band mergedBand = new VirtualBand(qualit_band, band.getDataType(), band.getRasterWidth(),  band.getRasterHeight(), bandExpression.toString());
                product.addBand(mergedBand);

                convertToRealBand(mergedBand, product, "Merged bands of the "+qualit_band);

                //remove the original source bands
                removeUnecessaryBand(bandList,product);

                product.getBand(qualit_band).setGeoCoding(geoCoding);
                for (int i = 0; i < maskInfo.getSubType().length; i++) {
                    String maskName = maskInfo.getSnapNameForBand(bandName, i);
                    Mask mask = null;
                    if (maskInfo.getMainType().contains("MSK_DETFOO")){
                        maskName = maskInfo.getSnapNameForDEFTOO(bandName, i);
                        mask = Mask.BandMathsType.create(maskName, maskInfo.getDescriptionForBandAndDetector(bandName, String.format("%d",i+1), i),
                            band.getRasterWidth(), band.getRasterHeight(),
                            String.format("bit_set(%s,%d)", qualit_band, i), maskInfo.getColor(i),
                            maskInfo.getTransparency(i));
                    }else {
                        mask = Mask.BandMathsType.create(maskName, maskInfo.getDescriptionForBand(bandName, i),
                            band.getRasterWidth(), band.getRasterHeight(),
                            String.format("bit_set(%s,%d)", qualit_band, i), maskInfo.getColor(i),
                            maskInfo.getTransparency(i));
                    }
                    ProductUtils.copyGeoCoding(band, mask);
                    product.addMask(mask);
                }
            }
        }
    }

    private void removeUnecessaryBand(String[] bandList, Product product) {
        for(String bandName:bandList) {
            product.removeBand(product.getBand(bandName));
        }
    }

    private void convertToRealBand(Band virtualBand, Product product, String description) {
        Band computedBand = virtualBand;
        String bandName = computedBand.getName();
        int width = computedBand.getRasterWidth();
        int height = computedBand.getRasterHeight();
        Band realBand = new Band(bandName, computedBand.getDataType(), width, height);
        realBand.setDescription(description);//createDescription(computedBand));
        realBand.setValidPixelExpression(computedBand.getValidPixelExpression());
        realBand.setUnit(computedBand.getUnit());
        realBand.setSpectralWavelength(computedBand.getSpectralWavelength());
        realBand.setGeophysicalNoDataValue(computedBand.getGeophysicalNoDataValue());
        realBand.setNoDataValueUsed(computedBand.isNoDataValueUsed());
        if (computedBand.isStxSet()) {
            realBand.setStx(computedBand.getStx());
        }

        ImageInfo imageInfo = computedBand.getImageInfo();
        if (imageInfo != null) {
            realBand.setImageInfo(imageInfo.clone());
        }

        ProductNodeGroup<Band> bandGroup = product.getBandGroup();
        int bandIndex = bandGroup.indexOf(computedBand);
        bandGroup.remove(computedBand);
        bandGroup.add(bandIndex, realBand);
        realBand.setSourceImage(createSourceImage(computedBand, realBand));
        realBand.setModified(true);
    }

    private MultiLevelImage createSourceImage(Band computedBand, Band realBand) {
        if (computedBand instanceof VirtualBand) {
            return VirtualBand.createSourceImage(realBand, ((VirtualBand) computedBand).getExpression());
        }else {
            return computedBand.getSourceImage();
        }
    }

    private void addVectorMasks(Product product, List<S2Metadata.Tile> tileList, List<BandInfo> bandInfoList,
            ProductSubsetDef subsetDef) throws IOException {
        for (MaskInfo maskInfo : MaskInfo.values()) {
            if (!maskInfo.isPresentAtLevel(getMaskLevel())) {
                continue;
            }
            if (!maskInfo.isEnabled()) {
                continue;
            }
            if (!maskInfo.isPerBand()) {
                // cloud masks are provided once and valid for all bands
                addVectorMask(product, tileList, maskInfo, null, bandInfoList, subsetDef);
            } else {
                // for other masks, we have one mask instance for each spectral band
                for (BandInfo bandInfo : bandInfoList) {
                    if (bandInfo.getBandInformation() instanceof S2SpectralInformation) {
                        addVectorMask(product, tileList, maskInfo,
                                (S2SpectralInformation) bandInfo.getBandInformation(), bandInfoList, subsetDef);
                    }
                }
            }
        }
    }

    private void addVectorMask(Product product, List<S2Metadata.Tile> tileList, MaskInfo maskInfo,
            S2SpectralInformation spectralInfo, List<BandInfo> bandInfoList, ProductSubsetDef subsetDef) {

        List<EopPolygon>[] productPolygons = new List[maskInfo.getSubType().length];
        for (int i = 0; i < maskInfo.getSubType().length; i++) {
            productPolygons[i] = new ArrayList<>();
        }

        boolean maskFilesFound = false;
        for (S2Metadata.Tile tile : tileList) {

            if (tile.getMaskFilenames() == null) {
                continue;
            }

            for (S2Metadata.MaskFilename maskFilename : tile.getMaskFilenames()) {

                // We are only interested in a single mask main type
                if (!maskFilename.getType().equals(maskInfo.getMainType())) {
                    continue;
                }

                if (spectralInfo != null) {
                    // We are only interested in masks for a certain band
                    if (!maskFilename.getBandId().equals(String.format("%s", spectralInfo.getBandId()))) {
                        continue;
                    }
                }

                maskFilesFound = true;

                List<EopPolygon> polygonsForTile;

                polygonsForTile = S2OrthoUtils.readPolygons(maskFilename.getPath());

                for (int i = 0; i < maskInfo.getSubType().length; i++) {
                    final int pos = i;
                    productPolygons[i].addAll(polygonsForTile.stream()
                            .filter(p -> p.getType().equals(maskInfo.getSubType()[pos])).collect(Collectors.toList()));
                }
            }
        }

        if (!maskFilesFound) {
            return;
        }

        for (int i = 0; i < maskInfo.getSubType().length; i++) {
            // TODO : why do we use this here ?
            final SimpleFeatureType type = Placemark.createGeometryFeatureType();
            // TODO : why "S2L1CMasks" ?
            final DefaultFeatureCollection collection = S2OrthoUtils.createDefaultFeatureCollection(productPolygons, i,
                    type);

            if (spectralInfo == null) {
                // This mask is not specific to a band
                // So we need one version of it for each resolution present in the band list
                for (S2SpatialResolution resolution : S2SpatialResolution.values()) {
                    // Find a band with this resolution
                    Band referenceBand = null;
                    for (BandInfo bandInfo : bandInfoList) {
                        if (bandInfo.getBandInformation().getResolution() == resolution) {
                            referenceBand = product.getBand(bandInfo.getBandInformation().getPhysicalBand());
                            break;
                        }
                    }

                    // We may not find a band with this resolution
                    if (referenceBand == null) {
                        continue;
                    }

                    // We need a different name for each resolution version
                    String description = maskInfo.getDescription(i);
                    if (!maskInfo.isPerPolygon()) {
                        String snapName = String.format("%s_%dm", maskInfo.getSnapName()[i], resolution.resolution);
                        if (subsetDef == null || subsetDef.isNodeAccepted(snapName)) {
                            VectorDataNode vdn = new VectorDataNode(snapName, collection);
                            product.addMask(snapName, vdn, description, maskInfo.getColor()[i],
                                    maskInfo.getTransparency()[i], referenceBand);
                            product.getVectorDataGroup().add(vdn);
                        }
                    } else {
                        // Currently there are no masks with this characteristics, the code should be
                        // tested if a new mask is added
                        SimpleFeatureIterator simpleFeatureIterator = collection.features();
                        List<String> distictPolygonsOrdered = S2OrthoUtils
                                .createDistictPolygonsOrdered(simpleFeatureIterator);
                        simpleFeatureIterator.close();

                        ColorIterator.reset();
                        for (String subId : distictPolygonsOrdered) {
                            final DefaultFeatureCollection subCollection = new DefaultFeatureCollection(subId, type);
                            simpleFeatureIterator = collection.features();
                            while (simpleFeatureIterator.hasNext()) {
                                SimpleFeature simpleFeature = simpleFeatureIterator.next();
                                if (simpleFeature.getID().startsWith(subId)) {
                                    subCollection.add(simpleFeature);
                                }
                            }
                            simpleFeatureIterator.close();
                            VectorDataNode vdnPolygon = new VectorDataNode(subId, subCollection);
                            String snapName = String.format("%s_%dm", subId, resolution.resolution);
                            if (subsetDef == null || subsetDef.isNodeAccepted(snapName)) {
                                product.addMask(snapName, vdnPolygon, description, ColorIterator.next(),
                                        maskInfo.getTransparency()[i], referenceBand);
                                product.getVectorDataGroup().add(vdnPolygon);
                            }
                        }
                    }
                }
            } else {

                // This mask is specific to a band
                Band referenceBand = product.getBand(spectralInfo.getPhysicalBand());
                String bandName = spectralInfo.getPhysicalBand();
                String description = maskInfo.getDescriptionForBand(bandName, i);

                if (!maskInfo.isPerPolygon()) {
                    String snapName = maskInfo.getSnapNameForBand(bandName, i);
                    if (subsetDef == null || subsetDef.isNodeAccepted(snapName)) {
                        VectorDataNode vdn = new VectorDataNode(snapName, collection);
                        product.addMask(snapName, vdn, description, maskInfo.getColor()[i],
                                maskInfo.getTransparency()[i], referenceBand);
                        product.getVectorDataGroup().add(vdn);
                    }
                } else {
                    SimpleFeatureIterator simpleFeatureIterator = collection.features();
                    List<String> distictPolygonsOrdered = S2OrthoUtils
                            .createDistictPolygonsOrdered(simpleFeatureIterator);
                    simpleFeatureIterator.close();

                    ColorIterator.reset();
                    for (String subId : distictPolygonsOrdered) {
                        final DefaultFeatureCollection subCollection = new DefaultFeatureCollection(subId, type);
                        simpleFeatureIterator = collection.features();
                        while (simpleFeatureIterator.hasNext()) {
                            SimpleFeature simpleFeature = simpleFeatureIterator.next();
                            if (simpleFeature.getID().startsWith(subId)) {
                                subCollection.add(simpleFeature);
                            }
                        }
                        simpleFeatureIterator.close();
                        if (subsetDef == null || subsetDef.isNodeAccepted(subId)) {
                            VectorDataNode vdnPolygon = new VectorDataNode(subId, subCollection);
                            product.addMask(subId, vdnPolygon, description, ColorIterator.next(),
                                    maskInfo.getTransparency()[i], referenceBand);
                            product.getVectorDataGroup().add(vdnPolygon);
                        }
                    }
                }
            }
        }
    }

    private void addTileIndexes(Product product, CoordinateReferenceSystem mapCRS,
            List<S2SpatialResolution> resolutions, List<S2Metadata.Tile> tileList, S2OrthoSceneLayout sceneDescription,
            S2SpatialResolution productResolution, GeoCoding productDefaultGeoCoding, ProductSubsetDef subsetDef)
            throws IOException, FactoryException {
        if (resolutions.isEmpty()) {
            throw new IllegalArgumentException("The resolution list is empty.");
        }
        if (tileList.isEmpty()) {
            throw new IllegalArgumentException("The tile list is empty.");
        }

        List<S2IndexBandInformation> listTileIndexBandInformation = new ArrayList<>();

        // for each resolution, add the tile information
        for (S2SpatialResolution res : S2SpatialResolution.values()) {
            if (resolutions.contains(res)) {
                listTileIndexBandInformation.add(makeTileInformation(res, sceneDescription));
            }
        }

        List<BandInfo> tileInfoList = new ArrayList<>();
        // Create BandInfo and add to tileInfoList
        for (S2BandInformation bandInformation : listTileIndexBandInformation) {
            HashMap<String, VirtualPath> tilePathMap = new HashMap<>();
            for (S2Metadata.Tile tile : tileList) {
                tilePathMap.put(tile.getId(), null); // it is not necessary any file
            }
            if (!tilePathMap.isEmpty()) {
                BandInfo tileInfo = createBandInfoFromHeaderInfo(bandInformation, tilePathMap,
                        this.orthoMetadataHeader.getConfig());
                if (tileInfo != null) {
                    tileInfoList.add(tileInfo);
                }
            }
        }

        if (tileInfoList.isEmpty()) {
            return;
        }
        // Add the bands
        for (BandInfo bandInfo : tileInfoList) {
            addTileIndex(product, mapCRS, bandInfo, sceneDescription, productResolution, productDefaultGeoCoding,
                    subsetDef);
        }

        // Add the index masks
        addIndexMasks(product, mapCRS, tileInfoList, sceneDescription, productResolution, productDefaultGeoCoding,
                subsetDef);
    }

    private void addTileIndex(Product product, CoordinateReferenceSystem mapCRS, BandInfo bandInfo,
            S2OrthoSceneLayout sceneDescription, S2SpatialResolution productResolution,
            GeoCoding productDefaultGeoCoding, ProductSubsetDef subsetDef) throws IOException, FactoryException {
        Dimension defaultProductSize = sceneDescription.getSceneDimension(productResolution);
        Dimension dimension = sceneDescription.getSceneDimension(bandInfo.getBandInformation().getResolution());
        MosaicMatrix mosaicMatrix = buildOrthoIndexBandMatrix(sceneDescription.getOrderedTileIds(), sceneDescription,
                bandInfo);
        int dataBufferType = computeMatrixCellsDataBufferType(mosaicMatrix);
        Dimension defaultBandSize = new Dimension(mosaicMatrix.computeTotalWidth(), mosaicMatrix.computeTotalHeight());
        double pixelSize;
        if (isMultiResolution()) {
            pixelSize = (double) bandInfo.getBandInformation().getResolution().resolution;
        } else {
            pixelSize = (double) productResolution.resolution;
        }

        Rectangle bandBounds;
        if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
            bandBounds = new Rectangle(defaultBandSize.width, defaultBandSize.height);
        } else {
            GeoCoding bandDefaultGeoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize,
                    defaultBandSize, null);
            bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding,
                    bandDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, defaultBandSize.width,
                    defaultBandSize.height, isMultiResolution());
        }
        if (!bandBounds.isEmpty()) {
            // there is an intersection
            int bandDataType = ImageManager.getProductDataType(dataBufferType);
            Band band = new Band(bandInfo.getBandName(), bandDataType, dimension.width, dimension.height);
            S2BandInformation bandInformation = bandInfo.getBandInformation();
            band.setScalingFactor(bandInformation.getScalingFactor());
            S2IndexBandInformation indexBandInfo = (S2IndexBandInformation) bandInformation;
            band.setSpectralWavelength(0);
            band.setSpectralBandwidth(0);
            band.setSpectralBandIndex(-1);
            band.setSampleCoding(indexBandInfo.getIndexCoding());
            band.setImageInfo(indexBandInfo.getImageInfo());

            band.setDescription(bandInfo.getBandInformation().getDescription());
            band.setUnit(bandInfo.getBandInformation().getUnit());

            band.setValidPixelExpression(String.format("%s.raw > 0", bandInfo.getBandInformation().getPhysicalBand()));

            GeoCoding geoCoding = buildGeoCoding(sceneDescription, mapCRS, pixelSize, pixelSize, defaultBandSize,
                    bandBounds);
            band.setGeoCoding(geoCoding);

            AffineTransform imageToModelTransform = Product.findImageToModelTransform(band.getGeoCoding());

            band.setImageToModelTransform(imageToModelTransform);

            Double mosaicOpSourceThreshold = null;
            double mosaicOpBackgroundValue = Double.NaN;
            int resolutionCount = DefaultMultiLevelModel.getLevelCount(bandBounds.width, bandBounds.height); // thisBandTileLayout.numResolutions;
            Dimension preferredTileSize = product.getPreferredTileSize();

            TileIndexMultiLevelSource tileIndex = new TileIndexMultiLevelSource(resolutionCount, mosaicMatrix,
                    bandBounds, preferredTileSize, imageToModelTransform, mosaicOpSourceThreshold,
                    mosaicOpBackgroundValue);
            ImageLayout imageLayout = ImageUtils.buildImageLayout(dataBufferType, bandBounds.width, bandBounds.height,
                    0, preferredTileSize);
            band.setSourceImage(new DefaultMultiLevelImage(tileIndex, imageLayout));

            product.addBand(band);
        }
    }

    private static MosaicMatrix buildOrthoIndexBandMatrix(List<String> bandMatrixTileIds,
            S2SceneDescription sceneDescription, BandInfo tileBandInfo) {
        MosaicMatrixCellCallback mosaicMatrixCellCallback = new MosaicMatrixCellCallback() {
            @Override
            public MosaicMatrix.MatrixCell buildMatrixCell(String tileId, BandInfo tileBandInfo, int sceneCellWidth,
                    int sceneCellHeight) {
                S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) tileBandInfo
                        .getBandInformation();
                S2OrthoGranuleDirFilename s2GranuleDirFilename = S2OrthoGranuleDirFilename.create(tileId);
                if (s2GranuleDirFilename == null) {
                    throw new NullPointerException("The granule dir file name is null.");
                }
                String tileNumber = s2GranuleDirFilename.tileNumber;
                Integer indexSample = indexBandInformation.findIndexSample(tileNumber);
                if (indexSample == null) {
                    throw new NullPointerException("The index sample is null.");
                }
                short indexValueShort = indexSample.shortValue();
                return new TileIndexBandMatrixCell(sceneCellWidth, sceneCellHeight, indexValueShort);
            }
        };
        return buildBandMatrix(bandMatrixTileIds, sceneDescription, tileBandInfo, mosaicMatrixCellCallback);
    }

    private static boolean isValidAngle(float value) {
        return !Float.isNaN(value) && !Float.isInfinite(value);
    }

    // Checks if every angleGrid has the expected size
    private static boolean checkAnglesGrids(S2Metadata.AnglesGrid[] anglesGrids, int expectedGridHeight,
            int expectedGridWidth) {
        if (anglesGrids == null) {
            return false;
        }
        for (S2Metadata.AnglesGrid angleGrid : anglesGrids) {
            if (angleGrid.getHeight() != expectedGridHeight || angleGrid.getWidth() != expectedGridWidth) {
                return false;
            }
        }
        return true;
    }

    private static S2BandAnglesGrid[] createS2OrthoAnglesGrids(S2Metadata metadataHeader, String tileId)
            throws IOException {
        S2BandAnglesGrid[] bandAnglesGrid = null;
        ArrayList<S2BandAnglesGrid> listBandAnglesGrid = new ArrayList<>();
        S2Metadata.Tile tile = metadataHeader.getTile(tileId);
        S2Metadata.AnglesGrid anglesGrid = tile.getSunAnglesGrid();

        int resolution = tile.getAnglesResolution();

        if (anglesGrid == null) {
            return bandAnglesGrid;
        }
        int gridHeight = tile.getSunAnglesGrid().getZenith().length;
        int gridWidth = tile.getSunAnglesGrid().getZenith()[0].length;
        float[] sunZeniths = new float[gridWidth * gridHeight];
        float[] sunAzimuths = new float[gridWidth * gridHeight];
        float[] viewingZeniths = new float[gridWidth * gridHeight];
        float[] viewingAzimuths = new float[gridWidth * gridHeight];
        int[] viewingZenithsCount = new int[gridWidth * gridHeight];
        int[] viewingAzimuthsCount = new int[gridWidth * gridHeight];

        Arrays.fill(viewingZeniths, Float.NaN);
        Arrays.fill(viewingAzimuths, Float.NaN);

        S2Metadata.AnglesGrid sunAnglesGrid = tile.getSunAnglesGrid();
        S2Metadata.AnglesGrid[] viewingIncidenceAnglesGrids = tile.getViewingIncidenceAnglesGrids();

        if (checkAnglesGrids(viewingIncidenceAnglesGrids, gridHeight, gridWidth)) {
            int iLastBandId = -1;
            int bandId;
            for (S2Metadata.AnglesGrid grid : viewingIncidenceAnglesGrids) {
                bandId = grid.getBandId();

                // if lastBand and the current band are different, the lecture of the last band
                // has finished and we add it to listBandAnglesGrid
                // after that, the arrays are filled again with NaN
                if (iLastBandId != bandId) {
                    if (iLastBandId >= 0) {
                        float[] zeniths = new float[gridWidth * gridHeight];
                        float[] azimuths = new float[gridWidth * gridHeight];
                        System.arraycopy(viewingZeniths, 0, zeniths, 0, gridWidth * gridHeight);
                        System.arraycopy(viewingAzimuths, 0, azimuths, 0, gridWidth * gridHeight);
                        listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_ZENITH_PREFIX,
                                S2BandConstants.getBand(iLastBandId), gridWidth, gridHeight,
                                (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(),
                                (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution,
                                resolution, zeniths));
                        listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_AZIMUTH_PREFIX,
                                S2BandConstants.getBand(iLastBandId), gridWidth, gridHeight,
                                (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(),
                                (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution,
                                resolution, azimuths));
                    }
                    Arrays.fill(viewingZeniths, Float.NaN);
                    Arrays.fill(viewingAzimuths, Float.NaN);
                    iLastBandId = bandId;
                }

                for (int y = 0; y < gridHeight; y++) {
                    for (int x = 0; x < gridWidth; x++) {
                        final int index = y * gridWidth + x;
                        try {
                            if (y < grid.getZenith().length) {
                                if (x < grid.getZenith()[y].length) {
                                    if (isValidAngle(grid.getZenith()[y][x])) {
                                        viewingZeniths[index] = grid.getZenith()[y][x];
                                    }
                                }
                            }

                            if (y < grid.getAzimuth().length) {
                                if (x < grid.getAzimuth()[y].length) {
                                    if (isValidAngle(grid.getAzimuth()[y][x])) {
                                        viewingAzimuths[index] = grid.getAzimuth()[y][x];
                                    }
                                }
                            }

                        } catch (Exception e) {
                            logger.severe(StackTraceUtils.getStackTrace(e));
                        }
                    }
                }
            }

            // add the last band which is in memory
            if (iLastBandId > 0) {
                float[] zeniths = new float[gridWidth * gridHeight];
                float[] azimuths = new float[gridWidth * gridHeight];
                System.arraycopy(viewingZeniths, 0, zeniths, 0, gridWidth * gridHeight);
                System.arraycopy(viewingAzimuths, 0, azimuths, 0, gridWidth * gridHeight);
                listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_ZENITH_PREFIX, S2BandConstants.getBand(iLastBandId),
                        gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(),
                        (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution,
                        zeniths));
                listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_AZIMUTH_PREFIX, S2BandConstants.getBand(iLastBandId),
                        gridWidth, gridHeight, (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(),
                        (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution,
                        azimuths));
            }

            // Compute the mean viewing angles
            Arrays.fill(viewingZeniths, 0.0f);
            Arrays.fill(viewingAzimuths, 0.0f);
            Arrays.fill(viewingZenithsCount, 0);
            Arrays.fill(viewingAzimuthsCount, 0);
            for (S2BandAnglesGrid grid : listBandAnglesGrid) {
                for (int i = 0; i < grid.getData().length; i++) {
                    float gridData = grid.getData()[i];
                    if (grid.getPrefix().equals(VIEW_ZENITH_PREFIX)) {
                        viewingZeniths[i] = viewingZeniths[i] + gridData;
                        viewingZenithsCount[i]++;
                    }
                    if (grid.getPrefix().equals(VIEW_AZIMUTH_PREFIX)) {
                        viewingAzimuths[i] = viewingAzimuths[i] + gridData;
                        viewingAzimuthsCount[i]++;
                    }
                }
            }
            for (int i = 0; i < viewingZeniths.length; i++) {
                if (viewingZenithsCount[i] != 0)
                    viewingZeniths[i] = viewingZeniths[i] / viewingZenithsCount[i];
                if (viewingAzimuthsCount[i] != 0)
                    viewingAzimuths[i] = viewingAzimuths[i] / viewingAzimuthsCount[i];
            }
        }

        // out of the "if" because we want always the mean view angles (perhaps they
        // will be NaN)
        listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_ZENITH_PREFIX, null, gridWidth, gridHeight,
                (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(),
                (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution,
                viewingZeniths));
        listBandAnglesGrid.add(new S2BandAnglesGrid(VIEW_AZIMUTH_PREFIX, null, gridWidth, gridHeight,
                (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(),
                (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution,
                viewingAzimuths));

        if (sunAnglesGrid != null) {
            for (int y = 0; y < gridHeight; y++) {
                for (int x = 0; x < gridWidth; x++) {
                    final int index = y * gridWidth + x;
                    sunZeniths[index] = sunAnglesGrid.getZenith()[y][x];
                    sunAzimuths[index] = sunAnglesGrid.getAzimuth()[y][x];
                }
            }
            listBandAnglesGrid.add(new S2BandAnglesGrid(SUN_ZENITH_PREFIX, null, gridWidth, gridHeight,
                    (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(),
                    (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution,
                    sunZeniths));
            listBandAnglesGrid.add(new S2BandAnglesGrid(SUN_AZIMUTH_PREFIX, null, gridWidth, gridHeight,
                    (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftX(),
                    (float) tile.getTileGeometry(S2SpatialResolution.R10M).getUpperLeftY(), resolution, resolution,
                    sunAzimuths));
        }

        if (listBandAnglesGrid.size() > 0) {
            bandAnglesGrid = listBandAnglesGrid.toArray(new S2BandAnglesGrid[listBandAnglesGrid.size()]);
        }

        return bandAnglesGrid;
    }

    private BandInfo createBandInfoFromHeaderInfo(S2BandInformation bandInformation,
            Map<String, VirtualPath> tilePathMap, S2Config config) {
        S2SpatialResolution spatialResolution = bandInformation.getResolution();
        if (config.getTileLayout(spatialResolution.resolution) == null) {
            return null;
        }
        return new BandInfo(tilePathMap, bandInformation, config.getTileLayout(spatialResolution.resolution));
    }

    private static VirtualPath getProductDir(VirtualPath productPath) throws IOException {
        if (!productPath.exists()) {
            throw new FileNotFoundException("File not found: " + productPath.getFullPathString());
        }
        return productPath.getParent();
    }

    /**
     * Check the content of first and last rows and columns, and if all the pixels
     * are zero, they are removed
     *
     * @param planarImage
     * @return
     */
    private static RenderedOp cropBordersIfAreZero(RenderedOp planarImage) {
        // First row
        boolean remove = true;
        for (int i = 0; i < planarImage.getWidth(); i++) {
            if (planarImage.copyData().getSampleFloat(planarImage.getMinX() + i, planarImage.getMinY(), 0) != 0) {
                remove = false;
                break;
            }
        }
        if (remove) {
            planarImage = CropDescriptor.create(planarImage, planarImage.getMinX() + 0.0f, planarImage.getMinY() + 1.0f,
                    (float) planarImage.getWidth(), (float) planarImage.getHeight() - 1, null);
        }

        // Last row
        remove = true;
        for (int i = 0; i < planarImage.getWidth(); i++) {
            if (planarImage.copyData().getSampleFloat(planarImage.getMinX() + i,
                    planarImage.getMinY() + planarImage.getHeight() - 1, 0) != 0) {
                remove = false;
                break;
            }
        }
        if (remove) {
            planarImage = CropDescriptor.create(planarImage, planarImage.getMinX() + 0.0f, planarImage.getMinY() + 0.0f,
                    (float) planarImage.getWidth(), (float) planarImage.getHeight() - 1, null);
        }

        // First column
        remove = true;
        for (int i = 0; i < planarImage.getHeight(); i++) {
            if (planarImage.copyData().getSampleFloat(planarImage.getMinX(), planarImage.getMinY() + i, 0) != 0) {
                remove = false;
                break;
            }
        }
        if (remove) {
            planarImage = CropDescriptor.create(planarImage, planarImage.getMinX() + 1.0f, planarImage.getMinY() + 0.0f,
                    (float) planarImage.getWidth() - 1, (float) planarImage.getHeight(), null);
        }

        // Last column
        remove = true;
        for (int i = 0; i < planarImage.getHeight(); i++) {
            if (planarImage.copyData().getSampleFloat(planarImage.getMinX() + planarImage.getWidth() - 1,
                    planarImage.getMinY() + i, 0) != 0) {
                remove = false;
                break;
            }
        }
        if (remove) {
            planarImage = CropDescriptor.create(planarImage, planarImage.getMinX() + 0.0f, planarImage.getMinY() + 0.0f,
                    (float) planarImage.getWidth() - 1, (float) planarImage.getHeight(), null);
        }

        return planarImage;
    }

    /**
     * The origin of planarImage and sceneLayout must be the same. Compute the
     * number of the pixels needed to cover the scene and remove the rows and
     * columns outside the scene in planarImage.
     *
     * @param planarImage
     * @param resolutionX
     * @param resolutionY
     * @param sceneLayout
     * @return
     */
    private static RenderedOp cropBordersOutsideScene(RenderedOp planarImage, float resolutionX, float resolutionY,
            S2OrthoSceneLayout sceneLayout) {
        if (sceneLayout.sceneDimensions.size() <= 0) {
            return planarImage;
        }
        int sceneHeight = 0;
        int sceneWidth = 0;
        for (S2SpatialResolution resolution : S2SpatialResolution.values()) {
            if (sceneLayout.sceneDimensions.get(resolution) != null) {
                sceneHeight = sceneLayout.getSceneDimension(resolution).height * resolution.resolution;
                sceneWidth = sceneLayout.getSceneDimension(resolution).width * resolution.resolution;
                break;
            }
        }

        int columnNumber = (int) Math.ceil(sceneWidth / resolutionX);
        int rowNumber = (int) Math.ceil(sceneHeight / resolutionY);
        return CropDescriptor.create(planarImage, planarImage.getMinX() + 0.0f, planarImage.getMinY() + 0.0f,
                (float) columnNumber, (float) rowNumber, null);
    }

    @Override
    public S2BandAnglesGridByDetector[] getViewingIncidenceAnglesGrids(int bandId, int detectorId) {
        if (this.orthoMetadataHeader == null) {
            return null;
        }
        return this.orthoMetadataHeader.getAnglesGridByDetector(bandId, detectorId);
    }

    @Override
    public S2BandAnglesGrid[] getSunAnglesGrid() {
        if (this.orthoMetadataHeader == null) {
            return null;
        }
        return this.orthoMetadataHeader.getSunAnglesGrid();
    }

    private static class AngleID implements Comparable<AngleID> {
        final String prefix;
        final S2BandConstants band;

        AngleID(String prefix, S2BandConstants band) {
            this.prefix = prefix;
            this.band = band;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            AngleID angleID = (AngleID) o;

            if (!prefix.equals(angleID.prefix))
                return false;
            return band == angleID.band;
        }

        @Override
        public int hashCode() {
            int result = prefix.hashCode();
            result = 31 * result + (band != null ? band.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(AngleID compareAngleID) {
            int order;
            String comparePrefix = compareAngleID.prefix;
            S2BandConstants compareBand = compareAngleID.band;

            if (compareBand == null && this.band == null) {
                if (comparePrefix.equals(this.prefix)) {
                    order = 0;
                } else if (comparePrefix.contains("sun") && !this.prefix.contains("sun")) {
                    order = -1;
                } else if (!comparePrefix.contains("sun") && this.prefix.contains("sun")) {
                    order = 1;
                } else if (this.prefix.contains("zenith")) {
                    order = -1;
                } else {
                    order = 1;
                }
            } else if (compareBand == null) {
                order = 1;
            } else if (this.band == null) {
                order = -1;
            } else if (compareBand.getBandIndex() < this.band.getBandIndex()) {
                order = 1;
            } else if (compareBand.getBandIndex() > this.band.getBandIndex()) {
                order = -1;
            } else if (this.prefix.contains("zenith")) {
                order = -1;
            } else {
                order = 1;
            }
            return order;
        }
    }

    private static String getAngleBandName(AngleID angleID) {
        String bandName;
        if (angleID.band != null) {
            bandName = angleID.prefix + "_" + angleID.band.getPhysicalName();
        } else if (angleID.prefix.equals(VIEW_AZIMUTH_PREFIX) || angleID.prefix.equals(VIEW_ZENITH_PREFIX)) {
            bandName = angleID.prefix + "_mean";
        } else {
            bandName = angleID.prefix;
        }
        return bandName;
    }

    private static List<PlanarImage> buildMosaicTileImages(AngleID angleID, S2OrthoSceneLayout sceneDescription,
            Dimension anglesTileSize, HashMap<String, S2BandAnglesGrid[]> bandAnglesGridsMap,
            Point.Float masterOrigin) {

        int[] bandOffsets = { 0 };
        SampleModel sampleModel = new PixelInterleavedSampleModel(TYPE_FLOAT, anglesTileSize.width,
                anglesTileSize.height, 1, anglesTileSize.width, bandOffsets);
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.TRANSLUCENT, TYPE_FLOAT);

        // mosaic of planar images
        List<PlanarImage> tileImages = new ArrayList<>();
        for (String tileId : sceneDescription.getOrderedTileIds()) {
            S2BandAnglesGrid[] bandAnglesGrids = bandAnglesGridsMap.get(tileId);
            // search index of angleID
            int i = -1;
            for (int j = 0; j < bandAnglesGrids.length; j++) {
                AngleID angleIDAux = new AngleID(bandAnglesGrids[j].getPrefix(), bandAnglesGrids[j].getBand());
                if (angleID.equals(angleIDAux)) {
                    i = j;
                }
            }

            float rasterPixels[];
            if (i == -1) {
                rasterPixels = new float[anglesTileSize.width * anglesTileSize.height];
                Arrays.fill(rasterPixels, Float.NaN);
            } else {
                rasterPixels = bandAnglesGrids[i].getData();
            }
            DataBuffer buffer = new DataBufferFloat(anglesTileSize.width * anglesTileSize.height);
            WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);
            raster.setPixels(0, 0, anglesTileSize.width, anglesTileSize.height, rasterPixels);

            // And finally create an image with this raster
            BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
            PlanarImage opImage = PlanarImage.wrapRenderedImage(image);

            // Translate tile
            float translateX = (bandAnglesGrids[0].originX - masterOrigin.x) / bandAnglesGrids[0].getResolutionX();
            float translateY = (bandAnglesGrids[0].originY - masterOrigin.y) / bandAnglesGrids[0].getResolutionY();
            RenderingHints hints = new RenderingHints(JAI.KEY_TILE_CACHE, null);
            RenderedOp translateOpImage = TranslateDescriptor.create(opImage, translateX, -translateY,
                    Interpolation.getInstance(Interpolation.INTERP_BILINEAR), hints);

            // Crop output image because with bilinear interpolation some pixels are 0.0
            RenderedOp cropOpImage = cropBordersIfAreZero(translateOpImage);
            // Feed the image list for mosaic
            tileImages.add(cropOpImage);
        }
        return tileImages;
    }

    public static GeoCoding buildGeoCoding(S2OrthoSceneLayout sceneDescription, CoordinateReferenceSystem mapCRS,
            double pixelSizeX, double pixelSizeY, Dimension defaultProductSize, Rectangle productBounds)
            throws IOException {
        try {
            return ImageUtils.buildCrsGeoCoding(sceneDescription.getSceneOrigin()[0],
                    sceneDescription.getSceneOrigin()[1], pixelSizeX, pixelSizeY, defaultProductSize, mapCRS,
                    productBounds);

        } catch (FactoryException | TransformException e) {
            throw new IOException(e);
        }
    }

    private static String buildAutoGroupingPattern() {
        return "sun:view:quality:ECMWF:tile:detector_footprint:nodata:partially_corrected_crosstalk:coarse_cloud:snow_and_ice_areas:saturated_l1a:saturated_l1b:defective:ancillary_lost:ancillary_degraded:msi_lost:msi_degraded:saturated_l1a:opaque_clouds:cirrus_clouds:scl:msc:ddv:tile:"
                + "detector_footprint-B01:" + "detector_footprint-B02:" + "detector_footprint-B03:"
                + "detector_footprint-B04:" + "detector_footprint-B05:" + "detector_footprint-B06:"
                + "detector_footprint-B07:" + "detector_footprint-B08:" + "detector_footprint-B8A:"
                + "detector_footprint-B09:" + "detector_footprint-B10:" + "detector_footprint-B11:"
                + "detector_footprint-B12:" + "qualit_mask";
    }
}
