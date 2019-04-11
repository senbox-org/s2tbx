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

package org.esa.s2tbx.dataio.jp2;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.BucketMap;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.jp2.internal.JP2MultiLevelSource;
import org.esa.s2tbx.dataio.jp2.internal.JP2ProductReaderConstants;
import org.esa.s2tbx.dataio.jp2.internal.OpjExecutor;
import org.esa.s2tbx.dataio.jp2.metadata.CodeStreamInfo;
import org.esa.s2tbx.dataio.jp2.metadata.ImageInfo;
import org.esa.s2tbx.dataio.jp2.metadata.Jp2XmlMetadata;
import org.esa.s2tbx.dataio.jp2.metadata.Jp2XmlMetadataReader;
import org.esa.s2tbx.dataio.jp2.metadata.OpjDumpFile;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegExecRetriever;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.readers.PathUtils;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.utils.FileHelper;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.media.jai.JAI;
import java.awt.geom.Point2D;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static org.esa.s2tbx.dataio.Utils.getMD5sum;
import static org.esa.s2tbx.dataio.openjpeg.OpenJpegUtils.validateOpenJpegExecutables;

/**
 * Generic reader for JP2 files.
 *
 * @author Cosmin Cara
 */
public class JP2ProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(JP2ProductReader.class.getName());

    private static final BucketMap<Integer, Integer> PRECISION_TYPE_MAP = new BucketMap<Integer, Integer>() {{
        put(1, 8, ProductData.TYPE_UINT8);
        put(9, 15, ProductData.TYPE_UINT16);
        put(16, ProductData.TYPE_INT16);
        put(17, 32, ProductData.TYPE_FLOAT32);
    }};

    private static final BucketMap<Integer, Integer> DATA_TYPE_MAP = new BucketMap<Integer, Integer>() {{
        put(1, 8, DataBuffer.TYPE_BYTE);
        put(9, 15, DataBuffer.TYPE_USHORT);
        put(16, DataBuffer.TYPE_SHORT);
        put(17, 32, DataBuffer.TYPE_FLOAT);
    }};

    private Product product;
    private Path tempFolder;

    protected JP2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        registerMetadataParser();
    }

    @Override
    public void close() throws IOException {
        if (this.product != null) {
            for (Band band : this.product.getBands()) {
                MultiLevelImage sourceImage = band.getSourceImage();
                if (sourceImage != null) {
                    sourceImage.reset();
                    sourceImage.dispose();
                }
            }
        }
        List<Path> files = PathUtils.listFiles(this.tempFolder);
        this.tempFolder.toFile().deleteOnExit();
        if (files != null) {
            for (Path file : files) {
                file.toFile().deleteOnExit();
            }
        }

        super.close();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        if (getReaderPlugIn().getDecodeQualification(super.getInput()) == DecodeQualification.UNABLE) {
            throw new IOException("The selected product cannot be read with the current reader.");
        }

        if(!validateOpenJpegExecutables(OpenJpegExecRetriever.getOpjDump(),OpenJpegExecRetriever.getOpjDecompress())){
            throw new IOException("Invalid OpenJpeg executables");
        }

        Path inputPath = BaseProductReaderPlugIn.convertInputToPath(getInput());

        createTempFolder(inputPath);

        Path localInputPath;
        FileSystemProvider fileSystemProvider = inputPath.getFileSystem().provider();
        if (fileSystemProvider == FileSystems.getDefault().provider()) {
            localInputPath = inputPath;
        } else {
            localInputPath = this.tempFolder.resolve(inputPath.getFileName().toString());
            FileHelper.copyFileUsingFileChannel(inputPath, localInputPath.toString());
        }

        logger.fine("Reading product metadata");

        try {
            Path dumpFilePath = PathUtils.get(this.tempFolder, String.format(JP2ProductReaderConstants.JP2_INFO_FILE, PathUtils.getFileNameWithoutExtension(inputPath)));
            OpjExecutor dumper = new OpjExecutor(OpenJpegExecRetriever.getOpjDump());
            OpjDumpFile dumpFile = new OpjDumpFile(dumpFilePath);
            Map<String, String> params = new HashMap<String, String>();
            params.put("-i", Utils.GetIterativeShortPathNameW(localInputPath.toString()));
            params.put("-o", dumpFile.getPath());
            int exitCode = dumper.execute(params);
            if (exitCode == 0) {
                String lastOutput = dumper.getLastOutput();
                if (lastOutput != null && !lastOutput.isEmpty()) {
                    logger.info(lastOutput);
                }
                dumpFile.parse();
                ImageInfo imageInfo = dumpFile.getImageInfo();
                CodeStreamInfo csInfo = dumpFile.getCodeStreamInfo();
                int imageWidth = imageInfo.getWidth();
                int imageHeight = imageInfo.getHeight();
                this.product = new Product(localInputPath.getFileName().toString(), JP2ProductReaderConstants.TYPE, imageWidth, imageHeight);

                MetadataElement metadataRoot = this.product.getMetadataRoot();
                metadataRoot.addElement(imageInfo.toMetadataElement());
                metadataRoot.addElement(csInfo.toMetadataElement());
                Jp2XmlMetadata metadata = new Jp2XmlMetadataReader(localInputPath).read();
                String[] bandNames = null;
                double[] bandScales = null;
                double[] bandOffsets = null;
                if (metadata != null) {
                    metadata.setFileName(localInputPath.toAbsolutePath().toString());
                    metadataRoot.addElement(metadata.getRootElement());
                    addGeoCoding(metadata);
                }

                addBands(imageInfo, bandNames, localInputPath, csInfo, bandScales, bandOffsets);

                this.product.setPreferredTileSize(JAI.getDefaultTileSize());
            } else {
                logger.warning(dumper.getLastError());
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception mex) {
            String msg = String.format("Error while reading file %s", localInputPath);
            throw new IOException(msg);
        }
        if (this.product != null) {
            this.product.setFileLocation(localInputPath.toFile());
            this.product.setModified(false);
        }
        return this.product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
    }

    private void addGeoCoding(Jp2XmlMetadata metadata) {
        int imageWidth = product.getSceneRasterWidth();
        int imageHeight = product.getSceneRasterHeight();

        String crsGeoCoding = metadata.getCrsGeocoding();
        Point2D origin = metadata.getOrigin();
        GeoCoding geoCoding = null;
        if (crsGeoCoding != null && origin != null) {
            try {
                CoordinateReferenceSystem mapCRS = CRS.decode(crsGeoCoding.replace("::", ":"));
                geoCoding = new CrsGeoCoding(mapCRS, imageWidth, imageHeight, origin.getX(), origin.getY(), metadata.getStepX(), -metadata.getStepY());
            } catch (Exception gEx) {
                // ignore
            }
        }
        if (geoCoding == null) {
            try {
                float[] latPoints = null;
                float[] lonPoints = null;
                if(origin != null){
                    float oX = (float) origin.getX();
                    float oY = (float) origin.getY();
                    float h = (float) imageHeight * (float) metadata.getStepY();
                    float w = (float) imageWidth * (float) metadata.getStepX();
                    latPoints = new float[]{oY + h, oY + h, oY, oY};
                    lonPoints = new float[]{oX, oX + w, oX, oX + w};
                } else {
                    List<Point2D> polygonPositions = metadata.getPolygonPositions();
                    if (polygonPositions != null) {
                        latPoints = new float[]{(float) polygonPositions.get(0).getX(),
                                (float) polygonPositions.get(1).getX(),
                                (float) polygonPositions.get(3).getX(),
                                (float) polygonPositions.get(2).getX()};
                        lonPoints = new float[]{(float) polygonPositions.get(0).getY(),
                                (float) polygonPositions.get(1).getY(),
                                (float) polygonPositions.get(3).getY(),
                                (float) polygonPositions.get(2).getY()};
                    }
                }
                if(latPoints != null ) {
                    TiePointGrid latGrid = createTiePointGrid("latitude", 2, 2, 0, 0, imageWidth, imageHeight, latPoints);
                    TiePointGrid lonGrid = createTiePointGrid("longitude", 2, 2, 0, 0, imageWidth, imageHeight, lonPoints);
                    geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
                    product.addTiePointGrid(latGrid);
                    product.addTiePointGrid(lonGrid);
                }
            } catch (Exception ignored) {
                // ignore
            }
        }
        if (geoCoding != null) {
            this.product.setSceneGeoCoding(geoCoding);
        }
    }

    private void addBands(ImageInfo imageInfo, String[] bandNames,
                          Path localInputPath, CodeStreamInfo csInfo, double[] bandScales, double[] bandOffsets) {

        List<CodeStreamInfo.TileComponentInfo> componentTilesInfo = csInfo.getComponentTilesInfo();

        int imageWidth = product.getSceneRasterWidth();
        int imageHeight = product.getSceneRasterHeight();

        int numBands = componentTilesInfo.size();
        for (int bandIdx = 0; bandIdx < numBands; bandIdx++) {
            int precision = imageInfo.getComponents().get(bandIdx).getPrecision();
            Band virtualBand = new Band(bandNames != null ? bandNames[bandIdx] : "band_" + String.valueOf(bandIdx + 1),
                    PRECISION_TYPE_MAP.get(precision),
                    imageWidth,
                    imageHeight);
            JP2MultiLevelSource source = new JP2MultiLevelSource(
                    localInputPath,
                    tempFolder,
                    bandIdx,
                    numBands,
                    imageWidth, imageHeight,
                    csInfo.getTileWidth(), csInfo.getTileHeight(),
                    csInfo.getNumTilesX(), csInfo.getNumTilesY(),
                    csInfo.getNumResolutions(), DATA_TYPE_MAP.get(precision),
                    product.getSceneGeoCoding());
            virtualBand.setSourceImage(new DefaultMultiLevelImage(source));
            if (bandScales != null && bandOffsets != null) {
                virtualBand.setScalingFactor(bandScales[bandIdx]);
                virtualBand.setScalingOffset(bandOffsets[bandIdx]);
            }
            product.addBand(virtualBand);
        }
    }

    private void createTempFolder(Path inputFile) throws IOException {
        Path versionFile = ResourceInstaller.findModuleCodeBasePath(getClass()).resolve("version/version.properties");
        Properties versionProp = new Properties();

        try (InputStream inputStream = Files.newInputStream(versionFile)) {
            versionProp.load(inputStream);
        } catch (IOException e) {
            SystemUtils.LOG.severe("JP2-reader configuration error: Failed to read " + versionFile.toString());
            return;
        }

        String version = versionProp.getProperty("project.version");
        if (version == null) {
            throw new IOException("Unable to get project.version property from " + versionFile);
        }

        String md5sum = getMD5sum(inputFile.toString());
        if (md5sum == null) {
            throw new IOException("Unable to get md5sum of path " + inputFile.toString());
        }

        this.tempFolder = PathUtils.get(SystemUtils.getCacheDir(), "s2tbx", "jp2-reader", version, md5sum, PathUtils.getFileNameWithoutExtension(inputFile).toLowerCase() + "_cached");
        if (!Files.exists(this.tempFolder)) {
            Files.createDirectories(this.tempFolder);
        }
    }

    private void registerMetadataParser() {
        XmlMetadataParserFactory.registerParser(Jp2XmlMetadata.class, new XmlMetadataParser<>(Jp2XmlMetadata.class));
    }
}
