package org.esa.s2tbx.fcc.trimming;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.util.ProductUtils;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class ObjectSelectionOpTest extends AbstractOpTest  {

    @Test
    public void testObObjectSelectionOp() throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {

        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        File segmentationProductFile = this.forestCoverChangeTestsFolderPath.resolve("S2A_20160713T125925_A005524_T35UMP_grm.dim").toFile();
        Product sourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(segmentationProductFile, null);

        File landCoverProductFile = this.forestCoverChangeTestsFolderPath.resolve("S2A_20160713T125925_A005524_T35UMP_grm_landCover.dim").toFile();
        Product landCoverProduct = productReaderPlugIn.createReaderInstance().readProductNodes(landCoverProductFile, null);



        Map<String, Object> selectionParameters = new HashMap<>();
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", sourceProduct);
        sourceProducts.put("landCoverProduct", landCoverProduct);
        ObjectsSelectionOp operator = (ObjectsSelectionOp) GPF.getDefaultInstance().createOperator("ObjectsSelectionOp", selectionParameters, sourceProducts, null);
        operator.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        Int2ObjectMap<PixelStatistic> statistics = operator.getStatistics();
        checkLandCoverProduct(landCoverProduct, sourceProduct);
        checkStatistics(statistics);
    }

    private static void checkLandCoverProduct(Product landCoverProduct, Product segmentationProduct) {
        assertNotNull(landCoverProduct);
        assertEquals(landCoverProduct.getName(), "S2A_20160713T125925_A005524_T35UMP_grm_landCover");
        assertEquals(landCoverProduct.getSceneRasterSize(), new Dimension(549, 549));
        assertEquals(landCoverProduct.getNumBands(), 1);
        assertEquals(landCoverProduct.getProductType(), segmentationProduct.getProductType());
        Band band1 = landCoverProduct.getBandAt(0);
      checkTargetBand(band1);
    }

    private static void checkTargetBand(Band targetBand) {
        assertNotNull(targetBand);
        assertEquals(targetBand.getName(), "land_cover_CCILandCover-2015");
        assertEquals(ProductData.TYPE_INT16, targetBand.getDataType());
        assertEquals(549 * 549, targetBand.getNumDataElems());
    }

    private static void checkStatistics(Int2ObjectMap<PixelStatistic> statistics) {
        assertEquals(statistics.size(), 1512);

        PixelStatistic pixel = statistics.get(100);
        assertEquals(pixel.getPixelsInRange(),18);
        assertEquals(pixel.getTotalNumberPixels(),122);

        pixel = statistics.get(200);
        assertEquals(pixel.getPixelsInRange(),31);
        assertEquals(pixel.getTotalNumberPixels(),163);

        pixel = statistics.get(300);
        assertEquals(pixel.getPixelsInRange(),4);
        assertEquals(pixel.getTotalNumberPixels(),150);

        pixel = statistics.get(400);
        assertEquals(pixel.getPixelsInRange(),196);
        assertEquals(pixel.getTotalNumberPixels(),197);

        pixel = statistics.get(500);
        assertEquals(pixel.getPixelsInRange(),0);
        assertEquals(pixel.getTotalNumberPixels(),43);

        pixel = statistics.get(600);
        assertEquals(pixel.getPixelsInRange(),1);
        assertEquals(pixel.getTotalNumberPixels(),392);

        pixel = statistics.get(700);
        assertEquals(pixel.getPixelsInRange(),68);
        assertEquals(pixel.getTotalNumberPixels(),84);

        pixel = statistics.get(800);
        assertEquals(pixel.getPixelsInRange(),4);
        assertEquals(pixel.getTotalNumberPixels(),119);

        pixel = statistics.get(900);
        assertEquals(pixel.getPixelsInRange(),67);
        assertEquals(pixel.getTotalNumberPixels(),106);

    }

}
