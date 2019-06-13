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
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyPane;
import org.esa.s2tbx.radiometry.annotations.BandParameter;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.RasterDataNode;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
import org.esa.snap.core.gpf.internal.RasterDataNodeValues;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.tango.TangoIcons;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Core UI class for radiometric indices.
 *
 * @author Cosmin Cara
 */
class RadiometricIndicesPanel {
    private static final String PROPERTY_UPSAMPLE = "upsamplingMethod";
    private static final String PROPERTY_DOWNSAMPLE = "downsamplingMethod";
    private static final String PROPERTY_RESAMPLE = "resampleType";
    private static final String resampleMessage = "Bands will be resampled at the %s resolution";

    private OperatorDescriptor operatorDescriptor;
    private PropertySet propertySet;

    private List<Field> bandFields;
    private JLabel messageLabel;
    private JPanel messagePanel;
    private BindingContext bindingContext;
    private Product currentProduct;
    private JScrollPane operatorPanel;
    private Callable<Product> sourceProductAccessor;

    RadiometricIndicesPanel(String operatorName, PropertySet propertySet, BindingContext bindingContext, Callable<Product> productAccessor) {
        if (productAccessor == null) {
            throw new IllegalArgumentException("The accessor for fetching source products must not be null");
        }
        OperatorSpi operatorSpi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(operatorName);
        if (operatorSpi == null) {
            throw new IllegalArgumentException("No SPI found for operator name '" + operatorName + "'");
        }
        this.operatorDescriptor = operatorSpi.getOperatorDescriptor();
        this.propertySet = propertySet;
        this.bindingContext = bindingContext == null ? new BindingContext(propertySet) : bindingContext;
        this.sourceProductAccessor = productAccessor;
        PropertyPane parametersPane = new PropertyPane(this.bindingContext);
        this.operatorPanel = new JScrollPane(parametersPane.createPanel());
    }

    JComponent createPanel() {
        this.bandFields = Arrays.stream(operatorDescriptor.getOperatorClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(BandParameter.class) != null)
                .collect(Collectors.toList());
        this.bandFields.stream()
                .map(f -> new AbstractMap.SimpleEntry<>(this.propertySet.getProperty(f.getName()),
                        f.getAnnotation(BandParameter.class)))
                .forEach(entry -> {
                    Property property = entry.getKey();
                    property.addPropertyChangeListener(evt -> checkResampling(this.currentProduct));
                    BandParameter annotation = entry.getValue();
                    if (annotation != null) {
                        final PropertyDescriptor propertyDescriptor = property.getDescriptor();
                        propertyDescriptor.setDescription(propertyDescriptor.getDescription()
                                + String.format(" Expected wavelength interval: [%dnm, %dnm]",
                                (int) annotation.minWavelength(), (int) annotation.maxWavelength()));
                    }
                });
        this.propertySet.getProperty(PROPERTY_RESAMPLE).addPropertyChangeListener(evt -> checkResampling(getSourceProduct()));

        insertMessageLabel(this.operatorPanel);

        return this.operatorPanel;
    }

    boolean validateParameters() {
        boolean ret = true;
        if (isResampleNeeded(getSourceProduct())) {
            if (!BaseIndexOp.RESAMPLE_NONE.equals(bindingContext.getPropertySet().getValue(PROPERTY_RESAMPLE))) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Sets the incidence angle and the quantification value according to the selected product.
     */
    void reactOnChange() {
        if (isInputProductChanged()) {
            if (this.currentProduct != null) {
                PropertySet propertySet = this.bindingContext.getPropertySet();
                for (Field field : this.bandFields) {
                    Property property = propertySet.getProperty(field.getName());
                    updateValueSet(property, this.currentProduct);
                    BandParameter annotation = field.getAnnotation(BandParameter.class);
                    float min = annotation.minWavelength();
                    float max = annotation.maxWavelength();
                    if (property.getValue() == null  || (property.getValue() != null && !this.currentProduct.containsBand(property.getValue()))) {
                        if (min != 0.0f && max != 0.0f) {
                            String bandName = BaseIndexOp.findBand(min, max, this.currentProduct);
                            try {
                                property.setValue(bandName);
                            } catch (ValidationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                checkResampling(this.currentProduct);
            }
        }
    }

    BindingContext getBindingContext() { return this.bindingContext; }

    private boolean isInputProductChanged() {
        Product sourceProduct = getSourceProduct();
        if (sourceProduct != this.currentProduct) {
            this.currentProduct = sourceProduct;
            return true;
        } else {
            return false;
        }
    }

    private Product getSourceProduct() {
        try {
            return this.sourceProductAccessor.call();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isResampleNeeded(Product product) {
        boolean needsResampling = false;
        if (product != null) {
            int sceneWidth = 0;
            PropertySet propertySet = this.bindingContext.getPropertySet();

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
                    sceneWidth = band.getRasterWidth();
                }
                needsResampling = bitSet.nextSetBit(0) != -1;
            }
        }

        return needsResampling;
    }

    private void checkResampling(Product product) {
        PropertySet propertySet = this.bindingContext.getPropertySet();
        boolean needsResampling = isResampleNeeded(product);
        if (!needsResampling) {
            propertySet.setValue(PROPERTY_RESAMPLE, BaseIndexOp.RESAMPLE_NONE);
        }
        setMessage(propertySet.getValue(PROPERTY_RESAMPLE));
        setEnabled(PROPERTY_RESAMPLE, needsResampling);
        this.messagePanel.setVisible(needsResampling);
    }

    private void insertMessageLabel(JScrollPane parent) {
        if (parent != null) {
            this.messagePanel = new JPanel();
            this.messagePanel.add(new JLabel(TangoIcons.status_dialog_information(TangoIcons.Res.R16)));
            this.messageLabel = new JLabel(resampleMessage);
            this.messageLabel.setForeground(Color.BLUE);
            this.messagePanel.add(messageLabel);
            this.messagePanel.setVisible(false);
            JViewport viewport = (JViewport) parent.getComponent(0);
            JPanel initial = (JPanel) viewport.getComponent(0);
            Dimension preferredSize = initial.getPreferredSize();
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.add(initial, BorderLayout.NORTH);
            wrapper.add(this.messagePanel, BorderLayout.SOUTH);
            this.messagePanel.setVisible(false);
            wrapper.setPreferredSize(new Dimension(preferredSize.width, preferredSize.height + 30));
            viewport.remove(initial);
            viewport.add(wrapper);
        }
    }

    private void setMessage(String method) {
        if (this.messageLabel == null) {
            insertMessageLabel(this.operatorPanel);
        }
        switch (method) {
            case BaseIndexOp.RESAMPLE_LOWEST:
                this.messageLabel.setText(String.format(resampleMessage, "lowest"));
                setEnabled(PROPERTY_DOWNSAMPLE, true);
                setEnabled(PROPERTY_UPSAMPLE, false);
                this.messageLabel.setForeground(Color.BLUE);
                break;
            case BaseIndexOp.RESAMPLE_HIGHEST:
                this.messageLabel.setText(String.format(resampleMessage, "highest"));
                setEnabled(PROPERTY_DOWNSAMPLE, false);
                setEnabled(PROPERTY_UPSAMPLE, true);
                this.messageLabel.setForeground(Color.BLUE);
                break;
            case BaseIndexOp.RESAMPLE_NONE:
                this.messageLabel.setText("Product needs to be resampled first");
                setEnabled(PROPERTY_DOWNSAMPLE, false);
                setEnabled(PROPERTY_UPSAMPLE, false);
                this.messageLabel.setForeground(Color.RED);
                break;
        }
    }

    private void updateValueSet(Property property, Product product) {
        String[] values = new String[0];
        PropertyDescriptor propertyDescriptor = property.getDescriptor();
        if (product != null) {
            Object object = propertyDescriptor.getAttribute(RasterDataNodeValues.ATTRIBUTE_NAME);
            if (object != null) {
                @SuppressWarnings("unchecked")
                Class<? extends RasterDataNode> rasterDataNodeType = (Class<? extends RasterDataNode>) object;
                boolean includeEmptyValue = !propertyDescriptor.isNotNull() && !propertyDescriptor.isNotEmpty() &&
                        !propertyDescriptor.getType().isArray();
                values = RasterDataNodeValues.getNames(product, rasterDataNodeType, includeEmptyValue);
            }
        }
        propertyDescriptor.setValueSet(new ValueSet(values));
    }

    private void setEnabled(String propertyName, boolean value) {
        this.bindingContext.setComponentsEnabled(propertyName, value);
    }
}
