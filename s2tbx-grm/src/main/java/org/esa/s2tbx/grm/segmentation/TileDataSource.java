package org.esa.s2tbx.grm.segmentation;

/**
 * @author  Jean Coravu
 */
public interface TileDataSource {

    float getSampleFloat(int x, int y);
}
