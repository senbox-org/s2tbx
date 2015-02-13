package org.openjpeg;

import org.apache.commons.lang.SystemUtils;
import org.esa.beam.dataio.Utils;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;

/**
 * Created by opicas-p on 13/02/2015.
 */
public class JpegUtils {


    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void setExecutable(File file, boolean executable)
    {
        try
        {
            Process p = Runtime.getRuntime().exec(new String[] {
                    "chmod",
                    "u"+(executable?'+':'-')+"x",
                    file.getAbsolutePath(),
            });
            p.waitFor();
            String output = convertStreamToString(p.getInputStream());
            String errorOutput = convertStreamToString(p.getErrorStream());
        }
        catch(Exception e)
        {
            BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
        }
    }

    // critical move function to s2tbx-commons
    public static String getExecutable(String modulesDir)
    {
        String winPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-win32-x86/bin/opj_decompress.exe";
        String linuxPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-i386/bin/opj_decompress";
        String linux64Path = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-x64/bin/opj_decompress";
        String macPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Darwin-i386/bin/opj_decompress";

        String target = "opj_decompress";

        if(SystemUtils.IS_OS_LINUX)
        {
            try {
                Process p = Runtime.getRuntime().exec("uname -m");
                p.waitFor();
                String output = convertStreamToString(p.getInputStream());
                String errorOutput = convertStreamToString(p.getErrorStream());

                BeamLogManager.getSystemLogger().fine(output);
                BeamLogManager.getSystemLogger().severe(errorOutput);

                if(output.startsWith("i686"))
                {
                    target = modulesDir + linuxPath;
                }
                else
                {
                    target = modulesDir + linux64Path;
                }
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
            }
        }
        else if(SystemUtils.IS_OS_MAC)
        {
            try {
                target = modulesDir + macPath;
                setExecutable(new File(target), true);
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
            }
        }
        else
        {
            try {
                target = modulesDir + winPath;
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
                target = target + ".exe";
            }
        }

        File fileTarget = new File(target);
        if(fileTarget.exists())
        {
            fileTarget.setExecutable(true);
        }

        return target;
    }
}
