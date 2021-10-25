package org.esa.s2tbx.dataio.s2.l2a.metadata;

import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by obarrile on 05/10/2016.
 * update 15/10/2021 fdouziech
 */
public class L2aMetadataFactory {
    public static IL2aProductMetadata createL2aProductMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getFullPSDversion(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L2aProductMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD13());
        } else if (psd == 143) {
            return L2aProductMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD143());
        }else if (psd > 147) {
            return L2aProductMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD148());
        } else {
            //TODO
            return null;
        }
    }

    public static IL2aGranuleMetadata createL2aGranuleMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getFullPSDversion(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L2aGranuleMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD13());
        } else if (psd == 143) {
            return L2aGranuleMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD143());
        } else if (psd > 147) {
            return L2aGranuleMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD148());
        }else {
            //TODO
            return null;
        }
    }


    public static IL2aDatastripMetadata createL2aDatastripMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getFullPSDversion(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L2aDatastripMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD13());
        } else if (psd == 143) {
            return L2aDatastripMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD143());
        } else if (psd >147) {
            return L2aDatastripMetadataGenericPSD.create(metadataPath, new L2aMetadataPathsProviderPSD148());
        } else {
            //TODO
            return null;
        }
    }


}
