package org.esa.beam.dataio.s2;

import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.beam.jai.ResolutionLevel;
import org.esa.beam.jai.SingleBandedOpImage;
import org.esa.beam.util.io.FileUtils;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.*;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
* @author Norman Fomferra
*/
class L1cTileOpImage extends SingleBandedOpImage {

    static final int NUM_SHORT_BYTES = 2;

    private static class Jp2File {
        File file;
        String header;
        ImageInputStream stream;
        long dataPos;
        int width;
        int height;
    }

    private final File imageFile;
    private final File cacheDir;
    private final L1cTileLayout imageLayout;
    private Map<File, Jp2File> openFiles;
    private Map<File, Object> locks;

    static PlanarImage create(File imageFile,
                              File cacheDir,
                              Point imagePos,
                              L1cTileLayout imageLayout,
                              MultiLevelModel imageModel,
                              SpatialResolution spatialResolution,
                              int level) throws IOException {
        PlanarImage opImage = new L1cTileOpImage(imageFile, cacheDir, imagePos, imageLayout, imageModel, level);
        if (spatialResolution != SpatialResolution.R10M) {
            return createScaledImage(opImage, spatialResolution, level);
        }
        return opImage;
    }

    static PlanarImage createScaledImage(PlanarImage sourceImage, SpatialResolution resolution, int level) {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();
        int targetWidth = Sentinel2ProductReader.L1C_TILE_LAYOUTS[0].width >> level;
        int targetHeight = Sentinel2ProductReader.L1C_TILE_LAYOUTS[0].height >> level;
        float scaleX = resolution.resolution / (float) SpatialResolution.R10M.resolution;
        float scaleY = resolution.resolution / (float) SpatialResolution.R10M.resolution;
        final Dimension tileDim = getTileDim(targetWidth, targetHeight);
        ImageLayout imageLayout = new ImageLayout();
        imageLayout.setTileWidth(tileDim.width);
        imageLayout.setTileHeight(tileDim.height);
        RenderingHints renderingHints = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
                                                           BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
        renderingHints.put(JAI.KEY_IMAGE_LAYOUT, imageLayout);
        RenderedOp scaledImage = ScaleDescriptor.create(sourceImage,
                                                        scaleX,
                                                        scaleY,
                                                        sourceImage.getMinX() - sourceImage.getMinX() * scaleX,
                                                        sourceImage.getMinY() - sourceImage.getMinY() * scaleY,
                                                        Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                                                        renderingHints);
        if (scaledImage.getWidth() != targetWidth || scaledImage.getHeight() != targetHeight) {
            return CropDescriptor.create(scaledImage,
                                         (float) sourceImage.getMinX(),
                                         (float) sourceImage.getMinY(),
                                         (float) targetWidth,
                                         (float) targetHeight,
                                         null);
        } else {
            return scaledImage;
        }
    }

    L1cTileOpImage(File imageFile,
                   File cacheDir,
                   Point imagePos,
                   L1cTileLayout imageLayout,
                   MultiLevelModel imageModel,
                   int level) throws IOException {
        super(DataBuffer.TYPE_USHORT,
              imagePos,
              imageLayout.width,
              imageLayout.height,
              getTileDimAtResolutionLevel(imageLayout.tileWidth, imageLayout.tileHeight, level),
              null,
              ResolutionLevel.create(imageModel, level));

        this.imageFile = imageFile;
        this.cacheDir = cacheDir;
        this.imageLayout = imageLayout;
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

        final Dimension jp2TileDim = getDimAtResolutionLevel(imageLayout.tileWidth, imageLayout.tileHeight, getLevel());

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
                                                                                   getLevel(), jp2TileX, jp2TileY)));
        final File outputFile0 = getFirstComponentOutputFile(outputFile);
        if (!outputFile0.exists()) {
            //System.out.printf("Jp2ExeImage.readTileData(): recomputing res=%d, tile=(%d,%d)\n", getLevel(), jp2TileX, jp2TileY);
            try {
                decompressTile(outputFile, jp2TileX, jp2TileY);
            } catch (IOException e) {
                // warn
                outputFile0.delete();
            }
            if (!outputFile0.exists()) {
                Arrays.fill(tileData, (short) 1000);
                return;
            }
        }

        try {
            //System.out.printf("Jp2ExeImage.readTileData(): reading res=%d, tile=(%d,%d)\n", getLevel(), jp2TileX, jp2TileY);
            readTileData(outputFile0, tileX, tileY, tileWidth, tileHeight, jp2TileX, jp2TileY, jp2TileWidth, jp2TileHeight, tileData, destRect);
        } catch (IOException e) {
            // warn
        }
    }

    private File getFirstComponentOutputFile(File outputFile) {
        return FileUtils.exchangeExtension(outputFile, "_0.pgx");
    }

    private void decompressTile(final File outputFile, int jp2TileX, int jp2TileY) throws IOException {
        final int tileIndex = imageLayout.numXTiles * jp2TileY + jp2TileX;
        final Process process = new ProcessBuilder(Sentinel2ProductReader.EXE,
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
                              int tileX, int tileY,
                              int tileWidth, int tileHeight,
                              int jp2TileX, int jp2TileY,
                              int jp2TileWidth, int jp2TileHeight,
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
                //System.out.printf("%s: tile=(%d,%d): jp2FileRect=%s, tileRect=%s, intersection=%s\n", jp2File.file, tileX, tileY, jp2FileRect, tileRect, intersection);
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

    static Dimension getTileDimAtResolutionLevel(int fullTileWidth, int fullTileHeight, int level) {
        int width = getSizeAtResolutionLevel(fullTileWidth, level);
        int height = getSizeAtResolutionLevel(fullTileHeight, level);
        return getTileDim(width, height);
    }

    static Dimension getDimAtResolutionLevel(int fullWidth, int fullHeight, int level) {
        int width = getSizeAtResolutionLevel(fullWidth, level);
        int height = getSizeAtResolutionLevel(fullHeight, level);
        return new Dimension(width, height);
    }

    static int getSizeAtResolutionLevel(int fullSize, int level) {
        // todo - find out how JPEG2000 computes its integer lower resolution sizes
        //        and use this algo also in DefaultMultiLevelModel
        //        and ImageManager.createSingleBandedImageLayout(..., level)
        int size = fullSize >> level;
        int sizeTest = size << level;
        if (sizeTest < fullSize) {
            size++;
        }
        return size;
    }

    static Dimension getTileDim(int width, int height) {
        return new Dimension(width < Sentinel2ProductReader.DEFAULT_TILE_SIZE ? width : Sentinel2ProductReader.DEFAULT_TILE_SIZE,
                             height < Sentinel2ProductReader.DEFAULT_TILE_SIZE ? height : Sentinel2ProductReader.DEFAULT_TILE_SIZE);
    }
}
