package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.snap.core.gpf.Tile;

import java.awt.*;

/**
 * todo: add comment
 * todo: we should try to get rid of this
 *
 */
class FlagDetectorSentinel2 implements FlagDetector {
    private int[] classifData;
    private int[] bufferData;
    private int roiWidth;
    private final int invalid_byte;
    private final int cloud_ambiguous_byte;
    private final int cloud_sure_byte;
    private final int cloud_byte;
    private final int cloud_buffer_byte;
    private final int land_byte;

    FlagDetectorSentinel2(Tile classifSourceTile, Tile bufferSourceTile, Rectangle roi) {
        invalid_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_INVALID);
        cloud_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_CLOUD);
        cloud_ambiguous_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_CLOUD_AMBIGUOUS);
        cloud_sure_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_CLOUD_SURE);
        cloud_buffer_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_CLOUD_BUFFER);
        land_byte = (int) Math.pow(2, S2IdepixConstants.IDEPIX_LAND);
        classifData = classifSourceTile.getSamplesInt();
        if (bufferSourceTile != null) {
            bufferData = bufferSourceTile.getSamplesInt();
        }
        roiWidth = roi.width;
    }

    @Override
    public boolean isLand(int x, int y) {
        final int sample = classifData[y * roiWidth + x];
        return (sample & land_byte) != 0;
    }

    @Override
    public boolean isCloud(int x, int y) {
        final int classifSample = classifData[y * roiWidth + x];
        return ((classifSample & cloud_byte) != 0 || (classifSample & cloud_ambiguous_byte) != 0 ||
                (classifSample & cloud_sure_byte) != 0);
    }

    @Override
    public boolean isCloudBuffer(int x, int y) {
        if (bufferData != null) {
            final int bufferSample = bufferData[y * roiWidth + x];
            return (bufferSample & cloud_buffer_byte) != 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean isInvalid(int x, int y) {
        final int sample = classifData[y * roiWidth + x];
        return (sample & invalid_byte) != 0;
    }
}

