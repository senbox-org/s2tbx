package org.esa.s2tbx.mapper;

import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import org.esa.s2tbx.mapper.util.SpectrumInput;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.ui.AppContext;
import org.esa.snap.ui.tool.ToolButtonFactory;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import static com.bc.ceres.swing.TableLayout.cell;

/**
 * @author Dumitrascu Razvan.
 */
class SpectralAngleMapperParametersPanel extends JPanel {

    private final AppContext appContext;
    private final SpectralAngleMapperFormModel samModel;
    private final BindingContext bindingCtx;
    private final SpectralAngleMapperForm samForm;
    private JList<String> sourceBandNames;
    JList<SpectrumInput> spectrumList;
    SAMSpectralFormModel formModel;
    SpectralAngleMapperThresholdPanel thresholdPanel;

    private DefaultListModel<String> model;
    SpectralAngleMapperParametersPanel(SpectralAngleMapperForm samForm, AppContext appContext, SpectralAngleMapperFormModel samModel) {
        this.appContext = appContext;
        this.samModel = samModel;
        this.samForm = samForm;
        bindingCtx = new BindingContext(samModel.getPropertySet());
        this.formModel = new SAMSpectralFormModel(this.appContext);
        createUI();
        bindingCtx.adjustComponents();
        bindComponents();
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

    private JPanel CSVSelectionPanel() {
        final TableLayout layout = new TableLayout(2);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.HORIZONTAL);
        layout.setTableWeightX(1.0);
        layout.setTableWeightY(1.0);
        layout.setTablePadding(3, 3);
        final JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createTitledBorder("spectrum CSV file:"));
        spectrumList = new JList<>();
        spectrumList.setModel(formModel.getSpectrumListModel());
        spectrumList.setSelectionModel(formModel.getSpectrumListSelectionModel());
        spectrumList.setPreferredSize(new Dimension(80, 160));
        panel.add(spectrumList);
        AbstractButton addButton = ToolButtonFactory.createButton(formModel.getAddAction(), false);
        AbstractButton removeButton = ToolButtonFactory.createButton(formModel.getRemoveAction(), false);
        GridBagLayout gbl = new GridBagLayout();
        JPanel actionPanel = new JPanel(gbl);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 2;
        gbc.gridy = 0;
        actionPanel.add(addButton, gbc);
        gbc.gridy++;
        actionPanel.add(removeButton, gbc);
        gbc.gridy++;

        JPanel spectrumSelectionPanel = new JPanel(new BorderLayout());
        spectrumSelectionPanel.add(new JScrollPane(spectrumList), BorderLayout.CENTER);
        spectrumSelectionPanel.add(actionPanel, BorderLayout.WEST);
        panel.add(spectrumSelectionPanel);
        return panel;
    }


    private JPanel selectionBandsPanel() {

        final TableLayout layout = new TableLayout(2);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.HORIZONTAL);
        layout.setTableWeightX(1.0);
        layout.setTableWeightY(1.0);
        layout.setTablePadding(3, 3);
        final JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createTitledBorder("Bands Selection"));
        model = new DefaultListModel<>();
        sourceBandNames =  new JList<>();
        sourceBandNames.setModel(model);
        sourceBandNames.setPreferredSize(new Dimension(80, 160));
        panel.add(new JLabel("Spectral source bands:"));
        panel.add(new JScrollPane(sourceBandNames));
        return panel;
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
}
