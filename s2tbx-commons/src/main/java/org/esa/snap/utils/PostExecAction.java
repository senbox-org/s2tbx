package org.esa.snap.utils;

import org.openide.LifecycleManager;
import org.openide.awt.NotificationDisplayer;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kraftek on 11/14/2016.
 */
public class PostExecAction {

    public static void register(String source, String[] args) {
        if (args != null && args.length > 0) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    Logger.getLogger("").log(Level.INFO, "Executing: " + String.join(" ", args));
                    new ProcessBuilder().command(args).start();
                } catch (IOException e) {
                    Logger.getLogger("").log(Level.SEVERE, "Failed to execute: " + String.join(" ", args));
                }
            }));
            Logger.getLogger("").log(Level.INFO, "Registered for later execution: " + String.join(" ", args));
            NotificationDisplayer.getDefault()
                    .notify("Restart Required",
                            new ImageIcon(PostExecAction.class.getResource("info.gif")),
                            String.format("Some environment settings have been modified by module %s.\n" +
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
