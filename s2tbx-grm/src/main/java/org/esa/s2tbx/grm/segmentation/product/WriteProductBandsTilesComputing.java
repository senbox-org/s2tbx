package org.esa.s2tbx.grm.segmentation.product;

import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.tiles.AbstractTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;
import org.esa.s2tbx.grm.segmentation.tiles.SegmentationSourceProductPair;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.BufferedOutputStreamWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class WriteProductBandsTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(WriteProductBandsTilesComputing.class.getName());

    private final Product sourceProduct;
    private final String[] sourceBandNames;
    private final int segmentationTileMargin;
    private final Path temporaryFolder;

    public WriteProductBandsTilesComputing(Product sourceProduct, String[] sourceBandNames, int tileWidth, int tileHeight, Path temporaryParentFolder)
                                           throws IOException {

        super(sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight(), tileWidth, tileHeight);

        this.sourceProduct = sourceProduct;
        this.sourceBandNames = sourceBandNames;

        String temporaryFolderName = "product-bands" + Long.toString(System.currentTimeMillis());
        this.temporaryFolder = Files.createDirectories(temporaryParentFolder.resolve(temporaryFolderName));

        this.segmentationTileMargin = AbstractTileSegmenter.computeTileMargin(tileWidth, tileHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Write band values for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        int imageWidth = getImageWidth();
        int imageHeight = getImageHeight();
        ProcessingTile segmentationProcessingTile = AbstractTileSegmenter.buildTile(tileLeftX, tileTopY, tileWidth, tileHeight, this.segmentationTileMargin, imageWidth, imageHeight);
        BoundingBox segmentationTileBounds = segmentationProcessingTile.getRegion();
        String tileFileName = SegmentationSourceProductPair.buildSegmentationTileFileName(segmentationTileBounds);
        File nodesFile = this.temporaryFolder.resolve(tileFileName).toFile();

        BufferedOutputStreamWrapper outputFileStream = null;
        try {
            outputFileStream = new BufferedOutputStreamWrapper(nodesFile);

            outputFileStream.writeInt(this.sourceBandNames.length);

            int segmentationTileBottomY = segmentationTileBounds.getTopY() + segmentationTileBounds.getHeight();
            int segmentationTileRightX = segmentationTileBounds.getLeftX() + segmentationTileBounds.getWidth();
            for (int y = segmentationTileBounds.getTopY(); y < segmentationTileBottomY; y++) {
                for (int x = segmentationTileBounds.getLeftX(); x < segmentationTileRightX; x++) {
                    for (int i=0; i<this.sourceBandNames.length; i++) {
                        Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
                        outputFileStream.writeFloat(band.getSampleFloat(x, y));
                    }
                }
            }
        } finally {
            if (outputFileStream != null) {
                try {
                    outputFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
        }
    }

    public Path runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        long startTime = System.currentTimeMillis();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start writing the product bands into local disk files: source product: '"+this.sourceProduct.getName()+"', image width: "+getImageWidth()+", image height: "+getImageHeight() + ", start time: " + new Date(startTime));
            logger.log(Level.FINE, "Temporary folder path to store the binary files: '" + this.temporaryFolder.toFile().getAbsolutePath()+"'");
        }

        boolean success = false;
        try {
            super.executeInParallel(threadCount, threadPool);
            success = true;
        } finally {
            if (!success) {
                // failed to copy the data and delete the folder
                FileUtils.deleteTree(this.temporaryFolder.toFile());
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            long finishTime = System.currentTimeMillis();
            long totalSeconds = (finishTime - startTime) / 1000;
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Finish writing product bands into local disk files: source product: '"+this.sourceProduct.getName()+"', image width: "+getImageWidth()+", image height: "+getImageHeight()+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
        }

        return this.temporaryFolder;
    }
}
