package org.esa.s2tbx.dataio.s2.l3.metadata;

import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadataProc;
import org.esa.snap.core.datamodel.ColorPaletteDef;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.getHSBColor;
import static java.lang.Math.floor;
import static org.esa.s2tbx.dataio.s2.l3.metadata.L3Metadata.MOSAIC_BAND_NAME;

/**
 * Created by obarrile on 15/06/2016.
 */
public class L3MetadataProc extends S2OrthoMetadataProc {

    private static String paletteRelativePath = "color_palettes";
    private static String paletteSpectrum = "spectrum.cpd";

    private static S2SpectralInformation makeSpectralInformation(String format, S2BandConstants bandConstant, S2SpatialResolution resolution, double quantification) {
        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                NamingConventionFactory.getSpectralBandImageTemplate_L3(format, bandConstant.getFilenameBandId()),
                "Reflectance in band " + bandConstant.getPhysicalName(),
                "dl",
                quantification,
                bandConstant.getBandIndex(),
                bandConstant.getWavelengthMin(),
                bandConstant.getWavelengthMax(),
                bandConstant.getWavelengthCentral());
    }

    public static List<S2BandInformation> getBandInformationList (String format, S2SpatialResolution resolution, double boaQuantification, int indexMax) {
        List<S2BandInformation> aInfo = new ArrayList<>();
        switch (resolution) {
            case R10M:
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

                aInfo.add(makeMSCInformation(format, S2SpatialResolution.R10M, indexMax));
                aInfo.add(makeMSCInformation(format, S2SpatialResolution.R20M, indexMax));
                aInfo.add(makeMSCInformation(format, S2SpatialResolution.R60M, indexMax));
                aInfo.add(makeSCLInformation(format, S2SpatialResolution.R10M));
                aInfo.add(makeSCLInformation(format, S2SpatialResolution.R20M));
                aInfo.add(makeSCLInformation(format, S2SpatialResolution.R60M));
                break;
            case R20M:
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

                aInfo.add(makeMSCInformation(format, S2SpatialResolution.R20M, indexMax));
                aInfo.add(makeMSCInformation(format, S2SpatialResolution.R60M, indexMax));
                aInfo.add(makeSCLInformation(format, S2SpatialResolution.R20M));
                aInfo.add(makeSCLInformation(format, S2SpatialResolution.R60M));
                break;
            case R60M:
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

                aInfo.add(makeMSCInformation(format, S2SpatialResolution.R60M, indexMax));
                aInfo.add(makeSCLInformation(format, S2SpatialResolution.R60M));
                break;
        }
        return aInfo;
    }


    private static S2BandInformation makeSCLInformation(String format, S2SpatialResolution resolution) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        /* Using the same colors as in the L2A-PDD */
        indexList.add(S2IndexBandInformation.makeIndex(0, new Color(0, 0, 0), "NODATA", "No data"));
        indexList.add(S2IndexBandInformation.makeIndex(1, new Color(255, 0, 0), "SATURATED_DEFECTIVE", "Saturated or defective"));
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
        indexList.add(S2IndexBandInformation.makeIndex(12, new Color(255, 127, 39), "URBAN_AREAS", "Urban areas"));
        return new S2IndexBandInformation("quality_scene_classification_" + resolution.resolution + "m", resolution, NamingConventionFactory.getSCLTemplate_L3(format), "Scene classification", "", indexList, "scl_" + resolution.resolution + "m_");
    }

    public static S2BandInformation makeMSCInformation(String format, S2SpatialResolution resolution, int indexMax) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();

        if(indexMax > 0) {
            //build color index using ColorPalette file

            ColorPaletteDef colorPalette = createDefaultColorPalette();
            int numPoints = colorPalette.getNumPoints();
            float interval = ((float) numPoints - 1) / indexMax;

            for (int i = 0; i <= indexMax; i++) {
                float f = interval * i;
                int point1 = (int) floor(f);
                float dec = f - point1;

                int red,green,blue;
                if(point1<numPoints-1) {
                    red = (int) (colorPalette.getColors()[point1].getRed() + (colorPalette.getColors()[point1 + 1].getRed() - colorPalette.getColors()[point1].getRed()) * dec);
                    green = (int) (colorPalette.getColors()[point1].getGreen() + (colorPalette.getColors()[point1 + 1].getGreen() - colorPalette.getColors()[point1].getGreen()) * dec);
                    blue = (int) (colorPalette.getColors()[point1].getBlue() + (colorPalette.getColors()[point1 + 1].getBlue() - colorPalette.getColors()[point1].getBlue()) * dec);
                } else {
                    red = colorPalette.getColors()[point1].getRed();
                    green = colorPalette.getColors()[point1].getGreen();
                    blue = colorPalette.getColors()[point1].getBlue();
                }

                indexList.add(S2IndexBandInformation.makeIndex(i, new Color(red, green, blue), String.valueOf(i), String.valueOf(i)));
            }
            return new S2IndexBandInformation(MOSAIC_BAND_NAME + "_" + resolution.resolution + "m", resolution, NamingConventionFactory.getMSCTemplate_L3(format), "Pixel count", "", indexList, "msc_" + resolution.resolution + "m_");
        }

        //Default color index
        for(int i = 0; i <= indexMax; i++) {
            float f = 0;
            f = i*(float)1.0/(indexMax+1);
            f = (float) 0.75 - f;
            if (f < 0) f++;
            indexList.add(S2IndexBandInformation.makeIndex(i, getHSBColor(f, (float)1.0, (float)1.0),  String.valueOf(i), String.valueOf(i)));
        }
        return new S2IndexBandInformation(MOSAIC_BAND_NAME + "_" + resolution.resolution + "m", resolution, NamingConventionFactory.getMSCTemplate_L3(format), "Pixel count", "", indexList, "msc_" + resolution.resolution + "m_");
    }

    private static ColorPaletteDef createDefaultColorPalette () {
        ColorPaletteDef.Point[] points = new ColorPaletteDef.Point[8];

        points[0] = new ColorPaletteDef.Point(0.0, new Color(0,0,0));
        points[1] = new ColorPaletteDef.Point(1.0, new Color(85,0,136));
        points[2] = new ColorPaletteDef.Point(2.0, new Color(0,0,255));
        points[3] = new ColorPaletteDef.Point(3.0, new Color(0,255,255));
        points[4] = new ColorPaletteDef.Point(4.0, new Color(0,255,0));
        points[5] = new ColorPaletteDef.Point(5.0, new Color(255,255,0));
        points[6] = new ColorPaletteDef.Point(6.0, new Color(255,140,0));
        points[7] = new ColorPaletteDef.Point(7.0, new Color(255,0,0));

        ColorPaletteDef colorPaletteDef = new ColorPaletteDef(points, 256);
        colorPaletteDef.setAutoDistribute(true);
        return colorPaletteDef;
    }
}