package org.esa.s2tbx.fcc;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyPane;
import org.esa.snap.graphbuilder.gpf.ui.BaseOperatorUI;
import org.esa.snap.graphbuilder.gpf.ui.OperatorUIUtils;
import org.esa.snap.graphbuilder.gpf.ui.UIValidation;
import org.esa.snap.ui.AppContext;

import javax.swing.*;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Graph Builder - compatible UI for Bands Extractor.
 *
 * @author Cosmin Cara
 */
public class BandsExtractorUI extends BaseOperatorUI {

    private static final String SOURCE_BAND_NAMES_PROPERTY_NAME = "sourceBandNames";
    private static final String SOURCE_MASK_NAMES_PROPERTY_NAME = "sourceMaskNames";

    private BindingContext bindingContext;

    @Override
    public JComponent CreateOpTab(String operatorName, Map<String, Object> parameterMap, AppContext appContext) {
        initializeOperatorUI(operatorName, parameterMap);
        this.bindingContext = this.bindingContext == null ? new BindingContext(this.propertySet) : this.bindingContext;
        this.propertySet.setDefaultValues();
        PropertyPane parametersPane = new PropertyPane(this.bindingContext);
        final JComponent panel = parametersPane.createPanel();
        initParameters();
        return new JScrollPane(panel);
    }

    @Override
    public void initParameters() {
        JList bandList = (JList) this.bindingContext.getBinding(SOURCE_BAND_NAMES_PROPERTY_NAME).getComponents()[0];
        OperatorUIUtils.initParamList(bandList, getBandNames(), (Object[]) this.paramMap.get(SOURCE_BAND_NAMES_PROPERTY_NAME));
        JList maskList = (JList) this.bindingContext.getBinding(SOURCE_MASK_NAMES_PROPERTY_NAME).getComponents()[0];
        OperatorUIUtils.initParamList(maskList, getGeometries(), (Object[]) this.paramMap.get(SOURCE_MASK_NAMES_PROPERTY_NAME));
    }

    @Override
    public UIValidation validateParameters() {
        PropertySet propertySet = this.bindingContext.getPropertySet();
        String[] referenceBands = propertySet.getProperty(SOURCE_BAND_NAMES_PROPERTY_NAME).getValue();
        if (referenceBands == null || referenceBands.length < 1) {
            return new UIValidation(UIValidation.State.WARNING, "Please select at least one band.");
        }
        return new UIValidation(UIValidation.State.OK, "");
    }

    @Override
    public void updateParameters() {
        updateSourceBands();
        updateSourceMasks();

        JList bandList = (JList) this.bindingContext.getBinding(SOURCE_BAND_NAMES_PROPERTY_NAME).getComponents()[0];
        OperatorUIUtils.updateParamList(bandList, this.paramMap, SOURCE_BAND_NAMES_PROPERTY_NAME);
        JList maskList = (JList) this.bindingContext.getBinding(SOURCE_MASK_NAMES_PROPERTY_NAME).getComponents()[0];
        OperatorUIUtils.updateParamList(maskList, this.paramMap, SOURCE_MASK_NAMES_PROPERTY_NAME);
    }

    private void updateSourceBands() {
        if (this.propertySet == null) return;

        final Property[] properties = this.propertySet.getProperties();
        for (Property p : properties) {
            final PropertyDescriptor descriptor = p.getDescriptor();
            final String name = descriptor.getName();
            final String[] bandNames = getBandNames();

            if (this.sourceProducts == null || name == null || !name.equals(SOURCE_BAND_NAMES_PROPERTY_NAME) || bandNames.length < 1)
                continue;

            final ValueSet valueSet = new ValueSet(bandNames);
            descriptor.setValueSet(valueSet);

            try {
                if (descriptor.getType().isArray()) {
                    if (p.getValue() == null)
                        p.setValue(bandNames);
                } else {
                    p.setValue(bandNames[0]);
                }
            } catch (ValidationException e) {
                Logger.getLogger(BandsExtractorUI.class.getName()).warning(e.getMessage());
            }
        }
    }

    private void updateSourceMasks() {
        if (this.propertySet == null) return;

        final Property[] properties = this.propertySet.getProperties();
        for (Property p : properties) {
            final PropertyDescriptor descriptor = p.getDescriptor();
            final String name = descriptor.getName();
            final String[] maskNames = getGeometries();

            if (this.sourceProducts == null || name == null || !name.equals(SOURCE_MASK_NAMES_PROPERTY_NAME) || maskNames.length < 1)
                continue;

            final ValueSet valueSet = new ValueSet(maskNames);
            descriptor.setValueSet(valueSet);

            try {
                if (descriptor.getType().isArray()) {
                    if (p.getValue() == null)
                        p.setValue(maskNames);
                } else {
                    p.setValue(maskNames[0]);
                }
            } catch (ValidationException e) {
                Logger.getLogger(BandsExtractorUI.class.getName()).warning(e.getMessage());
            }
        }
    }
}
