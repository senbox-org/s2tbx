package org.esa.s2tbx.tooladapter.ui.utils;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyEditor;
import com.bc.ceres.swing.binding.PropertyEditorRegistry;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;

/**
 * Created by ramonag on 1/16/2015.
 */
public class OperatorDescriptorTableModel extends AbstractTableModel {
    private String[] columnNames = {"name", "description", "label", "datatype", "defaultvalue","unit", "valueset", "pattern", "format", "notnull", "notempty"};
    private Property[] data = new Property[0];
    BindingContext context = null;

    public OperatorDescriptorTableModel(BindingContext context){
        super();
        this.context = context;
        this.data = context.getPropertySet().getProperties();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        PropertyDescriptor descriptor = data[row].getDescriptor();
        switch (col){
            case 0: return descriptor.getName();
            case 1: return descriptor.getDescription();
            case 2: return descriptor.getAlias();
            case 3: return descriptor.getType();
            case 4:
                final PropertyEditorRegistry registry = PropertyEditorRegistry.getInstance();
                PropertyEditor propertyEditor = registry.findPropertyEditor(descriptor);
                return propertyEditor.createEditorComponent(descriptor, context);
            case 5: return descriptor.getUnit();
            case 6: return descriptor.getValueSet();
            case 7: return descriptor.getPattern();
            case 8: return descriptor.getFormat();
            case 9: return descriptor.isNotNull();
            case 10: return descriptor.isNotEmpty();
        }
        return null;
    }

    @Override
    public Class getColumnClass(int c) {
        if(c == 10){
            return JComponent.class;
        } else {
            return JTextField.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        //not yet implemented
        throw new UnsupportedOperationException();
    }

    class DescriptorPropertyCellEditor extends AbstractCellEditor
            implements TableCellEditor,
            ActionListener {
        //Color currentColor;
        JButton button;
        JColorChooser colorChooser;
        JDialog dialog;
        protected static final String EDIT = "edit";

        public DescriptorPropertyCellEditor() {
            /*button = new JButton();
            button.setActionCommand(EDIT);
            button.addActionListener(this);
            button.setBorderPainted(false);

            //Set up the dialog that the button brings up.
            colorChooser = new JColorChooser();
            dialog = JColorChooser.createDialog(button,
                    "Pick a Color",
                    true,  //modal
                    colorChooser,
                    this,  //OK button handler
                    null); //no CANCEL button handler
                    */
        }

        public void actionPerformed(ActionEvent e) {
            /*if (EDIT.equals(e.getActionCommand())) {
                //The user has clicked the cell, so
                //bring up the dialog.
                button.setBackground(currentColor);
                colorChooser.setColor(currentColor);
                dialog.setVisible(true);

                fireEditingStopped(); //Make the renderer reappear.

            } else { //User pressed dialog's "OK" button.
                currentColor = colorChooser.getColor();
            }*/
        }

        //Implement the one CellEditor method that AbstractCellEditor doesn't.
        public Object getCellEditorValue() {
            return null;
        }

        //Implement the one method defined by TableCellEditor.
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            PropertyDescriptor descriptor = ((Property)data[row]).getDescriptor();
            return PropertyEditorRegistry.getInstance().findPropertyEditor(descriptor).createEditorComponent(descriptor, context);
        }
    }
}