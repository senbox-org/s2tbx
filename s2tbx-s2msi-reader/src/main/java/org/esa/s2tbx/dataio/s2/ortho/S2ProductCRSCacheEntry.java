package org.esa.s2tbx.dataio.s2.ortho;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleMetadataFilename;
import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.esa.s2tbx.dataio.s2.ortho.S2CRSHelper.tileIdentifierToEPSG;

/**
 * Created by obarrile on 25/10/2016.
 */

public class S2ProductCRSCacheEntry {
    Set<String> epsgCodeList = new HashSet<>();
    private S2Config.Sentinel2ProductLevel level = S2Config.Sentinel2ProductLevel.UNKNOWN;
    private S2Config.Sentinel2InputType inputType;

    public S2ProductCRSCacheEntry (Set<String> epsgCodeList, S2Config.Sentinel2ProductLevel level, S2Config.Sentinel2InputType inputType) {
        this.epsgCodeList.addAll(epsgCodeList);
        this.level = level;
        this.inputType = inputType;
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
