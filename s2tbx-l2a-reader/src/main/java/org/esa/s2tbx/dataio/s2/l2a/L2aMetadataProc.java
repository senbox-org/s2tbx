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

import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.AN_INCIDENCE_ANGLE_GRID;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_DATATAKE_IDENTIFICATION;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_GEOMETRIC_INFO_TILE;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_L2A_Product_Info;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_INFO_USERL2A;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_ORGANIZATION_2A;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_SUN_INCIDENCE_ANGLE_GRID;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_TILE_DESCRIPTION;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_2a_tile_metadata.Level2A_Tile;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_2a.Level2A_User_Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.s2.l2a.filepatterns.S2L2aDatastripDirFilename;
import org.esa.s2tbx.dataio.s2.l2a.filepatterns.S2L2aDatastripFilename;
import org.esa.snap.util.logging.BeamLogManager;
import org.openjpeg.StackTraceUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class L2aMetadataProc {

    public static String getModulesDir() throws URISyntaxException, FileNotFoundException {
        String subStr = "s2tbx-l2a-reader";

        ClassLoader s2c = Sentinel2L2AProductReader.class.getClassLoader();
        URLClassLoader s2ClassLoader = (URLClassLoader) s2c;

        URL[] theURLs = s2ClassLoader.getURLs();
        for (URL url : theURLs) {
            if (url.getPath().contains(subStr) && url.getPath().contains(".jar")) {
                URI uri = url.toURI();
                URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
                return parent.getPath();
            } else {
                //todo please note that in dev, all the module jar files are unzipped in modules folder, so SNAP only reaches this code in dev environments
                if (url.getPath().contains(subStr)) {
                    URI uri = url.toURI();
                    URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
                    return parent.getPath();
                }
            }
        }

        throw new FileNotFoundException("Module " + subStr + " not found !");
    }

    public static String tryGetModulesDir() {
        String theDir = "./";
        try {
            theDir = getModulesDir();
        } catch (Exception e) {
            BeamLogManager.getSystemLogger().severe(StackTraceUtils.getStackTrace(e));
        }
        return theDir;
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Deprecated
    public static void setExecutable(File file, boolean executable) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                    "chmod",
                    "u" + (executable ? '+' : '-') + "x",
                    file.getAbsolutePath(),
            });
            p.waitFor();
            String output = convertStreamToString(p.getInputStream());
            String errorOutput = convertStreamToString(p.getErrorStream());
        } catch (Exception e) {
            BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
        }
    }

    @Deprecated
    public static Object readJaxbFromFilename(InputStream stream) throws JAXBException, FileNotFoundException {

        ClassLoader s2c = Sentinel2L2AProductReader.class.getClassLoader();
        JAXBContext jaxbContext = JAXBContext.newInstance(L2AMetadataType.L2A, s2c);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        Object ob = unmarshaller.unmarshal(stream);
        Object casted = ((JAXBElement) ob).getValue();

        return casted;
    }

    public static JAXBContext getJaxbContext() throws JAXBException, FileNotFoundException {
        ClassLoader s2c = Sentinel2L2AProductReader.class.getClassLoader();
        JAXBContext jaxbContext = JAXBContext.newInstance(L2AMetadataType.L2A, s2c);
        return jaxbContext;
    }

    public static L2aMetadata.ProductCharacteristics parseCharacteristics(Level2A_User_Product product) {
        A_DATATAKE_IDENTIFICATION info = product.getGeneral_Info().getL2A_Product_Info().getDatatake();

        L2aMetadata.ProductCharacteristics characteristics = new L2aMetadata.ProductCharacteristics();
        characteristics.spacecraft = info.getSPACECRAFT_NAME();
        characteristics.datasetProductionDate = product.getGeneral_Info().getL2A_Product_Info().getGENERATION_TIME().toString();
        characteristics.processingLevel = product.getGeneral_Info().getL2A_Product_Info().getPROCESSING_LEVEL().getValue().toString();

        List<L2aMetadata.SpectralInformation> targetList = new ArrayList<L2aMetadata.SpectralInformation>();

        List<A_PRODUCT_INFO_USERL2A.L2A_Product_Image_Characteristics.Spectral_Information_List.Spectral_Information> aList = product.getGeneral_Info().getL2A_Product_Image_Characteristics().getSpectral_Information_List().getSpectral_Information();
        for (A_PRODUCT_INFO_USERL2A.L2A_Product_Image_Characteristics.Spectral_Information_List.Spectral_Information si : aList) {
            L2aMetadata.SpectralInformation newInfo = new L2aMetadata.SpectralInformation();
            newInfo.bandId = Integer.parseInt(si.getBandId());
            newInfo.physicalBand = si.getPhysicalBand().value();

            if(newInfo.physicalBand.length() == 2)
            {
                char[] bigBand = new char[3];
                bigBand[0] = newInfo.physicalBand.charAt(0);
                bigBand[1] =  '0';
                bigBand[2] = newInfo.physicalBand.charAt(1);
                newInfo.physicalBand = new String(bigBand);
            }

            newInfo.resolution = si.getRESOLUTION();
            newInfo.spectralResponseStep = si.getSpectral_Response().getSTEP().getValue();
            newInfo.wavelenghtCentral = si.getWavelength().getCENTRAL().getValue();
            newInfo.wavelenghtMax = si.getWavelength().getMAX().getValue();
            newInfo.wavelenghtMin = si.getWavelength().getMIN().getValue();

            int size = si.getSpectral_Response().getVALUES().size();
            newInfo.spectralResponseValues = ArrayUtils.toPrimitive(si.getSpectral_Response().getVALUES().toArray(new Double[size]));
            targetList.add(newInfo);
        }

        int size = targetList.size();
        characteristics.bandInformations = targetList.toArray(new L2aMetadata.SpectralInformation[size]);

        return characteristics;
    }

    public static L2aMetadata.ProductCharacteristics getProductOrganization(Level2A_User_Product product) {
        A_L2A_Product_Info.L2A_Product_Organisation info = product.getGeneral_Info().getL2A_Product_Info().getL2A_Product_Organisation();

        L2aMetadata.ProductCharacteristics characteristics = new L2aMetadata.ProductCharacteristics();
        characteristics.spacecraft = product.getGeneral_Info().getL2A_Product_Info().getDatatake().getSPACECRAFT_NAME();
        characteristics.datasetProductionDate = product.getGeneral_Info().getL2A_Product_Info().getDatatake().getDATATAKE_SENSING_START().toString();
        characteristics.processingLevel = product.getGeneral_Info().getL2A_Product_Info().getPROCESSING_LEVEL().getValue().value();

        A_PRODUCT_INFO_USERL2A.L2A_Product_Image_Characteristics.Spectral_Information_List spectralInformationList = product.getGeneral_Info().getL2A_Product_Image_Characteristics().getSpectral_Information_List();
        List<L2aMetadata.SpectralInformation> aInfo = new ArrayList<L2aMetadata.SpectralInformation>();

        if (spectralInformationList != null) {
            List<A_PRODUCT_INFO_USERL2A.L2A_Product_Image_Characteristics.Spectral_Information_List.Spectral_Information> spectralInfoList = spectralInformationList.getSpectral_Information();



            for (A_PRODUCT_INFO_USERL2A.L2A_Product_Image_Characteristics.Spectral_Information_List.Spectral_Information sin : spectralInfoList) {
                L2aMetadata.SpectralInformation data = new L2aMetadata.SpectralInformation();
                data.bandId = Integer.parseInt(sin.getBandId());
                data.physicalBand = sin.getPhysicalBand().value();

                if(data.physicalBand.length() == 2)
                {
                    char[] bigBand = new char[3];
                    bigBand[0] = data.physicalBand.charAt(0);
                    bigBand[1] =  '0';
                    bigBand[2] = data.physicalBand.charAt(1);
                    data.physicalBand = new String(bigBand);
                }

                data.resolution = sin.getRESOLUTION();
                data.spectralResponseStep = sin.getSpectral_Response().getSTEP().getValue();

                int size = sin.getSpectral_Response().getVALUES().size();
                data.spectralResponseValues = ArrayUtils.toPrimitive(sin.getSpectral_Response().getVALUES().toArray(new Double[size]));
                data.wavelenghtCentral = sin.getWavelength().getCENTRAL().getValue();
                data.wavelenghtMax = sin.getWavelength().getMAX().getValue();
                data.wavelenghtMin = sin.getWavelength().getMIN().getValue();

                aInfo.add(data);
            }

            int size = aInfo.size();
            characteristics.bandInformations = aInfo.toArray(new L2aMetadata.SpectralInformation[size]);
        }
        else
        {
            // warning hardcoded resolutions
            aInfo.add(new L2aMetadata.SpectralInformation("B1",0,60));
            aInfo.add(new L2aMetadata.SpectralInformation("B2",1,10));
            aInfo.add(new L2aMetadata.SpectralInformation("B3",2,10));
            aInfo.add(new L2aMetadata.SpectralInformation("B4",3,10));
            aInfo.add(new L2aMetadata.SpectralInformation("B5",4,20));
            aInfo.add(new L2aMetadata.SpectralInformation("B6",5,20));
            aInfo.add(new L2aMetadata.SpectralInformation("B7",6,20));
            aInfo.add(new L2aMetadata.SpectralInformation("B8",7,10));
            aInfo.add(new L2aMetadata.SpectralInformation("B8A",8,20));
            aInfo.add(new L2aMetadata.SpectralInformation("B9",9,60));
            aInfo.add(new L2aMetadata.SpectralInformation("B10",10,60));
            aInfo.add(new L2aMetadata.SpectralInformation("B11",11,20));
            aInfo.add(new L2aMetadata.SpectralInformation("B12",12,20));
        }

        return characteristics;
    }

    public static Collection<String> getTiles(Level2A_User_Product product) {
        A_L2A_Product_Info.L2A_Product_Organisation info = product.getGeneral_Info().getL2A_Product_Info().getL2A_Product_Organisation();

        List<A_L2A_Product_Info.L2A_Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();

        Transformer tileSelector = new Transformer() {
            @Override
            public Object transform(Object o) {
                A_L2A_Product_Info.L2A_Product_Organisation.Granule_List ali = (A_L2A_Product_Info.L2A_Product_Organisation.Granule_List) o;
                A_PRODUCT_ORGANIZATION_2A.Granules gr = ali.getGranules();
                return gr.getGranuleIdentifier();
            }
        };

        Collection col = CollectionUtils.collect(aGranuleList, tileSelector);
        return col;
    }

    public static S2L2aDatastripFilename getDatastrip(Level2A_User_Product product) {
        A_L2A_Product_Info.L2A_Product_Organisation info = product.getGeneral_Info().getL2A_Product_Info().getL2A_Product_Organisation();

        String dataStripMetadataFilenameCandidate = info.getGranule_List().get(0).getGranules().getDatastripIdentifier();
        S2L2aDatastripDirFilename dirDatastrip = S2L2aDatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);
        return dirDatastrip.getDatastripFilename(null);
    }

    public static S2L2aDatastripDirFilename getDatastripDir(Level2A_User_Product product) {
        A_L2A_Product_Info.L2A_Product_Organisation info = product.getGeneral_Info().getL2A_Product_Info().getL2A_Product_Organisation();
        String dataStripMetadataFilenameCandidate = info.getGranule_List().get(0).getGranules().getDatastripIdentifier();

        S2L2aDatastripDirFilename dirDatastrip = S2L2aDatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);
        return dirDatastrip;
    }

    public static Collection<ImageInfo> getImages(Level2A_User_Product product) {
        A_L2A_Product_Info.L2A_Product_Organisation info = product.getGeneral_Info().getL2A_Product_Info().getL2A_Product_Organisation();

        List<A_L2A_Product_Info.L2A_Product_Organisation.Granule_List> theList = info.getGranule_List();
        List<ImageInfo> aGranuleList = new ArrayList<ImageInfo>();

        for (A_L2A_Product_Info.L2A_Product_Organisation.Granule_List currentList : theList) {
            List<A_PRODUCT_ORGANIZATION_2A.Granules.IMAGE_ID_2A> images = currentList.getGranules().getIMAGE_ID_2A();

            for (int granuleIndex = 0; granuleIndex < images.size(); granuleIndex++) {
                ImageInfo newImage = new ImageInfo(images.get(granuleIndex).getValue());
                newImage.put("DatastripIdentifier", currentList.getGranules().getDatastripIdentifier());
                newImage.put("GranuleIdentifier", currentList.getGranules().getGranuleIdentifier());
                newImage.put("ImageFormat", currentList.getGranules().getImageFormat());
                aGranuleList.add(newImage);
            }
        }

        return aGranuleList;
    }

    public static Map<Integer, L2aMetadata.TileGeometry> getTileGeometries(Level2A_Tile product) {
        String id = product.getGeneral_Info().getTILE_ID_2A().getValue();

        A_GEOMETRIC_INFO_TILE info = product.getGeometric_Info();
        A_GEOMETRIC_INFO_TILE.Tile_Geocoding tgeo = info.getTile_Geocoding();


        List<A_TILE_DESCRIPTION.Geoposition> poss = tgeo.getGeoposition();
        List<A_TILE_DESCRIPTION.Size> sizz = tgeo.getSize();

        Map<Integer, L2aMetadata.TileGeometry> resolutions = new HashMap<Integer, L2aMetadata.TileGeometry>();

        for (A_TILE_DESCRIPTION.Geoposition gpos : poss) {
            int index = gpos.getResolution();
            L2aMetadata.TileGeometry tgeox = new L2aMetadata.TileGeometry();
            tgeox.upperLeftX = gpos.getULX();
            tgeox.upperLeftY = gpos.getULY();
            tgeox.xDim = gpos.getXDIM();
            tgeox.yDim = gpos.getYDIM();
            resolutions.put(index, tgeox);
        }

        for (A_TILE_DESCRIPTION.Size asize : sizz) {
            int index = asize.getResolution();
            L2aMetadata.TileGeometry tgeox = resolutions.get(index);
            tgeox.numCols = asize.getNCOLS();
            tgeox.numRows = asize.getNROWS();
        }

        return resolutions;
    }

    public static L2aMetadata.AnglesGrid getSunGrid(Level2A_Tile product) {
        String id = product.getGeneral_Info().getTILE_ID_2A().getValue();

        A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();
        A_SUN_INCIDENCE_ANGLE_GRID sun = ang.getSun_Angles_Grid();

        int azrows = sun.getAzimuth().getValues_List().getVALUES().size();
        int azcolumns = sun.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

        int zenrows = sun.getZenith().getValues_List().getVALUES().size();
        int zencolumns = sun.getZenith().getValues_List().getVALUES().size();

        L2aMetadata.AnglesGrid ag = new L2aMetadata.AnglesGrid();
        ag.azimuth = new float[azrows][azcolumns];
        ag.zenith = new float[zenrows][zencolumns];

        for (int rowindex = 0; rowindex < azrows; rowindex++) {
            List<Float> azimuths = sun.getAzimuth().getValues_List().getVALUES().get(rowindex).getValue();
            for (int colindex = 0; colindex < azcolumns; colindex++) {
                ag.azimuth[rowindex][colindex] = azimuths.get(colindex);
            }
        }

        for (int rowindex = 0; rowindex < zenrows; rowindex++) {
            List<Float> zeniths = sun.getZenith().getValues_List().getVALUES().get(rowindex).getValue();
            for (int colindex = 0; colindex < zencolumns; colindex++) {
                ag.zenith[rowindex][colindex] = zeniths.get(colindex);
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
            ag2.azimuth = new float[azrows2][azcolumns2];
            ag2.zenith = new float[zenrows2][zencolumns2];

            for (int rowindex = 0; rowindex < azrows2; rowindex++) {
                List<Float> azimuths = angleGrid.getAzimuth().getValues_List().getVALUES().get(rowindex).getValue();
                for (int colindex = 0; colindex < azcolumns2; colindex++) {
                    ag2.azimuth[rowindex][colindex] = azimuths.get(colindex);
                }
            }

            for (int rowindex = 0; rowindex < zenrows2; rowindex++) {
                List<Float> zeniths = angleGrid.getZenith().getValues_List().getVALUES().get(rowindex).getValue();
                for (int colindex = 0; colindex < zencolumns2; colindex++) {
                    ag2.zenith[rowindex][colindex] = zeniths.get(colindex);
                }
            }

            ag2.bandId = Integer.parseInt(angleGrid.getBandId());
            ag2.detectorId = Integer.parseInt(angleGrid.getDetectorId());
            darr[index] = ag2;
        }

        return darr;
    }
}
