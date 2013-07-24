package org.esa.beam.dataio.atmcorr;

import com.bc.ceres.swing.TableLayout;
import org.esa.beam.dataio.atmcorr.ui.AtmCorrIOParametersPanel;
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
    private JCheckBox scOnlyBox;
    private JCheckBox acOnlyBox;
    private JComboBox<Integer> resolutionBox;

    @Override
    public void actionPerformed(CommandEvent event) {
        if (VisatApp.getApp().getSelectedProduct() != null) {
            run();
        }
    }

    public void run() {
        final Window parent = VisatApp.getApp().getApplicationWindow();
        final ModelessDialog dialog = new ModelessDialog(parent, appName, ModelessDialog.ID_APPLY_CLOSE_HELP, null);
        JTabbedPane form = new JTabbedPane();
        final AtmCorrIOParametersPanel ioParametersPanel = new AtmCorrIOParametersPanel(getAppContext(), new TargetProductSelector(new TargetProductSelectorModel()));
        form.add("I/O Parameters", ioParametersPanel);
        form.add("Processing Parameters", createParametersPanel());
        dialog.setContent(form);
        int i = dialog.show();
        if (i == ModalDialog.ID_OK) {
            final Product sourceProduct = ioParametersPanel.getSourceProduct();
            final int resolution = (Integer) resolutionBox.getSelectedItem();
            try {
                AtmCorrCaller.call(sourceProduct.getFileLocation().getPath(), resolution, scOnlyBox.isSelected(), acOnlyBox.isSelected());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public JPanel createParametersPanel() {
        JPanel panel = new JPanel();

        final TableLayout tableLayout = new TableLayout(1);
        tableLayout.setTableAnchor(TableLayout.Anchor.WEST);
        tableLayout.setTableWeightX(1.0);
        tableLayout.setTableFill(TableLayout.Fill.HORIZONTAL);
        tableLayout.setTablePadding(3, 3);

        panel.setLayout(tableLayout);
        panel.add(createTargetResolutionPanel());

        scOnlyBox = new JCheckBox("Scene classification at 60m resolution only");
        acOnlyBox = new JCheckBox("Use ATCOR compatiblity mode");
        panel.add(scOnlyBox);
        panel.add(acOnlyBox);
        panel.add(tableLayout.createVerticalSpacer());

        return panel;
    }

    private JPanel createTargetResolutionPanel() {
        JPanel panel = new JPanel();
        final TableLayout tableLayout = new TableLayout(3);
        tableLayout.setTableAnchor(TableLayout.Anchor.WEST);
        tableLayout.setTableWeightX(1.0);
        tableLayout.setTableFill(TableLayout.Fill.HORIZONTAL);
        tableLayout.setTablePadding(3, 3);
        panel.setLayout(tableLayout);

        tableLayout.setCellWeightX(0, 0, 0.0);
        final JLabel resolutionLabel = new JLabel("Target resolution:");

        tableLayout.setCellWeightX(0, 1, 1.0);
        Integer[] resolutions = {60, 20, 10};
        resolutionBox = new JComboBox<Integer>(resolutions);

        tableLayout.setCellWeightX(0, 2, 0.0);
        final JLabel resolutionUnitLabel = new JLabel("m");

        panel.add(resolutionLabel);
        panel.add(resolutionBox);
        panel.add(resolutionUnitLabel);

        return panel;
    }

}
