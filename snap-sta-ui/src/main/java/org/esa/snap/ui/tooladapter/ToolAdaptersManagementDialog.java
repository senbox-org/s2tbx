package org.esa.snap.ui.tooladapter;

import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterConstants;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterIO;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterOp;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterRegistry;
import org.esa.snap.framework.ui.AppContext;
import org.esa.snap.framework.ui.ModalDialog;
import org.esa.snap.framework.ui.UIUtils;
import org.esa.snap.framework.ui.tool.ToolButtonFactory;
import org.esa.snap.ui.tooladapter.interfaces.ToolAdapterExecutionDialog;
import org.esa.snap.ui.tooladapter.utils.OperatorsTableModel;
import org.esa.snap.ui.tooladapter.utils.ToolAdapterActionRegistrar;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Dialog that allows the management (create, edit, remove and execute) of external
 * tool adapters
 *
 * @author Ramona Manda
 * @author Cosmin Cara
 */
@NbBundle.Messages({
        "ToolTipNewOperator_Text=Define new operator",
        "ToolTipCopyOperator_Text=Duplicate the selected operator",
        "ToolTipEditOperator_Text=Edit the selected operator",
        "ToolTipExecuteOperator_Text=Execute the selected operator",
        "ToolTipDeleteOperator_Text=Delete the selected operator(s)",
        "Icon_New=/org/esa/snap/resources/images/icons/New24.gif",
        "Icon_Copy=/org/esa/snap/resources/images/icons/Copy24.gif",
        "Icon_Edit=/org/esa/snap/resources/images/icons/Edit24.gif",
        "Icon_Execute=/org/esa/snap/resources/images/icons/Update24.gif",
        "Icon_Remove=/org/esa/snap/resources/images/icons/Remove16.gif"

})
public class ToolAdaptersManagementDialog extends ModalDialog {

    private AppContext appContext;
    private JTable operatorsTable = null;

    public ToolAdaptersManagementDialog(AppContext appContext, String title, String helpID) {
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

        AbstractButton newButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_New()), false);
        newButton.setToolTipText(Bundle.ToolTipNewOperator_Text());
        newButton.addActionListener(e -> {
            close();
            ToolAdapterOperatorDescriptor newOperatorSpi = new ToolAdapterOperatorDescriptor(ToolAdapterConstants.OPERATOR_NAMESPACE + "DefaultOperatorName", ToolAdapterOp.class, "DefaultOperatorName", null, null, null, null, null);
            ToolAdapterEditorDialog dialog = new ToolAdapterEditorDialog(appContext, getHelpID(), newOperatorSpi, true);
            dialog.show();
        });
        panel.add(newButton);

        AbstractButton copyButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_Copy()),
                false);
        copyButton.setToolTipText(Bundle.ToolTipCopyOperator_Text());
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
            ToolAdapterOperatorDescriptor duplicatedOperatorSpi = new ToolAdapterOperatorDescriptor(operatorDesc, opName, opAlias);
            ToolAdapterEditorDialog dialog = new ToolAdapterEditorDialog(appContext, getHelpID(), duplicatedOperatorSpi, newNameIndex);
            dialog.show();
        });
        panel.add(copyButton);

        AbstractButton editButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_Edit()), false);
        editButton.setToolTipText(Bundle.ToolTipEditOperator_Text());
        editButton.addActionListener(e -> {
            close();
            ToolAdapterOperatorDescriptor operatorDesc = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
            ToolAdapterEditorDialog dialog = new ToolAdapterEditorDialog(appContext, getHelpID(), operatorDesc, false);
            dialog.show();
        });
        panel.add(editButton);

        AbstractButton runButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_Execute()), false);
        runButton.setToolTipText(Bundle.ToolTipExecuteOperator_Text());
        runButton.addActionListener(e -> {
            close();
            ToolAdapterOperatorDescriptor operatorSpi = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
            final ToolAdapterExecutionDialog operatorDialog = new ToolAdapterExecutionDialog(
                    operatorSpi,
                    appContext,
                    operatorSpi.getLabel(),
                    getHelpID());
            operatorDialog.show();
        });
        panel.add(runButton);

        AbstractButton delButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_Remove()), false);
        delButton.setToolTipText(Bundle.ToolTipDeleteOperator_Text());
        delButton.addActionListener(e -> {
            close();
            ToolAdapterOperatorDescriptor descriptor = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
            ToolAdapterActionRegistrar.removeOperatorMenu(descriptor);
            ToolAdapterIO.removeOperator(descriptor);
        });
        panel.add(delButton);

        return panel;
    }

    private JTable getOperatorsTable() {
        java.util.List<ToolAdapterOperatorDescriptor> toolboxSpis = new ArrayList<>();
        toolboxSpis.addAll(ToolAdapterRegistry.INSTANCE.getOperatorMap().values()
                                .stream()
                                .map(e -> (ToolAdapterOperatorDescriptor)e.getOperatorDescriptor())
                                .collect(Collectors.toList()));
        toolboxSpis.sort((o1, o2) -> o1.getAlias().compareTo(o2.getAlias()));
        OperatorsTableModel model = new OperatorsTableModel(toolboxSpis);
        operatorsTable = new JTable(model);
        operatorsTable.getColumnModel().getColumn(0).setMaxWidth(20);
        operatorsTable.getColumnModel().getColumn(1).setMaxWidth(250);
        operatorsTable.getColumnModel().getColumn(2).setMaxWidth(500);
        operatorsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        operatorsTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int selectedRow = operatorsTable.getSelectedRow();
                    operatorsTable.getModel().setValueAt(true, selectedRow, 0);
                    close();
                    ToolAdapterOperatorDescriptor operatorDesc = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
                    ToolAdapterEditorDialog dialog = new ToolAdapterEditorDialog(appContext, getHelpID(), operatorDesc, false);
                    dialog.show();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        return operatorsTable;
    }


}
