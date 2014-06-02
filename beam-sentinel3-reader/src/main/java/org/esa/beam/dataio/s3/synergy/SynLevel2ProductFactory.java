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
import org.esa.beam.framework.datamodel.GeoCodingFactory;
import org.esa.beam.framework.datamodel.IndexCoding;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.datamodel.VirtualBand;
import org.esa.beam.util.ProductUtils;
import ucar.nc2.Variable;

import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO - check flag codings, masks, auto-grouping, if everything is correct for mosaic of camera images

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

        return fileNames;
    }

    @Override
    protected int getSceneRasterWidth(Product masterProduct) {
        return masterProduct.getSceneRasterWidth() * 5;
    }

    @Override
    protected void addDataNodes(Product masterProduct, Product targetProduct) throws IOException {
        for (final Product sourceProduct : getOpenProductList()) {
            final Map<String, String> mapping = new HashMap<String, String>();
            final Map<String, List<String>> partition = Partitioner.partition(sourceProduct.getBandNames(), "_CAM");

            for (final Map.Entry<String, List<String>> entry : partition.entrySet()) {
                String targetBandName = buildTargetBandName(sourceProduct, entry.getKey());
                final List<String> sourceBandNames = entry.getValue();
                final String sourceBandName = sourceBandNames.get(0);
                final Band targetBand = ProductUtils.copyBand(sourceBandName, sourceProduct, targetBandName,
                                                              targetProduct, false);
                final MultiLevelImage[] sourceImages = new MultiLevelImage[sourceBandNames.size()];
                for (int i = 0; i < sourceImages.length; i++) {
                    sourceImages[i] = sourceProduct.getBand(sourceBandNames.get(i)).getSourceImage();
                }
                targetBand.setSourceImage(CameraImageMosaic.create(sourceImages));
                final Band sourceBand = sourceProduct.getBand(sourceBandName);
                configureTargetNode(sourceBand, targetBand);
                mapping.put(sourceBand.getName(), targetBand.getName());
            }
            copyMasks(targetProduct, sourceProduct, mapping);
        }
        addCameraIndexBand(targetProduct, masterProduct.getSceneRasterWidth());
    }

    private String buildTargetBandName(Product sourceProduct, String bandName) {
        StringBuilder targetBandNameBuilder = new StringBuilder(bandName);
        if (sourceProduct.getName().startsWith("r")) {
            if (sourceProduct.getName().endsWith("n")) {
                targetBandNameBuilder.append("_n");
            }
            if (sourceProduct.getName().endsWith("o")) {
                targetBandNameBuilder.append("_o");
            }
        }
        return targetBandNameBuilder.toString();
    }

    private void addCameraIndexBand(Product targetProduct, int cameraImageWidth) {
        final int sceneRasterWidth = targetProduct.getSceneRasterWidth();
        final int sceneRasterHeight = targetProduct.getSceneRasterHeight();
        StringBuilder expression = new StringBuilder();
        int width = 0;
        for (int i = 0; i < 4; i++) {
            width += cameraImageWidth;
            expression.append("X < ").append(width).append(" ? ");
            expression.append(i);
            expression.append(" : ");
            if (i == 3) {
                expression.append(i + 1);
            }
        }
        Band cameraIndexBand = new VirtualBand("Camera_Index", ProductData.TYPE_INT8,
                                               sceneRasterWidth, sceneRasterHeight, expression.toString());
        targetProduct.addBand(cameraIndexBand);
        IndexCoding indexCoding = new IndexCoding("Camera_Index");
        for (int i = 0; i < 5; i++) {
            final String description = "Images from camera " + i;
            indexCoding.addIndex("Camera_Index_" + (i + 1), i, description);
        }

        cameraIndexBand.setSampleCoding(indexCoding);
        targetProduct.getIndexCodingGroup().add(indexCoding);
    }

    @Override
    protected void addSpecialVariables(Product masterProduct, Product targetProduct) throws IOException {
        final double[] olcTpLon;
        final double[] olcTpLat;
        final NcFile olcTiePoints = openNcFile("tiepoints_olci.nc");
        try {
            olcTpLon = olcTiePoints.read("OLC_TP_lon");
            olcTpLat = olcTiePoints.read("OLC_TP_lat");
        } finally {
            olcTiePoints.close();
        }
        addVariables(targetProduct, olcTpLon, olcTpLat, "tiepoints_olci.nc");
        addVariables(targetProduct, olcTpLon, olcTpLat, "tiepoints_meteo.nc");

        final double[] slnTpLon;
        final double[] slnTpLat;
        final NcFile slnTiePoints = openNcFile("tiepoints_slstr_n.nc");
        try {
            slnTpLon = slnTiePoints.read("SLN_TP_lon");
            slnTpLat = slnTiePoints.read("SLN_TP_lat");
        } finally {
            slnTiePoints.close();
        }
        addVariables(targetProduct, slnTpLon, slnTpLat, "tiepoints_slstr_n.nc");

        final double[] sloTpLon;
        final double[] sloTpLat;
        final NcFile sloTiePoints = openNcFile("tiepoints_slstr_o.nc");
        try {
            sloTpLon = sloTiePoints.read("SLO_TP_lon");
            sloTpLat = sloTiePoints.read("SLO_TP_lat");
        } finally {
            sloTiePoints.close();
        }
        addVariables(targetProduct, sloTpLon, sloTpLat, "tiepoints_slstr_o.nc");
    }

    private void addVariables(Product targetProduct, double[] tpLon, double[] tpLat, String fileName) throws
                                                                                                      IOException {
        final String latBandName = "latitude";
        final String lonBandName = "longitude";
        final Band latBand = targetProduct.getBand(latBandName);
        final Band lonBand = targetProduct.getBand(lonBandName);

        final NcFile ncFile = openNcFile(fileName);
        try {
            final List<Variable> variables = ncFile.getVariables(".*");
            for (final Variable variable : variables) {
                final String targetBandName = variable.getName();
                final Band targetBand = targetProduct.addBand(targetBandName, ProductData.TYPE_FLOAT32);
                final double[] tpVar = ncFile.read(variable.getName());
                final MultiLevelImage targetImage = createTiePointImage(lonBand.getGeophysicalImage(),
                                                                        latBand.getGeophysicalImage(),
                                                                        tpLon,
                                                                        tpLat, tpVar,
                                                                        400);

                targetBand.setSourceImage(targetImage);
            }
        } finally {
            ncFile.close();
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
                                                                   tpFunctionData, colCount);
        return new DefaultMultiLevelImage(
                LonLatMultiLevelSource.create(lonImage, latImage, function, DataBuffer.TYPE_FLOAT));
    }

    @Override
    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        if (targetNode instanceof Band) {
            final MetadataElement variableAttributes = sourceBand.getProduct().getMetadataRoot().getElement(
                    "Variable_Attributes");
            if (variableAttributes != null) {
                final MetadataElement element = variableAttributes.getElement(
                        sourceBand.getName().replaceAll("_CAM[1-5]", ""));
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
        final String latBandName = "latitude";
        final String lonBandName = "longitude";
        final Band latBand = targetProduct.getBand(latBandName);
        final Band lonBand = targetProduct.getBand(lonBandName);

        targetProduct.setGeoCoding(GeoCodingFactory.createPixelGeoCoding(latBand, lonBand, null, 5));
    }

    @Override
    protected void setAutoGrouping(Product[] sourceProducts, Product targetProduct) {
        targetProduct.setAutoGrouping("SDR*er:SDR*er_n:SDR*er_o:SDR*n:SDR*o:SDR");
    }

}
