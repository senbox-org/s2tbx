package org.esa.s2tbx.dataio.alos.pri.internal;

import com.bc.ceres.core.VirtualDir;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Holder class for .MD.XML metadata file.
 *
 * @author Denisa Stefanescu
 */
public class AlosPRIMetadata extends XmlMetadata {

    private static final int BUFFER_SIZE = 4096;
    private String imageDirectoryPath;
    ImageMetadata.InsertionPoint upperLeftPointOrigin;

    List<ImageMetadata> componentMetadata;

    static {
        XmlMetadataParserFactory.registerParser(ImageMetadata.class, new ImageMetadata.ImageMetadataParser(ImageMetadata.class));
    }

    public static class AlosPRIMetadataParser extends XmlMetadataParser<AlosPRIMetadata> {

        public AlosPRIMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public AlosPRIMetadata(String name) {
        super(name);
        componentMetadata = new ArrayList<>();
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        String value = null;
        try {
            value = getAttributeValue(AlosPRIConstants.PATH_ID, AlosPRIConstants.PRODUCT_GENERIC_NAME);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, AlosPRIConstants.PATH_ID);
        }
        return value;
    }

    @Override
    public String getFormatName() {
        return AlosPRIConstants.DIMAP;
    }

    @Override
    public int getRasterWidth() {
        if (componentMetadata.get(0).hasInsertPoint()) {
            return (int) ((getMaxPoint().get("x") - getMinInsertPointX()) / 2.5);
        } else {
            return componentMetadata.stream()
                    .filter(metadata -> metadata instanceof ImageMetadata)
                    .map(metadata -> (ImageMetadata) metadata)
                    .map(ImageMetadata::getRasterWidth)
                    .collect(Collectors.maxBy(Integer::compare))
                    .get();
        }

    }

    @Override
    public int getRasterHeight() {
        if (componentMetadata.get(0).hasInsertPoint()) {
            return (int) ((getMaxInsertPointY() - getMaxPoint().get("y")) / 2.5);
        } else {
            return componentMetadata.stream()
                    .filter(metadata -> metadata instanceof ImageMetadata)
                    .map(metadata -> (ImageMetadata) metadata)
                    .map(ImageMetadata::getRasterHeight)
                    .collect(Collectors.maxBy(Integer::compare))
                    .get();
        }
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
            value = getAttributeValue(AlosPRIConstants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, AlosPRIConstants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosPRIConstants.ALOSPRI_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(AlosPRIConstants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, AlosPRIConstants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosPRIConstants.ALOSPRI_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return AlosPRIConstants.DESCRIPTION;
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
                        if (entry.getName().endsWith(AlosPRIConstants.IMAGE_METADATA_EXTENSION) ||
                                entry.getName().endsWith(AlosPRIConstants.IMAGE_EXTENSION.toUpperCase())
                        ) {
                            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                                try (BufferedOutputStream outputStream = new BufferedOutputStream(
                                        new FileOutputStream(filePath.toFile()))) {
                                    buffer = new byte[AlosPRIMetadata.BUFFER_SIZE];
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

    public boolean hasInsertPoint() {
        return componentMetadata.stream().
                filter(metadata -> metadata instanceof ImageMetadata).
                map(ImageMetadata::hasInsertPoint).allMatch(aBoolean -> true);

    }

    public float getMaxInsertPointX() {
        return componentMetadata.stream()
                .filter(metadata -> metadata instanceof ImageMetadata)
                .map(metadata -> (ImageMetadata) metadata)
                .map(ImageMetadata::getInsertPointX)
                .collect(Collectors.maxBy(Float::compare))
                .get();
    }

    public float getMaxInsertPointY() {
        return componentMetadata.stream()
                .filter(metadata -> metadata instanceof ImageMetadata)
                .map(metadata -> (ImageMetadata) metadata)
                .map(ImageMetadata::getInsertPointY)
                .collect(Collectors.maxBy(Float::compare))
                .get();
    }

    public float getMinInsertPointX() {
        return componentMetadata.stream()
                .filter(metadata -> metadata instanceof ImageMetadata)
                .map(metadata -> (ImageMetadata) metadata)
                .map(ImageMetadata::getInsertPointX)
                .collect(Collectors.minBy(Float::compare))
                .get();
    }

    public float getMinInsertPointY() {
        return componentMetadata.stream()
                .filter(metadata -> metadata instanceof ImageMetadata)
                .map(metadata -> (ImageMetadata) metadata)
                .map(ImageMetadata::getInsertPointY)
                .collect(Collectors.minBy(Float::compare))
                .get();
    }

    public float getStepSizeX() {
        return componentMetadata.stream()
                .filter(metadata -> metadata instanceof ImageMetadata)
                .map(metadata -> (ImageMetadata) metadata)
                .map(ImageMetadata::getPixelSizeX)
                .collect(Collectors.maxBy(Float::compare))
                .get();
    }

    public float getStepSizeY() {
        return componentMetadata.stream()
                .filter(metadata -> metadata instanceof ImageMetadata)
                .map(metadata -> (ImageMetadata) metadata)
                .map(ImageMetadata::getPixelSizeY)
                .collect(Collectors.maxBy(Float::compare))
                .get();
    }

    public Map<String, Float[]> bandOffset() {
        Map<String, Float[]> bandOffset = new HashMap<>();
        for (ImageMetadata imageMetadata : componentMetadata) {
            float originX = (imageMetadata.getInsertPointX() - upperLeftPointOrigin.x) / imageMetadata.getPixelSizeX();
            float originY = (upperLeftPointOrigin.y - imageMetadata.getInsertPointY()) / imageMetadata.getPixelSizeY();
            bandOffset.put(imageMetadata.getBandName(), new Float[]{originX, originY});
        }
        return bandOffset;
    }

    public String getCrsCode() {
        return componentMetadata.stream().
                filter(metadata -> metadata instanceof ImageMetadata).
                map(ImageMetadata::getCrsCode).findFirst().get();
    }

    public ImageMetadata.InsertionPoint getProductOrigin() {
        for (ImageMetadata metadata : componentMetadata) {
            ImageMetadata.InsertionPoint currentOrigigin = metadata.getInsertPoint();
            if (upperLeftPointOrigin == null) {
                upperLeftPointOrigin = currentOrigigin;
            } else {
                if (upperLeftPointOrigin.x > currentOrigigin.x) {
                    upperLeftPointOrigin.x = currentOrigigin.x;
                }
                if (upperLeftPointOrigin.y < currentOrigigin.y) {
                    upperLeftPointOrigin.y = currentOrigigin.y;
                }
            }

        }
        return upperLeftPointOrigin;
    }

    public Map<String, Float> getMaxPoint() {
        ArrayList<Float> maxPointXList = new ArrayList<>();
        ArrayList<Float> maxPointYList = new ArrayList<>();
        for (ImageMetadata metadata : componentMetadata) {
            maxPointXList.add(metadata.getInsertPointMaxRightX());
            maxPointYList.add(metadata.getInsertPointMaxLowerY());
        }
        return new HashMap<String, Float>() {
            {
                put("x", Collections.max(maxPointXList));
                put("y", Collections.min(maxPointYList));
            }
        };
    }

    public float[][] getMaxCorners() {
        float[][] maxCornerValue = new float[2][4];
        for (ImageMetadata metadata : componentMetadata) {
            float[][] cornerLats = metadata.getCornerLonsLats();
            if (Float.floatToIntBits(maxCornerValue[0][0]) == 0) {
                maxCornerValue = cornerLats;
            } else {
                if (maxCornerValue[0][0] < cornerLats[0][0]) {
                    maxCornerValue[0][0] = cornerLats[0][0];
                }
                if (maxCornerValue[1][0] > cornerLats[1][0]) {
                    maxCornerValue[1][0] = cornerLats[1][0];
                }
                if (maxCornerValue[0][1] < cornerLats[0][1]) {
                    maxCornerValue[0][1] = cornerLats[0][1];
                }
                if (maxCornerValue[1][1] > cornerLats[1][1]) {
                    maxCornerValue[1][1] = cornerLats[1][1];
                }
                if (maxCornerValue[0][2] > cornerLats[0][2]) {
                    maxCornerValue[0][2] = cornerLats[0][2];
                }
                if (maxCornerValue[1][2] < cornerLats[1][2]) {
                    maxCornerValue[1][2] = cornerLats[1][2];
                }
                if (maxCornerValue[0][3] > cornerLats[0][3]) {
                    maxCornerValue[0][3] = cornerLats[0][3];
                }
                if (maxCornerValue[1][3] < cornerLats[1][3]) {
                    maxCornerValue[1][3] = cornerLats[1][3];
                }
            }
        }
        return maxCornerValue;
    }

    public List<ImageMetadata> getImageMetadataList() {
        return componentMetadata.stream().filter(metadata -> metadata instanceof ImageMetadata).map(metadata -> (ImageMetadata) metadata).collect(Collectors.toList());
    }

    public void addComponentMetadata(File metadata) {
        ImageMetadata imageMetadata = create(ImageMetadata.class, metadata.toPath());
        imageMetadata.setFileName(metadata.getName());
        this.componentMetadata.add(imageMetadata);
    }
}
