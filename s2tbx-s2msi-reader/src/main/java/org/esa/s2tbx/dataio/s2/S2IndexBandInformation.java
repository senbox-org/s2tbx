package org.esa.s2tbx.dataio.s2;

import org.esa.snap.core.datamodel.IndexCoding;

/**
 * @author J. Malik
 */
public class S2IndexBandInformation extends S2BandInformation {

    private IndexCoding indexCoding;

    public S2IndexBandInformation(String physicalBand,
                                  S2SpatialResolution resolution,
                                  String imageFileTemplate,
                                  IndexCoding indexCoding) {
        super(physicalBand, resolution, imageFileTemplate);
        this.indexCoding = indexCoding;
    }

    public IndexCoding getIndexCoding() {
        return this.indexCoding;
    }
}
