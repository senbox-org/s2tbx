/*
 *
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.openjpeg;


import org.apache.commons.lang.SystemUtils;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.OnStart;

import java.io.File;
import java.io.IOException;

/**
 * Created by opicas-p on 02/04/2015.
 */
public class OpenJpegUtils {

    /**
     * {@code @OnStart}: {@code Runnable}s defined by various modules are invoked in parallel and as soon
     * as possible. It is guaranteed that execution of all {@code runnable}s is finished
     * before the startup sequence is claimed over.
     */
    @OnStart
    public static class CheckPermissions implements Runnable {

        @Override
        public void run() {
            String infoExtractor = getSafeInfoExtractor();
            String decompressor = getSafeDecompressor();

            if(!infoExtractor.isEmpty())
            {
                File infoExtractorFile = new File(infoExtractor);

                if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
                {
                    setExecutable(infoExtractorFile, true);
                }
            }

            if(!decompressor.isEmpty())
            {
                File decompressorFile = new File(decompressor);

                if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
                {
                    setExecutable(decompressorFile, true);
                }
            }
        }
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void setExecutable(File file, boolean executable) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                    "chmod",
                    "u" + (executable ? '+' : '-') + "x",
                    file.getAbsolutePath(),
            });
            p.waitFor();
            String output = convertStreamToString(p.getInputStream());
            String errorOutput = convertStreamToString(p.getErrorStream());
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
        }
    }

    public static String getSafeInfoExtractor()
    {
        try {
            String nbDirs = System.getProperty("netbeans.dirs");
            String[] dirModuleCandidates = nbDirs.split(SystemUtils.PATH_SEPARATOR);
            File candidateByIteration = null;
            for(String aModuleCandidate: dirModuleCandidates)
            {
                candidateByIteration = new File(aModuleCandidate, getInfoExtractor());
                if(candidateByIteration.exists())
                {
                    break;
                }
            }
            if(candidateByIteration != null)
            {
                return candidateByIteration.getAbsolutePath();
            }

            File candidateByAPI = InstalledFileLocator.getDefault().locate(getInfoExtractor(), null, false);
            if(candidateByAPI != null) {
                return candidateByAPI.getAbsolutePath();
            }
            else
            {
                return "";
            }
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static String getInfoExtractor() throws IOException, InterruptedException {
        String usedPath = null;

        String winPath = "openjpeg-2.1.0-win32-x86_dyn/bin/opj_dump.exe";
        String linuxPath = "openjpeg-2.1.0-Linux-i386/bin/opj_dump";
        String linux64Path = "openjpeg-2.1.0-Linux-x64/bin/opj_dump";
        String macPath = "openjpeg-2.1.0-Darwin-i386/bin/opj_dump";

        if (SystemUtils.IS_OS_LINUX) {
            Process p = Runtime.getRuntime().exec("uname -m");
            p.waitFor();
            String output = convertStreamToString(p.getInputStream());
            String errorOutput = convertStreamToString(p.getErrorStream());

            if (output.startsWith("i686")) {
                usedPath = linuxPath;
            } else {
                usedPath = linux64Path;
            }

        } else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
        {
            usedPath = macPath;
        }
        else
        {
            usedPath = winPath;
        }

        String goodPath = "modules/ext/org.esa.s2tbx.lib-openjpeg/" + usedPath;
        return goodPath;
    }

    public static String getSafeDecompressor()
    {
        try {
            String nbDirs = System.getProperty("netbeans.dirs");
            String[] dirModuleCandidates = nbDirs.split(SystemUtils.PATH_SEPARATOR);
            File candidateByIteration = null;
            for(String aModuleCandidate: dirModuleCandidates)
            {
                candidateByIteration = new File(aModuleCandidate, getDecompressor());
                if(candidateByIteration.exists())
                {
                    break;
                }
            }
            if(candidateByIteration != null)
            {
                return candidateByIteration.getAbsolutePath();
            }

            File candidateByAPI = InstalledFileLocator.getDefault().locate(getDecompressor(), null, false);
            if(candidateByAPI != null)
            {
                return candidateByAPI.getAbsolutePath();
            }
            else
            {
                return "";
            }
        } catch (InterruptedException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getDecompressor() throws InterruptedException, IOException {
        String usedPath = null;

        String winPath = "openjpeg-2.1.0-win32-x86_dyn/bin/opj_decompress.exe";
        String linuxPath = "openjpeg-2.1.0-Linux-i386/bin/opj_decompress";
        String linux64Path = "openjpeg-2.1.0-Linux-x64/bin/opj_decompress";
        String macPath = "openjpeg-2.1.0-Darwin-i386/bin/opj_decompress";

        if (SystemUtils.IS_OS_LINUX) {
            Process p = Runtime.getRuntime().exec("uname -m");
            p.waitFor();
            String output = convertStreamToString(p.getInputStream());
            String errorOutput = convertStreamToString(p.getErrorStream());

            if (output.startsWith("i686")) {
                usedPath = linuxPath;
            } else {
                usedPath = linux64Path;
            }

        } else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
        {
            usedPath = macPath;
        }
        else
        {
            usedPath = winPath;
        }

        String goodPath = "modules/ext/org.esa.s2tbx.lib-openjpeg/" + usedPath;
        return goodPath;
    }
}
