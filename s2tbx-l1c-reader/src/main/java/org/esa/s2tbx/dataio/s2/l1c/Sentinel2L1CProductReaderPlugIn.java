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
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.snap.framework.dataio.DecodeQualification;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.io.SnapFileFilter;

import java.io.File;
import java.util.Locale;

/**
 * @author Norman Fomferra
 */
public abstract class Sentinel2L1CProductReaderPlugIn implements ProductReaderPlugIn {

    static private L1cProductCRSCache crsCache = new L1cProductCRSCache();

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        SystemUtils.LOG.fine("Getting decoders...");

        File file = new File(input.toString());
        DecodeQualification deco = S2ProductFilename.isProductFilename(file.getName()) ? DecodeQualification.SUITABLE : DecodeQualification.UNABLE;
        if (deco.equals(DecodeQualification.SUITABLE)) {
            S2ProductFilename productFilename = S2ProductFilename.create(file.getName());
            if (productFilename != null && productFilename.fileSemantic.contains("L1C")) {
                crsCache.ensureIsCached(file.getAbsolutePath());
                if (crsCache.hasEPSG(file.getAbsolutePath(), getEPSG())) {
                    deco = DecodeQualification.INTENDED;
                }
                else {
                    deco = DecodeQualification.UNABLE;
                }
                // deco = DecodeQualification.INTENDED;
            }
            else
            {
                deco = DecodeQualification.UNABLE;
            }
        }

        return deco;
    }

    abstract public String getEPSG();

    @Override
    public Class[] getInputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader Multisize...");

        return new Sentinel2L1CProductReader(this, false, 10, true, getEPSG());
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{S2L1CConfig.getInstance().getFormatName()+"-MultiRes-" + S2CRSHelper.epsgToShortDisplayName(getEPSG())};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{S2Config.MTD_EXT};
    }

    @Override
    public String getDescription(Locale locale) {
        return String.format("Sentinel-2 MSI L1C - all resolutions - %s", S2CRSHelper.epsgToDisplayName(getEPSG()));
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(S2L1CConfig.getInstance().getFormatName(),
                                  getDefaultFileExtensions(),
                                  "Sentinel-2 MSI L1C product or tile");
    }
}
