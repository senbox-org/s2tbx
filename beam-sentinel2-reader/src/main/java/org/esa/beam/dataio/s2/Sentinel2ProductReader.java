package org.esa.beam.dataio.s2;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelModel;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.CrsGeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.jai.ResolutionLevel;
import org.esa.beam.jai.SingleBandedOpImage;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.util.io.FileUtils;
import org.esa.beam.util.logging.BeamLogManager;
import org.geotools.geometry.Envelope2D;
import org.jdom.JDOMException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Sentinel2ProductReader extends AbstractProductReader {
    private static final int DEFAULT_TILE_SIZE = 512;
    private static final int NUM_SHORT_BYTES = 2;
    private static final String EXE = System.getProperty("openjpeg2.decompressor.path", "opj_decompress");

    private static class ImgLayout {
        int width;
        int height;
        int tileWidth;
        int tileHeight;
        int numXTiles;
        int numYTiles;
        int numResolutions;

        private ImgLayout(int width, int height, int tileWidth, int tileHeight, int numXTiles, int numYTiles, int numResolutions) {
            this.width = width;
            this.height = height;
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.numXTiles = numXTiles;
            this.numYTiles = numYTiles;
            this.numResolutions = numResolutions;
        }
    }

    private static class BandInfo {
        final File imageFile;
        final int bandIndex;
        final S2WavebandInfo wavebandInfo;
        final ImgLayout imageLayout;

        private BandInfo(File imageFile, int bandIndex, S2WavebandInfo wavebandInfo, ImgLayout imageLayout) {
            this.imageFile = imageFile;
            this.bandIndex = bandIndex;
            this.wavebandInfo = wavebandInfo;
            this.imageLayout = imageLayout;
        }
    }


    S2WavebandInfo[] WAVEBAND_INFOS = new S2WavebandInfo[]{
            new S2WavebandInfo(0, "B1", 443, 20, S2Resolution.R60M),
            new S2WavebandInfo(1, "B2", 490, 65, S2Resolution.R10M),
            new S2WavebandInfo(2, "B3", 560, 35, S2Resolution.R10M),
            new S2WavebandInfo(3, "B4", 665, 30, S2Resolution.R10M),
            new S2WavebandInfo(4, "B5", 705, 15, S2Resolution.R20M),
            new S2WavebandInfo(5, "B6", 740, 15, S2Resolution.R20M),
            new S2WavebandInfo(6, "B7", 775, 20, S2Resolution.R20M),
            new S2WavebandInfo(7, "B8", 842, 115, S2Resolution.R10M),
            new S2WavebandInfo(8, "B8a", 865, 20, S2Resolution.R20M),
            new S2WavebandInfo(9, "B9", 940, 20, S2Resolution.R60M),
            new S2WavebandInfo(10, "B10", 1380, 30, S2Resolution.R60M),
            new S2WavebandInfo(11, "B11", 1610, 90, S2Resolution.R20M),
            new S2WavebandInfo(12, "B12", 2190, 180, S2Resolution.R20M),
    };


    // these numbers should actually been read from the JP2 files,
    // because they are likely to change if prod. spec. changes
    //
    private final static ImgLayout[] IMAGE_LAYOUTS = new ImgLayout[]{
            new ImgLayout(10690, 10690, 4096, 4096, 3, 3, 6),
            new ImgLayout(5480, 5480, 4096, 4096, 2, 2, 6),
            new ImgLayout(1826, 1826, 1826, 1826, 1, 1, 6),
    };


    Sentinel2ProductReader(Sentinel2ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        final String s = getInput().toString();

        final File file0 = new File(s);
        if (!file0.exists()) {
            throw new FileNotFoundException(file0.getPath());
        }

        if (isMetadataFilename(file0.getName())) {
            return readProductNodesImpl(file0);
        } else if (isJp2ImageFilename(file0.getName())) {
            return readTileProductNodesImpl(file0);
        } else {
            throw new IOException("Unhandled file type.");
        }
    }

    private Product readProductNodesImpl(File metadataFile) throws IOException {
        Header metadataHeader;

        try {
            metadataHeader = Header.parseHeader(metadataFile);
        } catch (JDOMException e) {
            throw new IOException("Failed to parse metadata in " + metadataFile.getName());
        }


        final Map<Integer, BandInfo> fileMap = new HashMap<Integer, BandInfo>();

        final Header.ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();
        final Header.ResampleData resampleData = metadataHeader.getResampleData();

        final ArrayList<Integer> bandIndexes = new ArrayList<Integer>(fileMap.keySet());
        Collections.sort(bandIndexes);

        if (bandIndexes.isEmpty()) {
            throw new IOException("No valid bands found.");
        }

        final SceneDescription sceneDescription = SceneDescription.create(metadataHeader);

        String prodType = "S2_MSI_" + productCharacteristics.processingLevel;
        final Product product = new Product(FileUtils.getFilenameWithoutExtension(metadataFile).substring("MTD_".length()),
                                            prodType,
                                            sceneDescription.getSceneRectangle().width,
                                            sceneDescription.getSceneRectangle().height);

        product.setPreferredTileSize(DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
        product.setNumResolutionsMax(IMAGE_LAYOUTS[0].numResolutions);

        for (Header.SpectralInformation bandInformation : productCharacteristics.bandInformations) {
            final Band band = product.addBand(bandInformation.physicalBand, ProductData.TYPE_UINT16);
            band.setSpectralBandIndex(bandInformation.bandId);
            band.setSpectralWavelength((float) bandInformation.wavelenghtCentral);
            band.setSpectralBandwidth((float) (bandInformation.wavelenghtMax - bandInformation.wavelenghtMin));
        }

       /*
        try {
            product.setStartTime(ProductData.UTC.parse(fni0.start, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // warn
        }

        try {
            product.setEndTime(ProductData.UTC.parse(fni0.stop, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // warn
        }
        */

        final Envelope2D sceneEnvelope = sceneDescription.getSceneEnvelope();
        try {
            product.setGeoCoding(new CrsGeoCoding(sceneEnvelope.getCoordinateReferenceSystem(),
                                                  product.getSceneRasterWidth(),
                                                  product.getSceneRasterHeight(),
                                                  sceneEnvelope.getMinX(),
                                                  sceneEnvelope.getMaxY(),
                                                  S2Resolution.R10M.res,
                                                  S2Resolution.R10M.res,
                                                  0.0, 0.0));
        } catch (FactoryException e) {
            // todo - handle e
        } catch (TransformException e) {
            // todo - handle e
        }

        return product;
    }

    private Product readTileProductNodesImpl(File file0) throws IOException {
        final File dir = file0.getParentFile();

        final S2FilenameInfo fni0 = S2FilenameInfo.create(file0.getName());
        if (fni0 == null) {
            throw new IOException();
        }
        Header metadataHeader = null;
        final Map<Integer, BandInfo> fileMap = new HashMap<Integer, BandInfo>();
        if (dir != null) {
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return isJp2ImageFilename(name);
                }
            });
            if (files != null) {
                for (File file : files) {
                    int bandIndex = fni0.getBand(file.getName());
                    if (bandIndex >= 0 && bandIndex < WAVEBAND_INFOS.length) {
                        final S2WavebandInfo wavebandInfo = WAVEBAND_INFOS[bandIndex];
                        BandInfo bandInfo = new BandInfo(file,
                                                         bandIndex,
                                                         wavebandInfo,
                                                         IMAGE_LAYOUTS[wavebandInfo.resolution.id]);
                        fileMap.put(bandIndex, bandInfo);
                    }
                }
            }
            File[] metadataFiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return isMetadataFilename(name);
                }
            });
            if (metadataFiles != null && metadataFiles.length > 0) {
                File metadataFile = metadataFiles[0];
                try {
                    metadataHeader = Header.parseHeader(metadataFile);
                } catch (JDOMException e) {
                    BeamLogManager.getSystemLogger().warning("Failed to parse metadata file: " + metadataFile);
                }
            } else {
                BeamLogManager.getSystemLogger().warning("No metadata file found");
            }
        }

        final ArrayList<Integer> bandIndexes = new ArrayList<Integer>(fileMap.keySet());
        Collections.sort(bandIndexes);

        if (bandIndexes.isEmpty()) {
            throw new IOException("No valid bands found.");
        }

        String prodType = "S2_MSI_" + fni0.procLevel;
        final Product product = new Product(String.format("%s_%s_%s", prodType, fni0.orbitNo, fni0.tileId),
                                            prodType,
                                            IMAGE_LAYOUTS[S2Resolution.R10M.id].width,
                                            IMAGE_LAYOUTS[S2Resolution.R10M.id].height);

        product.setPreferredTileSize(DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
        product.setNumResolutionsMax(IMAGE_LAYOUTS[0].numResolutions);

        try {
            product.setStartTime(ProductData.UTC.parse(fni0.start, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // warn
        }

        try {
            product.setEndTime(ProductData.UTC.parse(fni0.stop, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // warn
        }

        if (metadataHeader != null) {

            //Header.ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();

            SceneDescription sceneDescription = SceneDescription.create(metadataHeader);
            int tileIndex = sceneDescription.getTileIndex(fni0.tileId);
            Envelope2D tileEnvelope = sceneDescription.getTileEnvelope(tileIndex);
            Header.Tile tile = metadataHeader.getTileList().get(tileIndex);

            try {
                product.setGeoCoding(new CrsGeoCoding(tileEnvelope.getCoordinateReferenceSystem(),
                                                      IMAGE_LAYOUTS[S2Resolution.R10M.id].width,
                                                      IMAGE_LAYOUTS[S2Resolution.R10M.id].height,
                                                      tile.tileGeometry10M.upperLeftX,
                                                      tile.tileGeometry10M.upperLeftY,
                                                      tile.tileGeometry10M.xDim,
                                                      -tile.tileGeometry10M.yDim,
                                                      0.0, 0.0));
            } catch (FactoryException e) {
                // todo - handle e
            } catch (TransformException e) {
                // todo - handle e
            }
        }

        for (Integer bandIndex : bandIndexes) {
            final BandInfo bandInfo = fileMap.get(bandIndex);
            final Band band = product.addBand(bandInfo.wavebandInfo.bandName, ProductData.TYPE_UINT16);
            band.setSpectralWavelength((float) bandInfo.wavebandInfo.centralWavelength);
            band.setSpectralBandwidth((float) bandInfo.wavebandInfo.bandWidth);
            band.setSpectralBandIndex(bandIndex);
            band.setSourceImage(new DefaultMultiLevelImage(new Jp2MultiLevelSource(bandInfo)));
        }

        return product;
    }

    private static boolean isMetadataFilename(String name) {
        return name.startsWith("MTD_") && name.endsWith(Sentinel2ProductReaderPlugIn.MTD_EXT);
    }

    private static boolean isJp2ImageFilename(String name) {
        return name.startsWith("IMG_") && name.endsWith(Sentinel2ProductReaderPlugIn.JP2_EXT);
    }

    private MultiLevelModel createImageModel(BandInfo bandInfo) {
        return new DefaultMultiLevelModel(bandInfo.imageLayout.numResolutions,
                                          new AffineTransform(),
                                          IMAGE_LAYOUTS[0].width,
                                          IMAGE_LAYOUTS[0].height);
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        throw new IllegalStateException("Should not come here");
    }

    public Dimension getJp2TileDim(BandInfo bandInfo, int level) {
        int width = bandInfo.imageLayout.tileWidth >> level;
        int widthTest = width << level;
        if (widthTest < bandInfo.imageLayout.tileWidth) {
            width++;
        }
        int height = bandInfo.imageLayout.tileHeight >> level;
        int heightTest = height << level;
        if (heightTest < bandInfo.imageLayout.tileHeight) {
            height++;
        }
        return new Dimension(width,
                             height);
    }

    public Dimension getTileDim(BandInfo bandInfo, int level) {
        Dimension jp2TileDim = getJp2TileDim(bandInfo, level);
        final int width = jp2TileDim.width;
        final int height = jp2TileDim.height;
        return getTileDim(width, height);
    }

    private Dimension getTileDim(int imageWidth, int imageHeight) {
        return new Dimension(imageWidth < DEFAULT_TILE_SIZE ? imageWidth : DEFAULT_TILE_SIZE,
                             imageHeight < DEFAULT_TILE_SIZE ? imageHeight : DEFAULT_TILE_SIZE);
    }

    private class Jp2MultiLevelSource extends AbstractMultiLevelSource {
        public static final float R20M_X_FACTOR = 1.0252F;
        public static final float R20M_Y_FACTOR = 1.0253F;
        public static final float R60M_X_FACTOR = 1.02445F;
        public static final float R60M_Y_FACTOR = 1.0249F;
        final BandInfo bandInfo;

        public Jp2MultiLevelSource(BandInfo bandInfo) {
            super(createImageModel(bandInfo));
            this.bandInfo = bandInfo;
        }

        @Override
        protected RenderedImage createImage(int level) {
            try {
                RenderedImage opImage = new Jp2ExeOpImage(bandInfo, getModel(), level);
                if (bandInfo.wavebandInfo.resolution != S2Resolution.R10M) {
                    return createScaledImage(opImage, bandInfo.wavebandInfo.resolution, level);
                }
                return opImage;
            } catch (IOException e) {
                return null;
            }
        }

        private RenderedOp createScaledImage(RenderedImage sourceImage, S2Resolution resolution, int level) {
            int sourceWidth = sourceImage.getWidth();
            int sourceHeight = sourceImage.getHeight();
            int targetWidth = IMAGE_LAYOUTS[0].width >> level;
            int targetHeight = IMAGE_LAYOUTS[0].height >> level;
            float scaleX = (float) targetWidth / (float) sourceWidth;
            float scaleY = (float) targetHeight / (float) sourceHeight;
            float corrFactorX = resolution == S2Resolution.R20M ? R20M_X_FACTOR : R60M_X_FACTOR;
            float corrFactorY = resolution == S2Resolution.R20M ? R20M_Y_FACTOR : R60M_Y_FACTOR;
            final Dimension tileDim = getTileDim(targetWidth, targetHeight);
            ImageLayout imageLayout = new ImageLayout();
            imageLayout.setTileWidth(tileDim.width);
            imageLayout.setTileHeight(tileDim.height);
            RenderingHints renderingHints = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
                                                               BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
            renderingHints.put(JAI.KEY_IMAGE_LAYOUT, imageLayout);
            RenderedOp scaledImage = ScaleDescriptor.create(sourceImage,
                                                            scaleX * corrFactorX,
                                                            scaleY * corrFactorY,
                                                            0F, 0F,
                                                            Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                                                            renderingHints);
            if (scaledImage.getWidth() != targetWidth || scaledImage.getHeight() != targetHeight) {
                return CropDescriptor.create(scaledImage, 0.0F, 0.0F, (float) targetWidth, (float) targetHeight, null);
            } else {
                return scaledImage;
            }
        }

    }


    class Jp2File {
        File file;
        String header;
        ImageInputStream stream;
        long dataPos;
        int width;
        int height;
    }


    /**
     * Tiled image at a given resolution level.
     */
    class Jp2ExeOpImage extends SingleBandedOpImage {

        private final File imageFile;
        private final File cacheDir;
        private Map<File, Jp2File> openFiles;
        private Map<File, Object> locks;
        private final BandInfo bandInfo;

        Jp2ExeOpImage(BandInfo bandInfo, MultiLevelModel imageModel, int level) throws IOException {
            super(DataBuffer.TYPE_USHORT,
                  bandInfo.imageLayout.width,
                  bandInfo.imageLayout.height,
                  getTileDim(bandInfo, level),
                  null,
                  ResolutionLevel.create(imageModel, level));

            final File resolvedFile = bandInfo.imageFile.getCanonicalFile();
            if (!resolvedFile.exists()) {
                throw new FileNotFoundException("File not found: " + bandInfo.imageFile);
            }

            if (resolvedFile.getParentFile() == null) {
                throw new IOException("Can't determine package directory");
            }

            final File cacheDir = new File(new File(SystemUtils.getApplicationDataDir(), "jopenjpeg/cache"),
                                           resolvedFile.getParentFile().getName());
            cacheDir.mkdirs();
            if (!cacheDir.exists() || !cacheDir.isDirectory() || !cacheDir.canWrite()) {
                throw new IOException("Can't access package cache directory");
            }

            this.bandInfo = bandInfo;
            this.imageFile = resolvedFile;
            this.cacheDir = cacheDir;
            this.openFiles = new HashMap<File, Jp2File>();
            this.locks = new HashMap<File, Object>();
        }


        @Override
        protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
            final DataBufferUShort dataBuffer = (DataBufferUShort) dest.getDataBuffer();
            final short[] tileData = dataBuffer.getData();

            final int tileWidth = this.getTileWidth();
            final int tileHeight = this.getTileHeight();
            final int tileX = destRect.x / tileWidth;
            final int tileY = destRect.y / tileHeight;

            if (tileWidth * tileHeight != tileData.length) {
                throw new IllegalStateException(String.format("tileWidth (=%d) * tileHeight (=%d) != tileData.length (=%d)",
                                                              tileWidth, tileHeight, tileData.length));
            }

            final int resolution = getLevel();
            final Dimension jp2TileDim = getJp2TileDim(bandInfo, resolution);

            final int jp2TileWidth = jp2TileDim.width;
            final int jp2TileHeight = jp2TileDim.height;
            final int jp2TileX = destRect.x / jp2TileWidth;
            final int jp2TileY = destRect.y / jp2TileHeight;

            // Res - Img Size - Tile W
            //  0  -  10960   -  4096
            //  1  -   5480   -  2048
            //  2  -   2740   -  1024
            //  3  -   1370   -   512
            //  4  -    685   -   256
            //  5  -    343   -   128

            final File outputFile = new File(cacheDir,
                                             FileUtils.exchangeExtension(imageFile.getName(),
                                                                         String.format("_R%d_TX%d_TY%d.pgx",
                                                                                       resolution, jp2TileX, jp2TileY)));
            final File outputFile0 = getFirstComponentOutputFile(outputFile);
            if (!outputFile0.exists()) {
                System.out.printf("Jp2ExeImage.readTileData(): recomputing res=%d, tile=(%d,%d)\n", resolution, jp2TileX, jp2TileY);
                try {
                    decompressTile(outputFile, jp2TileX, jp2TileY);
                } catch (IOException e) {
                    // warn
                    outputFile0.delete();
                }
                if (!outputFile0.exists()) {
                    Arrays.fill(tileData, (short) 0);
                    return;
                }
            }

            try {
                System.out.printf("Jp2ExeImage.readTileData(): reading res=%d, tile=(%d,%d)\n", resolution, jp2TileX, jp2TileY);
                readTileData(outputFile0, tileX, tileY, tileWidth, tileHeight, jp2TileX, jp2TileY, jp2TileWidth, jp2TileHeight, tileData, destRect);
            } catch (IOException e) {
                // warn
            }
        }

        private File getFirstComponentOutputFile(File outputFile) {
            return FileUtils.exchangeExtension(outputFile, "_0.pgx");
        }

        private void decompressTile(final File outputFile, int jp2TileX, int jp2TileY) throws IOException {
            final int tileIndex = bandInfo.imageLayout.numXTiles * jp2TileY + jp2TileX;
            final Process process = new ProcessBuilder(EXE,
                                                       "-i", imageFile.getPath(),
                                                       "-o", outputFile.getPath(),
                                                       "-r", getLevel() + "",
                                                       "-t", tileIndex + "").directory(cacheDir).start();

            try {
                final int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.err.println("Failed to uncompress tile: exitCode = " + exitCode);
                }
            } catch (InterruptedException e) {
                System.err.println("InterruptedException: " + e.getMessage());
            }
        }

        @Override
        public synchronized void dispose() {

            for (Map.Entry<File, Jp2File> entry : openFiles.entrySet()) {
                System.out.println("closing " + entry.getKey());
                try {
                    final Jp2File jp2File = entry.getValue();
                    if (jp2File.stream != null) {
                        jp2File.stream.close();
                        jp2File.stream = null;
                    }
                } catch (IOException e) {
                    // warn
                }
            }

            for (File file : openFiles.keySet()) {
                System.out.println("deleting " + file);
                if (!file.delete()) {
                    // warn
                }
            }

            openFiles.clear();

            if (!cacheDir.delete()) {
                // warn
            }
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            dispose();
        }

        private void readTileData(File outputFile,
                                  int tileX, int tileY, int tileWidth, int tileHeight,
                                  int jp2TileX, int jp2TileY, int jp2TileWidth, int jp2TileHeight,
                                  short[] tileData,
                                  Rectangle destRect) throws IOException {

            synchronized (this) {
                if (!locks.containsKey(outputFile)) {
                    locks.put(outputFile, new Object());
                }
            }
            final Object lock = locks.get(outputFile);

            synchronized (lock) {

                Jp2File jp2File = getOpenJ2pFile(outputFile);

                int jp2Width = jp2File.width;
                int jp2Height = jp2File.height;
                if (jp2Width > jp2TileWidth || jp2Height > jp2TileHeight) {
                    throw new IllegalStateException(String.format("width (=%d) > tileWidth (=%d) || height (=%d) > tileHeight (=%d)",
                                                                  jp2Width, jp2TileWidth, jp2Height, jp2TileHeight));
                }

                int jp2X = destRect.x - jp2TileX * jp2TileWidth;
                int jp2Y = destRect.y - jp2TileY * jp2TileHeight;
                if (jp2X < 0 || jp2Y < 0) {
                    throw new IllegalStateException(String.format("jp2X (=%d) < 0 || jp2Y (=%d) < 0",
                                                                  jp2X, jp2Y));
                }

                final ImageInputStream stream = jp2File.stream;

                if (jp2X == 0 && jp2Width == tileWidth
                        && jp2Y == 0 && jp2Height == tileHeight
                        && tileWidth * tileHeight == tileData.length) {
                    stream.seek(jp2File.dataPos);
                    stream.readFully(tileData, 0, tileData.length);
                } else {
                    final Rectangle jp2FileRect = new Rectangle(0, 0, jp2Width, jp2Height);
                    final Rectangle tileRect = new Rectangle(jp2X,
                                                             jp2Y,
                                                             tileWidth, tileHeight);
                    final Rectangle intersection = jp2FileRect.intersection(tileRect);
                    System.out.printf("%s: tile=(%d,%d): jp2FileRect=%s, tileRect=%s, intersection=%s\n", jp2File.file, tileX, tileY, jp2FileRect, tileRect, intersection);
                    if (!intersection.isEmpty()) {
                        long seekPos = jp2File.dataPos + NUM_SHORT_BYTES * (intersection.y * jp2Width + intersection.x);
                        int tilePos = 0;
                        for (int y = 0; y < intersection.height; y++) {
                            stream.seek(seekPos);
                            stream.readFully(tileData, tilePos, intersection.width);
                            seekPos += NUM_SHORT_BYTES * jp2Width;
                            tilePos += tileWidth;
                            for (int x = intersection.width; x < tileWidth; x++) {
                                tileData[y * tileWidth + x] = (short) 0;
                            }
                        }
                        for (int y = intersection.height; y < tileWidth; y++) {
                            for (int x = 0; x < tileWidth; x++) {
                                tileData[y * tileWidth + x] = (short) 0;
                            }
                        }
                    } else {
                        Arrays.fill(tileData, (short) 0);
                    }
                }
            }
        }

        private Jp2File getOpenJ2pFile(File outputFile) throws IOException {
            Jp2File jp2File = openFiles.get(outputFile);
            if (jp2File == null) {
                jp2File = new Jp2File();
                jp2File.file = outputFile;
                jp2File.stream = new FileImageInputStream(outputFile);
                jp2File.header = jp2File.stream.readLine();
                jp2File.dataPos = jp2File.stream.getStreamPosition();

                final String[] tokens = jp2File.header.split(" ");
                if (tokens.length != 6) {
                    throw new IOException("Unexpected tile format");
                }

                // String pg = tokens[0];   // PG
                // String ml = tokens[1];   // ML
                // String plus = tokens[2]; // +
                int jp2Width;
                int jp2Height;
                try {
                    // int jp2File.nbits = Integer.parseInt(tokens[3]);
                    jp2File.width = Integer.parseInt(tokens[4]);
                    jp2File.height = Integer.parseInt(tokens[5]);
                } catch (NumberFormatException e) {
                    throw new IOException("Unexpected tile format");
                }

                openFiles.put(outputFile, jp2File);
            }

            return jp2File;
        }
    }
}
