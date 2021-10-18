package org.esa.s2tbx.dataio.s2.l2hf.l2h.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by fdouziech
 */
public class L2hMetadataFactory {
    public static IL2hProductMetadata createL2hProductMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = L2hMetadata.getFullPSDversion(metadataPath);
        if(psd > 145 )  {
            return L2hProductMetadataGenericPSD.create(metadataPath, new L2hMetadataPathsProviderPSD146());
        }else{
            return null;
        }
    }

    public static IL2hGranuleMetadata createL2hGranuleMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = L2hMetadata.getFullPSDversion(metadataPath);
        if(psd > 145 )  {
            return L2hGranuleMetadataGenericPSD.create(metadataPath, new L2hMetadataPathsProviderPSD146());
        }else{
            return null;
        }
    }

}
