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
        alias = "PviOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Vegetation Radiometric Indices",
        description = "Perpendicular Vegetation Index retrieves the Isovegetation lines parallel to soil line. Soil line has an arbitrary slope and passes through origin",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class PviOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "pvi";

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the red source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Angle between soil line and NIR axis (in degrees)", defaultValue = "45.0", description = "Soil line has an arbitrary slope and passes through origin.")
    private float angleSoilLineNIRAxis;

    @Parameter(label = "Red source band",
            description = "The red band for the PVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the PVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing PVI", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile pvi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile pviFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float pviValue;
            int pviFlagsValue;

            double radiansAngle = Math.toRadians(angleSoilLineNIRAxis);

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    pviValue = (float) (( Math.sin(radiansAngle)*nir ) - ( Math.cos(radiansAngle)*red ));

                    pviFlagsValue = 0;
                    if (Float.isNaN(pviValue) || Float.isInfinite(pviValue)) {
                        pviFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        pviValue = 0.0f;
                    }
                    if (pviValue < 0.0f) {
                        pviFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (pviValue > 1.0f) {
                        pviFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    pvi.setSample(x, y, pviValue);
                    pviFlags.setSample(x, y, pviFlagsValue);
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    protected void loadSourceBands(Product product) throws OperatorException {
        if (redSourceBand == null) {
            redSourceBand = findBand(600, 650, product);
            getLogger().info("Using band '" + redSourceBand + "' as red input band.");
        }
        if (nirSourceBand == null) {
            nirSourceBand = findBand(800, 900, product);
            getLogger().info("Using band '" + nirSourceBand + "' as NIR input band.");
        }
        if (redSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as red input band. Please specify band.");
        }
        if (nirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as nir input band. Please specify band.");
        }
    }


    public static class Spi extends OperatorSpi {

        public Spi() {
            super(PviOp.class);
        }

    }

}
