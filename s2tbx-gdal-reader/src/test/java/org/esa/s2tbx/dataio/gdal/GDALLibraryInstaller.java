package org.esa.s2tbx.dataio.gdal;

import java.util.concurrent.atomic.AtomicBoolean;

public final class GDALLibraryInstaller {

    private static final AtomicBoolean INSTALLED = new AtomicBoolean(false);

    private GDALLibraryInstaller() {
    }

    public static void install() {
        if (!INSTALLED.getAndSet(true)) {
            GDALLoader.getInstance().initGDAL();
        }
    }
}
