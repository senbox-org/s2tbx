package org.esa.s2tbx.dataio.spatiotemp;

import org.esa.s2tbx.dataio.spatiotemp.ui.SpatioTemporalDialog;
import org.esa.snap.framework.ui.ModelessDialog;
import org.esa.snap.framework.ui.command.CommandEvent;
import org.esa.snap.visat.VisatApp;
import org.esa.snap.visat.actions.AbstractVisatAction;

import java.awt.Window;

/**
 * @author Uwe Mueller-Wilm
 */
public class SpatioTemporalAction extends AbstractVisatAction {

    private String appName = "Sentinel 2 Level-3 Processor";

    @Override
    public void actionPerformed(CommandEvent event) {
        run();
    }

    public void run() {
        final Window parent = VisatApp.getApp().getApplicationWindow();
        final ModelessDialog dialog = SpatioTemporalDialog.createInstance(getAppContext(), parent,
                                                                          appName, ModelessDialog.ID_APPLY_CLOSE, null);
        dialog.show();
    }


}
