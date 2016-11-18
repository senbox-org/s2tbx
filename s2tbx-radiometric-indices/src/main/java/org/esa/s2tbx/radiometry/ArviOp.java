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
        alias = "ArviOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Vegetation Radiometric Indices",
        description = "Atmospherically Resistant Vegetation Index belongs to a family of indices with built-in atmospheric corrections.",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class ArviOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "arvi";

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the RED source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "Blue factor", defaultValue = "1.0F", description = "The value of the BLUE source band is multiplied by this value.")
    private float blueFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Gamma Parameter", defaultValue = "1.0F", description = "The gamma parameter is like a weighting function that depends on the aerosol type")
    private float gammaParameter;

    @Parameter(label = "Red source band",
            description = "The red band for the ARVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 600, maxWavelength = 650)
    private String redSourceBand;

    @Parameter(label = "Blue source band",
            description = "The blue band for the ARVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 450, maxWavelength = 495)
    private String blueSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the ARVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 800, maxWavelength = 900)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing ARVI", rectangle.height);
        try {

            Tile blueTile = getSourceTile(getSourceProduct().getBand(blueSourceBand), rectangle);
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile arvi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile arviFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float rbEquationValue;

            float arviValue;
            int arviFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float blue = blueFactor * blueTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);
                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);

                    rbEquationValue = red - gammaParameter * (blue - red);

                    arviValue = (nir - rbEquationValue) / (nir + rbEquationValue);

                    arviFlagsValue = 0;
                    if (Float.isNaN(arviValue) || Float.isInfinite(arviValue)) {
                        arviFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        arviValue = 0.0f;
                    }
                    if (arviValue < 0.0f) {
                        arviFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (arviValue > 1.0f) {
                        arviFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    arvi.setSample(x, y, arviValue);
                    arviFlags.setSample(x, y, arviFlagsValue);
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
        if (blueSourceBand == null) {
            blueSourceBand = findBand(450, 495, product);
            getLogger().info("Using band '" + blueSourceBand + "' as BLUE input band.");
        }
        if (redSourceBand == null) {
            redSourceBand = findBand(600, 650, product);
            getLogger().info("Using band '" + redSourceBand + "' as RED input band.");
        }
        if (nirSourceBand == null) {
            nirSourceBand = findBand(800, 900, product);
            getLogger().info("Using band '" + nirSourceBand + "' as NIR input band.");
        }

        if (blueSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as blue input band. Please specify band.");
        }
        if (redSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as red input band. Please specify band.");
        }
        if (nirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as nir input band. Please specify band.");
        }
        this.sourceBandNames = new String[] { blueSourceBand, redSourceBand, nirSourceBand };
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ArviOp.class);
        }

    }

}
