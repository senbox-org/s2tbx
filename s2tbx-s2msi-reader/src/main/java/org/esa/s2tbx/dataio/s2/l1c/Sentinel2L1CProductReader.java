/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.l1c.metadata.S2L1cProductMetadataReader;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo;
import org.esa.s2tbx.dataio.s2.ortho.Sentinel2OrthoProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;

import java.io.IOException;

/**
 * <p>
 * This product reader can currently read single L1C tiles (also called L1C granules) and entire L1C scenes composed of
 * multiple L1C tiles.
 * </p>
 * <p>
 * To read single tiles, select any tile image file (IMG_*.jp2) within a product package. The reader will then
 * collect other band images for the selected tile and wiull also try to read the metadata file (MTD_*.xml).
 * </p>
 * <p>To read an entire scene, select the metadata file (MTD_*.xml) within a product package. The reader will then
 * collect other tile/band images and create a mosaic on the fly.
 * </p>
 *
 * @author Norman Fomferra
 */
public class Sentinel2L1CProductReader extends Sentinel2OrthoProductReader {

    static final String L1C_CACHE_DIR = "l1c-reader";

    public Sentinel2L1CProductReader(ProductReaderPlugIn readerPlugIn, String epsgCode) {
        super(readerPlugIn, epsgCode);
    }

    @Override
    protected S2SpatialResolution getProductResolution(INamingConvention namingConvention) {
        return S2SpatialResolution.R10M; // namingConvention.getResolution();
    }

    @Override
    protected S2L1cProductMetadataReader buildMetadataReader(VirtualPath virtualPath) throws IOException {
        return new S2L1cProductMetadataReader(virtualPath, this.epsgCode);
    }

    @Override
    protected String getReaderCacheDir() {
        return L1C_CACHE_DIR;
    }

    @Override
    protected int getMaskLevel() {
        return MaskInfo.L1C;
    }
}
