package org.esa.s2tbx.dataio.gdal.preferences;

import org.esa.s2tbx.dataio.gdal.GDALLoaderConfig;
import org.esa.s2tbx.dataio.gdal.GDALVersion;
import org.openide.awt.Mnemonics;

import javax.swing.*;
import java.awt.*;

/**
 * GDAL Options Panel for GDAL native library loader.
 * Used for provide a UI to the strategy with loading GDAL native library.
 *
 * @author Adrian Draghici
 */
class GDALOptionsPanel extends JPanel {

    private JRadioButton useInternalGDALLibrary;
    private JRadioButton useInstalledGDALLibrary;

    /**
     * Creates new instance for this class
     *
     * @param controller the GDAL Options Controller instance
     */
    GDALOptionsPanel(final GDALOptionsPanelController controller) {
        initComponents();

        useInternalGDALLibrary.addItemListener(e -> controller.changed());
        useInstalledGDALLibrary.addItemListener(e -> controller.changed());
    }

    /**
     * Initializes the UI components
     */
    private void initComponents() {
        useInternalGDALLibrary = new JRadioButton();
        useInstalledGDALLibrary = new JRadioButton();
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(useInternalGDALLibrary);
        buttonGroup.add(useInstalledGDALLibrary);

        JTextField locationInternalField = new JTextField();
        locationInternalField.setEditable(false);
        GDALVersion internalVersion = GDALVersion.getInternalVersion();
        Mnemonics.setLocalizedText(useInternalGDALLibrary, "Use internal GDAL version from SNAP (" + internalVersion.getId() + ")");
        locationInternalField.setText(internalVersion.getLocation());
        locationInternalField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JTextField locationInstalledField = new JTextField();
        locationInstalledField.setEditable(false);
        GDALVersion installedVersion = GDALVersion.getInstalledVersion();
        String versionName = "not installed";
        String location = "not found";
        if (installedVersion != null) {
            versionName = installedVersion.getId();
            location = installedVersion.getLocation();
        }
        locationInstalledField.setText(location);
        locationInstalledField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        Mnemonics.setLocalizedText(useInstalledGDALLibrary, "Use installed GDAL version from Operating System (" + versionName + ")");

        JLabel label = new JLabel("NOTE: Restart SNAP to take changes effect.");
        JLabel locationInternalLabel = new JLabel("Location: ");
        JPanel locationInternalPanel = new JPanel();
        locationInternalPanel.add(locationInternalLabel);
        locationInternalPanel.add(locationInternalField);
        locationInternalPanel.setLayout(new BoxLayout(locationInternalPanel, BoxLayout.X_AXIS));
        JLabel locationInstalledLabel = new JLabel("Found in: ");
        JPanel locationInstalledPanel = new JPanel();
        locationInstalledPanel.add(locationInstalledLabel);
        locationInstalledPanel.add(locationInstalledField);
        locationInstalledPanel.setLayout(new BoxLayout(locationInstalledPanel, BoxLayout.X_AXIS));
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(useInternalGDALLibrary)
                                        .addGap(0, 512, Short.MAX_VALUE)
                                        .addComponent(locationInternalPanel)
                                        .addGap(0, 512, Short.MAX_VALUE)
                                        .addComponent(useInstalledGDALLibrary)
                                        .addGap(0, 512, Short.MAX_VALUE)
                                        .addComponent(locationInstalledPanel)
                                        .addGap(0, 512, Short.MAX_VALUE)
                                        .addComponent(label))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(useInternalGDALLibrary)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(locationInternalPanel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(useInstalledGDALLibrary).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(locationInstalledPanel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label)
                                .addContainerGap())
        );
    }

    /**
     * Loads the configuration from GDAL Loader Config on UI
     */
    void load() {
        if (GDALLoaderConfig.getInstance().useInstalledGDALLibrary()) {
            useInstalledGDALLibrary.setSelected(true);
        } else {
            useInternalGDALLibrary.setSelected(true);
        }
    }

    /**
     * Saves the configuration on GDAL Loader Config from UI
     */
    void store() {
        GDALLoaderConfig.getInstance().setUseInstalledGDALLibrary(useInstalledGDALLibrary.isSelected());
    }

    /**
     * Checks whether or not form is consistent and complete
     *
     * @return {@code true} if form is consistent and complete
     */
    boolean valid() {
        return true;
    }
}
