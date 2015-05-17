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

package org.openjpeg;

import jp2.TileLayout;
import org.apache.commons.lang.SystemUtils;
import org.esa.s2tbx.dataio.Utils;
import org.esa.snap.util.logging.BeamLogManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by opicas-p on 13/02/2015.
 */
public class JpegUtils {


    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static CommandOutput runProcess(ProcessBuilder builder) throws InterruptedException, IOException {
        final Process process = builder.start();
        final int exitCode = process.waitFor();
        String output = convertStreamToString(process.getInputStream());
        String errorOutput = convertStreamToString(process.getErrorStream());
        return new CommandOutput(exitCode, output, errorOutput);
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
            BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
        }
    }

    public static String getExecutable(String modulesDir) {
        String winPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-win32-x86/bin/opj_decompress.exe";
        String linuxPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-i386/bin/opj_decompress";
        String linux64Path = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-x64/bin/opj_decompress";
        String macPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Darwin-i386/bin/opj_decompress";

        String target = "opj_decompress";

        if (SystemUtils.IS_OS_LINUX) {
            try {
                Process p = Runtime.getRuntime().exec("uname -m");
                p.waitFor();
                String output = convertStreamToString(p.getInputStream());
                String errorOutput = convertStreamToString(p.getErrorStream());

                BeamLogManager.getSystemLogger().fine(output);
                BeamLogManager.getSystemLogger().severe(errorOutput);

                if (output.startsWith("i686")) {
                    target = modulesDir + linuxPath;
                } else {
                    target = modulesDir + linux64Path;
                }
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
            }
        } else if (SystemUtils.IS_OS_MAC) {
            try {
                target = modulesDir + macPath;
                setExecutable(new File(target), true);
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
            }
        } else {
            try {
                if (modulesDir.startsWith("/")) {
                    target = modulesDir.substring(1) + winPath;
                } else {
                    target = modulesDir + winPath;
                }

            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
                target = target + ".exe";
            }
        }

        File fileTarget = new File(target);
        if (fileTarget.exists()) {
            fileTarget.setExecutable(true);
        }

        return target;
    }

    public static String getInfoExecutable(String modulesDir) {
        String winPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-win32-x86/bin/opj_dump.exe";
        String linuxPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-i386/bin/opj_dump";
        String linux64Path = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-x64/bin/opj_dump";
        String macPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Darwin-i386/bin/opj_dump";

        String target = "opj_decompress";

        if (SystemUtils.IS_OS_LINUX) {
            try {
                Process p = Runtime.getRuntime().exec("uname -m");
                p.waitFor();
                String output = convertStreamToString(p.getInputStream());
                String errorOutput = convertStreamToString(p.getErrorStream());

                BeamLogManager.getSystemLogger().fine(output);
                BeamLogManager.getSystemLogger().severe(errorOutput);

                if (output.startsWith("i686")) {
                    target = modulesDir + linuxPath;
                } else {
                    target = modulesDir + linux64Path;
                }
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
            }
        } else if (SystemUtils.IS_OS_MAC) {
            try {
                target = modulesDir + macPath;
                setExecutable(new File(target), true);
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
            }
        } else {
            try {
                if (modulesDir.startsWith("/")) {
                    target = modulesDir.substring(1) + winPath;
                } else {
                    target = modulesDir + winPath;
                }
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
                target = target + ".exe";
            }
        }

        File fileTarget = new File(target);
        if (fileTarget.exists()) {
            fileTarget.setExecutable(true);
        }

        return target;
    }

    public static TileLayout parseOpjDump(String content) {
        List<String> splittedContent = new ArrayList<>();
        Collections.addAll(splittedContent, content.split("\n"));
        return parseOpjDump(splittedContent);
    }

    public static TileLayout parseOpjDump(List<String> content) {
        int Width = 0;
        int Height = 0;
        int tileWidth = 0;
        int tileHeight = 0;
        int xTiles = 0;
        int yTiles = 0;
        int resolutions = 0;

        for (String line : content) {
            if (line.contains("x1") && line.contains("y1")) {
                String[] segments = line.trim().split(",");
                Width = Integer.parseInt(segments[0].split("\\=")[1]);
                Height = Integer.parseInt(segments[1].split("\\=")[1]);
            }
            if (line.contains("tdx") && line.contains("tdy")) {
                String[] segments = line.trim().split(",");
                tileWidth = Integer.parseInt(segments[0].split("\\=")[1]);
                tileHeight = Integer.parseInt(segments[1].split("\\=")[1]);
            }
            if (line.contains("tw") && line.contains("th")) {
                String[] segments = line.trim().split(",");
                xTiles = Integer.parseInt(segments[0].split("\\=")[1]);
                yTiles = Integer.parseInt(segments[1].split("\\=")[1]);
            }
            if (line.contains("numresolutions")) {
                resolutions = Integer.parseInt(line.trim().split("\\=")[1]);
            }
        }

        TileLayout parsedTile = new TileLayout(Width, Height, tileWidth, tileHeight, xTiles, yTiles, resolutions);
        return parsedTile;

    }
}
