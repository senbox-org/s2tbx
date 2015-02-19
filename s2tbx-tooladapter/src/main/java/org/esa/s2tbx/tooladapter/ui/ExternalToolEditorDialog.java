package org.esa.s2tbx.tooladapter.ui;

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
import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterConstants;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterIO;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterOpSpi;
import org.esa.s2tbx.tooladapter.ui.utils.OperatorParametersTableNewModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

/**
 * Created by ramonag on 1/13/2015.
 */
public class ExternalToolEditorDialog extends ModelessDialog{

    private S2tbxOperatorDescriptor operatorDescriptor;
    private boolean operatorIsNew;
    private int newNameIndex = -1;
    private JPanel preprocessingPanel;
    private JPanel writeOnPrePanel;
    private JPanel writeOnProPanel;
    private PropertyContainer propertyContainer;
    private BindingContext bindingContext;
    private  JTextArea templateContent;

    private ExternalToolEditorDialog(AppContext appContext, String title, String helpID) {
        super(appContext.getApplicationWindow(), title, ID_APPLY_CLOSE, helpID);
    }

    private  ExternalToolEditorDialog(AppContext appContext, String title, String helpID, S2tbxOperatorDescriptor operatorDescriptor){
        this(appContext, title, helpID);
        this.operatorDescriptor = operatorDescriptor;

        propertyContainer = PropertyContainer.createObjectBacked(operatorDescriptor);
        ProductIOPlugInManager registry = ProductIOPlugInManager.getInstance();
        String[] writers = registry.getAllProductWriterFormatStrings();
        Arrays.sort(writers);
        propertyContainer.getDescriptor("processingWriter").setValueSet(new ValueSet(writers));
        Set<OperatorSpi> spis = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpis();
        java.util.List<String> toolboxSpis = new ArrayList<String>();
        spis.stream().filter(p -> p instanceof S2tbxToolAdapterOpSpi && ((S2tbxToolAdapterOpSpi) p).getOperatorDescriptor().getClass() != AnnotationOperatorDescriptor.class).
                forEach(operator -> toolboxSpis.add(operator.getOperatorDescriptor().getName()));
        toolboxSpis.sort(Comparator.<String>naturalOrder());
        propertyContainer.getDescriptor("preprocessingExternalTool").setValueSet(new ValueSet(toolboxSpis.toArray(new String[toolboxSpis.size()])));

        bindingContext = new BindingContext(propertyContainer);
    }

    public ExternalToolEditorDialog(AppContext appContext, String title, String helpID, S2tbxOperatorDescriptor operatorDescriptor, boolean operatorIsNew) {
        this(appContext, title, helpID, operatorDescriptor);
        this.operatorIsNew = operatorIsNew;
        this.newNameIndex = -1;
        setContent(createMainPanel());
        //getJDialog().setMaximumSize(new Dimension(400, 400));
        getJDialog().setResizable(false);
    }

    public ExternalToolEditorDialog(AppContext appContext, String title, String helpID, S2tbxOperatorDescriptor operatorDescriptor, int newNameIndex) {
        this(appContext, title, helpID, operatorDescriptor);
        this.newNameIndex = newNameIndex;
        if(this.newNameIndex >= 1){
            this.operatorIsNew = true;
        } else {
            this.operatorIsNew = false;
        }
        setContent(createMainPanel());
        //getJDialog().setMaximumSize(new Dimension(400, 400));
        getJDialog().setResizable(false);
    }

    private JPanel createSystemPropertiesPanel(){
        JScrollPane scroll = new JScrollPane();
        JPanel panel = new JPanel();
        return panel;
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
            ((JTextField) editorComponent).setText(operatorDescriptor.getAlias() + S2tbxToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + this.newNameIndex);
        }
        descriptorPanel.add(editorComponent, getConstraints(0, 1));

        descriptorPanel.add(new JLabel("Unique name:"), getConstraints(1, 0));
        propertyDescriptor = propertyContainer.getDescriptor("name");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        if (this.newNameIndex >= 1) {
            ((JTextField) editorComponent).setText(operatorDescriptor.getName() + S2tbxToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + this.newNameIndex);
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

    private GridBagConstraints getConstraints(int row, int col){
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = col;
        c.gridy = row;
        if(col == 1){
           c.gridwidth = 1;
        }
        c.insets = new Insets(2, 10, 2, 10);
        return c;
    }

    private JPanel createProcessingPanel(){

        final JPanel processingPanel = new JPanel();
        processingPanel.setLayout(new BorderLayout());
        //processingPanel.setPreferredSize(new Dimension(600, 230));
        //processingPanel.setMaximumSize(new Dimension(600, 230));

        JPanel preprocessingPanel = new JPanel();
        preprocessingPanel.setLayout(new GridLayout(2, 1, 5, 5));
        preprocessingPanel.setBorder(BorderFactory.createTitledBorder("Preprocessing"));

        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("preprocessingExternalTool");
        PropertyEditor editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        JComponent editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        JPanel panelLeft = new JPanel(new FlowLayout());
        panelLeft.add(createCheckboxComponent("preprocessTool", editorComponent, operatorDescriptor.getPreprocessTool()));
        panelLeft.add(new JLabel("Preprocessing tool:"));

        JPanel panelPreprocessingTool = new JPanel(new BorderLayout());
        panelPreprocessingTool.add(panelLeft, BorderLayout.LINE_START);
        panelPreprocessingTool.add(editorComponent, BorderLayout.CENTER);
        preprocessingPanel.add(panelPreprocessingTool);

        propertyDescriptor = propertyContainer.getDescriptor("processingWriter");
        editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        panelLeft = new JPanel(new FlowLayout());
        panelLeft.add(createCheckboxComponent("writeForProcessing", editorComponent, operatorDescriptor.getWriteForProcessing()));
        panelLeft.add(new JLabel("Write before processing using:"));

        JPanel panelProcessingWriter = new JPanel(new BorderLayout());
        panelProcessingWriter.add(panelLeft, BorderLayout.LINE_START);
        panelProcessingWriter.add(editorComponent, BorderLayout.CENTER);
        preprocessingPanel.add(panelProcessingWriter);

        processingPanel.add(preprocessingPanel, BorderLayout.PAGE_START);

        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BorderLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuration parameters"));

        JPanel topConfigPanel = new JPanel();
        topConfigPanel.setLayout(new GridLayout(3, 1, 5, 5));

        propertyDescriptor = propertyContainer.getDescriptor("mainToolFileLocation");
        editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

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

        templateContent = new JTextArea("err in log", 15, 15);
        try {
            templateContent.setText(S2tbxToolAdapterIO.readOperatorTemplate(operatorDescriptor.getName()));
        } catch (IOException e) {
            e.printStackTrace();
            //TODO log error
        }
        configPanel.add(templateContent, BorderLayout.CENTER);

        processingPanel.add(configPanel, BorderLayout.CENTER);



        /*processingPanel.add(createOperatorMemberPanel("Preprocessing", "preprocessTool", preprocessingPanel));
        processingPanel.add(preprocessingPanel);

        JPanel processingWriter = new JPanel();
        processingWriter.add(createOperatorMemberPanel("Processing writer", "processingWriter", null));
        processingWriter.setBorder(BorderFactory.createLineBorder(Color.black));

        processingPanel.add(createOperatorMemberPanel("Write before processing", "writeForProcessing", processingWriter));
        processingPanel.add(processingWriter);

        processingPanel.add(createOperatorMemberPanel("Processing tool location", "mainToolFileLocation", null));

        processingPanel.add(createOperatorMemberPanel("Processing temporary folder", "temporaryFolder", null));

        TitledBorder title = BorderFactory.createTitledBorder("Operator processing parameters");
        processingPanel.setBorder(title);
*/
        return processingPanel;
    }

    private JComponent createCheckboxComponent(String memberName, JComponent toogleComponentEnabled, Boolean value){
        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor(memberName);
        PropertyEditor editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);
        JComponent editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        if(editorComponent instanceof JCheckBox && toogleComponentEnabled != null){
            ((JCheckBox) editorComponent).setSelected(value);
            toogleComponentEnabled.setEnabled(value);
            ((JCheckBox) editorComponent).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toogleComponentEnabled.setEnabled(((JCheckBox) editorComponent).isSelected());
                }
            });
        }

        return editorComponent;
    }

    public JPanel createMainPanel() {
        JPanel toolDescriptorPanel = new JPanel();
        toolDescriptorPanel.setLayout(new BorderLayout());

        toolDescriptorPanel.add(createOperatorDescriptorPanel(), BorderLayout.LINE_START);
        toolDescriptorPanel.add(createProcessingPanel(), BorderLayout.CENTER);
        //toolDescriptorPanel.add(topPanel, BorderLayout.PAGE_START);

        JScrollPane tableScrollPane = new JScrollPane(new OperatorParametersTableNewModel(operatorDescriptor));
        //JTable table = new JTable(new OperatorParametersTableNewModel(operatorDescriptor));
        //JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(500, 200));
        TitledBorder title = BorderFactory.createTitledBorder("Operator Parameters");
        tableScrollPane.setBorder(title);
        toolDescriptorPanel.add(tableScrollPane, BorderLayout.PAGE_END);

        return toolDescriptorPanel;
    }

    protected void onApply(){
        super.onApply();
        try {
            S2tbxToolAdapterIO.saveAndRegisterOperator(operatorDescriptor,
                    templateContent.getText());
        } catch (IOException e) {
            e.printStackTrace();
            //TODO show error on screeen
        }
    }
}
