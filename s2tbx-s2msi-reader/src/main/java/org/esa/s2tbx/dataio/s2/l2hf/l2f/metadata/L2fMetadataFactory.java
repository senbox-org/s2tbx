package org.esa.s2tbx.dataio.s2.l2hf.l2f.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by fdouziech 04/2021
 */
public class L2fMetadataFactory {
    public static IL2fProductMetadata createL2fProductMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = L2fMetadata.getDeepPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L2fProductMetadataGenericPSD.create(metadataPath, new L2fMetadataPathsProviderPSD13());
        } else if (psd == 143) {
            return L2fProductMetadataGenericPSD.create(metadataPath, new L2fMetadataPathsProviderPSD143());
        } else {
            //TODO
            return null;
        }
    }

    public static IL2fGranuleMetadata createL2fGranuleMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = L2fMetadata.getDeepPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L2fGranuleMetadataGenericPSD.create(metadataPath, new L2fMetadataPathsProviderPSD13());
        } else if (psd == 143) {
            return L2fGranuleMetadataGenericPSD.create(metadataPath, new L2fMetadataPathsProviderPSD143());
        } else {
            //TODO
            return null;
        }
    }

}
