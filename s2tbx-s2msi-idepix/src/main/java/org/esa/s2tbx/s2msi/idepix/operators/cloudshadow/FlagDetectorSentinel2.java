package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.snap.core.gpf.Tile;

import java.awt.*;

/**
 * todo: add comment
 * todo: we should try to get rid of this
 *
 */
public class FlagDetectorSentinel2 implements FlagDetector {
    private int[] classifData;
    private int[] bufferData;
    private int roiWidth;

    FlagDetectorSentinel2(Tile classifSourceTile, Tile bufferSourceTile, Rectangle roi) {
        classifData = classifSourceTile.getSamplesInt();
        if (bufferSourceTile != null) {
            bufferData = bufferSourceTile.getSamplesInt();
        }
        roiWidth = roi.width;
    }

    @Override
    public boolean isLand(int x, int y) {
        final int sample = classifData[y * roiWidth + x];
        return (sample & S2IdepixConstants.IDEPIX_LAND) != 0;
    }

    @Override
    public boolean isCloud(int x, int y) {
        final int classifSample = classifData[y * roiWidth + x];
        return ((classifSample & S2IdepixConstants.IDEPIX_CLOUD) != 0 || (classifSample & S2IdepixConstants.IDEPIX_CLOUD_BUFFER) != 0);
    }

    @Override
    public boolean isCloudBuffer(int x, int y) {
        if (bufferData != null) {
            final int bufferSample = bufferData[y * roiWidth + x];
            return ((bufferSample & S2IdepixConstants.IDEPIX_CLOUD_BUFFER) != 0);
        } else {
            return  false;
        }
    }

    @Override
    public boolean isInvalid(int x, int y) {
        final int sample = classifData[y * roiWidth + x];
        return (sample & 1) != 0;
    }
}

