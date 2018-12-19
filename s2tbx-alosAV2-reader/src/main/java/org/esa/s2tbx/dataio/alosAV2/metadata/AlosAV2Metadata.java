package org.esa.s2tbx.dataio.alosAV2.metadata;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.VirtualDir;
import org.esa.s2tbx.dataio.alosAV2.internal.AlosAV2Constants;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AlosAV2Metadata extends XmlMetadata {

    private String imageDirectoryPath;
    private static final int BUFFER_SIZE = 4096;
    private List<BandMetadata> imageMetadatas;
    private AlosAV2Component component;

    private static class AlosAV2MetadataParser extends XmlMetadataParser<AlosAV2Metadata> {

        public AlosAV2MetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public AlosAV2Metadata(String name) {
        super(name);
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        return getAttributeValue(AlosAV2Constants.PATH_IDENTIFIER,AlosAV2Constants.DESCRIPTION);
    }

    @Override
    public String getFormatName() {
        return AlosAV2Constants.PRODUCT_GENERIC_NAME;
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
            value = getAttributeValue(AlosAV2Constants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosAV2Constants.ALOSAV2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(AlosAV2Constants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosAV2Constants.ALOSAV2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return AlosAV2Constants.DESCRIPTION;
    }

    @Override
    public String getFileName() {
        return this.name;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }

    public String getImageDirectoryPath() {
        return this.imageDirectoryPath;
    }

    /**
     * Creates the metadata element and the component associated to it
     *
     * @param path path to xml metadata file
     * @return WorldView2Metadata object
     * @throws IOException
     */
    public static AlosAV2Metadata create(Path path) throws IOException {
        Assert.notNull(path);
        AlosAV2Metadata result = null;
        try (InputStream inputStream = Files.newInputStream(path)) {
            AlosAV2MetadataParser parser = new AlosAV2MetadataParser(AlosAV2Metadata.class);
            result = parser.parse(inputStream);
            result.setPath(path.toString());
            result.setFileName(path.getFileName().toString());
            String tiePointGridPointsString = result.getAttributeValue(AlosAV2Constants.PATH_TIE_POINT_GRID, null);
            String crsCode = result.getAttributeValue(AlosAV2Constants.PATH_CRS_NAME, null);
            String originPos = result.getAttributeValue(AlosAV2Constants.PATH_ORIGIN, null);
                AlosAV2Component component = new AlosAV2Component(path.getParent());
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

        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return result;
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
                        if (entry.getName().endsWith(AlosAV2Constants.IMAGE_METADATA_EXTENSION) ||
                                entry.getName().endsWith(AlosAV2Constants.IMAGE_EXTENSION)) {
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

    public AlosAV2Component getMetadataComponent() {
        return this.component;
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
}
