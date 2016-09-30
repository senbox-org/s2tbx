package org.esa.s2tbx.dataio.s2.l1c;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by obarrile on 30/09/2016.
 */
public class L1cMetadataFactory {

    public static IL1cProductMetadata createL1cProductMetadata(Path metadataPath, String psd) throws IOException {
        if(psd.equals("PSD13")) {

            return L1cProductMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL1cGranuleMetadata createL1cGranuleMetadata(Path metadataPath, String psd) throws IOException {
        if(psd.equals("PSD13")) {

            return L1cGranuleMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

    public static IL1cDatastripMetadata createL1cDatastripMetadata(Path metadataPath, String psd) throws IOException {
        if(psd.equals("PSD13")) {

            return L1cDatastripMetadataPSD13.create(metadataPath);
        } else {
            //TODO
            return null;
        }
    }

}
