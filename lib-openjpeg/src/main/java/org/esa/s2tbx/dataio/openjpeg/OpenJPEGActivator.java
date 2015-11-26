package org.esa.s2tbx.dataio.openjpeg;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.runtime.Activator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.lang.SystemUtils.IS_OS_UNIX;

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
            fixUpPermissions(auxdataDirectory);
        } catch (IOException e) {
            SystemUtils.LOG.severe("OpenJPEG configuration error: failed to create " + auxdataDirectory);
        }
    }

    @Override
    public void stop() {
        // Purposely no-op
    }

    private static void fixUpPermissions(Path destPath) throws IOException {
        Stream<Path> files = Files.list(destPath);
        files.forEach(path -> {
            if (Files.isDirectory(path)) {
                try {
                    fixUpPermissions(path);
                } catch (IOException e) {
                    SystemUtils.LOG.severe("OpenJPEG configuration error: failed to fix permissions on " + path);
                }
            }
            else {
                setExecutablePermissions(path);
            }
        });
    }

    private static void setExecutablePermissions(Path executablePathName) {
        if (IS_OS_UNIX) {
            Set<PosixFilePermission> permissions = new HashSet<>(Arrays.asList(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.GROUP_READ,
                    PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.OTHERS_READ,
                    PosixFilePermission.OTHERS_EXECUTE));
            try {
                Files.setPosixFilePermissions(executablePathName, permissions);
            } catch (IOException e) {
                // can't set the permissions for this file, eg. the file was installed as root
                // send a warning message, user will have to do that by hand.
                SystemUtils.LOG.severe("Can't set execution permissions for executable " + executablePathName.toString() +
                        ". If required, please ask an authorised user to make the file executable.");
            }
        }
    }
}
