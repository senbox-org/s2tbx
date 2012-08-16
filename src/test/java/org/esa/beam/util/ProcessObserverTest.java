package org.esa.beam.util;

import com.bc.ceres.core.ProgressMonitor;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Norman Fomferra
 */
public class ProcessObserverTest {
    @Test
    public void testIt() throws Exception {

        final String commandLine;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            commandLine = "cmd /C \"dir\"";
        } else {
            commandLine = "ls";
        }

        final Process process = Runtime.getRuntime().exec(commandLine);
        final MyHandler handler = new MyHandler();
        new ProcessObserver(process).withHandler(handler).start();
        assertTrue(handler.trace.startsWith("started;out="));
        assertTrue(handler.trace.contains(".git"));
        assertTrue(handler.trace.contains(".gitignore"));
        assertTrue(handler.trace.contains("src"));
        assertTrue(handler.trace.contains("pom.xml"));
        assertTrue(handler.trace.endsWith("exit=0;"));

    }

    private static class MyHandler implements ProcessObserver.Handler {
        String trace = "";

        @Override
        public void onObservationStarted(ProcessObserver.ObservedProcess process, ProgressMonitor pm) {
            trace += "started;";
        }

        @Override
        public void onStdoutLineReceived(ProcessObserver.ObservedProcess process, String line, ProgressMonitor pm) {
            trace += "out=" + line + ";";
        }

        @Override
        public void onStderrLineReceived(ProcessObserver.ObservedProcess process, String line, ProgressMonitor pm) {
            trace += "err=" + line + ";";
        }

        @Override
        public void onObservationEnded(ProcessObserver.ObservedProcess process, Integer exitCode, ProgressMonitor pm) {
            trace += "exit=" + exitCode + ";";
        }
    }
}
