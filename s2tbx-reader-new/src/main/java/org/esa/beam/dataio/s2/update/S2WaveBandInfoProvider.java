package org.esa.beam.dataio.s2.update;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tonio Fincke
 * @author Norman Fomferra
 */
public class S2WaveBandInfoProvider {

    private static final Map<String, S2WavebandInfo> s2_waveband_infos = new HashMap<String, S2WavebandInfo>();

    static {
        s2_waveband_infos.put("B01", new S2WavebandInfo(0, 443, 20, 1895.27, 3413, 1.030577302));
        s2_waveband_infos.put("B02", new S2WavebandInfo(1, 490, 65, 1962.16, 3413, 1.030577302));
        s2_waveband_infos.put("B03", new S2WavebandInfo(2, 560, 35, 1822.88, 3413, 1.030577302));
        s2_waveband_infos.put("B04", new S2WavebandInfo(3, 665, 30, 1511.88, 3413, 1.030577302));
        s2_waveband_infos.put("B05", new S2WavebandInfo(4, 705, 15, 1420.58, 3413, 1.030577302));
        s2_waveband_infos.put("B06", new S2WavebandInfo(5, 740, 15, 1292.17, 3413, 1.030577302));
        s2_waveband_infos.put("B07", new S2WavebandInfo(6, 775, 20, 1165.87, 3413, 1.030577302));
        s2_waveband_infos.put("B08", new S2WavebandInfo(7, 842, 115, 1037.44, 3413, 1.030577302));
        s2_waveband_infos.put("B8A", new S2WavebandInfo(8, 865, 20, 959.53, 3413, 1.030577302));
        s2_waveband_infos.put("B09", new S2WavebandInfo(9, 940, 20, 814.1, 3413, 1.030577302));
        s2_waveband_infos.put("B10", new S2WavebandInfo(10, 1380, 30, 363.67, 3413, 1.030577302));
        s2_waveband_infos.put("B11", new S2WavebandInfo(11, 1610, 90, 246.28, 3413, 1.030577302));
        s2_waveband_infos.put("B12", new S2WavebandInfo(12, 2190, 180, 86.98, 3413, 1.030577302));
    }

    static boolean hasWaveBandInfo(String name) {
        return s2_waveband_infos.containsKey(name);
    }

    static S2WavebandInfo getWaveBandInfo(String name) {
        return s2_waveband_infos.get(name);
    }

}
