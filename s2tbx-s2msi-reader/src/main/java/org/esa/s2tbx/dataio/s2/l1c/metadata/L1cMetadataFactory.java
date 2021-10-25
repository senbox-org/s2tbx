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
        int psd = S2Metadata.getFullPSDversion(metadataPath);
        if(psd <148 ) {
            return L1cProductMetadataPSD13.create(metadataPath);
        } else if(psd >147 )  {
            return L1cProductMetadataPSD148.create(metadataPath);
        }else {
            //TODO
            return null;
        }
    }

    public static IL1cGranuleMetadata createL1cGranuleMetadata(VirtualPath metadataPath, VirtualPath metadataProductPath) throws IOException, ParserConfigurationException, SAXException {
        int psd= 0;
        if(metadataProductPath!=null)
            psd = S2Metadata.getFullPSDversion(metadataProductPath);
        else{
            //check if mask there are no gml format in QIDATA
            boolean gmlMaskFormat=false;
            VirtualPath maskDir = metadataPath.getParent().resolve("QI_DATA");
            VirtualPath[] pathList = maskDir.listPaths();
            if(pathList!=null)
            {
                for(VirtualPath path:pathList) {
                    if(path.getFullPathString().endsWith(".gml"))
                    {
                        gmlMaskFormat = true;
                        break;
                    }
                }
                if(!gmlMaskFormat)
                    psd=148;
            }
        }
        if(psd<148)  {
            return L1cGranuleMetadataPSD13.create(metadataPath);
        } else if(psd > 147)  {
            return L1cGranuleMetadataPSD148.create(metadataPath);
        }else {
            //TODO
            return null;
        }
    }

    public static IL1cDatastripMetadata createL1cDatastripMetadata(VirtualPath metadataPath) throws IOException, ParserConfigurationException, SAXException {
        int psd = S2Metadata.getFullPSDversion(metadataPath);
        if(psd <148) {
            return L1cDatastripMetadataPSD13.create(metadataPath);
        } else if(psd > 147 )  {
            return L1cDatastripMetadataPSD13.create(metadataPath);
        }else {
            //TODO
            return null;
        }
    }

}
