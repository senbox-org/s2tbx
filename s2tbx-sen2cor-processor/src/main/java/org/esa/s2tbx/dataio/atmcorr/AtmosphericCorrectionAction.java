package org.esa.s2tbx.dataio.atmcorr;

import org.esa.s2tbx.dataio.atmcorr.ui.AtmosphericCorrectionDialog;
import org.esa.snap.framework.ui.ModelessDialog;
import org.esa.snap.framework.ui.command.CommandEvent;
import org.esa.snap.visat.VisatApp;
import org.esa.snap.visat.actions.AbstractVisatAction;

import java.awt.Window;

/**
 * @author Tonio Fincke
 */
public class AtmosphericCorrectionAction extends AbstractVisatAction {

    private String appName = "Sentinel 2 Level-2A Processor";

    @Override
    public void actionPerformed(CommandEvent event) {
        run();
    }

    public void run() {
        final Window parent = VisatApp.getApp().getApplicationWindow();
        final ModelessDialog dialog = AtmosphericCorrectionDialog.createInstance(getAppContext(), parent,
                                                                                 appName, ModelessDialog.ID_APPLY_CLOSE, null);
        dialog.show();
    }


}
