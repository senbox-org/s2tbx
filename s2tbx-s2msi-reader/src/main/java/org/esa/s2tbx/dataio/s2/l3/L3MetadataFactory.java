package org.esa.s2tbx.dataio.s2.l3;

import org.esa.s2tbx.dataio.s2.S2Metadata;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by obarrile on 07/10/2016.
 */
public class L3MetadataFactory {
    public static IL3ProductMetadata createL3ProductMetadata(Path metadataPath) throws IOException {
        String psd = S2Metadata.getPSD(metadataPath);
        if(psd.equals("PSD13") || psd.equals("PSD12") || psd.equals("DEFAULT") ) {
            return L3ProductMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL3GranuleMetadata createL3GranuleMetadata(Path metadataPath) throws IOException {
        String psd = S2Metadata.getPSD(metadataPath);
        if(psd.equals("PSD13") || psd.equals("PSD12") || psd.equals("DEFAULT") ) {
            return L3GranuleMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL3DatastripMetadata createL3DatastripMetadata(Path metadataPath) throws IOException {
        String psd = S2Metadata.getPSD(metadataPath);
        if(psd.equals("PSD13") || psd.equals("PSD12") || psd.equals("DEFAULT") ) {
            return L3DatastripMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }
}
