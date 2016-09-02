/*
 * Copyright (C) 2014-2016 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2016 CS-Romania (office@c-s.ro)
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
package org.esa.s2tbx.dataio.spot6;

import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.spot6.dimap.Spot6Constants;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;
import org.openide.modules.OnStart;

import java.io.File;
import java.util.Locale;

/**
 * Reader plugin for SPOT6/7 products.
 *
 * @author Cosmin Cara
 */
public class Spot6ProductReaderPlugin extends BaseProductReaderPlugIn {
    public static final String SPOT6_COLOR_PALETTE_FILE_NAME = "spot6_color_palette.cpd";

    public Spot6ProductReaderPlugin() {
        super();
        folderDepth = 4;
    }

    @Override
    public Class[] getInputTypes() {
        return Spot6Constants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new Spot6ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return Spot6Constants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return Spot6Constants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return Spot6Constants.DIMAP_DESCRIPTION;
    }

    public File getFileInput(Object input) {
        return super.getFileInput(input);
    }

    @Override
    protected String[] getMinimalPatternList() {
        return Spot6Constants.MINIMAL_PATTERN_LIST;
    }

    @Override
    protected String[] getExclusionPatternList() {
        return new String[0];
    }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("SPOT 6/7", Spot6Constants.SPOT6_RGB_PROFILE));
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
            copyColorPaletteFileFromResources(Spot6ProductReaderPlugin.class.getClassLoader(), "org/esa/s2tbx/dataio/spot6/", SPOT6_COLOR_PALETTE_FILE_NAME);
        }
    }
}
