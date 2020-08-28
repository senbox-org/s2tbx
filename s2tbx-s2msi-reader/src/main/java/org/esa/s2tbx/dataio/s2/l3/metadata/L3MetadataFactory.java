package org.esa.s2tbx.dataio.s2.l3.metadata;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by obarrile on 07/10/2016.
 */
public class L3MetadataFactory {
    public static IL3ProductMetadata createL3ProductMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L3ProductMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL3GranuleMetadata createL3GranuleMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L3GranuleMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL3DatastripMetadata createL3DatastripMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 14 || psd == 13 || psd == 12 || psd == 0 )  {
            return L3DatastripMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }
}
