/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.jp2;

import org.apache.commons.lang.SystemUtils;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.openjpeg.CommandOutput;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegUtils;

import java.io.IOException;
import java.net.URI;

/**
 * @author Oscar Picas-Puig
 */
public class CodeStreamUtils {

    /**
     * Get the tile layout with opj_dump
     *
     * @param opjdumpPath path to opj_dump
     * @param uri the path to the jpeg file
     * @return the tile layout for the openjpeg file
     * @throws IOException
     * @throws InterruptedException
     */
    public static TileLayout getTileLayoutWithOpenJPEG(String opjdumpPath, URI uri) throws IOException, InterruptedException {


        TileLayout tileLayout = null;

        if(opjdumpPath != null) {

            String pathToImageFile = uri.getPath();
            if (SystemUtils.IS_OS_WINDOWS) {
                pathToImageFile = Utils.GetIterativeShortPathName(pathToImageFile.substring(1));
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
        }

        return tileLayout;
    }
}
