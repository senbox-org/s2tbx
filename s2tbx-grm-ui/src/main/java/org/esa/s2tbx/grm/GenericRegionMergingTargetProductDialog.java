package org.esa.s2tbx.grm;

import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.common.WriteOp;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.gpf.internal.OperatorProductReader;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.core.gpf.ui.TargetProductSelectorModel;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.rcp.actions.file.SaveProductAsAction;
import org.esa.snap.ui.AppContext;
import org.esa.snap.utils.StringHelper;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

/**
 * @author Jean Coravu.
 */
public class GenericRegionMergingTargetProductDialog extends DefaultSingleTargetProductDialog {
    private static final String PROPERTY_MEGING_COST_CRITERION = "mergingCostCriterion";
    private static final String PROPERTY_SPECTRAL_WEIGHT = "spectralWeight";
    private static final String PROPERTY_SHAPE_WEIGHT = "shapeWeight";
    private static final String PROPERTY_THRESHOLD = "threshold";

    private long createTargetProductTime;

    public GenericRegionMergingTargetProductDialog(String operatorName, AppContext appContext, String title, String helpID) {
        this(operatorName, appContext, title, helpID, true);
    }

    public GenericRegionMergingTargetProductDialog(String operatorName, AppContext appContext, String title, String helpID, boolean targetProductSelectorDisplay) {
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

        BindingContext bindingContext = getBindingContext();
        PropertySet propertySet = bindingContext.getPropertySet();
        propertySet.getProperty(PROPERTY_MEGING_COST_CRITERION).addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                updateEnabledState();
            }
        });
    }

    @Override
    public int show() {
        int result = super.show();
        processSelectedProduct();
        updateEnabledState();
        return result;
    }

    @Override
    protected void onApply() {
        if (!canApply()) {
            return;
        }

        String productDir = targetProductSelector.getModel().getProductDir().getAbsolutePath();
        appContext.getPreferences().setPropertyString(SaveProductAsAction.PREFERENCES_KEY_LAST_PRODUCT_DIR, productDir);

        Product targetProduct = null;
        try {
            long t0 = System.currentTimeMillis();
            targetProduct = createTargetProduct();
            createTargetProductTime = System.currentTimeMillis() - t0;

        } catch (Throwable t) {
            handleInitialisationError(t);
            return;
        }
        if (targetProduct == null) {
            throw new NullPointerException("The target product is null.");
        }

        targetProduct.setName(targetProductSelector.getModel().getProductName());
        targetProduct.setFileLocation(targetProductSelector.getModel().getProductFile());
        GRMProductWriterSwingWorker worker = new GRMProductWriterSwingWorker(targetProduct);
        worker.executeWithBlocking();
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
            PropertySet propertySet = bindingContext.getPropertySet();
            propertySet.setDefaultValues();
            if (bindingContext.getBinding(PROPERTY_SPECTRAL_WEIGHT) != null) {
                propertySet.setValue(PROPERTY_SPECTRAL_WEIGHT, 0.0f);
            }
            if (bindingContext.getBinding(PROPERTY_SHAPE_WEIGHT) != null) {
                propertySet.setValue(PROPERTY_SHAPE_WEIGHT, 0.0f);
            }
            if (bindingContext.getBinding(PROPERTY_THRESHOLD) != null) {
                propertySet.setValue(PROPERTY_THRESHOLD, 0);
            }
        }
    }

    private void updateEnabledState() {
        BindingContext bindingContext = getBindingContext();
        PropertySet propertySet = bindingContext.getPropertySet();
        String newValue = propertySet.getProperty(PROPERTY_MEGING_COST_CRITERION).getValue();
        boolean enabled = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION.equalsIgnoreCase(newValue);
        bindingContext.setComponentsEnabled(PROPERTY_SPECTRAL_WEIGHT, enabled);
        bindingContext.setComponentsEnabled(PROPERTY_SHAPE_WEIGHT, enabled);
    }

    private class GRMProductWriterSwingWorker extends ProgressMonitorSwingWorker<Product, Object> {
        private final Product targetProduct;
        private long saveTime;

        private GRMProductWriterSwingWorker(Product targetProduct) {
            super(getJDialog(), "Writing Target Product");

            this.targetProduct = targetProduct;
        }

        @Override
        protected Product doInBackground(ProgressMonitor pm) throws Exception {
            TargetProductSelectorModel model = getTargetProductSelector().getModel();
            pm.beginTask("Writing...", model.isOpenInAppSelected() ? 100 : 95);
            saveTime = 0L;
            Product product = null;
            try {
                long t0 = System.currentTimeMillis();
                OperatorProductReader opReader = (OperatorProductReader) this.targetProduct.getProductReader();
                GenericRegionMergingOp execOp = (GenericRegionMergingOp)opReader.getOperatorContext().getOperator();
                execOp.runSegmentation();

                File file = model.getProductFile();
                String formatName = model.getFormatName();
                boolean clearCacheAfterRowWrite = false;
                boolean incremental = false;
                GPF.writeProduct(this.targetProduct, file, formatName, clearCacheAfterRowWrite, incremental, ProgressMonitor.NULL);

                saveTime = System.currentTimeMillis() - t0;
                if (model.isOpenInAppSelected()) {
                    File targetFile = model.getProductFile();
                    if (!targetFile.exists())
                        targetFile = targetProduct.getFileLocation();
                    if (targetFile.exists()) {
                        product = ProductIO.readProduct(targetFile);
                        if (product == null) {
                            product = targetProduct; // todo - check - this cannot be ok!!! (nf)
                        }
                    }
                    pm.worked(5);
                }
            } finally {
                pm.done();
                if (product != targetProduct) {
                    targetProduct.dispose();
                }
                Preferences preferences = SnapApp.getDefault().getPreferences();
                if (preferences.getBoolean(GPF.BEEP_AFTER_PROCESSING_PROPERTY, false)) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            return product;
        }

        @Override
        protected void done() {
            final TargetProductSelectorModel model = getTargetProductSelector().getModel();
            long totalSaveTime = saveTime + createTargetProductTime;
            try {
                final Product targetProduct = get();
                if (model.isOpenInAppSelected()) {
                    appContext.getProductManager().addProduct(targetProduct);
                    //showSaveAndOpenInAppInfo(totalSaveTime);
                } else {
                    //showSaveInfo(totalSaveTime);
                }
            } catch (InterruptedException e) {
                // ignore
            } catch (ExecutionException e) {
                handleProcessingError(e.getCause());
            } catch (Throwable t) {
                handleProcessingError(t);
            }
        }
    }
}
