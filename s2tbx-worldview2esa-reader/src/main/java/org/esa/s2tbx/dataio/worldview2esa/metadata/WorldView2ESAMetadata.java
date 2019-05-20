package org.esa.s2tbx.dataio.worldview2esa.metadata;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.VirtualDir;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.worldview2esa.common.WorldView2ESAConstants;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WorldView2ESAMetadata extends XmlMetadata {

    private static final Logger log = Logger.getLogger(WorldView2ESAMetadata.class.getName());

    private static final int BUFFER_SIZE = 4096;
    private String imageDirectoryPath;


    private static class WorldView2ESAMetadataParser extends XmlMetadataParser<WorldView2ESAMetadata> {

        public WorldView2ESAMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public WorldView2ESAMetadata(String name) {
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
            value = getAttributeValue(WorldView2ESAConstants.PATH_ID, WorldView2ESAConstants.PRODUCT_GENERIC_NAME);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2ESAConstants.PATH_ID);
        }
        return value;
    }

    @Override
    public String getFormatName() {
        return WorldView2ESAConstants.PRODUCT_GENERIC_NAME;
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
            value = getAttributeValue(WorldView2ESAConstants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2ESAConstants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldView2ESAConstants.WORLDVIEW2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(WorldView2ESAConstants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2ESAConstants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldView2ESAConstants.WORLDVIEW2_UTC_DATE_FORMAT);
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
                        if (entry.getName().endsWith(WorldView2ESAConstants.METADATA_EXTENSION) ||
                                entry.getName().endsWith(WorldView2ESAConstants.IMAGE_EXTENSION)) {
                            //create all non exists folders
                            //else you will hit FileNotFoundException for compressed folder
                            final File newFile = new File(imageDirectoryPath + File.separator + entry.getName());
                            new File(newFile.getParent()).mkdirs();
                            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                                try (BufferedOutputStream outputStream = new BufferedOutputStream(
                                        new FileOutputStream(filePath.toFile()))) {
                                    buffer = new byte[WorldView2ESAMetadata.BUFFER_SIZE];
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
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Creates the metadata element and the component associated to it
     *
     * @param path path to xml metadata file
     * @return WorldView2Metadata object
     * @throws IOException
     */
    public static WorldView2ESAMetadata create(final Path path) throws IOException {
        Assert.notNull(path);
        WorldView2ESAMetadata result = null;

        try (InputStream inputStream = Files.newInputStream(path)) {
            WorldView2ESAMetadataParser parser = new WorldView2ESAMetadataParser(WorldView2ESAMetadata.class);
            result = parser.parse(inputStream);
            result.setPath(path);
            result.setFileName(path.getFileName().toString());
        } catch (ParserConfigurationException | SAXException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    public static WorldView2ESAMetadata create(final FilePathInputStream filePathInputStream) throws IOException {
        WorldView2ESAMetadata result = null;
        try {
            WorldView2ESAMetadataParser parser = new WorldView2ESAMetadataParser(WorldView2ESAMetadata.class);
            result = parser.parse(filePathInputStream);
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException(e);
        }
        Path path = filePathInputStream.getPath();
        result.setPath(path);
        result.setFileName(path.getFileName().toString());
        return result;
    }
}
