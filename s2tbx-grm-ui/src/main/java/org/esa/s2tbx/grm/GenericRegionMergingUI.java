package org.esa.s2tbx.grm;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyPane;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.graphbuilder.gpf.ui.BaseOperatorUI;
import org.esa.snap.graphbuilder.gpf.ui.OperatorUIUtils;
import org.esa.snap.graphbuilder.gpf.ui.UIValidation;
import org.esa.snap.ui.AppContext;

import javax.swing.*;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Graph Builder - compatible UI for Generic Region Merging.
 *
 * @author Cosmin Cara
 */
public class GenericRegionMergingUI extends BaseOperatorUI {

    private static final String MERGING_COST_CRITERION_PROPERTY_NAME = "mergingCostCriterion";
    private static final String REGION_MERGING_CRITERION_PROPERTY_NAME = "regionMergingCriterion";
    private static final String TOTAL_ITERATIONS_FOR_SECOND_SEGMENTATION_PROPERTY_NAME = "totalIterationsForSecondSegmentation";
    private static final String THRESHOLD_PROPERTY_NAME = "threshold";
    private static final String SPECTRAL_WEIGHT_PROPERTY_NAME = "spectralWeight";
    private static final String SHAPE_WEIGHT_PROPERTY_NAME = "shapeWeight";
    private static final String SOURCE_BAND_NAMES_PROPERTY_NAME = "sourceBandNames";

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
    }

    @Override
    public UIValidation validateParameters() {
        PropertySet propertySet = this.bindingContext.getPropertySet();
        String mergingCostCriterion=propertySet.getProperty(MERGING_COST_CRITERION_PROPERTY_NAME).getValue();
        if (mergingCostCriterion == null || mergingCostCriterion.isEmpty()) {
            return new UIValidation(UIValidation.State.WARNING, "Please specify the merging cost criterion.");
        }
        String regionMergingCriterion=propertySet.getProperty(REGION_MERGING_CRITERION_PROPERTY_NAME).getValue();
        if (regionMergingCriterion == null || regionMergingCriterion.isEmpty()) {
            return new UIValidation(UIValidation.State.WARNING, "Please specify the region merging criterion.");
        }
        Integer totalIterationsForSecondSegmentation=propertySet.getProperty(TOTAL_ITERATIONS_FOR_SECOND_SEGMENTATION_PROPERTY_NAME).getValue();
        if (totalIterationsForSecondSegmentation == null) {
            return new UIValidation(UIValidation.State.WARNING, "Please specify the total iterations for second segmentation.");
        }
        Float threshold=propertySet.getProperty(THRESHOLD_PROPERTY_NAME).getValue();
        if (threshold == null) {
            return new UIValidation(UIValidation.State.WARNING, "Please specify the threshold.");
        }
        Float spectralWeight=propertySet.getProperty(SPECTRAL_WEIGHT_PROPERTY_NAME).getValue();
        if (spectralWeight == null) {
            return new UIValidation(UIValidation.State.WARNING, "Please specify the spectral weight.");
        }
        Float shapeWeight=propertySet.getProperty(SHAPE_WEIGHT_PROPERTY_NAME).getValue();
        if (shapeWeight == null) {
            return new UIValidation(UIValidation.State.WARNING, "Please specify the shape weight.");
        }
        String[] referenceBands = propertySet.getProperty(SOURCE_BAND_NAMES_PROPERTY_NAME).getValue();
        if (referenceBands == null || referenceBands.length < 1) {
            return new UIValidation(UIValidation.State.WARNING, "Please select at least one band.");
        }
        return new UIValidation(UIValidation.State.OK, "");
    }

    @Override
    public void updateParameters() {
        updateSourceBands();

        JList bandList = (JList) this.bindingContext.getBinding(SOURCE_BAND_NAMES_PROPERTY_NAME).getComponents()[0];
        OperatorUIUtils.updateParamList(bandList, this.paramMap, SOURCE_BAND_NAMES_PROPERTY_NAME);
        PropertySet propertySet = this.bindingContext.getPropertySet();
        this.paramMap.put(MERGING_COST_CRITERION_PROPERTY_NAME, propertySet.getValue(MERGING_COST_CRITERION_PROPERTY_NAME));
        this.paramMap.put(REGION_MERGING_CRITERION_PROPERTY_NAME, propertySet.getValue(REGION_MERGING_CRITERION_PROPERTY_NAME));
        this.paramMap.put(TOTAL_ITERATIONS_FOR_SECOND_SEGMENTATION_PROPERTY_NAME, propertySet.getValue(TOTAL_ITERATIONS_FOR_SECOND_SEGMENTATION_PROPERTY_NAME));
        this.paramMap.put(THRESHOLD_PROPERTY_NAME, propertySet.getValue(THRESHOLD_PROPERTY_NAME));
        this.paramMap.put(SPECTRAL_WEIGHT_PROPERTY_NAME, propertySet.getValue(SPECTRAL_WEIGHT_PROPERTY_NAME));
        this.paramMap.put(SHAPE_WEIGHT_PROPERTY_NAME, propertySet.getValue(SHAPE_WEIGHT_PROPERTY_NAME));
    }

    private Product getCurrentProduct() {
        return this.sourceProducts != null && this.sourceProducts.length > 0 ? this.sourceProducts[0] : null;
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
                Logger.getLogger(GenericRegionMergingUI.class.getName()).warning(e.getMessage());
            }
        }
    }
}
