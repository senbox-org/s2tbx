package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.engine_utilities.gpf.OperatorUtils;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "ObjectsSelectionOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")

public class ObjectsSelectionOp extends Operator {

    @SuppressWarnings({"PackageVisibleField"})
    @SourceProduct(alias = "source", description = "The source product to be modified.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(itemAlias = "percentage", description = "The percentage of forrest coverage per object")
    private float percentage;
    private Product landCoverProduct;
    private String LAND_COVER_NAME = "CCILandCover-2015";
    @Override
    public void initialize() throws OperatorException {
        validateSourceProduct();
        try{
            createLandCoverProduct();
            this.landCoverProduct = addLandCoverBand();

        }catch (Throwable e) {
            OperatorUtils.catchOperatorException(getId(), e);
        }
        this.targetProduct = this.landCoverProduct;
        File file = new File("D:\\");
        String formatName = "BEAM-DIMAP";
        GPF.writeProduct(this.targetProduct, file, formatName, false, false, ProgressMonitor.NULL);
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {

    }

    private void validateSourceProduct() {
        GeoCoding geo = this.sourceProduct.getSceneGeoCoding();
        if(geo == null){
            throw new OperatorException("Source product must contain GeoCoding");
        }
    }

    private void createLandCoverProduct() {
        this.landCoverProduct = new Product(this.sourceProduct.getName(), this.sourceProduct.getProductType(),
                this.sourceProduct.getSceneRasterWidth(), this.sourceProduct.getSceneRasterHeight());
        this.landCoverProduct.setStartTime(this.sourceProduct.getStartTime());
        this.landCoverProduct.setEndTime(this.sourceProduct.getEndTime());
        this.landCoverProduct.setNumResolutionsMax(this.sourceProduct.getNumResolutionsMax());

        ProductUtils.copyMetadata(this.sourceProduct,  this.landCoverProduct);
        ProductUtils.copyGeoCoding(this.sourceProduct,  this.landCoverProduct);
        ProductUtils.copyTiePointGrids(this.sourceProduct,  this.landCoverProduct);
        ProductUtils.copyVectorData(this.sourceProduct,  this.landCoverProduct);

    }

    private Product addLandCoverBand() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("landCoverNames", LAND_COVER_NAME);
        return GPF.createProduct("AddLandCover", parameters, this.landCoverProduct);
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ObjectsSelectionOp.class);
        }
    }
}
