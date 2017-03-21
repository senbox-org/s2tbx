package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.grm.*;

import java.io.IOException;

/**
 * @author Jean Coravu
 */
public class FullLambdaScheduleTileSegmenter extends AbstractTileSegmenter {

    public FullLambdaScheduleTileSegmenter(float threshold) {
        super(threshold);
    }

    @Override
    protected FullLambdaScheduleNode buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel) {
        return new FullLambdaScheduleNode(nodeId, box, contour, perimeter, area, numberOfComponentsPerPixel);
    }

    @Override
    protected AbstractSegmenter buildSegmenter(float threshold) {
        return new FullLambdaScheduleSegmenter(threshold);
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
