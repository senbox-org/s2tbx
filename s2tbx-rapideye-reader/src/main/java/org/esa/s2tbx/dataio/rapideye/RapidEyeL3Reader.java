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

import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.s2tbx.dataio.readers.GeoTiffBasedReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.TreeNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Reader for RapidEye L3 (GeoTIFF) products.
 *
 * @author  Cosmin Cara
 */
public class RapidEyeL3Reader extends GeoTiffBasedReader<RapidEyeMetadata> {

    protected RapidEyeL3Reader(RapidEyeL3ReaderPlugin readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn, colorPaletteFilePath);
    }

    @Override
    protected String getMetadataExtension() {
        return RapidEyeConstants.METADATA_FILE_SUFFIX.substring(RapidEyeConstants.METADATA_FILE_SUFFIX.indexOf("."));
    }

    @Override
    protected String getMetadataProfile() {
        return RapidEyeConstants.PROFILE_L3;
    }

    @Override
    protected String getProductGenericName() {
        return RapidEyeConstants.PRODUCT_GENERIC_NAME;
    }

    @Override
    protected String getMetadataFileSuffix() {
        return RapidEyeConstants.METADATA_FILE_SUFFIX;
    }

    @Override
    protected String[] getBandNames() {
        return RapidEyeConstants.BAND_NAMES;
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isArchive()) {
            return super.getProductComponents();
        } else {
            RapidEyeMetadata firstMetadata = metadata.get(0);
            TreeNode<File> result = super.getProductComponents();
            String metaFileName = firstMetadata.getFileName();
            try{
                addProductComponentIfNotPresent(metaFileName, productDirectory.getFile(metaFileName), result);
            } catch (IOException e) {
                logger.warning(String.format("Error encountered while searching file %s", metaFileName));
            }
            String[] rasterFiles = firstMetadata.getRasterFileNames();
            for(String fileName : rasterFiles){
                try{
                    addProductComponentIfNotPresent(fileName, productDirectory.getFile(fileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", fileName));
                }
            }
            String maskFileName = firstMetadata.getMaskFileName();
            if (maskFileName != null) {
                try{
                    addProductComponentIfNotPresent(maskFileName, productDirectory.getFile(maskFileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", maskFileName));
                }
            }
            return result;
        }
    }
}
