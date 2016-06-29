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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Nicolas Ducoin
 */
public class S2SpectralInformation extends S2BandInformation {

    private int bandId;
    private double wavelengthMin;
    private double wavelengthMax;
    private double wavelengthCentral;
    private double[] spectralResponseValues;

    public S2SpectralInformation(String physicalBand,
                                 S2SpatialResolution resolution,
                                 String imageFileTemplate,
                                 String description,
                                 String unit,
                                 double quantificationValue,
                                 int bandId,
                                 double wavelengthMin,
                                 double wavelengthMax,
                                 double wavelengthCentral) {
        super(physicalBand, resolution, imageFileTemplate, description, unit, quantificationValue);
        this.bandId = bandId;
        this.wavelengthMin = wavelengthMin;
        this.wavelengthMax = wavelengthMax;
        this.wavelengthCentral = wavelengthCentral;
        this.spectralResponseValues = new double[]{};
    }

    public double getWavelengthMin() {
        return wavelengthMin;
    }

    public void setWavelengthMin(double wavelengthMin) {
        this.wavelengthMin = wavelengthMin;
    }

    public double getWavelengthMax() {
        return wavelengthMax;
    }

    public void setWavelengthMax(double wavelengthMax) {
        this.wavelengthMax = wavelengthMax;
    }

    public double getWavelengthCentral() {
        return wavelengthCentral;
    }

    public void setWavelengthCentral(double wavelengthCentral) {
        this.wavelengthCentral = wavelengthCentral;
    }

    public double getSpectralBandwith() {
        return (this.wavelengthMax - this.wavelengthMin);
    }

    public double[] getSpectralResponseValues() {
        return spectralResponseValues;
    }

    public void setSpectralResponseValues(double[] spectralResponseValues) {
        this.spectralResponseValues = spectralResponseValues;
    }

    public int getBandId() {
        return bandId;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
