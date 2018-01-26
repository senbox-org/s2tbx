package org.esa.s2tbx.mapper;

import com.bc.ceres.swing.TableLayout;
import com.vividsolutions.jts.geom.Polygon;
import org.esa.s2tbx.mapper.common.SpectrumInput;
import org.esa.s2tbx.mapper.common.SpectrumCsvIO;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.image.VectorDataMaskOpImage;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.rcp.util.Dialogs;
import org.esa.snap.tango.TangoIcons;
import org.esa.snap.ui.AppContext;
import org.esa.snap.ui.ModelessDialog;
import org.esa.snap.ui.diagram.DiagramGraphIO;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Dumitrascu Razvan.
 */

class SAMSpectralFormModel {
    private DefaultListModel<SpectrumInput> spectrumListModel;
    private DefaultListSelectionModel spectrumListSelectionModel;
    private int selectedSpectrumIndex;
    private final AppContext appContext;
    private final SpectralAngleMapperForm samForm;

    private Action loadAction = new LoadAction();
    private Action addAction = new AddAction();
    private Action removeAction = new RemoveAction();
    private Action exportAction = new ExportAction();
    private PropertyChangeSupport propertyChangeSupport;
    SAMSpectralFormModel(AppContext appContext, SpectralAngleMapperForm samForm) {
        this.appContext = appContext;
        this.samForm = samForm;
        spectrumListModel = new DefaultListModel<>();
        spectrumListSelectionModel = new DefaultListSelectionModel();
        spectrumListSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        spectrumListSelectionModel.addListSelectionListener(new EndmemberListSelectionListener());
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    ListModel<SpectrumInput> getSpectrumListModel() {
        return spectrumListModel;

    }

    DefaultListSelectionModel getSpectrumListSelectionModel() {
        return spectrumListSelectionModel;
    }

    Action getLoadAction() {
        return loadAction;
    }

    Action getAddAction() {
        return addAction;
    }

    Action getRemoveAction() {
        return removeAction;
    }

    Action getExportAction() {
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
        private ModelessDialog dialog;
        AddAction() {
            super("Add");
            putValue(LARGE_ICON_KEY, TangoIcons.actions_list_add(TangoIcons.Res.R16));
            putValue(SHORT_DESCRIPTION, "Add spectrum");
            xCoordinatesList = new ArrayList<>(1);
            yCoordinatesList = new ArrayList<>(1);
            spectrumName = "";
        }

        public void actionPerformed(ActionEvent e) {
            dialog = new ModelessDialog(null, "Add Spectrum Class", createDialogContent(), ModelessDialog.ID_CANCEL, "");
            dialog.show();
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
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e-> executeInput());
            final Map<String, Product> sourceProducts = samForm.getSourceProductMap();
            Product product = sourceProducts.entrySet().stream().findFirst().get().getValue();
            if (product != null) {
                Mask geometryMask = product.getMaskGroup().get("geometry");
                if(geometryMask != null) {
                    DefaultListModel<SpectrumInput> model = new DefaultListModel<>();
                    JList geometrySpectrumList = new JList<>();
                    geometrySpectrumList.setModel(model);
                    geometrySpectrumList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    geometrySpectrumList.addListSelectionListener(e -> {
                        SpectrumInput spec = (SpectrumInput) geometrySpectrumList.getSelectedValue();
                        setValues(spec);
                    });
                    int polygonLength = ((VectorDataMaskOpImage) (product.getMaskGroup().get("geometry").getSourceImage().getImage(0))).getVectorDataNode().getFeatureCollection().toArray().length;
                    for (int polygonIndex = 0; polygonIndex < polygonLength; polygonIndex++) {
                        SimpleFeatureImpl simpleFeatImp = ((SimpleFeatureImpl) ((VectorDataMaskOpImage) (product.getMaskGroup().get("geometry").getSourceImage().getImage(0))).getVectorDataNode().getFeatureCollection().toArray()[polygonIndex]);
                        Polygon pol = (Polygon) simpleFeatImp.getAttribute(0);
                        if (pol != null) {
                            int[] xCoordinates = new int[pol.getCoordinates().length];
                            int[] yCoordinates = new int[pol.getCoordinates().length];
                            if (product.getSceneGeoCoding() instanceof TiePointGeoCoding) {
                                for (int index = 0; index < pol.getCoordinates().length; index++) {
                                    xCoordinates[index] = (int) pol.getCoordinates()[index].x;
                                    yCoordinates[index] = (int) pol.getCoordinates()[index].y;
                                }
                            } else {
                                for (int index = 0; index < pol.getCoordinates().length; index++) {
                                    double x = pol.getCoordinates()[index].x;
                                    double y = pol.getCoordinates()[index].y;
                                    try {
                                        DirectPosition pos = CRS.findMathTransform(product.getSceneGeoCoding().getMapCRS(), product.getSceneGeoCoding().getImageCRS()).transform(new DirectPosition2D(x, y), null);
                                        double[] values = pos.getCoordinate();
                                        xCoordinates[index] = (int) values[0];
                                        yCoordinates[index] = (int) values[1];
                                    } catch (TransformException | FactoryException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            String polygonName = "POLYGON_" + polygonIndex + "(Starting Coords: " + xCoordinates[0] + " : " + yCoordinates[0] + ")";
                            model.addElement(new SpectrumInput(polygonName, xCoordinates, yCoordinates));
                        }
                    }
                    if (geometrySpectrumList.getModel().getSize() != 0) {
                        content.add(new JLabel("polygons defined in geometry mask"));
                        content.add(new JScrollPane(geometrySpectrumList));
                    }
                }
            }
            content.add(okButton);
            return content;
        }

        private void setValues(SpectrumInput spec) {
            StringBuilder xElements = new StringBuilder();
            StringBuilder yElements = new StringBuilder();
            int length = spec.getXPixelPolygonPositions().length;
            for(int index = 0; index< length-2; index++) {
                    xElements.append(spec.getXPixelPolygonPositions()[index]).append(",");
                    yElements.append(spec.getYPixelPolygonPositions()[index]).append(",");
            }
            xElements.append(spec.getXPixelPolygonPositions()[length - 2]);
            yElements.append(spec.getYPixelPolygonPositions()[length - 2]);

            xCoordinates.setText(xElements.toString());
            yCoordinates.setText(yElements.toString());
        }

        private void executeInput() {
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
            dialog.close();
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

    private void setSelectedSpectrumIndex(int index) {
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
