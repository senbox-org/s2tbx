package org.esa.s2tbx.grm.segmentation.tiles;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Jean Coravu
 */
public class TileSegmentationRunnable implements Runnable {
    private static final Logger logger = Logger.getLogger(TileSegmentationRunnable.class.getName());

    private final AbstractTileSegmentationHelper tileSegmentationHelper;

    public TileSegmentationRunnable(AbstractTileSegmentationHelper tileSegmentationHelper) {
        this.tileSegmentationHelper = tileSegmentationHelper;
        this.tileSegmentationHelper.incrementThreadCounter();
    }

    @Override
    public void run() {
        try {
            this.tileSegmentationHelper.executeSegmentation();
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Failed to execute the image segmentation.", exception);
        } finally {
            this.tileSegmentationHelper.decrementThreadCounter();
        }
    }
}
