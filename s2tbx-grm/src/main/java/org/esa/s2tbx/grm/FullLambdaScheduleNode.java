package org.esa.s2tbx.grm;

/**
 * @author Jean Coravu
 */
public class FullLambdaScheduleNode extends Node {
    private final float[] means;

    public FullLambdaScheduleNode(int id, int upperLeftX, int upperLeftY, int numberOfComponentsPerPixel) {
        super(id, upperLeftX, upperLeftY);

        this.means = new float[numberOfComponentsPerPixel];
    }

    @Override
    public void initData(int index, float pixel) {
        this.means[index] = pixel;
    }

    @Override
    public void updateSpecificAttributes(Node nn2) {
        FullLambdaScheduleNode n2 = (FullLambdaScheduleNode)nn2;
        float a1 = getArea();
        float a2 = n2.getArea();
		float a_sum = a1 + a2;

        for (int b=0; b<this.means.length; b++) {
            this.means[b] = (a1 * getMeansAt(b) + a2 * n2.getMeansAt(b)) / a_sum;
        }
    }

    public float getMeansAt(int index) {
        return this.means[index];
    }

    public int getNumberOfComponentsPerPixel() {
        return this.means.length;
    }
}
