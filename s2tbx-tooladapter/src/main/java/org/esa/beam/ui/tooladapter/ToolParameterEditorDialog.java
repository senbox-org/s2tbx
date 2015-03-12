        package org.esa.beam.ui.tooladapter;

        import com.bc.ceres.binding.PropertyContainer;
        import com.bc.ceres.binding.PropertyDescriptor;
        import com.bc.ceres.swing.binding.BindingContext;
        import com.bc.ceres.swing.binding.internal.TextFieldEditor;
        import org.apache.commons.collections.BidiMap;
        import org.apache.commons.collections.bidimap.DualHashBidiMap;
        import org.esa.beam.framework.gpf.descriptor.ToolParameterDescriptor;
        import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterConstants;
        import org.esa.beam.framework.ui.AppContext;
        import org.esa.beam.framework.ui.ModelessDialog;
        import org.esa.beam.ui.tooladapter.utils.PropertyMemberUIWrapper;

        import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.io.File;
        import java.util.*;

        /**
 * @author Ramona Manda
 */
public class ToolParameterEditorDialog extends ModelessDialog {

    private ToolParameterDescriptor parameter;
    private PropertyContainer container;
    private BindingContext context;
    private PropertyMemberUIWrapper uiWrapper;
            private
            JComponent editorComponent;
            private JPanel mainPanel;

    private static final BidiMap typesMap;

            static{
                typesMap = new DualHashBidiMap();
                typesMap.put("String", String.class);
                typesMap.put("File", File.class);
                typesMap.put("Integer", Integer.class);
                typesMap.put("List", java.util.List.class);
                typesMap.put("Boolean", Boolean.class);
            }


    public ToolParameterEditorDialog(AppContext appContext, String title, String helpID, ToolParameterDescriptor parameter, PropertyMemberUIWrapper uiWrapper) {
        super(appContext.getApplicationWindow(), title, ID_APPLY_CLOSE, helpID);
        this.parameter = new ToolParameterDescriptor(parameter);
        this.uiWrapper = uiWrapper;
        setContent(createMainPanel());
    }

    public JPanel createMainPanel(){
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[]{100, 315};

        mainPanel = new JPanel(layout);

        container = PropertyContainer.createObjectBacked(parameter);
        context = new BindingContext(container);

        addPropertyEditor(mainPanel, "Name: ", "name", parameter.getName(), 0);
        addPropertyEditor(mainPanel, "Alias: ", "alias", parameter.getAlias(), 1);


        //dataType
        mainPanel.add(new JLabel("Type"), getConstraints(2, 0));
        JComboBox comboEditor = new JComboBox(typesMap.keySet().toArray());
        comboEditor.setSelectedItem(typesMap.getKey(parameter.getDataType()));
        comboEditor.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            String typeName = (String)cb.getSelectedItem();
            parameter.setDataType((Class<?>)typesMap.get(typeName));
            //editor must updated
            try {
                mainPanel.remove(editorComponent);
                editorComponent = uiWrapper.reloadUIComponent((Class<?>)typesMap.get(typeName));
                mainPanel.add(editorComponent, getConstraints(3, 1));
                mainPanel.revalidate();
            } catch (Exception e1) {
                //TODO
                e1.printStackTrace();
            }
        });
        mainPanel.add(comboEditor, getConstraints(2, 1));

        //defaultValue
        mainPanel.add(new JLabel("Default value"), getConstraints(3, 0));
        PropertyDescriptor propertyDescriptor = container.getDescriptor("name");
        try {
            editorComponent = uiWrapper.getUIComponent();
            mainPanel.add(editorComponent, getConstraints(3, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        addPropertyEditor(mainPanel, "Description: ", "description", parameter.getDescription(), 4);
        addPropertyEditor(mainPanel, "label: ", "label", parameter.getLabel(), 5);
        addPropertyEditor(mainPanel, "unit: ", "unit", parameter.getUnit(), 6);
        addPropertyEditor(mainPanel, "interval: ", "interval", parameter.getInterval(), 7);
        //valueSet
        addPropertyEditor(mainPanel, "condition: ", "condition", parameter.getCondition(), 8);
        addPropertyEditor(mainPanel, "pattern: ", "pattern", parameter.getPattern(), 9);
        addPropertyEditor(mainPanel, "format: ", "format", parameter.getFormat(), 10);
        //notNull
        //notEmpty
        addPropertyEditor(mainPanel, "itemAlias: ", "itemAlias", parameter.getName(), 11);
        //deprecated

        return mainPanel;

    }

    private void addPropertyEditor(JPanel parent, String label, String propertyName, String value, int line){
        parent.add(new JLabel(label), getConstraints(line, 0));
        PropertyDescriptor propertyDescriptor = container.getDescriptor(propertyName);
        TextFieldEditor textEditor = new TextFieldEditor();
        JComponent editorComponent = textEditor.createEditorComponent(propertyDescriptor, context);
        ((JTextField) editorComponent).setText(value);
        parent.add(editorComponent, getConstraints(line, 1));
    }

    private GridBagConstraints getConstraints(int row, int col) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = col;
        c.gridy = row;
        if (col == 1) {
            c.gridwidth = 1;
        }
        c.insets = new Insets(2, 10, 2, 10);
        return c;
    }

}
