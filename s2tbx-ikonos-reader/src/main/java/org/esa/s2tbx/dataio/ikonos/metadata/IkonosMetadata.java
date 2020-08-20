package org.esa.s2tbx.dataio.ikonos.metadata;

import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class IkonosMetadata extends XmlMetadata {

    private static final int BUFFER_SIZE = 1024 * 1024;
    private IkonosComponent component;

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
     * @param filePathInputStream path to xml metadata file
     * @return IkonosMetadata object
     * @throws IOException
     */
    public static IkonosMetadata create(FilePathInputStream filePathInputStream) throws IOException, ParserConfigurationException, SAXException {
        IkonosMetadataParser parser = new IkonosMetadataParser(IkonosMetadata.class);
        IkonosMetadata result = parser.parse(filePathInputStream);
        result.setPath(filePathInputStream.getPath());
        result.setFileName(filePathInputStream.getPath().getFileName().toString());
        IkonosComponent component = new IkonosComponent(filePathInputStream.getPath().getParent());
        String tiePointGridPointsString = result.getAttributeValue(IkonosConstants.PATH_TIE_POINT_GRID, null);
        if (tiePointGridPointsString != null) {
            component.setTiePointGridPoints(parseTiePointGridAttribute(tiePointGridPointsString));
        }
        String crsCode = result.getAttributeValue(IkonosConstants.PATH_CRS_NAME, null);
        if (crsCode != null) {
            component.setCrsCode(crsCode);
        }
        String originPosition = result.getAttributeValue(IkonosConstants.PATH_ORIGIN, null);
        if (originPosition != null) {
            String[] splitLine = originPosition.split("\\s");
            double originPositionX = Double.parseDouble(splitLine[0]);
            double originPositionY = Double.parseDouble(splitLine[1]);
            component.setOriginPosition(originPositionX, originPositionY);
        }
        result.component = component;
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
     * UnGzip all elements in the gzip file containing the tiff images in a temporary directory.
     *
     * @param path path to image gzip files
     */
    public void unGZipImageFiles(Path path, String imageDirectoryPath) throws IOException {
        Path directoryFilePath = Paths.get(imageDirectoryPath);
        try (GZIPInputStream in = new GZIPInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            Path filePath = directoryFilePath.resolve(path.toString().substring(0, path.toString().lastIndexOf(".")));
            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
            }
        }
    }

    public static BandMetadata parseIMGMetadataFile(VirtualDirEx productDirectory, String metadataRelativeFilePath) throws IOException {
        try (FilePathInputStream inputStream = productDirectory.getInputStream(metadataRelativeFilePath)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                int extensionIndex = metadataRelativeFilePath.lastIndexOf(IkonosConstants.IMAGE_METADATA_EXTENSION);
                String tiffImageRelativeFilePath = metadataRelativeFilePath.substring(0, extensionIndex) + IkonosConstants.IMAGE_EXTENSION;
                if (!productDirectory.exists(tiffImageRelativeFilePath)) {
                    tiffImageRelativeFilePath += ".gz";
                    if (!productDirectory.exists(tiffImageRelativeFilePath)) {
                        throw new FileNotFoundException("The TIFF image file path '" + tiffImageRelativeFilePath+"' does not exists into the product directory '" + productDirectory.getBasePath()+"'.");
                    }
                }

                BandMetadata imgMetadata = new BandMetadata(tiffImageRelativeFilePath);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(IkonosConstants.TAG_BITS_PER_PIXEL)) {
                        String[] splitLine = line.split("\\s");
                        imgMetadata.setBitsPerPixel(Integer.parseInt(splitLine[1]));
                    } else if (line.startsWith(IkonosConstants.TAG_NUMBER_COLUMNS_IMAGE)) {
                        String[] splitLine = line.split("\\s");
                        imgMetadata.setNumColumns(Integer.parseInt(splitLine[1]));
                    } else if (line.startsWith(IkonosConstants.TAG_NUMBER_ROWS_IMAGE)) {
                        String[] splitLine = line.split("\\s");
                        imgMetadata.setNumLines(Integer.parseInt(splitLine[1]));
                    } else if (line.startsWith(IkonosConstants.TAG_PIXEL_SIZE_X)) {
                        String[] splitLine = line.split("\\s");
                        imgMetadata.setPixelSizeX(Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_PIXEL_SIZE_Y)) {
                        String[] splitLine = line.split("\\s");
                        imgMetadata.setPixelSizeY(Double.parseDouble(splitLine[3]));
                    }
                }
                return imgMetadata;
            }
        }
    }

    public static Map<String, Double> parseMetadataFile(VirtualDirEx productDirectory, String fileEntryName) throws IOException {
        try (FilePathInputStream inputStream = productDirectory.getInputStream(fileEntryName)) {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                Map<String, Double> metadataInformationList = new HashMap<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(IkonosConstants.TAG_NOMINAL_AZIMUTH)) {
                        String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_NOMINAL_AZIMUTH, Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_NOMINAL_ELEVATION)) {
                        String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_NOMINAL_ELEVATION, Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_SUN_ANGLE_ELEVATION)) {
                        String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_SUN_ANGLE_ELEVATION, Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_SUN_ANGLE_AZIMUTH)) {
                        String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_SUN_ANGLE_AZIMUTH, Double.parseDouble(splitLine[3]));
                    } else if (line.startsWith(IkonosConstants.TAG_ORDER_PIXEL_SIZE)) {
                        String[] splitLine = line.split("\\s");
                        metadataInformationList.put(IkonosConstants.TAG_ORDER_PIXEL_SIZE, Double.parseDouble(splitLine[4]));
                    }
                }
                return metadataInformationList;
            }
        }
    }
}
