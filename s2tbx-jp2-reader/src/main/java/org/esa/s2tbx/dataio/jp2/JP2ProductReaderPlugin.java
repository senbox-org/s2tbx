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
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.core.util.io.SnapFileFilter;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Plugin for reading JP2 files.
 *
 * @author Cosmin Cara
 */
public class JP2ProductReaderPlugin implements ProductReaderPlugIn {

    public JP2ProductReaderPlugin() {
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        Path path = BaseProductReaderPlugIn.convertInputToPath(input);
        DecodeQualification result = DecodeQualification.UNABLE;
        String fileName = path.getFileName().toString();
        int pointIndex = fileName.lastIndexOf(".");
        if (pointIndex > 0) {
            String extension = fileName.substring(pointIndex);
            if (".jp2".equalsIgnoreCase(extension)) {
                result = DecodeQualification.SUITABLE;
            }
        }
        return result;

//        DecodeQualification result = DecodeQualification.UNABLE;
//        if (input != null) {
//            File fileInput = null;
//            if (input instanceof String) {
//                fileInput = new File((String) input);
//            } else if (input instanceof File) {
//                fileInput = (File) input;
//            }
//            if (fileInput != null) {
//                final String ext = FileUtils.getExtension(fileInput);
//                if (".jp2".equalsIgnoreCase(ext)) {
//                    result = DecodeQualification.SUITABLE;
//                }
//            }
//        }
//        return result;
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
}
