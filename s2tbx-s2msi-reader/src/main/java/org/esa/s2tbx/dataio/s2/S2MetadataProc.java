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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas Ducoin
 */
public class S2MetadataProc {
    public static S2IndexBandInformation makeTileInformation(S2SpatialResolution resolution, S2OrthoSceneLayout sceneDescription) {
        List<S2IndexBandInformation.S2IndexBandIndex> indexList = new ArrayList<>();
        int index = 1;
        for(String tileId : sceneDescription.getTileIds()) {
            indexList.add(S2IndexBandInformation.makeIndex(index, new Color(255, 255, 255), tileId, tileId));
            index++;
        }
        return new S2IndexBandInformation("Tile_Index", resolution, "", "Tile Index", "", indexList, "tile_");
    }
}
