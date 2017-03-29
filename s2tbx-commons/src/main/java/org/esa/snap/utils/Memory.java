/*
 *
 *  * Copyright (C) 2016 CS ROMANIA
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.snap.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author Cosmin Cara
 */
public final class Memory {
    private static Unsafe theUnsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            theUnsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (theUnsafe == null) {
            throw new RuntimeException("Cannot get unsafe reference");
        }
    }

    public static long sizeOf(Object object) {
        long address = theUnsafe.getInt(object, 4L) + 12L;
        return theUnsafe.getAddress(address >= 0 ? address : (~0L >>> 32) & address);
    }
}
