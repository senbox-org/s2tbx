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
package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.S2CRSHelper;
import org.esa.s2tbx.dataio.s2.l1c.filepaterns.S2L1CGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.l1c.filepaterns.S2L1CGranuleMetadataFilename;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Julien Malik
 */
public class L1cProductCRSCache {

    class L1cProductCRSCacheEntry {
        Set<String> epsgCodeList = new HashSet<>();

        boolean hasEPSG(String epsg) {
            return epsgCodeList.contains(epsg);
        }

        boolean add(String epsg) { return epsgCodeList.add(epsg); }
    }

    private HashMap<String,L1cProductCRSCacheEntry> cache = new HashMap<>();

    /* ctor */
    public L1cProductCRSCache() {
    }

    /* Ensure the given product is in cache */
    synchronized void ensureIsCached(String productFileName) {
        Path rootPath = new File(productFileName).toPath().getParent();
        File granuleFolder = rootPath.resolve("GRANULE").toFile();
        for( File granule : granuleFolder.listFiles() ) {
            if (granule.isDirectory()) {
                S2L1CGranuleDirFilename granuleDirFilename = S2L1CGranuleDirFilename.create(granule.getName());
                String epsgCode = S2CRSHelper.tileIdentifierToEPSG(granuleDirFilename.tileNumber);
                if (!cache.containsKey(productFileName)) {
                    cache.put(productFileName, new L1cProductCRSCacheEntry());
                }
                L1cProductCRSCacheEntry l1cProductCRSCacheEntry = cache.get(productFileName);
                l1cProductCRSCacheEntry.add(epsgCode);
            }
        }
    }

    /* Get list of EPSG codes present in specified product */
    synchronized Set<String> getEPSGList(String productFileName) {
        if (!cache.containsKey(productFileName)) {
            throw new RuntimeException(String.format("The product %s was not parsed or does not contain any granule", productFileName));
        }
        return cache.get(productFileName).epsgCodeList;
    }

    /* Get list of EPSG codes present in specified product */
    synchronized boolean hasEPSG(String productFileName, String epsg) {
        if (!cache.containsKey(productFileName)) {
            throw new RuntimeException(String.format("The product %s was not parsed or does not contain any granule", productFileName));
        }
        return cache.get(productFileName).hasEPSG(epsg);
    }
}
