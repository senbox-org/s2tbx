package org.esa.s2tbx.dataio.s2.l1b.metadata;

import org.locationtech.jts.geom.Coordinate;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.core.datamodel.MetadataElement;

import java.util.List;
import java.util.Map;

/**
 * Created by obarrile on 07/10/2016.
 */
public interface IL1bGranuleMetadata {
    List<Coordinate> getGranuleCorners();
    String getGranuleID();
    String getDetectorID();
    Map<S2SpatialResolution, S2Metadata.TileGeometry> getGranuleGeometries(S2Config config);

    //To use only if the associated user product metadata is not available
    S2Metadata.ProductCharacteristics getTileProductOrganization();

    MetadataElement getMetadataElement();
    MetadataElement getSimplifiedMetadataElement();

    String getFormat();
}
