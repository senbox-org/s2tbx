package org.esa.s2tbx.reflectance2radiance;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

import java.awt.*;
import java.util.*;

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
    @Parameter(label = "Solar irradiance", defaultValue = "", description = "The solar irradiance.")
    private float solarIrradiance;

    @Parameter(label = "U", defaultValue = "", description = "U")
    private float u;

    @Parameter(label = "Incidence angle", defaultValue = "", description = "The incidence angle in degrees.")
    private float incidenceAngle;

    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(label = "Source band", description = "The source band for the computation.", rasterDataNodeType = Band.class)
    private String sourceBandName;

    private Band sourceBand;
    private double d2;
    private double scale;
    private TiePointGrid tiePointGrid;

    public ReflectanceToRadianceOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        if (this.sourceBandName == null) {
            throw new OperatorException("Unable to find band that could be used as source band. Please specify band.");
        }

        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();

        String name = getBandName();

        targetProduct = new Product(name, sourceProduct.getProductType() + "_" + name, sceneWidth, sceneHeight);
        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        ProductUtils.copyFlagBands(sourceProduct, targetProduct, true);
        ProductUtils.copyMasks(sourceProduct, targetProduct);
        ProductUtils.copyOverlayMasks(sourceProduct, targetProduct);

        Band outputBand = new Band(name, ProductData.TYPE_FLOAT32, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        this.targetProduct.addBand(outputBand);

        this.sourceBand = this.sourceProduct.getBand(this.sourceBandName);
        int sourceBandWidth = this.sourceBand.getRasterWidth();
        int sourceBandHeight = this.sourceBand.getRasterHeight();
        Band sunZenithBand = this.sourceProduct.getBand("sun_zenith");
        if (sunZenithBand == null) {
            if (this.solarIrradiance == 0.0f) {
                throw new OperatorException("Please specify the solar irradiance.");
            }
            if (this.u == 0.0f) {
                throw new OperatorException("Please specify the U.");
            }
            if (this.incidenceAngle == 0.0f) {
                throw new OperatorException("Please specify the incidence angle.");
            }
            float[] tiePoints = new float[] {this.incidenceAngle, this.incidenceAngle, this.incidenceAngle, this.incidenceAngle};
            this.tiePointGrid = new TiePointGrid(name, 2, 2, 0, 0, sourceBandWidth, sourceBandHeight, tiePoints);
        } else {
            MetadataElement metadataRoot = this.sourceBand.getProduct().getMetadataRoot();

            int bandIndex = extractSourceBandIndex(metadataRoot, this.sourceBandName);

            String[] reflectanceConversionPath = {"Level-1C_User_Product", "General_Info", "Product_Image_Characteristics", "Reflectance_Conversion"};
            MetadataElement reflectanceConversionElement = findTreeElement(metadataRoot, reflectanceConversionPath);

            MetadataAttribute metadataAttribute = reflectanceConversionElement.getAttribute("U");
            ProductData data = metadataAttribute.getData();
            this.u = Float.parseFloat(data.getElemString());

            MetadataElement solarIrradianceElement = reflectanceConversionElement.getElement("Solar_Irradiance_List");
            metadataAttribute = solarIrradianceElement.getAttributeAt(bandIndex);
            data = metadataAttribute.getData();
            this.solarIrradiance = Float.parseFloat(data.getElemString());

            int sunZenithBandWidth = sunZenithBand.getRasterWidth();
            int sunZenithBandHeight = sunZenithBand.getRasterHeight();
            float[] tiePoints = new float[sunZenithBandWidth * sunZenithBandHeight];
            int index = 0;
            for (int row=0; row<sunZenithBandHeight; row++) {
                for (int column=0; column<sunZenithBandWidth; column++) {
                    tiePoints[index++] = sunZenithBand.getSampleFloat(column, row);
                }
            }
            this.tiePointGrid = new TiePointGrid(name, sunZenithBandWidth, sunZenithBandHeight, 0, 0, sourceBandWidth, sourceBandHeight, tiePoints);
        }
        this.d2 = 1.0d / this.u;
        this.scale = 1.0d;
    }

    @Override
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
    }

    private String getBandName() {
        return "Reflectance to Radiance";
    }

    /**
     * Find an element in the tree according to its path and the tree root element.
     * @param rootElement the first element used to apply the path in order to the a certain element
     * @param pathElement the path of the element to find
     * @return the element to find
     */
    private static MetadataElement findTreeElement(MetadataElement rootElement, String[] pathElement) {
        MetadataElement currentElement = rootElement;
        for (int i=0; i<pathElement.length; i++) {
            String tagName = pathElement[i];
            currentElement = currentElement.getElement(tagName);
        }
        return currentElement;
    }

    /**
     * Extract the source band index from the xml file.
     * @param metadataRoot the tree root element of the xml file
     * @param sourceBandName the band name
     * @return the band index
     */
    private static int extractSourceBandIndex(MetadataElement metadataRoot, String sourceBandName) {
        String[] bandListPath = {"Level-1C_User_Product", "General_Info", "Product_Info", "Query_Options", "Band_List"};
        MetadataElement bandListElement = findTreeElement(metadataRoot, bandListPath);
        int bandIndex = -1;
        for (int i=0; i<bandListElement.getNumAttributes(); i++) {
            MetadataAttribute metadataAttribute = bandListElement.getAttributeAt(i);
            ProductData data = metadataAttribute.getData();
            String bandName = data.getElemString();
            if (sourceBandName.equals(bandName)) {
                bandIndex = i;
                break;
            }
        }
        return bandIndex;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ReflectanceToRadianceOp.class);
        }

    }
}
