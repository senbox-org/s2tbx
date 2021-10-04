package org.esa.s2tbx.dataio.s2.l2hf.l2f.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.core.datamodel.MetadataElement;

import java.util.Map;

/**
 * Created by fdouziech 04/2021
 */
public interface IL2fGranuleMetadata {
    //To use only if the associated user product metadata is not available
    S2Metadata.ProductCharacteristics getTileProductOrganization(VirtualPath path, S2SpatialResolution resolution);

    Map<S2SpatialResolution, S2Metadata.TileGeometry> getTileGeometries();
    String getTileID();
    String getHORIZONTAL_CS_CODE();
    String getHORIZONTAL_CS_NAME();
    int getAnglesResolution();

    S2Metadata.AnglesGrid getSunGrid();
    S2Metadata.AnglesGrid[] getViewingAnglesGrid();
    S2Metadata.MaskFilename[] getMasks(VirtualPath path);

    MetadataElement getMetadataElement();
    MetadataElement getSimplifiedMetadataElement();

    String getFormat();
}
