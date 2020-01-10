package org.esa.s2tbx.dataio.gdal.preferences;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A controller for GDAL native library loader.
 * Used for provide a UI to the strategy with loading GDAL native library.
 *
 * @author Adrian Draghici
 */
@OptionsPanelController.SubRegistration(
        location = "S2TBX",
        displayName = "GDAL Library Loader",
        keywords = "S2TBX, GDAL",
        keywordsCategory = "S2TBX",
        position = 3
)
public class GDALOptionsPanelController extends OptionsPanelController {
    private GDALOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    /**
     * Component should load its data here. You should not do any
     * time-consuming operations inside the constructor, because it
     * blocks initialization of OptionsDialog. Initialization
     * should be implemented in update method.
     * This method is called after {@link #getComponent} method.
     * Update method can be called more than one time for the same instance
     * of JComponent obtained from {@link #getComponent} call.
     */
    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    /**
     * This method is called off EDT when Options Dialog "OK" or "Apply" button is pressed.
     */
    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            getPanel().store();
            changed = false;
        });
    }

    /**
     * This method is called when Options Dialog "Cancel" button is pressed.
     */
    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    /**
     * Should return <code>true</code> if some option value in this
     * category is valid.
     *
     * @return <code>true</code> if some option value in this
     * category is valid
     */
    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    /**
     * Should return <code>true</code> if some option value in this
     * category has been changed.
     *
     * @return <code>true</code> if some option value in this
     * category has been changed
     */
    @Override
    public boolean isChanged() {
        return changed;
    }

    /**
     * Returns visual component representing this options category.
     * This method is called before {@link #update} method.
     *
     * @param masterLookup master lookup composed from lookups provided by
     *                     individual OptionsPanelControllers
     *                     - {@link OptionsPanelController#getLookup}
     * @return visual component representing this options category
     */
    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    /**
     * Get current help context asociated with this panel.
     *
     * @return current help context
     */
    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    /**
     * Registers new listener.
     *
     * @param l a new listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /**
     * Unregisters given listener.
     *
     * @param l a listener to be removed
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }


    private GDALOptionsPanel getPanel() {
        if (panel == null) {
            panel = new GDALOptionsPanel(this);
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
