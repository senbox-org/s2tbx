package org.esa.s2tbx.grm;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.tiles.AbstractTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;
import org.esa.s2tbx.grm.segmentation.tiles.TileSegmenterMetadata;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;

import javax.media.jai.JAI;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Jean Coravu
 */
@OperatorMetadata(
        alias = "FirstTileSegmentationGRMOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "The 'Generic Region Merging' operator computes the distinct regions from a product",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class FirstTileSegmentationGRMOp extends AbstractGenericRegionMergingOp {
    private static final Logger logger = Logger.getLogger(FirstTileSegmentationGRMOp.class.getName());

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    private AbstractTileSegmenter tileSegmenter;

    public FirstTileSegmentationGRMOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        super.initialize();

        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }

        File temporaryFolder = null;
        try {
            temporaryFolder = Files.createTempDirectory("_temp").toFile();
        } catch (IOException e) {
            throw new OperatorException(e);
        }

        createTargetProduct(temporaryFolder);

        this.tileSegmenter = buildTileSegmenter(temporaryFolder, new TileSegmenterMetadata());

        //TODO Jean remove
        Logger.getLogger("org.esa.s2tbx.grm").setLevel(Level.FINE);

        if (logger.isLoggable(Level.FINE)) {
            long startTime = System.currentTimeMillis();
            int imageWidth = this.tileSegmenter.getImageWidth();
            int imageHeight = this.tileSegmenter.getImageHeight();
            int tileWidth = this.tileSegmenter.getTileWidth();
            int tileHeight = this.tileSegmenter.getTileHeight();
            int tileMargin = this.tileSegmenter.computeTileMargin();
            int firstNumberOfIterations = this.tileSegmenter.getIterationsForEachFirstSegmentation();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start Segmentation: image width: " + imageWidth + ", image height: " + imageHeight + ", tile width: " + tileWidth + ", tile height: " + tileHeight + ", margin: " + tileMargin + ", first number of iterations: " + firstNumberOfIterations + ", start time: " + new Date(startTime));
        }
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle targetRectangle = targetTile.getRectangle();
        ProcessingTile currentTile = this.tileSegmenter.buildTile(targetRectangle.x, targetRectangle.y, targetRectangle.width, targetRectangle.height);
        Tile[] sourceTiles = getSourceTiles(currentTile.getRegion());
        try {
            this.tileSegmenter.runOneTileFirstSegmentation(sourceTiles, currentTile);
        } catch (Exception ex) {
            throw new OperatorException(ex);
        }
    }

    public String getMergingCostCriterion() {
        return mergingCostCriterion;
    }

    public String getRegionMergingCriterion() {
        return regionMergingCriterion;
    }

    public int getTotalIterationsForSecondSegmentation() {
        return totalIterationsForSecondSegmentation;
    }

    public float getThreshold() {
        return threshold;
    }

    public float getShapeWeight() {
        return shapeWeight;
    }

    public float getSpectralWeight() {
        return spectralWeight;
    }

    private void createTargetProduct(File temporaryFolder) {
        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        //TODO Jean remove
        tileSize = new Dimension(1876, 3567);

        this.targetProduct = new Product(this.sourceProduct.getName() + "_grm", this.sourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);

        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        this.targetProduct.setFileLocation(temporaryFolder);
    }

    private Tile[] getSourceTiles(BoundingBox tileRegion) {
        Tile[] sourceTiles = new Tile[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            sourceTiles[i] = getSourceTile(band, rectangleToRead);
        }
        return sourceTiles;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(FirstTileSegmentationGRMOp.class);
        }
    }
}
