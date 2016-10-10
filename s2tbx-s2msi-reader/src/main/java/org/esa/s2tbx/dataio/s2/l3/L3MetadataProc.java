package org.esa.s2tbx.dataio.s2.l3;

import https.psd_12_sentinel2_eo_esa_int.dico._12.pdgs.dimap.AN_INCIDENCE_ANGLE_GRID;
import https.psd_12_sentinel2_eo_esa_int.dico._12.pdgs.dimap.A_GEOMETRIC_INFO_TILE;
import https.psd_13_sentinel2_eo_esa_int.dico._13.pdgs.dimap.A_L3_Product_Info;
import https.psd_13_sentinel2_eo_esa_int.dico._13.pdgs.dimap.A_PRODUCT_ORGANIZATION_3;
import https.psd_12_sentinel2_eo_esa_int.dico._12.pdgs.dimap.A_SUN_INCIDENCE_ANGLE_GRID;
import https.psd_12_sentinel2_eo_esa_int.dico._12.pdgs.dimap.A_TILE_DESCRIPTION;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_3_tile_metadata.Level3_Tile;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_3.Level3_User_Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2MetadataType;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoMetadataProc;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoDatastripFilename;
import org.esa.snap.core.datamodel.ColorPaletteDef;
import org.esa.snap.core.util.SystemUtils;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.awt.Color.getHSBColor;
import static java.lang.Math.floor;
import static org.esa.s2tbx.dataio.s2.l3.L3Metadata.MOSAIC_BAND_NAME;
import static org.esa.snap.core.datamodel.ColorPaletteDef.loadColorPaletteDef;

/**
 * Created by obarrile on 15/06/2016.
 */
public class L3MetadataProc extends S2OrthoMetadataProc {

    private static String paletteRelativePath = "color_palettes";
    private static String paletteSpectrum = "spectrum.cpd";

    private static S2SpectralInformation makeSpectralInformation(S2BandConstants bandConstant, S2SpatialResolution resolution, double quantification) {
        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                makeSpectralBandImageFileTemplate(bandConstant.getFilenameBandId()),
                "Reflectance in band " + bandConstant.getPhysicalName(),
                "dl",
                quantification,
                bandConstant.getBandIndex(),
                bandConstant.getWavelengthMin(),
                bandConstant.getWavelengthMax(),
                bandConstant.getWavelengthCentral());
    }

    public static List<S2BandInformation> getBandInformationList (S2SpatialResolution resolution, double boaQuantification, int indexMax) {
        List<S2BandInformation> aInfo = new ArrayList<>();
        switch (resolution) {
            case R10M:
                aInfo.add(makeSpectralInformation(S2BandConstants.B1, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B2, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B3, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B4, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B5, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B6, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B7, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8A, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B9, S2SpatialResolution.R60M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B11, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B12, S2SpatialResolution.R20M, boaQuantification));

                aInfo.add(makeMSCInformation(S2SpatialResolution.R10M, indexMax));
                aInfo.add(makeMSCInformation(S2SpatialResolution.R20M, indexMax));
                aInfo.add(makeMSCInformation(S2SpatialResolution.R60M, indexMax));
                aInfo.add(makeSCLInformation(S2SpatialResolution.R10M));
                aInfo.add(makeSCLInformation(S2SpatialResolution.R20M));
                aInfo.add(makeSCLInformation(S2SpatialResolution.R60M));
                break;
            case R20M:
                aInfo.add(makeSpectralInformation(S2BandConstants.B1, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B2, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B3, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B4, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B5, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B6, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B7, S2SpatialResolution.R20M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8A, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B9, S2SpatialResolution.R60M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B11, S2SpatialResolution.R20M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B12, S2SpatialResolution.R20M, boaQuantification));

                aInfo.add(makeMSCInformation(S2SpatialResolution.R20M, indexMax));
                aInfo.add(makeMSCInformation(S2SpatialResolution.R60M, indexMax));
                aInfo.add(makeSCLInformation(S2SpatialResolution.R20M));
                aInfo.add(makeSCLInformation(S2SpatialResolution.R60M));
                break;
            case R60M:
                aInfo.add(makeSpectralInformation(S2BandConstants.B1, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B2, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B3, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B4, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B5, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B6, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B7, S2SpatialResolution.R60M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8A, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B9, S2SpatialResolution.R60M, boaQuantification));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B11, S2SpatialResolution.R60M, boaQuantification));
                aInfo.add(makeSpectralInformation(S2BandConstants.B12, S2SpatialResolution.R60M, boaQuantification));

                aInfo.add(makeMSCInformation(S2SpatialResolution.R60M, indexMax));
                aInfo.add(makeSCLInformation(S2SpatialResolution.R60M));
                break;
        }
        return aInfo;
    }


    private static S2BandInformation makeSCLInformation(S2SpatialResolution resolution) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        /* Using the same colors as in the L2A-PDD */
        indexList.add(S2IndexBandInformation.makeIndex(0, new Color(0, 0, 0), "NODATA", "No data"));
        indexList.add(S2IndexBandInformation.makeIndex(1, new Color(255, 0, 0), "SATURATED_DEFECTIVE", "Saturated or defective"));
        indexList.add(S2IndexBandInformation.makeIndex(2, new Color(46, 46, 46), "DARK_FEATURE_SHADOW", "Dark feature shadow"));
        indexList.add(S2IndexBandInformation.makeIndex(3, new Color(100, 50, 0), "CLOUD_SHADOW", "Cloud shadow"));
        indexList.add(S2IndexBandInformation.makeIndex(4, new Color(0, 128, 0), "VEGETATION", "Vegetation"));
        indexList.add(S2IndexBandInformation.makeIndex(5, new Color(255, 230, 90), "BARE_SOIL_DESERT", "Bare soil / Desert"));
        indexList.add(S2IndexBandInformation.makeIndex(6, new Color(0, 0, 255), "WATER", "Water"));
        indexList.add(S2IndexBandInformation.makeIndex(7, new Color(129, 129, 129), "CLOUD_LOW_PROBA", "Cloud (low probability)"));
        indexList.add(S2IndexBandInformation.makeIndex(8, new Color(193, 193, 193), "CLOUD_MEDIUM_PROBA", "Cloud (medium probability)"));
        indexList.add(S2IndexBandInformation.makeIndex(9, new Color(255, 255, 255), "CLOUD_HIGH_PROBA", "Cloud (high probability)"));
        indexList.add(S2IndexBandInformation.makeIndex(10, new Color(100, 200, 255), "THIN_CIRRUS", "Thin cirrus"));
        indexList.add(S2IndexBandInformation.makeIndex(11, new Color(255, 150, 255), "SNOW_ICE", "Snow or Ice"));
        indexList.add(S2IndexBandInformation.makeIndex(12, new Color(255, 127, 39), "URBAN_AREAS", "Urban areas"));
        return new S2IndexBandInformation("quality_scene_classification_" + resolution.resolution + "m", resolution, makeSCLFileTemplate(), "Scene classification", "", indexList, "scl_" + resolution.resolution + "m_");
    }

    public static S2BandInformation makeMSCInformation(S2SpatialResolution resolution, int indexMax) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();

        if(indexMax > 0) {
            //build color index using ColorPalette file
            try {
                String palettePath = SystemUtils.getAuxDataPath().resolve(paletteRelativePath).resolve(paletteSpectrum).toString();
                ColorPaletteDef colorPalette = loadColorPaletteDef(new File(palettePath));
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
                return new S2IndexBandInformation(MOSAIC_BAND_NAME + "_" + resolution.resolution + "m", resolution, makeMSCFileTemplate(), "Pixel count", "", indexList, "msc_" + resolution.resolution + "m_");

            } catch (IOException e) {

            }
        }

        //Default color index
        for(int i = 0; i <= indexMax; i++) {
            float f = 0;
            f = i*(float)1.0/(indexMax+1);
            f = (float) 0.75 - f;
            if (f < 0) f++;
            indexList.add(S2IndexBandInformation.makeIndex(i, getHSBColor(f, (float)1.0, (float)1.0),  String.valueOf(i), String.valueOf(i)));
        }
        return new S2IndexBandInformation(MOSAIC_BAND_NAME + "_" + resolution.resolution + "m", resolution, makeMSCFileTemplate(), "Pixel count", "", indexList, "msc_" + resolution.resolution + "m_");

    }

    private static String makeSCLFileTemplate() {
        return String.format("QI_DATA%s{{MISSION_ID}}_USER_SCL_L03_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_{{RESOLUTION}}m.jp2", File.separator);
    }

    private static String makeMSCFileTemplate() {
        return String.format("QI_DATA%s{{MISSION_ID}}_USER_MSC_L03_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_{{RESOLUTION}}m.jp2", File.separator);
    }

    private static String makeSpectralBandImageFileTemplate(String bandFileId) {
        /* Sample :
        MISSION_ID : S2A
        SITECENTRE : MTI_
        CREATIONDATE : 20150813T201603
        ABSOLUTEORBIT : A000734
        TILENUMBER : T32TQR
        RESOLUTION : 10 | 20 | 60
         */
        return String.format("IMG_DATA%sR{{RESOLUTION}}m%s{{MISSION_ID}}_USER_MSI_L03_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_%s_{{RESOLUTION}}m.jp2",
                             File.separator, File.separator, bandFileId);
    }




}