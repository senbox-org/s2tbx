package org.esa.s2tbx.tooladapter.ui.utils;

import org.esa.beam.framework.gpf.descriptor.DefaultParameterDescriptor;
import org.esa.beam.framework.gpf.descriptor.ParameterDescriptor;
import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.S2tbxParameterDescriptor;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ramonag on 1/16/2015.
 */
public class OperatorParametersTable extends JPanel{

    private static String[] columnNames = {"", "Name", "Description", "Label", "Data type", "Default value", "Unit", "Value set", "Pattern", "Format", "Not null", "Not empty"};
    private static String[] columnsMembers = {"del", "name", "description", "alias", "dataType", "defaultValue", "unit", "valueSet", "pattern", "format", "notNull", "notEmpty"};
    private static int[] columnWeights = {30, 150, 200, 70, 200, 250, 50, 50, 80, 80, 70, 70};
    private S2tbxOperatorDescriptor operator = null;
    private JLabel errorLabel = new JLabel("");
    private Map<S2tbxParameterDescriptor, PropertyUIDescriptor> propertiesUIDescriptorMap;
    private AbstractButton newButton;
    private TableCallBackAfterComponentEdit callback = new TableCallBackAfterComponentEdit(this);

    public OperatorParametersTable(S2tbxOperatorDescriptor operator) {
        super();
        this.operator = operator;
        buildPanel();
    }

    public TableCallBackAfterComponentEdit getCallback() {
        return callback;
    }

    private JPanel buildPanel() {
        propertiesUIDescriptorMap = new HashMap<>();

        newButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/New24.gif"),
                false);
        newButton.addActionListener(new NewPropertyActionListener(this));

        //List<S2tbxParameterDescriptor> data = operator.getS2tbxParameterDescriptors();
        ParameterDescriptor[] data = operator.getParameterDescriptors();
        for (ParameterDescriptor property : data) {
            S2tbxParameterDescriptor newParamDescriptor = new S2tbxParameterDescriptor((DefaultParameterDescriptor)property);
            operator.addParamDescriptor(newParamDescriptor);
            PropertyUIDescriptor descriptor = PropertyUIDescriptor.buildUIDescriptor(newParamDescriptor, columnsMembers, operator, new DeleteActionListener(newParamDescriptor, this), callback);
            propertiesUIDescriptorMap.put(newParamDescriptor, descriptor);
        }
        errorLabel.setForeground(Color.RED);

        rearrangeComponents();
        return this;
    }

    private void rearrangeComponents(){
        removeAll();
        GridBagLayout layout = new GridBagLayout();
        //layout.columnWidths = columnWeights;
        JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        GridBagConstraints c = new GridBagConstraints();
        //c.fill = GridBagConstraints.HORIZONTAL;
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        c.gridx = 0;
        c.gridy = 0;
        panel.add(newButton, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = columnNames.length;
        c.weighty = 20;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(errorLabel, c);

        c.gridwidth = 1;
        c.gridy++;
        c.gridx = -1;
        for (String columnMember : columnNames) {
            c.gridx++;
            c.weightx = columnWeights[c.gridx];
            JLabel label = new JLabel(columnMember);
            label.setPreferredSize(new Dimension(columnWeights[c.gridx], 20));
            label.setBorder(BorderFactory.createLineBorder(Color.black));
            panel.add(label, c);
        }

        for (S2tbxParameterDescriptor descriptor : operator.getS2tbxParameterDescriptors()) {
            c.gridy++;
            c.gridx = -1;
            for(String col : columnsMembers){
                Component uiComponent = null;
                if(col.equals("del")){
                    uiComponent = propertiesUIDescriptorMap.get(descriptor).getDelButton();
                } else {
                    try {
                        uiComponent = propertiesUIDescriptorMap.get(descriptor).getUIcomponentsMap().get(col).getUIComponent();
                    } catch (Exception e) {
                        //TODO
                        e.printStackTrace();
                    }
                }
                if(uiComponent == null){
                    //TODO
                    uiComponent = new JLabel("error!!!");
                }
                c.gridx++;
                c.weightx = columnWeights[c.gridx];
                uiComponent.setPreferredSize(new Dimension(columnWeights[c.gridx], 20));
                panel.add(uiComponent, c);
            }
        }
        add(new JScrollPane(panel), BorderLayout.NORTH);
        //add(panel, BorderLayout.NORTH);
        if(this.isVisible()) {
            repaint();
            revalidate();
        }
    }
/*
    private JPanel buildPanel(BindingContext contextParam) {
        uiComponentsMap = new HashMap<>();
        newButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/New24.gif"),
                false);
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Property prop = Property.create("DefaultOperatorName", "DefaultValue");
                contextParam.getPropertySet().addProperty(prop);
                uiComponentsMap.put(prop, buildPropertyComponents(prop));
                rearrangeComponents();
            }
        });

        Property[] data = contextParam.getPropertySet().getProperties();
        for (Property property : data) {
            uiComponentsMap.put(property, buildPropertyComponents(property));
        }

        errorLabel.setForeground(Color.RED);

        rearrangeComponents();
        return this;
    }

    private void rearrangeComponents(){
        removeAll();
        GridBagLayout layout = new GridBagLayout();
        //layout.columnWidths = columnWeights;
        JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        GridBagConstraints c = new GridBagConstraints();
        //c.fill = GridBagConstraints.HORIZONTAL;
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        c.gridx = 0;
        c.gridy = 0;
        panel.add(newButton, c);

        c.gridy++;
        c.gridx = 0;
        //c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(errorLabel, c);

        c.gridy++;
        c.gridx = -1;
        for (String columnMember : columnNames) {
            c.gridx++;
            //c.weightx = columnWeights[c.gridx];
            JLabel label = new JLabel(columnMember);
            label.setPreferredSize(new Dimension(columnWeights[c.gridx], 20));
            label.setBorder(BorderFactory.createLineBorder(Color.black));
            panel.add(label, c);
        }

        for (Property property : contextParam.getPropertySet().getProperties()) {
            PropertyDescriptor descriptor = property.getDescriptor();
            if (isInvisible(descriptor)) {
                continue;
            }
            c.gridy++;
            c.gridx = -1;
            for (JComponent component : uiComponentsMap.get(property)) {
                c.gridx++;
                component.setPreferredSize(new Dimension(columnWeights[c.gridx], 20));
                //c.weightx = columnWeights[c.gridx];
                panel.add(component, c);
            }
        }
        add(new JScrollPane(panel), BorderLayout.NORTH);
        //add(panel, BorderLayout.NORTH);
        if(this.isVisible()) {
            repaint();
            revalidate();
        }
    }

    private List<JComponent> buildPropertyComponents(Property property){
        PropertyDescriptor descriptor = property.getDescriptor();
        PropertyEditorRegistry registry = PropertyEditorRegistry.getInstance();
        List<JComponent> propertyComponents = new ArrayList<JComponent>();
        for (String columnMember : columnsMembers) {
            if (columnMember.equals("del")) {
                AbstractButton delButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/DeleteShapeTool16.gif"),
                        false);
                delButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        contextParam.getPropertySet().removeProperty(property);
                        uiComponentsMap.remove(property);
                        rearrangeComponents();
                    }
                });
                propertyComponents.add(delButton);
                continue;
            }
            if (columnMember.equals("defaultValue")) {
                PropertyEditor propertyEditor = registry.findPropertyEditor(descriptor);
                JComponent editorComponent = propertyEditor.createEditorComponent(descriptor, contextParam);
                propertyComponents.add(editorComponent);
                continue;
            }
            Object value = descriptor.getAttribute(columnMember);
            //string attributes go to JTextField, also dataType
            if (value instanceof String || columnMember.equals("type") || columnMember.equals("name")) {
                String valueToShow;
                if (columnMember.equals("name")) {
                    valueToShow = descriptor.getName();
                } else if (columnMember.equals("type")) {
                    valueToShow = descriptor.getType().getCanonicalName();
                } else if(value == null){
                    valueToShow = "";
                } else  {
                    valueToShow = value.toString();
                }
                JTextField editableTextField = new JTextField(valueToShow);
                editableTextField.addFocusListener(new PropertyFocusListener(editableTextField, columnMember, descriptor));
                propertyComponents.add(editableTextField);
                continue;
            }
            //bool attributes go to JRadioButton
            Boolean boolValue = null;
            try {
                boolValue = (boolean) value;
            } catch (Exception ex) {
            }
            if(boolValue != null || columnMember.equals("notNull") || columnMember.equals("notEmpty")) {
                JCheckBox editableRadio = new JCheckBox();
                if(boolValue != null){
                    editableRadio.setSelected(boolValue);
                }
                editableRadio.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        descriptor.setAttribute(columnMember, editableRadio.isEnabled() ? true : false);
                    }
                });
                propertyComponents.add(editableRadio);
                continue;
            }
            //textfield for the rest!
            if (value == null) {
                value = "";
            }
            //add
            JTextField anotherTextField = new JTextField(value.toString());
            anotherTextField.addFocusListener(new PropertyFocusListener(anotherTextField, columnMember, descriptor));
            propertyComponents.add(anotherTextField);
        }
        return propertyComponents;
    }
*/

    /*class PropertyFocusListener implements FocusListener {
        private JTextField editableTextField;
        private String columnMember;
        private PropertyDescriptor descriptor;

        public PropertyFocusListener(JTextField editableTextField, String columnMember, PropertyDescriptor descriptor) {
            this.editableTextField = editableTextField;
            this.columnMember = columnMember;
            this.descriptor = descriptor;
        }

        @Override
        public void focusGained(FocusEvent e) {

        }

        @Override
        public void focusLost(FocusEvent e) {
            if (!e.isTemporary()) {
                String content = editableTextField.getText();
                if (columnMember.equals("name")) {
                    if(content.length() == 0){
                        errorLabel.setText("Name of the property cannot be empty");
                        return;
                    }
                    PropertyDescriptor newdescriptor = new PropertyDescriptor(content, descriptor.getType());
                    for (String prop : descriptor.getPropertySetDescriptor().getPropertyNames()) {
                        newdescriptor.setAttribute(prop, descriptor.getAttribute(prop));
                    }
                    descriptor = newdescriptor;
                } else if (columnMember.equals("type")) {
                    try {
                        if(content.length() == 0){errorLabel.setText("Type of the property cannot be empty");}
                        Class<?> cls = Class.forName(content);
                        PropertyDescriptor newdescriptor = new PropertyDescriptor(descriptor.getName(), cls);
                        for (String prop : descriptor.getPropertySetDescriptor().getPropertyNames()) {
                            newdescriptor.setAttribute(prop, descriptor.getAttribute(prop));
                        }
                        descriptor = newdescriptor;

                    } catch (ClassNotFoundException ex) {
                        errorLabel.setText("Class " + content + " not found: " + ex.getMessage());
                    }
                } else {
                    try {
                        descriptor.setAttribute(columnMember, content);
                    } catch (Exception ex) {
                        errorLabel.setText("Could not set the value " + content + " to the property : " + columnMember + "; the message is: " + ex.getMessage());
                    }
                }
                //SwingUtilities.invokeLater(new FocusGrabber(editableTextField));
            }
        }
    }
*/
    public void removePropertyFromTable(S2tbxParameterDescriptor property){
        this.operator.removeParamDescriptor(property);

        this.propertiesUIDescriptorMap.remove(property);
        if(this.isVisible()){
            rearrangeComponents();
        }
    }

    public void addNewPropertyToTable(){
        int i = 1;
        while(operator.getFirstParamDescriptorByName("DefaultOperatorName" + i) != null){
            i++;
        }
        S2tbxParameterDescriptor descriptor = new S2tbxParameterDescriptor("DefaultOperatorName" + i, String.class);
        //Property property = Property.create("DefaultOperatorName" + i, (String)"DefaultValue");
        //contextParam.getPropertySet().addProperty(property);
        operator.addParamDescriptor(descriptor);

        PropertyUIDescriptor UIdescriptor = PropertyUIDescriptor.buildUIDescriptor((S2tbxParameterDescriptor) descriptor, OperatorParametersTable.columnsMembers, operator, new DeleteActionListener(descriptor, this), callback);
        propertiesUIDescriptorMap.put(descriptor, UIdescriptor);
        if(this.isVisible()){
            rearrangeComponents();
        }
    }

    class DeleteActionListener implements ActionListener{

        S2tbxParameterDescriptor descriptor;
        OperatorParametersTable table;

        public DeleteActionListener(S2tbxParameterDescriptor descriptor, OperatorParametersTable table){
            this.descriptor = descriptor;
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            table.removePropertyFromTable(descriptor);
        }
    }

    class TableCallBackAfterComponentEdit implements PropertyMemberUIWrapper.CallBackAfterEdit{

        OperatorParametersTable table;
        boolean hasLastFieldLostFocus = false;

        public TableCallBackAfterComponentEdit(OperatorParametersTable table){
            this.table = table;
        }

        @Override
        public void doCallBack(PropertyAttributeException exception, S2tbxParameterDescriptor oldProp, S2tbxParameterDescriptor newProp, String attributeName) {
            if (exception != null) {
                table.errorLabel.setText(exception.getMessage());
                //if(!hasLastFieldLostFocus) {//the first lost focus, from the component who had error
                 //   hasLastFieldLostFocus = true;
                //}
            } else {
                //if(hasLastFieldLostFocus) {//the second lost focus, from the second component which lost focus because of the wrong component who gained
                //    hasLastFieldLostFocus = false;
                //} else {
                    table.errorLabel.setText("   ");
                    PropertyMemberUIWrapper wrapper = propertiesUIDescriptorMap.get(oldProp).getUIcomponentsMap().get(attributeName);
                    if (wrapper.memberUIComponentNeedsRevalidation()) {

                    }
                    if (wrapper.propertyUIComponentsNeedsRevalidation()) {
                        //do something with table line corresponding to property!
                        PropertyUIDescriptor descriptor = PropertyUIDescriptor.buildUIDescriptor(oldProp, columnsMembers, table.operator, new DeleteActionListener(oldProp, table), table.callback);
                        table.propertiesUIDescriptorMap.put(oldProp, descriptor);
                        table.rearrangeComponents();
                    }
                    if (wrapper.contextUIComponentsNeedsRevalidation()) {
                        //do something with table line corresponding to property!
                        for (S2tbxParameterDescriptor property : table.operator.getS2tbxParameterDescriptors()) {
                            PropertyUIDescriptor descriptor = PropertyUIDescriptor.buildUIDescriptor(property, columnsMembers, table.operator, new DeleteActionListener(property, table), table.callback);
                            table.propertiesUIDescriptorMap.put(property, descriptor);
                            table.rearrangeComponents();
                        }
                    }
                //}
            }
        }
    }

    class NewPropertyActionListener implements ActionListener{

        OperatorParametersTable table;

        public NewPropertyActionListener(OperatorParametersTable table){
            this.table = table;
        }

            @Override
            public void actionPerformed(ActionEvent e) {
                table.addNewPropertyToTable();
            }

    }
}