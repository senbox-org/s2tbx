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
 * S2-MSI Masks model for sentinel 2 PSD 14.8
 *
 * @author Florian Douziech
 */
public enum MaskInfo148 {

    MSK_DETFOO(
            "MSK_DETFOO",
            new String[]{"DETECTOR_FOOTPRINT-D01","DETECTOR_FOOTPRINT-D02","DETECTOR_FOOTPRINT-D03","DETECTOR_FOOTPRINT-D04",
            "DETECTOR_FOOTPRINT-D05","DETECTOR_FOOTPRINT-D06","DETECTOR_FOOTPRINT-D07","DETECTOR_FOOTPRINT-D08",
            "DETECTOR_FOOTPRINT-D09","DETECTOR_FOOTPRINT-D10","DETECTOR_FOOTPRINT-D11","DETECTOR_FOOTPRINT-D12"},
            "Detector footprint mask",
            new String[]{"detector_footprint-D01","detector_footprint-D02","detector_footprint-D03","detector_footprint-D04",
            "detector_footprint-D05","detector_footprint-D06","detector_footprint-D07","detector_footprint-D08",
            "detector_footprint-D09","detector_footprint-D10","detector_footprint-D11","detector_footprint-D12"},
            new String[]{"detector_footprint-D01","detector_footprint-D02","detector_footprint-D03","detector_footprint-D04",
            "detector_footprint-D05","detector_footprint-D06","detector_footprint-D07","detector_footprint-D08",
            "detector_footprint-D09","detector_footprint-D10","detector_footprint-D11","detector_footprint-D12"},
            true,
            new int[]{MaskInfo148.L1C | MaskInfo148.L2A, MaskInfo148.L1C | MaskInfo148.L2A, MaskInfo148.L1C | MaskInfo148.L2A, MaskInfo148.L1C | MaskInfo148.L2A,
                MaskInfo148.L1C | MaskInfo148.L2A,MaskInfo148.L1C | MaskInfo148.L2A,MaskInfo148.L1C | MaskInfo148.L2A,MaskInfo148.L1C | MaskInfo148.L2A,
                MaskInfo148.L1C | MaskInfo148.L2A,MaskInfo148.L1C | MaskInfo148.L2A,MaskInfo148.L1C | MaskInfo148.L2A, MaskInfo148.L1C | MaskInfo148.L2A},
            new Color[]{Color.BLUE,Color.BLUE.brighter(),Color.GREEN,Color.GREEN.brighter(),
                Color.RED,Color.RED.brighter(),Color.PINK,Color.PINK.brighter(),
                Color.ORANGE,Color.ORANGE.brighter(),Color.MAGENTA,Color.MAGENTA.brighter()},
            new double[]{MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY,
                MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY,
                MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY},
            MaskCategory.DETECTOR_FOOTPRINT,
            new int[]{1,2,3,4,5,6,7,8,9,10,11,12}, false)
    ,MSK_QUALIT(
        "MSK_QUALIT",
        new String[]{"ANC_LOST", "ANC_DEG", "MSI_LOST", "MSI_DEG","QT_DEFECTIVE_PIXELS","QT_NODATA_PIXELS", "QT_PARTIALLY_CORRECTED_PIXELS", "CLOUD_INV"},
        "quality mask",
        new String[]{"Ancillary lost data", "Ancillary degraded data", "MSI lost data", "MSI degraded data","Defective pixels (matching defective columns)","Nodata pixels", "Pixels partially corrected during cross-talk processing","Coarse cloud"},
        new String[]{"ancillary_lost", "ancillary_degraded", "msi_lost", "msi_degraded","defective","nodata", "partially_corrected_crosstalk","coarse_cloud"},
        true,
        new int[]{MaskInfo148.L1A | MaskInfo148.L1B | MaskInfo148.L1C | MaskInfo148.L2A,
            MaskInfo148.L1A | MaskInfo148.L1B | MaskInfo148.L1C | MaskInfo148.L2A,
            MaskInfo148.L1A | MaskInfo148.L1B | MaskInfo148.L1C | MaskInfo148.L2A,
            MaskInfo148.L1A | MaskInfo148.L1B | MaskInfo148.L1C | MaskInfo148.L2A,
            MaskInfo148.L1A | MaskInfo148.L1B | MaskInfo148.L1C | MaskInfo148.L2A,
            MaskInfo148.L1A | MaskInfo148.L1B | MaskInfo148.L1C | MaskInfo148.L2A,
            MaskInfo148.L1A | MaskInfo148.L1B | MaskInfo148.L1C | MaskInfo148.L2A,
            MaskInfo148.L1A | MaskInfo148.L1C
        },
        new Color[]{Color.ORANGE, Color.YELLOW, Color.magenta, Color.RED, Color.RED, Color.CYAN, Color.PINK, Color.RED.darker()},
        new double[]{MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY,
            MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY, MaskInfo148.DEFAULT_TRANSPARENCY},
        MaskCategory.QUALITY,
        new int[]{1,1,1,1,1,1,1,1},true)
        ,MSK_CLASSI(
            "MSK_CLASSI",
            new String[]{"Opaque clouds", "Cirrus clouds","Snow and Ice areas"},
            "quality classification",
            new String[]{"Opaque clouds", "Cirrus clouds", "Snow and Ice areas"},
            new String[]{"opaque_clouds", "cirrus_clouds", "snow_and_ice_areas"},
            false,
            new int[]{MaskInfo148.L1A | MaskInfo148.L2A, MaskInfo148.L1A | MaskInfo148.L2A,MaskInfo148.L1A | MaskInfo148.L2A},
            new Color[]{Color.BLUE, Color.ORANGE, Color.WHITE},
            new double[]{MaskInfo148.DEFAULT_TRANSPARENCY,MaskInfo148.DEFAULT_TRANSPARENCY,MaskInfo148.DEFAULT_TRANSPARENCY},
            MaskCategory.CLASSI,
            new int[]{1,1,1},true)
    ;


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
    private final int[] values;
    private final boolean multiBand;

    public static final int L1A = (1 << 0);
    public static final int L1B = (1 << 1);
    public static final int L1C = (1 << 2);
    public static final int L2A = (1 << 3);
    public static final int L3 = (1 << 4);

    private static final double DEFAULT_TRANSPARENCY = 0.5;

    MaskInfo148(String mainType, String [] subType, String mainDescription, String [] subDescription, String [] snapName, boolean perBand, int [] levels, Color [] color, double [] transparency, MaskCategory category, int[] values, boolean multiBand) {
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
        this.values = values;
        this.multiBand = multiBand;
    }

    public String getMainType() {
        return mainType;
    }

    public String [] getSubType() {
        return subType;
    }

    public String [] getSnapNames() {
        return snapName;
    }

    public String getSnapName(int i) {
        return snapName[i];
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

    public String getSnapNameForOneBand(String bandName) {
        String[] strList = snapName[0].split("-");
        if(strList==null) {
            return null;
        }
        return String.format("%s_%s", strList[0], bandName);
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

    public Color [] getColors() {
        return color;
    }

    public Color getColor(int i) {
        return color[i];
    }

    public double [] getTransparencies() {
        return transparency;
    }

    public double getTransparency(int i) {
        return transparency[i];
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
        CLOUD ("s2tbx.dataio.cloudMasks"),
        QUALITY("s2tbx.dataio.QualityMasks"),
        CLASSI("s2tbx.dataio.ClassificationMasks");
        private final String key;

        MaskCategory(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public int[] getValues() {
        return values;
    }

    public int getValue(int i) {
        return values[i];
    }

    public boolean isMultiBand(){
        return multiBand;
    }
}

