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
import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.PixelGeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.RasterDataNode;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Product reader responsible for reading OLCI/SLSTR L2 SYN data products in SAFE format.
 *
 * @author Olaf Danne
 * @author Ralf Quast
 * @since 1.0
 */
class SynLevel2ProductReader extends ManifestProductReader {

    SynLevel2ProductReader(SynLevel2ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected List<String> getFileNames(Manifest manifest) {
        final List<String> fileNames = new ArrayList<String>();
        fileNames.addAll(manifest.getFileNames("geocoordinatesSchema"));
        fileNames.addAll(manifest.getFileNames("measurementDataSchema"));
        fileNames.addAll(manifest.getFileNames("geometryDataSchema"));

        // TODO - time  data are provided on a different grid, so we currently don't use them
        // TODO - meteo data are provided on a different grid, so we currently don't use them

        return fileNames;
    }

    @Override
    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        if (targetNode instanceof Band) {
            final MetadataElement variableAttributes = sourceBand.getProduct().getMetadataRoot().getElement("Variable_Attributes");
            if (variableAttributes != null) {
                final MetadataElement element =
                        variableAttributes.getElement(targetNode.getName().replaceAll("_CAM[1-5]", ""));
                if (element != null) {
                    final MetadataAttribute wavelengthAttribute = element.getAttribute("central_wavelength");
                    final Band targetBand = (Band) targetNode;
                    if (wavelengthAttribute != null) {
                        targetBand.setSpectralWavelength(wavelengthAttribute.getData().getElemFloat());
                    }
                    final MetadataAttribute minWavelengthAttribute = element.getAttribute("min_wavelength");
                    final MetadataAttribute maxWavelengthAttribute = element.getAttribute("max_wavelength");
                    if (minWavelengthAttribute != null && maxWavelengthAttribute != null) {
                        float bandwidth = maxWavelengthAttribute.getData().getElemFloat() - minWavelengthAttribute.getData().getElemFloat();
                        targetBand.setSpectralBandwidth(bandwidth);
                    }
                }
            }
        }
    }

    @Override
    protected void setGeoCoding(Product targetProduct) throws IOException {
        final List<GeoCoding> geoCodingList = new ArrayList<GeoCoding>();
        for (int i = 1; i <= 5; i++) {
            final String latBandName = "latitude_CAM" + i;
            final String lonBandName = "longitude_CAM" + i;
            final Band latBand = targetProduct.getBand(latBandName);
            final Band lonBand = targetProduct.getBand(lonBandName);
            final GeoCoding geoCoding = new PixelGeoCoding(latBand, lonBand, null, 5);

            geoCodingList.add(geoCoding);
        }
        for (final Band targetBand : targetProduct.getBands()) {
            for (int i = 1; i <= 5; i++) {
                if (targetBand.getName().contains("CAM" + i)) {
                    targetBand.setGeoCoding(geoCodingList.get(i - 1));
                    break;
                }
            }
        }
    }

}
