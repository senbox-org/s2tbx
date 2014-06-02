package org.esa.beam.dataio.s2.update;

import org.esa.beam.util.ArrayUtils;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.esa.beam.dataio.s2.update.S2Config.TILE_LAYOUTS;

/**
 * Represents information of a Sentinel 2 band
 *
 * @author Tonio Fincke
 * @author Norman Fomferra
 */
public class BandInfo {

    final Map<String, File> tileIdToFileMap;
    final int bandIndex;
    String bandName;
    final S2WavebandInfo wavebandInfo;
    final S2SpatialResolution resolution;
    final TileLayout imageLayout;
    final boolean isMask;

    //todo insert something meaningful instead of xxx
    String[] bandNames = new String[]{"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B8a", "B9", "B10", "B11",
            "B12", "xxx", "AOT", "DEM", "WVP", "CLOUDS", "TECQUA", "LANWAT", "DETFOO", "DEFECT", "SATURA", "NODATA",
            "CLD", "SNW", "L2A"};

    BandInfo(String tileId, File imageFile, int bandIndex, S2WavebandInfo wavebandInfo,
             S2SpatialResolution resolution, boolean isMask) {
        this.tileIdToFileMap = Collections.unmodifiableMap(createFileMap(tileId, imageFile));
        this.bandIndex = bandIndex;
        this.bandName = bandNames[bandIndex];
        this.wavebandInfo = wavebandInfo;
        this.imageLayout = TILE_LAYOUTS[resolution.id];
        this.resolution = resolution;
        this.isMask = isMask;
    }

    public BandInfo(String tileId, File imageFile, String bandName, S2WavebandInfo wavebandInfo,
             S2SpatialResolution resolution, boolean isMask) {
        this.tileIdToFileMap = Collections.unmodifiableMap(createFileMap(tileId, imageFile));
        this.bandIndex = ArrayUtils.getElementIndex(bandName, bandNames);
        this.bandName = bandName;
        this.wavebandInfo = wavebandInfo;
        this.imageLayout = TILE_LAYOUTS[resolution.id];
        this.resolution = resolution;
        this.isMask = isMask;
    }

    private static Map<String, File> createFileMap(String tileId, File imageFile) {
        Map<String, File> tileIdToFileMap = new HashMap<String, File>();
        tileIdToFileMap.put(tileId, imageFile);
        return tileIdToFileMap;
    }

}
