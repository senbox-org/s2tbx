package org.esa.s2tbx.grm;

/**
 * @author Jean Coravu
 */
public class Edge {
    private final Node target;
    private float cost;
    private boolean costUpdated;
    private int boundary;

    public Edge(Node target, float cost, int boundary) {
        this.target = target;
        this.cost = cost;
        this.boundary = boundary;
        this.costUpdated = false;
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
        return costUpdated;
    }

    public void setCostUpdated(boolean costUpdated) {
        this.costUpdated = costUpdated;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getCost() {
        return cost;
    }
}
