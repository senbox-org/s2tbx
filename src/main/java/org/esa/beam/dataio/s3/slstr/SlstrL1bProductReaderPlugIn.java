package org.esa.beam.dataio.s3.slstr;/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import org.esa.beam.dataio.s3.manifest.ManifestProductReaderPlugIn;
import org.esa.beam.framework.dataio.ProductReader;

public class SlstrL1bProductReaderPlugIn extends ManifestProductReaderPlugIn {
    static final String FORMAT_NAME = "SLSTR-L1B";

    public SlstrL1bProductReaderPlugIn() {
        super(FORMAT_NAME, "Sentinel-3 SLSTR L1b product", "S3.?_SL_1_SLT_.*", "L1b_EO_manifest", ".xml");
    }

    @Override
    public ProductReader createReaderInstance() {
        return new SlstrL1bProductReader(this);
    }
}
