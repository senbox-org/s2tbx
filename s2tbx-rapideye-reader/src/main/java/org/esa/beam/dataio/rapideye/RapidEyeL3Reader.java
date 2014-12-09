package org.esa.beam.dataio.rapideye;

import org.esa.beam.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.beam.dataio.readers.GeoTiffBasedReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.TreeNode;

import java.io.File;
import java.io.IOException;

/**
 * Reader for RapidEye L3 (GeoTIFF) products.
 *
 * @author  Cosmin Cara
 */
public class RapidEyeL3Reader extends GeoTiffBasedReader<RapidEyeMetadata> {

    protected RapidEyeL3Reader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected String getMetadataExtension() {
        return RapidEyeConstants.METADATA_FILE_SUFFIX.substring(RapidEyeConstants.METADATA_FILE_SUFFIX.indexOf("."));
    }

    @Override
    protected String getMetadataProfile() {
        return RapidEyeConstants.PROFILE_L3;
    }

    @Override
    protected String getProductGenericName() {
        return RapidEyeConstants.PRODUCT_GENERIC_NAME;
    }

    @Override
    protected String[] getBandNames() {
        return RapidEyeConstants.BAND_NAMES;
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isArchive()) {
            return super.getProductComponents();
        } else {
            RapidEyeMetadata firstMetadata = metadata.get(0);
            TreeNode<File> result = super.getProductComponents();
            String metaFileName = firstMetadata.getFileName();
            try{
                addProductComponentIfNotPresent(metaFileName, productDirectory.getFile(metaFileName), result);
            } catch (IOException e) {
                logger.warning(String.format("Error encountered while searching file %s", metaFileName));
            }
            String[] nitfFiles = firstMetadata.getRasterFileNames(false);
            for(String fileName : nitfFiles){
                try{
                    addProductComponentIfNotPresent(fileName, productDirectory.getFile(fileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", fileName));
                }
            }
            String maskFileName = firstMetadata.getMaskFileName();
            if (maskFileName != null) {
                try{
                    addProductComponentIfNotPresent(maskFileName, productDirectory.getFile(maskFileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", maskFileName));
                }
            }
            return result;
        }
    }
}
