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

package org.esa.s2tbx.dataio.s2.l1c.metadata;

import org.esa.s2tbx.dataio.s2.*;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadataProc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author opicas-p
 */
public class L1cMetadataProc extends S2OrthoMetadataProc {

    private static S2SpectralInformation makeSpectralInformation(String format, S2BandConstants bandConstant, S2SpatialResolution resolution, double quantification) {
        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                NamingConventionFactory.getSpectralBandImageTemplate_L1c(format,bandConstant.getFilenameBandId()),
                "Reflectance in band " + bandConstant.getPhysicalName(),
                "dl",
                quantification,
                bandConstant.getBandIndex(),
                bandConstant.getWavelengthMin(),
                bandConstant.getWavelengthMax(),
                bandConstant.getWavelengthCentral());
    }


    public static List<S2BandInformation> getBandInformationList (String format, double toaQuantification) {
        List<S2BandInformation> aInfo = new ArrayList<>();
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B1, S2SpatialResolution.R60M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B2, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B3, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B4, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B5, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B6, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B7, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B8, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B8A, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B9, S2SpatialResolution.R60M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B10, S2SpatialResolution.R60M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B11, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(format, S2BandConstants.B12, S2SpatialResolution.R20M, toaQuantification));
        return aInfo;
    }

}
