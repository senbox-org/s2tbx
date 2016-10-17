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
import org.esa.s2tbx.dataio.jp2.internal.JP2MultiLevelSource;
import org.esa.s2tbx.dataio.jp2.internal.JP2ProductReaderConstants;
import org.esa.s2tbx.dataio.jp2.internal.OpjExecutor;
import org.esa.s2tbx.dataio.jp2.metadata.*;
import org.esa.s2tbx.dataio.jp2.metadata.ImageInfo;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegExecRetriever;
import org.esa.s2tbx.dataio.readers.PathUtils;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.geotools.referencing.CRS;

import javax.media.jai.JAI;
import java.awt.geom.Point2D;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static org.esa.s2tbx.dataio.Utils.GetIterativeShortPathNameW;
import static org.esa.s2tbx.dataio.Utils.getMD5sum;
import static org.esa.s2tbx.dataio.openjpeg.OpenJpegUtils.validateOpenJpegExecutables;

/**
 * Generic reader for JP2 files.
 *
 * @author Cosmin Cara
 */
public class JP2ProductReader extends AbstractProductReader {

    private static final BucketMap<Integer, Integer> precisionTypeMap = new BucketMap<Integer, Integer>() {{
        put(1, 8, ProductData.TYPE_UINT8);
        put(9, 15, ProductData.TYPE_UINT16);
        put(16, ProductData.TYPE_INT16);
        put(17, 32, ProductData.TYPE_FLOAT32);
    }};

    private static final BucketMap<Integer, Integer> dataTypeMap = new BucketMap<Integer, Integer>() {{
        put(1, 8, DataBuffer.TYPE_BYTE);
        put(9, 15, DataBuffer.TYPE_USHORT);
        put(16, DataBuffer.TYPE_SHORT);
        put(17, 32, DataBuffer.TYPE_FLOAT);
    }};

    private final Logger logger;
    private Product product;
    private Path tmpFolder;

    protected JP2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        logger = Logger.getLogger(JP2ProductReader.class.getName());
        registerMetadataParser();
    }

    @Override
    public void close() throws IOException {
        //JAI.getDefaultInstance().getTileCache().flush();
        if (product != null) {
            for (Band band : product.getBands()) {
                MultiLevelImage sourceImage = band.getSourceImage();
                if (sourceImage != null) {
                    sourceImage.reset();
                    sourceImage.dispose();
                    sourceImage = null;
                }
            }
        }
        List<Path> files = PathUtils.listFiles(tmpFolder);
        tmpFolder.toFile().deleteOnExit();
        if (files != null) {
            for (Path file : files) {
                file.toFile().deleteOnExit();
            }
        }
        super.close();
    }

    Path createCacheDirRoot(Path inputFile) throws IOException {
        Path versionFile = ResourceInstaller.findModuleCodeBasePath(getClass()).resolve("version/version.properties");
        Properties versionProp = new Properties();

        try (InputStream inputStream = Files.newInputStream(versionFile)){
            versionProp.load(inputStream);
        } catch (IOException e) {
            SystemUtils.LOG.severe("JP2-reader configuration error: Failed to read " + versionFile.toString());
            return null;
        }

        String version = versionProp.getProperty("project.version");
        if (version == null) {
            throw new IOException("Unable to get project.version property from " + versionFile);
        }

        String md5sum = getMD5sum(inputFile.toString());
        if (md5sum == null) {
            throw new IOException("Unable to get md5sum of path " + inputFile.toString());
        }

        tmpFolder = PathUtils.get(SystemUtils.getCacheDir(), "s2tbx", "jp2-reader", version, md5sum, PathUtils.getFileNameWithoutExtension(inputFile).toLowerCase() + "_cached");
        if (!Files.exists(tmpFolder)) {
            Files.createDirectories(tmpFolder);
        }
        return tmpFolder;
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        if (getReaderPlugIn().getDecodeQualification(super.getInput()) == DecodeQualification.UNABLE) {
            throw new IOException("The selected product cannot be read with the current reader.");
        }

        if(!validateOpenJpegExecutables(OpenJpegExecRetriever.getOpjDump(),OpenJpegExecRetriever.getOpjDecompress())){
            throw new IOException("Invalid OpenJpeg executables");
        }

        Path inputFile = getFileInput(getInput());
        tmpFolder = createCacheDirRoot(inputFile);

        logger.info("Reading product metadata");

        try {
            OpjExecutor dumper = new OpjExecutor(OpenJpegExecRetriever.getOpjDump());
            OpjDumpFile dumpFile = new OpjDumpFile(PathUtils.get(tmpFolder, String.format(JP2ProductReaderConstants.JP2_INFO_FILE, PathUtils.getFileNameWithoutExtension(inputFile))));
            Map<String, String> params = new HashMap<String, String>() {{
                put("-i", GetIterativeShortPathNameW(inputFile.toAbsolutePath().toString()));
                put("-o", dumpFile.getPath());
            }};
            if (dumper.execute(params) == 0) {
                logger.info(dumper.getLastOutput());
                dumpFile.parse();
                ImageInfo imageInfo = dumpFile.getImageInfo();
                CodeStreamInfo csInfo = dumpFile.getCodeStreamInfo();
                int imageWidth = imageInfo.getWidth();
                int imageHeight = imageInfo.getHeight();
                product = new Product(inputFile.getFileName().toString(), JP2ProductReaderConstants.TYPE, imageWidth, imageHeight);
                MetadataElement metadataRoot = product.getMetadataRoot();
                metadataRoot.addElement(imageInfo.toMetadataElement());
                metadataRoot.addElement(csInfo.toMetadataElement());
                Jp2XmlMetadata metadata = new Jp2XmlMetadataReader(inputFile).read();
                if (metadata != null) {
                    metadata.setFileName(inputFile.toAbsolutePath().toString());
                    metadataRoot.addElement(metadata.getRootElement());
                    String crsGeocoding = metadata.getCrsGeocoding();
                    Point2D origin = metadata.getOrigin();
                    if (crsGeocoding != null && origin != null) {
                        GeoCoding geoCoding = null;
                        try {
                            geoCoding = new CrsGeoCoding(CRS.decode(crsGeocoding.replace("::", ":")),
                                                         imageWidth, imageHeight,
                                                         origin.getX(), origin.getY(),
                                                         metadata.getStepX(), -metadata.getStepY());
                        } catch (Exception gEx) {
                            try {
                                float oX = (float) origin.getX();
                                float oY = (float) origin.getY();
                                float h = (float) imageHeight * (float) metadata.getStepY();
                                float w = (float) imageWidth * (float) metadata.getStepX();
                                float[] latPoints = new float[]{oY + h, oY + h, oY, oY};
                                float[] lonPoints = new float[]{oX, oX + w, oX, oX + w};
                                TiePointGrid latGrid = createTiePointGrid("latitude", 2, 2, 0, 0, imageWidth, imageHeight, latPoints);
                                TiePointGrid lonGrid = createTiePointGrid("longitude", 2, 2, 0, 0, imageWidth, imageHeight, lonPoints);
                                geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
                                product.addTiePointGrid(latGrid);
                                product.addTiePointGrid(lonGrid);
                            } catch (Exception ignored) {
                            }
                        }
                        if (geoCoding != null) {
                            product.setSceneGeoCoding(geoCoding);
                        }
                    }
                }
                List<CodeStreamInfo.TileComponentInfo> componentTilesInfo = csInfo.getComponentTilesInfo();
                int numBands = componentTilesInfo.size();
                for (int bandIdx = 0; bandIdx < numBands; bandIdx++) {
                    int precision = imageInfo.getComponents().get(bandIdx).getPrecision();
                    Band virtualBand = new Band("band_" + String.valueOf(bandIdx + 1),
                                                precisionTypeMap.get(precision),
                                                imageWidth,
                                                imageHeight);
                    JP2MultiLevelSource source = new JP2MultiLevelSource(
                            getFileInput(getInput()),
                            tmpFolder,
                            bandIdx,
                            numBands,
                            imageWidth, imageHeight,
                            csInfo.getTileWidth(), csInfo.getTileHeight(),
                            csInfo.getNumTilesX(), csInfo.getNumTilesY(),
                            csInfo.getNumResolutions(), dataTypeMap.get(precision),
                            product.getSceneGeoCoding());
                    virtualBand.setSourceImage(new DefaultMultiLevelImage(source));
                    product.addBand(virtualBand);
                }
                product.setPreferredTileSize(JAI.getDefaultTileSize());
            } else {
                logger.warning(dumper.getLastError());
            }
        } catch (Exception mex) {
            String msg = String.format("Error while reading file %s", inputFile);
            throw new IOException(msg);
        }
        if (product != null) {
            product.setFileLocation(inputFile.toFile());
            product.setModified(false);
        }
        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
    }

    protected void registerMetadataParser() {
        XmlMetadataParserFactory.registerParser(Jp2XmlMetadata.class, new XmlMetadataParser<>(Jp2XmlMetadata.class));
    }

    /**
     * Returns a File object from the input of the reader.
     *
     * @param input the input object
     * @return Either a new instance of File, if the input represents the file name, or the casted input File.
     */
    protected Path getFileInput(Object input) {
        if (input instanceof String) {
            return Paths.get((String) input);
        } else if (input instanceof File) {
            return ((File) input).toPath();
        } else if (input instanceof Path) {
            return (Path) input;
        }
        return null;
    }
}
