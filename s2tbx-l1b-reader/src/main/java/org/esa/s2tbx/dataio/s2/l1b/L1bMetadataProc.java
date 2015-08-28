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

package org.esa.s2tbx.dataio.s2.l1b;


import com.vividsolutions.jts.geom.Coordinate;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_GRANULE_DIMENSIONS;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_INFO;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_INFO_USERL1B;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_ORGANIZATION;
import https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata.Level1B_Granule;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1b.Level1B_User_Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2MetadataProc;
import org.esa.s2tbx.dataio.s2.S2MetadataType;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BDatastripFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.snap.util.Guardian;
import org.esa.snap.util.SystemUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.esa.s2tbx.dataio.s2.l1b.CoordinateUtils.as3DCoordinates;

/**
 * @author opicas-p
 */
public class L1bMetadataProc extends S2MetadataProc {

    public static JAXBContext getJaxbContext() throws JAXBException, FileNotFoundException {

        ClassLoader s2c = Level1B_User_Product.class.getClassLoader();
        return JAXBContext.newInstance(S2MetadataType.L1B, s2c);
    }

    public static L1bMetadata.ProductCharacteristics getProductOrganization(Level1B_User_Product product) {

        L1bMetadata.ProductCharacteristics characteristics = new L1bMetadata.ProductCharacteristics();
        characteristics.setSpacecraft(product.getGeneral_Info().getProduct_Info().getDatatake().getSPACECRAFT_NAME());
        characteristics.setDatasetProductionDate(product.getGeneral_Info().getProduct_Info().getDatatake().getDATATAKE_SENSING_START().toString());
        characteristics.setProcessingLevel(product.getGeneral_Info().getProduct_Info().getPROCESSING_LEVEL().getValue().value());

        List<S2SpectralInformation> aInfo = new ArrayList<>();
        Object spectral_list = product.getGeneral_Info().getProduct_Image_Characteristics().getSpectral_Information_List();
        if (spectral_list != null) {

            List<A_PRODUCT_INFO_USERL1B.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information> spectralInfoList = product.getGeneral_Info().getProduct_Image_Characteristics().getSpectral_Information_List().getSpectral_Information();


            for (A_PRODUCT_INFO_USERL1B.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information sin : spectralInfoList) {
                S2SpectralInformation data = new S2SpectralInformation();
                data.setBandId(Integer.parseInt(sin.getBandId()));
                data.setPhysicalBand(sin.getPhysicalBand().value());
                data.setResolution(sin.getRESOLUTION());

                int size = sin.getSpectral_Response().getVALUES().size();
                data.setSpectralResponseValues(ArrayUtils.toPrimitive(sin.getSpectral_Response().getVALUES().toArray(new Double[size])));
                data.setWavelengthCentral(sin.getWavelength().getCENTRAL().getValue());
                data.setWavelengthMax(sin.getWavelength().getMAX().getValue());
                data.setWavelengthMin(sin.getWavelength().getMIN().getValue());

                aInfo.add(data);
            }
        } else {
            SystemUtils.LOG.warning("Empty spectral info !");
            aInfo.add(new S2SpectralInformation("B1",0,60, 414, 472, 443));
            aInfo.add(new S2SpectralInformation("B2",1,10, 425, 555, 490));
            aInfo.add(new S2SpectralInformation("B3",2,10, 510, 610, 560));
            aInfo.add(new S2SpectralInformation("B4",3,10, 617, 707, 665));
            aInfo.add(new S2SpectralInformation("B5",4,20, 625, 722, 705));
            aInfo.add(new S2SpectralInformation("B6",5,20, 720, 760, 740));
            aInfo.add(new S2SpectralInformation("B7",6,20, 741, 812, 783));
            aInfo.add(new S2SpectralInformation("B8",7,10, 752, 927, 842));
            aInfo.add(new S2SpectralInformation("B8A",8,20, 823, 902, 865));
            aInfo.add(new S2SpectralInformation("B9",9,60, 903, 982, 945));
            aInfo.add(new S2SpectralInformation("B10",10,60, 1338, 1413, 1375));
            aInfo.add(new S2SpectralInformation("B11",11,20, 1532, 1704, 1610));
            aInfo.add(new S2SpectralInformation("B12",12,20, 2035, 2311, 2190));
        }

        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2SpectralInformation[size]));

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

    public static Map<Integer, L1bMetadata.TileGeometry> getGranuleGeometries(Level1B_Granule granule,
                                                                              S2Config config) {
        Map<Integer, L1bMetadata.TileGeometry> resolutions = new HashMap<>();

        List<A_GRANULE_DIMENSIONS.Size> sizes = granule.getGeometric_Info().getGranule_Dimensions().getSize();
        int pos = granule.getGeometric_Info().getGranule_Position().getPOSITION();
        String detector = granule.getGeneral_Info().getDETECTOR_ID().getValue();

        for (A_GRANULE_DIMENSIONS.Size gpos : sizes) {
            int resolution = gpos.getResolution();

            S2Metadata.TileGeometry tgeox = new L1bMetadata.TileGeometry();
            tgeox.setNumCols(gpos.getNCOLS());

            TileLayout tileLayout = config.getTileLayout(resolution);

            if(tileLayout != null) {
                tgeox.setNumRows(tileLayout.height);
            } else {
                SystemUtils.LOG.fine("No TileLayout at resolution R" + resolution + "m");
            }

            tgeox.setNumRowsDetector(gpos.getNROWS());
            tgeox.setPosition(pos);
            tgeox.setResolution(resolution);
            tgeox.setxDim(resolution);
            tgeox.setyDim(-resolution);
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
