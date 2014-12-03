package org.esa.beam.dataio.deimos;

import org.esa.beam.dataio.deimos.dimap.DeimosConstants;
import org.esa.beam.dataio.deimos.dimap.DeimosMetadata;
import org.esa.beam.dataio.readers.GeotiffBasedReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Mask;
import org.esa.beam.framework.datamodel.Product;

/**
 * Created by kraftek on 9/22/2014.
 */
public class DeimosProductReader extends GeotiffBasedReader<DeimosMetadata> {

    protected DeimosProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected String getMetadataExtension() {
        return DeimosConstants.METADATA_EXTENSION;
    }

    @Override
    protected String getMetadataProfile() {
        if (metadata != null && metadata.size() > 0) {
            return metadata.get(0).getMetadataProfile();
        } else {
            return DeimosConstants.VALUE_NOT_AVAILABLE;
        }
    }

    @Override
    protected String getProductGenericName() {
        if (metadata != null && metadata.size() > 0) {
            return metadata.get(0).getProductName();
        } else {
            return DeimosConstants.VALUE_NOT_AVAILABLE;
        }
    }

    @Override
    protected String[] getBandNames() {
        if (metadata != null && metadata.size() > 0) {
            return metadata.get(0).getBandNames();
        } else {
            return new String[] { };
        }
    }

    @Override
    protected void addMetadataMasks(Product product, DeimosMetadata componentMetadata) {
        logger.info("Create masks");
        int noDataValue,saturatedValue;
        if ((noDataValue = componentMetadata.getNoDataValue()) >= 0) {
            product.getMaskGroup().add(Mask.BandMathsType.create(DeimosConstants.NODATA_VALUE,
                    DeimosConstants.NODATA_VALUE,
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    String.valueOf(noDataValue),
                    componentMetadata.getNoDataColor(),
                    0.5));
        }
        if ((saturatedValue = componentMetadata.getSaturatedPixelValue()) >= 0) {
            product.getMaskGroup().add(Mask.BandMathsType.create(DeimosConstants.SATURATED_VALUE,
                    DeimosConstants.SATURATED_VALUE,
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    String.valueOf(saturatedValue),
                    componentMetadata.getSaturatedColor(),
                    0.5));
        }
    }
}
