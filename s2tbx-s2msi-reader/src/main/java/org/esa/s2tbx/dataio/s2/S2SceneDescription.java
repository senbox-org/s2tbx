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

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Nicolas Ducoin
 */
public abstract class S2SceneDescription {

    public S2SceneDescription() {
    }

    public abstract Rectangle getMatrixTileRectangle(String tileId, S2SpatialResolution resolution);

    public static <T extends Comparable<? super T>> java.util.List<T> asSortedList(Collection<T> c) {
        java.util.List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }
}
