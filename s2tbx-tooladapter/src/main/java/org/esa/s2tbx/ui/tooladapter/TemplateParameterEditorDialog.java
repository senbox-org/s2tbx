package org.esa.s2tbx.ui.tooladapter;

import org.esa.s2tbx.framework.gpf.descriptor.TemplateParameterDescriptor;
import org.esa.s2tbx.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.s2tbx.framework.gpf.descriptor.ToolParameterDescriptor;
import org.esa.s2tbx.framework.gpf.operators.tooladapter.ToolAdapterIO;
import org.esa.s2tbx.framework.gpf.operators.tooladapter.ToolAdapterOp;
import org.esa.s2tbx.ui.tooladapter.utils.OperatorParametersTable;
import org.esa.s2tbx.ui.tooladapter.utils.PropertyMemberUIWrapper;
import org.esa.snap.framework.ui.AppContext;
import org.esa.snap.framework.ui.ModalDialog;
import org.esa.snap.framework.ui.UIUtils;
import org.esa.snap.framework.ui.tool.ToolButtonFactory;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Ramona Manda
 */
public class TemplateParameterEditorDialog extends ModalDialog {

    private TemplateParameterDescriptor parameter;
    private ToolAdapterOperatorDescriptor operator;
    private PropertyMemberUIWrapper fileWrapper;
    private AppContext appContext;
    private JTextArea fileContentArea = new JTextArea("", 10, 10);
    OperatorParametersTable paramsTable;

    public TemplateParameterEditorDialog(AppContext appContext, String title, String helpID) {
        super(appContext.getApplicationWindow(), title, ID_OK_CANCEL, helpID);
        this.appContext = appContext;
    }

    public TemplateParameterEditorDialog(AppContext appContext, String helpID, TemplateParameterDescriptor parameter, PropertyMemberUIWrapper fileWrapper) {
        this(appContext, parameter.getName(), helpID);
        this.parameter = parameter;
        this.operator = new ToolAdapterOperatorDescriptor("OperatorForParameters", ToolAdapterOp.class);
        for(ToolParameterDescriptor param : parameter.getToolParameterDescriptors()) {
            this.operator.getToolParameterDescriptors().add(new TemplateParameterDescriptor(param));
        }
        this.fileWrapper = fileWrapper;
        setContent(createMainPanel());
    }

    public JPanel createParametersPanel() {
        JPanel paramsPanel = new JPanel();
        BoxLayout layout = new BoxLayout(paramsPanel, BoxLayout.PAGE_AXIS);
        paramsPanel.setLayout(layout);
        AbstractButton addParamBut = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/s2tbx/resources/images/icons/Add16.png"),
                false);
        addParamBut.setAlignmentX(Component.LEFT_ALIGNMENT);
        paramsPanel.add(addParamBut);

        paramsTable =  new OperatorParametersTable(this.operator, appContext);
        JScrollPane tableScrollPane = new JScrollPane(paramsTable);
        tableScrollPane.setPreferredSize(new Dimension(500, 130));
        tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        paramsPanel.add(tableScrollPane);
        addParamBut.addActionListener(e -> {
            paramsTable.addParameterToTable(new TemplateParameterDescriptor("parameterName", String.class));
        });
        TitledBorder title = BorderFactory.createTitledBorder("Template Parameters");
        paramsPanel.setBorder(title);
        return paramsPanel;
    }

    private JPanel createMainPanel(){

        BorderLayout layout = new BorderLayout();
        JPanel mainPanel = new JPanel(layout);
        mainPanel.setPreferredSize(new Dimension(800, 550));

        JPanel filePanel = new JPanel();
        filePanel.add(new JLabel("File:"));
        try {
            JComponent fileEditor = this.fileWrapper.getUIComponent();
            fileEditor.setPreferredSize(new Dimension(770, 25));
            filePanel.add(fileEditor);
        } catch (Exception e) {
            //TODO error
            e.printStackTrace();
        }
        this.fileWrapper.getContext().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateFileAreaContent();
            }
        });

        mainPanel.add(filePanel, BorderLayout.PAGE_START);
        mainPanel.add(new JScrollPane(fileContentArea), BorderLayout.CENTER);
        updateFileAreaContent();
        mainPanel.add(createParametersPanel(), BorderLayout.PAGE_END);

        return mainPanel;
    }

    private void updateFileAreaContent(){
        byte[] encoded = new byte[0];
        String result = null;
        try {
            File defaultValue = fileWrapper.getContext().getPropertySet().getProperty(this.parameter.getName()).getValue();
            if(defaultValue.exists()) {
                encoded = Files.readAllBytes(Paths.get((defaultValue).getAbsolutePath()));
                result = new String(encoded, Charset.defaultCharset());
            } else {
                //if the file does not exist, it keeps the old content, in case the user wants to save in the new file
                result = fileContentArea.getText();
            }
        } catch (IOException e) {
            //TODO file could not be read
            e.printStackTrace();
        } catch (Exception e) {
            //TODO value not available
            e.printStackTrace();
        }
        if(result != null){
            fileContentArea.setText(result);
            fileContentArea.setCaretPosition(0);
        } else {
            //TODO error message
            fileContentArea.setText("ERROR!!!");
        }
    }

    @Override
    protected void onOK() {
        super.onOK();
        //set value
        File defaultValue = fileWrapper.getContext().getPropertySet().getProperty(this.parameter.getName()).getValue();
        this.parameter.setDefaultValue(defaultValue.getAbsolutePath());
        //save parameters
        parameter.getToolParameterDescriptors().clear();
        for(TemplateParameterDescriptor subparameter : operator.getToolParameterDescriptors()){
            if(paramsTable.getBindingContext().getBinding(subparameter.getName()) == null){
                //TODO why is this happening???
            } else {
                if(paramsTable.getBindingContext().getBinding(subparameter.getName()).getPropertyValue() != null) {
                    subparameter.setDefaultValue(paramsTable.getBindingContext().getBinding(subparameter.getName()).getPropertyValue().toString());
                }
            }
            parameter.addParameterDescriptor(subparameter);
        }
        //save file content
        try {
            ToolAdapterIO.saveFileContent(defaultValue, fileContentArea.getText());
        } catch (IOException e) {
            //TODO file could not be saved
            e.printStackTrace();
        }
    }
}
