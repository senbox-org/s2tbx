/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2.ortho;

import org.esa.s2tbx.dataio.s2.S2ProductReaderPlugIn;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.l1c.Sentinel2L1CProductReader;
import org.esa.s2tbx.dataio.s2.l2a.Sentinel2L2AProductReader;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleMetadataFilename;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;
import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;

import static org.esa.s2tbx.dataio.s2.ortho.S2CRSHelper.*;

/**
 * @author Norman Fomferra
 */
public abstract class S2OrthoProductReaderPlugIn extends S2ProductReaderPlugIn {

    private static S2ProductCRSCache crsCache = new S2ProductCRSCache();

    // Product level: L1C, L2A...
    private String level = "";

    public S2OrthoProductReaderPlugIn() {
        RGBImageProfileManager manager = RGBImageProfileManager.getInstance();
        manager.addProfile(new RGBImageProfile("Sentinel 2 MSI Natural Colors", new String[]{"B4", "B3", "B2"}));
        manager.addProfile(new RGBImageProfile("Sentinel 2 MSI False-color Infrared", new String[]{"B8", "B4", "B3"}));
    }

    protected String getLevel() {
        return level;
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        SystemUtils.LOG.fine("Getting decoders...");

        DecodeQualification decodeQualification = DecodeQualification.UNABLE;

        if (!(input instanceof File)) {
            return DecodeQualification.UNABLE;
        }

        File file = (File) input;
        String fileName = file.getName();
        Matcher matcher = PATTERN.matcher(fileName);

        // Checking for file regex first, it is quicker than File.isFile()
        if (!matcher.matches()) {
            return DecodeQualification.UNABLE;
        }
        if (!file.isFile()) {
            return DecodeQualification.UNABLE;
        }

        // test for granule filename first as it is more restrictive
        if (S2OrthoGranuleMetadataFilename.isGranuleFilename(fileName)) {
            level = matcher.group(4).substring(0, 3);
            S2OrthoGranuleMetadataFilename granuleMetadataFilename = S2OrthoGranuleMetadataFilename.create(fileName);
            if (granuleMetadataFilename != null &&
                    (level.equals("L1C") ||
                            (level.equals("L2A") && (this instanceof S2OrthoProduct10MReaderPlugIn ||
                                    this instanceof S2OrthoProduct20MReaderPlugIn ||
                                    this instanceof S2OrthoProduct60MReaderPlugIn
                            )))) {
                String tileId = granuleMetadataFilename.tileNumber;
                String epsg = tileIdentifierToEPSG(tileId);
                if (getEPSG() != null && getEPSG().equalsIgnoreCase(epsg)) {
                    decodeQualification = DecodeQualification.INTENDED;
                }
            }
        } else if (S2ProductFilename.isMetadataFilename(fileName)) {
            level = matcher.group(4).substring(3);
            S2ProductFilename productFilename = S2ProductFilename.create(fileName);
            if (productFilename != null) {
                if (level.equals("L1C") ||
                        // no multi-resolution for L2A products
                        (level.equals("L2A") &&
                                (this instanceof S2OrthoProduct10MReaderPlugIn ||
                                        this instanceof S2OrthoProduct20MReaderPlugIn ||
                                        this instanceof S2OrthoProduct60MReaderPlugIn
                                ))) {
                    crsCache.ensureIsCached(file.getAbsolutePath());
                    if (getEPSG() != null && crsCache.hasEPSG(file.getAbsolutePath(), getEPSG())) {
                        decodeQualification = DecodeQualification.INTENDED;
                    }
                }
            }
        }

        return decodeQualification;
    }

    public abstract String getEPSG();

    @Override
    public ProductReader createReaderInstance() {
        if (level != null && level.equals("L2A")) {
            return new Sentinel2L2AProductReader(this, Sentinel2ProductReader.ProductInterpretation.RESOLUTION_MULTI, getEPSG());
        } else {
            return new Sentinel2L1CProductReader(this, Sentinel2ProductReader.ProductInterpretation.RESOLUTION_MULTI, getEPSG());
        }
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{FORMAT_NAME + "-MultiRes-" + epsgToShortDisplayName(getEPSG())};
    }

    @Override
    public String getDescription(Locale locale) {
        return String.format("Sentinel-2 MSI %s - Native resolutions - %s", getLevel(), epsgToDisplayName(getEPSG()));
    }

    protected boolean hasL2ResolutionSpecificFolder(Object input, String specificFolder) {

        if (!(input instanceof File)) {
            return false;
        }

        File file = (File) input;
        String fileNameComplete = file.toString(); //file name with full path
        String fileName = file.getName(); //file name without path

        if (S2OrthoGranuleMetadataFilename.isGranuleFilename(fileName)) { //when input is a granule

            Path rootPath = new File(fileNameComplete).toPath().getParent();
            File imgFolder = rootPath.resolve("IMG_DATA").toFile();
            File[] files = imgFolder.listFiles();

            if (files != null) {
                for (File imgData : files) {
                    if (imgData.isDirectory()) {
                        if (imgData.getName().equals(specificFolder)) {
                            return true;
                        }
                    }
                }
            }

        } else if (S2ProductFilename.isMetadataFilename(fileName)) { //when input is the global xml

            Path rootPath = new File(fileNameComplete).toPath().getParent();
            File granuleFolder = rootPath.resolve("GRANULE").toFile();
            File[] files = granuleFolder.listFiles();

            if (files != null) {
                for (File granule : files) {
                    if (granule.isDirectory()) {
                        Path granulePath = new File(granule.toString()).toPath();
                        File internalGranuleFolder = granulePath.resolve("IMG_DATA").toFile();
                        File[] files2 = internalGranuleFolder.listFiles();
                        if (files2 != null) {
                            for (File imgData : files2) {
                                if (imgData.isDirectory()) {
                                    if (imgData.getName().equals(specificFolder)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }


}
