package org.esa.s2tbx.dataio.s2.l2a;

import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by obarrile on 05/10/2016.
 */
public class L2aMetadataFactory {
    public static IL2aProductMetadata createL2aProductMetadata(Path metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 13 || psd == 12 || psd == 0 )  {
            return L2aProductMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL2aGranuleMetadata createL2aGranuleMetadata(Path metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 13 || psd == 12 || psd == 0 )  {
            return L2aGranuleMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL2aDatastripMetadata createL2aDatastripMetadata(Path metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getPSD(metadataPath);
        if(psd == 13 || psd == 12 || psd == 0 )  {
            return L2aDatastripMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }
}
