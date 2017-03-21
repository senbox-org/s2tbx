package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.grm.*;

import java.io.IOException;

/**
 * @author Jean Coravu
 */
public class SpringTileSegmenter extends AbstractTileSegmenter {

    public SpringTileSegmenter(float threshold) {
        super(threshold);
    }

    @Override
    protected SpringNode buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel) {
        return new SpringNode(nodeId, box, contour, perimeter, area, numberOfComponentsPerPixel);
    }

    @Override
    protected AbstractSegmenter buildSegmenter(float threshold) {
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
