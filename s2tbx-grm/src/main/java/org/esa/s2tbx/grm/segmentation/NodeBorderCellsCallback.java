package org.esa.s2tbx.grm.segmentation;

/**
 * Created by jcoravu on 4/7/2017.
 */
public interface NodeBorderCellsCallback {

    void addBorderCellId(Node analyzedNode, int borderCellId);
}
