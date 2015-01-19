package org.esa.s2tbx.tooladapter.ui;

import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.BindingContext;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.descriptor.AnnotationOperatorDescriptor;
import org.esa.beam.framework.gpf.ui.OperatorParameterSupport;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterDialog;
import org.esa.s2tbx.tooladapter.S2tbxToolAdapterOpSpi;
import org.esa.s2tbx.tooladapter.ui.utils.OperatorsTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JPanel getButtonsPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        AbstractButton newButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/New16.gif"),
                false);
        newButton.setToolTipText("Define new operator");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                OperatorSpi operatorSpi = new S2tbxToolAdapterOpSpi();
                PropertySet propertySet = new OperatorParameterSupport(operatorSpi.getOperatorDescriptor()).getPropertySet();
                ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, "Define new Tool", getHelpID(), operatorSpi, new BindingContext(propertySet), true);
                dialog.show();
            }
        });
        panel.add(newButton);

        AbstractButton copyButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Copy16.gif"),
                false);
        copyButton.setToolTipText("Duplicate selected operator");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                OperatorSpi operatorSpi = ((OperatorsTableModel)operatorsTable.getModel()).getFirstCheckedOperator();
                String opName = operatorSpi.getOperatorDescriptor().getName();
                int i = 0;
                while(GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(opName) != null){
                    i++;
                    opName = operatorSpi.getOperatorDescriptor().getName() + "_" + i;
                }
                //TODO
                //write to disk the new operator file and then load, since there is no straight way to modify an operator!
                //then add the new operator to the table, and maybe edit the operator?
                PropertySet propertySet = new OperatorParameterSupport(operatorSpi.getOperatorDescriptor()).getPropertySet();
                ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, "Edit Tool", getHelpID(), operatorSpi, new BindingContext(propertySet), true);
                dialog.show();
            }
        });
        panel.add(copyButton);

        AbstractButton editButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/Edit16.gif"),
                false);
        editButton.setToolTipText("Edit selected operator");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                OperatorSpi operatorSpi = ((OperatorsTableModel)operatorsTable.getModel()).getFirstCheckedOperator();
                PropertySet propertySet = new OperatorParameterSupport(operatorSpi.getOperatorDescriptor()).getPropertySet();
                ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, "Edit Tool", getHelpID(), operatorSpi, new BindingContext(propertySet), true);
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
                OperatorSpi operatorSpi = ((OperatorsTableModel)operatorsTable.getModel()).getFirstCheckedOperator();
                PropertySet propertySet = new OperatorParameterSupport(operatorSpi.getOperatorDescriptor()).getPropertySet();
                final S2tbxToolAdapterDialog operatorDialog = new S2tbxToolAdapterDialog(
                        "copy",
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

    private JTable getOperatorsTable(){
        Set<OperatorSpi> spis = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpis();
        java.util.List<OperatorSpi> tollboxSpis = new ArrayList<OperatorSpi>();
        spis.stream().filter(p -> p instanceof S2tbxToolAdapterOpSpi && ((S2tbxToolAdapterOpSpi)p).getOperatorDescriptor().getClass() != AnnotationOperatorDescriptor.class).forEach(operator -> tollboxSpis.add(operator));

        OperatorsTableModel model = new OperatorsTableModel(tollboxSpis);
        operatorsTable = new JTable(model);
        operatorsTable.getColumnModel().getColumn(0).setPreferredWidth(5);
        return operatorsTable;
    }


}
