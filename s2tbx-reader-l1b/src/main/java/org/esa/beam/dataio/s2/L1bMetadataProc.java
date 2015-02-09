package org.esa.beam.dataio.s2;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.*;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.sy.image.A_PHYSICAL_BAND_NAME;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata.Level1B_Granule;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1b.Level1B_User_Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.esa.beam.dataio.s2.filepatterns.S2L1bDatastripDirFilename;
import org.esa.beam.dataio.s2.filepatterns.S2L1bDatastripFilename;
import org.esa.beam.dataio.s2.filepatterns.S2L1bGranuleDirFilename;
import org.esa.beam.util.Guardian;
import org.esa.beam.util.logging.BeamLogManager;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
import java.util.*;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class L1bMetadataProc {

    public static String getModulesDir() throws URISyntaxException, FileNotFoundException {
        String subStr = "s2tbx-l1b-reader";

        ClassLoader s2c = Sentinel2L1BProductReader.class.getClassLoader();
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

        ClassLoader s2c = Sentinel2L1BProductReader.class.getClassLoader();
        JAXBContext jaxbContext = JAXBContext.newInstance(MetadataType.L1B, s2c);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        Object ob =  unmarshaller.unmarshal(stream);
        Object casted = ((JAXBElement)ob).getValue();

        return casted;
    }

    public static JAXBContext getJaxbContext() throws JAXBException, FileNotFoundException {

        ClassLoader s2c = Sentinel2L1BProductReader.class.getClassLoader();
        JAXBContext jaxbContext = JAXBContext.newInstance(MetadataType.L1B, s2c);
        return jaxbContext;
    }

    public static L1bMetadata.ProductCharacteristics parseCharacteristics(Level1B_User_Product product)
    {
        A_DATATAKE_IDENTIFICATION info = product.getGeneral_Info().getProduct_Info().getDatatake();

        L1bMetadata.ProductCharacteristics characteristics = new L1bMetadata.ProductCharacteristics();
        characteristics.spacecraft = info.getSPACECRAFT_NAME();
        characteristics.datasetProductionDate = product.getGeneral_Info().getProduct_Info().getGENERATION_TIME().toString();
        characteristics.processingLevel = product.getGeneral_Info().getProduct_Info().getPROCESSING_LEVEL().getValue().toString();

        List<L1bMetadata.SpectralInformation> targetList = new ArrayList<L1bMetadata.SpectralInformation>();

        List<A_PRODUCT_INFO_USERL1B.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information> aList = product.getGeneral_Info().getProduct_Image_Characteristics().getSpectral_Information_List().getSpectral_Information();
        for(A_PRODUCT_INFO_USERL1B.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information si : aList) {
            L1bMetadata.SpectralInformation newInfo = new L1bMetadata.SpectralInformation();
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
        characteristics.bandInformations = targetList.toArray(new L1bMetadata.SpectralInformation[size]);

        return characteristics;
    }

    public static String getCrs(Level1B_User_Product product)
    {
        return product.getGeometric_Info().getCoordinate_Reference_System().getHorizontal_CS().getHORIZONTAL_CS_CODE();
    }

    public static L1bMetadata.ProductCharacteristics getProductOrganization(Level1B_User_Product product)
    {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();

        L1bMetadata.ProductCharacteristics characteristics= new L1bMetadata.ProductCharacteristics();
        characteristics.spacecraft = product.getGeneral_Info().getProduct_Info().getDatatake().getSPACECRAFT_NAME();
        characteristics.datasetProductionDate = product.getGeneral_Info().getProduct_Info().getDatatake().getDATATAKE_SENSING_START().toString();
        characteristics.processingLevel = product.getGeneral_Info().getProduct_Info().getPROCESSING_LEVEL().getValue().value();

        Object spectral_list = product.getGeneral_Info().getProduct_Image_Characteristics().getSpectral_Information_List();
        if(spectral_list != null) {

            List<A_PRODUCT_INFO_USERL1B.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information> spectralInfoList = product.getGeneral_Info().getProduct_Image_Characteristics().getSpectral_Information_List().getSpectral_Information();

            List<L1bMetadata.SpectralInformation> aInfo = new ArrayList<L1bMetadata.SpectralInformation>();

            // todo OPP Spectral_Information_List is optional on L1B ??
            for (A_PRODUCT_INFO_USERL1B.Product_Image_Characteristics.Spectral_Information_List.Spectral_Information sin : spectralInfoList) {
                L1bMetadata.SpectralInformation data = new L1bMetadata.SpectralInformation();
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
            characteristics.bandInformations = aInfo.toArray(new L1bMetadata.SpectralInformation[size]);
        }
        else
        {
            BeamLogManager.getSystemLogger().warning("Empty spectral info !");

            // todo OPP If there is no spectral info, get band names from Query_Options/Band_List
            List<A_PHYSICAL_BAND_NAME> bandList = product.getGeneral_Info().getProduct_Info().getQuery_Options().getBand_List().getBAND_NAME();
            // assume 0 based index for bands just retrieved...

            List<L1bMetadata.SpectralInformation> aInfo = new ArrayList<L1bMetadata.SpectralInformation>();
            aInfo.sort(new Comparator<L1bMetadata.SpectralInformation>() {
                @Override
                public int compare(L1bMetadata.SpectralInformation o1, L1bMetadata.SpectralInformation o2) {
                    return o1.physicalBand.compareTo(o2.physicalBand);
                }
            });

            int index = 0;
            for(A_PHYSICAL_BAND_NAME band_name : bandList)
            {
                L1bMetadata.SpectralInformation data = new L1bMetadata.SpectralInformation();
                data.physicalBand = band_name.value();
                data.bandId = index;

                // todo OPP remove hardcoded resolutions...
                data.resolution = 10;
                if(data.physicalBand.equals("B1") || data.physicalBand.equals("B9") || data.physicalBand.equals("B10"))
                {
                    data.resolution = 60;
                }
                else if(data.physicalBand.equals("B5") || data.physicalBand.equals("B6") || data.physicalBand.equals("B7") || data.physicalBand.equals("B8A") || data.physicalBand.equals("B11") || data.physicalBand.equals("B12"))
                {
                    data.resolution = 20;
                }

                index = index + 1;

                aInfo.add(data);
            }

            int size = aInfo.size();
            characteristics.bandInformations = aInfo.toArray(new L1bMetadata.SpectralInformation[size]);
        }

        return characteristics;
    }

    public static Collection<String> getTiles(Level1B_User_Product product) {
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

    public static S2L1bDatastripFilename getDatastrip(Level1B_User_Product product)
    {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();
        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();
        String granule = aGranuleList.get(0).getGranules().getGranuleIdentifier();
        S2L1bGranuleDirFilename grafile = S2L1bGranuleDirFilename.create(granule);
        Guardian.assertNotNull("Product files don't match regular expressions", grafile);

        String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
        S2L1bDatastripDirFilename dirDatastrip = S2L1bDatastripDirFilename.create(dataStripMetadataFilenameCandidate, null);
        return dirDatastrip.getDatastripFilename(null);
    }

    public static S2L1bDatastripDirFilename getDatastripDir(Level1B_User_Product product)
    {
        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();
        List<A_PRODUCT_INFO.Product_Organisation.Granule_List> aGranuleList = info.getGranule_List();
        String granule = aGranuleList.get(0).getGranules().getGranuleIdentifier();
        S2L1bGranuleDirFilename grafile = S2L1bGranuleDirFilename.create(granule);
        Guardian.assertNotNull("Product files don't match regular expressions", grafile);

        String fileCategory = grafile.fileCategory;

        String dataStripMetadataFilenameCandidate = aGranuleList.get(0).getGranules().getDatastripIdentifier();
        S2L1bDatastripDirFilename dirDatastrip = S2L1bDatastripDirFilename.create(dataStripMetadataFilenameCandidate, fileCategory);
        return dirDatastrip;
    }

    public static Collection<String> getImages(Level1B_User_Product product) {
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

    /**
     * todo move this to a utility class
     * @param in
     * @param size
     * @return
     */
    public static List<double[]> arraySplitter(List<Double> in, int size)
    {
        Guardian.assertTrue("Multiple size" ,(in.size() % size) == 0);

        List<double[]> result = new ArrayList<double[]>();
        for(int i = 0; i < in.size() / size; i++)
        {
            double[] item = new double[]{in.get(i * size), in.get(i * size + 1), in.get(i * size + 2)};
            result.add(item);
        }

        return result;
    }

    public static List<Coordinate> as2DCoordinates(List<Double> in)
    {
        List<double[]> tr = arraySplitter(in, 2);

        List<Coordinate> result = new ArrayList<Coordinate>();
        for(int i = 0; i < tr.size(); i++)
        {
            Coordinate c = new Coordinate(tr.get(i)[0], tr.get(i)[1]);
            result.add(c);
        }

        return result;
    }

    public static List<Coordinate> as3DCoordinates(List<Double> in)
    {
        List<double[]> tr = arraySplitter(in, 3);

        List<Coordinate> result = new ArrayList<Coordinate>();
        for(int i = 0; i < tr.size(); i++)
        {
            Coordinate c = new Coordinate(tr.get(i)[0], tr.get(i)[1], tr.get(i)[2]);
            result.add(c);
        }

        return result;
    }

    // todo OPP Move this function to a utility class
    public static double distanceToSegment(Vector3D v, Vector3D w, Vector3D  p)
    {
        // Return minimum distance between line segment vw and point p
        final double l2 = Vector3D.distanceSq(v, w);  // i.e. |w-v|^2 -  avoid a sqrt
        if (l2 == 0.0) return Vector3D.distance(p, v);   // v == w case
        // Consider the line extending the segment, parameterized as v + t (w - v).
        // We find projection of point p onto the line.
        // It falls where t = [(p-v) . (w-v)] / |w-v|^2
        double t = Vector3D.dotProduct(p.subtract(v), w.subtract(v)) / l2;
        if (t < 0.0) return Vector3D.distance(p, v);       // Beyond the 'v' end of the segment
        else if (t > 1.0) return Vector3D.distance(p, w);  // Beyond the 'w' end of the segment
        Vector3D projection = v.add(w.subtract(v).scalarMultiply(t));  // Projection falls on the segment
        return Vector3D.distance(p, projection);
    }

    public static Map<Integer, L1bMetadata.TileGeometry> getGranuleGeometries(Level1B_Granule granule) {
        List<Double> polygon = granule.getGeometric_Info().getGranule_Footprint().getGranule_Footprint().getFootprint().getEXT_POS_LIST();
        List<Coordinate> thePoints = as3DCoordinates(polygon);

        Coordinate[] arr = thePoints.toArray(new Coordinate[thePoints.size()]);

        Coordinate corner = arr[0];
        Coordinate llcorner = arr[1];

        Map<Integer, L1bMetadata.TileGeometry> resolutions = new HashMap<Integer, L1bMetadata.TileGeometry>();

        List<A_GRANULE_DIMENSIONS.Size> sizes = granule.getGeometric_Info().getGranule_Dimensions().getSize();
        int pos = granule.getGeometric_Info().getGranule_Position().getPOSITION();
        String detector = granule.getGeneral_Info().getDETECTOR_ID().getValue();

        for (A_GRANULE_DIMENSIONS.Size gpos : sizes)
        {
            int resolution = gpos.getResolution();

            // todo OPP retrieve tile layout per granule..
            // todo OPP remove hardcoded resolution

            int ratio = resolution / 10;
            L1bMetadata.TileGeometry tgeox = new L1bMetadata.TileGeometry();
            tgeox.numCols = gpos.getNCOLS();

            tgeox.numRows = Math.min(gpos.getNROWS() - (pos / ratio), S2L1bConfig.L1B_TILE_LAYOUTS[S2L1bConfig.LAYOUTMAP.get(resolution)].height);
            int first = gpos.getNROWS() - (pos / ratio);
            int second = S2L1bConfig.L1B_TILE_LAYOUTS[S2L1bConfig.LAYOUTMAP.get(resolution)].height;

            if (first < second)
            {
                BeamLogManager.getSystemLogger().fine("Good news !!");
            }

            tgeox.numRows = second;
            tgeox.numRowsDetector = gpos.getNROWS();
            tgeox.position = pos;
            tgeox.resolution = resolution;
            tgeox.corner = corner;
            tgeox.llcorner = llcorner;
            tgeox.xDim = resolution;
            tgeox.yDim = -resolution;
            tgeox.detector = detector;

            resolutions.put(resolution, tgeox);
        }

        return resolutions;
    }
}
