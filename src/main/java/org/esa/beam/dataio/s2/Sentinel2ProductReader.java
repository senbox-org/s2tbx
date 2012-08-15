package org.esa.beam.dataio.s2;

import com.bc.ceres.core.PrintWriterProgressMonitor;
import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.process.ProcessObserver;
import org.esa.beam.util.io.FileUtils;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;

/**
 * Only used to pop-up a reader configuration dialog mock-up.
 *
 * @author Norman Fomferra
 */
public class Sentinel2ProductReader extends AbstractProductReader {

    public Sentinel2ProductReader(Sentinel2ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }


    @Override
    protected Product readProductNodesImpl() throws IOException {
        final File inputFile = new File(getInput().toString());

        final String baseName = FileUtils.getFilenameWithoutExtension(inputFile);
        final File outputFile = new File(baseName + "_0.pgx");


        if (!outputFile.exists()) {

            final String exePath = "C:\\Program Files (x86)\\OpenJPEG 1.5\\bin\\j2k_to_image.exe";

            final String[] command = {
                    exePath,
                    "-i",
                    inputFile.getPath(),
                    "-o",
                    new File(baseName + ".pgx").getPath()
            };

            final String[] envp = new String[0];
            final File workingDir = new File(".");
            final Process process = Runtime.getRuntime().exec(command, envp, workingDir);

            ProcessObserver.start(process, "j2k_to_image", 100, ProcessObserver.Mode.BLOCKING, new PrintWriterProgressMonitor(System.out), new ProcessObserver.Handler() {
                @Override
                public void handleLineOnStdoutReceived(String line, Process process, ProgressMonitor pm) {
                    System.out.println(line);
                }

                @Override
                public void handleLineOnStderrReceived(String line, Process process, ProgressMonitor pm) {
                    System.err.println(line);
                }

                @Override
                public void handleProcessExited(int exitCode) {
                    if (exitCode != 0) {
                        outputFile.delete();
                    }
                    System.out.println("Exit code " + exitCode);
                }
            });
        }

        return ProductIO.readProduct(outputFile, "IMAGE");
    }

/*
    @Override
    public Product readProductNodes(Object input, ProductSubsetDef subsetDef) throws IOException {
        final int i = showReaderParametersDialog();
        if (i == ModalDialog.ID_OK) {
            return super.readProductNodes(input, subsetDef);
        } else {
            return null;
        }
    }
*/

    public int showReaderParametersDialog() throws IOException {
        ModalDialog modalDialog = new ModalDialog(null, "Sentinel-2 MSI Reader Options", ModalDialog.ID_OK_CANCEL_HELP, "");
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(6, 6, 6, 6));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets.top = 2;
        constraints.insets.bottom = 2;
        constraints.insets.left = 2;
        constraints.insets.right = 2;
        constraints.gridx = 0;
        constraints.gridy = -1;

        constraints.gridy++;
        content.add(new JLabel("This data product contains images in various spatial resolutions."), constraints);
        constraints.insets.top = 0;
        constraints.gridy++;
        content.add(new JLabel("Please specify how to treat them:"), constraints);

        constraints.insets.top = 12;
        constraints.gridy++;
        content.add(new JRadioButton("Read a single data product and make all bands have the same resolution:", true), constraints);
        constraints.insets.top = 0;
        constraints.insets.left = 24;

        constraints.gridy++;
        content.add(new JRadioButton("Scale to 10m (duplicate 20m and 60m pixels)", true), constraints);
        constraints.gridy++;
        content.add(new JRadioButton("Scale to 20m (average 10m and duplicate 60m pixels)", false), constraints);
        constraints.gridy++;
        content.add(new JRadioButton("Scale to 60m (average 10m and 20m pixels)", false), constraints);

        constraints.gridy++;
        constraints.insets.top = 12;
        constraints.insets.left = 2;
        content.add(new JRadioButton("Read as groups of bands with different resolutions:", false), constraints);
        constraints.insets.top = 2;
        constraints.insets.left = 24;

        constraints.insets.top = 0;
        constraints.gridy++;
        JCheckBox checkBox10 = new JCheckBox("Read 10m bands (B2, B8, B3, B4)", true);
        checkBox10.setEnabled(false);
        content.add(checkBox10, constraints);
        constraints.gridy++;
        JCheckBox checkBox20 = new JCheckBox("Read 20m bands (B5, B6, B7, B8a, B11, B12)", true);
        checkBox20.setEnabled(false);
        content.add(checkBox20, constraints);
        constraints.gridy++;
        JCheckBox checkBox60 = new JCheckBox("Read 60m bands (B1, B9, B10)", true);
        checkBox60.setEnabled(false);
        content.add(checkBox60, constraints);

        constraints.gridy++;
        constraints.insets.left = 2;
        constraints.insets.top = 12;
        content.add(new JCheckBox("Always use the above settings and don't ask again."), constraints);

        constraints.gridy++;
        constraints.insets.left = 24;
        constraints.insets.top = 0;
        content.add(new JLabel("<html><small>Note that you can always change settings later in the preferences dialog.</small></html>"), constraints);

        modalDialog.setContent(content);
        return modalDialog.show();

    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // todo
    }
}
