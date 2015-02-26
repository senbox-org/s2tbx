package org.esa.beam.ui.tooladapter.utils;

import org.esa.beam.framework.gpf.descriptor.ParameterDescriptor;
import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.ToolParameterDescriptor;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ramona Manda
 */
public class OperatorParametersTable extends JTable {

    private static String[] columnNames = {"", "Name", "Description", "Label", "Data type", "Default value", ""};
    private static String[] columnsMembers = {"del", "name", "description", "alias", "dataType", "defaultValue", "edit"};
    private ToolAdapterOperatorDescriptor operator = null;
    private Map<ToolParameterDescriptor, PropertyMemberUIWrapper> propertiesValueUIDescriptorMap;
    private MultiRenderer tableRenderer;

    public OperatorParametersTable(ToolAdapterOperatorDescriptor operator) {
        this.operator = operator;
        propertiesValueUIDescriptorMap = new HashMap<>();

        //List<ToolParameterDescriptor> data = operator.getS2tbxParameterDescriptors();
        List<ToolParameterDescriptor> data = operator.getS2tbxParameterDescriptors();
        for (ToolParameterDescriptor property : data) {
            propertiesValueUIDescriptorMap.put(property, PropertyMemberUIWrapperFactory.buildPropertyWrapper("defaultValue", property, operator, null));
        }
        tableRenderer = new MultiRenderer();
        setModel(new OperatorParametersTableNewTableModel());
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getColumnModel().getColumn(0).setPreferredWidth(27);
        getColumnModel().getColumn(1).setPreferredWidth(120);
        getColumnModel().getColumn(2).setPreferredWidth(200);
        getColumnModel().getColumn(3).setPreferredWidth(80);
        getColumnModel().getColumn(4).setPreferredWidth(150);
        getColumnModel().getColumn(5).setPreferredWidth(150);
        getColumnModel().getColumn(6).setPreferredWidth(40);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return tableRenderer;
    }

    @Override
    public TableCellEditor getCellEditor() {
        return tableRenderer;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        return tableRenderer;
    }

    class OperatorParametersTableNewTableModel extends AbstractTableModel {

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return operator.getS2tbxParameterDescriptors().size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            ToolParameterDescriptor descriptor = operator.getS2tbxParameterDescriptors().get(row);
            switch (column) {
                case 0:
                    return false;
                case 6:
                    return false;
                default:
                    try {
                        return descriptor.getAttribute(columnsMembers[column]);
                    } catch (PropertyAttributeException e) {
                        e.printStackTrace();
                        //TODO
                        return "ERROR!!!";
                    }
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            ToolParameterDescriptor descriptor = operator.getS2tbxParameterDescriptors().get(rowIndex);
            switch (columnIndex) {
                case 0:
                    operator.removeParamDescriptor(descriptor);
                    break;
                case 5:
                    //the custom editor should handle this
                    break;
                case 6:
                    //TODO edit
                    break;
                default:
                    try {
                        descriptor.setAttribute(columnsMembers[columnIndex], aValue == null ? null : aValue.toString());
                    } catch (PropertyAttributeException e) {
                        e.printStackTrace();
                        //TODO
                    }
            }
        }
    }

    class MultiRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        private TableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
        private AbstractButton delButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/DeleteShapeTool16.gif"),
                false);
        private AbstractButton editButton = new JButton("...");

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ParameterDescriptor descriptor = operator.getS2tbxParameterDescriptors().get(row);
            switch (column) {
                case 0:
                    return delButton;
                case 1:
                    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                case 2:
                    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                case 3:
                    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                case 4:
                    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                case 5:
                    try {
                        return propertiesValueUIDescriptorMap.get(descriptor).getUIComponent();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                case 6:
                    return editButton;
                default:
                    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            ParameterDescriptor descriptor = operator.getS2tbxParameterDescriptors().get(row);
            switch (column) {
                case 0:
                    return delButton;
                case 1:
                    return getDefaultEditor(String.class).getTableCellEditorComponent(table, value, isSelected, row, column);
                case 2:
                    return getDefaultEditor(String.class).getTableCellEditorComponent(table, value, isSelected, row, column);
                case 3:
                    return getDefaultEditor(String.class).getTableCellEditorComponent(table, value, isSelected, row, column);
                case 4:
                    return getDefaultEditor(String.class).getTableCellEditorComponent(table, value, isSelected, row, column);
                case 5:
                    try {
                        return propertiesValueUIDescriptorMap.get(descriptor).getUIComponent();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                case 6:
                    return editButton;
                default:
                    return getDefaultEditor(String.class).getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
}
