/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.mosaic;

import com.bc.ceres.swing.binding.BindingContext;
import org.esa.snap.core.gpf.ui.TargetProductSelector;
import org.esa.snap.ui.AppContext;

import javax.swing.*;
import java.util.Map;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class S2tbxMosaicForm extends JTabbedPane {

    private final AppContext appContext;
    private final S2tbxMosaicFormModel mosaicModel;
    private S2tbxMosaicIOPanel ioPanel;
    private S2tbxMosaicMapProjectionPanel mapProjectionPanel;

    public S2tbxMosaicForm(TargetProductSelector targetProductSelector, AppContext appContext) {
        this.appContext = appContext;
        mosaicModel = new S2tbxMosaicFormModel(this);
        createUI(targetProductSelector);
    }

    public S2tbxMosaicForm(TargetProductSelector targetProductSelector, AppContext appContext, Map<String, Object> parameterMap) {
        this.appContext = appContext;
        mosaicModel = new S2tbxMosaicFormModel(this, parameterMap);
        createUI(targetProductSelector);
    }
    private void createUI(TargetProductSelector selector) {
        ioPanel = null;
        mapProjectionPanel = new S2tbxMosaicMapProjectionPanel(appContext, mosaicModel);
        S2tbxMosaicExpressionsPanel expressionsPanel = new S2tbxMosaicExpressionsPanel(appContext, mosaicModel);
        if (selector != null) {
            ioPanel = new S2tbxMosaicIOPanel(appContext, mosaicModel, selector);
            addTab("I/O Parameters", ioPanel); /*I18N*/
        }
        addTab("Map Projection Definition", mapProjectionPanel); /*I18N*/
        addTab("Variables", expressionsPanel);  /*I18N*/
    }


    S2tbxMosaicFormModel getFormModel() {
        return mosaicModel;
    }

    public BindingContext getBindingContext() {
        return mapProjectionPanel.getBindingContext();
    }

    void prepareShow() {
        if (ioPanel != null) {
            ioPanel.prepareShow();
        }
        mapProjectionPanel.prepareShow();
    }

    void prepareHide() {
        mapProjectionPanel.prepareHide();
        if (ioPanel != null) {
            ioPanel.prepareHide();
        }
    }

    void setCardinalBounds(double southBoundValue, double northBoundValue, double westBoundValue, double eastBoundValue) {
        mapProjectionPanel.getBindingContext().getPropertySet().setValue(S2tbxMosaicFormModel.PROPERTY_SOUTH_BOUND, southBoundValue);
        mapProjectionPanel.getBindingContext().getPropertySet().setValue(S2tbxMosaicFormModel.PROPERTY_NORTH_BOUND, northBoundValue);
        mapProjectionPanel.getBindingContext().getPropertySet().setValue(S2tbxMosaicFormModel.PROPERTY_WEST_BOUND, westBoundValue);
        mapProjectionPanel.getBindingContext().getPropertySet().setValue(S2tbxMosaicFormModel.PROPERTY_EAST_BOUND, eastBoundValue);
    }

}
