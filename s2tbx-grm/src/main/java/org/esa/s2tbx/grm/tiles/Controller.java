package org.esa.s2tbx.grm.tiles;

import java.util.List;

/**
 * Created by jcoravu on 10/3/2017.
 */
public class Controller {
    LSGRMTilingMode tilingMode = LSGRMTilingMode.LSGRM_TILING_AUTO;
    int margin = 0;
    int numberOfIterations = 0;
    int numberOfFirstIterations = 0;
    int tileHeight = 0;
    int tileWidth = 0;
    int nbTilesX = 0;
    int nbTilesY = 0;
    int threshold = 75;
    int memory = 0;

    public Controller() {

    }

    public void runSegmentation() {
        int imageWidth = 100;
        int imageHeight = 100;
        List<ProcessingTile> tiles = ImageSplitter.splitImage(imageWidth, imageHeight, tileWidth, tileHeight, margin);
        if (tilingMode == LSGRMTilingMode.LSGRM_TILING_AUTO || tilingMode == LSGRMTilingMode.LSGRM_TILING_USER) {
		    int numberOfIterationsForPartialSegmentations = 3; // TODO: find a smart value
            int numberOfIterationsRemaining = numberOfIterations;

            // Boolean indicating if there are remaining fusions
            boolean isFusion = false;

            // Run first partial segmentation


        }
    }
}
