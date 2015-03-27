/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.beam.dataio.spot.internal;

import org.esa.beam.dataio.ZipVirtualDir;
import org.esa.beam.dataio.spot.dimap.SpotConstants;

import java.io.File;
import java.io.IOException;

/**
 * This is a specialisation of the more generic ZipVirtualDir.
 * The reason of creating this class is the possibility of encountering
 * different capitalisation of files inside zip archives.
 *
 * @author Cosmin Cara
 */
public class SpotVirtualDir extends ZipVirtualDir {
    /**
     * Constructor that wraps a virtual directory over a file or folder source.
     *
     * @param source The source file or folder.
     * @throws java.io.IOException
     */
    public SpotVirtualDir(File source) throws IOException {
        super(source);
    }

    @Override
    protected boolean correctCapitalisation() throws IOException {
        String[] list = wrappedVirtualDir.list("");
        while (list.length == 1) {
            unnecessaryPath += list[0];
            list = wrappedVirtualDir.list(unnecessaryPath);
            for (String file : list) {
                if (SpotConstants.DIMAP_VOLUME_FILE.equalsIgnoreCase(file)) {
                    shouldConvertCase = !SpotConstants.DIMAP_VOLUME_FILE.equals(file);
                }
            }
        }
        return shouldConvertCase;
    }
}
