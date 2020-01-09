package org.esa.s2tbx.dataio.s2.l1b.tiles;

import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.esa.s2tbx.dataio.s2.l1b.L1bSceneDescription;

import java.awt.geom.AffineTransform;

/**
 * A MultiLevelSource for a scene made of multiple L1B tiles.
 */
public abstract class AbstractL1bSceneMultiLevelSource extends AbstractMultiLevelSource {

    protected final L1bSceneDescription sceneDescription;

    protected AbstractL1bSceneMultiLevelSource(L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform, int numResolutions) {
        super(new DefaultMultiLevelModel(numResolutions,
                imageToModelTransform,
                sceneDescription.getSceneRectangle().width,
                sceneDescription.getSceneRectangle().height));
        this.sceneDescription = sceneDescription;
    }
}