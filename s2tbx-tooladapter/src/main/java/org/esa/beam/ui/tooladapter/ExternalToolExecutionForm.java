package org.esa.beam.ui.tooladapter;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.PropertyPane;
import com.bc.ceres.swing.selection.AbstractSelectionChangeListener;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterConstants;
import org.esa.beam.framework.gpf.ui.DefaultIOParametersPanel;
import org.esa.beam.framework.gpf.ui.SourceProductSelector;
import org.esa.beam.framework.gpf.ui.TargetProductSelector;
import org.esa.beam.framework.gpf.ui.TargetProductSelectorModel;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.util.io.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.File;
import java.util.ArrayList;

/**
 * Date: 13.07.11
 */
public class ExternalToolExecutionForm extends JTabbedPane {
    private AppContext appContext;
    private ToolAdapterOperatorDescriptor operatorSpi;
    private PropertySet propertySet;
    private TargetProductSelector targetProductSelector;
    private DefaultIOParametersPanel ioParamPanel;
    private String fileExtension;

    public ExternalToolExecutionForm(AppContext appContext, ToolAdapterOperatorDescriptor operatorSpi, PropertySet propertySet,
                                     TargetProductSelector targetProductSelector) {
        this.appContext = appContext;
        this.operatorSpi = operatorSpi;
        this.propertySet = propertySet;
        this.targetProductSelector = targetProductSelector;

        //before executing, the sourceProduct and sourceProductFile must be removed from the list, since they cannot be edited
        Property sourceProperty = this.propertySet.getProperty(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_FILE);
        if(sourceProperty != null) {
            this.propertySet.removeProperty(sourceProperty);
        }
        sourceProperty = this.propertySet.getProperty(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID);
        if(sourceProperty != null) {
            this.propertySet.removeProperty(sourceProperty);
        }

        //initialise the target product's directory to the working directory
        final TargetProductSelectorModel targetProductSelectorModel = targetProductSelector.getModel();
        targetProductSelectorModel.setProductDir(this.operatorSpi.getWorkingDir());

        ioParamPanel = createIOParamTab();
        addTab("I/O Parameters", ioParamPanel);
        addTab("Processing Parameters", createProcessingParamTab());
        updateTargetProductFields();
    }

    public void prepareShow() {
        ioParamPanel.initSourceProductSelectors();
    }

    public void prepareHide() {
        ioParamPanel.releaseSourceProductSelectors();
    }

    public Product getSourceProduct() {
        return ioParamPanel.getSourceProductSelectorList().get(0).getSelectedProduct();
    }

    public File getTargetProductFile() {
        return targetProductSelector.getModel().getProductFile();
    }

    private DefaultIOParametersPanel createIOParamTab() {
        final DefaultIOParametersPanel ioPanel = new DefaultIOParametersPanel(appContext, operatorSpi,
                targetProductSelector);
        final ArrayList<SourceProductSelector> sourceProductSelectorList = ioPanel.getSourceProductSelectorList();
        if (!sourceProductSelectorList.isEmpty()) {
            final SourceProductSelector sourceProductSelector = sourceProductSelectorList.get(0);
            sourceProductSelector.addSelectionChangeListener(new SourceProductChangeListener());
        }
        return ioPanel;
    }

    private JScrollPane createProcessingParamTab() {

        PropertyPane parametersPane = new PropertyPane(propertySet);
        final JPanel parametersPanel = parametersPane.createPanel();
        parametersPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        return new JScrollPane(parametersPanel);
    }

    private void updateTargetProductFields() {
        File file = new File(propertySet.getProperty(ToolAdapterConstants.TOOL_TARGET_PRODUCT_FILE).getValueAsText());
        String productName = FileUtils.getFilenameWithoutExtension(file);
        if (fileExtension == null) {
            fileExtension = FileUtils.getExtension(file);
        }
        TargetProductSelectorModel model = targetProductSelector.getModel();
        model.setProductName(productName);
        model.setSaveToFileSelected(false);
        targetProductSelector.getProductDirTextField().setEnabled(false);
    }

    private class SourceProductChangeListener extends AbstractSelectionChangeListener {

        private static final String TARGET_PRODUCT_NAME_SUFFIX = "_processed";

        @Override
        public void selectionChanged(SelectionChangeEvent event) {
            String productName = "";
            final Product selectedProduct = (Product) event.getSelection().getSelectedValue();
            if (selectedProduct != null) {
                productName = FileUtils.getFilenameWithoutExtension(selectedProduct.getName());
            }
            final TargetProductSelectorModel targetProductSelectorModel = targetProductSelector.getModel();
            productName += TARGET_PRODUCT_NAME_SUFFIX;
            targetProductSelectorModel.setProductName(productName);
            Property targetProperty = propertySet.getProperty(ToolAdapterConstants.TOOL_TARGET_PRODUCT_FILE);
            Object value = targetProperty.getValue();
            File oldValue = value instanceof File ? (File) value : new File((String) value);
            propertySet.setValue(ToolAdapterConstants.TOOL_TARGET_PRODUCT_FILE, new File(oldValue.getParentFile().getAbsolutePath(), productName + fileExtension));
        }
    }

}
