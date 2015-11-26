/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio.spot.dimap;

/**
 * This maps to the corresponding DIMAP Component element.
 *
 * @author Cosmin Cara
 */
public class VolumeComponent {
    String title;
    String type;
    String path;
    String thumbnailPath;
    int[] index;

    public VolumeComponent() {
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public int[] getIndex() {
        return index;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }
}
