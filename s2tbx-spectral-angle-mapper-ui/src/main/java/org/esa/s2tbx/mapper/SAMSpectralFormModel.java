package org.esa.s2tbx.mapper;


import com.bc.ceres.swing.TableLayout;
import org.esa.s2tbx.mapper.util.SpectrumInput;
import org.esa.s2tbx.mapper.util.SpectrumCsvIO;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.rcp.util.Dialogs;
import org.esa.snap.tango.TangoIcons;
import org.esa.snap.ui.AppContext;
import org.esa.snap.ui.ModalDialog;
import org.esa.snap.ui.diagram.DiagramGraphIO;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Dumitrascu Razvan.
 */

public class SAMSpectralFormModel {
    private DefaultListModel<SpectrumInput> spectrumListModel;
    private DefaultListSelectionModel spectrumListSelectionModel;
    private int selectedSpectrumIndex;
    private final AppContext appContext;

    private Action loadAction = new LoadAction();
    private Action addAction = new AddAction();
    private Action removeAction = new RemoveAction();
    private Action exportAction = new ExportAction();
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

    public Action getLoadAction() {
        return loadAction;
    }

    public Action getAddAction() {
        return addAction;
    }

    public Action getRemoveAction() {
        return removeAction;
    }

    public Action getExportAction() {
        return exportAction;
    }

    private class LoadAction extends AbstractAction {

         LoadAction() {
            super("Load");
            putValue(LARGE_ICON_KEY, TangoIcons.actions_document_open(TangoIcons.Res.R16));
            putValue(SHORT_DESCRIPTION, "Load spectrum csv");
        }

        public void actionPerformed(ActionEvent e) {
            //ensureDefaultDirSet();
            SpectrumInput[] csvSpectrumInput = SpectrumCsvIO.readSpectrum(null,
                    "Load spectrum csv",
                    new SnapFileFilter[]{SpectrumCsvIO.CSV_FILE_FILTER},
                    appContext.getPreferences());
            for (SpectrumInput spectrumInputCSV : csvSpectrumInput) {
                boolean exists = false;
                for(int index = 0; index < spectrumListModel.getSize(); index++){
                    if(spectrumListModel.get(index).getName().equals(spectrumInputCSV.getName())){
                        exists = true;
                    }
                }
                if(!exists) {
                    spectrumListModel.addElement(spectrumInputCSV);
                } else {
                    throw new OperatorException("Spectrum class " + spectrumInputCSV.getName() + " already exists");
                }
            }
        }
    }

    private class AddAction extends AbstractAction {

        private List<Integer> xCoordinatesList;
        private List<Integer> yCoordinatesList;
        private String spectrumName;
        private JTextField xCoordinates;
        private JTextField yCoordinates;

        AddAction() {
            super("Add");
            putValue(LARGE_ICON_KEY, TangoIcons.actions_list_add(TangoIcons.Res.R16));
            putValue(SHORT_DESCRIPTION, "Load spectrum csv");
            xCoordinatesList = new ArrayList<>(1);
            yCoordinatesList = new ArrayList<>(1);
            spectrumName = "";
        }

        public void actionPerformed(ActionEvent e) {
            ModalDialog dialog = new ModalDialog(null, "Add Spectrum Class", ModalDialog.ID_OK_CANCEL, "");
            dialog.setContent(createDialogContent());
            final int show = dialog.show();
            if (show == ModalDialog.ID_OK) {
                if(spectrumName.isEmpty()){
                    Dialogs.showError("spectrum class name must not be empty");
                    return;
                }
                if(xCoordinates.getText().isEmpty()) {
                    Dialogs.showError("tag X Coordinates must not be empty ");
                    return;
                } else {
                    updateTextField(xCoordinatesList, xCoordinates.getText());
                }
                if(yCoordinates.getText().isEmpty()) {
                    Dialogs.showError("tag Y Coordinates must not be empty ");
                    return;
                } else {
                    updateTextField(yCoordinatesList, yCoordinates.getText());
                }
                if(xCoordinatesList.size() != yCoordinatesList.size()) {
                    Dialogs.showError("Invalid number of coordinates. X coordinates and Y coordinates must have the same number of points ");
                    return;
                }
                for(int index = 0; index < spectrumListModel.getSize(); index++){
                    if(spectrumListModel.get(index).getName().equals(spectrumName)){
                        Dialogs.showError("spectrum class name already exists");
                        return;
                    }
                }
                SpectrumInput spec = new SpectrumInput(spectrumName,
                        xCoordinatesList.stream().mapToInt(i->i).toArray(),
                        yCoordinatesList.stream().mapToInt(i->i).toArray());
                spectrumListModel.addElement(spec);
            }
        }


        private JPanel createDialogContent() {
            final TableLayout layout = new TableLayout(2);
            layout.setTableAnchor(TableLayout.Anchor.WEST);
            layout.setTableFill(TableLayout.Fill.HORIZONTAL);
            layout.setTableWeightX(1.0);
            layout.setTableWeightY(1.0);
            layout.setTablePadding(3, 3);
            JPanel content = new JPanel(layout);
            content.setBorder(new EmptyBorder(2, 2, 2, 2));
            content.add(new JLabel("Spectrum Class Name"));
            JTextField spectrumClassName = new JTextField(40);
            spectrumClassName.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    spectrumName = spectrumClassName.getText();
                }
                public void removeUpdate(DocumentEvent e) {
                    spectrumName = spectrumClassName.getText();
                }
                public void changedUpdate(DocumentEvent e) {
                    spectrumName = spectrumClassName.getText();
                }
            });
            content.add(spectrumClassName);
            content.add(new JLabel("X Coordinates"));
            xCoordinates= new JTextField(40);
            xCoordinates.setToolTipText("elements must be comma separated");
            content.add(xCoordinates);
            content.add(new JLabel("Y Coordinates"));
            yCoordinates= new JTextField(40);
            yCoordinates.setToolTipText("elements must be comma separated");
            content.add(yCoordinates);
            return content;
        }

        private void updateTextField(List<Integer> coordinates, String textFieldText) {
            coordinates.clear();
            StringTokenizer str = new StringTokenizer(textFieldText, ",");
            while (str.hasMoreElements()) {
                int thresholdValue = Integer.parseInt(str.nextToken().trim());
                coordinates.add(thresholdValue);
            }
        }
    }

    private class RemoveAction extends AbstractAction {

        RemoveAction() {
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
    private class ExportAction extends AbstractAction {

        ExportAction() {
            super("Exort");
            putValue(LARGE_ICON_KEY, TangoIcons.actions_document_save(TangoIcons.Res.R16));
            putValue(SHORT_DESCRIPTION, "Export Spectrum List as CSV");
        }

        public void actionPerformed(ActionEvent e) {
            List<SpectrumInput> spectrumList = new ArrayList<>(spectrumListModel.getSize());
            for(int index = 0; index < spectrumListModel.size(); index++) {
                spectrumList.add(spectrumListModel.get(index));
            }
            SpectrumCsvIO.writeSpectrumList(null,
                    "Export Spectrum List",
                    new SnapFileFilter[]{DiagramGraphIO.SPECTRA_CSV_FILE_FILTER},
                    appContext.getPreferences(),
                    spectrumList);
        }
    }

    void setSelectedSpectrumIndex(int index) {
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
