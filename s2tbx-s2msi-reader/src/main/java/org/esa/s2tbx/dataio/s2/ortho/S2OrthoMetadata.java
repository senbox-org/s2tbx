package org.esa.s2tbx.dataio.s2.ortho;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.VirtualPath;

public abstract class S2OrthoMetadata extends S2Metadata {

    private VirtualPath path;
    private String granuleName;
    private boolean foundProductMetadata;

    protected S2OrthoMetadata(VirtualPath path, String granuleName, boolean foundProductMetadata, S2Config config) {
        super(config);

        this.path = path;
        this.granuleName = granuleName;
        this.foundProductMetadata = foundProductMetadata;
    }

    public VirtualPath getPath() {
        return path;
    }

    public String getGranuleName() {
        return granuleName;
    }

    public boolean isFoundProductMetadata() {
        return foundProductMetadata;
    }
}
