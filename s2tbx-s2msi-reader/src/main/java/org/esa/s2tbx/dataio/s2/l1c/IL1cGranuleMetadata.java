package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.core.datamodel.MetadataElement;

import java.io.File;
import java.util.Map;

/**
 * Created by obarrile on 30/09/2016.
 */
public interface IL1cGranuleMetadata {

    S2Metadata.ProductCharacteristics getTileProductOrganization();
    Map<S2SpatialResolution, L1cMetadata.TileGeometry> getTileGeometries();
    L1cMetadata.AnglesGrid getSunGrid();
    L1cMetadata.AnglesGrid[] getAnglesGrid();
    S2Metadata.MaskFilename[] getMasks(File file);
    MetadataElement getMetadataElement();
}
