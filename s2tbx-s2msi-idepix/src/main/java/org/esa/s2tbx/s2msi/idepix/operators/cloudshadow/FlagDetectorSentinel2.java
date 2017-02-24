package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.gpf.Tile;

import java.awt.*;

/**
 * todo: add comment
 *
 */
public class FlagDetectorSentinel2 implements FlagDetector {
    private int[] data1;
    private int roiWidth;

    FlagDetectorSentinel2(Tile flagTile1, Rectangle roi) {
        data1 = flagTile1.getSamplesInt();
        roiWidth = roi.width;
    }

    @Override
    public boolean isLand(int x, int y) {
        final int sample = data1[y * roiWidth + x];
        return (sample & 4096) != 0;
    }

    @Override
    public boolean isCloud(int x, int y) {
        final int sample = data1[y * roiWidth + x];
        return ((sample & 2) != 0 || (sample & 16) != 0);
        //return ((sample & 1)!= 0  || (sample & 8)!= 0);
    }

//    @Override
//    public boolean isOcean(int x, int y) {
//        final int sample = data1[y * roiWidth + x];
//        return (sample & 4096) != 1;
//    }

    @Override
    public boolean isInvalid(int x, int y) {
        final int sample = data1[y * roiWidth + x];
        return (sample & 1) != 0;
    }
}

