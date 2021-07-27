package org.esa.s2tbx.coregistration;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyPane;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.graphbuilder.gpf.ui.BaseOperatorUI;
import org.esa.snap.graphbuilder.gpf.ui.UIValidation;
import org.esa.snap.ui.AppContext;

import javax.swing.*;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Graph Builder - compatible UI for Coregistration operator.
 *
 * @author Adrian Draghici
 */
public class CoregistrationUI extends BaseOperatorUI {

    private static final String MASTER_SOURCE_BAND_PROPERTY_NAME = "masterSourceBand";
    private static final String SLAVE_SOURCE_BAND_PROPERTY_NAME = "slaveSourceBand";
    private static final String LEVELS_PROPERTY_NAME = "levels";
    private static final String RANK_PROPERTY_NAME = "rank";
    private static final String ITERATIONS_PROPERTY_NAME = "iterations";
    private static final String RADIUS_PROPERTY_NAME1 = "radius";

    private BindingContext bindingContext;

    @Override
    public JComponent CreateOpTab(final String operatorName,
                                  final Map<String, Object> parameterMap, final AppContext appContext) {

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
        Product masterProduct = getMasterProduct();
        Product slaveProduct = getSlaveProduct();
        if (masterProduct != null && slaveProduct != null) {

            updateMasterSourceBands();
            updateSlaveSourceBands();

            Integer levels = (Integer) this.paramMap.get(LEVELS_PROPERTY_NAME);
            if (levels != null) {
                propertySet.setValue(LEVELS_PROPERTY_NAME, levels);
            }
            Integer rank = (Integer) this.paramMap.get(RANK_PROPERTY_NAME);
            if (rank != null) {
                propertySet.setValue(RANK_PROPERTY_NAME, rank);
            }
            Integer iterations = (Integer) this.paramMap.get(ITERATIONS_PROPERTY_NAME);
            if (iterations != null) {
                propertySet.setValue(ITERATIONS_PROPERTY_NAME, iterations);
            }
            String radius = (String) this.paramMap.get(RADIUS_PROPERTY_NAME1);
            if (radius != null) {
                propertySet.setValue(RADIUS_PROPERTY_NAME1, radius);
            }
        }
    }

    @Override
    public UIValidation validateParameters() {
        return new UIValidation(UIValidation.State.OK, "");
    }

    @Override
    public void updateParameters() {
        updateMasterSourceBands();
        updateSlaveSourceBands();

        PropertySet propertySet = this.bindingContext.getPropertySet();

        this.paramMap.put(MASTER_SOURCE_BAND_PROPERTY_NAME, propertySet.getValue(MASTER_SOURCE_BAND_PROPERTY_NAME));

        this.paramMap.put(SLAVE_SOURCE_BAND_PROPERTY_NAME, propertySet.getValue(SLAVE_SOURCE_BAND_PROPERTY_NAME));

        this.paramMap.put(RANK_PROPERTY_NAME, propertySet.getValue(RANK_PROPERTY_NAME));

        this.paramMap.put(LEVELS_PROPERTY_NAME, propertySet.getValue(LEVELS_PROPERTY_NAME));

        this.paramMap.put(ITERATIONS_PROPERTY_NAME, propertySet.getValue(ITERATIONS_PROPERTY_NAME));

        this.paramMap.put(RADIUS_PROPERTY_NAME1, propertySet.getValue(RADIUS_PROPERTY_NAME1));

    }

    private void updateMasterSourceBands() {
        String currentMasterSourceBandValue = this.bindingContext.getPropertySet().getValue(MASTER_SOURCE_BAND_PROPERTY_NAME);
        updateSourceBands(getMasterProduct(), MASTER_SOURCE_BAND_PROPERTY_NAME, currentMasterSourceBandValue);
    }

    private void updateSlaveSourceBands() {
        String currentSlaveSourceBandValue = this.bindingContext.getPropertySet().getValue(SLAVE_SOURCE_BAND_PROPERTY_NAME);
        updateSourceBands(getSlaveProduct(), SLAVE_SOURCE_BAND_PROPERTY_NAME, currentSlaveSourceBandValue);
    }

    private void updateSourceBands(Product product, String sourceBandPropertyName, String currentSourceBandValue) {
        if (this.propertySet == null) return;

        String[] bandNames = getProductBandNames(product);

        if (bandNames.length > 0) {
            Property sourceBandProperty = this.propertySet.getProperty(sourceBandPropertyName);
            final PropertyDescriptor descriptor = sourceBandProperty.getDescriptor();
            final ValueSet valueSet = new ValueSet(bandNames);
            descriptor.setValueSet(valueSet);
            try {
                if (descriptor.getType().isArray()) {
                    if (sourceBandProperty.getValue() == null)
                        sourceBandProperty.setValue(bandNames);
                } else {
                    if (currentSourceBandValue != null) {
                        sourceBandProperty.setValue(currentSourceBandValue);
                    } else {
                        sourceBandProperty.setValue(bandNames[0]);
                    }
                }
            } catch (ValidationException e) {
                Logger.getLogger(CoregistrationUI.class.getName()).warning(e.getMessage());
            }
        }
    }

    private Product getMasterProduct() {
        return this.sourceProducts != null && this.sourceProducts.length == 2 ? this.sourceProducts[0] : null;
    }

    private Product getSlaveProduct() {
        return this.sourceProducts != null && this.sourceProducts.length == 2 ? this.sourceProducts[1] : null;
    }

    private String[] getProductBandNames(Product product) {
        if (product != null) {
            return product.getBandNames();
        } else {
            return new String[0];
        }
    }

}
