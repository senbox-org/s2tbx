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

package org.esa.s2tbx.dataio.spot;

import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.framework.datamodel.RGBImageProfile;
import org.esa.snap.framework.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Visat plugin for reading SPOT-4 and SPOT-5 view files which are not
 * in the "official" (DIMAP+GeoTIFF) format.
 *
 * @author Cosmin Cara
 */
public class SpotViewProductReaderPlugin extends BaseProductReaderPlugIn {

    @Override
    public Class[] getInputTypes() {
        return SpotConstants.SPOTVIEW_READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new SpotViewProductReader(this);
    }

    @Override
    public String[] getFormatNames() { return SpotConstants.SPOTVIEW_FORMAT_NAMES; }

    @Override
    public String[] getDefaultFileExtensions() {
        return SpotConstants.SPOTVIEW_DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return SpotConstants.SPOTVIEW_DESCRIPTION;
    }

    /*@Override
    protected String[] getProductFilePatterns() { return SpotConstants.SPOTVIEW_FILENAME_PATTERNS; }*/

    @Override
    protected String[] getMinimalPatternList() { return SpotConstants.SPOTVIEW_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("SPOT", new String[] { "XS1", "XS2", "XS3" }));
    }
}
