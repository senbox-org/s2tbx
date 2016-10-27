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
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import com.vividsolutions.jts.geom.Coordinate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.math3.util.Pair;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2TileOpImage;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleImageFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleMetadataFilename;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.io.FileUtils;
import org.xml.sax.SAXException;

import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.esa.s2tbx.dataio.openjpeg.OpenJpegUtils.validateOpenJpegExecutables;
import static org.esa.s2tbx.dataio.s2.S2Metadata.ProductCharacteristics;
import static org.esa.s2tbx.dataio.s2.S2Metadata.Tile;
import static org.esa.s2tbx.dataio.s2.l1b.CoordinateUtils.convertDoublesToFloats;
import static org.esa.s2tbx.dataio.s2.l1b.CoordinateUtils.getLatitudes;
import static org.esa.s2tbx.dataio.s2.l1b.CoordinateUtils.getLongitudes;
import static org.esa.s2tbx.dataio.s2.l1b.L1bMetadata.parseHeader;
import static org.esa.s2tbx.dataio.s2.l1b.L1bMetadataProc.makeTileInformation;
import static org.esa.snap.utils.DateHelper.parseDate;

// import com.jcabi.aspects.Loggable;

// import org.github.jamm.MemoryMeter;

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

    protected final Logger logger;
    protected final ProductInterpretation interpretation;

    public Sentinel2L1BProductReader(ProductReaderPlugIn readerPlugIn, ProductInterpretation interpretation) {
        super(readerPlugIn);
        this.interpretation = interpretation;
        logger = SystemUtils.LOG;
    }

    public ProductInterpretation getInterpretation() {
        return interpretation;
    }

    @Override
    public S2SpatialResolution getProductResolution() {
        if(interpretation == ProductInterpretation.RESOLUTION_20M) return S2SpatialResolution.R20M;
        if(interpretation == ProductInterpretation.RESOLUTION_60M) return S2SpatialResolution.R60M;
        return S2SpatialResolution.R10M;
    }

    @Override
    public boolean isMultiResolution() {
        return interpretation == ProductInterpretation.RESOLUTION_MULTI;
    }

    @Override
    protected String getReaderCacheDir() {
        return L1B_CACHE_DIR;
    }


    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

    private TiePointGrid addTiePointGrid(int width, int height, String gridName, float[] tiePoints) {
        return createTiePointGrid(gridName, 2, 2, 0, 0, width, height, tiePoints);
    }

    @Override
    protected Product getMosaicProduct(File metadataFile) throws IOException {

        if(!validateOpenJpegExecutables(S2Config.OPJ_INFO_EXE,S2Config.OPJ_DECOMPRESSOR_EXE)){
            throw new IOException("Invalid OpenJpeg executables");
        }

        boolean isAGranule = (namingConvention.getInputType() == S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA);
        boolean foundProductMetadata = true;

        if (isAGranule) {
            logger.fine("Reading a granule");
        }

        if (!updateTileLayout(metadataFile.toPath(), isAGranule)) {
            throw new IOException(String.format("Unable to retrieve the JPEG tile layout associated to product [%s]", metadataFile.getName()));
        }

        Objects.requireNonNull(metadataFile);

        String filterTileId = null;
        File productMetadataFile = null;
        String granuleDirName = null;

        // we need to recover parent metadata file if we have a granule
        if (isAGranule) {

            try {
                Objects.requireNonNull(metadataFile.getParentFile());
                granuleDirName = metadataFile.getParentFile().getName();
                File tileIdFilter = metadataFile.getParentFile();
                filterTileId = tileIdFilter.getName();
            }   catch (NullPointerException npe){
                throw new IOException(String.format("Unable to retrieve the product associated to granule metadata file [%s]", metadataFile.getName()));
            }

            Path rootMetadataPath = namingConvention.getInputProductXml();
            if(rootMetadataPath != null) {
                productMetadataFile = rootMetadataPath.toFile();
            }
            if (productMetadataFile == null) {
                foundProductMetadata = false;
                productMetadataFile = metadataFile;
            }
        } else {
            productMetadataFile = metadataFile;
        }

        final String aFilter = filterTileId;

        L1bMetadata metadataHeader;

        try {
            metadataHeader = parseHeader(productMetadataFile, granuleDirName, getConfig(),!foundProductMetadata, namingConvention);
        } catch (ParserConfigurationException | SAXException e) {
            SystemUtils.LOG.severe(Utils.getStackTrace(e));
            throw new IOException("Failed to parse metadata in " + productMetadataFile.getName());
        }

        L1bSceneDescription sceneDescription = L1bSceneDescription.create(metadataHeader, getProductResolution());
        Map<S2SpatialResolution,Dimension> sceneDimensions = L1bSceneDescription.computeSceneDimensions(metadataHeader);
        logger.fine("Scene Description: " + sceneDescription);

        File productDir = getProductDir(productMetadataFile);
        initCacheDir(productDir);

        ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();

        List<L1bMetadata.Tile> tileList = metadataHeader.getTileList();

        if (isAGranule) {
            tileList = metadataHeader.getTileList().stream().filter(p -> p.getId().equalsIgnoreCase(aFilter)).collect(Collectors.toList());
        }

        Map<String, Tile> tilesById = new HashMap<>(tileList.size());
        for (Tile aTile : tileList) {
            tilesById.put(aTile.getId(), aTile);
        }

        // Order bands by physicalBand
        Map<String, S2BandInformation> sin = new HashMap<>();
        for (S2BandInformation bandInformation : productCharacteristics.getBandInformations()) {
            sin.put(bandInformation.getPhysicalBand(), bandInformation);
        }

        Map<Pair<String, String>, Map<String, File>> detectorBandInfoMap = new HashMap<>();
        Map<String, L1BBandInfo> bandInfoByKey = new HashMap<>();
        if (productCharacteristics.getBandInformations() != null) {
            for (Tile tile : tileList) {
                S2L1BGranuleDirFilename gf = (S2L1BGranuleDirFilename) S2L1BGranuleDirFilename.create(tile.getId());
                Guardian.assertNotNull("Product files don't match regular expressions", gf);

                for(S2BandInformation bandInformation : productCharacteristics.getBandInformations()) {
                    String imgFilename;
                    if (foundProductMetadata) {
                        imgFilename = String.format("GRANULE%s%s%s%s", File.separator, metadataHeader.resolveResource(tile.getId()).getFileName().toString(),
                                                    File.separator,
                                                    bandInformation.getImageFileTemplate()
                                                            .replace("{{TILENUMBER}}", gf.getTileID())
                                                            .replace("{{MISSION_ID}}", gf.missionID)
                                                            .replace("{{SITECENTRE}}", gf.siteCentre)
                                                            .replace("{{CREATIONDATE}}", gf.creationDate)
                                                            .replace("{{STARTDATE}}", gf.startDate)
                                                            .replace("{{DETECTOR}}", gf.detectorId)
                                                            .replace("{{RESOLUTION}}", String.format("%d", bandInformation.getResolution().resolution)));

                    } else {
                        imgFilename = bandInformation.getImageFileTemplate()
                                .replace("{{TILENUMBER}}", gf.getTileID())
                                .replace("{{MISSION_ID}}", gf.missionID)
                                .replace("{{SITECENTRE}}", gf.siteCentre)
                                .replace("{{CREATIONDATE}}", gf.creationDate)
                                .replace("{{STARTDATE}}", gf.startDate)
                                .replace("{{DETECTOR}}", gf.detectorId)
                                .replace("{{RESOLUTION}}", String.format("%d", bandInformation.getResolution().resolution));

                    }
                    logger.finer("Adding file " + imgFilename + " to band: " + bandInformation.getPhysicalBand() + ", and detector: " + gf.getDetectorId());

                    File file = new File(productDir, imgFilename);
                    if (file.exists()) {
                        Pair<String, String> key = new Pair<>(bandInformation.getPhysicalBand(), gf.getDetectorId());
                        Map<String, File> fileMapper = detectorBandInfoMap.getOrDefault(key, new HashMap<>());
                        fileMapper.put(tile.getId(), file);
                        if (!detectorBandInfoMap.containsKey(key)) {
                            detectorBandInfoMap.put(key, fileMapper);
                        }
                    } else {
                        logger.warning(String.format("Warning: missing file %s\n", file));
                    }

                }
            }

            if (!detectorBandInfoMap.isEmpty()) {
                for (Pair<String, String> key : detectorBandInfoMap.keySet()) {
                    L1BBandInfo tileBandInfo = createBandInfoFromHeaderInfo(
                            key.getSecond(), sin.get(key.getFirst()), detectorBandInfoMap.get(key));

                    // composite band name : detector + band
                    String keyMix = key.getSecond() + key.getFirst();
                    bandInfoByKey.put(keyMix, tileBandInfo);
                }
            }
        } else {
            // fixme Look for optional info in schema
            logger.warning("There are no spectral information here !");
        }

        Product product;

        if (sceneDescription != null) {
            product = new Product(namingConvention.getProductName(),
                                  "S2_MSI_" + productCharacteristics.getProcessingLevel(),
                                  sceneDescription.getSceneRectangle().width,
                                  sceneDescription.getSceneRectangle().height);


            Map<String, GeoCoding> geoCodingsByDetector = new HashMap<>();

            if (!bandInfoByKey.isEmpty()) {
                for (L1BBandInfo tbi : bandInfoByKey.values()) {
                    if (!geoCodingsByDetector.containsKey(tbi.detectorId)) {
                        GeoCoding gc = getGeoCodingFromTileBandInfo(tbi, tilesById, product);
                        geoCodingsByDetector.put(tbi.detectorId, gc);
                    }
                }
            }

            addDetectorBands(product, bandInfoByKey,
                             new L1bSceneMultiLevelImageFactory(sceneDescription, Product.findImageToModelTransform(product.getSceneGeoCoding())));

            //add TileIndex if there are more than 1 tile
            if(sceneDescription.getOrderedTileIds().size()>1 && !bandInfoByKey.isEmpty()) {
                ArrayList<S2SpatialResolution> resolutions = new ArrayList<>();
                //look for the resolutions used in bandInfoList for generating the tile index only for them
                if(interpretation == ProductInterpretation.RESOLUTION_10M || interpretation == ProductInterpretation.RESOLUTION_MULTI) {
                    resolutions.add(S2SpatialResolution.R10M);
                }
                if(interpretation == ProductInterpretation.RESOLUTION_20M || interpretation == ProductInterpretation.RESOLUTION_MULTI) {
                    resolutions.add(S2SpatialResolution.R20M);
                }
                if(interpretation == ProductInterpretation.RESOLUTION_60M || interpretation == ProductInterpretation.RESOLUTION_MULTI) {
                    resolutions.add(S2SpatialResolution.R60M);
                }

                addTileIndexes(product, resolutions, tileList, sceneDescription,sceneDimensions);

            }

        } else {
            product = new Product(FileUtils.getFilenameWithoutExtension(productMetadataFile),
                                  "S2_MSI_" + productCharacteristics.getProcessingLevel());
        }

        product.setFileLocation(productMetadataFile.getParentFile());

        for (MetadataElement metadataElement : metadataHeader.getMetadataElements()) {
            product.getMetadataRoot().addElement(metadataElement);
        }

        product.setStartTime(parseDate(productCharacteristics.getProductStartTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        product.setEndTime(parseDate(productCharacteristics.getProductStopTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        return product;
    }

    /**
     * Uses the 4 lat-lon corners of a detector to create the geocoding
     */
    private GeoCoding getGeoCodingFromTileBandInfo(L1BBandInfo tileBandInfo, Map<String, Tile> tileList, Product product) {
        Objects.requireNonNull(tileBandInfo);
        Objects.requireNonNull(tileList);
        Objects.requireNonNull(product);

        Set<String> ourTileIds = tileBandInfo.getTileIdToFileMap().keySet();
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

        TiePointGrid latGrid = addTiePointGrid(aList.get(0).getTileGeometry(S2SpatialResolution.R10M).getNumCols(), aList.get(0).getTileGeometry(S2SpatialResolution.R10M).getNumRowsDetector(), tileBandInfo.getDetectorId() + tileBandInfo.getBandInformation().getPhysicalBand() + ",latitude", lats);
        product.addTiePointGrid(latGrid);
        TiePointGrid lonGrid = addTiePointGrid(aList.get(0).getTileGeometry(S2SpatialResolution.R10M).getNumCols(), aList.get(0).getTileGeometry(S2SpatialResolution.R10M).getNumRowsDetector(), tileBandInfo.getDetectorId() + tileBandInfo.getBandInformation().getPhysicalBand() + ",longitude", lons);
        product.addTiePointGrid(lonGrid);

        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    private void addDetectorBands(Product product, Map<String, L1BBandInfo> stringBandInfoMap, MultiLevelImageFactory mlif) throws IOException {
        product.setPreferredTileSize(S2Config.DEFAULT_JAI_TILE_SIZE, S2Config.DEFAULT_JAI_TILE_SIZE);
        product.setNumResolutionsMax(getConfig().getTileLayout(getProductResolution().resolution).numResolutions);

        product.setAutoGrouping("D01:D02:D03:D04:D05:D06:D07:D08:D09:D10:D11:D12");

        ArrayList<String> bandIndexes = new ArrayList<>(stringBandInfoMap.keySet());
        Collections.sort(bandIndexes);

        if (bandIndexes.isEmpty()) {
            throw new IOException("No valid bands found.");
        }

        for (String bandIndex : bandIndexes) {
            L1BBandInfo tileBandInfo = stringBandInfoMap.get(bandIndex);
            if (isMultiResolution() || tileBandInfo.getBandInformation().getResolution() == this.getProductResolution()) {
                TileLayout thisBandTileLayout = tileBandInfo.getImageLayout();
                TileLayout productTileLayout = getConfig().getTileLayout(getProductResolution());

                float factorX = (float) productTileLayout.width / thisBandTileLayout.width;
                float factorY = (float) productTileLayout.height / thisBandTileLayout.height;

                Dimension dimension = new Dimension(Math.round(product.getSceneRasterWidth() / factorX), Math.round(product.getSceneRasterHeight() / factorY));

                Band band = addBand(product, tileBandInfo, dimension);
                band.setDescription(tileBandInfo.getBandInformation().getDescription());
                band.setSourceImage(mlif.createSourceImage(tileBandInfo));
            }
        }
    }

    private L1BBandInfo createBandInfoFromHeaderInfo(String detector, S2BandInformation bandInformation, Map<String, File> tileFileMap) {
        S2SpatialResolution spatialResolution = bandInformation.getResolution();
        return new L1BBandInfo(tileFileMap,
                               detector,
                               bandInformation,
                               getConfig().getTileLayout(spatialResolution.resolution));
    }

    static File getProductDir(File productFile) throws IOException {
        final File resolvedFile = productFile.getCanonicalFile();
        if (!resolvedFile.exists()) {
            throw new FileNotFoundException("File not found: " + productFile);
        }

        if (productFile.getParentFile() == null) {
            return new File(".").getCanonicalFile();
        }

        return productFile.getParentFile();
    }

    private void addTileIndexes(Product product, ArrayList<S2SpatialResolution> resolutions, List<L1bMetadata.Tile> tileList, L1bSceneDescription sceneDescription, Map<S2SpatialResolution,Dimension> sceneDimensions) {

        if(resolutions.isEmpty() || tileList.isEmpty()) {
            return;
        }

        List<BandInfo> tileInfoList = new ArrayList<>();
        ArrayList<S2IndexBandInformation> listTileIndexBandInformation = new ArrayList<>();

        //for each resolution, add the tile information
        //Set of detectors
        TreeSet<String> detectors = new TreeSet<>();
        for(String tileId : sceneDescription.getOrderedTileIds()) {
            String detectorId = ((S2L1BGranuleDirFilename) S2L1BGranuleDirFilename.create(tileId)).getDetectorId();
            if (!detectors.contains(detectorId)) {
                detectors.add(detectorId);
            }
        }


        for (String detector : detectors) {
            for (S2SpatialResolution res : S2SpatialResolution.values()) {
                if(resolutions.contains(res)) {
                    listTileIndexBandInformation.add(makeTileInformation(detector, res, sceneDescription));
                }
            }
        }


        // Create BandInfo and add to tileInfoList
        for (S2BandInformation bandInformation : listTileIndexBandInformation) {
            String detector = bandInformation.getPhysicalBand().substring(0,bandInformation.getPhysicalBand().indexOf("_"));
            HashMap<String, File> tileFileMap = new HashMap<>();
            for (L1bMetadata.Tile tile : tileList) {
                if(("D" + tile.getDetectorId()).equals(detector)) {
                    tileFileMap.put(tile.getId(), null); //it is not necessary any file
                }
            }

            if (!tileFileMap.isEmpty()) {
                BandInfo tileInfo = createBandInfoFromHeaderInfo(bandInformation.getPhysicalBand().substring(0,bandInformation.getPhysicalBand().indexOf("_")),bandInformation, tileFileMap);
                if(tileInfo != null) {
                    tileInfoList.add(tileInfo);
                }
            }
        }

        if(tileInfoList.isEmpty()) {
            return;
        }


        //Add the bands
        for (BandInfo bandInfo : tileInfoList) {
            try {
                addTileIndex(product,
                             bandInfo, sceneDescription);
            } catch (Exception e) {
                logger.warning(String.format("It has not been possible to add tile id for resolution %s\n", bandInfo.getBandInformation().getResolution().toString()));
            }
        }

        //Add the index masks
        try {
            addIndexMasks(product, tileInfoList, sceneDimensions);
        } catch (IOException e) {

        }
    }

    private void addIndexMasks(Product product, List<BandInfo> bandInfoList, Map<S2SpatialResolution,Dimension> sceneDimensions) throws IOException {
        for (BandInfo bandInfo : bandInfoList) {
            if (bandInfo.getBandInformation() instanceof S2IndexBandInformation) {
                S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) bandInfo.getBandInformation();
                IndexCoding indexCoding = indexBandInformation.getIndexCoding();
                product.getIndexCodingGroup().add(indexCoding);


                List<Color> colors = indexBandInformation.getColors();
                Iterator<Color> colorIterator = colors.iterator();

                for (String indexName : indexCoding.getIndexNames()) {
                    int indexValue = indexCoding.getIndexValue(indexName);
                    String description = indexCoding.getIndex(indexName).getDescription();
                    if (!colorIterator.hasNext()) {
                        // we should never be here : programming error.
                        throw new IOException(String.format("Unexpected error when creating index masks : colors list does not have the same size as index coding"));
                    }
                    Color color = colorIterator.next();
                    Mask mask = Mask.BandMathsType.create(indexBandInformation.getPrefix() + indexName.toLowerCase(), description, sceneDimensions.get(bandInfo.getBandInformation().getResolution()).width, sceneDimensions.get(bandInfo.getBandInformation().getResolution()).height,
                                                          String.format("%s.raw == %d", indexBandInformation.getPhysicalBand(), indexValue), color, 0.5);
                    product.addMask(mask);
                }
            }
        }
    }

    private void addTileIndex(Product product, BandInfo bandInfo, L1bSceneDescription sceneDescription) throws IOException {
        product.setPreferredTileSize(S2Config.DEFAULT_JAI_TILE_SIZE, S2Config.DEFAULT_JAI_TILE_SIZE);
        product.setNumResolutionsMax(getConfig().getTileLayout(getProductResolution().resolution).numResolutions);

        product.setAutoGrouping("D01:D02:D03:D04:D05:D06:D07:D08:D09:D10:D11:D12");


        if (isMultiResolution() || bandInfo.getBandInformation().getResolution() == this.getProductResolution()) {
            TileLayout thisBandTileLayout = bandInfo.getImageLayout();
            TileLayout productTileLayout = getConfig().getTileLayout(getProductResolution());

            float factorX = (float) productTileLayout.width / thisBandTileLayout.width;
            float factorY = (float) productTileLayout.height / thisBandTileLayout.height;

            Dimension dimension = new Dimension(Math.round(product.getSceneRasterWidth() / factorX), Math.round(product.getSceneRasterHeight() / factorY));

            //Band band = addBand(product, bandInfo, dimension);
            Band band = new Band(
                    bandInfo.getBandInformation().getPhysicalBand(),
                    ProductData.TYPE_INT16,
                    dimension.width,
                    dimension.height
            );
            S2BandInformation bandInformation = bandInfo.getBandInformation();
            band.setScalingFactor(bandInformation.getScalingFactor());
            S2IndexBandInformation indexBandInfo = (S2IndexBandInformation) bandInformation;
            band.setSpectralWavelength(0);
            band.setSpectralBandwidth(0);
            band.setSpectralBandIndex(-1);
            band.setSampleCoding(indexBandInfo.getIndexCoding());
            band.setImageInfo(indexBandInfo.getImageInfo());
            band.setDescription(bandInfo.getBandInformation().getDescription());
            band.setValidPixelExpression(String.format("%s.raw > 0",bandInfo.getBandInformation().getPhysicalBand()));

            MultiLevelImageFactory mlif = new L1bTileIndexMultiLevelImageFactory(
                    sceneDescription,
                    Product.findImageToModelTransform(band.getGeoCoding()));

            band.setSourceImage(mlif.createSourceImage((L1BBandInfo) bandInfo));
            product.addBand(band);

        }

    }


    private abstract class MultiLevelImageFactory {
        protected final AffineTransform imageToModelTransform;

        protected MultiLevelImageFactory(AffineTransform imageToModelTransform) {
            this.imageToModelTransform = imageToModelTransform;
        }

        public abstract MultiLevelImage createSourceImage(L1BBandInfo tileBandInfo);
    }

    private class L1bSceneMultiLevelImageFactory extends MultiLevelImageFactory {

        private final L1bSceneDescription sceneDescription;

        public L1bSceneMultiLevelImageFactory(L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform) {
            super(imageToModelTransform);

            SystemUtils.LOG.fine("Model factory: " + ToStringBuilder.reflectionToString(imageToModelTransform));

            this.sceneDescription = sceneDescription;
        }

        @Override
        // @Loggable
        public MultiLevelImage createSourceImage(L1BBandInfo tileBandInfo) {
            BandL1bSceneMultiLevelSource bandScene = new BandL1bSceneMultiLevelSource(sceneDescription, tileBandInfo, imageToModelTransform);
            SystemUtils.LOG.log(Level.parse(S2Config.LOG_SCENE), "BandScene: " + bandScene);
            return new DefaultMultiLevelImage(bandScene);
        }
    }

    private class L1bTileIndexMultiLevelImageFactory extends MultiLevelImageFactory {

        private final L1bSceneDescription sceneDescription;

        public L1bTileIndexMultiLevelImageFactory(L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform) {
            super(imageToModelTransform);

            SystemUtils.LOG.fine("Model factory: " + ToStringBuilder.reflectionToString(imageToModelTransform));

            this.sceneDescription = sceneDescription;
        }

        @Override
        // @Loggable
        public MultiLevelImage createSourceImage(L1BBandInfo tileBandInfo) {
            TileIndexMultiLevelSource bandScene = new TileIndexMultiLevelSource(sceneDescription, tileBandInfo, imageToModelTransform);
            SystemUtils.LOG.log(Level.parse(S2Config.LOG_SCENE), "BandScene: " + bandScene);
            return new DefaultMultiLevelImage(bandScene);
        }
    }


    /**
     * A MultiLevelSource for a scene made of multiple L1C tiles.
     */
    private abstract class AbstractL1bSceneMultiLevelSource extends AbstractMultiLevelSource {
        protected final L1bSceneDescription sceneDescription;

        AbstractL1bSceneMultiLevelSource(L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform, int numResolutions) {
            super(new DefaultMultiLevelModel(numResolutions,
                                             imageToModelTransform,
                                             sceneDescription.getSceneRectangle().width,
                                             sceneDescription.getSceneRectangle().height));
            this.sceneDescription = sceneDescription;
        }
    }

    /**
     * A MultiLevelSource used by bands for a scene made of multiple L1C tiles.
     */
    private final class BandL1bSceneMultiLevelSource extends AbstractL1bSceneMultiLevelSource {
        private final L1BBandInfo tileBandInfo;

        public BandL1bSceneMultiLevelSource(L1bSceneDescription sceneDescription, L1BBandInfo tileBandInfo, AffineTransform imageToModelTransform) {
            super(sceneDescription, imageToModelTransform, tileBandInfo.getImageLayout().numResolutions);
            this.tileBandInfo = tileBandInfo;
        }

        protected PlanarImage createL1bTileImage(String tileId, int level) {
            File imageFile = tileBandInfo.getTileIdToFileMap().get(tileId);

            PlanarImage planarImage = S2TileOpImage.create(imageFile,
                                                           getCacheDir(),
                                                           null, // tileRectangle.getLocation(),
                                                           tileBandInfo.getImageLayout(),
                                                           getConfig(),
                                                           getModel(),
                                                           getProductResolution(),
                                                           level);

            logger.fine(String.format("Planar image model: %s", getModel().toString()));

            logger.fine(String.format("Planar image created: %s %s: minX=%d, minY=%d, width=%d, height=%d\n",
                                      tileBandInfo.getBandInformation().getPhysicalBand(), tileId,
                                      planarImage.getMinX(), planarImage.getMinY(),
                                      planarImage.getWidth(), planarImage.getHeight()));

            return planarImage;
        }

        @Override
        protected RenderedImage createImage(int level) {
            ArrayList<RenderedImage> tileImages = new ArrayList<>();

            List<String> tiles = sceneDescription.getTileIds().stream().filter(x -> x.contains(tileBandInfo.detectorId)).collect(Collectors.toList());


            TileLayout thisBandTileLayout = this.tileBandInfo.getImageLayout();
            TileLayout productTileLayout = getConfig().getTileLayout(getProductResolution());
            float layoutRatioX = (float) productTileLayout.width / thisBandTileLayout.width;
            float layoutRatioY = (float) productTileLayout.height / thisBandTileLayout.height;


            for (String tileId : tiles) {
                int tileIndex = sceneDescription.getTileIndex(tileId);
                Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);

                PlanarImage opImage = createL1bTileImage(tileId, level);
                {

                    double factorX = 1.0 / (Math.pow(2, level) * layoutRatioX);
                    double factorY = 1.0 / (Math.pow(2, level) * layoutRatioY);

                    opImage = TranslateDescriptor.create(opImage,
                                                         (float) Math.floor((tileRectangle.x * factorX)),
                                                         (float) Math.floor((tileRectangle.y * factorY)),
                                                         Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);

                    logger.log(Level.parse(S2Config.LOG_SCENE), String.format("Translate descriptor: %s", ToStringBuilder.reflectionToString(opImage)));
                }

                logger.log(Level.parse(S2Config.LOG_SCENE), String.format("opImage added for level %d at (%d,%d) with size (%d,%d)%n", level, opImage.getMinX(), opImage.getMinY(), opImage.getWidth(), opImage.getHeight()));
                tileImages.add(opImage);
            }

            if (tileImages.isEmpty()) {
                logger.warning("No tile images for mosaic");
                return null;
            }

            ImageLayout imageLayout = new ImageLayout();
            imageLayout.setMinX(0);
            imageLayout.setMinY(0);
            imageLayout.setTileWidth(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileHeight(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileGridXOffset(0);
            imageLayout.setTileGridYOffset(0);

            RenderedOp mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                                                          MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                          null, null, new double[][]{{1.0}}, new double[]{S2Config.FILL_CODE_MOSAIC_BG},
                                                          new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));


            int fitRectWidht = (int) (sceneDescription.getSceneEnvelope().getWidth() /
                    (layoutRatioX * getProductResolution().resolution));
            int fitRectHeight = (int) (sceneDescription.getSceneEnvelope().getHeight() /
                    (layoutRatioY * getProductResolution().resolution));

            Rectangle fitRect = new Rectangle(0, 0, fitRectWidht, fitRectHeight);
            final Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitRect, Math.pow(2.0, level));

            BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);

            if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
                int rightPad = destBounds.width - mosaicOp.getWidth();
                int bottomPad = destBounds.height - mosaicOp.getHeight();
                SystemUtils.LOG.log(Level.parse(S2Config.LOG_SCENE), String.format("Border: (%d, %d), (%d, %d)", mosaicOp.getWidth(), destBounds.width, mosaicOp.getHeight(), destBounds.height));

                mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, null);
            }

            logger.log(Level.parse(S2Config.LOG_SCENE), String.format("mosaicOp created for level %d at (%d,%d) with size (%d, %d)%n", level, mosaicOp.getMinX(), mosaicOp.getMinY(), mosaicOp.getWidth(), mosaicOp.getHeight()));

            return mosaicOp;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }


    private final class TileIndexMultiLevelSource extends AbstractL1bSceneMultiLevelSource {
        private final L1BBandInfo tileBandInfo;

        public TileIndexMultiLevelSource(L1bSceneDescription sceneDescription, L1BBandInfo tileBandInfo, AffineTransform imageToModelTransform) {
            super(sceneDescription, imageToModelTransform, tileBandInfo.getImageLayout().numResolutions);
            this.tileBandInfo = tileBandInfo;
        }

        protected PlanarImage createConstantTileImage(String tileId, int level) {

            S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) tileBandInfo.getBandInformation();
            IndexCoding indexCoding = indexBandInformation.getIndexCoding();
            Integer indexValue = indexCoding.getIndexValue(S2L1BGranuleDirFilename.create(tileId).getTileID());
            short indexValueShort = indexValue.shortValue();

            Rectangle tileRectangleL0 = new Rectangle();
            tileRectangleL0.height = tileBandInfo.getImageLayout().height;
            tileRectangleL0.width = tileBandInfo.getImageLayout().width;

            sceneDescription.getTileRectangle(sceneDescription.getTileIndex(tileId));

            Rectangle tileRectangle = DefaultMultiLevelSource.getLevelImageBounds(tileRectangleL0, getModel().getScale(level));
            PlanarImage planarImage = ConstantDescriptor.create((float) tileRectangle.width,
                                                                (float) tileRectangle.height,
                                                                new Short[]{indexValueShort},
                                                                null);

            return planarImage;
        }

        @Override
        protected RenderedImage createImage(int level) {
            ArrayList<RenderedImage> tileImages = new ArrayList<>();

            List<String> tiles = sceneDescription.getTileIds().stream().filter(x -> x.contains(tileBandInfo.detectorId)).collect(Collectors.toList());


            TileLayout thisBandTileLayout = this.tileBandInfo.getImageLayout();
            TileLayout productTileLayout = getConfig().getTileLayout(getProductResolution());
            float layoutRatioX = (float) productTileLayout.width / thisBandTileLayout.width;
            float layoutRatioY = (float) productTileLayout.height / thisBandTileLayout.height;


            for (String tileId : tiles) {
                int tileIndex = sceneDescription.getTileIndex(tileId);
                Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);

                PlanarImage opImage = createConstantTileImage(tileId, level);
                {

                    double factorX = 1.0 / (Math.pow(2, level) * layoutRatioX);
                    double factorY = 1.0 / (Math.pow(2, level) * layoutRatioY);

                    opImage = TranslateDescriptor.create(opImage,
                                                         (float) Math.floor((tileRectangle.x * factorX)),
                                                         (float) Math.floor((tileRectangle.y * factorY)),
                                                         Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);

                    logger.log(Level.parse(S2Config.LOG_SCENE), String.format("Translate descriptor: %s", ToStringBuilder.reflectionToString(opImage)));
                }

                logger.log(Level.parse(S2Config.LOG_SCENE), String.format("opImage added for level %d at (%d,%d) with size (%d,%d)%n", level, opImage.getMinX(), opImage.getMinY(), opImage.getWidth(), opImage.getHeight()));
                tileImages.add(opImage);
            }

            if (tileImages.isEmpty()) {
                logger.warning("No tile images for mosaic");
                return null;
            }

            ImageLayout imageLayout = new ImageLayout();
            imageLayout.setMinX(0);
            imageLayout.setMinY(0);
            imageLayout.setTileWidth(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileHeight(S2Config.DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileGridXOffset(0);
            imageLayout.setTileGridYOffset(0);

            RenderedOp mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                                                          MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                          null, null, new double[][]{{1.0}}, new double[]{S2Config.FILL_CODE_MOSAIC_BG},
                                                          new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));


            int fitRectWidht = (int) (sceneDescription.getSceneEnvelope().getWidth() /
                    (layoutRatioX * getProductResolution().resolution));
            int fitRectHeight = (int) (sceneDescription.getSceneEnvelope().getHeight() /
                    (layoutRatioY * getProductResolution().resolution));

            Rectangle fitRect = new Rectangle(0, 0, fitRectWidht, fitRectHeight);
            final Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitRect, Math.pow(2.0, level));

            BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);

            if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
                int rightPad = destBounds.width - mosaicOp.getWidth();
                int bottomPad = destBounds.height - mosaicOp.getHeight();
                SystemUtils.LOG.log(Level.parse(S2Config.LOG_SCENE), String.format("Border: (%d, %d), (%d, %d)", mosaicOp.getWidth(), destBounds.width, mosaicOp.getHeight(), destBounds.height));

                mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, null);
            }

            logger.log(Level.parse(S2Config.LOG_SCENE), String.format("mosaicOp created for level %d at (%d,%d) with size (%d, %d)%n", level, mosaicOp.getMinX(), mosaicOp.getMinY(), mosaicOp.getWidth(), mosaicOp.getHeight()));

            return mosaicOp;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    @Override
    protected String[] getBandNames(S2SpatialResolution resolution) {
        String[] bandNames;

        switch (resolution) {
            case R10M:
                bandNames = new String[]{"B02", "B03", "B04", "B08"};
                break;
            case R20M:
                bandNames = new String[]{"B05", "B06", "B07", "B8A", "B11", "B12"};
                break;
            case R60M:
                bandNames = new String[]{"B01", "B09", "B10"};
                break;
            default:
                SystemUtils.LOG.warning("Invalid resolution: " + resolution);
                bandNames = null;
                break;
        }

        return bandNames;
    }

    public static class L1BBandInfo extends BandInfo {

        private final String detectorId;

        L1BBandInfo(Map<String, File> tileIdToFileMap, String detector, S2BandInformation spectralInfo, TileLayout imageLayout) {
            super(tileIdToFileMap, spectralInfo, imageLayout);

            this.detectorId = detector == null ? "" : detector;
        }

        public String getDetectorId() {
            return detectorId;
        }

        public String getBandName() {
            return String.format("%s%s", getDetectorId(), getBandInformation().getPhysicalBand());
        }
    }
}
