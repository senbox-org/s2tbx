package org.esa.beam.dataio.s3;

import com.jidesoft.combobox.FileChooserComboBox;
import com.jidesoft.utils.Lm;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.util.SystemUtils;

import javax.media.jai.JAI;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class S3SpeedTester {

    private JTextArea textArea;

    public S3SpeedTester() {
        SystemUtils.init3rdPartyLibs(S3SpeedTester.class.getClassLoader());
        createUI();
    }

    private void createUI() {
        final JFrame frame = new JFrame("S3 speed test");
        frame.setSize(new Dimension(650, 450));

        final JPanel panel = GridBagUtils.createPanel();

        final JSpinner speedSpinner = new JSpinner(new SpinnerNumberModel(36.0, 0.1, Double.POSITIVE_INFINITY, 0.1));

        final JSpinner tileSizeSpinner = new JSpinner(new SpinnerNumberModel(512, 1, Integer.MAX_VALUE, 1));

        final long memoryCapacity = JAI.getDefaultInstance().getTileCache().getMemoryCapacity();
        final JSpinner tileCacheCapacitySpinner = new JSpinner(new SpinnerNumberModel(memoryCapacity, 1, Integer.MAX_VALUE, 1));

        textArea = new JTextArea();

        final JButton runButton = new JButton("Run speed test");

        final FileChooserComboBox fileChooserComboBox = new FileChooserComboBox();
        fileChooserComboBox.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("selectedItem")) {
                    runButton.setEnabled(true);
                }

            }
        });

        runButton.setEnabled(false);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final double tileCacheCapacityasDouble = Double.parseDouble(tileCacheCapacitySpinner.getValue().toString());
                    JAI.getDefaultInstance().getTileCache().setMemoryCapacity((long) tileCacheCapacityasDouble);
                    final String fileName = fileChooserComboBox.getSelectedItem().toString();
                    final double highestPossibleSpeed = Double.parseDouble(speedSpinner.getValue().toString());
                    final int tileSize = Integer.parseInt(tileSizeSpinner.getValue().toString());
                    runSpeedTest(panel, fileName, highestPossibleSpeed, tileSize);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }
            }
        });

        GridBagConstraints gbc = GridBagUtils.createConstraints("insets=4, anchor=NORTHWEST, fill=HORIZONTAL, weightx=1");
        GridBagUtils.addToPanel(panel, new JLabel("Highest Possible Reading Speed in MB/s:"), gbc, "gridx=0, gridy=0");
        GridBagUtils.addToPanel(panel, speedSpinner, gbc, "gridx=1, gridy=0");
        GridBagUtils.addToPanel(panel, new JLabel("Tile Size:"), gbc, "gridx=0, gridy=1");
        GridBagUtils.addToPanel(panel, tileSizeSpinner, gbc, "gridx=1, gridy=1");
        GridBagUtils.addToPanel(panel, new JLabel("Tile Cache Capacity:"), gbc, "gridx=0, gridy=2");
        GridBagUtils.addToPanel(panel, tileCacheCapacitySpinner, gbc, "gridx=1, gridy=2");
        GridBagUtils.addToPanel(panel, fileChooserComboBox, gbc, "gridx=0, gridy=3, gridwidth=2");
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        GridBagUtils.addToPanel(panel, textAreaScrollPane, gbc, "gridx=0, gridy=4, fill=BOTH, weighty=1");
        GridBagUtils.addToPanel(panel, runButton, gbc, "gridx=0, gridy=5, anchor=EAST, fill=NONE, weighty=0");

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private void runSpeedTest(final JPanel pane, String fileName, final double highestPossibleSpeed, final int tileSize) throws IOException, ExecutionException, InterruptedException {
        final ProductReaderPlugIn plugin = new Sentinel3ProductReaderPlugIn();
        Sentinel3ProductReader reader = new Sentinel3ProductReader(plugin);
        final Product product = reader.readProductNodes(fileName, null);
        textArea.setText("Info for " + product.getDisplayName() + ":\n");
        final Band[] productBands = product.getBands();
        for (final Band productBand : productBands) {
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    final String bandInfo = getBandInfo(highestPossibleSpeed, tileSize, productBand);
                    textArea.append(bandInfo);
                    return null;
                }
            };
            worker.execute();
            pane.repaint();
        }
    }

    private String getBandInfo(double highestPossibleSpeed, int tileSize, Band productBand) {
        final double rawStorageSizeInMB = (double) productBand.getRawStorageSize() / (1024 * 1024);
        final int width = productBand.getSceneRasterWidth();
        final int height = productBand.getSceneRasterHeight();
        final long before = System.nanoTime();
        for (int j = 0; j < width; j += tileSize) {
            for (int k = 0; k < height; k += tileSize) {
                Rectangle rectangle = new Rectangle(j, k, tileSize, tileSize);
                productBand.getSourceImage().getData(rectangle);
            }
        }
        final long after = System.nanoTime();
        final double elapsedTimeInSeconds = ((double) after - before) / 1e9;
        final double speed = rawStorageSizeInMB / elapsedTimeInSeconds;
        final double percentage = (speed / highestPossibleSpeed) * 100;
        return "Read '" + productBand.getName() + "' with a speed of " + speed + " MB/s (" + percentage + "%) \n";
    }

    public static void main(String[] args) throws IOException {
        Lm.verifyLicense("Brockmann Consult", "BEAM", "lCzfhklpZ9ryjomwWxfdupxIcuIoCxg2");
        new S3SpeedTester();
    }

}