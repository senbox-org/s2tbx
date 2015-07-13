/*
 *
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */
package org.esa.s2tbx.dataio.s2;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Nicolas Ducoin
 */
public class S2SpectralInformation {

    private int bandId;
    private String physicalBand;
    private int resolution;
    private double wavelenghtMin;
    private double wavelenghtMax;
    private double wavelenghtCentral;

    private double[] spectralResponseValues;

    public S2SpectralInformation() {
        spectralResponseValues = new double[]{};
    }

    public S2SpectralInformation(String physicalBand,
                                 int bandId)
    {
        this(physicalBand, bandId, 0, 0, 0, 0);
    }

    public S2SpectralInformation(String physicalBand,
                                 int bandId,
                                 int resolution,
                                 double wavelenghtMin,
                                 double wavelenghtMax,
                                 double wavelenghtCentral)
    {
        this.physicalBand = physicalBand;
        this.bandId = bandId;
        this.resolution = resolution;
        spectralResponseValues = new double[]{};
        this.wavelenghtMin = wavelenghtMin;
        this.wavelenghtMax = wavelenghtMax;
        this.wavelenghtCentral = wavelenghtCentral;
    }

    public int getBandId() {
        return bandId;
    }

    public void setBandId(int bandId) {
        this.bandId = bandId;
    }

    public String getPhysicalBand() {
        return physicalBand;
    }

    public void setPhysicalBand(String physicalBand) {
        this.physicalBand = physicalBand;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public double getWavelenghtMin() {
        return wavelenghtMin;
    }

    public void setWavelenghtMin(double wavelenghtMin) {
        this.wavelenghtMin = wavelenghtMin;
    }

    public double getWavelenghtMax() {
        return wavelenghtMax;
    }

    public void setWavelenghtMax(double wavelenghtMax) {
        this.wavelenghtMax = wavelenghtMax;
    }

    public double getWavelenghtCentral() {
        return wavelenghtCentral;
    }

    public void setWavelenghtCentral(double wavelenghtCentral) {
        this.wavelenghtCentral = wavelenghtCentral;
    }

    public double[] getSpectralResponseValues() {
        return spectralResponseValues;
    }

    public void setSpectralResponseValues(double[] spectralResponseValues) {
        this.spectralResponseValues = spectralResponseValues;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
