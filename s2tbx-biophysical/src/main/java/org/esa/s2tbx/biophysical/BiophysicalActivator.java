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

package org.esa.s2tbx.biophysical;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.runtime.Activator;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Activator class for deploying Biophysical operator auxdata to the aux data dir
 *
 * @author Julien Malik
 */
public class BiophysicalActivator implements Activator {

    @Override
    public void start() {
        Path auxdataDirectory = null;
        try {
            auxdataDirectory = SystemUtils.getAuxDataPath().resolve("s2tbx/biophysical");;
            final Path sourceDirPath = ResourceInstaller.findModuleCodeBasePath(BiophysicalAuxdata.class).resolve("auxdata");
            final ResourceInstaller resourceInstaller = new ResourceInstaller(sourceDirPath, auxdataDirectory);
            resourceInstaller.install(".*", ProgressMonitor.NULL);
        } catch (IOException e) {
            SystemUtils.LOG.severe("Unable to install resource for BiophysicalOperator to " + auxdataDirectory);
        }
    }

    @Override
    public void stop() {
        // Purposely no-op
    }

    public static Path getAuxDataDir() {
        return SystemUtils.getAuxDataPath().resolve("s2tbx/biophysical");
    }
}
