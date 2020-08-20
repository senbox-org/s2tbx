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

package org.esa.s2tbx.dataio.s2.l1b.metadata;


import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2MetadataProc;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.filepatterns.L1BNamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.l1b.L1bSceneDescription;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.getHSBColor;

/**
 * @author opicas-p
 */
public class L1bMetadataProc extends S2MetadataProc {

    public static S2SpectralInformation makeSpectralInformation(S2BandConstants bandConstant, S2SpatialResolution resolution, String format) {
        // TODO we should implement scaling factor here, using PHYSICAL_GAINS metadata per band, to provide physical radiance.
        final double quantification = 1.0;
        final String unit = "";

        return new S2SpectralInformation(
                bandConstant.getPhysicalName(),
                resolution,
                //makeSpectralBandImageFileTemplate(bandConstant.getFilenameBandId()),
                NamingConventionFactory.getSpectralBandImageTemplate_L1b(format,bandConstant.getFilenameBandId()),
                "Radiance in band " + bandConstant.getPhysicalName(),
                unit,
                quantification,
                bandConstant.getBandIndex(),
                bandConstant.getWavelengthMin(),
                bandConstant.getWavelengthMax(),
                bandConstant.getWavelengthCentral());
    }

   /* private static String makeSpectralBandImageFileTemplate(String bandFileId) {
        //Sample :
        //MISSION_ID : S2A
        //SITECENTRE : MTI_
        //CREATIONDATE : 20150813T201603
        //ABSOLUTEORBIT : A000734
        //TILENUMBER : T32TQR
        //RESOLUTION : 10 | 20 | 60

        return String.format("IMG_DATA%s{{MISSION_ID}}_OPER_MSI_L1B_GR_{{SITECENTRE}}_{{CREATIONDATE}}_{{ABSOLUTEORBIT}}_{{DETECTOR}}_%s.jp2", File.separator, bandFileId);
    }*/


    public static S2IndexBandInformation makeTileInformation(String detector, S2SpatialResolution resolution, L1bSceneDescription sceneDescription) {

        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        List<String> tiles = new ArrayList<>();

        for(String tileId : sceneDescription.getOrderedTileIds()) {
            String detectorId = ((S2L1BGranuleDirFilename) S2L1BGranuleDirFilename.create(tileId)).getDetectorId();
            if (detectorId.equals(detector)) {
                tiles.add(tileId);
            }
        }

        int numberOfTiles = tiles.size();
        int index = 1;

        for(String tileId : tiles) {
            float f;
            f = (index-1)*(float)1.0/(numberOfTiles+1);
            f = (float) 0.75 - f;
            if (f < 0) f++;
            if(S2L1BGranuleDirFilename.create(tileId).getTileID()!=null) {
                indexList.add(S2IndexBandInformation.makeIndex(index, getHSBColor(f, (float) 1.0, (float) 1.0), S2L1BGranuleDirFilename.create(tileId).getTileID(), tileId));
            } else {
                indexList.add(S2IndexBandInformation.makeIndex(index, getHSBColor(f, (float) 1.0, (float) 1.0), tileId, tileId));
            }
            index++;
        }
        return new S2IndexBandInformation(detector + "_tile_id_" + resolution.resolution + "m", resolution, "", "Tile ID", "", indexList, "tile_" + resolution.resolution + "m_");
    }
}
