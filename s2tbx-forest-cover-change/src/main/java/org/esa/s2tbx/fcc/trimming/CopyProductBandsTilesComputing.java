package org.esa.s2tbx.fcc.trimming;

import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.Edge;
import org.esa.s2tbx.grm.segmentation.Node;
import org.esa.s2tbx.grm.segmentation.tiles.AbstractTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.BufferedOutputStreamWrapper;
import org.esa.snap.utils.matrix.FloatMatrix;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class CopyProductBandsTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(CopyProductBandsTilesComputing.class.getName());

    private final Product sourceProduct;
    private final String[] sourceBandNames;
    private final File temporaryFolder;
    private final int segmentationTileMargin;

    public CopyProductBandsTilesComputing(Product sourceProduct, String[] sourceBandNames, int tileWidth, int tileHeight) throws IOException {
        super(sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight(), tileWidth, tileHeight);

        this.sourceProduct = sourceProduct;
        this.sourceBandNames = sourceBandNames;

        String temporaryFolderName = "_temp" + Long.toString(System.currentTimeMillis());
        this.temporaryFolder = Files.createTempDirectory(temporaryFolderName).toFile();

        this.segmentationTileMargin = AbstractTileSegmenter.computeTileMargin(tileWidth, tileHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Copy bands values for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        int imageWidth = getImageWidth();
        int imageHeight = getImageHeight();
        ProcessingTile segmentationProcessingTile = AbstractTileSegmenter.buildTile(tileLeftX, tileTopY, tileWidth, tileHeight, this.segmentationTileMargin, imageWidth, imageHeight);
        BoundingBox segmentationTileBounds = segmentationProcessingTile.getRegion();
        String tileFileName = "segmentationTile-"+segmentationTileBounds.getLeftX()+"-"+segmentationTileBounds.getTopY()+"-"+segmentationTileBounds.getWidth()+"-"+segmentationTileBounds.getHeight()+".bin";
        File nodesFile = new File(this.temporaryFolder, tileFileName);

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

    public File runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        boolean success = false;
        try {
            super.executeInParallel(threadCount, threadPool);
            success = true;
        } finally {
            if (!success) {
                // failed to copy the data and delete the folder
                FileUtils.deleteTree(this.temporaryFolder);
            }
        }
        return this.temporaryFolder;
    }
}
