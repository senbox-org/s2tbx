package org.esa.s2tbx.reflectance2radiance;

import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.ui.AppContext;
import org.esa.snap.utils.StringHelper;

import java.util.List;

/**
 * Reflectance-to-Radiance dialog.
 *
 * @author Jean Coravu.
 */
public class ReflectanceTargetProductDialog extends DefaultSingleTargetProductDialog {

    public ReflectanceTargetProductDialog(String operatorName, AppContext appContext, String title, String helpID) {
        this(operatorName, appContext, title, helpID, true);
    }

    public ReflectanceTargetProductDialog(String operatorName, AppContext appContext, String title, String helpID, boolean targetProductSelectorDisplay) {
        super(operatorName, appContext, title, helpID, targetProductSelectorDisplay);

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
    }

    @Override
    public int show() {
        int result = super.show();
        processSelectedProduct();
        return result;
    }

    @Override
    public void hide() {
        super.hide();
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

    /**
     * Sets the incidence angle and the quantification value according to the selected product.
     */
    private void processSelectedProduct() {
        Product selectedProduct = getSelectedProduct();
        if (selectedProduct != null) {
            boolean isSentinel2 = StringHelper.startsWithIgnoreCase(selectedProduct.getProductType(), "S2_MSI_Level");
            boolean isSpot = StringHelper.startsWithIgnoreCase(selectedProduct.getProductType(), "SPOTSCENE");
            BindingContext bindingContext = getBindingContext();
            PropertySet propertySet = bindingContext.getPropertySet();
            propertySet.setDefaultValues();

            // Solar irradiance: for Sentinel-2 and SPOT products it can be extracted from metadata (done in initialize() method of the operator)
            String propertyName = "solarIrradiance";
            if (bindingContext.getBinding(propertyName) != null) {
                bindingContext.setComponentsEnabled(propertyName, !(isSentinel2 || isSpot));
            }

            // Incident angle:
            // For Sentinel-2 the incidence angle is replaced with the values from the sun_zenith band. (done in initialize() method of the operator)
            // For SPOT the value is extracted from metadata, IF PRESENT (therefore for SPOT the field should be let editable
            propertyName = "incidenceAngle";
            if (bindingContext.getBinding(propertyName) != null) {
                bindingContext.setComponentsEnabled(propertyName, !isSentinel2);
                if (isSpot) {
                    propertySet.setValue(propertyName, ReflectanceToRadianceOp.extractIncidenceAngleFromSpotProduct(selectedProduct));
                }
            }
            propertyName = "u";
            if (bindingContext.getBinding(propertyName) != null) {
                if (isSentinel2) {
                    propertySet.setValue(propertyName, ReflectanceToRadianceOp.extractUFromSentinelProduct(selectedProduct));
                }
                bindingContext.setComponentsEnabled(propertyName, true);
            }
        }
    }
}