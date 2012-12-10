package org.esa.beam.dataio.s3.synergy;/*
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

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.beam.dataio.s3.AbstractProductFactory;
import org.esa.beam.dataio.s3.LonLatFunction;
import org.esa.beam.dataio.s3.LonLatMultiLevelSource;
import org.esa.beam.dataio.s3.Manifest;
import org.esa.beam.dataio.s3.Sentinel3ProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.PixelGeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.RasterDataNode;
import ucar.nc2.Variable;

import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SynLevel2ProductFactory extends AbstractProductFactory {

    public SynLevel2ProductFactory(Sentinel3ProductReader productReader) {
        super(productReader);
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
    protected void addVariables(Product masterProduct, Product targetProduct) throws IOException {
        NcFile tiePointsOlc = null;
        NcFile tiePointsMet = null;

        try {
            tiePointsOlc = openNcFile("tiepoints_olci.nc");
            tiePointsMet = openNcFile("tiepoints_meteo.nc");

            final double[] tpLon = tiePointsOlc.read("OLC_TP_lon");
            final double[] tpLat = tiePointsOlc.read("OLC_TP_lat");

            final List<Variable> variables = new ArrayList<Variable>();
            variables.addAll(tiePointsOlc.getVariables("[SV][AZ]A"));
            variables.addAll(tiePointsMet.getVariables(".*"));

            for (final Variable variable : variables) {
                final double[] tpVar = tiePointsOlc.read(variable.getName());

                for (int i = 1; i <= 5; i++) {
                    final String latBandName = "latitude_CAM" + i;
                    final String lonBandName = "longitude_CAM" + i;
                    final Band latBand = targetProduct.getBand(latBandName);
                    final Band lonBand = targetProduct.getBand(lonBandName);

                    final String targetBandName = variable.getName() + "_CAM" + i;
                    final Band targetBand = new Band(targetBandName, ProductData.TYPE_FLOAT32,
                                                     masterProduct.getSceneRasterWidth(),
                                                     masterProduct.getSceneRasterHeight());
                    final MultiLevelImage targetImage = createTiePointImage(lonBand.getGeophysicalImage(),
                                                                            latBand.getGeophysicalImage(),
                                                                            tpLon,
                                                                            tpLat, tpVar,
                                                                            77);

                    targetBand.setSourceImage(targetImage);
                    targetProduct.addBand(targetBand);
                }
            }
        } finally {
            if (tiePointsOlc != null) {
                tiePointsOlc.close();
            }
            if (tiePointsMet != null) {
                tiePointsMet.close();
            }
        }

    }

    private NcFile openNcFile(String fileName) throws IOException {
        return NcFile.open(new File(getInputFileParentDirectory(), fileName));
    }

    private MultiLevelImage createTiePointImage(MultiLevelImage lonImage,
                                                MultiLevelImage latImage,
                                                double[] tpLonData,
                                                double[] tpLatData,
                                                double[] tpFunctionData, int colCount) {
        final LonLatFunction function = new LonLatTiePointFunction(tpLonData,
                                                                   tpLatData,
                                                                   tpFunctionData, colCount, 0.1,
                                                                   new TiePointTileRectangleCalculator(),
                                                                   new ArcDistanceCalculatorFactory()
        );
        return new DefaultMultiLevelImage(
                LonLatMultiLevelSource.create(lonImage, latImage, function, DataBuffer.TYPE_FLOAT));
    }


    @Override
    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        if (targetNode instanceof Band) {
            final MetadataElement variableAttributes = sourceBand.getProduct().getMetadataRoot().getElement(
                    "Variable_Attributes");
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
        final GeoCoding[] geoCodings = new GeoCoding[5];
        for (int i = 1; i <= 5; i++) {
            final String latBandName = "latitude_CAM" + i;
            final String lonBandName = "longitude_CAM" + i;
            final Band latBand = targetProduct.getBand(latBandName);
            final Band lonBand = targetProduct.getBand(lonBandName);

            geoCodings[i - 1] = new PixelGeoCoding(latBand, lonBand, null, 5);
        }
        for (final Band targetBand : targetProduct.getBands()) {
            if (targetBand.getGeoCoding() == null) {
                for (int i = 1; i <= 5; i++) {
                    if (targetBand.getName().contains("CAM" + i)) {
                        targetBand.setGeoCoding(geoCodings[i - 1]);
                    }
                }
            }
        }
    }
}
