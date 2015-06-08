package org.esa.s2tbx.dataio.atmcorr;

import org.esa.s2tbx.dataio.atmcorr.ui.AtmosphericCorrectionDialog;
import org.esa.snap.framework.ui.ModelessDialog;
import org.esa.snap.framework.ui.command.CommandEvent;
import org.esa.snap.rcp.actions.AbstractSnapAction;
import org.esa.snap.visat.VisatApp;
import org.esa.snap.visat.actions.AbstractVisatAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

import java.awt.Window;
import java.awt.event.ActionEvent;

@ActionID(
        category = "Processing",
        id = "org.esa.s3tbx.meris.radiometry.ui.MerisRadiometryCorrectionAction"
)
@ActionRegistration(displayName = "#CTL_MerisRadiometryCorrectionAction_Text")
@ActionReference(
        path = "Menu/Processing/Preprocessing",
        position = 100
)
@NbBundle.Messages({"CTL_MerisRadiometryCorrectionAction_Text=MERIS Radiometric Correction"})
public class AtmosphericCorrectionAction extends AbstractSnapAction {


    private String appName = "Sentinel 2 Level-2A Processor";

    @Override
    public void actionPerformed(ActionEvent event) {
        run();
    }

    public void run() {
        final Window parent = VisatApp.getApp().getApplicationWindow();
        final ModelessDialog dialog = AtmosphericCorrectionDialog.createInstance(getAppContext(), parent,
                                                                                 appName, ModelessDialog.ID_APPLY_CLOSE, null);
        dialog.show();
    }


}
