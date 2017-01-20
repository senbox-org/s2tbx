package org.esa.s2tbx.gdal.activator;

import org.esa.s2tbx.dataio.gdal.GDALWriterPlugInListener;
import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.gdal.WriterPlugInExportProductAction;
import org.esa.snap.runtime.Activator;
import org.openide.util.Lookup;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jcoravu on 8/12/2016.
 */
public class GDALMenuActionActivator implements Activator, GDALWriterPlugInListener {
    private static final Logger logger = Logger.getLogger(GDALMenuActionActivator.class.getName());

    public GDALMenuActionActivator() {
    }

    @Override
    public void start() {
        GdalInstallInfo.INSTANCE.setListener(this);
    }

    @Override
    public void stop() {

    }

    @Override
    public void writeDriversSuccessfullyInstalled() {
        try {
            initActions();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void initActions() throws IOException {
        ActionRegistrationService ars = Lookup.getDefault().lookup(ActionRegistrationService.class);
        String actionDisplayName = "GDAL";
        AbstractAction action = new WriterPlugInExportProductAction();
        action.putValue(Action.NAME, actionDisplayName);
        action.putValue("displayName", actionDisplayName);
        String menuPath = "Menu/File/Export";
        String category = "GDAL";
        ars.registerAction(category, menuPath, action);
    }
}
