package org.esa.s2tbx.ui.tooladapter.interfaces;

import org.esa.s2tbx.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.s2tbx.ui.tooladapter.utils.ToolAdapterMenuRegistrar;
import org.esa.snap.rcp.actions.AbstractSnapAction;

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
