package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.grm.Node;

/**
 * Created by jcoravu on 20/3/2017.
 */
public class BorderNodeWrapper {
    private Node node;
    private int value;

    public BorderNodeWrapper(Node node, int value) {
        this.node = node;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Node getNode() {
        return node;
    }
}
