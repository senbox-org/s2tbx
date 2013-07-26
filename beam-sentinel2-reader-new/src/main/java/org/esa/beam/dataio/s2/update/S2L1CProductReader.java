package org.esa.beam.dataio.s2.update;

import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Product;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    final static String metadataName1CRegex =
            "(S2.?)_([A-Z]{4})_MTD_(DMP|SAF)(L1C)_R([0-9]{3})_V([0-9]{8})T([0-9]{6})_([0-9]{8})T([0-9]{6})_C([0-9]{3}).*.xml";
    final static Pattern metadataName1CPattern = Pattern.compile(metadataName1CRegex);

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be <code>null</code> for internal reader
     *                     implementations
     */
    protected S2L1CProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    public BandInfo getBandInfo(File file, Matcher matcher, String tileIndex, String bandName) {
        S2WavebandInfo wavebandInfo = S2WaveBandInfoProvider.getWaveBandInfo(bandName);
        return new BandInfo(tileIndex, file, bandName, wavebandInfo, resolutions[wavebandInfo.bandId]);
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
        if(metadataName1CPattern.matcher(metadataFile.getName()).matches()) {
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

}
