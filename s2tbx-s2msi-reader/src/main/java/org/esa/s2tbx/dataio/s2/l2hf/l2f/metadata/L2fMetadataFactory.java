package org.esa.s2tbx.dataio.s2.l2hf.l2f.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.l2a.metadata.L2aMetadataPathsProviderPSD148;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by fdouziech 04/2021
 */
public class L2fMetadataFactory {
    public static IL2fProductMetadata createL2fProductMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = L2fMetadata.getFullPSDversion(metadataPath);
        System.out.println("PSD "+psd);
        if(psd > 145) {
            return L2fProductMetadataGenericPSD.create(metadataPath, new L2fMetadataPathsProviderPSD146());
        // } else if (psd == 148) {
        //     return L2fProductMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD148());
        }else {
            //TODO
            return null;
        }
    }

    public static IL2fGranuleMetadata createL2fGranuleMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = L2fMetadata.getFullPSDversion(metadataPath);
        if(psd > 145)  {
            return L2fGranuleMetadataGenericPSD.create(metadataPath, new L2fMetadataPathsProviderPSD146());
        // }else if (psd == 148) {
        //     return L2fGranuleMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD148());
        } else {
            //TODO
            return null;
        }
    }

}
