package org.esa.s2tbx.dataio.ikonos.metadata;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.VirtualDir;
import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class IkonosMetadata extends XmlMetadata {

    private static final Logger logger = Logger.getLogger(IkonosMetadata.class.getName());

    private static final int BUFFER_SIZE = 4096;
    private IkonosComponent component;
    private String imageDirectoryPath;
    private List<BandMetadata> bandMetadatas;

    private static class IkonosMetadataParser extends XmlMetadataParser<IkonosMetadata> {

        public IkonosMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public IkonosMetadata(final String name) {
        super(name);
    }

    /**
     * Creates the metadata element and the component associated to it
     *
     * @param path path to xml metadata file
     * @return IkonosMetadata object
     * @throws IOException
     */
    public static IkonosMetadata create(final Path path) throws IOException {
        Assert.notNull(path);
        IkonosMetadata result = null;

        try (InputStream inputStream = Files.newInputStream(path)) {
            IkonosMetadataParser parser = new IkonosMetadataParser(IkonosMetadata.class);
            result = parser.parse(inputStream);
            result.setPath(path);
            result.setFileName(path.getFileName().toString());
            String directoryName = IkonosConstants.PATH_ZIP_FILE_NAME_PATTERN;

            final String tiePointGridPointsString = result.getAttributeValue(IkonosConstants.PATH_TIE_POINT_GRID, null);
            final String crsCode = result.getAttributeValue(IkonosConstants.PATH_CRS_NAME, null);

            final String originPos = result.getAttributeValue(IkonosConstants.PATH_ORIGIN, null);

            if (directoryName != null) {
                final IkonosComponent component = new IkonosComponent(path.getParent());
                if (tiePointGridPointsString != null) {
                    component.setTiePointGridPoints(parseTiePointGridAttribute(tiePointGridPointsString));
                }
                if (crsCode != null) {

                    component.setCrsCode(crsCode);
                }
                if (originPos != null) {
                    component.setOriginPos(originPos);
                }
                result.component = component;
            }
        } catch (ParserConfigurationException | SAXException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    /**
     * Parse tie point grid elements and rearrange them so that the order matches the one used in the
     * tie point grid constructor
     *
     * @param tiePointGridPointsString the tie point grid from metadata file to be parsed
     */
    private static float[][] parseTiePointGridAttribute(final String tiePointGridPointsString) {
        final String[] values = tiePointGridPointsString.split(" ");
        final float[][] tiePoint = new float[2][values.length / 2 - 1];
        for (int x = 0; x < 8; x += 2) {
            tiePoint[0][x / 2] = Float.parseFloat(values[x]);
            tiePoint[1][x / 2] = Float.parseFloat(values[x + 1]);
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

    public String getImageDirectoryPath() {
        return this.imageDirectoryPath;
    }

    public String getProductType() {
        String productType = null;
        try {
            productType = getAttributeValue(IkonosConstants.PATH_PRODUCT_TYPE, IkonosConstants.IKONOS_PRODUCT);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, IkonosConstants.PATH_PRODUCT_TYPE);
        }
        return productType;
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        String value = null;
        try {
            value = getAttributeValue(IkonosConstants.PATH_ID, IkonosConstants.PRODUCT_GENERIC_NAME);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, IkonosConstants.PATH_ID);
        }
        return value;
    }

    @Override
    public String getFormatName() {
        return IkonosConstants.PRODUCT_GENERIC_NAME;
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
        try {
            value = getAttributeValue(IkonosConstants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, IkonosConstants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, IkonosConstants.IKONOS_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(IkonosConstants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, IkonosConstants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, IkonosConstants.IKONOS_UTC_DATE_FORMAT);
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

    public IkonosComponent getMetadataComponent() {
        return this.component;
    }

    /**
     * Unzip all elements in the zip file containing the tiff images in a temporary directory.
     *
     * @param path path to image zip files
     */
    public void unZipImageFiles(final String path) {
        try {
            final File tempImageFile = VirtualDir.createUniqueTempDir();
            this.imageDirectoryPath = tempImageFile.getPath();
            byte[] buffer;
            final Path directoryFilePath = Paths.get(this.getImageDirectoryPath());
            try (ZipFile zipFile = new ZipFile(path)) {
                ZipEntry entry;
                final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    entry = entries.nextElement();
                    Path filePath = directoryFilePath.resolve(entry.getName());
                    if (entry.isDirectory()) {
                        Files.createDirectories(filePath);
                    } else {
                        if (entry.getName().endsWith(IkonosConstants.IMAGE_METADATA_EXTENSION) ||
                                entry.getName().endsWith(IkonosConstants.IMAGE_EXTENSION) ||
                                entry.getName().endsWith(IkonosConstants.IMAGE_COMMON_METADATA_EXTENSION) ||
                                entry.getName().endsWith(IkonosConstants.IMAGE_ARCHIVE_EXTENSION)
                        ) {

                            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                                try (BufferedOutputStream outputStream = new BufferedOutputStream(
                                        new FileOutputStream(filePath.toFile()))) {
                                    buffer = new byte[this.BUFFER_SIZE];
                                    int read;
                                    while ((read = inputStream.read(buffer)) > 0) {
                                        outputStream.write(buffer, 0, read);
                                    }
                                }
                            }
                            if (entry.getName().endsWith(IkonosConstants.IMAGE_ARCHIVE_EXTENSION)) {
                                unGZipImageFiles(directoryFilePath.resolve(entry.getName()).toString());
                            }
                        }

                    }
                }
            }
        } catch (IOException e) {
           logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * UnGzip all elements in the gzip file containing the tiff images in a temporary directory.
     *
     * @param path path to image gzip files
     */
    public void unGZipImageFiles(final String path) {
        try {
            final Path directoryFilePath = Paths.get(this.getImageDirectoryPath());
            if (path.endsWith(IkonosConstants.IMAGE_ARCHIVE_EXTENSION)) {
                try (GZIPInputStream in = new GZIPInputStream(new FileInputStream(path))) {
                    final File outFile = new File(directoryFilePath.resolve(path).toString().substring(0, path.lastIndexOf(".")));
                    try (FileOutputStream out = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Creates band metadata after the image gzip archive has been uncompressed for every band
     */
    public void createBandMetadata() {
        this.bandMetadatas = new ArrayList<>();
        final String imageDirectoryName = this.component.getImageDirectoryName();
        final Path folderPath = Paths.get(this.getImageDirectoryPath()).resolve(imageDirectoryName.substring(0, imageDirectoryName.lastIndexOf(".")));

        final File imageDirectory = folderPath.toFile();
        final Map<String, Double> metadataInformationList = new HashMap<>();
        for (File fileEntry : imageDirectory.listFiles()) {
            if (fileEntry.getName().endsWith(IkonosConstants.IMAGE_METADATA_EXTENSION)) {
                final String fileEntryName = fileEntry.getName();
                final BandMetadata bandMetadata = new BandMetadata(fileEntryName.substring(0, fileEntryName.lastIndexOf(".")));
                parseIMGMetadataFile(fileEntry, bandMetadata);
                this.bandMetadatas.add(bandMetadata);
            } else if (fileEntry.getName().endsWith(IkonosConstants.IMAGE_COMMON_METADATA_EXTENSION)) {
                parseMetadataFile(fileEntry, metadataInformationList);
            }
        }
        //in each band add the information found in the general metadata file
        if (!metadataInformationList.isEmpty()) {
            for (BandMetadata band : bandMetadatas) {
                band.setNominalAzimuth(metadataInformationList.get(IkonosConstants.TAG_NOMINAL_AZIMUTH));
                band.setNominalElevation(metadataInformationList.get(IkonosConstants.TAG_NOMINAL_ELEVATION));
                band.setSunAngleAzimuth(metadataInformationList.get(IkonosConstants.TAG_SUN_ANGLE_AZIMUTH));
                band.setSunAngleElevation(metadataInformationList.get(IkonosConstants.TAG_SUN_ANGLE_ELEVATION));
            }
        }
    }

    public List<BandMetadata> getBandsMetadata() {
        return this.bandMetadatas;
    }

    /**
     * Parses band metadata file on a set of already given tags
     *
     * @param fileEntry   band metadata file to be parsed
     * @param imgMetadata reference to band metadata object to be filled
     */
    private void parseIMGMetadataFile(final File fileEntry, final BandMetadata imgMetadata) {
        try (final FileInputStream in = new FileInputStream(fileEntry)) {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line = reader.readLine();
                while (line != null) {
                    if (line.startsWith(IkonosConstants.TAG_BITS_PER_PIXEL)) {
                        final String[] splitLine = line.split("\\s");
                        imgMetadata.setBitsPerPixel(Integer.parseInt(splitLine[1]));
                    } else if (line.startsWith(IkonosConstants.TAG_NUMBER_COLUMNS_IMAGE)) {
                        final String[] splitLine = line.split("\\s");
                        imgMetadata.setNumColumns(Integer.parseInt(splitLine[1]));
                    } else if (line.startsWith(IkonosConstants.TAG_NUMBER_ROWS_IMAGE)) {
                        final String[] splitLine = line.split("\\s");
                        imgMetadata.setNumLines(Integer.parseInt(splitLine[1]));
                    } else if (line.startsWith(IkonosConstants.TAG_PIXEL_SIZE_X)) {
                        final String[] splitLine = line.split("\\s");
                        imgMetadata.setPixelSizeX(Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_PIXEL_SIZE_Y)) {
                        final String[] splitLine = line.split("\\s");
                        imgMetadata.setPixelSizeY(Double.parseDouble(splitLine[3]));
                    }
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void parseMetadataFile(final File fileEntry, final Map<String, Double> metadataInformationList) {
        try (final FileInputStream in = new FileInputStream(fileEntry)) {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line = reader.readLine();
                while (line != null) {
                    if (line.startsWith(IkonosConstants.TAG_NOMINAL_AZIMUTH)) {
                        final String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_NOMINAL_AZIMUTH, Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_NOMINAL_ELEVATION)) {
                        final String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_NOMINAL_ELEVATION, Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_SUN_ANGLE_ELEVATION)) {
                        final String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_SUN_ANGLE_ELEVATION, Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_SUN_ANGLE_AZIMUTH)) {
                        final String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_SUN_ANGLE_AZIMUTH, Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_ORDER_PIXEL_SIZE)) {
                        final String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_ORDER_PIXEL_SIZE, Double.parseDouble(splitLine[4]));
                    }
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
