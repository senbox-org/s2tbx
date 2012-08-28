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

package org.esa.beam.dataio.s3.synergy;

import org.esa.beam.dataio.s3.manifest.Manifest;
import org.esa.beam.dataio.s3.manifest.ManifestProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;

import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Product reader responsible for reading VGT P data products in SAFE format.
 *
 * @author Olaf Danne
 * @author Ralf Quast
 * @since 1.0
 */
class VgtProductReader extends ManifestProductReader {

    VgtProductReader(VgtProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected List<String> getFileNames(Manifest manifest) {
        final List<String> fileNames = new ArrayList<String>();
        fileNames.addAll(manifest.getFileNames("measurementDataSchema"));
        fileNames.addAll(manifest.getFileNames("statusFlagsSchema"));
        fileNames.addAll(manifest.getFileNames("timeCoordinatesSchema"));
        fileNames.addAll(manifest.getFileNames("tiepointsSchema"));
        fileNames.addAll(manifest.getFileNames("geometryDataSchema"));

        return fileNames;
    }

    @Override
    protected Band addSpecialNode(Band sourceBand, Product targetProduct) {
        final Band targetBand = copyBand(sourceBand, targetProduct, false);
        final RenderingHints renderingHints = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
                                                                 BorderExtender.createInstance(
                                                                         BorderExtender.BORDER_COPY));

        targetBand.setSourceImage(ScaleDescriptor.create(sourceBand.getSourceImage(), 8.0f, 8.0f, 0.0f, 0.0f,
                                                         Interpolation.getInstance(Interpolation.INTERP_BILINEAR),
                                                         renderingHints));
        return targetBand;
    }
}
