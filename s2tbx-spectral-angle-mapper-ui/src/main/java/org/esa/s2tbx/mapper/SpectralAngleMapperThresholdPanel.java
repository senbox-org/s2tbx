package org.esa.s2tbx.mapper;

import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.esa.snap.ui.AppContext;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Dumitrascu Razvan.
 */

public class SpectralAngleMapperThresholdPanel extends JPanel {
    private final AppContext appContext;
    private final SpectralAngleMapperFormModel samModel;
    private BindingContext bindingCtx;
    private List<JTextField> componentList;
    private List<Spectrum> spectrumList;
    private JTextField text;
    public SpectralAngleMapperThresholdPanel(AppContext appContext, SpectralAngleMapperFormModel samModel) {
        this.appContext = appContext;
        this.samModel = samModel;
        componentList = new ArrayList<>();
        bindingCtx = new BindingContext(samModel.getPropertySet());
        bindingCtx.adjustComponents();
    }

    void updateThresholdComponents(List<Spectrum> spectrumList) {
        this.spectrumList = spectrumList;
        for(int index = 0; index<componentList.size() * 2; index++){
            this.remove(0);
        }
        final TableLayout layout = new TableLayout(2);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.HORIZONTAL);
        layout.setTableWeightX(1.0);
        layout.setTableWeightY(0.0);
        layout.setTablePadding(1, 1);
        this.setLayout(layout);
        this.setBorder(BorderFactory.createTitledBorder("Spectrum Thresholds"));
        componentList.clear();
        for(Spectrum spectrum : this.spectrumList){
            JLabel label = new JLabel(spectrum.getName());
            JTextField threshold = new JTextField(10);
            componentList.add(threshold);
            threshold.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    updateTextField(componentList);
                }
                public void removeUpdate(DocumentEvent e) {
                    updateTextField(componentList);
                }
                public void insertUpdate(DocumentEvent e) {
                    updateTextField(componentList);
                }

            });

            threshold.setText("0.0");
            this.add(label);
            this.add(threshold);
            this.revalidate();
            this.repaint();
        }
        updateTextField(componentList);
    }

    private void updateTextField(List<JTextField> componentList)  {
        StringBuilder stringBuilder = new StringBuilder();
        for(JTextField textField : componentList) {
            stringBuilder.append(textField.getText());
            stringBuilder.append(", ");
        }
        try {
            bindingCtx.getPropertySet().getProperty(SpectralAngleMapperFormModel.THRESHOLDS_PROPERTY).setValue(String.valueOf(stringBuilder));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
