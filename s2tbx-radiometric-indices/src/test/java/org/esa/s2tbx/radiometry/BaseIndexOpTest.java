/*
 *
 *  * Copyright (C) 2016 CS ROMANIA
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.radiometry;

import junit.framework.TestCase;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.junit.Assert;

import java.awt.image.Raster;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;


public abstract class BaseIndexOpTest<O extends BaseIndexOp> extends TestCase {

    protected Product sourceProduct;
    protected int width;
    protected int height;
    protected Class<O> operatorClass;
    protected float[] expectedValues;
    protected float threshold;
    protected Map<String, Float> annotatedFields;

    public BaseIndexOpTest() {
        threshold = 0.000001f;
    }

    public void testOperator() throws OperatorException {
        O operator = null;
        try {
            operatorClass = (Class<O>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            @SuppressWarnings("unchecked") Constructor<O> ctor = operatorClass.getConstructor();
            operator = ctor.newInstance();
        } catch (Exception e1) {
            try {
                @SuppressWarnings("unchecked") Constructor<O> ctor = operatorClass.getDeclaredConstructor();
                operator = ctor.newInstance();
            } catch (Exception e) {
                throw new OperatorException(e.getMessage());
            }
        }
        operator.setSourceProduct(sourceProduct);
        try {
            for (Map.Entry<String, Float> entry : annotatedFields.entrySet()) {
                Field field = operatorClass.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                if (field.isAnnotationPresent(Parameter.class)) {
                    field.setFloat(operator, entry.getValue());
                }
            }
        } catch (Exception e) {
            throw new OperatorException(e.getMessage());
        }
        Product targetProduct = operator.getTargetProduct();
        Raster data = targetProduct.getBandAt(0).getSourceImage().getData();
        for (int i = 0; i < expectedValues.length; i++) {
            float aFloat = data.getSampleFloat(i % height, i / width, 0);
            assertTrue(Math.abs(expectedValues[i] - aFloat) < threshold);
        }
    }

    protected void setOperatorParameters(Map<String, Float> params) throws NoSuchFieldException {
        annotatedFields = params;
    }

    protected void setTargetValues(float[] values) {
        this.expectedValues = values;
    }

    protected void setupBands(String[] names, int width, int height, float[] wavelengths, float[] minValues, float[] maxValues) {
        Assert.assertTrue(names != null && names.length > 0
                && minValues != null && maxValues != null && wavelengths != null
                && names.length == minValues.length && names.length == maxValues.length && names.length == wavelengths.length);
        this.width = width;
        this.height = height;
        int numElements = width * height;
        sourceProduct = new Product("IndexTest", "IndexTestType", width, height);
        for (int i = 0; i < names.length; i++) {
            Band band = new Band(names[i], ProductData.TYPE_FLOAT32, width, height);
            band.setSpectralWavelength(wavelengths[i]);
            band.setSpectralBandIndex(i);
            band.setRasterData(ProductData.createInstance(sampleData(minValues[i], maxValues[i], numElements)));
            sourceProduct.addBand(band);
        }
    }

    protected float[] sampleData(float min, float max, int elements) {
        float[] values = new float[elements];
        float step = (max - min) / (elements - 1);
        for (int i = 0; i < elements; i++) {
            values[i] = min + i * step;
        }
        return values;
    }

}