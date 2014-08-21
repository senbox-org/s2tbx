package org.esa.beam.dataio.s2;

import _int.esa.gs2.dico._1_0.pdgs.dimap.*;
import _int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata.Level1CTile;
import _int.esa.s2.pdgs.psd.user_product_level_1c.Level1CUserProduct;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;
import org.esa.beam.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.beam.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.beam.dataio.s2.filepatterns.S2GranuleDirFilename;

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
            e.printStackTrace();
        }
    }

    public static String getExecutable()
    {
        String winPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-win32-x86/bin/opj_decompress.exe";
        String linuxPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-i386/bin/opj_decompress";
        String linux64Path = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Linux-x64/bin/opj_decompress";
        String macPath = "lib-openjpeg-2.1.0/openjpeg-2.1.0-Darwin-i386/bin/opj_decompress";

        String target = "opj_decompress";

        //todo log stracktraces
        if(SystemUtils.IS_OS_LINUX)
        {
            try {
		Process p = Runtime.getRuntime().exec("uname -m");
                p.waitFor();
                String output = convertStreamToString(p.getInputStream());
                String errorOutput = convertStreamToString(p.getErrorStream());

                System.err.println(output);

                if(output.startsWith("i686"))
                {
                    target = getModulesDir() + linuxPath;
                }
                else
                {
                    target = getModulesDir() + linux64Path;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(SystemUtils.IS_OS_MAC)
        {
            try {
                target = getModulesDir() + macPath;
                setExecutable(new File(target), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                target = getModulesDir() + winPath;
            } catch (Exception e) {
                e.printStackTrace();
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
        JAXBContext jaxbContext = JAXBContext.newInstance("_int.esa.s2.pdgs.psd.user_product_level_1c:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_metadata:_int.esa.gs2.dico._1_0.pdgs.dimap", s2c);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();

        Object ob =  unmarshaller.unmarshal(stream);
        Object casted = ((JAXBElement)ob).getValue();

        return casted;
    }

    public static L1cMetadata.ProductCharacteristics parseCharacteristics(Level1CUserProduct product)
    {
        ADATATAKEIDENTIFICATION info = product.getGeneralInfo().getProductInfo().getDatatake();

        L1cMetadata.ProductCharacteristics characteristics = new L1cMetadata.ProductCharacteristics();
        characteristics.spacecraft = info.getSPACECRAFTNAME().getValue();
        characteristics.datasetProductionDate = product.getGeneralInfo().getProductInfo().getGENERATIONTIME().toString();
        characteristics.processingLevel = product.getGeneralInfo().getProductInfo().getPROCESSINGLEVEL().getValue().toString();

        List<L1cMetadata.SpectralInformation> targetList = new ArrayList<L1cMetadata.SpectralInformation>();

        List<APRODUCTINFOUSERL1C.ProductImageCharacteristics.SpectralInformationList.SpectralInformation> aList = product.getGeneralInfo().getProductImageCharacteristics().getSpectralInformationList().getSpectralInformation();
        for(APRODUCTINFOUSERL1C.ProductImageCharacteristics.SpectralInformationList.SpectralInformation si : aList) {
            L1cMetadata.SpectralInformation newInfo = new L1cMetadata.SpectralInformation();
            newInfo.bandId = Integer.parseInt(si.getBandId());
            newInfo.physicalBand = si.getPhysicalBand().value();
            newInfo.resolution = si.getRESOLUTION();
            newInfo.spectralResponseStep = si.getSpectralResponse().getSTEP().getValue();
            newInfo.wavelenghtCentral = si.getWavelength().getCENTRAL().getValue();
            newInfo.wavelenghtMax = si.getWavelength().getMAX().getValue();
            newInfo.wavelenghtMin = si.getWavelength().getMIN().getValue();

            int size = si.getSpectralResponse().getVALUES().size();
            newInfo.spectralResponseValues = ArrayUtils.toPrimitive(si.getSpectralResponse().getVALUES().toArray(new Double[size]));
            targetList.add(newInfo);
        }

        int size = targetList.size();
        characteristics.bandInformations = targetList.toArray(new L1cMetadata.SpectralInformation[size]);

        return characteristics;
    }

    public static L1cMetadata.ProductCharacteristics getProductOrganization(Level1CUserProduct product)
    {
        APRODUCTINFO.ProductOrganisation info = product.getGeneralInfo().getProductInfo().getProductOrganisation();

        L1cMetadata.ProductCharacteristics characteristics= new L1cMetadata.ProductCharacteristics();
        characteristics.spacecraft = product.getGeneralInfo().getProductInfo().getDatatake().getSPACECRAFTNAME().getValue();
        characteristics.datasetProductionDate = product.getGeneralInfo().getProductInfo().getDatatake().getDATATAKESENSINGSTART().toString();
        characteristics.processingLevel = product.getGeneralInfo().getProductInfo().getPROCESSINGLEVEL().getValue().value();

        List<APRODUCTINFOUSERL1C.ProductImageCharacteristics.SpectralInformationList.SpectralInformation> spectralInfoList = product.getGeneralInfo().getProductImageCharacteristics().getSpectralInformationList().getSpectralInformation();

        List<L1cMetadata.SpectralInformation> aInfo = new ArrayList<L1cMetadata.SpectralInformation>();

        for(APRODUCTINFOUSERL1C.ProductImageCharacteristics.SpectralInformationList.SpectralInformation sin : spectralInfoList)
        {
            L1cMetadata.SpectralInformation data = new L1cMetadata.SpectralInformation();
            data.bandId = Integer.parseInt(sin.getBandId());
            data.physicalBand = sin.getPhysicalBand().value();
            data.resolution = sin.getRESOLUTION();
            data.spectralResponseStep = sin.getSpectralResponse().getSTEP().getValue();

            int size = sin.getSpectralResponse().getVALUES().size();
            data.spectralResponseValues = ArrayUtils.toPrimitive(sin.getSpectralResponse().getVALUES().toArray(new Double[size]));
            data.wavelenghtCentral = sin.getWavelength().getCENTRAL().getValue();
            data.wavelenghtMax = sin.getWavelength().getMAX().getValue();
            data.wavelenghtMin = sin.getWavelength().getMIN().getValue();

            aInfo.add(data);
        }

        int size = aInfo.size();
        characteristics.bandInformations = aInfo.toArray(new L1cMetadata.SpectralInformation[size]);

        return characteristics;
    }

    public static Collection<String> getTiles(Level1CUserProduct product) {
        APRODUCTINFO.ProductOrganisation info = product.getGeneralInfo().getProductInfo().getProductOrganisation();

        List<APRODUCTINFO.ProductOrganisation.GranuleList> aGranuleList = info.getGranuleList();

        Transformer tileSelector = new Transformer() {
            @Override
            public Object transform(Object o) {
                APRODUCTINFO.ProductOrganisation.GranuleList ali = (APRODUCTINFO.ProductOrganisation.GranuleList) o;
                APRODUCTORGANIZATION.Granules gr = ali.getGranules();
                return gr.getGranuleIdentifier();
            }
        };

        Collection col = CollectionUtils.collect(aGranuleList, tileSelector);
        return col;
    }

    public static S2DatastripFilename getDatastrip(Level1CUserProduct product)
    {
        APRODUCTINFO.ProductOrganisation info = product.getGeneralInfo().getProductInfo().getProductOrganisation();
        List<APRODUCTINFO.ProductOrganisation.GranuleList> aGranuleList = info.getGranuleList();
        String granule = aGranuleList.get(0).getGranules().getGranuleIdentifier();
        S2GranuleDirFilename grafile = S2GranuleDirFilename.create(granule);
        String fileCategory = grafile.fileCategory;

        String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
        S2DatastripDirFilename dirDatastrip = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);
        return dirDatastrip.getDatastripFilename(null);
    }

    public static S2DatastripDirFilename getDatastripDir(Level1CUserProduct product)
    {
        APRODUCTINFO.ProductOrganisation info = product.getGeneralInfo().getProductInfo().getProductOrganisation();
        List<APRODUCTINFO.ProductOrganisation.GranuleList> aGranuleList = info.getGranuleList();
        String granule = aGranuleList.get(0).getGranules().getGranuleIdentifier();
        S2GranuleDirFilename grafile = S2GranuleDirFilename.create(granule);
        String fileCategory = grafile.fileCategory;

        String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
        S2DatastripDirFilename dirDatastrip = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, fileCategory);
        return dirDatastrip;
    }

    public static Collection<String> getImages(Level1CUserProduct product) {
        APRODUCTINFO.ProductOrganisation info = product.getGeneralInfo().getProductInfo().getProductOrganisation();

        List<APRODUCTINFO.ProductOrganisation.GranuleList> beautyQueen = info.getGranuleList();
        List<String> imagesList = new ArrayList<String>();

        for(APRODUCTINFO.ProductOrganisation.GranuleList aGranule: beautyQueen)
        {
            APRODUCTORGANIZATION.Granules gr = aGranule.getGranules();
            String dir_id = gr.getGranuleIdentifier();
            List<APRODUCTORGANIZATION.Granules.IMAGEID> imageid = gr.getIMAGEID();
            for(APRODUCTORGANIZATION.Granules.IMAGEID aImageName : imageid)
            {
                imagesList.add(dir_id + File.separator + aImageName.getValue() + ".jp2");
            }
        }

        Collections.sort(imagesList);
        return imagesList;
    }

    public static Map<Integer, L1cMetadata.TileGeometry> getTileGeometries(Level1CTile product) {
        String id = product.getGeneralInfo().getTILEID().getValue();

        AGEOMETRICINFOTILE info = product.getGeometricInfo();
        AGEOMETRICINFOTILE.TileGeocoding tgeo = info.getTileGeocoding();


        List<ATILEDESCRIPTION.Geoposition> poss = tgeo.getGeoposition();
        List<ATILEDESCRIPTION.Size> sizz = tgeo.getSize();

        Map<Integer, L1cMetadata.TileGeometry> resolutions = new HashMap<Integer, L1cMetadata.TileGeometry>();

        for (ATILEDESCRIPTION.Geoposition gpos : poss)
        {
            int index = gpos.getResolution();
            L1cMetadata.TileGeometry tgeox = new L1cMetadata.TileGeometry();
            tgeox.upperLeftX = gpos.getULX();
            tgeox.upperLeftY = gpos.getULY();
            tgeox.xDim = gpos.getXDIM();
            tgeox.yDim = gpos.getYDIM();
            resolutions.put(index, tgeox);
        }

        for(ATILEDESCRIPTION.Size asize : sizz)
        {
            int index = asize.getResolution();
            L1cMetadata.TileGeometry tgeox = resolutions.get(index);
            tgeox.numCols = asize.getNCOLS();
            tgeox.numRows = asize.getNROWS();
        }

        return resolutions;
    }

    public static L1cMetadata.AnglesGrid getSunGrid(Level1CTile product) {
        String id = product.getGeneralInfo().getTILEID().getValue();

        AGEOMETRICINFOTILE.TileAngles ang = product.getGeometricInfo().getTileAngles();
        ASUNINCIDENCEANGLEGRID sun = ang.getSunAnglesGrid();

        int azrows = sun.getAzimuth().getValuesList().getVALUES().size();
        int azcolumns = sun.getAzimuth().getValuesList().getVALUES().get(0).getValue().size();

        int zenrows = sun.getZenith().getValuesList().getVALUES().size();
        int zencolumns = sun.getZenith().getValuesList().getVALUES().size();

        L1cMetadata.AnglesGrid ag = new L1cMetadata.AnglesGrid();
        ag.azimuth = new float[azrows][azcolumns];
        ag.zenith = new float[zenrows][zencolumns];

        for(int rowindex = 0; rowindex < azrows; rowindex++)
        {
            List<Float> azimuths = sun.getAzimuth().getValuesList().getVALUES().get(rowindex).getValue();
            for(int colindex = 0; colindex < azcolumns; colindex++)
            {
                ag.azimuth[rowindex][colindex] = azimuths.get(colindex);
            }
        }

        for(int rowindex = 0; rowindex < zenrows; rowindex++)
        {
            List<Float> zeniths = sun.getZenith().getValuesList().getVALUES().get(rowindex).getValue();
            for(int colindex = 0; colindex < zencolumns; colindex++)
            {
                ag.zenith[rowindex][colindex] = zeniths.get(colindex);
            }
        }

        return ag;
    }

    public static L1cMetadata.AnglesGrid[] getAnglesGrid(Level1CTile product) {
        AGEOMETRICINFOTILE.TileAngles ang = product.getGeometricInfo().getTileAngles();
        List<ANINCIDENCEANGLEGRID> incilist = ang.getViewingIncidenceAnglesGrids();

        L1cMetadata.AnglesGrid[] darr = new L1cMetadata.AnglesGrid[incilist.size()];
        for(int index = 0; index < incilist.size() ; index++)
        {
            ANINCIDENCEANGLEGRID angleGrid = incilist.get(index);

            int azrows2 = angleGrid.getAzimuth().getValuesList().getVALUES().size();
            int azcolumns2 = angleGrid.getAzimuth().getValuesList().getVALUES().get(0).getValue().size();

            int zenrows2 = angleGrid.getZenith().getValuesList().getVALUES().size();
            int zencolumns2 = angleGrid.getZenith().getValuesList().getVALUES().size();


            L1cMetadata.AnglesGrid ag2 = new L1cMetadata.AnglesGrid();
            ag2.azimuth = new float[azrows2][azcolumns2];
            ag2.zenith = new float[zenrows2][zencolumns2];

            for(int rowindex = 0; rowindex < azrows2; rowindex++)
            {
                List<Float> azimuths = angleGrid.getAzimuth().getValuesList().getVALUES().get(rowindex).getValue();
                for(int colindex = 0; colindex < azcolumns2; colindex++)
                {
                    ag2.azimuth[rowindex][colindex] = azimuths.get(colindex);
                }
            }

            for(int rowindex = 0; rowindex < zenrows2; rowindex++)
            {
                List<Float> zeniths = angleGrid.getZenith().getValuesList().getVALUES().get(rowindex).getValue();
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
