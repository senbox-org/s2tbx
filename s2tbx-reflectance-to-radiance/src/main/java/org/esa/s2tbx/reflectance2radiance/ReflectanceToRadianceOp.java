package org.esa.s2tbx.reflectance2radiance;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.utils.StringHelper;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmihailescu on 11/08/2016.
 */

@OperatorMetadata(
        alias = "ReflectanceToRadianceOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "The 'Reflectance To Radiance Processor' operator retrieves the radiance from reflectance using Sentinel-2 products",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class ReflectanceToRadianceOp extends Operator {
    @Parameter(label = "Solar irradiance (if not Sentinel-2 or SPOT)", defaultValue = "", description = "The solar irradiance.")
    private float solarIrradiance;

    @Parameter(label = "U", defaultValue = "", description = "U")
    private float u;

    @Parameter(label = "Incidence angle", defaultValue = "", description = "The incidence angle in degrees.")
    private float incidenceAngle;

    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    private Band[] sourceBands;
    private double d2;
    private double scale;
    private Map<String, TiePointGrid> tiePointGrids;
    private Map<String, Float> solarIrradiances;

    public ReflectanceToRadianceOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }
        Band sunZenithBand = this.sourceProduct.getBand("sun_zenith");

        if (isSentinelProduct(this.sourceProduct)) {
            this.solarIrradiances = extractSolarIrradiancesFromSentinelProduct(this.sourceProduct, this.sourceBandNames);
            this.u = extractUFromSentinelProduct(this.sourceProduct);
        } else if (isSpotProduct(this.sourceProduct)) {
            this.solarIrradiances = extractSolarIrradianceFromSpotProduct(this.sourceProduct, this.sourceBandNames);
            this.incidenceAngle = extractIncidenceAngleFromSpotProduct(this.sourceProduct);
        }

        if (this.solarIrradiances == null && this.solarIrradiance == 0.0f) {
            throw new OperatorException("Please specify the solar irradiance.");
        }
        if (this.u == 0.0f) {
            throw new OperatorException("Please specify the U.");
        }

        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();

        targetProduct = new Product(sourceProduct.getName() + "_rad", sourceProduct.getProductType(), sceneWidth, sceneHeight);

        targetProduct.setNumResolutionsMax(this.sourceProduct.getNumResolutionsMax());

        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        ProductUtils.copyFlagBands(sourceProduct, targetProduct, true);
        ProductUtils.copyMasks(sourceProduct, targetProduct);
        ProductUtils.copyOverlayMasks(sourceProduct, targetProduct);

        this.sourceBands = new Band[this.sourceBandNames.length];
        this.tiePointGrids = new HashMap<>();
        for (int i = 0; i < this.sourceBandNames.length; i++) {
            Band sourceBand = sourceProduct.getBand(this.sourceBandNames[i]);
            this.sourceBands[i] = this.sourceProduct.getBand(this.sourceBandNames[i]);
            int sourceBandWidth = this.sourceBands[i].getRasterWidth();
            int sourceBandHeight = this.sourceBands[i].getRasterHeight();
            Band targetBand = new Band(this.sourceBandNames[i], ProductData.TYPE_FLOAT32, sourceBandWidth, sourceBandHeight);
            targetBand.setGeoCoding(sourceBand.getGeoCoding());
            targetBand.setScalingOffset(sourceBand.getScalingOffset());
            targetBand.setScalingFactor(sourceBand.getScalingFactor());
            targetBand.setSampleCoding(sourceBand.getSampleCoding());
            targetBand.setSolarFlux(sourceBand.getSolarFlux());
            targetBand.setSpectralBandIndex(sourceBand.getSpectralBandIndex());
            targetBand.setSpectralBandwidth(sourceBand.getSpectralBandwidth());
            targetBand.setSpectralWavelength(sourceBand.getSpectralWavelength());
            targetBand.setDescription(sourceBand.getDescription());
            targetBand.setGeophysicalNoDataValue(sourceBand.getGeophysicalNoDataValue());
            targetBand.setNoDataValue(sourceBand.getNoDataValue());

            this.targetProduct.addBand(targetBand);

            if (sunZenithBand == null) {
                if (this.incidenceAngle == 0.0f) {
                    throw new OperatorException("Please specify the incidence angle.");
                }
                float[] tiePoints = new float[] {this.incidenceAngle, this.incidenceAngle, this.incidenceAngle, this.incidenceAngle};
                this.tiePointGrids.put(sourceBand.getName(), new TiePointGrid("angles_" + sourceBand.getName(), 2, 2, 0, 0, sourceBandWidth, sourceBandHeight, tiePoints));
            } else {
                int sunZenithBandWidth = sunZenithBand.getRasterWidth();
                int sunZenithBandHeight = sunZenithBand.getRasterHeight();
                float[] tiePoints = new float[sunZenithBandWidth * sunZenithBandHeight];
                int index = 0;
                for (int row = 0; row < sunZenithBandHeight; row++) {
                    for (int column = 0; column < sunZenithBandWidth; column++) {
                        tiePoints[index++] = sunZenithBand.getSampleFloat(column, row);
                    }
                }
                this.tiePointGrids.put(sourceBand.getName(), new TiePointGrid("angles_" + sourceBand.getName(), sunZenithBandWidth, sunZenithBandHeight, 0, 0, sourceBandWidth, sourceBandHeight, tiePoints));
            }
        }

        this.d2 = 1.0d / this.u;
        this.scale = 1.0d;
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing Reflectance to Radiance", targetTile.getHeight());
        // https://github.com/umwilm/SEN2COR/blob/96a00464bef15404a224b2262accd0802a338ff9/sen2cor/L2A_Tables.py
        // The final formula is:
        // rad = rho * cos(radians(sza)) * Es * sc / (pi * d2)
        // where: d2 = 1.0 / U
        // scale: 1 / (0.001 * 1000) = 1 (default)
        Rectangle rectangle = targetTile.getRectangle();
        try {
            Tile sourceTile = getSourceTile(this.sourceProduct.getBand(targetBand.getName()), rectangle);
            TiePointGrid tiePointGrid = this.tiePointGrids.get(targetBand.getName());
            float slrIrr = this.solarIrradiances != null ?
                    this.solarIrradiances.get(targetBand.getName()) :
                    this.solarIrradiance;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    float sunZenitAngle = tiePointGrid.getSampleFloat(x, y);

                    double sunZenitRadians = Math.toRadians(sunZenitAngle);

                    float pixelValue = sourceTile.getSampleFloat(x, y);

                    float result = (float)((pixelValue * Math.cos(sunZenitRadians) * slrIrr * this.scale) / (Math.PI * this.d2));
                    targetTile.setSample(x, y, result);
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    /*@Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing Reflectance to Radiance", rectangle.height);
        // https://github.com/umwilm/SEN2COR/blob/96a00464bef15404a224b2262accd0802a338ff9/sen2cor/L2A_Tables.py
        // The final formula is:
        // rad = rho * cos(radians(sza)) * Es * sc / (pi * d2)
        // where: d2 = 1.0 / U
        // scale: 1 / (0.001 * 1000) = 1 (default)
        try {
            Tile sourceTile = getSourceTile(this.sourceBand, rectangle);
            Tile targetBandName = targetTiles.get(this.targetProduct.getBand(getBandName()));

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    float sunZenitAngle = this.tiePointGrid.getSampleFloat(x, y);

                    double sunZenitRadians = Math.toRadians(sunZenitAngle);

                    float pixelValue = sourceTile.getSampleFloat(x, y);

                    float result = (float)((pixelValue * Math.cos(sunZenitRadians) * this.solarIrradiance * this.scale) / (Math.PI * this.d2));
                    targetBandName.setSample(x, y, result);
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }*/

    /*private String getBandName() {
        return "Reflectance to Radiance";
    }*/

    /**
     * Find an element in the tree according to its path and the tree root element.
     *
     * @param rootElement the first element used to apply the path
     * @param pathElement the path of the element to find
     * @return element in the tree according to its path and the tree root element
     */
    private static MetadataElement findTreeElement(MetadataElement rootElement, String[] pathElement) {
        MetadataElement currentElement = rootElement;
        for (String tagName : pathElement) {
            currentElement = currentElement.getElement(tagName);
        }
        return currentElement;
    }

    /**
     * Extract the source band index from the xml file.
     *
     * @param metadataRoot the tree root element of the xml file
     * @param sourceBandName the band name
     * @return the band index
     */
    private static int extractSourceBandIndex(MetadataElement metadataRoot, String sourceBandName) {
        String[] bandListPath = {"Level-1C_User_Product", "General_Info", "Product_Info", "Query_Options", "Band_List"};
        MetadataElement bandListElement = findTreeElement(metadataRoot, bandListPath);
        for (int i=0; i<bandListElement.getNumAttributes(); i++) {
            MetadataAttribute metadataAttribute = bandListElement.getAttributeAt(i);
            ProductData data = metadataAttribute.getData();
            String bandName = data.getElemString();
            if (sourceBandName.equals(bandName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the solar irradiance for a certain band name of the Spot product.
     *
     * @param product the Spot product
     * @param sourceBandNames the band names to find their solar irradiance
     * @return the solar irradiance for a certain band name of the Spot product
     */
    static Map<String, Float> extractSolarIrradianceFromSpotProduct(Product product, String...sourceBandNames) {
        Map<String, Float> solarIrradiances = new HashMap<>();
        if (sourceBandNames != null && sourceBandNames.length > 0) {
            MetadataElement metadataRoot = product.getMetadataRoot();
            MetadataElement[] elements = metadataRoot.getElements();
            MetadataElement currentElement = null;
            for (MetadataElement element : elements) {
                String name = element.getName();
                if (StringHelper.startsWithIgnoreCase(name, "SPOTSCENE")) {
                    currentElement = element;
                    break;
                }
            }

            String[] pathElement = {"Image_Interpretation"};
            MetadataElement imageInterpretationElement = findTreeElementByNameAttribute(currentElement, pathElement);
            elements = imageInterpretationElement.getElements();
            Map<Integer, String> bandIndices = new HashMap<>();
            for (String sourceBandName : sourceBandNames) {
                for (int i = 0; i < elements.length; i++) {
                    String name = elements[i].getName();
                    if (name.equals("Spectral_Band_Info")) {
                        MetadataAttribute metadataAttribute = elements[i].getAttribute("BAND_DESCRIPTION");
                        if (sourceBandName.equals(metadataAttribute.getData().getElemString())) {
                            bandIndices.put(i, sourceBandName);
                            break;
                        }
                    }
                }
            }

            String[] irrandiacenPathElement = {"Data_Strip", "Sensor_Calibration", "Solar_Irradiance"};
            MetadataElement solarIrradianceElement = findTreeElementByNameAttribute(currentElement, irrandiacenPathElement);
            elements = solarIrradianceElement.getElements();
            for (Integer bandIndex : bandIndices.keySet()) {
                for (int i = 0; i < elements.length; i++) {
                    String name = elements[i].getName();
                    if (name.equals("Band_Solar_Irradiance") && bandIndex == i) {
                        MetadataAttribute metadataAttribute = elements[i].getAttribute("SOLAR_IRRADIANCE_VALUE");
                        ProductData data = metadataAttribute.getData();
                        solarIrradiances.put(bandIndices.get(bandIndex), Float.parseFloat(data.getElemString()));
                        break;
                    }
                }
            }
        }
        return solarIrradiances;
    }

    /**
     * Find the incidence angle of a Spot product.
     * @param product the Spot product
     * @return the incidence angle of a Spot product
     */
    static float extractIncidenceAngleFromSpotProduct(Product product) {
        MetadataElement metadataRoot = product.getMetadataRoot();
        MetadataElement[] elements = metadataRoot.getElements();
        MetadataElement currentElement = null;
        for (MetadataElement element : elements) {
            String name = element.getName();
            if (StringHelper.startsWithIgnoreCase(name, "SPOTSCENE")) {
                currentElement = element;
                break;
            }
        }

        String[] pathElement = {"Dataset_Sources", "Source_Information", "Scene_Source"};
        currentElement = findTreeElementByNameAttribute(currentElement, pathElement);
        MetadataAttribute incidenceAngleAttribute = currentElement.getAttribute("INCIDENCE_ANGLE");
        ProductData data = incidenceAngleAttribute.getData();
        return Float.parseFloat(data.getElemString());
    }

    /**
     * Find an element in the tree according to its path and the tree root element.
     *
     * @param rootElement the first element used to apply the path
     * @param pathElement the path of the element to find
     * @return element in the tree according to its path and the tree root element
     */
    private static MetadataElement findTreeElementByNameAttribute(MetadataElement rootElement, String[] pathElement) {
        MetadataElement currentElement = rootElement;
        for (String tagName : pathElement) {
            MetadataElement[] elements = currentElement.getElements();
            for (MetadataElement element : elements) {
                String name = element.getName();
                if (name.equals(tagName)) {
                    currentElement = element;
                    break;
                }
            }
        }

        return currentElement;
    }

    /**
     * Find the solar irradiance for a certain band name of the Sentinel product.
     *
     * @param product the Sentinel product
     * @param sourceBandNames the band names to find their solar irradiance
     * @return the solar irradiance for a certain band name of the Sentinel product
     */
    private Map<String, Float> extractSolarIrradiancesFromSentinelProduct(Product product, String...sourceBandNames) {
        Map<String, Float> irradiances = new HashMap<>();
        if (sourceBandNames != null && sourceBandNames.length > 0) {
            MetadataElement metadataRoot = product.getMetadataRoot();
            String[] reflectanceConversionPath = {"Level-1C_User_Product", "General_Info", "Product_Image_Characteristics", "Reflectance_Conversion"};
            MetadataElement reflectanceConversionElement = findTreeElement(metadataRoot, reflectanceConversionPath);
            MetadataElement solarIrradianceElement = reflectanceConversionElement.getElement("Solar_Irradiance_List");
            for (String sourceBandName : sourceBandNames) {
                int bandIndex = extractSourceBandIndex(metadataRoot, sourceBandName);
                if (bandIndex >= 0) {
                    MetadataAttribute metadataAttribute = solarIrradianceElement.getAttributeAt(bandIndex);
                    ProductData data = metadataAttribute.getData();
                    irradiances.put(sourceBandName, Float.parseFloat(data.getElemString()));
                }
            }
        }
        return irradiances;
    }

    /**
     * Find the quantification value (U) stored in the Sentinel product metadata.
     *
     * @param product the input product
     * @return the quantification value (U) stored in the Sentinel product metadata
     */
    static float extractUFromSentinelProduct(Product product) {
        MetadataElement metadataRoot = product.getMetadataRoot();
        String[] reflectanceConversionPath = {"Level-1C_User_Product", "General_Info", "Product_Image_Characteristics", "Reflectance_Conversion"};
        MetadataElement reflectanceConversionElement = findTreeElement(metadataRoot, reflectanceConversionPath);
        MetadataAttribute metadataAttribute = reflectanceConversionElement.getAttribute("U");
        ProductData data = metadataAttribute.getData();
        return Float.parseFloat(data.getElemString());
    }

    static boolean isSentinelProduct(Product product) {
        return StringHelper.startsWithIgnoreCase(product.getProductType(), "S2_MSI_Level");
    }

    static boolean isSpotProduct(Product product) {
        return StringHelper.startsWithIgnoreCase(product.getProductType(), "SPOTSCENE");
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ReflectanceToRadianceOp.class);
        }
    }
}
