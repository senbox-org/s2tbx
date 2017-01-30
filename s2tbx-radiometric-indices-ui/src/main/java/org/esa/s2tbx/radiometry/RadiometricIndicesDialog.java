/*
 *
 *  * Copyright (C) 2016 CS ROMANIA
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
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.radiometry;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import org.esa.s2tbx.radiometry.annotations.BandParameter;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.rcp.util.Dialogs;
import org.esa.snap.tango.TangoIcons;
import org.esa.snap.ui.AppContext;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Radiometric index dialog.
 *
 * @author Cosmin Cara.
 */
public class RadiometricIndicesDialog extends DefaultSingleTargetProductDialog {

    private static final String PROPERTY_UPSAMPLE = "upsamplingMethod";
    private static final String PROPERTY_DOWNSAMPLE = "downsamplingMethod";
    private static final String PROPERTY_RESAMPLE = "resampleType";
    private static final String resampleMessage = "Bands will be resampled at the %s resolution";

    private List<Field> bandFields;
    private JLabel messageLabel;
    private JPanel messagePanel;
    private volatile boolean productChanged;

    public RadiometricIndicesDialog(String operatorName, AppContext appContext, String title, String helpID) {
        this(operatorName, appContext, title, helpID, true);
    }

    public RadiometricIndicesDialog(String operatorName, AppContext appContext, String title, String helpID, boolean targetProductSelectorDisplay) {
        super(operatorName, appContext, title, helpID, targetProductSelectorDisplay);

        OperatorDescriptor descriptor = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(operatorName).getOperatorDescriptor();
        bandFields = Arrays.stream(descriptor.getOperatorClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(BandParameter.class) != null)
                .collect(Collectors.toList());
        DefaultIOParametersPanel ioParametersPanel = getDefaultIOParametersPanel();

        List<SourceProductSelector> sourceProductSelectorList = ioParametersPanel.getSourceProductSelectorList();
        if (!sourceProductSelectorList.isEmpty()) {
            SelectionChangeListener listener = new SelectionChangeListener() {
                public void selectionChanged(SelectionChangeEvent event) {
                    processSelectedProduct();
                }

                public void selectionContextChanged(SelectionChangeEvent event) {
                }
            };
            sourceProductSelectorList.get(0).addSelectionChangeListener(listener);
        }

        BindingContext bindingContext = getBindingContext();
        PropertySet propertySet = bindingContext.getPropertySet();

        this.bandFields.stream()
                .map(f -> new AbstractMap.SimpleEntry<>(propertySet.getProperty(f.getName()),
                        f.getAnnotation(BandParameter.class)))
                .forEach(entry -> {
                    Property property = entry.getKey();
                    property.addPropertyChangeListener(evt -> {
                        if (!productChanged) checkResampling(getSelectedProduct());
                    });
                    BandParameter annotation = entry.getValue();
                    if (annotation != null) {
                        final PropertyDescriptor propertyDescriptor = property.getDescriptor();
                        propertyDescriptor.setDescription(propertyDescriptor.getDescription()
                                + String.format(" Expected wavelength interval: [%dnm, %dnm]",
                                (int) annotation.minWavelength(), (int) annotation.maxWavelength()));
                    }
                });
        propertySet.getProperty(PROPERTY_RESAMPLE).addPropertyChangeListener(evt -> checkResampling(getSelectedProduct()));
        messagePanel = new JPanel();
        messagePanel.add(new JLabel(TangoIcons.status_dialog_information(TangoIcons.Res.R16)));
        messageLabel = new JLabel(resampleMessage);
        messageLabel.setForeground(Color.BLUE);
        messagePanel.add(messageLabel);
    }

    @Override
    public int show() {
        int result = super.show();
        insertMessageLabel();
        processSelectedProduct();
        return result;
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    protected void onApply() {
        if (isResampleNeeded(getSelectedProduct()) &&
                BaseIndexOp.RESAMPLE_NONE.equals(getBindingContext().getPropertySet().getValue(PROPERTY_RESAMPLE))) {
            Dialogs.showWarning("Please select how resampling should be performed");
            return;
        }
        super.onApply();
    }

    private void insertMessageLabel() {
        Container parent = getDefaultIOParametersPanel().getParent();
        JScrollPane scrollPane = (JScrollPane) parent.getComponent(1);
        JViewport viewport = (JViewport) scrollPane.getComponent(0);
        JPanel initial = (JPanel) viewport.getComponent(0);
        Dimension preferredSize = initial.getPreferredSize();
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(initial, BorderLayout.NORTH);
        wrapper.add(messagePanel, BorderLayout.SOUTH);
        messagePanel.setVisible(false);
        wrapper.setPreferredSize(preferredSize);
        viewport.remove(initial);
        viewport.add(wrapper);
        final Dimension windowSize = this.getJDialog().getPreferredSize();
        this.getJDialog().setMinimumSize(new Dimension(windowSize.width, windowSize.height + 30));
    }

    /**
     * Returns the selected product.
     *
     * @return the selected product
     */
    private Product getSelectedProduct() {
        DefaultIOParametersPanel ioParametersPanel = getDefaultIOParametersPanel();
        List<SourceProductSelector> sourceProductSelectorList = ioParametersPanel.getSourceProductSelectorList();
        return sourceProductSelectorList.get(0).getSelectedProduct();
    }

    private boolean isResampleNeeded(Product product) {
        boolean needsResampling = false;
        if (product != null) {
            int sceneWidth = 0;
            BindingContext bindingContext = getBindingContext();
            PropertySet propertySet = bindingContext.getPropertySet();

            Set<String> setBandNames = this.bandFields.stream()
                                                      .map(f -> propertySet.getProperty(f.getName()))
                                                      .filter(p -> !StringUtils.isNullOrEmpty(p.getValueAsText()))
                                                      .map(Property::getValueAsText)
                                                      .collect(Collectors.toSet());
            if (setBandNames.size() > 0) {
                BitSet bitSet = new BitSet(setBandNames.size());
                int idx = 0;
                for (String bandName : setBandNames) {
                    Band band = product.getBand(bandName);
                    bitSet.set(idx++, sceneWidth != 0 && sceneWidth != band.getRasterWidth());
                    if (band != null) {
                        sceneWidth = band.getRasterWidth();
                    }
                }
                needsResampling = bitSet.nextSetBit(0) != -1;
            }
        }
        return needsResampling;
    }

    private void checkResampling(Product product) {
        BindingContext bindingContext = getBindingContext();
        PropertySet propertySet = bindingContext.getPropertySet();
        boolean needsResampling = isResampleNeeded(product);
        if (!needsResampling) {
            propertySet.setValue(PROPERTY_RESAMPLE, BaseIndexOp.RESAMPLE_NONE);
        }
        setMessage(propertySet.getValue(PROPERTY_RESAMPLE));
        setEnabled(PROPERTY_RESAMPLE, needsResampling);
        messagePanel.setVisible(needsResampling);
    }

    private void setMessage(String method) {
        switch (method) {
            case BaseIndexOp.RESAMPLE_LOWEST:
                messageLabel.setText(String.format(resampleMessage, "lowest"));
                setEnabled(PROPERTY_DOWNSAMPLE, true);
                setEnabled(PROPERTY_UPSAMPLE, false);
                messageLabel.setForeground(Color.BLUE);
                break;
            case BaseIndexOp.RESAMPLE_HIGHEST:
                messageLabel.setText(String.format(resampleMessage, "highest"));
                setEnabled(PROPERTY_DOWNSAMPLE, false);
                setEnabled(PROPERTY_UPSAMPLE, true);
                messageLabel.setForeground(Color.BLUE);
                break;
            case BaseIndexOp.RESAMPLE_NONE:
                messageLabel.setText("Product needs to be resampled first");
                setEnabled(PROPERTY_DOWNSAMPLE, false);
                setEnabled(PROPERTY_UPSAMPLE, false);
                messageLabel.setForeground(Color.RED);
                break;
        }
    }

    /**
     * Sets the incidence angle and the quantification value according to the selected product.
     */
    private void processSelectedProduct() {
        Product selectedProduct = getSelectedProduct();
        if (selectedProduct != null) {
            productChanged = true;
            try {
                BindingContext bindingContext = getBindingContext();
                PropertySet propertySet = bindingContext.getPropertySet();
                propertySet.setDefaultValues();

                for (Field field : this.bandFields) {
                    BandParameter annotation = field.getAnnotation(BandParameter.class);
                    float min = annotation.minWavelength();
                    float max = annotation.maxWavelength();
                    if (min != 0.0f && max != 0.0f) {
                        String bandName = BaseIndexOp.findBand(min, max, selectedProduct);
                        propertySet.setValue(field.getName(), bandName);
                    }
                }
                checkResampling(selectedProduct);
            } finally {
                productChanged = false;
            }
        }
    }

    private void setEnabled(String propertyName, boolean value) {
        if (this.getJDialog().isVisible()) {
            BindingContext bindingContext = getBindingContext();
            bindingContext.setComponentsEnabled(propertyName, value);
        }
    }
}