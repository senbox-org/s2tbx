package org.esa.s2tbx.dataio.s2.preferences.ui.masks;

import com.bc.ceres.swing.TableLayout;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.runtime.Config;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by obarrile on 27/06/2016.
 */
public class S2ReaderOptionsPanel extends javax.swing.JPanel {

    private javax.swing.JCheckBox detectorFootprintMasks;
    private javax.swing.JCheckBox radiometricQualityMasks;
    private javax.swing.JCheckBox technicalQualityMasks;
    private javax.swing.JCheckBox cloudMasks;

    S2ReaderOptionsPanel(final S2ReaderOptionsPanelController controller) {
        initComponents();

        detectorFootprintMasks.addItemListener(e -> controller.changed());
        radiometricQualityMasks.addItemListener(e -> controller.changed());
        technicalQualityMasks.addItemListener(e -> controller.changed());
        cloudMasks.addItemListener(e -> controller.changed());
    }

    private void initComponents() {

        detectorFootprintMasks = new javax.swing.JCheckBox();
        Mnemonics.setLocalizedText(detectorFootprintMasks,
                                   NbBundle.getMessage(S2ReaderOptionsPanel.class,
                                                       "S2TBXReaderOptionsPanel.detectorFootprintMasks.text")); // NOI18N
        radiometricQualityMasks = new javax.swing.JCheckBox();
        Mnemonics.setLocalizedText(radiometricQualityMasks,
                                   NbBundle.getMessage(S2ReaderOptionsPanel.class,
                                                       "S2TBXReaderOptionsPanel.radiometricQualityMasks.text")); // NOI18N
        technicalQualityMasks = new javax.swing.JCheckBox();
        Mnemonics.setLocalizedText(technicalQualityMasks,
                                   NbBundle.getMessage(S2ReaderOptionsPanel.class,
                                                       "S2TBXReaderOptionsPanel.technicalQualityMasks.text")); // NOI18N
        cloudMasks = new javax.swing.JCheckBox();
        Mnemonics.setLocalizedText(cloudMasks,
                                   NbBundle.getMessage(S2ReaderOptionsPanel.class,
                                                       "S2TBXReaderOptionsPanel.cloudMasks.text")); // NOI18N


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(detectorFootprintMasks)
                                                            .addGap(0, 512, Short.MAX_VALUE)
                                                            .addComponent(radiometricQualityMasks)
                                                            .addGap(0, 512, Short.MAX_VALUE)
                                                            .addComponent(technicalQualityMasks)
                                                            .addGap(0, 512, Short.MAX_VALUE)
                                                            .addComponent(cloudMasks))
                                          .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                          .addComponent(detectorFootprintMasks)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(radiometricQualityMasks)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(technicalQualityMasks)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(cloudMasks)
                                          .addContainerGap())
        );
    }

    void load() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();

        detectorFootprintMasks.setSelected(
                preferences.getBoolean("s2tbx.dataio.detectorFootprintMasks", false));
        radiometricQualityMasks.setSelected(
                preferences.getBoolean("s2tbx.dataio.radiometricQualityMasks", false));
        technicalQualityMasks.setSelected(
                preferences.getBoolean("s2tbx.dataio.technicalQualityMasks", false));
        cloudMasks.setSelected(
                preferences.getBoolean("s2tbx.dataio.cloudMasks", false));
    }

    void store() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();

        preferences.putBoolean("s2tbx.dataio.detectorFootprintMasks", detectorFootprintMasks.isSelected());
        preferences.putBoolean("s2tbx.dataio.radiometricQualityMasks", radiometricQualityMasks.isSelected());
        preferences.putBoolean("s2tbx.dataio.technicalQualityMasks", technicalQualityMasks.isSelected());
        preferences.putBoolean("s2tbx.dataio.cloudMasks", cloudMasks.isSelected());

        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            SnapApp.getDefault().getLogger().severe(e.getMessage());
        }
    }

    boolean valid() {
        // Check whether form is consistent and complete
        return true;
    }
}
