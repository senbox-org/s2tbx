package org.esa.s2tbx.tooladapter.ui;

import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.PropertyPane;
import com.bc.ceres.swing.selection.AbstractSelectionChangeListener;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;
import org.esa.beam.framework.gpf.ui.DefaultIOParametersPanel;
import org.esa.beam.framework.gpf.ui.SourceProductSelector;
import org.esa.beam.framework.gpf.ui.TargetProductSelector;
import org.esa.beam.framework.gpf.ui.TargetProductSelectorModel;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.util.io.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;

/**
 * Date: 13.07.11
 */
public class ExternalToolExecutionForm extends JTabbedPane {
    private AppContext appContext;
    private S2tbxOperatorDescriptor operatorSpi;
    private PropertySet propertySet;
    private TargetProductSelector targetProductSelector;
    private DefaultIOParametersPanel ioParamPanel;

    public ExternalToolExecutionForm(AppContext appContext, S2tbxOperatorDescriptor operatorSpi, PropertySet propertySet,
                                     TargetProductSelector targetProductSelector) {
        this.appContext = appContext;
        this.operatorSpi = operatorSpi;
        this.propertySet = propertySet;
        this.targetProductSelector = targetProductSelector;

        //initialise the target product's directory to the working directory
        final TargetProductSelectorModel targetProductSelectorModel = targetProductSelector.getModel();
        targetProductSelectorModel.setProductDir(this.operatorSpi.getTemporaryFolder());

        //hide the targetProductFile from UI as its value is set differently
        //this.propertySet.getDescriptor("targetProductFile").setAttribute("visible", false);

        ioParamPanel = createIOParamTab();
        addTab("I/O Parameters", ioParamPanel);
        addTab("Processing Parameters", createProcessingParamTab());
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
//        BindingContext bindingContext = parametersPane.getBindingContext();
//        bindingContext.bindEnabledState("inputFactor", true, "includeInputFactor", true);

        //ExternalToolEditorDialog editor = new ExternalToolEditorDialog(operatorSpi, propertySet, false);
        final JPanel parametersPanel = parametersPane.createPanel();//editor.createPanel();
        parametersPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        return new JScrollPane(parametersPanel);
    }

    private class SourceProductChangeListener extends AbstractSelectionChangeListener {

        private static final String TARGET_PRODUCT_NAME_SUFFIX = "_simple";

        @Override
        public void selectionChanged(SelectionChangeEvent event) {
            String productName = "";
            final Product selectedProduct = (Product) event.getSelection().getSelectedValue();
            if (selectedProduct != null) {
                productName = FileUtils.getFilenameWithoutExtension(selectedProduct.getName());
            }
            final TargetProductSelectorModel targetProductSelectorModel = targetProductSelector.getModel();
            targetProductSelectorModel.setProductName(productName + TARGET_PRODUCT_NAME_SUFFIX);
        }
    }

}
