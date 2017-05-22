package org.esa.s2tbx.grm.segmentation;

/**
 * @author Jean Coravu
 */
public class SpringSegmenter extends AbstractSegmenter {

    public SpringSegmenter(float threshold) {
        super(threshold);
    }

    @Override
    protected SpringNode buildNode(int id, int upperLeftX, int upperLeftY, int numberOfComponentsPerPixel) {
        return new SpringNode(id, upperLeftX, upperLeftY, numberOfComponentsPerPixel);
    }

    @Override
    protected float computeMergingCost(Node nn1, Node nn2) {
        SpringNode n1 = (SpringNode)nn1;
        SpringNode n2 = (SpringNode)nn2;

        float euclideanDistance = 0.0f;
        int numberOfComponentsPerPixel = n1.getNumberOfComponentsPerPixel();
        for (int b = 0; b < numberOfComponentsPerPixel; b++) {
            euclideanDistance += (n1.getMeansAt(b) - n2.getMeansAt(b)) * (n1.getMeansAt(b) - n2.getMeansAt(b));
        }

        return (float)Math.sqrt(euclideanDistance);
    }
}
