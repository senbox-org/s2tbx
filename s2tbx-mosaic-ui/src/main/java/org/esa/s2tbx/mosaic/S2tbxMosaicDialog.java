/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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
import com.bc.ceres.swing.binding.Enablement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.dataop.barithm.BandArithmetic;
import org.esa.snap.core.dataop.dem.ElevationModelDescriptor;
import org.esa.snap.core.dataop.dem.ElevationModelRegistry;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.common.MosaicOp;
import org.esa.snap.core.gpf.ui.OperatorMenu;
import org.esa.snap.core.gpf.ui.OperatorParameterSupport;
import org.esa.snap.core.gpf.ui.SingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.TargetProductSelector;
import org.esa.snap.core.jexp.ParseException;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.ui.AppContext;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.geom.AffineTransform;
import java.util.Map;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

class S2tbxMosaicDialog extends SingleTargetProductDialog {

    private final S2tbxMosaicForm form;

    S2tbxMosaicDialog(final String title, final String helpID, AppContext appContext) {
        super(appContext, title, ID_APPLY_CLOSE, helpID);
        final TargetProductSelector selector = getTargetProductSelector();
        selector.getModel().setSaveToFileSelected(false);
        selector.getModel().setProductName("Multi-size Mosaic");
        selector.getSaveToFileCheckBox().setEnabled(true);
        form = new S2tbxMosaicForm(selector, appContext);

        final OperatorSpi operatorSpi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi("Multi-size Mosaic");

        S2tbxMosaicFormModel formModel = form.getFormModel();
        OperatorParameterSupport parameterSupport = new OperatorParameterSupport(operatorSpi.getOperatorDescriptor(),
                formModel.getPropertySet(),
                formModel.getParameterMap(),
                null);
        OperatorMenu operatorMenu = new OperatorMenu(this.getJDialog(),
                operatorSpi.getOperatorDescriptor(),
                parameterSupport,
                appContext,
                helpID);
        getJDialog().setJMenuBar(operatorMenu.createDefaultMenu());
    }

    @Override
    protected boolean verifyUserInput() {
        final S2tbxMosaicFormModel mosaicModel = form.getFormModel();

        if(!verifiyCompatibilityProducts(mosaicModel)){
            return false;
        }
        if (!verifySourceProducts(mosaicModel)) {
            return false;
        }
        if (!verifyTargetCrs(mosaicModel)) {
            return false;
        }
        if (!verifyVariablesAndConditions(mosaicModel)) {
            return false;
        }

        if (mosaicModel.isUpdateMode() && mosaicModel.getUpdateProduct() == null) {
            showErrorDialog("No product to update specified.");
            return false;
        }
        final String productName = getTargetProductSelector().getModel().getProductName();
        if (!mosaicModel.isUpdateMode() && StringUtils.isNullOrEmpty(productName)) {
            showErrorDialog("No name for the target product specified.");
            return false;
        }
        final boolean varsNotSpecified = mosaicModel.getVariables() == null || mosaicModel.getVariables().length == 0;
        final boolean condsNotSpecified =
                mosaicModel.getConditions() == null || mosaicModel.getConditions().length == 0;
        if (varsNotSpecified && condsNotSpecified) {
            showErrorDialog("No variables specified.");
            return false;
        }
        return verifyDEM(mosaicModel);
    }



    @Override
    protected Product createTargetProduct() throws Exception {
        final S2tbxMosaicFormModel formModel = form.getFormModel();
        final Map<String, Object> parameterMap = formModel.getParameterMap();
        boolean multiSize=false;
        for(Product product:formModel.getSourceProductMap().values() )
            if(product.isMultiSize()){
                multiSize = true;
                break;
            }
        if(!multiSize){
            return GPF.createProduct("Mosaic", parameterMap, formModel.getSourceProductMap());
        }
        return GPF.createProduct("Multi-size Mosaic", parameterMap, formModel.getSourceProductMap());
    }

    @Override
    public int show() {
        form.prepareShow();
        setContent(form);
        int result = super.show();
        setDisabledState();
        return result;
    }

    private void setDisabledState() {
        final BindingContext bindingCtx = form.getBindingContext();
        bindingCtx.bindEnabledState("pixelSizeX", false, enablePixelSize(true));
        bindingCtx.bindEnabledState("pixelSizeY", false, enablePixelSize(true));
    }

    private static Enablement.Condition enablePixelSize(final boolean state) {
        return new Enablement.Condition() {
            @Override
            public boolean evaluate(BindingContext bindingContext) {
                if(state){
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void hide() {
        form.prepareHide();
        super.hide();
    }

    private boolean verifyVariablesAndConditions(S2tbxMosaicFormModel mosaicModel) {
        final Map<String, Product> sourceProductMap = mosaicModel.getSourceProductMap();
        final MosaicOp.Variable[] variables = mosaicModel.getVariables();
        final MosaicOp.Condition[] conditions = mosaicModel.getConditions();
        for (Map.Entry<String, Product> entry : sourceProductMap.entrySet()) {
            final String productIdentifier = entry.getKey();
            final Product product = entry.getValue();
            if (variables != null) {
                for (MosaicOp.Variable variable : variables) {
                    if (!isExpressionValidForProduct(variable.getName(), variable.getExpression(), productIdentifier, product)) {
                        return false;
                    }
                }
            }
            if (conditions != null) {
                for (MosaicOp.Condition condition : conditions) {
                    if (!isExpressionValidForProduct(condition.getName(), condition.getExpression(), productIdentifier, product)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean verifiyCompatibilityProducts(S2tbxMosaicFormModel mosaicModel) {
            Product modelProduct = mosaicModel.getSourceProductMap().values().iterator().next();
            for (Product product : mosaicModel.getSourceProductMap().values()) {
                if (modelProduct.getNumBands() != product.getNumBands()) {
                    String msg = "Source product: '" + modelProduct.getName() + " is not compatible with product " + product.getName() + ".\n Different bands in products";
                    showErrorDialog(msg);
                    return false;
                }
                for (int index = 0; index < product.getNumBands(); index++) {
                    if (!modelProduct.getBandAt(index).getName().equals(product.getBandAt(index).getName())) {
                        String msg = "Source product: '" + modelProduct.getName() + " is not compatible with product " + product.getName() + ".\n Band " +
                                modelProduct.getBandAt(index).getName() + " does not appear in product " + modelProduct.getName();
                        showErrorDialog(msg);
                        return false;
                    }
                    AffineTransform affTransformModelProduct = modelProduct.getBandAt(index).getSourceImage().getModel().getImageToModelTransform(0);
                    AffineTransform affTransformTestProduct =  product.getBandAt(index).getSourceImage().getModel().getImageToModelTransform(0);
                    if ((affTransformModelProduct.getScaleX() != affTransformTestProduct.getScaleX()) ||
                            (affTransformModelProduct.getScaleY() != affTransformTestProduct.getScaleY())) {
                        String msg = "Source product: " + modelProduct.getName() + " is not compatible with product " + product.getName() +
                                ".\n Band " + modelProduct.getBandAt(index).getName() + " has a different pixel size for product " + product.getName();
                        showErrorDialog(msg);
                        return false;
                    }
                }
        }
        return true;
    }

    private boolean isExpressionValidForProduct(String expressionName, String expression, String productIdentifier, Product product) {
        try {
            BandArithmetic.parseExpression(expression, new Product[]{product}, 0);
            return true;
        } catch (ParseException e) {
            final String msg = String.format("Expression '%s' is invalid for product '%s'.\n%s",
                    expressionName,
                    productIdentifier,
                    e.getMessage());
            showErrorDialog(msg);
            e.printStackTrace();
            return false;
        }
    }

    private boolean verifyTargetCrs(S2tbxMosaicFormModel formModel) {
        try {
            final CoordinateReferenceSystem crs = formModel.getTargetCRS();
            if (crs == null) {
                showErrorDialog("No 'Coordinate Reference System' selected.");
                return false;
            }
        } catch (FactoryException e) {
            e.printStackTrace();
            showErrorDialog("No 'Coordinate Reference System' selected.\n" + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean verifySourceProducts(S2tbxMosaicFormModel formModel) {
        final Map<String, Product> sourceProductMap = formModel.getSourceProductMap();
        if (sourceProductMap == null || sourceProductMap.isEmpty()) {
            showErrorDialog("No source products specified.");
            return false;
        }
        return true;
    }

    private boolean verifyDEM(S2tbxMosaicFormModel formModel) {
        String externalDemName = formModel.getElevationModelName();
        if (externalDemName != null) {
            final ElevationModelRegistry elevationModelRegistry = ElevationModelRegistry.getInstance();
            final ElevationModelDescriptor demDescriptor = elevationModelRegistry.getDescriptor(externalDemName);
            if (demDescriptor == null) {
                showErrorDialog("The DEM '" + externalDemName + "' is not supported.");
                return false;
            }
        }
        return true;
    }
}
