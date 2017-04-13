package org.esa.s2tbx.grm.segmentation;

/**
 * @author Jean Coravu
 */
public class BaatzSchapeSegmenter extends AbstractSegmenter {
    private final float spectralWeight;
    private final float shapeWeight;

    public BaatzSchapeSegmenter(float spectralWeight, float shapeWeight, float threshold) {
        super(threshold * threshold);

        this.spectralWeight = spectralWeight;
        this.shapeWeight = shapeWeight;
    }

    @Override
    protected BaatzSchapeNode buildNode(int id, int upperLeftX, int upperLeftY, int numberOfComponentsPerPixel) {
        return new BaatzSchapeNode(id, upperLeftX, upperLeftY, numberOfComponentsPerPixel);
    }

    @Override
    protected float computeMergingCost(Node nn1, Node nn2) {
        BaatzSchapeNode n1 = (BaatzSchapeNode)nn1;
        BaatzSchapeNode n2 = (BaatzSchapeNode)nn2;

        int a1 = n1.getArea();
        int a2 = n2.getArea();
        float a_sum = a1 + a2;

        float spectralCost = 0.0f;
        int numberOfComponentsPerPixel = n1.getNumberOfComponentsPerPixel();
        for (int b = 0; b < numberOfComponentsPerPixel; b++) {
            float mean = ((a1 * n1.getMeansAt(b)) + (a2 * n2.getMeansAt(b))) / a_sum;
            float squareMean = n1.getSquareMeansAt(b) + n2.getSquareMeansAt(b);
            float sum = n1.getSpectralSumAt(b) + n2.getSpectralSumAt(b);
            float std = (float)Math.sqrt((squareMean - 2*mean*sum + a_sum * mean* mean) / a_sum);
            spectralCost += (a_sum * std - a1 * n1.getStdAt(b) - a2 * n2.getStdAt(b));
        }
        spectralCost *= this.spectralWeight;

        if (spectralCost < this.threshold) {
            // compute the shape merging cost
			float p1 = n1.getPerimeter();
			float p2 = n2.getPerimeter();
            int boundary = n1.findEdge(n2).getBoundary();

            float p3 = p1 + p2 - 2 * boundary;

			BoundingBox merged_bbox = AbstractSegmenter.mergeBoundingBoxes(n1.getBox(), n2.getBox());

			float bb1_perimeter = (2*n1.getBox().getWidth() + 2*n1.getBox().getHeight());

			float bb2_perimeter = (2*n2.getBox().getWidth() + 2*n2.getBox().getHeight());
			float mbb_perimeter = (2 * merged_bbox.getWidth() + 2 * merged_bbox.getHeight());

            float smooth_f = a_sum*p3/mbb_perimeter - a1*p1/bb1_perimeter - a2*p2/bb2_perimeter;

            float compact_f = (float)(a_sum*p3/Math.sqrt(a_sum) - a1*p1/Math.sqrt(a1) - a2*p2/Math.sqrt(a2));

            float shape_cost = this.shapeWeight * compact_f + (1.0f - this.shapeWeight) * smooth_f;

            spectralCost = (float)(spectralCost + (1.0f - this.spectralWeight) * shape_cost);
        }

        return spectralCost;
    }
}
