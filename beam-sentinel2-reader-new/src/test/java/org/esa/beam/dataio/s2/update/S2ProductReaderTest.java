package org.esa.beam.dataio.s2.update;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.junit.*;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class S2ProductReaderTest {

    private ProductReader readerInstance;
    //exchange this to the directory containing the testdata
    private String testDataDir = "C:\\Users\\tonio\\Desktop\\Produkte\\S2_updated_testdata\\";
    private S2PlugIn s2PPlugIn;

    @Before
    public void setup() {
        s2PPlugIn = new S2PlugIn();
    }

    @Test
//    @Ignore
    public void testReadingL1CTileFromMetadataFile() throws Exception {
        final File file = new File(testDataDir + "Level-1C_User_Product\\GRANULE\\S2A_HOWL_MSI_L1C_TL_CGS3_20131012T012355_049_15SWD\\S2A_HOWL_MSI_L1C_TL_CGS3_20131012T012355_049_15SWD.xml");
        if(s2PPlugIn.getDecodeQualification(file).equals(DecodeQualification.INTENDED)) {
            readerInstance = s2PPlugIn.createReaderInstance();
        }

        final Product product = readerInstance.readProductNodes(file, null);

        testProduct(product);
    }

    @Test
//    @Ignore
    public void testReadingL2ATileFromMetadataFile() throws Exception {
        final File file = new File(testDataDir + "Level-2A_User_Product\\GRANULE\\S2A_HOWL_MSI_L2A_TL_USER_20130430T084516_049_15SWD\\S2A_HOWL_MSI_L2A_TL_USER_20130430T084516_049_15SWD.xml");
        if(s2PPlugIn.getDecodeQualification(file).equals(DecodeQualification.INTENDED)) {
            readerInstance = s2PPlugIn.createReaderInstance();
        }

        final Product product = readerInstance.readProductNodes(file, null);

        testProduct(product);
    }

    @Test
//    @Ignore
    public void testReadingL1CProduct() throws Exception {
        File file = new File(testDataDir + "Level-1C_User_Product\\S2A_HOWL_MTD_DMPL1C_R049_V20131012T012355_20131012T012355_C0001.xml");
        if(s2PPlugIn.getDecodeQualification(file).equals(DecodeQualification.INTENDED)) {
            readerInstance = s2PPlugIn.createReaderInstance();
        }

        final Product product = readerInstance.readProductNodes(file, null);

        testProduct(product);
    }

    @Test
//    @Ignore
    public void testReadingL2AProduct() throws Exception {
        File file = new File(testDataDir + "Level-2A_User_Product\\S2A_HOWL_MTD_DMPL2A_R049_V201310430084516_20131012T012355_C0001.xml");
        if(s2PPlugIn.getDecodeQualification(file).equals(DecodeQualification.INTENDED)) {
            readerInstance = s2PPlugIn.createReaderInstance();
        }

        final Product product = readerInstance.readProductNodes(file, null);

        testProduct(product);
    }

    private void testProduct(Product product) throws Exception{
        assertNotNull(product);
        assertEquals(10960, product.getSceneRasterWidth());
        assertEquals(10960, product.getSceneRasterHeight());
        Band band = product.getBand("B3");
        if(band == null) {
            band = product.getBand("B03");
        }
        assertNotNull(band);

        final int[] pixels = new int[16 * 16];
        band.readPixels(0, 0, 16, 16, pixels);

        final RenderedImage image = band.getSourceImage().getImage(5);
        final Raster data = image.getData();
        assertNotNull(data);

        product.dispose();
    }

} 