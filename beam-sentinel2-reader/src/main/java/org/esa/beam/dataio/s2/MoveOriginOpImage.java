package org.esa.beam.dataio.s2;

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Map;

/**
 * Decorates a source image by adding minX,minY. Serves a replacement for the
 * TranslateDescriptor, which generated data copies.
 * (experimental)
 * @author Norman Fomferra
 */
public class MoveOriginOpImage extends PointOpImage {

    protected int computeType;

    private static ImageLayout layoutHelper(RenderedImage source,
                                            int translateX, int translateY) {
        ImageLayout layout = new ImageLayout(source);
        layout.setMinX(source.getMinX() + translateX);
        layout.setMinY(source.getMinY() + translateY);
        return layout;
    }

    public MoveOriginOpImage(RenderedImage source,
                             int translateX, int translateY,
                             Map configuration) {
        // cobbleSources is irrelevant since we override getTile().
        super(source,
              layoutHelper(source, translateX, translateY),
              configuration,
              false);
    }

    public Raster computeTile(int tileX, int tileY) {
        return getSourceImage(0).getTile(tileX, tileY);
    }

    /**
     * Returns false as MoveOriginOpImage can return via computeTile()
     * tiles that are internally cached.
     */
    public boolean computesUniqueTiles() {
        return false;
    }

}

