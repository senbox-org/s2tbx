package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.util.SystemUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kraftek on 11/15/2016.
 */
public final class GdalInstallInfo {
    private static Path binLocation;
    private static Path driversLocation;
    private static Path dataLocation;
    private static Path appsLocation;
    private static String[] fileTypes;
    private static String[] extensions;

    public static Path getBinLocation() { return binLocation; }
    static void setBinLocation(Path binLocation) { GdalInstallInfo.binLocation = binLocation; }

    public static Path getDriversLocation() { return driversLocation; }
    static void setDriversLocation(Path driversLocation) { GdalInstallInfo.driversLocation = driversLocation; }

    public static Path getAppsLocation() { return appsLocation; }
    static void setAppsLocation(Path appsLocation) { GdalInstallInfo.appsLocation = appsLocation; }

    public static Path getDataLocation() { return dataLocation; }
    static void setDataLocation(Path dataLocation) { GdalInstallInfo.dataLocation = dataLocation; }

    public static boolean isPresent() {
        return binLocation != null && Files.exists(binLocation);
    }

    public static String[] getFormatNames() { return fileTypes; }
    static void setFormatNames(String[] formats) { GdalInstallInfo.fileTypes = formats; }

    public static String[] getExtensions() { return extensions; }
    static void setExtensions(String[] extensions) { GdalInstallInfo.extensions = extensions; }

    static void runGdalInfo() {
        if (isPresent()) {
            List<String> formatsShortDesc = runProcess("gdalinfo", "--formats");
            List<String> fmtNames = new ArrayList<>();
            List<String> exts = new ArrayList<>();
            String ext;
            for (String desc : formatsShortDesc) {
                String format = desc.substring(0, desc.indexOf("-")).trim();
                List<String> lines = runProcess("gdalinfo", "--format", format);
                for (String line : lines) {
                    if (line.contains("Extension")) {
                        fmtNames.add(format);
                        exts.add("." + line.substring(line.indexOf(":")).trim());
                        break;
                    }
                }
            }
            if (fmtNames.size() > 0 && exts.size() > 0) {
                fileTypes = fmtNames.toArray(new String[fmtNames.size()]);
                extensions = exts.toArray(new String[exts.size()]);
            }
        }
    }

    private static List<String> runProcess(String... args) {
        List<String> lines = new ArrayList<>();
        if (args != null && args.length > 0) {
            try {
                ProcessBuilder builder = new ProcessBuilder(args);
                builder.environment().putAll(System.getenv());
                boolean isStopped = false;
                final Process process = builder.start();
                try (BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    while (!isStopped) {
                        if (!process.isAlive()) {
                            isStopped = true;
                        } else {
                            Thread.yield();
                        }
                        while (outReader.ready()) {
                            String line = outReader.readLine();
                            if (line != null && !line.isEmpty()) {
                                lines.add(line);
                            }
                        }
                    }
                    outReader.close();
                }
            } catch (Exception ex) {
                SystemUtils.LOG.severe(String.format("Cannot execute %s: %s", args[0], ex.getMessage()));
            }
        }
        return lines;
    }
}
