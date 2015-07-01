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
 * Created by jmalik on 29/06/15.
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
