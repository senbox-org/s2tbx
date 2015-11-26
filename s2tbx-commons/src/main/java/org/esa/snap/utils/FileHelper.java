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

package org.esa.snap.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by kraftek on 2/20/2015.
 */
public class FileHelper {

    /**
     * Gets a file (if it exists) or creates a new one.
     * If intermediate directories do not exist, they will be created.
     *
     * @param basePath  The parent folder
     * @param pathFragments Additional subfolders that should end with the file name
     * @return  The File object
     * @throws IOException
     */
    public static File getFile(String basePath, String...pathFragments) throws IOException {
        File file = new File(basePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (pathFragments != null) {
            for (int i = 0; i < pathFragments.length; i++) {
                file = new File(file, pathFragments[i]);
                if (i < pathFragments.length - 1) {
                    file.mkdirs();
                } else {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                }
            }
        }
        return file;
    }
}
