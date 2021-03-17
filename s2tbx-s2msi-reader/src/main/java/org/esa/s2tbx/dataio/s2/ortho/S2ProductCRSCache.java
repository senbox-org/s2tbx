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

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleMetadataFilename;
import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.esa.s2tbx.dataio.s2.ortho.S2CRSHelper.tileIdentifierToEPSG;

/**
 * @author Julien Malik
 */
public class S2ProductCRSCache {



    public class S2ProductCRSCacheEntry {
        Set<String> epsgCodeList = new HashSet<>();
        private S2Config.Sentinel2ProductLevel level = S2Config.Sentinel2ProductLevel.UNKNOWN;
        private S2Config.Sentinel2InputType inputType;
        private INamingConvention namingConvention;


        public S2ProductCRSCacheEntry (Path path) {
            try {
                VirtualPath virtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(path);
                namingConvention = NamingConventionFactory.createOrthoNamingConvention(virtualPath);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            if(namingConvention != null) {
                inputType = namingConvention.getInputType();
                level = namingConvention.getProductLevel();
                if (namingConvention.getEPSGList() != null) {
                    epsgCodeList.addAll(namingConvention.getEPSGList());
                }
            }
        }



        public S2ProductCRSCacheEntry (String productFileName) {

            File productFile = new File(productFileName);
            String fileName = productFile.getName(); //file name without path

            final Pattern PATTERN = Pattern.compile(S2Config.REGEX);
            Matcher matcher = PATTERN.matcher(fileName);
            if (!matcher.matches()) {
                return;
            }
            if (S2OrthoGranuleMetadataFilename.isGranuleFilename(fileName)) {
                inputType = S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA;
                level = S2Config.levelString2ProductLevel(matcher.group(4).substring(0, 3));
                S2OrthoGranuleMetadataFilename granuleMetadataFilename = S2OrthoGranuleMetadataFilename.create(fileName);
                if (granuleMetadataFilename != null &&
                        (level == S2Config.Sentinel2ProductLevel.L1C || level == S2Config.Sentinel2ProductLevel.L2A
                                || level == S2Config.Sentinel2ProductLevel.L3)) {
                    String tileId = granuleMetadataFilename.tileNumber;
                    String epsg = tileIdentifierToEPSG(tileId);
                    epsgCodeList.add(epsg);
                }
            } else if (S2ProductFilename.isMetadataFilename(fileName)) {
                inputType = S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA;
                level = S2Config.levelString2ProductLevel(matcher.group(4).substring(3));
                S2ProductFilename productFilename = S2ProductFilename.create(fileName);
                if (productFilename != null) {
                    if (level == S2Config.Sentinel2ProductLevel.L1C || level == S2Config.Sentinel2ProductLevel.L2A
                            || level == S2Config.Sentinel2ProductLevel.L3) {
                        Path rootPath = productFile.toPath().getParent();
                        File granuleFolder = rootPath.resolve("GRANULE").toFile();
                        if(!granuleFolder.exists() || !granuleFolder.isDirectory()) {
                            SystemUtils.LOG.warning("Invalid Sentinel-2 product: 'GRANULE' folder containing at least one granule is required");
                            return;
                        }
                        if(granuleFolder.listFiles() == null || granuleFolder.listFiles().length == 0) {
                            SystemUtils.LOG.warning("Invalid Sentinel-2 product: 'GRANULE' folder must contain at least one granule");
                            return;
                        }
                        for (File granule : granuleFolder.listFiles()) {
                            if (granule.isDirectory()) {
                                S2OrthoGranuleDirFilename granuleDirFilename = S2OrthoGranuleDirFilename.create(granule.getName());
                                String epsgCode = S2CRSHelper.tileIdentifierToEPSG(granuleDirFilename.tileNumber);
                                epsgCodeList.add(epsgCode);
                            }
                        }
                    }
                }
            }
        }


        S2Config.Sentinel2ProductLevel getLevel() {
            return level;
        }
        S2Config.Sentinel2InputType getInputType() {
            return inputType;
        }
        boolean hasEPSG(String epsg) {
            return epsgCodeList.contains(epsg);
        }
        boolean add(String epsg) {
            return epsgCodeList.add(epsg);
        }
    }

    private HashMap<String, S2ProductCRSCacheEntry> cache = new HashMap<>();

    /* ctor */
    public S2ProductCRSCache() {
    }

    /* Ensure the given product is in cache */
    public synchronized void ensureIsCached(String productFileName) {
        if (!cache.containsKey(productFileName)) {
            S2ProductCRSCacheEntry s2ProductCRSCacheEntry = new S2ProductCRSCacheEntry(productFileName);
            cache.put(productFileName, s2ProductCRSCacheEntry);
        }
    }

    /* Ensure the given product is in cache */
    public synchronized void ensureIsCached(Path productPath) {
        if (!cache.containsKey(productPath.toString())) {
            S2ProductCRSCacheEntry s2ProductCRSCacheEntry = new S2ProductCRSCacheEntry(productPath);
            cache.put(productPath.toString(), s2ProductCRSCacheEntry);
        }
    }

    /* Get list of EPSG codes present in specified product */
    synchronized Set<String> getEPSGList(String productFileName) {
        if (!cache.containsKey(productFileName)) {
            throw new RuntimeException(String.format("The product %s was not parsed or does not contain any granule", productFileName));
        }
        return cache.get(productFileName).epsgCodeList;
    }

    synchronized boolean hasEPSG(String productFileName, String epsg) {
        if (!cache.containsKey(productFileName)) {
            throw new RuntimeException(String.format("The product %s was not parsed or does not contain any granule", productFileName));
        }
        return cache.get(productFileName).hasEPSG(epsg);
    }

    synchronized S2Config.Sentinel2ProductLevel getProductLevel(String productFileName) {
        if (!cache.containsKey(productFileName)) {
            throw new RuntimeException(String.format("The product %s was not parsed or does not contain any granule", productFileName));
        }
        return cache.get(productFileName).getLevel();
    }

    public synchronized S2Config.Sentinel2InputType getInputType(String productFileName) {
        if (!cache.containsKey(productFileName)) {
            throw new RuntimeException(String.format("The product %s was not parsed or does not contain any granule", productFileName));
        }
        return cache.get(productFileName).getInputType();
    }

}
