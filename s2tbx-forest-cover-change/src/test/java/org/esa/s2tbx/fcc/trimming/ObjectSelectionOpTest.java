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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class ObjectSelectionOpTest extends AbstractOpTest  {

    @Test
    public void testObObjectSelectionOp() throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {

        Path folder = this.forestCoverChangeTestsFolderPath.resolve("object-selection");
        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        File segmentationProductFile = folder.resolve("S2A_20160713T125925_A005524_T35UMP_grm.dim").toFile();
        Product sourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(segmentationProductFile, null);

        File landCoverProductFile = folder.resolve("S2A_20160713T125925_A005524_T35UMP_grm_landCover.dim").toFile();
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
        assertEquals("land_cover_CCILandCover-2015", targetBand.getName());
        assertEquals(ProductData.TYPE_INT16, targetBand.getDataType());
        assertEquals(549 * 549, targetBand.getNumDataElems());
    }

    private static void checkStatistics(Int2ObjectMap<PixelStatistic> statistics) {
        assertEquals(statistics.size(), 1512);

        PixelStatistic pixel = statistics.get(100);
        assertEquals(18, pixel.getPixelsInRange());
        assertEquals(122, pixel.getTotalNumberPixels());

        pixel = statistics.get(200);
        assertEquals(31, pixel.getPixelsInRange());
        assertEquals(163, pixel.getTotalNumberPixels());

        pixel = statistics.get(300);
        assertEquals(4, pixel.getPixelsInRange());
        assertEquals(150, pixel.getTotalNumberPixels());

        pixel = statistics.get(400);
        assertEquals(196, pixel.getPixelsInRange());
        assertEquals(197, pixel.getTotalNumberPixels());

        pixel = statistics.get(500);
        assertEquals(0, pixel.getPixelsInRange());
        assertEquals(43, pixel.getTotalNumberPixels());

        pixel = statistics.get(600);
        assertEquals(1, pixel.getPixelsInRange());
        assertEquals(392, pixel.getTotalNumberPixels());

        pixel = statistics.get(700);
        assertEquals(68, pixel.getPixelsInRange());
        assertEquals(84, pixel.getTotalNumberPixels());

        pixel = statistics.get(800);
        assertEquals(4, pixel.getPixelsInRange());
        assertEquals(119, pixel.getTotalNumberPixels());

        pixel = statistics.get(900);
        assertEquals(67, pixel.getPixelsInRange());
        assertEquals(106, pixel.getTotalNumberPixels());

    }

}
