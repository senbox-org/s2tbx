package org.esa.s2tbx.s2msi.wv;

import org.esa.s2tbx.s2msi.aerosol.util.PixelGeometry;
import org.esa.snap.core.datamodel.Band;
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
import org.esa.snap.core.util.math.FracIndex;
import org.esa.snap.core.util.math.IntervalPartition;
import org.esa.snap.core.util.math.LookupTable;

import java.io.IOException;
import java.util.Map;

/**
 * Operator to derive water vapour from a resampled S2 MSI L1C  product.
 *
 * @author Tonio Fincke
 */
@OperatorMetadata(alias = "S2.WaterVapour",
        category = "Optical",
        description = "Water Vapour retrieval from S2 MSI",
        authors = "Tonio Fincke, Grit Kirches",
        version = "1.0",
        copyright = "(C) 2017 by Brockmann Consult")
public class S2WaterVapourRetrievalOp extends PixelOperator {

    private static String[] REQUIRED_INPUT_BAND_NAMES = {"B8A", "B9","sun_zenith", "view_zenith_mean", "sun_azimuth",
            "view_azimuth_mean", "elevation"};
    private final static int B8A_INDEX = 0;
    private final static int B9_INDEX = 1;
    private final static int SZA_INDEX = 2;
    private final static int VZA_INDEX = 3;
    private final static int SAA_INDEX = 4;
    private final static int VAA_INDEX = 5;
    private final static int ELEVATION_INDEX = 6;

    private final static double VZA_LUT_MIN = 0.0;
    private final static double VZA_LUT_MAX = 60.0;
    private final static double SZA_LUT_MIN = 0.0;
    private final static double SZA_LUT_MAX = 70.0;

    private static final int DAYS_FROM_1_1_1950_TO_31_12_1999 = 18261;
    private static final double CONVERTED_PI = Math.PI * 10000.0;

    //todo read these from product
    private static final double SOLAR_SPECTRAL_IRRADIANCE_B_8_A = 955.19;
    private static final double SOLAR_SPECTRAL_IRRADIANCE_B_9 = 813.04;

    private double conversionFactorB8A;
    private double conversionFactorB9;
    private final static double WV_NO_DATA_VALUE = -99999.;
    private final static String WATER_VAPOUR_BAND_NAME = "water_vapour";
    private LookupTable waterVapourLut;

    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();
        final Product sourceProduct = getSourceProduct();
        for (String requiredInputBandName : REQUIRED_INPUT_BAND_NAMES) {
            if (sourceProduct.getBand(requiredInputBandName) == null) {
                throw new OperatorException("Required band " + requiredInputBandName + " is not in source product");
            }
        }

        final double mjd = getSourceProduct().getStartTime().getMJD();
        final int julianDay = DAYS_FROM_1_1_1950_TO_31_12_1999 + ((int) (mjd + 0.5));
        int dayOfYear = ((int)(mjd % 365.25)) + 1;
        final double distanceCorrection = getDistanceCorrection(dayOfYear);
        final double conversionFactor = getConversionFactor(julianDay) / distanceCorrection;

        conversionFactorB8A = conversionFactor * SOLAR_SPECTRAL_IRRADIANCE_B_8_A;
        conversionFactorB9 = conversionFactor * SOLAR_SPECTRAL_IRRADIANCE_B_9;

        try {
            waterVapourLut = WaterVapourLUTAccessor.readLut();
        } catch (IOException e) {
            throw new OperatorException(e.getMessage());
        }
    }

    @Override
    protected void computePixel(int x, int y, Sample[] samples, WritableSample[] writableSamples) {
        double rhoToaB8a = samples[B8A_INDEX].getDouble();
        double rhoToaB9 = samples[B9_INDEX].getDouble();
        final double sza = samples[SZA_INDEX].getDouble();
        final double vza = samples[VZA_INDEX].getDouble();
        final double saa = samples[SAA_INDEX].getDouble();
        final double vaa = samples[VAA_INDEX].getDouble();
        if (isInValid(rhoToaB8a, rhoToaB9, sza, vza, saa, vaa)) {
            writableSamples[0].set(WV_NO_DATA_VALUE);
            return;
        }

        final double cosSza = Math.cos(Math.toRadians(sza));
        rhoToaB8a /=  10000;
        rhoToaB9 /= 10000;
        final double lToaB8A = rhoToaB8a * conversionFactorB8A * cosSza;
        final double lToaB9 = rhoToaB9 * conversionFactorB9 * cosSza;

        final double relativeAzi = PixelGeometry.getRelativeAzi(saa, vaa);

        double altitude = samples[ELEVATION_INDEX].getDouble();
        altitude /= 1000.;

        final double waterVapour = getWaterVapour(lToaB8A, lToaB9, rhoToaB8a, sza, vza, relativeAzi, altitude);

        writableSamples[0].set(waterVapour);
    }

    private double getWaterVapour(double lToaB8A, double lToaB9, double rhoToaB8a, double sunZenith, double viewZenith,
                                  double relAzi, double altitude) {
        double continuumInterpolatedBandRatio = lToaB8A / lToaB9;
        final double log10_cibr = Math.log10(continuumInterpolatedBandRatio);

        final IntervalPartition[] partitions = waterVapourLut.getDimensions();
        final double[] coordinates = new double[]{rhoToaB8a, sunZenith, viewZenith, relAzi, altitude};
        final FracIndex[] fracIndexes = FracIndex.createArray(partitions.length - 1);
        for (int i = 0; i < partitions.length - 1; i++) {
            final IntervalPartition partition = waterVapourLut.getDimension(i);
            LookupTable.computeFracIndex(partition, coordinates[i], fracIndexes[i]);
        }
        final double[] bValues = waterVapourLut.getValues(fracIndexes);
        double waterVapour = Math.pow(bValues[0] * log10_cibr + bValues[1], 2);
        waterVapour *= 10;  //changing from g/cm^2 to kg/m^2
        return waterVapour;
    }

    @Override
    protected void configureSourceSamples(SourceSampleConfigurer sourceSampleConfigurer) throws OperatorException {
        for (int i = 0; i < REQUIRED_INPUT_BAND_NAMES.length; i++) {
            sourceSampleConfigurer.defineSample(i, REQUIRED_INPUT_BAND_NAMES[i]);
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
        final Band waterVapourBand = productConfigurer.addBand(WATER_VAPOUR_BAND_NAME, ProductData.TYPE_FLOAT32);
        waterVapourBand.setNoDataValue(WV_NO_DATA_VALUE);
        waterVapourBand.setNoDataValueUsed(true);
        waterVapourBand.setUnit("kg/m^2");
    }

    @Override
    protected void configureTargetSamples(TargetSampleConfigurer targetSampleConfigurer) throws OperatorException {
        targetSampleConfigurer.defineSample(0, WATER_VAPOUR_BAND_NAME);
    }

    static double getConversionFactor(int julianDay) {
        // For calculation of distance correction (d(t)) see
        // https://earth.esa.int/web/sentinel/technical-guides/sentinel-2-msi/level-1c/algorithm
        double distanceCorrection = 1 / Math.pow(1 - 0.01673 * Math.cos(0.0172 * (julianDay - 2)), 2);
        return distanceCorrection / CONVERTED_PI;
    }

    static double getDistanceCorrection(int dayOfYear) {
        // see 'Astronomical Almanac'
        double gamma = (2 * Math.PI * (dayOfYear - 1)) / 365.0;
        return 1.000110 + 0.034221 * Math.cos(gamma) + 0.001280 * Math.sin(gamma) +
                0.000719 * Math.cos(2 * gamma) + 0.000077 * Math.sin(2 * gamma);
    }


    static boolean isInValid(double rhoToaB8A, double rhoToaB9, double sza, double vza, double saa, double vaa) {
        return Double.isNaN(rhoToaB8A) || Double.isNaN(rhoToaB9) ||
                Double.isNaN(sza) || Double.isNaN(vza) || Double.isNaN(saa) || Double.isNaN(vaa) ||
                sza < SZA_LUT_MIN || sza > SZA_LUT_MAX || vza < VZA_LUT_MIN || vza > VZA_LUT_MAX;
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
