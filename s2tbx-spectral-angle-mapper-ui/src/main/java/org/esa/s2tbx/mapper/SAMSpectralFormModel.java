package org.esa.s2tbx.mapper;


import org.esa.s2tbx.mapper.util.SpectrumInput;
import org.esa.s2tbx.mapper.util.SpectrumCsvIO;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.tango.TangoIcons;
import org.esa.snap.ui.AppContext;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeSupport;

/**
 * @author Dumitrascu Razvan.
 */

public class SAMSpectralFormModel {
    private DefaultListModel<SpectrumInput> spectrumListModel;
    private DefaultListSelectionModel spectrumListSelectionModel;
    private int selectedSpectrumIndex;
    private final AppContext appContext;

    private Action addAction = new AddAction();
    private Action removeAction = new RemoveAction();
    private PropertyChangeSupport propertyChangeSupport;

    public SAMSpectralFormModel(AppContext appContext) {
        this.appContext = appContext;
        spectrumListModel = new DefaultListModel<>();
        spectrumListSelectionModel = new DefaultListSelectionModel();
        spectrumListSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        spectrumListSelectionModel.addListSelectionListener(new EndmemberListSelectionListener());
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    public ListModel<SpectrumInput> getSpectrumListModel() {
        return spectrumListModel;

    }

    public DefaultListSelectionModel getSpectrumListSelectionModel() {
        return spectrumListSelectionModel;
    }

    public Action getAddAction() {
        return addAction;
    }

    public Action getRemoveAction() {
        return removeAction;
    }

    private class AddAction extends AbstractAction {

        public AddAction() {
            super("Add");
            putValue(LARGE_ICON_KEY, TangoIcons.actions_list_add(TangoIcons.Res.R16));
            putValue(SHORT_DESCRIPTION, "Add spectrum csv");
        }

        public void actionPerformed(ActionEvent e) {
            //ensureDefaultDirSet();
            SpectrumInput[] csvSpectrumInput = SpectrumCsvIO.readGraphs(null,
                    "Add spectrum csv",
                    new SnapFileFilter[]{SpectrumCsvIO.CSV_FILE_FILTER},
                    appContext.getPreferences());
           // SpectrumInput[] spectrum = convertGraphsToSpectrum(diagramGraphs);
            for (SpectrumInput spectrumInputCSV : csvSpectrumInput) {
                spectrumListModel.addElement(spectrumInputCSV);
            }
        }
    }

    private class RemoveAction extends AbstractAction {

        public RemoveAction() {
            super("Remove");
            putValue(LARGE_ICON_KEY, TangoIcons.actions_list_remove(TangoIcons.Res.R16));
            putValue(SHORT_DESCRIPTION, "Remove SpectrumInput");
        }

        public void actionPerformed(ActionEvent e) {
            int index = selectedSpectrumIndex;
            if (index >= 0) {
                setSelectedSpectrumIndex(-1);
                spectrumListModel.removeElementAt(index);
            }

        }
    }
    public void setSelectedSpectrumIndex(int index) {
        int oldIndex = selectedSpectrumIndex;
        if (oldIndex == index) {
            return;
        }
        selectedSpectrumIndex = index;
        propertyChangeSupport.firePropertyChange("selectedSpectrumIndex", oldIndex, selectedSpectrumIndex);
    }

    private class EndmemberListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                if (spectrumListSelectionModel.isSelectionEmpty()) {
                    setSelectedSpectrumIndex(-1);
                } else {
                    setSelectedSpectrumIndex(spectrumListSelectionModel.getLeadSelectionIndex());
                }
            }
        }
    }
}
