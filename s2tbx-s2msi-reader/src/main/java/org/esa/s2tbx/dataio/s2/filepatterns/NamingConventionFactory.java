package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.s2tbx.dataio.s2.l1c.L1cNamingConventionSAFE;
import org.esa.s2tbx.dataio.s2.l1c.L1cNamingConventionSAFECompactSingle;

import java.nio.file.Path;
import java.util.ArrayList;

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
        templates.add(L1cNamingConventionSAFE.getStaticProductXmlTemplate());
        templates.add(L1cNamingConventionSAFECompactSingle.getStaticProductXmlTemplate());
        return templates;
    }
}
