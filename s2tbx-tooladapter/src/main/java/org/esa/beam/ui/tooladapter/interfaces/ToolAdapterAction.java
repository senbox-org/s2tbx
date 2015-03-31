package org.esa.beam.ui.tooladapter.interfaces;

import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.ui.tooladapter.ExternalOperatorsEditorDialog;
import org.esa.beam.visat.actions.AbstractVisatAction;

/**
 * Sentinel-2 Tool Adapter action class
 *
 * @author Lucian Barbulescu
 */
public class ToolAdapterAction extends AbstractVisatAction {

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
