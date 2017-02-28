package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.aerosol.util.AerosolTypeProvider;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.pointop.PixelOperator;
import org.esa.snap.core.gpf.pointop.ProductConfigurer;
import org.esa.snap.core.gpf.pointop.Sample;
import org.esa.snap.core.gpf.pointop.SourceSampleConfigurer;
import org.esa.snap.core.gpf.pointop.TargetSampleConfigurer;
import org.esa.snap.core.gpf.pointop.WritableSample;
import org.esa.snap.core.util.ProductUtils;

import java.io.IOException;

/**
 * @author Tonio Fincke
 */
public class S2AerosolTypeOp extends PixelOperator {

    private AerosolTypeProvider aerosolTypeProvider;
    private GeoCoding sceneGeoCoding;

    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();
        final Product sourceProduct = getSourceProduct();
        ensureSingleRasterSize(sourceProduct);
        sceneGeoCoding = sourceProduct.getSceneGeoCoding();
        final double mjd = sourceProduct.getStartTime().getMJD();
        int dayOfYear = ((int)(mjd % 365.25)) + 1;
        try {
            aerosolTypeProvider = new AerosolTypeProvider(dayOfYear);
        } catch (IOException e) {
            throw new OperatorException("Could not determine aerosol type: " + e.getMessage());
        }
    }

    @Override
    protected void computePixel(int x, int y, Sample[] samples, WritableSample[] writableSamples) {
        final GeoPos geoPos = new GeoPos();
        sceneGeoCoding.getGeoPos(new PixelPos(x, y), geoPos);
        writableSamples[0].set(aerosolTypeProvider.getAerosolType(geoPos));
    }

    @Override
    protected void configureSourceSamples(SourceSampleConfigurer sourceSampleConfigurer) throws OperatorException {
    }


    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);
        String[] names = getSourceProduct().getBandNames();
        for (String name : names) {
            ProductUtils.copyBand(name, getSourceProduct(), getTargetProduct(), true);
            ProductUtils.copyGeoCoding(getSourceProduct().getRasterDataNode(name),
                                       getTargetProduct().getRasterDataNode(name));
        }
        productConfigurer.copyMasks();
        productConfigurer.copyMetadata();
        productConfigurer.copyVectorData();
        productConfigurer.addBand(InstrumentConsts.AEROSOL_TYPE_NAME, ProductData.TYPE_FLOAT32);
    }

    @Override
    protected void configureTargetSamples(TargetSampleConfigurer targetSampleConfigurer) throws OperatorException {
        targetSampleConfigurer.defineSample(0, InstrumentConsts.AEROSOL_TYPE_NAME);
    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(S2AerosolTypeOp.class);
        }
    }
}
