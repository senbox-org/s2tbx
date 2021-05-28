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

package org.esa.s2tbx.dataio.s2.ortho.filepatterns;

import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleImageFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleMetadataFilename;
import org.esa.snap.core.util.SystemUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Norman Fomferra
 */
public class S2OrthoGranuleDirFilename extends S2GranuleDirFilename {

    // example :                 S2A          _OPER          _MSI_            L1C_TL          _MTI_            _20150627T180307    _A000062    _T32SMH         _N01.00
    final static String REGEX = "(S2A|S2B|S2_|LS8)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{3,6})_([A-Z|0-9|_]{3,4})_([0-9]{8}T[0-9]{6})_(A[0-9]{6})_(T[A-Z|0-9]{5})(_N[0-9]{2}\\.[0-9]{2})(\\.[A-Z|a-z|0-9]{3,4})?";
    //                           S2A_          OPER          _MSI_            L2H_ ou L2F         EPA__            20200505T100915_    A025430_    T33TTG_N99.99
        //                       LS8          _OPER          _OLI_            L2F_                ZZZ__            20200214T094714_    A000000_    T33TTG_N99.99
    // final static String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{3})_([A-Z|0-9|_]{3})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_(A[0-9]{6})_(T[A-Z|0-9]{5})(_N[0-9]{2}\\.[0-9]{2})(\\.[A-Z|a-z|0-9]{3,4})?";
    
    final static Pattern PATTERN = Pattern.compile(REGEX);

    public final String absoluteOrbit;
    public final String tileNumber;

    private S2OrthoGranuleDirFilename(String name,
                                      String missionID,
                                      String fileClass,
                                      String fileCategory,
                                      String fileSemantic,
                                      String siteCentre,
                                      String creationDate,
                                      String absoluteOrbit,
                                      String tileNumber,
                                      String processingBaseline) {
        super(name,
                missionID,
                fileClass,
                fileCategory,
                fileSemantic,
                siteCentre,
                creationDate,
                processingBaseline);


        this.absoluteOrbit = absoluteOrbit;
        this.tileNumber = tileNumber;
    }

    public S2GranuleMetadataFilename getMetadataFilename() {
        String tmp = String.format("%s_%s_%s%s_%s_%s_%s_%s.xml", missionID, fileClass, "MTD_", fileSemantic, siteCentre, creationDate, absoluteOrbit, tileNumber);
        return S2OrthoGranuleMetadataFilename.create(tmp);
    }

    public S2GranuleImageFilename getImageFilename(String bandId) {
        String newBandId = bandId;

        if (newBandId.length() == 2) {
            newBandId = new String(bandId.charAt(0) + "0" + bandId.charAt(1));
        }

        String tmp = String.format("%s_%s_%s%s_%s_%s_%s_%s_%s.jp2", missionID, fileClass, fileCategory, fileSemantic, siteCentre, creationDate, absoluteOrbit, tileNumber, newBandId);
        return S2OrthoGranuleImageFilename.create(tmp);
    }

    public static S2OrthoGranuleDirFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return new S2OrthoGranuleDirFilename(fileName,
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
            SystemUtils.LOG.warning(String.format("%s S2GranuleDirFilename didn't match regexp %s", fileName, PATTERN.toString()));
            return null;
        }
    }

    public String getTileID () {
        return tileNumber;
    }
}
