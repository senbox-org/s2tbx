/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio.deimos;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.deimos.dimap.DeimosConstants;
import org.esa.s2tbx.dataio.deimos.dimap.DeimosMetadata;
import org.esa.s2tbx.dataio.readers.MetadataList;
import org.esa.s2tbx.dataio.readers.MultipleMetadataGeoTiffBasedReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This product reader is intended for reading DEIMOS-1 files
 * from compressed archive files, from tar files or from (uncompressed) file system.
 *
 * @author Cosmin Cara
 */
public class DeimosProductReader extends MultipleMetadataGeoTiffBasedReader<DeimosMetadata> {

    static {
        XmlMetadataParserFactory.registerParser(DeimosMetadata.class, new XmlMetadataParser<>(DeimosMetadata.class));
    }

    public DeimosProductReader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn);
    }

    @Override
    protected DeimosMetadata findFirstMetadataItem(MetadataList<DeimosMetadata> metadataList) {
        return (metadataList.getCount() > 0) ? metadataList.getMetadataAt(0) : null;
    }

    @Override
    protected TiePointGeoCoding buildTiePointGridGeoCoding(DeimosMetadata firstMetadata, MetadataList<DeimosMetadata> metadataList, ProductSubsetDef productSubsetDef) {
        return buildProductTiePointGridGeoCoding(firstMetadata, metadataList, productSubsetDef);
    }

    @Override
    protected String getGenericProductName() {
        return "Deimos";
    }

    @Override
    protected String[] getBandNames(DeimosMetadata metadata) {
        return metadata.getBandNames();
    }

    @Override
    protected List<Mask> buildMasks(int productWith, int productHeight, DeimosMetadata firstMetadata, ProductSubsetDef subsetDef) {
        List<Mask> availableMasks = new ArrayList<>();
        if (subsetDef == null || subsetDef.isNodeAccepted(DeimosConstants.NODATA_VALUE)) {
            int noDataValue = firstMetadata.getNoDataValue();
            if (noDataValue >= 0) {
                Mask mask = Mask.BandMathsType.create(DeimosConstants.NODATA_VALUE, DeimosConstants.NODATA_VALUE, productWith, productHeight, String.valueOf(noDataValue), firstMetadata.getNoDataColor(), 0.5);
                availableMasks.add(mask);
            }
        }
        if (subsetDef == null || subsetDef.isNodeAccepted(DeimosConstants.SATURATED_VALUE)) {
            int saturatedValue = firstMetadata.getSaturatedPixelValue();
            if (saturatedValue >= 0) {
                Mask mask = Mask.BandMathsType.create(DeimosConstants.SATURATED_VALUE, DeimosConstants.SATURATED_VALUE, productWith, productHeight, String.valueOf(saturatedValue), firstMetadata.getSaturatedColor(), 0.5);
                availableMasks.add(mask);
            }
        }
        return availableMasks;
    }

    @Override
    protected String getProductType() {
        return DeimosConstants.DIMAP_FORMAT_NAMES[0];
    }

    @Override
    protected MetadataList<DeimosMetadata> readMetadataList(VirtualDirEx productDirectory) throws IOException, InstantiationException, ParserConfigurationException, SAXException {
        return readMetadata(productDirectory);
    }

    public static TiePointGeoCoding buildProductTiePointGridGeoCoding(DeimosMetadata firstMetadata, MetadataList<DeimosMetadata> metadataList, ProductSubsetDef productSubsetDef) {
        for (int i = 0; i < metadataList.getCount(); i++) {
            DeimosMetadata currentMetadata = metadataList.getMetadataAt(i);
            if (DeimosConstants.PROCESSING_1R.equals(currentMetadata.getProcessingLevel())) {
                return buildProductTiePointGridGeoCoding(firstMetadata, productSubsetDef);
            }
        }
        return null;
    }

    public static MetadataList<DeimosMetadata> readMetadata(VirtualDirEx productDirectory) throws IOException, InstantiationException, ParserConfigurationException, SAXException {
        return readMetadata(productDirectory, DeimosConstants.METADATA_EXTENSION, DeimosMetadata.class);
    }

    private static TiePointGeoCoding buildProductTiePointGridGeoCoding(DeimosMetadata deimosMetadata, ProductSubsetDef productSubsetDef) {
        DeimosMetadata.InsertionPoint[] geoPositionPoints = deimosMetadata.getGeopositionPoints();
        if (geoPositionPoints != null) {
            int numPoints = geoPositionPoints.length;
            if (numPoints > 1 && (int)(numPoints / Math.sqrt((double)numPoints)) == numPoints) {
                float stepX = geoPositionPoints[1].stepX - geoPositionPoints[0].stepX;
                float stepY = geoPositionPoints[1].stepY - geoPositionPoints[0].stepY;
                float[] latitudes = new float[numPoints];
                float[] longitudes = new float[numPoints];
                for (int i = 0; i < numPoints; i++) {
                    latitudes[i] = geoPositionPoints[i].y;
                    longitudes[i] = geoPositionPoints[i].x;
                }
                int latitudeGridSize = (int) Math.sqrt(latitudes.length);
                TiePointGrid latGrid = buildTiePointGrid("latitude", latitudeGridSize, latitudeGridSize, 0, 0, stepX, stepY, latitudes, TiePointGrid.DISCONT_NONE);
                int longitudeGridSize = (int) Math.sqrt(longitudes.length);
                TiePointGrid lonGrid = buildTiePointGrid("longitude", longitudeGridSize, longitudeGridSize, 0, 0, stepX, stepY, longitudes, TiePointGrid.DISCONT_AT_180);
                if (productSubsetDef != null) {
                    latGrid = TiePointGrid.createSubset(latGrid, productSubsetDef);
                    lonGrid = TiePointGrid.createSubset(lonGrid, productSubsetDef);
                }
                return new TiePointGeoCoding(latGrid, lonGrid);
            }
        }
        return null;
    }
}
