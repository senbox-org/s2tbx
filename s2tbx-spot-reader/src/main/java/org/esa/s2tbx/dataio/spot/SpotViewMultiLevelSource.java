package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.image.AbstractMosaicSubsetMultiLevelSource;

import javax.media.jai.SourcelessOpImage;
import java.awt.*;

/**
 * Created by jcoravu on 11/12/2019.
 */
public class SpotViewMultiLevelSource extends AbstractMosaicSubsetMultiLevelSource {

    private final SpotViewImageReader spotViewImageReader;

    public SpotViewMultiLevelSource(SpotViewImageReader spotViewImageReader, int dataBufferType, Rectangle imageBounds, Dimension tileSize, int bandIndex, GeoCoding geoCoding) {
        super(dataBufferType, imageBounds, tileSize, bandIndex, geoCoding);

        this.spotViewImageReader = spotViewImageReader;
    }

    @Override
    protected SourcelessOpImage buildTileOpImage(int level, Dimension currentTileSize, Point tileOffset) {
        return new SpotViewTileOpImage(this.spotViewImageReader, getModel(), this.dataBufferType, this.bandIndex, this.imageBounds, currentTileSize, tileOffset, level);
    }
}
