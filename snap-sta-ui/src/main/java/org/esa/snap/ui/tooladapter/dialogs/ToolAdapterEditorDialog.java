/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */
package org.esa.snap.ui.tooladapter.dialogs;

import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyEditor;
import com.bc.ceres.swing.binding.PropertyEditorRegistry;
import com.bc.ceres.swing.binding.internal.TextFieldEditor;
import org.esa.snap.framework.dataio.ProductIOPlugInManager;
import org.esa.snap.framework.datamodel.Product;
import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.framework.gpf.descriptor.AnnotationOperatorDescriptor;
import org.esa.snap.framework.gpf.descriptor.SystemVariable;
import org.esa.snap.framework.gpf.descriptor.TemplateParameterDescriptor;
import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterConstants;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterIO;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterOpSpi;
import org.esa.snap.framework.ui.AppContext;
import org.esa.snap.framework.ui.ModalDialog;
import org.esa.snap.framework.ui.UIUtils;
import org.esa.snap.framework.ui.tool.ToolButtonFactory;
import org.esa.snap.rcp.SnapDialogs;
import org.esa.snap.ui.tooladapter.actions.ToolAdapterActionRegistrar;
import org.esa.snap.ui.tooladapter.model.OperatorParametersTable;
import org.esa.snap.ui.tooladapter.model.VariablesTable;
import org.esa.snap.utils.JarPackager;
import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A dialog window used to edit an operator, or to create a new operator.
 * It shows details of an operator such as: descriptor details (name, alias, label, version, copyright,
 * authors, description), system variables, preprocessing tool, product writer, tool location,
 * operator working directory, command line template content, tool output patterns and parameters.
 *
 * @author Ramona Manda
 */
@NbBundle.Messages({
        "CTL_Label_Alias_Text=Alias:",
        "CTL_Label_UniqueName_Text=Unique name:",
        "CTL_Label_Label_Text=Label:",
        "CTL_Label_Version_Text=Version:",
        "CTL_Label_Copyright_Text=Copyright:",
        "CTL_Label_Authors_Text=Authors:",
        "CTL_Label_Description_Text=Description:",
        "CTL_Panel_OperatorDescriptor_Text=Operator Descriptor",
        "CTL_Label_PreprocessingTool_Text=Preprocessing tool",
        "CTL_Label_WriteBefore_Text=Write before processing using:",
        "CTL_Panel_PreProcessing_Border_TitleText=Preprocessing",
        "CTL_Panel_ConfigParams_Text=Configuration Parameters",
        "CTL_Label_ToolLocation_Text=Tool location: ",
        "CTL_Label_WorkDir_Text=Working directory: ",
        "CTL_Label_CmdLineTemplate_Text=Command line template:",
        "CTL_Panel_OutputPattern_Border_TitleText=Tool Output Patterns",
        "CTL_Label_ProgressPattern=Progress pattern:",
        "CTL_Label_ErrorPattern=Error pattern:",
        "CTL_Panel_SysVar_Border_TitleText=System variables",
        "Icon_Add=/org/esa/snap/resources/images/icons/Add16.png",
        "CTL_Panel_OpParams_Border_TitleText=Operator Parameters"
})
public class ToolAdapterEditorDialog extends ModalDialog {

    private ToolAdapterOperatorDescriptor oldOperatorDescriptor;
    private ToolAdapterOperatorDescriptor newOperatorDescriptor;
    private boolean operatorIsNew = false;
    private int newNameIndex = -1;
    private PropertyContainer propertyContainer;
    private BindingContext bindingContext;
    private JTextArea templateContent;
    private OperatorParametersTable paramsTable;
    private Logger logger;

    private ToolAdapterEditorDialog(AppContext appContext, String title, String helpID) {
        super(appContext.getApplicationWindow(), title, ID_OK_CANCEL, helpID);
        this.logger = Logger.getLogger(ToolAdapterEditorDialog.class.getName());
        getJDialog().setResizable(false);
    }

    private ToolAdapterEditorDialog(AppContext appContext, String helpID, ToolAdapterOperatorDescriptor operatorDescriptor) {
        this(appContext, operatorDescriptor.getAlias(), helpID);
        this.oldOperatorDescriptor = operatorDescriptor;
        this.newOperatorDescriptor = new ToolAdapterOperatorDescriptor(this.oldOperatorDescriptor);

        //see if all necessary parameters are present:
        if(newOperatorDescriptor.getToolParameterDescriptors().stream().filter(p -> p.getName().equals(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID)).count() == 0){
            newOperatorDescriptor.getToolParameterDescriptors().add(new TemplateParameterDescriptor(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID, Product.class));
        }
        if(newOperatorDescriptor.getToolParameterDescriptors().stream().filter(p -> p.getName().equals(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_FILE)).count() == 0){
            newOperatorDescriptor.getToolParameterDescriptors().add(new TemplateParameterDescriptor(ToolAdapterConstants.TOOL_SOURCE_PRODUCT_FILE, File.class));
        }
        if(newOperatorDescriptor.getToolParameterDescriptors().stream().filter(p -> p.getName().equals(ToolAdapterConstants.TOOL_TARGET_PRODUCT_FILE)).count() == 0){
            newOperatorDescriptor.getToolParameterDescriptors().add(new TemplateParameterDescriptor(ToolAdapterConstants.TOOL_TARGET_PRODUCT_FILE, File.class));
        }

        propertyContainer = PropertyContainer.createObjectBacked(newOperatorDescriptor);
        ProductIOPlugInManager registry = ProductIOPlugInManager.getInstance();
        String[] writers = registry.getAllProductWriterFormatStrings();
        Arrays.sort(writers);
        propertyContainer.getDescriptor("processingWriter").setValueSet(new ValueSet(writers));
        Set<OperatorSpi> spis = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpis();
        java.util.List<String> toolboxSpis = new ArrayList<>();
        spis.stream().filter(p -> (p instanceof ToolAdapterOpSpi)
                                && (p.getOperatorDescriptor().getClass() != AnnotationOperatorDescriptor.class)
                                && !p.getOperatorAlias().equals(oldOperatorDescriptor.getAlias()))
                     .forEach(operator -> toolboxSpis.add(operator.getOperatorDescriptor().getAlias()));
        toolboxSpis.sort(Comparator.<String>naturalOrder());
        propertyContainer.getDescriptor("preprocessorExternalTool").setValueSet(new ValueSet(toolboxSpis.toArray(new String[toolboxSpis.size()])));

        bindingContext = new BindingContext(propertyContainer);

        paramsTable =  new OperatorParametersTable(newOperatorDescriptor, appContext);
    }

    /**
     * Constructs a new window for editing the operator
     * @param appContext the application context
     * @param helpID
     * @param operatorDescriptor the descriptor of the operator to be edited
     * @param operatorIsNew true if the operator was not previously registered (so it is a new operator) and false if the operator was registered and the editing operation is requested
     */
    public ToolAdapterEditorDialog(AppContext appContext, String helpID, ToolAdapterOperatorDescriptor operatorDescriptor, boolean operatorIsNew) {
        this(appContext, helpID, operatorDescriptor);
        this.operatorIsNew = operatorIsNew;
        this.newNameIndex = -1;
        setContent(createMainPanel());
    }

    /**
     * Constructs a new window for editing the operator
     * @param appContext the application context
     * @param helpID
     * @param operatorDescriptor the descriptor of the operator to be edited
     * @param newNameIndex an integer value representing the suffix for the new operator name; if this value is less than 1, the editing operation of the current operator is executed; if the value is equal to or greater than 1, the operator is duplicated and the index value is used to compute the name of the new operator
     */
    public ToolAdapterEditorDialog(AppContext appContext, String helpID, ToolAdapterOperatorDescriptor operatorDescriptor, int newNameIndex) {
        this(appContext, helpID, operatorDescriptor);
        this.newNameIndex = newNameIndex;
        this.operatorIsNew = this.newNameIndex >= 1;
        if(this.newNameIndex >= 1) {
            this.newOperatorDescriptor.setName(this.oldOperatorDescriptor.getName() + ToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + this.newNameIndex);
            this.newOperatorDescriptor.setAlias(this.oldOperatorDescriptor.getAlias() + ToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + this.newNameIndex);
        }
        setContent(createMainPanel());
    }

    private JPanel createMainPanel() {
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
        PropertyContainer propertyContainer = PropertyContainer.createObjectBacked(newOperatorDescriptor);
        BindingContext bindingContext = new BindingContext(propertyContainer);

        descriptorPanel.add(new JLabel(Bundle.CTL_Label_Alias_Text()), getConstraints(0, 0));
        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("alias");
        JComponent editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(0, 1));

        descriptorPanel.add(new JLabel(Bundle.CTL_Label_UniqueName_Text()), getConstraints(1, 0));
        propertyDescriptor = propertyContainer.getDescriptor("name");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(1, 1));

        descriptorPanel.add(new JLabel(Bundle.CTL_Label_Label_Text()), getConstraints(2, 0));
        propertyDescriptor = propertyContainer.getDescriptor("label");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(2, 1));

        descriptorPanel.add(new JLabel(Bundle.CTL_Label_Version_Text()), getConstraints(3, 0));
        propertyDescriptor = propertyContainer.getDescriptor("version");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(3, 1));

        descriptorPanel.add(new JLabel(Bundle.CTL_Label_Copyright_Text()), getConstraints(4, 0));
        propertyDescriptor = propertyContainer.getDescriptor("copyright");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(4, 1));

        descriptorPanel.add(new JLabel(Bundle.CTL_Label_Authors_Text()), getConstraints(5, 0));
        propertyDescriptor = propertyContainer.getDescriptor("authors");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(5, 1));

        descriptorPanel.add(new JLabel(Bundle.CTL_Label_Description_Text()), getConstraints(6, 0));
        propertyDescriptor = propertyContainer.getDescriptor("description");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent, getConstraints(6, 1));

        TitledBorder title = BorderFactory.createTitledBorder(Bundle.CTL_Panel_OperatorDescriptor_Text());
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

    private GridBagConstraints getConstraints(int row, int col, int noCells) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = noCells;
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

        preProcessingPanel.add(createCheckboxComponent("preprocessTool", editorComponent, newOperatorDescriptor.getPreprocessTool()), getConstraints(0, 0));
        preProcessingPanel.add(new JLabel(Bundle.CTL_Label_PreprocessingTool_Text()), getConstraints(0, 1));
        preProcessingPanel.add(editorComponent, getConstraints(0, 2));

        propertyDescriptor = propertyContainer.getDescriptor("processingWriter");
        editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        JComponent writeComponent = createCheckboxComponent("writeForProcessing", editorComponent, newOperatorDescriptor.shouldWriteBeforeProcessing());
        if(writeComponent instanceof JCheckBox){
            ((JCheckBox) writeComponent).addActionListener(e -> {
                //noinspection StatementWithEmptyBody
                if (((JCheckBox) writeComponent).isSelected()){

                }
            });
        }
        preProcessingPanel.add(writeComponent, getConstraints(1, 0));
        preProcessingPanel.add(new JLabel(Bundle.CTL_Label_WriteBefore_Text()), getConstraints(1, 1));
        preProcessingPanel.add(editorComponent, getConstraints(1, 2));

        TitledBorder title = BorderFactory.createTitledBorder(Bundle.CTL_Panel_PreProcessing_Border_TitleText());
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
        configPanel.setBorder(BorderFactory.createTitledBorder(Bundle.CTL_Panel_ConfigParams_Text()));

        //JPanel topConfigPanel = new JPanel();
        //topConfigPanel.setLayout(new GridLayout(3, 1, 5, 5));

        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("mainToolFileLocation");
        PropertyEditor editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        JComponent editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        JPanel panelToolFiles = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[]{120, 250};
        panelToolFiles.setLayout(layout);

        panelToolFiles.add(new JLabel(Bundle.CTL_Label_ToolLocation_Text()), getConstraints(0, 0));
        panelToolFiles.add(editorComponent, getConstraints(0, 1));

        propertyDescriptor = propertyContainer.getDescriptor("workingDir");
        propertyDescriptor.setAttribute("directory", true);
        editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        panelToolFiles.add(new JLabel(Bundle.CTL_Label_WorkDir_Text()), getConstraints(1, 0));
        panelToolFiles.add(editorComponent, getConstraints(1, 1));

        panelToolFiles.add(new JLabel(Bundle.CTL_Label_CmdLineTemplate_Text()), getConstraints(2, 0, 2));

        //topConfigPanel.add(panelToolFiles);
        //topConfigPanel.setBackground(Color.cyan);

        configPanel.add(panelToolFiles, BorderLayout.PAGE_START);

        templateContent = new JTextArea("", 15, 9);
        try {
            if (operatorIsNew) {
                templateContent.setText(ToolAdapterIO.readOperatorTemplate(oldOperatorDescriptor.getName()));
            } else {
                templateContent.setText(ToolAdapterIO.readOperatorTemplate(newOperatorDescriptor.getName()));
            }
        } catch (IOException e) {
            logger.warning(e.getMessage());
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
        patternsPanel.setBorder(BorderFactory.createTitledBorder(Bundle.CTL_Panel_OutputPattern_Border_TitleText()));

        TextFieldEditor textEditor = new TextFieldEditor();
        PropertyContainer propertyContainer = PropertyContainer.createObjectBacked(newOperatorDescriptor);
        BindingContext bindingContext = new BindingContext(propertyContainer);

        patternsPanel.add(new JLabel(Bundle.CTL_Label_ProgressPattern()), getConstraints(0, 0));
        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("progressPattern");
        JComponent editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        patternsPanel.add(editorComponent, getConstraints(0, 1));

        patternsPanel.add(new JLabel(Bundle.CTL_Label_ErrorPattern()), getConstraints(1, 0));
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
        variablesBorderPanel.setBorder(BorderFactory.createTitledBorder(Bundle.CTL_Panel_SysVar_Border_TitleText()));
        AbstractButton addVariableBut = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_Add()),
                false);
        addVariableBut.setAlignmentX(Component.LEFT_ALIGNMENT);
        variablesBorderPanel.add(addVariableBut);
        VariablesTable varTable = new VariablesTable(newOperatorDescriptor.getVariables());
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
            newOperatorDescriptor.getVariables().add(new SystemVariable("key", ""));
            varTable.revalidate();
        });

        return descriptorAndVariablesPanel;
    }

    public JPanel createParametersPanel() {
        JPanel paramsPanel = new JPanel();
        BoxLayout layout = new BoxLayout(paramsPanel, BoxLayout.PAGE_AXIS);
        paramsPanel.setLayout(layout);
        AbstractButton addParamBut = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_Add()),
                false);
        addParamBut.setAlignmentX(Component.LEFT_ALIGNMENT);
        paramsPanel.add(addParamBut);
        JScrollPane tableScrollPane = new JScrollPane(paramsTable);
        tableScrollPane.setPreferredSize(new Dimension(500, 130));
        tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        paramsPanel.add(tableScrollPane);
        addParamBut.addActionListener(e -> paramsTable.addParameterToTable(new TemplateParameterDescriptor("parameterName", String.class)));
        TitledBorder title = BorderFactory.createTitledBorder(Bundle.CTL_Panel_OpParams_Border_TitleText());
        paramsPanel.setBorder(title);
        return paramsPanel;
    }

    @Override
    protected void onOK() {
        super.onOK();
        if(!this.operatorIsNew) {
            ToolAdapterActionRegistrar.removeOperatorMenu(oldOperatorDescriptor);
            ToolAdapterIO.removeOperator(oldOperatorDescriptor);
        }
        String oldOperatorName = oldOperatorDescriptor.getName();
        if (oldOperatorDescriptor.isSystem() && oldOperatorName.equals(newOperatorDescriptor.getName())) {
            newOperatorDescriptor.setName(newOperatorDescriptor.getName() + ".custom");
            newOperatorDescriptor.setAlias(newOperatorDescriptor.getAlias() + "-custom");
        }
        newOperatorDescriptor.setSystem(false);
        newOperatorDescriptor.setTemplateFileLocation(newOperatorDescriptor.getAlias() + ToolAdapterConstants.TOOL_VELO_TEMPLATE_SUFIX);
        java.util.List<TemplateParameterDescriptor> toolParameterDescriptors = newOperatorDescriptor.getToolParameterDescriptors();
        toolParameterDescriptors.stream().filter(param -> paramsTable.getBindingContext().getBinding(param.getName()) != null)
                                         .filter(param -> paramsTable.getBindingContext().getBinding(param.getName()).getPropertyValue() != null)
                                         .forEach(param -> param.setDefaultValue(paramsTable.getBindingContext().getBinding(param.getName())
                                                 .getPropertyValue().toString()));
        try {
            ToolAdapterIO.saveAndRegisterOperator(newOperatorDescriptor, templateContent.getText());
			ToolAdapterActionRegistrar.registerOperatorMenu(newOperatorDescriptor);
            JarPackager.packAdapterJar(newOperatorDescriptor, new File(ToolAdapterIO.getUserAdapterPath(), newOperatorDescriptor.getAlias() + ".jar"));
        } catch (Exception e) {
            logger.warning(e.getMessage());
            SnapDialogs.showError(e.getMessage());
        }
    }
}
