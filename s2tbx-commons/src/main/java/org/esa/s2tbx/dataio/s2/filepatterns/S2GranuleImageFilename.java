/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.logging.BeamLogManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Norman Fomferra
 */
public class S2GranuleImageFilename {

    public final String name;
    public final String missionID;
    public final String fileClass;
    public final String fileCategory;
    public final String fileSemantic;
    public final String siteCentre;
    public final String creationDate;
    public final String absoluteOrbit;
    public final String tileNumber;
    public final String bandIndex;

    protected S2GranuleImageFilename(String name, String missionID, String fileClass, String fileCategory, String fileSemantic, String siteCentre, String creationDate, String instanceID, String absoluteOrbit, String tileNumber, String bandIndex) {
        this.name = name;
        this.missionID = missionID;
        this.fileClass = fileClass;
        this.fileCategory = fileCategory;
        this.fileSemantic = fileSemantic;
        this.siteCentre = siteCentre;
        this.creationDate = creationDate;
        this.absoluteOrbit = absoluteOrbit;
        this.tileNumber = tileNumber;
        this.bandIndex = bandIndex;
    }

    /**
     *
     * @return the band index, or -1 if the band index couldn't be parsed
     */
    public int getBandIndex() {
        int bandIndexAsInt = -1;
        try {
            Integer.parseInt(bandIndex);
        } catch (NumberFormatException ex) {
            SystemUtils.LOG.severe(String.format("Band index \"%s\" extracted from file name should be an integer", bandIndex));
        }

        return bandIndexAsInt;
    }

    public String getTileNumber() {
        return tileNumber;
    }
}
