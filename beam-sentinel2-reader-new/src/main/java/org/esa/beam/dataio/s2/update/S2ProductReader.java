package org.esa.beam.dataio.s2.update;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.TiePointGeoCoding;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.StringUtils;
import org.esa.beam.util.io.FileUtils;
import org.jdom.JDOMException;

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

import static org.esa.beam.dataio.s2.update.L1cMetadata.parseHeader;
import static org.esa.beam.dataio.s2.update.S2Config.TILE_LAYOUTS;

/**
 * @author Tonio Fincke
 * @author Norman Fomferra
 */
public abstract class S2ProductReader extends AbstractProductReader {

//    final static String metadataName1CRegex =
//            "((S2.?)_([A-Z]{4})_MTD_(DMP|SAF)(L1C)_R([0-9]{3})_V([0-9]{8})T([0-9]{6})_([0-9]{8})T([0-9]{6})_C([0-9]{3}).*.xml|Product_Metadata_File.xml)";
//    final static Pattern metadataName1CPattern = Pattern.compile(metadataName1CRegex);
//    final static Pattern metadataName2APattern = Pattern.compile("S2.?_([A-Z]{4})_MTD_(DMP|SAF)(L2A)_.*.xml");
//    final static Pattern metadataNameTilePattern = Pattern.compile("S2.?_([A-Z]{4})_([A-Z]{3})_(L1C|L2A)_TL_.*");


    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be <code>null</code> for internal reader
     *                     implementations
     */
    protected S2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }
        if (S2Config.METADATA_NAME_1C_PATTERN.matcher(inputFile.getName()).matches() ||
                S2Config.METADATA_NAME_1C_PATTERN_ALT.matcher(inputFile.getName()).matches()) {
            return readProductNodes(inputFile);
        } else if (S2Config.METADATA_NAME_2A_PATTERN.matcher(inputFile.getName()).matches()) {
            return readProductNodes(inputFile);
        } else if (S2Config.METADATA_NAME_1C_TILE_PATTERN.matcher(inputFile.getName()).matches() ||
                S2Config.METADATA_NAME_2A_TILE_PATTERN.matcher(inputFile.getName()).matches()) {
            return readSingleTile(inputFile, "");
        } else {
            throw new IOException("Unhandled file type.");
        }
    }

    public abstract Product readProductNodes(File inputFile) throws IOException;

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY, Band destBand, int destOffsetX,
                                          int destOffsetY, int destWidth, int destHeight, ProductData destBuffer,
                                          ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

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
                return S2Config.IMAGE_NAME_PATTERN.matcher(name).matches();
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
                    final Matcher matcher = S2Config.IMAGE_NAME_PATTERN.matcher(file.getName());
                    if (matcher.matches()) {
                        final String tileIndex = matcher.group(4);
                        String bandName = matcher.group(5);
                        bandName = trimUnderscores(bandName);
                        bandInfoMap.put(bandName, getBandInfo(file, matcher, tileIndex, bandName));
                    }
                }
            }
        }
        return bandInfoMap;
    }

    protected abstract BandInfo getBandInfo(File file, Matcher matcher, String tileIndex, String bandIndex);

    protected Product readSingleTile(File tileFile, String productName) throws IOException {
        //todo check whether L1C metadata is identical to L2A metadata
        L1cMetadata metadata = null;
        final String filePath;
        if (!tileFile.isDirectory()) {
            try {
                metadata = parseHeader(tileFile);
            } catch (JDOMException e) {
                e.printStackTrace();
            }
            filePath = tileFile.getParent();
            if(StringUtils.isNullOrEmpty(productName)) {
                productName = FileUtils.getFilenameWithoutExtension(tileFile);
            }
        } else {
            final String dirName = tileFile.getName();
            File metadataFile = new File(tileFile.getAbsolutePath() + "/" + dirName + ".xml");
            try {
                metadata = parseHeader(metadataFile);
            } catch (JDOMException e) {
                e.printStackTrace();
            }
            filePath = tileFile.getPath();
            if(StringUtils.isNullOrEmpty(productName)) {
                productName = tileFile.getName();
            }
        }
        Map<String, BandInfo> bandInfoMap = getBandInfoMap(filePath);
        final int width = TILE_LAYOUTS[S2SpatialResolution.R10M.id].width;
        final int height = TILE_LAYOUTS[S2SpatialResolution.R10M.id].height;
        Product product = new Product(productName,
                                      "S2_MSI_" + getProductType(),
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
        readMasks(product, filePath);
        return product;
    }

    public abstract String getProductType();

    private void addTiePointGrid(int width, int height, Product product, String gridName, float[] tiePoints) {
        final TiePointGrid latitudeGrid = createTiePointGrid(gridName, 2, 2, 0, 0, width, height, tiePoints);
        product.addTiePointGrid(latitudeGrid);
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

    public abstract void readMasks(Product product, String path) throws IOException;

    public void addBands(Product product, Map<String, BandInfo> bandInfoMap, MultiLevelImageFactory mlif) throws IOException {
        product.setPreferredTileSize(S2Config.DEFAULT_JAI_TILE_SIZE, S2Config.DEFAULT_JAI_TILE_SIZE);
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
    }

    private Band addBand(Product product, BandInfo bandInfo) {
        final Band band = product.addBand(bandInfo.bandName, S2Config.SAMPLE_PRODUCT_DATA_TYPE);

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
        return band;
    }

    private void setValidPixelMask(Band band, String bandName) {
        band.setNoDataValue(0);
        band.setValidPixelExpression(String.format("%s.raw > %s",
                                                   bandName, S2Config.RAW_NO_DATA_THRESHOLD));
    }

    protected String createProductNameFromValidMetadataName(String metadataName){
        final String filenameWithoutExtension = FileUtils.getFilenameWithoutExtension(metadataName);
        final String productNameWithoutExtension = filenameWithoutExtension.replace("MTD", "PRD").replace("SAF", "MSI").replace("DMP", "MSI");
        if(metadataName.contains("SAF")) {
            return productNameWithoutExtension + ".SAFE";
        } else {
            return productNameWithoutExtension + ".DIMAP";
        }
    }

}
