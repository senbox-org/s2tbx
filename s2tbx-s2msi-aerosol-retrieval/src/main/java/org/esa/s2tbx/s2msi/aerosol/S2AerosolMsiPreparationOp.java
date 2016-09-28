package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.idepix.algorithms.sentinel2.S2IdepixOp;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.VirtualBand;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.ProductUtils;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 22.09.2016
 * Time: 14:22
 *
 * @author olafd
 */
public class S2AerosolMsiPreparationOp extends Operator {

    @SourceProduct
    private Product sourceProduct;

    @Override
    public void initialize() throws OperatorException {

        final boolean needPixelClassif = (!sourceProduct.containsBand(S2IdepixUtils.IDEPIX_CLASSIF_FLAGS));

        final boolean needElevation = (!sourceProduct.containsBand("elevation"));
        final boolean needSurfacePres = (!sourceProduct.containsBand("surfPressEstimate"));

        // subset might have set product type to null, thus:
        if (sourceProduct.getDescription() == null) sourceProduct.setDescription("Sentinel S2A product");

        // setup target product primarily as copy of sourceProduct
        final int rasterWidth = sourceProduct.getSceneRasterWidth();
        final int rasterHeight = sourceProduct.getSceneRasterHeight();

        Product targetProduct = new Product(sourceProduct.getName(),
                                            sourceProduct.getProductType(),
                                            rasterWidth, rasterHeight);
        targetProduct.setStartTime(sourceProduct.getStartTime());
        targetProduct.setEndTime(sourceProduct.getEndTime());
        targetProduct.setPointingFactory(sourceProduct.getPointingFactory());
        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        ProductUtils.copyFlagBands(sourceProduct, targetProduct, true);

        // create pixel classification if missing in sourceProduct
        // and add flag band to targetProduct
        if (needPixelClassif) {
            S2IdepixOp s2IdepixOp = new S2IdepixOp();
            s2IdepixOp.setParameterDefaultValues();
            s2IdepixOp.setParameter("copyToaReflectances", false);
            s2IdepixOp.setParameter("cloudBufferWidth", 3);
            s2IdepixOp.setSourceProduct(sourceProduct);
            final Product idepixProduct = s2IdepixOp.getTargetProduct();
            ProductUtils.copyFlagBands(idepixProduct, targetProduct, true);
        }

        // create elevation product if band is missing in sourceProduct
        if (needElevation) {
            final Product elevProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(CreateElevationBandOp.class),
                                                          GPF.NO_PARAMS, sourceProduct);
            Guardian.assertNotNull("elevProduct", elevProduct);
            Band srcBand = elevProduct.getBand("elevation");
            Guardian.assertNotNull("elevation band", srcBand);
            ProductUtils.copyBand(srcBand.getName(), elevProduct, targetProduct, true);
        }

        // create surface pressure estimate product if band is missing in sourceProduct
        if (needSurfacePres) {
            String presExpr = "(1013.25 * exp(-elevation/8400))";
            final VirtualBand surfPresBand = new VirtualBand("surfPressEstimate",
                                                             ProductData.TYPE_FLOAT32,
                                                             rasterWidth, rasterHeight, presExpr);
            surfPresBand.setDescription("estimated sea level pressure (p0=1013.25hPa, hScale=8.4km)");
            surfPresBand.setNoDataValue(0);
            surfPresBand.setNoDataValueUsed(true);
            surfPresBand.setUnit("hPa");
            targetProduct.addBand(surfPresBand);
        }

        // copy all bands from sourceProduct
        for (Band srcBand : sourceProduct.getBands()) {
            String srcName = srcBand.getName();
            if (!srcBand.isFlagBand()) {
                ProductUtils.copyBand(srcName, sourceProduct, targetProduct, true);
            }
        }

        // in the end we have in the target product:
        // - B1,...,B12
        // - sun_zenith, sun_azimuth, view_zenith_mean, view_azimuth_mean
        // - pixel_classif_flag
        // - elevation
        // - surfPressEstimate

        setTargetProduct(targetProduct);
    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(S2AerosolMsiPreparationOp.class);
        }
    }
}
