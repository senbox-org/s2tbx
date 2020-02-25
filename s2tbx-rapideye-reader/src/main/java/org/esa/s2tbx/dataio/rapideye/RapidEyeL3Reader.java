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

package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.s2tbx.dataio.readers.MetadataList;
import org.esa.s2tbx.dataio.readers.MultipleMetadataGeoTiffBasedReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Reader for RapidEye L3 (GeoTIFF) products.
 *
 * @author  Cosmin Cara
 */
public class RapidEyeL3Reader extends MultipleMetadataGeoTiffBasedReader<RapidEyeMetadata> {

    static {
        XmlMetadataParserFactory.registerParser(RapidEyeMetadata.class, new XmlMetadataParser<>(RapidEyeMetadata.class));
    }

    public RapidEyeL3Reader(RapidEyeL3ReaderPlugin readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn);
    }

    @Override
    protected RapidEyeMetadata findFirstMetadataItem(MetadataList<RapidEyeMetadata> metadataList) {
        for (int i=0; i<metadataList.getCount(); i++) {
            RapidEyeMetadata rapidEyeMetadata = metadataList.getMetadataAt(i);
            String metadataProfile = rapidEyeMetadata.getMetadataProfile();
            if (metadataProfile != null && metadataProfile.startsWith(RapidEyeConstants.PROFILE_L3)) {
                return rapidEyeMetadata;
            }
        }
        return null;
    }

    @Override
    protected TiePointGeoCoding buildTiePointGridGeoCoding(RapidEyeMetadata firstMetadata, MetadataList<RapidEyeMetadata> metadataList, ProductSubsetDef productSubsetDef) {
        return null; // no geo coding for RapidEye product
    }

    @Override
    protected String getGenericProductName() {
        return "RapidEye";
    }

    @Override
    protected String[] getBandNames(RapidEyeMetadata metadata) {
        return RapidEyeConstants.BAND_NAMES;
    }

    @Override
    protected List<Mask> buildMasks(int productWith, int productHeight, RapidEyeMetadata firstMetadata, ProductSubsetDef subsetDef) {
        return null; // no masks for RapidEye product
    }

    @Override
    protected String getProductType() {
        return RapidEyeConstants.L3_FORMAT_NAMES[0];
    }

    @Override
    protected MetadataList<RapidEyeMetadata> readMetadataList(VirtualDirEx productDirectory) throws IOException, InstantiationException, ParserConfigurationException, SAXException {
        return readMetadata(productDirectory);
    }

    public static MetadataList<RapidEyeMetadata> readMetadata(VirtualDirEx productDirectory) throws IOException, InstantiationException, ParserConfigurationException, SAXException {
        return readMetadata(productDirectory, RapidEyeConstants.METADATA_FILE_SUFFIX, RapidEyeMetadata.class);
    }
}
