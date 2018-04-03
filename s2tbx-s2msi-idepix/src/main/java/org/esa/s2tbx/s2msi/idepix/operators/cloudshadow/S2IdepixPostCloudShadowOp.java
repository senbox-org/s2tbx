package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import java.awt.Rectangle;
import java.util.Map;

/**
 * @author Tonio Fincke, Dagmar Müller
 */
@OperatorMetadata(alias = "Idepix.Sentinel2.CloudShadow.Postprocess",
        category = "Optical",
        authors = "Grit Kirches, Michael Paperin, Olaf Danne, Tonio Fincke, Dagmar Müller",
        copyright = "(c) Brockmann Consult GmbH",
        version = "1.0",
        description = "Post-processing for algorithm detecting cloud shadow...")
public class S2IdepixPostCloudShadowOp extends Operator {

    @SourceProduct(alias = "source", optional = true)
    private Product sourceProduct;      // has only classifFlagBand with buffer added

    @TargetProduct
    private Product targetProduct;

    @Override
    public void initialize() throws OperatorException {
        //here you could retrieve the important information from the preProcessedProduct
        targetProduct = getSourceProduct();
        setTargetProduct(targetProduct);
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws OperatorException {
        //todo implement me
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixPostCloudShadowOp.class);
        }
    }

}
