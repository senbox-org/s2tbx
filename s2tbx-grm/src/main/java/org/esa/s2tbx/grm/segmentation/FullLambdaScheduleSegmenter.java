package org.esa.s2tbx.grm.segmentation;

/**
 * @author Jean Coravu
 */
public class FullLambdaScheduleSegmenter extends AbstractSegmenter {

    public FullLambdaScheduleSegmenter(float threshold) {
        super(threshold);
    }

    @Override
    protected float computeMergingCost(Node nn1, Node nn2) {
        FullLambdaScheduleNode n1 = (FullLambdaScheduleNode)nn1;
        FullLambdaScheduleNode n2 = (FullLambdaScheduleNode)nn2;

        float eucDist = 0.0f;
        float a1 = n1.getArea();
        float a2 = n2.getArea();
		float a_sum = a1 + a2;
        int numberOfComponentsPerPixel = n1.getNumberOfComponentsPerPixel();
        for (int b = 0; b < numberOfComponentsPerPixel; b++) {
            eucDist += (n1.getMeansAt(b) - n2.getMeansAt(b))*(n1.getMeansAt(b) - n2.getMeansAt(b));
        }

        // retrieve the length of the boundary between n1 and n2
        Edge toN2 = n1.findEdge(n2);

        float cost = (((a1*a2)/a_sum)*eucDist) / (float)toN2.getBoundary();
        return cost;
    }

    @Override
    protected FullLambdaScheduleNode buildNode(int id, int upperLeftX, int upperLeftY, int numberOfComponentsPerPixel) {
        return new FullLambdaScheduleNode(id, upperLeftX, upperLeftY, numberOfComponentsPerPixel);
    }
}
