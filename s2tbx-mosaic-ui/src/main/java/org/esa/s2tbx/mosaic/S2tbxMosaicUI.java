package org.esa.s2tbx.mosaic;

import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.common.MosaicOp;
import org.esa.snap.graphbuilder.gpf.ui.BaseOperatorUI;
import org.esa.snap.graphbuilder.gpf.ui.UIValidation;
import org.esa.snap.ui.AppContext;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class S2tbxMosaicUI extends BaseOperatorUI {

    private static final String VARIABLES_PROPERTY_NAME = "variables";

    private S2tbxMosaicForm s2tbxMosaicForm;

    @Override
    public JComponent CreateOpTab(String operatorName, Map<String, Object> parameterMap, AppContext appContext) {
        initializeOperatorUI(operatorName, parameterMap);
        this.paramMap.clear();
        this.s2tbxMosaicForm = new S2tbxMosaicForm(null, appContext, this.paramMap);
        this.s2tbxMosaicForm.prepareShow();
        initParameters();
        return new JScrollPane(this.s2tbxMosaicForm);
    }

    @Override
    public void initParameters() {
        if (!hasSourceProducts()) return;
        List<File> productFiles = new ArrayList<>();
        for (Product product : this.sourceProducts) {
            productFiles.add(product.getFileLocation());
        }
        try {
            this.s2tbxMosaicForm.getFormModel().setSourceProducts(productFiles.toArray(new File[0]));
        } catch (IOException e) {
            Logger.getLogger(S2tbxMosaicUI.class.getName()).severe("S2tbxMosaicUI: failed to set source products: " + e.getMessage());
        }
    }

    @Override
    public UIValidation validateParameters() {
        if (this.s2tbxMosaicForm == null) {
            return new UIValidation(UIValidation.State.ERROR, "UI not initialised");
        }
        if(this.sourceProducts!=null) {
            Object variables = s2tbxMosaicForm.getFormModel().getParameterMap().get(VARIABLES_PROPERTY_NAME);
            if (variables == null || !(variables instanceof MosaicOp.Variable[]) || ((MosaicOp.Variable[]) variables).length < 1) {
                return new UIValidation(UIValidation.State.ERROR, " Variables must be defined");
            }
        }
        return new UIValidation(UIValidation.State.OK, "");
    }

    @Override
    public void updateParameters() {

    }
}
