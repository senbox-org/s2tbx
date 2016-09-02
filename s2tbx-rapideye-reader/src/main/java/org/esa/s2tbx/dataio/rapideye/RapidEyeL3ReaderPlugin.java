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

package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;
import org.openide.modules.OnStart;

import java.util.Locale;

/**
 * Reader plugin class for RapidEye L3 products.
 * RE L3 products have a GeoTIFF raster.
 */
public class RapidEyeL3ReaderPlugin extends BaseProductReaderPlugIn {
    public static final String RAPID_EYE_L3_COLOR_PALETTE_FILE_NAME = "rapid_eye_l3_color_palette.cpd";

    @Override
    public Class[] getInputTypes() {
        return RapidEyeConstants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new RapidEyeL3Reader(this);
    }

    @Override
    public String[] getFormatNames() {
        return RapidEyeConstants.L3_FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return RapidEyeConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return RapidEyeConstants.L3_DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() { return RapidEyeConstants.L3_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return RapidEyeConstants.NOT_L3_FILENAME_PATTERNS; }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("RapidEye L3", new String[] { "red", "green", "blue" }));
    }

    /**
     * Startup class invoked by NetBeans that copies the color palette file.
     */
    @OnStart
    public static class StartOp implements Runnable {

        public StartOp() {
        }

        @Override
        public void run() {
            copyColorPaletteFileFromResources(RapidEyeL3ReaderPlugin.class.getClassLoader(), "org/esa/s2tbx/dataio/rapideye/", RAPID_EYE_L3_COLOR_PALETTE_FILE_NAME);
        }
    }
}
