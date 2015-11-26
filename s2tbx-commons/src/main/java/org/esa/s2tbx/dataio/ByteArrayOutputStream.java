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

package org.esa.s2tbx.dataio;

/**
 * This class is just a simple variation of <code>ByteArrayOutputStream</code>.
 * The difference consists in the way the internal buffer is returned.
 * In <code>java.io.ByteArrayOutputStream</code>, a copy (i.e. new array instance) of the buffer is returned.
 * This can lead, if called very frequently, to unnecessary memory allocations.
 * In this class, a reference to the internal buffer is returned.
 *
 * @author Cosmin Cara
 * @see java.io.ByteArrayOutputStream
 */
public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {
    @Override
    public synchronized byte[] toByteArray() {
        return buf;
    }
}
