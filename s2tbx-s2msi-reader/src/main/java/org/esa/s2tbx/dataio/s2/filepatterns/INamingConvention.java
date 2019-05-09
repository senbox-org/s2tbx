package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by obarrile on 02/11/2016.
 */
public interface INamingConvention {
    String[] getProductREGEXs();
    String[] getProductXmlREGEXs();
    String[] getGranuleREGEXs();
    String[] getGranuleXmlREGEXs();
    String[] getDatastripREGEXs();
    String[] getDatastripXmlREGEXs();
    boolean matches(String filename);
    boolean hasValidStructure() throws IOException;
    VirtualPath getXmlFromDir(VirtualPath path);
    S2Config.Sentinel2InputType getInputType();
    S2Config.Sentinel2ProductLevel getProductLevel();
    Set<String> getEPSGList();
    VirtualPath getInputXml();
    VirtualPath getInputProductXml();
    S2SpatialResolution getResolution();
    String getProductName();
    boolean matchesProductMetadata(String filename);
    ArrayList<VirtualPath> getDatastripXmlPaths();
    ArrayList<VirtualPath> getGranulesXmlPaths();
    String findGranuleId(Collection<String> availableGranuleIds, String granuleFolder);
    VirtualPath findGranuleFolderFromTileId(String tileId);
    VirtualPath findXmlFromTileId(String tileID);
}
