package org.esa.s2tbx.dataio.muscate;

import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.engine_utilities.util.FileSystemUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 17/12/2019.
 */
class ProductFilePathsHelper {

    static {
        XmlMetadataParserFactory.registerParser(MuscateMetadata.class, new XmlMetadataParser<>(MuscateMetadata.class));
    }

    private final VirtualDirEx productDirectory;

    private String[] productFilePaths;
    private String productFileSystemSeparator;
    private MuscateMetadata productMetadata;
    private Boolean isMultiSizeProduct;

    ProductFilePathsHelper(VirtualDirEx productDirectory) {
        this.productDirectory = productDirectory;
    }

    boolean isMultiSize() throws IllegalAccessException, IOException, InstantiationException, SAXException, ParserConfigurationException, InvocationTargetException {
        if (this.isMultiSizeProduct == null) {
            this.isMultiSizeProduct = checkIsMultiSize();
        }
        return this.isMultiSizeProduct.booleanValue();
    }

    String computeImageRelativeFilePath(String tiffImageRelativeFilePath) throws IOException {
        if (this.productDirectory.isArchive()) {
            // the product path is an archive
            if (findFile(tiffImageRelativeFilePath) == null) {
                // the tiff image does not exist
                String name = this.productDirectory.getBaseFile().getName();
                int extensionIndex = name.lastIndexOf(".");
                if (extensionIndex > 0) {
                    name = name.substring(0, extensionIndex);
                }
                String fileSystemSeparator = getFileSystemSeparator();
                String relativeFilePath = name + fileSystemSeparator + FileSystemUtils.replaceFileSeparator(tiffImageRelativeFilePath, fileSystemSeparator);
                String tiffImageFilePath = findFile(relativeFilePath);
                if (tiffImageFilePath == null) {
                    throw new FileNotFoundException("The tiff image file '" + tiffImageRelativeFilePath + "' does not exist.");
                }
                return tiffImageFilePath;
            }
        }
        return tiffImageRelativeFilePath;
    }

    MuscateMetadata getMetadata() throws IOException, InstantiationException, ParserConfigurationException, SAXException {
        if (this.productMetadata == null) {
            String metadataFile = null;
            String[] filePaths = getProductFilePaths();
            for (String file : filePaths) {
                if (file.endsWith(".xml") && file.matches(MuscateConstants.XML_PATTERN)) {
                    metadataFile = file;
                    break;
                }
            }
            if (metadataFile == null) {
                throw new NullPointerException("The metadata file is null.");
            }
            try (FilePathInputStream metadataInputStream = productDirectory.getInputStream(metadataFile)) {
                this.productMetadata = (MuscateMetadata) XmlMetadataParserFactory.getParser(MuscateMetadata.class).parse(metadataInputStream);
            }
        }
        return this.productMetadata;
    }

    private boolean checkIsMultiSize() throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException,
                                              ParserConfigurationException, SAXException {

        int defaultWidth = 0;
        int defaultHeight = 0;
        MuscateMetadata metadata = getMetadata();
        Path productPath = this.productDirectory.getBaseFile().toPath();
        for (MuscateImage muscateImage : metadata.getImages()) {
            if (muscateImage != null || muscateImage.nature != null) {
                for (String tiffImageRelativeFilePath : muscateImage.getImageFiles()) {
                    String tiffImageFilePath = computeImageRelativeFilePath(tiffImageRelativeFilePath);
                    try (GeoTiffImageReader imageReader = GeoTiffImageReader.buildGeoTiffImageReader(productPath, tiffImageFilePath)) {
                        if (defaultWidth == 0) {
                            defaultWidth = imageReader.getImageWidth();
                        } else if (defaultWidth != imageReader.getImageWidth()) {
                            return true;
                        }
                        if (defaultHeight == 0) {
                            defaultHeight = imageReader.getImageHeight();
                        } else if (defaultHeight != imageReader.getImageHeight()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private String getFileSystemSeparator() {
        if (this.productFileSystemSeparator == null) {
            this.productFileSystemSeparator = this.productDirectory.getFileSystemSeparator();
        }
        return productFileSystemSeparator;
    }

    private String[] getProductFilePaths() throws IOException {
        if (this.productFilePaths == null) {
            this.productFilePaths = this.productDirectory.listAllFiles();
        }
        return this.productFilePaths;
    }

    private String findFile(String relativeFilePath) throws IOException  {
        String[] filePaths = getProductFilePaths();
        for (int i=0; i<filePaths.length; i++) {
            if (filePaths[i].equalsIgnoreCase(relativeFilePath)) {
                return relativeFilePath;
            }
        }
        return null;
    }
}
