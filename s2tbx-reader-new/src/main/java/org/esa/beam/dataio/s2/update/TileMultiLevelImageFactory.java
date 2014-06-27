package org.esa.beam.dataio.s2.update;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;

import java.awt.geom.AffineTransform;
import java.io.File;

/**
 *
 * @author Norman Fomferra
 */
public class TileMultiLevelImageFactory extends MultiLevelImageFactory {

    public TileMultiLevelImageFactory(AffineTransform imageToModelTransform) {
        super(imageToModelTransform);
    }

    public MultiLevelImage createSourceImage(BandInfo bandInfo) {
        return new DefaultMultiLevelImage(new TileMultiLevelSource(bandInfo, imageToModelTransform));
    }
}

