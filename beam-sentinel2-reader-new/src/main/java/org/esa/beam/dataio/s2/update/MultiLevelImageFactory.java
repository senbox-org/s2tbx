package org.esa.beam.dataio.s2.update;

import com.bc.ceres.glevel.MultiLevelImage;

import java.awt.geom.AffineTransform;
import java.io.File;

/**
 *
 * @author Norman Fomferra
 */
public abstract class MultiLevelImageFactory {
    protected final AffineTransform imageToModelTransform;

    protected MultiLevelImageFactory(AffineTransform imageToModelTransform) {
        this.imageToModelTransform = imageToModelTransform;
    }

    public abstract MultiLevelImage createSourceImage(BandInfo bandInfo);
}
