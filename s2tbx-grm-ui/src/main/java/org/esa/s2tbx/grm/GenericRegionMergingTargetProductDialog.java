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
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.gpf.internal.OperatorProductReader;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.core.gpf.ui.TargetProductSelectorModel;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.rcp.actions.file.SaveProductAsAction;
import org.esa.snap.ui.AppContext;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.MessageFormat;
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

        TargetProductSelectorModel model = targetProductSelector.getModel();
        String productDirPath = model.getProductDir().getAbsolutePath();
        appContext.getPreferences().setPropertyString(SaveProductAsAction.PREFERENCES_KEY_LAST_PRODUCT_DIR, productDirPath);

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

        targetProduct.setName(model.getProductName());
        targetProduct.setFileLocation(model.getProductFile());

        GRMProductWriterSwingWorker worker = new GRMProductWriterSwingWorker(targetProduct);
        worker.executeWithBlocking(); // start the thread
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
                propertySet.setValue(PROPERTY_SPECTRAL_WEIGHT, Float.parseFloat(GenericRegionMergingOp.DEFAULT_SPECTRAL_WEIGHT));
            }
            if (bindingContext.getBinding(PROPERTY_SHAPE_WEIGHT) != null) {
                propertySet.setValue(PROPERTY_SHAPE_WEIGHT, Float.parseFloat(GenericRegionMergingOp.DEFAULT_SHAPE_WEIGHT));
            }
            if (bindingContext.getBinding(PROPERTY_THRESHOLD) != null) {
                propertySet.setValue(PROPERTY_THRESHOLD, Float.parseFloat(GenericRegionMergingOp.DEFAULT_THRESHOLD));
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

    private void showSaveInfo(long saveTime) {
        File productFile = getTargetProductSelector().getModel().getProductFile();
        final String message = MessageFormat.format(
                "<html>The target product has been successfully written to<br>{0}<br>" +
                        "Total time spend for processing: {1}",
                formatFile(productFile),
                formatDuration(saveTime)
        );
        showSuppressibleInformationDialog(message, "saveInfo");
    }

    private String formatFile(File file) {
        return FileUtils.getDisplayText(file, 54);
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        millis -= seconds * 1000;
        long minutes = seconds / 60;
        seconds -= minutes * 60;
        long hours = minutes / 60;
        minutes -= hours * 60;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }

    private void showSaveAndOpenInAppInfo(long saveTime) {
        File productFile = getTargetProductSelector().getModel().getProductFile();
        final String message = MessageFormat.format(
                "<html>The target product has been successfully written to<br>" +
                        "<p>{0}</p><br>" +
                        "and has been opened in {1}.<br><br>" +
                        "Total time spend for processing: {2}<br>",
                formatFile(productFile),
                appContext.getApplicationName(),
                formatDuration(saveTime)
        );
        showSuppressibleInformationDialog(message, "saveAndOpenInAppInfo");
    }

    private class GRMProductWriterSwingWorker extends ProgressMonitorSwingWorker<Product, Object> {
        private final Product targetProduct;
        private long saveTime;

        private GRMProductWriterSwingWorker(Product targetProduct) {
            super(getJDialog(), "Run Segmentation");

            this.targetProduct = targetProduct;
        }

        @Override
        protected Product doInBackground(ProgressMonitor pm) throws Exception {
            final TargetProductSelectorModel model = getTargetProductSelector().getModel();
            pm.beginTask("Writing...", model.isOpenInAppSelected() ? 100 : 95);
            saveTime = 0L;
            Product product = null;
            try {
                long t0 = System.currentTimeMillis();
                OperatorProductReader opReader = (OperatorProductReader) this.targetProduct.getProductReader();
                GenericRegionMergingOp execOp = (GenericRegionMergingOp)opReader.getOperatorContext().getOperator();

                OperatorExecutor executor = OperatorExecutor.create(execOp);
                executor.execute(SubProgressMonitor.create(pm, 95));

                if (model.isSaveToFileSelected()) {
                    File file = model.getProductFile();
                    String formatName = model.getFormatName();
                    boolean clearCacheAfterRowWrite = false;
                    boolean incremental = false;
                    GPF.writeProduct(this.targetProduct, file, formatName, clearCacheAfterRowWrite, incremental, ProgressMonitor.NULL);
                }

                product = this.targetProduct;

                saveTime = System.currentTimeMillis() - t0;
                if (model.isOpenInAppSelected()) {
                    File targetFile = model.getProductFile();
                    if (!targetFile.exists()) {
                        targetFile = this.targetProduct.getFileLocation();
                    }
                    if (targetFile.exists()) {
                        product = ProductIO.readProduct(targetFile);
                        if (product == null) {
                            product = this.targetProduct; // todo - check - this cannot be ok!!! (nf)
                        }
                    }
                    pm.worked(5);
                }
            } finally {
                pm.done();
                if (product != this.targetProduct) {
                    this.targetProduct.dispose();
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
                if (model.isSaveToFileSelected() && model.isOpenInAppSelected()) {
                    appContext.getProductManager().addProduct(targetProduct);
                    showSaveAndOpenInAppInfo(totalSaveTime);
                } else if (model.isOpenInAppSelected()) {
                    appContext.getProductManager().addProduct(targetProduct);
                    showOpenInAppInfo();
                } else {
                    showSaveInfo(totalSaveTime);
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
