package org.esa.s2tbx.mapper;

import com.bc.ceres.binding.PropertySet;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.graphbuilder.gpf.ui.OperatorUIUtils;
import org.esa.snap.ui.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Core UI class for radiometric indices.
 *
 * @author Adrian Drăghici
 */
class SpectralAngleMapperPanel {

    private final SpectralAngleMapperFormModel samModel;
    private SpectralAngleMapperParametersPanel parametersPanel;
    private Product currentProduct;
    private Map<String, Object> paramMap;

    private JScrollPane operatorPanel;
    private Callable<Product> sourceProductAccessor;

    SpectralAngleMapperPanel(AppContext appContext, PropertySet propertySet, Map<String, Object> paramMap, Callable<Product> productAccessor) {
        this.samModel = new SpectralAngleMapperFormModel(propertySet, paramMap);
        this.paramMap = paramMap;
        this.sourceProductAccessor = productAccessor;
        SpectralAngleMapperThresholdPanel thresholdPanel = new SpectralAngleMapperThresholdPanel(this.samModel);
        this.parametersPanel = new SpectralAngleMapperParametersPanel(appContext, this.samModel, productAccessor, thresholdPanel);
        thresholdPanel.setPreferredSize(new Dimension(640, 100));
        this.parametersPanel.add(thresholdPanel);
        this.operatorPanel = new JScrollPane(this.parametersPanel);
    }

    SpectralAngleMapperFormModel getFormModel() {
        return this.samModel;
    }

    JComponent createPanel() {
        return this.operatorPanel;
    }

    void reactOnChange() {
        if (isInputProductChanged() && this.currentProduct != null) {
            this.parametersPanel.updateBands(this.currentProduct);
        }
        OperatorUIUtils.updateParamList(this.parametersPanel.getSourceBandNames(), this.paramMap, SpectralAngleMapperFormModel.REFERENCE_BANDS_PROPERTY);
    }

    private Product getSourceProduct() {
        try {
            return this.sourceProductAccessor.call();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isInputProductChanged() {
        Product sourceProduct = getSourceProduct();
        if (sourceProduct != this.currentProduct) {
            this.currentProduct = sourceProduct;
            return true;
        } else {
            return false;
        }
    }
}
