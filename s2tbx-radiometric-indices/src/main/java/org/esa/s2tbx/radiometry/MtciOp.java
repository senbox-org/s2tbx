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
        alias = "MtciOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Vegetation Radiometric Indices",
        description = "The Meris Terrestrial Chlorophyll Index,  aims at estimating the Red Edge Position (REP).\n" +
                      "This is the maximum slant point in the red and near-infrared region of the vegetal spectral reflectance.\n" +
                      "It is useful for observing the chlorophyll contents, vegetation senescence, and stress for " +
                      "water and nutritional deficiencies, but it is less suitable for land classification",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class MtciOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "mtci";

    @Parameter(label = "Red (B4) factor", defaultValue = "1.0F", description = "The value of the red source band (B4) is multiplied by this value.")
    private float redB4Factor;

    @Parameter(label = "Red (B5) factor", defaultValue = "1.0F", description = "The value of the red source band (B5) is multiplied by this value.")
    private float redB5Factor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Red source band 4",
            description = "The red band (B4) for the MTCI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand4;

    @Parameter(label = "Red source band 5",
            description = "The red band (B5) for the MTCI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand5;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the MTCI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing MTCI", rectangle.height);
        try {
            Tile redB4Tile = getSourceTile(getSourceProduct().getBand(redSourceBand4), rectangle);
            Tile redB5Tile = getSourceTile(getSourceProduct().getBand(redSourceBand5), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile mtci = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile mtciFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float mtciValue;
            int mtciFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float redB4 = redB4Factor * redB4Tile.getSampleFloat(x, y);
                    final float redB5 = redB5Factor * redB5Tile.getSampleFloat(x, y);

                    mtciValue = (nir - redB5) / (redB5 - redB4);

                    mtciFlagsValue = 0;
                    if (Float.isNaN(mtciValue) || Float.isInfinite(mtciValue)) {
                        mtciFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        mtciValue = 0.0f;
                    }
                    if (mtciValue < 0.0f) {
                        mtciFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (mtciValue > 1.0f) {
                        mtciFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    mtci.setSample(x, y, mtciValue);
                    mtciFlags.setSample(x, y, mtciFlagsValue);
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
        if (redSourceBand4 == null) {
            redSourceBand4 = findBand(650, 680, product); /* (600, 650) */
            getLogger().info("Using band '" + redSourceBand4 + "' as red input band (B4).");
        }
        if (redSourceBand5 == null) {
            redSourceBand5 = findBand(697, 712, product); /* (600, 650) */
            getLogger().info("Using band '" + redSourceBand5 + "' as red input band (B5).");
        }
        if (nirSourceBand == null) {
            nirSourceBand = findBand(733, 748, product); /* (800, 900) */
            getLogger().info("Using band '" + nirSourceBand + "' as NIR input band.");
        }
        if (redSourceBand4 == null) {
            throw new OperatorException("Unable to find band that could be used as red input band (B4). Please specify band.");
        }
        if (redSourceBand5 == null) {
            throw new OperatorException("Unable to find band that could be used as red input band (B5). Please specify band.");
        }
        if (nirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as nir input band. Please specify band.");
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(MtciOp.class);
        }

    }

}
