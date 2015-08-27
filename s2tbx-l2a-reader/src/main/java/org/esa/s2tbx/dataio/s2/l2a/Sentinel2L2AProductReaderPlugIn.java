/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.l2a;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.snap.framework.dataio.DecodeQualification;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.io.SnapFileFilter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Norman Fomferra
 * @author Nicolas Ducoin
 */
public abstract class Sentinel2L2AProductReaderPlugIn implements ProductReaderPlugIn {

    static final String FORMAT_NAME = "SENTINEL-2-MSI-L2A";

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        SystemUtils.LOG.fine("Getting decoders...");

        File file = new File(input.toString());
        DecodeQualification deco = S2ProductFilename.isMetadataFilename(file.getName()) ? DecodeQualification.SUITABLE : DecodeQualification.UNABLE;
        if (deco.equals(DecodeQualification.SUITABLE)) {
            S2ProductFilename productFilename = S2ProductFilename.create(file.getName());
            if ( productFilename!= null &&
                    productFilename.fileSemantic.contains("L2A")) {
                deco = getDecodeQualificationFromResolution(file, getReaderResolution());
            } else {
                deco = DecodeQualification.UNABLE;
            }
        }

        return deco;
    }

    protected abstract S2SpatialResolution getReaderResolution();

    protected DecodeQualification getDecodeQualificationFromResolution(File productFile, S2SpatialResolution resolution) {
        DecodeQualification decodeQualification = DecodeQualification.UNABLE;

        File parentFolder = productFile.getParentFile();
        File granulesFolder = new File(parentFolder, "GRANULE");


        String folderNameFor10m="R10m";
        FileFilter fileFilterFor10m = pathname -> pathname.getAbsolutePath().endsWith(folderNameFor10m);
        boolean contains10mFiles = false;

        String folderNameFor20m="R20m";
        FileFilter fileFilterFor20m = pathname -> pathname.getAbsolutePath().endsWith(folderNameFor20m);
        boolean contains20mFiles = false;

        String folderNameFor60m="R60m";
        FileFilter fileFilterFor60m = pathname -> pathname.getAbsolutePath().endsWith(folderNameFor60m);
        boolean contains60mFiles = false;


        File[] filesFromGranulesFolder = granulesFolder.listFiles();
        if(filesFromGranulesFolder != null) {
            for (File granuleFolder :filesFromGranulesFolder) {
                File imageFolder = new File(granuleFolder, "IMG_DATA");

                File[] imageFolderContentFor10m = imageFolder.listFiles(fileFilterFor10m);
                if (imageFolderContentFor10m != null && imageFolderContentFor10m.length > 0) {
                    contains10mFiles = true;
                }
                File[] imageFolderContentFor20m = imageFolder.listFiles(fileFilterFor20m);
                if (imageFolderContentFor20m != null && imageFolderContentFor20m.length > 0) {
                    contains20mFiles = true;
                }
                File[] imageFolderContentFor60m = imageFolder.listFiles(fileFilterFor60m);
                if (imageFolderContentFor60m != null && imageFolderContentFor60m.length > 0) {
                    contains60mFiles = true;
                }

                if (contains10mFiles && contains20mFiles && contains60mFiles) {
                    break;
                }
            }
        }

        switch (resolution) {
            case R10M:
                if(contains10mFiles) {
                    decodeQualification = DecodeQualification.INTENDED;
                }
                break;
            case R20M:
                if(contains20mFiles) {
                    if(contains10mFiles) {
                        decodeQualification = DecodeQualification.SUITABLE;
                    } else {
                        decodeQualification = DecodeQualification.INTENDED;
                    }
                }
                break;
            case R60M:
                if(contains60mFiles) {
                    if (contains10mFiles || contains20mFiles) {
                        decodeQualification = DecodeQualification.SUITABLE;
                    } else {
                        decodeQualification = DecodeQualification.INTENDED;
                    }
                }
                break;
        }

        return decodeQualification;
    }

    @Override
    public Class[] getInputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{S2Config.MTD_EXT};
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        // returning a null formatName so that the user can further select between
        // all the intended readers for the product (different resolutions and UTM zones)

        return new SnapFileFilter(null,
                                  getDefaultFileExtensions(),
                                  "Sentinel-2 MSI L2A product or tile");
    }
}
