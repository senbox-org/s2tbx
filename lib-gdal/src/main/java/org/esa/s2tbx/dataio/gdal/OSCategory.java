package org.esa.s2tbx.dataio.gdal;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang.SystemUtils.IS_OS_MAC_OSX;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

/**
 * @author Cosmin Cara
 */
public enum OSCategory {
    WIN_32("Windows", "x86", "where"),
    WIN_64("Windows", "x64", "where"),
    LINUX_64("Linux", "x64", "which"),
    MAC_OS_X("MacOSX", "x64", "which"),
    UNSUPPORTED("", "", "");

    private static final Logger logger = Logger.getLogger(OSCategory.class.getName());

    private static final String ENV_NAME = "environment-variables";

    private static final OSCategory osCategory = retrieveOSCategory();

    private String operatingSystemName;
    private String architecture;
    private String findExecutableLocationCmd;

    OSCategory(String operatingSystemName, String architecture, String findExecutableLocationCmd) {
        this.operatingSystemName = operatingSystemName;
        this.architecture = architecture;
        this.findExecutableLocationCmd = findExecutableLocationCmd;
    }

    public static OSCategory getOSCategory() {
        return osCategory;
    }

    private static OSCategory retrieveOSCategory() {
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

    public String getExecutableLocation(String executableName) {
        try (java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(findExecutableLocationCmd + " " + executableName).getInputStream()).useDelimiter("\\A")) {
            String executableFilePath = s.hasNext() ? s.next() : "";
            executableFilePath = executableFilePath.replaceAll("\r\n", "");
            if (!executableFilePath.isEmpty()) {
                return Paths.get(executableFilePath).getParent().toString();
            }
            return "";
        } catch (IOException ignored) {
            logger.log(Level.INFO, () -> executableName + " not found");
        }
        return "";
    }
}
