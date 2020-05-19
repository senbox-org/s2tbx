package org.esa.s2tbx.reflectance2radiance;

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
import org.esa.snap.utils.StringHelper;

import javax.swing.*;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Graph Builder - compatible UI for radiometric indices.
 *
 * @author Cosmin Cara
 */
public class ReflectanceToRadianceUI extends BaseOperatorUI {

    private static final String SOURCE_BAND_NAMES_PROPERTY_NAME = "sourceBandNames";
    private static final String SOLAR_IRRADIANCE_PROPERTY_NAME = "solarIrradiance";
    private static final String INCIDENCE_ANGLE_PROPERTY_NAME = "incidenceAngle";
    private static final String U_PROPERTY_NAME = "u";
    private static final String COPY_MASKS_PROPERTY_NAME = "copyMasks";

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

        JList bandList = (JList) this.bindingContext.getBinding(SOURCE_BAND_NAMES_PROPERTY_NAME).getComponents()[0];
        OperatorUIUtils.initParamList(bandList, getBandNames(), (Object[]) this.paramMap.get("sourceBands"));
        Product sourceProduct = getCurrentProduct();
        if (sourceProduct != null) {
            boolean isSentinel2 = StringHelper.startsWithIgnoreCase(sourceProduct.getProductType(), "S2_MSI_Level");
            boolean isSpot = StringHelper.startsWithIgnoreCase(sourceProduct.getProductType(), "SPOTSCENE");
            PropertySet propertySet = this.bindingContext.getPropertySet();

            // Solar irradiance: for Sentinel-2 and SPOT products it can be extracted from metadata (done in initialize() method of the operator)
            Float solarIrradiance = (Float) this.paramMap.get(SOLAR_IRRADIANCE_PROPERTY_NAME);
            if (solarIrradiance != null) {
                propertySet.setValue(SOLAR_IRRADIANCE_PROPERTY_NAME, solarIrradiance);
            }
            this.bindingContext.setComponentsEnabled(SOLAR_IRRADIANCE_PROPERTY_NAME, !(isSentinel2 || isSpot));

            // Incident angle:
            // For Sentinel-2 the incidence angle is replaced with the values from the sun_zenith band. (done in initialize() method of the operator)
            // For SPOT the value is extracted from metadata, IF PRESENT (therefore for SPOT the field should be let editable
            this.bindingContext.setComponentsEnabled(INCIDENCE_ANGLE_PROPERTY_NAME, !isSentinel2);
            Float incidenceAngle = (Float) this.paramMap.get(INCIDENCE_ANGLE_PROPERTY_NAME);
            if (isSpot && (incidenceAngle == null || incidenceAngle == 0)) {
                incidenceAngle = ReflectanceToRadianceOp.extractIncidenceAngleFromSpotProduct(sourceProduct);
            }
            if (incidenceAngle != null) {
                propertySet.setValue(INCIDENCE_ANGLE_PROPERTY_NAME, incidenceAngle);
            }

            Float u = (Float) this.paramMap.get(U_PROPERTY_NAME);
            if (isSentinel2 && (u == null || u == 0)) {
                u = ReflectanceToRadianceOp.extractUFromSentinelProduct(sourceProduct);
            }
            if (u != null) {
                propertySet.setValue(U_PROPERTY_NAME, u);
            }
            this.bindingContext.setComponentsEnabled(U_PROPERTY_NAME, true);

            Boolean copyMasks = (Boolean) this.paramMap.get(COPY_MASKS_PROPERTY_NAME);
            if (copyMasks != null) {
                propertySet.setValue(COPY_MASKS_PROPERTY_NAME, copyMasks);
            }
        }
    }

    @Override
    public UIValidation validateParameters() {
        return new UIValidation(UIValidation.State.OK, "");
    }

    @Override
    public void updateParameters() {
        updateSourceBands();

        JList bandList = (JList) this.bindingContext.getBinding(SOURCE_BAND_NAMES_PROPERTY_NAME).getComponents()[0];
        OperatorUIUtils.updateParamList(bandList, this.paramMap, SOURCE_BAND_NAMES_PROPERTY_NAME);

        PropertySet propertySet = this.bindingContext.getPropertySet();

        this.paramMap.put(SOLAR_IRRADIANCE_PROPERTY_NAME, propertySet.getValue(SOLAR_IRRADIANCE_PROPERTY_NAME));

        this.paramMap.put(U_PROPERTY_NAME, propertySet.getValue(U_PROPERTY_NAME));

        this.paramMap.put(INCIDENCE_ANGLE_PROPERTY_NAME, propertySet.getValue(INCIDENCE_ANGLE_PROPERTY_NAME));

        this.paramMap.put(COPY_MASKS_PROPERTY_NAME, propertySet.getValue(COPY_MASKS_PROPERTY_NAME));
    }

    private void updateSourceBands() {
        if (this.propertySet == null) return;

        final Property[] properties = this.propertySet.getProperties();
        for (Property p : properties) {
            final PropertyDescriptor descriptor = p.getDescriptor();
            final String alias = descriptor.getAlias();
            final String[] bandNames = getBandNames();

            if (this.sourceProducts == null || alias == null || !alias.equals("sourceBands") || bandNames.length < 1)
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
                Logger.getLogger(ReflectanceToRadianceUI.class.getName()).warning(e.getMessage());
            }
        }
    }

    private Product getCurrentProduct() {
        return this.sourceProducts != null && this.sourceProducts.length > 0 ? this.sourceProducts[0] : null;
    }

}
