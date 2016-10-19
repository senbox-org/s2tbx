package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingItems;
import org.esa.snap.core.datamodel.MetadataElement;

import java.util.HashMap;

/**
 * Created by obarrile on 30/09/2016.
 */
public interface IL1cDatastripMetadata {
    MetadataElement getMetadataElement();
    HashMap<S2NamingItems,String> getNamingItems();
}
