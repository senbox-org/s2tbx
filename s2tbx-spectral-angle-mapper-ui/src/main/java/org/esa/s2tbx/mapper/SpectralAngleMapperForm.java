package org.esa.s2tbx.mapper;

import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.SourceProductSelector;
import org.esa.snap.core.gpf.ui.TargetProductSelector;
import org.esa.snap.ui.AppContext;
import javax.swing.JTabbedPane;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Dumitrascu Razvan.
 */

class SpectralAngleMapperForm extends JTabbedPane {

    private static final int CURRENT_PRODUCT = 0;
    private final AppContext appContext;
    private final SpectralAngleMapperFormModel samModel;
    private final OperatorDescriptor operatorDescriptor;
    private final TargetProductSelector targetProductSelector;
    private DefaultIOParametersPanel ioParametersPanel;
    private SpectralAngleMapperParametersPanel parametersPanel;
    private SpectralAngleMapperThresholdPanel thresholdPanel;

    SpectralAngleMapperForm(OperatorDescriptor operatorDescriptor, AppContext appContext, TargetProductSelector targetProductSelector ) {
        this.appContext = appContext;
        this.samModel = new SpectralAngleMapperFormModel(this);
        this.operatorDescriptor = operatorDescriptor;
        this.targetProductSelector = targetProductSelector;
        init();
        createUI();
    }
    private void init(){
        ioParametersPanel = new DefaultIOParametersPanel(appContext, operatorDescriptor, targetProductSelector, true);
        ArrayList<SourceProductSelector> sourceProductSelectorList = this.ioParametersPanel.getSourceProductSelectorList();
        parametersPanel = new SpectralAngleMapperParametersPanel(this, appContext, samModel);
        thresholdPanel = new SpectralAngleMapperThresholdPanel(samModel);
        SelectionChangeListener currentListenerProduct = new SelectionChangeListener() {
            public void selectionChanged(SelectionChangeEvent event) {
                Product product = sourceProductSelectorList.get(CURRENT_PRODUCT).getSelectedProduct();
                parametersPanel.updateBands(product);
            }
            public void selectionContextChanged(SelectionChangeEvent event) {
            }
        };
        sourceProductSelectorList.get(CURRENT_PRODUCT).addSelectionChangeListener(currentListenerProduct);
    }

    private void createUI() {
        addTab("I/O Parameters", ioParametersPanel);
        addTab("SAM Parameters", parametersPanel);
        addTab("Thresholds ", thresholdPanel);
    }

     SpectralAngleMapperThresholdPanel getThresholdPanelInstance() {
         return this.thresholdPanel;
     }

    SpectralAngleMapperFormModel getFormModel() {
        return samModel;
    }

    void prepareShow() {
        ioParametersPanel.initSourceProductSelectors();
    }

    void prepareHide() {
        ioParametersPanel.releaseSourceProductSelectors();
    }

    Map<String,Product> getSourceProductMap() {
        return ioParametersPanel.createSourceProductsMap();
    }
}
