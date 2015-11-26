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

package org.esa.s2tbx.dataio.nitf;

import java.io.File;

/**
 * Created by kraftek on 6/18/2015.
 */
public class NITFConstants {
    public static final Class[] READER_INPUT_TYPES = new Class[] { String.class, File.class };
    public static final String[] FORMAT_NAMES = new String[] { "GenericNITF" };
    public static final String NTF_EXTENSION = ".ntf";
    public static final String NITF_DESCRIPTION = "NITF Image";
}
