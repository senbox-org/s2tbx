package org.esa.s2tbx.mapper;


import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import org.esa.s2tbx.mapper.common.SpectrumInput;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.tango.TangoIcons;
import org.esa.snap.ui.AppContext;
import org.esa.snap.ui.tool.ToolButtonFactory;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * @author Dumitrascu Razvan.
 */
class SpectralAngleMapperParametersPanel extends JPanel {

    private static final String RESAMPLE_NONE = "None";
    private static final String RESAMPLE_LOWEST = "Lowest resolution";
    private static final String RESAMPLE_HIGHEST = "Highest resolution";
    private static final String RESAMPLE_MESSAGE = "Bands will be resampled at the %s resolution";

    private final AppContext appContext;
    private final SpectralAngleMapperFormModel samModel;
    private final BindingContext bindingCtx;
    private final SpectralAngleMapperForm samForm;
    private JList<String> sourceBandNames;
    private JList<SpectrumInput> spectrumList;
    private SAMSpectralFormModel formModel;
    private SpectralAngleMapperThresholdPanel thresholdPanel;

    private String[] resampleTypeValues;
    private String[] upsamplingMethodValues;
    private String[] downsamplingMethodValues;
    private JLabel messageLabel;

    private DefaultListModel<String> model;
    SpectralAngleMapperParametersPanel(SpectralAngleMapperForm samForm, AppContext appContext, SpectralAngleMapperFormModel samModel) {
        this.appContext = appContext;
        this.samModel = samModel;
        this.samForm = samForm;
        bindingCtx = new BindingContext(samModel.getPropertySet());
        this.formModel = new SAMSpectralFormModel(this.appContext);
        initResampleValues();
        createUI();
        bindingCtx.adjustComponents();
        bindComponents();
    }

    private void initResampleValues() {
        ValueSet resampleTypeSet = bindingCtx.getPropertySet().getProperty(SpectralAngleMapperFormModel.RESAMPLE_TYPE_PROPERTY).getDescriptor().getValueSet();
        resampleTypeValues = new String[resampleTypeSet.getItems().length];
        ValueSet upsamplingSet = bindingCtx.getPropertySet().getProperty(SpectralAngleMapperFormModel.UPSAMPLING_PROPERTY).getDescriptor().getValueSet();
        upsamplingMethodValues = new String[upsamplingSet.getItems().length];
        ValueSet downsamplingSet = bindingCtx.getPropertySet().getProperty(SpectralAngleMapperFormModel.DOWNSAMPLING_PROPERTY).getDescriptor().getValueSet();
        downsamplingMethodValues = new String[downsamplingSet.getItems().length];
        for (int index = 0; index < resampleTypeSet.getItems().length; index++) {
            resampleTypeValues[index] = resampleTypeSet.getItems()[index].toString();
        }
        for (int index = 0; index < upsamplingSet.getItems().length; index++) {
            upsamplingMethodValues[index] = upsamplingSet.getItems()[index].toString();
        }
        for (int index = 0; index < downsamplingSet.getItems().length; index++) {
            downsamplingMethodValues[index] = downsamplingSet.getItems()[index].toString();
        }
    }

    private void bindComponents() {
        bindingCtx.bind(SpectralAngleMapperFormModel.REFERENCE_BANDS_PROPERTY,sourceBandNames, true);
        bindingCtx.bind(SpectralAngleMapperFormModel.SPECTRA_PROPERTY, spectrumList, true);
    }

    private void createUI() {
        final TableLayout layout = new TableLayout(1);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.BOTH);
        layout.setTableWeightX(1.0);
        layout.setTableWeightY(0.0);
        layout.setRowWeightY(2, 1.0);
        layout.setTablePadding(3, 3);
        setLayout(layout);
        add(selectionBandsPanel());
        add(resamplingPanel());
        JPanel messagePanel = new JPanel();
        messagePanel.add(new JLabel(TangoIcons.status_dialog_information(TangoIcons.Res.R16)));
        messageLabel = new JLabel(RESAMPLE_MESSAGE);
        messageLabel.setForeground(Color.BLUE);
        messagePanel.add(messageLabel);
        messageLabel.setText("No resample is needed");
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(messagePanel, BorderLayout.CENTER);
        messagePanel.setVisible(true);
        add(messagePanel);
        add(CSVSelectionPanel());
        JButton createThresholdsPanel = new JButton("Set Thresholds");
        createThresholdsPanel.addActionListener(e -> {
            if(this.spectrumList != null){
                thresholdPanel =  samForm.getThresholdPanelInstance();
                thresholdPanel.updateThresholdComponents(this.spectrumList.getSelectedValuesList());
            }
        });
        createThresholdsPanel.setMaximumSize(new Dimension(20, 40));
        layout.setCellColspan(0, 0, 2);
        layout.setCellWeightX(1, 1, 1.0);
        layout.setTableAnchor(TableLayout.Anchor.EAST);
        layout.setTableFill(TableLayout.Fill.NONE);
        add(createThresholdsPanel);
    }


    private JPanel resamplingPanel() {
        final JPanel panel = new JPanel(getTableLayout(2));
        panel.setBorder(BorderFactory.createTitledBorder("Resampling Parameters:"));
        JLabel resampleTypeLabel = new JLabel("Resample Type");
        final JComboBox<String> resampleTypeComboBox = new JComboBox<>(new DefaultComboBoxModel<>(this.resampleTypeValues));
        bindingCtx.bind(SpectralAngleMapperFormModel.RESAMPLE_TYPE_PROPERTY, resampleTypeComboBox);
        samModel.getPropertySet().setValue(SpectralAngleMapperFormModel.RESAMPLE_TYPE_PROPERTY, resampleTypeValues[0]);
        bindingCtx.getPropertySet().getProperty(SpectralAngleMapperFormModel.RESAMPLE_TYPE_PROPERTY).addPropertyChangeListener(evt -> checkResampling());
        JLabel upsamplingMethodlabel = new JLabel("Upsampling Method");
        final JComboBox<String> upsamplingComboBox = new JComboBox<>(new DefaultComboBoxModel<>(this.upsamplingMethodValues));
        upsamplingComboBox.setSelectedItem(this.upsamplingMethodValues[0]);
        bindingCtx.bind(SpectralAngleMapperFormModel.UPSAMPLING_PROPERTY, upsamplingComboBox);
        samModel.getPropertySet().setValue(SpectralAngleMapperFormModel.UPSAMPLING_PROPERTY, upsamplingMethodValues[0]);
        JLabel downsamplingMethodlabel = new JLabel("Downsampling Method");
        final JComboBox<String> downsampligComboBox = new JComboBox<>(new DefaultComboBoxModel<>(this.downsamplingMethodValues));
        downsampligComboBox.setSelectedItem(this.downsamplingMethodValues[0]);
        bindingCtx.bind(SpectralAngleMapperFormModel.DOWNSAMPLING_PROPERTY, downsampligComboBox);
        samModel.getPropertySet().setValue(SpectralAngleMapperFormModel.DOWNSAMPLING_PROPERTY, downsamplingMethodValues[0]);
        panel.add(resampleTypeLabel);
        panel.add(resampleTypeComboBox);
        panel.add(upsamplingMethodlabel);
        panel.add(upsamplingComboBox);
        panel.add(downsamplingMethodlabel);
        panel.add(downsampligComboBox);
        return panel;

    }

    private JPanel CSVSelectionPanel() {
        final JPanel panel = new JPanel(getTableLayout(1));
        panel.setBorder(BorderFactory.createTitledBorder("spectrum CSV file:"));
        spectrumList = new JList<>();
        spectrumList.setModel(formModel.getSpectrumListModel());
        spectrumList.setSelectionModel(formModel.getSpectrumListSelectionModel());
        AbstractButton loadButton = ToolButtonFactory.createButton(formModel.getLoadAction(), false);
        AbstractButton addButton = ToolButtonFactory.createButton(formModel.getAddAction(), false);
        AbstractButton removeButton = ToolButtonFactory.createButton(formModel.getRemoveAction(), false);
        AbstractButton exportButton = ToolButtonFactory.createButton(formModel.getExportAction(), false);
        GridBagLayout gbl = new GridBagLayout();
        JPanel actionPanel = new JPanel(gbl);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 2;
        gbc.gridy = 0;
        actionPanel.add(loadButton, gbc);
        gbc.gridy++;
        actionPanel.add(addButton, gbc);
        gbc.gridy++;
        actionPanel.add(removeButton, gbc);
        gbc.gridy++;
        actionPanel.add(exportButton, gbc);
        gbc.gridy++;
        JPanel spectrumSelectionPanel = new JPanel(new BorderLayout());
        spectrumSelectionPanel.add(new JScrollPane(spectrumList), BorderLayout.CENTER);
        spectrumSelectionPanel.add(actionPanel, BorderLayout.WEST);
        panel.add(spectrumSelectionPanel);
        return panel;
    }


    private JPanel selectionBandsPanel() {
        final JPanel panel = new JPanel(getTableLayout(1));
        panel.setBorder(BorderFactory.createTitledBorder("Bands Selection"));
        model = new DefaultListModel<>();
        sourceBandNames =  new JList<>();
        sourceBandNames.setModel(model);
        sourceBandNames.addListSelectionListener(e -> checkResampling());
        JPanel spectrumSelectionPanel = new JPanel(new BorderLayout());
        spectrumSelectionPanel.add(new JScrollPane(sourceBandNames), BorderLayout.CENTER);
        spectrumSelectionPanel.add(new JLabel("Spectral source bands:"), BorderLayout.WEST);
        panel.add(spectrumSelectionPanel);
        return panel;
    }

    private TableLayout getTableLayout( int columnCount) {
        final TableLayout layout = new TableLayout(columnCount);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.BOTH);
        layout.setTableWeightX(1.0);
        layout.setTablePadding(3, 3);
        return layout;
    }

    private void checkResampling() {
        PropertySet propertySet = this.bindingCtx.getPropertySet();
        final Map<String, Product>sourceProducts = samForm.getSourceProductMap();
        Product product = sourceProducts.entrySet().stream().findFirst().get().getValue();
        boolean needsResampling = isResampleNeeded(product);
        if (!needsResampling) {
            propertySet.setValue(SpectralAngleMapperFormModel.RESAMPLE_TYPE_PROPERTY, RESAMPLE_NONE);
        }
        setMessage(propertySet.getValue(SpectralAngleMapperFormModel.RESAMPLE_TYPE_PROPERTY));
        setEnabled(SpectralAngleMapperFormModel.RESAMPLE_TYPE_PROPERTY, needsResampling);
        if(!needsResampling) {
            messageLabel.setForeground(Color.BLUE);
            messageLabel.setText("No resample is needed");
        }
    }

    private void setMessage(String method) {
        switch (method) {
            case RESAMPLE_LOWEST:
                messageLabel.setText(String.format(RESAMPLE_MESSAGE, "lowest"));
                setEnabled(SpectralAngleMapperFormModel.DOWNSAMPLING_PROPERTY, true);
                setEnabled(SpectralAngleMapperFormModel.UPSAMPLING_PROPERTY, false);
                messageLabel.setForeground(Color.BLUE);
                break;
            case RESAMPLE_HIGHEST:
                messageLabel.setText(String.format(RESAMPLE_MESSAGE, "highest"));
                setEnabled(SpectralAngleMapperFormModel.DOWNSAMPLING_PROPERTY, false);
                setEnabled(SpectralAngleMapperFormModel.UPSAMPLING_PROPERTY, true);
                messageLabel.setForeground(Color.BLUE);
                break;
            case RESAMPLE_NONE:
                messageLabel.setText("Product needs to be resampled first");
                setEnabled(SpectralAngleMapperFormModel.DOWNSAMPLING_PROPERTY, false);
                setEnabled(SpectralAngleMapperFormModel.UPSAMPLING_PROPERTY, false);
                messageLabel.setForeground(Color.RED);
                break;
        }
    }

    private boolean isResampleNeeded(Product product) {

        boolean needsResampling = false;
        if (product != null) {
            int sceneWidth = 0;
            List<String>  bandNames = sourceBandNames.getSelectedValuesList();
            if (bandNames.size() > 0) {
                BitSet bitSet = new BitSet(bandNames.size());
                int idx = 0;
                for (String bandName : bandNames) {
                    Band band = product.getBand(bandName);
                    bitSet.set(idx++, sceneWidth != 0 && sceneWidth != band.getRasterWidth());
                    if (band != null) {
                        sceneWidth = band.getRasterWidth();
                    }
                }
                needsResampling = bitSet.nextSetBit(0) != -1;
            }
        }
        return needsResampling;
    }


     void updateBands(Product product) {

        List<String> bandNames = new ArrayList<>();
        if (product != null) {
            ProductNodeGroup<Band> prod = product.getBandGroup();
            if (prod != null) {
                for (int index = 0; index < prod.getNodeCount(); index++) {
                    Band band = prod.get(index);
                    bandNames.add(band.getName());
                }
            }
            if(this.model != null) {
                this.model.clear();
                for(String band: bandNames){
                    this.model.addElement(band);
                }
            }
        }
    }

    private void setEnabled(String propertyName, boolean value) {
            this.bindingCtx.setComponentsEnabled(propertyName, value);
        }
}
