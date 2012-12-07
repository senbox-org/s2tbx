package org.esa.beam.dataio.s3.synergy;/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import org.esa.beam.dataio.s3.LonLatFunction;
import org.junit.Test;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LonLatTiePointFunctionTest {

    @Test
    public void testApproximation() throws Exception {
        final URL url = getClass().getResource("tiepoints_olci.nc");
        assertNotNull(url);

        final File file = new File(url.toURI());
        assertNotNull(file);

        final NetcdfFile ncFile = NetcdfFile.open(file.getPath());

        final List<Variable> variables = ncFile.getVariables();
        for (final Variable variable : variables) {
            System.out.println("variable.getName() = " + variable.getName());
        }

        final double[] lonData = getDoubles(ncFile, "OLC_TP_lon");
        final double[] latData = getDoubles(ncFile, "OLC_TP_lat");
        final double[] saaData = getDoubles(ncFile, "SAA");

        final LonLatFunction function = new LonLatTiePointFunction(lonData, latData, saaData, 77, 0.1);

        for (int i = 0; i < saaData.length; i++) {
            final double lon = lonData[i];
            final double lat = latData[i];
            final double saa = saaData[i];
            final double actual = function.getValue(new Point2D.Double(lon, lat));

            assertEquals(saa, actual, 0.1);
        }
    }

    private double[] getDoubles(NetcdfFile ncFile, String name) throws IOException {
        final Variable variable = ncFile.findVariable(name);
        assertEquals(1, variable.getRank());

        final double scaleFactor = variable.findAttribute("scale_factor").getNumericValue().doubleValue();
        final Array array = variable.read();

        final double[] data = new double[variable.getShape(0)];
        for (int i = 0; i < data.length; i++) {
            data[i] = array.getDouble(i) * scaleFactor;
        }
        return data;
    }
}
