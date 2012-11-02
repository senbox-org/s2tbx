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
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.RasterDataNode;

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
    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        if (targetNode instanceof Band) {
            final MetadataElement variableAttributes =
                    sourceBand.getProduct().getMetadataRoot().getElement("Variable_Attributes");
            if(variableAttributes != null) {
                final MetadataElement metadataElement = variableAttributes.getElement(sourceBand.getName());
                if (metadataElement != null) {
                    final MetadataAttribute bandwidthAttribute = metadataElement.getAttribute("bandwidth");
                    if (bandwidthAttribute != null) {
                        ((Band) targetNode).setSpectralBandwidth(bandwidthAttribute.getData().getElemFloat());
                    }
                    final MetadataAttribute wavelengthAttribute = metadataElement.getAttribute("wavelength");
                    if (wavelengthAttribute != null) {
                        ((Band) targetNode).setSpectralWavelength(wavelengthAttribute.getData().getElemFloat());
                    }
                }
            }
        }
    }

    @Override
    protected Band addSpecialNode(Band sourceBand, Product targetProduct) {
        final Band targetBand = copyBand(sourceBand, targetProduct, false);
        final RenderingHints renderingHints = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
                                                                 BorderExtender.createInstance(
                                                                         BorderExtender.BORDER_COPY));
        // TODO: here we border effects because no-data value is used for interpolation
        targetBand.setSourceImage(ScaleDescriptor.create(sourceBand.getSourceImage(), 8.0f, 8.0f, 0.0f, 0.0f,
                                                         Interpolation.getInstance(Interpolation.INTERP_BILINEAR),
                                                         renderingHints));
        return targetBand;
    }
}
