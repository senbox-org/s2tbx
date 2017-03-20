package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.grm.*;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by jcoravu on 14/3/2017.
 */
public class BaatzSchapeTileSegmenter extends AbstractTileSegmenter {
    private final float spectralWeight;
    private final float shapeWeight;

    public BaatzSchapeTileSegmenter(float spectralWeight, float shapeWeight, float threshold) {
        super(threshold);

        this.spectralWeight = spectralWeight;
        this.shapeWeight = shapeWeight;
    }

    @Override
    protected AbstractSegmenter buildSegmenter(float threshold) {
        return new BaatzSchapeSegmenter(this.spectralWeight, this.shapeWeight, threshold);
    }

    @Override
    protected void writeNode(RandomAccessFile nodesFileStream, Node nodeToWrite) throws IOException {
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
    protected Node readNode(RandomAccessFile nodesFileStream) throws IOException {
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

    @Override
    protected BaatzSchapeNode buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel) {
        return new BaatzSchapeNode(nodeId, box, contour, perimeter, area, numberOfComponentsPerPixel);
    }
}
