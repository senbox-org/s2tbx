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

package org.esa.s2tbx.dataio.mosaic.ui;

import org.esa.s2tbx.dataio.mosaic.S2tbxMosaicOp;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

class S2tbxConditionsTableAdapter extends S2tbxAbstractTableAdapter {

    S2tbxConditionsTableAdapter(JTable table) {
        super(table);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        final TableModel tableModel = (TableModel) e.getSource();
        final S2tbxMosaicOp.Condition[] conditions = new S2tbxMosaicOp.Condition[tableModel.getRowCount()];
        for (int i = 0; i < conditions.length; i++) {
            conditions[i] = new S2tbxMosaicOp.Condition((String) tableModel.getValueAt(i, 0),
                                                   (String) tableModel.getValueAt(i, 1),
                                                   Boolean.TRUE.equals(tableModel.getValueAt(i, 2)));
        }
        getBinding().setPropertyValue(conditions);
    }

    @Override
    protected final DefaultTableModel createTableModel(int rowCount) {
        return new DefaultTableModel(new String[]{"Name", "Expression", "Output"}, rowCount);
    }
}
