/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio.spot;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.GeoTiffBasedReader;
import org.esa.s2tbx.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.s2tbx.dataio.spot.dimap.SpotSceneMetadata;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.rcp.colormanip.ColorPaletteManager;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

/**
 * This is the base class for SPOT DIMAP readers, which regroups common
 * methods in order to avoid code duplication.
 *
 * @author Cosmin Cara
 */
public abstract class SpotProductReader extends GeoTiffBasedReader<SpotDimapMetadata> {
    protected static final String SPOT_COLOR_PALETTE_FILE_NAME = "7_spot_colors.cpd";

    static {
        ColorPaletteManager.getDefault().copyColorPaletteFileFromResources(SpotProductReader.class.getClassLoader(), "org/esa/s2tbx/dataio/spot/", SPOT_COLOR_PALETTE_FILE_NAME);
    }

    protected SpotSceneMetadata wrappingMetadata;

    protected SpotProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    public void setProductDirectory(VirtualDirEx productDirectory) {
        this.productDirectory = productDirectory;
    }

    public void setMetadata(SpotSceneMetadata metadata) {
        this.wrappingMetadata = metadata;
    }

    protected final ProductData createProductData(int dataType, int size) {
        ProductData buffer;
        switch (dataType) {
            case ProductData.TYPE_UINT8:
                buffer = ProductData.createUnsignedInstance(new byte[size]);
                break;
            case ProductData.TYPE_INT8:
                buffer = ProductData.createInstance(new byte[size]);
                break;
            case ProductData.TYPE_UINT16:
                buffer = ProductData.createUnsignedInstance(new short[size]);
                break;
            case ProductData.TYPE_INT16:
                buffer = ProductData.createInstance(new short[size]);
                break;
            case ProductData.TYPE_INT32:
                buffer = ProductData.createInstance(new int[size]);
                break;
            case ProductData.TYPE_UINT32:
                buffer = ProductData.createUnsignedInstance(new int[size]);
                break;
            case ProductData.TYPE_FLOAT32:
                buffer = ProductData.createInstance(new float[size]);
                break;
            default:
                buffer = ProductData.createUnsignedInstance(new byte[size]);
                break;
        }
        return buffer;
    }

    @SuppressWarnings("UnusedParameters")
    protected void readBandStatistics(Band band, int bandIndex, SpotDimapMetadata componentMetadata) {
        // TODO: uncomment when finding out how to compute a histogram
        /*if (band != null && componentMetadata != null) {
            HashMap<String, Double> statistics = componentMetadata.getStatistics(bandIndex);
            if (statistics != null) {
                Stx stx = new StxFactory().withMinimum(statistics.get(SpotConstants.TAG_STX_MIN))
                        .withMaximum(statistics.get(SpotConstants.TAG_STX_MAX))
                        .withMean(statistics.get(SpotConstants.TAG_STX_MEAN))
                        .withStandardDeviation(statistics.get(SpotConstants.TAG_STX_STDV))
                        .withHistogramBins(new int[0])
                        .create();
                if (stx != null) {
                    band.setStx(stx);
                }
            }
        }*/
    }

}
