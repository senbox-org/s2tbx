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

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.radiometry.annotations.BandParameter;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;

import java.awt.*;
import java.util.Map;

@OperatorMetadata(
        alias = "NdpiOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Water Radiometric Indices",
        description = "The normalized differential pond index, combines the short-wave infrared band-I and the green band",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class NdpiOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "ndpi";

    @Parameter(label = "Green factor", defaultValue = "1.0F", description = "The value of the green source band is multiplied by this value.")
    private float greenFactor;

    @Parameter(label = "SWIR factor", defaultValue = "1.0F", description = "The value of the SWIR source band is multiplied by this value.")
    private float swirFactor;

    @Parameter(label = "Green source band",
            description = "The green band for the NDPI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 525, maxWavelength = 605)
    private String greenSourceBand;

    @Parameter(label = "SWIR source band",
            description = "The the short-wave infrared band-I for the NDPI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 1550, maxWavelength = 1750)
    private String swirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing NDPI", rectangle.height);
        try {
            Tile greenTile = getSourceTile(getSourceProduct().getBand(greenSourceBand), rectangle);
            Tile swirTile = getSourceTile(getSourceProduct().getBand(swirSourceBand), rectangle);

            Tile ndpi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile ndpiFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float ndpiValue;
            int ndpiFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float swir = swirFactor * swirTile.getSampleFloat(x, y);
                    final float green = greenFactor * greenTile.getSampleFloat(x, y);

                    ndpiValue = (green - swir)/(green + swir);// (1 - green/swir)/(1 + green/swir)

                    ndpiFlagsValue = 0;
                    if (Float.isNaN(ndpiValue) || Float.isInfinite(ndpiValue)) {
                        ndpiFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        ndpiValue = 0.0f;
                    }
                    if (ndpiValue < 0.0f) {
                        ndpiFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (ndpiValue > 1.0f) {
                        ndpiFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    ndpi.setSample(x, y, ndpiValue);
                    ndpiFlags.setSample(x, y, ndpiFlagsValue);
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    @Override
    protected void loadSourceBands(Product product) throws OperatorException {
        if (greenSourceBand == null) {
            greenSourceBand = findBand(525, 605, product);
            getLogger().info("Using band '" + greenSourceBand + "' as GREEN input band.");
        }
        if (swirSourceBand == null) {
            swirSourceBand = findBand(1550, 1750, product);
            getLogger().info("Using band '" + swirSourceBand + "' as SWIR input band.");
        }
        if (greenSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as GREEN input band. Please specify band.");
        }
        if (swirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as SWIR input band. Please specify band.");
        }
        this.sourceBandNames = new String[] { greenSourceBand, swirSourceBand };
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(NdpiOp.class);
        }

    }

}
