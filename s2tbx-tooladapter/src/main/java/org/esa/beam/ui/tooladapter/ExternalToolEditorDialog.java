package org.esa.beam.ui.tooladapter;

import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyEditor;
import com.bc.ceres.swing.binding.PropertyEditorRegistry;
import com.bc.ceres.swing.binding.internal.TextFieldEditor;
import org.esa.beam.framework.dataio.ProductIOPlugInManager;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.descriptor.AnnotationOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.SystemVariable;
import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.ToolParameterDescriptor;
import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterConstants;
import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterIO;
import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterOpSpi;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;
import org.esa.beam.ui.tooladapter.utils.OperatorParametersTable;
import org.esa.beam.ui.tooladapter.utils.VariablesTable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class ExternalToolEditorDialog extends ModalDialog {

    private ToolAdapterOperatorDescriptor operatorDescriptor;
    private boolean operatorIsNew;
    private int newNameIndex = -1;
    private PropertyContainer propertyContainer;
    private BindingContext bindingContext;
    private JTextArea templateContent;
    private OperatorParametersTable paramsTable;
    private AppContext appContext;

    private ExternalToolEditorDialog(AppContext appContext, String title, String helpID) {
        super(appContext.getApplicationWindow(), title, ID_OK_CANCEL, helpID);
        this.appContext = appContext;
        getJDialog().setResizable(false);
    }

    private ExternalToolEditorDialog(AppContext appContext, String helpID, ToolAdapterOperatorDescriptor operatorDescriptor) {
        this(appContext, operatorDescriptor.getAlias(), helpID);
        this.operatorDescriptor = operatorDescriptor;

        propertyContainer = PropertyContainer.createObjectBacked(operatorDescriptor);
        ProductIOPlugInManager registry = ProductIOPlugInManager.getInstance();
        String[] writers = registry.getAllProductWriterFormatStrings();
        Arrays.sort(writers);
        propertyContainer.getDescriptor("processingWriter").setValueSet(new ValueSet(writers));
        Set<OperatorSpi> spis = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpis();
        java.util.List<String> toolboxSpis = new ArrayList<>();
        spis.stream().filter(p -> p instanceof ToolAdapterOpSpi && p.getOperatorDescriptor().getClass() != AnnotationOperatorDescriptor.class && p.getOperatorAlias() != operatorDescriptor.getAlias()).
                forEach(operator -> toolboxSpis.add(operator.getOperatorDescriptor().getAlias()));
        toolboxSpis.sort(Comparator.<String>naturalOrder());
        propertyContainer.getDescriptor("preprocessorExternalTool").setValueSet(new ValueSet(toolboxSpis.toArray(new String[toolboxSpis.size()])));

        bindingContext = new BindingContext(propertyContainer);

        paramsTable =  new OperatorParametersTable(operatorDescriptor, appContext);
    }

    public ExternalToolEditorDialog(AppContext appContext, String helpID, ToolAdapterOperatorDescriptor operatorDescriptor, boolean operatorIsNew) {
        this(appContext, helpID, operatorDescriptor);
        this.operatorIsNew = operatorIsNew;
        this.newNameIndex = -1;
        setContent(createMainPanel());
    }

    public ExternalToolEditorDialog(AppContext appContext, String helpID, ToolAdapterOperatorDescriptor operatorDescriptor, int newNameIndex) {
        this(appContext, helpID, operatorDescriptor);
        this.newNameIndex = newNameIndex;
        this.operatorIsNew = this.newNameIndex >= 1;
        setContent(createMainPanel());
    }

    public JPanel createMainPanel() {
        JPanel toolDescriptorPanel = new JPanel();
        toolDescriptorPanel.setLayout(new BorderLayout());
        toolDescriptorPanel.setPreferredSize(new Dimension(800, 550));

        toolDescriptorPanel.add(createDescriptorAndVariablesAndPreprocessingPanel(), BorderLayout.LINE_START);
        toolDescriptorPanel.add(createProcessingPanel(), BorderLayout.CENTER);
        toolDescriptorPanel.add(createParametersPanel(), BorderLayout.PAGE_END);

        return toolDescriptorPanel;
    }

    private JPanel createOperatorDescriptorPanel() {
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[]{100, 315};

        final JPanel descriptorPanel = new JPanel(layout);

        TextFieldEditor textEditor = new TextFieldEditor();
        PropertyContainer propertyContainer = PropertyContainer.createObjectBacked(operatorDescriptor);
        BindingContext bindingContext = new BindingContext(propertyContainer);

        descriptorPanel.add(new JLabel("Alias:"), getConstraints(0, 0));
        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("alias");
        JComponent editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        if (this.newNameIndex >= 1) {
            ((JTextField) editorComponent).setText(operatorDescriptor.getAlias() + ToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + this.newNameIndex);
        }
        descriptorPanel.add(editorComponent, getConstraints(0, 1));

        descriptorPanel.add(new JLabel("Unique name:"), getConstraints(1, 0));
        propertyDescriptor = propertyContainer.getDescriptor("name");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        if (this.newNameIndex >= 1) {
            ((JTextField) editorComponent).setText(operatorDescriptor.getName() + ToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + this.newNameIndex);
        }
        descriptorPanel.add(editorComponent, getConstraints(1, 1));

        descriptorPanel.add(new JLabel("Label:"), getConstraints(2, 0));
        propertyDescriptor = propertyContainer.getDescriptor("label");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(2, 1));

        descriptorPanel.add(new JLabel("Version:"), getConstraints(3, 0));
        propertyDescriptor = propertyContainer.getDescriptor("version");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(3, 1));

        descriptorPanel.add(new JLabel("Copyright:"), getConstraints(4, 0));
        propertyDescriptor = propertyContainer.getDescriptor("copyright");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(4, 1));

        descriptorPanel.add(new JLabel("Authors:"), getConstraints(5, 0));
        propertyDescriptor = propertyContainer.getDescriptor("authors");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(5, 1));

        descriptorPanel.add(new JLabel("Description:"), getConstraints(6, 0));
        propertyDescriptor = propertyContainer.getDescriptor("description");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(6, 1));

        TitledBorder title = BorderFactory.createTitledBorder("Operator Descriptor");
        descriptorPanel.setBorder(title);
        descriptorPanel.setPreferredSize(new Dimension(415, 200));

        return descriptorPanel;
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

    private JPanel createPreProcessingPanel(){
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[]{35, 180, 200};

        final JPanel preProcessingPanel = new JPanel(layout);

        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("preprocessorExternalTool");
        PropertyEditor editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        JComponent editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        preProcessingPanel.add(createCheckboxComponent("preprocessTool", editorComponent, operatorDescriptor.getPreprocessTool()), getConstraints(0, 0));
        preProcessingPanel.add(new JLabel("Preprocessing tool:"), getConstraints(0, 1));
        preProcessingPanel.add(editorComponent, getConstraints(0, 2));

        propertyDescriptor = propertyContainer.getDescriptor("processingWriter");
        editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        preProcessingPanel.add(createCheckboxComponent("writeForProcessing", editorComponent, operatorDescriptor.shouldWriteBeforeProcessing()), getConstraints(1, 0));
        preProcessingPanel.add(new JLabel("Write before processing using:"), getConstraints(1, 1));
        preProcessingPanel.add(editorComponent, getConstraints(1, 2));

        TitledBorder title = BorderFactory.createTitledBorder("Preprocessing");
        preProcessingPanel.setBorder(title);
        preProcessingPanel.setPreferredSize(new Dimension(415, 70));

        return preProcessingPanel;
    }

    private JPanel createProcessingPanel() {

        final JPanel processingPanel = new JPanel();
        processingPanel.setLayout(new BorderLayout());

        //processingPanel.add(createPreProcessingPanel(), BorderLayout.PAGE_START);

        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BorderLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuration parameters"));

        JPanel topConfigPanel = new JPanel();
        topConfigPanel.setLayout(new GridLayout(3, 2, 5, 5));

        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("mainToolFileLocation");
        PropertyEditor editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        JComponent editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        JPanel panelToolLocation = new JPanel(new BorderLayout());
        panelToolLocation.add(new JLabel("Tool location: "), BorderLayout.LINE_START);
        panelToolLocation.add(editorComponent, BorderLayout.CENTER);
        topConfigPanel.add(panelToolLocation);

        propertyDescriptor = propertyContainer.getDescriptor("workingDir");
        propertyDescriptor.setAttribute("directory", true);
        editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        JPanel panelWorkingDir = new JPanel(new BorderLayout());
        panelWorkingDir.add(new JLabel("Working directory: "), BorderLayout.LINE_START);
        panelWorkingDir.add(editorComponent, BorderLayout.CENTER);
        topConfigPanel.add(panelWorkingDir);

        topConfigPanel.add(new JLabel("Command line template:"));

        configPanel.add(topConfigPanel, BorderLayout.PAGE_START);

        templateContent = new JTextArea("", 15, 9);
        if (!operatorIsNew) {
            try {
                templateContent.setText(ToolAdapterIO.readOperatorTemplate(operatorDescriptor.getName()));
            } catch (IOException e) {
                e.printStackTrace();
                //TODO log error
            }
        }
        configPanel.add(new JScrollPane(templateContent), BorderLayout.CENTER);

        processingPanel.add(configPanel, BorderLayout.CENTER);

        processingPanel.add(createProgressPatternsPanel(), BorderLayout.PAGE_END);

        return processingPanel;
    }

    private JPanel createProgressPatternsPanel(){
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[]{100, 280};

        JPanel patternsPanel = new JPanel(layout);
        patternsPanel.setBorder(BorderFactory.createTitledBorder("Tool output patterns"));

        TextFieldEditor textEditor = new TextFieldEditor();
        PropertyContainer propertyContainer = PropertyContainer.createObjectBacked(operatorDescriptor);
        BindingContext bindingContext = new BindingContext(propertyContainer);

        patternsPanel.add(new JLabel("Progress pattern:"), getConstraints(0, 0));
        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("progressPattern");
        JComponent editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        patternsPanel.add(editorComponent, getConstraints(0, 1));

        patternsPanel.add(new JLabel("Error pattern:"), getConstraints(1, 0));
        propertyDescriptor = propertyContainer.getDescriptor("errorPattern");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        patternsPanel.add(editorComponent, getConstraints(1, 1));

        return patternsPanel;
    }

    private JComponent createCheckboxComponent(String memberName, JComponent toogleComponentEnabled, Boolean value) {
        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor(memberName);
        PropertyEditor editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        JComponent editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        if (editorComponent instanceof JCheckBox && toogleComponentEnabled != null) {
            ((JCheckBox) editorComponent).setSelected(value);
            toogleComponentEnabled.setEnabled(value);
            ((JCheckBox) editorComponent).addActionListener(e -> toogleComponentEnabled.setEnabled(((JCheckBox) editorComponent).isSelected()));
        }

        return editorComponent;
    }

    private JPanel createDescriptorAndVariablesAndPreprocessingPanel() {
        JPanel descriptorAndVariablesPanel = new JPanel();
        descriptorAndVariablesPanel.setPreferredSize(new Dimension(420, 480));
        BoxLayout layout = new BoxLayout(descriptorAndVariablesPanel, BoxLayout.PAGE_AXIS);
        descriptorAndVariablesPanel.setLayout(layout);

        JPanel descriptorPanel = createOperatorDescriptorPanel();
        descriptorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptorPanel.setMaximumSize(new Dimension(415, 400));
        descriptorAndVariablesPanel.add(descriptorPanel);

        JPanel variablesBorderPanel = new JPanel();
        layout = new BoxLayout(variablesBorderPanel, BoxLayout.PAGE_AXIS);
        variablesBorderPanel.setLayout(layout);
        variablesBorderPanel.setBorder(BorderFactory.createTitledBorder("System variables"));
        AbstractButton addVariableBut = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Add16.png"),
                false);
        addVariableBut.setAlignmentX(Component.LEFT_ALIGNMENT);
        variablesBorderPanel.add(addVariableBut);
        VariablesTable varTable = new VariablesTable(operatorDescriptor.getVariables());
        varTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        JScrollPane scrollPane = new JScrollPane(varTable);
        scrollPane.setPreferredSize(new Dimension(400, 80));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        variablesBorderPanel.add(scrollPane);
        variablesBorderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        variablesBorderPanel.setMaximumSize(new Dimension(415, 40));
        variablesBorderPanel.setMinimumSize(new Dimension(415, 40));
        descriptorAndVariablesPanel.add(variablesBorderPanel);

        JPanel preprocessingPanel = createPreProcessingPanel();
        preprocessingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        preprocessingPanel.setMaximumSize(new Dimension(415, 60));
        //preprocessingPanel.setMinimumSize(new Dimension(415, 35));
        descriptorAndVariablesPanel.add(preprocessingPanel);

        addVariableBut.addActionListener(e -> {
            operatorDescriptor.getVariables().add(new SystemVariable("key", ""));
            varTable.revalidate();
        });

        return descriptorAndVariablesPanel;
    }

    public JPanel createParametersPanel() {
        JPanel paramsPanel = new JPanel();
        BoxLayout layout = new BoxLayout(paramsPanel, BoxLayout.PAGE_AXIS);
        paramsPanel.setLayout(layout);
        AbstractButton addParamBut = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Add16.png"),
                false);
        addParamBut.setAlignmentX(Component.LEFT_ALIGNMENT);
        paramsPanel.add(addParamBut);
        JScrollPane tableScrollPane = new JScrollPane(paramsTable);
        tableScrollPane.setPreferredSize(new Dimension(500, 130));
        tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        paramsPanel.add(tableScrollPane);
        addParamBut.addActionListener(e -> {
            paramsTable.addParameterToTable(new ToolParameterDescriptor("parameterName", String.class));
        });
        TitledBorder title = BorderFactory.createTitledBorder("Operator Parameters");
        paramsPanel.setBorder(title);
        return paramsPanel;
    }

    @Override
    protected void onOK() {
        super.onOK();
        if (operatorIsNew) {
            if (operatorDescriptor.getTemplateFileLocation() == null) {
                operatorDescriptor.setTemplateFileLocation(operatorDescriptor.getAlias() + ToolAdapterConstants.TOOL_VELO_TEMPLATE_SUFIX);
            }
        }
        for(ToolParameterDescriptor param : operatorDescriptor.getToolParameterDescriptors()){
            if(paramsTable.getBindingContext().getBinding(param.getName()) == null){
                //TODO why is this happening???
            } else {
                if(paramsTable.getBindingContext().getBinding(param.getName()).getPropertyValue() != null) {
                    param.setDefaultValue(paramsTable.getBindingContext().getBinding(param.getName()).getPropertyValue().toString());
                }
            }
        }
        try {
            ToolAdapterIO.saveAndRegisterOperator(operatorDescriptor, templateContent.getText());
        } catch (Exception e) {
            e.printStackTrace();
            //TODO show error on screeen
        }
    }
}