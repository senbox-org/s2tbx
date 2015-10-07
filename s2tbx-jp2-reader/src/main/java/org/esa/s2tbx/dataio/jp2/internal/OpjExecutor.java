package org.esa.s2tbx.dataio.jp2.internal;

import org.esa.s2tbx.dataio.openjpeg.CommandOutput;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by kraftek on 7/15/2015.
 */
public class OpjExecutor {

    private Logger logger;
    private String exePath;
    private String lastError;
    private String lastOutput;

    public OpjExecutor(String executable) {
        logger = Logger.getLogger(OpjExecutor.class.getName());
        exePath = executable;
    }

    public int execute(Map<String, String> arguments) {
        int exitCode = 0;
        lastError = null;
        lastOutput = null;
        List<String> args = new ArrayList<>();
        args.add(exePath);
        for (String key : arguments.keySet()) {
            args.add(key);
            args.add(arguments.get(key));
        }
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.redirectErrorStream(true);
        try {
            CommandOutput commandOutput = OpenJpegUtils.runProcess(builder);
            lastOutput = commandOutput.getTextOutput();
            lastError = commandOutput.getErrorOutput();
            exitCode = commandOutput.getErrorCode();
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return exitCode;
    }

    public String getLastError() { return lastError; }

    public String getLastOutput() { return lastOutput; }
}
