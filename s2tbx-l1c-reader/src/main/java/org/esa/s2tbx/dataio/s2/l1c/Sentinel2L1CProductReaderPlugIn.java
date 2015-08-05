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

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.l1c.filepaterns.S2L1CGranuleMetadataFilename;
import org.esa.snap.framework.dataio.DecodeQualification;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.framework.datamodel.RGBImageProfile;
import org.esa.snap.framework.datamodel.RGBImageProfileManager;
import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.io.SnapFileFilter;

import java.io.File;
import java.util.Locale;

import static org.esa.s2tbx.dataio.s2.S2CRSHelper.*;

/**
 * @author Norman Fomferra
 */
public abstract class Sentinel2L1CProductReaderPlugIn implements ProductReaderPlugIn {

    private static L1cProductCRSCache crsCache = new L1cProductCRSCache();

    public Sentinel2L1CProductReaderPlugIn() {
        RGBImageProfileManager manager = RGBImageProfileManager.getInstance();
        manager.addProfile(new RGBImageProfile("Sentinel 2 MSI Natural Colors", new String[]{"B4", "B3", "B2"}));
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        SystemUtils.LOG.fine("Getting decoders...");

        DecodeQualification decodeQualification = DecodeQualification.UNABLE;

        if(input instanceof File) {
            File file = (File) input;

            if (file.isFile()) {
                String fileName = file.getName();

                // test for granule filename first as it is more restrictive
                if (S2L1CGranuleMetadataFilename.isGranuleFilename(fileName)) {
                    S2L1CGranuleMetadataFilename granuleMetadataFilename = S2L1CGranuleMetadataFilename.create(fileName);
                    if (granuleMetadataFilename != null && granuleMetadataFilename.fileSemantic.contains("L1C")) {
                        String tileId = granuleMetadataFilename.tileNumber;
                        String epsg = tileIdentifierToEPSG(tileId);
                        if (getEPSG().equalsIgnoreCase(epsg)) {
                            decodeQualification = DecodeQualification.INTENDED;
                        }
                    }
                } else if (S2ProductFilename.isMetadataFilename(fileName)) {
                    S2ProductFilename productFilename = S2ProductFilename.create(fileName);
                    if (productFilename != null && productFilename.fileSemantic.contains("L1C")) {
                        crsCache.ensureIsCached(file.getAbsolutePath());
                        if (crsCache.hasEPSG(file.getAbsolutePath(), getEPSG())) {
                            decodeQualification = DecodeQualification.INTENDED;
                        }
                    }
                }
            }
        }

        return decodeQualification;
    }

    abstract public String getEPSG();

    @Override
    public Class[] getInputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader Multisize...");

        return new Sentinel2L1CProductReader(this, 10, true, getEPSG());
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{S2L1CConfig.getInstance().getFormatName()+"-MultiRes-" + epsgToShortDisplayName(getEPSG())};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{S2Config.MTD_EXT};
    }

    @Override
    public String getDescription(Locale locale) {
        return String.format("Sentinel-2 MSI L1C - all resolutions - %s", epsgToDisplayName(getEPSG()));
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        // returning a null formatName so that the user can further select between
        // all the intended readers for the product (different resolutions and UTM zones)
        return new SnapFileFilter(null,
                                  getDefaultFileExtensions(),
                                  "Sentinel-2 MSI L1C product or tile");
    }
}
