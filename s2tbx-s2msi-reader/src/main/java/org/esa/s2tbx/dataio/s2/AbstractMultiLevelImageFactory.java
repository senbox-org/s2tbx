package org.esa.s2tbx.dataio.s2;

import com.bc.ceres.glevel.MultiLevelImage;

import java.awt.geom.AffineTransform;

/**
 * Created by jcoravu on 9/5/2019.
 */
public abstract class AbstractMultiLevelImageFactory<BandInfoType> {

    protected final AffineTransform imageToModelTransform;

    protected AbstractMultiLevelImageFactory(AffineTransform imageToModelTransform) {
        this.imageToModelTransform = imageToModelTransform;
    }

    public abstract MultiLevelImage createSourceImage(BandInfoType tileBandInfo);
}
