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

package org.esa.s2tbx.dataio.s2.l1c;


import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.AN_INCIDENCE_ANGLE_GRID;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_GEOMETRIC_INFO_TILE;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_MASK_LIST;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_INFO;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_ORGANIZATION;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_QUALITY_INDICATORS_INFO_TILE;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_SUN_INCIDENCE_ANGLE_GRID;
import https.psd_13_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_TILE_DESCRIPTION;
import https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_tile_metadata.Level1C_Tile;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1c.Level1C_User_Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.math3.util.Pair;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2MetadataProc;
import org.esa.s2tbx.dataio.s2.S2MetadataType;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoMetadataProc;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoDatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author opicas-p
 */
public class L1cMetadataProc extends S2OrthoMetadataProc {

    private static S2SpectralInformation makeSpectralInformation(S2BandConstants bandConstant, S2SpatialResolution resolution, double quantification) {
        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                makeSpectralBandImageFileTemplate(bandConstant.getFilenameBandId()),
                "Reflectance in band " + bandConstant.getPhysicalName(),
                "dl",
                quantification,
                bandConstant.getBandIndex(),
                bandConstant.getWavelengthMin(),
                bandConstant.getWavelengthMax(),
                bandConstant.getWavelengthCentral());
    }

    private static String makeSpectralBandImageFileTemplate(String bandFileId) {
        /* Sample :
        MISSION_ID : S2A
        SITECENTRE : MTI_
        CREATIONDATE : 20150813T201603
        ABSOLUTEORBIT : A000734
        TILENUMBER : T32TQR
        RESOLUTION : 10 | 20 | 60
         */
        return String.format("IMG_DATA%s{{MISSION_ID}}_OPER_MSI_L1C_TL_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{TILENUMBER}}_%s.jp2", File.separator, bandFileId);
    }

    public static List<S2BandInformation> getBandInformationList (double toaQuantification) {
        List<S2BandInformation> aInfo = new ArrayList<>();
        aInfo.add(makeSpectralInformation(S2BandConstants.B1, S2SpatialResolution.R60M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B2, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B3, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B4, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B5, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B6, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B7, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B8, S2SpatialResolution.R10M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B8A, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B9, S2SpatialResolution.R60M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B10, S2SpatialResolution.R60M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B11, S2SpatialResolution.R20M, toaQuantification));
        aInfo.add(makeSpectralInformation(S2BandConstants.B12, S2SpatialResolution.R20M, toaQuantification));
        return aInfo;
    }

}
