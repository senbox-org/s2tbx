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
        String filenameWithoutExtension = "";
        if(S2Config.METADATA_NAME_2A_PATTERN.matcher(metadataFile.getName()).matches()) {
            filenameWithoutExtension = createProductNameFromValidMetadataName(metadataFile.getName());
        } else {
            filenameWithoutExtension = metadataFile.getName();
        }
        final int width = TILE_LAYOUTS[S2SpatialResolution.R10M.id].width;
        final int height = TILE_LAYOUTS[S2SpatialResolution.R10M.id].height;
        Product product = new Product(filenameWithoutExtension,
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
        return product;
    }

    @Override
    public BandInfo getBandInfo(File file, Matcher matcher, String tileIndex, String bandIndex) {
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
        if (S2WaveBandInfoProvider.hasWaveBandInfo(bandIndex)) {
            wavebandInfo = S2WaveBandInfoProvider.getWaveBandInfo(bandIndex);
            bandInfo = new BandInfo(tileIndex, file, wavebandInfo.bandId, wavebandInfo, spatialResolution);
        } else {
            bandInfo = new BandInfo(tileIndex, file,
                                    bandIndex,
                                    wavebandInfo,
                                    spatialResolution);
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
        if(qiDataPath.isDirectory()) {
            maskMap = updateMaskMap(qiDataPath, maskMap);
            File l2aQualityMasksPath = new File(qiDataPath.getPath() + "/L2A_Quality_Masks");
            if(l2aQualityMasksPath.isDirectory()) {
                maskMap = updateMaskMap(l2aQualityMasksPath, maskMap);
            }
        }
        addBands(product, maskMap, new TileMultiLevelImageFactory(ImageManager.getImageToModelTransform(product.getGeoCoding())));
        setMasks(product);
    }

    protected void setMasks(Product targetProduct) {
        final Band[] bands = targetProduct.getBands();
        for (Band band : bands) {
            final SampleCoding sampleCoding = band.getSampleCoding();
            if (sampleCoding != null) {
                final String bandName = band.getName();
                final boolean flagBand = band.isFlagBand();
                for (int i = 0; i < sampleCoding.getNumAttributes(); i++) {
                    final String sampleName = sampleCoding.getSampleName(i);
                    final int sampleValue = sampleCoding.getSampleValue(i);
                    if (!"spare".equals(sampleName)) {
                        final String expression;
                        if (flagBand) {
                            expression = bandName + " & " + sampleValue + " == " + sampleValue;
                        } else {
                            expression = bandName + " == " + sampleValue;
                        }
                        final String maskName = bandName + "_" + sampleName;
                        targetProduct.addMask(maskName, expression, expression, Color.RED, 0.5);
                    }
                }
            }
        }
    }

    private Map<String, BandInfo> updateMaskMap(File masksDir, Map<String, BandInfo> maskMap) throws IOException {
        //we have two patterns here: The one specified in the specification and the one actually used

        final FilenameFilter specificationFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return S2Config.SPECIFICATION_MASK_IMAGE_NAME_PATTERN.matcher(name).matches();
            }
        };

        final FilenameFilter usedFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return S2Config.USED_MASK_IMAGE_NAME_PATTERN.matcher(name).matches();
            }
        };

        File[][] filesMatrix = new File[2][];
        filesMatrix[0] = masksDir.listFiles(specificationFilter);
        filesMatrix[1] = masksDir.listFiles(usedFilter);

        for (File[] files : filesMatrix) {
            if (files != null) {
                for (File file : files) {
                    putMasks(S2Config.SPECIFICATION_MASK_IMAGE_NAME_PATTERN, maskMap, file);
                    putMasks(S2Config.USED_MASK_IMAGE_NAME_PATTERN, maskMap, file);
                }
            }
        }
        return maskMap;
    }

    private void putMasks(Pattern specificationImageNamePattern, Map<String, BandInfo> maskMap, File file) {
        Matcher matcher = specificationImageNamePattern.matcher(file.getName());
        if (matcher.matches()) {
            final String tileIndex = matcher.group(4);
            String bandName = matcher.group(3);
            BandInfo bandInfo = new BandInfo(tileIndex, file, bandName, null, S2SpatialResolution.R60M);
            maskMap.put(bandName, bandInfo);
        }
    }

}
