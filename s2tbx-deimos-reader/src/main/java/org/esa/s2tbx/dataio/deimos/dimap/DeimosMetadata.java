/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio.deimos.dimap;

import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;

import java.awt.*;

import static org.esa.snap.utils.DateHelper.parseDate;

/**
 * Holder for DIMAP metadata file.
 *
 * @author Cosmin Cara
 */
public class DeimosMetadata extends XmlMetadata {

    private float[] bandGains;
    private float[] bandBiases;

    public DeimosMetadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public String getProductName() {
        String name = getAttributeValue(DeimosConstants.PATH_SOURCE_ID, DeimosConstants.VALUE_NOT_AVAILABLE);
        rootElement.setDescription(name);
        return name;
    }

    public String getProductDescription() {
        String descr = getAttributeValue(DeimosConstants.PATH_SOURCE_DESCRIPTION, DeimosConstants.VALUE_NOT_AVAILABLE);
        if (DeimosConstants.VALUE_NOT_AVAILABLE.equals(descr)) {
            descr = getAttributeValue(DeimosConstants.PATH_SOURCE_ID, DeimosConstants.VALUE_NOT_AVAILABLE);
        }
        rootElement.setDescription(descr);
        return descr;
    }

    @Override
    public String getFormatName() {
        return getAttributeValue(DeimosConstants.PATH_METADATA_FORMAT, DeimosConstants.DIMAP);
    }

    @Override
    public String getMetadataProfile() {
        return getAttributeValue(DeimosConstants.PATH_METADATA_PROFILE, DeimosConstants.DEIMOS);
    }

    @Override
    public int getRasterWidth() {
        if (width == 0) {
            try {
                width = Integer.parseInt(getAttributeValue(DeimosConstants.PATH_NCOLS, DeimosConstants.STRING_ZERO));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_NCOLS);
            }
        }
        return width;
    }

    @Override
    public int getRasterHeight() {
        if (height == 0) {
            try {
                height = Integer.parseInt(getAttributeValue(DeimosConstants.PATH_NROWS, DeimosConstants.STRING_ZERO));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_NROWS);
            }
        }
        return height;
    }

    @Override
    public String[] getRasterFileNames() {
        String path = getAttributeValue(DeimosConstants.PATH_DATA_FILE_PATH, null);
        return (path != null ? new String[] { path.toLowerCase() } : null);
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        return null;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        return null;
    }

    /**
     * Returns the names of the bands found in the metadata file.
     * If the expected metadata nodes are not present, then the default band names
     * are returned (i.e. band_n).
     *
     * @return an array of band names
     */
    public String[] getBandNames() {
        int nBands = getNumBands();
        String[] names = new String[nBands];
        for (int i = 0; i < nBands; i++) {
            names[i] = getAttributeValue(DeimosConstants.PATH_BAND_DESCRIPTION, i, DeimosConstants.DEFAULT_BAND_NAMES[i]);
            if (names[i].contains(" ")) {
                names[i] = names[i].replace(" ", "_");
            }
        }
        return names;
    }

    public String[] getBandUnits() {
        int nBands = getNumBands();
        String[] units = new String[nBands];
        for (int i = 0; i < nBands; i++) {
            units[i] = getAttributeValue(DeimosConstants.PATH_PHYSICAL_UNIT, i, DeimosConstants.DEFAULT_UNIT);
        }
        return units;
    }

    @Override
    public int getNumBands() {
        if (numBands == 0) {
            try {
                numBands = Integer.parseInt(getAttributeValue(DeimosConstants.PATH_NBANDS, "3"));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_NBANDS);
            }
        }
        return numBands;
    }

    public int getNoDataValue() {
        int noData = Integer.MIN_VALUE;
        try {
            noData = Integer.parseInt(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.NODATA_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_INDEX, Integer.toString(Integer.MIN_VALUE)));
        } catch (NumberFormatException e) {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.NODATA_VALUE);
        }
        return noData;
    }

    public Color getNoDataColor() {
        Color color;
        try {
            int red = (int) (DeimosConstants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.NODATA_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_RED_LEVEL, DeimosConstants.STRING_ZERO)));
            int green = (int) (DeimosConstants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.NODATA_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_GREEN_LEVEL, DeimosConstants.STRING_ZERO)));
            int blue = (int) (DeimosConstants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.NODATA_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_BLUE_LEVEL, DeimosConstants.STRING_ZERO)));
            color = new Color(red, green, blue);
        } catch (NumberFormatException e) {
            color = Color.BLACK;
        }
        return color;
    }

    public int getSaturatedPixelValue() {
        int saturatedValue = Integer.MAX_VALUE;
        try {
            saturatedValue = Integer.parseInt(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.SATURATED_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_INDEX, Integer.toString(Integer.MAX_VALUE)));
        } catch (NumberFormatException nfe) {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.SATURATED_VALUE);
        }
        return saturatedValue;
    }

    public Color getSaturatedColor() {
        Color color;
        try {
            int red = (int) (DeimosConstants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.SATURATED_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_RED_LEVEL, DeimosConstants.STRING_ZERO)));
            int green = (int) (DeimosConstants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.SATURATED_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_GREEN_LEVEL, DeimosConstants.STRING_ZERO)));
            int blue = (int) (DeimosConstants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.SATURATED_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_BLUE_LEVEL, DeimosConstants.STRING_ZERO)));
            color = new Color(red, green, blue);
        } catch (NumberFormatException e) {
            color = Color.WHITE;
        }
        return color;
    }

    public ProductData.UTC getCenterTime() {
        ProductData.UTC centerTime = null;
        String stringDate = getAttributeValue(DeimosConstants.PATH_SCENE_CENTER_DATE, null);
        if (stringDate != null) {
            String stringTime = getAttributeValue(DeimosConstants.PATH_SCENE_CENTER_TIME, null);
            if (stringTime == null) {
                warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_SCENE_CENTER_TIME);
                stringTime = "00:00:00";
            }
            centerTime = parseDate(stringDate + " " + stringTime, DeimosConstants.DEIMOS_DATE_FORMAT);
        } else {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_SCENE_CENTER_DATE);
        }
        return centerTime;
    }

    private void extractGainsAndBiases() {
        if (bandGains == null || bandBiases == null) {
            int nBands = getNumBands();
            bandGains = new float[nBands];
            // we extract both, because they are used in conjunction
            bandBiases = new float[nBands];
            try {
                for (int i = 0; i < nBands; i++) {
                    bandBiases[i] = Float.parseFloat(getAttributeValue(DeimosConstants.PATH_PHYSICAL_BIAS, i, DeimosConstants.STRING_ZERO)) * DeimosConstants.UNIT_MULTIPLIER;
                    bandGains[i] = Float.parseFloat(getAttributeValue(DeimosConstants.PATH_PHYSICAL_GAIN, i, DeimosConstants.STRING_ZERO)) * DeimosConstants.UNIT_MULTIPLIER;
                }
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_SPECTRAL_BAND_INFO);
            }
        }
    }

    public String getProcessingLevel() {
        String value = null;
        try {
            value = getAttributeValue(DeimosConstants.PATH_GEOMETRIC_PROCESSING, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_GEOMETRIC_PROCESSING);
        }
        if (value == null)
            value = DeimosConstants.PROCESSING_2T;
        return value;
    }

    public InsertionPoint[] getGeopositionPoints() {
        InsertionPoint[] points = null;
        try {
            String[] dataX = getAttributeValues(DeimosConstants.PATH_TIE_POINT_DATA_X);
            if (dataX != null) {
                String[] dataY = getAttributeValues(DeimosConstants.PATH_TIE_POINT_DATA_Y);
                String[] crsX = getAttributeValues(DeimosConstants.PATH_TIE_POINT_CRS_X);
                String[] crsY = getAttributeValues(DeimosConstants.PATH_TIE_POINT_CRS_Y);
                points = new InsertionPoint[dataX.length];
                for (int i = 0; i < points.length; i++) {
                    points[i] = new InsertionPoint();
                    points[i].x = Float.parseFloat(crsX[i]);
                    points[i].y = Float.parseFloat(crsY[i]);
                    points[i].stepX = Float.parseFloat(dataX[i]);
                    points[i].stepY = Float.parseFloat(dataY[i]);
                }
            }
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_GEOMETRIC_PROCESSING);
        }
        return points;
    }

    public class InsertionPoint {
        public float x;
        public float y;
        public float stepX;
        public float stepY;
    }
}
