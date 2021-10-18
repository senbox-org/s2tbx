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

package org.esa.s2tbx.dataio.s2.l2a.metadata;


import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadataProc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author opicas-p
 */
public class L2aMetadataProc extends S2OrthoMetadataProc {

    private static S2SpectralInformation makeSpectralInformation(String format, S2BandConstants bandConstant, S2SpatialResolution resolution, double quantification) {
        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                NamingConventionFactory.getSpectralBandImageTemplate_L2a(format, bandConstant.getFilenameBandId()),
                "Reflectance in band " + bandConstant.getPhysicalName(),
                "dl",
                quantification,
                bandConstant.getBandIndex(),
                bandConstant.getWavelengthMin(),
                bandConstant.getWavelengthMax(),
                bandConstant.getWavelengthCentral());
    }

    private static S2BandInformation makeAOTInformation(String format, S2SpatialResolution resolution, double quantification) {
        return new S2BandInformation("quality_aot", resolution, NamingConventionFactory.getAOTTemplate_L2a(format), "Aerosol Optical Thickness", "none", quantification);
    }

    private static S2BandInformation makeWVPInformation(String format,S2SpatialResolution resolution, double quantification) {
        return new S2BandInformation("quality_wvp", resolution, NamingConventionFactory.getWVPTemplate_L2a(format), "Water Vapour", "cm", quantification);
    }

    private static S2BandInformation makeCLDInformation(String format,S2SpatialResolution resolution) {
        return new S2BandInformation("quality_cloud_confidence", resolution, NamingConventionFactory.getCLDTemplate_L2a(format), "Cloud Confidence", "%", 1.0);
    }

    private static S2BandInformation makeSNWInformation(String format,S2SpatialResolution resolution) {
        return new S2BandInformation("quality_snow_confidence", resolution, NamingConventionFactory.getSNWTemplate_L2a(format), "Snow Confidence", "%", 1.0);
    }

    private static S2BandInformation makeCLASSIInformation(String format,S2SpatialResolution resolution) {
        // not used because the mask MSK_CLASSI is multiband. The system index should be refactor to manage multiband.
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        indexList.add(S2IndexBandInformation.makeIndex(1, Color.BLUE, "opaque_clouds", "Opaque clouds"));
        indexList.add(S2IndexBandInformation.makeIndex(1, Color.ORANGE, "cirrus_clouds", "Cirrus clouds"));
        indexList.add(S2IndexBandInformation.makeIndex(1, new Color(255, 150, 255), "snow_and_ice_areas", "Snow and Ice areas"));
        return new S2IndexBandInformation("MASK_CLASSI", resolution, NamingConventionFactory.getCLASSITemplate_L2a(format), "Quality classification", "", indexList, "");
    }

    private static S2BandInformation makeDDVInformation(String format,S2SpatialResolution resolution) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        /* Using the same colors as in the L2A-PDD */
        indexList.add(S2IndexBandInformation.makeIndex(0, new Color(255, 255, 255), "NODATA", "No data"));
        //indexList.add(S2IndexBandInformation.makeIndex(1, new Color(192, 192, 192), "NOT USED", "Not used"));
        indexList.add(S2IndexBandInformation.makeIndex(2, new Color(0, 0, 0), "DARK_FEATURE", "Dark feature"));
        //indexList.add(S2IndexBandInformation.makeIndex(3, new Color(192, 192, 192), "NOT USED", "Not used"));
        indexList.add(S2IndexBandInformation.makeIndex(4, new Color(0, 160, 0), "DDV", "Dark Dense Vegetation"));
        indexList.add(S2IndexBandInformation.makeIndex(5, new Color(192, 192, 192), "BACKGROUND", "Background"));
        indexList.add(S2IndexBandInformation.makeIndex(6, new Color(0, 0, 255), "WATER", "Water"));
        return new S2IndexBandInformation("quality_dense_dark_vegetation", resolution, NamingConventionFactory.getDDVTemplate_L2a(format), "Dense Dark Vegetation", "", indexList, "ddv_");
    }

    private static S2BandInformation makeSCLInformation(String format,S2SpatialResolution resolution, int psd) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        /* Using the same colors as in the L2A-PDD */
        indexList.add(S2IndexBandInformation.makeIndex(0, new Color(0, 0, 0), "NODATA", "No data"));
        indexList.add(S2IndexBandInformation.makeIndex(1, new Color(255, 0, 0), "SATURATED_DEFECTIVE", "Saturated or defective"));
        if(psd>147)
            indexList.add(S2IndexBandInformation.makeIndex(2, new Color(46, 46, 46), "TOPOGRAPHIC_AND_CASTED_SHADOWS", "topographic and casted shadows"));
        else
            indexList.add(S2IndexBandInformation.makeIndex(2, new Color(46, 46, 46), "DARK_FEATURE_SHADOW", "Dark feature shadow"));
        indexList.add(S2IndexBandInformation.makeIndex(3, new Color(100, 50, 0), "CLOUD_SHADOW", "Cloud shadow"));
        indexList.add(S2IndexBandInformation.makeIndex(4, new Color(0, 128, 0), "VEGETATION", "Vegetation"));
        indexList.add(S2IndexBandInformation.makeIndex(5, new Color(255, 230, 90), "NOT_VEGETATED", "Not vegetated"));
        indexList.add(S2IndexBandInformation.makeIndex(6, new Color(0, 0, 255), "WATER", "Water"));
        indexList.add(S2IndexBandInformation.makeIndex(7, new Color(129, 129, 129), "UNCLASSIFIED", "Unclassified"));
        indexList.add(S2IndexBandInformation.makeIndex(8, new Color(193, 193, 193), "CLOUD_MEDIUM_PROBA", "Cloud (medium probability)"));
        indexList.add(S2IndexBandInformation.makeIndex(9, new Color(255, 255, 255), "CLOUD_HIGH_PROBA", "Cloud (high probability)"));
        indexList.add(S2IndexBandInformation.makeIndex(10, new Color(100, 200, 255), "THIN_CIRRUS", "Thin cirrus"));
        indexList.add(S2IndexBandInformation.makeIndex(11, new Color(255, 150, 255), "SNOW_ICE", "Snow or Ice"));
        return new S2IndexBandInformation("quality_scene_classification", resolution, NamingConventionFactory.getSCLTemplate_L2a(format, psd), "Scene classification", "", indexList, "scl_");
    }

    public static List<S2BandInformation> getBandInformationList(String format, S2SpatialResolution resolution, int psd,
                                                                 double boaQuantification,
                                                                 double aotQuantification,
                                                                 double wvpQuantification) {
        List<S2BandInformation> aInfo = new ArrayList<>();
        switch (resolution) {
            case R10M:
                if(psd>147)
                    aInfo.add(makeSpectralInformation(format, S2BandConstants.B1, S2SpatialResolution.R20M, boaQuantification));
                else
                    aInfo.add(makeSpectralInformation(format, S2BandConstants.B1, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B2, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B3, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B4, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B5, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B6, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B7, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B8, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B8A, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B9, S2SpatialResolution.R60M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B11, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B12, S2SpatialResolution.R20M, boaQuantification));

                aInfo.add(makeAOTInformation(format, S2SpatialResolution.R10M, aotQuantification));
                aInfo.add(makeWVPInformation(format, S2SpatialResolution.R10M, wvpQuantification));
                aInfo.add(makeCLDInformation(format, S2SpatialResolution.R20M));
                aInfo.add(makeSNWInformation(format, S2SpatialResolution.R20M));
                aInfo.add(makeDDVInformation(format, S2SpatialResolution.R20M));

                // SCL only generated at 20m and 60m. upsample the 20m version
                aInfo.add(makeSCLInformation(format, S2SpatialResolution.R20M, psd));
                break;
            case R20M:
                if(psd>147)
                    aInfo.add(makeSpectralInformation(format, S2BandConstants.B1, S2SpatialResolution.R20M, boaQuantification));
                else
                    aInfo.add(makeSpectralInformation(format, S2BandConstants.B1, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B2, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B3, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B4, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B5, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B6, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B7, S2SpatialResolution.R20M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B8A, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B9, S2SpatialResolution.R60M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B11, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B12, S2SpatialResolution.R20M, boaQuantification));

                aInfo.add(makeAOTInformation(format, S2SpatialResolution.R20M, aotQuantification));
                aInfo.add(makeWVPInformation(format, S2SpatialResolution.R20M, wvpQuantification));
                aInfo.add(makeCLDInformation(format, S2SpatialResolution.R20M));
                aInfo.add(makeSNWInformation(format, S2SpatialResolution.R20M));
                aInfo.add(makeDDVInformation(format, S2SpatialResolution.R20M));

                aInfo.add(makeSCLInformation(format, S2SpatialResolution.R20M, psd));
                break;
            case R60M:
                if(psd > 147)
                    aInfo.add(makeSpectralInformation(format, S2BandConstants.B1, S2SpatialResolution.R20M, boaQuantification));
                else
                    aInfo.add(makeSpectralInformation(format, S2BandConstants.B1, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B2, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B3, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B4, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B5, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B6, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B7, S2SpatialResolution.R60M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B8A, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B9, S2SpatialResolution.R60M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B11, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(format, S2BandConstants.B12, S2SpatialResolution.R60M, boaQuantification));

                aInfo.add(makeAOTInformation(format, S2SpatialResolution.R60M, aotQuantification));
                aInfo.add(makeWVPInformation(format, S2SpatialResolution.R60M, wvpQuantification));
                aInfo.add(makeCLDInformation(format, S2SpatialResolution.R60M));
                aInfo.add(makeSNWInformation(format, S2SpatialResolution.R60M));
                aInfo.add(makeDDVInformation(format, S2SpatialResolution.R60M));

                aInfo.add(makeSCLInformation(format, S2SpatialResolution.R60M, psd));
                break;
        }
        return aInfo;
    }
}
