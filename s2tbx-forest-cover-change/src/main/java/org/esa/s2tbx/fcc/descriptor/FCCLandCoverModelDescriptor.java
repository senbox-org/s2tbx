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

package org.esa.s2tbx.fcc.descriptor;

import org.esa.snap.core.dataop.resamp.Resampling;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.engine_utilities.util.Settings;
import org.esa.snap.landcover.dataio.AbstractLandCoverModelDescriptor;
import org.esa.snap.landcover.dataio.FileLandCoverModel;
import org.esa.snap.landcover.dataio.LandCoverModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Cosmin Cara
 */
public class FCCLandCoverModelDescriptor extends AbstractLandCoverModelDescriptor {
    public static final String NAME = "CCILandCover-2015";

    private static final File INSTALL_DIR = new File(Settings.instance().getAuxDataFolder().getAbsolutePath(),
                                                     "LandCover" + File.separator + NAME);
    private static final File[] fileList = new File[] {
            new File(INSTALL_DIR, "ESACCI-LC-L4-LCCS-Map-300m-P1Y-2015-v2.0.7.zip")
    };

    public FCCLandCoverModelDescriptor() {
        Path moduleBasePath = ResourceInstaller.findModuleCodeBasePath(this.getClass());
        colourIndexFile = moduleBasePath.resolve("org/esa/s2tbx/fcc/fcclandcover_index.col");
    }

    @Override
    public LandCoverModel createLandCoverModel(Resampling resampling) throws IOException {
        return new FileLandCoverModel(this, fileList, resampling);
    }

    @Override
    public boolean isInstalled() { return true; }
}
