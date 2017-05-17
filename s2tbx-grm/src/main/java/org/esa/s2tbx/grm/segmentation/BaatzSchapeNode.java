package org.esa.s2tbx.grm.segmentation;

/**
 * @author Jean Coravu
 */
public class BaatzSchapeNode extends Node {
    private final float[] squareMeans;
    private final float[] spectralSum;
    private final float[] std;

    public BaatzSchapeNode(int id, int upperLeftX, int upperLeftY, int numberOfComponentsPerPixel) {
        super(id, upperLeftX, upperLeftY, numberOfComponentsPerPixel);

        this.squareMeans = new float[numberOfComponentsPerPixel];
        this.spectralSum = new float[numberOfComponentsPerPixel];
        this.std = new float[numberOfComponentsPerPixel];
    }

    public BaatzSchapeNode(int id, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel) {
        super(id, box, contour, perimeter, area, numberOfComponentsPerPixel);

        this.squareMeans = new float[numberOfComponentsPerPixel];
        this.spectralSum = new float[numberOfComponentsPerPixel];
        this.std = new float[numberOfComponentsPerPixel];
    }

    @Override
    public void initData(int index, float pixel) {
        super.initData(index, pixel);

        this.squareMeans[index] = pixel * pixel;
        this.spectralSum[index] = pixel;
        this.std[index] = 0.0f;
    }

    @Override
    public void updateSpecificAttributes(Node nn2) {
        BaatzSchapeNode n2 = (BaatzSchapeNode)nn2;
        float a1 = getArea();
        float a2 = n2.getArea();
        float a_sum = a1 + a2;

        for (int b=0; b<this.means.length; b++) {
            this.means[b] = (a1 * getMeansAt(b) + a2 * n2.getMeansAt(b)) / a_sum;
            this.squareMeans[b] += n2.getSquareMeansAt(b);
            this.spectralSum[b] += n2.getSpectralSumAt(b);
            this.std[b] = (float)Math.sqrt((getSquareMeansAt(b) - 2 * getMeansAt(b) * getSpectralSumAt(b)
                                                + a_sum * getMeansAt(b) * getMeansAt(b)) / a_sum);
        }
    }

    public void setSpectralSumAt(int index, float value) {
        this.spectralSum[index] = value;
    }

    public void setSquareMeansAt(int index, float value) {
        this.squareMeans[index] = value;
    }

    public void setStdAt(int index, float value) {
        this.std[index] = value;
    }

    public float getSpectralSumAt(int index) {
        return this.spectralSum[index];
    }

    public float getSquareMeansAt(int index) {
        return this.squareMeans[index];
    }

    public float getStdAt(int index) {
        return this.std[index];
    }
}
