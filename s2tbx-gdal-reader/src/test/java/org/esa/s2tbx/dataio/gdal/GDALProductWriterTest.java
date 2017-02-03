package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.gdal.activator.GDALDriverInfo;
import org.esa.s2tbx.dataio.gdal.reader.GDALProductReader;
import org.esa.s2tbx.dataio.gdal.reader.plugins.*;
import org.esa.s2tbx.dataio.gdal.writer.plugins.*;
import org.esa.snap.core.dataio.ProductWriter;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.io.FileUtils;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.geotools.referencing.CRS;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.JAI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The system properties to set:
 * snap.reader.tests.data.dir : the test folder
 *
 * @author Jean Coravu
 */
public class GDALProductWriterTest {
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
    public void setUp() throws Exception {
        GDALInstaller installer = new GDALInstaller();
        installer.install();
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            GDALUtils.initDrivers();
        }
    }

    @Test
    public final void testWriteFileOnDisk() throws IOException, FactoryException, TransformException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            Path gdalTestsFolderPath = this.testsFolderPath;

            int sceneRasterWidth = 20;
            int sceneRasterHeight = 30;
            double originX = 0.0d;
            double originY = 0.0d;
            double pixelSizeX = 1.234d;
            double pixelSizeY = 5.678d;
            String wellKnownText = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
            CoordinateReferenceSystem crsToSave = CRS.parseWKT(wellKnownText);
            GeoCoding geoCodingToSave = new CrsGeoCoding(crsToSave, sceneRasterWidth, sceneRasterHeight, originX, originY, pixelSizeX, pixelSizeY);

            Set<String> driverNamesToIgnoreGeoCoding = new HashSet<String>();
            driverNamesToIgnoreGeoCoding.add("netCDF");
            driverNamesToIgnoreGeoCoding.add("NITF");
            driverNamesToIgnoreGeoCoding.add("ILWIS");
            driverNamesToIgnoreGeoCoding.add("RMF");
            driverNamesToIgnoreGeoCoding.add("MFF");

            try {
                List<DriverWriter> driversToTest = buildDriversToTestList();

                for (int k=0; k<driversToTest.size(); k++) {
                    DriverWriter driverWriterToTest = driversToTest.get(k);
                    AbstractDriverProductWriterPlugIn writerPlugIn = driverWriterToTest.getWriterPlugIn();
                    GDALDriverInfo driverInfo = writerPlugIn.getWriterDriver();

                    assertEquals(driverWriterToTest.getFileExtension(), driverInfo.getExtensionName());

                    checkDriverCreationTypes(driverWriterToTest, driverInfo);

                    int gdalDataType = getBandDataTypeToSave(driverInfo);
                    boolean canIgnore = driverNamesToIgnoreGeoCoding.contains(driverInfo.getDriverName());
                    int bandDataType = GDALUtils.getBandDataType(gdalDataType);
                    File file = new File(gdalTestsFolderPath.toFile(), "tempFile" + driverInfo.getExtensionName());
                    try {
                        file.delete();
                        Product product = buildProductToSave(driverInfo, canIgnore, sceneRasterWidth, sceneRasterHeight, geoCodingToSave, bandDataType);

                        checkSaveProductToFile(file, writerPlugIn, product);

                        checkReadProductFromFile(file, driverWriterToTest.getReaderPlugIn(), product, canIgnore);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        file.delete();
                    }
                }
            } finally {}
        }
    }

    private static void checkDriverCreationTypes(DriverWriter driverWriterToTest, GDALDriverInfo driverInfo) {
        String driverCreationTypes = driverWriterToTest.getDriverCreationTypes();
        if (driverCreationTypes != null) {
            StringTokenizer str = new StringTokenizer(driverCreationTypes, " ");
            while (str.hasMoreTokens()) {
                String gdalDataTypeName = str.nextToken();
                int gdalDataType = gdal.GetDataTypeByName(gdalDataTypeName);
                boolean result = driverInfo.canExportProduct(gdalDataType);
                assertTrue(result);
            }
        }
    }

    private static int getBandDataTypeToSave(GDALDriverInfo driverInfo) {
        int gdalDataType = gdalconstConstants.GDT_Byte;
        String creationDataTypes = driverInfo.getCreationDataTypes();
        if (driverInfo.getCreationDataTypes() != null) {
            // get the first data type
            int index = creationDataTypes.indexOf(" ");
            if (index < 0) {
                index = creationDataTypes.length();
            }
            String gdalDataTypeName = creationDataTypes.substring(0, index).trim();
            gdalDataType = gdal.GetDataTypeByName(gdalDataTypeName);
        }
        return gdalDataType;
    }

    private static Product buildProductToSave(GDALDriverInfo driverInfo, boolean canIgnore, int sceneRasterWidth, int sceneRasterHeight, GeoCoding geoCodingToSave, int bandDataType) {
        Product product = new Product("tempProduct", driverInfo.getWriterPluginFormatName(), sceneRasterWidth, sceneRasterHeight);
        if (!canIgnore) {
            product.setSceneGeoCoding(geoCodingToSave);
        }

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

        return product;
    }

    private static void checkSaveProductToFile(File file, AbstractDriverProductWriterPlugIn writerPlugIn, Product productToSave) throws IOException {
        ProductWriter productWriter = writerPlugIn.createWriterInstance();
        try {
            productWriter.writeProductNodes(productToSave, file);

            int width = productToSave.getSceneRasterWidth();
            int height = productToSave.getSceneRasterHeight();
            int bandCount = productToSave.getNumBands();
            for (int i = 0; i < bandCount; i++) {
                Band band = productToSave.getBandAt(i);
                productWriter.writeBandRasterData(band, 0, 0, width, height, band.getData(), ProgressMonitor.NULL);
            }
            productWriter.flush();
        } finally {
            productWriter.close();
        }
        assertTrue(file.length() > 0);
    }

    private static void checkReadProductFromFile(File file, AbstractDriverProductReaderPlugIn readerPlugIn, Product savedProduct, boolean canIgnoreGeoCoding) throws IOException {
        GDALProductReader reader = (GDALProductReader)readerPlugIn.createReaderInstance();
        Product finalProduct = reader.readProductNodes(file, null);
        assertNotNull(finalProduct);

        if (!canIgnoreGeoCoding) {
            GeoCoding loadedGeoCoding = finalProduct.getSceneGeoCoding();
            assertNotNull(loadedGeoCoding);

            GeoCoding geoCodingToSave = savedProduct.getSceneGeoCoding();
            CoordinateReferenceSystem crsToSave = geoCodingToSave.getGeoCRS();

            CoordinateReferenceSystem loadedGeoCodingGeoCRS = loadedGeoCoding.getGeoCRS();
            assertEquals(crsToSave.getCoordinateSystem().getDimension(), loadedGeoCodingGeoCRS.getCoordinateSystem().getDimension());
            assertEquals(crsToSave.getCoordinateSystem().getName().getVersion(), loadedGeoCodingGeoCRS.getCoordinateSystem().getName().getVersion());
            assertNull(loadedGeoCodingGeoCRS.getCoordinateSystem().getRemarks());
            assertNull(loadedGeoCodingGeoCRS.getCoordinateSystem().getName().getAuthority());
        }

        assertEquals(savedProduct.getSceneRasterWidth(), finalProduct.getSceneRasterWidth());
        assertEquals(savedProduct.getSceneRasterHeight(), finalProduct.getSceneRasterHeight());
        assertEquals(savedProduct.getNumBands(), finalProduct.getNumBands());
    }

    private static class DriverWriter {
        private final String driverName;
        private final String fileExtension;
        private final String driverCreationTypes;
        private final AbstractDriverProductReaderPlugIn readerPlugIn;
        private final AbstractDriverProductWriterPlugIn writerPlugIn;

        DriverWriter(String driverName, String fileExtension, String driverCreationTypes, AbstractDriverProductReaderPlugIn readerPlugIn, AbstractDriverProductWriterPlugIn writerPlugIn) {
            this.driverName = driverName;
            this.fileExtension = fileExtension;
            this.driverCreationTypes = driverCreationTypes;
            this.readerPlugIn = readerPlugIn;
            this.writerPlugIn = writerPlugIn;
        }

        public AbstractDriverProductReaderPlugIn getReaderPlugIn() {
            return readerPlugIn;
        }

        public AbstractDriverProductWriterPlugIn getWriterPlugIn() {
            return writerPlugIn;
        }

        public String getDriverName() {
            return driverName;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public String getDriverCreationTypes() {
            return driverCreationTypes;
        }
    }

    private static List<DriverWriter> buildDriversToTestList() {
        List<DriverWriter> driversToTest = new ArrayList<DriverWriter>();
        driversToTest.add(new DriverWriter("KEA", ".kea", "Byte Int16 UInt16 Int32 UInt32 Float32 Float64", new KEADriverProductReaderPlugIn(), new KEADriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("netCDF", ".nc", null, new NetCDFDriverProductReaderPlugIn(), new NetCDFDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("GTiff", ".tif", "Byte UInt16 Int16 UInt32 Int32 Float32 Float64 CInt16 CInt32 CFloat32 CFloat64", new GTiffDriverProductReaderPlugIn(), new GTiffDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("NITF", ".ntf", "Byte UInt16 Int16 UInt32 Int32 Float32", new NITFDriverProductReaderPlugIn(), new NITFDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("HFA", ".img", "Byte Int16 UInt16 Int32 UInt32 Float32 Float64 CFloat32 CFloat64", new HFADriverProductReaderPlugIn(), new HFADriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("BMP", ".bmp", "Byte", new BMPDriverProductReaderPlugIn(), new BMPDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("PCIDSK", ".pix", "Byte UInt16 Int16 Float32 CInt16 CFloat32", new PCIDSKDriverProductReaderPlugIn(), new PCIDSKDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("ILWIS", ".mpr", "Byte Int16 Int32 Float64", new ILWISDriverProductReaderPlugIn(), new ILWISDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("SGI", ".rgb", "Byte", new SGIDriverProductReaderPlugIn(), new SGIDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("RMF", ".rsw", "Byte Int16 Int32 Float64", new RMFDriverProductReaderPlugIn(), new RMFDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("RST", ".rst", "Byte Int16 Float32", new RSTDriverProductReaderPlugIn(), new RSTDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("GSBG", ".grd", "Byte Int16 UInt16 Float32", new GSBGDriverProductReaderPlugIn(), new GSBGDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("GS7BG", ".grd", "Byte Int16 UInt16 Float32 Float64", new GS7BGDriverProductReaderPlugIn(), new GS7BGDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("PNM", ".pnm", "Byte UInt16", new PNMDriverProductReaderPlugIn(), new PNMDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("MFF", ".hdr", "Byte UInt16 Float32 CInt16 CFloat32", new MFFDriverProductReaderPlugIn(), new MFFDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("BT", ".bt", "Int16 Int32 Float32", new BTDriverProductReaderPlugIn(), new BTDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("GTX", ".gtx", "Float32", new GTXDriverProductReaderPlugIn(), new GTXDriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("KRO", ".kro", "Byte UInt16 Float32", new KRODriverProductReaderPlugIn(), new KRODriverProductWriterPlugIn()));
        driversToTest.add(new DriverWriter("SAGA", ".sdat", "Byte Int16 UInt16 Int32 UInt32 Float32 Float64", new SAGADriverProductReaderPlugIn(), new SAGADriverProductWriterPlugIn()));
        return driversToTest;
    }
}
