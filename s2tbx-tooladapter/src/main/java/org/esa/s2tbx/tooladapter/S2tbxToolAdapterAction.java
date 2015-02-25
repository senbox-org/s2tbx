package org.esa.s2tbx.tooladapter;

import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.actions.AbstractVisatAction;
import org.esa.s2tbx.tooladapter.ui.ExternalOperatorsEditorDialog;

/**
 * Sentinel-2 Tool Adapter action class
 *
 * @author Lucian Barbulescu
 */
public class S2tbxToolAdapterAction extends AbstractVisatAction {

    /**
     * Open the external tools selection window
     *
     * @param event the command event
     */
    @Override
    public void actionPerformed(CommandEvent event) {
        ExternalOperatorsEditorDialog operatorDialog = new ExternalOperatorsEditorDialog(getAppContext(), "External Tools", event.getCommand().getHelpId());
        operatorDialog.show();
    }

}
