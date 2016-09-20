package org.esa.s2tbx.reflectance2radiance;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.ui.AppContext;

import java.util.List;

/**
 * @author Jean Coravu.
 */
public class ReflectanceTargetProductDialog extends DefaultSingleTargetProductDialog {
    //private final PropertyChangeListener sourceBandChangeListener;

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

        /*this.sourceBandChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String[] selectedSourceBandNames = (String[])event.getNewValue();
                float solarIrradiance = 0.0f;
                //if (!StringHelper.isNullOrEmpty(selectedSourceBandName)) {
                    //Product selectedProduct = getSelectedProduct();
                    *//*if (ReflectanceToRadianceOp.isSentinelProduct(selectedProduct)) {
                        solarIrradiance = ReflectanceToRadianceOp.extractSolarIrradianceFromSentinelProduct(selectedProduct, selectedSourceBandName);
                    } else if (ReflectanceToRadianceOp.isSpotProduct(selectedProduct)) {
                        solarIrradiance = ReflectanceToRadianceOp.extractSolarIrradianceFromSpotProduct(selectedProduct, selectedSourceBandName);
                    }*//*
                //}
                BindingContext bindingContext = getBindingContext();
                PropertySet propertySet = bindingContext.getPropertySet();
                propertySet.setValue("solarIrradiance", solarIrradiance);
            }
        };*/
    }

    @Override
    public int show() {
        int result = super.show();

        //getSourceBandNameProperty().addPropertyChangeListener(this.sourceBandChangeListener);
        processSelectedProduct();

        return result;
    }

    @Override
    public void hide() {
        super.hide();

        //getSourceBandNameProperty().removePropertyChangeListener(this.sourceBandChangeListener);
    }

    /**
     * Returns the property of the source band name.
     *
     * @return the property of the source band name
     */
    private Property getSourceBandNameProperty() {
        BindingContext bindingContext = getBindingContext();
        return bindingContext.getPropertySet().getProperty("sourceBandNames");
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
            BindingContext bindingContext = getBindingContext();
            String propertyName = "incidenceAngle";
            if (bindingContext.getBinding(propertyName) != null) {
                boolean enabled = true;
                PropertySet propertySet = bindingContext.getPropertySet();
                propertySet.setDefaultValues();
                /*if (ReflectanceToRadianceOp.isSentinelProduct(selectedProduct)) {
                    enabled = false;
                    float u = ReflectanceToRadianceOp.extractUFromSentinelProduct(selectedProduct);
                    propertySet.setValue("u", u);
                } else if (ReflectanceToRadianceOp.isSpotProduct(selectedProduct)) {
                    float incidenceAngle = ReflectanceToRadianceOp.extractIncidenceAngleFromSpotProduct(selectedProduct);
                    propertySet.setValue(propertyName, incidenceAngle);
                }*/
                bindingContext.setComponentsEnabled(propertyName, enabled);
            }
        }
    }
}