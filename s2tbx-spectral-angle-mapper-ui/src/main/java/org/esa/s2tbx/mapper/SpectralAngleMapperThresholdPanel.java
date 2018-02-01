package org.esa.s2tbx.mapper;

import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import org.esa.s2tbx.mapper.common.SpectrumInput;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Dumitrascu Razvan.
 */

public class SpectralAngleMapperThresholdPanel extends JPanel {

    private static final int TOLERANCE_SLIDER_RESOLUTION = 1000;

    private BindingContext bindingCtx;
    private List<JTextField> componentList;

    private boolean adjustingSlider;

    SpectralAngleMapperThresholdPanel(SpectralAngleMapperFormModel samModel) {

        componentList = new ArrayList<>();
        bindingCtx = new BindingContext(samModel.getPropertySet());
        bindingCtx.adjustComponents();
    }

    void updateThresholdComponents(List<SpectrumInput> spectrumInputList) {
        this.removeAll();
        final TableLayout layout = new TableLayout(1);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.BOTH);
        layout.setTableWeightX(1.0);
        layout.setTableWeightY(0.2);
        layout.setTablePadding(2, 2);
        this.setLayout(layout);
        this.setBorder(BorderFactory.createTitledBorder("Spectrum Classes Input Thresholds"));
        JScrollPane scrollPane = new JScrollPane();
        this.setAutoscrolls(true);
        componentList.clear();
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        GridBagLayout gbl = new GridBagLayout();
        JPanel content = new JPanel(gbl);
        gbl.setConstraints(content, gbc);
        for(SpectrumInput spectrumInput : spectrumInputList){
            final JPanel panel = new JPanel(layout);
            panel.setBorder(BorderFactory.createTitledBorder(spectrumInput.getName()));
            JLabel label = new JLabel("Value:  ");
            JTextField threshold = new JTextField(10);
            threshold.setEditable(false);
            threshold.setBorder(BorderFactory.createEmptyBorder());
            JSlider toleranceSlider = new JSlider(0, TOLERANCE_SLIDER_RESOLUTION);
            toleranceSlider.setSnapToTicks(false);
            toleranceSlider.setPaintTicks(false);
            toleranceSlider.setPaintLabels(false);
            toleranceSlider.setFocusable(false);
            toleranceSlider.setBorder(BorderFactory.createEmptyBorder());
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
            toleranceSlider.addChangeListener(e -> {
                if (!adjustingSlider) {
                    int sliderValue = toleranceSlider.getValue();
                    threshold.setText(sliderValueToTolerance(sliderValue));
                }
            });

            threshold.setText("0.25");
            JLabel minToleranceField = new JLabel("0.0");
            JLabel maxToleranceField = new JLabel("0.5");

            JPanel valuePanel = new JPanel(new BorderLayout(2, 2));
            valuePanel.add(label, BorderLayout.WEST);
            valuePanel.add(threshold, BorderLayout.CENTER);

            JPanel toleranceSliderPanel = new JPanel(new BorderLayout(2, 2));
            toleranceSliderPanel.add(minToleranceField, BorderLayout.WEST);
            toleranceSliderPanel.add(toleranceSlider, BorderLayout.CENTER);
            toleranceSliderPanel.add(maxToleranceField, BorderLayout.EAST);

            panel.add(valuePanel);
            panel.add(toleranceSliderPanel);
            panel.revalidate();
            panel.repaint();
            content.add(panel, gbc);
            gbc.gridy++;
        }
        scrollPane.setViewportView(content);
        this.add(scrollPane);
        updateTextField(componentList);
    }


    private String sliderValueToTolerance(int sliderValue) {

        double minTolerance = 0.0;
        double maxTolerance = 0.5;
        double value = minTolerance + sliderValue * (maxTolerance - minTolerance) / TOLERANCE_SLIDER_RESOLUTION;
        return String.valueOf(value);
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
