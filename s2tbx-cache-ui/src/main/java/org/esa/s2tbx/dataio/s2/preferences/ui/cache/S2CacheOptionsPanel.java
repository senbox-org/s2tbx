/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esa.s2tbx.dataio.s2.preferences.ui.cache;

import com.bc.ceres.swing.TableLayout;

import org.esa.s2tbx.dataio.cache.S2CacheSizeChecking;
import org.esa.s2tbx.dataio.cache.S2CacheUtils;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.runtime.Config;
import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

final class S2CacheOptionsPanel extends javax.swing.JPanel {


    private JComboBox<String> box;
    private JCheckBox activeLImitedSizeCache;
    private JTextField limitedSizeCacheField;
    private JLabel informationLabel;

    S2CacheOptionsPanel(final S2CacheOptionsPanelController controller) {
        initComponents();

        box.addItemListener(e -> controller.changed());
        activeLImitedSizeCache.addItemListener(e -> controller.changed());
        limitedSizeCacheField.addActionListener(e -> controller.changed());
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
        limitedSizeCacheField = new DoubleTextField(String.valueOf(S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE_DEFAULT));
        activeLImitedSizeCache = new JCheckBox("Maximum size in cache (Go)"); 
        activeLImitedSizeCache.setSelected(false);
        activeLImitedSizeCache.addChangeListener(new ChangeListener() {

                public void stateChanged(final ChangeEvent e) {
                    if(activeLImitedSizeCache.isSelected()){
                        limitedSizeCacheField.setEditable(true);
                    }else{
                        limitedSizeCacheField.setEditable(false);
                    }
                }
            });
        activeLImitedSizeCache.setToolTipText("<html>This option launch a process to check the cache size every minute."
            +"In case of oversizing, the cache size will be return at 75% of the specified maximum size."
            +"<br>Only oldest files will be deleted</html>");
        
        comboPanel.add(activeLImitedSizeCache);
        comboPanel.add(limitedSizeCacheField);
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
        limitedSizeCacheField.setEditable(preferences.getBoolean(S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE_OPTION,
                                            S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE_OPTION_DEFAULT));
        limitedSizeCacheField.setText(String.valueOf(preferences.getDouble(S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE,
                                            S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE_DEFAULT)));
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
        preferences.putBoolean(S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE_OPTION,
                        activeLImitedSizeCache.isSelected());
        preferences.putDouble(S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE,
                        Double.valueOf(limitedSizeCacheField.getText()));
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            SnapApp.getDefault().getLogger().severe(e.getMessage());
        }
        S2CacheSizeChecking sizeCacheCheckingLoop = S2CacheSizeChecking.getInstance();
        sizeCacheCheckingLoop.setParameters(preferences.getBoolean(S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE_OPTION,
                        S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE_OPTION_DEFAULT), preferences.getDouble(S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE,
                        S2CacheUtils.SENTINEL_2_CACHE_MAX_SIZE_DEFAULT));
    }

    boolean valid() {
        // Check whether form is consistent and complete
        return true;
    }


    private class DoubleTextField extends JTextField {

        private final static String disallowedChars = "`§~!@#$%^&*()_+=\\|\"':;?/><,- ";

        public DoubleTextField(String defaultValue) {
            super(defaultValue);
        }

        @Override
        protected void processKeyEvent(KeyEvent e) {
            if (!Character.isLetter(e.getKeyChar()) && disallowedChars.indexOf(e.getKeyChar()) == -1) {
                super.processKeyEvent(e);
            }
        }
    }

}
