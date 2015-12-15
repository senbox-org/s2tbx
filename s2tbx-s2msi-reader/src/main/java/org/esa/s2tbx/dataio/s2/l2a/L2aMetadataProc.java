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

package org.esa.s2tbx.dataio.s2.l2a;

import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.*;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_2a_tile_metadata.Level2A_Tile;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_2a.Level2A_User_Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.esa.s2tbx.dataio.s2.*;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoDatastripFilename;
import org.esa.snap.core.datamodel.ColorPaletteDef;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.IndexCoding;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

/**
 * @author opicas-p
 */
public class L2aMetadataProc extends S2MetadataProc {

    public static JAXBContext getJaxbContext() throws JAXBException, FileNotFoundException {
        ClassLoader s2c = Level2A_User_Product.class.getClassLoader();
        return JAXBContext.newInstance(S2MetadataType.L2A, s2c);
    }

    private static S2SpectralInformation makeSpectralInformation(S2BandConstants bandConstant, S2SpatialResolution resolution) {
        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                makeSpectralBandImageFileTemplate(bandConstant.getFilenameBandId()),
                "Reflectance in band " + bandConstant.getPhysicalName(),
                "dl",
                bandConstant.getBandIndex(),
                bandConstant.getWavelengthMin(),
                bandConstant.getWavelengthMax(),
                bandConstant.getWavelengthCentral());
    }

    private static S2BandInformation makeAOTInformation(S2SpatialResolution resolution) {
        return new S2BandInformation("quality_aot", resolution, makeAOTFileTemplate(), "Aerosol Optical Thickness", "none (x 1000)");
    }

    private static S2BandInformation makeWVPInformation(S2SpatialResolution resolution) {
        return new S2BandInformation("quality_wvp", resolution, makeWVPFileTemplate(), "Water Vapour", "cm (x 1000)");
    }

    private static S2BandInformation makeCLDInformation(S2SpatialResolution resolution) {
        return new S2BandInformation("quality_cloud_confidence", resolution, makeCLDFileTemplate(), "Cloud Confidence", "%");
    }

    private static S2BandInformation makeSNWInformation(S2SpatialResolution resolution) {
        return new S2BandInformation("quality_snow_confidence", resolution, makeSNWFileTemplate(), "Snow Confidence", "%");
    }

    private static S2BandInformation makeSCLInformation(S2SpatialResolution resolution) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        /* Using the same colors as in the L2A-PDD */
        indexList.add(S2IndexBandInformation.makeIndex(0,  new Color(  0,   0,   0), "NODATA", "No data"));
        indexList.add(S2IndexBandInformation.makeIndex(1,  new Color(255,   0,   0), "SATURATED_DEFECTIVE", "Saturated or defective"));
        indexList.add(S2IndexBandInformation.makeIndex(2,  new Color( 46,  46,  46), "DARK_FEATURE_SHADOW","Dark feature shadow"));
        indexList.add(S2IndexBandInformation.makeIndex(3,  new Color(100,  50,   0), "CLOUD_SHADOW", "Cloud shadow"));
        indexList.add(S2IndexBandInformation.makeIndex(4,  new Color(  0, 128,   0), "VEGETATION", "Vegetation"));
        indexList.add(S2IndexBandInformation.makeIndex(5,  new Color(255, 230,  90), "BARE_SOIL_DESERT", "Bare soil / Desert"));
        indexList.add(S2IndexBandInformation.makeIndex(6,  new Color(  0,   0, 255), "WATER", "Water"));
        indexList.add(S2IndexBandInformation.makeIndex(7,  new Color(129, 129, 129), "CLOUD_LOW_PROBA", "Cloud (low probability)"));
        indexList.add(S2IndexBandInformation.makeIndex(8,  new Color(193, 193, 193), "CLOUD_MEDIUM_PROBA", "Cloud (medium probability)"));
        indexList.add(S2IndexBandInformation.makeIndex(9,  new Color(255, 255, 255), "CLOUD_HIGH_PROBA", "Cloud (high probability)"));
        indexList.add(S2IndexBandInformation.makeIndex(10, new Color(100, 200, 255), "THIN_CIRRUS", "Thin cirrus"));
        indexList.add(S2IndexBandInformation.makeIndex(11, new Color(255, 150, 255), "SNOW_ICE", "Snow or Ice"));
        return new S2IndexBandInformation("quality_scene_classification", resolution, makeSCLFileTemplate(), "Scene classification", "", indexList);
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
        return String.format("IMG_DATA%sR{{RESOLUTION}}m%s{{MISSION_ID}}_USER_MSI_L2A_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_%s_{{RESOLUTION}}m.jp2",
                File.separator, File.separator, bandFileId);
    }

    private static String makeAOTFileTemplate() {
        return String.format("IMG_DATA%sR{{RESOLUTION}}m%s{{MISSION_ID}}_USER_AOT_L2A_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_{{RESOLUTION}}m.jp2",
                File.separator, File.separator);
    }

    private static String makeWVPFileTemplate() {
        return String.format("IMG_DATA%sR{{RESOLUTION}}m%s{{MISSION_ID}}_USER_WVP_L2A_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_{{RESOLUTION}}m.jp2",
                File.separator, File.separator);
    }

    private static String makeSCLFileTemplate() {
        return String.format("IMG_DATA%s{{MISSION_ID}}_USER_SCL_L2A_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_{{RESOLUTION}}m.jp2", File.separator);
    }

    private static String makeCLDFileTemplate() {
        return String.format("QI_DATA%s{{MISSION_ID}}_USER_CLD_L2A_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_{{RESOLUTION}}m.jp2", File.separator);
    }

    private static String makeSNWFileTemplate() {
        return String.format("QI_DATA%s{{MISSION_ID}}_USER_SNW_L2A_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_{{RESOLUTION}}m.jp2", File.separator);
    }

    public static L2aMetadata.ProductCharacteristics getProductOrganization(Level2A_User_Product product, S2SpatialResolution resolution) {
        L2aMetadata.ProductCharacteristics characteristics = new L2aMetadata.ProductCharacteristics();
        characteristics.setSpacecraft(product.getGeneral_Info().getL2A_Product_Info().getDatatake().getSPACECRAFT_NAME());
        characteristics.setDatasetProductionDate(product.getGeneral_Info().getL2A_Product_Info().getDatatake().getDATATAKE_SENSING_START().toString());
        characteristics.setProcessingLevel(product.getGeneral_Info().getL2A_Product_Info().getPROCESSING_LEVEL().getValue().value());

        List<S2BandInformation> aInfo = new ArrayList<>();
        switch(resolution) {
            case R10M:
                aInfo.add(makeSpectralInformation(S2BandConstants.B1, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B2, S2SpatialResolution.R10M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B3, S2SpatialResolution.R10M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B4, S2SpatialResolution.R10M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B5, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B6, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B7, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8A, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B9, S2SpatialResolution.R60M));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B11, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B12, S2SpatialResolution.R20M));

                aInfo.add(makeAOTInformation(S2SpatialResolution.R10M));
                aInfo.add(makeWVPInformation(S2SpatialResolution.R10M));
                aInfo.add(makeCLDInformation(S2SpatialResolution.R20M));
                aInfo.add(makeSNWInformation(S2SpatialResolution.R20M));

                // SCL only generated at 20m and 60m. upsample the 20m version
                aInfo.add(makeSCLInformation(S2SpatialResolution.R20M));
                break;
            case R20M:
                aInfo.add(makeSpectralInformation(S2BandConstants.B1, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B2, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B3, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B4, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B5, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B6, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B7, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8A, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B9, S2SpatialResolution.R60M));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B11, S2SpatialResolution.R20M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B12, S2SpatialResolution.R20M));

                aInfo.add(makeAOTInformation(S2SpatialResolution.R20M));
                aInfo.add(makeWVPInformation(S2SpatialResolution.R20M));
                aInfo.add(makeCLDInformation(S2SpatialResolution.R20M));
                aInfo.add(makeSNWInformation(S2SpatialResolution.R20M));

                aInfo.add(makeSCLInformation(S2SpatialResolution.R20M));
                break;
            case R60M:
                aInfo.add(makeSpectralInformation(S2BandConstants.B1, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B2, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B3, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B4, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B5, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B6, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B7, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B8A, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B9, S2SpatialResolution.R60M));
                //aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B11, S2SpatialResolution.R60M));
                aInfo.add(makeSpectralInformation(S2BandConstants.B12, S2SpatialResolution.R60M));

                aInfo.add(makeAOTInformation(S2SpatialResolution.R60M));
                aInfo.add(makeWVPInformation(S2SpatialResolution.R60M));
                aInfo.add(makeCLDInformation(S2SpatialResolution.R60M));
                aInfo.add(makeSNWInformation(S2SpatialResolution.R60M));

                aInfo.add(makeSCLInformation(S2SpatialResolution.R60M));
                break;
        }
        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2BandInformation[size]));

        return characteristics;
    }

    public static Collection<String> getTiles(Level2A_User_Product product) {
        A_L2A_Product_Info.L2A_Product_Organisation info = product.getGeneral_Info().getL2A_Product_Info().getL2A_Product_Organisation();

        List<A_L2A_Product_Info.L2A_Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();

        Transformer tileSelector = o -> {
            A_L2A_Product_Info.L2A_Product_Organisation.Granule_List ali = (A_L2A_Product_Info.L2A_Product_Organisation.Granule_List) o;
            A_PRODUCT_ORGANIZATION_2A.Granules gr = ali.getGranules();
            return gr.getGranuleIdentifier();
        };

        return CollectionUtils.collect(aGranuleList, tileSelector);
    }

    public static S2DatastripFilename getDatastrip(Level2A_User_Product product) {
        A_L2A_Product_Info.L2A_Product_Organisation info = product.getGeneral_Info().getL2A_Product_Info().getL2A_Product_Organisation();

        String dataStripMetadataFilenameCandidate = info.getGranule_List().get(0).getGranules().getDatastripIdentifier();
        S2DatastripDirFilename dirDatastrip = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);

        if (dirDatastrip != null) {
            String fileName = dirDatastrip.getFileName(null);
            return S2OrthoDatastripFilename.create(fileName);
        } else {
            return null;
        }
    }

    public static S2DatastripDirFilename getDatastripDir(Level2A_User_Product product) {
        A_L2A_Product_Info.L2A_Product_Organisation info = product.getGeneral_Info().getL2A_Product_Info().getL2A_Product_Organisation();
        String dataStripMetadataFilenameCandidate = info.getGranule_List().get(0).getGranules().getDatastripIdentifier();

        return S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);
    }

    public static Map<S2SpatialResolution, L2aMetadata.TileGeometry> getTileGeometries(Level2A_Tile product) {

        A_GEOMETRIC_INFO_TILE info = product.getGeometric_Info();
        A_GEOMETRIC_INFO_TILE.Tile_Geocoding tgeo = info.getTile_Geocoding();


        List<A_TILE_DESCRIPTION.Geoposition> poss = tgeo.getGeoposition();
        List<A_TILE_DESCRIPTION.Size> sizz = tgeo.getSize();

        Map<S2SpatialResolution, L2aMetadata.TileGeometry> resolutions = new HashMap<>();

        for (A_TILE_DESCRIPTION.Geoposition gpos : poss) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(gpos.getResolution());
            L2aMetadata.TileGeometry tgeox = new L2aMetadata.TileGeometry();
            tgeox.setUpperLeftX(gpos.getULX());
            tgeox.setUpperLeftY(gpos.getULY());
            tgeox.setxDim(gpos.getXDIM());
            tgeox.setyDim(gpos.getYDIM());
            resolutions.put(resolution, tgeox);
        }

        for (A_TILE_DESCRIPTION.Size asize : sizz) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(asize.getResolution());
            L2aMetadata.TileGeometry tgeox = resolutions.get(resolution);
            tgeox.setNumCols(asize.getNCOLS());
            tgeox.setNumRows(asize.getNROWS());
        }

        return resolutions;
    }

    public static L2aMetadata.AnglesGrid getSunGrid(Level2A_Tile product) {

        A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();
        A_SUN_INCIDENCE_ANGLE_GRID sun = ang.getSun_Angles_Grid();

        int azrows = sun.getAzimuth().getValues_List().getVALUES().size();
        int azcolumns = sun.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

        int zenrows = sun.getZenith().getValues_List().getVALUES().size();
        int zencolumns = sun.getZenith().getValues_List().getVALUES().size();

        L2aMetadata.AnglesGrid ag = new L2aMetadata.AnglesGrid();
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

    public static L2aMetadata.AnglesGrid[] getAnglesGrid(Level2A_Tile product) {
        A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();
        List<AN_INCIDENCE_ANGLE_GRID> incilist = ang.getViewing_Incidence_Angles_Grids();

        L2aMetadata.AnglesGrid[] darr = new L2aMetadata.AnglesGrid[incilist.size()];
        for (int index = 0; index < incilist.size(); index++) {
            AN_INCIDENCE_ANGLE_GRID angleGrid = incilist.get(index);

            int azrows2 = angleGrid.getAzimuth().getValues_List().getVALUES().size();
            int azcolumns2 = angleGrid.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

            int zenrows2 = angleGrid.getZenith().getValues_List().getVALUES().size();
            int zencolumns2 = angleGrid.getZenith().getValues_List().getVALUES().get(0).getValue().size();


            L2aMetadata.AnglesGrid ag2 = new L2aMetadata.AnglesGrid();
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

    public static S2Metadata.MaskFilename[] getMasks(Level2A_Tile aTile, File file) {
        A_QUALITY_INDICATORS_INFO_TILE_L2A qualityInfo = aTile.getQuality_Indicators_Info();

        S2Metadata.MaskFilename[] maskFileNamesArray = null;
        if (qualityInfo != null) {
            List<A_MASK_LIST.MASK_FILENAME> masks = aTile.getQuality_Indicators_Info().getL1C_Pixel_Level_QI().getMASK_FILENAME();
            List<L2aMetadata.MaskFilename> aMaskList = new ArrayList<>();
            for (A_MASK_LIST.MASK_FILENAME filename : masks) {
                File QIData = new File(file.getParent(), "QI_DATA");
                File GmlData = new File(QIData, filename.getValue());
                aMaskList.add(new L2aMetadata.MaskFilename(filename.getBandId(), filename.getType(), GmlData));
            }

            maskFileNamesArray = aMaskList.toArray(new L2aMetadata.MaskFilename[aMaskList.size()]);
        }
        return maskFileNamesArray;
    }
}
