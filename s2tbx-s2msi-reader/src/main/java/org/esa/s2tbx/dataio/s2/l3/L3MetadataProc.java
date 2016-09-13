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

    public static JAXBContext getJaxbContext() throws JAXBException, FileNotFoundException {
        ClassLoader s2c = Level3_User_Product.class.getClassLoader();
        return JAXBContext.newInstance(S2MetadataType.L3, s2c);
    }

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

    public static L3Metadata.ProductCharacteristics getTileProductOrganization(Level3_Tile aTile, S2SpatialResolution resolution) {
        L3Metadata.ProductCharacteristics characteristics = new L3Metadata.ProductCharacteristics();
        characteristics.setSpacecraft("Sentinel-2");
        characteristics.setProcessingLevel("Level-3");

        //TODO anything from filename?
        //characteristics.setDatasetProductionDate("Unknown");
        //characteristics.setProductStartTime("Unknown");
        //characteristics.setProductStopTime("Unknown");

        double boaQuantification = 1000; //Default value
        characteristics.setQuantificationValue(boaQuantification);

        return characteristics;
    }


    public static L3Metadata.ProductCharacteristics getProductOrganization(Level3_User_Product product, S2SpatialResolution resolution) {
        L3Metadata.ProductCharacteristics characteristics = new L3Metadata.ProductCharacteristics();
        characteristics.setSpacecraft(product.getGeneral_Info().getL3_Product_Info().getDatatake().getSPACECRAFT_NAME());
        characteristics.setDatasetProductionDate(product.getGeneral_Info().getL3_Product_Info().getDatatake().getDATATAKE_SENSING_START().toString());
        characteristics.setProcessingLevel(product.getGeneral_Info().getL3_Product_Info().getPROCESSING_LEVEL().toString());

        characteristics.setProductStartTime(((Element) product.getGeneral_Info().getL3_Product_Info().getPRODUCT_START_TIME()).getFirstChild().getNodeValue());
        characteristics.setProductStopTime(((Element) product.getGeneral_Info().getL3_Product_Info().getPRODUCT_STOP_TIME()).getFirstChild().getNodeValue());
        double boaQuantification = product.getGeneral_Info().getL3_Product_Image_Characteristics().getL1C_L2A_Quantification_Values_List().getL2A_BOA_QUANTIFICATION_VALUE().getValue();
        characteristics.setQuantificationValue(boaQuantification);

        return characteristics;
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

                aInfo.add(makeMSCInformation(resolution, indexMax));
                aInfo.add(makeSCLInformation(S2SpatialResolution.R20M));
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
                aInfo.add(makeSCLInformation(S2SpatialResolution.R20M));
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
        return new S2IndexBandInformation("quality_scene_classification", resolution, makeSCLFileTemplate(), "Scene classification", "", indexList, "scl_");
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
                return new S2IndexBandInformation(MOSAIC_BAND_NAME, resolution, makeMSCFileTemplate(), "Pixel count", "", indexList, "msc_");

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
        return new S2IndexBandInformation(MOSAIC_BAND_NAME, resolution, makeMSCFileTemplate(), "Pixel count", "", indexList, "msc_");

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

    public static Collection<String> getTiles(Level3_User_Product product) {
        A_L3_Product_Info.L3_Product_Organisation info = product.getGeneral_Info().getL3_Product_Info().getL3_Product_Organisation();

        List<A_L3_Product_Info.L3_Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();

        //New list with only granules with different granuleIdentifier
        List<A_L3_Product_Info.L3_Product_Organisation.Granule_List> aGranuleListReduced = new ArrayList<>();
        Map<String, A_L3_Product_Info.L3_Product_Organisation.Granule_List> mapGranules = new LinkedHashMap<>(aGranuleList.size());
        for (A_L3_Product_Info.L3_Product_Organisation.Granule_List granule : aGranuleList) {
            mapGranules.put(granule.getGranules().getGranuleIdentifier(), granule);
        }
        for (Map.Entry<String, A_L3_Product_Info.L3_Product_Organisation.Granule_List> granule : mapGranules.entrySet()) {
            aGranuleListReduced.add(granule.getValue());
        }

        Transformer tileSelector = o -> {
            A_L3_Product_Info.L3_Product_Organisation.Granule_List ali = (A_L3_Product_Info.L3_Product_Organisation.Granule_List) o;
            A_PRODUCT_ORGANIZATION_3.Granules gr = ali.getGranules();
            return gr.getGranuleIdentifier();
        };

        return CollectionUtils.collect(aGranuleListReduced, tileSelector);
    }

    public static S2DatastripFilename getDatastrip(Level3_User_Product product) {
        A_L3_Product_Info.L3_Product_Organisation info = product.getGeneral_Info().getL3_Product_Info().getL3_Product_Organisation();

        String dataStripMetadataFilenameCandidate = info.getGranule_List().get(0).getGranules().getDatastripIdentifier();
        S2DatastripDirFilename dirDatastrip = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);

        if (dirDatastrip != null) {
            String fileName = dirDatastrip.getFileName(null);
            return S2OrthoDatastripFilename.create(fileName);
        } else {
            return null;
        }
    }

    public static S2DatastripDirFilename getDatastripDir(Level3_User_Product product) {
        A_L3_Product_Info.L3_Product_Organisation info = product.getGeneral_Info().getL3_Product_Info().getL3_Product_Organisation();
        String dataStripMetadataFilenameCandidate = info.getGranule_List().get(0).getGranules().getDatastripIdentifier();

        return S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);
    }

    public static Map<S2SpatialResolution, L3Metadata.TileGeometry> getTileGeometries(Level3_Tile product) {

        A_GEOMETRIC_INFO_TILE info = product.getGeometric_Info();
        A_GEOMETRIC_INFO_TILE.Tile_Geocoding tgeo = info.getTile_Geocoding();


        List<A_TILE_DESCRIPTION.Geoposition> poss = tgeo.getGeoposition();
        List<A_TILE_DESCRIPTION.Size> sizz = tgeo.getSize();

        Map<S2SpatialResolution, L3Metadata.TileGeometry> resolutions = new HashMap<>();

        for (A_TILE_DESCRIPTION.Geoposition gpos : poss) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(gpos.getResolution());
            L3Metadata.TileGeometry tgeox = new L3Metadata.TileGeometry();
            tgeox.setUpperLeftX(gpos.getULX());
            tgeox.setUpperLeftY(gpos.getULY());
            tgeox.setxDim(gpos.getXDIM());
            tgeox.setyDim(gpos.getYDIM());
            resolutions.put(resolution, tgeox);
        }

        for (A_TILE_DESCRIPTION.Size asize : sizz) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(asize.getResolution());
            L3Metadata.TileGeometry tgeox = resolutions.get(resolution);
            tgeox.setNumCols(asize.getNCOLS());
            tgeox.setNumRows(asize.getNROWS());
        }
        return resolutions;
    }

    public static L3Metadata.AnglesGrid getSunGrid(Level3_Tile product) {

        A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();
        A_SUN_INCIDENCE_ANGLE_GRID sun = ang.getSun_Angles_Grid();

        int azrows = sun.getAzimuth().getValues_List().getVALUES().size();
        int azcolumns = sun.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

        int zenrows = sun.getZenith().getValues_List().getVALUES().size();
        int zencolumns = sun.getZenith().getValues_List().getVALUES().size();

        L3Metadata.AnglesGrid ag = new L3Metadata.AnglesGrid();
        ag.setAzimuth(new float[azrows][azcolumns]);
        ag.setZenith(new float[zenrows][zencolumns]);

        for (int rowindex = 0; rowindex < azrows; rowindex++) {
            List<Float> azimuths = sun.getAzimuth().getValues_List().getVALUES().get(rowindex).getValue();
            for (int colindex = 0; colindex < azcolumns; colindex++) {
                ag.getAzimuth()[rowindex][colindex] = azimuths.get(colindex);
            }
        }

        for (int rowindex = 0; rowindex < zenrows; rowindex++) {
            List<Float> zeniths = sun.getZenith().getValues_List().getVALUES().get(rowindex).getValue();
            for (int colindex = 0; colindex < zencolumns; colindex++) {
                ag.getZenith()[rowindex][colindex] = zeniths.get(colindex);
            }
        }

        return ag;
    }

    public static L3Metadata.AnglesGrid[] getAnglesGrid(Level3_Tile product) {
        A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();
        List<AN_INCIDENCE_ANGLE_GRID> incilist = ang.getViewing_Incidence_Angles_Grids();

        L3Metadata.AnglesGrid[] darr = new L3Metadata.AnglesGrid[incilist.size()];
        for (int index = 0; index < incilist.size(); index++) {
            AN_INCIDENCE_ANGLE_GRID angleGrid = incilist.get(index);

            int azrows2 = angleGrid.getAzimuth().getValues_List().getVALUES().size();
            int azcolumns2 = angleGrid.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

            int zenrows2 = angleGrid.getZenith().getValues_List().getVALUES().size();
            int zencolumns2 = angleGrid.getZenith().getValues_List().getVALUES().get(0).getValue().size();


            L3Metadata.AnglesGrid ag2 = new L3Metadata.AnglesGrid();
            ag2.setAzimuth(new float[azrows2][azcolumns2]);
            ag2.setZenith(new float[zenrows2][zencolumns2]);

            for (int rowindex = 0; rowindex < azrows2; rowindex++) {
                List<Float> azimuths = angleGrid.getAzimuth().getValues_List().getVALUES().get(rowindex).getValue();
                for (int colindex = 0; colindex < azcolumns2; colindex++) {
                    ag2.getAzimuth()[rowindex][colindex] = azimuths.get(colindex);
                }
            }

            for (int rowindex = 0; rowindex < zenrows2; rowindex++) {
                List<Float> zeniths = angleGrid.getZenith().getValues_List().getVALUES().get(rowindex).getValue();
                for (int colindex = 0; colindex < zencolumns2; colindex++) {
                    ag2.getZenith()[rowindex][colindex] = zeniths.get(colindex);
                }
            }

            ag2.setBandId(Integer.parseInt(angleGrid.getBandId()));
            ag2.setDetectorId(Integer.parseInt(angleGrid.getDetectorId()));
            darr[index] = ag2;
        }
        return darr;
    }
}
