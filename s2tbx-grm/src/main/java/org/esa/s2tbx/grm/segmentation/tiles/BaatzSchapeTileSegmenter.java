package org.esa.s2tbx.grm.segmentation.tiles;

import org.esa.s2tbx.grm.RegionMergingProcessingParameters;
import org.esa.s2tbx.grm.segmentation.*;
import org.esa.snap.utils.BufferedInputStreamWrapper;
import org.esa.snap.utils.BufferedOutputStreamWrapper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executor;

/**
 * @author Jean Coravu
 */
public class BaatzSchapeTileSegmenter extends AbstractTileSegmenter {
    private final float spectralWeight;
    private final float shapeWeight;

    public BaatzSchapeTileSegmenter(RegionMergingProcessingParameters processingParameters, int totalIterationsForSecondSegmentation,
                                    float threshold, boolean fastSegmentation, float spectralWeight, float shapeWeight, Path temporaryParentFolder)
                                    throws IOException {

        super(processingParameters, totalIterationsForSecondSegmentation, threshold, fastSegmentation, temporaryParentFolder);

        this.spectralWeight = spectralWeight;
        this.shapeWeight = shapeWeight;
    }

    @Override
    protected BaatzSchapeNode buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel) {
        return new BaatzSchapeNode(nodeId, box, contour, perimeter, area, numberOfComponentsPerPixel);
    }

    @Override
    public AbstractSegmenter buildSegmenter(float threshold) {
        return new BaatzSchapeSegmenter(this.spectralWeight, this.shapeWeight, threshold);
    }

    @Override
    protected void writeNode(BufferedOutputStreamWrapper nodesFileStream, Node nodeToWrite) throws IOException {
        super.writeNode(nodesFileStream, nodeToWrite);

        BaatzSchapeNode node = (BaatzSchapeNode)nodeToWrite;
        int count = node.getNumberOfComponentsPerPixel();
        for (int i=0; i<count; i++) {
            nodesFileStream.writeFloat(node.getMeansAt(i));
            nodesFileStream.writeFloat(node.getSpectralSumAt(i));
            nodesFileStream.writeFloat(node.getSquareMeansAt(i));
            nodesFileStream.writeFloat(node.getStdAt(i));
        }
    }

    @Override
    protected Node readNode(BufferedInputStreamWrapper nodesFileStream) throws IOException {
        BaatzSchapeNode node = (BaatzSchapeNode)super.readNode(nodesFileStream);

        int count = node.getNumberOfComponentsPerPixel();
        for (int i=0; i<count; i++) {
            node.setMeansAt(i, nodesFileStream.readFloat());
            node.setSpectralSumAt(i, nodesFileStream.readFloat());
            node.setSquareMeansAt(i, nodesFileStream.readFloat());
            node.setStdAt(i, nodesFileStream.readFloat());
        }

        return node;
    }
}
