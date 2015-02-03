package org.esa.s2tbx.tooladapter.ui;

import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyEditor;
import com.bc.ceres.swing.binding.PropertyEditorRegistry;
import com.bc.ceres.swing.binding.internal.TextFieldEditor;
import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterConstants;
import org.esa.s2tbx.tooladapter.ui.utils.OperatorParametersTable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ramonag on 1/13/2015.
 */
public class ExternalToolEditorDialog extends ModelessDialog{

    private S2tbxOperatorDescriptor operatorSpi;
    private boolean operatorIsNew;
    private int newNameIndex = -1;
    private JPanel preprocessingPanel;
    private JPanel writeOnPrePanel;
    private JPanel writeOnProPanel;
    private PropertyContainer propertyContainer;
    private BindingContext bindingContext;

    private ExternalToolEditorDialog(AppContext appContext, String title, String helpID) {
        super(appContext.getApplicationWindow(), title, ID_APPLY_CLOSE, helpID);
    }

    private  ExternalToolEditorDialog(AppContext appContext, String title, String helpID, S2tbxOperatorDescriptor operatorSpi){
        this(appContext, title, helpID);
        this.operatorSpi = operatorSpi;
        propertyContainer = PropertyContainer.createObjectBacked(operatorSpi);
        bindingContext = new BindingContext(propertyContainer);
    }

    public ExternalToolEditorDialog(AppContext appContext, String title, String helpID, S2tbxOperatorDescriptor operatorSpi, boolean operatorIsNew) {
        this(appContext, title, helpID, operatorSpi);
        this.operatorIsNew = operatorIsNew;
        this.newNameIndex = -1;
        setContent(createMainPanel());
        getJDialog().setMinimumSize(new Dimension(1200, 250));
    }

    public ExternalToolEditorDialog(AppContext appContext, String title, String helpID, S2tbxOperatorDescriptor operatorSpi, int newNameIndex) {
        this(appContext, title, helpID, operatorSpi);
        this.newNameIndex = newNameIndex;
        if(this.newNameIndex >= 1){
            this.operatorIsNew = true;
        } else {
            this.operatorIsNew = false;
        }
        setContent(createMainPanel());
        getJDialog().setMinimumSize(new Dimension(1200, 250));
    }

    private JPanel createOperatorDescriptorPanel() {

        TableLayout descriptorLayout = new TableLayout(2);
        descriptorLayout.setTableAnchor(TableLayout.Anchor.NORTHWEST);
        descriptorLayout.setTableFill(TableLayout.Fill.HORIZONTAL);
        descriptorLayout.setTablePadding(3, 3);

        final JPanel descriptorPanel = new JPanel(descriptorLayout);

        TextFieldEditor textEditor = new TextFieldEditor();
        PropertyContainer propertyContainer = PropertyContainer.createObjectBacked(operatorSpi);
        BindingContext bindingContext = new BindingContext(propertyContainer);

        descriptorPanel.add(new JLabel("Operator alias:"));
        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("alias");
        JComponent editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        if (this.newNameIndex >= 1) {
            ((JTextField) editorComponent).setText(operatorSpi.getAlias() + S2tbxToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + this.newNameIndex);
        }
        descriptorPanel.add(editorComponent);

        descriptorPanel.add(new JLabel("Operator unique name:"));
        propertyDescriptor = propertyContainer.getDescriptor("name");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        if (this.newNameIndex >= 1) {
            ((JTextField) editorComponent).setText(operatorSpi.getName() + S2tbxToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + this.newNameIndex);
        }
        descriptorPanel.add(editorComponent);

        descriptorPanel.add(new JLabel("Operator label:"));
        propertyDescriptor = propertyContainer.getDescriptor("label");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent);

        descriptorPanel.add(new JLabel("Operator version:"));
        propertyDescriptor = propertyContainer.getDescriptor("version");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent);

        descriptorPanel.add(new JLabel("Operator copyright:"));
        propertyDescriptor = propertyContainer.getDescriptor("copyright");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent);

        descriptorPanel.add(new JLabel("Operator authors:"));
        propertyDescriptor = propertyContainer.getDescriptor("authors");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent);

        descriptorPanel.add(new JLabel("Operator description:"));
        propertyDescriptor = propertyContainer.getDescriptor("description");
        editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent);

        TitledBorder title = BorderFactory.createTitledBorder("Operator Descriptor");
        descriptorPanel.setBorder(title);

        return descriptorPanel;
    }

    private JPanel createProcessingPanel(){

        final JPanel processingPanel = new JPanel();
        processingPanel.setLayout(new BoxLayout(processingPanel, BoxLayout.PAGE_AXIS));
        processingPanel.setPreferredSize(new Dimension(600, 230));
        processingPanel.setMaximumSize(new Dimension(600, 230));
        processingPanel.setBorder(BorderFactory.createLineBorder(Color.red));

        JPanel preprocessingPanel = new JPanel();
        preprocessingPanel.setLayout(new BoxLayout(preprocessingPanel, BoxLayout.PAGE_AXIS));
        preprocessingPanel.add(createOperatorMemberPanel("Preprocessing writer", "preprocessingWriter", null));
        preprocessingPanel.add(createOperatorMemberPanel("Preprocessing tool file location:", "preprocessingToolFileLocation", null));
        preprocessingPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        processingPanel.add(createOperatorMemberPanel("Preprocessing", "preprocessTool", preprocessingPanel));
        processingPanel.add(preprocessingPanel);

        JPanel processingWriter = new JPanel();
        processingWriter.add(createOperatorMemberPanel("Processing writer", "processingWriter", null));
        processingWriter.setBorder(BorderFactory.createLineBorder(Color.black));

        processingPanel.add(createOperatorMemberPanel("Write before processing", "writeForProcessing", processingWriter));
        processingPanel.add(processingWriter);

        processingPanel.add(createOperatorMemberPanel("Processing tool location", "mainToolFileLocation", null));

        processingPanel.add(createOperatorMemberPanel("Processing temporary folder", "tempFolder", null));

        TitledBorder title = BorderFactory.createTitledBorder("Operator processing parameters");
        processingPanel.setBorder(title);

        return processingPanel;
    }

    private JPanel createOperatorMemberPanel(String label, String memberName, JPanel tooglePanelEnabled){
        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor(memberName);
        PropertyEditor editor = PropertyEditorRegistry.getInstance().findPropertyEditor(propertyDescriptor);

        JPanel panel = new JPanel(new BorderLayout());
        JComponent editorComponent = editor.createEditorComponent(propertyDescriptor, bindingContext);

        if(editorComponent instanceof JCheckBox) {
            panel.add(editorComponent, BorderLayout.LINE_START);
            panel.add(new JLabel(label), BorderLayout.CENTER);
        }else{
            panel.add(new JLabel(label), BorderLayout.LINE_START);
            panel.add(editorComponent, BorderLayout.CENTER);
        }

        if(editorComponent instanceof JCheckBox && tooglePanelEnabled != null){
            enableSubComponents(tooglePanelEnabled, ((JCheckBox)editorComponent).isSelected());
            ((JCheckBox)editorComponent).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    enableSubComponents(tooglePanelEnabled, ((JCheckBox)editorComponent).isSelected());
                }
            });
        }
        if(editorComponent instanceof JTextField){
            editorComponent.setPreferredSize(new Dimension(200, 30));
            editorComponent.setEnabled(true);
        }
        panel.setPreferredSize(new Dimension(600, 30));
        //panel.setBorder(BorderFactory.createLineBorder(Color.black));
        return panel;
    }

    public JPanel createMainPanel() {
        JPanel toolDescriptorPanel = new JPanel();
        toolDescriptorPanel.setLayout(new BorderLayout());

        toolDescriptorPanel.add(createOperatorDescriptorPanel(), BorderLayout.LINE_START);
        toolDescriptorPanel.add(createProcessingPanel(), BorderLayout.LINE_END);

        JScrollPane tableScrollPane = new JScrollPane(new OperatorParametersTable(operatorSpi));
        TitledBorder title = BorderFactory.createTitledBorder("Operator Parameters");
        tableScrollPane.setBorder(title);
        toolDescriptorPanel.add(tableScrollPane, BorderLayout.PAGE_END);

        return toolDescriptorPanel;
    }

    private void enableSubComponents(Component component, boolean enabled){
        component.setEnabled(enabled);
        if(component instanceof Container) {
            for (int i = 0; i < ((Container) component).getComponents().length; i++) {
                enableSubComponents(((Container) component).getComponents()[i], enabled);
            }
        }
    }

    protected void onApply(){
        super.onApply();
    }
}
