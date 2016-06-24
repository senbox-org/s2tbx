/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.s2.masks;

import org.esa.s2tbx.dataio.s2.ColorIterator;

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
            true,
            MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_NODATA_NODATA(
            "MSK_NODATA",
            "QT_NODATA_PIXELS",
            "Radiometric quality mask",
            "No–data pixels",
            "nodata",
            true,
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_NODATA_CROSSTALK(
            "MSK_NODATA",
            "QT_PARTIALLY_CORRECTED_PIXELS",
            "Radiometric quality mask",
            "Pixels partially corrected during cross-talk processing",
            "partially_corrected_crosstalk",
            true,
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_SATURA_L1A(
            "MSK_SATURA",
            "QT_SATURATED_PIXELS_L1A",
            "Radiometric quality mask",
            "Saturated pixels before on-ground radiometric processing",
            "saturated_l1a",
            true,
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_SATURA_L1B(
            "MSK_SATURA",
            "QT_SATURATED_PIXELS_L1B",
            "Radiometric quality mask",
            "Saturated pixels after on-ground radiometric processing",
            "saturated_l1b",
            true,
            MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_DEFECT(
            "MSK_DEFECT",
            "QT_DEFECTIVE_PIXELS",
            "Radiometric quality mask",
            "Defective pixels (matching defective columns)",
            "defective",
            true,
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_TECQUA_ANC_LOST(
            "MSK_TECQUA",
            "ANC_LOST",
            "Technical quality mask",
            "Ancillary lost data",
            "ancillary_lost",
            true,
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A,
            Color.ORANGE,
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_TECQUA_ANC_DEG(
            "MSK_TECQUA",
            "ANC_DEG",
            "Technical quality mask",
            "Ancillary degraded data",
            "ancillary_degraded",
            true,
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_TECQUA_MSI_LOST(
            "MSK_TECQUA",
            "MSI_LOST",
            "Technical quality mask",
            "MSI lost data",
            "msi_lost",
            true,
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_TECQUA_MSI_DEG(
            "MSK_TECQUA",
            "MSI_DEG",
            "Technical quality mask",
            "MSI degraded data",
            "msi_degraded",
            true,
            MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_CLOLOW(
            "MSK_CLOLOW",
            "CLOUD_INV",
            "Coarse cloud mask",
            null,
            "coarse_cloud",
            true,
            MaskInfo.L1A | MaskInfo.L1B,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_CLOUDS_OPAQUE(
            "MSK_CLOUDS",
            "OPAQUE",
            "Finer cloud mask",
            "Opaque clouds",
            "opaque_clouds",
            false,
            MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY),
    MSK_CLOUDS_CIRRUS(
            "MSK_CLOUDS",
            "CIRRUS",
            "Finer cloud mask",
            "Cirrus clouds",
            "cirrus_clouds",
            false,
            MaskInfo.L1C | MaskInfo.L2A,
            ColorIterator.next(),
            MaskInfo.DEFAULT_TRANSPARENCY);

    private final String mainType;
    private final String subType;
    private final String mainDescription;
    private final String subDescription;
    private final String snapName;
    private final boolean perBand;
    private final int levels;
    private final Color color;
    private final double transparency;

    public static final int L1A = (1 << 0);
    public static final int L1B = (1 << 1);
    public static final int L1C = (1 << 2);
    public static final int L2A = (1 << 3);
    public static final int L3 = (1 << 4);

    private static final double DEFAULT_TRANSPARENCY = 0.5;

    MaskInfo(String mainType, String subType, String mainDescription, String subDescription, String snapName, boolean perBand, int levels, Color color, double transparency) {
        this.mainType = mainType;
        this.subType = subType;
        this.mainDescription = mainDescription;
        this.subDescription = subDescription;
        this.snapName = snapName;
        this.perBand = perBand;
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

    public String getSnapName() {
        return snapName;
    }

    public String getSnapNameForBand(String bandName) {
        return String.format("%s_%s", snapName, bandName);
    }

    public String getDescription() {
        String description;
        if (subDescription == null) {
            description = mainDescription;
        }
        else {
            description = String.format("%s - %s", mainDescription, subDescription);
        }
        return description;
    }

    public String getDescriptionForBand(String bandName) {
        return String.format("%s - %s", getDescription(), bandName);
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

    public boolean isPerBand() {
        return this.perBand;
    }

}
