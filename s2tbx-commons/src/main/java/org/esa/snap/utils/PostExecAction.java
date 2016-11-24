package org.esa.snap.utils;

import org.esa.snap.tango.TangoIcons;
import org.openide.LifecycleManager;
import org.openide.awt.NotificationDisplayer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kraftek on 11/14/2016.
 */
public class PostExecAction {

    private static final Map<String, String[]> commandList = new ConcurrentHashMap<>();
    private static Runnable finalTask;

    public static void register(String source, String[] args) {
        if (args != null && args.length > 0) {
            String key = String.join(" ", args);
            commandList.putIfAbsent(key, args);
            if (finalTask == null) {
                finalTask = () -> {
                    for (Map.Entry<String, String[]> entry : commandList.entrySet()) {
                        try {
                            Logger.getLogger("").log(Level.INFO, "Executing: " + entry.getKey());
                            new ProcessBuilder().command(entry.getValue()).start();
                        } catch (IOException e) {
                            Logger.getLogger("").log(Level.SEVERE, "Failed to execute: " + entry.getKey());
                        }
                    }
                };
                Runtime.getRuntime().addShutdownHook(new Thread(finalTask));
            }
            Logger.getLogger("").log(Level.INFO, "Registered for later execution: " + key);
            NotificationDisplayer.getDefault()
                    .notify("Restart Required",
                            TangoIcons.status_software_update_urgent(TangoIcons.Res.R16),
                            String.format("Some environment settings have been modified by module '%s'.\n" +
                                    "Please restart the application for them to become effective", source),
                            e -> {
                                try {
                                    LifecycleManager.getDefault().markForRestart();
                                } catch (UnsupportedOperationException uex) {
                                    LifecycleManager.getDefault().exit();
                                }
                            },
                            NotificationDisplayer.Priority.NORMAL,
                            NotificationDisplayer.Category.INFO);
        }
    }
}
