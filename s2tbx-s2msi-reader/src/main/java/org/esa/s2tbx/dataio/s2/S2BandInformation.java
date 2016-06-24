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
 * @author Julien Malik (CS SI)
 */
public class S2BandInformation {
    private String physicalBand;
    private S2SpatialResolution resolution;
    private String imageFileTemplate;
    private String description;
    private String unit;
    private double quantificationValue;

    public S2BandInformation(String physicalBand,
                             S2SpatialResolution resolution,
                             String imageFileTemplate,
                             String description,
                             String unit,
                             double quantificationValue) {
        this.physicalBand = physicalBand;
        this.resolution = resolution;
        this.imageFileTemplate = imageFileTemplate;
        this.description = description;
        this.unit = unit;
        this.quantificationValue = quantificationValue;
    }

    public String getPhysicalBand() {
        return physicalBand;
    }

    public void setPhysicalBand(String physicalBand) {
        this.physicalBand = physicalBand;
    }

    public S2SpatialResolution getResolution() {
        return resolution;
    }

    public void setResolution(S2SpatialResolution resolution) {
        this.resolution = resolution;
    }

    public void setImageFileTemplate(String template) {
        this.imageFileTemplate = template;
    }

    public String getImageFileTemplate() {
        return this.imageFileTemplate;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public double getQuantificationValue() {
        return quantificationValue;
    }
    
    public double getScalingFactor() {
        return 1.0 / quantificationValue;
    }

}
