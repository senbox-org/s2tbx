package org.esa.s2tbx.grm.segmentation.tiles;

import com.bc.ceres.glevel.MultiLevelImage;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.internal.TileImpl;
import org.esa.snap.utils.AbstractImageTilesHelper;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.io.IOException;

/**
 * @author Jean Coravu
 */
public class TileSecondSegmentationHelper extends AbstractImageTilesHelper {
    private final AbstractTileSegmenter tileSegmenter;
    private final int iteration;
    private final int numberOfNeighborLayers;

    public TileSecondSegmentationHelper(int iteration, int numberOfNeighborLayers, AbstractTileSegmenter tileSegmenter) {
        super(tileSegmenter.getImageWidth(), tileSegmenter.getImageHeight(), tileSegmenter.getTileWidth(), tileSegmenter.getTileHeight());

        this.tileSegmenter = tileSegmenter;
        this.iteration = iteration;
        this.numberOfNeighborLayers = numberOfNeighborLayers;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException, InterruptedException {
        this.tileSegmenter.runTileSecondSegmentation(this.iteration, localRowIndex, localColumnIndex, this.numberOfNeighborLayers);
    }
}