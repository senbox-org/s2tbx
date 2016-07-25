package org.esa.s2tbx.colourmanipulation;

import org.esa.snap.rcp.windows.ToolTopComponent;
import org.esa.snap.ui.product.ProductSceneView;
import org.netbeans.api.annotations.common.NonNull;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;


/**
 * Created by dmihailescu on 12/07/2016.
 */

/**
 * Brightness and Contrast tool view.
 */

public abstract class AbstractBrightnessContrastTopComponent extends ToolTopComponent {

    private ProductSceneView selectedView;

    private static final String NAME = "D:\\image.jpg";
    private BufferedImage image, copy;
    private JSlider slider = new JSlider();

    protected AbstractBrightnessContrastTopComponent() {

        initUI();
    }

    protected abstract String getTitle();

    protected abstract String getHelpId();

    protected void initUI() {

        PlanarImage pi = JAI.create("fileload", NAME);
        image = pi.getAsBufferedImage();
        copy = pi.getAsBufferedImage();
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(2, 2, 2, 2));
        this.add(new JLabel(new ImageIcon(copy)));
        this.add(slider, BorderLayout.SOUTH);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setValue(slider.getMaximum() / 2);
        slider.addChangeListener(new SliderListener());

        this.setDisplayName(getTitle());
        this.setSelectedView(getSelectedProductSceneView());
    }

    class SliderListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {

            float value = (float) slider.getValue();
            float scaleFactor = 2 * value / slider.getMaximum();
            RescaleOp op = new RescaleOp(scaleFactor, 0, null);
            copy = op.filter(image, copy);
            repaint();
        }
    }

    /**
     * A view opened.
     *
     * @param view The view.
     */
    protected void viewOpened(ProductSceneView view)
    {
        JFrame f = new JFrame("RescaleImage");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(this);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        //rescaleImage.display();
    }

    /**
     * A view closed.
     *
     * @param view The view.
     */
    protected void viewClosed(ProductSceneView view) {
    }

    /**
     * The selected view changed.
     *
     * @param oldView The old selected view. May be null.
     * @param newView The new selected view. May be null.
     */
    protected void viewSelectionChanged(ProductSceneView oldView, ProductSceneView newView) {
    }

    private void setSelectedView(final ProductSceneView newView) {
        ProductSceneView oldView = selectedView;
        if (newView != oldView) {
            if (oldView != null) {
            }
            if (newView != null) {
            }
            selectedView = newView;
            viewSelectionChanged(oldView, newView);
        }
    }

    @Override
    protected void productSceneViewSelected(@NonNull ProductSceneView view) {
        viewOpened(view);
    }

    @Override
    protected void productSceneViewDeselected(@NonNull ProductSceneView view) {
        setSelectedView(null);
    }


}
