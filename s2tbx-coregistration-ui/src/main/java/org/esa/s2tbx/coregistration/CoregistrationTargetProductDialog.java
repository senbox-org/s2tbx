package org.esa.s2tbx.coregistration;

import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
import org.esa.snap.core.gpf.internal.RasterDataNodeValues;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.core.gpf.ui.TargetProductSelectorModel;
import org.esa.snap.core.util.PrivilegedAccessor;
import org.esa.snap.ui.AppContext;

import java.lang.reflect.Method;
import java.util.List;

public class CoregistrationTargetProductDialog extends DefaultSingleTargetProductDialog {
    private static final String PROPERTY_MASTER_SOURCE_BAND = "masterSourceBand";
    private static final String PROPERTY_SLAVE_SOURCE_BAND = "slaveSourceBand";
    private static final int MASTER_PRODUCT_ID = 0;
    private static final int SLAVE_PRODUCT_ID = 1;

    private long createTargetProductTime;

    public CoregistrationTargetProductDialog(String operatorName, AppContext appContext, String title, String helpID) {
        this(operatorName, appContext, title, helpID, true);
    }

    public CoregistrationTargetProductDialog(String operatorName, AppContext appContext, String title, String helpID, boolean targetProductSelectorDisplay) {
        super(operatorName, appContext, title, helpID, targetProductSelectorDisplay);

        DefaultIOParametersPanel ioParametersPanel = getDefaultIOParametersPanel();
        List<SourceProductSelector> sourceProductSelectorList = ioParametersPanel.getSourceProductSelectorList();
        if (!sourceProductSelectorList.isEmpty()) {
            //sourceProductSelectorList.get(MASTER_PRODUCT_ID).addSelectionChangeListener(new ProductListSelectionChangeListener(MASTER_PRODUCT_ID, PROPERTY_MASTER_SOURCE_BAND));
            //sourceProductSelectorList.get(SLAVE_PRODUCT_ID).addSelectionChangeListener(new ProductListSelectionChangeListener(SLAVE_PRODUCT_ID, PROPERTY_SLAVE_SOURCE_BAND));
            SelectionChangeListener listener = new SelectionChangeListener() {

                @Override
                public void selectionChanged(SelectionChangeEvent event) {
                    processSelectedProducts();
                }

                @Override
                public void selectionContextChanged(SelectionChangeEvent event) {
                }
            };
            sourceProductSelectorList.get(MASTER_PRODUCT_ID).addSelectionChangeListener(listener);
            sourceProductSelectorList.get(SLAVE_PRODUCT_ID).addSelectionChangeListener(listener);
        }
    }

    @Override
    public void onApply() {
//        ParameterDescriptor[] opDescriptors = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi("CoregistrationOp").getOperatorDescriptor().getParameterDescriptors();
//        for (ParameterDescriptor desc : opDescriptors) {
//            if(desc.getName().equals(PROPERTY_MASTER_SOURCE_BAND)){
//                //((AnnotationParameterDescriptor) desc).getValueSet()[0] = "gray";
//                //getBindingContext().getPropertySet().getDescriptor("").setValueSet("");
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        super.onApply();
    }

    @Override
    public int show() {
        int result = super.show();
        return result;
    }

    private void processSelectedProducts() {
        DefaultIOParametersPanel ioParametersPanel = getDefaultIOParametersPanel();
        List<SourceProductSelector> sourceProductSelectorList = ioParametersPanel.getSourceProductSelectorList();
        BindingContext bindingContext = getBindingContext();
        PropertySet propertySet = bindingContext.getPropertySet();

        try {
            Method m = PrivilegedAccessor.getMethod((DefaultSingleTargetProductDialog) this, "updateValueSet",
                    new Class[]{PropertyDescriptor.class, Product.class});
//            Product selectedProduct = sourceProductSelectorList.get(MASTER_PRODUCT_ID).getSelectedProduct();
//            if (selectedProduct != null) {
//                m.invoke(null, new Object[]{propertySet.getDescriptor(PROPERTY_MASTER_SOURCE_BAND), selectedProduct});
//            }
            PropertyDescriptor descriptor = propertySet.getDescriptor(PROPERTY_SLAVE_SOURCE_BAND);
            descriptor.setAttribute(RasterDataNodeValues.ATTRIBUTE_NAME, Band.class);
            Product selectedProduct = sourceProductSelectorList.get(SLAVE_PRODUCT_ID).getSelectedProduct();
            if (selectedProduct != null) {
                m.invoke(null, new Object[]{descriptor, selectedProduct});

                TargetProductSelectorModel targetProductSelectorModel = getTargetProductSelector().getModel();
                targetProductSelectorModel.setProductName(selectedProduct.getName() + getTargetProductNameSuffix());
            }
            //GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi("")
            Object obj = PrivilegedAccessor.getValue(this, "operatorDescriptor");
            OperatorDescriptor opDescriptor = (OperatorDescriptor) obj;
            System.out.println(opDescriptor.getName());
        } catch (Exception ex) {
            throw new OperatorException("Could not update band list as parameter: " + ex.getMessage());
        }
    }

    /**
     * Sets values according to the selected product.
     */
    private void processSelectedProducts(int productId, String propertyName) {

        DefaultIOParametersPanel ioParametersPanel = getDefaultIOParametersPanel();
        List<SourceProductSelector> sourceProductSelectorList = ioParametersPanel.getSourceProductSelectorList();
        Product selectedProduct = sourceProductSelectorList.get(productId).getSelectedProduct();

        try {
            if (selectedProduct != null) {
                //String bands = String.join(",", selectedProduct.getBandGroup().getNodeNames());
                BindingContext bindingContext = getBindingContext();
                PropertySet propertySet = bindingContext.getPropertySet();
                //PrivilegedAccessor.invokeMethod((DefaultSingleTargetProductDialog)this, "updateValueSet", new Object[]{selectedProduct, propertySet.getDescriptor(propertyName)});
                Method m = PrivilegedAccessor.getMethod((DefaultSingleTargetProductDialog) this, "updateValueSet",
                        new Class[]{PropertyDescriptor.class, Product.class});
                m.invoke(null, new Object[]{propertySet.getDescriptor(propertyName), selectedProduct});
                //propertySet.getDescriptor(propertyName)//.getDescriptor().setValueSet(ValueSet.parseValueSet(bands, String.class));
            }
        } catch (Exception ex) {
            throw new OperatorException("Could not update band list as parameter: " + ex.getMessage());
        }
        //update also the target product name according to slave
        if (productId != SLAVE_PRODUCT_ID) {
            selectedProduct = sourceProductSelectorList.get(SLAVE_PRODUCT_ID).getSelectedProduct();
        }
        if (selectedProduct != null) {
            TargetProductSelectorModel targetProductSelectorModel = getTargetProductSelector().getModel();
            targetProductSelectorModel.setProductName(selectedProduct.getName() + getTargetProductNameSuffix());
        }
    }

    class ProductListSelectionChangeListener implements SelectionChangeListener {

        private int productId;
        private String propertyName;

        public ProductListSelectionChangeListener(int productId, String propertyName) {
            this.productId = productId;
            this.propertyName = propertyName;
        }

        public void selectionChanged(SelectionChangeEvent event) {
            processSelectedProducts(this.productId, this.propertyName);
        }

        public void selectionContextChanged(SelectionChangeEvent event) {
        }
    }

}
