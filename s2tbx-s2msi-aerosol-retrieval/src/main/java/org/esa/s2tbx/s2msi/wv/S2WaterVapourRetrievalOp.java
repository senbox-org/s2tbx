package org.esa.s2tbx.s2msi.wv;

import org.esa.s2tbx.s2msi.aerosol.util.PixelGeometry;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.pointop.PixelOperator;
import org.esa.snap.core.gpf.pointop.ProductConfigurer;
import org.esa.snap.core.gpf.pointop.Sample;
import org.esa.snap.core.gpf.pointop.SourceSampleConfigurer;
import org.esa.snap.core.gpf.pointop.TargetSampleConfigurer;
import org.esa.snap.core.gpf.pointop.WritableSample;
import org.esa.snap.core.util.ProductUtils;

import java.util.Map;

/**
 * Operator to derive water vapour from a resampled S2 MSI L1C  product.
 *
 * @author Tonio Fincke
 */
@OperatorMetadata(alias = "S2.WaterVapour",
        description = "Water Vapour retrieval from S2 MSI",
        authors = "Tonio Fincke, Grit Kirches",
        version = "1.0",
        copyright = "(C) 2017 by Brockmann Consult")
public class S2WaterVapourRetrievalOp extends PixelOperator {

    private static String[] REQUIRED_INPUT_BANDS = {"B8A", "B9","sun_zenith", "view_zenith_mean", "sun_azimuth",
            "view_azimuth_mean", "elevation"};
    private final static int B8A_INDEX = 0;
    private final static int B9_INDEX = 1;
    private final static int SZA_INDEX = 2;
    private final static int VZA_INDEX = 3;
    private final static int SAA_INDEX = 4;
    private final static int VAA_INDEX = 5;
    private final static int ELEVATION_INDEX = 6;

    private static final int DAYS_FROM_1_1_1950_TO_31_12_1999 = 18261;
    private static final double CONVERTED_PI = Math.PI * 10000.0;

    //todo read these from product
    private static final double solarSpectralIrradianceB8A = 955.19;
    private static final double solarSpectralIrradianceB9 = 813.04;

    private int julianDay;
    private double distanceCorrection;

    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();
        final Product sourceProduct = getSourceProduct();
        for (String REQUIRED_INPUT_BAND : REQUIRED_INPUT_BANDS) {
            if (sourceProduct.getBand(REQUIRED_INPUT_BAND) == null) {
                throw new OperatorException("Required band " + REQUIRED_INPUT_BAND + " is not in source product");
            }
        }

        final double mjd = getSourceProduct().getStartTime().getMJD();
        julianDay = DAYS_FROM_1_1_1950_TO_31_12_1999 + ((int) (mjd + 0.5));
        int dayOfYear = ((int)(mjd % 365.25)) + 1;
        distanceCorrection = getDistanceCorrection(dayOfYear);

    }

    @Override
    protected void computePixel(int x, int y, Sample[] samples, WritableSample[] writableSamples) {
        final float sza = samples[SZA_INDEX].getFloat();
        final double cosSza = Math.cos(Math.toRadians(sza));
        final double rhoToaB8a = samples[B8A_INDEX].getDouble() / 10000;
        final double rhoToaB9 = samples[B9_INDEX].getDouble() / 10000;
        double lToaB8A = convertAlbedoToRadiance(rhoToaB8a, cosSza, solarSpectralIrradianceB8A) / distanceCorrection;
        lToaB8A /= distanceCorrection;
        double lToaB9 = convertAlbedoToRadiance(rhoToaB9, cosSza, solarSpectralIrradianceB9);
        lToaB9 /= distanceCorrection;

        double altitude = samples[ELEVATION_INDEX].getDouble();
        altitude /= 1000.;

        final double saa = samples[SAA_INDEX].getDouble();
        final double vaa = samples[VAA_INDEX].getDouble();
        final double relativeAzi = PixelGeometry.getRelativeAzi(saa, vaa);


    }

    @Override
    protected void configureSourceSamples(SourceSampleConfigurer sourceSampleConfigurer) throws OperatorException {
        for (int i = 0; i < REQUIRED_INPUT_BANDS.length; i++) {
            sourceSampleConfigurer.defineSample(i, REQUIRED_INPUT_BANDS[i]);
        }
    }


    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);
        String[] names = getSourceProduct().getBandNames();
        for (String name : names) {
            ProductUtils.copyBand(name, getSourceProduct(), getTargetProduct(), true);
            ProductUtils.copyGeoCoding(getSourceProduct().getRasterDataNode(name), getTargetProduct().getRasterDataNode(name));
        }
        productConfigurer.copyMasks();
        productConfigurer.copyMetadata();
        productConfigurer.copyVectorData();
        productConfigurer.addBand("water_vapour", ProductData.TYPE_FLOAT32);
    }

    @Override
    protected void configureTargetSamples(TargetSampleConfigurer targetSampleConfigurer) throws OperatorException {
        targetSampleConfigurer.defineSample(0, "water_vapour");
    }

    private double convertAlbedoToRadiance(double rhoToa, double cosSza, double solarSpectralIrradiance) {
        // RHO = TOA albedo = PI * RADIANCE / IRRADIANCE * sun distance correction (=d(t)) * cos(sun_zenith)
        // For calculation of distance correction (d(t)) see
        // https://earth.esa.int/web/sentinel/technical-guides/sentinel-2-msi/level-1c/algorithm
        double distanceCorrection = 1 / Math.pow(1 - 0.01673 * Math.cos(0.0172 * (julianDay - 2)), 2);
        double conversion_factor = (solarSpectralIrradiance * distanceCorrection * cosSza) / CONVERTED_PI;
        return rhoToa * conversion_factor;
    }

    static double getDistanceCorrection(int dayOfYear) {
        // see 'Astronomical Almanac'
        double gamma = (2 * Math.PI * (dayOfYear - 1)) / 365.0;
        return 1.000110 + 0.034221 * Math.cos(gamma) + 0.001280 * Math.sin(gamma) +
                0.000719 * Math.cos(2 * gamma) + 0.000077 * Math.sin(2 * gamma);
    }

    /**
     * The SPI is used to register this operator in the graph processing framework
     * via the SPI configuration file
     * {@code META-INF/services/org.esa.beam.framework.gpf.OperatorSpi}.
     * This class may also serve as a factory for new operator instances.
     *
     * @see OperatorSpi#createOperator()
     * @see OperatorSpi#createOperator(Map, Map)
     */
    public static class Spi extends OperatorSpi {
        public Spi() {
            super(S2WaterVapourRetrievalOp.class);
        }
    }
}
