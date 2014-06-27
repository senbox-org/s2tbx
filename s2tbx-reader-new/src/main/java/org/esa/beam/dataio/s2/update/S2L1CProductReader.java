package org.esa.beam.dataio.s2.update;

import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Product;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;


/**
 * Represents information of a Sentinel 2 band
 *
 * @author Tonio Fincke
 * @author Norman Fomferra
 */
public class S2L1CProductReader extends S2ProductReader {

    static S2SpatialResolution[] resolutions = {S2SpatialResolution.R60M, S2SpatialResolution.R10M,
            S2SpatialResolution.R10M, S2SpatialResolution.R10M, S2SpatialResolution.R20M, S2SpatialResolution.R20M,
            S2SpatialResolution.R20M, S2SpatialResolution.R10M, S2SpatialResolution.R20M, S2SpatialResolution.R60M,
            S2SpatialResolution.R60M, S2SpatialResolution.R20M, S2SpatialResolution.R20M};

    static final String productType = "L1C";

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be <code>null</code> for internal reader
     *                     implementations
     */
    protected S2L1CProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    public BandInfo getBandInfo(File file, String tileIndex, String bandName, int resolution) {
        //todo read width and height from jpeg file
        try {
            final FileImageInputStream inputStream = new FileImageInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        S2WavebandInfo wavebandInfo = S2WaveBandInfoProvider.getWaveBandInfo(bandName);
        return new BandInfo(tileIndex, file, bandName, wavebandInfo, resolutions[wavebandInfo.bandId], false);
    }

    @Override
    public String getProductType() {
        return productType;
    }

    @Override
    public void readMasks(Product product, String granulePath) {
        // do nothing...not yet, at least
    }

    @Override
    public Product readProductNodes(File metadataFile) throws IOException {
        //todo read metadata
        final String parentDirectory = metadataFile.getParent();
        final File granuleDirectory = new File(parentDirectory + "/GRANULE");
        final File[] granules = granuleDirectory.listFiles();
        String productName;
        if(S2Config.METADATA_NAME_1C_PATTERN.matcher(metadataFile.getName()).matches()) {
            productName = createProductNameFromValidMetadataName(metadataFile.getName());
        } else {
            productName = metadataFile.getParentFile().getName();
        }
        if (granules != null) {
            if (granules.length > 1) {
                //todo how to align multiple tiles -> tile consolidation?
            } else if (granules.length == 1) {
                return readSingleTile(granules[0], productName);
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

    Map<String, BandInfo> getBandInfoMap(String filePath) throws IOException {
        final String imageDataPath = filePath + "/IMG_DATA";
        final File productDir = new File(imageDataPath);
        final FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return S2Config.IMAGE_NAME_PATTERN_1C.matcher(name).matches();
            }
        };
        File[] files  = productDir.listFiles(filter);
        Map<String, BandInfo> bandInfoMap = new HashMap<String, BandInfo>();
        putFilesIntoBandInfoMap(bandInfoMap, files);
        return bandInfoMap;
    }

    protected void putFilesIntoBandInfoMap(Map<String, BandInfo> bandInfoMap, File[] files) {
        if (files != null) {
            for (File file : files) {
                final Matcher matcher = S2Config.IMAGE_NAME_PATTERN_1C.matcher(file.getName());
                if (matcher.matches()) {
                    final String tileIndex = matcher.group(3);
                    String bandName = matcher.group(4);
                    bandInfoMap.put(bandName, getBandInfo(file, tileIndex, bandName, -1));
                }
            }
        }
    }

}
