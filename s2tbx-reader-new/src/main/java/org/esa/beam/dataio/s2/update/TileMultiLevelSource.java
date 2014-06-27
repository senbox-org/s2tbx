package org.esa.beam.dataio.s2.update;

import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.esa.beam.util.SystemUtils;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;

import static org.esa.beam.dataio.s2.update.S2Config.TILE_LAYOUTS;

/**
 *
 * @author Norman Fomferra
 */
public class TileMultiLevelSource extends AbstractMultiLevelSource {
    final BandInfo bandInfo;
    private File cacheDir;

    public TileMultiLevelSource(BandInfo bandInfo, AffineTransform imageToModelTransform) {
        super(new DefaultMultiLevelModel(bandInfo.imageLayout.numResolutions,
                                         imageToModelTransform,
                                         TILE_LAYOUTS[0].width,
                                         TILE_LAYOUTS[0].height));
        this.bandInfo = bandInfo;
    }

    @Override
    protected RenderedImage createImage(int level) {
        File imageFile = bandInfo.tileIdToFileMap.values().iterator().next();
        checkCacheDir(imageFile);
        return TileOpImage.create(imageFile,
                                  cacheDir,
                                  null,
                                  bandInfo.imageLayout,
                                  getModel(),
                                  bandInfo.resolution,
                                  level,
                                  bandInfo.isMask);
    }

    void checkCacheDir(File productDir) {
        File dir = new File(new File(SystemUtils.getApplicationDataDir(), "s2tbx-reader/cache"),
                            productDir.getName());
        //        noinspection ResultOfMethodCallIgnored
        if(dir != cacheDir) {
            cacheDir = dir;
            cacheDir.mkdirs();
        }
//        if (!cacheDir.exists() || !cacheDir.isDirectory() || !cacheDir.canWrite()) {
//            throw new IOException("Can't access package cache directory");
//        }
    }

}
