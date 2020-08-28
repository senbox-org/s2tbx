package org.esa.s2tbx.dataio.s2.l3.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.core.datamodel.MetadataElement;

import java.util.Map;

/**
 * Created by obarrile on 07/10/2016.
 */
public interface IL3GranuleMetadata {
    //To use only if the associated user product metadata is not available
    S2Metadata.ProductCharacteristics getTileProductOrganization(VirtualPath path, S2SpatialResolution resolution);

    Map<S2SpatialResolution, S2Metadata.TileGeometry> getTileGeometries();
    String getTileID();
    String getHORIZONTAL_CS_CODE();
    String getHORIZONTAL_CS_NAME();
    int getAnglesResolution();

    S2Metadata.AnglesGrid getSunGrid();
    S2Metadata.AnglesGrid[] getViewingAnglesGrid();


    MetadataElement getMetadataElement();
    MetadataElement getSimplifiedMetadataElement();

    int getMaximumMosaicIndex();

    String getFormat();
}
