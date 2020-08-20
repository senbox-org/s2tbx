package org.esa.s2tbx.fcc;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyPane;
import com.bc.ceres.swing.progress.ProgressMonitorSwingWorker;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductManager;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorSpi;
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
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.landcover.dataio.LandCoverFactory;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.rcp.actions.file.SaveProductAsAction;
import org.esa.snap.ui.AppContext;
import org.esa.snap.ui.ModalDialog;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import static com.bc.ceres.swing.TableLayout.cell;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class ForestCoverChangeTargetProductDialog extends SingleTargetProductDialog {

    private static final String RECENT_PRODUCT_PROPERTY = "recentProduct";
    private static final String PREVIOUS_PRODUCT_PROPERTY = "previousProduct";
    private static final String CURRENT_PRODUCT_SOURCE_MASK = "currentProductSourceMaskFile";
    private static final String PREVIOUS_PRODUCT_SOURCE_MASK = "previousProductSourceMaskFile";
    private static final String LAND_COVER_NAME = "landCoverName";
    private static final String LAND_COVER_MAP_INDICES = "landCoverMapIndices";

    private static final int CURRENT_PRODUCT = 0;
    private static final int PREVIOUS_PRODUCT = 1;

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
    private JComboBox<String> landCoverNamesComboBox;
    private JTextField landCoverMapIndices;

    public ForestCoverChangeTargetProductDialog(String operatorName, AppContext appContext, String title, String helpID) {
        super(appContext, title, ID_APPLY_CLOSE, helpID);

        if (StringUtils.isNullOrEmpty(operatorName)) {
            throw new NullPointerException("The operator name is null or empty.");
        }
        OperatorSpi operatorSpi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(operatorName);
        if (operatorSpi == null) {
            throw new IllegalArgumentException("No SPI found for operator name '" + operatorName + "'");
        }

        this.operatorName = operatorName;
        this.targetProductNameSuffix = "";

        TargetProductSelector selector = getTargetProductSelector();
        selector.getModel().setSaveToFileSelected(false);
        selector.getSaveToFileCheckBox().setEnabled(true);

        processAnnotationsRec(ForestCoverChangeOp.class);

        OperatorDescriptor baseOperatorDescriptor = operatorSpi.getOperatorDescriptor();
        ParameterDescriptor[] params = this.parameterDescriptors.toArray(new ParameterDescriptor[0]);
        SourceProductDescriptor[] sourceProducts = this.sourceProductDescriptors.toArray(new SourceProductDescriptor[0]);
        this.operatorDescriptor = new OperatorDescriptorClass(baseOperatorDescriptor, params, sourceProducts);

        this.ioParametersPanel = new DefaultIOParametersPanel(getAppContext(), this.operatorDescriptor, getTargetProductSelector(), true);

        this.parameterSupport = new OperatorParameterSupport(this.operatorDescriptor);
        ArrayList<SourceProductSelector> sourceProductSelectorList = this.ioParametersPanel.getSourceProductSelectorList();
        PropertySet propertySet = this.parameterSupport.getPropertySet();
        this.bindingContext = new BindingContext(propertySet);

        SelectionChangeListener currentListenerProduct = new SelectionChangeListener() {
            public void selectionChanged(SelectionChangeEvent event) {
                Product product = sourceProductSelectorList.get(CURRENT_PRODUCT).getSelectedProduct();
                if (product != null) {
                    updateTargetProductName(product);
                }
            }
            public void selectionContextChanged(SelectionChangeEvent event) {
            }
        };

        sourceProductSelectorList.get(CURRENT_PRODUCT).addSelectionChangeListener(currentListenerProduct);
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
            Product currentSourceProduct = sourceProducts.get(RECENT_PRODUCT_PROPERTY);
            Product previousSourceProduct = sourceProducts.get(PREVIOUS_PRODUCT_PROPERTY);
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
    protected boolean verifyUserInput() {
        PropertySet propertySet = bindingContext.getPropertySet();
        String pattern = "[0-9]+([ ]*,[ ]*[0-9]*)*";
        Object landCoverIndicesProperty = propertySet.getValue(LAND_COVER_MAP_INDICES);
        String indices = landCoverIndicesProperty.toString();
        if (!indices.matches(pattern)) {
            showErrorDialog("Invalid land cover map forest indices.");
            return false;
        }

        ArrayList<SourceProductSelector> sourceProductSelectorList = this.ioParametersPanel.getSourceProductSelectorList();
        Product currentProduct = sourceProductSelectorList.get(CURRENT_PRODUCT).getSelectedProduct();
        Product previousProduct = sourceProductSelectorList.get(PREVIOUS_PRODUCT).getSelectedProduct();
        Object currentProductSourceMask = propertySet.getValue(CURRENT_PRODUCT_SOURCE_MASK);
        Object previousProductSourceMask = propertySet.getValue(PREVIOUS_PRODUCT_SOURCE_MASK);
        String message;
        String information;
        ModalDialog modalDialog = new ModalDialog(null, "Forest Cover Change Information", ModalDialog.ID_OK_CANCEL, "");
        if ((currentProduct != null) && (previousProduct != null)) {
            if (ForestCoverChangeOp.isSentinelProduct(currentProduct) && currentProductSourceMask == null
                    && ForestCoverChangeOp.isSentinelProduct(previousProduct) && previousProductSourceMask == null) {

                message = "Products " + currentProduct.getName() + " and " + previousProduct.getName() + " are of type Sentinel 2." ;
                information =  "The forest cover change output product  will take in consideration the cloud masks from these products.";

                modalDialog.setContent(getModalDialogContent(message, information));
                final int show = modalDialog.show();
                if (show == ModalDialog.ID_CANCEL) {
                    return false;
                }
            } else if (ForestCoverChangeOp.isSentinelProduct(currentProduct) && currentProductSourceMask == null
                    && ForestCoverChangeOp.isSentinelProduct(previousProduct) && previousProductSourceMask != null) {

                message = "Product " + currentProduct.getName() + " is of type Sentinel 2.";
                information = "The forest cover change output product  will take in consideration the cloud masks from this product.";
                modalDialog.setContent(getModalDialogContent(message, information));
                final int show = modalDialog.show();
                if (show == ModalDialog.ID_CANCEL) {
                    return false;
                }
            } else if (ForestCoverChangeOp.isSentinelProduct(currentProduct) && currentProductSourceMask != null
                    && ForestCoverChangeOp.isSentinelProduct(previousProduct) && previousProductSourceMask == null) {

                message = "Product " + previousProduct.getName() + " is of type Sentinel 2.";
                information = "The forest cover change output product  will take in consideration the cloud masks from this product.";
                modalDialog.setContent(getModalDialogContent(message, information));
                final int show = modalDialog.show();
                if (show == ModalDialog.ID_CANCEL) {
                    return false;
                }
            }
        }
        return true;
    }

    private JPanel getModalDialogContent(String message, String information){
        final TableLayout layout = new TableLayout(1);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.BOTH);
        layout.setTableWeightX(1.0);
        layout.setTableWeightY(0.0);
        layout.setRowWeightY(1, 1.0);
        layout.setTablePadding(3, 3);
        JPanel content = new JPanel(layout);
        content.setBorder(new EmptyBorder(4, 4, 4, 4));
        content.add(new JLabel(message));
        content.add(new JLabel(information));
        return content;
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
                    if (pair.getKey().equals("Land Cover")) {
                        List<String> names = Arrays.asList(LandCoverFactory.getNameList());
                        Comparator<String> comparator = new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return o1.compareToIgnoreCase(o2);
                            }
                        };
                        Collections.sort(names, comparator);
                        String[] landCoverNames = names.toArray(new String[names.size()]);
                        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<String>(landCoverNames);
                        this.landCoverNamesComboBox = new JComboBox<String>(comboBoxModel);
                        this.bindingContext.bind(LAND_COVER_NAME, this.landCoverNamesComboBox);

                        this.landCoverMapIndices = new JTextField();
                        this.bindingContext.bind(LAND_COVER_MAP_INDICES, this.landCoverMapIndices);

                        this.landCoverNamesComboBox.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent event) {
                                try {
                                    landCoverMapIndices.setText("");
                                    bindingContext.getPropertySet().getProperty(LAND_COVER_MAP_INDICES).setValue("");
                                } catch (ValidationException e) {
                                }
                            }
                        });

                        List<JComponent[]> componentsList = new ArrayList<>();
                        JComponent[] firstRow = new JComponent[] {this.landCoverNamesComboBox, new JLabel("Name")};
                        JComponent[] secondRow = new JComponent[] {this.landCoverMapIndices, new JLabel("Map Indices")};
                        componentsList.add(firstRow);
                        componentsList.add(secondRow);
                        ParametersPanel panel = new ParametersPanel();
                        panel.populate(componentsList);
                        panel.setBorder(BorderFactory.createTitledBorder(pair.getKey()));
                        parametersPanel.add(panel);
                    } else {
                        parametersPanel.add(createPanel(pair.getKey() + " parameters", this.bindingContext, pair.getValue()));
                    }
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
        PropertyContainer propertyContainer = new PropertyContainer();
        for (String parameter: parameters) {
            Property prop = bindingContext.getPropertySet().getProperty(parameter);
            propertyContainer.addProperty(prop);
        }

        boolean displayUnitColumn = ParametersPanel.wantDisplayUnitColumn(propertyContainer.getProperties());
        ParametersPanel panel = new ParametersPanel(displayUnitColumn);
        panel.populate(propertyContainer);
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
        return new OperatorMenu(getJDialog(), this.operatorDescriptor, this.parameterSupport, getAppContext(), getHelpID());
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
                sourceProducts.put(RECENT_PRODUCT_PROPERTY, this.currentSourceProduct);
                sourceProducts.put(PREVIOUS_PRODUCT_PROPERTY, this.previousSourceProduct);

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
