package org.esa.s2tbx.tooladapter.ui;

import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.BindingContext;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.descriptor.AnnotationOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.DefaultOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;
import org.esa.beam.framework.gpf.ui.OperatorParameterSupport;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterConstants;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterDialog;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterOp;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterOpSpi;
import org.esa.s2tbx.tooladapter.ui.utils.OperatorsTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by ramonag on 1/16/2015.
 */
public class ExternalOperatorsEditorDialog extends ModelessDialog {

    private AppContext appContext;
    private JTable operatorsTable = null;

    public ExternalOperatorsEditorDialog(AppContext appContext, String title, String helpID) {
        super(appContext.getApplicationWindow(), title, ID_CLOSE, helpID);
        this.appContext = appContext;

        //compute content and other buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(getButtonsPanel(), BorderLayout.WEST);
        panel.add(new JScrollPane(getOperatorsTable()));
        setContent(panel);
    }

    private JPanel getButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        AbstractButton newButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/New24.gif"),
                false);
        newButton.setToolTipText("Define new operator");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                S2tbxOperatorDescriptor newOperatorSpi = new S2tbxOperatorDescriptor("DefaulsOperatorName", S2tbxToolAdapterOp.class, null, null, null, null, null, null);
                ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, "Define new Tool", getHelpID(), newOperatorSpi, true);
                dialog.show();
            }
        });
        panel.add(newButton);

        AbstractButton copyButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Copy24.gif"),
                false);
        copyButton.setToolTipText("Duplicate selected operator");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();

                S2tbxOperatorDescriptor operatorDesc = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
                String opName = operatorDesc.getName();
                int newNameIndex = 0;
                while (GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(opName) != null) {
                    newNameIndex++;
                    opName = operatorDesc.getName() + S2tbxToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + newNameIndex;
                }

                //String descriptorString = ((DefaultOperatorDescriptor) operatorDesc).toXml(ExternalOperatorsEditorDialog.class.getClassLoader());
                //DefaultOperatorDescriptor dod = DefaultOperatorDescriptor.fromXml(new StringReader(descriptorString), "New duplicate operator", ExternalOperatorsEditorDialog.class.getClassLoader());
                S2tbxOperatorDescriptor duplicatedOperatorSpi = new S2tbxOperatorDescriptor((DefaultOperatorDescriptor)operatorDesc);
                ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, "Edit Tool", getHelpID(), duplicatedOperatorSpi, newNameIndex);
                dialog.show();
            }
        });
        panel.add(copyButton);

        AbstractButton editButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Edit24.gif"),
                false);
        editButton.setToolTipText("Edit selected operator");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                S2tbxOperatorDescriptor operatorDesc = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
                PropertySet propertySet = new OperatorParameterSupport(operatorDesc).getPropertySet();
                ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, "Edit Tool", getHelpID(), operatorDesc, true);
                dialog.show();
            }
        });
        panel.add(editButton);

        AbstractButton runButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Update24.gif"),
                false);
        runButton.setToolTipText("Execute selected operator");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                S2tbxOperatorDescriptor operatorSpi = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
                PropertySet propertySet = new OperatorParameterSupport(operatorSpi).getPropertySet();
                final S2tbxToolAdapterDialog operatorDialog = new S2tbxToolAdapterDialog(
                        operatorSpi,
                        appContext,
                        "Sentinel-2 Tool Adapter",
                        getHelpID());
                operatorDialog.show();
            }
        });
        panel.add(runButton);

        AbstractButton delButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Remove16.gif"),
                false);
        delButton.setToolTipText("Delete selected operator(s)");
        panel.add(delButton);

        return panel;
    }

    private JTable getOperatorsTable() {
        Set<OperatorSpi> spis = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpis();
        java.util.List<S2tbxOperatorDescriptor> tollboxSpis = new ArrayList<S2tbxOperatorDescriptor>();
        spis.stream().filter(p -> p instanceof S2tbxToolAdapterOpSpi && ((S2tbxToolAdapterOpSpi) p).getOperatorDescriptor().getClass() != AnnotationOperatorDescriptor.class).
                forEach(operator -> tollboxSpis.add((S2tbxOperatorDescriptor)operator.getOperatorDescriptor()));

        OperatorsTableModel model = new OperatorsTableModel(tollboxSpis);
        operatorsTable = new JTable(model);
        operatorsTable.getColumnModel().getColumn(0).setPreferredWidth(5);
        return operatorsTable;
    }


}
