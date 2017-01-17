/*
 *
 *  * Copyright (C) 2016 CS ROMANIA
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.radiometry;

import com.bc.ceres.swing.binding.BindingContext;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.graphbuilder.gpf.ui.BaseOperatorUI;
import org.esa.snap.graphbuilder.gpf.ui.UIValidation;
import org.esa.snap.ui.AppContext;

import javax.swing.*;
import java.util.Map;

/**
 * Graph Builder - compatible UI for radiometric indices.
 *
 * @author Cosmin Cara
 */
public class RadiometricIndicesUI extends BaseOperatorUI {

    private RadiometricIndicesPanel baseUI;

    @Override
    public JComponent CreateOpTab(String operatorName, Map<String, Object> parameterMap, AppContext appContext) {
        initializeOperatorUI(operatorName, parameterMap);
        this.baseUI = new RadiometricIndicesPanel(operatorName,
                                                this.propertySet,
                                                new BindingContext(this.propertySet),
                                                this::getCurrentProduct);
        return this.baseUI.createPanel();
    }

    @Override
    public void initParameters() {
        updateParameters();
    }

    @Override
    public UIValidation validateParameters() {
        return this.baseUI.validateParameters() ?
                new UIValidation(UIValidation.State.OK, "") :
                new UIValidation(UIValidation.State.WARNING, "Product needs to be resampled first");
    }

    @Override
    public void updateParameters() {
        this.baseUI.reactOnChange();
    }

    private Product getCurrentProduct() {
        return sourceProducts != null && sourceProducts.length > 0 ? sourceProducts[0] : null;
    }
}
