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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Jean Coravu
 */
public abstract class AbstractTilesComputingOp extends Operator {
    private static final Logger logger = Logger.getLogger(AbstractTilesComputingOp.class.getName());

    @TargetProduct
    protected Product targetProduct;

    private AtomicInteger processingTileCount;
    private AtomicInteger processedTileCount;
    private int totalTileCount;
    private Set<String> processedTiles;

    protected AbstractTilesComputingOp() {
    }

    @Override
    public final void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle targetRectangle = targetTile.getRectangle();
        Dimension tileSize = this.targetProduct.getPreferredTileSize();
        int tileRowIndex = targetRectangle.y / tileSize.height;
        int tileColumnIndex = targetRectangle.x / tileSize.width;

        String key = tileRowIndex+"|"+tileColumnIndex;
        boolean canProcessTile = false;
        synchronized (this.processedTiles) {
            canProcessTile = this.processedTiles.add(key);
        }
        if (canProcessTile) {
            int startProcessingTileCount = this.processingTileCount.incrementAndGet();
            if (startProcessingTileCount == 1) {
                beforeProcessingFirstTile(targetBand, targetTile, pm, tileRowIndex, tileColumnIndex);
            }

            try {
                processTile(targetBand, targetTile, pm, tileRowIndex, tileColumnIndex);
            } catch (Exception ex) {
                throw new OperatorException(ex);
            } finally {
                synchronized (this.processedTileCount) {
                    int finishProcessingTileCount = this.processedTileCount.incrementAndGet();
                    if (finishProcessingTileCount == this.totalTileCount) {
                        this.processedTileCount.notifyAll();
                    }
                }
            }

            if (startProcessingTileCount == this.totalTileCount) {
                synchronized (this.processedTileCount) {
                    if (this.processedTileCount.get() < this.totalTileCount) {
                        try {
                            this.processedTileCount.wait();
                        } catch (InterruptedException e) {
                            throw new OperatorException(e);
                        }
                    }
                }

                try {
                    afterProcessedLastTile(targetBand, targetTile, pm, tileRowIndex, tileColumnIndex);
                } catch (Exception e) {
                    throw new OperatorException(e);
                }
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Tile already computed: row index: "+tileRowIndex+", column index: "+tileColumnIndex+", bounds [x=" + targetRectangle.x+", y="+targetRectangle.y+", width="+targetRectangle.width+", height="+targetRectangle.height+"]");
            }
        }
    }

    protected void initTargetProduct(int sceneWidth, int sceneHeight, String productName, String productType, String bandName, int bandDataType) {
        this.targetProduct = new Product(productName, productType, sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(JAI.getDefaultTileSize());

        Band targetBand = new Band(bandName, bandDataType, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        initTiles();
    }

    protected void beforeProcessingFirstTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) {
    }

    protected void afterProcessedLastTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
    }

    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
    }

    protected final void initTiles() {
        this.processedTiles = new HashSet<String>();

        this.processingTileCount = new AtomicInteger(0);
        this.processedTileCount = new AtomicInteger(0);

        int sceneWidth = this.targetProduct.getSceneRasterWidth();
        int sceneHeight = this.targetProduct.getSceneRasterHeight();
        Dimension tileSize = this.targetProduct.getPreferredTileSize();

        int tileCountX = MathUtils.ceilInt(sceneWidth / (double) tileSize.width);
        int tileCountY = MathUtils.ceilInt(sceneHeight / (double) tileSize.height);
        this.totalTileCount = tileCountX * tileCountY;
    }
}
