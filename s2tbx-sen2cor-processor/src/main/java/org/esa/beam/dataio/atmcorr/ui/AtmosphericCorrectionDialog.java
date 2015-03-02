package org.esa.beam.dataio.atmcorr.ui;

import java.awt.Window;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.esa.beam.dataio.atmcorr.AtmCorrProcessBuilder;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.util.io.FileUtils;

import com.bc.ceres.core.ProcessObserver;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.progress.DialogProgressMonitor;
import com.bc.ceres.swing.progress.ProgressDialog;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;

/**
 * @author Tonio Fincke
 */
public class AtmosphericCorrectionDialog extends ModelessDialog {

    private JCheckBox scOnlyBox;
    private JComboBox resolutionBox;
    private final JTabbedPane form;
    private static AppContext appContext;
    private final AtmCorrIOParametersPanel ioParametersPanel;
    private final Window parentComponent;
    private ProgressDialog progressDialog;
    private String fileLocation;
    private JTextArea area;

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
        AbstractButton button = getButton(ID_APPLY);
        button.setText("Run");
        button.setMnemonic('R');
    }

    @Override
    public int show() {
        setContent(form);
        return super.show();
    }

    @Override
    protected void onApply() {
        super.onApply();
        final Product sourceProduct = ioParametersPanel.getSourceProduct();
        fileLocation = ((File) sourceProduct.getProductReader().getInput()).getParent();
        final int resolution = (Integer) resolutionBox.getSelectedItem();
        try {
            final Process atmCorrProcess =
                    new AtmCorrProcessBuilder().createProcess(fileLocation, resolution, scOnlyBox.isSelected());
            executeProcess(atmCorrProcess);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onClose() {
        super.onClose();
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

        scOnlyBox = new JCheckBox("Only Scene Classification");
        panel.add(scOnlyBox);
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
        resolutionBox = new JComboBox(resolutions);

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
                progressDialog.setExtensibleMessageComponent(createMessageComponent(), false);
                progressDialog.setTitle("Performing Atmospheric Correction...");

                final ProcessObserver processObserver = new ProcessObserver(process);
                processObserver.setProgressMonitor(monitor);
                processObserver.setHandler(new ProcessObserverHandler());
                processObserver.start();
                return null;
            }

            @Override
            protected void done() {
                if (!progressDialog.isCanceled() && process.exitValue() == 0) {
                    String targetDir = ioParametersPanel.getTargetDir();
                    String targetName = ioParametersPanel.getTargetName();
                    String targetPath = targetDir + "/" + targetName + ".xml";
                    try {
                        File targetMetadataFile = new File(targetPath);
                        if (ioParametersPanel.shallBeOpenedInApp()) {
                            Product product = ProductIO.readProduct(targetMetadataFile);
                            appContext.getProductManager().addProduct(product);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (progressDialog.isCanceled()) {
                    progressDialog.close();
                }
            }
        };
        swingWorker.execute();
    }

    private JComponent createMessageComponent() {
        area = new JTextArea();
        area.setEditable(false);
        JScrollPane areaScrollPane = new JScrollPane(area);
        return areaScrollPane;
    }


    private class ProcessObserverHandler implements ProcessObserver.Handler {

        int lastWork;
        StringBuilder errorMessageBuilder;

        @Override
        public void onObservationStarted(ProcessObserver.ObservedProcess process, ProgressMonitor pm) {
            progressDialog.show();
            pm.beginTask("Atmospheric Correction", 10000);
            lastWork = 0;
        }

        @Override
        public void onStdoutLineReceived(ProcessObserver.ObservedProcess process, String line, ProgressMonitor pm) {
            if (line.contains("error")) {
                showErrorDialog(line);
            } else if (line.contains("%") && lastWork < 10000) {
                updateProgressMonitor(line.split(":")[1], pm);
            }
            area.append(line + "\n");
        }

        private void updateProgressMonitor(String s, ProgressMonitor pm) {
            double workDone = Double.parseDouble(s) * 100;
            int progress = (int) workDone - lastWork;
            if (workDone > 10000) {
                progress = 9999 - lastWork;
            }
            lastWork = (int) workDone;
            pm.worked(progress);
        }

        @Override
        public void onStderrLineReceived(ProcessObserver.ObservedProcess process, String line, ProgressMonitor pm) {
            if (errorMessageBuilder == null) {
                errorMessageBuilder = new StringBuilder();
            }
            errorMessageBuilder.append(line).append("\n");
        }

        @Override
        public void onObservationEnded(ProcessObserver.ObservedProcess process, Integer exitCode, ProgressMonitor pm) {
            if (errorMessageBuilder != null) {
                showErrorDialog(errorMessageBuilder.toString());
            }
            errorMessageBuilder = null;
            progressDialog.close();
        }
    }

}
