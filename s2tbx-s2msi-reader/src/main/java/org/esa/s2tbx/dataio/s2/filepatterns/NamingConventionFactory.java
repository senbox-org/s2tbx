package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.l1c.L1cNamingConventionSAFE;
import org.esa.s2tbx.dataio.s2.l1c.L1cNamingConventionSAFECompactSingle;
import org.esa.s2tbx.dataio.s2.ortho.S2ProductCRSCacheEntry;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 19/10/2016.
 */
public class NamingConventionFactory {
    public static INamingConvention createNamingConvention(Path path, String level) {

        INamingConvention namingConvention = null;
        String filename = path.getFileName().toString();
        if(level.equals("L1C")) {
            if(L1cNamingConventionSAFE.productMatches(filename) || L1cNamingConventionSAFE.granuleMatches(filename)) {
                namingConvention = new L1cNamingConventionSAFE();
            } else if (L1cNamingConventionSAFECompactSingle.productMatches(filename) || L1cNamingConventionSAFECompactSingle.granuleMatches(filename)) {
                namingConvention = new L1cNamingConventionSAFECompactSingle();
            }
            //TODO
        } else if(level.equals("L2A")) {
            //TODO
        }else if (level.equals("L3")) {
            //TODO
        } else {
            //TODO
        }
        return namingConvention;
    }

    public static ArrayList<S2FileNamingTemplate> getXmlProductsOrthoTemplate (){
        ArrayList<S2FileNamingTemplate> templates = new ArrayList<>();
        //templates.add(L1cNamingConventionSAFE.getStaticProductXmlTemplate());
        //templates.add(L1cNamingConventionSAFECompactSingle.getStaticProductXmlTemplate());
        return templates;
    }

    public static ArrayList<Pattern> getPatterns (S2Config.Sentinel2ProductLevel level) {
        ArrayList<Pattern> patterns = new ArrayList<>();
        switch (level) {
            case L1B:
                break;

            case L1C:
                patterns.addAll(L1cNamingConventionSAFE.getInputPatterns());
                patterns.addAll(L1cNamingConventionSAFECompactSingle.getInputPatterns());
                break;

            case L2A:

                break;
            case L3:

                break;

            default:

                break;
        }
        return patterns;

    }

    public static S2ProductCRSCacheEntry getCacheEntry(Path path) {
        //TODO
        return L1cNamingConventionSAFE.getCacheEntry(path);
    }

    public static File preprocessInput (File file) {
        //TODO
    }
}
