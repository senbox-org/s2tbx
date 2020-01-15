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

package org.esa.s2tbx.dataio.pleiades.dimap;

import org.esa.snap.core.metadata.GenericXmlMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This maps to the corresponding DIMAP Component element.
 *
 * @author Cosmin Cara
 */
public class VolumeComponent {

    private final Path parentPath;

    private String relativePath;

    private String title;
    private String content;
    private String type;
    private String thumbnailPath;
    private String thumbnailFormat;

    public enum Type {
        VOLUME,
        IMAGE
    }

    public VolumeComponent(Path parentPath) {
        this.parentPath = parentPath;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    @Deprecated
    public Path getPathOLD() {
        return null;//path;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public String getContent() {
        return content;
    }

    public String getThumbnailFormat() {
        return thumbnailFormat;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setThumbnailFormat(String thumbnailFormat) {
        this.thumbnailFormat = thumbnailFormat;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public GenericXmlMetadata getComponentMetadata() {
        GenericXmlMetadata metadata = null;
        if (Constants.METADATA_FORMAT.equals(this.type)) {
            if (this.relativePath != null) {
                Path fullPath = this.parentPath.resolve(this.relativePath);
                if (Files.exists(fullPath)) {
                    if (this.relativePath.contains("DIM_")) {
                        try {
                            metadata = ImageMetadata.create(fullPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            metadata = VolumeMetadata.create(fullPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return metadata;
    }
}
