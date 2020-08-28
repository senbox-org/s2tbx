package org.esa.s2tbx.dataio.s2.l1c.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by obarrile on 30/09/2016.
 */
public class L1cMetadataFactory {

    public static IL1cProductMetadata createL1cProductMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 ) {
            return L1cProductMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL1cGranuleMetadata createL1cGranuleMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L1cGranuleMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL1cDatastripMetadata createL1cDatastripMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 ) {
            return L1cDatastripMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

}
