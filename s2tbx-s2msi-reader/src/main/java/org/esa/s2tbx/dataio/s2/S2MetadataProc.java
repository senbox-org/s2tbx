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

package org.esa.s2tbx.dataio.s2;


import org.esa.s2tbx.dataio.s2.ortho.S2OrthoSceneLayout;
import org.esa.snap.core.datamodel.ColorPaletteDef;
import org.esa.snap.core.util.SystemUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.getHSBColor;
import static java.lang.Math.floor;
import static org.esa.s2tbx.dataio.s2.l3.L3Metadata.MOSAIC_BAND_NAME;
import static org.esa.snap.core.datamodel.ColorPaletteDef.loadColorPaletteDef;

/**
 * @author Nicolas Ducoin
 */
public class S2MetadataProc {

    public static S2IndexBandInformation makeTileInformation(S2SpatialResolution resolution, S2OrthoSceneLayout sceneDescription) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        int numberOfTiles = sceneDescription.getOrderedTileIds().size();
        int index = 1;
        for(String tileId : sceneDescription.getOrderedTileIds()) {
            float f = 0;
            f = (index-1)*(float)1.0/(numberOfTiles+1);
            f = (float) 0.75 - f;
            if (f < 0) f++;
            indexList.add(S2IndexBandInformation.makeIndex(index, getHSBColor(f, (float)1.0, (float)1.0), tileId, tileId));
            index++;
        }
        return new S2IndexBandInformation("Tile_Index"+ resolution.name(), resolution, "", "Tile Index", "", indexList, "tile_");
    }
}
