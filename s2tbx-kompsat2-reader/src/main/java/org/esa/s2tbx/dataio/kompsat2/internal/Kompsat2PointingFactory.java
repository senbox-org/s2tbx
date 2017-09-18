/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.kompsat2.internal;

import org.esa.snap.core.datamodel.Pointing;
import org.esa.snap.core.datamodel.PointingFactory;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.RasterDataNode;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.datamodel.TiePointGridPointing;

/**
 * The {@link PointingFactory} for Kompsat 2 1R products.
 */
public class Kompsat2PointingFactory implements PointingFactory {

    private static final String[] PRODUCT_TYPES = new String[]{
            Kompsat2Constants.KOMPSAT2_PRODUCT,
    };

    /**
     * Retrieves the product types for which this instance can create {@link Pointing pointings}.
     *
     * @return the product types
     */
    public String[] getSupportedProductTypes() {
        return PRODUCT_TYPES;
    }

    public Pointing createPointing(RasterDataNode raster) {
        final Product product = raster.getProduct();
        return new TiePointGridPointing(raster.getGeoCoding(),null, null, product.getTiePointGrid("view_zenith"),
               product.getTiePointGrid("view_azimuth"), null);
    }

    private TiePointGrid toZenithTiePointGrid(final Product product, final String name) {
        final TiePointGrid base = product.getTiePointGrid(name);
        return base != null ? TiePointGrid.createZenithFromElevationAngleTiePointGrid(base) : null;
    }
}
