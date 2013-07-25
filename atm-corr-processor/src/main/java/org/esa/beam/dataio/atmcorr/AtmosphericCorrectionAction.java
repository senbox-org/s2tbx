package org.esa.beam.dataio.atmcorr;

import org.esa.beam.dataio.atmcorr.ui.AtmosphericCorrectionDialog;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.VisatApp;
import org.esa.beam.visat.actions.AbstractVisatAction;

import java.awt.Window;

/**
 * @author Tonio Fincke
 */
public class AtmosphericCorrectionAction extends AbstractVisatAction {

    private String appName = "Atmospheric Correction Processor (Sentinel 2)";

    @Override
    public void actionPerformed(CommandEvent event) {
        run();
    }

    public void run() {
        final Window parent = VisatApp.getApp().getApplicationWindow();
        final ModelessDialog dialog = AtmosphericCorrectionDialog.createInstance(getAppContext(), parent, appName, ModelessDialog.ID_APPLY_CLOSE_HELP, null);
        dialog.show();
    }


}
