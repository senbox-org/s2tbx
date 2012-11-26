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

import org.esa.beam.dataio.s3.AbstractManifestProductFactory;
import org.esa.beam.dataio.s3.ManifestI;
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
import java.util.Properties;

public class OlciLevel2ProductFactory extends AbstractManifestProductFactory {

    private static final float[] spectralWavelengths = new float[21];
    private static final float[] spectralBandwidths = new float[21];

    public OlciLevel2ProductFactory(Sentinel3ProductReader productReader) {
        super(productReader);
    }

    static {
        getSpectralBandsProperties(spectralWavelengths, spectralBandwidths);
    }

    static void getSpectralBandsProperties(float[] wavelengths, float[] bandwidths) {
        final Properties properties = new Properties();

        try {
            properties.load(OlciLevel2ProductFactory.class.getResourceAsStream("spectralBands.properties"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        for (int i = 0; i < wavelengths.length; i++) {
            wavelengths[i] = Float.parseFloat(properties.getProperty("wavelengths." + i));
        }
        for (int i = 0; i < bandwidths.length; i++) {
            bandwidths[i] = Float.parseFloat(properties.getProperty("bandwidths." + i));
        }
    }

    @Override
    protected List<String> getFileNames(ManifestI manifest) {
        final List<String> fileList = new ArrayList<String>();

        fileList.addAll(manifest.getFileNames("measurementDataSchema"));
        fileList.addAll(manifest.getFileNames("geoCoordinatesSchema"));
        fileList.addAll(manifest.getFileNames("tiepointsDataSchema"));

        return fileList;
    }

    @Override
    protected RasterDataNode addSpecialNode(Band sourceBand, Product targetProduct) {
        final Product sourceProduct = sourceBand.getProduct();
        final MetadataElement metadataRoot = sourceProduct.getMetadataRoot();
        final MetadataElement globalAttributes = metadataRoot.getElement("Global_Attributes");
        final int subsampling = globalAttributes.getAttributeInt("subsampling_factor");

        return copyBand(sourceBand, targetProduct, subsampling, subsampling, 0.0f, 0.0f);
    }

    @Override
    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        if (targetNode.getName().matches("RC?[0-9]{3}[0-9]?")) {
            if (targetNode instanceof Band) {
                final Band targetBand = (Band) targetNode;
                int numberIndex = 1;
                if (targetNode.getName().matches("RC[0-9]{3}[0-9]?")) {
                    numberIndex = 2;
                }
                final int bandWavelength = Integer.parseInt(targetNode.getName().substring(numberIndex));
                int spectralBandIndex = getSpectralBandIndex(bandWavelength);
                targetBand.setSpectralWavelength(spectralWavelengths[spectralBandIndex]);
                targetBand.setSpectralBandwidth(spectralBandwidths[spectralBandIndex]);
            }
        }
    }

    private int getSpectralBandIndex(int bandWavelength) {
        float lastWavelengthDist = Float.POSITIVE_INFINITY;
        for (int i = 0; i < spectralWavelengths.length; i++) {
            final float wavelengthDist = Math.abs(spectralWavelengths[i] - bandWavelength);
            if (wavelengthDist < lastWavelengthDist) {
                lastWavelengthDist = wavelengthDist;
            } else {
                if(i>0) {
                    return i-1;
                }
                else {
                    return 0;
                }
            }
        }
        return spectralWavelengths.length - 1;
    }

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

}
