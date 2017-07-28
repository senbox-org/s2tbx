package org.esa.s2tbx.grm.segmentation.tiles;

import com.bc.ceres.glevel.MultiLevelImage;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.DifferenceTileDataSourceImpl;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.internal.TileImpl;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.awt.*;
import java.awt.image.Raster;
import java.io.IOException;

/**
 * @author Jean Coravu
 */
public class CheckTemporaryTileFilesParallelComputing extends AbstractImageTilesParallelComputing {
    private final AbstractTileSegmenter tileSegmenter;
    private final int iteration;

    public CheckTemporaryTileFilesParallelComputing(int iteration, AbstractTileSegmenter tileSegmenter) {
        super(tileSegmenter.getImageWidth(), tileSegmenter.getImageHeight(), tileSegmenter.getTileWidth(), tileSegmenter.getTileHeight());

        this.iteration = iteration;
        this.tileSegmenter = tileSegmenter;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IllegalAccessException, IOException, InterruptedException {

        this.tileSegmenter.checkTemporaryTileFiles(this.iteration, tileLeftX, tileTopY, tileWidth, tileHeight, localRowIndex, localColumnIndex);
    }
}