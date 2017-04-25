package org.esa.s2tbx.grm;

import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.AbstractTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.BaatzSchapeTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.TileSegmenterMetadata;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import javax.media.jai.JAI;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Jean Coravu
 */
@OperatorMetadata(
        alias = "SecondTileSegmentationGRMOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "The 'Generic Region Merging' operator computes the distinct regions from a product",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class SecondTileSegmentationGRMOp extends AbstractGenericRegionMergingOp {
    private static final Logger logger = Logger.getLogger(SecondTileSegmentationGRMOp.class.getName());

    @Parameter(label = "Folder path", description = "The temporary folder path to load the data.")
    private File temporaryFolder;

    @Parameter(label = "Start time", description = "The start time in milliseconds.")
    private long startTime;

    public SecondTileSegmentationGRMOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        super.initialize();

        if (this.temporaryFolder == null) {
            throw new OperatorException("Please specify the temporary folder path.");
        }

        this.targetProduct = this.sourceProduct;

        try {
            TileSegmenterMetadata tileSegmenterMetadata = TileSegmenterMetadata.readMetadata(temporaryFolder);
            AbstractTileSegmenter tileSegmenter = buildTileSegmenter(temporaryFolder, tileSegmenterMetadata);

            AbstractSegmenter segmenter = tileSegmenter.runAllTilesSecondSegmentation();

            Band targetBand = this.targetProduct.getBandAt(0);
            targetBand.setSourceImage(null); // reset the source image
            segmenter.fillBandData(targetBand);
            targetBand.getSourceImage();

            if (logger.isLoggable(Level.FINE)) {
                int imageWidth = tileSegmenter.getImageWidth();
                int imageHeight = tileSegmenter.getImageHeight();
                int tileWidth = tileSegmenter.getTileWidth();
                int tileHeight = tileSegmenter.getTileHeight();
                int tileMargin = tileSegmenter.computeTileMargin();

                long finishTime = System.currentTimeMillis();
                long totalSeconds =(finishTime - this.startTime) / 1000;
                int graphNodeCount = segmenter.getGraph().getNodeCount();
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Finish Segmentation: image width: " +imageWidth+", image height: "+imageHeight+", tile width: "+tileWidth+", tile height: "+tileHeight+", margin: "+tileMargin+", graph node count: "+graphNodeCount+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
            }
        } catch (Exception ex) {
            throw new OperatorException(ex);
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(SecondTileSegmentationGRMOp.class);
        }
    }
}
