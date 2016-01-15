/*
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

package org.esa.s2tbx.radiometry;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;

import java.awt.*;
import java.util.Map;

@OperatorMetadata(
        alias = "IpviOp",
        category = "Optical/Thematic Land Processing",
        description = "Infrared Percentage Vegetation Index retrieves the Isovegetation lines converge at origin",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class IpviOp extends BaseIndexOp {

    // constants
    public static final String IPVI_BAND_NAME = "ipvi";
    public static final String IPVI_FLAGS_BAND_NAME = "ipvi_flags";

    public static final String IPVI_ARITHMETIC_FLAG_NAME = "IPVI_ARITHMETIC";
    public static final String IPVI_LOW_FLAG_NAME = "IPVI_NEGATIVE";
    public static final String IPVI_HIGH_FLAG_NAME = "IPVI_SATURATION";

    public static final int IPVI_ARITHMETIC_FLAG_VALUE = 1;
    public static final int IPVI_LOW_FLAG_VALUE = 1 << 1;
    public static final int IPVI_HIGH_FLAG_VALUE = 1 << 2;

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the red source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Red source band",
            description = "The red band for the IPVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the IPVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;


    @Override
    public void initialize() throws OperatorException {

        super.initialize();

        loadSourceBands(sourceProduct);

        Band ipviOutputBand = new Band(IPVI_BAND_NAME, ProductData.TYPE_FLOAT32, sourceProduct.getSceneRasterWidth(),
                sourceProduct.getSceneRasterHeight());
        targetProduct.addBand(ipviOutputBand);

        Band ipviFlagsOutputBand = new Band(IPVI_FLAGS_BAND_NAME, ProductData.TYPE_INT32, sourceProduct.getSceneRasterWidth(),
                sourceProduct.getSceneRasterHeight());
        ipviFlagsOutputBand.setDescription("ipvi specific flags");

        FlagCoding flagCoding = super.createFlagCoding(getFlagCodingDescriptor());
        ipviFlagsOutputBand.setSampleCoding(flagCoding);

        targetProduct.getFlagCodingGroup().add(flagCoding);
        targetProduct.addBand(ipviFlagsOutputBand);

    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing IPVI", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile ipvi = targetTiles.get(targetProduct.getBand(IPVI_BAND_NAME));
            Tile ipviFlags = targetTiles.get(targetProduct.getBand(IPVI_FLAGS_BAND_NAME));

            float ipviValue;
            int ipviFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    ipviValue = nir / (nir + red);

                    ipviFlagsValue = 0;
                    if (Float.isNaN(ipviValue) || Float.isInfinite(ipviValue)) {
                        ipviFlagsValue |= IPVI_ARITHMETIC_FLAG_VALUE;
                        ipviValue = 0.0f;
                    }
                    if (ipviValue < 0.0f) {
                        ipviFlagsValue |= IPVI_LOW_FLAG_VALUE;
                    }
                    if (ipviValue > 1.0f) {
                        ipviFlagsValue |= IPVI_HIGH_FLAG_VALUE;
                    }
                    ipvi.setSample(x, y, ipviValue);
                    ipviFlags.setSample(x, y, ipviFlagsValue);
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


    @Override
    protected OperatorDescriptor getOperatorDescriptor() {

        return new OperatorDescriptor("ipvi", new MaskDescriptor[]{
                new MaskDescriptor(IPVI_ARITHMETIC_FLAG_NAME, IPVI_FLAGS_BAND_NAME + "." + IPVI_ARITHMETIC_FLAG_NAME,
                        "An arithmetic exception occurred.",
                        Color.red.brighter(), 0.7),
                new MaskDescriptor(IPVI_LOW_FLAG_NAME, IPVI_FLAGS_BAND_NAME + "." + IPVI_LOW_FLAG_NAME,
                        "ipvi value is too low.",
                        Color.red, 0.7),
                new MaskDescriptor(IPVI_HIGH_FLAG_NAME, IPVI_FLAGS_BAND_NAME + "." + IPVI_HIGH_FLAG_NAME,
                        "ipvi value is too high.",
                        Color.red.darker(), 0.7)
        }
        );


    }


    private FlagCodingDescriptor getFlagCodingDescriptor() {
        return new FlagCodingDescriptor("ipvi_flags", "IPVI Flag Coding", new FlagDescriptor[]{
                new FlagDescriptor(IPVI_ARITHMETIC_FLAG_NAME, IPVI_ARITHMETIC_FLAG_VALUE, "IPVI value calculation failed due to an arithmetic exception"),
                new FlagDescriptor(IPVI_LOW_FLAG_NAME, IPVI_LOW_FLAG_VALUE, "IPVI value is too low"),
                new FlagDescriptor(IPVI_HIGH_FLAG_NAME, IPVI_HIGH_FLAG_VALUE, "IPVI value is too high")});
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(IpviOp.class);
        }

    }

}
