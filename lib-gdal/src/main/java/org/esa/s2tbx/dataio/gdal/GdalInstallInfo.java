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
public enum GdalInstallInfo {
    INSTANCE;

    private Path binLocation;
    private Path driversLocation;
    private Path dataLocation;
    private Path appsLocation;
    private String[] fileTypes;
    private String[] extensions;

    public Path getBinLocation() {
        return binLocation;
    }

    void setBinLocation(Path binLocation) { this.binLocation = binLocation; }

    public Path getDriversLocation() { return driversLocation; }
    void setDriversLocation(Path driversLocation) { this.driversLocation = driversLocation; }

    public Path getAppsLocation() { return appsLocation; }
    void setAppsLocation(Path appsLocation) { this.appsLocation = appsLocation; }

    public Path getDataLocation() { return dataLocation; }
    void setDataLocation(Path dataLocation) { this.dataLocation = dataLocation; }

    public boolean isPresent() {
        return binLocation != null && Files.exists(binLocation);
    }

    public String[] getFormatNames() { return fileTypes; }
    void setFormatNames(String[] formats) { this.fileTypes = formats; }

    public String[] getExtensions() { return extensions; }
    void setExtensions(String[] extensions) { this.extensions = extensions; }

    static void runGdalInfo() {
        if (INSTANCE.isPresent()) {
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
                INSTANCE.fileTypes = fmtNames.toArray(new String[fmtNames.size()]);
                INSTANCE.extensions = exts.toArray(new String[exts.size()]);
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
