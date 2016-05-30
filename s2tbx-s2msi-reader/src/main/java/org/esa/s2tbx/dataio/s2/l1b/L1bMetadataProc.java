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


import com.vividsolutions.jts.geom.Coordinate;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_GRANULE_DIMENSIONS;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_INFO;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_ORGANIZATION;
import https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata.Level1B_Granule;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1b.Level1B_User_Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2MetadataProc;
import org.esa.s2tbx.dataio.s2.S2MetadataType;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BDatastripFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.SystemUtils;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.esa.s2tbx.dataio.s2.l1b.CoordinateUtils.*;

/**
 * @author opicas-p
 */
public class L1bMetadataProc extends S2MetadataProc {

    public static JAXBContext getJaxbContext() throws JAXBException, FileNotFoundException {

        ClassLoader s2c = Level1B_User_Product.class.getClassLoader();
        return JAXBContext.newInstance(S2MetadataType.L1B, s2c);
    }

    private static S2SpectralInformation makeSpectralInformation(S2BandConstants bandConstant, S2SpatialResolution resolution) {
        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                makeSpectralBandImageFileTemplate(bandConstant.getFilenameBandId()),
                "Radiance in band " + bandConstant.getPhysicalName(),
                "",
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

    public static Collection<String> getTiles(Level1B_User_Product product) {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();

        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();

        Transformer tileSelector = o -> {
            A_PRODUCT_INFO.Product_Organisation.Granule_List ali = (A_PRODUCT_INFO.Product_Organisation.Granule_List) o;
            A_PRODUCT_ORGANIZATION.Granules gr = ali.getGranules();
            return gr.getGranuleIdentifier();
        };

        return CollectionUtils.collect(aGranuleList, tileSelector);
    }

    public static S2DatastripFilename getDatastrip(Level1B_User_Product product) {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();
        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();
        String granule = aGranuleList.get(0).getGranules().getGranuleIdentifier();
        S2GranuleDirFilename grafile = S2L1BGranuleDirFilename.create(granule);
        Guardian.assertNotNull("Product files don't match regular expressions", grafile);

        String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
        S2DatastripDirFilename dirDatastrip = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);
        String fileName = dirDatastrip.getFileName(null);
        return S2L1BDatastripFilename.create(fileName);
    }

    public static S2DatastripDirFilename getDatastripDir(Level1B_User_Product product) {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();
        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();
        String granule = aGranuleList.get(0).getGranules().getGranuleIdentifier();
        S2GranuleDirFilename grafile = S2L1BGranuleDirFilename.create(granule);
        Guardian.assertNotNull("Product files don't match regular expressions", grafile);

        String fileCategory = grafile.fileCategory;

        String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
        return S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, fileCategory);
    }


    public static List<Coordinate> getGranuleCorners(Level1B_Granule granule) {
        List<Double> polygon = granule.getGeometric_Info().getGranule_Footprint().getGranule_Footprint().getFootprint().getEXT_POS_LIST();

        return as3DCoordinates(polygon);
    }

    public static Map<S2SpatialResolution, L1bMetadata.TileGeometry> getGranuleGeometries(Level1B_Granule granule, S2Config config) {
        Map<S2SpatialResolution, L1bMetadata.TileGeometry> resolutions = new HashMap<>();

        List<A_GRANULE_DIMENSIONS.Size> sizes = granule.getGeometric_Info().getGranule_Dimensions().getSize();
        int pos = granule.getGeometric_Info().getGranule_Position().getPOSITION();
        String detector = granule.getGeneral_Info().getDETECTOR_ID().getValue();

        for (A_GRANULE_DIMENSIONS.Size gpos : sizes) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(gpos.getResolution());

            S2Metadata.TileGeometry tgeox = new L1bMetadata.TileGeometry();
            tgeox.setNumCols(gpos.getNCOLS());

            TileLayout tileLayout = config.getTileLayout(resolution);

            if (tileLayout != null) {
                tgeox.setNumRows(tileLayout.height);
            } else {
                SystemUtils.LOG.fine("No TileLayout at resolution R" + resolution + "m");
            }

            tgeox.setNumRowsDetector(gpos.getNROWS());
            tgeox.setPosition(pos);
            tgeox.setResolution(resolution.resolution);
            tgeox.setxDim(resolution.resolution);
            tgeox.setyDim(-resolution.resolution);
            tgeox.setDetector(detector);

            resolutions.put(resolution, tgeox);
        }

        return resolutions;
    }

    /*
    public static L1bMetadata.AnglesGrid getSunGrid(Level1B_Granule aGranule) {
        A_GRANULE_POSITION.Geometric_Header geoHeader = aGranule.getGeometric_Info().getGranule_Position().getGeometric_Header();
        L1bMetadata.AnglesGrid grid = new L1bMetadata.AnglesGrid();
        grid.setZenith(geoHeader.getSolar_Angles().getZENITH_ANGLE().getValue());
        grid.setAzimuth(geoHeader.getSolar_Angles().getAZIMUTH_ANGLE().getValue());
        return grid;
    }

    public static S2Metadata.AnglesGrid getAnglesGrid(Level1B_Granule aGranule) {
        A_GRANULE_POSITION.Geometric_Header geoHeader = aGranule.getGeometric_Info().getGranule_Position().getGeometric_Header();
        L1bMetadata.AnglesGrid grid = new L1bMetadata.AnglesGrid();
        grid.setZenith(geoHeader.getIncidence_Angles().getZENITH_ANGLE().getValue());
        grid.setAzimuth(geoHeader.getIncidence_Angles().getAZIMUTH_ANGLE().getValue());
        return grid;
    }
    */
}
