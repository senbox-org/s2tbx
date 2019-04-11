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

/**
 * @author Cosmin Cara
 */
@OperatorMetadata(
        alias = "TsaviOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Vegetation Radiometric Indices",
        description = "This retrieves the Transformed Soil Adjusted Vegetation Index (TSAVI).",
        authors = "Cosmin Cara",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class TsaviOp extends BaseIndexOp {

    // constants
    static final String BAND_NAME = "tsavi";

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the RED source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Soil line slope", defaultValue = "0.5F", description = "The soil line slope.")
    private float slope;

    @Parameter(label = "Soil line intercept", defaultValue = "0.5F", description = "The soil line intercept.")
    private float intercept;

    @Parameter(label = "Adjustment", defaultValue = "0.08F", description = "Adjustment factor to minimize soil background.")
    private float adjustment;

    @Parameter(label = "Red source band",
            description = "The red band for the TSAVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 600, maxWavelength = 650)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the TSAVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 800, maxWavelength = 900)
    private String nirSourceBand;

    public TsaviOp() {
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
        pm.beginTask("Computing tsavi", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile tsavi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile tsaviFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float tsaviValue;

            float constant1 = slope * intercept;
            float constant2 =  adjustment * (1 + slope * slope);

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    /**
                     * https://senbox.atlassian.net/browse/SIITBX-351
                     * The formula should be as follow:
                     TSAVI = s * (IR_factor * near_IR - s * red_factor * red - a) / (s * IR_factor * near_IR + red_factor * red - a * s + X * ( 1 + s * s))
                     */
                    tsaviValue = slope * (nir - slope * red - intercept) / (slope * nir + red - constant1 + constant2);

                    tsavi.setSample(x, y, computeFlag(x, y, tsaviValue, tsaviFlags));
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
            super(TsaviOp.class);
        }

    }
}
