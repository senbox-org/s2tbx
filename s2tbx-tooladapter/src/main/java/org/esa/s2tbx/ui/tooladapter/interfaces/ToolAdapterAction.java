package org.esa.s2tbx.ui.tooladapter.interfaces;

import org.esa.s2tbx.ui.tooladapter.ExternalOperatorsEditorDialog;
import org.esa.snap.rcp.actions.AbstractSnapAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

import java.awt.event.ActionEvent;

/**
 * Sentinel-2 Tool Adapter action class
 *
 * @author Lucian Barbulescu
 */
@ActionID(category = "Tools", id = "org.esa.beam.ui.tooladapter.interfaces.BinningOperatorAction")
@ActionRegistration(displayName = "#CTL_ToolAdapterOperatorAction_Text", lazy = false)
@ActionReference(path = "Menu/Tools", position = 10000)
@NbBundle.Messages({
        "CTL_ToolAdapterOperatorAction_Text=Manage external tools",
        "CTL_ToolAdapterOperatorAction_Description=Define adapters for external processes."
})
public class ToolAdapterAction extends AbstractSnapAction {

    public ToolAdapterAction() {
        putValue(NAME, "Manage external tools");
        putValue(SHORT_DESCRIPTION, "Define adapters for external processes");
    }

    /**
     * Open the external tools selection window
     *
     * @param event the command event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        ExternalOperatorsEditorDialog operatorDialog = new ExternalOperatorsEditorDialog(getAppContext(), "External Tools", event.getActionCommand());
        operatorDialog.show();
    }

}
