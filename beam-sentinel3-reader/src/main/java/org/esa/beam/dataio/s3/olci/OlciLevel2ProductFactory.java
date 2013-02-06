package org.esa.beam.dataio.s3.olci;/*
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

import org.esa.beam.dataio.s3.AbstractProductFactory;
import org.esa.beam.dataio.s3.Manifest;
import org.esa.beam.dataio.s3.Sentinel3ProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.datamodel.TiePointGeoCoding;
import org.esa.beam.framework.datamodel.TiePointGrid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OlciLevel2ProductFactory extends AbstractProductFactory {

    private static final SpectralBandProperties SPECTRAL_BAND_PROPERTIES;

    static {
        SPECTRAL_BAND_PROPERTIES = new SpectralBandProperties();
    }

    public OlciLevel2ProductFactory(Sentinel3ProductReader productReader) {
        super(productReader);
    }

    @Override
    protected List<String> getFileNames(Manifest manifest) {
        final List<String> fileList = new ArrayList<String>();

        fileList.addAll(manifest.getFileNames("measurementDataSchema"));
        fileList.addAll(manifest.getFileNames("geoCoordinatesSchema"));
        fileList.addAll(manifest.getFileNames("tiepointsDataSchema"));

        return fileList;
    }

    @Override
    protected RasterDataNode addSpecialNode(Product masterProduct, Band sourceBand, Product targetProduct) {
        final Product sourceProduct = sourceBand.getProduct();
        final MetadataElement metadataRoot = sourceProduct.getMetadataRoot();
        final MetadataElement globalAttributes = metadataRoot.getElement("Global_Attributes");
        final int subsampling = globalAttributes.getAttributeInt("subsampling_factor");

        return copyBandAsTiePointGrid(sourceBand, targetProduct, subsampling, subsampling, 0.0f, 0.0f);
    }

    @Override
    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        if (targetNode.getName().matches("RC?[0-9]{3}[0-9]?")) {
            if (targetNode instanceof Band) {
                final Band targetBand = (Band) targetNode;
                final int beginIndex = targetNode.getName().matches("RC[0-9]{3}[0-9]?") ? 2 : 1;
                final int bandWavelength = Integer.parseInt(targetBand.getName().substring(beginIndex));
                final int bandIndex = findNearestSpectralBandIndex(bandWavelength);
                targetBand.setSpectralWavelength(SPECTRAL_BAND_PROPERTIES.getWavelength(bandIndex));
                targetBand.setSpectralBandwidth(SPECTRAL_BAND_PROPERTIES.getBandwidth(bandIndex));
            }
        }
    }

    private int findNearestSpectralBandIndex(int bandWavelength) {
        float minWavelengthDifference = Float.POSITIVE_INFINITY;

        for (int i = 0; i < SPECTRAL_BAND_PROPERTIES.getSpectralBandCount(); i++) {
            final float wavelengthDist = Math.abs(SPECTRAL_BAND_PROPERTIES.getWavelength(i) - bandWavelength);
            if (wavelengthDist < minWavelengthDifference) {
                minWavelengthDifference = wavelengthDist;
            } else {
                if (i > 0) {
                    return i - 1;
                } else {
                    return 0;
                }
            }
        }

        return SPECTRAL_BAND_PROPERTIES.getSpectralBandCount() - 1;
    }

    // TODO - use pixel geocoding
    @Override
    protected void setGeoCoding(Product targetProduct) throws IOException {
        final TiePointGrid latGrid = targetProduct.getTiePointGrid("TP_latitude");
        if (latGrid != null) {
            final TiePointGrid lonGrid = targetProduct.getTiePointGrid("TP_longitude");
            if (lonGrid != null) {
                targetProduct.setGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
            }
        }
    }

    @Override
    protected void setAutoGrouping(Product[] sourceProducts, Product targetProduct) {
        targetProduct.setAutoGrouping("R*_er:R*:A865:AD443:ADG:APH:ATOT:BBP:CHL:IWV:KD490:PAR:T865:TSM:ZHL:ZSD");
    }

}
