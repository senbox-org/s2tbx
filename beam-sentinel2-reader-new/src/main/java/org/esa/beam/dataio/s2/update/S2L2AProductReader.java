package org.esa.beam.dataio.s2.update;

import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.SampleCoding;
import org.esa.beam.jai.ImageManager;

import java.awt.Color;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.esa.beam.dataio.s2.update.S2Config.TILE_LAYOUTS;

/**
 * Represents information of a Sentinel 2 band
 *
 * @author Tonio Fincke
 * @author Norman Fomferra
 */
public class S2L2AProductReader extends S2ProductReader {

    static final String productType = "L2A";
    private static final String[] scl_names = {"NoData", "Saturated", "Shadows", "CloudShadows", "Vegetation",
            "BareSoils", "Water", "CloudLowProb", "CloudMediumProb", "CloudHighProb", "Cirrus", "Snow"};
    private static final String[] scl_descriptions = {"No Data (Missing data on projected tiles)",
            "Saturated or defective pixel", "Dark features / Shadows", "Cloud Shadows", "Vegetation",
            "Bare soils / Deserts", "Water (dark and bright)", "Cloud low probability", "Cloud medium probability",
            "Cloud high probability", "Thin cirrus", "Snow or ice"};
    private static final Color[] scl_colors = {Color.BLACK, Color.RED, Color.DARK_GRAY.darker(), new Color(139, 69, 19),
            Color.GREEN, Color.YELLOW.darker(), Color.BLUE, Color.DARK_GRAY, Color.GRAY, Color.WHITE,
            Color.BLUE.brighter().brighter(), Color.PINK};

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be <code>null</code> for internal reader
     *                     implementations
     */
    protected S2L2AProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    public Product readProductNodes(File metadataFile) throws IOException {
        //todo read metadata
        String productName = "";
        if (S2Config.METADATA_NAME_2A_PATTERN.matcher(metadataFile.getName()).matches()) {
            productName = createProductNameFromValidMetadataName(metadataFile.getName());
        } else {
            productName = metadataFile.getParentFile().getName();
        }
        final int width = TILE_LAYOUTS[S2SpatialResolution.R10M.id].width;
        final int height = TILE_LAYOUTS[S2SpatialResolution.R10M.id].height;
        Product product = new Product(productName,
                                      "S2_MSI_L2A",
                                      width,
                                      height);
        final String parentDirectory = metadataFile.getParent();
        final File granuleDirectory = new File(parentDirectory + "/GRANULE");
        final File[] granules = granuleDirectory.listFiles();
        if (granules != null) {
            //todo read all granules
            final Map<String, BandInfo> bandInfoMap = getBandInfoMap(granules[0].getPath());
            addBands(product, bandInfoMap, new TileMultiLevelImageFactory(ImageManager.getImageToModelTransform(product.getGeoCoding())));
            readMasks(product, granules[0].getPath());
        }
        product.setAutoGrouping("B*10:B*20:B*60");
        return product;
    }

    @Override
    public BandInfo getBandInfo(File file, String tileIndex, String bandIndex, int resolution) {
        BandInfo bandInfo;
        S2WavebandInfo wavebandInfo = null;
        S2SpatialResolution spatialResolution = null;
        switch (resolution) {
            case 10:
                spatialResolution = S2SpatialResolution.R10M;
                break;
            case 20:
                spatialResolution = S2SpatialResolution.R20M;
                break;
            case 60:
                spatialResolution = S2SpatialResolution.R60M;
                break;
        }
        if (S2WaveBandInfoProvider.hasWaveBandInfo(bandIndex)) {
            wavebandInfo = S2WaveBandInfoProvider.getWaveBandInfo(bandIndex);
            bandInfo = new BandInfo(tileIndex, file, wavebandInfo.bandId, wavebandInfo, spatialResolution, false);
        } else {
            bandInfo = new BandInfo(tileIndex, file,
                                    bandIndex,
                                    wavebandInfo,
                                    spatialResolution, false);
        }
        return bandInfo;
    }

    @Override
    public String getProductType() {
        return productType;
    }

    @Override
    public void readMasks(Product product, String granulePath) throws IOException {
        File qiDataPath = new File(granulePath + "/QI_DATA");
        Map<String, BandInfo> maskMap = new HashMap<String, BandInfo>();
        if (qiDataPath.isDirectory()) {
            maskMap = updateMaskMap(qiDataPath, maskMap, S2Config.USED_MASK_IMAGE_NAME_PATTERN);
            File l2aQualityMasksPath = new File(qiDataPath.getPath() + "/L2A_Quality_Masks");
            if (l2aQualityMasksPath.isDirectory()) {
                maskMap = updateMaskMap(l2aQualityMasksPath, maskMap, S2Config.USED_MASK_IMAGE_NAME_PATTERN);
            }
        }
        File sclDataPath = new File(granulePath + "/IMG_DATA");
        if (sclDataPath.isDirectory()) {
            maskMap = updateMaskMap(sclDataPath, maskMap, S2Config.SCL_NAME_PATTERN_2A);
        }
        addBands(product, maskMap, new TileMultiLevelImageFactory(ImageManager.getImageToModelTransform(product.getGeoCoding())));
        setMasks(product);
    }

    protected void putFilesIntoBandInfoMap(Map<String, BandInfo> bandInfoMap, File[] files) {
        if (files != null) {
            for (File file : files) {
                final Matcher matcher = S2Config.IMAGE_NAME_PATTERN_2A.matcher(file.getName());
                if (matcher.matches()) {
                    final String tileIndex = matcher.group(3);
                    String bandName = matcher.group(4);
                    String resolution = matcher.group(6);
                    bandName = trimUnderscores(bandName) + "_" + resolution;
                    bandInfoMap.put(bandName, getBandInfo(file, tileIndex, bandName, Integer.parseInt(resolution)));
                }
            }
        }
    }

    private String trimUnderscores(String bandIndex) {
        if (bandIndex.startsWith("_")) {
            bandIndex = bandIndex.substring(1);
        }
        if (bandIndex.endsWith("_")) {
            bandIndex = bandIndex.substring(0, bandIndex.length() - 1);
        }
        return bandIndex;
    }

    @Override
    Map<String, BandInfo> getBandInfoMap(String filePath) throws IOException {

        final String imageDataPath = filePath + "/IMG_DATA";
        final File productDir = new File(imageDataPath);

        final String atmCorrPath60 = imageDataPath + "/Atmospheric_Correction_Tiles/Bands_60m";
        final String atmCorrPath20 = imageDataPath + "/Atmospheric_Correction_Tiles/Bands_20m";
        final String atmCorrPath10 = imageDataPath + "/Atmospheric_Correction_Tiles/Bands_10m";
        final File atmCorrDir60 = new File(atmCorrPath60);
        final File atmCorrDir20 = new File(atmCorrPath20);
        final File atmCorrDir10 = new File(atmCorrPath10);

        final FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return S2Config.IMAGE_NAME_PATTERN_2A.matcher(name).matches();
            }
        };
        File[][] filesMatrix = new File[3][];
        if (atmCorrDir60.isDirectory()) {
            filesMatrix[0] = atmCorrDir60.listFiles(filter);
        }
        if (atmCorrDir20.isDirectory()) {
            filesMatrix[1] = atmCorrDir20.listFiles(filter);
        }
        if (atmCorrDir10.isDirectory()) {
            filesMatrix[2] = atmCorrDir10.listFiles(filter);
        }
        Map<String, BandInfo> bandInfoMap = new HashMap<String, BandInfo>();
        for (File[] files : filesMatrix) {
            putFilesIntoBandInfoMap(bandInfoMap, files);
        }
        return bandInfoMap;
    }

    protected void setMasks(Product targetProduct) {
        final Band sclBand = targetProduct.getBand("SCL");
        if (sclBand != null) {
            String bandName = sclBand.getName();
            final SampleCoding sclCoding = new SampleCoding("sclCoding");
            for (int i = 0; i < scl_names.length; i++) {
                sclCoding.addSample(scl_names[i], i, scl_descriptions[i]);
            }
            for (int i = 0; i < sclCoding.getNumAttributes(); i++) {
                final int sampleValue = sclCoding.getSampleValue(i);
                final String expression;
                expression = bandName + " == " + sampleValue;
                targetProduct.addMask(scl_names[i], expression, scl_descriptions[i], scl_colors[i], 0.5);
            }
        }
    }

    private Map<String, BandInfo> updateMaskMap(File masksDir, Map<String, BandInfo> maskMap, final Pattern pattern) throws IOException {
        //todo use pattern from specification
        final FilenameFilter usedFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        };
        File[] files = masksDir.listFiles(usedFilter);
        if (files != null) {
            for (File file : files) {
                putMasks(pattern, maskMap, file);
            }
        }
        return maskMap;
    }

    private void putMasks(Pattern pattern, Map<String, BandInfo> maskMap, File file) {
        Matcher matcher = pattern.matcher(file.getName());
        if (matcher.matches()) {
            final String tileIndex = matcher.group(4);
            String bandName = matcher.group(3);
            BandInfo bandInfo = new BandInfo(tileIndex, file, bandName, null, S2SpatialResolution.R60M, true);
            maskMap.put(bandName, bandInfo);
        }
    }

}
