package org.esa.s2tbx.radiometry;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.internal.TileImpl;
import org.esa.snap.core.util.ProductUtils;
import org.junit.Before;
import org.junit.Test;

import javax.media.jai.operator.ConstantDescriptor;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by dmihailescu on 2/4/2016.
 */
public class DviOpTest {

    class DviOpImplForTest extends DviOp{

        public void setSourceProductImpl(Product sourceProduct) {
            this.sourceProduct = sourceProduct;
        }

        public void setTargetProductImpl(Product targetProduct) {
            this.targetProduct = targetProduct;
        }

        public void setIndices(float i, float j){
            redFactor = i;
            nirFactor = j;
        }
    }

    private int bandDimension = 3;

    private Product prTest;

    //ProductData inputData = ProductData.createInstance(new float[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

    final float[] testFloat32s = new float[]{1.001f, 2.002f, 3.003f, 4.004f, 5.005f, 6.006f};

    @Before
    public void initialize(){

        /*prTest = new Product("ProductTestDvi", "CustomProduct", bandDimension, bandDimension, new AbstractProductReader(null) {
            @Override
            protected Product readProductNodesImpl() throws IOException {
                return new Product("aaa", "type", 3, 3, this);
            }

            @Override
            protected void readBandRasterDataImpl(int i, int i1, int i2, int i3, int i4, int i5, Band band, int i6, int i7, int i8, int i9, ProductData productData, ProgressMonitor progressMonitor) throws IOException {
                band.setData(inputData);

//band.getSampleCoding().
            }
        });*/
        String name = "x";
        prTest = new Product(name, "NO_TYPE", 3, 2);

        Band bandRed = new Band("RED", ProductData.TYPE_FLOAT32, 3, 2);
        bandRed.ensureRasterData();
        bandRed.setPixels(0, 0, 3, 2, testFloat32s);
        prTest.addBand(bandRed);

        Band bandNir = new Band("NIR", ProductData.TYPE_FLOAT32, 3, 2);
        bandNir.ensureRasterData();
        bandNir.setPixels(0, 0, 3, 2, testFloat32s);
        prTest.addBand(bandNir);

        /*prTest.setPreferredTileSize(1,1);
        Band bandRed = addBand(prTest, "RED", 625);
        Band bandNir = addBand(prTest, "NIR", 850);
        */

        //try {
            //bandRed.setSourceImage(ConstantDescriptor.create((float) 3, (float) 3, new Float[]{1f}, null));
        //bandRed.loadRasterData();
        //bandRed.readRasterData(0, 0, bandDimension, bandDimension, inputData);
            //bandNir.readRasterData(0, 0, bandDimension, bandDimension, inputData);
        //}
        //catch (IOException ex){

        //}

    }

    @Test
    public void testComputeTileStackTwoBands(){

        DviOpImplForTest op = new DviOpImplForTest();

        op.setSourceProduct(prTest);
        op.setIndices(1, 0.5f);

        //op.setSourceProductImpl(prTest); //not in context
        op.setTargetProductImpl(prTest);
        op.setRedSourceBand("RED");
        op.setNirSourceBand("NIR");

        Map<Band, Tile> targetTiles = new HashMap<Band, Tile>();

        WritableRaster raster1 = WritableRaster.createBandedRaster(DataBuffer.TYPE_INT, bandDimension, 2, 1, new Point(0, 0));
        Band targetBand1 = new Band(DviOp.BAND_NAME, ProductData.TYPE_FLOAT32, bandDimension, 2);
        targetBand1.ensureRasterData();
        targetBand1.setPixels(0, 0, 3, 2, testFloat32s);
        targetTiles.put(targetBand1, new TileImpl(targetBand1, raster1));
        prTest.addBand(targetBand1);

        WritableRaster raster2 = WritableRaster.createBandedRaster(DataBuffer.TYPE_INT, bandDimension, 2, 1, new Point(0, 0));
        Band targetBand2 = new Band(DviOp.FLAGS_BAND_NAME, ProductData.TYPE_FLOAT32, bandDimension, 2);
        targetBand2.ensureRasterData();
        targetBand2.setPixels(0, 0, 3, 2, testFloat32s);
        targetTiles.put(targetBand2, new TileImpl(targetBand2, raster2));
        prTest.addBand(targetBand2);

        //prTest data should be modified after computeTileStack() call
        op.computeTileStack(targetTiles, new Rectangle(bandDimension, 2), ProgressMonitor.NULL );
    }


    public Band addBand(Product product, String bandName, int wavelength) {
        Band a = new Band(bandName, ProductData.TYPE_FLOAT32, bandDimension, 2);
        a.setSpectralWavelength(wavelength);
        product.addBand(a);

        return a;
    }

}
