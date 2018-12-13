package org.esa.s2tbx.dataio.worldviewESAarchive.metadata;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.VirtualDir;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.worldviewESAarchive.common.WorldViewESAarchiveConstants;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WorldViewESAarchiveMetadata extends XmlMetadata {

    private static final int BUFFER_SIZE = 4096;
    private String imageDirectoryPath;


    private static class WorldViewESAarchiveMetadataParser extends XmlMetadataParser<WorldViewESAarchiveMetadata> {

        public WorldViewESAarchiveMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public WorldViewESAarchiveMetadata(String name) {
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
            value = getAttributeValue(WorldViewESAarchiveConstants.PATH_ID, WorldViewESAarchiveConstants.PRODUCT_GENERIC_NAME);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldViewESAarchiveConstants.PATH_ID);
        }
        return value;
    }

    @Override
    public String getFormatName() {
        return WorldViewESAarchiveConstants.PRODUCT_GENERIC_NAME;
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
            value = getAttributeValue(WorldViewESAarchiveConstants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldViewESAarchiveConstants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldViewESAarchiveConstants.WORLDVIEW2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(WorldViewESAarchiveConstants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldViewESAarchiveConstants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldViewESAarchiveConstants.WORLDVIEW2_UTC_DATE_FORMAT);
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

    public String getImageDirectoryPath() {
        return this.imageDirectoryPath;
    }

    public void setImageDirectoryPath(String imageDirectoryPath) {
        this.imageDirectoryPath = imageDirectoryPath;
    }

    public String getProductType() {

        String productType = null;
        try {
            productType = getAttributeValue(WorldViewESAarchiveConstants.PATH_PRODUCT_TYPE, WorldViewESAarchiveConstants.PRODUCT_GENERIC_NAME);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldViewESAarchiveConstants.PATH_PRODUCT_TYPE);
        }
        return productType;
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
                        if (entry.getName().endsWith(WorldViewESAarchiveConstants.METADATA_EXTENSION) ||
                                entry.getName().endsWith(WorldViewESAarchiveConstants.IMAGE_EXTENSION)) {
                            //create all non exists folders
                            //else you will hit FileNotFoundException for compressed folder
                            File newFile = new File(imageDirectoryPath + File.separator + entry.getName());
                            new File(newFile.getParent()).mkdirs();
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
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the metadata element and the component associated to it
     *
     * @param path path to xml metadata file
     * @return WorldView2Metadata object
     * @throws IOException
     */
    public static WorldViewESAarchiveMetadata create(Path path) throws IOException {
        Assert.notNull(path);
        WorldViewESAarchiveMetadata result = null;

        try (InputStream inputStream = Files.newInputStream(path)) {
            WorldViewESAarchiveMetadataParser parser = new WorldViewESAarchiveMetadataParser(WorldViewESAarchiveMetadata.class);
            result = parser.parse(inputStream);
            result.setPath(path.toString());
            result.setFileName(path.getFileName().toString());
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return result;
    }
}
