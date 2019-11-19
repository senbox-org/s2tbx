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

package org.esa.s2tbx.dataio.s2.gml;

import org.locationtech.jts.geom.Polygon;

/**
 * Created by Oscar on 23/05/2015.
 */
public class EopPolygon {
    String id;
    String type;
    Polygon polygon;

    public EopPolygon(String id, String type, Polygon pol) {
        this.id = id;
        this.type = type;
        this.polygon = pol;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Polygon getPolygon() {
        return polygon;
    }
}
