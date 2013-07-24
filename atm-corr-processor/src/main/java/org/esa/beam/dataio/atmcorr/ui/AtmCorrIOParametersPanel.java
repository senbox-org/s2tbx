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
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductFilter;
import org.esa.beam.framework.gpf.ui.SourceProductSelector;
import org.esa.beam.framework.gpf.ui.TargetProductSelector;
import org.esa.beam.framework.ui.AppContext;

import javax.swing.JPanel;
import java.util.regex.Pattern;

/**
 * @author Tonio Fincke
 */
public class AtmCorrIOParametersPanel extends JPanel {

    private final SourceProductSelector sourceProductSelector;

    public AtmCorrIOParametersPanel(AppContext appContext, TargetProductSelector targetProductSelector) {
        final ProductFilter productFilter = new L1CSourceProductFilter();
        sourceProductSelector = new SourceProductSelector(appContext);
        sourceProductSelector.setProductFilter(productFilter);
        sourceProductSelector.initProducts();
        sourceProductSelector.getProductNameLabel().setText("Name:");
        sourceProductSelector.getProductNameComboBox().setToolTipText("A Sentinel2 L1C product");

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

    public Product getSourceProduct() {
        return sourceProductSelector.getSelectedProduct();
    }

    private static class L1CSourceProductFilter implements ProductFilter {
        final static Pattern L1CProductPattern = Pattern.compile("(S2.?)_([A-Z]{4})_MTD_(DMP|SAF)(L1C)_R([0-9]{3})_.*.");
        final static Pattern L1CTilePattern = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_L1C_TL_.*");

        @Override
        public boolean accept(Product product) {
            return L1CProductPattern.matcher(product.getName()).matches() ||
                    L1CTilePattern.matcher(product.getName()).matches();
        }
    }

}
