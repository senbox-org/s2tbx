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
        alias = "CiOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Soil Radiometric Indices",
        description = "Colour Index  was developed to differentiate soils in the field.\n" +
                      "In most cases the CI gives complementary information with the BI and the NDVI.\n" +
                      " Used for diachronic analyses, they help for a better understanding of the evolution of soil surfaces.",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class CiOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "ci";

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the red source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "Green factor", defaultValue = "1.0F", description = "The value of the green source band is multiplied by this value.")
    private float greenFactor;

    @Parameter(label = "Red source band",
            description = "The red band for the CI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "Green source band",
            description = "The green band for the CI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String greenSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing CI", rectangle.height);
        try {

            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile greenTile = getSourceTile(getSourceProduct().getBand(greenSourceBand), rectangle);

            Tile ci = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile ciFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float ciValue;
            int ciFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float red = redFactor * redTile.getSampleFloat(x, y);
                    final float green = greenFactor * greenTile.getSampleFloat(x, y);

                    ciValue = (red - green)/(red + green);

                    ciFlagsValue = 0;
                    if (Float.isNaN(ciValue) || Float.isInfinite(ciValue)) {
                        ciFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        ciValue = 0.0f;
                    }
                    if (ciValue < 0.0f) {
                        ciFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (ciValue > 1.0f) {
                        ciFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    ci.setSample(x, y, ciValue);
                    ciFlags.setSample(x, y, ciFlagsValue);
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
        if (redSourceBand == null) {
            redSourceBand = findBand(600, 650, product); /* Band Centre = 550.7 nm, Band width 88.6 nm*/
            getLogger().info("Using band '" + redSourceBand + "' as RED input band.");
        }
        if (greenSourceBand == null) {
            greenSourceBand = findBand(495, 570, product); /* Band Centre = 664.8 nm, Band width 65.8 nm*/
            getLogger().info("Using band '" + greenSourceBand + "' as GREEN input band.");
        }

        if (redSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as red input band. Please specify band.");
        }
        if (greenSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as green input band. Please specify band.");
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(CiOp.class);
        }

    }

}
