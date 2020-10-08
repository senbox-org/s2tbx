/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.mosaic;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.common.MosaicOp;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import javax.media.jai.operator.ConstantDescriptor;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.Raster;
import java.io.IOException;
import static org.junit.Assert.assertEquals;



/**
 * @author Razvan Dumitrascu
 * @since 5.0.2
 */
public class S2tbxMosaicOpTest {

    private static final int WIDTH1 = 10;
    private static final int HEIGHT1 = 10;
    private static final int WIDTH2 = 20;
    private static final int HEIGHT2 = 20;
    private static final int WIDTH3 = 15;
    private static final int HEIGHT3 = 15;

    private static Product product1;
    private static Product product2;
    private static Product product3;
    private static Product product4;

    @BeforeClass
    public static void setup() throws FactoryException, TransformException, IOException {

        product1 = createProduct("P1", 4, -4, 3.0f);
        product2 = createProduct("P2", -5, 5, 5.0f);
        product3 = newProduct("P3", 0, 0, 2.0f);
        product4 = newProduct("P4",4, 4, 7.0f);
    }

    @AfterClass
    public static void teardown() {
        product1.dispose();
        product2.dispose();
        product3.dispose();
        product4.dispose();
    }

    @Test
    public void testMosaickingNativeResolutionTypeBlend() throws IOException {
        final S2tbxMosaicOp op = new S2tbxMosaicOp();
        op.setParameterDefaultValues();

        op.setSourceProducts(product1, product2);
        op.variables = new MosaicOp.Variable[]{
                new MosaicOp.Variable("b1", "b1"),
                new MosaicOp.Variable("b2", "b2"),
                new MosaicOp.Variable("b3", "b3")
        };

        op.westBound = -20.0;
        op.northBound = 20.0;
        op.eastBound = 20.0;
        op.southBound = -20.0;
        op.pixelSizeX = 1.0;
        op.pixelSizeY = 1.0;
        op.nativeResolution = true;
        op.overlappingMethod = "MOSAIC_TYPE_BLEND";

        // execute the operator
        op.execute(ProgressMonitor.NULL);
        // get the operator target product
        final Product product = op.getTargetProduct();

        final GeoPos[] geoPositions = {
                new GeoPos(-5, 3), new GeoPos(4, -4), new GeoPos(0, 0), new GeoPos(-4, 4), new GeoPos(5,3)
        };

        Band b1Band = product.getBand("b1");
        assertSampleValuesFloat(b1Band, geoPositions, new float[]{5.0f, 5.0f, 5.0f, 5.0f, Float.NaN});
        Band b2Band = product.getBand("b2");
        assertSampleValuesFloat(b2Band, geoPositions, new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f});
        Band b3Band = product.getBand("b3");
        assertSampleValuesFloat(b3Band, geoPositions, new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f});

    }
    @Test
    public void testMosaickingNativeResolutionTypeOverlayP1P2() throws IOException {
        final S2tbxMosaicOp op = new S2tbxMosaicOp();
        op.setParameterDefaultValues();

        op.setSourceProducts(product1, product2);
        op.variables = new MosaicOp.Variable[]{
                new MosaicOp.Variable("b1", "b1"),
                new MosaicOp.Variable("b2", "b2"),
                new MosaicOp.Variable("b3", "b3")
        };

        op.westBound = -20.0;
        op.northBound = 20.0;
        op.eastBound = 20.0;
        op.southBound = -20.0;
        op.pixelSizeX = 1.0;
        op.pixelSizeY = 1.0;
        op.nativeResolution = true;
        op.overlappingMethod = "MOSAIC_TYPE_OVERLAY";

        // execute the operator
        op.execute(ProgressMonitor.NULL);
        // get the operator target product
        final Product product = op.getTargetProduct();
        final GeoPos[] geoPositions = {
                new GeoPos(-5, 3), new GeoPos(4, -4), new GeoPos(0, 0), new GeoPos(-4, 4), new GeoPos(5,3)
        };

        Band b1Band = product.getBand("b1");
        assertSampleValuesFloat(b1Band, geoPositions, new float[]{5.0f, 3.0f, 5.0f, 5.0f, Float.NaN});
        Band b2Band = product.getBand("b2");
        assertSampleValuesFloat(b2Band, geoPositions, new float[]{5.0f, 3.0f, 5.0f, 5.0f, 5.0f});
        Band b3Band = product.getBand("b3");
        assertSampleValuesFloat(b3Band, geoPositions, new float[]{5.0f, 3.0f, 5.0f, 5.0f, 5.0f});

    }
    @Test
    public void testMosaickingNativeResolutionTypeOverlayP2P1() throws IOException {
        final S2tbxMosaicOp op = new S2tbxMosaicOp();
        op.setParameterDefaultValues();

        op.setSourceProducts(product2, product1);
        op.variables = new MosaicOp.Variable[]{
                new MosaicOp.Variable("b1", "b1"),
                new MosaicOp.Variable("b2", "b2"),
                new MosaicOp.Variable("b3", "b3")
        };

        op.westBound = -20.0;
        op.northBound = 20.0;
        op.eastBound = 20.0;
        op.southBound = -20.0;
        op.pixelSizeX = 1.0;
        op.pixelSizeY = 1.0;
        op.nativeResolution = true;
        op.overlappingMethod = "MOSAIC_TYPE_OVERLAY";

        // execute the operator
        op.execute(ProgressMonitor.NULL);
        // get the operator target product
        final Product product = op.getTargetProduct();
        final GeoPos[] geoPositions = {
                new GeoPos(-5, 3), new GeoPos(4, -4), new GeoPos(0, 0), new GeoPos(-4, 4), new GeoPos(5,3)
        };

        Band b1Band = product.getBand("b1");
        assertSampleValuesFloat(b1Band, geoPositions, new float[]{5.0f, 5.0f, 5.0f, 5.0f, Float.NaN});
        Band b2Band = product.getBand("b2");
        assertSampleValuesFloat(b2Band, geoPositions, new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f});
        Band b3Band = product.getBand("b3");
        assertSampleValuesFloat(b3Band, geoPositions, new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f});

    }
    @Test
    public void testMosaickingPreferedResolutionTypeOverlayP3P4() throws IOException {
        final S2tbxMosaicOp op = new S2tbxMosaicOp();
        op.setParameterDefaultValues();

        op.setSourceProducts(product3, product4);
        op.variables = new MosaicOp.Variable[]{
                new MosaicOp.Variable("b1", "b1"),
                new MosaicOp.Variable("b2", "b2"),
                new MosaicOp.Variable("b3", "b3")
        };

        op.westBound = -1.0;
        op.northBound = 10.0;
        op.eastBound = 10.0;
        op.southBound = -10.0;
        op.pixelSizeX = 1.0;
        op.pixelSizeY = 1.0;
        op.nativeResolution = false;
        op.overlappingMethod = "MOSAIC_TYPE_OVERLAY";

        // execute the operator
        op.execute(ProgressMonitor.NULL);
        // get the operator target product
        final Product product = op.getTargetProduct();

        final GeoPos[] geoPositions = {
                new GeoPos(0, 0), new GeoPos(3, 3), new GeoPos(4, 4), new GeoPos(6,6), new GeoPos(8,8),new GeoPos(10, 10),  new GeoPos(1, 1)
        };

        Band b1Band = product.getBand("b1");
        assertSampleValuesFloat(b1Band, geoPositions, new float[]{2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 0.0f, 2.0f});
        Band b2Band = product.getBand("b2");
        assertSampleValuesFloat(b2Band, geoPositions, new float[]{2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 0.0f, 2.0f});
        Band b3Band = product.getBand("b3");
        assertSampleValuesFloat(b3Band, geoPositions, new float[]{2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 0.0f, 2.0f});

    }
    @Test
    public void testMosaickingPreferedResolutionTypeOverlayP4P3() throws IOException {
        final S2tbxMosaicOp op = new S2tbxMosaicOp();
        op.setParameterDefaultValues();

        op.setSourceProducts(product4, product3);
        op.variables = new MosaicOp.Variable[]{
                new MosaicOp.Variable("b1", "b1"),
                new MosaicOp.Variable("b2", "b2"),
                new MosaicOp.Variable("b3", "b3")
        };

        op.westBound = -10.0;
        op.northBound = 10.0;
        op.eastBound = 10.0;
        op.southBound = -10.0;
        op.pixelSizeX = 1.0;
        op.pixelSizeY = 1.0;
        op.nativeResolution = false;
        op.overlappingMethod = "MOSAIC_TYPE_OVERLAY";

        // execute the operator
        op.execute(ProgressMonitor.NULL);
        // get the operator target product
        final Product product = op.getTargetProduct();

        final GeoPos[] geoPositions = {
                new GeoPos(0, 0), new GeoPos(3, 3), new GeoPos(4, 4), new GeoPos(6,6), new GeoPos(8,8), new GeoPos(10, 10),  new GeoPos(1, 1)
        };

        Band b1Band = product.getBand("b1");
        assertSampleValuesFloat(b1Band, geoPositions, new float[]{2.0f, 2.0f, 7.0f, 7.0f, 7.0f, 0.0f, 2.0f});
        Band b2Band = product.getBand("b2");
        assertSampleValuesFloat(b2Band, geoPositions, new float[]{2.0f, 2.0f, 7.0f, 7.0f, 7.0f, 0.0f, 2.0f});
        Band b3Band = product.getBand("b3");
        assertSampleValuesFloat(b3Band, geoPositions, new float[]{2.0f, 2.0f, 7.0f, 7.0f, 7.0f, 0.0f, 2.0f});

    }
    @Test
    public void testMosaickingPreferedResolutionTypeBlend() throws IOException {
        final S2tbxMosaicOp op = new S2tbxMosaicOp();
        op.setParameterDefaultValues();

        op.setSourceProducts(product3, product4);
        op.variables = new MosaicOp.Variable[]{
                new MosaicOp.Variable("b1", "b1"),
                new MosaicOp.Variable("b2", "b2"),
                new MosaicOp.Variable("b3", "b3")
        };

        op.westBound = -10.0;
        op.northBound = 10.0;
        op.eastBound = 10.0;
        op.southBound = -10.0;
        op.pixelSizeX = 1.0;
        op.pixelSizeY = 1.0;
        op.nativeResolution = false;
        op.overlappingMethod = "MOSAIC_TYPE_BLEND";

        // execute the operator
        op.execute(ProgressMonitor.NULL);
        // get the operator target product
        final Product product = op.getTargetProduct();

        final GeoPos[] geoPositions = {
                new GeoPos(0, 0), new GeoPos(3, 3), new GeoPos(4, 4), new GeoPos(6,6), new GeoPos(8,8), new GeoPos(10, 10),  new GeoPos(1, 1)
        };

        Band b1Band = product.getBand("b1");
        assertSampleValuesFloat(b1Band, geoPositions, new float[]{2.0f, 2.0f, 7.0f, 7.0f, 7.0f, 0.0f, 2.0f});
        Band b2Band = product.getBand("b2");
        assertSampleValuesFloat(b2Band, geoPositions, new float[]{2.0f, 2.0f, 7.0f, 7.0f, 7.0f, 0.0f, 2.0f});
        Band b3Band = product.getBand("b3");
        assertSampleValuesFloat(b3Band, geoPositions, new float[]{2.0f, 2.0f, 7.0f, 7.0f, 7.0f, 0.0f, 2.0f});

    }
    private void assertSampleValuesFloat(Band Band, GeoPos[] geoPositions, float[] expectedValues) {
        GeoCoding geoCoding = Band.getGeoCoding();
        final Raster b1Raster = Band.getSourceImage().getData();
        for (int i = 0; i < geoPositions.length; i++) {
            PixelPos pp = geoCoding.getPixelPos(geoPositions[i], null);
            final float expectedValue = expectedValues[i];
            final float actualValue = b1Raster.getSampleFloat((int) pp.x, (int) pp.y, 0);
            final String message = String.format("At <%d>:", i);
            assertEquals(message, expectedValue, actualValue, 1.0e-6);
        }
    }

    private void assertSampleValuesInt(Band Band, GeoPos[] geoPositions, int[] expectedValues) {
        GeoCoding geoCoding = Band.getGeoCoding();
        final Raster b1Raster = Band.getSourceImage().getData();
        for (int i = 0; i < geoPositions.length; i++) {
            PixelPos pp = geoCoding.getPixelPos(geoPositions[i], null);
            final int expectedValue = expectedValues[i];
            final int actualValue = b1Raster.getSample((int) pp.x, (int) pp.y, 0);
            final String message = String.format("At <%d>:", i);
            assertEquals(message, expectedValue, actualValue);
        }
    }

    private static Product createProduct(final String name, final int easting, final int northing,
                                         final float bandFillValue) throws FactoryException, TransformException {
        final Product product = new Product(name, "Mosaic", WIDTH2, HEIGHT2);
        final Band band1 = new Band("b1", ProductData.TYPE_FLOAT32, WIDTH1, HEIGHT1);
        band1.setSourceImage(ConstantDescriptor.create((float) WIDTH1, (float) HEIGHT1, new Float[]{bandFillValue}, null));
        product.addBand(band1);
        final Band band2 = new Band("b2", ProductData.TYPE_FLOAT32, WIDTH2, HEIGHT2);
        band2.setSourceImage(ConstantDescriptor.create((float) WIDTH2, (float) HEIGHT2, new Float[]{bandFillValue}, null));
        product.addBand(band2);
        final Band band3 = new Band("b3", ProductData.TYPE_FLOAT32, WIDTH3, HEIGHT3);
        band3.setSourceImage(ConstantDescriptor.create((float) WIDTH3, (float) HEIGHT3, new Float[]{bandFillValue}, null));
        product.addBand(band3);
        final AffineTransform transform = new AffineTransform();
        transform.translate(easting, northing);
        transform.scale(1, -1);
        transform.translate(-0.5, -0.5);
        product.setSceneGeoCoding(
                new CrsGeoCoding(CRS.decode("EPSG:4326", true), new Rectangle(0, 0, WIDTH2, HEIGHT2), transform));
        return product;
    }
    private static Product newProduct(final String name, final int easting, final int northing,
                                         final float bandFillValue) throws FactoryException, TransformException {
        final Product product = new Product(name, "Mosaic", WIDTH2, HEIGHT2);
        final Band band1 = new Band("b1", ProductData.TYPE_FLOAT32, WIDTH1, HEIGHT1);
        band1.setSourceImage(ConstantDescriptor.create((float) WIDTH1, (float) HEIGHT1, new Float[]{bandFillValue}, null));
        product.addBand(band1);
        final Band band2 = new Band("b2", ProductData.TYPE_FLOAT32, WIDTH2, HEIGHT2);
        band2.setSourceImage(ConstantDescriptor.create((float) WIDTH2, (float) HEIGHT2, new Float[]{bandFillValue}, null));
        product.addBand(band2);
        final Band band3 = new Band("b3", ProductData.TYPE_FLOAT32, WIDTH3, HEIGHT3);
        band3.setSourceImage(ConstantDescriptor.create((float) WIDTH3, (float) HEIGHT3, new Float[]{bandFillValue}, null));
        product.addBand(band3);
        final AffineTransform transform = new AffineTransform();
        transform.translate(easting, northing);
        transform.scale(1,1);
        transform.translate(-0.5, -0.5);
        product.setSceneGeoCoding(
                new CrsGeoCoding(CRS.decode("EPSG:4326", true), new Rectangle(0, 0, WIDTH2, HEIGHT2), transform));
        return product;
    }
}
