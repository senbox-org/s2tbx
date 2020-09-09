package org.esa.s2tbx.dataio.jp2;

import org.esa.s2tbx.dataio.jp2.internal.JP2Constants;

import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.datamodel.VirtualBand;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.lib.openjpeg.activator.OpenJPEGActivator;
import org.geotools.referencing.CRS;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

/**
 *  @author  Razvan Dumitrascu
 *  @since 5.0.2.
 */
public class JP2ProductWriteReadTest {

    private final String FILE_NAME = "test_product.jp2";
    private Product outProduct;
    private File location;
    private static Path testsFolderPath;

    @BeforeClass
    public static void oneTimeSetUp() throws IOException {
        Path temp = Files.createTempDirectory("_temp");
        testsFolderPath = temp;
        if (!Files.exists(testsFolderPath)) {
            fail("The test directory path '"+temp+"' is not valid.");
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        if (!FileUtils.deleteTree(testsFolderPath.toFile())) {
            fail("Unable to delete test directory");
        }
    }

    @Before
    public void setup() {
        final int width = 14;
        final int height = 14;
        OpenJPEGActivator openJPEGActivator = new OpenJPEGActivator();
        openJPEGActivator.start();
        outProduct = new Product("JP2Product", "JPEG-2000", width, height);
        final Band bandInt16 = outProduct.addBand("band_1", ProductData.TYPE_INT16);
        bandInt16.setDataElems(createShortData(getProductSize(), 23));
        ImageManager.getInstance().getSourceImage(bandInt16, 0);
    }

    @After
    public void tearDown() {
        if (!(new File(testsFolderPath.toFile(), FILE_NAME)).delete()) {
            fail("Unable to delete test file");
        }}

    @Test
    public void testWriteReadBeamMetadata() throws IOException {
        final Band expectedBand = outProduct.getBand("band_1");
        expectedBand.setLog10Scaled(false);
        expectedBand.setNoDataValueUsed(false);

        final Product inProduct = writeReadProduct();
        try {
            assertEquals(outProduct.getName(), inProduct.getName());
            assertEquals(outProduct.getProductType(), inProduct.getProductType());
            assertEquals(outProduct.getNumBands(), inProduct.getNumBands());

            final Band actualBand = inProduct.getBandAt(0);
            assertEquals(expectedBand.getName(), actualBand.getName());
            assertEquals(expectedBand.getDataType(), actualBand.getDataType());
            assertEquals(expectedBand.isLog10Scaled(), actualBand.isLog10Scaled());
            assertEquals(expectedBand.isNoDataValueUsed(), actualBand.isNoDataValueUsed());
        } finally {
            inProduct.dispose();
        }
    }

    @Test
    public void testWriteReadVirtualBandIsNotExcludedInProduct() throws IOException {
        final VirtualBand virtualBand = new VirtualBand("band_2", ProductData.TYPE_UINT16,
                outProduct.getSceneRasterWidth(),
                outProduct.getSceneRasterHeight(), "X * Y");
        outProduct.addBand(virtualBand);
        final Product inProduct = writeReadProduct();
        try {
            assertEquals(2, inProduct.getNumBands());
            assertNotNull(inProduct.getBand("band_2"));
        } finally {
            inProduct.dispose();
        }
    }

    @Test
    public void testWriteReadTiePointGeoCoding() throws IOException {
        setTiePointGeoCoding(outProduct);
        final Band bandUInt8 = outProduct.addBand("band_2", ProductData.TYPE_UINT16);
        bandUInt8.setDataElems(createShortData(getProductSize(), 23));
        final Product inProduct = writeReadProduct();
        try {
            assertEquals(outProduct.getName(), inProduct.getName());
            assertEquals(outProduct.getProductType(), inProduct.getProductType());
            assertEquals(outProduct.getNumBands(), inProduct.getNumBands());
            assertEquals(outProduct.getBandAt(0).getName(), inProduct.getBandAt(0).getName());
            assertEquals(outProduct.getBandAt(0).getDataType(), inProduct.getBandAt(0).getDataType());
            assertEquals(outProduct.getBandAt(0).getScalingFactor(), inProduct.getBandAt(0).getScalingFactor(), 1.0e-6);
            assertEquals(outProduct.getBandAt(0).getScalingOffset(), inProduct.getBandAt(0).getScalingOffset(), 1.0e-6);
            assertEquals(location, inProduct.getFileLocation());
            assertNotNull(inProduct.getSceneGeoCoding());
            assertNotNull(outProduct.getSceneGeoCoding());
            assertEquals(inProduct.getSceneGeoCoding().canGetGeoPos(), outProduct.getSceneGeoCoding().canGetGeoPos());
            assertEquals(inProduct.getSceneGeoCoding().isCrossingMeridianAt180(), outProduct.getSceneGeoCoding().isCrossingMeridianAt180());

            if (inProduct.getSceneGeoCoding() instanceof CrsGeoCoding) {
                assertEquals(CrsGeoCoding.class, outProduct.getSceneGeoCoding().getClass());
                CRS.equalsIgnoreMetadata(inProduct.getSceneGeoCoding(), outProduct.getSceneGeoCoding());
            } else if (inProduct.getSceneGeoCoding() instanceof TiePointGeoCoding) {
                assertEquals(TiePointGeoCoding.class, outProduct.getSceneGeoCoding().getClass());
            }
            final int width = outProduct.getSceneRasterWidth();
            final int height = outProduct.getSceneRasterHeight();
            GeoPos geoPos1 = null;
            GeoPos geoPos2 = null;
            final String msgPattern = "%s at [%d,%d] is not equal:";
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    final PixelPos pixelPos = new PixelPos(i, j);
                    geoPos1 = inProduct.getSceneGeoCoding().getGeoPos(pixelPos, geoPos1);
                    geoPos2 = outProduct.getSceneGeoCoding().getGeoPos(pixelPos, geoPos2);
                    assertEquals(String.format(msgPattern, "Latitude", i, j), geoPos1.lat, geoPos2.lat, 1e-6f);
                    assertEquals(String.format(msgPattern, "Longitude", i, j), geoPos1.lon, geoPos2.lon, 1e-6f);
                }
            }
        } finally {
            inProduct.dispose();
        }
    }

    private static void setTiePointGeoCoding(final Product product) {
        final TiePointGrid latGrid = new TiePointGrid("latitude", 2, 2, 0, 0, 14,14, new float[]{
                85, 83,
                65, 63
        },TiePointGrid.DISCONT_NONE);

        final TiePointGrid lonGrid = new TiePointGrid("longitude", 2, 2, 0, 0, 14, 14, new float[]{
                -15, 5,
                -17, 3
        },TiePointGrid.DISCONT_AT_180);

        product.addTiePointGrid(latGrid);
        product.addTiePointGrid(lonGrid);
        product.setSceneGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
    }

    private static short[] createShortData(final int size, final int offset) {
        final short[] shorts = new short[size];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = (short) (i + offset);
        }
        return shorts;
    }

    private static byte[] createByteData(final int size, final int offset) {
        final byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i + offset);
        }
        return bytes;
    }

    private int getProductSize() {
        final int w = outProduct.getSceneRasterWidth();
        final int h = outProduct.getSceneRasterHeight();
        return w * h;
    }

    private Product writeReadProduct() throws IOException {
        location = new File(testsFolderPath.toFile(), FILE_NAME);
        final String JP2FormatName = JP2Constants.FORMAT_NAMES[0];
        ProductIO.writeProduct(outProduct, location.getAbsolutePath(), JP2FormatName);
        return ProductIO.readProduct(location, JP2FormatName);
    }

}
