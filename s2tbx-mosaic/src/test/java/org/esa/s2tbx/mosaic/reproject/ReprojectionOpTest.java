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

package org.esa.s2tbx.mosaic.reproject;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PinDescriptor;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Placemark;
import org.esa.snap.core.datamodel.PlacemarkDescriptor;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.junit.Test;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *  Added test for referenceProduct parameter
 *
 * @author Razvan Dumitrascu
 * @since BEAM 5.0.2
 */

public class ReprojectionOpTest extends AbstractReprojectionOpTest {

    @Test
    public void testGeoLatLon() throws IOException {
        parameterMap.put("crs", WGS84_CODE);
        final Product targetProduct = createReprojectedProduct();

        assertNotNull(targetProduct);
        // because source is rectangular the size of source is preserved
        assertEquals(51, targetProduct.getSceneRasterWidth());
        assertEquals(51, targetProduct.getSceneRasterHeight());
        assertNotNull(targetProduct.getSceneGeoCoding());

        assertPixelValue(targetProduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testUTMWithWktText() throws IOException {
        parameterMap.put("crs", UTM33N_WKT);
        final Product targetProduct = createReprojectedProduct();

        assertNotNull(targetProduct);
        assertPixelValue(targetProduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testWithWktFile() throws IOException {
        parameterMap.put("wktFile", wktFile);
        final Product targetProduct = createReprojectedProduct();

        assertNotNull(targetProduct);
        assertPixelValue(targetProduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testWithCollocationProduct() {
        Map<String, Product> productMap = new HashMap<>(5);
        productMap.put("source", multiSizeSourceProduct);
        parameterMap.put("crs", "AUTO:42002");
        final Product collocationProduct = createReprojectedProduct(productMap);

        productMap = new HashMap<>(5);
        productMap.put("source", multiSizeSourceProduct);
        productMap.put("collocateWith", collocationProduct);
        parameterMap.remove("crs");
        final Product targetProduct = createReprojectedProduct(productMap);
        assertNotNull(targetProduct);
        assertTrue(targetProduct.isCompatibleProduct(collocationProduct, 1.0e-6f));
    }

    @Test
    public void testWithReferenceProduct() {
        Map<String, Product> productMap = new HashMap<>(5);
        productMap.put("source", referenceProduct);
        parameterMap.put("crs", WGS84_CODE);

        final Product referenceProduct = createReprojectedProduct(productMap);
        productMap = new HashMap<>(5);
        productMap.put("source", multiSizeSourceProduct);
        productMap.put("reference", referenceProduct);
        parameterMap.put("pixelSizeX", computeStepX(referenceProduct));
        parameterMap.put("pixelSizeY", computeStepY(referenceProduct));
        parameterMap.put("crs", WGS84_CODE);
        final Product targetProduct = createReprojectedProduct(productMap);
        assertNotNull(targetProduct);
        assertTrue(targetProduct.isCompatibleProduct(referenceProduct, 1.0e-6f));
        for(int index = 0; index< referenceProduct.getNumBands();index++){
            AffineTransform affTransform1 = referenceProduct.getBandAt(index).getImageToModelTransform();
            AffineTransform affTransform2 = targetProduct.getBandAt(index).getImageToModelTransform();
            assertEquals("band " + referenceProduct.getBandAt(index).getName(),affTransform1.getScaleX(), affTransform2.getScaleX(), 1.0e-6 );
            assertEquals("band " + referenceProduct.getBandAt(index).getName(),affTransform1.getScaleY(), affTransform2.getScaleY(), 1.0e-6 );
        }
    }

    @Test
    public void testUTM() throws IOException {
        parameterMap.put("crs", UTM33N_CODE);
        final Product targetProduct = createReprojectedProduct();

        assertNotNull(targetProduct);
        assertPixelValue(targetProduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testStartAndEndTime() throws Exception {
        parameterMap.put("crs", UTM33N_CODE);
        final Product targetPoduct = createReprojectedProduct();
        assertNotNull(targetPoduct.getStartTime());
        assertNotNull(targetPoduct.getEndTime());
        String meanTime = "02-MAY-2017 10:30:30.000000";
        assertEquals(meanTime, targetPoduct.getStartTime().format());
        assertEquals(meanTime, targetPoduct.getEndTime().format());
    }

    @Test
    public void testUTM_Bilinear() throws IOException {
        parameterMap.put("crs", UTM33N_CODE);
        parameterMap.put("resampling", "Bilinear");
        final Product targetProduct = createReprojectedProduct();

        assertNotNull(targetProduct);
        assertNotNull(targetProduct.getSceneGeoCoding());
        // 299, 312
        // 322, 336
        // interpolated = 311.96527 considering fractional accuracy for pixel (24, 14)
        assertPixelValue(targetProduct.getBand(FLOAT_BAND_NAME), 24f, 14f, 299, 1.0e-2);
    }

    @Test
    public void testSpecifyingTargetDimension() throws IOException {
        final int width = 200;
        final int height = 300;
        parameterMap.put("crs", WGS84_CODE);
        parameterMap.put("width", width);
        parameterMap.put("height", height);
        final Product targetProduct = createReprojectedProduct();

        assertNotNull(targetProduct);
        assertEquals(width, targetProduct.getSceneRasterWidth());
        assertEquals(height, targetProduct.getSceneRasterHeight());

        assertPixelValue(targetProduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);
    }

    @Test
    public void testSpecifyingPixelSize() throws IOException {
        final double sizeX = 5; // degree
        final double sizeY = 10;// degree
        parameterMap.put("crs", WGS84_CODE);
        parameterMap.put("pixelSizeX", sizeX);
        parameterMap.put("pixelSizeY", sizeY);
        final Product targetProduct = createReprojectedProduct();

        assertNotNull(targetProduct);
        assertEquals(5, targetProduct.getSceneRasterWidth());
        assertEquals(3, targetProduct.getSceneRasterHeight());
    }

    @Test
    public void testSpecifyingReferencing() throws IOException {
        parameterMap.put("crs", WGS84_CODE);
        parameterMap.put("referencePixelX", 0.5);
        parameterMap.put("referencePixelY", 0.5);
        parameterMap.put("easting", 9.0);   // just move it 3° degrees eastward
        parameterMap.put("northing", 52.0); // just move it 2° degrees up
        parameterMap.put("orientation", 0.0);
        final Product targetProduct = createReprojectedProduct();
        assertNotNull(targetProduct);
        final GeoPos geoPos = targetProduct.getSceneGeoCoding().getGeoPos(new PixelPos(0.5f, 0.5f), null);
        assertEquals(new GeoPos(52.0f, 9.0f), geoPos);
        assertPixelValue(targetProduct.getBand(FLOAT_BAND_NAME), 23.5f, 13.5f, (double) 299, EPS);

    }

    @Test
    public void testIncludeTiePointGrids() throws Exception {
        parameterMap.put("crs", WGS84_CODE);
        Product targetProduct = createReprojectedProduct();

        TiePointGrid[] tiePointGrids = targetProduct.getTiePointGrids();
        assertNotNull(tiePointGrids);
        assertEquals(0, tiePointGrids.length);
        Band latGrid = targetProduct.getBand("latGrid");
        assertNotNull(latGrid);

        parameterMap.put("includeTiePointGrids", false);
        targetProduct = createReprojectedProduct();
        tiePointGrids = targetProduct.getTiePointGrids();
        assertNotNull(tiePointGrids);
        assertEquals(0, tiePointGrids.length);
        latGrid = targetProduct.getBand("latGrid");
        assertNull(latGrid);
    }

    @Test
    public void testCopyPlacemarkGroups() throws IOException {
        final PlacemarkDescriptor pinDescriptor = PinDescriptor.getInstance();
        final Placemark pin = Placemark.createPointPlacemark(pinDescriptor, "P1", "", "", new PixelPos(1.5f, 1.5f), null, multiSizeSourceProduct.getSceneGeoCoding());
        final Placemark gcp = Placemark.createPointPlacemark(pinDescriptor, "G1", "", "", new PixelPos(2.5f, 2.5f), null, multiSizeSourceProduct.getSceneGeoCoding());

        multiSizeSourceProduct.getPinGroup().add(pin);
        multiSizeSourceProduct.getGcpGroup().add(gcp);

        parameterMap.put("crs", WGS84_CODE);
        Product targetProduct = createReprojectedProduct();

        assertEquals(1, targetProduct.getPinGroup().getNodeCount());
        assertEquals(1, targetProduct.getGcpGroup().getNodeCount());
        final Placemark pin2 = targetProduct.getPinGroup().get(0);
        final Placemark gcp2 = targetProduct.getGcpGroup().get(0);

        assertEquals("P1", pin2.getName());
        assertEquals("G1", gcp2.getName());

        assertEquals(pin.getGeoPos(), pin2.getGeoPos());
        assertEquals(gcp.getGeoPos(), gcp2.getGeoPos());
        assertNotNull(pin2.getPixelPos());
        assertNotNull(gcp2.getPixelPos());
    }

    private double computeStepX(Product product){
        double pixelSizeX = 0.0;
        OptionalDouble result = Arrays.stream(product.getBands())
                .mapToDouble(band -> Math.abs(band.getSourceImage().getModel().getImageToModelTransform(0).getScaleX()))
                .min();
        if (result.isPresent()) {
            return result.getAsDouble();
        } else {
            return pixelSizeX;
        }
    }

    private double computeStepY(Product product){
        double pixelSizeY = 0.0;
        OptionalDouble result = Arrays.stream(product.getBands())
                .mapToDouble(band -> Math.abs(band.getSourceImage().getModel().getImageToModelTransform(0).getScaleY()))
                .min();
        if (result.isPresent()) {
            return result.getAsDouble();
        } else {
            return pixelSizeY;
        }
    }
}
