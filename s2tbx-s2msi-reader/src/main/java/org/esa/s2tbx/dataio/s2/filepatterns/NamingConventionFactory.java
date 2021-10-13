package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.s2tbx.dataio.s2.VirtualPath;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 02/11/2016.
 */
public class NamingConventionFactory {

    /**
     * Checks the different NamingConventions and returns an instance to the first that matches().
     * It is obtained by using exclusively the REGEX, the metadata file is not open to get the format.
     *
     * @param virtualPath
     * @return appropriated naming convention or null
     */
    public static INamingConvention createNamingConvention(VirtualPath virtualPath) {
        L1BNamingConvention l1bConvention = new L1BNamingConvention(virtualPath);
        if (l1bConvention.getInputType() != null) {
            return l1bConvention;
        }
        SAFENamingConvention safe = new SAFENamingConvention(virtualPath);
        if (safe.getInputType() != null) {
            return safe;
        }

        SAFECOMPACTNamingConvention safeCompact = new SAFECOMPACTNamingConvention(virtualPath);
        if (safeCompact.getInputType() != null) {
            return safeCompact;
        }
        return null;
    }

    public static L1BNamingConvention createL1BNamingConvention(VirtualPath virtualPath) {
        L1BNamingConvention l1bConvention = new L1BNamingConvention(virtualPath);
        if (l1bConvention.getInputType() != null) {
            return l1bConvention;
        }
        return null;
    }

    public static INamingConvention createOrthoNamingConvention(VirtualPath virtualPath) {
        SAFENamingConvention safe = new SAFENamingConvention(virtualPath);
        if (safe.getInputType() != null) {
            return safe;
        }
        SAFECOMPACTNamingConvention safeCompact = new SAFECOMPACTNamingConvention(virtualPath);
        if (safeCompact.getInputType() != null) {
            return safeCompact;
        }
        return null;
    }

    //getters L1B templates
    public static String getSpectralBandImageTemplate_L1b(String format, String bandFileId) {
        //Currently, it is always the same for all formats
        return L1BNamingConvention.SPECTRAL_BAND_TEMPLATE_L1B.replace("{{BANDFILEID}}", bandFileId);
    }

    //getters L1C templates
    public static String getSpectralBandImageTemplate_L1c(String format, String bandFileId) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.SPECTRAL_BAND_TEMPLATE_L1C.replace("{{BANDFILEID}}", bandFileId);
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.SPECTRAL_BAND_TEMPLATE_L1C.replace("{{BANDFILEID}}", bandFileId);
        }
        return null;
    }


    //getters L2A templates
    public static String getSpectralBandImageTemplate_L2a(String format, String bandFileId) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.SPECTRAL_BAND_TEMPLATE_L2A.replace("{{BANDFILEID}}", bandFileId);
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.SPECTRAL_BAND_TEMPLATE_L2A.replace("{{BANDFILEID}}", bandFileId);
        }
        return null;
    }

    public static String getSpectralBandImageTemplate_L2h(String format, String bandFileId, boolean isInNativeSubFolder) {
        if (format.equals("SAFE")) {
            if(isInNativeSubFolder)
                return SAFENamingConvention.SPECTRAL_NATIVE_BAND_TEMPLATE_L2H_PSD14.replace("{{BANDFILEID}}", bandFileId);
            else
                return SAFENamingConvention.SPECTRAL_BAND_TEMPLATE_L2H_PSD14.replace("{{BANDFILEID}}", bandFileId);
        } else if (format.equals("SAFE_COMPACT")) {
            if(isInNativeSubFolder)
                return SAFECOMPACTNamingConvention.SPECTRAL_NATIVE_BAND_TEMPLATE_L2H_PSD14.replace("{{BANDFILEID}}", bandFileId);
            else
                return SAFECOMPACTNamingConvention.SPECTRAL_BAND_TEMPLATE_L2H_PSD14.replace("{{BANDFILEID}}", bandFileId);
        }
        return null;
    }

    public static String getSpectralBandImageTemplate_L2f(String format, String bandFileId, boolean isInNativeSubFolder) {
        if (format.equals("SAFE")) {
            if(isInNativeSubFolder)
                return SAFENamingConvention.SPECTRAL_NATIVE_BAND_TEMPLATE_L2F_PSD14.replace("{{BANDFILEID}}", bandFileId);
            else
                return SAFENamingConvention.SPECTRAL_BAND_TEMPLATE_L2F_PSD14.replace("{{BANDFILEID}}", bandFileId);
        } else if (format.equals("SAFE_COMPACT")) {
            if(isInNativeSubFolder)
                return SAFECOMPACTNamingConvention.SPECTRAL_NATIVE_BAND_TEMPLATE_L2F_PSD14.replace("{{BANDFILEID}}", bandFileId);
            else
                return SAFECOMPACTNamingConvention.SPECTRAL_BAND_TEMPLATE_L2F_PSD14.replace("{{BANDFILEID}}", bandFileId);
        }
        return null;
    }


    

    public static String getAOTTemplate_L2a(String format) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.AOT_FILE_TEMPLATE_L2A;
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.AOT_FILE_TEMPLATE_L2A;
        }
        return null;
    }

    public static String getWVPTemplate_L2a(String format) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.WVP_FILE_TEMPLATE_L2A;
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.WVP_FILE_TEMPLATE_L2A;
        }
        return null;
    }

    public static String getSCLTemplate_L2a(String format, int psd) {
        if (format.equals("SAFE")) {
            if (psd >= 14) {
                return SAFENamingConvention.SCL_FILE_TEMPLATE_L2A_PSD14;
            }
            return SAFENamingConvention.SCL_FILE_TEMPLATE_L2A;
        } else if (format.equals("SAFE_COMPACT")) {
            if (psd >= 14) {
                return SAFECOMPACTNamingConvention.SCL_FILE_TEMPLATE_L2A_PSD14;
            }
            return SAFECOMPACTNamingConvention.SCL_FILE_TEMPLATE_L2A;
        }
        return null;
    }

    public static String getCLDTemplate_L2a(String format) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.CLD_FILE_TEMPLATE_L2A;
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.CLD_FILE_TEMPLATE_L2A;
        }
        return null;
    }

    public static String getSNWTemplate_L2a(String format) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.SNW_FILE_TEMPLATE_L2A;
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.SNW_FILE_TEMPLATE_L2A;
        }
        return null;
    }

    public static String getDDVTemplate_L2a(String format) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.DDV_FILE_TEMPLATE_L2A;
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.DDV_FILE_TEMPLATE_L2A;
        }
        return null;
    }

    public static String getCLASSITemplate_L2a(String format) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.CLASSI_FILE_TEMPLATE_L2A;
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.CLASSI_FILE_TEMPLATE_L2A;
        }
        return null;
    }

    //getters level3 templates
    public static String getSpectralBandImageTemplate_L3(String format, String bandFileId) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.SPECTRAL_BAND_TEMPLATE_L3.replace("{{BANDFILEID}}", bandFileId);
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.SPECTRAL_BAND_TEMPLATE_L3.replace("{{BANDFILEID}}", bandFileId);
        }
        return null;
    }

    public static String getSCLTemplate_L3(String format) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.SCL_FILE_TEMPLATE_L3;
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.SCL_FILE_TEMPLATE_L3;
        }
        return null;
    }

    public static String getMSCTemplate_L3(String format) {
        if (format.equals("SAFE")) {
            return SAFENamingConvention.MSC_FILE_TEMPLATE_L3;
        } else if (format.equals("SAFE_COMPACT")) {
            return SAFECOMPACTNamingConvention.MSC_FILE_TEMPLATE_L3;
        }
        return null;
    }


    /**
     * When reading a granule without an associated product metadata,
     * it is not possible to get the format from the metadata content.
     * This function is used in this case to get the format by using
     * only the granule xml regex.
     *
     * @param path
     * @return
     */
    public static String getGranuleFormat(VirtualPath path) {
        String filename = path.getFileName().toString();
        Pattern pattern = Pattern.compile(SAFECOMPACTNamingConvention.GRANULE_XML_REGEX);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return "SAFE_COMPACT";
        }

        pattern = Pattern.compile(SAFENamingConvention.GRANULE_XML_REGEX);
        matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return "SAFE";
        }
        return "SAFE"; //return SAFE by default
    }
}
