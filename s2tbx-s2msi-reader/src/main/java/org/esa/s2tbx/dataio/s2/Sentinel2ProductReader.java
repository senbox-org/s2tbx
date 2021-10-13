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

package org.esa.s2tbx.dataio.s2;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.s2tbx.dataio.s2.metadata.AbstractS2MetadataReader;
import org.esa.s2tbx.dataio.s2.tiles.MosaicMatrixCellCallback;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.quicklooks.Quicklook;
import org.esa.snap.core.image.BandMatrixCell;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixCell;
import org.esa.snap.engine_utilities.util.Pair;
import org.esa.snap.jp2.reader.JP2ImageFile;
import org.esa.snap.jp2.reader.internal.JP2MosaicBandMatrixCell;
import org.esa.snap.lib.openjpeg.dataio.Utils;
import org.esa.snap.lib.openjpeg.jp2.TileLayout;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bc.ceres.core.ProgressMonitor;

import static org.esa.snap.lib.openjpeg.utils.OpenJpegUtils.validateOpenJpegExecutables;

/**
 * Base class for all Sentinel-2 product readers
 *
 * @author Nicolas Ducoin
 */
public abstract class Sentinel2ProductReader extends AbstractProductReader {

    protected static final Logger logger = Logger.getLogger(Sentinel2ProductReader.class.getName());

    private Path cacheDir;
    private VirtualPath virtualPath;

    protected Sentinel2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    public boolean isMultiResolution() {
        return true;
    }

    protected abstract Product readProduct(String defaultProductName, boolean isGranule, S2Metadata metadataHeader,
            INamingConvention namingConvention, ProductSubsetDef subsetDef) throws Exception;

    protected abstract String getReaderCacheDir();

    protected abstract AbstractS2MetadataReader buildMetadataReader(VirtualPath virtualPath) throws IOException;

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    protected void initCacheDir(VirtualPath productPath) throws IOException {
        Path versionFile = ResourceInstaller.findModuleCodeBasePath(getClass()).resolve("version/version.properties");
        Properties versionProp = new Properties();
        try (InputStream inputStream = Files.newInputStream(versionFile)) {
            versionProp.load(inputStream);
        }
        String version = versionProp.getProperty("project.version");
        if (version == null) {
            throw new IOException("Unable to get project.version property from " + versionFile);
        }
        String fullPathString = productPath.getFullPathString();
        String md5sum = Utils.getMD5sum(fullPathString);
        if (md5sum == null) {
            throw new IOException("Unable to get md5sum of path " + fullPathString);
        }
        String readerDirName = getReaderCacheDir();
        String productName = productPath.getFileName().toString();
        Path cacheFolderPath = SystemUtils.getCacheDir().toPath();
        cacheFolderPath = cacheFolderPath.resolve("s2tbx");
        cacheFolderPath = cacheFolderPath.resolve(readerDirName);
        cacheFolderPath = cacheFolderPath.resolve(version);
        cacheFolderPath = cacheFolderPath.resolve(md5sum);
        //cacheFolderPath = cacheFolderPath.resolve(productName);
        this.cacheDir = cacheFolderPath;
        if (!Files.exists(this.cacheDir)) {
            Files.createDirectories(this.cacheDir);
        }
        if (!Files.exists(this.cacheDir) || !Files.isDirectory(this.cacheDir) || !Files.isWritable(this.cacheDir)) {
            throw new IOException("Can't access package cache directory");
        }

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST,
                    "Successfully set up cache dir for product " + productName + " to " + this.cacheDir.toString());
        }
    }

    /**
     * @return the cacheDir
     */
    public Path getCacheDir() {
        return cacheDir;
    }
    
    @Override
    protected final Product readProductNodesImpl() throws IOException {
        if (!validateOpenJpegExecutables(S2Config.OPJ_INFO_EXE, S2Config.OPJ_DECOMPRESSOR_EXE)) {
            throw new IllegalStateException("Invalid OpenJpeg executables.");
        }

        boolean success = false;
        try {
            Object inputObject = super.getInput(); // invoke the 'getInput' method from the parent class
            ProductSubsetDef subsetDef = super.getSubsetDef(); // invoke the 'getSubsetDef' method from the parent class

            this.virtualPath = null;
            if (inputObject instanceof File) {
                File inputFile = (File) inputObject;
                Path inputPath = S2ProductNamingUtils.processInputPath(inputFile.toPath());
                this.virtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(inputPath);
            } else if (inputObject instanceof VirtualPath) {
                this.virtualPath = (VirtualPath) inputObject;
            } else if (inputObject instanceof Path) {
                Path inputPath = S2ProductNamingUtils.processInputPath((Path) inputObject);
                this.virtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(inputPath);
            } else {
                throw new IllegalArgumentException("Unknown input type '" + inputObject + "'.");
            }

            AbstractS2MetadataReader metadataReader = buildMetadataReader(this.virtualPath);

            Product product;
            VirtualPath inputVirtualPath = metadataReader.getNamingConvention().getInputXml();
            if (inputVirtualPath.exists()) {
                long startTime = System.currentTimeMillis();

                S2Config config = metadataReader.readTileLayouts(inputVirtualPath);

                if (logger.isLoggable(Level.FINE)) {
                    double elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.d;
                    logger.log(Level.FINE,
                            "Finish reading the tile layouts using the metadata file '"
                                    + inputVirtualPath.getFullPathString() + "', elapsed time: " + elapsedTimeInSeconds
                                    + " seconds.");
                }

                if (config == null) {
                    throw new NullPointerException(
                            String.format("Unable to retrieve the image tile layout associated to product [%s]",
                                    inputVirtualPath.getFileName().toString()));
                }

                startTime = System.currentTimeMillis();

                S2Metadata metadataHeader = metadataReader.readMetadataHeader(inputVirtualPath, config);

                if (logger.isLoggable(Level.FINE)) {
                    double elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.d;
                    logger.log(Level.FINE,
                            "Finish reading the header using the metadata file '" + inputVirtualPath.getFullPathString()
                                    + "', elapsed time: " + elapsedTimeInSeconds + " seconds.");
                }
                String defaultProductName = metadataReader.getNamingConvention().getProductName();
                product = readProduct(defaultProductName, metadataReader.isGranule(), metadataHeader,
                        metadataReader.getNamingConvention(), subsetDef);

                File productFileLocation;
                if (inputVirtualPath.getVirtualDir().isArchive()) {
                    productFileLocation = inputVirtualPath.getVirtualDir().getBaseFile();
                } else {
                    productFileLocation = inputVirtualPath.getFilePath().getPath().toFile();
                }
                product.setFileLocation(productFileLocation);

                Path qlFile = getQuickLookFile(inputVirtualPath);
                if (qlFile != null) {
                    product.getQuicklookGroup()
                            .add(new Quicklook(product, Quicklook.DEFAULT_QUICKLOOK_NAME, qlFile.toFile()));
                }
            } else {
                throw new FileNotFoundException(inputVirtualPath.getFullPathString());
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

    private void closeResources() {
        if (this.virtualPath != null) {
            this.virtualPath.close();
            this.virtualPath = null;
        }
        System.gc();
    }

    private Path getQuickLookFile(VirtualPath inputVirtualPath) throws IOException {
        VirtualPath parentPath = inputVirtualPath.getParent();
        if (parentPath != null) {
            String[] files = parentPath.list();
            if (files != null && files.length > 0) {
                for (String relativePath : files) {
                    if (relativePath.endsWith(".png")
                            && (relativePath.startsWith("S2") || relativePath.startsWith("BWI_"))) {
                        return inputVirtualPath.resolveSibling(relativePath).getLocalFile();
                    }
                }
            }
        }
        return null;
    }

    protected static int computeMatrixCellsDataBufferType(MosaicMatrix mosaicMatrix) {
        if (mosaicMatrix.getRowCount() > 0 && mosaicMatrix.getColumnCount() > 0) {
            BandMatrixCell firstMatrixCell = (BandMatrixCell) mosaicMatrix.getCellAt(0, 0);
            for (int rowIndex = 0; rowIndex < mosaicMatrix.getRowCount(); rowIndex++) {
                for (int columnIndex = 0; columnIndex < mosaicMatrix.getColumnCount(); columnIndex++) {
                    BandMatrixCell matrixCell = (BandMatrixCell) mosaicMatrix.getCellAt(rowIndex, columnIndex);
                    if (firstMatrixCell.getDataBufferType() != matrixCell.getDataBufferType()) {
                        throw new IllegalStateException("Different data buffer types: cell at " + rowIndex + ", "
                                + columnIndex + " has data type " + matrixCell.getDataBufferType() + " and cell at " + 0
                                + ", " + 0 + " has data type " + firstMatrixCell.getDataBufferType() + ".");
                    }
                }
            }
            return firstMatrixCell.getDataBufferType();
        } else {
            throw new IllegalArgumentException("The matrix is empty: rowCount=" + mosaicMatrix.getRowCount()
                    + ", columnCount=" + mosaicMatrix.getColumnCount() + ".");
        }
    }

    protected static int computeMatrixCellsResolutionCount(MosaicMatrix mosaicMatrix) {
        return computeMatrixCellsResolutionCount(mosaicMatrix, false);
    }

    protected static int computeMatrixCellsResolutionCount(MosaicMatrix mosaicMatrix, boolean geoTiffOption) {
        if (mosaicMatrix.getRowCount() > 0 && mosaicMatrix.getColumnCount() > 0) {
            if (geoTiffOption)
                return computeTiffMatrixCellsResolutionCount(mosaicMatrix);
            else
                return computeJP2MatrixCellsResolutionCount(mosaicMatrix);
        } else {
            throw new IllegalArgumentException("The matrix is empty: rowCount=" + mosaicMatrix.getRowCount()
                    + ", columnCount=" + mosaicMatrix.getColumnCount() + ".");
        }
    }

    protected static int computeJP2MatrixCellsResolutionCount(MosaicMatrix mosaicMatrix) {
        JP2MosaicBandMatrixCell firstMatrixCell = (JP2MosaicBandMatrixCell) mosaicMatrix.getCellAt(0, 0);
        for (int rowIndex = 0; rowIndex < mosaicMatrix.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < mosaicMatrix.getColumnCount(); columnIndex++) {
                JP2MosaicBandMatrixCell matrixCell = (JP2MosaicBandMatrixCell) mosaicMatrix.getCellAt(rowIndex,
                        columnIndex);
                if (firstMatrixCell.getResolutionCount() != matrixCell.getResolutionCount()) {
                    throw new IllegalStateException("Different resolution count: cell at " + rowIndex + ", "
                            + columnIndex + " has data type " + matrixCell.getResolutionCount() + " and cell at " + 0
                            + ", " + 0 + " has resolution count " + firstMatrixCell.getResolutionCount() + ".");
                }
            }
        }
        return firstMatrixCell.getResolutionCount();
    }

    protected static int computeTiffMatrixCellsResolutionCount(MosaicMatrix mosaicMatrix) {
        GeoTiffMatrixCell firstMatrixCell = (GeoTiffMatrixCell) mosaicMatrix.getCellAt(0, 0);
        for (int rowIndex = 0; rowIndex < mosaicMatrix.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < mosaicMatrix.getColumnCount(); columnIndex++) {
                GeoTiffMatrixCell matrixCell = (GeoTiffMatrixCell) mosaicMatrix.getCellAt(rowIndex, columnIndex);
                if (firstMatrixCell.getResolutionCount() != matrixCell.getResolutionCount()) {
                    throw new IllegalStateException("Different resolution count: cell at " + rowIndex + ", "
                            + columnIndex + " has data type " + matrixCell.getResolutionCount() + " and cell at " + 0
                            + ", " + 0 + " has resolution count " + firstMatrixCell.getResolutionCount() + ".");
                }
            }
        }
        int nbResolution=firstMatrixCell.getResolutionCount();
        return (nbResolution>5) ? nbResolution : 5;
    }

    protected final MosaicMatrix buildBandMatrix(Collection<String> bandMatrixTileIds,
            S2SceneDescription sceneDescription, BandInfo tileBandInfo) {
        MosaicMatrixCellCallback mosaicMatrixCellCallback = new MosaicMatrixCellCallback() {
            @Override
            public MosaicMatrix.MatrixCell buildMatrixCell(String tileId, BandInfo tileBandInfo, int sceneCellWidth,
                    int sceneCellHeight) {
                VirtualPath imageFilePath = tileBandInfo.getTileIdToPathMap().get(tileId);
                TileLayout tileLayout;
                try {
                    if (imageFilePath.getFileName().toString().endsWith(".TIF"))
                        tileLayout = AbstractS2MetadataReader.readTileLayoutFromTIFFile(imageFilePath);
                    else
                        tileLayout = AbstractS2MetadataReader.readTileLayoutFromJP2File(imageFilePath);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to read the tile layout for image file '"
                            + imageFilePath.getFullPathString() + "'.", e);
                }
                if (imageFilePath.getFileName().toString().endsWith(".TIF")) {
                    Path tiffImagePath = null;
                    try {
                        tiffImagePath = imageFilePath.getFilePath().getPath();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    int cellWidth = 0;
                    int cellHeight = 0;
                    int dataBufferType = -1;
                    try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader
                            .buildGeoTiffImageReader(tiffImagePath)) {
                        cellWidth = geoTiffImageReader.getImageWidth();
                        cellHeight = geoTiffImageReader.getImageHeight();
                        dataBufferType = geoTiffImageReader.getSampleModel().getDataType();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new GeoTiffMatrixCell(cellWidth, cellHeight, dataBufferType, tiffImagePath, null,
                            Sentinel2ProductReader.this.cacheDir, 1);
                } else {
                    JP2ImageFile jp2ImageFile = new JP2ImageFile(imageFilePath);
                    int cellWidth = Math.min(sceneCellWidth, tileLayout.width);
                    int cellHeight = Math.min(sceneCellHeight, tileLayout.height);
                    return new JP2MosaicBandMatrixCell(jp2ImageFile, Sentinel2ProductReader.this.cacheDir, tileLayout,
                            cellWidth, cellHeight);
                }
            }
        };
        return buildBandMatrix(bandMatrixTileIds, sceneDescription, tileBandInfo, mosaicMatrixCellCallback);
    }

    protected static Band buildBand(BandInfo bandInfo, int bandWidth, int bandHeight, int dataBufferType) {
        int bandDataType = ImageManager.getProductDataType(dataBufferType);
        Band band = new Band(bandInfo.getBandName(), bandDataType, bandWidth, bandHeight);

        S2BandInformation bandInformation = bandInfo.getBandInformation();
        band.setScalingFactor(bandInformation.getScalingFactor());

        if (bandInformation instanceof S2SpectralInformation) {
            S2SpectralInformation spectralInfo = (S2SpectralInformation) bandInformation;
            band.setSpectralWavelength((float) spectralInfo.getWavelengthCentral());
            band.setSpectralBandwidth((float) spectralInfo.getSpectralBandwith());
            band.setSpectralBandIndex(spectralInfo.getBandId());
            band.setNoDataValueUsed(false);
            band.setNoDataValue(0);
            band.setValidPixelExpression(
                    String.format("%s.raw > %s", bandInfo.getBandName(), S2Config.RAW_NO_DATA_THRESHOLD));
        } else if (bandInformation instanceof S2IndexBandInformation) {
            S2IndexBandInformation indexBandInfo = (S2IndexBandInformation) bandInformation;
            band.setSpectralWavelength(0);
            band.setSpectralBandwidth(0);
            band.setSpectralBandIndex(-1);
            band.setSampleCoding(indexBandInfo.getIndexCoding());
            band.setImageInfo(indexBandInfo.getImageInfo());
        } else {
            band.setSpectralWavelength(0);
            band.setSpectralBandwidth(0);
            band.setSpectralBandIndex(-1);
        }
        return band;
    }

    protected static MosaicMatrix buildBandMatrix(Collection<String> bandMatrixTileIds,
            S2SceneDescription sceneDescription, BandInfo tileBandInfo,
            MosaicMatrixCellCallback mosaicMatrixCellCallback) {
        // find the top left rectangle of the matrix
        Pair<String, Rectangle> topLeftRectanglePair = null;
        S2SpatialResolution bandNativeResolution = tileBandInfo.getBandInformation().getResolution();
        List<Pair<String, Rectangle>> remainingRectanglePairs = new ArrayList<>(bandMatrixTileIds.size() - 1);
        for (String tileId : bandMatrixTileIds) {
            Rectangle tileRectangle = sceneDescription.getMatrixTileRectangle(tileId, bandNativeResolution);
            Pair<String, Rectangle> pair = new Pair<>(tileId, tileRectangle);
            if (tileRectangle.x == 0 && tileRectangle.y == 0) {
                if (topLeftRectanglePair == null) {
                    topLeftRectanglePair = pair;
                } else {
                    throw new IllegalStateException("The top left rectangle is duplicate.");
                }
            } else {
                remainingRectanglePairs.add(pair);
            }
        }
        if (topLeftRectanglePair == null) {
            throw new IllegalStateException("No tile images.");
        }

        Set<BandRectangleCoordinates> matrixCellsSet = new HashSet<>();
        Stack<BandRectangleCoordinates> stack = new Stack<>();
        stack.push(
                new BandRectangleCoordinates(topLeftRectanglePair.getFirst(), topLeftRectanglePair.getSecond(), 0, 0));
        while (!stack.isEmpty()) {
            BandRectangleCoordinates currentRectangleCoordinates = stack.pop();
            if (!matrixCellsSet.add(currentRectangleCoordinates)) {
                throw new IllegalStateException("Dupllicate cell.");
            }
            Rectangle currentRectangle = currentRectangleCoordinates.tileRectangle;
            int leftX = currentRectangle.x;
            int rightX = currentRectangle.x + currentRectangle.width;
            int topY = currentRectangle.y;
            int bottomY = currentRectangle.y + currentRectangle.height;
            for (int i = 0; i < remainingRectanglePairs.size(); i++) {
                Pair<String, Rectangle> pair = remainingRectanglePairs.get(i);
                if (pair != null) {
                    Rectangle rectangle = pair.getSecond();
                    if (rectangle.x > leftX && rectangle.x <= rightX) {
                        // the next column
                        int rowIndex = currentRectangleCoordinates.rowIndex;
                        int columnIndex = currentRectangleCoordinates.columnIndex + 1;
                        BandRectangleCoordinates rectangleCoordinates = new BandRectangleCoordinates(pair.getFirst(),
                                pair.getSecond(), rowIndex, columnIndex);
                        stack.push(rectangleCoordinates);
                        remainingRectanglePairs.set(i, null); // reset the position
                    } else if (rectangle.y > topY && rectangle.y <= bottomY) {
                        // the next row
                        int rowIndex = currentRectangleCoordinates.rowIndex + 1;
                        int columnIndex = currentRectangleCoordinates.columnIndex;
                        BandRectangleCoordinates rectangleCoordinates = new BandRectangleCoordinates(pair.getFirst(),
                                pair.getSecond(), rowIndex, columnIndex);
                        stack.push(rectangleCoordinates);
                        remainingRectanglePairs.set(i, null); // reset the position
                    }
                }
            }
        }
        if (matrixCellsSet.size() != bandMatrixTileIds.size()) {
            throw new IllegalStateException("Invalid matrix size: matrixCellsSet.size = " + matrixCellsSet.size()
                    + ", bandMatrixTileIds.size() = " + bandMatrixTileIds.size());
        }
        int lastRowIndex = -1;
        int lastColumnIndex = -1;
        for (BandRectangleCoordinates rectangleCoordinates : matrixCellsSet) {
            lastRowIndex = Math.max(lastRowIndex, rectangleCoordinates.rowIndex);
            lastColumnIndex = Math.max(lastColumnIndex, rectangleCoordinates.columnIndex);
        }
        int rowCount = lastRowIndex + 1;
        int columnCount = lastColumnIndex + 1;
        if (rowCount * columnCount != bandMatrixTileIds.size()) {
            throw new IllegalStateException("Invalid matrix size: row count = " + rowCount + ", column count = "
                    + columnCount + ", bandMatrixTileIds.size()=" + bandMatrixTileIds.size());
        }
        BandRectangleCoordinates[][] bandRectangleCoordinates = new BandRectangleCoordinates[rowCount][columnCount];
        for (BandRectangleCoordinates rectangleCoordinates : matrixCellsSet) {
            bandRectangleCoordinates[rectangleCoordinates.rowIndex][rectangleCoordinates.columnIndex] = rectangleCoordinates;
        }
        MosaicMatrix mosaicMatrix = new MosaicMatrix(rowCount, columnCount);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                BandRectangleCoordinates rectangleCoordinates = bandRectangleCoordinates[rowIndex][columnIndex];
                int sceneCellHeight = rectangleCoordinates.tileRectangle.height;
                if (rowIndex < rowCount - 1) {
                    BandRectangleCoordinates nextRowRectangle = bandRectangleCoordinates[rowIndex + 1][columnIndex];
                    sceneCellHeight = nextRowRectangle.tileRectangle.y - rectangleCoordinates.tileRectangle.y;
                }
                int sceneCellWidth = rectangleCoordinates.tileRectangle.width;
                if (columnIndex < columnCount - 1) {
                    BandRectangleCoordinates nextColumnRectangle = bandRectangleCoordinates[rowIndex][columnIndex + 1];
                    sceneCellWidth = nextColumnRectangle.tileRectangle.x - rectangleCoordinates.tileRectangle.x;
                }
                MosaicMatrix.MatrixCell matrixCell = mosaicMatrixCellCallback
                        .buildMatrixCell(rectangleCoordinates.tileId, tileBandInfo, sceneCellWidth, sceneCellHeight);
                mosaicMatrix.setCellAt(rowIndex, columnIndex, matrixCell, false, false);
            }
        }
        return mosaicMatrix;
    }

    public static class BandInfo {

        private final Map<String, VirtualPath> tileIdToPathMap;
        private final S2BandInformation bandInformation;

        public BandInfo(Map<String, VirtualPath> tileIdToPathMap, S2BandInformation spectralInformation,
                TileLayout imageLayout) {
            this.tileIdToPathMap = Collections.unmodifiableMap(tileIdToPathMap);
            this.bandInformation = spectralInformation;
        }

        public S2BandInformation getBandInformation() {
            return bandInformation;
        }

        public Map<String, VirtualPath> getTileIdToPathMap() {
            return tileIdToPathMap;
        }

        public String getBandName() {
            return getBandInformation().getPhysicalBand();
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    private static class BandRectangleCoordinates {

        private Rectangle tileRectangle;
        private String tileId;
        private int rowIndex;
        private int columnIndex;

        private BandRectangleCoordinates(String tileId, Rectangle tileRectangle, int rowIndex, int columnIndex) {
            this.tileId = tileId;
            this.tileRectangle = tileRectangle;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }
    }
}
