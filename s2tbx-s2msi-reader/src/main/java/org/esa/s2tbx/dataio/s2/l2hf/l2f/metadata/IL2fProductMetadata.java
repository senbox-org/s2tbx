package org.esa.s2tbx.dataio.s2.l2hf.l2f.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.snap.core.datamodel.MetadataElement;

import java.util.Collection;

/**
 * Created by fdouziech 04/2021
 */
public interface IL2fProductMetadata {
    L2fMetadata.ProductCharacteristics getProductOrganization(VirtualPath path, S2SpatialResolution resolution);
    Collection<String> getTiles();
    String[] getGranules();
    S2DatastripFilename getDatastrip();
    S2DatastripDirFilename getDatastripDir();
    MetadataElement getMetadataElement();
    String getFormat();
}
