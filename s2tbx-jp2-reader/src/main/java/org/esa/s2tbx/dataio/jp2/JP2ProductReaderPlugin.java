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

package org.esa.s2tbx.dataio.jp2;

import org.esa.s2tbx.dataio.jp2.internal.JP2ProductReaderConstants;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.openide.modules.OnStart;

import java.io.File;
import java.util.Locale;

/**
 * Plugin for reading JP2 files.
 *
 * @author Cosmin Cara
 */
public class JP2ProductReaderPlugin implements ProductReaderPlugIn {
    public static final String JP2_COLOR_PALETTE_FILE_NAME = "jp2_cc_general.cpd";

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification result = DecodeQualification.UNABLE;
        if (input != null) {
            File fileInput = null;
            if (input instanceof String) {
                fileInput = new File((String) input);
            } else if (input instanceof File) {
                fileInput = (File) input;
            }
            if (fileInput != null) {
                final String ext = FileUtils.getExtension(fileInput);
                if (".jp2".equalsIgnoreCase(ext)) {
                    result = DecodeQualification.SUITABLE;
                }
            }
        }
        return result;
    }

    @Override
    public Class[] getInputTypes() {
        return JP2ProductReaderConstants.INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new JP2ProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return JP2ProductReaderConstants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return JP2ProductReaderConstants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return JP2ProductReaderConstants.DESCRIPTION;
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(getFormatNames()[0], getDefaultFileExtensions()[0], JP2ProductReaderConstants.DESCRIPTION);
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
            BaseProductReaderPlugIn.copyColorPaletteFileFromResources(JP2ProductReaderPlugin.class.getClassLoader(), "org/esa/s2tbx/dataio/jp2/", JP2_COLOR_PALETTE_FILE_NAME);
        }
    }
}
