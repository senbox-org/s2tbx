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

import org.apache.commons.lang.SystemUtils;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Reader plugin class for RapidEye L1 products.
 * RE L1 products have rasters in NITF format.
 *
 * @author Cosmin Cara
 */
public class RapidEyeL1ReaderPlugin extends BaseProductReaderPlugIn {
    private static final String COLOR_PALETTE_FILE_NAME = "RapidEye_color_palette.cpd";

    public RapidEyeL1ReaderPlugin() {
        super("org/esa/s2tbx/dataio/rapideye/" + RapidEyeL1ReaderPlugin.COLOR_PALETTE_FILE_NAME);
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new RapidEyeL1MetadataInspector();
    }

    @Override
    public Class[] getInputTypes() {
        return RapidEyeConstants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new RapidEyeL1Reader(this, getColorPaletteFilePath());
    }

    public DecodeQualification getDecodeQualification(Object input) {
        if(SystemUtils.IS_OS_MAC){
            return DecodeQualification.UNABLE;
        }
        return super.getDecodeQualification(input);
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

    @Override
    protected String[] getMinimalPatternList() { return RapidEyeConstants.L1_MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("RapidEye L1", new String[] { "red", "green", "blue" }));
    }
}
