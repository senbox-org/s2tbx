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
import org.esa.beam.framework.dataio.ProductSubsetDef;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCodingFactory;
import org.esa.beam.framework.datamodel.Mask;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductNodeGroup;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.datamodel.TiePointGeoCoding;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OlciLevel1ProductFactory extends AbstractProductFactory {

    private static final SpectralBandProperties SPECTRAL_BAND_PROPERTIES;

    static {
        SPECTRAL_BAND_PROPERTIES = new SpectralBandProperties();
    }

    public OlciLevel1ProductFactory(Sentinel3ProductReader productReader) {
        super(productReader);
    }

    @Override
    protected List<String> getFileNames(Manifest manifest) {
        final File directory = getInputFileParentDirectory();

        final List<String> fileNameList = new ArrayList<String>();
        collectFileNames(directory, fileNameList, true);
        collectFileNames(directory, fileNameList, false);

        return Collections.unmodifiableList(fileNameList);
    }

    private static void collectFileNames(File directory, List<String> fileNameList, final boolean acceptRadiances) {
        final String[] radianceFileNames = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".nc") && (acceptRadiances == name.contains("radiances") && !name.contains(
                        "timeCoordinates"));
            }
        });
        fileNameList.addAll(Arrays.asList(radianceFileNames));
    }

    @Override
    protected ProductSubsetDef getSubsetDef(String fileName) {
        ProductSubsetDef subsetDef = null;
        if (fileName.equals("generalInfo.nc")) {
            subsetDef = new ProductSubsetDef();
            subsetDef.addNodeName("detector_index");
        }
        return subsetDef;
    }

    @Override
    protected void setAutoGrouping(Product[] sourceProducts, Product targetProduct) {
        targetProduct.setAutoGrouping("TOA_radiances_Oa:error_estimates_Oa:TOA_radiances_Ob:error_estimates_Ob");
    }

    @Override
    protected RasterDataNode addSpecialNode(Product masterProduct, Band sourceBand, Product targetProduct) {
        final Product sourceProduct = sourceBand.getProduct();
        final MetadataElement metadataRoot = sourceProduct.getMetadataRoot();
        final MetadataElement globalAttributes = metadataRoot.getElement("Global_Attributes");
        if (globalAttributes.containsAttribute("subsampling_factor")) {
            final int subSampling = globalAttributes.getAttributeInt("subsampling_factor");

            return copyBandAsTiePointGrid(sourceBand, targetProduct, subSampling, subSampling, 0.0f, 0.0f);
        } else {
            // TODO - handle timeCoordinates.nc and removedPixels.nc
            return null;
        }
    }

    @Override
    protected void setGeoCoding(Product targetProduct) throws IOException {
        final Band latBand = targetProduct.getBand("latitude");
        final Band lonBand = targetProduct.getBand("longitude");
        if (latBand != null && lonBand != null) {
            targetProduct.setGeoCoding(
                    GeoCodingFactory.createPixelGeoCoding(latBand, lonBand, "!quality_flags_invalid && !quality_flags_duplicated", 5));
        }
        if (targetProduct.getGeoCoding() == null) {
            if (targetProduct.getTiePointGrid("TP_latitude") != null && targetProduct.getTiePointGrid(
                    "TP_longitude") != null) {
                targetProduct.setGeoCoding(new TiePointGeoCoding(targetProduct.getTiePointGrid("TP_latitude"),
                                                                 targetProduct.getTiePointGrid("TP_longitude")));
            }
        }
    }

    @Override
    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
        if (targetNode instanceof Band) {
            final Band targetBand = (Band) targetNode;
            final String sourceBandName = sourceBand.getName();
            if (sourceBandName.matches("TOA_radiances_Oa[0-2][0-9]")) {
                final int channel = Integer.parseInt(sourceBandName.substring(16, 18));
                final int index = channel - 1;
                targetBand.setSpectralBandIndex(index);
                targetBand.setSpectralWavelength(SPECTRAL_BAND_PROPERTIES.getWavelength(index));
                targetBand.setSpectralBandwidth(SPECTRAL_BAND_PROPERTIES.getBandwidth(index));
            }
        }
        targetNode.setValidPixelExpression("!quality_flags_invalid");
    }

    @Override
    protected ProductNodeGroup<Mask> prepareMasksForCopying(ProductNodeGroup<Mask> maskGroup) {
        for (int i = 0; i < maskGroup.getNodeCount(); i++) {
            final Mask mask = maskGroup.get(i);
            if(mask.getName().equals("quality_flags_invalid")) {
                mask.setName("quality_flags_cosmetic");
            }
            else if(mask.getName().equals("quality_flags_cosmetic")) {
                mask.setName("quality_flags_invalid");
            }
            else if(mask.getName().equals("quality_flags_duplicated")) {
                mask.setName("quality_flags_sun_glint_risk");
            }
            else if(mask.getName().equals("quality_flags_sun_glint_risk")) {
                mask.setName("quality_flags_duplicated");
            }
        }
        return maskGroup;
    }
}
