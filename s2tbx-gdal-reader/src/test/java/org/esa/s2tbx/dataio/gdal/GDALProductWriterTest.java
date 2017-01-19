package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import junit.framework.TestCase;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * The system properties to set:
 * snap.reader.tests.data.dir : the test folder
 *
 * @author Jean Coravu
 */
public class GDALProductWriterTest extends TestCase {
    private GDALProductWriterPlugIn writerPlugIn;
    private GDALProductReaderPlugin readerPlugIn;
    private Path testsFolderPath;

    public GDALProductWriterTest() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        GDALInstaller installer = new GDALInstaller();
        installer.install();

        checkTestDirectoryExists();

        if (GdalInstallInfo.INSTANCE.isPresent()) {
            GDALDriverInfo[] writerDrivers = GDALUtils.loadAvailableWriterDrivers();
            this.writerPlugIn = new GDALProductWriterPlugIn(writerDrivers);
            this.readerPlugIn = new GDALProductReaderPlugin();
        }
    }

    public final void testWriterDrivers() {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            GDALDriverInfo[] writerDrivers = this.writerPlugIn.getWriterDrivers();
            assertNotNull(writerDrivers);

            Map<String, String> extensionMap = new HashMap<String, String>();
            extensionMap.put("KEA", ".kea");
            extensionMap.put("netCDF", ".nc");
            extensionMap.put("GTiff", ".tif");
            extensionMap.put("NITF", ".ntf");
            extensionMap.put("HFA", ".img");
            extensionMap.put("BMP", ".bmp");
            extensionMap.put("PCIDSK", ".pix");
            extensionMap.put("ILWIS", ".mpr");
            extensionMap.put("SGI", ".rgb");
            extensionMap.put("RMF", ".rsw");
            extensionMap.put("RST", ".rst");
            extensionMap.put("GSBG", ".grd");
            extensionMap.put("GS7BG", ".grd");
            extensionMap.put("PNM", ".pnm");
            extensionMap.put("MFF", ".hdr");
            extensionMap.put("BT", ".bt");
            extensionMap.put("GTX", ".gtx");
            extensionMap.put("KRO", ".kro");
            extensionMap.put("SAGA", ".sdat");

            assertEquals(writerDrivers.length, extensionMap.size());

            Map<String, String> creationTypesMap = new HashMap<String, String>();
            creationTypesMap.put("KEA", "Byte Int16 UInt16 Int32 UInt32 Float32 Float64");
            creationTypesMap.put("netCDF", "null");
            creationTypesMap.put("GTiff", "Byte UInt16 Int16 UInt32 Int32 Float32 Float64 CInt16 CInt32 CFloat32 CFloat64");
            creationTypesMap.put("NITF", "Byte UInt16 Int16 UInt32 Int32 Float32");
            creationTypesMap.put("HFA", "Byte Int16 UInt16 Int32 UInt32 Float32 Float64 CFloat32 CFloat64");
            creationTypesMap.put("BMP", "Byte");
            creationTypesMap.put("PCIDSK", "Byte UInt16 Int16 Float32 CInt16 CFloat32");
            creationTypesMap.put("ILWIS", "Byte Int16 Int32 Float64");
            creationTypesMap.put("SGI", "Byte");
            creationTypesMap.put("RMF", "Byte Int16 Int32 Float64");
            creationTypesMap.put("RST", "Byte Int16 Float32");
            creationTypesMap.put("GSBG", "Byte Int16 UInt16 Float32");
            creationTypesMap.put("GS7BG", "Byte Int16 UInt16 Float32 Float64");
            creationTypesMap.put("PNM", "Byte UInt16");
            creationTypesMap.put("MFF", "Byte UInt16 Float32 CInt16 CFloat32");
            creationTypesMap.put("BT", "Int16 Int32 Float32");
            creationTypesMap.put("GTX", "Float32");
            creationTypesMap.put("KRO", "Byte UInt16 Float32");
            creationTypesMap.put("SAGA", "Byte Int16 UInt16 Int32 UInt32 Float32 Float64");

            assertEquals(writerDrivers.length, creationTypesMap.size());

            for (int k = 0; k < writerDrivers.length; k++) {
                String driverExtension = extensionMap.get(writerDrivers[k].getDriverName());
                assertNotNull(driverExtension);
                assertEquals(driverExtension, writerDrivers[k].getExtensionName());

                String driverCreationTypes = creationTypesMap.get(writerDrivers[k].getDriverName());
                StringTokenizer str = new StringTokenizer(driverCreationTypes, " ");
                while (str.hasMoreTokens()) {
                    String gdalDataTypeName = str.nextToken();
                    int gdalDataType = gdal.GetDataTypeByName(gdalDataTypeName);
                    boolean result = writerDrivers[k].canExportProduct(gdalDataType);
                    assertTrue(result);
                }
            }
        }
    }

    public final void testWriteFileOnDisk() throws IOException {
        if (!GdalInstallInfo.INSTANCE.isPresent()) {
            return;
        }

        Path tempTestsFolderPath = this.testsFolderPath.resolve("_temp");
        Path gdalTestsFolderPath = tempTestsFolderPath.resolve("gdal_writer_tests");
        if (!Files.exists(gdalTestsFolderPath)) {
            Files.createDirectories(gdalTestsFolderPath);
        }

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

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        this.testsFolderPath = Paths.get(testDirectoryPathProperty);
        if (!Files.exists(this.testsFolderPath)) {
            fail("The test directory path '"+testDirectoryPathProperty+"' is not valid.");
        }
    }
}
