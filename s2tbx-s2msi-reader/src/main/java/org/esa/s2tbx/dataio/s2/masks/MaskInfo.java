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
import org.esa.snap.runtime.Config;

import java.awt.*;
import java.util.prefs.Preferences;

/**
 * S2-MSI Masks model
 *
 * @author J. Malik
 */
public enum MaskInfo {

    MSK_DETFOO(
            "MSK_DETFOO",
            new String[]{"DETECTOR_FOOTPRINT"},
            "Detector footprint mask",
            null,
            new String[]{"detector_footprint"},
            true,
            new int[]{MaskInfo.L1C | MaskInfo.L2A},
            new Color[]{Color.RED},
            new double[]{MaskInfo.DEFAULT_TRANSPARENCY},
            MaskCategory.DETECTOR_FOOTPRINT,
            true),
    MSK_NODATA(
            "MSK_NODATA",
            new String[]{"QT_NODATA_PIXELS", "QT_PARTIALLY_CORRECTED_PIXELS"},
            "Radiometric quality mask",
            new String[]{"Nodata pixels", "Pixels partially corrected during cross-talk processing"},
            new String[]{"nodata", "partially_corrected_crosstalk"},
            true,
            new int[]{MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A, MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A},
            new Color[]{Color.CYAN, Color.PINK},
            new double[]{MaskInfo.DEFAULT_TRANSPARENCY, MaskInfo.DEFAULT_TRANSPARENCY},
            MaskCategory.RADIOMETRIC_QUALITY,
            false),
    MSK_SATURA(
            "MSK_SATURA",
            new String[]{"QT_SATURATED_PIXELS_L1A", "QT_SATURATED_PIXELS_L1B"},
            "Radiometric quality mask",
            new String[]{"Saturated pixels before on-ground radiometric processing", "Saturated pixels after on-ground radiometric processing"},
            new String[]{"saturated_l1a", "saturated_l1b"},
            true,
            new int[]{MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A, MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A},
            new Color[]{Color.BLUE, Color.GREEN},
            new double[]{MaskInfo.DEFAULT_TRANSPARENCY, MaskInfo.DEFAULT_TRANSPARENCY},
            MaskCategory.RADIOMETRIC_QUALITY,
            false),
    MSK_DEFECT(
            "MSK_DEFECT",
            new String[]{"QT_DEFECTIVE_PIXELS"},
            "Radiometric quality mask",
            new String[]{"Defective pixels (matching defective columns)"},
            new String[]{"defective"},
            true,
            new int[]{MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A},
            new Color[]{Color.RED},
            new double[]{MaskInfo.DEFAULT_TRANSPARENCY},
            MaskCategory.RADIOMETRIC_QUALITY,
            false),
    MSK_TECQUA(
            "MSK_TECQUA",
            new String[]{"ANC_LOST", "ANC_DEG", "MSI_LOST", "MSI_DEG"},
            "Technical quality mask",
            new String[]{"Ancillary lost data", "Ancillary degraded data", "MSI lost data", "MSI degraded data"},
            new String[]{"ancillary_lost", "ancillary_degraded", "msi_lost", "msi_degraded"},
            true,
            new int[]{MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A, MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A, MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A, MaskInfo.L1A | MaskInfo.L1B | MaskInfo.L1C | MaskInfo.L2A},
            new Color[]{Color.ORANGE, Color.YELLOW, Color.magenta, Color.RED},
            new double[]{MaskInfo.DEFAULT_TRANSPARENCY, MaskInfo.DEFAULT_TRANSPARENCY, MaskInfo.DEFAULT_TRANSPARENCY, MaskInfo.DEFAULT_TRANSPARENCY},
            MaskCategory.TECHNICAL_QUALITY,
            false),

    MSK_CLOLOW(
            "MSK_CLOLOW",
            new String[]{"CLOUD_INV"},
            "Coarse cloud mask",
            null,
            new String[]{"coarse_cloud"},
            true,
            new int[]{MaskInfo.L1A | MaskInfo.L1B},
            new Color[]{Color.RED.darker()},
            new double[]{MaskInfo.DEFAULT_TRANSPARENCY},
            MaskCategory.CLOUD,
            false),
    MSK_CLOUDS(
            "MSK_CLOUDS",
            new String[]{"OPAQUE", "CIRRUS"},
            "Finer cloud mask",
            new String[]{"Opaque clouds", "Cirrus clouds"},
            new String[]{"opaque_clouds", "cirrus_clouds"},
            false,
            new int[]{MaskInfo.L1C | MaskInfo.L2A, MaskInfo.L1C | MaskInfo.L2A},
            new Color[]{Color.WHITE, Color.ORANGE},
            new double[]{MaskInfo.DEFAULT_TRANSPARENCY, MaskInfo.DEFAULT_TRANSPARENCY},
            MaskCategory.CLOUD,
            false);


    private final String mainType;
    private final String [] subType;
    private final String mainDescription;
    private final String [] subDescription;
    private final String [] snapName;
    private final boolean perBand;
    private final int [] levels;
    private final Color [] color;
    private final double [] transparency;
    private final MaskCategory category;
    private final boolean perPolygon;

    public static final int L1A = (1 << 0);
    public static final int L1B = (1 << 1);
    public static final int L1C = (1 << 2);
    public static final int L2A = (1 << 3);
    public static final int L3 = (1 << 4);

    private static final double DEFAULT_TRANSPARENCY = 0.5;

    MaskInfo(String mainType, String [] subType, String mainDescription, String [] subDescription, String [] snapName, boolean perBand, int [] levels, Color [] color, double [] transparency, MaskCategory category,boolean perPolygon) {
        this.mainType = mainType;
        this.subType = subType;
        this.mainDescription = mainDescription;
        this.subDescription = subDescription;
        this.snapName = snapName;
        this.perBand = perBand;
        this.levels = levels;
        this.color = color;
        this.transparency = transparency;
        this.category = category;
        this.perPolygon = perPolygon;
    }

    public String getMainType() {
        return mainType;
    }

    public String [] getSubType() {
        return subType;
    }

    public String [] getSnapName() {
        return snapName;
    }

    public int [] getLevels() {
        return levels;
    }

    public String getSnapNameForBand(String bandName,int index) {
        if(!validateIndex(index)) {
            return null;
        }
        return String.format("%s_%s", snapName[index], bandName);
    }

    public String getDescription(int index) {

        String description;
        if (subDescription == null || !validateIndex(index)) {
            description = mainDescription;
        }
        else {
            description = String.format("%s - %s", mainDescription, subDescription[index]);
        }
        return description;
    }

    public String getDescriptionForBand(String bandName,int index) {
        if(!validateIndex(index)) {
            return null;
        }
        return String.format("%s - %s", getDescription(index), bandName);
    }

    public String getDescriptionForBandAndDetector(String bandName, String detector,int index) {
        if(!validateIndex(index)) {
            return null;
        }
        return String.format("%s - %s - Detector %s", getDescription(index), bandName, detector);
    }

    public Color [] getColor() {
        return color;
    }

    public double [] getTransparency() {
        return transparency;
    }

    public boolean isPresentAtLevel(int level,String sub) {

        int levels = 0;
        for(int i=0;i<getSubType().length;i++)
        {
            if(getSubType()[i].equals(sub)){
                return (levels & level) != 0;
            }
        }

        return false;
    }

    public boolean isPresentAtLevel(int level) {

        for(int i=0;i<getSubType().length;i++)
        {
            if((this.getLevels()[i] & level) != 0) return true;
        }

        return false;
    }

    public boolean isPerBand() {
        return this.perBand;
    }

    private boolean validateIndex(int index) {
        if(index>=0 && index<subType.length)
            return true;

        return false;
    }

    public boolean isEnabled() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();
        final boolean DEFAULT_MASK_ENABLEMENT = true;
        return preferences.getBoolean(category.getKey(), DEFAULT_MASK_ENABLEMENT);
    }

    public enum MaskCategory {

        DETECTOR_FOOTPRINT ("s2tbx.dataio.detectorFootprintMasks"),
        RADIOMETRIC_QUALITY ("s2tbx.dataio.radiometricQualityMasks"),
        TECHNICAL_QUALITY ("s2tbx.dataio.technicalQualityMasks"),
        CLOUD ("s2tbx.dataio.cloudMasks");

        private final String key;

        MaskCategory(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public boolean isPerPolygon() {
        return this.perPolygon;
    }
}

