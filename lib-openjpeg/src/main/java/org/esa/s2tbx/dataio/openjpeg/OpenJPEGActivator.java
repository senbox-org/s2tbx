package org.esa.s2tbx.dataio.openjpeg;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.runtime.Activator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Activator class for deploying OpenJPEG binaries to the aux data dir
 *
 * @author Julien Malik
 */
public class OpenJPEGActivator implements Activator {

    @Override
    public void start() {
        Path auxdataDirectory = SystemUtils.getAuxDataPath().resolve("openjpeg");
        Path sourceDirPath = ResourceInstaller.findModuleCodeBasePath(getClass()).resolve("auxdata/openjpeg");
        final ResourceInstaller resourceInstaller = new ResourceInstaller(sourceDirPath, auxdataDirectory);

        try {
            resourceInstaller.install(".*", ProgressMonitor.NULL);
        } catch (IOException e) {
            SystemUtils.LOG.log(Level.WARNING, "OpenJPEG configuration error: failed to create " + auxdataDirectory, e);
        }
    }

    @Override
    public void stop() {
        // Purposely no-op
    }
}
