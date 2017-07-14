package org.esa.s2tbx.grm.segmentation.tiles;

import org.esa.s2tbx.grm.segmentation.*;
import org.esa.snap.utils.BufferedInputStreamWrapper;
import org.esa.snap.utils.BufferedOutputStreamWrapper;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * @author Jean Coravu
 */
public class SpringTileSegmenter extends AbstractTileSegmenter {

    public SpringTileSegmenter(int threadCount, Executor threadPool, Dimension imageSize, Dimension tileSize,
                               int totalIterationsForSecondSegmentation, float threshold, boolean fastSegmentation)
                               throws IOException {

        super(threadCount, threadPool, imageSize, tileSize, totalIterationsForSecondSegmentation, threshold, fastSegmentation);
    }

    @Override
    protected SpringNode buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel) {
        return new SpringNode(nodeId, box, contour, perimeter, area, numberOfComponentsPerPixel);
    }

    @Override
    public AbstractSegmenter buildSegmenter(float threshold) {
        return new SpringSegmenter(threshold);
    }

    @Override
    protected void writeNode(BufferedOutputStreamWrapper nodesFileStream, Node nodeToWrite) throws IOException {
        super.writeNode(nodesFileStream, nodeToWrite);

        SpringNode node = (SpringNode)nodeToWrite;
        int count = node.getNumberOfComponentsPerPixel();
        for (int i=0; i<count; i++) {
            nodesFileStream.writeFloat(node.getMeansAt(i));
        }
    }

    @Override
    protected Node readNode(BufferedInputStreamWrapper nodesFileStream) throws IOException {
        SpringNode node = (SpringNode)super.readNode(nodesFileStream);

        int count = node.getNumberOfComponentsPerPixel();
        for (int i=0; i<count; i++) {
            node.setMeansAt(i, nodesFileStream.readFloat());
        }

        return node;
    }
}
