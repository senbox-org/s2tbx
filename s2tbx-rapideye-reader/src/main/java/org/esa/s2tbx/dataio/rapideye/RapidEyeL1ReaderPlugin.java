/*
 *
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.framework.dataio.ProductReader;

import java.util.Locale;

/**
 * Reader plugin class for RapidEye L1 products.
 * RE L1 products have rasters in NITF format.
 *
 * @author Cosmin Cara
 */
public class RapidEyeL1ReaderPlugin extends BaseProductReaderPlugIn {

    @Override
    public Class[] getInputTypes() {
        return RapidEyeConstants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new RapidEyeL1Reader(this);
    }

    @Override
    public String[] getFormatNames() {
        return RapidEyeConstants.L1_FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return RapidEyeConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return RapidEyeConstants.L1_DESCRIPTION;
    }

    /*@Override
    protected String[] getProductFilePatterns() { return RapidEyeConstants.L1_FILENAME_PATTERNS; }*/

    @Override
    protected String[] getMinimalPatternList() { return RapidEyeConstants.L1_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }
}
