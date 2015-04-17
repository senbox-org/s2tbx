package org.esa.snap.ui.tooladapter;

import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.framework.gpf.descriptor.AnnotationOperatorDescriptor;
import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterConstants;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterOp;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterOpSpi;
import org.esa.snap.framework.ui.AppContext;
import org.esa.snap.framework.ui.ModalDialog;
import org.esa.snap.framework.ui.UIUtils;
import org.esa.snap.framework.ui.tool.ToolButtonFactory;
import org.esa.snap.ui.tooladapter.interfaces.ToolAdapterDialog;
import org.esa.snap.ui.tooladapter.utils.OperatorsTableModel;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * @author Ramona Manda
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

        AbstractButton newButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_New()),
                false);
        newButton.setToolTipText(Bundle.ToolTipNewOperator_Text());
        newButton.addActionListener(e -> {
            close();
            ToolAdapterOperatorDescriptor newOperatorSpi = new ToolAdapterOperatorDescriptor(ToolAdapterConstants.OPERATOR_NAMESPACE + "DefaultOperatorName", ToolAdapterOp.class, "DefaultOperatorName", null, null, null, null, null);
            ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, getHelpID(), newOperatorSpi, true);
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
            //String descriptorString = ((DefaultOperatorDescriptor) operatorDesc).toXml(ExternalOperatorsEditorDialog.class.getClassLoader());
            //DefaultOperatorDescriptor dod = DefaultOperatorDescriptor.fromXml(new StringReader(descriptorString), "New duplicate operator", ExternalOperatorsEditorDialog.class.getClassLoader());
            ToolAdapterOperatorDescriptor duplicatedOperatorSpi = new ToolAdapterOperatorDescriptor(operatorDesc, opName, opAlias);
            ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, getHelpID(), duplicatedOperatorSpi, newNameIndex);
            dialog.show();
        });
        panel.add(copyButton);

        AbstractButton editButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_Edit()),
                false);
        editButton.setToolTipText(Bundle.ToolTipEditOperator_Text());
        editButton.addActionListener(e -> {
            close();
            ToolAdapterOperatorDescriptor operatorDesc = ((OperatorsTableModel) operatorsTable.getModel()).getFirstCheckedOperator();
            ExternalToolEditorDialog dialog = new ExternalToolEditorDialog(appContext, getHelpID(), operatorDesc, false);
            dialog.show();
        });
        panel.add(editButton);

        AbstractButton runButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_Execute()),
                false);
        runButton.setToolTipText(Bundle.ToolTipExecuteOperator_Text());
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

        AbstractButton delButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon(Bundle.Icon_Remove()),
                false);
        delButton.setToolTipText(Bundle.ToolTipDeleteOperator_Text());
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
