package org.esa.snap.ui.tooladapter.interfaces;

import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.rcp.actions.AbstractSnapAction;
import org.esa.snap.ui.tooladapter.utils.ToolAdapterActionRegistrar;

import java.awt.event.ActionEvent;

/**
 * Action to be performed when a toll adapter menu entry is invoked.
 *
 * @author Cosmin Cara
 */
public class ExecuteToolAdapterAction extends AbstractSnapAction {

    public ExecuteToolAdapterAction() {
        super();
    }

    public ExecuteToolAdapterAction(String label) {
        putValue(NAME, label);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ToolAdapterOperatorDescriptor operatorDescriptor = ToolAdapterActionRegistrar.getActionMap().get(getValue(NAME));
        if (operatorDescriptor != null) {
            final ToolAdapterExecutionDialog operatorDialog = new ToolAdapterExecutionDialog(operatorDescriptor, getAppContext(), operatorDescriptor.getLabel(), getHelpId());
            operatorDialog.show();
        }
    }
}
