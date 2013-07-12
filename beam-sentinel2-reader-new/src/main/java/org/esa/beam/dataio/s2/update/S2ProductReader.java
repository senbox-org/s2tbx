package org.esa.beam.dataio.s2.update;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.TiePointGeoCoding;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.util.io.FileUtils;
import org.jdom.JDOMException;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.esa.beam.dataio.s2.update.L1cMetadata.parseHeader;
import static org.esa.beam.dataio.s2.update.S2Config.TILE_LAYOUTS;

/**
 * @author Tonio Fincke
 * @author Norman Fomferra
 */
public class S2ProductReader extends AbstractProductReader {

    /**
     * Only used, if metadata header (manifest file) is not found.
     */
    private final Map<String, S2WavebandInfo> s2_waveband_infos;

    int SAMPLE_PRODUCT_DATA_TYPE = ProductData.TYPE_UINT16;

    static class BandInfo {
        final Map<String, File> tileIdToFileMap;
        final int bandIndex;
        String bandName;
        final S2WavebandInfo wavebandInfo;
        final S2SpatialResolution resolution;
        final TileLayout imageLayout;

        BandInfo(Map<String, File> tileIdToFileMap, int bandIndex, S2WavebandInfo wavebandInfo,
                 S2SpatialResolution resolution) {
            this.tileIdToFileMap = Collections.unmodifiableMap(tileIdToFileMap);
            this.bandIndex = bandIndex;
            this.bandName = bandindexToBandname.get(bandIndex);
            this.wavebandInfo = wavebandInfo;
            this.imageLayout = TILE_LAYOUTS[resolution.id];
            this.resolution = resolution;
        }
    }

    final static String metadataName1CRegex =
            "(S2.?)_([A-Z]{4})_MTD_(DMP|SAF)(L1C)_R([0-9]{3})_V([0-9]{8})T([0-9]{6})_([0-9]{8})T([0-9]{6})_C([0-9]{3}).*.xml";
    final static Pattern metadataName1CPattern = Pattern.compile(metadataName1CRegex);
    final static Pattern metadataName2APattern = Pattern.compile("S2.?_([A-Z]{4})_MTD_(DMP|SAF)(L2A)_.*.xml");
    final static Pattern metadataNameTilePattern = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_(L1C|L2A)_TL_.*");

    File cacheDir;
    int DEFAULT_JAI_TILE_SIZE = 512;

    Map<String, Integer> bandnameToBandindex = new HashMap<String, Integer>();
    static Map<Integer, String> bandindexToBandname = new HashMap<Integer, String>();
    static S2SpatialResolution[] resolutions = {S2SpatialResolution.R60M, S2SpatialResolution.R10M,
            S2SpatialResolution.R10M, S2SpatialResolution.R10M, S2SpatialResolution.R20M, S2SpatialResolution.R20M,
            S2SpatialResolution.R20M, S2SpatialResolution.R10M, S2SpatialResolution.R20M, S2SpatialResolution.R60M,
            S2SpatialResolution.R60M, S2SpatialResolution.R20M, S2SpatialResolution.R20M};

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be <code>null</code> for internal reader
     *                     implementations
     */
    protected S2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        s2_waveband_infos = new HashMap<String, S2WavebandInfo>();
        s2_waveband_infos.put("B01", new S2WavebandInfo(0, 443, 20, 1895.27, 3413, 1.030577302));
        s2_waveband_infos.put("B02", new S2WavebandInfo(1, 490, 65, 1962.16, 3413, 1.030577302));
        s2_waveband_infos.put("B03", new S2WavebandInfo(2, 560, 35, 1822.88, 3413, 1.030577302));
        s2_waveband_infos.put("B04", new S2WavebandInfo(3, 665, 30, 1511.88, 3413, 1.030577302));
        s2_waveband_infos.put("B05", new S2WavebandInfo(4, 705, 15, 1420.58, 3413, 1.030577302));
        s2_waveband_infos.put("B06", new S2WavebandInfo(5, 740, 15, 1292.17, 3413, 1.030577302));
        s2_waveband_infos.put("B07", new S2WavebandInfo(6, 775, 20, 1165.87, 3413, 1.030577302));
        s2_waveband_infos.put("B08", new S2WavebandInfo(7, 842, 115, 1037.44, 3413, 1.030577302));
        s2_waveband_infos.put("B8A", new S2WavebandInfo(8, 865, 20, 959.53, 3413, 1.030577302));
        s2_waveband_infos.put("B09", new S2WavebandInfo(9, 940, 20, 814.1, 3413, 1.030577302));
        s2_waveband_infos.put("B10", new S2WavebandInfo(10, 1380, 30, 363.67, 3413, 1.030577302));
        s2_waveband_infos.put("B11", new S2WavebandInfo(11, 1610, 90, 246.28, 3413, 1.030577302));
        s2_waveband_infos.put("B12", new S2WavebandInfo(12, 2190, 180, 86.98, 3413, 1.030577302));
        bandnameToBandindex.put("", 13);
        bandnameToBandindex.put("AOT", 14);
        bandnameToBandindex.put("DEM", 15);
        bandnameToBandindex.put("WVP", 16);
        bandindexToBandname.put(0, "B1");
        bandindexToBandname.put(1, "B2");
        bandindexToBandname.put(2, "B3");
        bandindexToBandname.put(3, "B4");
        bandindexToBandname.put(4, "B5");
        bandindexToBandname.put(5, "B6");
        bandindexToBandname.put(6, "B7");
        bandindexToBandname.put(7, "B8");
        bandindexToBandname.put(8, "B8a");
        bandindexToBandname.put(9, "B9");
        bandindexToBandname.put(10, "B10");
        bandindexToBandname.put(11, "B11");
        bandindexToBandname.put(12, "B12");
        bandindexToBandname.put(13, "xxx");
        bandindexToBandname.put(14, "AOT");
        bandindexToBandname.put(15, "DEM");
        bandindexToBandname.put(16, "WVP");
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }
        if (metadataName1CPattern.matcher(inputFile.getName()).matches()) {
            return readL1CProductNodes(inputFile);
        } else if (metadataName2APattern.matcher(inputFile.getName()).matches()) {
            return readL2AProductNodes(inputFile);
        } else {
            final Matcher metadataNameTilePatternMatcher = metadataNameTilePattern.matcher(inputFile.getName());
            if (metadataNameTilePatternMatcher.matches()) {
                return readSingleTile(inputFile, metadataNameTilePatternMatcher.group(3));
                //            return readSingleL1CTile(inputFile);
                //        } else if (metadataNameTilePattern.matcher(inputFile.getName()).matches()) {
                //            return readSingleTile(inputFile);
                //            return readSingleL2ATile(inputFile);
            } else {
                throw new IOException("Unhandled file type.");
            }
        }
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY, Band destBand, int destOffsetX,
                                          int destOffsetY, int destWidth, int destHeight, ProductData destBuffer,
                                          ProgressMonitor pm) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Map<String, BandInfo> getBandInfoMap(String filePath, String productType) throws IOException {

        final String imageDataPath = filePath + "\\IMG_DATA";
        final File productDir = new File(imageDataPath);

        final String atmCorrPath60 = imageDataPath + "\\Atmospheric_Correction_Tiles\\Bands_60m";
        final String atmCorrPath20 = imageDataPath + "\\Atmospheric_Correction_Tiles\\Bands_20m";
        final String atmCorrPath10 = imageDataPath + "\\Atmospheric_Correction_Tiles\\Bands_10m";
        final File atmCorrDir60 = new File(atmCorrPath60);
        final File atmCorrDir20 = new File(atmCorrPath20);
        final File atmCorrDir10 = new File(atmCorrPath10);

        initCacheDir(productDir);

        final Pattern imageNamePattern =
                Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_(L2A|L1C)_TL_.*_(\\d{2}[A-Z]{3})(|_AOT_|_WVP_|_DEM_|_B[0-9A]{2})(_([1-6]{1}0)m)?.jp2");

        final FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return imageNamePattern.matcher(name).matches();
            }
        };
        File[][] filesMatrix = new File[4][];
        filesMatrix[0] = productDir.listFiles(filter);
        if (atmCorrDir60.isDirectory()) {
            filesMatrix[1] = atmCorrDir60.listFiles(filter);
        }
        if (atmCorrDir20.isDirectory()) {
            filesMatrix[2] = atmCorrDir20.listFiles(filter);
        }
        if (atmCorrDir10.isDirectory()) {
            filesMatrix[3] = atmCorrDir10.listFiles(filter);
        }
        Map<String, BandInfo> bandInfoMap = new HashMap<String, BandInfo>();
        for (File[] files : filesMatrix) {
            if (files != null) {
                for (File file : files) {
                    final Matcher matcher = imageNamePattern.matcher(file.getName());
                    if (matcher.matches()) {
//                        productType = matcher.group(3);
                        final String tileIndex = matcher.group(4);
                        String bandIndex = matcher.group(5);
                        bandIndex = trimUnderscores(bandIndex);
                        BandInfo bandInfo = null;
                        if (productType.equals("L1C")) {
                            bandInfo = getL1CBandInfo(file, tileIndex, bandIndex);
                        } else {
                            bandInfo = getL2ABandInfo(file, matcher, tileIndex, bandIndex);
                        }
                        bandInfoMap.put(bandIndex, bandInfo);
                    }
                }
            }
        }
        return bandInfoMap;
    }

    private Product readSingleTile(File inputFile, String productType) throws IOException {
        //todo check whether L1C metadata is identical to L2A metadata
        L1cMetadata metadata = null;
        final String filePath;
        String filenameWithoutExtension = "";
        if (!inputFile.isDirectory()) {
            try {
                metadata = parseHeader(inputFile);
            } catch (JDOMException e) {
                e.printStackTrace();
            }
            filePath = inputFile.getParent();
            filenameWithoutExtension = FileUtils.getFilenameWithoutExtension(inputFile);
        } else {
            filePath = inputFile.getPath();
            filenameWithoutExtension = inputFile.getName();
        }
        Map<String, BandInfo> bandInfoMap = getBandInfoMap(filePath, productType);
        final int width = TILE_LAYOUTS[S2SpatialResolution.R10M.id].width;
        final int height = TILE_LAYOUTS[S2SpatialResolution.R10M.id].height;
        //todo decide on L1C or L2A
        Product product = new Product(filenameWithoutExtension,
                                      "S2_MSI_" + productType,
                                      width,
                                      height);
        if (metadata != null) {
            addTiePointGrid(width, height, product, "latitude", metadata.getCornerLatitudes());
            addTiePointGrid(width, height, product, "longitude", metadata.getCornerLongitudes());
            addTiePointGrid(width, height, product, "sza", metadata.getSolarZenith());
            addTiePointGrid(width, height, product, "saa", metadata.getSolarAzimuth());
            addTiePointGrid(width, height, product, "vza", metadata.getViewZenith());
            addTiePointGrid(width, height, product, "vaa", metadata.getViewAzimuth());
            GeoCoding tiePointGeocoding = new TiePointGeoCoding(product.getTiePointGrid("latitude"),
                                                                product.getTiePointGrid("longitude"));
            product.setGeoCoding(tiePointGeocoding);
        }
        addBands(product, bandInfoMap, new TileMultiLevelImageFactory(ImageManager.getImageToModelTransform(product.getGeoCoding())));
        return product;
    }

    private BandInfo getL2ABandInfo(File file, Matcher matcher, String tileIndex, String bandIndex) {
        BandInfo bandInfo;
        S2WavebandInfo wavebandInfo = null;
        int resolution = Integer.parseInt(matcher.group(7));
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
        if (s2_waveband_infos.containsKey(bandIndex)) {
            wavebandInfo = s2_waveband_infos.get(bandIndex);
            bandInfo = createBandInfoFromDefaults(wavebandInfo.bandId, wavebandInfo,
                                                  tileIndex,
                                                  file,
                                                  spatialResolution
            );
        } else {
            bandInfo = createBandInfoFromDefaults(bandnameToBandindex.get(bandIndex), null, tileIndex, file, spatialResolution);
        }
        return bandInfo;
    }

    private BandInfo getL1CBandInfo(File file, String tileIndex, String bandIndex) {
        BandInfo bandInfo;
        S2WavebandInfo wavebandInfo = s2_waveband_infos.get(bandIndex);
        bandInfo = createBandInfoFromDefaults(wavebandInfo.bandId, wavebandInfo,
                                              tileIndex,
                                              file,
                                              resolutions[wavebandInfo.bandId]);
        return bandInfo;
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

    private void addTiePointGrid(int width, int height, Product product, String gridName, float[] tiePoints) {
        final TiePointGrid latitudeGrid = createTiePointGrid(gridName, 2, 2, 0, 0, width, height, tiePoints);
        product.addTiePointGrid(latitudeGrid);
    }

    private void addBands(Product product, Map<String, BandInfo> bandInfoMap, MultiLevelImageFactory mlif) throws IOException {
        product.setPreferredTileSize(DEFAULT_JAI_TILE_SIZE, DEFAULT_JAI_TILE_SIZE);
        product.setNumResolutionsMax(TILE_LAYOUTS[0].numResolutions);
        product.setAutoGrouping("reflec:radiance:sun:view");

        List<String> bandIndexes = new ArrayList<String>(bandInfoMap.keySet());
        Collections.sort(bandIndexes);

        if (bandIndexes.isEmpty()) {
            throw new IOException("No valid bands found.");
        }

        for (String bandIndex : bandIndexes) {
            BandInfo bandInfo = bandInfoMap.get(bandIndex);
            Band band = addBand(product, bandInfo);
            band.setSourceImage(mlif.createSourceImage(bandInfo));
        }

        // todo - S2 spec is unclear about the use of this variable "Resample_Data/Reflectance_Conversion/U"
        // todo - Uncomment setting the spectral properties as soon as we have groups in VISAT's spectrum view

        // For testing - add TOA reflectance bands
        for (String bandIndex : bandIndexes) {
            BandInfo bandInfo = bandInfoMap.get(bandIndex);
            if (bandInfo.wavebandInfo != null) {
                Band reflec = product.addBand(bandInfo.bandName.replace("B", "reflec_"),
                                              bandInfo.bandName
                                                      + " * (" + bandInfo.wavebandInfo.reflecUnit
                                                      + " / " + bandInfo.wavebandInfo.quantificationValue + ")");
                reflec.setSpectralBandIndex(bandInfo.bandIndex);
                reflec.setSpectralWavelength((float) bandInfo.wavebandInfo.wavelength);
                reflec.setValidPixelExpression(bandInfo.bandName + ".raw > ");
                reflec.setDescription("TOA reflectance in " + bandInfo.bandName + " for demonstration purpose");
                setValidPixelMask(reflec, bandInfo.bandName);
            }
        }

        // For testing - add TOA radiance bands
        for (String bandIndex : bandIndexes) {
            BandInfo bandInfo = bandInfoMap.get(bandIndex);
            if (bandInfo.wavebandInfo != null) {
                Band radiance = product.addBand(bandInfo.bandName.replace("B", "radiance_"),
                                                bandInfo.bandName
                                                        + " * (" + bandInfo.wavebandInfo.solarIrradiance
                                                        + " * " + bandInfo.wavebandInfo.reflecUnit
                                                        + " / " + bandInfo.wavebandInfo.quantificationValue + ")");
                radiance.setSpectralBandIndex(bandInfo.bandIndex);
                radiance.setSpectralWavelength((float) bandInfo.wavebandInfo.wavelength);
                radiance.setDescription("TOA radiance in " + bandInfo.bandName + " for demonstration purpose");
                setValidPixelMask(radiance, bandInfo.bandName);
            }
        }

        Band ndvi = product.addBand("toa_ndvi", "(reflec_4 - reflec_9) / (reflec_4 + reflec_9)");
        ndvi.setDescription("Top-of-atmosphere NDVI for demonstration purpose");
        ndvi.setValidPixelExpression(String.format("B4.raw > %s and B4.raw > %s",
                                                   S2Config.RAW_NO_DATA_THRESHOLD,
                                                   S2Config.RAW_NO_DATA_THRESHOLD));
    }

    private void setValidPixelMask(Band band, String bandName) {
        band.setNoDataValue(0);
        band.setValidPixelExpression(String.format("%s.raw > %s",
                                                   bandName, S2Config.RAW_NO_DATA_THRESHOLD));
    }

    private Band addBand(Product product, BandInfo bandInfo) {
        final Band band = product.addBand(bandInfo.bandName, SAMPLE_PRODUCT_DATA_TYPE);

        if (bandInfo.wavebandInfo != null) {
            band.setSpectralBandIndex(bandInfo.bandIndex);
            band.setSpectralWavelength((float) bandInfo.wavebandInfo.wavelength);
            band.setSpectralBandwidth((float) bandInfo.wavebandInfo.bandwidth);
            band.setSolarFlux((float) bandInfo.wavebandInfo.solarIrradiance);

            setValidPixelMask(band, bandInfo.bandName);
        }
        // todo - We don't use the scaling factor because we want to stay with 16bit unsigned short samples due to the large
        // amounts of data when saving the images. We provide virtual reflectance bands for this reason. We can use the
        // scaling factor again, once we have product writer parameters, so that users can decide to write data as
        // 16bit samples.
        //
        //band.setScalingFactor(bandInfo.wavebandInfo.scalingFactor);

        return band;
    }

    private BandInfo createBandInfoFromDefaults(int bandIndex, S2WavebandInfo wavebandInfo, String tileId, File imageFile, S2SpatialResolution resolution) {
        return new BandInfo(createFileMap(tileId, imageFile),
                            bandIndex,
                            wavebandInfo,
                            resolution);
    }

    private static Map<String, File> createFileMap(String tileId, File imageFile) {
        Map<String, File> tileIdToFileMap = new HashMap<String, File>();
        tileIdToFileMap.put(tileId, imageFile);
        return tileIdToFileMap;
    }

    private Product readL1CProductNodes(File metadataFile) throws IOException {
        //todo read metadata
        final String parentDirectory = metadataFile.getParent();
        final File granuleDirectory = new File(parentDirectory + "\\GRANULE");
        final File[] granules = granuleDirectory.listFiles();
        if (granules != null) {
            if (granules.length > 1) {
                //todo how to align multiple tiles -> tile consolidation?
            } else if (granules.length == 1) {
                return readSingleTile(granules[0], "L1C");
//            final Matcher matcher = metadataName1CPattern.matcher(metadataFile.getName());
//            if (matcher.matches()) {
//                final String regex = matcher.group(1) + "_" + matcher.group(2) + "_([A-Z]{3})_" + matcher.group(4)
//                        + "_TL_([A-Z0-9]{4})" + matcher.group(6) + "T" + matcher.group(7) + "_" + matcher.group(5) +
//                        "_[0-9]{2}[A-Z]{3}";
//                final Pattern directoryPattern = Pattern.compile(regex);
//                for (File granule : granules) {
//                    if (granule.isDirectory() && directoryPattern.matcher(granule.getName()).matches()) {
//                        File tileMetadataFile = new File(granule.getPath() + "\\" + granule.getName() + ".xml");
//                        L1cMetadata tileMetadata;
//                        try {
//                            tileMetadata = L1cMetadata.parseHeader(tileMetadataFile);
//                        } catch (JDOMException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
            }
        }
        return null;
    }

    private Product readL2AProductNodes(File inputFile) throws IOException {
        //todo read metadata
        String filenameWithoutExtension = "";
        if (!inputFile.isDirectory()) {
            filenameWithoutExtension = FileUtils.getFilenameWithoutExtension(inputFile);
        } else {
            filenameWithoutExtension = inputFile.getName();
        }
        final int width = TILE_LAYOUTS[S2SpatialResolution.R10M.id].width;
        final int height = TILE_LAYOUTS[S2SpatialResolution.R10M.id].height;
        Product product = new Product(filenameWithoutExtension,
                                      "S2_MSI_L2A",
                                      width,
                                      height);
        final String parentDirectory = inputFile.getParent();
        final File granuleDirectory = new File(parentDirectory + "\\GRANULE");
        final File[] granules = granuleDirectory.listFiles();
        if (granules != null) {
            //todo read all granules
//            for (File granule : granules) {
                final Map<String, BandInfo> bandInfoMap = getBandInfoMap(granules[0].getPath(), "L2A");
                addBands(product, bandInfoMap, new TileMultiLevelImageFactory(ImageManager.getImageToModelTransform(product.getGeoCoding())));
//            }
        }
        return product;
    }

    void initCacheDir(File productDir) throws IOException {
        cacheDir = new File(new File(SystemUtils.getApplicationDataDir(), "beam-sentinel2-reader/cache"),
                            productDir.getName());
        //noinspection ResultOfMethodCallIgnored
        cacheDir.mkdirs();
        if (!cacheDir.exists() || !cacheDir.isDirectory() || !cacheDir.canWrite()) {
            throw new IOException("Can't access package cache directory");
        }
    }

    private abstract class MultiLevelImageFactory {
        protected final AffineTransform imageToModelTransform;

        protected MultiLevelImageFactory(AffineTransform imageToModelTransform) {
            this.imageToModelTransform = imageToModelTransform;
        }

        public abstract MultiLevelImage createSourceImage(BandInfo bandInfo);
    }

    private class TileMultiLevelImageFactory extends MultiLevelImageFactory {
        private TileMultiLevelImageFactory(AffineTransform imageToModelTransform) {
            super(imageToModelTransform);
        }

        public MultiLevelImage createSourceImage(BandInfo bandInfo) {
            return new DefaultMultiLevelImage(new TileMultiLevelSource(bandInfo, imageToModelTransform));
        }
    }

    private class TileMultiLevelSource extends AbstractMultiLevelSource {
        final BandInfo bandInfo;

        public TileMultiLevelSource(BandInfo bandInfo, AffineTransform imageToModelTransform) {
            super(new DefaultMultiLevelModel(bandInfo.imageLayout.numResolutions,
                                             imageToModelTransform,
                                             TILE_LAYOUTS[0].width,
                                             TILE_LAYOUTS[0].height));
            this.bandInfo = bandInfo;
        }

        @Override
        protected RenderedImage createImage(int level) {
            File imageFile = bandInfo.tileIdToFileMap.values().iterator().next();
            return L1cTileOpImage.create(imageFile,
                                         cacheDir,
                                         null,
                                         bandInfo.imageLayout,
                                         getModel(),
                                         bandInfo.resolution,
                                         level);
        }

    }


}
