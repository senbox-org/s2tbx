package org.esa.beam.dataio.atmcorr;

import com.bc.ceres.swing.TableLayout;
import org.esa.beam.dataio.atmcorr.ui.AtmCorrIOParametersPanel;
import org.esa.beam.dataio.atmcorr.ui.AtmosphericCorrectionDialog;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.ui.TargetProductSelector;
import org.esa.beam.framework.gpf.ui.TargetProductSelectorModel;
import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.VisatApp;
import org.esa.beam.visat.actions.AbstractVisatAction;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.Window;
import java.io.IOException;

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
        final ModelessDialog dialog = AtmosphericCorrectionDialog.createInstance(getAppContext(), parent,
                appName, ModelessDialog.ID_APPLY_CLOSE, null);
        dialog.show();
    }


}
