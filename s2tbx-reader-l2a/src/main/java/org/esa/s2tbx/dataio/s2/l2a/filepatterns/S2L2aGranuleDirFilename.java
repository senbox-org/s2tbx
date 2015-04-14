/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.l2a.filepatterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Norman Fomferra
 */
public class S2L2aGranuleDirFilename {

    final static String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})(_A[0-9]{6})(_T[A-Z|0-9]{5})(_N[0-9]{2}\\.[0-9]{2})(\\.[A-Z|a-z|0-9]{3,4})?";
    final static Pattern PATTERN = Pattern.compile(REGEX);

    public final String name;
    public final String missionID;
    public final String fileClass;
    public final String fileCategory;
    public final String fileSemantic;
    public final String siteCentre;
    public final String creationDate;
    public final String absoluteOrbit;
    public final String tileNumber;
    public final String processingBaseline;

    private S2L2aGranuleDirFilename(String name, String missionID, String fileClass, String fileCategory, String fileSemantic, String siteCentre, String creationDate, String absoluteOrbit, String tileNumber, String processingBaseline) {
        this.name = name;
        this.missionID = missionID;
        this.fileClass = fileClass;
        this.fileCategory = fileCategory;
        this.fileSemantic = fileSemantic;
        this.siteCentre = siteCentre;
        this.creationDate = creationDate;
        this.absoluteOrbit = absoluteOrbit;
        this.tileNumber = tileNumber;
        this.processingBaseline = processingBaseline;
    }

    public static boolean isGranuleFilename(String name) {
        return PATTERN.matcher(name).matches();
    }

    public S2L2aGranuleMetadataFilename getMetadataFilename() {
        String tmp = String.format("%s_%s_%s%s_%s_%s%s%s.xml", missionID, fileClass, "MTD_", fileSemantic, siteCentre, creationDate, absoluteOrbit, tileNumber);
        return S2L2aGranuleMetadataFilename.create(tmp);
    }

    public S2L2aGranuleImageFilename getImageFilename(String bandId) {
        String newBandId = bandId;

        if (newBandId.length() == 2) {
            newBandId = new String(bandId.charAt(0) + "0" + bandId.charAt(1));
        }

        String tmp = String.format("%s_%s_%s%s_%s_%s%s%s_%s.jp2", missionID, fileClass, fileCategory, fileSemantic, siteCentre, creationDate, absoluteOrbit, tileNumber, newBandId);
        return S2L2aGranuleImageFilename.create(tmp);
    }

    public static S2L2aGranuleDirFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return new S2L2aGranuleDirFilename(fileName,
                                               matcher.group(1),
                                               matcher.group(2),
                                               matcher.group(3),
                                               matcher.group(4),
                                               matcher.group(5),
                                               matcher.group(6),
                                               matcher.group(7),
                                               matcher.group(8),
                                               matcher.group(9)
            );
        } else {
            return null;
        }
    }
}
