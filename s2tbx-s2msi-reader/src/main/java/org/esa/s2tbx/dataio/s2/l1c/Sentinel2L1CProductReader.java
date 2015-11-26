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

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo;
import org.esa.s2tbx.dataio.s2.ortho.Sentinel2OrthoProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.SystemUtils;
import org.jdom.JDOMException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

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

    protected final Logger logger;

    public Sentinel2L1CProductReader(ProductReaderPlugIn readerPlugIn, ProductInterpretation interpretation, String epsgCode) {
        super(readerPlugIn, interpretation, epsgCode);
        logger = SystemUtils.LOG;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

    @Override
    protected String getReaderCacheDir() {
        return L1C_CACHE_DIR;
    }

    protected S2Metadata parseHeader(
            File file, String granuleName, S2Config config, String epsg) throws IOException {

        try {
            return L1cMetadata.parseHeader(file, granuleName, config, epsg);
        } catch (JDOMException | JAXBException e) {
            throw new IOException("Failed to parse metadata in " + file.getName());
        }
    }

    @Override
    protected String getImagePathString(String imageFileName, S2SpatialResolution resolution) {
        return imageFileName;
    }

    @Override
    protected int getMaskLevel() {
        return MaskInfo.L1C;
    }
}
