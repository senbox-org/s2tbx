package org.esa.s2tbx.dataio.gdal;

import static org.apache.commons.lang.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang.SystemUtils.IS_OS_MAC_OSX;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

/**
 * @author Cosmin Cara
 */
public enum OSCategory {
    WIN_32("Windows", "x86"),
    WIN_64("Windows", "x64"),
    LINUX_64("Linux", "x64"),
    MAC_OS_X("MacOSX", "x64"),
    UNSUPPORTED("", "");

    private static final String ENV_NAME = "environment-variables";

    String operatingSystemName;
    String architecture;

    OSCategory(String operatingSystemName, String architecture) {
        this.operatingSystemName = operatingSystemName;
        this.architecture = architecture;
    }

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

    public String getEnvironmentVariablesFileName() {
        return ENV_NAME;
    }

    public String getOperatingSystemName() {
        return operatingSystemName;
    }

    public String getArchitecture() {
        return this.architecture;
    }
}
