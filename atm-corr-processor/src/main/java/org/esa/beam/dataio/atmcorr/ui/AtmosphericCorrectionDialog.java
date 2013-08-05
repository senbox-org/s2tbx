package org.esa.beam.dataio.atmcorr.ui;

import com.bc.ceres.core.ProcessObserver;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.progress.DialogProgressMonitor;
import com.bc.ceres.swing.progress.ProgressDialog;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import org.esa.beam.dataio.atmcorr.AtmCorrCaller;
import org.esa.beam.dataio.s2.update.S2Config;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.util.io.FileUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author Tonio Fincke
 */
public class AtmosphericCorrectionDialog extends ModelessDialog {

    private JCheckBox scOnlyBox;
    private JCheckBox acOnlyBox;
    private JComboBox resolutionBox;
    private final JTabbedPane form;
    private static AppContext appContext;
    private final AtmCorrIOParametersPanel ioParametersPanel;
    private final Window parentComponent;
    private ProgressDialog progressDialog;
    private String fileLocation;
    private final static String default_l1c_name = "Level-1C_User_Product";
    private final static String default_l2a_name = "Level-2A_User_Product";
    private JTextArea area;
    private JScrollPane areaScrollPane;

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
        fileLocation = ((File) sourceProduct.getProductReader().getInput()).getParent();
        final int resolution = (Integer) resolutionBox.getSelectedItem();
        try {
            final Process atmCorrProcess =
                    new AtmCorrCaller().createProcess(fileLocation, resolution, scOnlyBox.isSelected(), acOnlyBox.isSelected());
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
                progressDialog.setMessageComponent(createMessageComponent());

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
//                        Files.move(new File(defaultPath).toPath(), l2File.toPath());
                        }
                        File targetMetadataFile = addMetadataFileIfNecessary(l2File);
                        if (ioParametersPanel.shallBeOpenedInApp()) {
                            Product product = ProductIO.readProduct(targetMetadataFile);
                            appContext.getProductManager().addProduct(product);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
        final JPanel panel = GridBagUtils.createPanel();
        GridBagConstraints gbc = new GridBagConstraints();

        area = new JTextArea();
        areaScrollPane = new JScrollPane(area);
        areaScrollPane.setVisible(false);

        final String moreButtonText = "More >>";
        final String lessButtonText = "Less <<";
        final JButton extendButton = new JButton(moreButtonText);
        extendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (areaScrollPane.isVisible()) {
                    areaScrollPane.setVisible(false);
                    extendButton.setText(moreButtonText);
                } else {
                    areaScrollPane.setVisible(true);
                    extendButton.setText(lessButtonText);
                }
            }
        });


        GridBagUtils.addToPanel(panel, extendButton, gbc, "gridx=2,gridy=0,anchor=NORTHEAST,weightx=1,weighty=0.1");
        GridBagUtils.addToPanel(panel, areaScrollPane, gbc, "gridx=0,gridy=1,weighty=1,gridwidth=3,gridheight=2,fill=BOTH");

        return panel;
    }

    /**
     * Currently, the AtmCorrProcessor does not create metadata files at product level. This method adds an empty dummy
     * metadata file and will hopefully become redundant in the near future
     */
    private File addMetadataFileIfNecessary(File l2FileDir) throws IOException {
        final FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return S2Config.METADATA_NAME_2A_PATTERN.matcher(name).matches();
            }
        };
        File[] l2MetadataFiles = l2FileDir.listFiles(filter);
        if (l2MetadataFiles.length > 0) {
            return l2MetadataFiles[0];
        } else {
            String l2MetadataFilename;
            String l1cDirPath = new File(fileLocation).getName();
            final FilenameFilter l1cMetadataFilter = new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return S2Config.METADATA_NAME_1C_PATTERN.matcher(s).matches();
                }
            };
            File[] metadataFiles = new File(l1cDirPath).listFiles(l1cMetadataFilter);
            if (metadataFiles != null && metadataFiles.length > 0) {
                l2MetadataFilename = metadataFiles[0].getName().replace("1C", "2A");
            } else if (S2Config.PRODUCT_DIRECTORY_1C_PATTERN.matcher(l1cDirPath).matches()) {
                String changingName = FileUtils.getFilenameWithoutExtension(l1cDirPath).replace("PRD", "MTD").replace("1C", "2A") + ".xml";
                if (l1cDirPath.endsWith(".SAFE")) {
                    l2MetadataFilename = changingName.replace("MSI", "SAF");
                } else {
                    l2MetadataFilename = changingName.replace("MSI", "DMP");
                }
            } else {
                l2MetadataFilename = "Product_Metadata_File.xml";
            }
            File l2MetadataFile = new File(l2FileDir + "/" + l2MetadataFilename);
            l2MetadataFile.createNewFile();
            return l2MetadataFile;
        }
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
            if(line.contains("error")) {
                showErrorDialog(line);
            } else if (line.contains("%")) {
                double workDone = Double.parseDouble(line.split(":")[1]) * 100;
                int progress = (int) workDone - lastWork;
                lastWork = (int) workDone;
                pm.worked(progress);
            }
            area.append(line + "\n");
        }

        @Override
        public void onStderrLineReceived(ProcessObserver.ObservedProcess process, String line, ProgressMonitor pm) {
            if(errorMessageBuilder == null) {
                errorMessageBuilder = new StringBuilder();
            }
            errorMessageBuilder.append(line).append("\n");
        }

        @Override
        public void onObservationEnded(ProcessObserver.ObservedProcess process, Integer exitCode, ProgressMonitor pm) {
            if(errorMessageBuilder != null) {
                showErrorDialog(errorMessageBuilder.toString());
            }
            progressDialog.close();
        }
    }

}
