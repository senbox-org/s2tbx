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

package org.esa.s2tbx.dataio.s2.l1b;


import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1b.Level1B_User_Product;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2MetadataProc;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.getHSBColor;

/**
 * @author opicas-p
 */
public class L1bMetadataProc extends S2MetadataProc {

    public static S2SpectralInformation makeSpectralInformation(S2BandConstants bandConstant, S2SpatialResolution resolution) {
        // TODO we should implement scaling factor here, using PHYSICAL_GAINS metadata per band, to provide physical radiance.
        final double quantification = 1.0;
        final String unit = "";

        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                makeSpectralBandImageFileTemplate(bandConstant.getFilenameBandId()),
                "Radiance in band " + bandConstant.getPhysicalName(),
                unit,
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
        return String.format("IMG_DATA%s{{MISSION_ID}}_OPER_MSI_L1B_GR_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{DETECTOR}}_%s.jp2", File.separator, bandFileId);
    }

    public static L1bMetadata.ProductCharacteristics getProductOrganization(Level1B_User_Product product) {

        L1bMetadata.ProductCharacteristics characteristics = new L1bMetadata.ProductCharacteristics();
        characteristics.setSpacecraft(product.getGeneral_Info().getProduct_Info().getDatatake().getSPACECRAFT_NAME());
        characteristics.setDatasetProductionDate(product.getGeneral_Info().getProduct_Info().getDatatake().getDATATAKE_SENSING_START().toString());
        characteristics.setProcessingLevel(product.getGeneral_Info().getProduct_Info().getPROCESSING_LEVEL().getValue().value());

        characteristics.setProductStartTime(((Element) product.getGeneral_Info().getProduct_Info().getPRODUCT_START_TIME()).getFirstChild().getNodeValue());
        characteristics.setProductStopTime(((Element) product.getGeneral_Info().getProduct_Info().getPRODUCT_STOP_TIME()).getFirstChild().getNodeValue());

        List<S2BandInformation> aInfo = new ArrayList<>();

        /*
         * User products do not provide spectral information
         * so we hardcode them here
         *
        Object spectral_list = product.getGeneral_Info().getProduct_Image_Characteristics().getSpectral_Information_List();

        if (spectral_list != null) {

            List<A_PRODUCT_INFO_USERL1B.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information> spectralInfoList = product.getGeneral_Info().getProduct_Image_Characteristics().getSpectral_Information_List().getSpectral_Information();


            for (A_PRODUCT_INFO_USERL1B.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information sin : spectralInfoList) {
                S2SpectralInformation data = new S2SpectralInformation();
                data.setBandId(Integer.parseInt(sin.getBandId()));
                data.setPhysicalBand(sin.getPhysicalBand().value());
                data.setResolution(S2SpatialResolution.valueOfResolution(sin.getRESOLUTION()));

                int size = sin.getSpectral_Response().getVALUES().size();
                data.setSpectralResponseValues(ArrayUtils.toPrimitive(sin.getSpectral_Response().getVALUES().toArray(new Double[size])));
                data.setWavelengthCentral(sin.getWavelength().getCENTRAL().getValue());
                data.setWavelengthMax(sin.getWavelength().getMAX().getValue());
                data.setWavelengthMin(sin.getWavelength().getMIN().getValue());

                aInfo.add(data);
            }
        }
        */
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
        aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M));
        aInfo.add(makeSpectralInformation(S2BandConstants.B11, S2SpatialResolution.R20M));
        aInfo.add(makeSpectralInformation(S2BandConstants.B12, S2SpatialResolution.R20M));

        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2BandInformation[size]));

        return characteristics;
    }

    public static S2IndexBandInformation makeTileInformation(String detector, S2SpatialResolution resolution, L1bSceneDescription sceneDescription) {

        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        List<String> tiles = new ArrayList<>();

        for(String tileId : sceneDescription.getOrderedTileIds()) {
            String detectorId = ((S2L1BGranuleDirFilename) S2L1BGranuleDirFilename.create(tileId)).getDetectorId();
            if (detectorId.equals(detector)) {
                tiles.add(tileId);
            }
        }

        int numberOfTiles = tiles.size();
        int index = 1;

        for(String tileId : tiles) {
            float f;
            f = (index-1)*(float)1.0/(numberOfTiles+1);
            f = (float) 0.75 - f;
            if (f < 0) f++;
            if(S2L1BGranuleDirFilename.create(tileId).getTileID()!=null) {
                indexList.add(S2IndexBandInformation.makeIndex(index, getHSBColor(f, (float) 1.0, (float) 1.0), S2L1BGranuleDirFilename.create(tileId).getTileID(), tileId));
            } else {
                indexList.add(S2IndexBandInformation.makeIndex(index, getHSBColor(f, (float) 1.0, (float) 1.0), tileId, tileId));
            }
            index++;
        }
        return new S2IndexBandInformation(detector + "_tile_id_" + resolution.resolution + "m", resolution, "", "Tile ID", "", indexList, "tile_" + resolution.resolution + "m_");
    }
}
