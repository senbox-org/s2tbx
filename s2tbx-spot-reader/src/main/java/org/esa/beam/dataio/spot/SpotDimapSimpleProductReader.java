package org.esa.beam.dataio.spot;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.geotiff.GeoTiffProductReader;
import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.spot.dimap.SpotConstants;
import org.esa.beam.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.*;
import org.geotools.metadata.InvalidMetadataException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This rootProduct reader is intended for reading SPOT-1 to SPOT-5 scene files
 * from compressed archive files or from file system.
 * @author Cosmin Cara
 */
public class SpotDimapSimpleProductReader extends SpotProductReader {

    private Product rootProduct;
    private final Map<Band, Band> bandMap;

    protected SpotDimapSimpleProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        bandMap = new HashMap<Band, Band>();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Assert.notNull(metadata, "This reader should be instantiated only by SpotDimapProductReader");
        Assert.argument(metadata.getComponentsMetadata().size() == 1, "Wrong reader for multiple volume components");

        SpotDimapMetadata dimapMetadata = metadata.getComponentMetadata(0);
        if (dimapMetadata.getRasterWidth() > 0 && dimapMetadata.getRasterHeight() > 0) {
            rootProduct = initProduct(dimapMetadata.getRasterWidth(), dimapMetadata.getRasterHeight(), dimapMetadata);
        }
        for (int fileIndex = 0; fileIndex < metadata.getNumComponents(); fileIndex++) {
            addBands(rootProduct, metadata.getComponentMetadata(fileIndex), fileIndex);
            addMasks(rootProduct, metadata.getComponentMetadata(fileIndex));
        }
        rootProduct.setModified(false);

        return rootProduct;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY,
                                          int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY,
                                          Band destBand,
                                          int destOffsetX, int destOffsetY,
                                          int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm) throws IOException {
        //GeoTiffProductReader reader = readerMap.get(destBand);
        Band tiffBand = bandMap.get(destBand);
        ProductReader reader = tiffBand.getProductReader();
        if (reader == null) {
            logger.severe("No reader found for band data");
        } else {
            reader.readBandRasterData(tiffBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
        }
    }

    Product initProduct(int width, int height, SpotDimapMetadata dimapMetadata) {
        rootProduct = new Product(dimapMetadata.getProductName(),
                SpotConstants.DIMAP_FORMAT_NAMES[0],
                width,
                height);
        rootProduct.getMetadataRoot().addElement(dimapMetadata.getRootElement());
        ProductData.UTC centerTime = dimapMetadata.getCenterTime();
        rootProduct.setStartTime(centerTime);
        rootProduct.setEndTime(centerTime);
        rootProduct.setDescription(dimapMetadata.getProductDescription());
        return rootProduct;
    }

    void addBands(Product product, SpotDimapMetadata componentMetadata, int componentIndex) {
        String[] bandNames = componentMetadata.getBandNames();
        String[] bandUnits = componentMetadata.getBandUnits();
        int width, height, currentW, currentH;
        width = product.getSceneRasterWidth();
        height = product.getSceneRasterHeight();
        currentW = componentMetadata.getRasterWidth();
        currentH = componentMetadata.getRasterHeight();
        if (width == currentW && height == currentH) {
            try {
                if (SpotConstants.DIMAP.equals(componentMetadata.getFormatName())) {
                    String[] fileNames = componentMetadata.getRasterFileNames();
                    if (fileNames == null || fileNames.length == 0)
                        throw new InvalidMetadataException("No raster file found in metadata");
                    File rasterFile = productDirectory.getFile(componentMetadata.getPath().toLowerCase().replace(componentMetadata.getFileName().toLowerCase(),
                                                                                                                 fileNames[0].toLowerCase()));
                    GeoTiffProductReader tiffReader = new GeoTiffProductReader(getReaderPlugIn());
                    logger.info("Read product nodes");
                    Product tiffProduct = tiffReader.readProductNodes(rasterFile, null);
                    if (tiffProduct != null) {
                        if (product == null)
                            product = initProduct(tiffProduct.getSceneRasterWidth(), tiffProduct.getSceneRasterHeight(), metadata.getComponentMetadata(0));
                        MetadataElement tiffMetadata = tiffProduct.getMetadataRoot();
                        if (tiffMetadata != null) {
                            XmlMetadata.CopyChildElements(tiffMetadata, product.getMetadataRoot());
                        }
                        tiffProduct.transferGeoCodingTo(product, null);
                        product.setPreferredTileSize(tiffProduct.getPreferredTileSize());

                        int numBands = tiffProduct.getNumBands();
                        String bandPrefix = "";
                        logger.info("Read bands");
                        if (metadata.hasMultipleComponents()) {
                            bandPrefix = "scene_" + String.valueOf(componentIndex) + "_";
                            String groupPattern = "";
                            for (int idx = 0; idx < metadata.getNumComponents(); idx++) {
                                groupPattern += "scene_" + String.valueOf(idx) + ":";
                            }
                            groupPattern = groupPattern.substring(0, groupPattern.length() - 1);
                            product.setAutoGrouping(groupPattern);
                        }
                        for (int idx = 0; idx < numBands; idx++) {
                            Band srcBand = tiffProduct.getBandAt(idx);
                            String bandName = bandPrefix + (idx < bandNames.length ? bandNames[idx] : SpotConstants.DEFAULT_BAND_NAME_PREFIX + idx);
                            Band targetBand = product.addBand(bandName, srcBand.getDataType());
                            targetBand.setNoDataValue(componentMetadata.getNoDataValue() > -1 ? componentMetadata.getNoDataValue() : srcBand.getNoDataValue());
                            targetBand.setNoDataValueUsed((componentMetadata.getNoDataValue() > -1));
                            targetBand.setSpectralWavelength(componentMetadata.getWavelength(idx) > 0 ? componentMetadata.getWavelength(idx) : srcBand.getSpectralWavelength());
                            targetBand.setSpectralBandwidth(componentMetadata.getBandwidth(idx) > 0 ? componentMetadata.getBandwidth(idx) : srcBand.getSpectralBandwidth());
                            targetBand.setScalingFactor(srcBand.getScalingFactor());
                            targetBand.setScalingOffset(srcBand.getScalingOffset());
                            targetBand.setSolarFlux(srcBand.getSolarFlux());
                            targetBand.setUnit(srcBand.getUnit() != null ? srcBand.getUnit() : bandUnits[idx]);
                            targetBand.setSampleCoding(srcBand.getSampleCoding());
                            targetBand.setImageInfo(srcBand.getImageInfo());
                            targetBand.setSpectralBandIndex(srcBand.getSpectralBandIndex());
                            targetBand.setDescription(bandName);
                            readBandStatistics(targetBand, idx, componentMetadata);
                            bandMap.put(targetBand, srcBand);
                        }
                    }
                } else {
                    logger.warning(String.format("Component product %s is not in DIMAP format!", componentMetadata.getProductName()));
                }
            } catch (IOException ioEx) {
                logger.severe("Error while reading component: " + ioEx.getMessage());
            }
        } else {
            logger.warning(String.format("Cannot add component product %s due to raster size [Found: %d x %d pixels, Expected: %d x %d pixels]",
                    componentMetadata.getProductName(), currentW, currentH, width, height));
        }
    }

    void addMasks(Product product, SpotDimapMetadata componentMetadata) {
        logger.info("Create masks");
        int noDataValue,saturatedValue;
        if ((noDataValue = componentMetadata.getNoDataValue()) >= 0) {
            product.getMaskGroup().add(Mask.BandMathsType.create(SpotConstants.NODATA_VALUE,
                    SpotConstants.NODATA_VALUE,
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    String.valueOf(noDataValue),
                    componentMetadata.getNoDataColor(),
                    0.5));
        }
        if ((saturatedValue = componentMetadata.getSaturatedPixelValue()) >= 0) {
            product.getMaskGroup().add(Mask.BandMathsType.create(SpotConstants.SATURATED_VALUE,
                    SpotConstants.SATURATED_VALUE,
                    product.getSceneRasterWidth(),
                    product.getSceneRasterHeight(),
                    String.valueOf(saturatedValue),
                    componentMetadata.getSaturatedColor(),
                    0.5));
        }
    }
}
