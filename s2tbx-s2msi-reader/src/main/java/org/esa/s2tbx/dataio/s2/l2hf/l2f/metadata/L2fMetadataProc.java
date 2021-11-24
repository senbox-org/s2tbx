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

package org.esa.s2tbx.dataio.s2.l2hf.l2f.metadata;


import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadataProc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author created by fdouziech 04/2021
 */
public class L2fMetadataProc extends S2OrthoMetadataProc {

    private static S2SpectralInformation makeSpectralInformation(String format, S2BandConstants bandConstant, S2SpatialResolution resolution, double quantification, boolean isInNativeSubFolder) {
        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                NamingConventionFactory.getSpectralBandImageTemplate_L2f(format, bandConstant.getFilenameBandId(),isInNativeSubFolder),
                "Reflectance in band " + bandConstant.getPhysicalName(),
                "dl",
                quantification,
                bandConstant.getBandIndex(),
                bandConstant.getWavelengthMin(),
                bandConstant.getWavelengthMax(),
                bandConstant.getWavelengthCentral());
    }

    public static List<S2BandInformation> getBandInformationList(String format, S2SpatialResolution resolution, int psd,
                                                                 double boaQuantification,
                                                                 double aotQuantification,
                                                                 double wvpQuantification, S2Config.Sentinel2ProductMission missionId) {
        List<S2BandInformation> aInfo = new ArrayList<>();
        if(missionId == S2Config.Sentinel2ProductMission.LS8){
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B1, S2SpatialResolution.R30M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B2, S2SpatialResolution.R10M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B3, S2SpatialResolution.R10M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B4, S2SpatialResolution.R10M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B8A, S2SpatialResolution.R20M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B11, S2SpatialResolution.R20M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B12, S2SpatialResolution.R20M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B8, S2SpatialResolution.R15M, boaQuantification, true));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B10, S2SpatialResolution.R30M, boaQuantification, true));
        } else {
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B1, S2SpatialResolution.R60M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B2, S2SpatialResolution.R10M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B3, S2SpatialResolution.R10M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B4, S2SpatialResolution.R10M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B8A, S2SpatialResolution.R20M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B11, S2SpatialResolution.R20M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B12, S2SpatialResolution.R20M, boaQuantification, false));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B5, S2SpatialResolution.R20M, boaQuantification, true));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B6, S2SpatialResolution.R20M, boaQuantification, true));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B7, S2SpatialResolution.R20M, boaQuantification, true));
            aInfo.add(makeSpectralInformation(format, S2BandConstants.B8, S2SpatialResolution.R10M, boaQuantification, true));
        }

        return aInfo;
    }
}
