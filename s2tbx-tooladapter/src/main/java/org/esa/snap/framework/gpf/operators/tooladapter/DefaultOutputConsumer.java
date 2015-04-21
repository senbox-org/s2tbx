package org.esa.snap.framework.gpf.operators.tooladapter;

import com.bc.ceres.core.ProgressMonitor;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation for process output processing.
 * The class would expect specific patterns (i.e. RegEx) for errors and progress messages.
 * Any other message (which does not respect one of these patterns) will be considered informational.
 * If no pattern is given in ctor, all messages are treated as informational.
 *
 * @author Cosmin Cara
 */
public class DefaultOutputConsumer implements ProcessOutputConsumer {

    private Pattern error;
    private Pattern progress;
    private Logger logger;
    private ProgressMonitor progressMonitor;

    public DefaultOutputConsumer() {
        this(null, null, null);
    }

    public DefaultOutputConsumer(String progressPattern, ProgressMonitor pm) {
        this(progressPattern, null, pm);
    }

    public DefaultOutputConsumer(String progressPattern, String errorPattern, ProgressMonitor pm) {
        progressMonitor = pm;
        if (errorPattern != null && errorPattern.trim().length() > 0) {
            error = Pattern.compile(errorPattern, Pattern.CASE_INSENSITIVE);
        }
        if (progressPattern != null && progressPattern.trim().length() > 0) {
            progress = Pattern.compile(progressPattern, Pattern.CASE_INSENSITIVE);
            initializeProgressMonitor();
        }
        logger = Logger.getLogger(DefaultOutputConsumer.class.getName());
    }

    public void setProgressMonitor(ProgressMonitor monitor) {
        this.progressMonitor = monitor;
        initializeProgressMonitor();
    }

    @Override
    public void consumeOutput(String line) {
        Matcher matcher = null;
        try {
            if (progress != null && (matcher = progress.matcher(line)).matches()) {
                int worked = Integer.parseInt(matcher.group(1));
                /*if (worked < 2)
                    progressMonitor.beginTask("Processing", worked);
                else*/
                progressMonitor.worked(worked);
            } else if (error != null && (matcher = error.matcher(line)).matches()) {
                logger.severe(matcher.group(1));
            } else {
                //progressMonitor.setSubTaskName(line);
                //progressMonitor.setSubTaskName(line);
                logger.info(line);
            }
        } catch (Exception e) {
        }
    }

    private void initializeProgressMonitor() {
        if (progressMonitor == null) {
            progressMonitor = ProgressMonitor.NULL;
            progressMonitor.beginTask("Starting", 100);
        }
    }

    public void close() {
        if (progressMonitor != null) {
            progressMonitor.done();
        }
    }
}
