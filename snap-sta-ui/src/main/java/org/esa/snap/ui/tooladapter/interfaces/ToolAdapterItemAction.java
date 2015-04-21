package org.esa.snap.ui.tooladapter.interfaces;

import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.rcp.actions.AbstractSnapAction;
import org.esa.snap.ui.tooladapter.utils.ToolAdapterMenuRegistrar;

import java.awt.event.ActionEvent;

/**
 * Action to be performed when a toll adapter menu entry is invoked.
 *
 * @author Cosmin Cara
 */
public class ToolAdapterItemAction extends AbstractSnapAction {

    public ToolAdapterItemAction() {
        super();
    }

    public ToolAdapterItemAction(String label) {
        putValue(NAME, label);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ToolAdapterOperatorDescriptor operatorDescriptor = ToolAdapterMenuRegistrar.getActionMap().get(getValue(NAME));
        if (operatorDescriptor != null) {
            final ToolAdapterDialog operatorDialog = new ToolAdapterDialog(operatorDescriptor, getAppContext(), operatorDescriptor.getLabel(), getHelpId());
            operatorDialog.show();
        }
    }
}
