package org.esa.s2tbx.dataio.spatiotemp.ui;

import com.bc.ceres.core.ProcessObserver;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.progress.DialogProgressMonitor;
import com.bc.ceres.swing.progress.ProgressDialog;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import org.esa.s2tbx.dataio.spatiotemp.SpatioTempProcessBuilder;
import org.esa.snap.framework.dataio.ProductIO;
import org.esa.snap.framework.datamodel.Product;
import org.esa.snap.framework.ui.AppContext;
import org.esa.snap.framework.ui.ModelessDialog;
import org.esa.snap.util.io.FileUtils;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.awt.Window;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Tonio Fincke
 */
public class SpatioTemporalDialog extends ModelessDialog {

    private JCheckBox scOnlyBox;
    private JComboBox resolutionBox;
    private final JTabbedPane form;
    private static AppContext appContext;
    private final SpatioTempIOParametersPanel ioParametersPanel;
    private final Window parentComponent;
    private ProgressDialog progressDialog;
    private String fileLocation;
    private final static String default_l1c_name = "Level-2A_User_Product";
    private final static String default_l2a_name = "Level-03_User_Product";
    private JTextArea area;

    public static SpatioTemporalDialog createInstance(AppContext app, Window parent, String title, int buttonMask, String helpID) {
        appContext = app;
        return new SpatioTemporalDialog(parent, title, buttonMask, helpID);
    }

    public SpatioTemporalDialog(Window parent, String title, int buttonMask, String helpID) {
        super(parent, title, buttonMask, helpID);
        parentComponent = parent;
        form = new JTabbedPane();
        ioParametersPanel = new SpatioTempIOParametersPanel(appContext);
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
            final Process spatioTempProcess =
                    new SpatioTempProcessBuilder().createProcess(fileLocation, resolution, scOnlyBox.isSelected());
            executeProcess(spatioTempProcess);
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
        final String processName = "Spatio Temporal";
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
                progressDialog.setTitle("Performing spatio temporal synthesis");

                final ProcessObserver processObserver = new ProcessObserver(process);
                processObserver.setProgressMonitor(monitor);
                processObserver.setHandler(new ProcessObserverHandler());
                processObserver.start();
                return null;
            }

            @Override
            protected void done() {
                if (!progressDialog.isCanceled() && process.exitValue() == 0) {
                    String defaultPath = new File(fileLocation).getParent() + "/" + default_l2a_name;
                    String targetDir = ioParametersPanel.getTargetDir();
                    String targetName = ioParametersPanel.getTargetName();
                    String targetPath = targetDir + "/" + targetName;
                    try {
                        File l2File = new File(targetPath);
                        if (!defaultPath.equals(targetPath)) {
                            if (l2File.exists()) {
                                FileUtils.deleteTree(l2File);
                            }
                            File defaultFile = new File(defaultPath);
                            copyDir(defaultFile, l2File);
                            FileUtils.deleteTree(defaultFile);
                        }
                        File targetMetadataFile = l2File; // ToDo
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

    //todo replace this method with Files.move as soon as a build agent for java 7 is available
    public void copyDir(File source, File dest) throws FileNotFoundException, IOException {
        File[] files = source.listFiles();
        dest.mkdirs();
        for (File file : files) {
            if (file.isDirectory()) {
                copyDir(file, new File(dest.getAbsolutePath() + System.getProperty("file.separator") + file.getName()));
            } else {
                copyFile(file, new File(dest.getAbsolutePath() + System.getProperty("file.separator") + file.getName()));
            }
        }
    }

    public void copyFile(File file, File ziel) throws FileNotFoundException, IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(ziel, true));
        int bytes = 0;
        while ((bytes = in.read()) != -1) {
            out.write(bytes);
        }
        in.close();
        out.close();
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
            pm.beginTask("Spatio Temporal", 10000);
            lastWork = 0;
        }

        @Override
        public void onStdoutLineReceived(ProcessObserver.ObservedProcess process, String line, ProgressMonitor pm) {
            if (line.contains("error")) {
                showErrorDialog(line);
            } else if (line.contains("%") && line.contains("Procedure") && lastWork < 10000) {
                String[] splitLine = line.split("P");
                updateProgressMonitor(splitLine[1].split(":")[1], pm);

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
