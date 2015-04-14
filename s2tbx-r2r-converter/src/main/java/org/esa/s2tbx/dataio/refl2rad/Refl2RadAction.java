package org.esa.s2tbx.dataio.refl2rad;

import org.esa.s2tbx.dataio.refl2rad.ui.Refl2RadDialog;
import org.esa.snap.framework.ui.ModelessDialog;
import org.esa.snap.framework.ui.command.CommandEvent;
import org.esa.snap.visat.VisatApp;
import org.esa.snap.visat.actions.AbstractVisatAction;

import java.awt.Window;

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
