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

    private List<Field> bandFields;
    private JLabel messageLabel;
    private JPanel messagePanel;
    private Property upsampleProperty;
    private Property downsampleProperty;
    private static final String resampleMessage = "Bands will be resampled at the %s resolution";

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
        this.upsampleProperty = propertySet.getProperty("upsamplingMethod");
        this.downsampleProperty = propertySet.getProperty("downsamplingMethod");

        this.bandFields.stream()
                .map(f -> new AbstractMap.SimpleEntry<>(propertySet.getProperty(f.getName()),
                        f.getAnnotation(BandParameter.class)))
                .forEach(entry -> {
                    Property property = entry.getKey();
                    property.addPropertyChangeListener(evt -> checkResampling(getSelectedProduct()));
                    BandParameter annotation = entry.getValue();
                    if (annotation != null) {
                        final PropertyDescriptor propertyDescriptor = property.getDescriptor();
                        propertyDescriptor.setDisplayName(String.format("%s [%dnm,%dnm]",
                                propertyDescriptor.getDisplayName(),
                                (int) annotation.minWavelength(), (int) annotation.maxWavelength()));
                    }
                });
        propertySet.getProperty("resampleType").addPropertyChangeListener(evt -> checkResampling(getSelectedProduct()));
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

    private void checkResampling(Product product) {
        int sceneWidth = 0;
        boolean needsResampling = false;
        BindingContext bindingContext = getBindingContext();
        PropertySet propertySet = bindingContext.getPropertySet();

        Set<Property> propNames = this.bandFields.stream().map(f -> propertySet.getProperty(f.getName())).collect(Collectors.toSet());
        Set<String> setBandNames = propNames.stream().filter(p -> !StringUtils.isNullOrEmpty(p.getValueAsText()))
                .map(Property::getValueAsText).collect(Collectors.toSet());
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
        if (!needsResampling) {
            propertySet.setValue("resampleType", BaseIndexOp.RESAMPLE_NONE);
        }
        setMessage(propertySet.getValue("resampleType"));
        messagePanel.setVisible(needsResampling);
    }

    private void setMessage(String method) {
        switch (method) {
            case BaseIndexOp.RESAMPLE_LOWEST:
                messageLabel.setText(String.format(resampleMessage, "lowest"));
                setEnabled(this.downsampleProperty, true);
                setEnabled(this.upsampleProperty, false);
                messageLabel.setForeground(Color.BLUE);
                break;
            case BaseIndexOp.RESAMPLE_HIGHEST:
                messageLabel.setText(String.format(resampleMessage, "highest"));
                setEnabled(this.downsampleProperty, false);
                setEnabled(this.upsampleProperty, true);
                messageLabel.setForeground(Color.BLUE);
                break;
            case BaseIndexOp.RESAMPLE_NONE:
                messageLabel.setText("Product needs to be resampled first");
                setEnabled(this.downsampleProperty, false);
                setEnabled(this.upsampleProperty, false);
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
        }
    }

    private void setEnabled(Property property, boolean value) {
        if (this.getJDialog().isVisible()) {
            BindingContext bindingContext = getBindingContext();
            bindingContext.setComponentsEnabled(property.getName(), value);
        }
    }
}