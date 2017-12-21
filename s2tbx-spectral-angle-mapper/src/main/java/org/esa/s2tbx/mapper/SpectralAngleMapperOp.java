package org.esa.s2tbx.mapper;

import org.esa.s2tbx.mapper.common.SpectralAngleMapperConstants;
import org.esa.snap.core.datamodel.Band;
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

import java.awt.Rectangle;
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
    private Throwable error;
    @Override
    public void initialize() throws OperatorException {
        if (this.sourceProduct == null) {
            throw new OperatorException("Source product not set");
        }

        if(spectra.length == 0) {
            throw new OperatorException("No spectrum classes have been set");
        }

        ensureSingleRasterSize(this.sourceProduct);
        int targetWidth = this.sourceProduct.getSceneRasterWidth();
        int targetHeight = this.sourceProduct.getSceneRasterHeight();

        this.targetProduct = new Product(SpectralAngleMapperConstants.TARGET_PRODUCT_NAME, this.sourceProduct.getProductType() + "_SAM", targetWidth, targetHeight);
        ProductUtils.copyTimeInformation(this.sourceProduct, this.targetProduct);

        Band samOutputBand = new Band(SpectralAngleMapperConstants.SAM_BAND_NAME, ProductData.TYPE_FLOAT32, targetWidth, targetHeight);
        samOutputBand.setNoDataValueUsed(true);
        samOutputBand.setNoDataValue(SpectralAngleMapperConstants.NO_DATA_VALUE);
        this.targetProduct.addBand(samOutputBand);

        boolean sceneSizeRetained = this.sourceProduct.getSceneRasterSize().equals(this.targetProduct.getSceneRasterSize());
        if (sceneSizeRetained) {
            ProductUtils.copyTiePointGrids(this.sourceProduct, this.targetProduct);
            ProductUtils.copyGeoCoding(this.sourceProduct, this.targetProduct);
        }
    }

    @Override
    public Product getSourceProduct() {
        return this.sourceProduct;
    }

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        this.threshold = new ArrayList<>();
        this.classColor = new HashMap<>();
        int classColorLevel = 200;
        for(Spectrum spectrum : this.spectra) {
            this.classColor.put(spectrum.getName(), classColorLevel);
            classColorLevel += 200;
        }
        parseThresholds();
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing SpectralAngleMapperOp", rectangle.height);
        try {
            List<Tile> sourceTileList = new ArrayList<>();
            for(int index = 0; index < this.sourceProduct.getNumBands(); index++) {
                if(Arrays.asList(this.referenceBands).contains(this.sourceProduct.getBandAt(index).getName())){
                    sourceTileList.add(getSourceTile(getSourceProduct().getBandAt(index), rectangle));
                }
            }

            Tile samTile = targetTiles.get(this.targetProduct.getBand(SpectralAngleMapperConstants.SAM_BAND_NAME));
            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                if(pm.isCanceled()){
                    break;
                }
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    if(pm.isCanceled()){
                        break;
                    }
                    boolean setPixelColor = false;
                    for(int spectrumIndex = 0; spectrumIndex<this.spectra.length; spectrumIndex++) {
                        Spectrum spectrum = this.spectra[spectrumIndex];
                        int xPosition = spectrum.getXPixelPosition();
                        int yPosition = spectrum.getYPixelPosition();
                        float valueSum = 0;
                        float pixelValueSquareSum = 0;
                        float spectrumPixelValueSquareSum = 0;
                        for(int tileIndex=0; tileIndex < sourceTileList.size(); tileIndex++) {
                            valueSum += sourceTileList.get(tileIndex).getSampleFloat(x, y) *
                                    this.sourceProduct.getBand(this.referenceBands[tileIndex]).getSampleFloat(xPosition, yPosition);
                            pixelValueSquareSum += Math.pow(sourceTileList.get(tileIndex).getSampleFloat(x, y), 2);
                            spectrumPixelValueSquareSum += Math.pow(this.sourceProduct.getBand(this.referenceBands[tileIndex]).getSampleFloat(xPosition, yPosition), 2);
                        }
                        double samAngle = Math.acos(valueSum/(Math.sqrt(pixelValueSquareSum)*(Math.sqrt(spectrumPixelValueSquareSum))));
                        if(samAngle < this.threshold.get(spectrumIndex)){
                          samTile.setSample(x, y,this.classColor.get(spectrum.getName()));
                            setPixelColor = true;
                        }
                    }
                    if(!setPixelColor){
                        samTile.setSample(x, y,SpectralAngleMapperConstants.NO_DATA_VALUE);
                    }
                }
            }
            pm.worked(1);
        }finally {
            pm.done();
        }
    }

    private void parseThresholds() {
        StringTokenizer str = new StringTokenizer(this.thresholds, ", ");
        while (str.hasMoreElements()) {
            double thresholdValue = Double.parseDouble(str.nextToken().trim());
            this.threshold.add(thresholdValue);
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(SpectralAngleMapperOp.class);
        }

    }
}
