/*
 * Copyright (c) 2012. Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 */

package org.esa.beam.dataio.s3.olci;

import org.esa.beam.dataio.s3.manifest.ManifestProductReaderPlugIn;
import org.esa.beam.framework.dataio.ProductReader;

/**
 * PlugIn class which provides a Level 1 Sentinel 3 product reader to the framework.
 *
 * @author Marco Peters
 * @since 1.0
 */
public class OlciLevel1ProductReaderPlugIn extends ManifestProductReaderPlugIn {

    public static final String FORMAT_NAME = "OLCI-L1B";

    public OlciLevel1ProductReaderPlugIn() {
        super(FORMAT_NAME, "Sentinel-3 OLCI Level-1b products", ".*OL_1_(ERR|EFR)_.*", "L1b_EO_manifest", ".xml");
    }

    @Override
    public ProductReader createReaderInstance() {
        return new OlciLevel1ProductReader(this);
    }
}
