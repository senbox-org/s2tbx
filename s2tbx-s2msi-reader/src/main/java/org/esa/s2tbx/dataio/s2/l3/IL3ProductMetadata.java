package org.esa.s2tbx.dataio.s2.l3;

import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.snap.core.datamodel.MetadataElement;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Created by obarrile on 07/10/2016.
 */
public interface IL3ProductMetadata {
    S2Metadata.ProductCharacteristics getProductOrganization(Path path, S2SpatialResolution resolution);
    Collection<String> getTiles();
    S2DatastripFilename getDatastrip();
    S2DatastripDirFilename getDatastripDir();
    MetadataElement getMetadataElement();
    String getFormat();
}
