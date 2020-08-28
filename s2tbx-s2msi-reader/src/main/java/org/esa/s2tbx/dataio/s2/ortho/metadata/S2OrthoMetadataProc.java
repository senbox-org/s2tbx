package org.esa.s2tbx.dataio.s2.ortho.metadata;

import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2MetadataProc;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoSceneLayout;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.getHSBColor;

/**
 * Created by obarrile on 06/07/2016.
 */
public class S2OrthoMetadataProc extends S2MetadataProc {

    public static S2IndexBandInformation makeTileInformation(S2SpatialResolution resolution, S2OrthoSceneLayout sceneDescription) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        int numberOfTiles = sceneDescription.getOrderedTileIds().size();
        int index = 1;
        for(String tileId : sceneDescription.getOrderedTileIds()) {
            float f;
            f = (index-1)*(float)1.0/(numberOfTiles+1);
            f = (float) 0.75 - f;
            if (f < 0) f++;
            if(S2OrthoGranuleDirFilename.create(tileId).tileNumber!=null) {
                indexList.add(S2IndexBandInformation.makeIndex(index, getHSBColor(f, (float) 1.0, (float) 1.0), S2OrthoGranuleDirFilename.create(tileId).getTileID(), tileId));
            } else {
                indexList.add(S2IndexBandInformation.makeIndex(index, getHSBColor(f, (float) 1.0, (float) 1.0), tileId, tileId));
            }
            index++;
        }
        return new S2IndexBandInformation("tile_id_" + resolution.resolution + "m", resolution, "", "Tile ID", "", indexList, "tile_" + resolution.resolution + "m_");
    }
}
