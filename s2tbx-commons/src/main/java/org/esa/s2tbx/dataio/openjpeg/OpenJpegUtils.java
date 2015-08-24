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

package org.esa.s2tbx.dataio.openjpeg;

import org.esa.s2tbx.dataio.jp2.TileLayout;

import java.io.IOException;
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

    /**
     * Parse the {@code String} returned by opj_dump command
     *
     * @param content the tring returned by opj_dump
     * @return the TileLayout extracted from this string
     */
    public static TileLayout parseOpjDump(String content) {
        List<String> splittedContent = new ArrayList<>();
        Collections.addAll(splittedContent, content.split("\n"));
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
}
