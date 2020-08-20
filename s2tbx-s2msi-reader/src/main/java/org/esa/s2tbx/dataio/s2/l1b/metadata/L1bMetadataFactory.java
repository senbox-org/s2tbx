package org.esa.s2tbx.dataio.s2.l1b.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by obarrile on 10/10/2016.
 */
public class L1bMetadataFactory {

    public static IL1bProductMetadata createL1bProductMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L1bProductMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL1bGranuleMetadata createL1bGranuleMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L1bGranuleMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL1bDatastripMetadata createL1bDatastripMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L1bDatastripMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }
}
