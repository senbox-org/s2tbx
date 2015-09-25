package org.esa.s2tbx.dataio.s2.masks;

import java.awt.*;

/**
 * S2-MSI Masks model
 *
 * @author J. Malik
 */
public enum MaskInfo {

    MSK_DETFOO(
            "MSK_DETFOO",
            "DETECTOR_FOOTPRINT",
            "Detector footprint mask",
            null,
            "detector_footprint",
            MaskInfo.L1C | MaskInfo.L2A,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_NODATA_NODATA(
            "MSK_NODATA",
            "QT_NODATA_PIXELS",
            "Radiometric quality mask",
            "No–data pixels",
            "nodata",
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_NODATA_CROSSTALK(
            "MSK_NODATA",
            "QT_PARTIALLY_CORRECTED_PIXELS",
            "Radiometric quality mask",
            "Pixels partially corrected during cross-talk processing",
            "partially_corrected_crosstalk",
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_SATURA_L1A(
            "MSK_SATURA",
            "QT_SATURATED_PIXELS_L1A",
            "Radiometric quality mask",
            "Saturated pixels before on-ground radiometric processing",
            "saturated_l1a",
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_SATURA_L1B(
            "MSK_SATURA",
            "QT_SATURATED_PIXELS_L1B",
            "Radiometric quality mask",
            "Saturated pixels after on-ground radiometric processing",
            "saturated_l1b",
            MaskInfo.L1B | MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_DEFECT(
            "MSK_DEFECT",
            "QT_DEFECTIVE_PIXELS",
            "Radiometric quality mask",
            "Defective pixels (matching defective columns)",
            "defective",
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_TECQUA_ANC_LOST(
            "MSK_TECQUA",
            "ANC_LOST",
            "Technical quality mask",
            "Ancillary lost data",
            "ancillary_lost",
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_TECQUA_ANC_DEG(
            "MSK_TECQUA",
            "ANC_DEG",
            "Technical quality mask",
            "Ancillary degraded data",
            "ancillary_degraded",
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_TECQUA_MSI_LOST(
            "MSK_TECQUA",
            "MSI_LOST",
            "Technical quality mask",
            "MSI lost data",
            "msi_lost",
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_TECQUA_MSI_DEG(
            "MSK_TECQUA",
            "MSI_DEG",
            "Technical quality mask",
            "MSI degraded data",
            "msi_degraded",
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_CLOLOW(
            "MSK_CLOLOW",
            "CLOUD_INV",
            "Coarse cloud mask",
            null,
            "coarse_cloud",
            MaskInfo.L1A | MaskInfo.L1B,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_CLOUDS_OPAQUE(
            "MSK_CLOUDS",
            "OPAQUE",
            "Finer cloud mask",
            "Opaque clouds",
            "opaque_clouds",
            MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_CLOUDS_CIRRUS(
            "MSK_CLOUDS",
            "CIRRUS",
            "Finer cloud mask",
            "Cirrus clouds",
            "cirrus_clouds",
            MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_LAND(
            "MSK_LANWAT",
            "LAND",
            "Land mask",
            null,
            "land",
            MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_WATER(
            "MSK_LANWAT",
            "WATER",
            "Water mask",
            null,
            "water",
            MaskInfo.L1C,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY);

    private final String mainType;
    private final String subType;
    private final String mainDescription;
    private final String subDescription;
    private final String snapName;
    private final int levels;
    private final Color color;
    private final double transparency;

    public static final int L1A = (1 << 0);
    public static final int L1B = (1 << 1);
    public static final int L1C = (1 << 2);
    public static final int L2A = (1 << 3);
    public static final int L2B = (1 << 4);
    public static final int L3 = (1 << 5);

    private static final double DEFAULT_TRANSPARENCY = 0.8;

    MaskInfo(String mainType, String subType, String mainDescription, String subDescription, String snapName, int levels, Color color, double transparency) {
        this.mainType = mainType;
        this.subType = subType;
        this.mainDescription = mainDescription;
        this.subDescription = subDescription;
        this.snapName = snapName;
        this.levels = levels;
        this.color = color;
        this.transparency = transparency;
    }

    public String getMainType() {
        return mainType;
    }

    public String getSubType() {
        return subType;
    }

    public String getTypeForBand(String bandName) {
        return String.format("%s_%s", snapName, bandName);
    }

    public String getDescriptionForBand(String bandName) {
        String description;
        if (subDescription == null) {
            description = String.format("%s - %s", mainDescription, bandName);
        }
        else {
            description = String.format("%s - %s - %s", mainDescription, subDescription, bandName);
        }
        return description;
    }

    public Color getColor() {
        return color;
    }

    public double getTransparency() {
        return transparency;
    }

    public boolean isPresentAtLevel(int level) {
        return (levels & level) != 0;
    }

}
