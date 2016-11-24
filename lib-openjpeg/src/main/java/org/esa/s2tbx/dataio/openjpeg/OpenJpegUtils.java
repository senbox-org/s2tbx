/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.openjpeg;

import org.apache.commons.lang.SystemUtils;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.readers.TileLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to work with openjpeg:
 * - retrieve TileLayout information from a granule
 * - execute openjpeg
 *
 * @author Oscar Picas-Puig
 */
public class OpenJpegUtils {

    /**
     * Get the tile layout with opj_dump
     *
     * @param opjdumpPath path to opj_dump
     * @param jp2FilePath the path to the jpeg file
     * @return the tile layout for the openjpeg file
     * @throws IOException
     * @throws InterruptedException
     */
    public static TileLayout getTileLayoutWithOpenJPEG(String opjdumpPath, Path jp2FilePath) throws IOException, InterruptedException {

        if(opjdumpPath == null) {
            throw new IllegalStateException("Cannot retrieve tile layout, opj_dump cannot be found");
        }

        TileLayout tileLayout;

        String pathToImageFile = jp2FilePath.toAbsolutePath().toString();
        if (SystemUtils.IS_OS_WINDOWS) {
            pathToImageFile = Utils.GetIterativeShortPathNameW(pathToImageFile);
        }

        ProcessBuilder builder = new ProcessBuilder(opjdumpPath, "-i", pathToImageFile);
        builder.redirectErrorStream(true);

        CommandOutput exit = OpenJpegUtils.runProcess(builder);

        if (exit.getErrorCode() != 0) {
            StringBuilder sbu = new StringBuilder();
            for (String fragment : builder.command()) {
                sbu.append(fragment);
                sbu.append(' ');
            }

            throw new IOException(String.format("Command [%s] failed with error code [%d], stdoutput [%s] and stderror [%s]", sbu.toString(), exit.getErrorCode(), exit.getTextOutput(), exit.getErrorOutput()));
        }
        tileLayout = OpenJpegUtils.parseOpjDump(exit.getTextOutput());

        if (tileLayout.numResolutions == 0) {
            return null;
        }

        return tileLayout;
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static CommandOutput runProcess(ProcessBuilder builder) throws InterruptedException, IOException {
        builder.environment().putAll(System.getenv());
        StringBuilder output = new StringBuilder();
        boolean isStopped = false;
        final Process process = builder.start();
        try (BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (!isStopped) {
                if (!process.isAlive()) {
                    isStopped = true;
                } else {
                    Thread.yield();
                }
                while (outReader.ready()) {
                    String line = outReader.readLine();
                    if (line != null && !line.isEmpty()) {
                        output.append(line);
                    }
                }
            }
            outReader.close();
        }
        int exitCode = process.exitValue();
        //String output = convertStreamToString(process.getInputStream());
        String errorOutput = convertStreamToString(process.getErrorStream());
        return new CommandOutput(exitCode, output.toString(), errorOutput);
    }

    /**
     * Parse the {@code String} returned by opj_dump command
     *
     * @param content the tring returned by opj_dump
     * @return the TileLayout extracted from this string
     */
    public static TileLayout parseOpjDump(String content) {
        List<String> splittedContent = new ArrayList<>();
        Collections.addAll(splittedContent, content.split(content.contains("\n") ? "\n" : "\t"));
        return parseOpjDump(splittedContent);
    }

    /**
     * Parse opj_dump result lines
     *
     * @param content the lines of text returned from opj_dump command
     * @return the TileLayout extracted from the lines
     */
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

        return new TileLayout(Width, Height, tileWidth, tileHeight, xTiles, yTiles, resolutions);

    }

    public static boolean validateOpenJpegExecutables(String opjdumpPath, String opjdecompPath) {

        ProcessBuilder builder = new ProcessBuilder(opjdumpPath, "-h");
        builder.redirectErrorStream(true);

        CommandOutput exit;
        try {
            exit = OpenJpegUtils.runProcess(builder);
        } catch (Exception e) {
            return false;
        }

        if (exit.getErrorCode() != 1) {
            return false;
        }

        builder = new ProcessBuilder(opjdecompPath, "-h");
        builder.redirectErrorStream(true);

        try {
            exit = OpenJpegUtils.runProcess(builder);
        } catch (Exception e) {
            return false;
        }

        if (exit.getErrorCode() != 1) {
            return false;
        }

        return true;
    }
}
