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

package org.esa.beam.dataio.atmcorr.ui;

import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import com.bc.ceres.swing.selection.SelectionChangeListener;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductFilter;
import org.esa.beam.framework.gpf.ui.SourceProductSelector;
import org.esa.beam.framework.ui.AppContext;

import javax.swing.*;
import java.io.File;

/**
 * @author Tonio Fincke
 */
public class AtmCorrIOParametersPanel extends JPanel {

    private final SourceProductSelector sourceProductSelector;
    private final AtmCorrTargetProductSelector targetProductSelector;

    public AtmCorrIOParametersPanel(AppContext appContext) {
        final ProductFilter productFilter = new L1CSourceProductFilter();
        sourceProductSelector = new SourceProductSelector(appContext);
        sourceProductSelector.setProductFilter(productFilter);
        sourceProductSelector.initProducts();
        sourceProductSelector.getProductNameLabel().setText("Name:");
        sourceProductSelector.getProductNameComboBox().setToolTipText("A Sentinel2 L1C product");
        sourceProductSelector.addSelectionChangeListener(new SelectionChangeListener() {
            @Override
            public void selectionChanged(SelectionChangeEvent event) {
                updateTargetProductName();
            }

            @Override
            public void selectionContextChanged(SelectionChangeEvent event) {
            }
        });

        targetProductSelector = new AtmCorrTargetProductSelector(new AtmCorrTargetProductSelectorModel());
        targetProductSelector.getOpenInAppCheckBox().setText("Open in " + appContext.getApplicationName());
        targetProductSelector.getProductDirChooserButton().setVisible(false);
        targetProductSelector.getProductDirTextField().setEditable(false);
        targetProductSelector.getProductNameTextField().setEditable(false);
        updateTargetProductName();
        final TableLayout tableLayout = new TableLayout(1);
        tableLayout.setTableAnchor(TableLayout.Anchor.WEST);
        tableLayout.setTableWeightX(1.0);
        tableLayout.setTableFill(TableLayout.Fill.HORIZONTAL);
        tableLayout.setTablePadding(3, 3);

        setLayout(tableLayout);
        add(sourceProductSelector.createDefaultPanel());
        add(targetProductSelector.createDefaultPanel());
        add(tableLayout.createVerticalSpacer());
    }

    private void updateTargetProductName() {
        if (sourceProductSelector.getSelectedProduct() != null) {
            String sourceName = sourceProductSelector.getSelectedProduct().getName();
            String sourceDir = sourceProductSelector.getSelectedProduct().getFileLocation().getPath();
            String targetDir = sourceDir.replace("1C", "2A");
            targetDir = targetDir.replace("OPER", "USER");
            targetProductSelector.getModel().setProductDir(new File(targetDir));
            targetProductSelector.getProductDirTextField().setToolTipText(targetDir);
            String targetName = sourceName.replace("1C", "2A");
            targetName = targetName.replace("OPER", "USER");
            targetProductSelector.getModel().setProductName(targetName);
            targetProductSelector.getProductNameTextField().setToolTipText(targetName);
        } else {
            targetProductSelector.getModel().setProductName("Please select Source Product...");
        }
    }

    public Product getSourceProduct() {
        return sourceProductSelector.getSelectedProduct();
    }

    public String getTargetDir() {
        return targetProductSelector.getModel().getProductDir().getPath();
    }

    public String getTargetName() {
        return targetProductSelector.getModel().getProductName();
    }

    public boolean shallBeOpenedInApp() {
        return targetProductSelector.getModel().isOpenInAppSelected();
    }

    private static class L1CSourceProductFilter implements ProductFilter {

        @Override
        public boolean accept(Product product) {
            String productName = product.getName();
            return true;
            // return S2Config.PRODUCT_DIRECTORY_1C_PATTERN.matcher(productName).matches() ||
            //        S2Config.DIRECTORY_1C_PATTERN_ALT.matcher(productName).matches();
        }
    }

}
