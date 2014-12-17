package org.esa.beam.dataio.s2;


import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.*;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_tile_metadata.Level1C_Tile;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1c.Level1C_User_Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;
import org.esa.beam.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.beam.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.beam.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.esa.beam.util.logging.BeamLogManager;

import javax.xml.bind.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class L1cMetadataProc {

    public static String getModulesDir() throws URISyntaxException, FileNotFoundException {
        String subStr = "s2tbx-reader";

        ClassLoader s2c = Sentinel2ProductReader.class.getClassLoader();
        URLClassLoader s2ClassLoader = (URLClassLoader) s2c;

        URL[] theURLs = s2ClassLoader.getURLs();
        for (URL url : theURLs)
        {
            if(url.getPath().contains(subStr) && url.getPath().contains(".jar"))
            {
                URI uri = url.toURI();
                URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
                return parent.getPath();
            }
            else
            {
                //todo please note that in dev, all the module jar files are unzipped in modules folder, so SNAP only reaches this code in dev environments
                if(url.getPath().contains(subStr))
                {
                    URI uri = url.toURI();
                    URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
                    return parent.getPath();
                }
            }
        }

        throw new FileNotFoundException("Module " + subStr + " not found !");
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void setExecutable(File file, boolean executable)
    {
        try
        {
            Process p = Runtime.getRuntime().exec(new String[] {
            "chmod",
            "u"+(executable?'+':'-')+"x",
            file.getAbsolutePath(),
            });
            p.waitFor();
            String output = convertStreamToString(p.getInputStream());
            String errorOutput = convertStreamToString(p.getErrorStream());
        }
        catch(Exception e)
        {
            BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
        }
    }

    public static String getExecutable()
    {
        String winPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-win32-x86/bin/opj_decompress.exe";
        String linuxPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-i386/bin/opj_decompress";
        String linux64Path = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-x64/bin/opj_decompress";
        String macPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Darwin-i386/bin/opj_decompress";

        String target = "opj_decompress";

        if(SystemUtils.IS_OS_LINUX)
        {
            try {
		        Process p = Runtime.getRuntime().exec("uname -m");
                p.waitFor();
                String output = convertStreamToString(p.getInputStream());
                String errorOutput = convertStreamToString(p.getErrorStream());

                BeamLogManager.getSystemLogger().fine(output);
                BeamLogManager.getSystemLogger().severe(errorOutput);

                if(output.startsWith("i686"))
                {
                    target = getModulesDir() + linuxPath;
                }
                else
                {
                    target = getModulesDir() + linux64Path;
                }
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
            }
        }
        else if(SystemUtils.IS_OS_MAC)
        {
            try {
                target = getModulesDir() + macPath;
                setExecutable(new File(target), true);
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
            }
        }
        else
        {
            try {
                target = getModulesDir() + winPath;
            } catch (Exception e) {
                BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
                target = target + ".exe";
            }
        }

        File fileTarget = new File(target);
        if(fileTarget.exists())
        {
            fileTarget.setExecutable(true);
        }

        return target;
    }

    public static Object readJaxbFromFilename(InputStream stream) throws JAXBException, FileNotFoundException {

        ClassLoader s2c = Sentinel2ProductReader.class.getClassLoader();

        //todo get modules classpath
        //todo test new lecture style
        JAXBContext jaxbContext = JAXBContext.newInstance(MetadataType.L1C + MetadataType.SEPARATOR + MetadataType.L1B + MetadataType.SEPARATOR + MetadataType.L1A, s2c);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();

        Object ob =  unmarshaller.unmarshal(stream);
        Object casted = ((JAXBElement)ob).getValue();

        return casted;
    }

    public static L1cMetadata.ProductCharacteristics parseCharacteristics(Level1C_User_Product product)
    {
        A_DATATAKE_IDENTIFICATION info = product.getGeneral_Info().getProduct_Info().getDatatake();

        L1cMetadata.ProductCharacteristics characteristics = new L1cMetadata.ProductCharacteristics();
        characteristics.spacecraft = info.getSPACECRAFT_NAME();
        characteristics.datasetProductionDate = product.getGeneral_Info().getProduct_Info().getGENERATION_TIME().toString();
        characteristics.processingLevel = product.getGeneral_Info().getProduct_Info().getPROCESSING_LEVEL().getValue().toString();

        List<L1cMetadata.SpectralInformation> targetList = new ArrayList<L1cMetadata.SpectralInformation>();

        List<A_PRODUCT_INFO_USERL1C.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information> aList = product.getGeneral_Info().getProduct_Image_Characteristics().getSpectral_Information_List().getSpectral_Information();
        for(A_PRODUCT_INFO_USERL1C.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information si : aList) {
            L1cMetadata.SpectralInformation newInfo = new L1cMetadata.SpectralInformation();
            newInfo.bandId = Integer.parseInt(si.getBandId());
            newInfo.physicalBand = si.getPhysicalBand().value();
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
        characteristics.bandInformations = targetList.toArray(new L1cMetadata.SpectralInformation[size]);

        return characteristics;
    }

    public static L1cMetadata.ProductCharacteristics getProductOrganization(Level1C_User_Product product)
    {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();

        L1cMetadata.ProductCharacteristics characteristics= new L1cMetadata.ProductCharacteristics();
        characteristics.spacecraft = product.getGeneral_Info().getProduct_Info().getDatatake().getSPACECRAFT_NAME();
        characteristics.datasetProductionDate = product.getGeneral_Info().getProduct_Info().getDatatake().getDATATAKE_SENSING_START().toString();
        characteristics.processingLevel = product.getGeneral_Info().getProduct_Info().getPROCESSING_LEVEL().getValue().value();

        List<A_PRODUCT_INFO_USERL1C.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information> spectralInfoList = product.getGeneral_Info().getProduct_Image_Characteristics().getSpectral_Information_List().getSpectral_Information();

        List<L1cMetadata.SpectralInformation> aInfo = new ArrayList<L1cMetadata.SpectralInformation>();

        for(A_PRODUCT_INFO_USERL1C.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information sin : spectralInfoList)
        {
            L1cMetadata.SpectralInformation data = new L1cMetadata.SpectralInformation();
            data.bandId = Integer.parseInt(sin.getBandId());
            data.physicalBand = sin.getPhysicalBand().value();
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
        characteristics.bandInformations = aInfo.toArray(new L1cMetadata.SpectralInformation[size]);

        return characteristics;
    }

    public static Collection<String> getTiles(Level1C_User_Product product) {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();

        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();

        Transformer tileSelector = new Transformer() {
            @Override
            public Object transform(Object o) {
                A_PRODUCT_INFO.Product_Organisation.Granule_List ali = (A_PRODUCT_INFO.Product_Organisation.Granule_List) o;
                A_PRODUCT_ORGANIZATION.Granules gr = ali.getGranules();
                return gr.getGranuleIdentifier();
            }
        };

        Collection col = CollectionUtils.collect(aGranuleList, tileSelector);
        return col;
    }

    public static S2DatastripFilename getDatastrip(Level1C_User_Product product)
    {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();
        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();
        String granule = aGranuleList.get(0).getGranules().getGranuleIdentifier();
        S2GranuleDirFilename grafile = S2GranuleDirFilename.create(granule);
        String fileCategory = grafile.fileCategory;

        String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
        S2DatastripDirFilename dirDatastrip = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);
        return dirDatastrip.getDatastripFilename(null);
    }

    public static S2DatastripDirFilename getDatastripDir(Level1C_User_Product product)
    {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();
        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();
        String granule = aGranuleList.get(0).getGranules().getGranuleIdentifier();
        S2GranuleDirFilename grafile = S2GranuleDirFilename.create(granule);
        String fileCategory = grafile.fileCategory;

        String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
        S2DatastripDirFilename dirDatastrip = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, fileCategory);
        return dirDatastrip;
    }

    public static Collection<String> getImages(Level1C_User_Product product) {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();

        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> granulesList = info.getGranule_List();
        List<String> imagesList = new ArrayList<String>();

        for(A_PRODUCT_INFO.Product_Organisation.Granule_List aGranule: granulesList)
        {
            A_PRODUCT_ORGANIZATION.Granules gr = aGranule.getGranules();
            String dir_id = gr.getGranuleIdentifier();
            List<A_PRODUCT_ORGANIZATION.Granules.IMAGE_ID> imageid = gr.getIMAGE_ID();
            for(A_PRODUCT_ORGANIZATION.Granules.IMAGE_ID aImageName : imageid)
            {
                imagesList.add(dir_id + File.separator + aImageName.getValue() + ".jp2");
            }
        }

        Collections.sort(imagesList);
        return imagesList;
    }

    public static Map<Integer, L1cMetadata.TileGeometry> getTileGeometries(Level1C_Tile product) {
        String id = product.getGeneral_Info().getTILE_ID().getValue();

        A_GEOMETRIC_INFO_TILE info = product.getGeometric_Info();
        A_GEOMETRIC_INFO_TILE.Tile_Geocoding tgeo = info.getTile_Geocoding();


        List<A_TILE_DESCRIPTION.Geoposition> poss = tgeo.getGeoposition();
        List<A_TILE_DESCRIPTION.Size> sizz = tgeo.getSize();

        Map<Integer, L1cMetadata.TileGeometry> resolutions = new HashMap<Integer, L1cMetadata.TileGeometry>();

        for (A_TILE_DESCRIPTION.Geoposition gpos : poss)
        {
            int index = gpos.getResolution();
            L1cMetadata.TileGeometry tgeox = new L1cMetadata.TileGeometry();
            tgeox.upperLeftX = gpos.getULX();
            tgeox.upperLeftY = gpos.getULY();
            tgeox.xDim = gpos.getXDIM();
            tgeox.yDim = gpos.getYDIM();
            resolutions.put(index, tgeox);
        }

        for(A_TILE_DESCRIPTION.Size asize : sizz)
        {
            int index = asize.getResolution();
            L1cMetadata.TileGeometry tgeox = resolutions.get(index);
            tgeox.numCols = asize.getNCOLS();
            tgeox.numRows = asize.getNROWS();
        }

        return resolutions;
    }

    public static L1cMetadata.AnglesGrid getSunGrid(Level1C_Tile product) {
        String id = product.getGeneral_Info().getTILE_ID().getValue();

        A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();
        A_SUN_INCIDENCE_ANGLE_GRID sun = ang.getSun_Angles_Grid();

        int azrows = sun.getAzimuth().getValues_List().getVALUES().size();
        int azcolumns = sun.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

        int zenrows = sun.getZenith().getValues_List().getVALUES().size();
        int zencolumns = sun.getZenith().getValues_List().getVALUES().size();

        L1cMetadata.AnglesGrid ag = new L1cMetadata.AnglesGrid();
        ag.azimuth = new float[azrows][azcolumns];
        ag.zenith = new float[zenrows][zencolumns];

        for(int rowindex = 0; rowindex < azrows; rowindex++)
        {
            List<Float> azimuths = sun.getAzimuth().getValues_List().getVALUES().get(rowindex).getValue();
            for(int colindex = 0; colindex < azcolumns; colindex++)
            {
                ag.azimuth[rowindex][colindex] = azimuths.get(colindex);
            }
        }

        for(int rowindex = 0; rowindex < zenrows; rowindex++)
        {
            List<Float> zeniths = sun.getZenith().getValues_List().getVALUES().get(rowindex).getValue();
            for(int colindex = 0; colindex < zencolumns; colindex++)
            {
                ag.zenith[rowindex][colindex] = zeniths.get(colindex);
            }
        }

        return ag;
    }

    public static L1cMetadata.AnglesGrid[] getAnglesGrid(Level1C_Tile product) {
        A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();
        List<AN_INCIDENCE_ANGLE_GRID> incilist = ang.getViewing_Incidence_Angles_Grids();

        L1cMetadata.AnglesGrid[] darr = new L1cMetadata.AnglesGrid[incilist.size()];
        for(int index = 0; index < incilist.size() ; index++)
        {
            AN_INCIDENCE_ANGLE_GRID angleGrid = incilist.get(index);

            int azrows2 = angleGrid.getAzimuth().getValues_List().getVALUES().size();
            int azcolumns2 = angleGrid.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

            int zenrows2 = angleGrid.getZenith().getValues_List().getVALUES().size();
            int zencolumns2 = angleGrid.getZenith().getValues_List().getVALUES().size();


            L1cMetadata.AnglesGrid ag2 = new L1cMetadata.AnglesGrid();
            ag2.azimuth = new float[azrows2][azcolumns2];
            ag2.zenith = new float[zenrows2][zencolumns2];

            for(int rowindex = 0; rowindex < azrows2; rowindex++)
            {
                List<Float> azimuths = angleGrid.getAzimuth().getValues_List().getVALUES().get(rowindex).getValue();
                for(int colindex = 0; colindex < azcolumns2; colindex++)
                {
                    ag2.azimuth[rowindex][colindex] = azimuths.get(colindex);
                }
            }

            for(int rowindex = 0; rowindex < zenrows2; rowindex++)
            {
                List<Float> zeniths = angleGrid.getZenith().getValues_List().getVALUES().get(rowindex).getValue();
                for(int colindex = 0; colindex < zencolumns2; colindex++)
                {
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
