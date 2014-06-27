package org.esa.beam.dataio.s2;

import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.junit.Test;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Norman Fomferra
 */
public class Sentinel2ProductReaderTest {

    @Test
    public void testReader() throws Exception {
        final URL resource = getClass().getResource("l1c/IMG_GPPL1C_054_20091210235100_20091210235130_02_000000_15SUC.jp2");
        assertNotNull(resource);
        final File file = new File(resource.toURI());

        final Sentinel2ProductReaderPlugIn sentinel2ProductReaderPlugIn = new Sentinel2ProductReaderPlugIn();
        final ProductReader readerInstance = sentinel2ProductReaderPlugIn.createReaderInstance();
        final Product product = readerInstance.readProductNodes(file, null);
        assertNotNull(product);
        assertEquals(10960, product.getSceneRasterWidth());
        assertEquals(10960, product.getSceneRasterHeight());
        assertEquals(4, product.getNumBands());
        final Band band = product.getBand("B3");
        assertNotNull(band);

        final int[] pixels = new int[16 * 16];
        band.readPixels(0, 0, 16, 16, pixels);

        final RenderedImage image = band.getSourceImage().getImage(5);
        final Raster data = image.getData();
        assertNotNull(data);

        product.dispose();
    }
}
