package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import junit.framework.TestCase;
import org.esa.snap.core.dataio.ProductWriter;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;

import javax.media.jai.JAI;
import java.io.File;
import java.io.IOException;

/**
 * @author Jean Coravu
 */
public abstract class AbstractGDALDriverProductWriterTest extends TestCase {
    private final AbstractGDALProductWriterPlugIn plugIn;

    protected AbstractGDALDriverProductWriterTest(AbstractGDALProductWriterPlugIn plugIn) {
        this.plugIn = plugIn;
    }

    public final void testWriteFileOnDisk() throws IOException {
        File file = new File("D:/tempFile.gdal");
        file.delete();
        try {
            Product product = new Product("tempProduct", "GDAL", 20, 30);
            product.setPreferredTileSize(JAI.getDefaultTileSize());
            Band firstBand = product.addBand("band_1", ProductData.TYPE_UINT8);

            ProductData data = firstBand.createCompatibleRasterData();
            for (int i = 0; i < firstBand.getRasterWidth() * firstBand.getRasterHeight(); i++) {
                int value = i + 1;
                data.setElemIntAt(i, value);
                //data.setElemUIntAt(i, value);
                //data.setElemFloatAt(i, value);
                //data.setElemDoubleAt(i, value);
            }
            firstBand.setData(data);

            ProductWriter productWriter = this.plugIn.createWriterInstance();
            try {
                productWriter.writeProductNodes(product, file);

                int width = product.getSceneRasterWidth();
                int height = product.getSceneRasterHeight();
                int bandCount = product.getNumBands();
                for (int i=0; i<bandCount; i++) {
                    Band band = product.getBandAt(i);
                    productWriter.writeBandRasterData(band, 0, 0, width, height, band.getData(), ProgressMonitor.NULL);
                }
                productWriter.flush();
            } finally {
                productWriter.close();
            }

            assertTrue(file.length() > 0);
        } finally {
            file.delete();
        }
    }
}
