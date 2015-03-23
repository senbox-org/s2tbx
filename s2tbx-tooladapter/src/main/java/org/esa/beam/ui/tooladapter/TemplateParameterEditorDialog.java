package org.esa.beam.ui.tooladapter;

import org.esa.beam.framework.gpf.descriptor.TemplateParameterDescriptor;
import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.ToolParameterDescriptor;
import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterOp;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;
import org.esa.beam.ui.tooladapter.utils.OperatorParametersTable;
import org.esa.beam.ui.tooladapter.utils.PropertyMemberUIWrapper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
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

    public TemplateParameterEditorDialog(AppContext appContext, String title, String helpID) {
        super(appContext.getApplicationWindow(), title, ID_OK_CANCEL, helpID);
        this.appContext = appContext;
    }

    public TemplateParameterEditorDialog(AppContext appContext, String helpID, TemplateParameterDescriptor parameter, PropertyMemberUIWrapper fileWrapper) {
        this(appContext, parameter.getName(), helpID);
        this.parameter = parameter;
        this.operator = new ToolAdapterOperatorDescriptor("OperatorForParameters", ToolAdapterOp.class);
        for(ToolParameterDescriptor param : parameter.getToolParameterDescriptors()) {
            this.operator.getToolParameterDescriptors().add(param);
        }
        this.fileWrapper = fileWrapper;
        setContent(createMainPanel());
    }

    public JPanel createParametersPanel() {
        JPanel paramsPanel = new JPanel();
        BoxLayout layout = new BoxLayout(paramsPanel, BoxLayout.PAGE_AXIS);
        paramsPanel.setLayout(layout);
        AbstractButton addParamBut = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Add16.png"),
                false);
        addParamBut.setAlignmentX(Component.LEFT_ALIGNMENT);
        paramsPanel.add(addParamBut);

        OperatorParametersTable paramsTable =  new OperatorParametersTable(this.operator, appContext);
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

    private JPanel createMainPanel(){
        JTextArea fileContentArea = new JTextArea("TEXT", 10, 10);

        BorderLayout layout = new BorderLayout();
        JPanel mainPanel = new JPanel(layout);
        mainPanel.setPreferredSize(new Dimension(800, 550));

        JPanel filePanel = new JPanel();
        filePanel.add(new JLabel("File:"));
        try {
            filePanel.add(this.fileWrapper.getUIComponent());
        } catch (Exception e) {
            //TODO error
            e.printStackTrace();
        }
        this.fileWrapper.getContext().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                byte[] encoded = new byte[0];
                String result = null;
                try {
                    encoded = Files.readAllBytes(Paths.get(((File)evt.getNewValue()).getAbsolutePath()));
                    result = new String(encoded, Charset.defaultCharset());
                } catch (IOException e) {
                    //TODO file could not be read
                    e.printStackTrace();
                }
                if(result != null){
                    fileContentArea.setText(result);
                } else {
                    //TODO error message
                    fileContentArea.setText("ERROR!!!");
                }
            }
        });

        mainPanel.add(filePanel, BorderLayout.PAGE_START);
        mainPanel.add(new JScrollPane(fileContentArea), BorderLayout.CENTER);
        mainPanel.add(createParametersPanel(), BorderLayout.PAGE_END);

        return mainPanel;
    }
}
