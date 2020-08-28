package org.esa.s2tbx.dataio.readers;

import org.esa.snap.core.metadata.XmlMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcoravu on 22/1/2020.
 */
public class RastersMetadata<MetadataType extends XmlMetadata> {

    private final Map<MetadataType, Integer> rastersBandCount;

    private int maximumWidh;
    private int maximumHeight;

    public RastersMetadata() {
        this.rastersBandCount = new HashMap<>();
        this.maximumWidh = 0;
        this.maximumHeight = 0;
    }

    public void setRasterBandCount(MetadataType metadata, int bandCount) {
        this.rastersBandCount.put(metadata, bandCount);
    }

    public void setMaximumSize(int maximumWidh, int maximumHeight) {
        this.maximumWidh = maximumWidh;
        this.maximumHeight = maximumHeight;
    }

    public int getMaximumHeight() {
        return maximumHeight;
    }

    public int getMaximumWidh() {
        return maximumWidh;
    }

    public int getRasterBandCount(MetadataType metadata) {
        return this.rastersBandCount.get(metadata).intValue();
    }
}
