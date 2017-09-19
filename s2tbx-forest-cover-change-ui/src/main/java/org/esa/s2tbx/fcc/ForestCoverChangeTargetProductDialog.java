package org.esa.s2tbx.fcc;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyPane;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import com.bc.ceres.swing.selection.AbstractSelectionChangeListener;
import com.bc.ceres.swing.selection.Selection;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductNodeEvent;
import org.esa.snap.core.datamodel.ProductNodeListener;
import org.esa.snap.core.datamodel.RasterDataNode;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.SourceProducts;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.annotations.TargetProperty;
import org.esa.snap.core.gpf.descriptor.AnnotationParameterDescriptor;
import org.esa.snap.core.gpf.descriptor.AnnotationSourceProductDescriptor;
import org.esa.snap.core.gpf.descriptor.AnnotationTargetPropertyDescriptor;
import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
import org.esa.snap.core.gpf.descriptor.ParameterDescriptor;
import org.esa.snap.core.gpf.descriptor.SourceProductDescriptor;
import org.esa.snap.core.gpf.descriptor.TargetPropertyDescriptor;
import org.esa.snap.core.gpf.internal.RasterDataNodeValues;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.OperatorMenu;
import org.esa.snap.core.gpf.ui.OperatorParameterSupport;
import org.esa.snap.core.gpf.ui.SingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.core.gpf.ui.TargetProductSelectorModel;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.rcp.actions.file.SaveProductAsAction;
import org.esa.snap.ui.AppContext;
import org.esa.snap.ui.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;
/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class ForestCoverChangeTargetProductDialog extends SingleTargetProductDialog {
    private final String operatorName;
    private final OperatorDescriptor operatorDescriptor;
    private final OperatorParameterSupport parameterSupport;
    private final BindingContext bindingContext;
    private final DefaultIOParametersPanel ioParametersPanel;
    private final ProductChangedHandler productChangedHandler;

    private List<ParameterDescriptor> parameterDescriptors;
    private List<SourceProductDescriptor> sourceProductDescriptors;
    private List<TargetPropertyDescriptor> targetPropertyDescriptors;
    private Map<String, List<String>> parameterGroupDescriptors;
    private JTabbedPane form;
    private PropertyDescriptor[] rasterDataNodeTypeProperties;
    private String targetProductNameSuffix;
    private ForestCoverChange forestCoverChange;

    public ForestCoverChangeTargetProductDialog(String operatorName, AppContext appContext, String title, String helpID) {
        super(appContext, title, ID_APPLY_CLOSE, helpID);

        this.operatorName = operatorName;
        this.targetProductNameSuffix = "";

        processAnnotationsRec(ForestCoverChange.class);
        this.operatorDescriptor = new OperatorDescriptorClass( this.parameterDescriptors.toArray(new ParameterDescriptor[0]),
                this.sourceProductDescriptors.toArray(new SourceProductDescriptor[0]));
        this.ioParametersPanel = new DefaultIOParametersPanel(getAppContext(), this.operatorDescriptor, getTargetProductSelector(), true);

        this.parameterSupport = new OperatorParameterSupport(this.operatorDescriptor);
        ArrayList<SourceProductSelector> sourceProductSelectorList = this.ioParametersPanel.getSourceProductSelectorList();
        PropertySet propertySet = this.parameterSupport.getPropertySet();

        this.bindingContext = new BindingContext(propertySet);

        if (propertySet.getProperties().length > 0) {
            if (!sourceProductSelectorList.isEmpty()) {
                Property[] properties = propertySet.getProperties();
                List<PropertyDescriptor> rdnTypeProperties = new ArrayList<>(properties.length);
                for (Property property : properties) {
                    PropertyDescriptor parameterDescriptor = property.getDescriptor();
                    if (parameterDescriptor.getAttribute(RasterDataNodeValues.ATTRIBUTE_NAME) != null) {
                        rdnTypeProperties.add(parameterDescriptor);
                    }
                }
                this.rasterDataNodeTypeProperties = rdnTypeProperties.toArray(new PropertyDescriptor[rdnTypeProperties.size()]);
            }
        }
        this.productChangedHandler = new ProductChangedHandler();
        if (!sourceProductSelectorList.isEmpty()) {
            sourceProductSelectorList.get(0).addSelectionChangeListener(productChangedHandler);
        }

    }

    @Override
    protected void onApply() {
        if (!canApply()) {
            return;
        }

        TargetProductSelectorModel model = targetProductSelector.getModel();
        String productDirPath = model.getProductDir().getAbsolutePath();
        appContext.getPreferences().setPropertyString(SaveProductAsAction.PREFERENCES_KEY_LAST_PRODUCT_DIR, productDirPath);
        long createTargetProductTime = 0;
        try {
            final HashMap<String, Product> sourceProducts = ioParametersPanel.createSourceProductsMap();

            this.forestCoverChange = new ForestCoverChange(sourceProducts.get("recentProduct"),
                                                           sourceProducts.get("previousProduct"),
                                                           this.parameterSupport.getParameterMap());
        } catch (Throwable t) {
            handleInitialisationError(t);
            return;
        }
        TargetProductSwingWorker worker = new TargetProductSwingWorker(this.forestCoverChange, createTargetProductTime);
        worker.executeWithBlocking(); // start the thread
    }

    @Override
    public int show() {
        this.ioParametersPanel.initSourceProductSelectors();
        if (this.form == null) {
            initForm();
            if (getJDialog().getJMenuBar() == null) {
                OperatorMenu operatorMenu = createDefaultMenuBar();
                getJDialog().setJMenuBar(operatorMenu.createDefaultMenu());
            }
        }
        setContent(this.form);
        return super.show();
    }

    @Override
    public void hide() {
        productChangedHandler.releaseProduct();
        ioParametersPanel.releaseSourceProductSelectors();
        super.hide();
    }

    @Override
    protected Product createTargetProduct() throws Exception {
        HashMap<String, Product> sourceProducts = this.ioParametersPanel.createSourceProductsMap();
        return GPF.createProduct(this.operatorName, this.parameterSupport.getParameterMap(), sourceProducts);
    }

    public String getTargetProductNameSuffix() {
        return this.targetProductNameSuffix;
    }

    public void setTargetProductNameSuffix(String suffix) {
        this.targetProductNameSuffix = suffix;
    }

    private void initForm() {
        this.form = new JTabbedPane();
        this.form.add("I/O Parameters", this.ioParametersPanel);
        final TableLayout layout = new TableLayout(1);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.BOTH);
        layout.setTableWeightX(1.0);
        layout.setTableWeightY(0.0);
        layout.setRowWeightY(2, 1.0);
        layout.setTablePadding(3, 3);

        if (this.bindingContext.getPropertySet().getProperties().length > 0) {
            PropertyContainer container = new PropertyContainer();
            container.addProperties(this.bindingContext.getPropertySet().getProperties());
            if (this.parameterGroupDescriptors != null) {
                for (Map.Entry<String, List<String>> pair : this.parameterGroupDescriptors.entrySet()) {
                    for (String prop : pair.getValue()) {
                        container.removeProperty(this.bindingContext.getPropertySet().getProperty(prop));
                    }
                }
            }
            final PropertyPane parametersPane = new PropertyPane(container);
            final JPanel parametersPanel = new JPanel(layout);
            parametersPanel.add(parametersPane.createPanel());
            parametersPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            form.add("Processing Parameters", new JScrollPane(parametersPanel));
            if (this.parameterGroupDescriptors != null) {
                for (Map.Entry<String, List<String>> pair : this.parameterGroupDescriptors.entrySet()) {
                    parametersPanel.add(createPanel(pair.getKey() + " parameters", this.bindingContext, pair.getValue()));
                }
            }
            updateSourceProduct();
        }
    }

    private void showSaveInfo(long saveTime) {
        File productFile = getTargetProductSelector().getModel().getProductFile();
        String message = MessageFormat.format(
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
        String message = MessageFormat.format(
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

    private JPanel createPanel(String name, BindingContext bindingContext, List<String> parameters) {
        PropertyContainer container = new PropertyContainer();
        for (String parameter: parameters) {
            Property prop = bindingContext.getPropertySet().getProperty(parameter);
            container.addProperty(prop);
        }
        final PropertyPane parametersPane = new PropertyPane(container);
        final JPanel panel = parametersPane.createPanel();
        panel.setBorder(BorderFactory.createTitledBorder(name));
        return panel;
    }

    private void processAnnotationsRec(Class<?> operatorClass) {
        Class<?> superclass = operatorClass.getSuperclass();
        if (superclass != null && !superclass.equals(Operator.class)) {
            processAnnotationsRec(superclass);
        }

        final Field[] declaredFields = operatorClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {

            String fieldName = declaredField.getName();
            Class<?> fieldType = declaredField.getType();

            ParameterGroup parameterGroupAnnotation = declaredField.getAnnotation(ParameterGroup.class);
            if(parameterGroupAnnotation!=null){
                String alias = parameterGroupAnnotation.alias();
                if (parameterGroupDescriptors == null) {
                    parameterGroupDescriptors = new HashMap<>();
                }
                List<String>value = parameterGroupDescriptors.get(alias);
                if(value == null){
                    value = new ArrayList<>();
                    parameterGroupDescriptors.put(alias, value);
                }
                value.add(fieldName);
            }
            Parameter parameterAnnotation = declaredField.getAnnotation(Parameter.class);
            if (parameterAnnotation != null) {
                if (parameterDescriptors == null) {
                    parameterDescriptors = new ArrayList<>();
                }
                boolean isDeprecated = declaredField.getAnnotation(Deprecated.class) != null;
                parameterDescriptors.add(new AnnotationParameterDescriptor(fieldName, fieldType, isDeprecated, parameterAnnotation));
                continue;
            }

            SourceProduct sourceProductAnnotation = declaredField.getAnnotation(SourceProduct.class);
            if (sourceProductAnnotation != null && Product.class.isAssignableFrom(fieldType)) {
                if (sourceProductDescriptors == null) {
                    sourceProductDescriptors = new ArrayList<>();
                }
                sourceProductDescriptors.add(new AnnotationSourceProductDescriptor(fieldName, sourceProductAnnotation));
                continue;
            }

            SourceProducts sourceProductsAnnotation = declaredField.getAnnotation(SourceProducts.class);
            if (sourceProductsAnnotation != null && Product[].class.isAssignableFrom(fieldType)) {
                continue;
            }

            TargetProduct targetProductAnnotation = declaredField.getAnnotation(TargetProduct.class);
            if (targetProductAnnotation != null) {
                continue;
            }

            TargetProperty targetPropertyAnnotation = declaredField.getAnnotation(TargetProperty.class);
            if (targetPropertyAnnotation != null) {
                if (targetPropertyDescriptors == null) {
                    targetPropertyDescriptors = new ArrayList<>();
                }
                targetPropertyDescriptors.add(new AnnotationTargetPropertyDescriptor(fieldName, fieldType, targetPropertyAnnotation));
            }
        }
    }

    private OperatorMenu createDefaultMenuBar() {
        return new OperatorMenu(getJDialog(), operatorDescriptor, parameterSupport, getAppContext(), getHelpID());
    }

    private void updateSourceProduct() {
        try {
            Property property = bindingContext.getPropertySet().getProperty(UIUtils.PROPERTY_SOURCE_PRODUCT);
            if (property != null) {
                property.setValue(productChangedHandler.currentProduct);
            }
        } catch (ValidationException e) {
            throw new IllegalStateException("Property '" + UIUtils.PROPERTY_SOURCE_PRODUCT + "' must be of type " + Product.class + ".", e);
        }
    }

    private class ProductChangedHandler extends AbstractSelectionChangeListener implements ProductNodeListener {

        private Product currentProduct;

        public void releaseProduct() {
            if (currentProduct != null) {
                currentProduct.removeProductNodeListener(this);
                currentProduct = null;
                updateSourceProduct();
            }
        }

        @Override
        public void selectionChanged(SelectionChangeEvent event) {
            Selection selection = event.getSelection();
            if (selection != null) {
                final Product selectedProduct = (Product) selection.getSelectedValue();
                if (selectedProduct != currentProduct) {
                    if (currentProduct != null) {
                        currentProduct.removeProductNodeListener(this);
                    }
                    currentProduct = selectedProduct;
                    if (currentProduct != null) {
                        currentProduct.addProductNodeListener(this);
                    }
                    if(getTargetProductSelector() != null){
                        updateTargetProductName();
                    }
                    updateValueSets(currentProduct);
                    updateSourceProduct();
                }
            }
        }

        @Override
        public void nodeAdded(ProductNodeEvent event) {
            handleProductNodeEvent();
        }

        @Override
        public void nodeChanged(ProductNodeEvent event) {
            handleProductNodeEvent();
        }

        @Override
        public void nodeDataChanged(ProductNodeEvent event) {
            handleProductNodeEvent();
        }

        @Override
        public void nodeRemoved(ProductNodeEvent event) {
            handleProductNodeEvent();
        }

        private void updateTargetProductName() {
            String productName = "";
            if (currentProduct != null) {
                productName = currentProduct.getName();
            }
            final TargetProductSelectorModel targetProductSelectorModel = getTargetProductSelector().getModel();
            targetProductSelectorModel.setProductName(productName + getTargetProductNameSuffix());
        }

        private void handleProductNodeEvent() {
            updateValueSets(currentProduct);
        }

        private void updateValueSets(Product product) {
            if (rasterDataNodeTypeProperties != null) {
                for (PropertyDescriptor propertyDescriptor : rasterDataNodeTypeProperties) {
                    updateValueSet(propertyDescriptor, product);
                }
            }
        }
    }

    private static void updateValueSet(PropertyDescriptor propertyDescriptor, Product product) {
        String[] values = new String[0];
        if (product != null) {
            Object object = propertyDescriptor.getAttribute(RasterDataNodeValues.ATTRIBUTE_NAME);
            if (object != null) {
                @SuppressWarnings("unchecked")
                Class<? extends RasterDataNode> rasterDataNodeType = (Class<? extends RasterDataNode>) object;
                boolean includeEmptyValue = !propertyDescriptor.isNotNull() && !propertyDescriptor.isNotEmpty() &&
                        !propertyDescriptor.getType().isArray();
                values = RasterDataNodeValues.getNames(product, rasterDataNodeType, includeEmptyValue);
            }
        }
        propertyDescriptor.setValueSet(new ValueSet(values));
    }

    private class TargetProductSwingWorker extends ProgressMonitorSwingWorker<Product, Object> {
        private long saveTime;
        private final long createTargetProductTime;
        private final ForestCoverChange forestCoverChange;

        private TargetProductSwingWorker(ForestCoverChange forestCoverChange, long createTargetProductTime) {
            super(getJDialog(), "Run Forest Cover Change");

            this.forestCoverChange = forestCoverChange;
            this.createTargetProductTime = createTargetProductTime;
        }

        @Override
        protected Product doInBackground(ProgressMonitor pm) throws Exception {
            final TargetProductSelectorModel model = getTargetProductSelector().getModel();
            pm.beginTask("Running...", model.isOpenInAppSelected() ? 100 : 95);
            saveTime = 0L;
            Product product = null;
            Product targetProduct = this.forestCoverChange.getTargetProduct();
            try {
                long t0 = System.currentTimeMillis();

                this.forestCoverChange.doExecute();
                if (model.isSaveToFileSelected()) {
                    File file = model.getProductFile();
                    String formatName = model.getFormatName();
                    boolean clearCacheAfterRowWrite = false;
                    boolean incremental = false;
                    GPF.writeProduct(targetProduct, file, formatName, clearCacheAfterRowWrite, incremental, ProgressMonitor.NULL);
                }

                product = targetProduct;

                saveTime = System.currentTimeMillis() - t0;
                if (model.isOpenInAppSelected()) {
                    File targetFile = model.getProductFile();
                    if (targetFile == null || !targetFile.exists()) {
                        targetFile = targetProduct.getFileLocation();
                    }
                    if (targetFile != null && targetFile.exists()) {
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
