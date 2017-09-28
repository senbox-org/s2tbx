package org.esa.s2tbx.grm;

import org.esa.s2tbx.grm.segmentation.AbstractSegmenter;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.OutputMarkerMatrixHelper;
import org.esa.s2tbx.grm.segmentation.OutputMaskMatrixHelper;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.s2tbx.grm.segmentation.TileDataSourceImpl;
import org.esa.s2tbx.grm.segmentation.tiles.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.*;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.utils.matrix.IntMatrix;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.concurrent.Executor;

/**
 * @author  Jean Coravu
 */
@OperatorMetadata(
        alias = "GenericRegionMergingOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "The 'Generic Region Merging' operator computes the distinct regions from a product",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class GenericRegionMergingOp extends AbstractRegionMergingOp {
    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    public GenericRegionMergingOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        super.initialize();

        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }
        Band firstSelectedSourceBand = this.sourceProduct.getBand(this.sourceBandNames[0]);
        for (int i=1; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            if (firstSelectedSourceBand.getRasterWidth() != band.getRasterWidth() || firstSelectedSourceBand.getRasterHeight() != band.getRasterHeight()) {
                throw new OperatorException("Please select the bands with the same resolution.");
            }
        }

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.sourceProduct.getName() + "_grm", this.sourceProduct.getProductType(), "band_1", ProductData.TYPE_INT32);
        ProductUtils.copyGeoCoding(this.sourceProduct, this.targetProduct);
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

    public String[] getSourceBandNames() {
        return sourceBandNames;
    }

    @Override
    protected TileDataSource[] getSourceTiles(BoundingBox tileRegion) {
        TileDataSource[] sourceTiles = new TileDataSource[this.sourceBandNames.length];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band band = this.sourceProduct.getBand(this.sourceBandNames[i]);
            sourceTiles[i] = new TileDataSourceImpl(getSourceTile(band, rectangleToRead));
        }
        return sourceTiles;
    }

    private static OutputMaskMatrixHelper computeOutputMaskMatrix(RegionMergingProcessingParameters processingParameters, RegionMergingInputParameters inputParameters,
                                                                  SegmentationSourceProductPair segmentationSourceProducts, Path temporaryParentFolder)
                                                                  throws Exception {

        AbstractTileSegmenter tileSegmenter = buildTileSegmenter(processingParameters, inputParameters, temporaryParentFolder);
        tileSegmenter.runFirstSegmentationsInParallel(segmentationSourceProducts);
        AbstractSegmenter segmenter = tileSegmenter.runSecondSegmentationsAndMergeGraphs();

        tileSegmenter.doClose();
        WeakReference<AbstractTileSegmenter> referenceTileSegmenter = new WeakReference<AbstractTileSegmenter>(tileSegmenter);
        referenceTileSegmenter.clear();

        OutputMaskMatrixHelper outputMaskMatrixHelper = segmenter.buildOutputMaskMatrixHelper();

        segmenter.doClose();
        WeakReference<AbstractSegmenter> referenceSegmenter = new WeakReference<AbstractSegmenter>(segmenter);
        referenceSegmenter.clear();

        return outputMaskMatrixHelper;
    }

    private static OutputMarkerMatrixHelper computeOutputMarkerMatrix(RegionMergingProcessingParameters processingParameters, RegionMergingInputParameters inputParameters,
                                                                      SegmentationSourceProductPair segmentationSourceProducts, Path temporaryParentFolder)
                                                                      throws Exception {

        OutputMaskMatrixHelper outputMaskMatrixHelper = computeOutputMaskMatrix(processingParameters, inputParameters, segmentationSourceProducts, temporaryParentFolder);

        OutputMarkerMatrixHelper outputMarkerMatrix = outputMaskMatrixHelper.buildMaskMatrix();

        outputMaskMatrixHelper.doClose();
        WeakReference<OutputMaskMatrixHelper> referenceMaskMatrix = new WeakReference<OutputMaskMatrixHelper>(outputMaskMatrixHelper);
        referenceMaskMatrix.clear();

        return outputMarkerMatrix;
    }

    public static IntMatrix computeSegmentation(RegionMergingProcessingParameters processingParameters, RegionMergingInputParameters inputParameters,
                                                SegmentationSourceProductPair segmentationSourceProducts, Path temporaryParentFolder)
                                                throws Exception {

        long startTime = System.currentTimeMillis();

        // log the start message
        logStartSegmentation(startTime, processingParameters.getImageWidth(), processingParameters.getImageHeight(),
                             processingParameters.getTileWidth(), processingParameters.getTileHeight(), processingParameters.getThreadCount());

        OutputMarkerMatrixHelper outputMarkerMatrix = computeOutputMarkerMatrix(processingParameters, inputParameters, segmentationSourceProducts, temporaryParentFolder);

        IntMatrix result = outputMarkerMatrix.buildOutputMatrix();
        int graphNodeCount = outputMarkerMatrix.getGraphNodeCount();

        outputMarkerMatrix.doClose();
        WeakReference<OutputMarkerMatrixHelper> referenceMarkerMatrix = new WeakReference<OutputMarkerMatrixHelper>(outputMarkerMatrix);
        referenceMarkerMatrix.clear();

        // log the final message
        logFinishSegmentation(startTime, result.getColumnCount(), result.getRowCount(), processingParameters.getTileWidth(), processingParameters.getTileHeight(), graphNodeCount);

        return result;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(GenericRegionMergingOp.class);
        }
    }
}
