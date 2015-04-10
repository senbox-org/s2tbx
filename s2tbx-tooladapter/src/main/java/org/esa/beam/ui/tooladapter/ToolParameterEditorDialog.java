package org.esa.beam.ui.tooladapter;

import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.internal.CheckBoxEditor;
import com.bc.ceres.swing.binding.internal.TextFieldEditor;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.esa.beam.framework.gpf.descriptor.ToolParameterDescriptor;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.ui.tooladapter.utils.PropertyMemberUIWrapper;
import org.esa.beam.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author Ramona Manda
 */
public class ToolParameterEditorDialog extends ModalDialog {

    private ToolParameterDescriptor parameter;
    private ToolParameterDescriptor oldParameter;
    private PropertyContainer container;
    private BindingContext context;
    private PropertyMemberUIWrapper uiWrapper;
    private JComponent editorComponent;
    private JPanel mainPanel;

    private static final BidiMap typesMap;

    static{
        typesMap = new DualHashBidiMap();
        typesMap.put("String", String.class);
        typesMap.put("File", File.class);
        typesMap.put("Integer", Integer.class);
        typesMap.put("List", String[].class);
        typesMap.put("Boolean", Boolean.class);
    }


    public ToolParameterEditorDialog(AppContext appContext, String title, String helpID, ToolParameterDescriptor parameter, PropertyMemberUIWrapper uiWrapper) {
        super(appContext.getApplicationWindow(), parameter.getName(), ID_OK_CANCEL, helpID);
        this.oldParameter = parameter;
        this.parameter = new ToolParameterDescriptor(parameter);
        this.uiWrapper = uiWrapper;
        setContent(createMainPanel());
        getJDialog().setPreferredSize(new Dimension(500, 500));
    }

    public JPanel createMainPanel(){
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[]{100, 390};

        mainPanel = new JPanel(layout);

        container = PropertyContainer.createObjectBacked(parameter);
        context = new BindingContext(container);

        addTextPropertyEditor(mainPanel, "Name: ", "name", parameter.getName(), 0);
        addTextPropertyEditor(mainPanel, "Alias: ", "alias", parameter.getAlias(), 1);


        //dataType
        mainPanel.add(new JLabel("Type"), getConstraints(2, 0, 1));
        JComboBox comboEditor = new JComboBox(typesMap.keySet().toArray());
        comboEditor.setSelectedItem(typesMap.getKey(parameter.getDataType()));
        comboEditor.addActionListener(e -> {
            JComboBox cb = (JComboBox) e.getSource();
            String typeName = (String) cb.getSelectedItem();
            if (!parameter.getDataType().equals((Class<?>) typesMap.get(typeName))) {
                parameter.setDataType((Class<?>) typesMap.get(typeName));
                //editor must updated
                try {
                    if(editorComponent != null) {
                        mainPanel.remove(editorComponent);
                    }
                    editorComponent = uiWrapper.reloadUIComponent((Class<?>) typesMap.get(typeName));
                    mainPanel.add(editorComponent, getConstraints(3, 1, 1));
                    mainPanel.revalidate();
                } catch (Exception e1) {
                    //TODO
                    e1.printStackTrace();
                }
            }
        });
        mainPanel.add(comboEditor, getConstraints(2, 1, 1));

        //defaultValue
        mainPanel.add(new JLabel("Default value"), getConstraints(3, 0, 1));
        try {
            editorComponent = uiWrapper.getUIComponent();
            mainPanel.add(editorComponent, getConstraints(3, 1, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        addTextPropertyEditor(mainPanel, "Description: ", "description", parameter.getDescription(), 4);
        addTextPropertyEditor(mainPanel, "Label: ", "label", parameter.getLabel(), 5);
        addTextPropertyEditor(mainPanel, "Unit: ", "unit", parameter.getUnit(), 6);
        addTextPropertyEditor(mainPanel, "Interval: ", "interval", parameter.getInterval(), 7);
        addTextPropertyEditor(mainPanel, "Value set: ", "valueSet", StringUtils.join(parameter.getValueSet(), ","), 8);
        addTextPropertyEditor(mainPanel, "Condition: ", "condition", parameter.getCondition(), 9);
        addTextPropertyEditor(mainPanel, "Pattern: ", "pattern", parameter.getPattern(), 10);
        addTextPropertyEditor(mainPanel, "Format: ", "format", parameter.getFormat(), 11);
        addBoolPropertyEditor(mainPanel, "Not null", "notNull", parameter.isNotNull(), 12);
        addBoolPropertyEditor(mainPanel, "Not empty", "notEmpty", parameter.isNotEmpty(), 13);
        addTextPropertyEditor(mainPanel, "ItemAlias: ", "itemAlias", parameter.getItemAlias(), 14);
        addBoolPropertyEditor(mainPanel, "Deprecated", "deprecated", parameter.isDeprecated(), 15);

        return mainPanel;
    }

    private JComponent addTextPropertyEditor(JPanel parent, String label, String propertyName, String value, int line){
        parent.add(new JLabel(label), getConstraints(line, 0, 1));
        PropertyDescriptor propertyDescriptor = container.getDescriptor(propertyName);
        TextFieldEditor textEditor = new TextFieldEditor();
        JComponent editorComponent = textEditor.createEditorComponent(propertyDescriptor, context);
        ((JTextField) editorComponent).setText(value);
        parent.add(editorComponent, getConstraints(line, 1, 1));
        return editorComponent;
    }

    private JComponent addBoolPropertyEditor(JPanel parent, String label, String propertyName, Boolean value, int line){
        parent.add(new JLabel(label), getConstraints(line, 1, 1));
        PropertyDescriptor propertyDescriptor = container.getDescriptor(propertyName);
        CheckBoxEditor boolEditor = new CheckBoxEditor();
        JComponent editorComponent = boolEditor.createEditorComponent(propertyDescriptor, context);
        ((JCheckBox) editorComponent).setSelected(value);
        editorComponent.setPreferredSize(new Dimension(30, 30));
        GridBagConstraints constraints = getConstraints(line, 0, 1);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor  = GridBagConstraints.LINE_END;
        parent.add(editorComponent, constraints);
        return editorComponent;
    }

    private GridBagConstraints getConstraints(int row, int col, int noCells) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = col;
        c.gridy = row;
        if(noCells != -1){
            c.gridwidth = noCells;
        }
        c.insets = new Insets(2, 10, 2, 10);
        return c;
    }

    protected void onOK(){
        super.onOK();
        if(parameter.getName() != null) {
            oldParameter.setName(parameter.getName());
        }
        if(parameter.getAlias() != null) {
            oldParameter.setAlias(parameter.getAlias());
        }
        if(parameter.getDataType() != null) {
            oldParameter.setDataType(parameter.getDataType());
        }
        if(parameter.getDefaultValue() != null) {
            oldParameter.setDefaultValue(parameter.getDefaultValue());
        }
        if(parameter.getDescription() != null) {
            oldParameter.setDescription(parameter.getDescription());
        }
        if(parameter.getLabel() != null) {
            oldParameter.setLabel(parameter.getLabel());
        }
        if(parameter.getUnit() != null) {
            oldParameter.setUnit(parameter.getUnit());
        }
        if(parameter.getInterval() != null) {
            oldParameter.setInterval(parameter.getInterval());
        }
        if(parameter.getValueSet() != null) {
            oldParameter.setValueSet(parameter.getValueSet());
        }
        if(parameter.getCondition() != null) {
            oldParameter.setCondition(parameter.getCondition());
        }
        if(parameter.getPattern() != null) {
            oldParameter.setPattern(parameter.getPattern());
        }
        if(parameter.getFormat() != null) {
            oldParameter.setFormat(parameter.getFormat());
        }
        oldParameter.setNotNull(parameter.isNotNull());
        oldParameter.setNotEmpty(parameter.isNotEmpty());
        if(parameter.getRasterDataNodeClass() != null) {
            oldParameter.setRasterDataNodeClass(parameter.getRasterDataNodeClass());
        }
        if(parameter.getValidatorClass() != null) {
            oldParameter.setValidatorClass(parameter.getValidatorClass());
        }
        if(parameter.getConverterClass() != null) {
            oldParameter.setConverterClass(parameter.getConverterClass());
        }
        if(parameter.getDomConverterClass() != null) {
            oldParameter.setDomConverterClass(parameter.getDomConverterClass());
        }
        if(parameter.getItemAlias() != null) {
            oldParameter.setItemAlias(parameter.getItemAlias());
        }
        oldParameter.setDeprecated(parameter.isDeprecated());
        oldParameter.setParameterType(parameter.getParameterType());
    }

}
