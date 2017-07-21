package org.esa.snap.utils;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.math.MathUtils;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author  Jean Coravu
 */
public abstract class AbstractTilesComputingOp extends Operator {
    @TargetProduct
    protected Product targetProduct;

    private AtomicInteger processingTiles;
    private AtomicInteger processedTiles;
    private int totalTileCount;

    protected AbstractTilesComputingOp() {
    }

    @Override
    public final void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle targetRectangle = targetTile.getRectangle();
        Dimension tileSize = this.targetProduct.getPreferredTileSize();
        int tileColumnIndex = targetRectangle.x / tileSize.width;
        int tileRowIndex = targetRectangle.y / tileSize.height;

        int startProcessingTileCount = this.processingTiles.incrementAndGet();
        if (startProcessingTileCount == 1) {
            beforeProcessingFirstTile(targetBand, targetTile, pm, tileRowIndex, tileColumnIndex);
        }

        try {
            processTile(targetBand, targetTile, pm, tileRowIndex, tileColumnIndex);
        } finally {
            synchronized (this.processedTiles) {
                int finishProcessingTileCount = this.processedTiles.incrementAndGet();
                if (finishProcessingTileCount == this.totalTileCount) {
                    this.processedTiles.notifyAll();
                }
            }
        }

        if (startProcessingTileCount == this.totalTileCount) {
            synchronized (this.processedTiles) {
                if (this.processedTiles.get() < this.totalTileCount) {
                    try {
                        this.processedTiles.wait();
                    } catch (InterruptedException e) {
                        throw new OperatorException(e);
                    }
                }
            }

            afterProcessedLastTile(targetBand, targetTile, pm, tileRowIndex, tileColumnIndex);
        }
    }

    protected void initTargetProduct(int sceneWidth, int sceneHeight, String productName, String productType, String bandName, int bandDataType) {
        this.targetProduct = new Product(productName, productType, sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(JAI.getDefaultTileSize());

        Band targetBand = new Band(bandName, bandDataType, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        initTiles();
    }

    private void initTiles() {
        this.processingTiles = new AtomicInteger(0);
        this.processedTiles = new AtomicInteger(0);

        int sceneWidth = this.targetProduct.getSceneRasterWidth();
        int sceneHeight = this.targetProduct.getSceneRasterHeight();
        Dimension tileSize = this.targetProduct.getPreferredTileSize();

        int tileCountX = MathUtils.ceilInt(sceneWidth / (double) tileSize.width);
        int tileCountY = MathUtils.ceilInt(sceneHeight / (double) tileSize.height);
        this.totalTileCount = tileCountX * tileCountY;
    }

    protected void beforeProcessingFirstTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) {
    }

    protected void afterProcessedLastTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) {
    }

    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws OperatorException {
    }
}
