package org.esa.s2tbx.tooladapter;

import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.actions.AbstractVisatAction;

/**
 * Sentinel-2 Tool Adapter action class
 *
 * @author Lucian Barbulescu
 */
public class S2tbxToolAdapterAction extends AbstractVisatAction {

    /**
     * Open the external tools selection window
     * @param event the command event
     */
    @Override
    public void actionPerformed(CommandEvent event) {

        //TODO: the alias will be selected based on the tool chosen by the user in the UI

        final S2tbxToolAdapterDialog operatorDialog = new S2tbxToolAdapterDialog(
                "copy",
                getAppContext(),
                "Sentinel-2 Tool Adapter",
                event.getCommand().getHelpId());
        operatorDialog.getJDialog().pack();
        operatorDialog.show();

    }

}
