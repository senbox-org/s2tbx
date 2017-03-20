package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.grm.AbstractSegmenter;
import org.esa.s2tbx.grm.SpringSegmenter;

/**
 * Created by jcoravu on 14/3/2017.
 */
public class SpringTileSegmenter extends AbstractTileSegmenter {

    public SpringTileSegmenter(float threshold) {
        super(threshold);
    }

    @Override
    protected AbstractSegmenter buildSegmenter(float threshold) {
        return new SpringSegmenter(threshold);
    }
}
