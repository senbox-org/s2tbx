package org.esa.s2tbx.dataio.kompsat2.metadata;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.VirtualDir;
import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Specialized <code>XmlMetadata</code> for Kompsat2.
 *
 * @author  Razvan Dumitrascu
 * @see XmlMetadata
 */

public class Kompsat2Metadata extends XmlMetadata {

    private Kompsat2Component component;
    private String imageDirectoryPath;
    private List<BandMetadata> bandMetadatas;
    private static final int BUFFER_SIZE = 4096;


    private static class Kompsat2MetadataParser extends XmlMetadataParser<Kompsat2Metadata> {

        public Kompsat2MetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public Kompsat2Metadata(String name) {
        super(name);
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        String value = null;
        try {
            value= getAttributeValue(Kompsat2Constants.PATH_ID, Kompsat2Constants.PRODUCT_GENERIC_NAME);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, Kompsat2Constants.PATH_ID);
        }
        return value;
    }

    @Override
    public String getFormatName() {
        return Kompsat2Constants.PRODUCT_GENERIC_NAME;
    }

    @Override
    public int getRasterWidth() {
        return 0;
    }

    @Override
    public int getRasterHeight() {
        return 0;
    }

    @Override
    public String[] getRasterFileNames() {
        return new String[0];
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        ProductData.UTC date = null;
        String value = null;
        try{
            value = getAttributeValue(Kompsat2Constants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, Kompsat2Constants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, Kompsat2Constants.KOMPSAT2_UTC_DATE_FORMAT);
        }
        return date;
    }

    public String getProductType() {

        String productType = null;
        try {
            productType = getAttributeValue(Kompsat2Constants.PATH_PRODUCT_TYPE, Kompsat2Constants.KOMPSAT2_PRODUCT);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, Kompsat2Constants.PATH_PRODUCT_TYPE);
        }
        return productType;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(Kompsat2Constants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, Kompsat2Constants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, Kompsat2Constants.KOMPSAT2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return this.name;
    }

    @Override
    public String getFileName() {
        return this.name;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }


    public String getImageFileDirectoryName() {
        String directoryName = getAttributeValue(Kompsat2Constants.PATH_ZIP_FILE_NAME, null);
        if (directoryName == null) {
            warn(MISSING_ELEMENT_WARNING, Kompsat2Constants.PATH_ZIP_FILE_NAME);
        }
        return directoryName;
    }


    public Kompsat2Component getMetadataComponent() {
        return this.component;
    }

    /**
     * Creates the metadata element and the component associated to it
     *
     * @param path path to xml metadata file
     * @return Kompsat2Metadata object
     * @throws IOException
     */
    public static Kompsat2Metadata create(Path path) throws IOException {
        Assert.notNull(path);
        Kompsat2Metadata result = null;

        try (InputStream inputStream = Files.newInputStream(path)) {
            Kompsat2MetadataParser parser = new Kompsat2MetadataParser(Kompsat2Metadata.class);
            result = parser.parse(inputStream);
            result.setPath(path);
            result.setFileName(path.getFileName().toString());
            String directoryName = result.getAttributeValue(Kompsat2Constants.PATH_ZIP_FILE_NAME, null);
            String tiePointGridPointsString = result.getAttributeValue(Kompsat2Constants.PATH_TIE_POINT_GRID, null);
            String crsCode = result.getAttributeValue(Kompsat2Constants.PATH_CRS_NAME, null);
            String originPos = result.getAttributeValue(Kompsat2Constants.PATH_ORIGIN, null);
            if (directoryName != null) {
                Kompsat2Component component = new Kompsat2Component(path.getParent());
                component.setImageDirectoryName(directoryName);
                if(tiePointGridPointsString!=null) {
                    component.setTiePointGridPoints( parseTiePointGridAttribute(tiePointGridPointsString));
                }
                if(crsCode!=null){
                    component.setCrsCode(crsCode);
                }
                if(originPos!= null){
                    component.setOriginPos(originPos);
                }
                result.component = component;
            }
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Creates band metadata after the image zip archive has been uncompressed for every band
     */
    public void createBandMetadata() {
        this.bandMetadatas = new ArrayList<>();
        String imageDirectoryName = this.component.getImageDirectoryName();
        Path folderPath = Paths.get(this.getImageDirectoryPath()).resolve(imageDirectoryName.substring(0, imageDirectoryName.lastIndexOf(".")));
        File imageDirectory = folderPath.toFile();
        for (final File fileEntry : imageDirectory.listFiles()) {
            if (fileEntry.getName().endsWith(Kompsat2Constants.IMAGE_METADATA_EXTENSION)) {
                String fileEntryName = fileEntry.getName();
                BandMetadata bandMetadata = new BandMetadata(fileEntryName.substring(0, fileEntryName.lastIndexOf(".")));
                parseFile(fileEntry, bandMetadata);
                this.bandMetadatas.add(bandMetadata);
            }
        }
    }

    public String getImageDirectoryPath() {
        return this.imageDirectoryPath;
    }


    public List<BandMetadata> getBandsMetadata(){
        return this.bandMetadatas;
    }

    /**
     * Parses band metadata file on a set of already given tags
     *
     * @param fileEntry band metadata file to be parsed
     * @param imgMetadata reference to band metadata object to be filled
     */
    private void parseFile(File fileEntry, BandMetadata imgMetadata) {
        try (FileInputStream in = new FileInputStream(fileEntry)){
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                String line = reader.readLine();
                while (line != null) {
                    if (line.startsWith(Kompsat2Constants.TAG_BAND_WIDTH)) {
                        String[] splitLine = line.split("\\t");
                        imgMetadata.setBandwidth(Double.parseDouble(splitLine[1]));
                    } else if (line.startsWith(Kompsat2Constants.TAG_BITS_PER_PIXEL)) {
                        String[] splitLine = line.split("\\t");
                        imgMetadata.setBitsPerPixel(Integer.parseInt(splitLine[1]));
                    } else if (line.startsWith(Kompsat2Constants.TAG_NUMBER_COLUMNS_MS_IMAGE)||
                            (line.startsWith(Kompsat2Constants.TAG_NUMBER_COLUMNS_PAN_IMAGE))) {
                        String[] splitLine = line.split("\\t");
                        imgMetadata.setNumColumns(Integer.parseInt(splitLine[1]));
                    } else if(line. startsWith(Kompsat2Constants.TAG_NUMBER_ROWS_MS_IMAGE) ||
                            (line. startsWith(Kompsat2Constants.TAG_NUMBER_ROWS_PAN_IMAGE))) {
                        String[] splitLine = line.split("\\t");
                        imgMetadata.setNumLines(Integer.parseInt(splitLine[1]));
                    } else if (line.startsWith(Kompsat2Constants.TAG_PIXEL_SIZE)) {
                        String[] splitLine = line.split("\\t");
                        String[] splitResultLine = splitLine[1].split(" ");
                        imgMetadata.setStepSizeX(Double.parseDouble(splitResultLine[0]));
                        imgMetadata.setStepSizeY(Double.parseDouble(splitResultLine[1]));
                    } else if (line.startsWith(Kompsat2Constants.TAG_AZIMUTH_ANGLE)) {
                        String[] splitLine = line.split("\\t");
                        imgMetadata.setAzimuth(Double.parseDouble(splitLine[1]));
                    } else if (line.startsWith(Kompsat2Constants.TAG_INCIDENCE_ANGLE)) {
                        String[] splitLine = line.split("\\t");
                        imgMetadata.setIncidenceAngle(Double.parseDouble(splitLine[1]));
                    }
                    line = reader.readLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/**
 *  Parse tie point grid elements and rearrange them so that the order matches the one used in the
 *  tie point grid constructor
 *
 *  @param tiePointGridPointsString the tie point grid from metadata file to be parsed
 */
    private static float[][] parseTiePointGridAttribute(String tiePointGridPointsString) {
        String[] values;
        values = tiePointGridPointsString.split(" ");
        float[][] tiePoint = new float[2][values.length/2-1];
        for (int x=0; x<8; x+=2) {
            tiePoint[0][x/2] = Float.parseFloat(values[x]);
            tiePoint[1][x/2] = Float.parseFloat(values[x+1]);
        }
        float[] interchangeLat = new float[4];
        float[] interchangeLon = new float[4];
        interchangeLat[0] = tiePoint[0][0];
        interchangeLon[0] = tiePoint[1][0];
        interchangeLat[1] = tiePoint[0][3];
        interchangeLon[1] = tiePoint[1][3];
        interchangeLat[2] = tiePoint[0][1];
        interchangeLon[2] = tiePoint[1][1];
        interchangeLat[3] = tiePoint[0][2];
        interchangeLon[3] = tiePoint[1][2];
        tiePoint[0] = interchangeLat;
        tiePoint[1] = interchangeLon;
        return tiePoint;
    }

    /**
     * Unzip all elements in the zip file containing the tiff images in a temporary directory.
     *
     * @param path path to image zip files
     */
    public void unZipImageFiles(String path) {
        try {
            File tempImageFile = VirtualDir.createUniqueTempDir();
            this.imageDirectoryPath = tempImageFile.getPath();
            byte[] buffer;
            Path directoryFilePath = Paths.get(this.getImageDirectoryPath());
            try (ZipFile zipFile = new ZipFile(path)) {
                ZipEntry entry;
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    entry = entries.nextElement();
                    Path filePath = directoryFilePath.resolve(entry.getName());
                    if (entry.isDirectory()) {
                        Files.createDirectories(filePath);
                    } else {
                        if (entry.getName().endsWith(Kompsat2Constants.IMAGE_METADATA_EXTENSION) ||
                                entry.getName().endsWith(Kompsat2Constants.IMAGE_EXTENSION)) {
                            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                                try (BufferedOutputStream bos = new BufferedOutputStream(
                                        new FileOutputStream(filePath.toFile()))) {
                                    buffer = new byte[this.BUFFER_SIZE];
                                    int read;
                                    while ((read = inputStream.read(buffer)) > 0) {
                                        bos.write(buffer, 0, read);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
