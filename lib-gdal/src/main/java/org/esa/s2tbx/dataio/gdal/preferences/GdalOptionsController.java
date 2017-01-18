/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */
package org.esa.s2tbx.dataio.gdal.preferences;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.internal.TextComponentAdapter;
import org.esa.snap.rcp.preferences.DefaultConfigController;
import org.esa.snap.rcp.preferences.Preference;
import org.esa.snap.rcp.util.Dialogs;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Options controller for Standalone Tool Adapter.
 *
 * @author Cosmin Cara
 */
@OptionsPanelController.SubRegistration(location = "GeneralPreferences",
        displayName = "#Options_DisplayName_GdalOptions",
        keywords = "#Options_Keywords_GdalOptions",
        keywordsCategory = "GDAL",
        id = "GDAL",
        position = 10)
@org.openide.util.NbBundle.Messages({
        "Options_DisplayName_GdalOptions=GDAL",
        "Options_Keywords_GdalOptions=gdal"
})
public class GdalOptionsController extends DefaultConfigController {
    public static final String PREFERENCE_KEY_GDAL_BIN_PATH = "gdal.path";
    public static final String DEFAULT_VALUE_GDAL_BIN_PATH = "";

    private BindingContext context;
    private Preferences preferences;

    @Override
    protected PropertySet createPropertySet() {
        return createPropertySet(new GdalOptionsBean());
    }

    @Override
    protected JPanel createPanel(BindingContext context) {
        this.context = context;
        this.preferences = NbPreferences.forModule(Dialogs.class);

        Property gdalPathProperty = context.getPropertySet().getProperty(PREFERENCE_KEY_GDAL_BIN_PATH);
        PropertyDescriptor propertyDescriptor = gdalPathProperty.getDescriptor();
        propertyDescriptor.setAttribute("directory", true);
        setPropertyValue(gdalPathProperty, DEFAULT_VALUE_GDAL_BIN_PATH);
        if (propertyDescriptor.getDefaultValue() == null) {
            propertyDescriptor.setDefaultValue(gdalPathProperty.getValue());
        }

        JLabel gdalLabel = new JLabel(propertyDescriptor.getDisplayName());
        JTextField textField = new JTextField();
        JButton button = new JButton("...");
        Insets margins = button.getMargin();
        button.setMargin(new Insets(margins.top, 5, margins.bottom, 5));

        TextComponentAdapter adapter = new TextComponentAdapter(textField);
        context.bind(propertyDescriptor.getName(), adapter);

        gdalLabel.setEnabled(false);
        textField.setEnabled(false);
        button.setEnabled(false);

        Box top = Box.createHorizontalBox();
        top.add(gdalLabel);
        top.add(Box.createHorizontalStrut(5));
        top.add(textField);
        top.add(Box.createHorizontalStrut(5));
        top.add(button);

        JPanel pageUI = new JPanel(new BorderLayout());
        pageUI.add(top, BorderLayout.NORTH);

        return pageUI;
    }

    @Override
    public void update() {
        Property property = this.context.getPropertySet().getProperty(PREFERENCE_KEY_GDAL_BIN_PATH);
        if (property != null) {
            this.preferences.put(PREFERENCE_KEY_GDAL_BIN_PATH, property.getValueAsText());
            property.getDescriptor().setDefaultValue(property.getValue());
            try {
                this.preferences.flush();
            } catch (BackingStoreException ignored) {
                // ignore exception
            }
        }
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("gdal");
    }

    private File getPropertyValue(String key, String defaultValue) {
        if (this.preferences == null) {
            this.preferences = NbPreferences.forModule(Dialogs.class);
        }
        return new File(this.preferences.get(key, defaultValue));
    }

    private void setPropertyValue(Property property, String defaultValue) {
        try {
            File value = getPropertyValue(property.getName(), defaultValue);
            property.setValue(value);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    static class GdalOptionsBean {
        @Preference(label = "GDAL binaries path", key = PREFERENCE_KEY_GDAL_BIN_PATH)
        File path = new File(DEFAULT_VALUE_GDAL_BIN_PATH);
    }
}
