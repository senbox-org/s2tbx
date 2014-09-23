package org.esa.beam.dataio.deimos;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.deimos.dimap.DeimosConstants;
import org.esa.beam.dataio.deimos.dimap.DeimosMetadata;
import org.esa.beam.dataio.deimos.internal.DeimosVirtualDir;
import org.esa.beam.dataio.geotiff.GeoTiffProductReader;
import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.metadata.XmlMetadataParserFactory;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by kraftek on 9/22/2014.
 */
public class DeimosProductReader extends AbstractProductReader {

    private DeimosVirtualDir productDirectory;
    private final List<DeimosMetadata> metadataList;
    private Map<Band, Band> bandMap;
    private final Logger logger;

    static {
        XmlMetadataParserFactory.registerParser(DeimosMetadata.class, new DeimosMetadata.DeimosMetadataParser(DeimosMetadata.class));
    }


    protected DeimosProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        logger = BeamLogManager.getSystemLogger();
        metadataList = new ArrayList<DeimosMetadata>();
        bandMap = new HashMap<Band, Band>();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        productDirectory = DeimosProductReaderPlugin.getInput(getInput());
        String[] metadataFiles = productDirectory.findAll(DeimosConstants.METADATA_EXTENSION);
        Product product = null;
        if (metadataFiles != null) {
            for (String file : metadataFiles) {
                metadataList.add(XmlMetadata.create(DeimosMetadata.class, productDirectory.getFile(file)));
            }
            if (metadataList.size() > 0) {
                DeimosMetadata deimosMetadata = metadataList.get(0);
                int width = deimosMetadata.getRasterWidth();
                int height = deimosMetadata.getRasterHeight();
                product = new Product(deimosMetadata.getProductName(), DeimosConstants.DIMAP_FORMAT_NAMES[0], width, height);
                product.getMetadataRoot().addElement(deimosMetadata.getRootElement());
                ProductData.UTC centerTime = deimosMetadata.getCenterTime();
                if (centerTime != null) {
                    product.setStartTime(centerTime);
                    product.setEndTime(centerTime);
                }
                product.setDescription(deimosMetadata.getProductDescription());
                if (metadataList.size() == 1) {
                    addBands(product, deimosMetadata);
                } else {
                    String groupingText = "";
                    for (int i = 0; i < metadataList.size(); i++) {
                        DeimosMetadata metadata = metadataList.get(i);
                        addBands(product, metadata);
                        groupingText += metadata.getProductName() + ":";
                    }
                    groupingText = groupingText.substring(0, groupingText.length() - 1);
                    product.setAutoGrouping(groupingText);
                }
                product.setModified(false);
            }
        }
        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY,
                                          int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY,
                                          Band destBand,
                                          int destOffsetX, int destOffsetY,
                                          int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm) throws IOException {
        Band tiffBand = bandMap.get(destBand);
        ProductReader reader = tiffBand.getProductReader();
        if (reader == null) {
            logger.severe("No reader found for band data");
        } else {
            reader.readBandRasterData(tiffBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
        }
    }

    /**
     * This method adds the bands of the given tiff image to the product.
     * Also, the GeoCoding of the bands is transferred to the product.
     *
     * @param product               the product to which the bands are added
     * @param componentMetadata     the associated metadata file
     */
    void addBands(Product product, DeimosMetadata componentMetadata) {
        logger.info("Read product component: " + componentMetadata.getFileName());
        String[] bandNames = componentMetadata.getBandNames();
        try {
            File rasterFile = productDirectory.getFile(componentMetadata.getRasterFileNames()[0]);
            GeoTiffProductReader tiffReader = new GeoTiffProductReader(getReaderPlugIn());
            Product tiffProduct = tiffReader.readProductNodes(rasterFile, null);
            if (tiffProduct != null) {
                MetadataElement tiffMetadata = tiffProduct.getMetadataRoot();
                if (tiffMetadata != null) {
                    XmlMetadata.CopyChildElements(tiffMetadata, product.getMetadataRoot());
                }
                tiffProduct.transferGeoCodingTo(product, null);

                int numBands = tiffProduct.getNumBands();
                String bandPrefix = metadataList.size() > 1 ? componentMetadata.getProductName() + "_" : "";
                for (int idx = 0; idx < numBands; idx++) {
                    Band srcBand = tiffProduct.getBandAt(idx);
                    String bandName = (idx < bandNames.length ? bandNames[idx] : DeimosConstants.DEFAULT_BAND_NAME_PREFIX + idx);
                    //if (product.getBand(bandName) != null) {
                        bandName = bandPrefix + bandName;
                    //}
                    Band targetBand = product.addBand(bandName, srcBand.getDataType());
                    bandMap.put(targetBand, srcBand);
                    targetBand.setValidPixelExpression(srcBand.getValidPixelExpression());
                    targetBand.setNoDataValue(srcBand.getNoDataValue());
                    targetBand.setNoDataValueUsed(false);
                    String unit = srcBand.getUnit();
                    targetBand.setUnit(unit == null ? DeimosConstants.VALUE_NOT_AVAILABLE : unit);
                    targetBand.setGeophysicalNoDataValue(srcBand.getGeophysicalNoDataValue());
                    targetBand.setSpectralWavelength(srcBand.getSpectralWavelength());
                    targetBand.setSpectralBandwidth(srcBand.getSpectralBandwidth());
                    targetBand.setScalingFactor(srcBand.getScalingFactor());
                    targetBand.setScalingOffset(srcBand.getScalingOffset());
                    targetBand.setSolarFlux(srcBand.getSolarFlux());
                    targetBand.setSampleCoding(srcBand.getSampleCoding());
                    targetBand.setImageInfo(srcBand.getImageInfo());
                    targetBand.setSpectralBandIndex(srcBand.getSpectralBandIndex());
                    targetBand.setDescription(bandName);
                }
            }
        } catch (IOException ioEx) {
            logger.severe("Error while reading component: " + ioEx.getMessage());
        }
    }

    void addMasks(Product product, DeimosMetadata componentMetadata) {
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
