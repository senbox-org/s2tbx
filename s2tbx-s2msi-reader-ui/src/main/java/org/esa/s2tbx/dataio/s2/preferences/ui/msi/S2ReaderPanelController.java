package org.esa.s2tbx.dataio.s2.preferences.ui.msi;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by obarrile on 27/06/2016.
 * Updated by Florian Douziech on 20 10 2021
 */

@OptionsPanelController.SubRegistration(
        location = "S2TBX",
        displayName = "Sentinel-2 Reader",
        keywords = "S2TBX,reader",
        keywordsCategory = "S2TBX"
)

public class S2ReaderPanelController extends OptionsPanelController {
    private S2ReaderPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    public void update() {
        getOptionPanel().load();
        changed = false;
    }

    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            getOptionPanel().store();
            changed = false;
        });
    }

    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    public boolean isValid() {
        return getOptionPanel().valid();
    }

    public boolean isChanged() {
        return changed;
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx("s2tbxoptionshelp");
    }

    public JComponent getComponent(Lookup masterLookup) {
        return getOptionPanel();
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private S2ReaderPanel getOptionPanel() {
        if (panel == null) {
            panel = new S2ReaderPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
