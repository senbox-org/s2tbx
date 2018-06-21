package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.snap.core.gpf.Tile;

import java.awt.*;

/**
 * todo: add comment
 * todo: we should try to get rid of this
 *
 */
class FlagDetector {

    private int[] classifData;
    private int roiWidth;
    private final int invalid_byte;
    private final int cloud_ambiguous_byte;
    private final int cloud_sure_byte;
    private final int cloud_byte;
    private final int land_byte;

    FlagDetector(Tile classifSourceTile, Rectangle roi) {
        invalid_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_INVALID);
        cloud_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_CLOUD);
        cloud_ambiguous_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_CLOUD_AMBIGUOUS);
        cloud_sure_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_CLOUD_SURE);
        land_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_LAND);
        classifData = classifSourceTile.getSamplesInt();
        roiWidth = roi.width;
    }

    boolean isLand(int x, int y) {
        final int sample = classifData[y * roiWidth + x];
        return (sample & land_byte) != 0;
    }

    boolean isCloud(int x, int y) {
        final int classifSample = classifData[y * roiWidth + x];
        return ((classifSample & cloud_byte) != 0 || (classifSample & cloud_ambiguous_byte) != 0 || //(classifSample & cloud_buffer_byte)!=0 ||//(classifSample & cirrus_ambiguous_byte)!=0 ||
                (classifSample & cloud_sure_byte) != 0);
    }

    boolean isInvalid(int x, int y) {
        final int sample = classifData[y * roiWidth + x];
        return (sample & invalid_byte) != 0;
    }
}

