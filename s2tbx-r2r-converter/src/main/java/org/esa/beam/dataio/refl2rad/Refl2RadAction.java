package org.esa.beam.dataio.refl2rad;

import java.awt.Window;

import org.esa.beam.dataio.refl2rad.ui.Refl2RadDialog;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.VisatApp;
import org.esa.beam.visat.actions.AbstractVisatAction;

/**
 * @author Tonio Fincke
 */
public class Refl2RadAction extends AbstractVisatAction {

    private String appName = "Sentinel 2 Refl. To Rad. Converter";
    @Override
    public void actionPerformed(CommandEvent event) {
        run();
    }

    public void run() {
        final Window parent = VisatApp.getApp().getApplicationWindow();
        final ModelessDialog dialog = Refl2RadDialog.createInstance(getAppContext(), parent,
                appName, ModelessDialog.ID_APPLY_CLOSE, null);
        dialog.show();
    }


}
