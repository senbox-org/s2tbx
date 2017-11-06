package org.esa.s2tbx.fcc;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyPane;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductManager;
import org.esa.snap.core.datamodel.ProductNodeGroup;
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
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.OperatorMenu;
import org.esa.snap.core.gpf.ui.OperatorParameterSupport;
import org.esa.snap.core.gpf.ui.SingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.core.gpf.ui.TargetProductSelector;
import org.esa.snap.core.gpf.ui.TargetProductSelectorModel;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.rcp.actions.file.SaveProductAsAction;
import org.esa.snap.ui.AppContext;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
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

    private static final int CURRENT_PRODUCT = 0;
    private static final int CURRENT_PRODUCT_OPTIONAL_MASK = 1;
    private static final int PREVIOUS_PRODUCT = 2;
    private static final int PREVIOUS_PRODUCT_OPTIONAL_MASK = 3;

    private final String operatorName;
    private final OperatorDescriptor operatorDescriptor;
    private final OperatorParameterSupport parameterSupport;
    private final BindingContext bindingContext;
    private final DefaultIOParametersPanel ioParametersPanel;
    private List<ParameterDescriptor> parameterDescriptors;
    private List<SourceProductDescriptor> sourceProductDescriptors;
    private List<TargetPropertyDescriptor> targetPropertyDescriptors;
    private Map<String, List<String>> parameterGroupDescriptors;
    private JTabbedPane form;
    private String targetProductNameSuffix;
    private JComboBox<String> currentProductMaskComboBox;
    private JComboBox<String> previousProductMaskComboBox;


    public ForestCoverChangeTargetProductDialog(String operatorName, AppContext appContext, String title, String helpID) {
        super(appContext, title, ID_APPLY_CLOSE, helpID);

        this.operatorName = operatorName;
        this.targetProductNameSuffix = "";
        final TargetProductSelector selector = getTargetProductSelector();
        selector.getModel().setSaveToFileSelected(false);
        selector.getSaveToFileCheckBox().setEnabled(true);
        processAnnotationsRec(ForestCoverChangeOp.class);
        this.operatorDescriptor = new OperatorDescriptorClass( this.parameterDescriptors.toArray(new ParameterDescriptor[0]),
                this.sourceProductDescriptors.toArray(new SourceProductDescriptor[0]));
        this.ioParametersPanel = new DefaultIOParametersPanel(getAppContext(), this.operatorDescriptor, getTargetProductSelector(), true);

        this.parameterSupport = new OperatorParameterSupport(this.operatorDescriptor);
        ArrayList<SourceProductSelector> sourceProductSelectorList = this.ioParametersPanel.getSourceProductSelectorList();
        PropertySet propertySet = this.parameterSupport.getPropertySet();
        this.bindingContext = new BindingContext(propertySet);

        SelectionChangeListener currentListenerProduct = new SelectionChangeListener() {
            public void selectionChanged(SelectionChangeEvent event) {
                Product product = sourceProductSelectorList.get(CURRENT_PRODUCT).getSelectedProduct();
                processCurrentSelectedProduct(product);
                if (product != null) {
                    updateTargetProductName(product);
                }
            }
            public void selectionContextChanged(SelectionChangeEvent event) {
            }
        };

        SelectionChangeListener currentListenerProductOptionalMask = new SelectionChangeListener() {
            public void selectionChanged(SelectionChangeEvent event) {
                Product selectedProduct =  sourceProductSelectorList.get(CURRENT_PRODUCT_OPTIONAL_MASK).getSelectedProduct();
                if(selectedProduct == null) {
                    processCurrentSelectedProduct(sourceProductSelectorList.get(CURRENT_PRODUCT).getSelectedProduct());
                } else {
                    processCurrentSelectedProduct(selectedProduct);
                }
            }
            public void selectionContextChanged(SelectionChangeEvent event) {
            }
        };

        SelectionChangeListener previousListenerProduct = new SelectionChangeListener() {
            public void selectionChanged(SelectionChangeEvent event) {
                processPreviousSelectedProduct(sourceProductSelectorList.get(PREVIOUS_PRODUCT).getSelectedProduct());
            }
            public void selectionContextChanged(SelectionChangeEvent event) {
            }
        };

        SelectionChangeListener previousListenerProductOptionalMask = new SelectionChangeListener() {
            public void selectionChanged(SelectionChangeEvent event) {
                Product selectedProduct =  sourceProductSelectorList.get(PREVIOUS_PRODUCT_OPTIONAL_MASK).getSelectedProduct();
                if(selectedProduct == null) {
                    processPreviousSelectedProduct(sourceProductSelectorList.get(PREVIOUS_PRODUCT).getSelectedProduct());
                } else {
                    processPreviousSelectedProduct(selectedProduct);
                }
            }
            public void selectionContextChanged(SelectionChangeEvent event) {
            }
        };

        sourceProductSelectorList.get(CURRENT_PRODUCT).addSelectionChangeListener(currentListenerProduct);
        sourceProductSelectorList.get(CURRENT_PRODUCT_OPTIONAL_MASK).addSelectionChangeListener(currentListenerProductOptionalMask);
        sourceProductSelectorList.get(PREVIOUS_PRODUCT).addSelectionChangeListener(previousListenerProduct);
        sourceProductSelectorList.get(PREVIOUS_PRODUCT_OPTIONAL_MASK).addSelectionChangeListener(previousListenerProductOptionalMask);
    }

    @Override
    protected void onApply() {
        if (!canApply()) {
            return;
        }

        TargetProductSelectorModel model = targetProductSelector.getModel();
        String productDirPath = model.getProductDir().getAbsolutePath();
        appContext.getPreferences().setPropertyString(SaveProductAsAction.PREFERENCES_KEY_LAST_PRODUCT_DIR, productDirPath);
        try {
            HashMap<String, Product> sourceProducts = ioParametersPanel.createSourceProductsMap();
            Product currentSourceProduct = sourceProducts.get("recentProduct");
            Product previousSourceProduct = sourceProducts.get("previousProduct");
            ProductManager productManager = appContext.getProductManager();
            Component parentComponent = getJDialog();
            TargetProductSwingWorker worker = new TargetProductSwingWorker(parentComponent, productManager, model, currentSourceProduct,
                    previousSourceProduct, this.parameterSupport.getParameterMap());
            worker.executeWithBlocking(); // start the thread
        } catch (Throwable t) {
            handleInitialisationError(t);
            return;
        }
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
        ioParametersPanel.releaseSourceProductSelectors();
        super.hide();
    }

    @Override
    protected Product createTargetProduct() throws Exception {
        HashMap<String, Product> sourceProducts = this.ioParametersPanel.createSourceProductsMap();
        return GPF.createProduct(this.operatorName, this.parameterSupport.getParameterMap(), sourceProducts);
    }

    void setTargetProductNameSuffix(String suffix) {
        this.targetProductNameSuffix = suffix;
    }

    private void updateTargetProductName(Product product) {
        String productName = "";
        if (product != null) {
            productName = product.getName();
        }
        final TargetProductSelectorModel targetProductSelectorModel = getTargetProductSelector().getModel();
        targetProductSelectorModel.setProductName(productName + getTargetProductNameSuffix());
    }

    private  String getTargetProductNameSuffix() {
        return targetProductNameSuffix;
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
        ArrayList<SourceProductSelector> sourceProductSelectorList = this.ioParametersPanel.getSourceProductSelectorList();
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
                    if (pair.getKey().equals("Product masks")) {
                        JPanel panel = new JPanel(layout);
                        panel.setBorder(BorderFactory.createTitledBorder(pair.getKey()));
                        for (String value : pair.getValue())
                            if (value.equals("currentProductMask")) {
                                final Product selectedProduct = sourceProductSelectorList.get(0).getSelectedProduct();
                                currentProductMaskComboBox = new JComboBox<>(new DefaultComboBoxModel<>());

                                setComponentsPanel("Recent Product Mask", currentProductMaskComboBox, selectedProduct, panel, "currentProductMask");
                            } else if (value.equals("previousProductMask")) {
                                final Product selectedProduct = sourceProductSelectorList.get(2).getSelectedProduct();
                                previousProductMaskComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
                                setComponentsPanel("Previous Product Mask", previousProductMaskComboBox, selectedProduct, panel, "previousProductMask");
                            }
                        parametersPanel.add(panel);
                    } else {
                        parametersPanel.add(createPanel(pair.getKey() + " parameters", this.bindingContext, pair.getValue()));
                    }
                }
            }
        }
    }


    private void setComponentsPanel(String labelString, JComboBox<String> productMaskComboBox, Product selectedProduct, JPanel panel, String bindingProperty) {
        JLabel label = new JLabel(labelString);
        panel.add(label);
        this.bindingContext.bind(bindingProperty, productMaskComboBox);
        panel.add(productMaskComboBox);
        addComboBoxItems(selectedProduct, productMaskComboBox);

    }

    private void processPreviousSelectedProduct(Product product) {
        processComboBox(product, this.previousProductMaskComboBox);
    }

    private void processCurrentSelectedProduct(Product product) {
        processComboBox(product, this.currentProductMaskComboBox);

    }
    private void processComboBox(Product selectedProduct, JComboBox<String> productMaskComboBox) {
        if (productMaskComboBox != null) {
            productMaskComboBox.removeAllItems();
            addComboBoxItems(selectedProduct, productMaskComboBox);
        }
    }

    private void addComboBoxItems(Product selectedProduct, JComboBox<String> productMaskComboBox) {
        if (selectedProduct != null) {
            ProductNodeGroup<Mask> prod = selectedProduct.getMaskGroup();
            if (prod != null) {
                productMaskComboBox.addItem(null);
                for (int index = 0; index < prod.getNodeCount(); index++) {
                    Mask mask = prod.get(index);
                    productMaskComboBox.addItem(mask.getName());
                }
            }
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

    private class TargetProductSwingWorker extends ProgressMonitorSwingWorker<Product, Object> {
        private final ProductManager productManager;
        private final TargetProductSelectorModel model;
        private final Product currentSourceProduct;
        private final Product previousSourceProduct;
        private final Map<String, Object> parameters;

        private long totalTime;

        private TargetProductSwingWorker(Component parentComponent, ProductManager productManager, TargetProductSelectorModel model, Product currentSourceProduct,
                                         Product previousSourceProduct, Map<String, Object> parameters) {

            super(parentComponent, "Run Forest Cover Change");

            this.productManager = productManager;
            this.model = model;
            this.currentSourceProduct = currentSourceProduct;
            this.previousSourceProduct = previousSourceProduct;
            this.parameters = parameters;
            this.totalTime = 0L;
        }

        @Override
        protected Product doInBackground(ProgressMonitor pm) throws Exception {
            pm.beginTask("Running...", this.model.isOpenInAppSelected() ? 100 : 95);

            Product productToReturn = null;
            Product operatorTargetProduct = null;
            try {
                long startTime = System.currentTimeMillis();

                Map<String, Product> sourceProducts = new HashMap<String, Product>();
                sourceProducts.put("recentProduct", this.currentSourceProduct);
                sourceProducts.put("previousProduct", this.previousSourceProduct);

                // create the operator
                Operator operator = GPF.getDefaultInstance().createOperator("ForestCoverChangeOp", this.parameters, sourceProducts, null);

                // execute the operator
                operator.execute(ProgressMonitor.NULL);

                // get the operator target product
                operatorTargetProduct = operator.getTargetProduct();

                productToReturn = operatorTargetProduct;

                if (this.model.isSaveToFileSelected()) {
                    File targetFile = this.model.getProductFile();
                    String formatName = this.model.getFormatName();
                    GPF.writeProduct(operatorTargetProduct, targetFile, formatName, false, false, ProgressMonitor.NULL);

                    productToReturn = ProductIO.readProduct(targetFile);

                    operatorTargetProduct.dispose();
                }

                this.totalTime = System.currentTimeMillis() - startTime;
            } finally {
                pm.done();
                Preferences preferences = SnapApp.getDefault().getPreferences();
                if (preferences.getBoolean(GPF.BEEP_AFTER_PROCESSING_PROPERTY, false)) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            return productToReturn;
        }

        @Override
        protected void done() {
            try {
                final Product targetProduct = get();
                if (this.model.isSaveToFileSelected() && this.model.isOpenInAppSelected()) {
                    this.productManager.addProduct(targetProduct);
                    showSaveAndOpenInAppInfo(this.totalTime);
                } else if (this.model.isOpenInAppSelected()) {
                    this.productManager.addProduct(targetProduct);
                    showOpenInAppInfo();
                } else {
                    showSaveInfo(this.totalTime);
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
