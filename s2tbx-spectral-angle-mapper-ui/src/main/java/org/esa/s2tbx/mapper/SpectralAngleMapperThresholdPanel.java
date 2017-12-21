package org.esa.s2tbx.mapper;

import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.esa.snap.ui.AppContext;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Dumitrascu Razvan.
 */

public class SpectralAngleMapperThresholdPanel extends JPanel {

    public static final int TOLERANCE_SLIDER_RESOLUTION = 1000;

    private final AppContext appContext;
    private final SpectralAngleMapperFormModel samModel;
    private BindingContext bindingCtx;
    private List<JTextField> componentList;
    private List<Spectrum> spectrumList;

    boolean adjustingSlider;

    public SpectralAngleMapperThresholdPanel(AppContext appContext, SpectralAngleMapperFormModel samModel) {
        this.appContext = appContext;
        this.samModel = samModel;
        componentList = new ArrayList<>();
        bindingCtx = new BindingContext(samModel.getPropertySet());
        bindingCtx.adjustComponents();
    }

    void updateThresholdComponents(List<Spectrum> spectrumList) {
        this.spectrumList = spectrumList;
        this.removeAll();
        final TableLayout layout = new TableLayout(1);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.BOTH);
        layout.setTableWeightX(1.0);
        layout.setTableWeightY(0.0);
        layout.setTablePadding(2, 2);
        this.setLayout(layout);
        this.setBorder(BorderFactory.createTitledBorder("Spectrum Thresholds"));
        componentList.clear();
        for(Spectrum spectrum : this.spectrumList){
            JLabel label = new JLabel(spectrum.getName());
            JTextField threshold = new JTextField(10);
            JSlider toleranceSlider = new JSlider(0, TOLERANCE_SLIDER_RESOLUTION);
            toleranceSlider.setSnapToTicks(false);
            toleranceSlider.setPaintTicks(false);
            toleranceSlider.setPaintLabels(false);
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
            final PropertyChangeListener minMaxToleranceListener = evt -> adjustSlider(toleranceSlider);

            JPanel valuePanel = new JPanel(new BorderLayout(2, 2));
            valuePanel.add(label, BorderLayout.WEST);
            valuePanel.add(threshold, BorderLayout.CENTER);

            JPanel toleranceSliderPanel = new JPanel(new BorderLayout(2, 2));
            toleranceSliderPanel.add(minToleranceField, BorderLayout.WEST);
            toleranceSliderPanel.add(toleranceSlider, BorderLayout.CENTER);
            toleranceSliderPanel.add(maxToleranceField, BorderLayout.EAST);

            this.add(valuePanel);
            this.add(toleranceSliderPanel);
            this.revalidate();
            this.repaint();
        }
        updateTextField(componentList);
    }

    private void adjustSlider(JSlider toleranceSlider) {
        adjustingSlider = true;
        double tolerance = 0.1;
        toleranceSlider.setValue(toleranceToSliderValue(tolerance));
        adjustingSlider = false;
    }

    private String sliderValueToTolerance(int sliderValue) {

        double minTolerance = 0.0;
        double maxTolerance = 0.5;
        double value = minTolerance + sliderValue * (maxTolerance - minTolerance) / TOLERANCE_SLIDER_RESOLUTION;
        return String.valueOf(value);
    }
    private int toleranceToSliderValue(double tolerance) {
        double minTolerance = 0.0;
        double maxTolerance = 0.5;
        return (int) Math.round(Math.abs(TOLERANCE_SLIDER_RESOLUTION * ((tolerance - minTolerance) / (maxTolerance - minTolerance))));
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
