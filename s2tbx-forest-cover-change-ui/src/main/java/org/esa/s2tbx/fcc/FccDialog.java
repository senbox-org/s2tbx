package org.esa.s2tbx.fcc;

import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import java.util.List;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.ui.AppContext;
import org.esa.snap.utils.StringHelper;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class FccDialog extends DefaultSingleTargetProductDialog {


    public FccDialog(String operatorName, AppContext appContext, String title, String helpID) {
        this(operatorName, appContext, title, helpID, true);
    }

    public FccDialog(String operatorName, AppContext appContext, String title, String helpID, boolean targetProductSelectorDisplay) {
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
            BindingContext bindingContext = getBindingContext();
            PropertySet propertySet = bindingContext.getPropertySet();
            propertySet.setDefaultValues();
            String propertyName = "percentage";
            if (bindingContext.getBinding(propertyName) != null) {

            }
        }
    }
}
