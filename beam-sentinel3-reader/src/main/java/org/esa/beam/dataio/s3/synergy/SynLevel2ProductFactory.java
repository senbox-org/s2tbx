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
import com.bc.ceres.glevel.MultiLevelModel;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.beam.dataio.netcdf.util.DataTypeUtils;
import org.esa.beam.dataio.s3.AbstractProductFactory;
import org.esa.beam.dataio.s3.Manifest;
import org.esa.beam.dataio.s3.Sentinel3ProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.PixelGeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.jai.ResolutionLevel;
import ucar.ma2.DataType;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynLevel2ProductFactory extends AbstractProductFactory {

    private List<GeoCoding> geoCodingList;

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
    protected void addDataNodes(Product targetProduct) throws IOException {
        super.addDataNodes(targetProduct);
        /*
        final List<String> fileNames = getManifest().getFileNames("tiepointsSchema");
        final Product masterProduct = findMasterProduct();
        final File directory = getInputFileParentDirectory();

        Map<String, float[]> latValueMap = new HashMap<String, float[]>();
        Map<String, float[]> lonValueMap = new HashMap<String, float[]>();
        Map<String, List<Variable>> variableMap = new HashMap<String, List<Variable>>();
        for (String fileName : fileNames) {
            final File fileLocation = new File(directory, fileName);
            final NetcdfFile netcdfFile = NetcdfFile.open(fileLocation.getPath());
            try {
                final List<Variable> fileVariables = netcdfFile.getVariables();
                variableMap.put(fileName, fileVariables);
                for (Variable fileVariable : fileVariables) {
                    if (fileVariable.getName().endsWith("_lat")) {
                        latValueMap.put(fileName, getVariableDataAsFloatArray(fileVariable));
                    } else if (fileVariable.getName().endsWith("_lon")) {
                        lonValueMap.put(fileName, getVariableDataAsFloatArray(fileVariable));
                    }
                }
            } finally {
                try {
                    netcdfFile.close();
                } catch (IOException ignored) {
                }
            }
        }
        if (latValueMap.containsKey("tiepoints_olci.nc")) {
            latValueMap.put("tiepoints_meteo.nc", latValueMap.get("tiepoints_olci.nc"));
        }
        if (lonValueMap.containsKey("tiepoints_olci.nc")) {
            lonValueMap.put("tiepoints_meteo.nc", lonValueMap.get("tiepoints_olci.nc"));
        }
        for (String fileName : fileNames) {
            for (Variable variable : variableMap.get(fileName)) {
                for (int i = 1; i <= 5; i++) {
                    final int dataType = DataTypeUtils.getRasterDataType(variable);
                    Band targetBand = new Band(variable.getName() + "_CAM" + i, dataType,
                                               masterProduct.getSceneRasterWidth(), masterProduct.getSceneRasterHeight());
                    final RenderedImage sourceImage = createTiepointSourceImage(masterProduct, variable, geoCodingList.get(i - 1),
                                                                                latValueMap.get(fileName), lonValueMap.get(fileName));
                    targetBand.setSourceImage(sourceImage);
                    targetProduct.addBand(targetBand);
                }
            }
        }
        */
    }

    private float[] getVariableDataAsFloatArray(Variable fileVariable) throws IOException {
        final Object storage = fileVariable.read().getStorage();
        if (storage instanceof int[]) {
            float[] positions = new float[((int[]) storage).length];
            for (int i = 0; i < positions.length; i++) {
                String positionString;
                positionString = Integer.toString(((int[]) storage)[i]);
                positions[i] = Float.parseFloat(positionString);
            }
            return positions;
        }
        if (storage instanceof double[]) {
            float[] positions = new float[((double[]) storage).length];
            for (int i = 0; i < positions.length; i++) {
                String positionString;
                positionString = Double.toString(((double[]) storage)[i]);
                positions[i] = Float.parseFloat(positionString);
            }
            return positions;
        }
        return (float[]) storage;
    }

    protected RenderedImage createTiepointSourceImage(final Product masterProduct, final Variable variable,
                                                      final GeoCoding geoCoding, final float[] latValues,
                                                      final float[] lonValues) {
        final MultiLevelModel model = ImageManager.getInstance().getMultiLevelModel(masterProduct.getBandAt(0));
        MultiLevelImage multiLevelImage =
                new DefaultMultiLevelImage(new AbstractMultiLevelSource(model) {

                    @Override
                    public RenderedImage createImage(int level) {
                        return new SynTiePointImage(variable, getDataBufferType(variable.getDataType()), masterProduct,
                                                    ResolutionLevel.create(getModel(), level), geoCoding, latValues, lonValues);
                    }
                });
        return multiLevelImage;
    }

    private int getDataBufferType(DataType dataType) {
        if (dataType == DataType.SHORT) {
            return DataBuffer.TYPE_SHORT;
        } else if (dataType == DataType.INT) {
            return DataBuffer.TYPE_INT;
        } else if (dataType == DataType.FLOAT) {
            return DataBuffer.TYPE_FLOAT;
        } else if (dataType == DataType.DOUBLE) {
            return DataBuffer.TYPE_DOUBLE;
        } else {
            return DataBuffer.TYPE_UNDEFINED;
        }
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

    private void setGeoCoding(Band targetBand, Product targetProduct) {
        initGeoCodingList(targetProduct);
        final int camera = Integer.parseInt("" + targetBand.getName().charAt(targetBand.getName().length() - 1));
        targetBand.setGeoCoding(geoCodingList.get(camera - 1));
    }

    @Override
    protected void setGeoCoding(Product targetProduct) throws IOException {
        initGeoCodingList(targetProduct);
        for (final Band targetBand : targetProduct.getBands()) {
//            targetBand.get
            if (targetBand.getGeoCoding() == null) {
                for (int i = 1; i <= 5; i++) {
                    if (targetBand.getName().contains("CAM" + i)) {
                        targetBand.setGeoCoding(geoCodingList.get(i - 1));
                        break;
                    }
                }
            }
        }
    }

    private void initGeoCodingList(Product targetProduct) {
        if (geoCodingList == null) {
            geoCodingList = new ArrayList<GeoCoding>();
            for (int i = 1; i <= 5; i++) {
                final String latBandName = "latitude_CAM" + i;
                final String lonBandName = "longitude_CAM" + i;
                final Band latBand = targetProduct.getBand(latBandName);
                final Band lonBand = targetProduct.getBand(lonBandName);
                final GeoCoding geoCoding = new PixelGeoCoding(latBand, lonBand, null, 5);

                geoCodingList.add(geoCoding);
            }
        }
    }

}
