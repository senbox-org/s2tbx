/*
 * Copyright (C) 2014-2015 CS SI
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
 *  with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.framework.dataio.IllegalFileFormatException;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.framework.dataio.ProductSubsetDef;
import org.esa.snap.framework.datamodel.Band;
import org.esa.snap.framework.datamodel.Product;
import org.esa.snap.framework.datamodel.ProductData;

import java.io.IOException;

/**
 * Created by Oscar on 21/05/2015.
 */
public class Sentinel2NMSProductReader implements ProductReader {

    final ProductReader innerReader;

    Sentinel2NMSProductReader(ProductReaderPlugIn readerPlugIn, boolean forceResize) {
        innerReader = new Sentinel2ProductReader(readerPlugIn, forceResize);
    }

    @Override
    public ProductReaderPlugIn getReaderPlugIn() {
        return innerReader.getReaderPlugIn();
    }

    @Override
    public Object getInput() {
        return innerReader.getInput();
    }

    @Override
    public ProductSubsetDef getSubsetDef() {
        return innerReader.getSubsetDef();
    }

    @Override
    public Product readProductNodes(Object input, ProductSubsetDef subsetDef) throws IOException, IllegalFileFormatException {
        return innerReader.readProductNodes(input, subsetDef);
    }

    @Override
    public void readBandRasterData(Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        innerReader.readBandRasterData(destBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
    }

    @Override
    public void close() throws IOException {
        innerReader.close();
    }
}
