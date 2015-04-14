package org.esa.s2tbx.ui.tooladapter;

import org.esa.s2tbx.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.s2tbx.framework.gpf.operators.tooladapter.ToolAdapterConstants;
import org.esa.s2tbx.framework.gpf.operators.tooladapter.ToolAdapterOp;
import org.esa.s2tbx.framework.gpf.operators.tooladapter.ToolAdapterOpSpi;
import org.esa.s2tbx.ui.tooladapter.interfaces.ToolAdapterDialog;
import org.esa.s2tbx.ui.tooladapter.utils.OperatorsTableModel;
import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.framework.gpf.descriptor.AnnotationOperatorDescriptor;
import org.esa.snap.framework.ui.AppContext;
import org.esa.snap.framework.ui.ModalDialog;
import org.esa.snap.framework.ui.UIUtils;
import org.esa.snap.framework.ui.tool.ToolButtonFactory;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * @author Ramona Manda
 */
public class ExternalOperatorsEditorDialog extends ModalDialog {

    private AppContext appContext;
    private JTable operatorsTable = null;

    public ExternalOperatorsEditorDialog(AppContext appContext, String title, String helpID) {
        super(appContext.getApplicationWindow(), title, ID_CLOSE, helpID);
        this.appContext = appContext;

        //compute content and other buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JPanel buttonsPanel = getButtonsPanel();
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(buttonsPanel);
        panel.add(new JScrollPane(getOperatorsTable()));
        setContent(panel);
    }

    private JPanel getButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        AbstractButton newButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/s2tbx/resources/images/icons/New24.gif"),
                false);
        newButton.setToolTipText("Define new operator");
        newButton.addActionListener(e -> {
            close();
            ToolAdapterOperatorDescriptor newOperatorSpi = new ToolAdapterOperatorDescriptor(ToolAdapterConstants.OPERATOR_NAMESPACE + "DefaultOperatorName", ToolAdapterOp.class, "DefaultOperatorName", null, null, null, null, null);
            ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, getHelpID(), newOperatorSpi, true);
            dialog.show();
        });
        panel.add(newButton);

        AbstractButton copyButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/s2tbx/resources/images/icons/Copy24.gif"),
                false);
        copyButton.setToolTipText("Duplicate selected operator");
        copyButton.addActionListener(e -> {
            close();

            ToolAdapterOperatorDescriptor operatorDesc = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
            String opName = operatorDesc.getName();
            int newNameIndex = 0;
            while (GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(opName) != null) {
                newNameIndex++;
                opName = operatorDesc.getName() + ToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + newNameIndex;
            }
            String opAlias = operatorDesc.getAlias() + ToolAdapterConstants.OPERATOR_GENERATED_NAME_SEPARATOR + newNameIndex;
            //String descriptorString = ((DefaultOperatorDescriptor) operatorDesc).toXml(ExternalOperatorsEditorDialog.class.getClassLoader());
            //DefaultOperatorDescriptor dod = DefaultOperatorDescriptor.fromXml(new StringReader(descriptorString), "New duplicate operator", ExternalOperatorsEditorDialog.class.getClassLoader());
            ToolAdapterOperatorDescriptor duplicatedOperatorSpi = new ToolAdapterOperatorDescriptor(operatorDesc, opName, opAlias);
            ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, getHelpID(), duplicatedOperatorSpi, newNameIndex);
            dialog.show();
        });
        panel.add(copyButton);

        AbstractButton editButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/s2tbx/resources/images/icons/Edit24.gif"),
                false);
        editButton.setToolTipText("Edit selected operator");
        editButton.addActionListener(e -> {
            close();
            ToolAdapterOperatorDescriptor operatorDesc = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
            ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, getHelpID(), operatorDesc, false);
            dialog.show();
        });
        panel.add(editButton);

        AbstractButton runButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/s2tbx/resources/images/icons/Update24.gif"),
                false);
        runButton.setToolTipText("Execute selected operator");
        runButton.addActionListener(e -> {
            close();
            ToolAdapterOperatorDescriptor operatorSpi = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
            //PropertySet propertySet = new OperatorParameterSupport(operatorSpi).getPropertySet();
            final ToolAdapterDialog operatorDialog = new ToolAdapterDialog(
                    operatorSpi,
                    appContext,
                    operatorSpi.getLabel(),
                    getHelpID());
            operatorDialog.show();
        });
        panel.add(runButton);

        AbstractButton delButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/s2tbx/resources/images/icons/Remove16.gif"),
                false);
        delButton.setToolTipText("Delete selected operator(s)");
        panel.add(delButton);

        return panel;
    }

    private JTable getOperatorsTable() {
        Set<OperatorSpi> spis = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpis();
        java.util.List<ToolAdapterOperatorDescriptor> toolboxSpis = new ArrayList<>();
        spis.stream().filter(p -> p instanceof ToolAdapterOpSpi && p.getOperatorDescriptor().getClass() != AnnotationOperatorDescriptor.class).
                forEach(operator -> toolboxSpis.add((ToolAdapterOperatorDescriptor) operator.getOperatorDescriptor()));
        toolboxSpis.sort(new Comparator<ToolAdapterOperatorDescriptor>() {
            @Override
            public int compare(ToolAdapterOperatorDescriptor o1, ToolAdapterOperatorDescriptor o2) {
                return o1.getAlias().compareTo(o2.getAlias());
            }
        });
        OperatorsTableModel model = new OperatorsTableModel(toolboxSpis);
        operatorsTable = new JTable(model);
        operatorsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        operatorsTable.getColumnModel().getColumn(0).setPreferredWidth(2);
        operatorsTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        return operatorsTable;
    }


}
