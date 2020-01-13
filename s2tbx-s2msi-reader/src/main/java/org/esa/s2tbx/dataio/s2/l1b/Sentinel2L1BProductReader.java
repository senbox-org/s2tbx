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

package org.esa.s2tbx.dataio.s2.l1b;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.vividsolutions.jts.geom.Coordinate;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.*;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1b.metadata.L1bMetadata;
import org.esa.s2tbx.dataio.s2.l1b.metadata.L1bProductMetadataReader;
import org.esa.s2tbx.dataio.s2.l1b.tiles.BandL1bSceneMultiLevelSource;
import org.esa.s2tbx.dataio.s2.l1b.tiles.TileIndexMultiLevelSource;
import org.esa.s2tbx.dataio.s2.metadata.AbstractS2ProductMetadataReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.core.util.ImageUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

import static org.esa.s2tbx.dataio.s2.S2Metadata.ProductCharacteristics;
import static org.esa.s2tbx.dataio.s2.S2Metadata.Tile;
import static org.esa.s2tbx.dataio.s2.l1b.CoordinateUtils.*;
import static org.esa.s2tbx.dataio.s2.l1b.metadata.L1bMetadataProc.makeTileInformation;
import static org.esa.snap.utils.DateHelper.parseDate;

// todo - register reasonable RGB profile(s)
// todo - set a band's validMaskExpr or no-data value (read from GML)
// todo - viewing incidence tie-point grids contain NaN values - find out how to correctly treat them

// todo - better collect problems during product opening and generate problem report (requires reader API change), see {@report "Problem detected..."} code marks

/**
 * <p>
 * This product reader can currently read single L1C tiles (also called L1C granules) and entire L1C scenes composed of
 * multiple L1C tiles.
 * </p>
 * <p>
 * To read single tiles, select any tile image file (IMG_*.jp2) within a product package. The reader will then
 * collect other band images for the selected tile and wiull also try to read the metadata file (MTD_*.xml).
 * </p>
 * <p>To read an entire scene, select the metadata file (MTD_*.xml) within a product package. The reader will then
 * collect other tile/band images and create a mosaic on the fly.
 * </p>
 *
 * @author Norman Fomferra
 */
public class Sentinel2L1BProductReader extends Sentinel2ProductReader {

    static final String L1B_CACHE_DIR = "l1b-reader";

    public enum ProductInterpretation {
        RESOLUTION_10M,
        RESOLUTION_20M,
        RESOLUTION_60M,
        RESOLUTION_MULTI
    }

    private final ProductInterpretation interpretation;

    public Sentinel2L1BProductReader(ProductReaderPlugIn readerPlugIn, ProductInterpretation interpretation) {
        super(readerPlugIn);

        this.interpretation = interpretation;
    }

    @Override
    public S2SpatialResolution getProductResolution() {
        if (this.interpretation == ProductInterpretation.RESOLUTION_20M) {
            return S2SpatialResolution.R20M;
        }
        if (this.interpretation == ProductInterpretation.RESOLUTION_60M) {
            return S2SpatialResolution.R60M;
        }
        return S2SpatialResolution.R10M;
    }

    @Override
    public boolean isMultiResolution() {
        return this.interpretation == ProductInterpretation.RESOLUTION_MULTI;
    }

    @Override
    protected String getReaderCacheDir() {
        return L1B_CACHE_DIR;
    }


    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand,
                                          int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
                                          throws IOException {
        // should never not come here, since we have an OpImage that reads data
    }

    @Override
    protected AbstractS2ProductMetadataReader buildProductMetadata(VirtualPath virtualPath) throws IOException {
        return new L1bProductMetadataReader(virtualPath);
    }

    @Override
    protected Product readProduct(String defaultProductName, boolean isGranule, S2Metadata metadataHeader) throws Exception {
        L1bMetadata l1bMetadataHeader = (L1bMetadata)metadataHeader;

        L1bSceneDescription sceneDescription = L1bSceneDescription.create(l1bMetadataHeader, getProductResolution());
        Map<S2SpatialResolution, Dimension> sceneDimensions = L1bSceneDescription.computeSceneDimensions(l1bMetadataHeader);

        VirtualPath productDir = l1bMetadataHeader.getProductMetadataPath().getParent();
        S2Config config = l1bMetadataHeader.getConfig();
        initCacheDir(productDir);

        List<L1bMetadata.Tile> tileList = l1bMetadataHeader.computeTiles();
        Map<String, L1BBandInfo> bandInfoByKey = l1bMetadataHeader.computeBandInfoByKey(tileList);
        if (bandInfoByKey.isEmpty()) {
            throw new IllegalStateException("No valid bands found.");
        }

        ProductCharacteristics productCharacteristics = l1bMetadataHeader.getProductCharacteristics();
        String productType = "S2_MSI_" + productCharacteristics.getProcessingLevel();
        ProductSubsetDef subsetDef = getSubsetDef();
        Product product;
        if (sceneDescription == null) {
            product = new Product(l1bMetadataHeader.getProductMetadataPath().getFileName().toString(), productType);
        } else {
            Dimension defaultProductSize = new Dimension(sceneDescription.getSceneRectangle().width, sceneDescription.getSceneRectangle().height);
            Rectangle productBounds = ImageUtils.computeProductBounds(defaultProductSize.width, defaultProductSize.height, subsetDef);

            product = new Product(defaultProductName, productType, productBounds.width, productBounds.height);
            product.setPreferredTileSize(S2Config.DEFAULT_JAI_TILE_SIZE, S2Config.DEFAULT_JAI_TILE_SIZE);
            TileLayout productTileLayout = config.getTileLayout(getProductResolution());
            product.setNumResolutionsMax(productTileLayout.numResolutions);
            product.setAutoGrouping("D01:D02:D03:D04:D05:D06:D07:D08:D09:D10:D11:D12");

            Map<String, GeoCoding> geoCodingsByDetector = new HashMap<>();
            Map<String, Tile> tilesById = new HashMap<>(tileList.size());
            for (Tile tile : tileList) {
                tilesById.put(tile.getId(), tile);
            }
            for (L1BBandInfo tbi : bandInfoByKey.values()) {
                if (!geoCodingsByDetector.containsKey(tbi.getDetectorId())) {
                    TiePointGeoCoding tiePointGeoCoding = buildGeoCodingFromTileBandInfo(tbi, tilesById);
                    product.addTiePointGrid(tiePointGeoCoding.getLatGrid());
                    product.addTiePointGrid(tiePointGeoCoding.getLonGrid());
                    geoCodingsByDetector.put(tbi.getDetectorId(), tiePointGeoCoding);
                }
            }

            AffineTransform imageToModelTransform = Product.findImageToModelTransform(product.getSceneGeoCoding());
            addDetectorBands(defaultProductSize, productBounds, product, bandInfoByKey, sceneDescription, imageToModelTransform, productTileLayout);

            // add TileIndex if there are more than 1 tile
            if (sceneDescription.getOrderedTileIds().size() > 1 && !bandInfoByKey.isEmpty()) {
                List<S2SpatialResolution> resolutions = computeResolutions(this.interpretation);
                if (!(resolutions.isEmpty() || tileList.isEmpty())) {
                    addTileIndexes(defaultProductSize, productBounds, product, resolutions, tileList, sceneDescription, sceneDimensions, imageToModelTransform, config);
                }
            }
        }

        if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
            for (MetadataElement metadataElement : l1bMetadataHeader.getMetadataElements()) {
                product.getMetadataRoot().addElement(metadataElement);
            }
        }

        product.setStartTime(parseDate(productCharacteristics.getProductStartTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        product.setEndTime(parseDate(productCharacteristics.getProductStopTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        return product;
    }

    private void addDetectorBands(Dimension defaultProductSize, Rectangle productBounds, Product product, Map<String, L1BBandInfo> bandInfoByKey,
                                  L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform, TileLayout productTileLayout) {

        S2SpatialResolution productResolution = getProductResolution();
        List<String> bandIndexes = new ArrayList<>(bandInfoByKey.keySet());
        Collections.sort(bandIndexes);
        ProductSubsetDef subsetDef = getSubsetDef();

        for (String bandIndex : bandIndexes) {
            L1BBandInfo tileBandInfo = bandInfoByKey.get(bandIndex);
            if (isMultiResolution() || tileBandInfo.getBandInformation().getResolution() == productResolution) {
                if (subsetDef == null || subsetDef.isNodeAccepted(tileBandInfo.getBandName())) {
                    MosaicMatrix mosaicMatrix = buildBandMatrix(sceneDescription.getMatrixTileIds(tileBandInfo), sceneDescription, tileBandInfo);
                    int defaultBandWidth = mosaicMatrix.computeTotalWidth();
                    int defaultBandHeight = mosaicMatrix.computeTotalHeight();
                    TileLayout thisBandTileLayout = tileBandInfo.getImageLayout();
                    validateBandSize(defaultBandWidth, defaultBandHeight, thisBandTileLayout, defaultProductSize, productTileLayout);

                    Rectangle bandBounds = ImageUtils.computeBandBoundsBasedOnPercent(productBounds, defaultProductSize.width, defaultProductSize.height, defaultBandWidth, defaultBandHeight);

                    Band band = buildBand(tileBandInfo, bandBounds.width, bandBounds.height);
                    band.setDescription(tileBandInfo.getBandInformation().getDescription());

                    BandL1bSceneMultiLevelSource multiLevelSource = new BandL1bSceneMultiLevelSource(thisBandTileLayout.numResolutions, mosaicMatrix, bandBounds, imageToModelTransform);
                    band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource));

                    product.addBand(band);
                }
            }
        }
    }

    private void addTileIndexes(Dimension defaultProductSize, Rectangle productBounds, Product product, List<S2SpatialResolution> resolutions,
                                List<L1bMetadata.Tile> tileList, L1bSceneDescription sceneDescription, Map<S2SpatialResolution, Dimension> sceneDimensions,
                                AffineTransform imageToModelTransform, S2Config config) {

        List<L1BBandInfo> tileInfoList = computeTileIndexesList(resolutions, tileList, sceneDescription, config);
        if (tileInfoList.size() > 0) {
            ProductSubsetDef subsetDef = getSubsetDef();

            // add the index bands
            S2SpatialResolution productResolution = getProductResolution();
            TileLayout productTileLayout = config.getTileLayout(productResolution);
            for (L1BBandInfo bandInfo : tileInfoList) {
                if (isMultiResolution() || bandInfo.getBandInformation().getResolution() == productResolution) {
                    if (subsetDef == null || subsetDef.isNodeAccepted(bandInfo.getBandInformation().getPhysicalBand())) {
                        Band band = buildIndexBand(defaultProductSize, productBounds, bandInfo, sceneDescription, imageToModelTransform, productTileLayout);
                        product.addBand(band);
                    }
                }
            }

            // add the index masks
            for (L1BBandInfo bandInfo : tileInfoList) {
                if (bandInfo.getBandInformation() instanceof S2IndexBandInformation) {
                    S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) bandInfo.getBandInformation();
                    IndexCoding indexCoding = indexBandInformation.getIndexCoding();
                    product.getIndexCodingGroup().add(indexCoding);

                    List<Color> colors = indexBandInformation.getColors();
                    Iterator<Color> colorIterator = colors.iterator();

                    for (String indexName : indexCoding.getIndexNames()) {
                        String maskName = indexBandInformation.getPrefix() + indexName.toLowerCase();
                        if (subsetDef == null || subsetDef.isNodeAccepted(maskName)) {
                            int indexValue = indexCoding.getIndexValue(indexName);
                            String description = indexCoding.getIndex(indexName).getDescription();
                            if (!colorIterator.hasNext()) {
                                // we should never be here : programming error.
                                throw new IllegalStateException(String.format("Unexpected error when creating index masks : colors list does not have the same size as index coding"));
                            }
                            Color color = colorIterator.next();
                            int width = sceneDimensions.get(bandInfo.getBandInformation().getResolution()).width;
                            int height = sceneDimensions.get(bandInfo.getBandInformation().getResolution()).height;
                            Mask mask = Mask.BandMathsType.create(maskName, description, width, height, String.format("%s.raw == %d", indexBandInformation.getPhysicalBand(), indexValue), color, 0.5);
                            product.addMask(mask);
                        }
                    }
                }
            }
        }
    }

    public static class L1BBandInfo extends BandInfo {

        private final String detectorId;

        public L1BBandInfo(Map<String, VirtualPath> tileIdToPathMap, String detector, S2BandInformation spectralInfo, TileLayout imageLayout) {
            super(tileIdToPathMap, spectralInfo, imageLayout);

            this.detectorId = detector == null ? "" : detector;
        }

        public String getDetectorId() {
            return detectorId;
        }

        public String getBandName() {
            return String.format("%s%s", getDetectorId(), getBandInformation().getPhysicalBand());
        }
    }

    private static Band buildIndexBand(Dimension defaultProductSize, Rectangle productBounds, L1BBandInfo bandInfo,
                                       L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform, TileLayout productTileLayout) {

        MosaicMatrix mosaicMatrix = buildIndexBandMatrix(sceneDescription.getMatrixTileIds(bandInfo), sceneDescription, bandInfo);
        int defaultBandWidth = mosaicMatrix.computeTotalWidth();
        int defaultBandHeight = mosaicMatrix.computeTotalHeight();
        TileLayout thisBandTileLayout = bandInfo.getImageLayout();
        validateBandSize(defaultBandWidth, defaultBandHeight, thisBandTileLayout, defaultProductSize, productTileLayout);

        Rectangle bandBounds = ImageUtils.computeBandBoundsBasedOnPercent(productBounds, defaultProductSize.width, defaultProductSize.height, defaultBandWidth, defaultBandHeight);

        S2BandInformation bandInformation = bandInfo.getBandInformation();
        Band band = new Band(bandInformation.getPhysicalBand(), ProductData.TYPE_INT16, bandBounds.width, bandBounds.height);
        band.setScalingFactor(bandInformation.getScalingFactor());
        S2IndexBandInformation indexBandInfo = (S2IndexBandInformation) bandInformation;
        band.setSpectralWavelength(0);
        band.setSpectralBandwidth(0);
        band.setSpectralBandIndex(-1);
        band.setSampleCoding(indexBandInfo.getIndexCoding());
        band.setImageInfo(indexBandInfo.getImageInfo());
        band.setDescription(bandInfo.getBandInformation().getDescription());
        band.setValidPixelExpression(String.format("%s.raw > 0",bandInfo.getBandInformation().getPhysicalBand()));

        TileIndexMultiLevelSource copyOfTileIndexMultiLevelSource = new TileIndexMultiLevelSource(thisBandTileLayout.numResolutions, mosaicMatrix, bandBounds, imageToModelTransform);
        band.setSourceImage(new DefaultMultiLevelImage(copyOfTileIndexMultiLevelSource));

        return band;
    }

    private static TiePointGrid buildTiePointGrid(int width, int height, String gridName, float[] tiePoints) {
        return buildTiePointGrid(gridName, 2, 2, 0, 0, width, height, tiePoints);
    }

    /**
     * Uses the 4 lat-lon corners of a detector to create the geocoding
     */
    private static TiePointGeoCoding buildGeoCodingFromTileBandInfo(L1BBandInfo tileBandInfo, Map<String, Tile> tileList) {
        Objects.requireNonNull(tileBandInfo);
        Objects.requireNonNull(tileList);

        Set<String> ourTileIds = tileBandInfo.getTileIdToPathMap().keySet();
        List<Tile> aList = new ArrayList<>(ourTileIds.size());
        List<Coordinate> coords = new ArrayList<>();
        for (String tileId : ourTileIds) {
            Tile currentTile = tileList.get(tileId);
            aList.add(currentTile);
        }

        // sort tiles by position
        Collections.sort(aList, (Tile u1, Tile u2) -> u1.getTileGeometry(S2SpatialResolution.R10M).getPosition().compareTo(u2.getTileGeometry(S2SpatialResolution.R10M).getPosition()));

        coords.add(aList.get(0).corners.get(0));
        coords.add(aList.get(0).corners.get(3));
        coords.add(aList.get(aList.size() - 1).corners.get(1));
        coords.add(aList.get(aList.size() - 1).corners.get(2));

        float[] lats = convertDoublesToFloats(getLatitudes(coords));
        float[] lons = convertDoublesToFloats(getLongitudes(coords));

        TiePointGrid latGrid = buildTiePointGrid(aList.get(0).getTileGeometry(S2SpatialResolution.R10M).getNumCols(), aList.get(0).getTileGeometry(S2SpatialResolution.R10M).getNumRowsDetector(),
                tileBandInfo.getDetectorId() + tileBandInfo.getBandInformation().getPhysicalBand() + ",latitude", lats);

        TiePointGrid lonGrid = buildTiePointGrid(aList.get(0).getTileGeometry(S2SpatialResolution.R10M).getNumCols(), aList.get(0).getTileGeometry(S2SpatialResolution.R10M).getNumRowsDetector(),
                tileBandInfo.getDetectorId() + tileBandInfo.getBandInformation().getPhysicalBand() + ",longitude", lons);

        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    private static void validateBandSize(int defaultBandWidth, int defaultBandHeight, TileLayout thisBandTileLayout, Dimension defaultProductSize, TileLayout productTileLayout) {
        float factorX = (float) productTileLayout.width / thisBandTileLayout.width;
        int nativeBandWidth = Math.round(defaultProductSize.width / factorX);
        if (nativeBandWidth != defaultBandWidth) {
            throw new IllegalStateException("Invalid band width: nativeBandWidth="+nativeBandWidth+", defaultBandWidth="+defaultBandWidth);
        }

        float factorY = (float) productTileLayout.height / thisBandTileLayout.height;
        int nativeBandHeight = Math.round(defaultProductSize.height / factorY);
        if (nativeBandHeight != defaultBandHeight) {
            throw new IllegalStateException("Invalid band height: nativeBandHeight="+nativeBandHeight+", defaultBandHeight="+defaultBandHeight);
        }
    }

    public static List<L1BBandInfo> computeTileIndexesList(List<S2SpatialResolution> resolutions, List<L1bMetadata.Tile> tileList, L1bSceneDescription sceneDescription, S2Config config) {
        //for each resolution, add the tile information
        //Set of detectors
        TreeSet<String> detectors = new TreeSet<>();
        for (String tileId : sceneDescription.getOrderedTileIds()) {
            String detectorId = ((S2L1BGranuleDirFilename) S2L1BGranuleDirFilename.create(tileId)).getDetectorId();
            if (!detectors.contains(detectorId)) {
                detectors.add(detectorId);
            }
        }

        List<S2IndexBandInformation> listTileIndexBandInformation = new ArrayList<>();
        for (String detector : detectors) {
            for (S2SpatialResolution res : S2SpatialResolution.values()) {
                if (resolutions.contains(res)) {
                    listTileIndexBandInformation.add(makeTileInformation(detector, res, sceneDescription));
                }
            }
        }

        // create BandInfo and add to tileInfoList
        List<L1BBandInfo> tileInfoList = new ArrayList<>();
        for (S2BandInformation bandInformation : listTileIndexBandInformation) {
            String detector = bandInformation.getPhysicalBand().substring(0, bandInformation.getPhysicalBand().indexOf("_"));
            HashMap<String, VirtualPath> tilePathMap = new HashMap<>();
            for (L1bMetadata.Tile tile : tileList) {
                if (("D" + tile.getDetectorId()).equals(detector)) {
                    tilePathMap.put(tile.getId(), null); //it is not necessary any file
                }
            }
            if (!tilePathMap.isEmpty()) {
                String bandDetector = bandInformation.getPhysicalBand().substring(0, bandInformation.getPhysicalBand().indexOf("_"));
                L1BBandInfo tileInfo = L1bMetadata.createBandInfoFromHeaderInfo(bandDetector, bandInformation, tilePathMap, config);
                if (tileInfo != null) {
                    tileInfoList.add(tileInfo);
                }
            }
        }

        return tileInfoList;
    }

    public static List<S2SpatialResolution> computeResolutions(ProductInterpretation interpretation) {
        List<S2SpatialResolution> resolutions = new ArrayList<>();
        // look for the resolutions used in bandInfoList for generating the tile index only for them
        if (interpretation == ProductInterpretation.RESOLUTION_10M || interpretation == ProductInterpretation.RESOLUTION_MULTI) {
            resolutions.add(S2SpatialResolution.R10M);
        }
        if (interpretation == ProductInterpretation.RESOLUTION_20M || interpretation == ProductInterpretation.RESOLUTION_MULTI) {
            resolutions.add(S2SpatialResolution.R20M);
        }
        if (interpretation == ProductInterpretation.RESOLUTION_60M || interpretation == ProductInterpretation.RESOLUTION_MULTI) {
            resolutions.add(S2SpatialResolution.R60M);
        }
        return resolutions;
    }
}
