package org.esa.beam.dataio.spatiotemp;

import java.awt.Window;

import org.esa.beam.dataio.spatiotemp.ui.SpatioTemporalDialog;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.VisatApp;
import org.esa.beam.visat.actions.AbstractVisatAction;

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
