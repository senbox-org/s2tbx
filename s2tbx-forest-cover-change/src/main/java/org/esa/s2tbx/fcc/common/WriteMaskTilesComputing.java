package org.esa.s2tbx.fcc.common;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Jean Coravu
 */
public class WriteMaskTilesComputing extends AbstractWriteMasksTilesComputing {

    private final Band band;

    public WriteMaskTilesComputing(Band band, int tileWidth, int tileHeight, Path temporaryParentFolder) throws IOException {
        super(band.getRasterWidth(), band.getRasterHeight(), tileWidth, tileHeight, temporaryParentFolder);

        this.band = band;
    }

    @Override
    protected boolean isValidMaskPixel(int x, int y) {
        return (this.band.getSampleInt(x, y) != ForestCoverChangeConstants.NO_DATA_VALUE);
    }
}