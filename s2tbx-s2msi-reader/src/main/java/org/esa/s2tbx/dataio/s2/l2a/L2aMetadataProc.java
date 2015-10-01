/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author opicas-p
 */
public class L2aMetadataProc extends S2MetadataProc {

    public static JAXBContext getJaxbContext() throws JAXBException, FileNotFoundException {
        ClassLoader s2c = Level2A_User_Product.class.getClassLoader();
        return JAXBContext.newInstance(S2MetadataType.L2A, s2c);
    }


    public static L2aMetadata.ProductCharacteristics getProductOrganization(Level2A_User_Product product, S2SpatialResolution resolution) {

        L2aMetadata.ProductCharacteristics characteristics = new L2aMetadata.ProductCharacteristics();
        characteristics.setSpacecraft(product.getGeneral_Info().getL2A_Product_Info().getDatatake().getSPACECRAFT_NAME());
        characteristics.setDatasetProductionDate(product.getGeneral_Info().getL2A_Product_Info().getDatatake().getDATATAKE_SENSING_START().toString());
        characteristics.setProcessingLevel(product.getGeneral_Info().getL2A_Product_Info().getPROCESSING_LEVEL().getValue().value());

        List<S2SpectralInformation> aInfo = new ArrayList<>();

        aInfo.add(new S2SpectralInformation("B01", 0, resolution, 414, 472, 443));
        aInfo.add(new S2SpectralInformation("B02", 1, resolution, 425, 555, 490));
        aInfo.add(new S2SpectralInformation("B03", 2, resolution, 510, 610, 560));
        aInfo.add(new S2SpectralInformation("B04", 3, resolution, 617, 707, 665));
        aInfo.add(new S2SpectralInformation("B05", 4, resolution, 625, 722, 705));
        aInfo.add(new S2SpectralInformation("B06", 5, resolution, 720, 760, 740));
        aInfo.add(new S2SpectralInformation("B07", 6, resolution, 741, 812, 783));
        aInfo.add(new S2SpectralInformation("B08", 7, resolution, 752, 927, 842));
        aInfo.add(new S2SpectralInformation("B8A", 8, resolution, 823, 902, 865));
        aInfo.add(new S2SpectralInformation("B09", 9, resolution, 903, 982, 945));
        aInfo.add(new S2SpectralInformation("B10", 10, resolution, 1338, 1413, 1375));
        aInfo.add(new S2SpectralInformation("B11", 11, resolution, 1532, 1704, 1610));
        aInfo.add(new S2SpectralInformation("B12", 12, resolution, 2035, 2311, 2190));

        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2SpectralInformation[size]));

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
