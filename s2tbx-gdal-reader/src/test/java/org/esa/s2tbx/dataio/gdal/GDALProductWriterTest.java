package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.gdal.activator.GDALDriverInfo;
import org.esa.s2tbx.dataio.gdal.activator.GDALPlugInActivator;
import org.esa.snap.core.dataio.ProductWriter;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.utils.TestUtil;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import javax.media.jai.JAI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * The system properties to set:
 * gdal.distribution.root.dir : the folder containing the GDAL distribution
 * snap.writer.tests.data.dir : the folder to create a temp folder used to write the products
 *
 * @author Jean Coravu
 */
public class GDALProductWriterTest extends AbstractGDALPlugInTest {
    private GDALProductWriterPlugIn writerPlugIn;
    private GDALProductReaderPlugin readerPlugIn;
    private Path testFolderPath;

    public GDALProductWriterTest() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        checkWriteTestDirectoryExists();

        GDALDriverInfo[] writerDrivers = GDALUtils.loadAvailableWriterDrivers();
        if (writerDrivers != null && writerDrivers.length > 0) {
            this.writerPlugIn = new GDALProductWriterPlugIn(writerDrivers);
            this.readerPlugIn = new GDALProductReaderPlugin();
        }
    }

    private void checkWriteTestDirectoryExists() {
        String testDirectoryPathPropertyName = "snap.writer.tests.data.dir";

        String testDirectoryPathProperty = System.getProperty(testDirectoryPathPropertyName);
        assertNotNull("The system property '" + testDirectoryPathPropertyName + "' representing the test directory used to write the products is not set.", testDirectoryPathProperty);
        this.testFolderPath = Paths.get(testDirectoryPathProperty);
        if (!this.testFolderPath.toFile().isDirectory()) {
            fail("The test directory path '"+testDirectoryPathProperty+"' is not valid.");
        }
    }

    public final void testWriteFileOnDisk() throws IOException {
        Path gdalTestsFolderPath = this.testFolderPath.resolve("gdal-writer-tests");
        Files.createDirectories(gdalTestsFolderPath);

        try {
            GDALDriverInfo[] writerDrivers = this.writerPlugIn.getWriterDrivers();
            for (int k=0; k<writerDrivers.length; k++) {
                GDALDriverInfo driverInfo = writerDrivers[k];
                int gdalDataType = gdalconstConstants.GDT_Byte;
                String creationDataTypes = driverInfo.getCreationDataTypes();
                if (driverInfo.getCreationDataTypes() != null) {
                    int index = creationDataTypes.indexOf(" ");
                    if (index < 0) {
                        index = creationDataTypes.length();
                    }
                    String gdalDataTypeName = creationDataTypes.substring(0, index).trim();
                    gdalDataType = gdal.GetDataTypeByName(gdalDataTypeName);
                }

                int bandDataType = GDALUtils.getBandDataType(gdalDataType);
                File file = new File(gdalTestsFolderPath.toFile(), "tempFile" + driverInfo.getExtensionName());
                file.delete();
                try {
                    Product product = new Product("tempProduct", "GDAL", 20, 30);
                    product.setPreferredTileSize(JAI.getDefaultTileSize());
                    Band firstBand = product.addBand("band_1", bandDataType);

                    ProductData data = firstBand.createCompatibleRasterData();
                    for (int i = 0; i < firstBand.getRasterWidth() * firstBand.getRasterHeight(); i++) {
                        int value = i + 1;
                        if (bandDataType == ProductData.TYPE_UINT8) {
                            data.setElemIntAt(i, value);
                        } else if (bandDataType == ProductData.TYPE_INT16) {
                            data.setElemIntAt(i, value);
                        } else if (bandDataType == ProductData.TYPE_UINT16) {
                            data.setElemUIntAt(i, value);
                        } else if (bandDataType == ProductData.TYPE_INT32) {
                            data.setElemLongAt(i, value);
                        } else if (bandDataType == ProductData.TYPE_UINT32) {
                            data.setElemLongAt(i, value);
                        } else if (bandDataType == ProductData.TYPE_FLOAT32) {
                            data.setElemFloatAt(i, value);
                        } else if (bandDataType == ProductData.TYPE_FLOAT64) {
                            data.setElemDoubleAt(i, value);
                        }
                    }
                    firstBand.setData(data);

                    ProductWriter productWriter = this.writerPlugIn.createWriterInstance();
                    try {
                        productWriter.writeProductNodes(product, file);

                        int width = product.getSceneRasterWidth();
                        int height = product.getSceneRasterHeight();
                        int bandCount = product.getNumBands();
                        for (int i = 0; i < bandCount; i++) {
                            Band band = product.getBandAt(i);
                            productWriter.writeBandRasterData(band, 0, 0, width, height, band.getData(), ProgressMonitor.NULL);
                        }
                        productWriter.flush();
                    } finally {
                        productWriter.close();
                    }

                    assertTrue(file.length() > 0);

                    GDALProductReader reader = (GDALProductReader)this.readerPlugIn.createReaderInstance();
                    Product finalProduct = reader.readProductNodes(file, null);
                    assertNotNull(finalProduct);

                    assertEquals(product.getSceneRasterWidth(), finalProduct.getSceneRasterWidth());
                    assertEquals(product.getSceneRasterHeight(), finalProduct.getSceneRasterHeight());
                    assertEquals(product.getNumBands(), finalProduct.getNumBands());
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    file.delete();
                }
            }
        } finally {
            FileUtils.deleteTree(gdalTestsFolderPath.toFile());
        }
    }
}
