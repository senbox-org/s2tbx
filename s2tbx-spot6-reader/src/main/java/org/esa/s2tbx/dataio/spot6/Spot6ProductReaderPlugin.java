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
import org.esa.s2tbx.dataio.spot6.internal.Spot6MetadataInspector;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Reader plugin for SPOT6/7 products.
 *
 * @author Cosmin Cara
 */
public class Spot6ProductReaderPlugin extends BaseProductReaderPlugIn {
    private static final String COLOR_PALETTE_FILE_NAME = "Spot6_color_palette.cpd";

    public Spot6ProductReaderPlugin() {
        super("org/esa/s2tbx/dataio/spot6/" + Spot6ProductReaderPlugin.COLOR_PALETTE_FILE_NAME);

        this.folderDepth = 4;
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new Spot6MetadataInspector();
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
}
