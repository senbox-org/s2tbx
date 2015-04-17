package org.esa.snap.ui.tooladapter.interfaces;

import org.esa.snap.rcp.actions.AbstractSnapAction;
import org.esa.snap.ui.tooladapter.ExternalOperatorsEditorDialog;
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
@ActionID(category = "Tools", id = "ToolAdapterAction")
@ActionRegistration(displayName = "#CTL_ToolAdapterOperatorAction_Text", lazy = false)
@ActionReference(path = "Menu/Tools", position = 10000)
@NbBundle.Messages({
        "CTL_ToolAdapterOperatorAction_Text=Manage external tools",
        "CTL_ToolAdapterOperatorAction_Description=Define adapters for external processes.",
        "CTL_ExternalOperatorsEditorDialog_Title=External Tools"
})
public class ToolAdapterAction extends AbstractSnapAction {

    public ToolAdapterAction() {
        putValue(NAME, Bundle.CTL_ToolAdapterOperatorAction_Text());
        putValue(SHORT_DESCRIPTION, Bundle.CTL_ToolAdapterOperatorAction_Description());
    }

    /**
     * Open the external tools selection window
     *
     * @param event the command event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        ExternalOperatorsEditorDialog operatorDialog = new ExternalOperatorsEditorDialog(getAppContext(),
                Bundle.CTL_ExternalOperatorsEditorDialog_Title(),
                event.getActionCommand());
        operatorDialog.show();
    }

}
