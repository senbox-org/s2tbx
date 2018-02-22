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

package org.esa.s2tbx.dataio.s2;

/**
 *
 */
public enum S2BandConstants {

    B1("B1", "B01", 0, 414, 472, 443),
    B2("B2", "B02", 1, 425, 555, 490),
    B3("B3", "B03", 2, 510, 610, 560),
    B4("B4", "B04", 3, 617, 707, 665),
    B5("B5", "B05", 4, 625, 722, 705),
    B6("B6", "B06", 5, 720, 760, 740),
    B7("B7", "B07", 6, 741, 812, 783),
    B8("B8", "B08", 7, 752, 927, 842),
    B8A("B8A", "B8A", 8, 823, 902, 865),
    B9("B9", "B09", 9, 903, 982, 945),
    B10("B10", "B10", 10, 1338, 1413, 1375),
    B11("B11", "B11", 11, 1532, 1704, 1610),
    B12("B12", "B12", 12, 2035, 2311, 2190);

    private String physicalName;
    private String filenameBandId;
    private int bandIndex;
    private double wavelengthMin;
    private double wavelengthMax;
    private double wavelengthCentral;

    S2BandConstants(String physicalName,
                    String filenameBandId,
                    int bandIndex,
                    double wavelengthMin,
                    double wavelengthMax,
                    double wavelengthCentral ) {
        this.physicalName = physicalName;
        this.filenameBandId = filenameBandId;
        this.bandIndex = bandIndex;
        this.wavelengthMin = wavelengthMin;
        this.wavelengthMax = wavelengthMax;
        this.wavelengthCentral = wavelengthCentral;
    }


    public String getPhysicalName() {
        return physicalName;
    }

    public String getFilenameBandId() {
        return filenameBandId;
    }

    public int getBandIndex() {
        return bandIndex;
    }

    public double getWavelengthMin() {
        return wavelengthMin;
    }

    public double getWavelengthMax() {
        return wavelengthMax;
    }

    public double getWavelengthCentral() {
        return wavelengthCentral;
    }

    public static S2BandConstants getBand(int bandIndex) {
        for (S2BandConstants band : S2BandConstants.values()) {
            if (band.getBandIndex() == bandIndex) return band;
        }
        return null;
    }

    public static S2BandConstants getBandFromPhysicalName(String physicalName) {
        for (S2BandConstants band : S2BandConstants.values()) {
            if (band.getPhysicalName().equals(physicalName)) return band;
        }
        return null;
    }
}
