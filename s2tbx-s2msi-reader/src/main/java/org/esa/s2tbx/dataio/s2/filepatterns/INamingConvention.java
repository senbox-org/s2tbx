package org.esa.s2tbx.dataio.s2.filepatterns;

/**
 * Created by obarrile on 19/10/2016.
 */
public interface INamingConvention {
    String getConventionID();
    S2FileNamingTemplate getProductDirTemplate();
    S2FileNamingTemplate getProductXmlTemplate();
    S2FileNamingTemplate getDatastripDirTemplate();
    S2FileNamingTemplate getDatastripXmlTemplate();
    S2FileNamingTemplate getGranuleDirTemplate();
    S2FileNamingTemplate getGranuleXmlTemplate();
}
