package org.esa.s2tbx.dataio.gdal;

import static org.apache.commons.lang.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang.SystemUtils.IS_OS_MAC_OSX;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

/**
 * @author Cosmin Cara
 */
public enum OSCategory {
    WIN_32("gdal-2.1.0-win32", "release-1500-gdal-2-1-0-mapserver-7-0-1.zip", "Windows"),
    WIN_64("gdal-2.1.0-win64", "release-1500-x64-gdal-2-1-0-mapserver-7-0-1.zip", "Windows"),
    LINUX_64("gdal-2.2.0-linux", "gdal-2.2.0-linux-bin.zip", "Linux"),
    MAC_OS_X(null, null, "Macintosh"),
    UNSUPPORTED(null, null, "");

    String directory;
    String zipFileName;
    String operatingSystemName;

    OSCategory(String directory, String zipFileName, String operatingSystemName) {
        this.directory = directory;
        this.zipFileName = zipFileName;
        this.operatingSystemName = operatingSystemName;
    }

    public String getOperatingSystemName() {
        return operatingSystemName;
    }

    public String getDirectory() { return this.directory; }

    public String getZipFileName() { return this.zipFileName; }

    public static OSCategory getOSCategory() {
        OSCategory category;
        if (IS_OS_LINUX) {
            category = OSCategory.LINUX_64;
        } else if (IS_OS_MAC_OSX) {
            category = OSCategory.MAC_OS_X;
        } else if (IS_OS_WINDOWS) {
            String sysArch = System.getProperty("os.arch").toLowerCase();
            if (sysArch.contains("amd64") || sysArch.contains("x86_x64")) {
                category = OSCategory.WIN_64;
            } else {
                category = OSCategory.WIN_32;
            }
        } else {
            // we should never be here since we do not release installers for other systems.
            category = OSCategory.UNSUPPORTED;
        }
        return category;
    }
}
