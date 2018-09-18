package org.esa.s2tbx.dataio.ikonos.metadata;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Class for basic band metadata operations
 *
 * @author Denisa Stefanescu
 */
public class BandMetadataUtil {

    BandMetadata[] bandsMetadata;

    public BandMetadataUtil(BandMetadata... bMetadata) {
        this.bandsMetadata = bMetadata;
    }

    public int getMaxNumColumns() {
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumColumns));
        return this.bandsMetadata[bandsMetadata.length - 1].getNumColumns();
    }

    public int getMaxNumLines() {
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumLines));
        return this.bandsMetadata[bandsMetadata.length - 1].getNumLines();
    }

    public double getMaxStepSizeX() {
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getPixelSizeX));
        return this.bandsMetadata[0].getPixelSizeX();
    }

    public double getMaxStepSizeY() {
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getPixelSizeY));
        return this.bandsMetadata[0].getPixelSizeY();
    }
}
