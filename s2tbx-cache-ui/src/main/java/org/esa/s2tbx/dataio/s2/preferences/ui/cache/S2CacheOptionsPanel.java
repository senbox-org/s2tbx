/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esa.s2tbx.dataio.s2.preferences.ui.cache;

import com.bc.ceres.swing.TableLayout;
import org.esa.s2tbx.dataio.cache.S2CacheUtils;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.runtime.Config;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

final class S2CacheOptionsPanel extends javax.swing.JPanel {


    private JComboBox<String> box;
    private JLabel informationLabel;

    S2CacheOptionsPanel(final S2CacheOptionsPanelController controller) {
        initComponents();

        box.addItemListener(e -> controller.changed());
    }

    private void initComponents() {

        //Main panel
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Combos Panel
        TableLayout tableLayout = new TableLayout(2);
        tableLayout.setTableAnchor(TableLayout.Anchor.NORTHWEST);
        tableLayout.setTablePadding(new Insets(4, 10, 0, 0));
        tableLayout.setTableFill(TableLayout.Fill.BOTH);
        tableLayout.setColumnWeightX(1, 2.0);


        JPanel comboPanel = new JPanel(tableLayout);

        ArrayList<String> timeOptions = new ArrayList<>();
        timeOptions.add(S2CacheUtils.SENTINEL_2_CACHE_OPTION_DAY);
        timeOptions.add(S2CacheUtils.SENTINEL_2_CACHE_OPTION_WEEK);
        timeOptions.add(S2CacheUtils.SENTINEL_2_CACHE_OPTION_MONTH);
        timeOptions.add(S2CacheUtils.SENTINEL_2_CACHE_OPTION_NEVER_DELETE);
        timeOptions.add(S2CacheUtils.SENTINEL_2_CACHE_OPTION_EACH_START_UP);

        box = new JComboBox<>();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(timeOptions.toArray(new String[timeOptions.size()]));
        box.setModel(model);
        informationLabel = new JLabel(" ");
        Dimension dimLabel = new Dimension(180, 20);
        informationLabel.setPreferredSize(dimLabel);
        informationLabel.setForeground(Color.lightGray);
        comboPanel.add(new JLabel(NbBundle.getMessage(S2CacheOptionsPanel.class,
                                                      "S2TBXCacheOptionsPanel.cachePolicyComboBox.text")));
        comboPanel.add(box);
        comboPanel.add(tableLayout.createHorizontalSpacer());
        comboPanel.add(informationLabel);
        comboPanel.add(tableLayout.createVerticalSpacer());

        //Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cleanUpButton = new JButton(NbBundle.getMessage(S2CacheOptionsPanel.class,
                                                                "S2TBXCacheOptionsPanel.deleteButton.text"));
        cleanUpButton.addActionListener(e -> {
            S2CacheUtils.deleteCache();
        });
        buttonPanel.add(cleanUpButton);


        //Bottom panel
        JPanel bottomPanel = new JPanel(tableLayout);
        bottomPanel.add(tableLayout.createVerticalSpacer());

        this.add(comboPanel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);

    }

    void load() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();
        box.setSelectedItem(preferences.get(S2CacheUtils.SENTINEL_2_CACHE_MAX_TIME,
                                            S2CacheUtils.SENTINEL_2_CACHE_OPTION_WEEK));
        updateLayer();
    }

    public void updateLayer() {
        if (box.getSelectedItem().equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_DAY)) {
            informationLabel.setText(NbBundle.getMessage(S2CacheOptionsPanel.class,
                                                         "S2TBXCacheOptionsPanel.infoLabelDay.text"));
            return;
        }
        if (box.getSelectedItem().equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_WEEK)) {
            informationLabel.setText(NbBundle.getMessage(S2CacheOptionsPanel.class,
                                                         "S2TBXCacheOptionsPanel.infoLabelWeek.text"));
            return;
        }
        if (box.getSelectedItem().equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_MONTH)) {
            informationLabel.setText(NbBundle.getMessage(S2CacheOptionsPanel.class,
                                                         "S2TBXCacheOptionsPanel.infoLabelMonth.text"));
            return;
        }
        if (box.getSelectedItem().equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_EACH_START_UP)) {
            informationLabel.setText(NbBundle.getMessage(S2CacheOptionsPanel.class,
                                                         "S2TBXCacheOptionsPanel.infoLabelStartUp.text"));
            return;
        }
        if (box.getSelectedItem().equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_NEVER_DELETE)) {
            informationLabel.setText(NbBundle.getMessage(S2CacheOptionsPanel.class,
                                                         "S2TBXCacheOptionsPanel.infoLabelNever.text"));
            return;
        }
        informationLabel.setText(" ");
    }

    void store() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();
        preferences.put(S2CacheUtils.SENTINEL_2_CACHE_MAX_TIME,
                        box.getSelectedItem().toString());
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
