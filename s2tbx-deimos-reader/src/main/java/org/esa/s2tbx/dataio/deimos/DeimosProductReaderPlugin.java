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

package org.esa.s2tbx.dataio.deimos;

import org.esa.s2tbx.dataio.deimos.dimap.DeimosConstants;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.util.Locale;

/**
 * Plugin for reading DEIMOS-1 files.
 * The files are GeoTIFF with DIMAP metadata.
 *
 * @author  Cosmin Cara
 */
public class DeimosProductReaderPlugin extends BaseProductReaderPlugIn {
    private static final String COLOR_PALETTE_FILE_NAME = "Deimos_color_palette.cpd";

    public DeimosProductReaderPlugin() {
        super("org/esa/s2tbx/dataio/deimos/" + DeimosProductReaderPlugin.COLOR_PALETTE_FILE_NAME);
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new DeimosMetadataInspector();
    }

    @Override
    public Class[] getInputTypes() {
        return DeimosConstants.DIMAP_READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new DeimosProductReader(this, getColorPaletteFilePath());
    }

    @Override
    public String[] getFormatNames() {
        return DeimosConstants.DIMAP_FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return DeimosConstants.DIMAP_DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return DeimosConstants.DIMAP_DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() { return DeimosConstants.MINIMAL_PRODUCT_PATTERNS; }

    @Override
    protected String[] getExclusionPatternList() { return new String[0]; }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("DEIMOS-1", new String[] { "Red", "Green", "NIR" }));
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        return super.getDecodeQualification(input);
    }
}
