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
        alias = "Ndwi2Op",
        version="1.0",
        category = "Optical/Thematic Land Processing/Water Radiometric Indices",
        description = "The Normalized Difference Water Index, allowing for the measurement of surface water extent",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class Ndwi2Op extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "ndwi2";

    @Parameter(label = "Green factor", defaultValue = "1.0F", description = "The value of the green source band is multiplied by this value.")
    private float greenFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Green source band",
            description = "The green band for the NDWI2 computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 495, maxWavelength = 570)
    private String greenSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the NDWI2 computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 800, maxWavelength = 900)
    private String nirSourceBand;

    public Ndwi2Op() {
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
        pm.beginTask("Computing NDWI2", rectangle.height);
        try {
            Tile greenTile = getSourceTile(getSourceProduct().getBand(greenSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile ndwi2 = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile ndwi2Flags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float ndwi2Value;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float green = greenFactor * greenTile.getSampleFloat(x, y);

                    ndwi2Value = (green - nir)/(green + nir);

                    ndwi2.setSample(x, y, computeFlag(x, y, ndwi2Value, ndwi2Flags));
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
            super(Ndwi2Op.class);
        }

    }

}
