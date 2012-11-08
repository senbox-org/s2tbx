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

package org.esa.beam.dataio.s3.slstr;

import org.esa.beam.dataio.s3.manifest.Manifest;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Product reader responsible for reading SLSTR L2 data products in SAFE format.
 *
 * @author Olaf Danne
 * @author Ralf Quast
 * @since 1.0
 */
class SlstrLstProductReader extends SlstrProductReader {

    SlstrLstProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected List<String> getFileNames(Manifest manifest) {
        final List<String> fileNames = new ArrayList<String>();
        fileNames.addAll(manifest.getFileNames("LSTdataSchema"));
        fileNames.addAll(manifest.getFileNames("FRPdataSchema"));

        fileNames.addAll(manifest.getFileNames("geodeticTiepointCoordinatesSchema"));
        fileNames.addAll(manifest.getFileNames("cartesianTiepointCoordinatesSchema"));
        fileNames.addAll(manifest.getFileNames("nadirSolarViewGeometrySchema"));
        fileNames.addAll(manifest.getFileNames("meteorologicalDataSchema"));

        fileNames.addAll(manifest.getFileNames("nadirFlagsSchema"));
        fileNames.addAll(manifest.getFileNames("nadirIndicesSchema"));

        // TODO - time data are provided in a 64-bit variable, so we currently don't use them

        return fileNames;
    }

    @Override
    protected void setGeoCoding(Product targetProduct) throws IOException {
        // TODO - delete when tie point data in LST are valid
    }
}
