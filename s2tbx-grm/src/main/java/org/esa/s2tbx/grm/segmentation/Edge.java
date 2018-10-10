package org.esa.s2tbx.grm.segmentation;

/**
 * @author Jean Coravu
 */
public class Edge {
    private final Node target;
    private float cost;
    private byte costUpdated;
    private int boundary;

    Edge(Node target, int boundary) {
        if (target == null) {
            throw new NullPointerException("The target node cannot be null.");
        }
        this.target = target;
        this.boundary = boundary;
        this.costUpdated = 0; // false
        this.cost = 0.0f;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + this.hashCode() + "[target="+this.target.toString()+"]";
    }

    public Node getTarget() {
        return target;
    }

    public int getBoundary() {
        return boundary;
    }

    public void setBoundary(int boundary) {
        this.boundary = boundary;
    }

    public boolean isCostUpdated() {
        return (this.costUpdated != (byte)0);
    }

    public void setCostUpdated(boolean costUpdated) {
        this.costUpdated = costUpdated ? (byte)1 : (byte)0;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getCost() {
        return cost;
    }
}
