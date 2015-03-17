package org.esa.beam.ui.tooladapter;

import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.internal.TextFieldEditor;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.esa.beam.framework.gpf.descriptor.ToolParameterDescriptor;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;
import org.esa.beam.ui.tooladapter.utils.PropertyMemberUIWrapper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

 /**
 * @author Ramona Manda
 */
public class ToolParameterEditorDialog extends ModelessDialog {

    private ToolParameterDescriptor parameter;
    private PropertyContainer container;
    private BindingContext context;
    private PropertyMemberUIWrapper uiWrapper;
    private JComponent editorComponent;
    private JPanel mainPanel;
    private JTextArea fileContentArea;
    private AbstractButton editFileButton;
    private JScrollPane scrollFileContent;
    private Boolean fileParamIsEdited = false;

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
        getJDialog().setPreferredSize(new Dimension(500, 400));
    }

    public JPanel createMainPanel(){
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[]{100, 315, 30};

        mainPanel = new JPanel(layout);

        container = PropertyContainer.createObjectBacked(parameter);
        context = new BindingContext(container);

        addPropertyEditor(mainPanel, "Name: ", "name", parameter.getName(), 0);
        addPropertyEditor(mainPanel, "Alias: ", "alias", parameter.getAlias(), 1);


        //dataType
        mainPanel.add(new JLabel("Type"), getConstraints(2, 0, 1));
        JComboBox comboEditor = new JComboBox(typesMap.keySet().toArray());
        comboEditor.setSelectedItem(typesMap.getKey(parameter.getDataType()));
        comboEditor.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            String typeName = (String)cb.getSelectedItem();
            if(!parameter.getDataType().equals((Class<?>)typesMap.get(typeName))) {
                parameter.setDataType((Class<?>) typesMap.get(typeName));
                //editor must updated
                try {
                    mainPanel.remove(editorComponent);
                    editorComponent = uiWrapper.reloadUIComponent((Class<?>) typesMap.get(typeName));
                    mainPanel.add(editorComponent, getConstraints(3, 1, 1));
                    mainPanel.revalidate();
                } catch (Exception e1) {
                    //TODO
                    e1.printStackTrace();
                }
                if(parameter.getDataType().equals(File.class)){
                    refreshFileContentArea();
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
        editFileButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Edit24.gif"),
                false);
        editFileButton.addActionListener(e -> {
            fileParamIsEdited = !fileParamIsEdited;
            if(scrollFileContent != null){
                if(fileParamIsEdited) {
                    scrollFileContent.setVisible(true);
                    getJDialog().setPreferredSize(new Dimension(500, 700));
                } else {
                    scrollFileContent.setVisible(false);
                    getJDialog().setPreferredSize(new Dimension(500, 400));
                }
                getJDialog().pack();
            }
        });
        editFileButton.setEnabled(false);
        mainPanel.add(editFileButton, getConstraints(3, 2, 1));
        fileContentArea = new JTextArea(10, 10);
        scrollFileContent = new JScrollPane(fileContentArea);
        scrollFileContent.setPreferredSize(new Dimension(430, 295));
        scrollFileContent.setMinimumSize(new Dimension(430, 295));
        mainPanel.add(scrollFileContent, getConstraints(4, 0, 3));
        scrollFileContent.setVisible(false);
        refreshFileContentArea();

        addPropertyEditor(mainPanel, "Description: ", "description", parameter.getDescription(), 5);
        addPropertyEditor(mainPanel, "Label: ", "label", parameter.getLabel(), 6);
        addPropertyEditor(mainPanel, "Unit: ", "unit", parameter.getUnit(), 7);
        addPropertyEditor(mainPanel, "interval: ", "interval", parameter.getInterval(), 8);
        //valueSet
        addPropertyEditor(mainPanel, "Condition: ", "condition", parameter.getCondition(), 9);
        addPropertyEditor(mainPanel, "Pattern: ", "pattern", parameter.getPattern(), 10);
        addPropertyEditor(mainPanel, "Format: ", "format", parameter.getFormat(), 11);
        //notNull
        //notEmpty
        addPropertyEditor(mainPanel, "ItemAlias: ", "itemAlias", parameter.getName(), 12);
        //deprecated

        return mainPanel;
    }

    private void refreshFileContentArea(){
        if(parameter.getDataType().equals(File.class)){
            editFileButton.setEnabled(true);
            fileContentArea.setText("");
            String fileName = parameter.getDefaultValue();
            File paramFile = new File(fileName);
            if (paramFile.exists()) {
                byte[] encoded = new byte[0];
                try {
                    encoded = Files.readAllBytes(Paths.get(fileName));
                } catch (IOException e) {
                    //TODO!!!
                    fileContentArea.setText("ERROR!!!");
                    e.printStackTrace();
                }
                String content = new String(encoded, Charset.defaultCharset());
                fileContentArea.setText(content);
                fileContentArea.setCaretPosition(0);
            }
            if(fileParamIsEdited) {
                scrollFileContent.setVisible(true);
                getJDialog().setPreferredSize(new Dimension(500, 700));
            }
        } else {
            editFileButton.setEnabled(false);
            scrollFileContent.setVisible(false);
            fileContentArea.setText("");
            getJDialog().setPreferredSize(new Dimension(500, 400));
        }
    }

    private void addPropertyEditor(JPanel parent, String label, String propertyName, String value, int line){
        parent.add(new JLabel(label), getConstraints(line, 0, 1));
        PropertyDescriptor propertyDescriptor = container.getDescriptor(propertyName);
        TextFieldEditor textEditor = new TextFieldEditor();
        JComponent editorComponent = textEditor.createEditorComponent(propertyDescriptor, context);
        ((JTextField) editorComponent).setText(value);
        parent.add(editorComponent, getConstraints(line, 1, 1));
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

    protected void onApply(){
        //TODO save the parameter
    }

}
