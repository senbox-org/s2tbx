/*
 *
 *  * Copyright (C) 2016 CS ROMANIA
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
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.landcover.dataio;

import org.esa.snap.core.dataop.resamp.Resampling;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.engine_utilities.util.Settings;
import org.esa.snap.landcover.dataio.AbstractLandCoverModelDescriptor;
import org.esa.snap.landcover.dataio.LandCoverModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Model Descriptor for JAXA Forest Map 2016
 *
 * @author Cosmin Cara
 */
public class JaxaForestMapModelDescriptor extends AbstractLandCoverModelDescriptor {
    private static final String NAME = "JaxaForestMap-2016";

    private static final File INSTALL_DIR = new File(Settings.instance().getAuxDataFolder().getAbsolutePath(),
                                                     "LandCover" + File.separator + NAME);
    private static File[] fileList;

    static {
        Path moduleBasePath = ResourceInstaller.findModuleCodeBasePath(CCILandCoverModelDescriptor.class);
        Path tilesFile = moduleBasePath.resolve("org/esa/snap/landcover/auxdata/jaxa_tiles.txt");
        try {
            fileList = Files.readAllLines(tilesFile).stream().map(s -> new File(INSTALL_DIR, s)).toArray(File[]::new);
        } catch (IOException e) {
            SystemUtils.LOG.severe(e.getMessage());
        }
    }

    public JaxaForestMapModelDescriptor() {
        remotePath = "ftp://ftp.eorc.jaxa.jp/pub/ALOS-2/ext1/PALSAR-2_MSC/25m_MSC/2016/";
        name = NAME;
        NO_DATA_VALUE = 0;
        installDir = INSTALL_DIR;

        final Path moduleBasePath = ResourceInstaller.findModuleCodeBasePath(this.getClass());
        colourIndexFile = moduleBasePath.resolve("org/esa/snap/landcover/auxdata/jaxaforestmap_index.col");
    }

    @Override
    public LandCoverModel createLandCoverModel(Resampling resampling) throws IOException {
        return new JaxaFileModel(this, fileList, resampling);
    }

    void changeRemoteDir(String path) {
        if (path != null) {
            if (!path.endsWith("/")) {
                path += "/";
            }
            if (remotePath.endsWith("2016/")) {
                remotePath += path;
            } else {
                remotePath = remotePath.substring(0, remotePath.indexOf("2016/") + 5) + path;
            }
        }
    }
}
