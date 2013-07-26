package org.esa.beam.dataio.atmcorr.ui;

import com.bc.ceres.core.ProcessObserver;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.progress.DialogProgressMonitor;
import com.bc.ceres.swing.progress.ProgressDialog;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import org.esa.beam.dataio.atmcorr.AtmCorrCaller;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModelessDialog;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.Window;
import java.io.File;
import java.io.IOException;

/**
 * @author Tonio Fincke
 */
public class AtmosphericCorrectionDialog extends ModelessDialog {

    private JCheckBox scOnlyBox;
    private JCheckBox acOnlyBox;
    private JComboBox<Integer> resolutionBox;
    private final JTabbedPane form;
    private static AppContext appContext;
    private final AtmCorrIOParametersPanel ioParametersPanel;
    private final Window parentComponent;
    private ProgressDialog progressDialog;

    public static AtmosphericCorrectionDialog createInstance(AppContext app, Window parent, String title, int buttonMask, String helpID) {
        appContext = app;
        return new AtmosphericCorrectionDialog(parent, title, buttonMask, helpID);
    }

    public AtmosphericCorrectionDialog(Window parent, String title, int buttonMask, String helpID) {
        super(parent, title, buttonMask, helpID);
        parentComponent = parent;
        form = new JTabbedPane();
        ioParametersPanel = new AtmCorrIOParametersPanel(appContext);
        form.add("I/O Parameters", ioParametersPanel);
        form.add("Processing Parameters", createParametersPanel());
    }

    @Override
    public int show() {
        setContent(form);
        return super.show();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onApply() {
        super.onApply();
        final Product sourceProduct = ioParametersPanel.getSourceProduct();
        String fileLocation = ((File) sourceProduct.getProductReader().getInput()).getParent();
        final int resolution = (Integer) resolutionBox.getSelectedItem();
        try {
            final Process atmCorrProcess =
                    new AtmCorrCaller().createProcess(fileLocation, resolution, scOnlyBox.isSelected(), acOnlyBox.isSelected());

            executeProcess(atmCorrProcess);


//            AtmCorrCaller.call(fileLocation, resolution, scOnlyBox.isSelected(), acOnlyBox.isSelected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onClose() {
        super.onClose();
    }

    @Override
    protected void onHelp() {
        super.onHelp();
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

    private void executeProcess(final Process process) {
        final String processName = "Atmospheric Correction";
        ProgressMonitorSwingWorker swingWorker = new ProgressMonitorSwingWorker(parentComponent, processName) {
            @Override
            protected Object doInBackground(ProgressMonitor pm) throws Exception {
                if (process == null) {
                    throw new IOException("Failed to create process" + processName);
                }
                progressDialog = new ProgressDialog(parentComponent);
                progressDialog.setMaximum(10000);
                final DialogProgressMonitor monitor = new DialogProgressMonitor(progressDialog);

                final ProcessObserver processObserver = new ProcessObserver(process);
                processObserver.setProgressMonitor(monitor);
                processObserver.setHandler(new ProcessObserverHandler());
                processObserver.start();
                return null;
            }
        };
        swingWorker.execute();
    }

    private class ProcessObserverHandler implements ProcessObserver.Handler {

        int lastWork;

        @Override
        public void onObservationStarted(ProcessObserver.ObservedProcess process, ProgressMonitor pm) {
            progressDialog.show();
            pm.beginTask("Atmospheric Correction", 10000);
            lastWork = 0;
        }

        @Override
        public void onStdoutLineReceived(ProcessObserver.ObservedProcess process, String line, ProgressMonitor pm) {
            if(line.contains("%")) {
                double workDone = Double.parseDouble(line.split(":")[1]) * 100;
                int progress = (int) workDone - lastWork;
                lastWork = (int) workDone;
                pm.worked(progress);

            }
        }

        @Override
        public void onStderrLineReceived(ProcessObserver.ObservedProcess process, String line, ProgressMonitor pm) {
        }

        @Override
        public void onObservationEnded(ProcessObserver.ObservedProcess process, Integer exitCode, ProgressMonitor pm) {
            System.out.println("ended");
            progressDialog.close();
        }
    }

}
