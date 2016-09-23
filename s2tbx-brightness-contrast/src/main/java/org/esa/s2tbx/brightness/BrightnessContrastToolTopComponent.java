package org.esa.s2tbx.brightness;

/**
 * This component shows the panel containing the three sliders: brightness, contrast, saturation.
 *
 * @author Jean Coravu
 */
import org.esa.s2tbx.brightness.BrightnessContrastData;
import org.esa.s2tbx.brightness.ColorUtils;
import org.esa.s2tbx.brightness.SliderPanel;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.rcp.windows.ToolTopComponent;
import org.esa.snap.ui.product.ProductSceneView;
import org.netbeans.api.annotations.common.NonNull;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

@TopComponent.Description(
        preferredID = "BrightnessContrastTopComponent",
        iconBase = "org/esa/snap/rcp/icons/BrightnessContrast.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(
        mode = "navigator",
        openAtStartup = true,
        position = 60
)
@ActionID(category = "Window", id = "org.esa.snap.rcp.imagebrightness.BrightnessContrastToolTopComponent")
@ActionReferences({
        @ActionReference(path = "Menu/View/Tool Windows"),
        @ActionReference(path = "Toolbars/Tool Windows")
})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BrightnessContrastTopComponent_Name",
        preferredID = "BrightnessContrastTopComponent"
)
@NbBundle.Messages({
        "CTL_BrightnessContrastTopComponent_Name=Brightness and Contrast",
        "CTL_BrightnessContrastTopComponent_HelpId=showBrightnessContrastWnd"
})

public class BrightnessContrastToolTopComponent extends ToolTopComponent {
    private SliderPanel brightnessPanel;
    private SliderPanel contrastPanel;
    private SliderPanel saturationPanel;
    private JLabel messageLabel;
    private PropertyChangeListener imageInfoChangeListener;
    private Map<ProductSceneView, BrightnessContrastData> visibleProductScenes;

    public BrightnessContrastToolTopComponent() {
        super();

        setDisplayName(getTitle());
        setLayout(new BorderLayout());

        StringBuilder str = new StringBuilder();
        str.append("<html>")
                .append("This tool window is used to change the brightness, contrast, saturation of an image.")
                .append("<br>")
                .append("Right now, there is no selected image view.")
                .append("</html>");
        this.messageLabel = new JLabel(str.toString(), JLabel.CENTER);

        ChangeListener sliderChangeListener = event -> applySliderValues();

        this.brightnessPanel = new SliderPanel("Brightness", sliderChangeListener);
        this.contrastPanel = new SliderPanel("Contrast", sliderChangeListener);
        this.saturationPanel = new SliderPanel("Saturation", sliderChangeListener);

        int maximumPreferredWidth = Math.max(this.brightnessPanel.getTitlePreferredWidth(), this.contrastPanel.getTitlePreferredWidth());
        maximumPreferredWidth = Math.max(maximumPreferredWidth, this.saturationPanel.getTitlePreferredWidth());

        this.saturationPanel.setTitlePreferredWidth(maximumPreferredWidth);
        this.contrastPanel.setTitlePreferredWidth(maximumPreferredWidth);
        this.saturationPanel.setTitlePreferredWidth(maximumPreferredWidth);

        this.visibleProductScenes = new HashMap<>();

        this.imageInfoChangeListener = event -> {
            ProductSceneView selectedView = getSelectedProductSceneView();
            sceneImageInfoChangedOutside(selectedView);
        };

        ProductSceneView selectedView = getSelectedProductSceneView();
        if (selectedView == null) {
            displayNoSelectedImageView();
        } else {
            productSceneViewSelected(selectedView);
        }
    }

    @Override
    protected void productSceneViewSelected(@NonNull ProductSceneView selectedSceneView) {
        selectedSceneView.addPropertyChangeListener(ProductSceneView.PROPERTY_NAME_IMAGE_INFO, this.imageInfoChangeListener);

        displaySelectedImageView();

        BrightnessContrastData brightnessContrastData = this.visibleProductScenes.get(selectedSceneView);
        if (brightnessContrastData == null) {
            RasterDataNode[] rasterDataNodes = selectedSceneView.getSceneImage().getRasters();

            ImageInfo imageInfo = ImageManager.getInstance().getImageInfo(rasterDataNodes);
            ImageInfo initialImageInfo = imageInfo.clone();
            brightnessContrastData = new BrightnessContrastData(initialImageInfo);
            for (int i=0; i<rasterDataNodes.length; i++) {
                ImageInfo nodeImageInfo = rasterDataNodes[i].getImageInfo().clone();
                brightnessContrastData.putImageInfo(rasterDataNodes[i], nodeImageInfo);
            }

            this.visibleProductScenes.put(selectedSceneView, brightnessContrastData);
        }
        refreshSliderValues(brightnessContrastData);
    }

    @Override
    protected void productSceneViewDeselected(@NonNull ProductSceneView deselectedSceneView) {
        deselectedSceneView.removePropertyChangeListener(ProductSceneView.PROPERTY_NAME_IMAGE_INFO, this.imageInfoChangeListener);

        displayNoSelectedImageView();
    }

    private String getTitle() {
        return Bundle.CTL_BrightnessContrastTopComponent_Name();
    }

    private void displayNoSelectedImageView() {
        removeAll();
        add(this.messageLabel, BorderLayout.CENTER);
    }

    private void refreshSliderValues(BrightnessContrastData brightnessContrastData) {
        this.brightnessPanel.setSliderValue(brightnessContrastData.getBrightnessSliderValue());
        this.contrastPanel.setSliderValue(brightnessContrastData.getContrastSliderValue());
        this.saturationPanel.setSliderValue(brightnessContrastData.getSaturationSliderValue());
    }

    private void sceneImageInfoChangedOutside(ProductSceneView selectedSceneView) {
        BrightnessContrastData brightnessContrastData = this.visibleProductScenes.get(selectedSceneView);
        RasterDataNode[] rasterDataNodes = selectedSceneView.getSceneImage().getRasters();
        ImageInfo imageInfo = ImageManager.getInstance().getImageInfo(rasterDataNodes);
        ImageInfo initialImageInfo = imageInfo.clone();
        brightnessContrastData.setInitialImageInfo(initialImageInfo);
        brightnessContrastData.setSliderValues(0, 0, 0);
        refreshSliderValues(brightnessContrastData);
    }

    private void displaySelectedImageView() {
        removeAll();

        JPanel colorsPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        colorsPanel.add(this.brightnessPanel);
        colorsPanel.add(this.contrastPanel);
        colorsPanel.add(this.saturationPanel);

        JScrollPane scrollPane = new JScrollPane(colorsPanel);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(scrollPane, BorderLayout.NORTH);
    }

    private void applySliderValues() {
        ProductSceneView selectedSceneView = getSelectedProductSceneView();

        int brightnessValue = this.brightnessPanel.getSliderValue();
        int contrastValue = this.contrastPanel.getSliderValue();
        int saturationValue = this.saturationPanel.getSliderValue();

        BrightnessContrastData brightnessContrastData = this.visibleProductScenes.get(selectedSceneView);
        brightnessContrastData.setSliderValues(brightnessValue, contrastValue, saturationValue);

        // recompute the slider values before applying them to the colors
        brightnessValue = computeSliderValueToApply(brightnessValue, this.brightnessPanel.getSliderMaximumValue(), 255);
        contrastValue = computeSliderValueToApply(contrastValue, this.contrastPanel.getSliderMaximumValue(), 255);
        saturationValue = computeSliderValueToApply(saturationValue, this.saturationPanel.getSliderMaximumValue(), 100);

        RasterDataNode[] rasterDataNodes = selectedSceneView.getSceneImage().getRasters();
        for (int k=0; k<rasterDataNodes.length; k++) {
            RasterDataNode currentDataNode = rasterDataNodes[k];

            ImageInfo currentNodeImageInfo = currentDataNode.getImageInfo();
            ColorPaletteDef currentColorPaletteDef = currentNodeImageInfo.getColorPaletteDef();

            ColorPaletteDef initialColorPaletteDef = brightnessContrastData.getInitialImageInfo(currentDataNode).getColorPaletteDef();
            int pointCount = initialColorPaletteDef.getNumPoints();
            for (int i=0; i<pointCount; i++) {
                ColorPaletteDef.Point initialPoint = initialColorPaletteDef.getPointAt(i);
                int rgb = initialPoint.getColor().getRGB();

                int newRgb = computePixelBrightness(rgb, brightnessValue);
                newRgb = computePixelContrast(newRgb, contrastValue);
                newRgb = computePixelSaturation(newRgb, saturationValue);

                ColorPaletteDef.Point currentPoint = currentColorPaletteDef.getPointAt(i);
                currentPoint.setColor(new Color(newRgb));
            }

            currentDataNode.setImageInfo(currentNodeImageInfo);
        }

        ImageInfo sceneImageInfo = ImageManager.getInstance().getImageInfo(rasterDataNodes);

        RGBChannelDef initialRGBChannelDef = brightnessContrastData.getInitialImageInfo().getRgbChannelDef();
        if (initialRGBChannelDef != null) {
            RGBChannelDef rgbChannelDef = sceneImageInfo.getRgbChannelDef();
            for (int i=0; i<rasterDataNodes.length; i++) {
                RasterDataNode currentDataNode = rasterDataNodes[i];
                if (currentDataNode instanceof Band) {
                    ColorPaletteDef initialColorPaletteDef = brightnessContrastData.getInitialImageInfo(currentDataNode).getColorPaletteDef();
                    ColorPaletteDef currentColorPaletteDef = currentDataNode.getImageInfo().getColorPaletteDef();

                    float firstPercent = computePercent(initialColorPaletteDef.getFirstPoint(), currentColorPaletteDef.getFirstPoint());
                    double min = initialRGBChannelDef.getMinDisplaySample(i);
                    min = min + (min * firstPercent);
                    rgbChannelDef.setMinDisplaySample(i, min);

                    float lastPercent = computePercent(initialColorPaletteDef.getLastPoint(), currentColorPaletteDef.getLastPoint());
                    double max = initialRGBChannelDef.getMaxDisplaySample(i);
                    max = max + (max * lastPercent);
                    rgbChannelDef.setMaxDisplaySample(i, max);
                }
            }
        }

        selectedSceneView.removePropertyChangeListener(ProductSceneView.PROPERTY_NAME_IMAGE_INFO, this.imageInfoChangeListener);
        try {
            selectedSceneView.setImageInfo(sceneImageInfo);
        } finally {
            selectedSceneView.addPropertyChangeListener(ProductSceneView.PROPERTY_NAME_IMAGE_INFO, this.imageInfoChangeListener);
        }
    }

    private static int computeSliderValueToApply(int visibleSliderValue, int maximumVisibleSliderValue, int maximumAllowedValue) {
        float visiblePercent = (float)visibleSliderValue / (float) maximumVisibleSliderValue;
        float percent = Math.round(visiblePercent * maximumAllowedValue);
        return (int)percent;
    }

    private static float computePercent(ColorPaletteDef.Point initialPoint, ColorPaletteDef.Point currentPoint) {
        Color initialColor = initialPoint.getColor();
        Color currentColor = currentPoint.getColor();

        float initialRedPercent = (float)initialColor.getRed() / 255.0f;
        float initialGreenPercent = (float)initialColor.getGreen() / 255.0f;
        float initialBluePercent = (float)initialColor.getBlue() / 255.0f;

        float currentRedPercent = (float)currentColor.getRed() / 255.0f;
        float currentGreenPercent = (float)currentColor.getGreen() / 255.0f;
        float currentBluePercent = (float)currentColor.getBlue() / 255.0f;

        float redPercent = initialRedPercent - currentRedPercent;
        float greenPercent = initialGreenPercent - currentGreenPercent;
        float bluePercent = initialBluePercent - currentBluePercent;

        return (redPercent + greenPercent + bluePercent) / 3.0f;
    }

    private static int checkRGBValue(int v) {
        if (v > 255) {
            return 255;
        }
        if (v < 0) {
            return 0;
        }
        return v;
    }

    private static int computePixelBrightness(int pixel, int sliderValue) {
        int red = ColorUtils.red(pixel) + sliderValue;
        int green = ColorUtils.green(pixel) + sliderValue;
        int blue = ColorUtils.blue(pixel) + sliderValue;

        return ColorUtils.rgba(checkRGBValue(red), checkRGBValue(green), checkRGBValue(blue));
    }

    private static int computePixelSaturation(int pixel, int sliderValue) {
        int red = ColorUtils.red(pixel);
        int green = ColorUtils.green(pixel);
        int blue = ColorUtils.blue(pixel);

        float[] hsv = new float[3];
        Color.RGBtoHSB(red, green, blue, hsv);
        hsv[1] += (sliderValue * 0.01f);
        if (hsv[1] > 1.0f) {
            hsv[1] = 1.0f;
        } else if (hsv[1] < 0.0f) {
            hsv[1] = 0.0f;
        }

        return Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
    }

    private static int computePixelContrast(int pixel, int sliderValue) {
        float factor = (259.0f * (sliderValue + 255.0f)) / (255.0f * (259.0f - sliderValue));
        int red = ColorUtils.red(pixel);
        int green = ColorUtils.green(pixel);
        int blue = ColorUtils.blue(pixel);

        int newRed = (int)(factor * (red - 128) + 128);
        int newGreen = (int)(factor * (green - 128) + 128);
        int newBlue = (int)(factor * (blue - 128) + 128);
        return ColorUtils.rgba(checkRGBValue(newRed), checkRGBValue(newGreen), checkRGBValue(newBlue));
    }
}
