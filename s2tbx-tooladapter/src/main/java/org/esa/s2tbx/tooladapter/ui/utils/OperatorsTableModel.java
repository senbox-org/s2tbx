package org.esa.s2tbx.tooladapter.ui.utils;

import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ramona Manda
 */
public class OperatorsTableModel extends AbstractTableModel {

    private String[] columnNames = {"", "Tool name", "Tool description"};
    private boolean[] toolsChecked = null;
    private List<S2tbxOperatorDescriptor> data = null;

    public OperatorsTableModel(List<S2tbxOperatorDescriptor> operators) {
        this.data = operators;
        this.toolsChecked = new boolean[this.data.size()];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return toolsChecked[rowIndex];
            case 1:
                return data.get(rowIndex).getAlias();
            case 2:
                return data.get(rowIndex).getName();
        }
        return "";
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class getColumnClass(int c) {
        if (c == 0) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        //TODO
        this.toolsChecked[row] = (boolean) value;
    }

    public S2tbxOperatorDescriptor getFirstCheckedOperator() {
        for (int i = 0; i < this.toolsChecked.length; i++) {
            if (this.toolsChecked[i]) {
                return this.data.get(i);
            }
        }
        return null;
    }

    public List<S2tbxOperatorDescriptor> getCheckedOperators() {
        List<S2tbxOperatorDescriptor> result = new ArrayList<>();
        for (int i = 0; i < this.toolsChecked.length; i++) {
            if (this.toolsChecked[i]) {
                result.add(this.data.get(i));
            }
        }
        return null;
    }
}
