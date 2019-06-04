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

    /**
     * https://senbox.atlassian.net/browse/SIITBX-353
     * The formula should be as follow:
     * NDPI = (mir_factor * middle_IR - green_factor * green) / (mir_factor * middle_IR + green_factor * green)
     *
     * instead of:
     * NDPI = (green_factor * green - swir_factor * ShortWave_IR) / (green_factor * green + swir_factor * ShortWave_IR)
     */

    // constants
    public static final String BAND_NAME = "ndpi";

    @Parameter(label = "Green factor", defaultValue = "1.0F", description = "The value of the green source band is multiplied by this value.")
    private float greenFactor;

    /*@Parameter(label = "SWIR factor", defaultValue = "1.0F", description = "The value of the SWIR source band is multiplied by this value.")
    private float swirFactor;*/

    @Parameter(label = "MIR factor", defaultValue = "1.0F", description = "The value of the MIR source band is multiplied by this value.")
    private float mirFactor;

    @Parameter(label = "Green source band",
            description = "The green band for the NDPI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 525, maxWavelength = 605)
    private String greenSourceBand;

    /*@Parameter(label = "SWIR source band",
            description = "The the short-wave infrared band-I for the NDPI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 1550, maxWavelength = 1750)
    private String swirSourceBand;*/

    @Parameter(label = "MIR source band",
      description = "The mid-infrared band for the NDPI computation. If not provided," +
                " the operator will try to find the best fitting band.",
      rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 3000, maxWavelength = 8000)
    private String mirSourceBand;

    public NdpiOp() {
        super();
        this.lowValueThreshold = -1f;
        this.highValueThreshold = 1f;
    }

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing NDPI", rectangle.height);
        try {
            Tile greenTile = getSourceTile(getSourceProduct().getBand(greenSourceBand), rectangle);
            //Tile swirTile = getSourceTile(getSourceProduct().getBand(swirSourceBand), rectangle);
            Tile mirTile = getSourceTile(getSourceProduct().getBand(mirSourceBand), rectangle);

            Tile ndpi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile ndpiFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float ndpiValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    //final float swir = swirFactor * swirTile.getSampleFloat(x, y);
                    final float mir = mirFactor * mirTile.getSampleFloat(x, y);
                    final float green = greenFactor * greenTile.getSampleFloat(x, y);

                    //ndpiValue = (green - swir)/(green + swir);// (1 - green/swir)/(1 + green/swir)
                    ndpiValue = (mir - green)/(mir + green);

                    ndpi.setSample(x, y, computeFlag(x, y, ndpiValue, ndpiFlags));
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(NdpiOp.class);
        }

    }

}
