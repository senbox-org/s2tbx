package org.esa.s2tbx.mapper;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ColorPaletteDef;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.util.ProductUtils;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Basic reader for WorldView 2 products.
 *
 * @author Razvan Dumitrascu
 */
@OperatorMetadata(
        alias = "SpectralAngleMapperOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "",
        authors = "Dumitrascu Razvan",
        copyright = "Copyright (C) 2017 by CS ROMANIA")

public class SpectralAngleMapperOp extends Operator {

    // constants
    private static final String SAM_BAND_NAME = "SpectralAngleMapperBand";
    private static final String TARGET_PRODUCT_NAME = "SpectralAngleMapper";
    private static final float NO_DATA_VALUE = Float.NaN;

    @SourceProduct(alias = "sourceProduct", description = "The source product.")
    private Product sourceProduct;
    @TargetProduct
    private Product targetProduct;


    @Parameter(alias = "referenceBands",
            label = "Reference Bands",
            description = "The reference bands to be used for the Spectral Angle Mapper Processor ")
    private String[] referenceBands;

    @Parameter(description = "Thresholds", defaultValue = "0.0")
    String thresholds;

    @Parameter(description = "The list of spectra.", alias = "spectra")
    private Spectrum[] spectra;

    private List<Double> threshold;
    private Map<String, Integer>  classColor;

    @Override
    public void initialize() throws OperatorException {
        if (this.sourceProduct == null) {
            throw new OperatorException("Source product not set");
        }
        ensureSingleRasterSize(sourceProduct);
        int targetWidth = sourceProduct.getSceneRasterWidth();
        int targetHeight = sourceProduct.getSceneRasterHeight();

        targetProduct = new Product(TARGET_PRODUCT_NAME, sourceProduct.getProductType() + "_SAM", targetWidth, targetHeight);
        ProductUtils.copyTimeInformation(sourceProduct, targetProduct);

        Band samOutputBand = new Band(SAM_BAND_NAME, ProductData.TYPE_FLOAT32, targetWidth, targetHeight);
        samOutputBand.setNoDataValueUsed(true);
        samOutputBand.setNoDataValue(NO_DATA_VALUE);
        targetProduct.addBand(samOutputBand);

        boolean sceneSizeRetained = sourceProduct.getSceneRasterSize().equals(targetProduct.getSceneRasterSize());
        if (sceneSizeRetained) {
            ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
            ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        }
    }

    @Override
    public Product getSourceProduct() {
        return this.sourceProduct;
    }

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        threshold = new ArrayList<>();
        classColor = new HashMap<>();
        int classColorLevel = 100;
        for(Spectrum spectrum : spectra) {
            classColor.put(spectrum.getName(), classColorLevel);
            classColorLevel += 100;
        }
        parseThresholds();
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing SpectralAngleMapperOp", rectangle.height);
        try {
            List<Tile> sourceTileList = new ArrayList<Tile>();
            for(int index = 0; index < sourceProduct.getNumBands(); index++) {
                if(Arrays.asList(this.referenceBands).contains(sourceProduct.getBandAt(index).getName())){
                    sourceTileList.add(getSourceTile(getSourceProduct().getBandAt(index), rectangle));
                }
            }

            Tile samTile = targetTiles.get(targetProduct.getBand(SAM_BAND_NAME));
            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    boolean setPixelColor = false;
                    for(int spectrumIndex = 0; spectrumIndex<spectra.length; spectrumIndex++) {
                        Spectrum spectrum = spectra[spectrumIndex];
                        int xPosition = spectrum.getXPixelPosition();
                        int yPosition = spectrum.getYPixelPosition();
                        float valueSum = 0;
                        float pixelValueSquareSum = 0;
                        float spectrumPixelValueSquareSum = 0;
                        for(int tileIndex=0; tileIndex < sourceTileList.size(); tileIndex++) {
                            valueSum += sourceTileList.get(tileIndex).getSampleFloat(x, y) *
                                    sourceProduct.getBand(referenceBands[tileIndex]).getSampleFloat(xPosition, yPosition);
                            pixelValueSquareSum += Math.pow(sourceTileList.get(tileIndex).getSampleFloat(x, y), 2);
                            spectrumPixelValueSquareSum += Math.pow(sourceProduct.getBand(referenceBands[tileIndex]).getSampleFloat(xPosition, yPosition), 2);
                        }
                        double samAngle = Math.acos(valueSum/(Math.sqrt(pixelValueSquareSum)*(Math.sqrt(spectrumPixelValueSquareSum))));
                        if(samAngle < threshold.get(spectrumIndex)){
                          samTile.setSample(x, y,classColor.get(spectrum.getName()));
                            setPixelColor = true;
                        }
                    }
                    if(!setPixelColor){
                        samTile.setSample(x, y, Float.NaN);
                    }
                }
            }
            checkForCancellation();
            pm.worked(1);
        }finally {
            pm.done();
        }
    }

    private void parseThresholds() {
        StringTokenizer str = new StringTokenizer(this.thresholds, ", ");
        while (str.hasMoreElements()) {
            double thresholdValue = Double.parseDouble(str.nextToken().trim());
            threshold.add(thresholdValue);
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(SpectralAngleMapperOp.class);
        }

    }
}
