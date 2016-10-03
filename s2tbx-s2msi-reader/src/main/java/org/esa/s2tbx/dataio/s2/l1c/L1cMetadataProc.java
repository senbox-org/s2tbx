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

package org.esa.s2tbx.dataio.s2.l1c;


import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.AN_INCIDENCE_ANGLE_GRID;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_GEOMETRIC_INFO_TILE;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_MASK_LIST;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_INFO;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_ORGANIZATION;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_QUALITY_INDICATORS_INFO_TILE;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_SUN_INCIDENCE_ANGLE_GRID;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_TILE_DESCRIPTION;
import https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_tile_metadata.Level1C_Tile;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1c.Level1C_User_Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.math3.util.Pair;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2MetadataProc;
import org.esa.s2tbx.dataio.s2.S2MetadataType;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoMetadataProc;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoDatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author opicas-p
 */
public class L1cMetadataProc extends S2OrthoMetadataProc {

    public static JAXBContext getJaxbContext() throws JAXBException, FileNotFoundException {
        ClassLoader s2c = Level1C_User_Product.class.getClassLoader();
        return JAXBContext.newInstance(S2MetadataType.L1C, s2c);
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

    private static String makeSpectralBandImageFileTemplate(String bandFileId) {
        /* Sample :
        MISSION_ID : S2A
        SITECENTRE : MTI_
        CREATIONDATE : 20150813T201603
        ABSOLUTEORBIT : A000734
        TILENUMBER : T32TQR
        RESOLUTION : 10 | 20 | 60
         */
        return String.format("IMG_DATA%s{{MISSION_ID}}_OPER_MSI_L1C_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_%s.jp2", File.separator, bandFileId);
    }

    public static S2Metadata.ProductCharacteristics getProductOrganization(Level1C_User_Product product) {
        S2Metadata.ProductCharacteristics characteristics = new S2Metadata.ProductCharacteristics();
        characteristics.setSpacecraft(product.getGeneral_Info().getProduct_Info().getDatatake().getSPACECRAFT_NAME());
        characteristics.setDatasetProductionDate(product.getGeneral_Info().getProduct_Info().getDatatake().getDATATAKE_SENSING_START().toString());

        characteristics.setProductStartTime(((Element) product.getGeneral_Info().getProduct_Info().getPRODUCT_START_TIME()).getFirstChild().getNodeValue());
        characteristics.setProductStopTime(((Element) product.getGeneral_Info().getProduct_Info().getPRODUCT_STOP_TIME()).getFirstChild().getNodeValue());

        characteristics.setProcessingLevel(product.getGeneral_Info().getProduct_Info().getPROCESSING_LEVEL().getValue().value());
        characteristics.setMetaDataLevel(product.getGeneral_Info().getProduct_Info().getQuery_Options().getMETADATA_LEVEL());

        double toaQuantification = product.getGeneral_Info().getProduct_Image_Characteristics().getQUANTIFICATION_VALUE().getValue();
        characteristics.setQuantificationValue(toaQuantification);

        List<S2BandInformation> aInfo = getBandInformationList (toaQuantification);
        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2BandInformation[size]));

        return characteristics;
    }

    public static S2Metadata.ProductCharacteristics getTileProductOrganization(Level1C_Tile aTile) {
        S2Metadata.ProductCharacteristics characteristics = new S2Metadata.ProductCharacteristics();
        characteristics.setSpacecraft("Sentinel-2");

        //TODO anything from filename?
        //characteristics.setDatasetProductionDate("Unknown");
        //characteristics.setProductStartTime("Unknown");
        //characteristics.setProductStopTime("Unknown");

        characteristics.setProcessingLevel("Level-1C");

        double toaQuantification = 10000; //Default value
        characteristics.setQuantificationValue(toaQuantification);

        List<S2BandInformation> aInfo = getBandInformationList (toaQuantification);
        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2BandInformation[size]));

        return characteristics;
    }

    public static List<S2BandInformation> getBandInformationList (double toaQuantification) {
        List<S2BandInformation> aInfo = new ArrayList<>();
        aInfo.add(makeSpectralInformation(S2BandConstants.B1, S2SpatialResolution.R60M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B2, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B3, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B4, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B5, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B6, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B7, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B8A, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B9, S2SpatialResolution.R60M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B11, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B12, S2SpatialResolution.R20M, toaQuantification));
        return aInfo;
    }

    public static Collection<String> getTiles(Level1C_User_Product product) {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();

        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();

        Transformer tileSelector;
        tileSelector = o -> {
            A_PRODUCT_INFO.Product_Organisation.Granule_List ali = (A_PRODUCT_INFO.Product_Organisation.Granule_List) o;
            A_PRODUCT_ORGANIZATION.Granules gr = ali.getGranules();
            return gr.getGranuleIdentifier();
        };

        return CollectionUtils.collect(aGranuleList, tileSelector);
    }

    public static S2DatastripFilename getDatastrip(Level1C_User_Product product) {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();
        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();

        String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
        S2DatastripDirFilename dirDatastrip = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);

        S2DatastripFilename datastripFilename = null;
        if (dirDatastrip != null) {
            String fileName = dirDatastrip.getFileName(null);

            if (fileName != null) {
                datastripFilename = S2OrthoDatastripFilename.create(fileName);
            }
        }

        return datastripFilename;
    }

    public static S2DatastripDirFilename getDatastripDir(Level1C_User_Product product) {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();
        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();
        String granule = aGranuleList.get(0).getGranules().getGranuleIdentifier();
        S2OrthoGranuleDirFilename grafile = S2OrthoGranuleDirFilename.create(granule);

        S2DatastripDirFilename datastripDirFilename = null;
        if (grafile != null) {
            String fileCategory = grafile.fileCategory;
            String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
            datastripDirFilename = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, fileCategory);

        }
        return datastripDirFilename;
    }

    public static Map<S2SpatialResolution, L1cMetadata.TileGeometry> getTileGeometries(Level1C_Tile product) {
        A_GEOMETRIC_INFO_TILE info = product.getGeometric_Info();
        A_GEOMETRIC_INFO_TILE.Tile_Geocoding tgeo = info.getTile_Geocoding();


        List<A_TILE_DESCRIPTION.Geoposition> poss = tgeo.getGeoposition();
        List<A_TILE_DESCRIPTION.Size> sizz = tgeo.getSize();

        Map<S2SpatialResolution, L1cMetadata.TileGeometry> resolutions = new HashMap<>();

        for (A_TILE_DESCRIPTION.Geoposition gpos : poss) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(gpos.getResolution());
            L1cMetadata.TileGeometry tgeox = new L1cMetadata.TileGeometry();
            tgeox.setUpperLeftX(gpos.getULX());
            tgeox.setUpperLeftY(gpos.getULY());
            tgeox.setxDim(gpos.getXDIM());
            tgeox.setyDim(gpos.getYDIM());
            resolutions.put(resolution, tgeox);
        }

        for (A_TILE_DESCRIPTION.Size asize : sizz) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(asize.getResolution());
            L1cMetadata.TileGeometry tgeox = resolutions.get(resolution);
            tgeox.setNumCols(asize.getNCOLS());
            tgeox.setNumRows(asize.getNROWS());
        }

        return resolutions;
    }

    public static L1cMetadata.AnglesGrid getSunGrid(Level1C_Tile product) {
        A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();

        L1cMetadata.AnglesGrid ag = null;
        if (ang != null) {
            A_SUN_INCIDENCE_ANGLE_GRID sun = ang.getSun_Angles_Grid();

            int azrows = sun.getAzimuth().getValues_List().getVALUES().size();
            int azcolumns = sun.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

            int zenrows = sun.getZenith().getValues_List().getVALUES().size();
            int zencolumns = sun.getZenith().getValues_List().getVALUES().get(0).getValue().size();

            ag = new L1cMetadata.AnglesGrid();
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
        }

        return ag;
    }

    public static L1cMetadata.AnglesGrid[] getAnglesGrid(Level1C_Tile product) {
        A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();

        L1cMetadata.AnglesGrid[] darr = null;
        if (ang != null) {
            List<AN_INCIDENCE_ANGLE_GRID> filteredListe = ang.getViewing_Incidence_Angles_Grids();

            Map<Pair<String, String>, AN_INCIDENCE_ANGLE_GRID> theMap = new LinkedHashMap<>();
            for (AN_INCIDENCE_ANGLE_GRID aGrid : filteredListe) {
                theMap.put(new Pair<>(aGrid.getBandId(), aGrid.getDetectorId()), aGrid);
            }

            List<AN_INCIDENCE_ANGLE_GRID> incilist = new ArrayList<>(theMap.values());

            darr = new L1cMetadata.AnglesGrid[incilist.size()];
            for (int index = 0; index < incilist.size(); index++) {
                AN_INCIDENCE_ANGLE_GRID angleGrid = incilist.get(index);

                int azrows2 = angleGrid.getAzimuth().getValues_List().getVALUES().size();
                int azcolumns2 = angleGrid.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

                int zenrows2 = angleGrid.getZenith().getValues_List().getVALUES().size();
                int zencolumns2 = angleGrid.getZenith().getValues_List().getVALUES().get(0).getValue().size();


                L1cMetadata.AnglesGrid ag2 = new L1cMetadata.AnglesGrid();
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
        }

        return darr;
    }

    public static S2Metadata.MaskFilename[] getMasks(Level1C_Tile aTile, File file) {
        A_QUALITY_INDICATORS_INFO_TILE qualityInfo = aTile.getQuality_Indicators_Info();

        S2Metadata.MaskFilename[] maskFileNamesArray = null;
        if (qualityInfo != null) {
            List<A_MASK_LIST.MASK_FILENAME> masks = aTile.getQuality_Indicators_Info().getPixel_Level_QI().getMASK_FILENAME();
            List<L1cMetadata.MaskFilename> aMaskList = new ArrayList<>();
            for (A_MASK_LIST.MASK_FILENAME filename : masks) {
                File QIData = new File(file.getParent(), "QI_DATA");
                File GmlData = new File(QIData, filename.getValue());
                aMaskList.add(new L1cMetadata.MaskFilename(filename.getBandId(), filename.getType(), GmlData));
            }

            maskFileNamesArray = aMaskList.toArray(new L1cMetadata.MaskFilename[aMaskList.size()]);
        }
        return maskFileNamesArray;
    }

    public static String getPSD(Path path){
        //TODO
        return "PSD13";
    }
}
