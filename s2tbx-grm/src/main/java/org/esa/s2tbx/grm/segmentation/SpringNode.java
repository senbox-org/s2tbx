package org.esa.s2tbx.grm.segmentation;

/**
 * @author Jean Coravu
 */
public class SpringNode extends Node {

    public SpringNode(int id, int upperLeftX, int upperLeftY, int numberOfComponentsPerPixel) {
        super(id, upperLeftX, upperLeftY, numberOfComponentsPerPixel);
    }

    public SpringNode(int id, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel) {
        super(id, box, contour, perimeter, area, numberOfComponentsPerPixel);
    }

    @Override
    public void updateSpecificAttributes(Node nn2) {
        SpringNode n2 = (SpringNode)nn2;
        int a1 = getArea();
        int a2 = n2.getArea();
        float a_sum = a1 + a2;
        for (int b=0; b<this.means.length; b++) {
            this.means[b] = (a1 * getMeansAt(b) + a2 * n2.getMeansAt(b)) / a_sum;
        }
    }
}
